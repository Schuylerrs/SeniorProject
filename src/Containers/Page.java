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
    private List<Line> lines;
    
    public Page(BufferedImage input)
    {
        lines = new ArrayList<>();
        List<BufferedImage> tempLines = getTextRows(input);
        List<Integer> sizes = new ArrayList<>();
        for(BufferedImage line : tempLines)
        {
            Line newLine = new Line(line);
            lines.add(newLine);
            sizes.addAll(newLine.getLetterSizes());
        }
        
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
    
    public void addLine(Line word) 
    {
        lines.add(word);
    }
    
    public int getLetterCount()
    {
        int count = 0;
        for (Line line : lines)
        {
            count += line.getLetterCount();
        }
        return count;
    }
    
    public List<BufferedImage> getLetters()
    {
        List<BufferedImage> letters = new ArrayList<>();
        
        for (Line line : lines)
        {
            letters.addAll(line.getLetters());
        }
        
        return letters;
    }
        
    public Line getLine(int index)
    {
        return lines.get(index);
    }
    
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
