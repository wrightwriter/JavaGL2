package _0_1.wrightgl.Pass

import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.shader.ProgRender

abstract class AbstractPass (): AbstractUniformsContainer() {
    lateinit var shaderProgram: ProgRender

}