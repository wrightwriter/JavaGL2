package _0_1.wrightglPackage

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.ptr.PointerByReference
import java.lang.AutoCloseable

interface RenderdocLibrary : Library {
    fun RENDERDOC_GetAPI(version: Int, out: PointerByReference?): Int
}

class Renderdoc : AutoCloseable {
    private enum class RenderdocVersion(  // RENDERDOC_API_1_1_1 = 1 01 01
        public val value: Int
    ) {
        eRENDERDOC_API_Version_1_0_0(10000),  // RENDERDOC_API_1_0_0 = 1 00 00
        eRENDERDOC_API_Version_1_0_1(10001),  // RENDERDOC_API_1_0_1 = 1 00 01
        eRENDERDOC_API_Version_1_0_2(10002),  // RENDERDOC_API_1_0_2 = 1 00 02
        eRENDERDOC_API_Version_1_1_0(10100),  // RENDERDOC_API_1_1_0 = 1 01 00
        eRENDERDOC_API_Version_1_4_1(10401), eRENDERDOC_API_Version_1_1_1(10101);
    }

    var renderdocAttached = false
    var instance: RenderdocLibrary
    override fun close() {
        assert(true)
    }

    init {
        val libspath = System.getProperty("jna.library.path")
        //        libspath = libspath + ";D:/Programming/School/JavaGL2/src";
//        System.setProperty("jna.library.path",libspath);
//        System.setProperty("jna.library.path","D:/Programming/School/JavaGL2/src");
        instance = Native.loadLibrary("renderdoc", RenderdocLibrary::class.java) as RenderdocLibrary
        val p = PointerByReference()
        instance.RENDERDOC_GetAPI(RenderdocVersion.eRENDERDOC_API_Version_1_4_1.value, p)
    }
}