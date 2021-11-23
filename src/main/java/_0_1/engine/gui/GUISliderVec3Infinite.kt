package _0_1.engine.gui

import _0_1.math.vector.Vec3

class GUISliderVec3Infinite(
    override var name: String,
    _value: Vec3 = Vec3(0.0f)
): AbstractGUISetting(){
    override var value: Any = _value.copy().vals

    fun set(v: Vec3){
        (value as FloatArray)[0] = v.x
        (value as FloatArray)[1] = v.y
        (value as FloatArray)[2] = v.z
    }
    override fun get(): Vec3 {
        return Vec3(value as FloatArray)
    }
}