package _0_1.engine.gui

import _0_1.main.Glob

abstract class AbstractGUISetting protected constructor (){
    abstract var name: String
        protected set
    abstract var value: Any
        protected set

    init{
        Glob.engine.gui.settings.add(this)
    }
    abstract fun get(): Any
}