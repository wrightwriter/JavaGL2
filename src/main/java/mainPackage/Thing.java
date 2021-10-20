package mainPackage;

import WrightGL.WrightGL;
import WrightGL.Geometry;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.jogamp.opengl.GL.*;

public class Thing {
    public WrightGL.Program shaderProgram;
    public List<WrightGL.VertexBuffer> vertexBuffers = new ArrayList<WrightGL.VertexBuffer>();
//    WrightGL.VertexBuffer vertexBuffer;
    public float[] modelMatrix;
    Consumer<Thing> modelMatrixCallback = null;

    public Thing(final WrightGL.Program _program, WrightGL.VertexBuffer _vertexBuffer){
        final GL4 gl = Global.engine.WGL.gl;
        shaderProgram = _program;
        vertexBuffers.add(_vertexBuffer);
        FloatUtil.makeIdentity(modelMatrix = new float[16]);
    }
    void setCallback(Consumer<Thing> _modelMatrixCallback){
        modelMatrixCallback = _modelMatrixCallback;
    }
    public void render(WrightGL.Program _program){
        final GL4 gl = Global.engine.WGL.gl;
        // Shader
        final WrightGL.Program currProgram = _program != null ? _program : shaderProgram;
        Global.engine.WGL.useProgram(currProgram);

        // Update model matrix
        if (modelMatrixCallback != null)
            modelMatrixCallback.accept(this);

        // Uniforms
        Global.engine.WGL.setSharedUniforms();
        Global.engine.WGL.setUniform("M", modelMatrix, WrightGL.UniformType.Matrix);

        // Render
        for (WrightGL.VertexBuffer buffer: vertexBuffers){
            buffer.render();
        }
    }
}
