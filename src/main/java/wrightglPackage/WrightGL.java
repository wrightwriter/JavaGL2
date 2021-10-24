package wrightglPackage;

import enginePackage.Engine;
import mainPackage.Global;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import mathPackage.*;


//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class WrightGL {

    public Program currProgram;
    public FB currReadFB;
    public FB currDrawFB;
    private Engine engine;
    public int currTexBindNumber;

    public void updateMatrices(){
        // Update view matrix
        Global.engine.viewMatrix = Mat4.getIdentityMatrix();

        // Update projection matrix
//        float aspectRatio = (Global.engine.resx/Global.engine.resy);
//        aspectRatio = aspectRatio/1.0f;

        Global.engine.projectionMatrix = Mat4.getPerspectiveMatrix(50, 0.001f, 1024.0f, engine.getResX(), engine.getResY());

    }
    public void setSharedUniforms(){
        // Set uniforms
        float Time = (float)(System.currentTimeMillis() - Global.engine.timeStart)/1000.f;
        Global.engine.wgl.setUniform("Time", Time, WrightGL.UniformType.Float);
        float[] res = {Global.engine.getResX(), Global.engine.getResY()};
        Global.engine.wgl.setUniform("R", res, WrightGL.UniformType.Float);
        Global.engine.wgl.setUniform("V", Global.engine.viewMatrix.vals, WrightGL.UniformType.Matrix);
        Global.engine.wgl.setUniform("P", Global.engine.projectionMatrix.vals, WrightGL.UniformType.Matrix);
    }

    static public class Shader{
        public int pid = 0;
        public enum Type {
            VERTEX_SHADER(GL_VERTEX_SHADER),
            FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
            COMPUTE_SHADER(GL_COMPUTE_SHADER),
            GEOMETRY_SHADER(GL_GEOMETRY_SHADER);
            private int value; private Type(int value){ this.value = value; }
        }
        Shader(String fileLocation, Type shaderType){
            try {
                String rootFolder = System.getProperty("user.dir") + "/src/res/shaders/";
                String shaderCode = Files.readString(Paths.get(rootFolder + fileLocation), StandardCharsets.US_ASCII);
                pid = glCreateShader(shaderType.value);
                glShaderSource(pid, shaderCode);
                glCompileShader(pid);
                if (glGetShaderi(pid, GL_COMPILE_STATUS) == 0){
                    assert(false);
                    String errorLog =  glGetShaderInfoLog(pid, 1024) ;
                    // TODO: error
                    System.out.println("Error compiling shader: " + fileLocation  + "\n"+  glGetShaderInfoLog(pid, 1024) ); ;
                }
            } catch (IOException e){
                assert(false);
                // yoo
            }

    }
}
    static public class Program {
        public int pid = 0;
        Shader fragShader;
        Shader vertShader;
        public Program( final String _fileNameVert, final String _fileNameFrag){
            String errorStr;

            Shader vertShader = new Shader(_fileNameVert, Shader.Type.VERTEX_SHADER);
            Shader fragShader = new Shader(_fileNameFrag, Shader.Type.FRAGMENT_SHADER);

            pid = glCreateProgram();
            if (pid < 0) {
                // TODO Error
                assert (false);
            }

            glAttachShader(pid, vertShader.pid);
            glAttachShader(pid, fragShader.pid);

            glLinkProgram(pid);

            if (glGetProgrami(pid, GL_LINK_STATUS) == 0) {
                assert (false);
                // TODO: throw error
                errorStr = "Error linking Shader code: " + glGetProgramInfoLog(pid, 1024);
            }

            if (vertShader.pid == 0) {
                glDetachShader(pid, vertShader.pid);
            }
            if (fragShader.pid == 0) {
                glDetachShader(pid, fragShader.pid);
            }

            glValidateProgram(pid);
            if (glGetProgrami(pid, GL_VALIDATE_STATUS) == 0) {
                // TODO: throw error;
                System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(pid, 1024));
            }
        }
        public void use(){
            Global.engine.wgl.currProgram = this;
            Global.engine.wgl.currTexBindNumber = 0;
            Global.engine.wgl.setSharedUniforms();
            glUseProgram(pid);
        }
    }
    static public class ComputeProgram extends WrightGL.Program {
        Shader computeShader;
        WrightGL.Texture texture;
        int workGroupSize;
        int workGroupCount;
        public void dispatch(){

        }
        public ComputeProgram(String _fileNameVert, String _fileNameFrag) {
            super(_fileNameVert, _fileNameFrag);
        }
    }

    public enum UniformType {
        Float,
        Int,
        Matrix
    }

    static public class Texture {
        String filePath = null;
        InternalFormat internalFormat;
        Format format;
        Type type;
        int pid;
        int[] res = new int[2];
        String name;
        boolean isWritable = false;

        public enum InternalFormat{
            RGBA32F(GL_RGBA32F),
            RGBA(GL_RGBA),
            RGB(GL_RGB),
            RG(GL_RG),
            R(GL_R),
            DEPTH_COMPONENT(GL_DEPTH_COMPONENT) ; private int value; private InternalFormat(int value){ this.value = value; }
        }
        public enum Format{
            RGBA(GL_RGBA),
            RGB(GL_RGB),
            RG(GL_RG),
            R(GL_R),
            DEPTH_COMPONENT(GL_DEPTH_COMPONENT) ;
            private int value; private Format(int value){ this.value = value; }
        }
//        public enum Filter{
//            RGBA(GL_RGBA),
//            DEPTH_COMPONENT(GL_DEPTH_COMPONENT) ;
//            private int value; private Format(int value){ this.value = value; }
//        }
        public enum Type{
            FLOAT(GL_FLOAT),
            UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
        ; private int value; private Type(int value){ this.value = value; }
        }
        public Texture(FloatBuffer data, int resx , int resy , InternalFormat internalFormat /*= GL_RGBA32F*/, Format format /*= GL_RGBA*/, Type type /*= GL_FLOAT*/){
                // set default parameters
                internalFormat = internalFormat == null ? InternalFormat.RGBA32F : internalFormat;
                format = format == null ? Format.RGBA : format;
                type = type == null ? Type.FLOAT : type;

                // set res
                res[0] = resx;
                res[1] = resy;

                pid = glCreateTextures(GL_TEXTURE_2D);

                glBindTexture(GL_TEXTURE_2D, pid);

                if (data != null)
                    glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.value, resx, resy, 0, format.value, type.value, data);
                else{
                    final float[] nullPx = null;
                    // TODO: check if this works.
                    glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.value, resx, resy, 0, format.value, type.value, nullPx);
                }
                // TODO
                // THIS SHIT GONNA BREAK RIGHT HERE

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                glBindTexture(GL_TEXTURE_2D, 0);
        }
        public Texture(String _filePath){
            filePath = _filePath;
//            ByteBuffer image = stbi_load(filePath)
            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer channels = BufferUtils.createIntBuffer(1);
            ByteBuffer image = stbi_load(filePath, width, height, channels, 0);
            if (image == null){
                // TODO: Error
                assert false : "Image not found" ;
            } else {
                type = Type.UNSIGNED_BYTE;
                res[0] = width.get(0);
                res[1] = height.get(0);
                if (channels.get(0) == 3) {
                    internalFormat = InternalFormat.RGB;
                    format = Format.RGB;
                } else if (channels.get(0) == 4) {
                    internalFormat = InternalFormat.RGBA;
                    format = Format.RGBA;
                } else {
                    assert false : "Error: (Texture) Unknown number of channesl '" + channels.get(0) + "'";
                }

                glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.value, res[0], res[1],
                        0, format.value, type.value, image);
            }
            stbi_image_free(image);
        }
        public void setUniform(int bindNumber, String name){
            Program program = Global.engine.wgl.currProgram;
            if (bindNumber < 0) {
                Global.engine.wgl.currTexBindNumber++;
                bindNumber = Global.engine.wgl.currTexBindNumber;
            }
            int loc = glGetUniformLocation(program.pid, name);
            if (isWritable)
                glBindImageTexture(bindNumber, pid, 0, false, 0, GL_READ_WRITE, this.format.value);
            else {
                glActiveTexture(GL_TEXTURE0 + bindNumber);
                glBindTexture(GL_TEXTURE_2D, pid);
            }
            glUniform1i(loc, bindNumber);
        }
        public void clear(){
            IntBuffer zero = IntBuffer.allocate(1);
            zero.put(1);
            zero.rewind();
            glClearTexImage(pid, 0, format.value,type.value, zero);
        }

    }
    static public class FB {
        public int pid;
        public List<Texture> textures = new ArrayList<Texture>();
        String name = null;
        public Texture depthTexture;
//        static Framebuffer DefaultFramebuffer;
        public enum Target {
            FRAMEBUFFER(GL_FRAMEBUFFER),
            DRAW_FRAMEBUFFER(GL_DRAW_FRAMEBUFFER),
            READ_FRAMEBUFFER(GL_READ_FRAMEBUFFER),
            ; private final int value; private Target(int value){ this.value = value; }
        }
        public enum FbBitmask {
            COLOR_BUFFER_BIT(GL_COLOR_BUFFER_BIT),
            DEPTH_BUFFER_BIT(GL_DEPTH_BUFFER_BIT),
            ACCUM_BUFFER_BIT(GL_ACCUM_BUFFER_BIT),
            STENCIL_BUFFER_BIT(GL_STENCIL_BUFFER_BIT),
            ; private final int value; private FbBitmask(int value){ this.value = value; }
        }
        public FB(int texturesCnt, boolean hasDepth){
            this(
                Global.engine.getResX(), Global.engine.getResY(), 1, true,
                null, null, null, false);
        }
        public FB(int _width, int _height, int texturesCnt, boolean hasDepth, Texture.InternalFormat internalFormat, Texture.Format format, Texture.Type type, boolean renderbuffer){
            pid = glCreateFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, pid);

            if (hasDepth){
                depthTexture = new Texture(null, _width, _height, Texture.InternalFormat.DEPTH_COMPONENT, Texture.Format.DEPTH_COMPONENT, type);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.pid, 0);
            }
            for(int i = 0; i < texturesCnt; i++){
                textures.add(new Texture(null, _width, _height, internalFormat, format, type));
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, textures.get(i).pid, 0);
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
        public static void bind(Target target, FB fb){
            if ( target == Target.READ_FRAMEBUFFER)
                Global.engine.wgl.currReadFB = fb;
            else if ( target == Target.DRAW_FRAMEBUFFER)
                Global.engine.wgl.currDrawFB = fb;
            else if ( target == Target.FRAMEBUFFER)
                Global.engine.wgl.currDrawFB = Global.engine.wgl.currReadFB = fb;

            glBindFramebuffer(target.value, fb == null ? 0 : fb.pid);
            if(target == Target.DRAW_FRAMEBUFFER || target == Target.FRAMEBUFFER){
                if (fb != null){
                    final int texAttachmentsCnt = fb.textures.size();
                    final int[] attachments = new int[texAttachmentsCnt];
                    for (int i = 0; i < texAttachmentsCnt; i++){
                        attachments[i] = GL_COLOR_ATTACHMENT0 + i;
                    }
                    glDrawBuffers(attachments);
                } else {
                    final int[] attachments = {GL_BACK};
                    glDrawBuffers(attachments);
                }
            }
        }

        public void setUniformTextures(String uniformName) {
            for(int i = 0; i < textures.size(); i++ ){
                Texture tex = textures.get(i);
                tex.setUniform(-1, uniformName + (char)('A' + i));
            }
        }
        // TODO: add filtering maybe?
        public static void blitColour(FB read, FB write){
            blit(read, write, new FB.FbBitmask[]{ FB.FbBitmask.COLOR_BUFFER_BIT });
        }
        public static void blit(FB read, FB write, FbBitmask[] bitmasks){
            int pidRead = read == null ? 0 : read.pid;
            int pidWrite = write == null ? 0 : write.pid;

            int[] screenRes = {Global.engine.getResX(),Global.engine.getResY()};

            int[] resRead = read == null ? screenRes : read.textures.get(0).res;
            int[] resWrite = write == null ? screenRes : write.textures.get(0).res;

            glBindFramebuffer(GL_READ_FRAMEBUFFER, pidRead);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, pidWrite);

            glReadBuffer(GL_COLOR_ATTACHMENT0);
            glDrawBuffer(GL_COLOR_ATTACHMENT0);

