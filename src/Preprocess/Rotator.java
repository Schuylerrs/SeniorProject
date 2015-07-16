/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Preprocess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

/**
 *
 * @author Schuyler
 */
public class Rotator 
{
    @SuppressWarnings("empty-statement")
    public static BufferedImage autoSkew(BufferedImage input)
    {
        double bestAngle = 0;
        BufferedImage temp = rotateImage(input, -45.0);
        double bestRows = getRows(temp);
        double lastRows;
        double i;
        double min;
        double max;
        double[] range = {45.0, 5.0, 1.0}; 
        double[] diff = {10.0, 1.0, 0.1};
        double change;
        
        for (int k = 0; k < 3; k++)
        {
            min = bestAngle - range[k];
            max = bestAngle + range[k];
            change = diff[k];
            
            for(i = min; i <= max; i += change)
            {
                temp = input;
                temp = rotateImage(temp, i);
                lastRows = getRows(temp);

                if (lastRows > bestRows)
                {
                    bestRows = lastRows;
                    bestAngle = i;
                }
            } 
        }

        return rotateImage(input, bestAngle);
    }
    
    public static BufferedImage rotateImage(BufferedImage input, double angle)
    {
        // Rotation information
        double rotationRequired = Math.toRadians(angle);
        int w = input.getWidth();
        int h = input.getHeight();
  
        BufferedImage result = new BufferedImage(w, h, input.getType());  
        Graphics2D g2 = result.createGraphics();  
        g2.fillRect(0, 0, w, h);
        g2.rotate(rotationRequired, w / 2, w / 2);
        g2.drawImage(input, null, 0, 0);  
        
        return result;
    }
    
    private static int getRows(BufferedImage input)
    {
        int count = 0;
        int j;
        int width = input.getWidth();
        int height = input.getHeight();
        
        WritableRaster rasterOne = input.getRaster();
        
        int[] pixelsOne = new int[width];

        for (int i = 0; i < height; ++i)
        {
            rasterOne.getPixels(0, i, width, 1, pixelsOne);
            
            for (j = 0; j < width; ++j)
                if (pixelsOne[j] == 0)
                    break;
            
            if (j == width)
                count++;
        }
        
        return count;
    }
}
