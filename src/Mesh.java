import java.util.ArrayList;

/**
 * Class for representing 3D objects as a mesh
 * <p>
 * Meshes can be created by directly adding triangles and verticies
 * in code, or they can be created by reading information from an
 * obj file
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
class Mesh {

    public AABB root;
    public ArrayList<Vector3> verticies;
    public ArrayList<Triangle> triangles;


    /**
     * Creates a new mesh with no 3d data
     */
    Mesh() {
        verticies = new ArrayList<Vector3>();
        triangles = new ArrayList<Triangle>();
    } 

    /**
     * Once a mesh contains all of it's data, run this method
     * to create the BVH for it
     */
    public void buildBVH() {
        root = BVHBuilder.build(triangles);
    }
    
}