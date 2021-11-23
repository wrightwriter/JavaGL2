package _0_1.wrightgl

import _0_1.engine.Sketch
import _0_1.main.Global
import _0_1.math.Mat4
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec3
import _0_1.math.vector.Vec4
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.thing.Thing

abstract class Light {
    var id: Int = 0
        protected set
    var position: Vec3 = Vec3(1,1,1)
        set(v){
            field = v
            // Cringe
            updateMatrices()
        }
    var colour = Vec4(1,1,1,1)
    abstract val fb: FB
    var near = .01f
        set(v){
            field = v
            // Cringe
            updateMatrices()
        }
    var far = 10.0f
        set(v){
            field = v
            // Cringe
            updateMatrices()
        }
    var projMatrix = Mat4.identityMatrix
    var viewMatrix = Mat4.identityMatrix

    abstract fun run(thing: Thing)
    abstract fun updateMatrices()
}

class DirectionalLight(
    private var sketch: Sketch,
    _near: Float = 0.1f,
    _far: Float = 100.0f,
    _res: IVec2 = IVec2(2500,2500),
    _position: Vec3 = Vec3(1,1,1),
    _colour: Vec4 = Vec4(1,1,1, 1),
    _lookAt: Vec3 = Vec3(0,0,0),
): Light() {
    override val fb: FB

    var lookAt = Vec3(0,0,0)
        set(v){
            field = v
            // Cringe
            updateMatrices()
        }

    init{
        id = sketch.lights.size

        fb = FB(_res.x, _res.y, texturesCnt = 0,hasDepth = true, _name = "Directional Light")

        position = _position
        colour = _colour
        lookAt = _lookAt
        near = _near
        far = _far

        sketch.lights.add(this)
        // Dear god
        sketch.lightsUBO.rewind()
        sketch.lightsUBO.add( sketch.lights.size )
        updateMatrices()
    }

    override fun updateMatrices(){
        // View matrix
        org.joml.Matrix4f().lookAt(
            -position.x, position.y, position.z,
            -lookAt.x, lookAt.y, lookAt.z,
            0.0f,1.0f,0.0f).get(viewMatrix.vals)

        val scale = 0.005f;
        org.joml.Matrix4f().ortho(
            -fb.depthTexture!!.res.x.toFloat()*scale,
            fb.depthTexture!!.res.x.toFloat()*scale,
            -fb.depthTexture!!.res.y.toFloat()*scale,
            fb.depthTexture!!.res.y.toFloat()*scale,
            near,
            far,
        ).get(projMatrix.vals)

//        projMatrix = Mat4.getPerspectiveProjMatrix(
//            90.0f,
//            near,
//            far,
//            fb.depthTexture!!.res.x.toFloat(),
//            fb.depthTexture!!.res.y.toFloat()
//        )


        sketch.lightsUBO.rewind(16 + id * 16*(1 + 1 + 4 + 4 + 1) )
        sketch.lightsUBO.add( position )
        sketch.lightsUBO.add( colour )
        sketch.lightsUBO.add( viewMatrix )
        sketch.lightsUBO.add( projMatrix )
        sketch.lightsUBO.add( near )
        sketch.lightsUBO.add( far )
    }
    override fun run(thing: Thing){

        if(thing.shaderProgramShadowMap != null){
            fb.bind()
            thing.shaderProgramShadowMap!!.use()
            Global.engine.wgl.setUniform("VLight", viewMatrix)
            Global.engine.wgl.setUniform("PLight", projMatrix)
            thing.render(
                thing.shaderProgramShadowMap!!,
                _depthTest = true
            )
        }
    }
}
class SpotLight(
    private var sketch: Sketch,
    _near: Float = 0.1f,
    _far: Float = 100.0f,
    _res: IVec2 = IVec2(2500,2500),
    _position: Vec3 = Vec3(1,1,1),
    _colour: Vec4 = Vec4(1,1,1, 1),
    _lookAt: Vec3 = Vec3(0,0,0),
): Light() {
    override val fb: FB

    var lookAt = Vec3(0,0,0)
        set(v){
            field = v
            // Cringe
            updateMatrices()
        }

    init{
        fb = FB(_res.x, _res.y, texturesCnt = 0,hasDepth = true, _name = "Directional Light")

        position = _position
        colour = _colour
        lookAt = _lookAt
        near = _near
        far = _far

        id = sketch.lights.size
        sketch.lights.add(this)
        // Dear god
        sketch.lightsUBO.rewind()
        sketch.lightsUBO.add( sketch.lights.size )
        updateMatrices()
    }

    override fun updateMatrices(){
        // View matrix
        org.joml.Matrix4f().lookAt(
            -position.x, position.y, position.z,
            -lookAt.x, lookAt.y, lookAt.z,
            0.0f,1.0f,0.0f).get(viewMatrix.vals)

        val scale = 0.005f;
        org.joml.Matrix4f().ortho(
            -fb.depthTexture!!.res.x.toFloat()*scale,
            fb.depthTexture!!.res.x.toFloat()*scale,
            -fb.depthTexture!!.res.y.toFloat()*scale,
            fb.depthTexture!!.res.y.toFloat()*scale,
            near,
            far,
        ).get(projMatrix.vals)

//        projMatrix = Mat4.getPerspectiveProjMatrix(
//            90.0f,
//            near,
//            far,
//            fb.depthTexture!!.res.x.toFloat(),
//            fb.depthTexture!!.res.y.toFloat()
//        )


        sketch.lightsUBO.rewind(16 + id * 16*(1 + 1 + 4 + 4 + 1) )
        sketch.lightsUBO.add( position )
        sketch.lightsUBO.add( colour )
        sketch.lightsUBO.add( viewMatrix )
        sketch.lightsUBO.add( projMatrix )
        sketch.lightsUBO.add( near )
        sketch.lightsUBO.add( far )
    }
    override fun run(thing: Thing){

        if(thing.shaderProgramShadowMap != null){
            fb.bind()
            thing.shaderProgramShadowMap!!.use()
            Global.engine.wgl.setUniform("VLight", viewMatrix)
            Global.engine.wgl.setUniform("PLight", projMatrix)
            thing.render(
                thing.shaderProgramShadowMap!!,
                _depthTest = true
            )
        }
    }
}
