

public class Matrix {

    public static Matrix UNIT_MATRIX = Matrix.of(1,0,0,
                                                 0,1,0, 
                                                 0,0,1);            

    public static interface MatrixConsumer{
        public void accept(int column, int row, double value);
    }

    private double[][] m_Data;

    public Matrix(double[][] data){
        m_Data = data;
    }

    public Matrix(int columns, int rows){
        this(makeArray(columns, rows));
    }

    public Matrix(){
        this(new double[][]{
            {0,0,0},
            {0,0,0},
            {0,0,0},
        });
    }

    public void forEach(MatrixConsumer consumer){
        for(int column = 0; column < m_Data.length; ++column){
            for(int row = 0; row < m_Data[column].length; ++row){
                consumer.accept(column, row, data(column,row) );
            }
        }
    }

    public double data(int column, int row){
        return m_Data[column][row];
    }

    public Matrix data(int column, int row, double value){
        m_Data[column][row] = value;
        return this;
    }

    public Matrix assign(Matrix src){
        src.forEach(this::data);
        return this;
    }

    public Matrix fill(double value){
        forEach( (column, row, val) -> {
            data(column, row, value);
        });
        return this;
    }

    public int columns(){
        return m_Data.length;
    }

    public int rows(){
        return 3; //TODO: make variable
    }

    private static double[][] makeArray(int columns, int rows){
        final double[][] col = new double[columns][];
        for(int i = 0; i < col.length; ++i){
            col[i] = new double[rows];
        }
        return col;
    }

    private static double valueAt(Matrix a, Matrix b, int column, int row){
        double res = 0;
        for(int i = 0; i < a.columns(); ++i){
            res += a.data( i, row) * b.data(column, i);
        }
        return res;
    }

    public static Matrix print(Matrix a){
        System.out.println("M = {");
        System.out.print("\t");
        for(int row = 0; row < a.rows(); ++row){
            for(int column = 0; column < a.columns(); ++column){
                System.out.print(a.data(column, row));
                if( (column+1) != a.columns() ){
                    System.out.print(", ");
                }
            }
            System.out.println();
            if( (row+1) != a.rows() ){
                System.out.print("\t");
            }
        }
        System.out.print("}");
        return a;
    }

    public static Matrix of(double... values){
        final Matrix m = new Matrix();
        int row = 0;
        int column = 0;
        int cnt = 0;
        while(cnt < values.length){
            m.data(column++, row, values[cnt++]);
            if(column >= m.columns()){
                column = 0;
                ++row;
            }
        }
        return m;
    }

    private static void multHelp(Matrix a, Matrix b, Matrix target){
        target.forEach( (column,row,val) -> {
            target.data(column, row, Matrix.valueAt(a, b, column, row));
        });
    }

    public static Matrix mult(Matrix a, Matrix b){
        Matrix m = new Matrix(b.columns(), a.rows());
        multHelp(a, b, m);
        return m;
    }

    public static Matrix mult(Matrix a, Matrix... rest){
        Matrix res = a;
        for(Matrix b : rest){
            Matrix m  = new Matrix(b.columns(), a.rows());
            multHelp(res, b, m);
            res = m;
        }
        return res;
    }

    public static Matrix unit(){
        return UNIT_MATRIX;
    }

    public static Vector3 mult(Matrix a, Vector3 b){
        Vector3 res = Vector3.of(0, 0);
        multHelp(a, b, res);
        return res;
    }

    public static Matrix inverseTranslate(int x, int y){
        return Matrix.of( 1, 0, -x,
                          0, 1, -y,
                          0, 0, 1);
    }

    public static Matrix inverseRotate(float alpha){
        return Matrix.of( Math.cos(-alpha), -Math.sin(-alpha), 0,
                          Math.sin(-alpha), Math.cos(-alpha), 0,
                          0, 0, 1);
    }

    public static Matrix inverseScale(float s){
        if(s == 0){
            return Matrix.unit();
        }
        return Matrix.of( 1/s, 0, 0,
                          0, 1/s, 0,
                          0, 0, 1);
    }

    public static Matrix inverseXShear(float s){
        if(s == 0){
            return Matrix.unit();
        }
        return Matrix.of( 1, 1/s, 0,
                          0, 1, 0,
                          0, 0, 1);
    }
    
    public static Matrix inverseYShear(float s){
        if(s == 0){
            return Matrix.unit();
        }
        return Matrix.of( 1, 0, 0,
                          1/s, 1, 0,
                          0, 0, 1);
    }
}