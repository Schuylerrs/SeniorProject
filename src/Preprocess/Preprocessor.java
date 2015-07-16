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
 *
 * @author Schuyler
 */
public class Preprocessor 
{
    public static BufferedImage denoise(BufferedImage sourceImage, float denoiseLevel)
    {
        BufferedImage destImage = null;
        float[] blur = new float[] 
        {
            0.0625f, 0.125f, 0.0625f,
            0.125f,  0.25f, 0.125f,
            0.0625f, 0.125f, 0.0625f
        };
        Kernel blurKernel = new Kernel(3, 3, blur);
        ConvolveOp blurOp = new ConvolveOp(blurKernel, EDGE_NO_OP, null);
        
        float[] sharp = new float[] 
        {
            0.0f, -1.0f, 0.0f,
            -1.0f,  5.0f, -1.0f,
            0.0f, -1.0f, 0.0f
        };
            
        Kernel sharpKernel = new Kernel(3, 3, sharp);
        ConvolveOp sharpOp = new ConvolveOp(sharpKernel, EDGE_NO_OP, null);
        
        for (int i = 0; i < denoiseLevel; i++)
        {
            sourceImage = blurOp.filter(sourceImage, destImage);
            sourceImage = sharpOp.filter(sourceImage, destImage);
        }
        
        return sourceImage;
    }
    
    public static int guessChunks(BufferedImage sourceImage)
    {
        int threshold = (int) ((sourceImage.getHeight() * sourceImage.getWidth()) / 128);
        int histogram[] = Thresholder.getHistogram(sourceImage);
        int count = 0;
        
        for (int i = 0; i < histogram.length; i++)
        {
            if (histogram[i] > threshold)
            {
                count++;
            }
        }
        
        count /= 10;
        
        if (count == 0)
        {
            count = 1;
        }
        
        return count;
    }
    
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
