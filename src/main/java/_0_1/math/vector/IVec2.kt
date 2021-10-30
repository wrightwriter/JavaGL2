package _0_1.math.vector

class IVec2: IVec {
    override var vals = IntArray(2)
    operator fun get(i: Int):Int = vals[i]
    operator fun set(i: Int, v: Int) { vals[i] = v}
    operator fun plus(b: IVec2) = this.add(b)
    operator fun minus(b: IVec2) = this.sub(b)
    operator fun times(b: IVec2) = this.mul(b)
    operator fun plus(b: Int) = this.add(b)
    operator fun minus(b: Int) = this.sub(b)
    operator fun times(b: Int) = this.mul(b)



    var x: Int
        get():Int = x()
        set(value) {this[0] = value}
    var y: Int
        get():Int = y()
        set(value) {this[1] = value}
    var xy: IVec2
        get():IVec2 = IVec2(x,y)
        set(v) {x = v[0]; y = v[1];}
    var yx: IVec2
        get():IVec2 = IVec2(y,x)
        set(v) {y = v[0]; x = v[1];}


    fun x(): Int {
        return vals[0]
    }


    fun y(): Int {
        return vals[1]
    }
    constructor(_x: Float, _y: Float) {
        vals[0] = _x.toInt()
        vals[1] = _y.toInt()
    }

    constructor(_x: Int, _y: Int) {
        vals[0] = _x
        vals[1] = _y
    }

    constructor(vec: IVec2) {
        vals[0] = vec.x()
        vals[1] = vec.y()
    }

    constructor(v: Int) {
        vals[0] = v
        vals[1] = v
    }

    constructor(vals: IntArray) {
        this.vals[0] = vals[0]
        this.vals[1] = vals[1]
    }

    fun copy(): IVec2 {
        return IVec2(this)
    }

    fun mul(b: IVec2): IVec2 {
        val a = this
        return IVec2(a.x() * b.x(), a.y() * b.y())
    }

    fun mul(b: Int): IVec2 {
        val a = this
        return IVec2(a.x() * b, a.y() * b)
    }

    operator fun div(b: IVec2): IVec2 {
        val a = this
        return IVec2(a.x() / b.x(), a.y() / b.y())
    }

    operator fun div(b: Int): IVec2 {
        val a = this
        return IVec2(a.x() / b, a.y() / b)
    }

    fun add(b: IVec2): IVec2 {
        val a = this
        return IVec2(a.x() + b.x(), a.y() + b.y())
    }

    fun add(b: Int): IVec2 {
        val a = this
        return IVec2(a.x() + b, a.y() + b)
    }

    fun sub(b: IVec2): IVec2 {
        val a = this
        return IVec2(a.x() - b.x(), a.y() - b.y())
    }

    fun sub(b: Int): IVec2 {
        val a = this
        return IVec2(a.x() - b, a.y() - b)
    }

}