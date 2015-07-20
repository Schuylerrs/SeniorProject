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
 * A container for a word containing a list of letters that are in the word.
 * @author Schuyler
 */
class Word 
{
    private List<BufferedImage> letters;
    
    /**
     * Takes the image of the word and splits it into letters.
     * @param input - An image of the word this object represents
     */
    public Word(BufferedImage input)
    {
        letters = Segmenter.splitHorizontally(input, 0);
    }
    
    /**
     * Looks at each letter to check if they are larger than the threshold.
     * If they are then the program attempts to split them in half.
     * @param threshold - The size cutoff for when to try and split a letter.
     */
    public void checkLetters(float threshold)
    {
        // A new list so that split letters are not inserted out of order
        List<BufferedImage> newLetters = new ArrayList<>();
        for (BufferedImage letter : letters)
        {
            if (letter.getWidth() > threshold)
            {
                List<BufferedImage> temp = trySplit(letter);
                // If the there is more than one letter coming out of try split
                if (temp.size() > 1)
                {
                    // Remove the old "letter" so that there isn't duplication
                    newLetters.remove(letter);
                    // Add each of the newly found letters into the collection
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
        
        // Replace our old collection of letters with our new one
        letters = newLetters;
        
        // Trim the letters to remove any extra white space caused by the splitting
        for(BufferedImage letter : letters)
        {            
            letter = trimLetter(letter);
        }
    }
    
    /**
     * Gets the number of letters in the word
     * @return The number of letters in the word
     */
    public int getLetterCount()
    {
        return letters.size();
    }
    
    /**
     * Trims any white space off of the edge of the image
     * @param letter - The image to trim off the white space
     * @return The trimmed image
     */
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
     /**
      * Checks if a horizontal line contains black pixels
      * @param input - The image to check
      * @param y - The line to check on
      * @return True => There is a black pixel; False => The line is all white
      */
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
    
    /**
     * An image that possible contains merged letters
     * @param input - Image of the letter(s)
     * @return A list of the letter(s)
     */
    public List<BufferedImage> trySplit(BufferedImage input)
    {        
        List<BufferedImage> temp = new ArrayList<>();
        
        // Rotates the image to the slant that the italics are generally at
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

    /**
     * Returns the width of each of the letters
     * @return A list of ints representing the width of the letters
     */
    public List<Integer> getLetterSizes()
    {
        List<Integer> sizes = new ArrayList<>();
        for(BufferedImage letter : letters)
        {
            sizes.add(letter.getWidth());
        }
        
        return sizes;
    }
    
    /**
     * Gets the collection of letters from the word
     * @return All of the letters in a list
     */
    public List<BufferedImage> getLetters()
    {
        return letters;
    }
   
    /**
     * Attempts to read the word
     * @return A string representing what the word says
     */
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
