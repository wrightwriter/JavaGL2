package mathPackage;

public class Vec3 {
    public float[] val = new float[3];
    public float x(){
        return val[0];
    }
    public float y(){
        return val[1];
    }
    public float z(){
        return val[2];
    }
    public Vec3(float x, float y, float z){
        val[0] = x; val[1] = y; val[2] = z;
    }
    public Vec3(Vec3 vec){
        val[0] = vec.x(); val[1] = vec.y(); val[2] = vec.z();
    }
    public Vec3(float v){
        val[0] = v; val[1] = v; val[2] = v;
    }
    public Vec3(float[] vals){
        val[0] = vals[0]; val[1] = vals[1]; val[2] = vals[2];
    }
    public Vec3 copy(){
        return new Vec3(this);
    }
    public Vec3 mul(Vec3 b){
        Vec3 a = this;
        return new Vec3(a.x()*b.x(),a.y()*b.y(),a.z()*b.z());
    }
    public Vec3 mul(float b){
        Vec3 a = this;
        return new Vec3(a.x()*b,a.y()*b,a.z()*b);
    }
    public Vec3 div(Vec3 b){
        Vec3 a = this;
        return new Vec3(a.x()/b.x(),a.y()/b.y(),a.z()/b.z());
    }
    public Vec3 div(float b){
        Vec3 a = this;
        return new Vec3(a.x()/b,a.y()/b,a.z()/b);
    }
    public Vec3 add(Vec3 b){
        Vec3 a = this;
        return new Vec3(a.x()+b.x(),a.y()+b.y(),a.z()+b.z());
    }
    public Vec3 add(float b){
        Vec3 a = this;
        return new Vec3(a.x()+b,a.y()+b,a.z()+b);
    }
    public Vec3 sub(Vec3 b){
        Vec3 a = this;
        return new Vec3(a.x()-b.x(),a.y()-b.y(),a.z()-b.z());
    }
    public Vec3 sub(float b){
        Vec3 a = this;
        return new Vec3(a.x()-b,a.y()-b,a.z()-b);
    }
    static float dot(Vec3 a, Vec3 b){
        return (a.x()*b.x()+a.y()*b.y()+a.z()*b.z());
    }
    static Vec3 cross(Vec3 a, Vec3 b){
        float res[] = new float[3];
        res[0] = a.y() * b.z() - a.z() * b.y();
        res[1] = a.z() * b.x() - a.x() * b.z();
        res[2] = a.x() * b.y() - a.y() * b.x();
        return new Vec3(res);
    }
    static float length(Vec3 a){
        return (float) Math.sqrt(Vec3.dot(a,a));
    }
    static float distance(Vec3 a, Vec3 b){
        return (float) (a.dot(a,a));
    }
    static Vec3 normalize(Vec3 vec){
        return vec.div(length(vec));
    }
}
