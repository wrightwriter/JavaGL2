package enginePackage;


import mathPackage.Vec2;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class IO {
	public Vec2 mousePos = new Vec2(0);
	public Vec2 deltaMousePos = new Vec2(0);

	public boolean LMBPress = false;
	public boolean LMBRelease = false;
	public boolean LMBDown = false;
	public boolean LMBUp = false;

	public boolean RMBPress = false;
	public boolean RMBRelease = false;
	public boolean RMBDown = false;
	public boolean RMBUp = false;

	public enum Key{
		N0(GLFW_KEY_0),
		N1(GLFW_KEY_1),
		N2(GLFW_KEY_2),
		N3(GLFW_KEY_3),
		N4(GLFW_KEY_4),
		N5(GLFW_KEY_5),
		N6(GLFW_KEY_6),
		N7(GLFW_KEY_7),
		N8(GLFW_KEY_8),
		N9(GLFW_KEY_9),
		A(GLFW_KEY_A),
		B(GLFW_KEY_B),
		C(GLFW_KEY_C),
		D(GLFW_KEY_D),
		E(GLFW_KEY_E),
		F(GLFW_KEY_F),
		G(GLFW_KEY_G),
		H(GLFW_KEY_H),
		I(GLFW_KEY_I),
		J(GLFW_KEY_J),
		K(GLFW_KEY_K),
		L(GLFW_KEY_L),
		M(GLFW_KEY_M),
		N(GLFW_KEY_N),
		O(GLFW_KEY_O),
		P(GLFW_KEY_P),
		Q(GLFW_KEY_Q),
		R(GLFW_KEY_R),
		S(GLFW_KEY_S),
		T(GLFW_KEY_T),
		U(GLFW_KEY_U),
		V(GLFW_KEY_V),
		W(GLFW_KEY_W),
		X(GLFW_KEY_X),
		Y(GLFW_KEY_Y),
		Z(GLFW_KEY_Z),
		Tilde(GLFW_KEY_GRAVE_ACCENT),

		LCtrl(GLFW_KEY_LEFT_CONTROL),
		LShift(GLFW_KEY_LEFT_SHIFT),
		LAlt(GLFW_KEY_LEFT_ALT),

		Left(GLFW_KEY_LEFT),
		Right(GLFW_KEY_RIGHT),
		Up(GLFW_KEY_UP),
		Down(GLFW_KEY_DOWN),

		Space(GLFW_KEY_SPACE),
		Enter(GLFW_KEY_ENTER),
		Tab(GLFW_KEY_TAB),

		;final private int value;
		private static HashMap<Integer, Key> mapKeyCodeToEnum = new HashMap<>();
		Key(int value){
			this.value = value;
		}
		public static Key getKey(int value){
			return mapKeyCodeToEnum.get(value);
		}
		private int getSelfValue(){
			return value;
		}
		static {
			for (Key d : Key.values()) {
				mapKeyCodeToEnum.put(d.getSelfValue(), d);
			}
		}

		public static class State {
			boolean Down = false;
			boolean Up = true;
			boolean Press = false;
			boolean Release = false;
		}
	}
	public record KeyStateEnum(
			boolean Down,
			boolean Up,
			boolean Press,
			boolean Release
	){}
	public HashMap<Key, Key.State> keyboard = new HashMap<>(Key.values().length);
	public IO(){
		for(Key state: Key.values()){
			keyboard.put(state, new Key.State() );
		}
	}
}
