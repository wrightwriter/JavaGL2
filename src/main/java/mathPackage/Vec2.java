package mathPackage;

public class Vec2 {
    public float[] val = new float[2];
    public float x(){
        return val[0];
    }
    public float y(){
        return val[1];
    }
    public Vec2(float x, float y){
        val[0] = x; val[1] = y;
    }
    public Vec2(Vec2 vec){
        val[0] = vec.x(); val[1] = vec.y();
    }
    public Vec2(float v){
        val[0] = v; val[1] = v;
    }
    public Vec2(float[] vals){
        val[0] = vals[0]; val[1] = vals[1];
    }
    public Vec2 copy(){
        return new Vec2(this);
    }
    public Vec2 mul(Vec2 b){
        Vec2 a = this;
        return new Vec2(a.x()*b.x(),a.y()*b.y());
    }
    public Vec2 mul(float b){
        Vec2 a = this;
        return new Vec2(a.x()*b,a.y()*b);
    }
    public Vec2 div(Vec2 b){
        Vec2 a = this;
        return new Vec2(a.x()/b.x(),a.y()/b.y());
    }
    public Vec2 div(float b){
        Vec2 a = this;
        return new Vec2(a.x()/b,a.y()/b);
    }
    public Vec2 add(Vec2 b){
        Vec2 a = this;
        return new Vec2(a.x()+b.x(),a.y()+b.y());
    }
    public Vec2 add(float b){
        Vec2 a = this;
        return new Vec2(a.x()+b,a.y()+b);
    }
    public Vec2 sub(Vec2 b){
        Vec2 a = this;
        return new Vec2(a.x()-b.x(),a.y()-b.y());
    }
    public Vec2 sub(float b){
        Vec2 a = this;
        return new Vec2(a.x()-b,a.y()-b);
    }
    static float dot(Vec2 a, Vec2 b){
        return (a.x()*b.x()+a.y()*b.y());
    }
//    static Vec2 cross(Vec2 a, Vec2 b){
//        float res[] = new float[3];
//        res[0] = a.y() * b.z() - a.z() * b.y();
//        res[1] = a.z() * b.x() - a.x() * b.z();
//        res[2] = a.x() * b.y() - a.y() * b.x();
//        return new Vec2(res);
//    }
    static float length(Vec2 a){
        return (float) Math.sqrt(Vec2.dot(a,a));
    }
    static float distance(Vec2 a, Vec2 b){
        return (float) (a.dot(a,a));
    }
    static Vec2 normalize(Vec2 vec){
        return vec.div(length(vec));
    }
}
