package _0_1.mathPackage

class Vec4 {
    var `val` = FloatArray(4)
    operator fun get(i: Int) = `val`[i]
    operator fun set(i: Int, v: Float) { `val`[i] = v}
    operator fun plus(b: Vec4) = this.add(b)
    operator fun minus(b: Vec4) = this.sub(b)
    operator fun times(b: Vec4) = this.mul(b)


    var x: Float
        get():Float = x()
        set(value) {this[0] = value}
    var y: Float
        get():Float = y()
        set(value) {this[1] = value}
    var z: Float
        get():Float = z()
        set(value) {this[2] = value}
    var w: Float
        get():Float = w()
        set(value) {this[3] = value}

    fun x(): Float {
        return `val`[0]
    }

    fun y(): Float {
        return `val`[1]
    }

    fun z(): Float {
        return `val`[2]
    }

    fun w(): Float {
        return `val`[3]
    }

    constructor(x: Float, y: Float, z: Float, w: Float) {
        `val`[0] = x
        `val`[1] = y
        `val`[2] = z
        `val`[3] = w
    }

    constructor(vec: Vec4) {
        `val`[0] = vec.x()
        `val`[1] = vec.y()
        `val`[2] = vec.z()
        `val`[3] = vec.w()
    }

    constructor(v: Float) {
        `val`[0] = v
        `val`[1] = v
        `val`[2] = v
        `val`[3] = v
    }

    constructor(vals: FloatArray) {
        `val`[0] = vals[0]
        `val`[1] = vals[1]
        `val`[2] = vals[2]
        `val`[3] = vals[3]
    }

    fun copy(): Vec4 {
        return Vec4(this)
    }

    fun mul(b: Vec4): Vec4 {
        val a = this
        return Vec4(a.x() * b.x(), a.y() * b.y(), a.z() * b.z(), a.w() * b.w())
    }

    fun mul(b: Float): Vec4 {
        val a = this
        return Vec4(a.x() * b, a.y() * b, a.z() * b, a.w() * b)
    }

    operator fun div(b: Vec4): Vec4 {
        val a = this
        return Vec4(a.x() / b.x(), a.y() / b.y(), a.z() / b.z(), a.w() / b.w())
    }

    operator fun div(b: Float): Vec4 {
        val a = this
        return Vec4(a.x() / b, a.y() / b, a.z() / b, a.w() / b)
    }

    fun add(b: Vec4): Vec4 {
        val a = this
        return Vec4(a.x() + b.x(), a.y() + b.y(), a.z() + b.z(), a.w() + b.w())
    }

    fun add(b: Float): Vec4 {
        val a = this
        return Vec4(a.x() + b, a.y() + b, a.z() + b, a.w() + b)
    }

    fun sub(b: Vec4): Vec4 {
        val a = this
        return Vec4(a.x() - b.x(), a.y() - b.y(), a.z() - b.z(), a.w() - b.w())
    }

    fun sub(b: Float): Vec4 {
        val a = this
        return Vec4(a.x() - b, a.y() - b, a.z() - b, a.w() - b)
    }

    companion object {
        fun dot(a: Vec4, b: Vec4): Float {
            return a.x() * b.x() + a.y() * b.y() + a.z() * b.z() + a.w() * b.w()
        }

        //    static Vec4 cross(Vec4 a, Vec4 b){
        //        float res[] = new float[3];
        //        res[0] = a.y() * b.z() - a.z() * b.y();
        //        res[1] = a.z() * b.x() - a.x() * b.z();
        //        res[2] = a.x() * b.y() - a.y() * b.x();
        //        return new Vec4(res);
        //    }
        fun length(a: Vec4): Float {
            return Math.sqrt(dot(a, a).toDouble()).toFloat()
        }

        fun distance(a: Vec4, b: Vec4?): Float {
            return dot(a, a)
        }

        fun normalize(vec: Vec4): Vec4 {
            return vec.div(length(vec))
        }
    }
}