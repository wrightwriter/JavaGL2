package _0_1.wrightgl.buffer

import io.github.jdiemke.triangulation.DelaunayTriangulator
import io.github.jdiemke.triangulation.Vector2D
import org.lwjgl.opengl.*
import org.poly2tri.Poly2Tri
import org.poly2tri.geometry.polygon.Polygon
import org.poly2tri.geometry.polygon.PolygonPoint
import org.poly2tri.triangulation.TriangulationAlgorithm
import org.tinfour.common.Vertex
import org.tinfour.voronoi.BoundedVoronoiBuildOptions
import org.tinfour.voronoi.BoundedVoronoiDiagram
import java.util.ArrayList

class VBEditable : VB {
    private constructor(
        _geometry: FloatArray,
        _signature: IntArray,
        _primitiveType: PrimitiveType,
        _mapped: Boolean
    ) : super(_geometry, _signature, _primitiveType, true){ }

    var maxVertCnt: Int = 1000000
        private set

    var vbTriangulated: VBEditable? = null


    override var flags = GL46.GL_MAP_WRITE_BIT or GL46.GL_MAP_READ_BIT or GL46.GL_MAP_PERSISTENT_BIT or GL46.GL_MAP_COHERENT_BIT
        protected set


    fun getFloat(idx: Int, offs: Int): Float{
        return gpuBuff!!.get(singleVertElementsCnt*(idx) + offs)
    }
    fun getVertAt(idx: Int): FloatArray{
        val result = FloatArray(singleVertElementsCnt)
        gpuBuff!!.get(singleVertElementsCnt*(idx), result )
        return result
    }
    fun setAtOffs(idx: Int, offs: Int,value: Float){
        gpuBuff!!.put(singleVertElementsCnt*(idx) + offs, value)
    }
    fun setAt(idx: Int,value: Float){
        gpuBuff!!.put(singleVertElementsCnt*(idx), value)
    }
    fun setAt(idx: Int,value: FloatArray){
        gpuBuff!!.put(singleVertElementsCnt*idx, value)
    }
    @JvmName("setAt1")
    fun setAt(idx: Int,vararg value: Float){
        gpuBuff!!.put(singleVertElementsCnt*idx, value)
    }
    fun pushOne(vararg value: Float){
        gpuBuff!!.put(singleVertElementsCnt*(totalVertCnt++), value)
    }
    @JvmName("push1")
    fun pushOne(value: FloatArray){
        gpuBuff!!.put(singleVertElementsCnt*(totalVertCnt++), value)
    }
    fun pushMany(vararg value: Float){
        val cnt = value.size/singleVertElementsCnt
        gpuBuff!!.put(singleVertElementsCnt*(totalVertCnt), value)
        totalVertCnt += cnt
    }
    fun clearAll(){
        totalVertCnt = 0
    }
    fun increment(value: FloatArray){
        totalVertCnt++
    }

