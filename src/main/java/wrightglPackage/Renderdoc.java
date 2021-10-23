package wrightglPackage;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;

interface RenderdocLibrary extends Library {
    int RENDERDOC_GetAPI(int version, PointerByReference out);
}


public class Renderdoc implements AutoCloseable {
    private enum RenderdocVersion{
        eRENDERDOC_API_Version_1_0_0(10000),    // RENDERDOC_API_1_0_0 = 1 00 00
        eRENDERDOC_API_Version_1_0_1(10001),    // RENDERDOC_API_1_0_1 = 1 00 01
        eRENDERDOC_API_Version_1_0_2(10002),    // RENDERDOC_API_1_0_2 = 1 00 02
        eRENDERDOC_API_Version_1_1_0( 10100),    // RENDERDOC_API_1_1_0 = 1 01 00
        eRENDERDOC_API_Version_1_4_1( 10401),
        eRENDERDOC_API_Version_1_1_1(10101);    // RENDERDOC_API_1_1_1 = 1 01 01
        private int value;
        private RenderdocVersion(int value) { this.value = value; }
    }

    boolean renderdocAttached = false;

    RenderdocLibrary instance;

    public Renderdoc (){
        String libspath = System.getProperty("jna.library.path");
//        libspath = libspath + ";D:/Programming/School/JavaGL2/src";
//        System.setProperty("jna.library.path",libspath);
//        System.setProperty("jna.library.path","D:/Programming/School/JavaGL2/src");

        instance = (RenderdocLibrary)Native.loadLibrary("renderdoc", RenderdocLibrary.class);
        PointerByReference p = new PointerByReference();
        instance.RENDERDOC_GetAPI(RenderdocVersion.eRENDERDOC_API_Version_1_4_1.value, p);

    }

    @Override
    public void close() {
        assert(true);
    }
}
