package net.lavahoppers;

/**
 * PathTracer.java
 * 
 * 19 Sep 2021
 */

import java.util.Random;

import org.json.simple.JSONObject;

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

	public static boolean isMultithreadRender = false;

	public static final int BUCKET_SIZE = 48;

	public static int raysPerPixel = 1;
	public static int rayBounces = 1;
	public static int recursiveScatters = 1;
	public static int initialScatters = 1;
	public static double globalIllumScalar = 1;

	public static String outputFileLocation = "./";
	public static String hdriFileName = "";
 
	public static FastBufferedImage image = null;
	public static Display display = null;
	public static Scene	scene = null;
	public static final Random RANDOM = new Random();

	public static Vector3 cameraLocation = null;
	public static double cameraTheta = 0;
	public static double cameraPhi = 0;
	public static Matrix cameraPhiMatrix = null;
	public static Matrix cameraThetaMatrix = null;

	public static int pixelsRendered = 0;
			
	
	/**
	 * Returns a normalized camera ray for a pixel
	 * 
	 * @param x the pixel's x position
	 * @param y the pixel's y position
	 * 
	 * @return the camera ray
	 */
	public static Vector3 getCameraRay(int x, int y) {

		double inv = (double)image.getHeight() / image.getWidth();

		Vector3 ray = new Vector3(
			1.0,
			inv - (2.0 * inv) * (y + RANDOM.nextDouble()) / image.getHeight(),
			1.0 - (2.0 		) * (x + RANDOM.nextDouble()) / image.getWidth()
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
	public static void parseSettings() {

		JSONObject root = JSONReader.getRoot("settings.json");

		int[] res = JSONReader.getIntArray(root, "resolution");
		image = new FastBufferedImage(res[0], res[1]);

		image.fillGrayChecker(BUCKET_SIZE, 0xAF, 0xC0);

		isMultithreadRender = JSONReader.getBoolean(
			root, "multithreaded-render"
		);
		
		raysPerPixel = JSONReader.getInt(root, "rays-per-pixel");

		display = JSONReader.getBoolean(root, "realtime-display") ?
			new Display("Path Tracer", image) :
			null;

		outputFileLocation = JSONReader.getString(root, "image-output-dir");

		double[] loc = JSONReader.getDoubleArray(root, "camera-location");
		cameraLocation = new Vector3(loc[0], loc[1], loc[2]);

		cameraPhi = JSONReader.getDouble(root, "camera-pitch");
		cameraTheta = JSONReader.getDouble(root, "camera-yaw");
		cameraPhiMatrix = Matrix.getZRotationMatrix(cameraPhi);
		cameraThetaMatrix = Matrix.getYRotationMatrix(cameraTheta);

		rayBounces = JSONReader.getInt(
			root, "ray-bounces"
		);
		initialScatters = JSONReader.getInt(
			root, "initial-scatters"
		);
		recursiveScatters = JSONReader.getInt(
			root, "recursive-scatters"
		);
		globalIllumScalar = JSONReader.getDouble(
			root, "inverse-square-law-constant"
		);

		hdriFileName = JSONReader.getString(root, "hdri-file-name");

	
	}

	/**
     * Render a single pixel on the image
	 * 
     * @param x the x location of the pixel 
     * @param y the y location of the pixel
     */
    public static void renderPixel(int x, int y) {

		Vector3 pixelColor = new Vector3();

		Vector3 surfaceLocation = new Vector3();
		Vector3 surfaceNormal = new Vector3();
		Vector3 surfaceColor = new Vector3();

		Vector3 cameraRay = null;
		
		for (int i = 0; i < raysPerPixel; i++) {

			cameraRay = getCameraRay(x, y);

			boolean isRayIntersect = scene.intersect(
				cameraLocation, 
				cameraRay, 
				surfaceLocation,
				surfaceNormal, 
				surfaceColor
			);

			if (isRayIntersect) {
				pixelColor.setAdd(renderingEquation(
					cameraLocation, 
					cameraRay, 
					surfaceLocation,
					surfaceNormal, 
					surfaceColor,
					initialScatters,
					0
				));
			} else {
				pixelColor.setAdd(Scene.getDirectionalLight(cameraRay));
			}

		}

		
/*
        Vector3 pixelColor = new Vector3();
		
        for (int i = 0; i < raysPerPixel; i++)
			pixelColor.setAdd(getLightFromDirection(
					initialScatters,
					0, 
					cameraLocation, 
					getCameraRay(x, y)
			));
*/
        pixelColor.setScale(1.0 / raysPerPixel);

        image.setPixel(x, y, 
			(int)(pixelColor.getX() > 255 ? 255 : pixelColor.getX()), 
			(int)(pixelColor.getY() > 255 ? 255 : pixelColor.getY()), 
			(int)(pixelColor.getZ() > 255 ? 255 : pixelColor.getZ())); 

		if (display != null)
			display.repaint();
		
		pixelsRendered++;
    }

	/**
	 * 
	 * @param observer
	 * @param omega0
	 * @param x
	 * @param n
	 * @param gamma
	 * @param scatters
	 * @param bounce
	 * @return
	 */
	private static Vector3 renderingEquation(
		Vector3 observer, Vector3 omega0, Vector3 x, Vector3 n, Vector3 gamma, 
		int scatters, int bounce
	) {

		if (rayBounces < bounce)
			return new Vector3();

		Vector3 colorOut = new Vector3();

		Vector3 surfaceLocation = new Vector3();
		Vector3 surfaceNormal = new Vector3();
		Vector3 surfaceColor = new Vector3();

		for (int i = 0; i < scatters; i++) {

			double rand = RANDOM.nextDouble();
			Vector3 scatterRay = 
				fresnelEffect(omega0, n) < rand ?
				scatterRay = getDiffuseScatter(n) :
				getSpecularScatter(.2, n, omega0);

			boolean isRayIntersect = scene.intersect(
				x, 
				scatterRay, 
				surfaceLocation,
				surfaceNormal, 
				surfaceColor
			);

			if (isRayIntersect) {

				double cosOfAngle = n.dot(scatterRay);
				cosOfAngle = cosOfAngle < 0 ? -cosOfAngle : cosOfAngle;

				colorOut.setAdd(renderingEquation(
					x, 
					scatterRay, 
					surfaceLocation,
					surfaceNormal, 
					surfaceColor,
					recursiveScatters,
					bounce++
				).scale(cosOfAngle));

			} else {
				colorOut.setAdd(Scene.getDirectionalLight(scatterRay));
			}

		}

		colorOut.setScale(1.0 / scatters);

		return colorOut;
	}

	/**
	 * Get the illumination of a location in a direction
	 * @param scatters
	 * @param point
	 * @param ray
	 */
/*
	private static Vector3 getLightFromDirection(
		int scatters, int bounce, Vector3 point, Vector3 ray
	) {

		if (rayBounces < bounce)
			return new Vector3();

		Vector3 totalLight = new Vector3();

		Vector3 surfaceLocation = new Vector3();
		Vector3 surfaceNormal = new Vector3();
		Vector3 surfaceColor = new Vector3();

		boolean isRayIntersection = scene.intersect(
			point, 
			ray, 
			surfaceLocation,
			surfaceNormal, 
			surfaceColor
		);

		if (isRayIntersection) {

			for (int i = 0; i < scatters; i++) {

//				double rand = RANDOM.nextDouble();
//				Vector3 scatterRay = 
//					fresnelEffect(ray, surfaceNormal) < rand ?
//					scatterRay = getDiffuseScatter(surfaceNormal) :
//					getSpecularScatter(.2, surfaceNormal, ray);

				Vector3 scatterRay = getDiffuseScatter(surfaceNormal);

				double cosOfAngle = surfaceNormal.dot(scatterRay);
				cosOfAngle = cosOfAngle < 0 ? -cosOfAngle : cosOfAngle;

				totalLight.setAdd(getLightFromDirection(
					recursiveScatters, bounce++, surfaceLocation, scatterRay
				).scale(cosOfAngle));
				
			}

			double distance = bounce < 1 ? 
				1 : point.sub(surfaceLocation).mag() + 1;

			totalLight.setScale(1.0 / (scatters * distance * distance));

		} else {
			return Scene.getDirectionalLight(ray);
		}

		return totalLight;

	}
*/

	/**
	 * Calculate the likelyhood of a specular scatter based on the angle
	 * between the observing ray and the surface normal.
	 * @param observerRay the ray observing the surface
	 * @param surfaceNormal the normal to the surface
	 * @return the likelyhood of a specular scatter between 0 and 1.
	 */
	public static double fresnelEffect(Vector3 observerRay, Vector3 surfaceNormal) {
		double c = 10;
		double b = .01;
		double cosOfAngle = observerRay.scale(-1).dot(surfaceNormal);
		cosOfAngle = cosOfAngle < 0 ? -cosOfAngle : cosOfAngle;

		return 1.0 / (1.0 + b) * (Math.pow(2.0, cosOfAngle * -c) + b);
	}

	/**
	 * Gives a random sunlight ray
	 * <p>
	 * Randomizing the sulight rays a bit allows for soft shadows.
	 * @param surfaceNormal the normal to the surfece that the ray is scattering
	 * 						from
	 * @return get a random sunlight ray
	 */
	public static Vector3 getDiffuseScatter(Vector3 surfaceNormal) {

		Vector3 scatterRay = new Vector3(
			RANDOM.nextDouble() - 0.5,
			RANDOM.nextDouble() - 0.5,
			RANDOM.nextDouble() - 0.5
		).setNorm();

		if (scatterRay.dot(surfaceNormal) < 0) 
			scatterRay.setScale(-1.0);
		
		return scatterRay;
	}

	public static Vector3 getSpecularScatter(double spread, Vector3 surfaceNormal, Vector3 ray) {

		Vector3 scatterRay = new Vector3(
			RANDOM.nextDouble() - 0.5,
			RANDOM.nextDouble() - 0.5,
			RANDOM.nextDouble() - 0.5
		).setNorm();

		return Vector3.rotate(ray, surfaceNormal, Math.PI).scale(-1).add(scatterRay.scale(spread)).norm();
	}


	/**
	 * returns the light intensity of a certain ray based on its surroundings
	 * 
	 * @param bounceNumber the current bounce
	 * @param surfaceLocation the point looking to be illuminated
	 * @param surfaceNormal the normal to the point
	 * @return
/*
	public static Vector3 recursiveIllumination(int bounceNumber, 
		Vector3 surfaceLocation, Vector3 surfaceNormal, Vector3 cameraRay) {

		if (rayBounces <= bounceNumber)
			return new Vector3(0x00, 0x00, 0x00);

		Vector3 totalLightContribution = new Vector3();

		for (int scatters = 0; scatters < recursiveScatters; scatters++) {

			double c = 10;
			double b = .1;
			double a = cameraRay.scale(-1).dot(surfaceNormal);
			a = a < 0 ? -a : a;

			double fresnel = 1.0 / (1.0 + b) * 
				(Math.pow(2.0, a * -c) + b);

			Vector3 scatterRay = null;
			if (RANDOM.nextDouble() > fresnel)
				scatterRay = getDiffuseScatter(surfaceNormal);
			else
				scatterRay = getSpecularScatter(.2, surfaceNormal, cameraRay);

//			Vector3 scatterRay = getDiffuseScatter(surfaceNormal);
			Vector3 hitPoint = new Vector3();
			Vector3 hitSurfaceNormal = new Vector3();
			Vector3 hitSurfaceRGB = new Vector3();

			// did we scatter to a surface?
			if (scene.intersect(surfaceLocation, scatterRay, hitPoint, 
					hitSurfaceNormal, hitSurfaceRGB)) {

				Vector3 workingSunRay =  getDiffuseScatter(surfaceNormal);
				Vector3 hitSurfaceIllumination = new Vector3();

				// is that surface directly illuminated?
				if (!scene.intersect(hitPoint, workingSunRay, 
						null, null, null)) {
					double lambert = hitSurfaceNormal.dot(workingSunRay);
					lambert = lambert < 0 ? -lambert : lambert;
					hitSurfaceIllumination.setAdd(hitSurfaceRGB.scale(lambert));
				}
				
				// how much light is that surface giving us?
				double distance = surfaceLocation.sub(hitPoint).mag() + 1;
				double lambert = surfaceNormal.dot(scatterRay);
				lambert = lambert < 0 ? -lambert : lambert;

 				totalLightContribution.setAdd(hitSurfaceIllumination
				 		.add(recursiveIllumination(
						bounceNumber++, hitPoint, hitSurfaceNormal, cameraRay))
				 		.scale(lambert / (globalIllumScalar * distance * distance)));
			}
		}

		
		return totalLightContribution;

	}
*/

	/**
	 * Create all the render threads to run
	 * 
	 * @return an array of render threads
	 */
	public static RenderThread[] getRenderThreads() {

		int neededThreads = (int)(Math.ceil((double)image.getWidth() / BUCKET_SIZE) * 
								  Math.ceil((double)image.getHeight() / BUCKET_SIZE));

		RenderThread[] threads = new RenderThread[neededThreads];
		
		int i = 0;

		for (int y = 0; y < image.getHeight(); y += BUCKET_SIZE) {
			for (int x = 0; x < image.getWidth(); x += BUCKET_SIZE) {

				int width = image.getWidth() < x + BUCKET_SIZE ? // too large?
							image.getWidth() - x : // okay, resize
							BUCKET_SIZE;
				int height = image.getHeight() < y + BUCKET_SIZE ? 
							 image.getHeight() - y : 
							 BUCKET_SIZE;

				threads[i] = new RenderThread(x, y, width, height);
				i++;
			}
		}

		return threads;
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

		long startTime = System.currentTimeMillis();

		parseSettings();

		scene = new Scene();

		scene.meshes.add(OBJReader.read("obj/dragon.obj"));
		scene.meshes.add(OBJReader.read("obj/plane.obj"));


		if (isMultithreadRender) {
			RenderThread[] threads = getRenderThreads();
			for (int i = 0; i < threads.length;)  {
				printProgressBar();				
				if (RenderThread.running() < Runtime.getRuntime().availableProcessors()) 
					threads[i++].start();
			}
		} else {
			new RenderThread(0, 0, image.getWidth(), image.getHeight()).start();
		}	

		while(0 < RenderThread.running()) { 
			sleep(); 
			printProgressBar();
		}

		System.out.println(
			"Done!                                                             "
			 + "   "
		);

		String fileName = "" + System.currentTimeMillis();
		image.savePNG(outputFileLocation, fileName);

		long deltaTime = System.currentTimeMillis() - startTime;

		System.out.printf("Render finished in %s.\n", milliToTime(deltaTime));

		System.out.printf(
			"The average milliseconds per pixel was %f.\n",
			(double)deltaTime / (double)(pixelsRendered)
		);

		System.out.println("Saved completed render as \"" + fileName + 
			".png\" in \"" + outputFileLocation + "\"." );

	}

	/**
	 * Get the time in hours, minutes, and seconds for a quantity of 
	 * milliseconds
	 * @param milli the milliseconds
	 * @return the milliseconds formatted as a string
	 */
	public static String milliToTime(long milli) {
		return String.format("%dh:%dm:%ds:%dms", 
		milli / 1000L / 60L / 60L, (milli / 1000L / 60L) % 60L,
		(milli / 1000L) % 60L, milli  % 1000L);
	}

	/**
	 * Prints out a progress bar for the render
	 */
	public static void printProgressBar() {
		double progress = 
			pixelsRendered / (double)(image.getWidth() * image.getHeight());
		String bar = "";
		for (double i = 0; i < 1; i+=.02) {
			if (i < progress)
				bar = bar + "#";
			else 
				bar = bar + " ";
		}
		System.out.printf("Progress: %5.1f%% |%s|\r", progress * 100, bar);
	}

}