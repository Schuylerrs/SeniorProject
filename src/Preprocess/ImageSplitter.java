package Preprocess;

import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageSplitter 
{
    public static BufferedImage[] splitImage(BufferedImage image, int divs)
    {
        int chunks = divs * divs;
        int chunkWidth = image.getWidth() / divs; // determines the chunk width and height
        int chunkHeight = image.getHeight() / divs;
        
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        
        for (int x = 0; x < divs; x++) 
        {
            for (int y = 0; y < divs; y++) 
            {
                //Initialize the image array with image chunks                
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        
        return imgs;
    }
    
    public static BufferedImage mergeImages(BufferedImage[] inputImages, int divs)
    {
        int chunkWidth;
        int chunkHeight;
        int type;

        type = inputImages[0].getType();
        chunkWidth = inputImages[0].getWidth();
        chunkHeight = inputImages[0].getHeight();

        //Initializing the final image
        BufferedImage resultImage = new BufferedImage(chunkWidth * divs, chunkHeight * divs, type);

        int num = 0;
        for (int i = 0; i < divs; i++) 
        {
            for (int j = 0; j < divs; j++) 
            {
                resultImage.createGraphics().drawImage(inputImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }

        return resultImage;
    }
}
