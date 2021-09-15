/*
 * Matrix.java
 * 
 * 31 May 2021
 * 
 */

/**
 * An instantiable, mutable class that represents a Matrix from linear algebra.
 * 
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 * @version 0.0.2
 */
public class Matrix {

    protected final double[] data;
    protected final int M;
    protected final int N;

    /**
     * Exception for when there is incompatablity between two matracies'
     * dimensions
     * 
     * @since 0.0.2
     */
    public class DimException extends RuntimeException {

        /**
         * Create a dimension exception with no message
         */
        public DimException() {
            super();
        }

        /**
         * Create a dimension exception with a message for the user
         * 
         * @param message the message for the user.
         */
        public DimException(String message) {
            super(message);
        }

    }
    
    /**
     * Create a new MxN matrix from a size and a list of values.
     * <p>
     * The Matrix's values are put in the matrix in breadth first order. Any 
     * unspecified entries in the matrix will be defaulted to zero.
     * 
     * @param M the height of the matrix
     * @param N the width of the matrix
     * @param values the value to be put into the matrix
     * @since 1.0.1
     */
    public Matrix(int M, int N, double... values) {
        this.M = M;
        this.N = N;
        data = new double[M * N];
        for (int i = 0; i < data.length; i++)
            if (i < values.length)
                data[i] = values[i];
            else
                data[i] = 0.0;
    }

    /**
     * Create a new empty matrix
     * 
     * @param M the height of the matrix
     * @param N the width of the matrix
     * @since 1.0.1
     */
    public Matrix(int M, int N) {
        this(M, N, 0.0);
    }

    /**
     * Set an element of the caller to a new value.
     * 
     * @param m the index of the height
     * @param n the index of the width
     * @param value the value to replace the element
     * @throws IndexOutOfBoundsException if the m or n values are larger than the
     *         martix's height of width respectively
     * @since 1.0.1
     */
    public void set(int m, int n, double value) throws IndexOutOfBoundsException {
        data[n + m * N] = value;
    }

    /**
     * Return an element of the caller.
     * 
     * @param m the index of the height
     * @param n the index of the width
     * @param value the value to replace the element
     * @return the element at location m,n in the caller
     * @throws IndexOutOfBoundsException if the m or n values are larger than the
     *         martix's height of width respectively
     * @since 1.0.1
     */
    public double get(int m, int n) throws IndexOutOfBoundsException {
        return data[n + m * N];
    }

    /**
     * Create a rotation matrix for the X axis.
     * <p>
     * The rotation matrix is a 3x3 Matrix used for roatating vectors
     * 
     * @param theta the angle in radians
     * @return the rotation matrix
     * @since 1.0.1
     */
    public static Matrix getXRotationMatrix(double theta) {
        return new Matrix(3, 3, 
            1.0, 0.0, 0.0,
            0.0, Math.cos(theta), -Math.sin(theta),
            0.0, Math.sin(theta), Math.cos(theta)
        );
    }

    /**
     * Create a rotation matrix for the Y axis.
     * <p>
     * The rotation matrix is a 3x3 Matrix used for roatating vectors
     * 
     * @param theta the angle in radians
     * @return the rotation matrix
     * @since 1.0.1
     */
    public static Matrix getYRotationMatrix(double theta) {
        return new Matrix(3, 3, 
            Math.cos(theta), 0.0, Math.sin(theta),
            0.0, 1.0, 0.0,
            -Math.sin(theta), 0.0, Math.cos(theta)
        );
    }

    /**
     * Create a rotation matrix for the Z axis.
     * <p>
     * The rotation matrix is a 3x3 Matrix used for roatating vectors
     * 
     * @param theta the angle in radians
     * @return the rotation matrix
     * @since 1.0.1
     */
    public static Matrix getZRotationMatrix(double theta) {
        return new Matrix(3, 3, 
            Math.cos(theta), -Math.sin(theta), 0.0,
            Math.sin(theta), Math.cos(theta), 0.0,
            0.0, 0.0, 1.0
        );
    }

    /**
     * Multiply two matracies together and return the product
     * 
     * @param a the matrix to multiply by
     * @return the product
     * @throws Matrix.DimException if the width of the caller is not
     *         equal to the height of {@code a}
     * @since 1.0.1
     */
    public Matrix mult(Matrix a) throws Matrix.DimException {

        if (this.N != a.M)
            throw new DimException();

        Matrix out = new Matrix(this.M, a.N);

        for (int y = 0; y < out.M; y++) {
            for (int x = 0; x < out.N; x++) {

                double value = 0;

                for (int i = 0; i < this.N; i++)
                    value += this.get(y, i) * a.get(i, x);

                out.set(y, x, value);

            }
        }

        return out;
    }

    @Override
    public String toString() {
        String out = "";
        for (int y = 0; y < this.M; y++) {
            out += "| ";
            for (int x = 0; x < this.N; x++) {
                out += String.format("%6.2f ", this.get(y, x));
            }
            out += "|\n";
        }
        return out;
    }

}
