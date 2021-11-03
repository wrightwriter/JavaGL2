package _0_1.wrightgl

import _0_1.main.Global
import _0_1.math.vector.*
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.fb.Texture

abstract class AbstractUniformsContainer{
    var uniformNumbers: HashMap<String, Any> = HashMap()
    var uniformTextures: HashMap<String, Texture> = HashMap()
    var uniformImages: HashMap<String, Texture> = HashMap()
    var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()

//    var uniforms: HashMap<String, Any>

    fun setUniformTexture(name: String, value: Texture){
        uniformTextures.set(name, value)
    }
    fun setBoundImage(name: String, value: Texture){
        uniformImages.set(name, value)
    }
    fun bindSSBOToIdx(bindIdx: Int, value: StorageBuffer){
        boundSSBOs.set(bindIdx, value)
    }
    fun setUniform(name: String, value: IVec2){
        uniformNumbers.set(name, value as Any)
    }
    fun setUniform(name: String, value: Vec3){
        uniformNumbers.set(name, value as Any)
    }
    fun setUniform(name: String, value: Vec4){
        uniformNumbers.set(name, value as Any)
    }
    fun setUniform(name: String, value: Vec2){
        uniformNumbers.set(name, value as Any)
    }
    fun setUniform(name: String, value: Array<Float>){
        uniformNumbers.set(name, value as Any)
    }
    fun setCurrObjectUniforms(){
        for((numberName, uniformValue ) in uniformNumbers){
            if (uniformValue is Vec)
                Global.engine.wgl.setUniform(numberName,(uniformValue as Vec).vals)
            else if (uniformValue is Array<*>)
                Global.engine.wgl.setUniform(numberName,(uniformValue as Vec).vals)
            else if (uniformValue is IVec)
                Global.engine.wgl.setUniform(numberName,(uniformValue as IVec).vals)
        }
        for((textureName, texture ) in uniformTextures){
            texture.setUniform(textureName)
        }
        for((textureName, texture ) in uniformImages){
            texture.setUniformWritableImage(name = textureName)
        }
        for((ssboBindIdx, ssbo ) in boundSSBOs){
            ssbo.bindAsSSBO(ssboBindIdx)
        }
    }

}