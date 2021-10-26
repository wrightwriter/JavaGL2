package _0_1.mathPackage

class Vec3 {
    var `val` = FloatArray(3)
    operator fun get(i: Int) = `val`[i]
    operator fun set(i: Int, v: Float) { `val`[i] = v}
    operator fun plus(b: Vec3) = this.add(b)
    operator fun minus(b: Vec3) = this.sub(b)
    operator fun times(b: Vec3) = this.mul(b)

    var x: Float
        get():Float = x()
        set(value) {this[0] = value}
    var y: Float
        get():Float = y()
        set(value) {this[1] = value}
    var z: Float
        get():Float = z()
        set(value) {this[2] = value}

    fun x(): Float {
        return `val`[0]
    }

    fun y(): Float {
        return `val`[1]
    }

    fun z(): Float {
        return `val`[2]
    }

    constructor(x: Float, y: Float, z: Float) {
        `val`[0] = x
        `val`[1] = y
        `val`[2] = z
    }

    constructor(vec: Vec3) {
        `val`[0] = vec.x()
        `val`[1] = vec.y()
        `val`[2] = vec.z()
    }

    constructor(v: Float) {
        `val`[0] = v
        `val`[1] = v
        `val`[2] = v
    }

    constructor(vals: FloatArray) {
        `val`[0] = vals[0]
        `val`[1] = vals[1]
        `val`[2] = vals[2]
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
            return Math.sqrt(dot(a, a).toDouble()).toFloat()
        }

        fun distance(a: Vec3, b: Vec3?): Float {
            return dot(a, a)
        }

        fun normalize(vec: Vec3): Vec3 {
            return vec.div(length(vec))
        }
    }
}