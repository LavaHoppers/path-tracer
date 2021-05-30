import java.lang.Thread;

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

    public static int THREAD_WIDTH = 0;
	public static int THREAD_HEIGHT = 0;
	public static int RUNNING_THREADS = 0;
	public static int DEAD_THREADS = 0;

    private int x;
    private int y;

    /**
     * Create a new rendering thread to render the portion of the screen from the starting y
     * value to the ending y value. 
     * 
     * @param x the starting x value of the thread
     * @param y the ending y value

     */
    RenderThread(int x, int y) {
        super(); // let this thread name itself
        this.x = x;
        this.y = y;
    }

    /**
     * Rendered a horizontal stripe of the screen
     * <p>
     * Instead of calling this method directly, call the start function for this 
     * rendering thread to run this method on an available cpu core.
     */
    @Override
    public void run() {

        int finaly = y + THREAD_HEIGHT > Main.HEIGHT ? Main.HEIGHT : y + THREAD_HEIGHT;
        int finalx = x + THREAD_WIDTH > Main.WIDTH ? Main.WIDTH : x + THREAD_WIDTH;

        for (int y = this.y; y < finaly; y++) 
			for (int x = this.x; x < finalx; x++) 
                renderPixel(x, y);
        Main.DISPLAY.repaint();
        DEAD_THREADS++;
        
    } 

    /**
     * Renders a single pixel on the screen
     * <p>
     * This method only updates the buffer so repaint still needs to be called
     * to actually see the rendered pixel
     * @param x         the x location of the pixel 
     * @param y         the y location of the pixel
     */
    private void renderPixel(int x, int y) {

        int c = 0;

        for (int i = 0; i < Main.ANTIALIASING; i++) {
            for (int j = 0; j < Main.ANTIALIASING; j++) {

                Vector3 ray = Main.RAYS[
                    (x * Main.ANTIALIASING + j) + 
                    (y * Main.ANTIALIASING + i) * Main.WIDTH * Main.ANTIALIASING
                ];
        
                Vector3 pt = new Vector3();
                Vector3 norm = new Vector3();

                if (Main.SCENE.intersect(Main.CAMERA, ray, pt, norm, null))  {
                    double dot = ray.dot(norm) > 0 ? ray.dot(norm) : -ray.dot(norm);
                    c += (int)(dot * 0xFF);
                }
            }
        }

        c /= Main.ANTIALIASING * Main.ANTIALIASING;
        Main.DISPLAY.set(x, y, c, c, c);
    }
    
}
