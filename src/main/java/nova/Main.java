package nova;

public class Main 
{
    public static void main( String[] args )
    {
        final Controller controller = new Controller();
        controller.startUp();

        Matrix a = Matrix.of(1, 0, 0,
                             0, 1, 0,
                             0, 0, 1);

        Matrix b = Matrix.of().fill(4);

        //Matrix.print(Matrix.mult(a, b));

        Vector3 vec = Vector3.of(3,2);

        Matrix.print(vec);
        Matrix.print(Matrix.mult(a, vec));
    }
}

