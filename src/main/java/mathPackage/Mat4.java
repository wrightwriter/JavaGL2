package mathPackage;

public class Mat4 {
    public float[] vals = new float[4*4];

    public Mat4(float[] _vals){
        vals = _vals;
    }
    public Mat4 copy(){
        return new Mat4(this.vals);
    }
    float at(final int row, final int col){
        return vals[row*4 + col];
    }
    public static Mat4 multiply(Mat4 _a, Mat4 _b){

        float[] a = _a.vals;
        float[] b = _b.vals;

        float result[] = {
                a[ 0 ] * b[ 0 ] + a[ 4 ] * b[ 1 ] + a[ 8 ] * b[ 2 ] + a[ 12 ] * b[ 3 ],
                a[ 1 ] * b[ 0 ] + a[ 5 ] * b[ 1 ] + a[ 9 ] * b[ 2 ] + a[ 13 ] * b[ 3 ],
                a[ 2 ] * b[ 0 ] + a[ 6 ] * b[ 1 ] + a[ 10 ] * b[ 2 ] + a[ 14 ] * b[ 3 ],
                a[ 3 ] * b[ 0 ] + a[ 7 ] * b[ 1 ] + a[ 11 ] * b[ 2 ] + a[ 15 ] * b[ 3 ],

                a[ 0 ] * b[ 4 ] + a[ 4 ] * b[ 5 ] + a[ 8 ] * b[ 6 ] + a[ 12 ] * b[ 7 ],
                a[ 1 ] * b[ 4 ] + a[ 5 ] * b[ 5 ] + a[ 9 ] * b[ 6 ] + a[ 13 ] * b[ 7 ],
                a[ 2 ] * b[ 4 ] + a[ 6 ] * b[ 5 ] + a[ 10 ] * b[ 6 ] + a[ 14 ] * b[ 7 ],
                a[ 3 ] * b[ 4 ] + a[ 7 ] * b[ 5 ] + a[ 11 ] * b[ 6 ] + a[ 15 ] * b[ 7 ],

                a[ 0 ] * b[ 8 ] + a[ 4 ] * b[ 9 ] + a[ 8 ] * b[ 10 ] + a[ 12 ] * b[ 11 ],
                a[ 1 ] * b[ 8 ] + a[ 5 ] * b[ 9 ] + a[ 9 ] * b[ 10 ] + a[ 13 ] * b[ 11 ],
                a[ 2 ] * b[ 8 ] + a[ 6 ] * b[ 9 ] + a[ 10 ] * b[ 10 ] + a[ 14 ] * b[ 11 ],
                a[ 3 ] * b[ 8 ] + a[ 7 ] * b[ 9 ] + a[ 11 ] * b[ 10 ] + a[ 15 ] * b[ 11 ],

                a[ 0 ] * b[ 12 ] + a[ 4 ] * b[ 13 ] + a[ 8 ] * b[ 14 ] + a[ 12 ] * b[ 15 ],
                a[ 1 ] * b[ 12 ] + a[ 5 ] * b[ 13 ] + a[ 9 ] * b[ 14 ] + a[ 13 ] * b[ 15 ],
                a[ 2 ] * b[ 12 ] + a[ 6 ] * b[ 13 ] + a[ 10 ] * b[ 14 ] + a[ 14 ] * b[ 15 ],
                a[ 3 ] * b[ 12 ] + a[ 7 ] * b[ 13 ] + a[ 11 ] * b[ 14 ] + a[ 15 ] * b[ 15 ]
        };
//        float result[] = new float[16];
//        result[0] = a[0] * b[0] + a[4] * b[1] + a[8] * b[2] + a[12] * b[3];
//        result[1] = a[1] * b[0] + a[5] * b[1] + a[9] * b[2] + a[13] * b[3];
//        result[2] = a[2] * b[0] + a[6] * b[1] + a[10] * b[2] + a[14] * b[3];
//        result[3] = a[3] * b[0] + a[7] * b[1] + a[11] * b[2] + a[15] * b[3];
//
//        result[4] = a[0] * b[4] + a[4] * b[5] + a[8] * b[6] + a[12] * b[7];
//        result[5] = a[1] * b[4] + a[5] * b[5] + a[9] * b[6] + a[13] * b[7];
//        result[6] = a[2] * b[4] + a[6] * b[5] + a[10] * b[6] + a[14] * b[7];
//        result[7] = a[3] * b[4] + a[7] * b[5] + a[11] * b[6] + a[15] * b[7];
//
//        result[8] = a[0] * b[8] + a[4] * b[9] + a[8] * b[10] + a[12] * b[11];
//        result[9] = a[1] * b[8] + a[5] * b[9] + a[9] * b[10] + a[13] * b[11];
//        result[10] = a[2] * b[8] + a[6] * b[9] + a[10] * b[10] + a[14] * b[11];
//        result[11] = a[3] * b[8] + a[7] * b[9] + a[11] * b[10] + a[15] * b[11];
//
//        result[12] = a[0] * b[12] + a[4] * b[13] + a[8] * b[14] + a[12] * b[15];
//        result[13] = a[1] * b[12] + a[5] * b[13] + a[9] * b[14] + a[13] * b[15];
//        result[14] = a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15];
//        result[15] = a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15];

        return new Mat4(result);
    }
    public static Mat4 transpose(Mat4 _a) {
        float[] a = _a.vals;
        return new Mat4(new float[]{
                a[0], a[4], a[8], a[12],
                a[1], a[5], a[9], a[13],
                a[2], a[6], a[10], a[14],
                a[3], a[7], a[11], a[15]
        });
    };


