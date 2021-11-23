package sketches.subdivisionLines

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear

import _0_1.engine.Engine
import _0_1.engine.Sketch
import _0_1.engine.gui.GUISliderFloat
import _0_1.engine.gui.GUISliderVec3Infinite
import _0_1.math.Random
import _0_1.math.vector.Vec3
import _0_1.wrightgl.CameraPilot
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.Pass.post.*
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.buffer.VBEditable
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FB.Companion.bind
import _0_1.wrightgl.fb.FB.Companion.defaultFB
import _0_1.wrightgl.shader.ProgRender
import _0_1.wrightgl.thing.Thing
import org.lwjgl.opengl.GL32.*
import kotlin.math.sin


//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class SketchSubdivisionLines(
    engine: Engine
) : Sketch(engine) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val engineSettings = Engine.Settings()
            engineSettings.res.x = 1200
            engineSettings.res.y = 768
            engineSettings.renderdocEnabled = true
            engineSettings.openGLDebugEnabled = true
            engineSettings.nanoVGEnabled = false
            engineSettings.liveShaderReloading = true
            Engine.startSketch(SketchSubdivisionLines.javaClass, engineSettings)
        }
    }
    // ---------------- GUI ---------------- //
    var cameraRotGUI = GUISliderFloat("Camera Rotation")
    var cameraPosGUI = GUISliderVec3Infinite(
        "Camera Position",
        Vec3(0.0f,0.0f,-5.0f)
    )
    var cameraLookAtGUI = GUISliderVec3Infinite(
        "Camera Look At",
        Vec3(0.0f,0.0f,0.0f)
    )

    // ---------------- Programs ---------------- //
    lateinit var compositeProgram: ProgRender
    lateinit var postProgram: ProgRender


    // ---------------- FRAMEBUFFERS ---------------- //
    var drawFB: FB = FB(hasDepth = true)

    // ---------------- SHADERS ---------------- //
    val lineProgram = ProgRender( "line.vp", "line.fp", "line.gp" )
    val polyProgram = ProgRender( "tri.vp", "tri.fp")
    val pointsProgram = ProgRender( "line.vp", "points.fp", "points.gp")

    // ---------------- VERT BUFFERS ---------------- //
    val lineVBEditable = VBEditable( )
    val polyVBEditable = VBEditable( )
//        val quadVB = VB( Geometry.quadTriangleStrip, intArrayOf(2), VB.PrimitiveType.TRIANGLES_STRIP )

    // ---------------- THINGS ---------------- //

//    val cubeThing = Thing(cubeProgram, cubeVB)
    val lineThing = Thing(lineProgram, lineVBEditable)
    val polyThing = Thing(polyProgram, polyVBEditable)

    var dofPassTeardown = PassFXFB("dof.fp" )

    var fxaaPass = FXAA()
//    var gaussianApproxPass = GaussianApprox()
//    var gaussianPass = Gaussian()
//    var dofPass = DOF()
//    var doOUv - gl_FragCoord.xy/RassScatter = DOFScatter()
//    var dofPassNoGeom = DOFNoGeom()

