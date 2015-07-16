/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Containers;

import Reader.Segmenter;
import java.awt.image.BufferedImage;
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
public class Line 
{
    private final List<Word> words;
    public static int num = 0;
    public int thisNum;
    
    public Line(BufferedImage input)
    {
        words = new ArrayList<>();
        List<BufferedImage> tempWords = Segmenter.getWords(input);
        
            num++;
            thisNum = num;
            File outputfile = new File("C:\\Users\\Schuyler\\Pictures\\Rows\\row" + thisNum + ".png");
            try 
            {
                ImageIO.write(input, "png", outputfile);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Segmenter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        int temp = 0;
        for(BufferedImage word : tempWords)
        {
            words.add(new Word(word, thisNum, temp));
            
            
            File outputWordfile = new File("C:\\Users\\Schuyler\\Pictures\\Rows\\row" + thisNum + "word" + temp++ + ".png");
            try 
            {
                ImageIO.write(word, "png", outputWordfile);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Segmenter.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    public void addWord(Word word) 
    {
        words.add(word);
    }
    
    public Word getWord(int index)
    {
        return words.get(index);
    }

    public List<Word> getWords() {
        return words;
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
