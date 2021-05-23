import java.util.LinkedList;

/**
 * Container class for the main method. This class also contains the important constants
 * like screen width and height.
 */
public class Main {

	/* Constants for defining the width and height of the display in
	pixels. The common resolutions I use are 1366, 768 and 1920, 1080 
	and 480, 360 */
	public static final int 	WIDTH 			= 1920; 
	public static final int 	HEIGHT 			= 1080;
	public static final double 	ASPECT_RATIO 	= (double)WIDTH / (double)HEIGHT;
	public static final double 	INV_ASPECT_RATIO = (double)HEIGHT / (double)WIDTH;

	/* constants for the 3D space */
	public static final Vector3 I_HAT 			= new Vector3(1.0, 0.0, 0.0);
	public static final Vector3 J_HAT 			= new Vector3(0.0, 1.0, 0.0);
	public static final Vector3 K_HAT 			= new Vector3(0.0, 0.0, 1.0);

	/* create a display for outputing pixels */
	public static final Display DISPLAY 		= new Display(WIDTH, HEIGHT);

	/* the veiwing angles of the camera */
	public static final Vector3 CAMERA  		= new Vector3(0, 5.25, 3.0);
	public static final double 	CAMERA_THETA 	= -Math.PI / 2.0;
	public static final double 	CAMERA_PHI   	= Math.PI / 8.0;

	/* Flags for runtime */
	public static boolean MULTITHREADED = false;
	
	/**
	 * Start of execution. Instantiates all the rendering threads.
	 * 
	 * @param args flags for runtime
	 */
	public static void main(String[] args) {

		/* parse the runtime arguments */
		for (String arg : args) {
			parse:
			switch (arg) {
				case "-m":
				case "-M":
				case "Multithread":
				case "multithread":
					MULTITHREADED = true;
					break parse;
				default:
					break parse;
			}
		}

		/* Create a 'scene' to add all the meshes to */
		LinkedList<Mesh> scene = new LinkedList<Mesh>();

		/* Add all the meshes to the scene */
		scene.add(new Mesh("obj/bunny.obj", 1, new Vector3(-2.0, 0, 0)));
		scene.add(new Mesh("obj/teapot.obj", .75, new Vector3(2.0, 0, .5)));

		/**
		 * This loop will compute all of the rays, one for each pixel. It makes each
		 * ray as a sum of two offset rays from the middle of the screen, and then rotates
		 * each ray around the appropriate axis by the camera veiwing angles.
		 * 
		 * It's done this way because simply rotating unit vectors by the viewing angles 
		 * creates a fish eye effect.
		 * 
		 * It should be noted that x and y in screen space corrispond to y and z in 3d
		 * space before the rotations.
		 */
		Vector3[] rays = new Vector3[WIDTH * HEIGHT];
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				Vector3 ray = new Vector3(
					1.0,
					1.0 - 2.0 * x / (double)WIDTH,
					INV_ASPECT_RATIO - 2.0 * INV_ASPECT_RATIO * y / (double)HEIGHT
				);
				ray = Vector3.rotate(ray, J_HAT, CAMERA_PHI);
				ray = Vector3.rotate(ray, K_HAT, CAMERA_THETA);
				rays[x + y * WIDTH] = ray.norm();
			}
		}
		
		/**
		 * Create all the rendering threads to actually output the picture. Each thread 
		 * is created to render a horizontal band of the screen.
		 */
		if (MULTITHREADED) {
			int cores = Runtime.getRuntime().availableProcessors();
			int threadWidth = HEIGHT / cores;

			for (int i = 0; i < cores; i++)
				new RenderThread(i * threadWidth, i * threadWidth + threadWidth, 
				scene, rays).start();
		} else {
			new RenderThread(0, HEIGHT, scene, rays).start();
		} 
		


	}
}