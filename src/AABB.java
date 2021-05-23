import java.util.ArrayList;
/**
 * An "axis aligned bounding box" is a primative that is used to subdivide space
 * @author Joshua Hopwood
 * @see <a href=https://github.com/lavahoppers>GitHub</a>
 */
class AABB {

    public Vector3 max;
    public Vector3 min;

    public AABB leftChild;
    public AABB rightChild;

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
     * @see <a href =https://tinyurl.com/s3ypwcpc>Original Author</a>
     * @param origin the origin of the ray
     * @param ray    the unit vector of the ray's direction
     * @param rayInv the reciprocal of the ray elemement wise 
     * @return the magnitude of the vector from origin to intersection, 
     *         -1 if no intersection
     */
    public double intersects(Vector3 origin, Vector3 ray, Vector3 rayInv) {
        
        double t1, t2, t3, t4, t5, t6, tmin, tmax;

        t1 = (min.x - origin.x) * rayInv.x;
        t2 = (max.x - origin.x) * rayInv.x;
        t3 = (min.y - origin.y) * rayInv.y;
        t4 = (max.y - origin.y) * rayInv.y;
        t5 = (min.z - origin.z) * rayInv.z;
        t6 = (max.z - origin.z) * rayInv.z;

        tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if ((tmax < 0) || (tmin > tmax))
            return -1;

        return tmin;
    }

    /**
     * Add a triangle to this AABB and expand the box to accomodate for it
     * @param tri the triangle to be added
     */
    public void add(Triangle tri) {

        if (leaves == null)
            leaves = new ArrayList<Triangle>();
        leaves.add(tri);

        if (min == null) {
            min = new Vector3(
                Math.min(tri.a.x, Math.min(tri.b.x, tri.c.x)),
                Math.min(tri.a.y, Math.min(tri.b.y, tri.c.y)),
                Math.min(tri.a.z, Math.min(tri.b.z, tri.c.z))
            );
        } else {
            min = new Vector3(
                Math.min(min.x, Math.min(tri.a.x, Math.min(tri.b.x, tri.c.x))),
                Math.min(min.y, Math.min(tri.a.y, Math.min(tri.b.y, tri.c.y))),
                Math.min(min.z, Math.min(tri.a.z, Math.min(tri.b.z, tri.c.z)))
            );
        }
        if (max == null) 
            max = new Vector3(
                Math.max(tri.a.x, Math.max(tri.b.x, tri.c.x)),
                Math.max(tri.a.y, Math.max(tri.b.y, tri.c.y)),
                Math.max(tri.a.z, Math.max(tri.b.z, tri.c.z))
            );
        else 
            max = new Vector3(
                Math.max(max.x, Math.max(tri.a.x, Math.max(tri.b.x, tri.c.x))),
                Math.max(max.y, Math.max(tri.a.y, Math.max(tri.b.y, tri.c.y))),
                Math.max(max.z, Math.max(tri.a.z, Math.max(tri.b.z, tri.c.z)))
            );
                    
    }


}