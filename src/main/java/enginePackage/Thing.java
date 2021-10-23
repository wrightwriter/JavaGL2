package enginePackage;

import wrightglPackage.WrightGL;
import mathPackage.Mat4;
import mainPackage.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Thing {
    public WrightGL.Program shaderProgram;
    public List<WrightGL.VertexBuffer> vertexBuffers = new ArrayList<WrightGL.VertexBuffer>();
//    WrightGL.VertexBuffer vertexBuffer;
    public Mat4 modelMatrix;
    Consumer<Thing> callback = null;

    public Thing(final WrightGL.Program _program, WrightGL.VertexBuffer _vertexBuffer){
        shaderProgram = _program;
        vertexBuffers.add(_vertexBuffer);
        modelMatrix = Mat4.getIdentityMatrix();
    }
    public void setCallback(Consumer<Thing> _modelMatrixCallback){
        callback = _modelMatrixCallback;
    }
    public void render(WrightGL.Program _program, Consumer<Thing> innerCallback){
        // Shader
        final WrightGL.Program currProgram = _program != null ? _program : shaderProgram;
        currProgram.use();

        if (innerCallback != null)
            innerCallback.accept(this);

        // Update model matrix
        if (callback != null)
            callback.accept(this);

        // Uniforms
        Global.engine.WGL.setUniform("M", modelMatrix.vals, WrightGL.UniformType.Matrix);

        // Render
        for (WrightGL.VertexBuffer buffer: vertexBuffers){
            buffer.render();
        }
    }
}
