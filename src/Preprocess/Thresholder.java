/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Preprocess;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author Schuyler
 */
public class Thresholder 
{    
    public static BufferedImage thresholdImage(BufferedImage sourceImage)
    {
        BufferedImage processedImage = toGrayScale(sourceImage);
        int histogram[] = getHistogram(toByteArray(processedImage));
        int threshold = Otsu(histogram);
        
        return toBitmap(processedImage, threshold);
    }
    
    public static int Otsu(int [] data ) 
    {
        int curThresh;        
        int optThresh;        
        double gtrThenThresh; 
        double numPoints;     
        double BCV;           
        double BCVmax;        
        double num; 
        double denom;    
        double Sk;            
        double S;
        double L=256;      

        S = numPoints = 0;
        for (curThresh=0; curThresh<L; curThresh++)
        {
            S += (double)curThresh * data[curThresh];
            numPoints += data[curThresh];
        }

        Sk = 0;
        gtrThenThresh = data[0];
        BCV = 0;
        BCVmax=0;
        optThresh = 0;

        for (curThresh = 1; curThresh < L - 1; curThresh++) 
        {
            Sk += (double)curThresh * data[curThresh];
            gtrThenThresh += data[curThresh];

            denom = (double)( gtrThenThresh) * (numPoints - gtrThenThresh);

            if (denom != 0 )
            {
                    num = ( (double)gtrThenThresh / numPoints ) * S - Sk;
                    BCV = (num * num) / denom;
            }
            else
                    BCV = 0;

            if (BCV >= BCVmax)
            {
                    BCVmax = BCV;
                    optThresh = curThresh;
            }
        }

        return optThresh;
    }
    
    public static int[] getHistogram(BufferedImage srcImage)
    {
        BufferedImage processedImage = toGrayScale(srcImage);
        return getHistogram(toByteArray(processedImage));
    }
    
    private static int[] getHistogram(byte[] greyImage)
    {
        int ptr = 0;
        int histData[] = new int[256];
        
        for (int i = 0; i < histData.length; i++) 
            histData[i] = 0;

        while (ptr < greyImage.length) 
        {
            int h = 0xFF & greyImage[ptr];
            histData[h] ++;
            ptr ++;
        }
        
        return histData;
    }
    
    private static BufferedImage toGrayScale(BufferedImage input)
    {
        BufferedImage greyImage = new BufferedImage(input.getWidth(), 
                                                    input.getHeight(), 
                                                    BufferedImage.TYPE_BYTE_GRAY);  
        Graphics g = greyImage.getGraphics();  
        g.drawImage(input, 0, 0, null);  
        g.dispose();
        
        return greyImage;
    }
    
    private static byte[] toByteArray(BufferedImage input)
    {
        // Get raw image data
        Raster raster = input.getData();
        DataBuffer buffer = raster.getDataBuffer();

        int type = buffer.getDataType();

        DataBufferByte byteBuffer = (DataBufferByte) buffer;
        return byteBuffer.getData(0);
    }
    
    public static BufferedImage toBitmap(BufferedImage image, int threshold) 
    {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(image, 0, 0, null);
        WritableRaster raster = result.getRaster();
        
        int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) 
        {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) 
            {
                if (pixels[i] < threshold) 
                    pixels[i] = 0;
                else 
                    pixels[i] = 255;
            }
            raster.setPixels(0, y, image.getWidth(), 1, pixels);
        }
        return result;
    }
    
    public static BufferedImage andImage(BufferedImage one, BufferedImage two)
    {
        int width;
        int height;
        
        if (one.getWidth() > two.getWidth())
        {
            width = two.getWidth();
        }
        else
        {
            width = one.getWidth();            
        }
        
        if (one.getHeight() > two.getHeight())
        {
            height = two.getHeight();
        }
        else
        {
            height = one.getHeight();            
        }
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster rasterResult = result.getRaster();
        WritableRaster rasterOne = one.getRaster();
        WritableRaster rasterTwo = two.getRaster();
        
        int[] pixelsResult;
        int[] pixelsOne = new int[width];
        int[] pixelsTwo = new int[width];

        for (int y = 0; y < height; y++) 
        {
            pixelsResult = new int[width];
            rasterOne.getPixels(0, y, width, 1, pixelsOne);
            rasterTwo.getPixels(0, y, width, 1, pixelsTwo);

            for (int i = 0; i < pixelsOne.length; i++) 
            {
                if (pixelsTwo[i] == 0 && pixelsOne[i] == 0)
                {
                    pixelsResult[i] = 0;
                }
                else 
                {
                    pixelsResult[i] = 255;
                }
            }
            rasterResult.setPixels(0, y, width, 1, pixelsResult);
        }
        
        return result;
    }
}
