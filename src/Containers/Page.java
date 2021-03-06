/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Containers;

import static Reader.Segmenter.getTextRows;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Schuyler
 */
public class Page 
{
    private final List<Line> lines;
    
    /**
     * Takes the page and splits it into lines. Once it has been completely split
     * the page retrieves the sizes of each of the letters to calculate the mean
     * and standard deviation. These are used to guess which letters should be
     * split up.
     * @param input - An image of the entire page
     */
    public Page(BufferedImage input)
    {
        lines = new ArrayList<>();
        // Split the page into rows and make a line object for each one
        List<BufferedImage> tempLines = getTextRows(input);
        List<Integer> sizes = new ArrayList<>();
        for(BufferedImage line : tempLines)
        {
            Line newLine = new Line(line);
            lines.add(newLine);
            sizes.addAll(newLine.getLetterSizes());
        }
        
        // Get the size of each letter and find the mean and std deviation so
        // that we can guess which letters are merged together and attempt to
        // split them
        float sum = 0;
        float mean;
        float threshold;
        
        for(Integer gap : sizes)
        {
            sum += gap;
        }
        mean = sum / (float)sizes.size();
        sum = 0;
        
        for(Integer gap : sizes)
        {
            sum += Math.pow((gap - mean), 2);
        }
        
        threshold = (float) (mean + 1.0 * Math.sqrt(sum / (float)sizes.size()));
        
        for(Line line : lines)
        {
            line.checkLetters(threshold);
        }
    }
    
    /**
     * Adds up the number of letters from each line
     * @return The total number of letters on the page
     */
    public int getLetterCount()
    {
        int count = 0;
        for (Line line : lines)
        {
            count += line.getLetterCount();
        }
        return count;
    }
    
    /**
     * Collects all of the letters from each line and aggregates them into a list
     * @return A list of the letters stored as BufferedImages in a List
     */
    public List<BufferedImage> getLetters()
    {
        List<BufferedImage> letters = new ArrayList<>();
        
        for (Line line : lines)
        {
            letters.addAll(line.getLetters());
        }
        
        return letters;
    }
    
    /**
     * Collects the output of read from each line and adds a newline at the end
     * of each line.
     * @return The full text of the page as a string
     */
    public String read()
    {
        String text = "";
        
        for (Line line : lines)
        {
            text += line.read() + "\n";
        }
        
        return text;
    }
}
