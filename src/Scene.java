/*
 * Scene.java
 * 
 * 30 May 2021
 */

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class for containing all the 3D elements of a scene
 * <p>
 * There should be one of these per render and all the objects, lights, and
 * materials should be added to it
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class Scene {
    
    public ArrayList<Mesh> meshes;

    /**
     * Create a new scene
     */
    Scene() {
        meshes = new ArrayList<Mesh>();
    }

    /**
     * Determine if a ray intersects with elements of the scene
     * <p>
     * This funciton is the core of path tracing and improving it's speed will
     * significantly improve render time
     * 
     * @param origin  the origin point of the ray
     * @param ray     the ray normalized
     * @param ptOut   the Vector3 to be overriden to the point of intersection
     * @param normOut the Vector3 to be overridden to the normal of the intersection
     * @param rgbOut  the RGB value out
     * @return the distance from the origin to the point of intersection, -1 if no
     *         intersection
     */
    public boolean intersect(Vector3 origin, Vector3 ray, Vector3 ptOut, Vector3 normOut, Vector3 rgbOut) {

        Triangle closeTri = null;
        double   closeDist = 0;

        LinkedList<AABB> AABBQueue = new LinkedList<AABB>();
        Vector3 rayInv = ray.reciprocal();

        for (Mesh mesh : meshes) {

            AABBQueue.add(mesh.getRoot());

            while (0 < AABBQueue.size()) {

                AABB current = AABBQueue.pop();

                double distance = current.intersects(origin, ray, rayInv);

                if (distance == -1)
                    continue;

                if (closeTri == null || distance < closeDist) {
                    if (current.getLeftChild() != null)
                        AABBQueue.add(current.getLeftChild());
                    if (current.getRightChild() != null)
                        AABBQueue.add(current.getRightChild());
                }

                if (!current.isLeafNode())
                    continue;

                for (Triangle tri : current.getLeaves()) {

                    distance = tri.intersects(origin, ray);

                    if (distance == -1)
                        continue;

                    if (closeTri == null || distance < closeDist) {
                        closeTri = tri;
                        closeDist = distance;
                    }  
                    
                }

            }

        }

        if (closeTri == null) {
            if (rgbOut != null)
                rgbOut.set(135f, 206f, 235f);
            return false;
            
        }
        if (ptOut != null)
            ptOut.set(origin.copy().setScaleAdd(ray, closeDist));
        if (normOut != null)
            normOut.set(closeTri.norm());
        if (rgbOut != null)
            rgbOut.set(new Vector3(255, 255, 255));
        
        return true;

    }

}
