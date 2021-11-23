package _0_1.engine.gui

class GUISliderFloatInfinite(
    override var name: String,
    _value: Float = 0.0f
) : AbstractGUISetting() {
    override var value: Any = floatArrayOf(_value)

    fun set(v: Float){
        (value as FloatArray)[0] = v;
    }
    override fun get(): Float {
        return (value as FloatArray)[0];
    }
}