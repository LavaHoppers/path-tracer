package net.lavahoppers;
import java.io.FileReader;
import java.util.Random;

import javax.swing.event.InternalFrameListener;

import org.json.simple.*;
import org.json.simple.parser.*;

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
	public static int multithreadDimension = 48;
	public static int subPixelSamples = 1;

	public static boolean isGlobalIllumination = false;
	public static int globalIllumBounces = 1;
	public static int globalIllumScatters = 1;
	public static double globalIllumScalar = 1;

	public static String outputFileLocation = "./";
 
	public static FastBufferedImage image = null;
	public static Display display = null;
	public static Scene	scene = new Scene();
	public static final Random RANDOM = new Random();

	public static Vector3 cameraLocation = null;
	public static double cameraTheta = 0;
	public static double cameraPhi = 0;
	public static Matrix cameraPhiMatrix = null;
	public static Matrix cameraThetaMatrix = null;
			
	
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

		if (cameraPhi != 0)
			ray.setMatrixRotation(cameraPhiMatrix);
		if (cameraTheta != 0)
			ray.setMatrixRotation(cameraThetaMatrix);
		

		return ray.norm();
	}

	/**
	 * Imports the variables from settings.json
	 * 
	 * @see https://www.tutorialspoint.com/how-can-we-read-a-json-file-in-java
	 * @return true if the import is successful, false otherwise
	 */
	public static boolean parseSettings() {

		JSONParser parser = new JSONParser();
		try {

			JSONObject json = (JSONObject)parser.parse(
				new FileReader("settings.json"));

			JSONArray resolution = (JSONArray)json.get("render-resolution");
			image = new FastBufferedImage((int)(long)resolution.get(0),
				(int)(long)resolution.get(1));
			image.fillGrayChecker(50, 0xAF, 0xC0);

			isVerboseConsole = (boolean)json.get("verbose-console");
		 	isSaveToFile = (boolean)json.get("save-render-to-png");
			isMultithreadRender = (boolean)json.get("multithreaded-render");
			if ((boolean)json.get("antialiasing"))
				subPixelSamples = (int)(long)json.get("antialiasing-multiplier");
			else
				subPixelSamples = 1;
			if ((boolean)json.get("realtime-display"))
				display = new Display("Path Tracer", image);

			outputFileLocation = (String)json.get("png-output-location");

			JSONArray position = (JSONArray)json.get("camera-position");
			cameraLocation = new Vector3((double)position.get(0), 
				(double)position.get(1), 
				(double)position.get(2));
			cameraPhi = (double)json.get("camera-pitch");
			cameraTheta = (double)json.get("camera-yaw");
			cameraPhiMatrix = Matrix.getZRotationMatrix(cameraPhi);
			cameraThetaMatrix = Matrix.getYRotationMatrix(cameraTheta);

			isGlobalIllumination = (boolean)json.get("global-illumination");
			globalIllumBounces = (int)(long)json.get("global-illumination-bounces");
			globalIllumScatters = (int)(long)json.get("global-illumination-scatters");
			globalIllumScalar = (double)json.get("inverse-square-law-constant");

		} catch(Exception e) {
			
			System.out.println("Couldn't read supplied settings.json");
			e.printStackTrace();
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

        Vector3 pixelRGB = new Vector3();
		

        for (int j = 0; j < subPixelSamples; j++) 
            for (int i = 0; i < subPixelSamples; i++) {

                Vector3 cameraRay = getCameraRay(x, y, i, j);
                Vector3 intersectPoint = new Vector3();
                Vector3 surfaceNormal = new Vector3();
				Vector3 returnedRGB = new Vector3();

				if (scene.intersect(cameraLocation, cameraRay, intersectPoint, 
						surfaceNormal, returnedRGB)) {
				
					Vector3 workingSunRay = getSunlightRay();
					Vector3 ambientLight = Scene.getDirectionalLight(workingSunRay);

					// direct illumination
					if (!scene.intersect(intersectPoint, 
							workingSunRay.scale(-1), null, null, null)) {

						double lambert = surfaceNormal.dot(workingSunRay);
						lambert = lambert < 0 ? -lambert : lambert;



						//ambientLight = new Vector3(255, 255, 255);
						pixelRGB.setAdd(new Vector3(
							ambientLight.getX(), 
							ambientLight.getY(), 
							ambientLight.getZ())
							.scale(lambert));
					}

					// global illumination
					if (isGlobalIllumination)
						pixelRGB.setAdd(recursiveIllumination(0, intersectPoint, 
						surfaceNormal));

				} else {
					pixelRGB.setAdd(returnedRGB);
				}
				

				
            }

        pixelRGB.setScale(1.0 / (subPixelSamples * subPixelSamples));
        image.setPixel(x, y, 
			(int)(pixelRGB.getX() > 255 ? 255 : pixelRGB.getX()), 
			(int)(pixelRGB.getY() > 255 ? 255 : pixelRGB.getY()), 
			(int)(pixelRGB.getZ() > 255 ? 255 : pixelRGB.getZ())); 

		if (display != null)
			display.repaint();
    }

	/**
	 * Gives a random sunlight ray
	 * <p>
	 * Randomizing the sulight rays a bit allows for soft shadows.
	 * 
	 * @return get a random sunlight ray
	 */
	public static Vector3 getSunlightRay() {
		/**
		return new Vector3((RANDOM.nextDouble() - 0.5) * .05, -1, 
				(RANDOM.nextDouble() - 0.5) *.05).norm();
		// */
		//*
		return new Vector3((RANDOM.nextDouble() - 0.5), (RANDOM.nextDouble() - 0.5), 
				(RANDOM.nextDouble() - 0.5)).norm();
		// */
	}

	/**
	 * returns the light intensity of a certain ray based on its surroundings
	 * 
	 * @param bounceNumber the current bounce
	 * @param surfacePoint the point looking to be illuminated
	 * @param surfaceNormal the normal to the point
	 * @return
	 */
	public static Vector3 recursiveIllumination(int bounceNumber, 
		Vector3 surfacePoint, Vector3 surfaceNormal) {

		if (globalIllumBounces <= bounceNumber)
			return new Vector3(0x00, 0x00, 0x00);

		Vector3 totalLightContribution = new Vector3();

		for (int scatters = 0; scatters < globalIllumScatters; scatters++) {

			double angle1 = RANDOM.nextDouble() * Math.PI / 2;
			double angle2 = RANDOM.nextDouble() * Math.PI * 2;

			Vector3 localXAxis = Vector3.I_HAT.cross(surfaceNormal);

			Vector3 scatterRay = Vector3.rotate(surfaceNormal, localXAxis, 
					angle1);
			scatterRay =  Vector3.rotate(scatterRay, surfaceNormal, 
					angle2);

			Vector3 hitPoint = new Vector3();
			Vector3 hitSurfaceNormal = new Vector3();
			Vector3 hitSurfaceRGB = new Vector3();

			// did we scatter to a surface?
			if (scene.intersect(surfacePoint, scatterRay, hitPoint, 
					hitSurfaceNormal, hitSurfaceRGB)) {

				Vector3 workingSunRay =  getSunlightRay();
				Vector3 hitSurfaceIllumination = new Vector3();

				// is that surface directly illuminated?
				if (!scene.intersect(hitPoint, workingSunRay.scale(-1), 
						null, null, null)) {
					double lambert = hitSurfaceNormal.dot(workingSunRay);
					lambert = lambert < 0 ? -lambert : lambert;
					hitSurfaceIllumination.setAdd(hitSurfaceRGB.scale(lambert));
				}
				
				// how much light is that surface giving us?
				double distance = surfacePoint.sub(hitPoint).mag() + 1;
				double lambert = surfaceNormal.dot(scatterRay);
				lambert = lambert < 0 ? -lambert : lambert;

 				totalLightContribution.setAdd(hitSurfaceIllumination
				 		.add(recursiveIllumination(
						bounceNumber++, hitPoint, hitSurfaceNormal))
				 		.scale(lambert / (globalIllumScalar * distance * distance)));
			}
		}

		
		return totalLightContribution;

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

		parseSettings();

		scene.meshes.add(OBJReader.read("obj/dragon.obj"));
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

		if (isSaveToFile) {
			image.savePNG(outputFileLocation, System.currentTimeMillis() + ".png");
			System.out.println("Saved file");
		}
		System.out.print("Render Complete");
	}

}