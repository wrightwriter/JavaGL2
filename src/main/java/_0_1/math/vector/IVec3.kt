package _0_1.math.vector

class IVec3: IVec {
    override var vals = IntArray(3)
    operator fun get(i: Int):Int = vals[i]
    operator fun set(i: Int, v: Int) { vals[i] = v}
    operator fun plus(b: IVec3) = this.add(b)
    operator fun minus(b: IVec3) = this.sub(b)
    operator fun times(b: IVec3) = this.mul(b)
    operator fun plus(b: Int) = this.add(b)
    operator fun minus(b: Int) = this.sub(b)
    operator fun times(b: Int) = this.mul(b)



    var x: Int
        get():Int = x()
        set(value) {this[0] = value}
    var y: Int
        get():Int = y()
        set(value) {this[1] = value}
    var z: Int
        get():Int = z()
        set(value) {this[2] = value}

    var xy: IVec2
        get():IVec2 = IVec2(x,y)
        set(v) {x = v[0]; y = v[1];}
    var xz: IVec2
        get():IVec2 = IVec2(x,z)
        set(v) {x = v[0]; z = v[1];}
    var yx: IVec2
        get():IVec2 = IVec2(y,x)
        set(v) {y = v[0]; x = v[1];}
    var yz: IVec2
        get():IVec2 = IVec2(y,z)
        set(v) {y = v[0]; z = v[1];}
    var zx: IVec2
        get():IVec2 = IVec2(z,x)
        set(v) {z = v[0]; x = v[1];}
    var zy: IVec2
        get():IVec2 = IVec2(z,y)
        set(v) {z = v[0]; y = v[1];}


    fun x(): Int {
        return vals[0]
    }
    fun y(): Int {
        return vals[1]
    }
    fun z(): Int {
        return vals[2]
    }

    constructor(x: Int, y: Int, z:Int) {
        vals[0] = x
        vals[1] = y
        vals[2] = z
    }

    constructor(vec: IVec3) {
        vals[0] = vec.x
        vals[1] = vec.y
        vals[2] = vec.z
    }

    constructor(v: Int) {
        vals[0] = v
        vals[1] = v
        vals[2] = v
    }

    constructor(vals: IntArray) {
        this.vals[0] = vals[0]
        this.vals[1] = vals[1]
        this.vals[2] = vals[2]
    }

    fun copy(): IVec3 {
        return IVec3(this)
    }

    fun mul(b: IVec3): IVec3 {
        val a = this
        return IVec3(a.x() * b.x(), a.y() * b.y(),a.z() * b.z())
    }

    fun mul(b: Int): IVec3 {
        val a = this
        return IVec3(a.x() * b, a.y() * b, a.z() * b)
    }

    operator fun div(b: IVec3): IVec3 {
        val a = this
        return IVec3(a.x() / b.x(), a.y() / b.y(), a.z() / b.z())
    }

    operator fun div(b: Int): IVec3 {
        val a = this
        return IVec3(a.x() / b, a.y() / b, a.z() / b)
    }

    fun add(b: IVec3): IVec3 {
        val a = this
        return IVec3(a.x() + b.x(), a.y() + b.y(), a.z() + b.z())
    }

    fun add(b: Int): IVec3 {
        val a = this
        return IVec3(a.x() + b, a.y() + b, a.z() + b)
    }

    fun sub(b: IVec3): IVec3 {
        val a = this
        return IVec3(a.x() - b.x(), a.y() - b.y(), a.z() - b.z())
    }

    fun sub(b: Int): IVec3 {
        val a = this
        return IVec3(a.x() - b, a.y() - b, a.z() - b)
    }

}