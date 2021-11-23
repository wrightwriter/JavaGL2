package _0_1.engine.gui

import _0_1.math.vector.Vec4

class GUISliderVec4Infinite(
    override var name: String,
    _value: Vec4 = Vec4(0.0f)
): AbstractGUISetting(){
    override var value: Any = _value.copy().vals

    fun set(v: Vec4){
        (value as FloatArray)[0] = v.x;
        (value as FloatArray)[1] = v.y;
        (value as FloatArray)[2] = v.z;
        (value as FloatArray)[3] = v.w;
    }
    override fun get(): Vec4 {
        return Vec4(value as FloatArray)
    }
}