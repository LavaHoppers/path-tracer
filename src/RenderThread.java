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
 */
public class RenderThread extends Thread {

    private int starty;
    private int endy;

    private Mesh mesh;
    private Display display;

    private Vector3 camera;
    private Vector3[] rays;

    /**
     * Create a new rendering thread to render the portion of the screen from the starting y
     * value to the ending y value. 
     * 
     * @param starty the starting y value of the threa
     * @param endy the ending y value
     * @param mesh the mesh to render
     * @param display the display to output to
     * @param camera the camera position
     * @param rays the array of all the initial veiwing rays from the camera
     */
    RenderThread(int starty, int endy, Mesh mesh, Display display, Vector3 camera, Vector3[] rays) {

        /* call the super constructor */
        super();

        /* save the starting and ending values */
        this.starty = starty;
        this.endy = endy;

        /* save the mesh to render over */
        this.mesh = mesh;

        /* save the display to output to */
        this.display = display;

        /* the position of the camera */
        this.camera = camera;
        this.rays = rays;

    }

    @Override
    public void run() {

        LinkedList<AABB> queue = new LinkedList<AABB>();
        

        for (int y = this.starty; y < endy; y++) {
			for (int x = 0; x < Main.WIDTH; x++) {

                Vector3 closest = null;
                Triangle closestTri = null;

				// ARGB values that will be sent to the display.set method
				int a = 0xFF; // 	(165,42,42)
				int r = 0;
				int g = 0;
				int b = 0;

			    Vector3 ray = rays[x + y * Main.WIDTH];

                /* for (Triangle tri : mesh.triangles) {
                    Vector3 pt = tri.intersects(camera, ray);
                    if (pt != null) 
                        r = g = b = (int)Math.min(255.0, Vector3.sub(camera, pt).mag() * 40);
                } */

                queue.add(mesh.bvh.root);

                while (!queue.isEmpty()) {

                    AABB current = queue.pop();
                    if (current == null)
                        continue;

                    if (current.intersects(camera, ray)){
                        queue.add(current.leftChild);
                        queue.add(current.rightChild);
                    }

                    if (current.leaves != null) {
                        for (Triangle tri : current.leaves) {
                            Vector3 pt = tri.intersects(camera, ray);
                            if (pt != null) {
                                if (closest == null) {
                                    closest = pt;
                                    closestTri = tri;
                                }
                                else {
                                    double closestDist = Vector3.sub(camera, closest).mag();
                                    double newDist = Vector3.sub(camera, pt).mag();
                                    if (newDist < closestDist) {
                                        closest = pt;
                                        closestTri = tri;
                                    }
                                }   
                            }

                        }
                    }

                }

                if (closest != null && closestTri != null) {
                    Vector3 edge1 = Vector3.sub(closestTri.a, closestTri.b);
                    Vector3 edge2 = Vector3.sub(closestTri.a, closestTri.c);
                    Vector3 norm = Vector3.cross(edge1, edge2).norm();
                    r = (int)(Vector3.dot(norm, ray) * 0xB5); //B57E3A
                    g = (int)(Vector3.dot(norm, ray) * 0x7E);
                    b = (int)(Vector3.dot(norm, ray) * 0x3A);
                }
                
                
				display.set(x, y, a, r, g, b);
			}
            display.repaint();
        }
        
    }
    
}
