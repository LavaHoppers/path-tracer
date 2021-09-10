/*
 * PathTracer.java
 * 
 * 29 May 2021
 */

/**
 * The main class for the PathTracer.
 * <p>
 * This class is meant for containing the important constants and functions
 * directly responsible for pathtracing.
 * 
 * @version 1.0.0
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class PathTracer {

	public static boolean verbose 	    = false;
	public static boolean save_as_file  = false;
	public static boolean multithreaded = false;
	public static int 	  threadDim	    = 0;
	public static int 	  antialiasing  = 1;
 
	public static FastBufferedImage image   = null;
	public static Display			display = null;
	public static Scene				scene   = new Scene();
 
	public static Vector3 camera  	   = new Vector3(0, 5, 2);
	public static double  camera_theta = -Math.PI * 0.5;
	public static double  camera_phi   = Math.PI * 0.0;
//	public static Matrix  camera_rot   = null;
	
	/**
	 * Returns a normalized camera ray for the pixel x, y
	 * 
	 * @param x the pixel's x position
	 * @param y the pixel's y position
	 * @param i the pixel's sub x location
	 * @param j the pixel's sub y location
	 * @return the camera ray
	 */
	public static Vector3 getCameraRay(int x, int y, int i, int j) {

		double xp = x * antialiasing + i;
		double yp = y * antialiasing + j;

		double inv = (double)image.getHeight() / image.getWidth();

		Vector3 ray = new Vector3(
			1.0,
			1.0 - (2.0 		) * xp / (image.getWidth() * antialiasing),
			inv - (2.0 * inv) * yp / (image.getHeight() * antialiasing)
		);

		ray = Vector3.rotate(ray, Vector3.J_HAT, camera_phi);
		ray = Vector3.rotate(ray, Vector3.K_HAT, camera_theta);

		return ray.norm();
	}

	/**
	 * Parse the runtime arguments and setup the PathTracer
	 * <p>
	 * This function is very strict and will not hesitate to throw you an error
	 * if the supplied arguments are in the wrong format! Handle with care...
	 * 
	 * @param args the runtime arguments
	 * @return true if the runtime arguments are in the correct format, false
	 * 		   otherwise
	 */
	public static boolean parseArgs(String[] args) {

		if (args == null || args.length < 2)
			return false;

		try {

			int width = Integer.parseInt(args[0]);
			int height = Integer.parseInt(args[1]);
			assert 0 < width;
			assert 0 < height;
			image = new FastBufferedImage(width, height);
			image.fillGrayChecker(200, 0xAF, 0xC0);

		} catch (Exception e) {
			System.out.println("Usage: PathTracer <width> <height>");
			return false;
		}	

		for (int i = 0; i < args.length; i++) {

			if (args[i].equals("-v")) {
				verbose = true;
			}

			if (args[i].equals("-m")) {
				multithreaded = true;
				try {
					int d = Integer.parseInt(args[i + 1]);
					threadDim = d;
				} 
				catch (Exception e) {
					System.out.println("Usage: -m <sub_space_dim>");
					return false;
				}

			}
			
			else if (args[i].equals("-a")) {
				try {
					int a = Integer.parseInt(args[i + 1]);
					antialiasing = a;
				} 
				catch (Exception e) {
					System.out.println("Usage: -a <antialiasing_count>");
					return false;
				}
			}

			else if (args[i].equals("-o")) {
				save_as_file = true;
			}

			else if (args[i].equals("-d")) {
				vPrint("Creating Display");
				display = new Display("Path Tracer", image);
				vPrint("Display created");
			}

		}
		
		return true;
	}

	/**
     * Render a single pixel on the image
	 * 
     * @param x the x location of the pixel 
     * @param y the y location of the pixel
     */
    public static void renderPixel(int x, int y) {

        int c = 0;

        for (int j = 0; j < antialiasing; j++) 
            for (int i = 0; i < antialiasing; i++) {

                Vector3 ray = getCameraRay(x, y, i, j);
                Vector3 pt = new Vector3();
                Vector3 norm = new Vector3();

                if (scene.intersect(camera, ray, pt, norm, null))  {
                    double dot = ray.dot(norm) > 0 ? ray.dot(norm) : -ray.dot(norm);
                    c += (int)(dot * 0xFF);
                }
            }
        
        c = c / (antialiasing * antialiasing);
        image.setPixel(x, y, c, c, c);
		display.repaint();
    }

	/**
	 * Create all the render threads to run
	 * 
	 * @return an array of render threads
	 */
	public static RenderThread[] getRenderThreads() {

		int neededThreads = (int)(Math.ceil((double)image.getWidth() / threadDim) * 
								  Math.ceil((double)image.getHeight() / threadDim));

		RenderThread[] threads = new RenderThread[neededThreads];
		
		int i = 0;

		for (int y = 0; y < image.getHeight(); y += threadDim) {
			for (int x = 0; x < image.getWidth(); x += threadDim) {

				int width = image.getWidth() < x + threadDim ? // too large?
							image.getWidth() - x : // okay, resize
							threadDim;
				int height = image.getHeight() < y + threadDim ? 
							 image.getHeight() - y : 
							 threadDim;

				threads[i] = new RenderThread(x, y, width, height);
				i++;
			}
		}

		return threads;
	}

	/**
	 * Print this string if the verbose flag is true
	 * 
	 * @param text the text to be printed
	 */
	public static void vPrint(String text) {
		if (verbose) System.out.println(text);
	}

	/**
	 * Wait 10 milliseconds
	 */
	public static void sleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start of execution. Instantiates all the rendering threads.
	 * 
	 * @param args runtime flags
	 */
	public static void main(String[] args) {

		if(!parseArgs(args)) {
			System.out.println("Could not read supplied arguments");
			System.exit(1);
		}

		scene.meshes.add(OBJReader.read("obj/helicopter.obj"));


		if (multithreaded) {
			RenderThread[] threads = getRenderThreads();
			for (int i = 0; i < threads.length;) 
				if (RenderThread.running() < Runtime.getRuntime().availableProcessors()) 
					threads[i++].start();
		} else {
			new RenderThread(0, 0, image.getWidth(), image.getHeight()).start();
		}	

		while(0 < RenderThread.running()) { sleep(); }

	}

}