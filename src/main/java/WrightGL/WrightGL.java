package WrightGL;


import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderCode;
import mainPackage.Engine;
import mainPackage.Global;


import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


public class WrightGL {
    public final GL4 gl = Global.gl;

    public Program currProgram;
    public Framebuffer currFramebuffer;

    public void updateMatrices(){
        // Update view matrix
        float[] view = FloatUtil.makeIdentity(new float[16]);
        {
            for (int i = 0; i < 16; i++)
//            Global.engine.globalMatricesPointer.putFloat( i * 4, view[i]);
                Global.engine.viewMatrix[i] = view[i];
        }


        // Update projection matrix
        float aspectRatio = (Engine.window.getWidth()/Engine.window.getHeight());
        aspectRatio = aspectRatio/1.0f;
        FloatUtil.makePerspective(Global.engine.projectionMatrix, 0, false, 1, aspectRatio, 0.001f,1024 );

    }
    public void setSharedUniforms(){
        // Set uniforms
        float Time = (float)(System.currentTimeMillis() - Global.engine.start)/1000.f;
        Global.engine.WGL.setUniform("Time", Time, WrightGL.UniformType.Float);
        float[] res = {Engine.window.getWidth(), Engine.window.getHeight()};
        Global.engine.WGL.setUniform("R", res, WrightGL.UniformType.Float);
        Global.engine.WGL.setUniform("V", Global.engine.viewMatrix, WrightGL.UniformType.Matrix);
        Global.engine.WGL.setUniform("P", Global.engine.projectionMatrix, WrightGL.UniformType.Matrix);
    }

    static public class Program {
        public int pid = 0;
        public final String rootFolder = "shaders";
        public Program( final String _fileNameVert, final String _fileNameFrag){
//            final URL resource = WrightGL.class.getResource("");

//            final URLConnection conn = IOUtil.getResource(WrightGL.class, "/shaders/passthrough.vp");
//            final URLConnection conn = IOUtil.getResource(WrightGL.class, "../../shaders/passthrough.vp");


            ShaderCode vertShader = ShaderCode.create(
                    Global.engine.WGL.gl,
                    GL_VERTEX_SHADER,
                    WrightGL.class,
                    rootFolder,
                    rootFolder + "bin/",
                    _fileNameVert,
                    false);
            ShaderCode fragShader = ShaderCode.create(
                    Global.engine.WGL.gl,
                    GL_FRAGMENT_SHADER,
                    WrightGL.class,
                    rootFolder,
                    rootFolder + "bin",
                    _fileNameFrag,
                    false);

            ShaderProgram shaderProgram = new ShaderProgram();
            shaderProgram.add(vertShader);
            shaderProgram.add(fragShader);
            shaderProgram.init(Global.engine.WGL.gl);

            pid = shaderProgram.program();

            shaderProgram.link(Global.engine.WGL.gl,System.err);
        }
    }

    public enum UniformType {
        Float,
        Int,
        Matrix
    }

