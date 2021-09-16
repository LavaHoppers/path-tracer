package net.lavahoppers;

/*
 * Display.java
 * 
 * 29 May 2021
 */

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class for displaying a {@code BufferedImage} to the screen
 * <p>
 * This class makes use of a {@code FastBufferedImage} to display the 
 * frames quickly
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class Display {

	private final JFrame 		frame;
	private final JPanel 		panel;
	private final KeyListener 	keyListener;

	/**
	 * Create a new display from a {@code FastBufferedImage}.
	 * <p>
	 * The display size is the same as the image. The display only renders it's one
	 * buffered image that can be altered through the set method. The display can
	 * be closed through force or by simply pressing escape.
	 * <p>
	 * Call the repaint method to send the image to the display.
	 * 
	 * @see FastBufferedImage#set(int, int, int, int, int)
	 * @param name the name of the window
	 * @param image the image to be displayed in the window
	 */
    public Display(String name, final FastBufferedImage image) {


		/* Create the OS window that will be used to display the image */
		frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

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
		panel.repaint();

    }


	/**
	 * Repaints the display window
	 * <p>
	 * Displays the most up-to-date image information
	 */
	public void repaint() {
		panel.repaint();
	}



}