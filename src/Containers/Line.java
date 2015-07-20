/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Containers;

import Reader.Segmenter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * A container for a line of text containing a collection of words.
 * @author Schuyler
 */
public class Line 
{
    private final List<Word> words;
    
    /**
     * Splits the line of text into words
     * @param input - An image of an individual line
     */
    public Line(BufferedImage input)
    {
        words = new ArrayList<>();
        List<BufferedImage> tempWords = Segmenter.getWords(input);
            
        for(BufferedImage word : tempWords)
        {
            words.add(new Word(word));
        }
    }
    
    /**
     * Adds together the number of letters in each word
     * @return The number of letters in the line 
     */
    public int getLetterCount()
    {
        int count = 0;
        
        for (Word word : words)
        {
            count += word.getLetterCount();
        }
        
        return count;
    }
    
    /**
     * Gets the size of each all of the letters in the line
     * @return A list of the different sizes of letters
     */
    public List<Integer> getLetterSizes()
    {
        List<Integer> sizes = new ArrayList<>();
        
        for (Word word : words)
        {
            sizes.addAll(word.getLetterSizes());
        }
        
        return sizes;
    }
    
    /**
     * Checks each of the words for letters that look like they are merged
     * @param threshold - The size used to tell is a letter is two letters merged
     */
    public void checkLetters(float threshold)
    {
        for (Word word : words)
        {
            word.checkLetters(threshold);
        }
    }
    
    /**
     * Gets all of the letters in the line
     * @return A list of all of the letters
     */
    public List<BufferedImage> getLetters() {
        List<BufferedImage> letters = new ArrayList<>();
        
        for (Word word : words)
        {
            letters.addAll(word.getLetters());
        }
        
        return letters;
    }
    
    /**
     * Combines the words with spaces between them
     * @return The full text from all of the lines
     */
    public String read()
    {
        String text = "";
        
        for (Word word : words)
        {
            text += word.read() + " ";
        }
        
        return text;
    }
}