    static public class Texture {
        public enum InternalFormat{
            RGBA32F(GL_RGBA32F),
            DEPTH_COMPONENT(GL_DEPTH_COMPONENT) ; private int value; private InternalFormat(int value){ this.value = value; }
        }
        public enum Format{
            RGBA(GL_RGBA),
            DEPTH_COMPONENT(GL_DEPTH_COMPONENT) ;
            private int value; private Format(int value){ this.value = value; }
        }
        public enum Type{
            FLOAT(GL_FLOAT); private int value; private Type(int value){ this.value = value; }
        }
        public Texture(FloatBuffer data , int resx , int resy , InternalFormat internalFormat /*= GL_RGBA32F*/, Format format /*= GL_RGBA*/, Type type /*= GL_FLOAT*/){
                // set default parameters
                internalFormat = internalFormat == null ? InternalFormat.RGBA32F : internalFormat;
                format = format == null ? Format.RGBA : format;
                type = type == null ? Type.FLOAT : type;

                // set res
                res[0] = resx;
                res[1] = resy;

                GL4 gl = Global.engine.WGL.gl;

                int[] tempPid = new int[1];
                gl.glCreateTextures(GL_TEXTURE_2D, 1, tempPid,0);
                pid = tempPid[0];

                gl.glBindTexture(GL_TEXTURE_2D, pid);

                if (data != null)
                    gl.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.value, resx, resy, 0, format.value, type.value, data);
                else
                    gl.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.value, resx, resy, 0, format.value, type.value, null);

                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                gl.glBindTexture(GL_TEXTURE_2D, 0);
        }
        public void clear(){
            GL4 gl = Global.engine.WGL.gl;
            IntBuffer zero = IntBuffer.allocate(1);
            zero.put(1);
            zero.rewind();
            gl.glClearTexImage(pid, 0, format.value,type.value, zero);
        }
        InternalFormat internalFormat;
        Format format;
        Type type;
        int pid;
        int[] res = new int[2];
        String name;
    }
    static public class Framebuffer{
        public int pid;
        public List<Texture> textures = new ArrayList<Texture>();
        String name = null;
        Texture depthTexture;
        public Framebuffer(int _width, int _height, int texturesCnt, boolean hasDepth, Texture.InternalFormat internalFormat, Texture.Format format, Texture.Type type, boolean renderbuffer){
            GL4 gl = Global.engine.WGL.gl;
            int[] tempPid = new int[1];
            gl.glCreateFramebuffers(1, tempPid, 0);
            pid = tempPid[0];
            gl.glBindFramebuffer(GL_FRAMEBUFFER, pid);

            if (hasDepth){
                depthTexture = new Texture(null, _width, _height, Texture.InternalFormat.DEPTH_COMPONENT, Texture.Format.DEPTH_COMPONENT, type);
            }
            for(int i = 0; i < texturesCnt; i++){
                textures.add(new Texture(null, _width, _height, internalFormat, format, type));
                gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, textures.get(i).pid, 0);
            }
            gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
        public static void blit(Framebuffer read, Framebuffer write){
            GL4 gl = Global.engine.WGL.gl;
            int pidRead = read == null ? 0 : read.pid;
            int pidWrite = write == null ? 0 : write.pid;

            int[] screenRes = {Engine.window.getWidth(),Engine.window.getHeight()};

            int[] resRead = read == null ? screenRes : read.textures.get(0).res;
            int[] resWrite = write == null ? screenRes : write.textures.get(0).res;

            gl.glBindFramebuffer(GL_READ_FRAMEBUFFER, pidRead);
            gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, pidWrite);

            gl.glBlitFramebuffer(
                    0,0,resRead[0], resRead[1],
                    0,0,resWrite[0], resWrite[1],
                    GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST
            );
        }
    }
    public static class VertexBuffer {
        int pid;
        String name;
        FloatBuffer data;
        Type type;
        PrimitiveType primitiveType = PrimitiveType.TRIANGLES;
        int[] signature;
        int vertSize;
        int vertCnt;

        public enum Type{
            FLOAT(GL_FLOAT); private int value; private Type(int value){ this.value = value; }
        }
        // SHOULDN'T BE HERE
        public enum PrimitiveType{
            TRIANGLES_STRIP(GL_TRIANGLE_STRIP), TRIANGLES(GL_TRIANGLES), POINTS(GL_TRIANGLES);
            private int value; private PrimitiveType(int value){ this.value = value; }
        }
        public VertexBuffer(final float[] _geometry, final int[] _signature, PrimitiveType _primitiveType){
            final GL4 gl = Global.engine.WGL.gl;

            primitiveType = _primitiveType == null ? primitiveType : _primitiveType;

            signature = _signature;
            vertSize = 0;
            for (int i = 0; i < signature.length; i++){
                vertSize += signature[i];
            }
            vertCnt = _geometry.length/vertSize;


            type = Type.FLOAT;
            data = GLBuffers.newDirectFloatBuffer( _geometry );

            IntBuffer tempPid = GLBuffers.newDirectIntBuffer(1);
            gl.glCreateBuffers(1, tempPid); // gl.glGenBuffers(1, vertexBuffer);
            pid = tempPid.get(0);

            // Create vertex buffer.
//            shaderProgram = _program;

            gl.glBindBuffer(GL_ARRAY_BUFFER, pid);
            gl.glBufferData(GL_ARRAY_BUFFER, data.capacity() * Float.BYTES, data, GL_STATIC_DRAW);
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        public void render(){
            final GL4 gl = Global.engine.WGL.gl;

            gl.glBindBuffer(GL_ARRAY_BUFFER, pid);
            for (int i = 0; i < signature.length; i++){
                gl.glVertexAttribPointer(
                        i,signature[i],Type.FLOAT.value,
                        false, vertSize * Float.BYTES, 0);
                gl.glEnableVertexAttribArray(i);
            }

            gl.glDrawArrays(GL_TRIANGLES, 0,  vertCnt);
        }
    }

    public void useProgram(Program _program ){
        gl.glUseProgram(_program.pid);
        currProgram = _program;
    }
    public void setUniform(String _name, float[] _value, WrightGL.UniformType _type){
        final int loc = gl.glGetUniformLocation(currProgram.pid, _name);
        if (loc < 0){
            System.out.println("Error: loc location not found.");
        }
        final int len = _value.length;
        if(_type == WrightGL.UniformType.Matrix){
            gl.glUniformMatrix4fv(loc,1,false,_value, 0);
        } else if (_type == WrightGL.UniformType.Int){
            if (len == 1){
                gl.glUniform1i(loc,(int)_value[0]);
            } else if (len == 2){
                gl.glUniform2i(loc,(int)_value[0], (int)_value[1]);
            } else if (len == 3){
                gl.glUniform3i(loc,(int)_value[0], (int)_value[1] ,(int)_value[2]);
            }
        } else if (_type == WrightGL.UniformType.Float){
            if (len == 1){
                gl.glUniform1f(loc,_value[0]);
            } else if (len == 2){
                gl.glUniform2f(loc,_value[0], _value[1]);
            } else if (len == 3){
                gl.glUniform3f(loc,_value[0], _value[1] ,_value[2]);
            }
        }
    }
    public void setUniform(String _name, float _value, WrightGL.UniformType _type){
        final int loc = gl.glGetUniformLocation(currProgram.pid, _name);
        if (loc < 0){
            System.out.println("Error: loc location not found.");
        }
        if (_type == WrightGL.UniformType.Matrix){
            // TODO: THROW ERROR
        } else if (_type == WrightGL.UniformType.Int){
            gl.glUniform1i(loc,(int)_value);
        } else if (_type == WrightGL.UniformType.Float){
            gl.glUniform1f(loc,_value);
        }
    }
    public void setUniformTexture(Texture texture, boolean isWritable, int bindNumber, String uniformName){
        final int loc = gl.glGetUniformLocation(currProgram.pid, uniformName);
        gl.glBindImageTexture(bindNumber, texture.pid, 0, false, 0, GL_READ_WRITE,texture.format.value);
        gl.glActiveTexture(texture.pid);
        gl.glBindTexture(GL_TEXTURE_2D, texture.pid);
        gl.glUniform1i(loc, bindNumber);
    }



    public WrightGL(){
        initDebug();
        initGL();
        initVertexArray();
    }

    private void initDebug() {
        Global.engine.window.getContext().addGLDebugListener(new GLDebugListener() {
            @Override
            public void messageSent(GLDebugMessage event) {
                System.out.println(event);
            }
        });
//        gl.glDebugMessageControl( GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, null, false);
//        gl.glDebugMessageControl( GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_HIGH, 0, null, true);
//        gl.glDebugMessageControl( GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_MEDIUM, 0, null, true);

    }

    private void initGL() {
        // Create VAO
        IntBuffer vertexArray = GLBuffers.newDirectIntBuffer(1);
        gl.glCreateVertexArrays(1, vertexArray);
        gl.glBindVertexArray(vertexArray.get(0));
        gl.glEnable(GL_CULL_FACE);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_TEXTURE_2D);



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
