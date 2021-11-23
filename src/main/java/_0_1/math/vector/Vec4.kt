package _0_1.math.vector

class Vec4: Vec {
    override var vals = FloatArray(4)
    operator fun get(i: Int) = vals[i]
    operator fun set(i: Int, v: Float) { vals[i] = v}
    operator fun plus(b: Vec4) = this.add(b)
    operator fun minus(b: Vec4) = this.sub(b)
    operator fun times(b: Vec4) = this.mul(b)
    operator fun plus(b: Float) = this.add(b)
    operator fun minus(b: Float) = this.sub(b)
    operator fun times(b: Float) = this.mul(b)

//    fun toIntVec(): IVec4{
//        return IVec4(x,y)
//    }

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

    fun w(): Float {
        return vals[3]
    }

    constructor(x: Int, y: Int, z: Int, w: Int) {
        vals[0] = x.toFloat()
        vals[1] = y.toFloat()
        vals[2] = z.toFloat()
        vals[3] = w.toFloat()
    }

    constructor(x: Float, y: Float, z: Float, w: Float) {
        vals[0] = x
        vals[1] = y
        vals[2] = z
        vals[3] = w
    }

    constructor(vec: Vec4) {
        vals[0] = vec.x()
        vals[1] = vec.y()
        vals[2] = vec.z()
        vals[3] = vec.w()
    }

    constructor(v: Float) {
        vals[0] = v
        vals[1] = v
        vals[2] = v
        vals[3] = v
    }

    constructor(vals: FloatArray) {
        this.vals[0] = vals[0]
        this.vals[1] = vals[1]
        this.vals[2] = vals[2]
        this.vals[3] = vals[3]
    }


    fun lerp(other: Vec4, amt: Float): Vec4{
        return this*amt + other*(1.0f - amt)
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