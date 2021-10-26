package _0_1.mathPackage

class Vec2 {
    var `val` = FloatArray(2)
    operator fun get(i: Int) = `val`[i]
    operator fun set(i: Int, v: Float) { `val`[i] = v}
    operator fun plus(b: Vec2) = this.add(b)
    operator fun minus(b: Vec2) = this.sub(b)
    operator fun times(b: Vec2) = this.mul(b)

    // getters and setters
    var x: Float
        get():Float = x()
        set(value) {this[0] = value}
    var y: Float
        get():Float = y()
        set(value) {this[1] = value}


    fun x(): Float {
        return `val`[0]
    }

    fun y(): Float {
        return `val`[1]
    }


    constructor(x: Float, y: Float) {
        `val`[0] = x
        `val`[1] = y
    }

    constructor(vec: Vec2) {
        `val`[0] = vec.x()
        `val`[1] = vec.y()
    }

    constructor(v: Float) {
        `val`[0] = v
        `val`[1] = v
    }

    constructor(vals: FloatArray) {
        `val`[0] = vals[0]
        `val`[1] = vals[1]
    }

    fun copy(): Vec2 {
        return Vec2(this)
    }

    fun mul(b: Vec2): Vec2 {
        val a = this
        return Vec2(a.x() * b.x(), a.y() * b.y())
    }

    fun mul(b: Float): Vec2 {
        val a = this
        return Vec2(a.x() * b, a.y() * b)
    }

    operator fun div(b: Vec2): Vec2 {
        val a = this
        return Vec2(a.x() / b.x(), a.y() / b.y())
    }

    operator fun div(b: Float): Vec2 {
        val a = this
        return Vec2(a.x() / b, a.y() / b)
    }

    fun add(b: Vec2): Vec2 {
        val a = this
        return Vec2(a.x() + b.x(), a.y() + b.y())
    }

    fun add(b: Float): Vec2 {
        val a = this
        return Vec2(a.x() + b, a.y() + b)
    }

    fun sub(b: Vec2): Vec2 {
        val a = this
        return Vec2(a.x() - b.x(), a.y() - b.y())
    }

    fun sub(b: Float): Vec2 {
        val a = this
        return Vec2(a.x() - b, a.y() - b)
    }

    companion object {
        fun dot(a: Vec2, b: Vec2): Float {
            return a.x() * b.x() + a.y() * b.y()
        }

        //    static Vec2 cross(Vec2 a, Vec2 b){
        //        float res[] = new float[3];
        //        res[0] = a.y() * b.z() - a.z() * b.y();
        //        res[1] = a.z() * b.x() - a.x() * b.z();
        //        res[2] = a.x() * b.y() - a.y() * b.x();
        //        return new Vec2(res);
        //    }
        fun length(a: Vec2): Float {
            return Math.sqrt(dot(a, a).toDouble()).toFloat()
        }

        fun distance(a: Vec2, b: Vec2?): Float {
            return dot(a, a)
        }

        fun normalize(vec: Vec2): Vec2 {
            return vec.div(length(vec))
        }
    }
}