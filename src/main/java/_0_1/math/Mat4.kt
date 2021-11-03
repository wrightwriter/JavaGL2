package _0_1.math

// Some functions gathered from 0b5vr on github

import _0_1.math.vector.Vec3
import kotlin.math.tan

class Mat4(_vals: FloatArray) {
    @JvmField
    var vals = FloatArray(4 * 4)
    init {
        vals = _vals.clone()
    }

    operator fun get(i: Int):Float = vals[i]
//    operator fun get(x: Int, y: Int):Float = vals[y*4 + x]
    operator fun set(i: Int, v: Float) { vals[i] = v}
    operator fun times(b: Mat4) = Mat4.multiply(this,b)
//    operator fun timesAssign(b: Mat4) {this.vals = Mat4.multiply(b, this).vals}
    fun rotateX( radians: Float): Mat4{
        return getRotationMatrix(Axis.X, radians) * this
    }
    fun rotateY( radians: Float): Mat4{
        return getRotationMatrix(Axis.Y, radians)* this
    }
    fun rotateZ( radians: Float): Mat4{
        return (getRotationMatrix(Axis.Z, radians)* this)
    }
    fun translate( translation: Vec3): Mat4{
        return getTranslationMatrix(translation) * this
    }
    fun scale( sc: Vec3): Mat4{
        return getScaleMatrix(sc) * this
    }

    fun copy(): Mat4 {
        return Mat4(vals)
    }

    fun at(row: Int, col: Int): Float {
        return vals[row * 4 + col]
    }

    fun inverse(): Mat4 {
        return Companion.inverse(this)!!
    }

    enum class Axis {
        X, Y, Z
    }