    public enum Axis{
        X, Y, Z
    }

    public static Mat4 getIdentityMatrix(){
        float[] a = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        return new Mat4(a);
    }
    public static Mat4 getTranslationMatrix(Vector translation){
        float[] a = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                -translation.x(), -translation.y(), -translation.z(), 1,
        };
        return new Mat4(a);
    }
    public static Mat4 getScaleMatrix(Vector scale){
        float[] a = {
                scale.x(), 0, 0, 0,
                0, scale.y(), 0, 0,
                0, 0, scale.z(), 0,
                0, 0, 0, 1,
        };
        return new Mat4(a);
    }
    public static Mat4 getRotationMatrix(Axis axis, float radians) {

        float r1 = (float) Math.cos(radians);
        float r2 = (float) -Math.sin(radians);
        float r3 = -r2;
        float r4 = r1;
        float[] a = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
        };

        if (axis == Axis.X) {
            a[4 * 1 + 1] = r1;
            a[4 * 1 + 2] = r2;
            a[4 * 2 + 1] = r3;
            a[4 * 2 + 2] = r4;
        }
        else if (axis == Axis.Y) {
            a[4 * 0 + 0] = r1;
            a[4 * 0 + 2] = -r2;
            a[4 * 2 + 0] = -r3;
            a[4 * 2 + 2] = r4;
        }
        else if (axis == Axis.Z) {
            a[4 * 0 + 0] = r1;
            a[4 * 0 + 1] = r2;
            a[4 * 1 + 0] = r3;
            a[4 * 1 + 1] = r4;
        }

        return new Mat4(a);
    }
    public static Mat4 getLookAtMatrix(Vector eye, Vector center, Vector up) {
        // TODO: check if this works.
        final Vector f = (Vector.normalize(center.sub(eye)));
        final Vector s = (Vector.normalize(Vector.cross(f, up)));
        final Vector u = ((Vector.cross(s, f)));
        float[] projMatrix = {
                s.x(), u.x(), -f.x(), 0.0f,
                s.y(), u.y(), -f.y(), 0.0f,
                s.z(), u.z(), -f.z(), 0.0f,
                -Vector.dot(s,eye), -Vector.dot(u,eye), Vector.dot(f,eye), 0.0f
        };
        return new Mat4(projMatrix);
    }
    public static Mat4 getPerspectiveMatrix(float fov, float _near, float _far, float _xRes, float _yRes) {
        final float aspectRatio = _xRes / _yRes;
        final float p = (float) (1.0 / Math.tan(MathUtils.Pi * fov / 360.))*aspectRatio;
		final float d = _far - _near;
        float[] projMatrix = {
            p, 0.0f, 0.0f, 0.0f,
            0.0f, p, 0.0f, 0.0f,
            0.0f, 0.0f, -(_far + _near) / d, -1.0f,
            0.0f, 0.0f, -2.0f * _far * _near / d, 0.0f
        };
        return new Mat4(projMatrix);
    }
}
