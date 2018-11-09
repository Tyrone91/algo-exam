

public class Vector3 extends Matrix {

    public Vector3(double a, double b, double c){
        super(1,3);
        data(0, 0, a);
        data(0, 1, b);
        data(0, 2, c);
    }

    public Vector3(){
        this(0,0,1);
    }


    public double getX(){
        return data(0, 0);
    }

    public double getY(){
        return data(0, 1);
    }

    public Vector3 setX(double d){
        data(0,0, d);
        return this;
    }

    public Vector3 setY(double d){
        data(0,1, d);
        return this;
    }

    public static Vector3 of(double a, double b){
        return of(a,b,1);
    }

    public static Vector3 of(double a, double b, double c){
        return new Vector3(a,b,c);
    }

    public static Vector3 asVec(Matrix a){
        return of(
            a.data(0, 0),
            a.data(0, 2),
            a.data(0, 3)
        );
    }
}