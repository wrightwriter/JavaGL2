package _0_1.wrightgl.fb

import _0_1.main.Glob
import _0_1.math.vector.Vec4

class FBPingPong: FB{
//    var pidBack: Int
    lateinit var backBuffer: FB
        private set

    companion object{
        var pingPongFBs: ArrayList<FBPingPong> = ArrayList()
    }

    constructor(
        _width: Int = Glob.engine.res.x,
        _height: Int = Glob.engine.res.x,
        texturesCnt: Int = 1,
        hasDepth: Boolean = false,
        internalFormat: Texture.InternalFormat = Texture.InternalFormat.RGBA32F,
        format: Texture.Format = Texture.Format.RGBA,
        type: Texture.Type = Texture.Type.FLOAT,
        // TODO: implement
        renderbuffer: Boolean = false,
        pingPong: Boolean = false,
    ): super(
        _width,
        _height,
        texturesCnt,
        hasDepth,
        internalFormat,
        format,
        type,
        // TODO: implement
        renderbuffer,
        pingPong,
    ) {
        backBuffer = FB(
            _width,
            _height,
            texturesCnt,
            hasDepth,
            internalFormat,
            format,
            type,
            // TODO: implement
            renderbuffer,
            pingPong,
        )
        pingPongFBs.add(this)
    }

    // Exchange frontbuffer and backbuffer
    fun ping(){
        run { val temp = pid; this.pid = backBuffer.pid; backBuffer.pid = temp}
        run { val temp = textures; this.textures = backBuffer.textures; backBuffer.textures = textures }
        run { val temp = name; this.name = backBuffer.name; backBuffer.name = name }
        run { val temp = depthTexture; this.depthTexture = backBuffer.depthTexture; backBuffer.depthTexture = depthTexture }
    }

}