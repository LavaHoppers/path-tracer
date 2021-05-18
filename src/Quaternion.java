class Quaternion {

    private double w;
    private double x;
    private double y;
    private double z;

    /**
     * Create a new quaternion for use with rotations
     * 
     * @param w the real component
     * @param x the i component
     * @param y the j component
     * @param z the k component
     */
    Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the vector component of the caller
     * @return the vector component of the caller
     */
    Vector3 getVector3() {
        return new Vector3(x, y, z);
    }

    /**
     * Adds two quaternions and returns the sum
     * 
     * @param a the first quaternion
     * @param b the second quaternion
     * @return the sum of a and b
     */
    public static Quaternion add(Quaternion a, Quaternion b) {
        return new Quaternion(a.w + b.w, a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /**
     * Subtracts two quaternions and returns the difference
     * 
     * @param a the first quaternion
     * @param b the second quaternion
     * @return the difference of a and b
     */
    public static Quaternion sub(Quaternion a, Quaternion b) {
        return new Quaternion(a.w - b.w, a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * multiplies to quaternions according to the rules specified on this webpage:
     * https://en.wikipedia.org/wiki/Quaternion
     * 
     * @param a first quaternion
     * @param b second quaternion
     * @return the product
     */
    public static Quaternion multiply(Quaternion a, Quaternion b) {
        return new Quaternion(
            a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z,
            a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
            a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x,
            a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w
        );
    }

    /**
     * Return the inverse of the quaternion
     * 
     * @param a the quaternion to be inverted
     * @return the inverted quaternion
     */
    public static Quaternion inv(Quaternion a) {
        double s = a.w * a.w + a.x * a.x + a.y * a.y + a.z * a.z;
        return scale(new Quaternion(a.w, -a.x, -a.y, -a.z), 1.0 / s);
    }

    /**
     * Scale a Quaternion by a constant c
     * 
     * @param a the quaternion
     * @param c the value to scale by
     * @return the scaled quaternion
     */
    public static Quaternion scale(Quaternion a, double c) {
        return new Quaternion(a.w * c, a.x * c, a.y * c, a.z * c);
    }


}