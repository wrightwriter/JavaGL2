package _0_1.wrightgl.shader

import _0_1.main.Global
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.WrightGL
import org.lwjgl.opengl.GL20

abstract class AbstractProgram protected constructor(){
    var pid = 0
        protected set
    var uniformTextures: HashMap<Texture,String> = HashMap()
        private set
    var errorLog: String? = null
        protected set
    abstract fun link(): Boolean
    fun setUniformTexture(_texture: Texture, _uniformName: String ){
        uniformTextures.set(_texture,_uniformName)
    }

    fun use() {
        val wgl: WrightGL = Global.engine.wgl
        wgl.currProgram = this
        wgl.currTexBindNumber = 0
        wgl.currImageBindNumber = 0
        wgl.currSSBOBindNumber = 0
        GL20.glUseProgram(pid)
        // Set uniform textures
        for(t: Texture in uniformTextures.keys){
            val texUniformName = uniformTextures[t]
            t.setUniform(name = texUniformName!!)
        }
    }
}