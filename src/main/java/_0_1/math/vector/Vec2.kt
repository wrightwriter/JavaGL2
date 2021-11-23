package _0_1.math.vector

class Vec2: Vec {
    override var vals = FloatArray(2)

    operator fun get(i: Int) = vals[i]
    operator fun set(i: Int, v: Float) { vals[i] = v}
    operator fun plus(b: Vec2) = this.add(b)
    operator fun minus(b: Vec2) = this.sub(b)
    operator fun times(b: Vec2) = this.mul(b)
    operator fun plus(b: Float) = this.add(b)
    operator fun minus(b: Float) = this.sub(b)
    operator fun times(b: Float) = this.mul(b)

    fun toIntVec(): IVec2{
        return IVec2(x,y)
    }
    // getters and setters
    var x: Float
        get():Float = x()
        set(value) {this[0] = value}
    var y: Float
        get():Float = y()
        set(value) {this[1] = value}


    fun x(): Float {
        return vals[0]
    }

    fun y(): Float {
        return vals[1]
    }
    var xy: Vec2
        get():Vec2 = Vec2(x,y)
        set(v) {x = v[0]; y = v[1];}
    var yx: Vec2
        get():Vec2 = Vec2(y,x)
        set(v) {y = v[0]; x = v[1];}



    constructor(x: Int, y: Int) {
        vals[0] = x.toFloat()
        vals[1] = y.toFloat()
    }
    constructor(x: Float, y: Float) {
        vals[0] = x
        vals[1] = y
    }

    constructor(vec: Vec2) {
        vals[0] = vec.x()
        vals[1] = vec.y()
    }

    constructor(v: Float) {
        vals[0] = v
        vals[1] = v
    }

    constructor(vals: FloatArray) {
        this.vals[0] = vals[0]
        this.vals[1] = vals[1]
    }

    fun lerp(other: Vec2, amt: Float): Vec2{
        return this*amt + other*(1.0f - amt)
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