/**
 * Class for computing 3D vector math
 * <p>
 * Uses double floating point values and is mutable.
 * 
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
class Vector3 {

    private double x;
    private double y;
    private double z;

    /**
     * Create a new vector from three double values
     * 
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
     * Create a new vector on the origin
     */
    Vector3() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * Set the components of the caller
     * 
     * @param x the new x component
     * @param y the new y component
     * @param z the new z component
     */
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * return the x component of the caller
     * 
     * @return the x component of the caller
     */
    public double getX() {
        return this.x;
    }

    /**
     * return the y component of the caller
     * 
     * @return the y component of the caller
     */
    public double getY() {
        return this.y;
    }

    /**
     * return the z component of the caller
     * 
     * @return the z component of the caller
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Set the x component of the caller
     * 
     * @param x the new x component
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the y component of the caller
     * 
     * @param y the new y component
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Set the z component of the caller
     * 
     * @param z the new z component
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * returns the element-wise reciprocal of the caller
     * 
     * @return the element-wise reciprocal of the caller
     */
    public Vector3 reciprocal() {
        return new Vector3(1.0 / this.x, 1.0 / this.y, 1.0 / this.z);
    }

    /**
     * Get a component-wise copy of the caller
     * 
     * @return a copy of the caller
     */
    public Vector3 copy() {
        return new Vector3(this.x, this.y, this.z);
    }

    /**
     * Get the length or magnitude of the caller
     * 
     * @return the length of the caller
     */
    public double mag() {
        return Math.sqrt(this.dot(this));
    }

    /**
     * Set the caller to a normalized version of itself
     * 
     * @return the caller
     */
    public Vector3 setNorm() {
        double invMag = 1.0 / this.mag();
        this.x *= invMag;
        this.y *= invMag;
        this.z *= invMag;
        return this;
    }

    /**
     * Returns a normalized version of the caller
     * 
     * @return the caller with a magnitude of one
     */
    public Vector3 norm() {
        return this.copy().setNorm();
    }

    /**
     * Return the caller as a string in the format:
     * <p>
     * <b>&lt;x, y, z&gt;</b>
     */
    @Override
    public String toString() {
        return String.format("<%f, %f, %f>", x, y, z);
    }

    /**
     * Add the components of a vector to the caller
     * 
     * @param a the vector to be added to the caller
     * @return the caller
     */
    public Vector3 setAdd(Vector3 a) {
        this.x += a.x;
        this.y += a.y;
        this.z += a.z;
        return this;
    }

    /**
     * Adds two vectors element wise and returns the sum
     * 
     * @param a the vector to add to the caller
     * @return the sum vector
     */
    public Vector3 add(Vector3 a) {
        return this.copy().setAdd(a);
    }

    /**
     * Adds n vectors to the caller
     * 
     * @param vecs the n vectors to be added
     * @return the caller
     */
    public Vector3 setAdd(Vector3... vecs) {
        for (Vector3 vec : vecs) {
            this.x += vec.x;
            this.y += vec.y;
            this.z += vec.z;
        }
        return this;
    }

    /**
     * Return a sum of the caller and all the vectors
     * 
     * @param vecs the extra vector
     * @return the sum of all the vectors
     */
    public Vector3 add(Vector3... vecs) {
        return this.copy().setAdd(vecs);
    }

    /**
     * Set the caller to the differnce of the caller and a vector
     * 
     * @param a the vector to be subtracted
     * @return the caller
     */
    public Vector3 setSub(Vector3 a) {
        this.x -= a.x;
        this.y -= a.y;
        this.z -= a.z;
        return this;
    }

    /**
     * Subtracts a vector from the caller
     * 
     * @param a the vector to be subracted
     * @return the difference of the caller and the vector
     */
    public Vector3 sub(Vector3 a) {
        return this.copy().setSub(a);
    }

    /**
     * Sets the caller to component-wise scaled version of itself
     * 
     * @param c the constant to scale the caller by
     * @return the caller
     */
    public Vector3 setScale(double c) {
        this.x *= c;
        this.y *= c;
        this.z *= c;
        return this;
    }

    /**
     * Scales a vector element wise by a given value
     * 
     * @param c the scaling factor
     * @return the scaled vector
     */
    public Vector3 scale(double c) {
        return this.copy().setScale(c);
    }

    /**
     * Set the caller to sum of the caller and a scaled vector
     * 
     * @param a the vector to scale
     * @param c the constant to scale by
     * @return the caller
     */
    public Vector3 setScaleAdd(Vector3 a, double c) {
        this.x += a.x * c;
        this.y += a.y * c;
        this.z += a.z * c;
        return this;
    }

    /**
     * Scales vector a by constant c and adds the result to vector the caller
     * 
     * @param a the vector to be scaled and added
     * @param c the constant to scale by
     * @return the sum
     */
    public Vector3 scaleAdd(Vector3 a, double c) {
        return this.copy().setScaleAdd(a, c);
    }

    /**
     * Computes the inner product of the caller and a vector
     * 
     * @param a the vector
     * @return the dot product of the two vectors
     */
    public double dot(Vector3 a) {
        return a.x * this.x + a.y * this.y + a.z * this.z;
    }

    /**
     * Computes the cross product of the caller and a vector
     * 
     * @param a the vector
     * @return a vector orthoganal to both the caller and a vector
     */
    public Vector3 cross(Vector3 a) {
        return new Vector3(this.y * a.z - this.z * a.y, this.z * a.x - this.x * a.z, this.x * a.y - this.y * a.x);
    }

    /**
     * Sets this vector to a vector with the minimum elements of the caller and and
     * another vector
     * 
     * @param a the other vector
     * @return the caller
     */
    public Vector3 setElemMin(Vector3 a) {
        this.x = Math.min(this.x, a.x);
        this.y = Math.min(this.y, a.y);
        this.z = Math.min(this.z, a.z);
        return this;
    }

    /**
     * Sets this vector to a vector with the maxium elements of the caller and and
     * another vector
     * 
     * @param a the other vector
     * @return the caller
     */
    public Vector3 setElemMax(Vector3 a) {
        this.x = Math.max(this.x, a.x);
        this.y = Math.max(this.y, a.y);
        this.z = Math.max(this.z, a.z);
        return this;
    }

    /**
     * Sets this vector's components to the minimum value for each component of each
     * vector
     * 
     * @param vecs the other vectors
     * @return the caller
     */
    public Vector3 setElemMin(Vector3... vecs) {
        for (Vector3 vec : vecs)
            this.setElemMin(vec);
        return this;
    }

    /**
     * Sets this vector's components to the maximum value for each component of each
     * vector
     * 
     * @param vecs the other vectors
     * @return the caller
     */
    public Vector3 setElemMax(Vector3... vecs) {
        for (Vector3 vec : vecs)
            this.setElemMax(vec);
        return this;
    }

    /**
     * Rotate the vector v around the vector k theta radians according to the right
     * hand rule.
     * 
     * @see <a href=https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula>
     *      Rodrigues' rotation formula</a>
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
        Vector3 cross = k.cross(v);
        double w = k.dot(v) * (1.0 - c);
        return new Vector3(v.x * c + cross.x * s + k.x * w, v.y * c + cross.y * s + k.y * w,
                v.z * c + cross.z * s + k.z * w);
    }

    /**
     * Fast quaternion based arbitrary axis rotation
     * 
     * @see<a href=https://blog.molecular-matters.com/2013/05/24/a-faster-quaternion-vector-multiplication/>
     *        article with the formula</a>
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

        return new Vector3(v.x + cos * tx + (qy * tz - qz * ty), v.y + cos * ty + (qz * tx - qx * tz),
                v.z + cos * tz + (qx * ty - qy * tx));
    }

    /**
     * Rotate a vector around an axis by theta degree according to the right hand
     * rule
     * <p>
     * This implementation uses 'quaternions' represented by four doubles.
     * 
     * @see <a href=https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation>
     *      Quaternion vector rotation</a>
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

        return new Vector3(tw * qxp + tx * cos + ty * qzp - tz * qyp, tw * qyp - tx * qzp + ty * cos + tz * qxp,
                tw * qzp + tx * qyp - ty * qxp + tz * cos);

    }

}