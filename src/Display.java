/*
 * Display.java
 * 
 * 29 May 2021
 */

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Class for displaying an image to the screen
 * <p>
 * This class makes use of a buffered image's databuffer to significantly speed
 * up the process of displaying a ray traced image.
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
class Display {

	private final JFrame 		frame;
	public final BufferedImage image;
	private final DataBuffer 	data;
	private final JPanel 		panel;
	private final KeyListener 	keyListener;

	private final int width;
	private final int height;

	/**
	 * Create a new display with size width by height
	 * <p>
	 * The display size is width by height in pixels. The display only renders it's one
	 * internal buffered image that can be altered through the set method. The display can
	 * be closed through force or by simply pressing escape.
	 * <p>
	 * Call the repaint method to send the buffered image to the display.
	 * @see Display#set(int, int, int, int, int, int)
	 * @param width in pixels
	 * @param height in pixels
	 */
    Display(int width, int height) {

		this.width = width;
		this.height = height;

		/* Create the OS window that will be used to display the image */
		frame = new JFrame("path tracer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		/* Creat the buffered image that will be displayed every time the repaint
		method is called, and grab it's databuffer for fast pixel assignment */
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		data = image.getRaster().getDataBuffer();

		/* Create a component and implement it's paint method to draw the buffered
		image */
		panel = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(image, 0, 0, null);
            }
        };

		/* Create a key listener that simpley closes the window and stop the 
		program when escape is presed */
		keyListener = new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					frame.dispose();
					System.exit(0);
				}
			}
		};

		/* Add the component to the frame and validate the frame to force the added
		components to work... Not sure why this is neccessary, but it is! */
		frame.addKeyListener(keyListener);	
		frame.add(panel);
		frame.validate();

		/* Set the databuffer to the default background and push the buffer to the
		display */
		cheker();
		panel.repaint();

    }

	/**
	 * Set the image buffer to the "default" background
	 * <p>
	 * The default background is just a grey checkerboard pattern.
	 */
	private void cheker() {

		int lightGrey = 0xFF999999;
		int darkGrey = 0xFF808080;

		for (int y = 0; y < this.height; y++)
			for (int x = 0; x < this.width; x++) {
				int xp = x % 80;
				int yp = y % 80;
				if (xp < 40 && yp < 40 || 40 < xp && 40 < yp)
					data.setElem(x + y * this.width, darkGrey);
				else
					data.setElem(x + y * this.width, lightGrey);
			}
	}

	/**
	 * Repaints the display window
	 * <p>
	 * Calling the set function will update the array dictating the pixel colors
	 * of the image. Then, calling this function will push that array to the screen.
	 */
	public void repaint() {
		panel.repaint();
	}

    /**
	 * Set a pixel on the screen to a color. 
	 * <p>
	 * There is no error checking done on this
	 * function for the sake of speed. All ARGB colors should be in the range of 0:255,
	 * and the x and y should be valid pixel locations on the screen.
	 * <p>
	 * the argb value is made by shifting all the bits to the correct spots and 
	 * concatinating them with bitwise-or.
	 * <p>
	 * @see <a href = https://stackoverflow.com/questions/13638793>stackoverflow</a>
	 * @param x the x location of the pixel
	 * @param y the y location of the pixel
	 * @param r the red value of the pixel
	 * @param g the green value of the pixel
	 * @param b the blue value of the pixel
	 */
	public void set(int x, int y, int r, int g, int b) {
		data.setElem(x + y * width, 0xFF000000 | r << 16 | g << 8 | b);
	}


}