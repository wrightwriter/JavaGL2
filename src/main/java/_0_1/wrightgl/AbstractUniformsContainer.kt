package _0_1.wrightgl

import _0_1.main.Global
import _0_1.math.vector.*
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.fb.Texture

abstract class AbstractUniformsContainer{
    var name = ""

    var uniformNumbers: HashMap<String, Any> = HashMap()
    var uniformTextures: HashMap<String, Texture> = HashMap()
    var uniformImages: HashMap<String, Texture> = HashMap()
    var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()

//    var uniforms: HashMap<String, Any>

    fun setUniformTexture(name: String, value: Texture){
        uniformTextures[name] = value
    }
    fun setBoundImage(name: String, value: Texture){
        uniformImages[name] = value
    }
    fun bindSSBOToIdx(bindIdx: Int, value: StorageBuffer){
        boundSSBOs[bindIdx] = value
    }
    fun setUniform(name: String, value: IVec2){
        uniformNumbers[name] = value as Any
    }
    fun setUniform(name: String, value: Vec3){
        uniformNumbers[name] = value as Any
    }
    fun setUniform(name: String, value: Vec4){
        uniformNumbers[name] = value as Any
    }
    fun setUniform(name: String, value: Vec2){
        uniformNumbers[name] = value as Any
    }
    fun setUniform(name: String, value: Float){
        uniformNumbers[name] = value as Any
    }
    fun setUniform(name: String, value: Int){
        uniformNumbers[name] = value as Any
    }
    fun setUniform(name: String, value: Boolean){
        uniformNumbers[name] = value as Any
    }
    fun setUniformLightTextures(lightsArray: MutableList<Light>){
        for(light in lightsArray){
            setUniformTexture("lightTextures[" + light.id.toString() + "]", light.fb.depthTexture!! )
        }
    }
//    fun setUniform(name: String, value: Array<Float>){
//        uniformNumbers.set(name, value as Any)
//    }
    fun setCurrObjectUniforms(){
        for((numberName, uniformValue ) in uniformNumbers){
            if (uniformValue is Vec)
                Global.engine.wgl.setUniform(numberName,(uniformValue as Vec).vals)
//            else if (uniformValue is Array<*>)
//                Global.engine.wgl.setUniform(numberName,(uniformValue as Array<Float>).)
            else if (uniformValue is IVec)
                Global.engine.wgl.setUniform(numberName,(uniformValue as IVec).vals)
            else if (uniformValue is Float)
                Global.engine.wgl.setUniform(numberName,(uniformValue as Float))
            else if (uniformValue is Int)
                Global.engine.wgl.setUniform(numberName,(uniformValue as Int))
            else if (uniformValue is Boolean)
                Global.engine.wgl.setUniform(numberName,(uniformValue as Boolean))
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

    fun <T> setUniform(name: String, v: T){
        setUniform(name, v)
    }
    abstract class AbstractUniform protected constructor(
        val name: String = "",
        val container: AbstractUniformsContainer
    ){

    }
    class UniformFloat(
        name: String = "",
        value: Float,
        owningClass: AbstractUniformsContainer
    ) : AbstractUniform(name, owningClass){

        var valueArray : FloatArray = floatArrayOf(value)
            private set

        init{
            set(value)
        }

        fun set(v: Float){
            valueArray[0] = v;
            container.setUniform(name, v)
        }
        fun get(): Float{
            return valueArray[0]
        }
    }

    class UniformBoolean(
        name: String = "",
        value: Boolean,
        owningClass: AbstractUniformsContainer
    ) : AbstractUniform(name,  owningClass){
        var value  = value
            private set

        init{
            set(value)
        }

        fun set(v: Boolean){
            container.setUniform(name, v)
        }
        fun get(): Boolean{
            return value
        }
    }

    class UniformInt(
        name: String = "",
        value: Int,
        owningClass: AbstractUniformsContainer
    ) : AbstractUniform(name, owningClass){

        var valueArray = intArrayOf(value)
            private set

        init{
            set(value)
        }

        fun set(v: Int){
            valueArray[0] = v;
            container.setUniform(name, v)
        }
        fun get(): Int{
            return valueArray[0]
        }
    }


    fun exposeUniformsToGUI(){
        val fields = this.javaClass.fields
        for( field in fields){
            println(field)
        }
        // kill me
        var uniformsToExpose: ArrayList<AbstractUniform> = ArrayList()

        for( field in this.javaClass.declaredFields){
            val className = field.type.name
            val uniformFloatClassName = UniformFloat::class.java.typeName
            val uniformBooleanClassName = UniformBoolean::class.java.typeName
            val uniformIntClassName = UniformInt::class.java.typeName

            println("Exposed uniform float: $className")
            field.isAccessible = true
            if (className == uniformFloatClassName){
                uniformsToExpose.add(field.get(this) as UniformFloat)
            } else if (className == uniformBooleanClassName){
                uniformsToExpose.add(field.get(this) as UniformBoolean)
            } else if (className == uniformIntClassName){
                uniformsToExpose.add(field.get(this) as UniformInt)
            }
        }
    Global.engine.gui.exposedUniforms[this] = uniformsToExpose
    }

}