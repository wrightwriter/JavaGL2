package _0_1.mathPackage

class IVec2 {
    var `val` = IntArray(2)
    operator fun get(i: Int):Int = `val`[i]
    operator fun set(i: Int, v: Int) { `val`[i] = v}
    operator fun plus(b: IVec2) = this.add(b)
    operator fun minus(b: IVec2) = this.sub(b)
    operator fun times(b: IVec2) = this.mul(b)



    var x: Int
        get():Int = x()
        set(value) {this[0] = value}
    var y: Int
        get():Int = y()
        set(value) {this[1] = value}


    fun x(): Int {
        return `val`[0]
    }


    fun y(): Int {
        return `val`[1]
    }

    constructor(x: Int, y: Int) {
        `val`[0] = x
        `val`[1] = y
    }

    constructor(vec: IVec2) {
        `val`[0] = vec.x()
        `val`[1] = vec.y()
    }

    constructor(v: Int) {
        `val`[0] = v
        `val`[1] = v
    }

    constructor(vals: IntArray) {
        `val`[0] = vals[0]
        `val`[1] = vals[1]
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