    companion object {
        fun multiply(_a: Mat4, _b: Mat4): Mat4 {
            val a = _a.vals
            val b = _b.vals
            val result = floatArrayOf(
                a[0] * b[0] + a[4] * b[1] + a[8] * b[2] + a[12] * b[3],
                a[1] * b[0] + a[5] * b[1] + a[9] * b[2] + a[13] * b[3],
                a[2] * b[0] + a[6] * b[1] + a[10] * b[2] + a[14] * b[3],
                a[3] * b[0] + a[7] * b[1] + a[11] * b[2] + a[15] * b[3],
                a[0] * b[4] + a[4] * b[5] + a[8] * b[6] + a[12] * b[7],
                a[1] * b[4] + a[5] * b[5] + a[9] * b[6] + a[13] * b[7],
                a[2] * b[4] + a[6] * b[5] + a[10] * b[6] + a[14] * b[7],
                a[3] * b[4] + a[7] * b[5] + a[11] * b[6] + a[15] * b[7],
                a[0] * b[8] + a[4] * b[9] + a[8] * b[10] + a[12] * b[11],
                a[1] * b[8] + a[5] * b[9] + a[9] * b[10] + a[13] * b[11],
                a[2] * b[8] + a[6] * b[9] + a[10] * b[10] + a[14] * b[11],
                a[3] * b[8] + a[7] * b[9] + a[11] * b[10] + a[15] * b[11],
                a[0] * b[12] + a[4] * b[13] + a[8] * b[14] + a[12] * b[15],
                a[1] * b[12] + a[5] * b[13] + a[9] * b[14] + a[13] * b[15],
                a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15],
                a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15]
            )
            return Mat4(result)
        }



        fun inverse(_a: Mat4): Mat4? {
            val a00 = _a[  0 ]
            val a01 = _a[  1 ]
            val a02 = _a[  2 ]
            val a03 = _a[  3 ]

            val a10 = _a[  4 ]
            val a11 = _a[  5 ]
            val a12 = _a[  6 ]
            val a13 = _a[  7 ]

            val a20 = _a[  8 ]
            val a21 = _a[  9 ]
            val a22 = _a[ 10 ]
            val a23 = _a[ 11 ]

            val a30 = _a[ 12 ]
            val a31 = _a[ 13 ]
            val a32 = _a[ 14 ]
            val a33 = _a[ 15 ]

            val b00 = a00 * a11 - a01 * a10
              val b01 = a00 * a12 - a02 * a10

            val b02 = a00 * a13 - a03 * a10
              val b03 = a01 * a12 - a02 * a11

            val b04 = a01 * a13 - a03 * a11
              val b05 = a02 * a13 - a03 * a12

            val b06 = a20 * a31 - a21 * a30
              val b07 = a20 * a32 - a22 * a30

            val b08 = a20 * a33 - a23 * a30
              val b09 = a21 * a32 - a22 * a31

            val b10 = a21 * a33 - a23 * a31
              val b11 = a22 * a33 - a23 * a32;

            val det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;

            val invDet = 1.0f / det;
            if ( det == 0.0f ) { return null; }
            val ret = Mat4(
                floatArrayOf(
                    a11 * b11 - a12 * b10 + a13 * b09,
                    a02 * b10 - a01 * b11 - a03 * b09,
                    a31 * b05 - a32 * b04 + a33 * b03,
                    a22 * b04 - a21 * b05 - a23 * b03,
                    a12 * b08 - a10 * b11 - a13 * b07,
                    a00 * b11 - a02 * b08 + a03 * b07,
                    a32 * b02 - a30 * b05 - a33 * b01,
                    a20 * b05 - a22 * b02 + a23 * b01,
                    a10 * b10 - a11 * b08 + a13 * b06,
                    a01 * b08 - a00 * b10 - a03 * b06,
                    a30 * b04 - a31 * b02 + a33 * b00,
                    a21 * b02 - a20 * b04 - a23 * b00,
                    a11 * b07 - a10 * b09 - a12 * b06,
                    a00 * b09 - a01 * b07 + a02 * b06,
                    a31 * b01 - a30 * b03 - a32 * b00,
                    a20 * b03 - a21 * b01 + a22 * b00
                )
            )
            var i = 0
            for(v in ret.vals){
                ret.vals[i] *= invDet
                i++
            }
            return ret
        }

        fun transpose(_a: Mat4): Mat4 {
            val a = _a.vals
            return Mat4(
                floatArrayOf(
                    a[0], a[4], a[8], a[12],
                    a[1], a[5], a[9], a[13],
                    a[2], a[6], a[10], a[14],
                    a[3], a[7], a[11], a[15]
                )
            )
        }

        @JvmStatic
        val identityMatrix: Mat4
            get() {
                val a = floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f)
                return Mat4(a)
            }

        fun getTranslationMatrix(translation: Vec3): Mat4 {
            val a = floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                -translation.x(), -translation.y(), -translation.z(), 1f
            )
            return Mat4(a)
        }

        fun getScaleMatrix(scale: Vec3): Mat4 {
            val a = floatArrayOf(
                scale.x(), 0f, 0f, 0f,
                0f, scale.y(), 0f, 0f,
                0f, 0f, scale.z(), 0f,
                0f, 0f, 0f, 1f
            )
            return Mat4(a)
        }

        fun getRotationMatrix(axis: Axis, radians: Float): Mat4 {
            val r1 = Math.cos(radians.toDouble()).toFloat()
            val r2 = (-Math.sin(radians.toDouble())).toFloat()
            val r3 = -r2
            val a = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
            if (axis == Axis.X) {
                a[4 * 1 + 1] = r1
                a[4 * 1 + 2] = r2
                a[4 * 2 + 1] = r3
                a[4 * 2 + 2] = r1
            } else if (axis == Axis.Y) {
                a[4 * 0 + 0] = r1
                a[4 * 0 + 2] = -r2
                a[4 * 2 + 0] = -r3
                a[4 * 2 + 2] = r1
            } else if (axis == Axis.Z) {
                a[4 * 0 + 0] = r1
                a[4 * 0 + 1] = r2
                a[4 * 1 + 0] = r3
                a[4 * 1 + 1] = r1
            }
            return Mat4(a)
        }

        // CAT VERSION
        fun getLookAtMatrixCat(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            // TODO: check if this works.
//            val dir = Vec3.normalize(lookAt - eye)
            val dir = Vec3.normalize(eye.sub(lookAt))
//            val right = Vec3.normalize(Vec3.cross(up, dir))
            val right = (Vec3.cross(up, dir))
            val top = Vec3.cross(dir, right)
            val lookAtMat = floatArrayOf(
                right.x, right.y, right.z, 0.0f,
                top.x, top.y, top.z, 0.0f,
                dir.x, dir.y, dir.z, 0.0f,
                eye.x, eye.y, eye.z, 1.0f
            )
            return Mat4(lookAtMat)
        }

        fun getLookAtMatrixCatUntil(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            // TODO: check if this works.
//            val dir = Vec3.normalize(lookAt - eye)
            val dir = Vec3.normalize(lookAt.sub(eye))
//            val right = Vec3.normalize(Vec3.cross(up, dir))
            val sid = Vec3.normalize(Vec3.cross(dir, up))
            val top = Vec3.normalize(Vec3.cross(sid, dir))
//                sid = MathCat.vecAdd(
//                    MathCat.vecScale( Math.cos( rot || 0.0 ), sid ),
//                    MathCat.vecScale( Math.sin( rot || 0.0 ), top )
//                );
//                top = MathCat.vec3Cross( sid, dir );

            return Mat4(
                    floatArrayOf( sid[ 0 ], top[ 0 ], dir[ 0 ], 0.0f,
                    sid[ 1 ], top[ 1 ], dir[ 1 ], 0.0f,
                    sid[ 2 ], top[ 2 ], dir[ 2 ], 0.0f,
                    -sid[ 0 ] * eye[ 0 ] - sid[ 1 ] * eye[ 1 ] - sid[ 2 ] * eye[ 2 ],
                    -top[ 0 ] * eye[ 0 ] - top[ 1 ] * eye[ 1 ] - top[ 2 ] * eye[ 2 ],
                    -dir[ 0 ] * eye[ 0 ] - dir[ 1 ] * eye[ 1 ] - dir[ 2 ] * eye[ 2 ],
                    1.0f )
            )
        }
        fun getLookAtMatrix(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            val dir = Vec3.normalize(eye.sub(lookAt))
            val right = Vec3.normalize(Vec3.cross(up, dir))
            val top = Vec3.normalize(Vec3.cross(dir, right))
            val lookAtMat = floatArrayOf(
                right.x, right.y, right.z, 0.0f,
                top.x, top.y, top.z, 0.0f,
                dir.x, dir.y, dir.z, 0.0f,
                Vec3.dot(right.negative(),eye), Vec3.dot(top.negative(),eye), Vec3.dot(dir.negative(),eye), 1.0f
            )
            return Mat4(lookAtMat)
        }
