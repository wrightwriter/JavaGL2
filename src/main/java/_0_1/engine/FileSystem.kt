package _0_1.engine

import _0_1.wrightgl.shader.Shader
import _0_1.wrightgl.fb.Texture
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

class FileSystem(
    private val engine: Engine,
    sketchClass: Class<Any>
    ) : Thread() {
    var shadersToRecompile: MutableList<Shader> = ArrayList()
    var texturesToReload: MutableList<Texture> = ArrayList()

    var foundChangedFiles = AtomicBoolean(false)
    var changedFiles = ArrayList<Path>()

    var globalResourcesFolder: String = System.getProperty("user.dir") + "/src/res/"
        private set
    var sketchResourcesFolder: String
        private set

    init{

//        val sketchClassName = sketchClass.simpleName + ".class"
        val sketchClassName = sketchClass.declaringClass.simpleName + ".class"
        sketchResourcesFolder = sketchClass.getResource(sketchClassName)
            .toString().replace(
        "out/production/classes",
        "src/main/java"
            ).replace(
                "file:/",
                ""
            )
        sketchResourcesFolder = sketchResourcesFolder.substring(0, sketchResourcesFolder.length - sketchClassName.length)
    }
    override fun run( ) {
        if (!engine.engineSettings.liveShaderReloading) return
        val globalPath = Path.of("$globalResourcesFolder/shaders")
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
                        // GLOBAL
                        val changed = event.context() as Path
                        if (changed.toString()[changed.toString().length - 1] != '~') {
//                            println(changed)
                            val changedPath: Path = globalPath.resolve(changed)
                            changedFiles.add(changedPath)
                            found = true
                        }
                    }
                    for (event in sketchKey.pollEvents()) {
                        // LOCAL
                        val changed = event.context() as Path
                        if (changed.toString()[changed.toString().length - 1] != '~'){
//                            println(changed)
                            val changedPath: Path = sketchPath.resolve(changed)
                            changedFiles.add(changedPath)
                            found = true
                        }
                    }
                    if (found) foundChangedFiles.set(true)

                    // reset the key
                    val valid = globalKey.reset() and sketchKey.reset()
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