//            if ()
//            glDrawBuffer(GL_FRONT);
            int bitmask = 0;
            for (FbBitmask m: bitmasks){
                bitmask = bitmask | m.value;
            }

            glBlitFramebuffer(
                    0,0,resRead[0], resRead[1],
                    0,0,resWrite[0], resWrite[1],
                    bitmask, GL_NEAREST
            );

            // Revert to revious framebuffers.
            glBindFramebuffer(GL_READ_FRAMEBUFFER, Global.engine.wgl.currReadFB == null ? 0 : Global.engine.wgl.currReadFB.pid);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, Global.engine.wgl.currDrawFB == null ? 0 : Global.engine.wgl.currDrawFB.pid);

        }
    }
    public static class VB {
        int pid;
        String name;
        FloatBuffer data;
        Type type;
        public VertexCulling culling = VertexCulling.BACK;
        PrimitiveType primitiveType = PrimitiveType.TRIANGLES;
        int[] signature;
        int vertSize;
        int vertCnt;

        public enum VertexCulling{
            FRONT(GL_FRONT), BACK(GL_BACK), DISABLED(GL_NONE) ; private final int value; VertexCulling(int value){ this.value = value; }
        }

        public enum Type{
            FLOAT(GL_FLOAT); private final int value; Type(int value){ this.value = value; }
        }

        // SHOULDN'T BE HERE
        public enum PrimitiveType{
            TRIANGLES_STRIP(GL_TRIANGLE_STRIP), TRIANGLES(GL_TRIANGLES), POINTS(GL_TRIANGLES);
            private int value; private PrimitiveType(int value){ this.value = value; }
        }

        public VB(final float[] _geometry, final int[] _signature, PrimitiveType _primitiveType){
            primitiveType = _primitiveType == null ? primitiveType : _primitiveType;

            signature = _signature;
            vertSize = 0;
            for (int j : signature) {
                vertSize += j;
            }
            vertCnt = _geometry.length/vertSize;


            type = Type.FLOAT;

            pid = glCreateBuffers(); // gl.glGenBuffers(1, vertexBuffer);

            // Create vertex buffer.

            glBindBuffer(GL_ARRAY_BUFFER, pid);
            glBufferData(GL_ARRAY_BUFFER,  _geometry, GL_STATIC_DRAW);
//            glBufferData(GL_ARRAY_BUFFER, data.capacity() * Float.BYTES, data, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

        public void render(){
            int oldCulling = glGetInteger(GL_CULL_FACE_MODE);
            glCullFace(culling.value);

            glBindBuffer(GL_ARRAY_BUFFER, pid);
            for (int i = 0; i < signature.length; i++){
                glVertexAttribPointer(
                        i,signature[i],Type.FLOAT.value,
                        false, vertSize * Float.BYTES, 0);
                glEnableVertexAttribArray(i);
            }

            glDrawArrays(primitiveType.value, 0,  vertCnt);

            glCullFace(oldCulling);
        }
    }

    public void useProgram(Program _program ){
        glUseProgram(_program.pid);
        currProgram = _program;
    }
    public void setUniform(String _name, float[] _value, WrightGL.UniformType _type){
        final int loc = glGetUniformLocation(currProgram.pid, _name);
        if (loc < 0){
//            System.out.println("Error: loc location not found.");
        }
        final int len = _value.length;
        if(_type == WrightGL.UniformType.Matrix){
            glUniformMatrix4fv(loc,false,_value);
        } else if (_type == WrightGL.UniformType.Int){
            if (len == 1){
                glUniform1i(loc,(int)_value[0]);
            } else if (len == 2){
                glUniform2i(loc,(int)_value[0], (int)_value[1]);
            } else if (len == 3){
                glUniform3i(loc,(int)_value[0], (int)_value[1] ,(int)_value[2]);
            }
        } else if (_type == WrightGL.UniformType.Float){
            if (len == 1){
                glUniform1f(loc,_value[0]);
            } else if (len == 2){
                glUniform2f(loc,_value[0], _value[1]);
            } else if (len == 3){
                glUniform3f(loc,_value[0], _value[1] ,_value[2]);
            }
        }
    }
    public void setUniform(String _name, float _value, WrightGL.UniformType _type){
        final int loc = glGetUniformLocation(currProgram.pid, _name);
        if (loc < 0){
//            System.out.println("Error: loc location not found.");
        }
        if (_type == WrightGL.UniformType.Matrix){
            // TODO: THROW ERROR
        } else if (_type == WrightGL.UniformType.Int){
            glUniform1i(loc,(int)_value);
        } else if (_type == WrightGL.UniformType.Float){
            glUniform1f(loc,_value);
        }
    }
    public void setUniformTexture(Texture texture, boolean isWritable, int bindNumber, String uniformName){
        final int loc = glGetUniformLocation(currProgram.pid, uniformName);
        glBindImageTexture(bindNumber, texture.pid, 0, false, 0, GL_READ_WRITE,texture.format.value);
        glActiveTexture(texture.pid);
        glBindTexture(GL_TEXTURE_2D, texture.pid);
        glUniform1i(loc, bindNumber);
    }



    public WrightGL(Engine _engine){
        engine = _engine;
//        initDebug();
        initGL();
        initVertexArray();
    }

    private void initGL() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.getCapabilities();

        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback( (
                source, type, id, severity, length, message, userParam)->{
            assert(false);
            System.out.printf("bruh");
        },0);
        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, true);

        // Create VAO

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        int vao = glCreateVertexArrays();
        glBindVertexArray(vao);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);



//        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream.A);
//        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.COLOR, Semantic.Stream.A);
//
//        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 2, GL_FLOAT, false, 0);
//        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.COLOR, 3, GL_FLOAT, false, 2 * 4);
//
//        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.POSITION);
//        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.COLOR);
//
//        gl.glVertexArrayElementBuffer(vertexArrayName.get(0), bufferName.get(Buffer.ELEMENT));
//        gl.glVertexArrayVertexBuffer(vertexArrayName.get(0), Semantic.Stream.A, bufferName.get(Buffer.VERTEX), 0, (2 + 3) * 4);


//        IntBuffer vertexBuffer = GLBuffers.newDirectIntBuffer(1);
//        // map the transform buffers and keep them mapped
//        ByteBuffer globalMatricesPointer = gl.glMapNamedBufferRange(
//                bufferName.get(Buffer.GLOBAL_MATRICES),
//                0,
//                16 * 4 * 2,
//                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT); // flags
//
//        modelMatrixPointer = gl.glMapNamedBufferRange(
//                bufferName.get(Buffer.MODEL_MATRIX),
//                0,
//                16 * 4,
//                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
    }
    private void initVertexArray() {
//
    }

}
