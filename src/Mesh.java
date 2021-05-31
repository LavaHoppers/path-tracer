/*
 * Mesh.java
 * 
 * 29 May 2021
 */

import java.util.ArrayList;

/**
 * Class for representing 3D objects as a mesh
 * <p>
 * Meshes can be created by directly adding triangles and verticies in code, or
 * they can be created by reading information from an obj file
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
class Mesh {

    private final AABB root = new AABB();
    private final ArrayList<Vector3> verticies;
    private final ArrayList<Triangle> triangles;

    /**
     * Creates a new mesh!
     */
    public Mesh(ArrayList<Vector3> verticies, ArrayList<Triangle> triangles) {
        this.verticies = verticies;
        this.triangles = triangles;
        root.add(triangles);
        System.out.println("Building BVH");
        root.buildBVH();
        System.out.println("Done Building BVH");
    }

    /**
     * Returns the root of this mesh
     * 
     * @return the root of this mesh
     */
    public AABB getRoot() {
        return root;
    }

    /**
     * Returns the verticies in this mesh
     * 
     * @return the verticies in this mesh
     */
    public ArrayList<Vector3> getVerticies() {
        return verticies;
    }

    /**
     * Returns the triangles in this mesh
     * 
     * @return the triangles in this mesh
     */
    public ArrayList<Triangle> getTriangles() {
        return triangles;
    }

}