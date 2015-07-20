/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Used when reading the text fro the image
 * @author Schuyler
 */
public class Classifyer  {
    // If the learner is trained or not
    private static boolean trained;
    // The part that does the actual heavy lifting
    private static KnnClassifier knn;
    // Used in training the learner and in intrepreting the learner's output
    private static final String[] letterVal = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                                                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
                                                "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", 
                                                "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", 
                                                "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", 
                                                "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", 
                                                "9", "0", ".", "?", "#", "@", "!", "$", "%", "(", 
                                                ":", ")", ";", "\'", "&", "\"", "\""};
    // One attribute for each pixle plus the height and the width times two for 
    // histograms of the height/width and the stroke count for height/width
    private static final int ATT_COUNT = 512 + ((32 + 16) * 2);
    
    /**
     * Takes in an image with 24 of each letter in the order specified by letterVal
     * @param images - The images containing the letters
     * @throws Exception
     */   
    public static void trainClassifyer(List<BufferedImage> images) throws Exception
    {
        Instances trainingData = ImageListToInstances(images);
        
        knn = new KnnClassifier();
        knn.buildClassifier(trainingData);
        trained = true;
    }
    
    /**
     * Converts a list of images into instances
     * @param images
     * @return 
     */
    private static Instances ImageListToInstances(List<BufferedImage> images)
    {
        int letterCount = 0;
        FastVector letters = new FastVector();
        for (String thisLetter : letterVal)
        {
            letters.addElement(thisLetter);
        }
        
        FastVector fvNominalVal = new FastVector(ATT_COUNT + 1);
        for (Integer x = 0; x < ATT_COUNT + 1; x++)
        {
            fvNominalVal.addElement(new Attribute(x.toString()));
        }
        
        Instances trainingData = new Instances("letters", fvNominalVal, 6500);
        trainingData.setClassIndex(ATT_COUNT);
        
        for (BufferedImage image : images)
        {
            Instance temp = imageToInstance(image);
            try {
                temp.setValue(ATT_COUNT, letterCount++ / 24);
            }
            catch (Exception ex) {
                System.out.println(ex + " Thrown in Classifying");
            }

            trainingData.add(temp);
        }
        
        return trainingData;
    }
    
    /**
     * Resizes the image to a standard size
     * @param input - The image to resize
     * @return The resized (16 X 32) image
     */
    private static BufferedImage resize(BufferedImage input)
    {
        int Y_SIZE = 32;
        int X_SIZE = 16;

        BufferedImage newImage = new BufferedImage(X_SIZE, Y_SIZE, BufferedImage.TYPE_INT_RGB);

        Graphics g = newImage.createGraphics();
        g.drawImage(input, 0, 0, X_SIZE, Y_SIZE, null);
        g.dispose();
        
        return newImage;
    }
    
    /**
     * Converts the image into an instance by taking the value of each pixel, the
     * number of black pixels in each row/col of the image, and the number of strokes
     * on each row/col.
     * @param image - The image to convert
     * @return The resulting Instance
     */
    private static Instance imageToInstance(BufferedImage image)
    {
        BufferedImage newImage = resize(image);
        int height = newImage.getHeight();
        int width = newImage.getWidth();
        List<Integer> gaps = new ArrayList<>();
        WritableRaster raster = newImage.getRaster();
        
        int[] pixels = new int[1537];
        raster.getPixels(0, 0, width, height, pixels);
    
        Instance imageInstance = new Instance(ATT_COUNT + 1);
        
        for (int i = 0; i < 513; i++)
        {
            imageInstance.setValue(i, pixels[i] / 250); 
        }
        
        int sum;
        for (int i = 0; i < height; i++)
        {
            sum = 0;
            for (int j = 0; j < width; j++)
            {
                sum += pixels[(i * width) + j];
            }
            
            imageInstance.setValue(i + 512, sum / 255);
        }
        
        for (int i = 0; i < width; i++)
        {
            sum = 0;
            for (int j = 0; j < height; j++)
            {
                sum += pixels[(j * width) + i];
            }
            
            imageInstance.setValue(i + height + 512, sum / 255);
        }
        
        int offset = 512 + height + width;
        
        // Extra weight added to the number of strokes because it is less likely
        // to be effected by noise in the image
        // Strokes across the x
        int[] rowPixels = new int[width * 3];
        for (int i = 0; i < height; i++)
        {            
            raster.getPixels(0, i, width, 1, rowPixels);
            imageInstance.setValue(i + offset, getStrokes(rowPixels) * 20);
        }
        
        offset += height;
        
        // Strokes accross the y
        int[] colPixels = new int[height * 3];
        for (int i = 0; i < width; i++)
        {            
            raster.getPixels(i, 0, 1, height, colPixels);
            imageInstance.setValue(i + offset, getStrokes(colPixels) * 20);
        }
        
        return imageInstance;
    }
    
    /**
     * Gets the number of times that a line in the character crosses a specific
     * row/col of the picture.
     * @param pixels - The row/col to check
     * @return The number of strokes found (x2)
     */
    private static int getStrokes(int[] pixels)
    {
        int strokes = 0;
        
        for (int i = 1; i < pixels.length; i++)
        {
            if (pixels[i] != pixels[i-1])
            {
                strokes++;
            }
        }
        
        return strokes;
    }
    
    /**
     * Takes in an image and turns it into a string.
     * @param image - The image of a letter
     * @return The string that it guessed
     */
    public static String clasifyImage(BufferedImage image)
    {
        if (trained)
        {
            Instance letter = imageToInstance(image);
            Double value = 0.0;
            try {
                value = knn.classifyInstance(letter, 6);
            } catch (Exception ex) {
                Logger.getLogger(Classifyer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return letterVal[value.intValue()];
        }
        else
            return "?";
    }

    /**
     * Adds instance to the set of instances used to classify letters.
     * @param images - A list of images containing individual letters.
     * @throws Exception 
     */
    public static void addData(List<BufferedImage> images) throws Exception
    {        
        if (trained)
        {
            Instances trainingData = ImageListToInstances(images);
            knn.addInstances(trainingData);
        }
        else
        {
            trainClassifyer(images);
        }
    }
    
    /**
     * Saves the knn classifier to the users home directory. 
     */
    public static void save()
    {
        // Don't save it there isn't anything to save
        if (!trained)
            return;
        
        try {
            String savePath = System.getProperty("user.home") + File.separator + "learner.ser";
            FileOutputStream fout;
            fout = new FileOutputStream(savePath);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(knn);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Classifyer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Classifyer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loads the classifier from the users home directory. 
     */
    public static void load()
    {
        try {
            String savePath = System.getProperty("user.home") + File.separator + "learner.ser";
            FileInputStream fout;
            fout = new FileInputStream(savePath);
            ObjectInputStream oos = new ObjectInputStream(fout);
            knn = (KnnClassifier) oos.readObject();
            trained = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Classifyer.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
            Logger.getLogger(Classifyer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Classifyer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}