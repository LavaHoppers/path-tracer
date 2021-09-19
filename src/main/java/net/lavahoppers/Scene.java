package net.lavahoppers;

/*
 * Scene.java
 * 
 * 30 May 2021
 */


import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;


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
    public static BufferedImage HDRI = null;
    public static BufferedImage exr = null;

    /**
     * Create a new scene
     */
    Scene() {
        meshes = new ArrayList<Mesh>();
        try {
            HDRI = ImageIO.read(new File(PathTracer.hdriFileName));
        } catch (IOException e) {
            System.err.println("Couldn't read HDRI \"" + 
                PathTracer.hdriFileName + "\".");
                System.exit(1);
        }
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
                rgbOut.set(getDirectionalLight(ray));
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

    
    public static Vector3 getDirectionalLight(Vector3 direction) {

        Vector3 horizontalComponent = direction.copy();
        horizontalComponent.setY(0);
        horizontalComponent.setNorm();
        double horizontalDot = horizontalComponent.dot(Vector3.I_HAT);
        double hAngle = Math.acos(horizontalDot);
        if (horizontalComponent.getZ() > 0)
            hAngle = Math.PI * 2 - hAngle;

        double verticalDot = direction.dot(Vector3.J_HAT);
        double vAngle = Math.acos(verticalDot);

        double x = HDRI.getWidth() * (hAngle) / (Math.PI * 2) - 0.00001;
        double y = HDRI.getHeight() * (vAngle / Math.PI);

        int color = HDRI.getRGB((int)x, (int)y);

        return new Vector3(
            (color >> 16) & 0xFF,  
            (color >> 8 ) & 0xFF,
            (color      ) & 0xFF 
        );
    }

}
