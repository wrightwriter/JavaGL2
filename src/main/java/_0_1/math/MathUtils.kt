package _0_1.math

import _0_1.engine.Constants
import _0_1.math.vector.Vec3
import _0_1.wrightgl.buffer.VBIndexed
import java.lang.Math.pow


object MathUtils {
    const val Pi = Math.PI.toFloat()
    const val Tau = 2 * Pi
    fun getIcoSphere(subdivisons: Int = 0): VBIndexed{
        val a = 0.52573f
        val b = 0.85065f
        val verticesVec3 = arrayListOf(
            Vec3(-a, 0.0f, b),
            Vec3(a, 0.0f, b),
            Vec3(-a, 0.0f, -b),
            Vec3(a, 0.0f, -b),

            Vec3(0.0f, b, a),
            Vec3(0.0f, b, -a),
            Vec3(0.0f, -b, a),
            Vec3(0.0f, -b, -a),
//            Vec3(-a, 0.0f, -b),
//            Vec3(a, 0.0f, -b),

            Vec3(b, a, 0.0f),
            Vec3(-b, a, 0.0f),
            Vec3(b, -a, 0.0f),
            Vec3(-b, -a, 0.0f),
        )

        var indices = arrayListOf<Int>(
            0, 1, 4, 0,
            Constants.primitiveRestartIndex,
            0, 4, 9,0,
            Constants.primitiveRestartIndex,
            9, 4, 5,9,
            Constants.primitiveRestartIndex,
            4, 8, 5,4,
            Constants.primitiveRestartIndex,
            4, 1, 8,4,
            Constants.primitiveRestartIndex,

            8, 1, 10,8,
            Constants.primitiveRestartIndex,
            8, 10, 3,8,
            Constants.primitiveRestartIndex,
            5, 8, 3,5,
            Constants.primitiveRestartIndex,
            5, 3, 2,5,
            Constants.primitiveRestartIndex,
            2, 3, 7,2,
            Constants.primitiveRestartIndex,

            7, 3, 10,7,
            Constants.primitiveRestartIndex,
            7, 10, 6,7,
            Constants.primitiveRestartIndex,
            7, 6, 11,7,
            Constants.primitiveRestartIndex,
            11, 6, 0,11,
            Constants.primitiveRestartIndex,
            0, 6, 1,0,
            Constants.primitiveRestartIndex,

            6, 10, 1,6,
            Constants.primitiveRestartIndex,
            9, 11, 0,9,
            Constants.primitiveRestartIndex,
            9, 2, 11,9,
            Constants.primitiveRestartIndex,
            9, 5, 2,9,
            Constants.primitiveRestartIndex,
            7, 11, 2,7,
            Constants.primitiveRestartIndex
        )


        for (subdivIdx in 0 until subdivisons){

            val newIndices = arrayListOf<Int>()

            val cntTrisPrev = indices.size / 5
            val cntTrisNew = 20 * pow(4.0,(subdivIdx + 1).toDouble()).toInt()

            for (triIdx in 0 until cntTrisPrev){
//                val arrayIdx = triIdx * 3 * 3
//                val va = Vec3(vertices[arrayIdx], vertices[arrayIdx + 1], vertices[arrayIdx + 2])
                val vaIdx = indices[triIdx*5]
                val vbIdx = indices[triIdx*5 + 1]
                val vcIdx = indices[triIdx*5 + 2]

                val va = verticesVec3[vaIdx]
                val vb = verticesVec3[vbIdx]
                val vc = verticesVec3[vcIdx]

                var d = va.lerp(vb, 0.5f)
                var e = vb.lerp(vc, 0.5f)
                var f = vc.lerp(va, 0.5f)

                d = Vec3.normalize(d);
                e = Vec3.normalize(e);
                f = Vec3.normalize(f);


                val dIdx = verticesVec3.size
                verticesVec3.add(d)
                val eIdx = verticesVec3.size
                verticesVec3.add(e)
                val fIdx = verticesVec3.size
                verticesVec3.add(f)

                // mid
                newIndices.add( dIdx)
                newIndices.add( eIdx)
                newIndices.add( fIdx)
                newIndices.add( dIdx)
                newIndices.add( Constants.primitiveRestartIndex)

                // a
                newIndices.add( vaIdx)
                newIndices.add( dIdx)
                newIndices.add( fIdx)
                newIndices.add( vaIdx)
                newIndices.add( Constants.primitiveRestartIndex)

                // b
                newIndices.add( dIdx)
                newIndices.add( vbIdx)
                newIndices.add( eIdx)
                newIndices.add( dIdx)
                newIndices.add( Constants.primitiveRestartIndex)

                // c
                newIndices.add( fIdx)
                newIndices.add( eIdx)
                newIndices.add( vcIdx)
                newIndices.add( fIdx)
                newIndices.add( Constants.primitiveRestartIndex)
            }
            indices = newIndices
        }

        val verticesFloat = arrayListOf<Float>()
        for(vert in verticesVec3){
            // vert
            verticesFloat.add(vert.x)
            verticesFloat.add(vert.y)
            verticesFloat.add(vert.z)
            // normal
            verticesFloat.add(vert.x)
            verticesFloat.add(vert.y)
            verticesFloat.add(vert.z)
        }

        val vb = VBIndexed(verticesFloat.toFloatArray(), indices.toIntArray(), intArrayOf(3,3))

        return vb
    }
}