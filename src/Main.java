/**
 * Container class for the main method
 */
public class Main {

	/* Constants for defining the width and height of the display in
	pixels */
	public static final int WIDTH = 1920; // 1366, 768
	public static final int HEIGHT = 1080;
	public static final double ASPECT_RATIO = (double)WIDTH / (double)HEIGHT;

	/* Constants for defining the fov. Should be a double in radians */
	public static final double HORIZONTAL_FOV = Math.PI / 2f;
	public static final double VERTICAL_FOV = HORIZONTAL_FOV / ASPECT_RATIO;

	/**
	 * Start of execution
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {

		/* create a camera and a display for outputing pixels and knowing where
		the rays will originate from */
		Display display = new Display(WIDTH, HEIGHT); 
		Vector3 camera  = new Vector3(0.0, 4.0, 0.0);

		/* the veiwing angles of the camera */
		double cameraTheta =  -Math.PI / 2.0;
		double cameraPhi   =  0 * Math.PI / 2.0;

		/* create a mesh to render */
		Mesh teapot = new Mesh("bunny.obj");

		/**
		 * This loop will compute all of the rays, one for each pixel. It makes each
		 * ray as a sum of two offset rays from the middle of the screen, and then rotates
		 * each ray around the appropriate axis by the camera veiwing angles.
		 * 
		 * It's done this way because simply rotating unit vectors by the viewing angles 
		 * creates a fish eye effect.
		 * 
		 * It should be noted that x and y in screen space corrispond to x and z in 3d
		 * space before the rotations.
		 */

		/* create an array to hold all the pre-computed rays for each pixel */
		Vector3[] rays = new Vector3[WIDTH * HEIGHT];

		Vector3 topLeft = new Vector3(1.0, 1.0, ASPECT_RATIO / 2.0);
		Vector3 xoffset = new Vector3(0.0, -2.0 / (double)WIDTH, 0.0);
		Vector3 yoffset = new Vector3(0.0, 0.0, -2.0 / ASPECT_RATIO / (double)HEIGHT);

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				Vector3 ray = topLeft;
				ray = Vector3.scaleAdd(ray, xoffset, x);
				ray = Vector3.scaleAdd(ray, yoffset, y);
				ray = ray.norm();
				ray = Vector3.rotate(ray, new Vector3(0.0, 1.0, 0.0), cameraPhi);
				ray = Vector3.rotate(ray, new Vector3(0.0, 0.0, 1.0), cameraTheta);
				rays[x + y * WIDTH] = ray;
			}
		}
		
		/**
		 * Create all the rendering threads to actually output the picture. Each thread 
		 * is created to render a horizontal band of the screen.
		 */
		int cores = Runtime.getRuntime().availableProcessors();
		int threadWidth = HEIGHT / cores;

		for (int i = 0; i < cores; i++)
			new RenderThread(i * threadWidth, i * threadWidth + threadWidth, 
			teapot, display, camera, rays).start();
	}
}