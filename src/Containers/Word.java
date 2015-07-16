/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Containers;

import Preprocess.Rotator;
import Reader.Classifyer;
import Reader.Segmenter;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Schuyler
 */
class Word 
{
    private List<BufferedImage> letters;
    private final int lineNum;
    private final int wordNum;
    private static int splits = 0;
    
    public Word(BufferedImage input, int lineNum, int wordNum)
    {
        this.lineNum = lineNum;
        this.wordNum = wordNum;
        letters = Segmenter.splitHorizontally(input, 0);
    }
    
    public void checkLetters(float threshold)
    {
        List<BufferedImage> newLetters = new ArrayList<>();
        for (BufferedImage letter : letters)
        {
            if (letter.getWidth() > threshold)
            {
                List<BufferedImage> temp = trySplit(letter);
                if (temp.size() > 1)
                {
                    newLetters.remove(letter);
                    for (BufferedImage splitLetter : temp)
                    {
                        if (splitLetter.getWidth() > 5)
                            newLetters.add(splitLetter);                    
                    }
                }
                else
                {
                    newLetters.add(letter);
                }
            }
            else
            {
                newLetters.add(letter);
            }
        }
        
        letters = newLetters;
        
        int tempNum = 0;
        for(BufferedImage letter : letters)
        {            
            letter = trimLetter(letter);
//            File outputWordfile = new File("C:\\Users\\Schuyler\\Pictures\\Rows\\row" + lineNum + "word" + wordNum + "letter" + tempNum++ + ".png");
//            
//            try 
//            {
//                ImageIO.write(letter, "png", outputWordfile);            
//            } 
//            catch (Exception ex) 
//            {
//                Logger.getLogger(Segmenter.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }
    
    public int getLetterCount()
    {
        return letters.size();
    }
    
    public BufferedImage trimLetter(BufferedImage letter)
    {
        int height = letter.getHeight();
        int newHeight = height;
        int topMargin = 0;
        WritableRaster raster = letter.getRaster();
        
        for (int y = 0; y < height && isText(raster, y) != true; y++)
        {
            topMargin++;
            newHeight--;
        }
        
        for (int y  = height - 1; y > topMargin && isText(raster, y) != true; y--)
        {
            newHeight--;
        }
        
        if (newHeight < 5)
        {
            System.out.println("here is a small letter");
            return letter;
        }
        
        return letter.getSubimage(0, topMargin, letter.getWidth(), newHeight);
    }
    
    private boolean isText(WritableRaster input, int y)
    {
        int threshold = 1;
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
    
    public List<BufferedImage> trySplit(BufferedImage input)
    {
        File outputWordfile = new File("C:\\Users\\Schuyler\\Pictures\\Rows\\triedToSplit" + splits++ + ".png");
        try 
        {
            ImageIO.write(input, "png", outputWordfile);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Segmenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<BufferedImage> temp = new ArrayList<>();
        
        BufferedImage italic = Rotator.rotateImage(input, -6);
        Segmenter.trimEdge(italic);
        List<BufferedImage> split = Segmenter.splitHorizontally(italic, 0);
        
//        outputWordfile = new File("C:\\Users\\Schuyler\\Pictures\\Rows\\triedToSplit" + splits++ + "rotated.png");
//        try 
//        {
//            ImageIO.write(italic, "png", outputWordfile);
//        } 
//        catch (IOException ex) 
//        {
//            Logger.getLogger(Segmenter.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        // TODO: Make another attempt to split the letters
        if (split.size() > 1)
        {
            for (BufferedImage letter : split)
            {
                temp.add(Rotator.rotateImage(letter, 6));
            }
        }
        
        return temp;
    }
    
    public List<Integer> getLetterSizes()
    {
        List<Integer> sizes = new ArrayList<>();
        for(BufferedImage letter : letters)
        {
            sizes.add(letter.getWidth());
        }
        
        return sizes;
    }
    
    public void addLetter(BufferedImage letter)
    {
        letters.add(letter);
    }
    
    public List<BufferedImage> getLetters()
    {
        return letters;
    }
    
    public void getLetter(int index)
    {
        letters.get(index);
    }
    
    public String read()
    {
        String text = "";
        
        for (BufferedImage letter : letters)
        {
            text += Classifyer.clasifyImage(letter);
        }
        
        return text;
    }
}
