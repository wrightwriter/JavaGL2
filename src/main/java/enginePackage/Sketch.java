package enginePackage;

import mainPackage.Global;
import mathPackage.*;
import static wrightglPackage.WrightGL.*;

import imgui.ImGui;

import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

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
		Global.engine.destroy();
	}


	protected Program cubeProgram, compositeProgram, postProgram;
	public FB mainFB;
	public FB compositeFB;
	public FB postFB;

	Thing postThing;
	Thing compositeThing;
//	@Override
//	public void loop(){}
	@Override
	protected void initSettings(){
		resx = 1024;
		resy = 768;
	}

	@Override
	protected void setup(){
		// Framebuffers
		mainFB = new FB( 1, true );
		compositeFB = new FB( 1, true);
		postFB = new FB( 1, true);

		// Matrices
		viewMatrix = Mat4.getIdentityMatrix();
		projectionMatrix = Mat4.getIdentityMatrix();

		// Shaders
		cubeProgram = new Program("cube.vp", "cube.fp");
		compositeProgram = new Program("quad.vp", "composite.fp");
		postProgram = new Program("quad.vp", "post.fp");

		// Vertex Buffers
		VB cubeVB = new VB(
				Geometry.cubeNormals, new int[] {3,3}, VB.PrimitiveType.TRIANGLES);
//		cubeVertexBuffer.culling = VertexBuffer.VertexCulling.FRONT;
		VB quadVB = new VB(
				Geometry.triangleStripQuad, new int[] {2}, VB.PrimitiveType.TRIANGLES_STRIP
		);

		// Things

		Thing cube = new Thing(cubeProgram, cubeVB);
		thingQueue.add(cube);
		cube.setCallback((thing) -> {
			// Update model matrix
			long now = System.currentTimeMillis();

			Mat4 scale = Mat4.getScaleMatrix(new Vec3( .5f, 0.5f, 0.5f));
			Mat4 translate = Mat4.getTranslationMatrix( new Vec3(0,0, 6) );
			Mat4 rotateZ = Mat4.getRotationMatrix(Mat4.Axis.Y, (float)time );

//			Mat4 model = Mat4.multiply(scale, translate);
//			model = Mat4.multiply(rotateZ, model);

			thing.modelMatrix = Mat4.multiply(translate,rotateZ);
		});

		compositeThing = new Thing( compositeProgram, quadVB);
		postThing = new Thing( postProgram, quadVB);



	}

	public void display(){

		wgl.updateMatrices();

		// Settings
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_LESS);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);

		// Framebuffer
		FB.bind(FB.Target.DRAW_FRAMEBUFFER, mainFB);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


		// Draw stuff
		for(Thing thing: thingQueue){
			thing.render();
		}


		// Post
//		FB.bind(FB.Target.DRAW_FRAMEBUFFER, compositeFB);
//		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//		glClear(GL_DEPTH_BUFFER_BIT);
//
//		compositeThing.render((thing)->{
//			mainFB.setUniformTextures("g");
//		});


//		// Blit framebuffer
		FB.blitColour(mainFB, null );
//		FB.blitColour(compositeFB, null );
		FB.bind(FB.Target.DRAW_FRAMEBUFFER,null);

	}

	@Override
	protected void drawImGui(){
		if (io.keyboard.get(IO.Key.A).Down){
			ImGui.begin("aa");
			if (ImGui.button("aaaaa")){

			}
			ImGui.end();

		}
	}
	public Sketch(){
		String potato = "aaaaaaaaaaaaaaaaaa potat";
		System.out.print(potato);
	}
}
