package _0_1.math.vector

import kotlin.math.sqrt

class Vec3: Vec {
    override var vals = FloatArray(3)
    operator fun get(i: Int) = vals[i]
    operator fun set(i: Int, v: Float) { vals[i] = v}
    operator fun plus(b: Vec3) = this.add(b)
    operator fun minus(b: Vec3) = this.sub(b)
    operator fun times(b: Vec3) = this.mul(b)
    operator fun plus(b: Float) = this.add(b)
    operator fun minus(b: Float) = this.sub(b)
    operator fun times(b: Float) = this.mul(b)



    var x: Float
        get():Float = x()
        set(value) {this[0] = value}
    var y: Float
        get():Float = y()
        set(value) {this[1] = value}
    var z: Float
        get():Float = z()
        set(value) {this[2] = value}
    var xy: Vec2
        get():Vec2 = Vec2(x,y)
        set(v) {x = v[0]; y = v[1];}
    var xz: Vec2
        get():Vec2 = Vec2(x,z)
        set(v) {x = v[0]; z = v[1];}
    var yx: Vec2
        get():Vec2 = Vec2(y,x)
        set(v) {y = v[0]; x = v[1];}
    var yz: Vec2
        get():Vec2 = Vec2(y,z)
        set(v) {y = v[0]; z = v[1];}
    var zx: Vec2
        get():Vec2 = Vec2(z,x)
        set(v) {z = v[0]; x = v[1];}
    var zy: Vec2
        get():Vec2 = Vec2(z,y)
        set(v) {z = v[0]; y = v[1];}


    fun x(): Float {
        return vals[0]
    }

    fun y(): Float {
        return vals[1]
    }

    fun z(): Float {
        return vals[2]
    }

    constructor(x: Int, y: Int, z: Int) {
        vals[0] = x.toFloat()
        vals[1] = y.toFloat()
        vals[2] = z.toFloat()
    }
    constructor(x: Float, y: Float, z: Float) {
        vals[0] = x
        vals[1] = y
        vals[2] = z
    }

    constructor(vec: Vec3) {
        vals[0] = vec.x()
        vals[1] = vec.y()
        vals[2] = vec.z()
    }

    constructor(v: Float) {
        vals[0] = v
        vals[1] = v
        vals[2] = v
    }

    constructor(vals: FloatArray) {
        this.vals[0] = vals[0]
        this.vals[1] = vals[1]
        this.vals[2] = vals[2]
    }

    fun copy(): Vec3 {
        return Vec3(this)
    }

    fun mul(b: Vec3): Vec3 {
        val a = this
        return Vec3(a.x() * b.x(), a.y() * b.y(), a.z() * b.z())
    }

    fun mul(b: Float): Vec3 {
        val a = this
        return Vec3(a.x() * b, a.y() * b, a.z() * b)
    }

    operator fun div(b: Vec3): Vec3 {
        val a = this
        return Vec3(a.x() / b.x(), a.y() / b.y(), a.z() / b.z())
    }

    operator fun div(b: Float): Vec3 {
        val a = this
        return Vec3(a.x() / b, a.y() / b, a.z() / b)
    }

    fun add(b: Vec3): Vec3 {
        val a = this
        return Vec3(a.x() + b.x(), a.y() + b.y(), a.z() + b.z())
    }

    fun add(b: Float): Vec3 {
        val a = this
        return Vec3(a.x() + b, a.y() + b, a.z() + b)
    }

    fun sub(b: Vec3): Vec3 {
        val a = this
        return Vec3(a.x() - b.x(), a.y() - b.y(), a.z() - b.z())
    }

    fun sub(b: Float): Vec3 {
        val a = this
        return Vec3(a.x() - b, a.y() - b, a.z() - b)
    }

    fun negative(): Vec3 {
        return Vec3(
        vals[0] * -1.0f,
        vals[1] * -1.0f,
        vals[2] * -1.0f,
        )
    }

    companion object {
        fun dot(a: Vec3, b: Vec3): Float {
            return a.x() * b.x() + a.y() * b.y() + a.z() * b.z()
        }

        fun cross(a: Vec3, b: Vec3): Vec3 {
            val res = FloatArray(3)
            res[0] = a.y() * b.z() - a.z() * b.y()
            res[1] = a.z() * b.x() - a.x() * b.z()
            res[2] = a.x() * b.y() - a.y() * b.x()
            return Vec3(res)
        }

        fun length(a: Vec3): Float {
            return sqrt(dot(a, a).toDouble()).toFloat()
        }

        fun distance(a: Vec3, b: Vec3): Float {
            return length(a - b)
        }

        fun normalize(vec: Vec3): Vec3 {
            val len = length(vec)
            return vec / len
        }
    }
}