package _0_1.mathPackage

class Mat4(_vals: FloatArray) {
    @JvmField
    var vals = FloatArray(4 * 4)
    init {
        vals = _vals
    }

    operator fun get(i: Int):Float = vals[i]
    operator fun get(x: Int, y: Int):Float = vals[y*4 + x]
    operator fun set(i: Int, v: Float) { vals[i] = v}
    operator fun times(b: Mat4) = Mat4.multiply(this,b)

    fun copy(): Mat4 {
        return Mat4(vals)
    }

    fun at(row: Int, col: Int): Float {
        return vals[row * 4 + col]
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
            //        float result[] = new float[16];
//        result[0] = a[0] * b[0] + a[4] * b[1] + a[8] * b[2] + a[12] * b[3];
//        result[1] = a[1] * b[0] + a[5] * b[1] + a[9] * b[2] + a[13] * b[3];
//        result[2] = a[2] * b[0] + a[6] * b[1] + a[10] * b[2] + a[14] * b[3];
//        result[3] = a[3] * b[0] + a[7] * b[1] + a[11] * b[2] + a[15] * b[3];
//
//        result[4] = a[0] * b[4] + a[4] * b[5] + a[8] * b[6] + a[12] * b[7];
//        result[5] = a[1] * b[4] + a[5] * b[5] + a[9] * b[6] + a[13] * b[7];
//        result[6] = a[2] * b[4] + a[6] * b[5] + a[10] * b[6] + a[14] * b[7];
//        result[7] = a[3] * b[4] + a[7] * b[5] + a[11] * b[6] + a[15] * b[7];
//
//        result[8] = a[0] * b[8] + a[4] * b[9] + a[8] * b[10] + a[12] * b[11];
//        result[9] = a[1] * b[8] + a[5] * b[9] + a[9] * b[10] + a[13] * b[11];
//        result[10] = a[2] * b[8] + a[6] * b[9] + a[10] * b[10] + a[14] * b[11];
//        result[11] = a[3] * b[8] + a[7] * b[9] + a[11] * b[10] + a[15] * b[11];
//
//        result[12] = a[0] * b[12] + a[4] * b[13] + a[8] * b[14] + a[12] * b[15];
//        result[13] = a[1] * b[12] + a[5] * b[13] + a[9] * b[14] + a[13] * b[15];
//        result[14] = a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15];
//        result[15] = a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15];
            return Mat4(result)
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
                val a = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
                return Mat4(a)
            }

        fun getTranslationMatrix(translation: Vec3): Mat4 {
            val a = floatArrayOf(
                1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f,
                -translation.x(), -translation.y(), -translation.z(), 1f
            )
            return Mat4(a)
        }

        fun getScaleMatrix(scale: Vec3): Mat4 {
            val a = floatArrayOf(
                scale.x(), 0f, 0f, 0f, 0f, scale.y(), 0f, 0f, 0f, 0f, scale.z(), 0f, 0f, 0f, 0f, 1f
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

        fun getLookAtMatrix(eye: Vec3, center: Vec3, up: Vec3): Mat4 {
            // TODO: check if this works.
            val f = Vec3.normalize(center.sub(eye))
            val s = Vec3.normalize(Vec3.cross(f, up))
            val u = Vec3.cross(s, f)
            val projMatrix = floatArrayOf(
                s.x(), u.x(), -f.x(), 0.0f,
                s.y(), u.y(), -f.y(), 0.0f,
                s.z(), u.z(), -f.z(), 0.0f,
                -Vec3.dot(s, eye), -Vec3.dot(u, eye), Vec3.dot(f, eye), 0.0f
            )
            return Mat4(projMatrix)
        }

        fun getPerspectiveMatrix(fov: Float, _near: Float, _far: Float, _xRes: Float, _yRes: Float): Mat4 {
            val aspectRatio = _xRes / _yRes
            val p = (1.0 / Math.tan(MathUtils.Pi * fov / 360.0)).toFloat() * aspectRatio
            val d = _far - _near
            val projMatrix = floatArrayOf(
                p, 0.0f, 0.0f, 0.0f,
                0.0f, p, 0.0f, 0.0f,
                0.0f, 0.0f, -(_far + _near) / d, -1.0f,
                0.0f, 0.0f, -2.0f * _far * _near / d, 0.0f
            )
            return Mat4(projMatrix)
        }
    }

}