//        fun getLookAtMatJoml(
//            lookAt: Vec3,
//            eye: Vec3,
//            up: Vec3 = Vec3(0,1,0)
//        ): Mat4 {
//            return getLookAtOldMatrix(
//                Vec3(eye.x(), eye.y(), eye.z()),
//                Vec3(lookAt.x(), lookAt.y(), lookAt.z()),
//                Vec3(up.x(), up.y(), up.z()),
//            )
//        }
//            val dir = Vec3.normalize(lookAt - eye)
//            val right = Vec3.normalize(Vec3.cross(up, dir))
//            val top = Vec3.cross(dir, right)
    fun getLookAtConditionScratchAPIxelMatrix(
        position: Vec3,
        lookAt: Vec3,
        up: Vec3 = Vec3(0,1,0)
    ): Mat4 {
        val dir = Vec3.normalize(lookAt.sub(position))
        val sid = Vec3.normalize(Vec3.cross(Vec3.normalize(up), dir))
        val top = Vec3.normalize(Vec3.cross(dir, sid))
        val lookAtMat = floatArrayOf(
            sid.x, sid.y, sid.z, 0.0f,
            top.x, top.y, top.z, 0.0f,
            dir.x, dir.y, dir.z, 0.0f,
            position.x, position.y, position.z, 1.0f
        )
        return Mat4(lookAtMat)
    }
        fun getLookAtOldMatrix(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            val dir = Vec3.normalize(lookAt.sub(eye))
            val right = Vec3.normalize(Vec3.cross(dir, up))
            val top = Vec3.normalize(Vec3.cross(right, dir))
            val lookAtMat = floatArrayOf(
                right.x, right.y, right.z, 0.0f,
                top.x, top.y, top.z, 0.0f,
                -dir.x, -dir.y, -dir.z, 0.0f,
                -Vec3.dot(right,eye), -Vec3.dot(top,eye), Vec3.dot(dir,eye), 1.0f
            )
            return Mat4(lookAtMat)
        }
        fun getLookAtSOMatrix(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            val dir = Vec3.normalize(eye.sub(lookAt))
            var y = up.copy();
            var x = Vec3.normalize(Vec3.cross(y,dir))

            y = Vec3.cross(dir,x)
//
            val res = Mat4.identityMatrix
            res[0] = x.x
            res[1] = x.y
            res[2] = x.z

            res[4*1] = y.x
            res[4*1 + 1] = y.y
            res[4*1 + 2] = y.z

            res[4*2] = dir.x
            res[4*2 + 1] = dir.y
            res[4*2 + 2] = dir.z

            return (res * Mat4.getTranslationMatrix(eye))
//            val yaw = (atan(dir.x / dir.z))
//            val pitch = (acos(Vec2.length(Vec2(dir.x, dir.z))))
//
//            val yRot = Mat4.getRotationMatrix(Axis.Y, -yaw)
//            val xRot = Mat4.getRotationMatrix(Axis.X, pitch)
//
//            val translation = Mat4.getTranslationMatrix(eye)


//            val right = Vec3.normalize(Vec3.cross(up, dir))
//            val top = Vec3.normalize(Vec3.cross(dir, right))
//            val lookAtMat = floatArrayOf(
//                right.x, right.y, right.z, 0.0f,
//                top.x, top.y, top.z, 0.0f,
//                -dir.x, -dir.y, -dir.z, 0.0f,
//                Vec3.dot(right,eye.negative()), Vec3.dot(top,eye.negative()), Vec3.dot(dir.negative(),eye.negative()), 1.0f
//            )
        }
        fun getLookAtCoolMatrix(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            val dir = Vec3.normalize(lookAt.sub(eye))
            val right = Vec3.normalize(Vec3.cross(up, dir))
            val top = Vec3.normalize(Vec3.cross(dir, right))
            val lookAtMat = floatArrayOf(
                right.x, right.y, right.z, 0.0f,
                top.x, top.y, top.z, 0.0f,
                -dir.x, -dir.y, -dir.z, 0.0f,
                Vec3.dot(right,eye.negative()), Vec3.dot(top,eye.negative()), Vec3.dot(dir.negative(),eye.negative()), 1.0f
            )
            return Mat4(lookAtMat)
        }

        fun getInverseLookAtMatrix(
            eye: Vec3,
            lookAt: Vec3,
            up: Vec3 = Vec3(0,1,0)
        ): Mat4 {
            // TODO: check if this works.
//            val dir = Vec3.normalize(lookAt - eye)
            val dir = Vec3.normalize(lookAt - eye)
//            val side = Vec3.normalize(Vec3.cross(dir, up))
            val side = Vec3.normalize(Vec3.cross(dir, up))
//            val top = Vec3.cross(side, dir)
            val top = Vec3.cross(side, dir)
            val projMatrix = floatArrayOf(
                side.x(), top.x(), -dir.x(), 0.0f,
                side.y(), top.y(), -dir.y(), 0.0f,
                side.z(), top.z(), -dir.z(), 0.0f,
                -Vec3.dot(side, eye),
                -Vec3.dot(top, eye),
                -Vec3.dot(dir, eye),
//                Vec3.dot(side.negative(), eye),
//                Vec3.dot(top.negative(), eye),
//                Vec3.dot(dir.negative(), eye),
                1.0f
            )
            return Mat4(projMatrix)
        }

        fun getPerspectiveMatrix(fov: Float, _near: Float, _far: Float, _xRes: Float, _yRes: Float): Mat4 {
            val aspectRatio = 1.0f/(_xRes / _yRes)
            val tanHalfFov = (1.0 / tan(MathUtils.Pi * fov / 360.0)).toFloat()
            val dz =  1.0f/(_far - _near)
            val projMatrix = floatArrayOf(
                1.0f*(tanHalfFov*aspectRatio), 0.0f, 0.0f, 0.0f,
                0.0f, tanHalfFov, 0.0f, 0.0f,
                0.0f, 0.0f, -(_far + _near)*dz, -1.0f,
                0.0f, 0.0f,  -2.0f * _far * _near * dz, 0.0f
            )
            return Mat4(projMatrix)
        }
        fun getPerspectiveOldMatrix(fov: Float, _near: Float, _far: Float, _xRes: Float, _yRes: Float): Mat4 {
            val aspectRatio = 1.0f/(_xRes / _yRes)
            val tanHalfFov = (1.0 / tan(MathUtils.Pi * fov / 360.0)).toFloat()
            val dz =  1.0f/(_far - _near)
            val projMatrix = floatArrayOf(
                aspectRatio*tanHalfFov, 0.0f, 0.0f, 0.0f,
                0.0f, tanHalfFov, 0.0f, 0.0f,
                0.0f, 0.0f, -(_near + _far)*dz, -1.0f,
                0.0f, 0.0f,  -2.0f * _far * _near * dz, 0.0f
            )
            return Mat4(projMatrix)
        }
    }

}