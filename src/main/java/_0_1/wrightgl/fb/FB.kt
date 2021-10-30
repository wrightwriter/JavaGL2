package _0_1.wrightgl.fb

import _0_1.main.Glob
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec4
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL45.glCheckNamedFramebufferStatus
import java.util.ArrayList

open class FB {
    var pid = -1
        internal set
    var textures: MutableList<Texture> = ArrayList()
        internal set
    var name: String? = null
        internal set
    var depthTexture: Texture? = null
        internal set


    enum class Target(val value: Int) {
        FRAMEBUFFER(GL30.GL_FRAMEBUFFER),
        DRAW(GL30.GL_DRAW_FRAMEBUFFER),
        READ(GL30.GL_READ_FRAMEBUFFER);
    }

    enum class FbBitmask(val value: Int) {
        COLOR(GL46.GL_COLOR),
        DEPTH(GL46.GL_DEPTH),
        ACCUM(GL46.GL_ACCUM),
        STENCIL(GL46.GL_STENCIL);
        operator fun plus(other: FbBitmask) = this.value or other.value
        operator fun plus(other: Int) = this.value or other
        // Getter
        var i: Int = 0
            get() = this.value
            private set
//        operator fun plus(other: FbBitmask) = FbBitmask(this.value or other.value)

//        operator fun xor(other: Int): FbBitmask {
//            return COLOR_BUFFER_BIT
////            return this.value or b.value
//        }
        companion object {
            fun toGLEnum(bitmasks: Array<FbBitmask>): Int {
                var bitmask = 0
                for (m in bitmasks) {
                    bitmask = bitmask or m.value
                }
                return bitmask
            }
        }
    }

    companion object {
        val defaultFB = FB(0)
        var frameBuffersList: MutableList<FB> = ArrayList()
            private set

        fun bind(target: Target, fb: FB) {
            when (target) {
                Target.READ -> Glob.engine!!.wgl.currReadFB = fb
                Target.DRAW -> Glob.engine!!.wgl.currDrawFB = fb
                Target.FRAMEBUFFER -> {
                    Glob.engine!!.wgl.currReadFB = fb
                    Glob.engine!!.wgl.currDrawFB = Glob.engine!!.wgl.currReadFB
                }
            }
            GL30.glBindFramebuffer(target.value, fb.pid)
//            GL46.glFramebuff
            if (target == Target.DRAW || target == Target.FRAMEBUFFER) {
                if (fb !== defaultFB) {
                    val texAttachmentsCnt = fb.textures.size
                    val attachments = IntArray(texAttachmentsCnt)
                    for (i in 0 until texAttachmentsCnt) {
                        attachments[i] = GL30.GL_COLOR_ATTACHMENT0 + i
                    }
//                    GL46.glNamedFramebufferDrawBuffer(fb.pid, 1, attachments)
                    GL46.glNamedFramebufferDrawBuffers(fb.pid, attachments)
                    GL20.glDrawBuffers(attachments)
                } else {
                    val attachments = intArrayOf(GL11.GL_BACK)
//                    GL20.glDrawBuffers(attachments)
                    GL20.glDrawBuffers(attachments)
                    GL46.glNamedFramebufferDrawBuffer(fb.pid, GL11.GL_BACK)
                }
            }
        }

        // TODO: add 3d
        // TODO: add filtering maybe?
        fun blit(
            readFB: FB = defaultFB, writeFb: FB = defaultFB,
            bitmasks: Array<FbBitmask> = arrayOf(FbBitmask.COLOR)
        ) {

            val pidRead = readFB.pid
            val pidWrite = writeFb.pid
//            val screenRes = IVec2(Global.engine!!.res.x, Global.engine!!.resY)
            val screenRes =  Glob.engine!!.res.copy()
            val resRead: IVec2 = if (readFB === defaultFB) screenRes else readFB.textures[0].res.xy
            val resWrite = if (writeFb === defaultFB) screenRes else writeFb.textures[0].res.xy
//            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, pidRead)
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, pidWrite)
//            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0)
//            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
//            val bitmask = FbBitmask.toGLEnum(bitmasks)
//            GL30.glBlitFramebuffer(
//                0, 0, resRead[0], resRead[1],
//                0, 0, resWrite[0], resWrite[1],
//                bitmask, GL11.GL_NEAREST
//            )
            var bitmask = 0
            for (m in bitmasks) {
                var v: Int = 0
                when (m) {
                    FbBitmask.COLOR -> v = GL_COLOR_BUFFER_BIT
                    FbBitmask.DEPTH -> v = GL_DEPTH_BUFFER_BIT
                    FbBitmask.ACCUM -> v = GL_ACCUM_BUFFER_BIT
                    FbBitmask.STENCIL -> v = GL_STENCIL_BUFFER_BIT
                }
                bitmask = bitmask or v
            }
            GL46.glBlitNamedFramebuffer(
                readFB.pid, writeFb.pid,
                0, 0, resRead[0], resRead[1],
                0, 0, resWrite[0], resWrite[1],
                bitmask, GL11.GL_NEAREST
            )

            // Revert to revious framebuffers.
//            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Glob.engine!!.wgl.currReadFB.pid)
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Glob.engine!!.wgl.currDrawFB.pid)
        }
    }

    // Cursed.
    // Constructor for deault FB only.
    internal constructor(_pid: Int) {
        pid = _pid
    }

    // Constructor for FB


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
    ) {
//        val globalDrawFB = Glob.engine.wgl.currDrawFB
//        val globalReadFB = Glob.engine.wgl.currReadFB
        pid = GL45.glCreateFramebuffers()
        GL45.glNamedFramebufferDrawBuffers(pid, 1,)
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, pid)

        val texAttachmentsCnt = textures.size
        val attachments = IntArray(texAttachmentsCnt)
        for (i in 0 until texAttachmentsCnt) {
            attachments[i] = GL30.GL_COLOR_ATTACHMENT0 + i
        }
        GL46.glNamedFramebufferDrawBuffers(pid, attachments)
        GL20.glDrawBuffers(attachments)

        if (hasDepth) {
            depthTexture = Texture(
                null,
                _width,
                _height,
//                Texture.InternalFormat.DEPTH_COMPONENT,
                Texture.InternalFormat.DEPTH_COMPONENT32F,
                Texture.Format.DEPTH_COMPONENT,
                type
            )
//            GL30.glFramebufferTexture2D(
//                GL30.GL_FRAMEBUFFER,
//                GL30.GL_DEPTH_ATTACHMENT,
//                GL11.GL_TEXTURE_2D,
//                depthTexture!!.pid,
//                0
//            )
            GL46.glNamedFramebufferTexture(
                pid,
                GL46.GL_DEPTH_ATTACHMENT,
                depthTexture!!.pid,
                0
            )
        }
        for (i in 0 until texturesCnt) {
            textures.add(Texture(null, _width, _height, internalFormat, format, type))
//            GL30.glFramebufferTexture2D(
//                GL30.GL_FRAMEBUFFER,
//                GL30.GL_COLOR_ATTACHMENT0 + i,
//                GL11.GL_TEXTURE_2D,
//                textures[i].pid,
//                0
//            )

            GL46.glNamedFramebufferTexture(
                pid,
                GL46.GL_COLOR_ATTACHMENT0 + i,
                textures[i].pid,
                0
            )
        }

        val fbStatus = glCheckNamedFramebufferStatus(pid, GL_FRAMEBUFFER)
        if(fbStatus != GL_FRAMEBUFFER_COMPLETE){
            // TODO: error
            println("Error: FB Incomplete")
        }
