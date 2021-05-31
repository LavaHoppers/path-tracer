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
 * @version 1.0.1
 */
public class Matrix {

    private final double[] data;
    private final int M;
    private final int N;
    
    /**
     * Create a new MxN matrix from a size and a list of values.
     * <p>
     * The Matrix's values are put in the matrix in breadth first order. Any 
     * unspecified entries in the matrix will be defaulted to zero.
     * 
     * @param M the height of the matrix
     * @param N the width of the matrix
     * @param values the value to be put into the matrix
     * @throws IndexOutOfBoundsException if the list of values is larger than
     *         the specified matrix size
     * @since 1.0.1
     */
    public Matrix(int M, int N, double... values) throws IndexOutOfBoundsException {
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
}
