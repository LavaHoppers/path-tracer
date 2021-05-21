import java.util.ArrayList;
/**
 * Class for use with the mesh class. It is an acceleration structure called a 
 * "bounding volume hierarchy". By subdividing the primitives of a mesh into a tree
 * structure, you can efficiently cull many of the intersection checks
 */
class BVH {

    public AABB root;
    
    /**
     * Constructor for a new Bounding Volume Hierarchy. Takes the triangles 
     * from a mesh and subdivides them into Axis aligned bounding boxes for 
     * efficient searching.
     * 
     * @param tris the arraylist of triangles to subdivide
     */
    BVH (ArrayList<Triangle> tris) {

        root = new AABB();
        for (Triangle tri : tris)
            root.add(tri);
            
        /* further subdivide the mesh with a recursive funtion */
        subdivide(root);
    }

    /**
     * Recursively subdivies the bounding box into 8 sub boxes
     * @param parent the box to be subdived
     */
    private void subdivide(AABB parent) {

        /* termination condition: if there are not enough leaves
        to benifit from subdividing, return */
        if (parent.leaves.size() <= 25)
            return;

        /* see which of the boxes dimensions is the longest and divide on that axis
        the results are saves in booleans names x, and y. if it's neither of those it's 
        z */
        double xLength = parent.max.x - parent.min.x;
        double yLength = parent.max.y - parent.min.y;
        double zLength = parent.max.z - parent.min.z;
        double maxLen = Math.max(xLength, Math.max(yLength, zLength));
        boolean x = xLength == maxLen;
        boolean y = yLength == maxLen;
        
        AABB leftChild = new AABB();
        AABB rightChild = new AABB();
        
        if (x)
            for (Triangle tri : parent.leaves) {
                double midPoint = (tri.a.x + tri.b.x + tri.c.x) / 3.0;
                if ((midPoint - parent.min.x) < (parent.max.x - midPoint))
                    rightChild.add(tri);
                else
                    leftChild.add(tri);
            }
        else if (y)
            for (Triangle tri : parent.leaves) {
                double midPoint = (tri.a.y + tri.b.y + tri.c.y) / 3.0;
                if ((midPoint - parent.min.y) < (parent.max.y - midPoint))
                    rightChild.add(tri);
                else
                    leftChild.add(tri);
            }
        else
            for (Triangle tri : parent.leaves) {
                double midPoint = (tri.a.z + tri.b.z + tri.c.z) / 3.0;
                if ((midPoint - parent.min.z) < (parent.max.z - midPoint))
                    rightChild.add(tri);
                else
                    leftChild.add(tri);
            }

        parent.leaves.clear();
        parent.leaves = null;

        if (rightChild.leaves != null) {
            parent.rightChild = rightChild;
            rightChild = null;
            subdivide(parent.rightChild);
        }

        if (leftChild.leaves != null) {
            parent.leftChild = leftChild;
            leftChild = null;
            subdivide(parent.leftChild);
        }

        
            
    }




}
