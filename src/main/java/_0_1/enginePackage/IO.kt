package _0_1.enginePackage

import _0_1.mathPackage.Vec2
import org.lwjgl.glfw.GLFW
import java.util.HashMap

class IO {
    var mousePos: Vec2 = Vec2(0.0f)
    var deltaMousePos: Vec2 = Vec2(0.0f)
    var LMBPress = false
    var LMBRelease = false
    var LMBDown = false
    var LMBUp = false
    var RMBPress = false
    var RMBRelease = false
    var RMBDown = false
    var RMBUp = false

    enum class Key(private val selfValue: Int) {
        N0(GLFW.GLFW_KEY_0), N1(GLFW.GLFW_KEY_1), N2(GLFW.GLFW_KEY_2), N3(GLFW.GLFW_KEY_3), N4(GLFW.GLFW_KEY_4), N5(GLFW.GLFW_KEY_5), N6(
            GLFW.GLFW_KEY_6
        ),
        N7(GLFW.GLFW_KEY_7), N8(GLFW.GLFW_KEY_8), N9(GLFW.GLFW_KEY_9), A(GLFW.GLFW_KEY_A), B(
            GLFW.GLFW_KEY_B
        ),
        C(GLFW.GLFW_KEY_C), D(GLFW.GLFW_KEY_D), E(GLFW.GLFW_KEY_E), F(GLFW.GLFW_KEY_F), G(GLFW.GLFW_KEY_G), H(
            GLFW.GLFW_KEY_H
        ),
        I(GLFW.GLFW_KEY_I), J(GLFW.GLFW_KEY_J), K(GLFW.GLFW_KEY_K), L(GLFW.GLFW_KEY_L), M(GLFW.GLFW_KEY_M), N(
            GLFW.GLFW_KEY_N
        ),
        O(GLFW.GLFW_KEY_O), P(GLFW.GLFW_KEY_P), Q(GLFW.GLFW_KEY_Q), R(GLFW.GLFW_KEY_R), S(GLFW.GLFW_KEY_S), T(
            GLFW.GLFW_KEY_T
        ),
        U(GLFW.GLFW_KEY_U), V(GLFW.GLFW_KEY_V), W(GLFW.GLFW_KEY_W), X(GLFW.GLFW_KEY_X), Y(GLFW.GLFW_KEY_Y), Z(
            GLFW.GLFW_KEY_Z
        ),
        Tilde(GLFW.GLFW_KEY_GRAVE_ACCENT), LCtrl(GLFW.GLFW_KEY_LEFT_CONTROL), LShift(GLFW.GLFW_KEY_LEFT_SHIFT), LAlt(
            GLFW.GLFW_KEY_LEFT_ALT
        ),
        Left(GLFW.GLFW_KEY_LEFT), Right(GLFW.GLFW_KEY_RIGHT), Up(GLFW.GLFW_KEY_UP), Down(
            GLFW.GLFW_KEY_DOWN
        ),
        Space(GLFW.GLFW_KEY_SPACE), Enter(GLFW.GLFW_KEY_ENTER), Tab(GLFW.GLFW_KEY_TAB);

        companion object {
            private val mapKeyCodeToEnum = HashMap<Int, Key>()
            fun getKey(value: Int): Key? {
                return mapKeyCodeToEnum[value]
            }

            init {
                for (d in values()) {
                    mapKeyCodeToEnum[d.selfValue] = d
                }
            }
        }

        class State {
            var Down = false
            var Up = true
            var Press = false
            var Release = false
        }
    }

    inner class KeyStateEnum

    var keyboard = HashMap<Key, Key.State>(Key.values().size)

    init {
        for (state in Key.values()) {
            keyboard[state] = Key.State()
        }
    }
}