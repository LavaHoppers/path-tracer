import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class for representing 3D objects as a mesh
 * <p>
 * TODO write discription
 * @author Joshua Hopwood
 * @see <a href=https://github.com/lavahoppers>Github</a>
 */
class Mesh {

    public AABB root;

    /**
     * Creates a new mesh from an OBJ file
     * @param path the path to the OBJ file
     * @param scale a scalar to scale the model's size by
     * @param pos the position of the object
     */
    Mesh(String path, double scale, Vector3 pos) {

        /* read the contents of the file into these two array lists */
        ArrayList<Vector3> verticies = new ArrayList<Vector3>();
        ArrayList<Integer> triVerticies = new ArrayList<Integer>();

        File file = null;
        Scanner scanner = null;

        /* attempt to read the file */
        try {
            file = new File(path);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + path);
            e.printStackTrace();
        }

        /* get the data from the file into the two array lists */
        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            if (line.length() == 0)
                continue;

            if (line.charAt(0) == 'v') {
                String[] split = line.split(" ");
                verticies.add(new Vector3(
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[3]),
                    Double.parseDouble(split[2])
                ));
            }

            if (line.charAt(0) == 'f') {
                String[] split = line.split(" ");
                triVerticies.add(Integer.parseInt(split[1].split("//")[0]) - 1);
                triVerticies.add(Integer.parseInt(split[2].split("//")[0]) - 1);
                triVerticies.add(Integer.parseInt(split[3].split("//")[0]) - 1);
            }

        }
        scanner.close();

        /* find the center of the mesh */
        Vector3 center = new Vector3();
        for (Vector3 vertex : verticies)
            center = Vector3.add(center, vertex);
        center = Vector3.scale(center, 1.0 / (double)verticies.size());

        /* find the positional offset to put it at the origin */
        Vector3 offset = Vector3.scale(center, -1.0);

        /* move each vertex by the offset */
        for (int i = 0; i < verticies.size(); i++)
            verticies.set(i, Vector3.add(verticies.get(i), offset));

        /* scale the model to size */
        for (int i = 0; i < verticies.size(); i++)
            verticies.set(i, Vector3.scale(verticies.get(i), scale));
        
        /* rotate everything */

        /* move each vertex by the position */
        for (int i = 0; i < verticies.size(); i++)
            verticies.set(i, Vector3.add(verticies.get(i), pos));

        /* create the triangle array */
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();
        for (int i = 0; i < triVerticies.size(); i += 3) {
            triangles.add(new Triangle(
                verticies.get(triVerticies.get(i)),
                verticies.get(triVerticies.get(i + 1)),
                verticies.get(triVerticies.get(i + 2))
            ));
        }

        /* create a bounding volume hierarchy */
        buildBVH(triangles);

    } 

    
    /**
     * Constructor for a new Bounding Volume Hierarchy. Takes the triangles 
     * from a mesh and subdivides them into Axis aligned bounding boxes for 
     * efficient searching.
     * 
     * @param tris the arraylist of triangles to subdivide
     */
    private void buildBVH(ArrayList<Triangle> tris) {

        root = new AABB();
        for (Triangle tri : tris)
            root.add(tri);

        LinkedList<AABB> queue = new LinkedList<AABB>();
        queue.add(root);

        while(queue.size() > 0) {

            AABB curr = queue.pop();

            double xLength = curr.max.x - curr.min.x;
            double yLength = curr.max.y - curr.min.y;
            double zLength = curr.max.z - curr.min.z;
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
                        double midPoint = (tri.a.x + tri.b.x + tri.c.x) / 3.0;
                        if ((midPoint - curr.min.x) < (curr.max.x - midPoint))
                            rightChild.add(tri);
                        else
                            leftChild.add(tri);
                    }
                else if (y)
                    for (Triangle tri : curr.leaves) {
                        double midPoint = (tri.a.y + tri.b.y + tri.c.y) / 3.0;
                        if ((midPoint - curr.min.y) < (curr.max.y - midPoint))
                            rightChild.add(tri);
                        else
                            leftChild.add(tri);
                    }
                else
                    for (Triangle tri : curr.leaves) {
                        double midPoint = (tri.a.z + tri.b.z + tri.c.z) / 3.0;
                        if ((midPoint - curr.min.z) < (curr.max.z - midPoint))
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
    }
}
