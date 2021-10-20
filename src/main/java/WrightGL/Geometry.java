package WrightGL;

public class Geometry {
    public static float[] triangle = {
            0,0,0,
            1,1,0,
            0,1,0,
            0,0,0,
            1,0,0,
            1,1,0,
    };
    public static float triangleStripQuad[] = { -1, -1, 1, -1, -1, 1, 1, 1 };
    public static float triangleNormals[] = {
            -1, -1,0, 0, 0, -1,
            1, -1, 0, 0, 0, -1,
            0,  1, 0, 0, 0, -1
    };
    public static float cubeNormals[] = {
            // left
            -1.0f,-1.0f,-1.0f,        -1, 0, 0,
            -1.0f,-1.0f, 1.0f,        -1, 0, 0,
            -1.0f, 1.0f, 1.0f,         -1, 0, 0,

            -1.0f,-1.0f,-1.0f,        -1, 0, 0,
            -1.0f, 1.0f, 1.0f,        -1, 0, 0,
            -1.0f, 1.0f,-1.0f,        -1, 0, 0,

            // down
            1.0f,-1.0f, 1.0f,        0, -1, 0,
            -1.0f,-1.0f, 1.0f,        0, -1, 0,
            -1.0f,-1.0f,-1.0f,        0, -1, 0,

            1.0f,-1.0f, 1.0f,        0, -1, 0,
            -1.0f,-1.0f,-1.0f,        0, -1, 0,
            1.0f,-1.0f,-1.0f,        0, -1, 0,

            // back
            1.0f, 1.0f,-1.0f,          0, 0,-1,
            -1.0f,-1.0f,-1.0f,         0, 0,-1,
            -1.0f, 1.0f,-1.0f,         0, 0,-1,

            1.0f, 1.0f,-1.0f,         0, 0,-1,
            1.0f,-1.0f,-1.0f,         0, 0,-1,
            -1.0f,-1.0f,-1.0f,         0, 0,-1,

            // front
            -1.0f, 1.0f, 1.0f,       0, 0, 1,
            -1.0f,-1.0f, 1.0f,       0, 0, 1,
            1.0f,-1.0f, 1.0f,        0, 0, 1,

            1.0f, 1.0f, 1.0f,        0, 0, 1,
            -1.0f, 1.0f, 1.0f,       0, 0, 1,
            1.0f,-1.0f, 1.0f,        0, 0, 1,

            // right
            1.0f, 1.0f, 1.0f,        1, 0, 0,
            1.0f,-1.0f,-1.0f,        1, 0, 0,
            1.0f, 1.0f,-1.0f,        1, 0, 0,

            1.0f,-1.0f,-1.0f,        1, 0, 0,
            1.0f, 1.0f, 1.0f,        1, 0, 0,
            1.0f,-1.0f, 1.0f,        1, 0, 0,

            // up
            1.0f, 1.0f, 1.0f,        0, 1, 0,
            1.0f, 1.0f,-1.0f,        0, 1, 0,
            -1.0f, 1.0f,-1.0f,       0, 1, 0,

            1.0f, 1.0f, 1.0f,        0, 1, 0,
            -1.0f, 1.0f,-1.0f,       0, 1, 0,
            -1.0f, 1.0f, 1.0f,       0, 1, 0

    };
}
