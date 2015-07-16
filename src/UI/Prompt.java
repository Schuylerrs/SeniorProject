/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.io.File;
import javafx.stage.FileChooser;

/**
 * Encapsulates the process of prompting the user to select a directory or file
 * @author Schuyler
 */
public class Prompt 
{
    private static final FileChooser fChooser;
    
    static
    {
        fChooser = new FileChooser();
    }
    
    /**
     * Prompts the user to select a file
     * 
     * @param pTitle - Title to be displayed on the prompt window
     * @return Users selected file
     * @throws Exception if the user clicks cancel or enter without choosing anything
     */
    public static File getFile(String pTitle) 
            throws Exception
    {
        fChooser.setTitle(pTitle);
        File dir = fChooser.showOpenDialog(null);
        
        if (null == dir)
        {
            throw new Exception("No File Chosen");
        }
        else
        {
            fChooser.setInitialDirectory(dir.getParentFile());
        }
        return dir;
    }
}
