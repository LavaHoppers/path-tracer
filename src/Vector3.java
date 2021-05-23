/**
 * Class for computing 3D vector math
 * <p>
 * Uses double floating point values and is immutable. All method calls will
 * return a brand new vector3.
 * @author Joshua Hopwood
 * @see <a href=https://github.com/lavahoppers>GitHub</a>
 */
class Vector3 {

    public final double x;
    public final double y;
    public final double z;

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
     * Get the length or magnitude of the caller
     * @return the length of the caller
     */
    public double mag() {
        return Math.sqrt(dot(this, this));
    }

    /**
     * Returns a normalized version of the caller
     * @return the caller with a magnitude of one
     */
    public Vector3 norm() {
        return scale(this, 1f / this.mag());
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
     * Adds n vectors
     * @param vecs the n vectors to be added
     * @return the sum of all the vectors
     */
    static Vector3 add(Vector3... vecs) {
        double x = 0.0, y = 0.0, z = 0.0;
        for (Vector3 vec : vecs) {
            x += vec.x;
            y += vec.y;
            z += vec.z;
        }
        return new Vector3(x, y, z);
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
     * Scales vector b by constant c and adds the result to vector a
     * 
     * @param a the vector to be added to 
     * @param b the vector to be scaled
     * @param c the constant
     * @return
     */
    static Vector3 scaleAdd(Vector3 a, Vector3 b, double c) {
        return new Vector3(
            a.x + b.x * c,
            a.y + b.y * c,
            a.z + b.z * c
        );
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
     * hand rule. 
     * @see <a href=https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula>
     * Rodrigues' rotation formula</a>
     * @see Vector3#rotate(Vector3, Vector3, double)
     * @param v     the vector to be rotated
     * @param k     the vector to act as the axis of rotation
     * @param theta the angle to rotate the vector by
     * @return the rotated vector
     * @deprecated aledgedly slowed that quaternion based rotation
     */
    @Deprecated
    public static Vector3 rotateEuler(Vector3 v, Vector3 k, double theta) {
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
     * Fast quaternion based arbitrary axis rotation
     * @see<a href=https://blog.molecular-matters.com/2013/05/24/a-faster-quaternion-vector-multiplication/>
     * article with the formula</a>
     * @see Vector3#rotate(Vector3, Vector3, double)
     * @param a     the vector to be rotated
     * @param axis  the axis to rotate around
     * @param theta the angle to rotate around the axis
     * @return the rotated vector
     * @deprecated use the rotate method until the bugs with this one are fixed
     */
    @Deprecated
    public static Vector3 rotateFQ(Vector3 v, Vector3 axis, double theta) {

        double sin, cos, qx, qy, qz, tx, ty, tz; 

        sin = Math.sin(theta / 2.0);
        cos = Math.cos(theta / 2.0);

        qx = axis.x * sin;
        qy = axis.y * sin;
        qz = axis.z * sin;

        tx = 2 * (qy * v.z - qz * v.y);
        ty = 2 * (qz * v.x - qx * v.z);
        tz = 2 * (qx * v.y - qy * v.x);
        
        return new Vector3(
            v.x + cos * tx + (qy * tz - qz * ty),
            v.y + cos * ty + (qz * tx - qx * tz),
            v.z + cos * tz + (qx * ty - qy * tx)
        );
    }

    /**
     * Rotate a vector around an axis by theta degree according to the right hand rule
     * <p>
     * This implementation uses 'quaternions' represented by four doubles.
     * @see <a href=https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation>
     * Quaternion vector rotation</a>
     * @param p     the vector to be rotated
     * @param axis  the axis of rotation
     * @param theta the angle of rotation
     * @return the rotated vector
     */
    public static Vector3 rotate(Vector3 p, Vector3 axis, double theta) {

        double sin, cos; 

        sin = Math.sin(theta / 2.0);
        cos = Math.cos(theta / 2.0);

        double qx = axis.x * sin;
        double qy = axis.y * sin; // p' = qpq'
        double qz = axis.z * sin;

        double qxp = qx * -1.0;
        double qyp = qy * -1.0;
        double qzp = qz * -1.0;

        double tw = -qx * p.x - qy * p.y - qz * p.z;
        double tx = cos * p.x + qy * p.z - qz * p.y;
        double ty = cos * p.y - qx * p.z + qz * p.x;
        double tz = cos * p.z + qx * p.y - qy * p.x;

        return new Vector3(
            tw * qxp + tx * cos + ty * qzp - tz * qyp,
            tw * qyp - tx * qzp + ty * cos + tz * qxp,
            tw * qzp + tx * qyp - ty * qxp + tz * cos 
        );
       
    }


}