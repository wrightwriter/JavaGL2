package _0_1.engine

//import uk.co.caprica.vlcj.binding.internal.ReportSizeChanged;
//import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
//import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
//import uk.co.caprica.vlcj.player.embedded.videosurface.VideoEngineVideoSurface;
//import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngine;
//import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallback;
//import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallbackAdapter;

//import uk.co.caprica.vlcj.binding.internal.Report
//import uk.co
//import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
//import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
//import uk.co.caprica.vlcj.player.embedded.videosurface.VideoEngineVideoSurface;
//import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngine;
//import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallback;
//import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallbackAdapter;
//

import _0_1.main.Global
import com.sun.jna.Pointer
import org.lwjgl.glfw.GLFW.*
import uk.co.caprica.vlcj.binding.internal.ReportSizeChanged
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngine
import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallbackAdapter
import java.awt.SystemColor.window
import java.util.concurrent.Semaphore





class Video{
    private val videoEngineCallback: VideoEngineCallback = VideoEngineHandler()
    val contextSemaphore = Semaphore(0, true)
    val mediaPlayerFactory = MediaPlayerFactory("--quiet")
    val mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer()
    val videoSurface = mediaPlayerFactory.videoSurfaces().newVideoSurface(VideoEngine.libvlc_video_engine_opengl, videoEngineCallback)

    val mrl = "Potato.mp4"
    init{
        this.mediaPlayer.videoSurface().set(videoSurface);
        mediaPlayer.media().play(mrl);

    }

    val enableResize = true
    val preserveAspectRatio = true


    inner class VideoEngineHandler : VideoEngineCallbackAdapter() {
        override fun onGetProcAddress(opaque: Pointer, functionName: String): Long {
            return glfwGetProcAddress(functionName)
        }

        override fun onMakeCurrent(opaque: Pointer, enter: Boolean): Boolean {
            if (enter) {
                try {
                    contextSemaphore.acquire()
                    glfwMakeContextCurrent(Global.engine.glfwWindow)
                } catch (e: InterruptedException) {
                    return false
                } catch (e: Exception) {
                    glfwMakeContextCurrent(0L)
                    contextSemaphore.release()
                    return false
                }
            } else {
                try {
                    glfwMakeContextCurrent(0L)
                } finally {
                    contextSemaphore.release()
                }
            }
            return true
        }

        override fun onSwap(opaque: Pointer?) {
            glfwSwapBuffers(Global.engine.glfwWindow)
        }

//        override fun onSetResizeCallback(
//            opaque: Pointer?,
//            report_size_change: ReportSizeChanged,
//            report_opaque: Pointer
//        ) {
//            // Stash the callback and the opaque reference - the opaque reference MUST be passed when invoking the
//            // callback
//            reportSizeChanged = report_size_change
//            reportOpaque = report_opaque
//
//            // FIXME is it ok to do this here and call back into the native library on this thread, also outside of any
//            //       GLFW context - it seems to work... but it feels like this should be synchronized - in theory it's
//            //       possible that the reportSizeChanged callback could become invalidated while processing this?
//            if (reportSizeChanged != null && window != 0) {
//                val w = intArrayOf(0)
//                val h = intArrayOf(0)
//                glfwGetWindowSize(window, w, h)
//                reportSizeChanged.reportSizeChanged(report_opaque, w[0], h[0])
//            }
//        }
    }
}