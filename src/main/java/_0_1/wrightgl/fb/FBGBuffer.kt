package _0_1.wrightgl.fb

import _0_1.main.Global
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL45
import org.lwjgl.opengl.GL46

class FBGBuffer private constructor(): FB() {

    val albedoTexture: Texture
        get() = textures[0]
    val normalsTexture: Texture
        get() = textures[1]
    val materialTexture: Texture
        get() = textures[2]
    val idTexture: Texture
        get() = textures[3]
    val positionTexture: Texture
        get() = textures[4]
//        private set

    constructor(
        _width: Int = Global.engine.res.x,
        _height: Int = Global.engine.res.y,
        _name: String? = "Deferred FB",
    ) : this() {
        name = _name
        pid = GL45.glCreateFramebuffers()
        GL45.glNamedFramebufferDrawBuffers(pid, 1,)


        depthTexture = Texture(
            null,
            _width,
            _height,
//                Texture.InternalFormat.DEPTH_COMPONENT,
            Texture.InternalFormat.DEPTH_COMPONENT32F,
            Texture.Format.DEPTH_COMPONENT,
            Texture.Type.FLOAT
        )
        GL46.glNamedFramebufferTexture(
            pid,
            GL46.GL_DEPTH_ATTACHMENT,
            depthTexture!!.pid,
            0
        )

        textures.add(Texture(null, _width, _height, Texture.InternalFormat.RGBA32F, Texture.Format.RGBA, Texture.Type.FLOAT))
        GL46.glNamedFramebufferTexture(
            pid,
            GL46.GL_COLOR_ATTACHMENT0 + 0,
            textures[0].pid,
            0
        )

        textures.add(Texture(null, _width, _height, Texture.InternalFormat.RGBA32F, Texture.Format.RGBA, Texture.Type.FLOAT))
        GL46.glNamedFramebufferTexture(
            pid,
            GL46.GL_COLOR_ATTACHMENT0 + 1,
            textures[1].pid,
            0
        )

        textures.add(Texture(null, _width, _height, Texture.InternalFormat.RGBA32F, Texture.Format.RGBA, Texture.Type.FLOAT))
        GL46.glNamedFramebufferTexture(
            pid,
            GL46.GL_COLOR_ATTACHMENT0 + 2,
            textures[2].pid,
            0
        )

        textures.add(Texture(null, _width, _height, Texture.InternalFormat.RGBA32F, Texture.Format.RGBA, Texture.Type.FLOAT))
        GL46.glNamedFramebufferTexture(
            pid,
            GL46.GL_COLOR_ATTACHMENT0 + 3,
            textures[3].pid,
            0
        )

        textures.add(Texture(null, _width, _height, Texture.InternalFormat.RGBA32F, Texture.Format.RGBA, Texture.Type.FLOAT))
        GL46.glNamedFramebufferTexture(
            pid,
            GL46.GL_COLOR_ATTACHMENT0 + 4,
            textures[4].pid,
            0
        )

        val texAttachmentsCnt = textures.size
        val attachments = IntArray(texAttachmentsCnt)
        for (i in 0 until texAttachmentsCnt) {
            attachments[i] = GL30.GL_COLOR_ATTACHMENT0 + i
        }
        GL46.glNamedFramebufferDrawBuffers(pid, attachments)
        GL20.glDrawBuffers(attachments)

        val fbStatus = GL45.glCheckNamedFramebufferStatus(pid, GL30.GL_FRAMEBUFFER)
        if(fbStatus != GL30.GL_FRAMEBUFFER_COMPLETE){
            // TODO: error
            println("Error: FB Incomplete")
        }
//        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, globalReadFB.pid)
//        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, globalDrawFB.pid)
        frameBuffersList.add(this)
    }

}