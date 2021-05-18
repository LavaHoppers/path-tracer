/**
 * Container class for the main method
 */
public class Main {

	/**
	 * Start of execution
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		Display display = new Display(1366, 768);
		
		Vector3 camera = new Vector3();
		Vector3 cameraI = new Vector3(1.0, 0.0, 0.0);
		Vector3 cameraJ = new Vector3(0.0, 1.0, 0.0);
		Vector3 cameraK = new Vector3(0.0, 0.0, 1.0);

		double horFov = Math.PI / 2f;
		double verFov = Math.PI / 2f * (double)display.height / (double)display.width;

		double horRadPerPix = horFov / display.width;
		double verRadPerPix = verFov / display.height;

		double horInitRad = -horFov / 2f;
		double verInitRad = verFov / 2f;

		Triangle testTri = new Triangle(
			new Vector3(-1, 5, 0), 
			new Vector3(1, 5, 0), 
			new Vector3(0, 5, 1)
		);

		long startRenderTime = System.currentTimeMillis();

		for (int y = 0; y < display.height; y++) {

			Vector3 rayV = Vector3.rotateQ(cameraJ, cameraI, verInitRad - verRadPerPix * y);

			for (int x = 0; x < display.width; x++) {

				// ARGB values that will be sent to the display.set method
				int a = 0xFF;
				int r = 0;
				int g = 0;
				int b = 0;

			    Vector3 ray = Vector3.rotateQ(rayV, cameraK, horInitRad + horRadPerPix * x);
				
				if (Triangle.intersects(camera, ray, testTri) != null) 
					r = 0xFF;
				
				display.set(x, y, a, r, g, b);
				
				// Adds roughly 120 milliseconds to the render time inside the
				// rendering loop
				display.repaint();
			}
		}

		

		System.out.printf(
			"Total Time:  %d millis\nRender Time: %d millis\n",
			System.currentTimeMillis() - startTime,
			System.currentTimeMillis() - startRenderTime
		);
	}

	

}