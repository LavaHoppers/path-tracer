import java.lang.Thread;
import java.util.LinkedList;
/**
 * An instantiable class designed to render a certain portion of pixels on the screen.
 * The RenderThread should be created, passed the pixels to render, and the necessary info
 * for rendering, and then should have it's .start() method called to perform the rendering.
 * 
 * This class extends the Thread class from java lang so when .start() is called it will run
 * this rendering thread on a new CPU core.
 * 
 * The renderThread will render the portion of the screen from the starting y value to the
 * ending y value specified in the constructor
 * @author Joshua Hopwood
 * @see <a href=https://github.com/lavahoppers>GitHub</a>
 */
public class RenderThread extends Thread {

    private int starty;
    private int endy;

    private LinkedList<Mesh> scene;
    private Vector3[] rays;

    /**
     * Create a new rendering thread to render the portion of the screen from the starting y
     * value to the ending y value. 
     * 
     * @param starty the starting y value of the threa
     * @param endy the ending y value
     * @param scene the mesh to render
     * @param rays the array of all the initial veiwing rays from the camera
     */
    RenderThread(int starty, int endy, LinkedList<Mesh> scene, Vector3[] rays) {
        super(); // let this thread name itself
        this.starty = starty;
        this.endy = endy;
        this.scene = scene;
        this.rays = rays;
    }

    /**
     * Rendered a horizontal stripe of the screen
     * <p>
     * Instead of calling this method directly, call the start function for this 
     * rendering thread to run this method on an available cpu core.
     */
    @Override
    public void run() {

        LinkedList<AABB>     boxQueue = new LinkedList<AABB>();

        for (int y = this.starty; y < this.endy; y++) {
			for (int x = 0; x < Main.WIDTH; x++) 
                renderPixel(x, y, boxQueue);
            Main.DISPLAY.repaint();
        }
    } 

    /**
     * Renders a single pixel on the screen
     * <p>
     * This method only updates the buffer so repaint still needs to be called
     * to actually see the rendered pixel
     * @param x         the x location of the pixel 
     * @param y         the y location of the pixel
     * @param boxQueue  the queue for holding the bounding boxes
     * @param triQueue  the queue for holding the triangles
     */
    private void renderPixel(int x, int y, LinkedList<AABB> boxQueue) {

        Vector3  ray = rays[x + y * Main.WIDTH];
        Vector3  rayInv = new Vector3(1.0 / ray.x, 1.0 / ray.y, 1.0 / ray.z);
        int      r = 0;
        int      g = 0;
        int      b = 0;

        Vector3  closestPt = null;
        Triangle closestTri = null;
        double   closestDist = 0;

        for (Mesh mesh : scene) {

            boxQueue.add(mesh.root);

            boxes:
            while (boxQueue.size() > 0) {

                AABB   curr = boxQueue.pop();
                double interDist = curr.intersects(Main.CAMERA, ray, rayInv);

                if (interDist == -1)
                    continue boxes;

                if (closestPt == null || interDist < closestDist) {
                    if (curr.leftChild != null)
                        boxQueue.add(curr.leftChild);
                    if (curr.rightChild != null)
                        boxQueue.add(curr.rightChild);
                }

                if (curr.leaves == null)
                    continue boxes;

                tris:
                for (Triangle tri : curr.leaves) {

                    Vector3 pt = tri.intersects(Main.CAMERA, ray);

                    if (pt == null)
                        continue tris;

                    double newDist = Vector3.sub(Main.CAMERA, pt).mag();
                    if (closestPt == null || newDist < closestDist) {
                        closestPt = pt;
                        closestTri = tri;
                        closestDist = newDist;
                    }  
                    
                }
                
            } /* END bounding queue */
            
    
            /* Not so great shading */
            if (closestPt != null) {
                Vector3 edge1 = Vector3.sub(closestTri.b, closestTri.a);
                Vector3 edge2 = Vector3.sub(closestTri.c, closestTri.a);
                Vector3 norm = Vector3.cross(edge1, edge2).norm();
                double dot = Vector3.dot(norm, ray);
                dot = Math.abs(dot);
                r = g = b = (int)(dot * 0xFF);
            }

        } 

        Main.DISPLAY.set(x, y, r, g, b);
    }
    
}