//        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, globalReadFB.pid)
//        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, globalDrawFB.pid)
        frameBuffersList.add(this)
    }


    // TODO
    fun clearAllAttachments(
        colour: Vec4 = Vec4(0.0f, 0.0f, 0.0f, 1.0f),
        bitmask: Array<FbBitmask> = arrayOf(
            FbBitmask.COLOR,
            FbBitmask.DEPTH,
//            FbBitmask.STENCIL,
        ),
    ){
        var allAttachmentIndices: IntArray = IntArray(textures.size){i->i}
        clear(
            colour,
            bitmask,
            allAttachmentIndices
        )
    }
    fun clear(
        colour: Vec4 = Vec4(0.0f, 0.0f, 0.0f, 1.0f),
        bitmask: Array<FbBitmask> = arrayOf(
            FbBitmask.COLOR,
            FbBitmask.DEPTH
        ),
        colourAttachments: IntArray = IntArray(1){i->i},
        depthValue: Float = 1.0f,
        stencilValue: Int = 0,
    ) {
        val framebuffer = this


            for (mask in bitmask){
                if (mask == FbBitmask.DEPTH || mask == FbBitmask.STENCIL){
                    // fi is used only for cleaning depth and stencil
                    GL46.glClearNamedFramebufferfi(
                        framebuffer.pid,
                        GL46.GL_DEPTH_STENCIL,
                        0,
                        depthValue,
                        stencilValue
                    )
                }

                if (this != FB.defaultFB) {
                    if (mask == FbBitmask.COLOR) {
                        for (colourAttachment in colourAttachments) {
                            GL46.glClearNamedFramebufferfv(
                                framebuffer.pid,
                                GL46.GL_COLOR,
                                colourAttachment,
                                colour.vals
                            )
                        }
                    }
                } else {
                    GL46.glClearNamedFramebufferfv(
                        framebuffer.pid,
                        FbBitmask.COLOR.value,
                        0,
                        colour.vals
                    )

                }

            }
//                    GL46.glNamedFramebufferDrawBuffer(fb.pid, 1, attachments)

//        val globalFB = Glob.engine!!.wgl.currDrawFB
//        val globalPid = globalFB.pid
//        val isAlreadyBound = globalFB === framebuffer
//
//        if (!isAlreadyBound) {
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer.pid)
//        }
//        GL11.glClearColor(colour.x, colour.y, colour.z, colour.w)
//        GL11.glClear(FbBitmask.toGLEnum(bitmask))
//        if (!isAlreadyBound) {
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Glob.engine!!.wgl.currDrawFB.pid)
//        }

    }

//    fun clear(colour: Vec4 = Vec4(0.0f, 0.0f, 0.0f, 1.0f), bitmask: Int) {
//        val globalFB = Glob.engine!!.wgl.currDrawFB
//        val globalPid = globalFB.pid
//        val isAlreadyBound = globalFB === framebuffer
//        if (!isAlreadyBound) {
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer.pid)
//        }
//        GL11.glClearColor(colour.x, colour.y, colour.z, colour.w)

//        GL11.glClear(bitmask)
//        if (!isAlreadyBound) {
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Glob.engine!!.wgl.currDrawFB.pid)
//        }
//    }

    fun setUniformTextures(uniformName: String) {
        for (i in textures.indices) {
            val tex = textures[i]
            tex.setUniform( uniformName + "_" + ('A'.code + i).toChar())
        }
    }

}