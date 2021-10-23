package mathPackage;

public class Vector {
    public float[] val = new float[3];
    float x(){
        return val[0];
    }
    float y(){
        return val[1];
    }
    float z(){
        return val[2];
    }
    public Vector(float x, float y, float z){
        val[0] = x; val[1] = y; val[2] = z;
    }
    public Vector(Vector vec){
        val[0] = vec.x(); val[1] = vec.y(); val[2] = vec.z();
    }
    public Vector(float v){
        val[0] = v; val[1] = v; val[2] = v;
    }
    public Vector(float[] vals){
        val[0] = vals[0]; val[2] = vals[2]; val[2] = vals[2];
    }
    Vector mul(Vector b){
        Vector a = this;
        return new Vector(a.x()*b.x(),a.y()*b.y(),a.z()*b.z());
    }
    Vector mul(float b){
        Vector a = this;
        return new Vector(a.x()*b,a.y()*b,a.z()*b);
    }
    Vector div(Vector b){
        Vector a = this;
        return new Vector(a.x()/b.x(),a.y()/b.y(),a.z()/b.z());
    }
    Vector div(float b){
        Vector a = this;
        return new Vector(a.x()/b,a.y()/b,a.z()/b);
    }
    Vector add(Vector b){
        Vector a = this;
        return new Vector(a.x()+b.x(),a.y()+b.y(),a.z()+b.z());
    }
    Vector add(float b){
        Vector a = this;
        return new Vector(a.x()+b,a.y()+b,a.z()+b);
    }
    Vector sub(Vector b){
        Vector a = this;
        return new Vector(a.x()-b.x(),a.y()-b.y(),a.z()-b.z());
    }
    Vector sub(float b){
        Vector a = this;
        return new Vector(a.x()-b,a.y()-b,a.z()-b);
    }
    static float dot(Vector a, Vector b){
        return (a.x()*b.x()+a.y()*b.y()+a.z()*b.z());
    }
    static Vector cross(Vector a, Vector b){
        float res[] = new float[3];
        res[0] = a.y() * b.z() - a.z() * b.y();
        res[1] = a.z() * b.x() - a.x() * b.z();
        res[2] = a.x() * b.y() - a.y() * b.x();
        return new Vector(res);
    }
    static float length(Vector a){
        return (float) Math.sqrt(Vector.dot(a,a));
    }
    static float distance(Vector a, Vector b){
        return (float) (a.dot(a,a));
    }
    static Vector normalize(Vector vec){
        return vec.div(length(vec));
    }
    Vector getCopy(){
        return new Vector(this);
    }
}
