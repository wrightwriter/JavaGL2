package _0_1.engine.gui

import imgui.type.ImBoolean

class GUICheckbox(
    override var name: String,
    _value: Boolean = false
) : AbstractGUISetting() {
    override var value: Any = ImBoolean(_value)

    fun set(v: Boolean){
        (value as ImBoolean).set(v)
    }
    override fun get(): Boolean {
        return (value as ImBoolean).get()
    }
}