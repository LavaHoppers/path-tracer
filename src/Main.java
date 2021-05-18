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

		// 1366, 768
		Display display = new Display(480, 360);
		
		Vector3 camera  = new Vector3(0.0, -7.0, 0.0);
		Vector3 cameraI = new Vector3(1.0, 0.0, 0.0);
		Vector3 cameraJ = new Vector3(0.0, 1.0, 0.0);
		Vector3 cameraK = new Vector3(0.0, 0.0, 1.0);

		double horFov = Math.PI / 2f;
		double verFov = Math.PI / 2f * (double)display.height / (double)display.width;

		double horRadPerPix = horFov / display.width;
		double verRadPerPix = verFov / display.height;

		double horInitRad = -horFov / 2f;
		double verInitRad = verFov / 2f;

		Mesh teapot = new Mesh("teapot.obj");

		long startRenderTime = System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++)
		for (int y = 0; y < display.height; y++) {

			Vector3 rayV = Vector3.rotate(cameraJ, cameraI, verInitRad - verRadPerPix * y);

			for (int x = 0; x < display.width; x++) {

				// ARGB values that will be sent to the display.set method
				int a = 0xFF;
				int r = 0;
				int g = 0;
				int b = 0;

			    Vector3 ray = Vector3.rotate(rayV, cameraK, horInitRad + horRadPerPix * x);
				
				for (Triangle tri : teapot.triangles) {
					if (Triangle.intersects(camera, ray, tri) != null) 
						r = g = b = 0xFF;
				}
				display.set(x, y, a, r, g, b);
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