import java.util.ArrayList;

/**
 * An "axis aligned bounding box" is a primative that is used to subdivide space
 */
class AABB {

    public Vector3 max;
    public Vector3 min;

    public AABB leftChild;
    public AABB rightChild;

    public ArrayList<Triangle> leaves;

    /**
     * Create a new Axis aligned bounding box. 
     */
    AABB() {

        this.min = null;
        this.max = null;
        
        this.leftChild = null;
        this.rightChild = null;

        this.leaves = null;
    }

    /**
     * Test if a ray intersects the calling AABB. <a href =
     * https://tinyurl.com/s3ypwcpc> Link to article with algo</a>
     * 
     * @param origin the origin of the ray
     * @param ray    the unit vector of the ray's direction
     * @return true if the ray intersects, false otherwise
     */
    public boolean intersects(Vector3 origin, Vector3 ray) {
        
        double rx, ry, rz, t1, t2, t3, t4, t5, t6, tmin, tmax;

        rx = 1.0 / ray.x;
        ry = 1.0 / ray.y;
        rz = 1.0 / ray.z;

        t1 = (min.x - origin.x) * rx;
        t2 = (max.x - origin.x) * rx;
        t3 = (min.y - origin.y) * ry;
        t4 = (max.y - origin.y) * ry;
        t5 = (min.z - origin.z) * rz;
        t6 = (max.z - origin.z) * rz;

        tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, 
        // but the whole AABB is behind us
        if (tmax < 0)
            return false;

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax)
            return false;

        return true;
    }

    /**
     * Add a triangle to this AABB and expand the box to accomodate for it
     * @param tri the triangle to be added
     */
    public void add(Triangle tri) {

        if (leaves == null)
            leaves = new ArrayList<Triangle>();
        leaves.add(tri);

        if (min == null)
            min = new Vector3(
                Math.min(tri.a.x, Math.min(tri.b.x, tri.c.x)),
                Math.min(tri.a.y, Math.min(tri.b.y, tri.c.y)),
                Math.min(tri.a.z, Math.min(tri.b.z, tri.c.z))
            );
        else 
            min = new Vector3(
                Math.min(min.x, Math.min(tri.a.x, Math.min(tri.b.x, tri.c.x))),
                Math.min(min.y, Math.min(tri.a.y, Math.min(tri.b.y, tri.c.y))),
                Math.min(min.z, Math.min(tri.a.z, Math.min(tri.b.z, tri.c.z)))
            );
        
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