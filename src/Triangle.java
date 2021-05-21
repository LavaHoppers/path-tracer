/**
 * Class for use with Vector3 to build 3d models that can
 *  be displayed with path tracing.
 * @see Vector3
 */
class Triangle {

    public Vector3 a;
    public Vector3 b;
    public Vector3 c;

    /**
     * Create a triangle from three points in 3d space.
     * @param a point 1
     * @param b point 2
     * @param c point 3
     */
    Triangle(Vector3 a, Vector3 b, Vector3 c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    /**
     * Detects if a ray originating from a point intersects with a triangle in 3d space.
     * Somewhat unintuitively, this function returns the point of intersection, or null 
     * if the triangle is not intersected.
     * 
     * https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
     * @param rayOrigin the origin of the ray
     * @param rayVector the ray dir vec
     * @param tri the triangle
     * @return null if there is no intersection, the point of intersection otherwise
     */
    public Vector3 intersects(Vector3 rayOrigin, Vector3 rayVector) {
        
        double epsilon = 0.0000001;
        Vector3 edge1 = Vector3.sub(this.b, this.a);
        Vector3 edge2 = Vector3.sub(this.c, this.a);
        Vector3 h = Vector3.cross(rayVector, edge2);
        double a = Vector3.dot(edge1, h);

        if (-epsilon < a && a < epsilon)
            return null;    

        double f = 1f / a;
        Vector3 s = Vector3.sub(rayOrigin, this.a);
        double u = f * (Vector3.dot(s, h));
        if (u < 0 || 1 < u)
            return null;
        
        Vector3 q = Vector3.cross(s, edge1);
        double v = f * Vector3.dot(rayVector, q);
        if (v < 0 || 1 < u + v)
            return null;
    
        double t = f * Vector3.dot(edge2, q);
        if (epsilon < t) {
            return Vector3.scaleAdd(rayOrigin, rayVector, t);
        } else {
            return null;
        }
        
    }

    
}