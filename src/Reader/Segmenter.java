/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reader;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

/**
 * Used in all sorts of splitting up of images
 * @author Schuyler
 */
public class Segmenter 
{

    /**
     * Calculates the mean and standard deviation of the gaps in order to determine
     * which gaps are actual spaces and which ones are just spaces between letters
     * @param input - The line of text to split into words
     * @return
     */
    public static List<BufferedImage> getWords(BufferedImage input)
    {
        List<Integer> gaps = getGaps(input);
        float sum = 0;
        float mean;
        float threshold;
        
        for(Integer gap : gaps)
        {
            sum += gap;
        }
        mean = sum / (float)gaps.size();
        sum = 0;
        for(Integer gap : gaps)
        {
            sum += Math.pow((gap - mean), 2);
        }
        threshold = (float) (mean + 0.50 * Math.sqrt(sum / (float)gaps.size()));
                
        return splitHorizontally(input, threshold);
    }
    
    /**
     * Splits an image on gaps that are large than a given threshold
     * @param input
     * @param threshold
     * @return The list of split off images
     */
    public static List<BufferedImage> splitHorizontally(BufferedImage input, float threshold)
    {
        int height = input.getHeight();
        int width = input.getWidth();
        WritableRaster raster = input.getRaster();
        int gapSize;
        int pieceSize = 0;
        ArrayList<BufferedImage> pieces = new ArrayList<>();
        int x;
        
        for (x  = 0; x < width; x++)
        {
            if (isTextVert(raster, x) == false)
            {
                gapSize = 0;
                // Loop until the bottom of the text if found
                for (; x < width && isTextVert(raster, x) == false; x++)
                   gapSize++;
                
                if (gapSize > threshold)
                {
                    if (pieceSize > 0)
                    {
                        pieces.add(input.getSubimage(x - (pieceSize + gapSize), 0, pieceSize, height));
                    }
                    pieceSize = 1;
                }
                else
                {
                    pieceSize += gapSize + 1;
                }
            }
            else
            {
                pieceSize++;
            }
        }
        
        // This can occasionally throw exceptions when pieces are too small
        // This is normally caused by noise
        try
        {
            pieces.add(input.getSubimage(x - pieceSize, 0, pieceSize, height));
        }
        catch(Exception e)
        {
        }
        
        return pieces;
    }
    
    /**
     * Gets a list of the sizes of each gap in an image
     * @param input
     * @return
     */
    public static List<Integer> getGaps(BufferedImage input)
    {
        int width = input.getWidth();
        List<Integer> gaps = new ArrayList<>();
        WritableRaster raster = input.getRaster();
        int size;
        
        for (int x  = 0; x < width; x++)
        {
            if (isTextVert(raster, x) == false)
            {
                size = 0;
                // Loop until the bottom of the text if found
                for (; x < width && isTextVert(raster, x) == false; x++)
                   size++;
                
                gaps.add(size);
            }
        }
        
        return gaps;
    }
    
    /**
     * Splits a page of text into rows
     * @param input
     * @return
     */
    public static List<BufferedImage> getTextRows(BufferedImage input)
    {
        int height = input.getHeight();
        int width = input.getWidth();
        int top;
        int subHeight;
        
        List<BufferedImage> rows = new ArrayList<>();
        WritableRaster raster = input.getRaster();
        
        for (int y  = 0; y < height; y++)
        {
            if (isText(raster, y) == true)
            {
                top = y;
                // Loop until the bottom of the text if found
                for (subHeight = 1; y < height && isText(raster, y) == true; subHeight++)
                    y++;
                try
                {
                    rows.add(trimEdge(input.getSubimage(0, top, width, subHeight)));
                }
                catch(Exception e)
                {
                    
                }
            }
        }
        
        return rows;
    }
    
    /**
     * Trims white space around a word/letter
     * @param input
     * @return
     */
    public static BufferedImage trimEdge(BufferedImage input)
    {
        int width = input.getWidth();
        int newWidth = width;
        int leftMargin = 0;
        int rightMargin = 0;
        WritableRaster raster = input.getRaster();
        
        for (int x = 0; x < width && isTextVert(raster, x) != true; x++)
        {
            leftMargin++;
            newWidth--;
        }
        
        for (int x  = width - 1; x > leftMargin && isTextVert(raster, x) != true; x--)
        {
            rightMargin++;
            newWidth--;
        }
        
        return input.getSubimage(leftMargin, 0, newWidth, input.getHeight());
    }
    
    /**
     * Test if there is text in a given column
     * @param input The image to check
     * @param x The column to check
     * @return 
     */
    private static boolean isTextVert(WritableRaster input, int x)
    {
        int height = input.getHeight();
        
        // Get the line of pixels
        int[] pixelsOne = new int[height];
        input.getPixels(x, 0, 1, height--, pixelsOne);
            
        for (int j = 0; j < pixelsOne.length; ++j)
            if (pixelsOne[j] != 255)
                return true;
        
        // If it makes it through it returns false
        return false;
    }
    
    /**
     * Counts how many vernicle gaps are found in a an image
     * @param input
     * @return
     */
    public static int countGapCollumns(BufferedImage input)
    {
        int count = 0;
        int width = input.getWidth();
        
        WritableRaster rasterOne = input.getRaster();
        
        for (int i = 0; i < width; ++i)
        {
            if (!isTextVert(rasterOne, i))
                count++;
        }
        
        return count;
    }
    
    /**
     * Checks if there is text in a given row on an image
     * @param input
     * @param y - The row to check
     * @return 
     */
    private static boolean isText(WritableRaster input, int y)
    {
        int threshold = 5;
        int count = 0;
        int width = input.getWidth();
        
        // Get the line of pixels
        int[] pixelsOne = new int[width];
        input.getPixels(0, y, width, 1, pixelsOne);
            
        for (int j = 0; j < width; ++j)
        {
            if (pixelsOne[j] == 0)
                count++;
            
            if (count == threshold)
                return true;
        }
        // If it makes it through it returns false
        return false;
    }
}