/*
 * FastBufferedImage.java
 * 
 * 29 May 20201
 */

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class for writing to a {@code BufferedImage} quickly.
 * <p>
 * This class extends the {@code BufferedImage} class.
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class FastBufferedImage extends BufferedImage {

    private DataBuffer data;

    /**
     * Create a {@code FastBufferedImage} with dimensions width and height in
     * pixels.
     * <p>
     * The {@code FastBufferedImage} class <b>ONLY</b> supports RGB images
     * because I said so!
     * 
     * @param width  the width of the image in pixels
     * @param height the height of the image in pixels
     */
    public FastBufferedImage(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
        data = this.getRaster().getDataBuffer();
    }

    /**
     * Set a pixel on the caller to a color.
     * <p>
     * There is no error checking done on this function for the sake of speed. All
     * RGB colors should be in the range of 0 to 0xFF, and the x and y should be
     * valid pixel locations on the screen.
     * 
     * @see <a href = https://stackoverflow.com/questions/13638793>stackoverflow</a>
     * @param x the x location of the pixel
     * @param y the y location of the pixel
     * @param r the red value of the pixel
     * @param g the green value of the pixel
     * @param b the blue value of the pixel
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        data.setElem(x + y * getWidth(), r << 16 | g << 8 | b);
    }

    /**
     * Fill the caller with an RBG value.
     * <p>
     * The color value elements should be in the range of 0 to 0xFF.
     * 
     * @param r the red value
     * @param g the green value
     * @param b the blue value
     */
    public void fill(int r, int g, int b) {
        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++)
                setPixel(x, y, r, g, b);
    }

    /**
     * Fills the caller with a gray checkered pattern.
     * <p>
     * The two shades should be specified as integers 0 to 0xFF that represent
     * the R, G, and B components of two RGB values.
     * 
     * @param dim    the dimensions of the checker pattern
     * @param shade1 the intensity of the first shade
     * @param shade2 the intensity of the second shade
     */
    public void fillGrayChecker(int dim, int shade1, int shade2) {
        int b = dim / 2;
        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++) {
                int xp = x % dim;
                int yp = y % dim;
                if (xp < b && yp < b || b < xp && b < yp)
                    setPixel(x, y, shade1, shade1, shade1);
                else
                    setPixel(x, y, shade2, shade2, shade2);
            }
    }

    /**
     * Save the caller as a PNG in a directory.
     * 
     * @param path the directory to save the file into. The path should
     *             <b>NOT</b> include the trailing seperator.
     * @param name the name of the file
     * @return {@code true} if the image was saved succesfully, false otherwise.
     */
    public boolean savePNG(String path, String name) {
        File outputfile = new File(path + File.separator + name + ".png");
        try {
            ImageIO.write(this, "png", outputfile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
