import java.util.ArrayList;
import java.util.LinkedList;

public class Scene {
    
    public ArrayList<Mesh> meshes;
    public ArrayList<Light> lights;

    Scene() {
        meshes = new ArrayList<Mesh>();
        lights = new ArrayList<Light>();
    }

    public boolean intersect(Vector3 origin, Vector3 ray, Vector3 ptOut, Vector3 normOut, Vector3 rgbOut) {

        Triangle closeTri = null;
        double   closeDist = 0;

        LinkedList<AABB> AABBQueue = new LinkedList<AABB>();
        Vector3 rayInv = ray.reciprocal();

        for (Mesh mesh : meshes) {

            AABBQueue.add(mesh.root);

            while (0 < AABBQueue.size()) {

                AABB current = AABBQueue.pop();

                double distance = current.intersects(origin, ray, rayInv);

                if (distance == -1)
                    continue;

                if (closeTri == null || distance < closeDist) {
                    if (current.leftChild != null)
                        AABBQueue.add(current.leftChild);
                    if (current.rightChild != null)
                        AABBQueue.add(current.rightChild);
                }

                if (current.leaves == null)
                    continue;

                for (Triangle tri : current.leaves) {

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

        if (closeTri == null)
            return false;
        
        ptOut.set(origin.copy().setScaleAdd(ray, closeDist));
        normOut.set(closeTri.norm());
        return true;

    }

}
