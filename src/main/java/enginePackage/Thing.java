package enginePackage;

import wrightglPackage.WrightGL;
import mathPackage.Mat4;
import mainPackage.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Thing {
    public WrightGL.Program shaderProgram;
    public List<WrightGL.VB> vertexBuffers = new ArrayList<>();
    public Mat4 modelMatrix;
    Consumer<Thing> callback = null;


    public Thing(final WrightGL.Program _program, WrightGL.VB _vertexBuffer){
        shaderProgram = _program;
        vertexBuffers.add(_vertexBuffer);
        modelMatrix = Mat4.getIdentityMatrix();
    }
    public void setCallback(Consumer<Thing> _modelMatrixCallback){
        callback = _modelMatrixCallback;
    }
    public void render(Consumer<Thing> innerCallback){
        render(null,  innerCallback);
    }
    public void render(WrightGL.Program _program){
        render(_program,  null);
    }
    public void render(){
        render(null,  null);
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
        Global.engine.wgl.setUniform("M", modelMatrix.vals, WrightGL.UniformType.Matrix);

        // Render
        for (WrightGL.VB buffer: vertexBuffers){
            buffer.render();
        }
    }
}
