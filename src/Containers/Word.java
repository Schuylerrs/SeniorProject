/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Containers;

import Preprocess.Rotator;
import Reader.Classifyer;
import Reader.Segmenter;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Schuyler
 */
class Word 
{
    private List<BufferedImage> letters;
    
    public Word(BufferedImage input)
    {
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
        
        for(BufferedImage letter : letters)
        {            
            letter = trimLetter(letter);
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
        List<BufferedImage> temp = new ArrayList<>();
        
        BufferedImage italic = Rotator.rotateImage(input, -6);
        Segmenter.trimEdge(italic);
        List<BufferedImage> split = Segmenter.splitHorizontally(italic, 0);
        
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
