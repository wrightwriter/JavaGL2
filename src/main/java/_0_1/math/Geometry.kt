package _0_1.math

object Geometry {
    var triangle = floatArrayOf(0f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f)
    var quadTriangleStrip = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
    var triangleNormals = floatArrayOf(-1f, -1f, 0f, 0f, 0f, -1f, 1f, -1f, 0f, 0f, 0f, -1f, 0f, 1f, 0f, 0f, 0f, -1f)
    var cubeNormals = floatArrayOf( // left
        -1.0f, -1.0f, -1.0f, -1f, 0f, 0f,
        -1.0f, -1.0f, 1.0f, -1f, 0f, 0f,
        -1.0f, 1.0f, 1.0f, -1f, 0f, 0f,
        -1.0f, -1.0f, -1.0f, -1f, 0f, 0f,
        -1.0f, 1.0f, 1.0f, -1f, 0f, 0f,
        -1.0f, 1.0f, -1.0f, -1f, 0f, 0f,  // down
        1.0f, -1.0f, 1.0f, 0f, -1f, 0f,
        -1.0f, -1.0f, 1.0f, 0f, -1f, 0f,
        -1.0f, -1.0f, -1.0f, 0f, -1f, 0f,
        1.0f, -1.0f, 1.0f, 0f, -1f, 0f,
        -1.0f, -1.0f, -1.0f, 0f, -1f, 0f,
        1.0f, -1.0f, -1.0f, 0f, -1f, 0f,  // back
        1.0f, 1.0f, -1.0f, 0f, 0f, -1f,
        -1.0f, -1.0f, -1.0f, 0f, 0f, -1f,
        -1.0f, 1.0f, -1.0f, 0f, 0f, -1f,
        1.0f, 1.0f, -1.0f, 0f, 0f, -1f,
        1.0f, -1.0f, -1.0f, 0f, 0f, -1f,
        -1.0f, -1.0f, -1.0f, 0f, 0f, -1f,  // front
        -1.0f, 1.0f, 1.0f, 0f, 0f, 1f,
        -1.0f, -1.0f, 1.0f, 0f, 0f, 1f,
        1.0f, -1.0f, 1.0f, 0f, 0f, 1f,
        1.0f, 1.0f, 1.0f, 0f, 0f, 1f,
        -1.0f, 1.0f, 1.0f, 0f, 0f, 1f,
        1.0f, -1.0f, 1.0f, 0f, 0f, 1f,  // right
        1.0f, 1.0f, 1.0f, 1f, 0f, 0f,
        1.0f, -1.0f, -1.0f, 1f, 0f, 0f,
        1.0f, 1.0f, -1.0f, 1f, 0f, 0f,
        1.0f, -1.0f, -1.0f, 1f, 0f, 0f,
        1.0f, 1.0f, 1.0f, 1f, 0f, 0f,
        1.0f, -1.0f, 1.0f, 1f, 0f, 0f,  // up
        1.0f, 1.0f, 1.0f, 0f, 1f, 0f,
        1.0f, 1.0f, -1.0f, 0f, 1f, 0f,
        -1.0f, 1.0f, -1.0f, 0f, 1f, 0f,
        1.0f, 1.0f, 1.0f, 0f, 1f, 0f,
        -1.0f, 1.0f, -1.0f, 0f, 1f, 0f,
        -1.0f, 1.0f, 1.0f, 0f, 1f, 0f
    )
}