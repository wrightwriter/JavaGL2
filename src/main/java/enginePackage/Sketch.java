package enginePackage;

import mainPackage.Global;
import mathPackage.*;
import static wrightglPackage.WrightGL.*;

import mathPackage.Geometry;


//import framework.Semantic;

import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL46.*;

public class Sketch extends Engine {

	public static void main(String[] args){
		Global.engine = new Sketch();
		Global.engine.start();
	}


	protected Program cubeProgram, compositeProgram, postProgram;
	public Framebuffer mainFramebuffer;
	public Framebuffer compositeFramebuffer;
	public Framebuffer postFramebuffer;

	Thing postThing;
	Thing compositeThing;
//	@Override
//	public void loop(){}
	@Override
	protected void settings(){
		resx = 1024;
		resy = 768;
	}

	@Override
	protected void setup(){
		// Framebuffers
		mainFramebuffer = new Framebuffer( 1, true );
		compositeFramebuffer = new Framebuffer( 1, false);
		postFramebuffer = new Framebuffer( 1, false);

		// Matrices
		viewMatrix = Mat4.getIdentityMatrix();
		projectionMatrix = Mat4.getIdentityMatrix();

		// Shaders
		cubeProgram = new Program("cube.vp", "cube.fp");
		compositeProgram = new Program("quad.vp", "composite.fp");
		postProgram = new Program("quad.vp", "post.fp");

		// Vertex Buffers
		VertexBuffer cubeVertexBuffer = new VertexBuffer(
				Geometry.cubeNormals, new int[] {3,3}, VertexBuffer.PrimitiveType.TRIANGLES);
//		cubeVertexBuffer.culling = VertexBuffer.VertexCulling.FRONT;
		VertexBuffer quadVertexBuffer = new VertexBuffer(
				Geometry.triangleStripQuad, new int[] {3}, VertexBuffer.PrimitiveType.TRIANGLES_STRIP
		);

		// Things

		Thing cube = new Thing(cubeProgram, cubeVertexBuffer );
		things.add(cube);
		cube.setCallback((thing) -> {
			// Update model matrix
			long now = System.currentTimeMillis();

			Mat4 scale = Mat4.getScaleMatrix(new Vector( .5f, 0.5f, 0.5f));
			Mat4 translate = Mat4.getTranslationMatrix( new Vector(0,0, 6) );
			Mat4 rotateZ = Mat4.getRotationMatrix(Mat4.Axis.Y, (float)time );

//			Mat4 model = Mat4.multiply(scale, translate);
//			model = Mat4.multiply(rotateZ, model);

			thing.modelMatrix = Mat4.multiply(translate,rotateZ);
		});

		compositeThing = new Thing( compositeProgram, quadVertexBuffer);
		postThing = new Thing( postProgram, quadVertexBuffer);



	}

	public void display(){

		WGL.updateMatrices();

		// Settings
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_LESS);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);

		// Framebuffer
		Framebuffer.bind(Framebuffer.FbTarget.DRAW_FRAMEBUFFER, mainFramebuffer);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


		// Draw stuff
		for(Thing thing: things){
			thing.render(null, null);
		}


		// Post
		Framebuffer.bind(Framebuffer.FbTarget.DRAW_FRAMEBUFFER, null);

		compositeThing.render(null,(thing)->{
			mainFramebuffer.setUniformTextures("g");
		});


		// Blit framebuffer
		Framebuffer.blit(mainFramebuffer, null,
				new Framebuffer.FbBitmask[]{ Framebuffer.FbBitmask.COLOR_BUFFER_BIT });
//		Framebuffer.blit(compositeFramebuffer, null,
//				new Framebuffer.FbBitmask[]{ Framebuffer.FbBitmask.COLOR_BUFFER_BIT });

		glfwSwapBuffers(window);
		glfwPollEvents();
	}
	public Sketch(){
		String potato = "aaaaaaaaaaaaaaaaaa potat";
		System.out.print(potato);
	}
}
