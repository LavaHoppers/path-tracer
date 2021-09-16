package net.lavahoppers;

/*
 * AABB.java
 * 
 * 29 May 20201
 */

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * An axis aligned bounding box is a primative used to divide space.
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class AABB {

    public Vector3 max = null;
    public Vector3 min = null;

    private AABB leftChild = null;
    private AABB rightChild = null;
    
    private ArrayList<Triangle> leaves = new ArrayList<>();

    /**
     * Create a new Axis aligned bounding box.
     */
    public AABB() { }

    /**
     * Set the status of the caller as a leaf node or not
     * @param bool true if the caller should contain leaves, false otherwise
     */
    public void setIsLeafNode(boolean bool) {
        if (bool && isLeafNode())
            return;
        else if (bool && !isLeafNode())
            leaves = new ArrayList<>();
        else
            leaves = null;
    }

    /**
     * See if the caller contains primatives
     * @return true if the caller contains primatives, else false
     */
    public boolean isLeafNode() {
        return leaves != null;
    }

    /**
     * Get the number of leaves contained in the caller
     * @throws NullPointerException if this is not a leaf node
     * @return the number of leaves
     */
    public int leafCount() throws NullPointerException {
        return leaves.size();
    }

    /**
     * returns the right child of the bounding box 
     * @return the right child of the bounding box
     */
    public AABB getRightChild() {
        return rightChild;
    }

    /**
     * returns the left child of the bounding box 
     * @return the left child of the bounding box
     */
    public AABB getLeftChild() {
        return leftChild;
    }

    /**
     * returns the primatives of the bounding box 
     * <p>
     * if there are no primatives atthis level this will return null
     * @return the primatives of the bounding box
     */
    public ArrayList<Triangle> getLeaves() {
        return leaves;
    }
    
    /**
     * Get the distance to the intersection of a bounding box
     * <p>
     * How this method works is entirely a mystery to me!
     * 
     * @see <a href =https://tinyurl.com/s3ypwcpc>Original Author</a>
     * @param origin the origin of the ray
     * @param ray    the unit vector of the ray's direction
     * @param rayInv the reciprocal of the ray elemement wise
     * @return the magnitude of the vector from origin to intersection, -1 if no
     *         intersection
     */
    public double intersects(Vector3 origin, Vector3 ray, Vector3 rayInv) {

        double t1, t2, t3, t4, t5, t6, tmin, tmax;

        t1 = (min.getX() - origin.getX()) * rayInv.getX();
        t2 = (max.getX() - origin.getX()) * rayInv.getX();
        t3 = (min.getY() - origin.getY()) * rayInv.getY();
        t4 = (max.getY() - origin.getY()) * rayInv.getY();
        t5 = (min.getZ() - origin.getZ()) * rayInv.getZ();
        t6 = (max.getZ() - origin.getZ()) * rayInv.getZ();

        tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), 
                Math.min(t5, t6));
        tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), 
                Math.max(t5, t6));

        if ((tmax < 0) || (tmin > tmax))
            return -1;

        return tmin;
    }

    /**
     * Add a triangle to this AABB and expand the box to accomodate for it
     * 
     * @param tri the triangle to be added
     */
    public void add(Triangle tri) {

        if (min == null)
            min = tri.a.copy();
        if (max == null)
            max = tri.a.copy();

        leaves.add(tri);
        min.setElemMin(tri.a, tri.b, tri.c);
        max.setElemMax(tri.a, tri.b, tri.c);

    }

    /**
     * Add an array of triangles to this AABB and expand the box to
     * encompass them
     * 
     * @param tris the triangles to be added
     */
    public void add(ArrayList<Triangle> tris) {
        for (Triangle tri : tris)
            add(tri);
    }

    /**
     * Create a bounding volume hierarchy from all the leaves contained in
     * the caller
     */
    public void buildBVH() {

        LinkedList<AABB> queue = new LinkedList<AABB>();
        queue.add(this);

        while(0 < queue.size()) {

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

                if (leftChild.leafCount() > 0 && rightChild.leafCount() > 0) {
                    curr.leftChild = leftChild;
                    curr.rightChild = rightChild;
                    curr.setIsLeafNode(false);
                    if (leftChild.leafCount() > 1)
                        queue.add(leftChild);
                    if (rightChild.leafCount() > 1)
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

    }

}