//    var video = Video()


    override fun setup() {
        lineThing.primitiveType = VB.PrimitiveType.LINES_STRIP

        val iters = 100.0f
        for(i in 0 until iters.toInt()){
            val iter = i.toFloat()
//            lineVBEditable.pushOne(
//                cos(iter/iters* MathUtils.Pi),
//                sin(iter/iters*MathUtils.Pi),
//                0.0f,
//            )
            lineVBEditable.pushOne(
                Random.Companion.XOR.getRandomRange(-1.0f,1.0f),
                Random.Companion.XOR.getRandomRange(-1.0f,1.0f),
                Random.Companion.XOR.getRandomRange(-1.0f,1.0f),
//                0.0f,
            )
        }
        // ----------------------- DELAUNAY TRIANGULATOR ----------------------- //
//        val pointSet = ArrayList<Vector2D>()
//        for(i in 0 until lineVBEditable.totalVertCnt){
//            val vert = lineVBEditable.getVertAt(i)
//            pointSet.add(
//                Vector2D(vert[0].toDouble(),vert[1].toDouble() )
//            )
//        }
//        val triangulator: DelaunayTriangulator = DelaunayTriangulator(pointSet);
//        triangulator.triangulate()
//
//        for(triangle in triangulator.triangles) {
//            polyVBEditable.pushOne(
//                triangle.a.x.toFloat(),
//                triangle.a.y.toFloat(),
//                0.0f,
//            )
//            polyVBEditable.pushOne(
//                triangle.b.x.toFloat(),
//                triangle.b.y.toFloat(),
//                0.0f,
//            )
//            polyVBEditable.pushOne(
//                triangle.c.x.toFloat(),
//                triangle.c.y.toFloat(),
//                0.0f,
//            )
//        }


        // ----------------------- TINFOUR ----------------------- //
//
//        val vList = ArrayList<Vertex>()
//
//        for(i in 0 until lineVBEditable.totalVertCnt){
//            val vert = lineVBEditable.getVertAt(i)
//            vList.add(
//                Vertex(vert[0].toDouble(),vert[1].toDouble(), vert[2].toDouble() )
//            )
//        }
//        val options = BoundedVoronoiBuildOptions()
//        options.enableAutomaticColorAssignment(true)
//
//        val diagram = BoundedVoronoiDiagram(vList, options)
//        polyVBEditable.clearAll()
//
//        for(triangle in diagram.polygons) {
//            polyVBEditable.pushOne(
//                triangle.index,
//                triangle.vertex.y.toFloat(),
//                0.0f,
//            )
////            polyVBEditable.pushOne(
////                triangle.vertex.x.toFloat(),
////                triangle.vertex.y.toFloat(),
////                0.0f,
////            )
//        }

        // ----------------------- POLY2TRI ----------------------- //

//        var pointsArr = ArrayList<PolygonPoint>()
//
//        for(i in 0 until lineVBEditable.totalVertCnt){
//            val vert = lineVBEditable.getVertAt(i)
//            pointsArr.add(
//                PolygonPoint(vert[0].toDouble(),vert[1].toDouble(), vert[2].toDouble() )
//            )
//        }
////        val poly = Polygon( pointsArr )
//
////        val poly = PointSet(pointsArr as List<TriangulationPoint>?)
//        val poly = Polygon(pointsArr )
//        Poly2Tri.triangulate(poly)
//        val triangles = poly.triangles
//
//        polyVBEditable.clearAll()
//
//        for(triangle in triangles){
//            println(triangle.points.size)
//            polyVBEditable.pushOne(
//                triangle.points[0].x.toFloat(),
//                triangle.points[0].y.toFloat(),
//                triangle.points[0].z.toFloat(),
//            )
//            polyVBEditable.pushOne(
//                triangle.points[1].x.toFloat(),
//                triangle.points[1].y.toFloat(),
//                triangle.points[1].z.toFloat(),
//            )
//            polyVBEditable.pushOne(
//                triangle.points[2].x.toFloat(),
//                triangle.points[2].y.toFloat(),
//                triangle.points[2].z.toFloat(),
//            )
//        }


//        camera = CameraLookAt()
        camera = CameraPilot()
    }
    override fun display() {

/*
        camera.eyePos = cameraPosGUI.get()
        (camera as CameraLookAt).lookAt = cameraLookAtGUI.get()
*/

        // Setting
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDepthFunc(GL_LESS)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_DEPTH_TEST)

//        glAlphaFunc ( GL_GREATER, 0.1f ) ;
//        glEnable ( GL_ALPHA_TEST ) ;

//        if(Random.Companion.XOR.getRandom() < 0.8 && time < 2.0f){
//            lineVBEditable.pushOne(
//                Random.Companion.XOR.getRandomRange(-1.0f, 1.0f),
//                Random.Companion.XOR.getRandomRange(-1.0f, 1.0f),
//                Random.Companion.XOR.getRandomRange(-1.0f, 1.0f),
//            )
//        }

        for(i in 0 until lineVBEditable.totalVertCnt){
            val iter = i.toFloat()
//            lineVBEditable.pushOne(
//                cos(iter/iters* MathUtils.Pi),
//                sin(iter/iters*MathUtils.Pi),
//                0.0f,
//            )
            val prevVert = lineVBEditable.getVertAt(
                i,
            )
            lineVBEditable.setAt(
                i,
                sin((iter) + time*0.1).toFloat(),
                sin((iter*1.5) + time*0.1).toFloat(),
                sin((iter*2.2) + time*0.1).toFloat(),
            )
        }

        drawFB.clearAllAttachments()
        bind(FB.Target.DRAW, drawFB)

        // Draw stuff
        lineThing.depthTest = true // inconveneint ADD TO RENDER()
        lineThing.render(
        )
        lineThing.render(
            pointsProgram,
            _primitiveType = VB.PrimitiveType.POINTS
        )
//        polyThing.render( )

//        polyThing.render(
//            _primitiveType = VB.PrimitiveType.TRIANGLES,
//            _culling = VB.CullMode.DISABLED
//        )

//        dofPass.run(drawFB)
        dofPassTeardown.run(drawFB)
//        gaussianApproxPass.run(dofPass.fb)
//        gaussianPass.run(dofPass.fb)


        FB.blit(dofPassTeardown.fb, defaultFB)


        glFinish()

    }


    override fun drawImGui() {
    }



}