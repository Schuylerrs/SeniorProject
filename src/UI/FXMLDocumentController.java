/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import Containers.Page;
import Preprocess.Preprocessor;
import Preprocess.Thresholder;
import Reader.Classifyer;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;

/**
 *
 * @author Schuyler
 */
public class FXMLDocumentController implements Initializable 
{
    private BufferedImage srcImage = null;
    private BufferedImage processedImage = null;
    
    @FXML
    public ImageView imgBefore;
    public ImageView imgAfter;
    public TextArea txtPreview;
    public Slider sldNumRows;
    public Slider sldNoiseReduction;
    public CheckBox chkAutoChunk;
    public Label lblTime;
    public Label lblCount;
    
    @FXML
    private void clickOpen(ActionEvent event) 
    {
        try 
        {
            File srcFile = Prompt.getFile("Choose Image");
            srcImage = ImageIO.read(srcFile);
            imgBefore.setImage(new Image(srcFile.toURI().toString()));
        } 
        catch (Exception ex) 
        {
        }
    }
    
    @FXML
    private void clickPreprocess(ActionEvent event)
    {
        int numChunks = 1;
        if (!chkAutoChunk.isSelected())
        {
            numChunks = (int) sldNumRows.getValue();
        }
        else
        {
            numChunks = Preprocessor.guessChunks(srcImage);
            sldNumRows.setValue(numChunks);
        }
        
        int noiseLevel = (int) sldNoiseReduction.getValue();
        processedImage = Preprocessor.preprocess(srcImage, numChunks, noiseLevel);
        imgAfter.setImage(SwingFXUtils.toFXImage(processedImage, null));
    }
    
    @FXML
    private void clickSave(ActionEvent event)
    {   
        BufferedWriter writer = null;
        try
        {
            File destFile = Prompt.getFile("Choose Output File");
            String separator = System.getProperty("line.separator");
            String output = txtPreview.getText();
            writer = new BufferedWriter( new FileWriter(destFile));
            writer.write(output.replace("\n", separator));
        }
        catch ( IOException e)
        {
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
    }
    
    @FXML
    private void clickSaveImage(ActionEvent event)
    {
        try 
        {
            File saveImage = Prompt.getFile("Where do you want to save the image?");
            ImageIO.write(processedImage, "png", saveImage);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void clickAutoChunk(ActionEvent event)
    {
        sldNumRows.setDisable(!sldNumRows.disableProperty().get());
    }
    
    @FXML
    private void clickAndImage(ActionEvent event)
    {
        int noiseLevel = (int) sldNoiseReduction.getValue();
        
        BufferedImage temp1 = Preprocessor.preprocess(srcImage, 1, noiseLevel);
        processedImage = Thresholder.andImage(temp1, processedImage);
        temp1 = Preprocessor.preprocess(srcImage, 3, noiseLevel);
        processedImage = Thresholder.andImage(temp1, processedImage);
        temp1 = Preprocessor.preprocess(srcImage, 5, noiseLevel);
        processedImage = Thresholder.andImage(temp1, processedImage);
        temp1 = Preprocessor.preprocess(srcImage, 7, noiseLevel);
        processedImage = Thresholder.andImage(temp1, processedImage);
        temp1 = Preprocessor.preprocess(srcImage, 9, noiseLevel);
        processedImage = Thresholder.andImage(temp1, processedImage);
        temp1 = Preprocessor.preprocess(srcImage, 15, noiseLevel);
        processedImage = Thresholder.andImage(temp1, processedImage);
        imgAfter.setImage(SwingFXUtils.toFXImage(processedImage, null));
    }
    
    @FXML
    private void clickRead(ActionEvent event)
    {
        long startTime = System.currentTimeMillis();
        Page test = new Page(processedImage);
        txtPreview.setText(test.read());
        Long estimatedTime = (System.currentTimeMillis() - startTime) / 1000;
        Integer count = test.getLetterCount();
        
        lblTime.setText(estimatedTime.toString());
        lblCount.setText(count.toString());
    }
    
    @FXML
    private void clickTrain(ActionEvent event)
    {
        Page test = new Page(processedImage);
        try {
            Classifyer.trainClassifyer(test.getLetters());
            txtPreview.setText("Trained");
        } catch (Exception ex) {
            txtPreview.setText("Failed to train\n" + ex.toString());
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    @FXML
    private void clickAddTraining(ActionEvent event)
    {
        Page test = new Page(processedImage);
        try {
            Classifyer.addData(test.getLetters());
            txtPreview.setText("Training Added");
        } catch (Exception ex) {
            txtPreview.setText("Failed to add\n" + ex.toString());            
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @FXML
    private void clickSaveLearner(ActionEvent event)
    {
        Classifyer.save();
        txtPreview.setText("Classifyer Saved");
    }
    
    @FXML
    private void clickLoadLearner(ActionEvent event)
    {
        Classifyer.load();
        txtPreview.setText("Classifyer Loader");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
    }    
    
    
}
