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
 *
 * @author Schuyler
 */
public class Line 
{
    private final List<Word> words;
    
    public Line(BufferedImage input)
    {
        words = new ArrayList<>();
        List<BufferedImage> tempWords = Segmenter.getWords(input);
            
        for(BufferedImage word : tempWords)
        {
            words.add(new Word(word));
        }
    }
    
    public int getLetterCount()
    {
        int count = 0;
        
        for (Word word : words)
        {
            count += word.getLetterCount();
        }
        
        return count;
    }
    
    public List<Integer> getLetterSizes()
    {
        List<Integer> sizes = new ArrayList<>();
        
        for (Word word : words)
        {
            sizes.addAll(word.getLetterSizes());
        }
        
        return sizes;
    }
    
    public void checkLetters(float threshold)
    {
        for (Word word : words)
        {
            word.checkLetters(threshold);
        }
    }
    
    public List<BufferedImage> getLetters() {
        List<BufferedImage> letters = new ArrayList<>();
        
        for (Word word : words)
        {
            letters.addAll(word.getLetters());
        }
        
        return letters;
    }
    
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
