/*
 * Main.java
 * 
 * 29 May 2021
 */

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Container class for the main method.
 * 
 * @version 1.0.0
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class Main {

	/* Constants for defining the width and height of the display in
	pixels. The common resolutions I use are 1366, 768 and 1920, 1080 
	and 480, 360 */
	public static int 			WIDTH 			 = 480; 
	public static int 			HEIGHT 			 = 360;
	public static double 		ASPECT_RATIO 	 = 0;
	public static double 		INV_ASPECT_RATIO = 0;
	/* create a display for outputing pixels */
	public static Display 		DISPLAY 		= null;
	/* the veiwing angles of the camera */
	public static final Vector3 CAMERA  		= new Vector3(0, 50, 30);
	public static final double 	CAMERA_THETA 	= -Math.PI / 2.0; //+ Math.PI;
	public static final double 	CAMERA_PHI   	= Math.PI / 2.0 / 2.0;
	/* Flags for runtime */
	public static boolean 		MULTITHREADED 	= false;
	public static int 			ANTIALIASING	= 1;
	/* The scene */
	public static Scene			SCENE   		= new Scene();
	public static Vector3[]		RAYS  			= null;
	public static boolean 		SAVE_AS_FILE	= false;

	public static FastBufferedImage image 	= null;
	public static Display			display = null;

	
	
	/**
	 * Start of execution. Instantiates all the rendering threads.
	 * 
	 * @param args runtime flags
	 */
	public static void main(String[] args) {

		image = new FastBufferedImage(480, 360);
		image.fillGrayChecker(80, 0xF0, 0xA0);

		//display = new Display(image);

		/* parse the runtime arguments */
		for (String arg : args) {
			switch (arg) {

				case "-m":
					System.out.println("Multithreaded");
					MULTITHREADED = true;
					RenderThread.THREAD_WIDTH = 100;
					RenderThread.THREAD_HEIGHT = 100;
					break;

				case "-a2":
					System.out.println("2X antialiasing");
					ANTIALIASING = 2;
					break;

				case "-a4":
					System.out.println("4X antialiasing");
					ANTIALIASING = 4;
					break;

				case "-a8":
					System.out.println("8X antialiasing");
					ANTIALIASING = 8;
					break;
				
				case "-o":
					System.out.println("File output");
					SAVE_AS_FILE = true;
					break;

				case "-d":
					System.out.println("Realtime Display");
					break;

				case "-hd":
					System.out.println("1080p");
					WIDTH = 1920;
					HEIGHT = 1080;
					break;
				
				case "-4k":
					System.out.println("3840p");
					WIDTH = 3840;
					HEIGHT = 2160;
					break;
					
				default:
					break;
			}
		}

		if (!MULTITHREADED) {
			RenderThread.THREAD_WIDTH = WIDTH;
			RenderThread.THREAD_HEIGHT = HEIGHT;
		}

		ASPECT_RATIO = (double)WIDTH / (double)HEIGHT;
		INV_ASPECT_RATIO = 1.0 / ASPECT_RATIO;

		DISPLAY = new Display(WIDTH, HEIGHT);

		long start = System.currentTimeMillis();

		/* Add all the meshes to the scene */
		for (Mesh mesh : OBJReader.read("obj/room.obj")) {
			mesh.buildBVH();
			SCENE.meshes.add(mesh);
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
		
		RAYS = new Vector3[WIDTH * ANTIALIASING * HEIGHT * ANTIALIASING];
		for (int y = 0; y < HEIGHT * ANTIALIASING; y++) {
			for (int x = 0; x < WIDTH * ANTIALIASING; x++) {
				Vector3 ray = new Vector3(
					1.0,
					1.0 - 2.0 * x / ((double)WIDTH * ANTIALIASING),
					INV_ASPECT_RATIO - 2.0 * INV_ASPECT_RATIO * y / ((double)HEIGHT * ANTIALIASING)
				);
				ray = Vector3.rotate(ray, Vector3.J_HAT, CAMERA_PHI);
				ray = Vector3.rotate(ray, Vector3.K_HAT, CAMERA_THETA);
				RAYS[x + y * WIDTH * ANTIALIASING] = ray.norm();
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
			int neededThreads = (int)Math.ceil((double)WIDTH/ RenderThread.THREAD_WIDTH) * (int)Math.ceil((double)HEIGHT / RenderThread.THREAD_HEIGHT);
			int x = 0;
			int y = 0;
			
			while (createdThreads < neededThreads) {

				for (int i = Thread.activeCount(); i < cores + nativeThreads; i++) {

					new RenderThread(x, y).start();

					x += RenderThread.THREAD_WIDTH;

					if (x > WIDTH) {
						x = 0;
						y += RenderThread.THREAD_HEIGHT;
					}

					createdThreads++;
				}

			}

			while (RenderThread.DEAD_THREADS < neededThreads) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
			
		} else {
			new RenderThread(0, 0).start();
		} 

		
		System.out.println("Render time: " + (System.currentTimeMillis() - start));

		if (SAVE_AS_FILE) {
			File outputfile = new File("img/" + System.currentTimeMillis() + ".png");
			try {
				ImageIO.write(DISPLAY.image, "png", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}