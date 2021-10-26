package _0_1.enginePackage

import _0_1.wrightglPackage.Shader
import _0_1.wrightglPackage.Texture
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

class FileSystem(private val engine: Engine) : Thread() {
    var shadersToRecompile: MutableList<Shader> = ArrayList()
    var texturesToReload: MutableList<Texture> = ArrayList()

    var foundChangedFiles = AtomicBoolean(false)
    var changedFiles = ArrayList<Path>()

    var globalResourcesFolder: String = System.getProperty("user.dir") + "/src/res/"
        private set
    var sketchResourcesFolder: String
        private set

    init{

        val engineClassName = engine.javaClass.simpleName + ".class"
        sketchResourcesFolder = engine.javaClass.getResource(engineClassName)
            .toString().replace(
        "out/production/classes",
        "src/main/java"
            ).replace(
                "file:/",
                ""
            )
        sketchResourcesFolder = sketchResourcesFolder.substring(0, sketchResourcesFolder.length - engineClassName.length)
    }
    override fun run( ) {
        if (!engine.engineSettings.liveShaderReloading) return
        val globalPath = Path.of(globalResourcesFolder)
        val sketchPath = Path.of(sketchResourcesFolder)
        println("Watching folders: \n" + globalPath + "\n" + sketchPath + "\n")
        try {
            FileSystems.getDefault().newWatchService().use { watchService ->
                val globalKey = globalPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
                val sketchKey = sketchPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
                while (true) {
                    val wk = watchService.take()
                    sleep(50)
                    var found = false
                    for (event in globalKey.pollEvents()) {
                        //we only register "ENTRY_MODIFY" so the context is always a Path.
                        val changed = event.context() as Path
                        println(changed)
                        changedFiles.add(changed)
                        found = true
                    }
                    for (event in sketchKey.pollEvents()) {
                        //we only register "ENTRY_MODIFY" so the context is always a Path.
                        val changed = event.context() as Path
                        println(changed)
                        changedFiles.add(changed)
                        found = true
                    }
                    if (found) foundChangedFiles.set(true)

                    // reset the key
                    val valid = globalKey.reset()
                    if (!valid) {
                        println("Key has been unregisterede")
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}