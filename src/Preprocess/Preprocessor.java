/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Preprocess;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import static java.awt.image.ConvolveOp.EDGE_NO_OP;
import java.awt.image.Kernel;

/**
 * Used for processing the image before it is read
 * @author Schuyler
 */
public class Preprocessor 
{

    /**
     * The program uses a Gaussian blur to try and reduce noise in an image
     * @param sourceImage - The image to blur
     * @param denoiseLevel - How much noise to try and remove
     * @return The denoised image
     */
    public static BufferedImage denoise(BufferedImage sourceImage, float denoiseLevel)
    {
        // A placeholder because the program doesn't allow the same image to be
        // both source and destination
        BufferedImage destImage = null;
        float[] gaussianBlur = new float[] 
        {
            0.00000067f * denoiseLevel, 0.00002292f * denoiseLevel, 0.00019117f * denoiseLevel, 0.00038771f * denoiseLevel, 0.00019117f * denoiseLevel, 0.00002292f * denoiseLevel, 0.00000067f * denoiseLevel,
            0.00002292f * denoiseLevel, 0.00078634f * denoiseLevel, 0.00655965f * denoiseLevel, 0.01330373f * denoiseLevel, 0.00655965f * denoiseLevel, 0.00078633f * denoiseLevel, 0.00002292f * denoiseLevel,
            0.00019117f * denoiseLevel, 0.00655965f * denoiseLevel, 0.05472157f * denoiseLevel, 0.11098164f * denoiseLevel, 0.05472157f * denoiseLevel, 0.00655965f * denoiseLevel, 0.00019117f * denoiseLevel,
            0.00038771f * denoiseLevel, 0.01330373f * denoiseLevel, 0.11098164f * denoiseLevel, 0.22508352f * denoiseLevel, 0.11098164f * denoiseLevel, 0.01330373f * denoiseLevel, 0.00038771f * denoiseLevel,
            0.00019117f * denoiseLevel, 0.00655965f * denoiseLevel, 0.05472157f * denoiseLevel, 0.11098164f * denoiseLevel, 0.05472157f * denoiseLevel, 0.00655965f * denoiseLevel, 0.00019117f * denoiseLevel,
            0.00002292f * denoiseLevel, 0.00078633f * denoiseLevel, 0.00655965f * denoiseLevel, 0.01330373f * denoiseLevel, 0.00655965f * denoiseLevel, 0.00078633f * denoiseLevel, 0.00002292f * denoiseLevel,
            0.00000067f * denoiseLevel, 0.00002292f * denoiseLevel, 0.00019117f * denoiseLevel, 0.00038771f * denoiseLevel, 0.00019117f * denoiseLevel, 0.00002292f * denoiseLevel, 0.00000067f * denoiseLevel
        };
        Kernel blurKernel = new Kernel(7, 7, gaussianBlur);
        ConvolveOp blurOp = new ConvolveOp(blurKernel, EDGE_NO_OP, null);
        
        sourceImage = blurOp.filter(sourceImage, destImage);
        
        return sourceImage;
    }
    
    /**
     * Attempts to automatically detect the number of chunks to use when thresholding
     * the image.
     * @param sourceImage - The image to check to see how many chunks are needed
     * @return The number of chunks to use when thresholding
     */
    public static int guessChunks(BufferedImage sourceImage)
    {
        int threshold = (int) ((sourceImage.getHeight() * sourceImage.getWidth()) / 128);
        int histogram[] = Thresholder.getHistogram(sourceImage);
        int count = 0;
        
        // Find the different RGB values that take up a large portion of the image
        for (int i = 0; i < histogram.length; i++)
        {
            if (histogram[i] > threshold)
            {
                count++;
            }
        }
        
        // A fudge factor... This just seemed to work well on the test images
        count /= 10;
        
        // The count needs to be at least 1
        if (count == 0)
        {
            count = 1;
        }
        
        return count;
    }
    
    /**
     * Removes noise, converts the image to black and white, and then rotates the
     * image to upright.
     * @param sourceImage - The image to process
     * @param numChunks - The number of chunks to break the image into
     * @param denoiseLevel - How much noise to remove
     * @return The processed image
     */
    public static BufferedImage preprocess(BufferedImage sourceImage, int numChunks, int denoiseLevel)
    {
        // Clean up image
        if (denoiseLevel > 0)
        {
            sourceImage = Preprocessor.denoise(sourceImage, denoiseLevel);
        }
        
        // Threshold the image
        BufferedImage chunks[] = ImageSplitter.splitImage(sourceImage, numChunks);
        
        for (int i = 0; i < chunks.length; i++)
        {
            chunks[i] = Thresholder.thresholdImage(chunks[i]);
        }
        
        sourceImage = ImageSplitter.mergeImages(chunks, numChunks);
        
        // Rotate the image
        sourceImage = Rotator.autoSkew(sourceImage);
        
        
        return sourceImage;
    }
}
