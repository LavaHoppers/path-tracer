import java.util.ArrayList;
import java.util.LinkedList;

public class BVHBuilder {

    /**
    * Constructor for a new Bounding Volume Hierarchy. Takes triangles 
    * from a mesh and subdivides them into Axis aligned bounding boxes for 
    * efficient searching.
    * 
    * @param tris the arraylist of triangles to subdivide
    */
    public static AABB build(ArrayList<Triangle> tris) {

        AABB root = new AABB();
        for (Triangle tri : tris)
            root.add(tri);

        LinkedList<AABB> queue = new LinkedList<AABB>();
        queue.add(root);

        while(queue.size() > 0) {

            AABB curr = queue.pop();

            double xLength = curr.max.getX() - curr.min.getX();
            double yLength = curr.max.getY() - curr.min.getY();
            double zLength = curr.max.getZ() - curr.min.getZ();
            double maxLen = Math.max(xLength, Math.max(yLength, zLength));
            boolean x = xLength == maxLen;
            boolean y = yLength == maxLen;

            AABB leftChild = new AABB();
            AABB rightChild = new AABB();

            int tries = 0;

            algo:
            while (tries < 3) {
                if (x)
                    for (Triangle tri : curr.leaves) {
                        double midPoint = (tri.a.getX() + tri.b.getX() + tri.c.getX()) / 3.0;
                        if ((midPoint - curr.min.getX()) < (curr.max.getX() - midPoint))
                            rightChild.add(tri);
                        else
                            leftChild.add(tri);
                    }
                else if (y)
                    for (Triangle tri : curr.leaves) {
                        double midPoint = (tri.a.getY() + tri.b.getY() + tri.c.getY()) / 3.0;
                        if ((midPoint - curr.min.getY()) < (curr.max.getY() - midPoint))
                            rightChild.add(tri);
                        else
                            leftChild.add(tri);
                    }
                else
                    for (Triangle tri : curr.leaves) {
                        double midPoint = (tri.a.getZ() + tri.b.getZ() + tri.c.getZ()) / 3.0;
                        if ((midPoint - curr.min.getZ()) < (curr.max.getZ() - midPoint))
                            rightChild.add(tri);
                        else
                            leftChild.add(tri);
                    }

                if (leftChild.leaves != null && rightChild.leaves != null) {
                    curr.leftChild = leftChild;
                    curr.rightChild = rightChild;
                    curr.leaves.clear();
                    curr.leaves = null;
                    if (leftChild.leaves.size() > 1)
                        queue.add(leftChild);
                    if (rightChild.leaves.size() > 1)
                        queue.add(rightChild);
                    break algo;
                } else {
                    leftChild = new AABB();
                    rightChild = new AABB();
                    tries++;

                    if (x) {
                        y = true;
                        x = false;
                    } else if (y) {
                        x = false;
                        y = false;
                    } else {
                        x = true;
                        y = false;
                    }
                }
            }

        }
        return root;
    }
}