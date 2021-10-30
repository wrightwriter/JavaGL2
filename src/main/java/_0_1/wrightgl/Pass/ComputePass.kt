package _0_1.wrightgl.Pass

import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgCompute
import org.lwjgl.opengl.GL42
import org.lwjgl.opengl.GL42.GL_FRAMEBUFFER_BARRIER_BIT
import org.lwjgl.opengl.GL43
import org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT
import org.lwjgl.opengl.GL46
import java.util.function.Consumer

class ComputePass private constructor( ): AbstractPass(){
//    override var uniformNumbers: HashMap<String, Any>
//    var texture: Texture? = null
//    var workGroupCount: IVec2 = IVec2(32)
    override var uniformNumbers: HashMap<String, Any> = HashMap()
    override var uniformTextures: HashMap<String, Texture> = HashMap()
    override var uniformImages: HashMap<String, Texture> = HashMap()
    override var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()


    lateinit var program: ProgCompute
        private set

    constructor(_fileNameComp: String) : this() {
        program = ProgCompute(_fileNameComp)
    }

    // TODO
    fun dispatch(
        frameBuffer: FB,
        callback: Consumer<ComputePass>? = null,
    ) {
//        Glob.engine.wgl.useProgram(this)
        program.use()
        callback?.accept(this)
        setCurrObjectUniforms()

        val tex: Texture = frameBuffer.textures[0]
        GL43.glDispatchCompute(
            frameBuffer.textures[0].res.x / program.localGroupSize.x + 1,
            frameBuffer.textures[0].res.y / program.localGroupSize.y + 1,
            frameBuffer.textures[0].res.z / program.localGroupSize.z + 1,
        );
        GL46.glMemoryBarrier(GL_FRAMEBUFFER_BARRIER_BIT)
//

    }
    fun dispatch(
        vertexBuffer: VB,
        callback: Consumer<ComputePass>? = null,
    ) {
        program.use()
        // TODO: make this shit way cheaper AND BIND AS SSBO
        callback?.accept(this)
        setCurrObjectUniforms()

        // THIS SHIT IS PROB WRONG
        val szx: Int = (vertexBuffer.totalVertCnt / program.localGroupSize.x) + 1;
//        val szy: Int = kotlin.math.ceil(sqrt((vertCnt / program.localGroupSize.y).toFloat())).toInt();
//        val szz: Int = kotlin.math.ceil(sqrt((vertCnt / program.localGroupSize.z).toFloat())).toInt();
        GL43.glDispatchCompute(szx, 1, 1);
        // HM?
        GL46.glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT)
    }
    fun dispatch(
        storageBuffer: StorageBuffer,
        callback: Consumer<ComputePass>? = null,
    ) {
        program.use()
        storageBuffer.bindAsSSBO(0)
        callback?.accept(this)
        setCurrObjectUniforms()

        // THIS SHIT IS PROB WRONG
        val szx: Int = (storageBuffer.totalVertCnt / program.localGroupSize.x) + 1;

//        val szx: Int = kotlin.math.ceil(sqrt((storageBuffer.totalVertCnt / program.localGroupSize.x).toFloat())).toInt();
//        val szy: Int = kotlin.math.ceil(sqrt((storageBuffer.totalVertCnt / program.localGroupSize.y).toFloat())).toInt();
//        val szz: Int = kotlin.math.ceil(sqrt((storageBuffer.totalVertCnt / program.localGroupSize.z).toFloat())).toInt();

        GL43.glDispatchCompute(szx, 1, 1);
        GL46.glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT)
    }


    fun dispatch(
        texture: Texture,
        bindIdx: Int = 0,
        name: String = "i_computeTex",
        callback: Consumer<ComputePass>? = null,
    ) {
        program.use()
        texture.setUniformWritableImage( bindIdx,name )
        callback?.accept(this)
        setCurrObjectUniforms()

        val szx = texture.res.x / program.localGroupSize.x + 1
        val szy = texture.res.y / program.localGroupSize.y + 1
        val szz = texture.res.z / program.localGroupSize.z + 1

        GL43.glDispatchCompute( szx, szy, szz, );
        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }
}