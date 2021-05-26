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
	public static final Vector3 CAMERA  		= new Vector3(-2.5, 3.7525, 3.000);
	public static final double 	CAMERA_THETA 	= -Math.PI / 3.0;
	public static final double 	CAMERA_PHI   	= Math.PI / 8.0;

	public static final int 	THREAD_WIDTH 	= 100;
	public static final int 	THREAD_HEIGHT 	= 100;

	/* Flags for runtime */
	public static boolean 		MULTITHREADED 	= false;
	public static boolean		ANTIALIASED		= false;
	public static int 			ANTIALIASING	= 8;
	
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
				case "-a":
					ANTIALIASED = true;
					ANTIALIASING = 2;
					break;
				default:
					break parse;
			}
		}

		long start = System.currentTimeMillis();

		/* Create a 'scene' to add all the meshes to */
		LinkedList<Mesh> scene = new LinkedList<Mesh>();

		/* Add all the meshes to the scene */
		for (Mesh mesh : OBJReader.read("obj/bunny.obj")) {
			mesh.buildBVH();
			scene.add(mesh);
		}

		System.out.println("Mesh building time: " + (System.currentTimeMillis() - start));


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

		start = System.currentTimeMillis();

		Vector3[] rays;
		if (ANTIALIASED) {
			rays = new Vector3[WIDTH * HEIGHT * ANTIALIASING * ANTIALIASING];

			for (int y = 0; y < HEIGHT * ANTIALIASING; y++) {
				for (int x = 0; x < WIDTH * ANTIALIASING; x++) {
					Vector3 ray = new Vector3(
						1.0,
						1.0 - 2.0 * x / ((double)WIDTH * ANTIALIASING),
						INV_ASPECT_RATIO - 2.0 * INV_ASPECT_RATIO * y / ((double)HEIGHT * ANTIALIASING)
					);
					ray = Vector3.rotate(ray, J_HAT, CAMERA_PHI);
					ray = Vector3.rotate(ray, K_HAT, CAMERA_THETA);
					rays[x + y * WIDTH * ANTIALIASING] = ray.norm();
				}
			}
		}
		else {
			rays = new Vector3[WIDTH * HEIGHT];
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
		}

		System.out.println("Ray creation time: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		
		/**
		 * Create all the rendering threads to actually output the picture. Each thread 
		 * is created to render a horizontal band of the screen.
		 */
		if (MULTITHREADED) {

			int nativeThreads = Thread.activeCount();
			int cores = Runtime.getRuntime().availableProcessors();
			int createdThreads = 0;
			int neededThreads = (int)Math.ceil((double)WIDTH/ THREAD_WIDTH) * (int)Math.ceil((double)HEIGHT / THREAD_HEIGHT);
			int x = 0;
			int y = 0;
			
			while (createdThreads < neededThreads) {

				for (int i = Thread.activeCount(); i < cores + nativeThreads; i++) {

					new RenderThread(x, y, scene, rays).start();

					x += THREAD_WIDTH;

					if (x > WIDTH) {
						x = 0;
						y += THREAD_HEIGHT;
					}

					createdThreads++;
				}

			}
			
		} else {
			new RenderThread(0, 0, scene, rays).start();
		} 

		System.out.println("Render time: " + (System.currentTimeMillis() - start));
	}
}