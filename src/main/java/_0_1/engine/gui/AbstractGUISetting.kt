package _0_1.engine.gui

import _0_1.main.Global

abstract class AbstractGUISetting protected constructor (){
    abstract var name: String
        protected set
    abstract var value: Any
        protected set

    init{
        Global.engine!!.gui.settings.add(this)
    }
    abstract fun get(): Any
}