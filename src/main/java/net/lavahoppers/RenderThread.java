package net.lavahoppers;

/*
 * RenderThread.java
 * 
 * 30 May 2021
 * 
 */

import java.lang.Thread;

/**
 * An instantiable class designed to render a certain portion of pixels on the
 * image. The RenderThread should be created, passed the pixels to render,
 * and the necessary info for rendering, and then should have it's .start() 
 * method called to perform the rendering.
 * <p>
 * This class extends the Thread class from java lang so when .start() is called
 * it will run this rendering thread on a new CPU core.
 * <p>
 * The renderThread will render the portion of the image from the starting point
 * in raster space until it 
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class RenderThread extends Thread {

    private int x;
    private int y;
    private int width;
    private int height;

    private static final ThreadGroup GROUP = new ThreadGroup("render");

    /**
     * Create a render thread that can render a portion of the image
     * <p>
     * All the arguments are in pixels
     * 
     * @param x the lowest x value of the image area
     * @param y the lowest y value of the image area
     * @param width the width of the image area 
     * @param height the height of the image area
     */
    public RenderThread(int x, int y, int width, int height) {
        super(GROUP, x + " " + y); 
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    /**
     * Rendered a horizontal stripe of the screen
     * <p>
     * Instead of calling this method directly, call the start function for this 
     * rendering thread to run this method on an available cpu core.
     */
    @Override
    public void run() {
        for (int y = this.y; y < this.y + this.height; y++) {
			for (int x = this.x; x < this.x + this.width; x++){ 
                PathTracer.renderPixel(x, y);
            }
        }

    } 

    /**
     * Get the active thread count for this group
     * @return the active thread count
     */
    public static int running() {
        return GROUP.activeCount();
    }



    
    
}
