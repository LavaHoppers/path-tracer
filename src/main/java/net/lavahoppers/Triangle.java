package net.lavahoppers;
/*
 * Triangle.java
 * 
 * 30 May 2021
 * 
 */

/**
 * Class for use with Vector3 to build 3d models that can
 * be displayed with path tracing.
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
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
    public double intersects(Vector3 rayOrigin, Vector3 rayVector) {
        
        double epsilon = 0.0000001;
        Vector3 edge1 = b.sub(a);
        Vector3 edge2 = c.sub(a);
        Vector3 h = rayVector.cross(edge2);
        double a = edge1.dot(h);

        //if (-epsilon < a && a < epsilon)
        //    return -1;    

        double f = 1f / a;
        Vector3 s = rayOrigin.sub(this.a);
        double u = f * (s.dot(h));
        if (u < 0 || 1 < u)
            return -1;
        
        Vector3 q = s.cross(edge1);
        double v = f * rayVector.dot(q);
        if (v < 0 || 1 < u + v)
            return -1;
    
        double t = f * edge2.dot(q);
        if (epsilon < t) {
            return t;
        }

        return -1;
        
        
    }

    public Vector3 norm() {
        return (b.sub(a)).cross(c.sub(a)).setNorm();
    }

    
}