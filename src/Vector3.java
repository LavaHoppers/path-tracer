/**
 * Class for computing vector math. Uses double floating point values and is
 * mutable. Can use both static or method calls.
 */
class Vector3 {

    private final double x;
    private final double y;
    private final double z;

    /**
     * Create a new vector on the origin
     */
    Vector3() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * Create a new vector from three double values
     * @param x the first value
     * @param y the second value
     * @param z the third value
     */
    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * The length of the caller
     * @return the length of the caller
     */
    public double mag() {
        return Math.sqrt(dot(this, this));
    }

    /**
     * Returns a normalized version of the vector
     * @param a the vector
     * @return a, but normalized
     */
    public Vector3 norm() {
        return scale(this, 1f / this.mag());
    }

    /**
     * Return a deep copy of the caller
     * @return a deep copy of the caller
     */
    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    /**
     * Returns the quaternion representation of the caller
     * @return the quaternion representation of the caller
     */
    public Quaternion getQuaternion() {
        return new Quaternion(0.0, x, y, z);
    }


    @Override
    public String toString() {
        return String.format("<%f, %f, %f>", x, y, z);
    }

    /**
     * Adds two vectors element wise and returns the sum
     * @param a the first vector
     * @param b the second vector
     * @return the sum of a and b
     */
    static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    /**
     * Subtracts two vectors element wise and returns the difference
     * @param a the first vector
     * @param b the second vector
     * @return the differnce of a and b
     */
    static Vector3 sub(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * Scales a vector element wise by a given value
     * @param a the vector
     * @param c the scaling factor
     * @return the scaled vector
     */
    static Vector3 scale(Vector3 a, double c) {
        return new Vector3(a.x * c, a.y * c, a.z * c);
    }

    /**
     * Computes the inner product of two vectors
     * @param a the first vector
     * @param b the second vector
     * @return the dot product of the two vectors
     */
    static double dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * Computes the cross product of two vectors
     * @param a the first vector
     * @param b the second vector
     * @return a vector orthoganal to bot a and b
     */
    static Vector3 cross(Vector3 a, Vector3 b) {
        return new Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    /**
     * Rotate the vector v around the vector k theta radians according to the right
     * hand rule. <a href =
     * https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula>
     * Rodrigues' rotation formula</a>
     * 
     * @param v     the vector to be rotated
     * @param k     the vector to act as the axis of rotation
     * @param theta the angle to rotate the vector by
     * @return the rotated vector
     */
    static Vector3 rotate(Vector3 v, Vector3 k, double theta) {
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        Vector3 cross = Vector3.cross(k, v);
        double w = Vector3.dot(k, v) * (1.0 - c);
        return new Vector3(
            v.x * c + cross.x * s + k.x * w,
            v.y * c + cross.y * s + k.y * w,
            v.z * c + cross.z * s + k.z * w
        );
    }

    /**
     * Rotate a vector b around a vector axis a by theta degrees according to the
     * right hand rule.
     * <a href = 
     * https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation#Using_quaternion_as_rotations>
     * Quaternions and spatial rotation</a>
     * @param a     the vector to be rotated
     * @param axis  the axis of rotation
     * @param theta the angle of rotation
     * @return the rotated vector
     */
    public static Vector3 rotateQ(Vector3 a, Vector3 axis, double theta) {
        double s = Math.sin(theta / 2.0);
        Quaternion q = new Quaternion(
            Math.cos(theta / 2.0), axis.x * s, axis.y * s, axis.z * s
        );
        Quaternion p = new Quaternion(0.0, a.x, a.y, a.z);
        Quaternion product = Quaternion.multiply(Quaternion.multiply(q, p), Quaternion.inv(q));
        return product.getVector3();
    } 

    /**
     * <a href = https://blog.molecular-matters.com/2013/05/24/a-faster-quaternion-vector-multiplication/>
     * link to article with the formula</a>
     * @param a the vector to be rotated
     * @param axis the axis to rotate around
     * @param theta the angle to rotate around the axis
     * @return
     */
    public static Vector3 rotateFQ(Vector3 a, Vector3 axis, double theta) {
        
    }

}