    fun triangulate(){
        if (vbTriangulated == null){
            vbTriangulated = VBEditable()
        }

        // ----------------------- DELAUNAY TRIANGULATOR ----------------------- //
//        val pointSet = ArrayList<Vector2D>()
//        for(i in 0 until this.totalVertCnt){
//            val vert = this.getVertAt(i)
//            pointSet.add(
//                Vector2D(vert[0].toDouble(),vert[1].toDouble() )
//            )
//        }
//        val triangulator: DelaunayTriangulator = DelaunayTriangulator(pointSet);
//        triangulator.triangulate()
//
//        for(triangle in triangulator.triangles) {
//            vbTriangulated!!.pushOne(
//                triangle.b.x.toFloat(),
//                triangle.b.y.toFloat(),
//                0.0f,
//            )
//            vbTriangulated!!.pushOne(
//                triangle.a.x.toFloat(),
//                triangle.a.y.toFloat(),
//                0.0f,
//            )
//            vbTriangulated!!.pushOne(
//                triangle.c.x.toFloat(),
//                triangle.c.y.toFloat(),
//                0.0f,
//            )
//        }


//                 ----------------------- TINFOUR ----------------------- //

//        val vList = ArrayList<Vertex>()
//
//        for(i in 0 until this.totalVertCnt){
//            val vert = this.getVertAt(i)
//            vList.add(
//                Vertex(vert[0].toDouble(),vert[1].toDouble(), vert[2].toDouble() )
//            )
//        }
//        val options = BoundedVoronoiBuildOptions()
//        options.enableAutomaticColorAssignment(true)
//
//        val diagram = BoundedVoronoiDiagram(vList, options)
//        vbTriangulated!!.clearAll()
//
//        for(triangle in diagram.polygons) {
//            for(edge in triangle.edges) {
//                vbTriangulated!!.pushOne(
//    //                triangle.vertex.x.toFloat(),
////                    edge.v.x.toFloat(),
//                    edge.,
//                    edge.v.y.toFloat(),
//                    0.0f,
//                )
//
//            }
////            )
//            //            polyVBEditable.pushOne(
//            //                triangle.vertex.x.toFloat(),
//            //                triangle.vertex.y.toFloat(),
//            //                0.0f,
//            //            )
//        }


//                 ----------------------- POLY2TRI ----------------------- //

        var pointsArr = ArrayList<PolygonPoint>()

        for(i in 0 until this.totalVertCnt){
            val vert = this.getVertAt(i)
            pointsArr.add(
                PolygonPoint(vert[0].toDouble(),vert[1].toDouble(), vert[2].toDouble() )
            )
        }
//        val poly = Polygon( pointsArr )

//        val poly = PointSet(pointsArr as List<TriangulationPoint>?)
        val poly = Polygon(pointsArr )
        Poly2Tri.triangulate(TriangulationAlgorithm.DTSweep, poly)
        val triangles = poly.triangles

        vbTriangulated!!.clearAll()

        for(triangle in triangles){
            assert(triangle.points.size == 3)
            println(triangle.points.size)
            vbTriangulated!!.pushOne(
                triangle.points[1].x.toFloat(),
                triangle.points[1].y.toFloat(),
                triangle.points[1].z.toFloat(),
            )
            vbTriangulated!!.pushOne(
                triangle.points[2].x.toFloat(),
                triangle.points[2].y.toFloat(),
                triangle.points[2].z.toFloat(),
            )
            vbTriangulated!!.pushOne(
                triangle.points[0].x.toFloat(),
                triangle.points[0].y.toFloat(),
                triangle.points[0].z.toFloat(),
            )
        }
    }

    fun renderTriangulated(
//            _primitiveType: PrimitiveType? = null,
        _culling: CullMode? = null,
        _instanceCnt: Int = 1,
    ) {
        if (vbTriangulated == null){
            throw Exception("VB must be triangulated.")
        } else {
            vbTriangulated!!.render(
                _culling = _culling,
                _instanceCnt = _instanceCnt,
                _primitiveType = PrimitiveType.TRIANGLES
            )
//                if (_culling == CullMode.DISABLED)
//                    GL46.glDisable(GL11.GL_CULL_FACE)
//                else{
//                    GL46.glEnable(GL11.GL_CULL_FACE)
//                    if (_culling != null)
//                        GL46.glCullFace(_culling.value)
//
//                }
//
//                GL46.glBindVertexArray(vaoPid)
//
//                var primitives = primitiveType
//                if (_primitiveType != null )
//                    primitives = _primitiveType
//
//                if (_instanceCnt > 1)
//                    GL46.glDrawArraysInstanced(primitives.value, 0, totalVertCnt, _instanceCnt)
//                else
//                    GL46.glDrawArrays(primitives.value, 0, totalVertCnt)
//
//                GL46.glBindVertexArray(0)
        }
    }

    constructor(
        _geometry: FloatArray,
        _signature: IntArray,
        _primitiveType: PrimitiveType,
    ) : super(_geometry, _signature, _primitiveType, true) {
        maxVertCnt = totalVertCnt
    }


    constructor(
        _initialVertCnt: Int = 0,
        _maxVertCnt: Int = 1000000,
        _signature: IntArray = intArrayOf(3),
        _primitiveType: PrimitiveType = PrimitiveType.TRIANGLES_STRIP,
    ){
        signature = _signature
        totalVertCnt = _initialVertCnt

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }
        singleVertSizeBytes  = singleVertElementsCnt * Float.SIZE_BYTES
        totalSizeBytes = singleVertSizeBytes * totalVertCnt

        data = FloatArray(_maxVertCnt*singleVertElementsCnt){0.0f}

        primitiveType = _primitiveType

        createVBOandVAO( )
        mapGpuBuff(
            maxVertCnt,
            flags
        )

    }
}