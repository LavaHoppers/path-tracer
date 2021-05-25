import java.util.ArrayList;

/**
 * An "axis aligned bounding box" is a primative that is used to subdivide space
 * 
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
class AABB {

    public Vector3 max;
    public Vector3 min;
    public AABB    leftChild;
    public AABB    rightChild;
    
    public ArrayList<Triangle> leaves;

    /**
     * Create a new Axis aligned bounding box.
     * <p>
     * On creation, all of it's elements will be assigned to null
     */
    AABB() {
        this.min = null;
        this.max = null;
        this.leftChild = null;
        this.rightChild = null;
        this.leaves = null;
    }

    /**
     * Get the distance to the intersection of a bounding box
     * <p>
     * How this method works is entirely a mystery to me!
     * 
     * @see <a href =https://tinyurl.com/s3ypwcpc>Original Author</a>
     * @param origin the origin of the ray
     * @param ray    the unit vector of the ray's direction
     * @param rayInv the reciprocal of the ray elemement wise
     * @return the magnitude of the vector from origin to intersection, -1 if no
     *         intersection
     */
    public double intersects(Vector3 origin, Vector3 ray, Vector3 rayInv) {

        double t1, t2, t3, t4, t5, t6, tmin, tmax;

        t1 = (min.getX() - origin.getX()) * rayInv.getX();
        t2 = (max.getX() - origin.getX()) * rayInv.getX();
        t3 = (min.getY() - origin.getY()) * rayInv.getY();
        t4 = (max.getY() - origin.getY()) * rayInv.getY();
        t5 = (min.getZ() - origin.getZ()) * rayInv.getZ();
        t6 = (max.getZ() - origin.getZ()) * rayInv.getZ();

        tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if ((tmax < 0) || (tmin > tmax))
            return -1;

        return tmin;
    }

    /**
     * Add a triangle to this AABB and expand the box to accomodate for it
     * 
     * @param tri the triangle to be added
     */
    public void add(Triangle tri) {

        if (leaves == null)
            leaves = new ArrayList<Triangle>();
        if (min == null)
            min = tri.a.copy();
        if (max == null)
            max = tri.a.copy();

        leaves.add(tri);
        min.setElemMin(tri.a, tri.b, tri.c);
        max.setElemMax(tri.a, tri.b, tri.c);

    }

}