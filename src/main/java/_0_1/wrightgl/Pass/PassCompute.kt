package _0_1.wrightgl.Pass

import _0_1.engine.Constants
import _0_1.main.Global
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgCompute
import _0_1.wrightgl.shader.ProgFX
import org.lwjgl.opengl.GL42
import org.lwjgl.opengl.GL42.GL_FRAMEBUFFER_BARRIER_BIT
import org.lwjgl.opengl.GL43
import org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT
import org.lwjgl.opengl.GL46
import java.util.function.Consumer

class PassCompute constructor(
    _fileNameComp: String,
    _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder
): AbstractPass(){
//    override var uniformNumbers: HashMap<String, Any>
//    var texture: Texture? = null
//    var workGroupCount: IVec2 = IVec2(32)

//    lateinit var shaderProgram: ProgCompute
//        private set
//
//    override var shaderProgram: AbstractProgram = null
//        get() = program

    init{
        shaderProgram = ProgCompute(_fileNameComp, _folderPath)
    }

//    // TODO
//    fun dispatch(
//        frameBuffer: FB,
//        callback: Consumer<PassCompute>? = null,
//    ) {
////        Glob.engine.wgl.useProgram(this)
//        shaderProgram.use()
//        callback?.accept(this)
//        setCurrObjectUniforms()
//
//        val tex: Texture = frameBuffer.textures[0]
//        GL43.glDispatchCompute(
//            frameBuffer.textures[0].res.x / shaderProgram.localGroupSize.x + 1,
//            frameBuffer.textures[0].res.y / shaderProgram.localGroupSize.y + 1,
//            frameBuffer.textures[0].res.z / shaderProgram.localGroupSize.z + 1,
//        );
//        GL46.glMemoryBarrier(GL_FRAMEBUFFER_BARRIER_BIT)
////
//
//    }
    fun dispatch(
        vertexBuffer: VB,
        callback: Consumer<PassCompute>? = null,
    ) {
        if (shaderProgram.pid > 0) {
            shaderProgram.use()
            // TODO: make this shit way cheaper AND BIND AS SSBO
            callback?.accept(this)
            setCurrObjectUniforms()

            // THIS SHIT IS PROB WRONG
            val szx: Int = (vertexBuffer.totalVertCnt / (shaderProgram as ProgCompute).localGroupSize.x) + 1;
            //        val szy: Int = kotlin.math.ceil(sqrt((vertCnt / program.localGroupSize.y).toFloat())).toInt();
            //        val szz: Int = kotlin.math.ceil(sqrt((vertCnt / program.localGroupSize.z).toFloat())).toInt();
            GL43.glDispatchCompute(szx, 1, 1);
            // HM?
            GL46.glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT)
        }
    }
    fun dispatch(
        storageBuffer: StorageBuffer,
        callback: Consumer<PassCompute>? = null,
    ) {
        if (shaderProgram.pid > 0){
            shaderProgram.use()
            storageBuffer.bindAsSSBO(0)
            callback?.accept(this)
            setCurrObjectUniforms()

            // THIS SHIT IS PROB WRONG
            val szx: Int = (storageBuffer.totalVertCnt / (shaderProgram as ProgCompute).localGroupSize.x) + 1;

//        val szx: Int = kotlin.math.ceil(sqrt((storageBuffer.totalVertCnt / program.localGroupSize.x).toFloat())).toInt();
//        val szy: Int = kotlin.math.ceil(sqrt((storageBuffer.totalVertCnt / program.localGroupSize.y).toFloat())).toInt();
//        val szz: Int = kotlin.math.ceil(sqrt((storageBuffer.totalVertCnt / program.localGroupSize.z).toFloat())).toInt();

            GL43.glDispatchCompute(szx, 1, 1);
            GL46.glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT)

        }
    }


    fun dispatch(
        texture: Texture,
        bindIdx: Int = 0,
        name: String = "i_computeTex",
        callback: Consumer<PassCompute>? = null,
    ) {
        if (shaderProgram.pid > 0){
            shaderProgram.use()
            texture.setUniformWritableImage( bindIdx,name )
            callback?.accept(this)
            setCurrObjectUniforms()

            val szx = texture.res.x / (shaderProgram as ProgCompute).localGroupSize.x + 1
            val szy = texture.res.y / (shaderProgram as ProgCompute).localGroupSize.y + 1
            val szz = texture.res.z / (shaderProgram as ProgCompute).localGroupSize.z + 1

            GL43.glDispatchCompute( szx, szy, szz, );
            GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        }
    }
}