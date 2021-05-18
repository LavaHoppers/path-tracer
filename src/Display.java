// Advice used for drawing pixels fast
// stackoverflow.com/questions/13638793

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Class for interactin with the "path finding"
 */
class Display {

    private JFrame frame;
	private BufferedImage image;
	private DataBuffer data;
	private JPanel panel;

	public final int width;
	public final int height;

	public boolean running = true;

	/**
	 * Create a new display with size width by height
	 * @param width in pixels
	 * @param height in pixels
	 */
    Display(int width, int height) {

        this.width = width;
        this.height = height;

		frame = new JFrame("path tracer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		data = image.getRaster().getDataBuffer();
		panel = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(image, 0, 0, null);
            }
        };
								
		frame.add(panel);
		frame.validate();
		fill(0xFFFF0000);
		panel.repaint();

    }

	/**
	 * Repaints the display window
	 */
	public void repaint() {
		panel.repaint();
	}

	/**
	 * Fills the screen with a color.
	 */
	public void fill(int argbHex) {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				data.setElem(x + y * width, argbHex);
	}

    /**
	 * Fills the screen with a color.
	 */
	public void fill(int a, int r, int g, int b) {
		fill(a << 24 | r << 16 | g << 8 | b);
	}

    /**
	 * Set a pixel on the screen to a color. There is no error checking done on this
	 * function for the sake of speed. All ARGB colors should be in the range of 0:255,
	 * and the x and y should be valid pixel locations on the screen.
	 * 
	 * @param x the x location of the pixel
	 * @param y the y location of the pixel
	 * @param a the alpha value of the pixel
	 * @param r the red value of the pixel
	 * @param g the green value of the pixel
	 * @param b the blue value of the pixel
	 */
	public void set(int x, int y, int a, int r, int g, int b) {
		/* the argb value is made by shifting all the bits to the 
		correct spots and concatinating them with bitwise-or.*/

		data.setElem(x + y * width, a << 24 | r << 16 | g << 8 | b);
	}


}