import java.util.Random;

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
 * @version 0.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class PathTracer {

	private static boolean isVerboseConsole = false;
	private static boolean isSaveToFile = false;
	private static boolean isMultithreadRender = false;
	public static int multithreadDimension = 0;
	public static int subPixelSamples = 1;
 
	public static FastBufferedImage image = null;
	public static Display display = null;
	public static Scene	scene = new Scene();
 
	public static Vector3 cameraLocation = new Vector3(-5, 1, 0);
	public static double cameraTheta = Math.PI * 0;
	public static double cameraPhi = Math.PI * 0;
	public static Matrix cameraRotationMatrix = null;
	
	/**
	 * Returns a normalized camera ray for a pixel
	 * 
	 * @param x the pixel's x position
	 * @param y the pixel's y position
	 * @param i the pixel's sub x location
	 * @param j the pixel's sub y location
	 * @return the camera ray
	 */
	public static Vector3 getCameraRay(int x, int y, int i, int j) {

		double xp = x * subPixelSamples + i;
		double yp = y * subPixelSamples + j;

		double inv = (double)image.getHeight() / image.getWidth();

		Vector3 ray = new Vector3(
			1.0,
			inv - (2.0 * inv) * yp / (image.getHeight() * subPixelSamples),
			1.0 - (2.0 		) * xp / (image.getWidth() * subPixelSamples)
		);

		// TODO replace with a fast rotation matrix

		return ray.norm();
	}

	/**
	 * Parse the runtime arguments and setup the engine
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
				isVerboseConsole = true;
			}

			if (args[i].equals("-m")) {
				isMultithreadRender = true;
				try {
					int d = Integer.parseInt(args[i + 1]);
					multithreadDimension = d;
				} 
				catch (Exception e) {
					System.out.println("Usage: -m <sub_space_dim>");
					return false;
				}

			}
			
			else if (args[i].equals("-a")) {
				try {
					int a = Integer.parseInt(args[i + 1]);
					subPixelSamples = a;
				} 
				catch (Exception e) {
					System.out.println("Usage: -a <antialiasing_count>");
					return false;
				}
			}

			else if (args[i].equals("-o")) {
				isSaveToFile = true;
			}

			else if (args[i].equals("-d")) {
				vPrint("Creating Display");
				display = new Display("Path Tracer", image);
				vPrint("Display created");
			}

		}
		
		return true;
	}

	final static Vector3 SUN_LIGHT_DIR = new Vector3(1, 1, -1).norm();

	/**
     * Render a single pixel on the image
	 * 
     * @param x the x location of the pixel 
     * @param y the y location of the pixel
     */
    public static void renderPixel(int x, int y) {

        int r = 0;
        int g = 0;
        int b = 0;


        for (int j = 0; j < subPixelSamples; j++) 
            for (int i = 0; i < subPixelSamples; i++) {

                Vector3 ray = getCameraRay(x, y, i, j);
                Vector3 pt = new Vector3();
                Vector3 norm = new Vector3();
				Vector3 rgb = new Vector3();

				//**
				if (scene.intersect(cameraLocation, ray, pt, norm, rgb)) {
					double dot = ray.dot(norm) > 0 ? ray.dot(norm) : -ray.dot(norm);
					r += (int)(dot * rgb.getX());
					g += (int)(dot * rgb.getY());
					b += (int)(dot * rgb.getZ());
				} else {
					r += (int)(rgb.getX());
					g += (int)(rgb.getY());
					b += (int)(rgb.getZ());
				}
				//*/
				

				// TODO THIS SECTION
				/**
				if (scene.intersect(cameraLocation, ray, pt, norm, rgb)) {
					Random rand = new Random();

					double xcon = ((rand.nextDouble() * 2.0) - 1.0) * (0.5);
					double zcon = ((rand.nextDouble() * 2.0) - 1.0) * (0.5);
					Vector3 sunDir = new Vector3(xcon, -1, zcon).norm();

					if (scene.intersect(pt, sunDir, null, null, null)) {

					} else {
						double dot = sunDir.dot(norm) > 0 ? 
						sunDir.dot(norm) : -sunDir.dot(norm);
					r += (int)(dot * rgb.getX());
					g += (int)(dot * rgb.getY());
					b += (int)(dot * rgb.getZ());
						
					}
					

					// SECOND BOUNCE
					
					double angle1 = ((rand.nextDouble() * 2.0) - 1.0) * (3.14159 / 2.0);
					double angle2 = ((rand.nextDouble() * 2.0) - 1.0) * (3.14159 / 2.0);

					Vector3 axis1 = norm.cross(new Vector3(1, 0, 0)); 
					Vector3 axis2 = norm.cross(axis1); 

					Vector3 bounce = Vector3.rotate(norm.copy(), axis1, angle1);
					bounce = Vector3.rotate(bounce, axis2, angle2);

					Vector3 ptB  = new Vector3();
					Vector3 normB  = new Vector3();
					Vector3 rgbB  = new Vector3();

					if (scene.intersect(pt, bounce, ptB, normB, rgbB)) {

						

						if (scene.intersect(ptB, sunDir, null, null, null)) {

						} else {
							double dotB = sunDir.dot(normB) > 0 ? 
							sunDir.dot(normB) : -sunDir.dot(normB);
							
							double contribution_fac = 1.0 / (pt.sub(ptB).mag())
									* (pt.sub(ptB).mag());

							r += contribution_fac * (int)(dotB * rgbB.getX());
							g += contribution_fac * (int)(dotB * rgbB.getY());
							b += contribution_fac * (int)(dotB * rgbB.getZ());
						}

						
					}
					


				} else {
					r += (int)(rgb.getX());
					g += (int)(rgb.getY());
					b += (int)(rgb.getZ());
				}
				// */

				//TODO THIS SECTION
            }
        
		float correction = 1f / (subPixelSamples * subPixelSamples);
        r = (int)(r * correction);
        g = (int)(g * correction);
        b = (int)(b * correction);
        image.setPixel(x, y, r, g, b);
		if (display != null)
			display.repaint();
    }

	/**
	 * Create all the render threads to run
	 * 
	 * @return an array of render threads
	 */
	public static RenderThread[] getRenderThreads() {

		int neededThreads = (int)(Math.ceil((double)image.getWidth() / multithreadDimension) * 
								  Math.ceil((double)image.getHeight() / multithreadDimension));

		RenderThread[] threads = new RenderThread[neededThreads];
		
		int i = 0;

		for (int y = 0; y < image.getHeight(); y += multithreadDimension) {
			for (int x = 0; x < image.getWidth(); x += multithreadDimension) {

				int width = image.getWidth() < x + multithreadDimension ? // too large?
							image.getWidth() - x : // okay, resize
							multithreadDimension;
				int height = image.getHeight() < y + multithreadDimension ? 
							 image.getHeight() - y : 
							 multithreadDimension;

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
		if (isVerboseConsole) System.out.println(text);
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

		scene.meshes.add(OBJReader.read("obj/bunny.obj"));
		scene.meshes.add(OBJReader.read("obj/plane.obj"));


		if (isMultithreadRender) {
			RenderThread[] threads = getRenderThreads();
			for (int i = 0; i < threads.length;) 
				if (RenderThread.running() < Runtime.getRuntime().availableProcessors()) 
					threads[i++].start();
		} else {
			new RenderThread(0, 0, image.getWidth(), image.getHeight()).start();
		}	

		while(0 < RenderThread.running()) { sleep(); }

		if (isSaveToFile)
			image.savePNG("./img", "" + System.currentTimeMillis() + ".png");
		System.out.print("Render Complete");
	}

}