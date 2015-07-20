/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reader;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Schuyler
 */
public class KnnClassifier extends Classifier implements Serializable
{
    private Instances trainingData;
    
    /**
     * Builds the classifier based on an initial training set
     * @param data - Data used for training
     * @throws Exception
     */
    @Override
    public void buildClassifier(Instances data) throws Exception 
    {
        trainingData = data;
    }
    
    /**
     * A default classify that calls the other classifyer with the k value of 3
     * @param instance - The instance to classify
     * @return A double value representing the letter in the image
     */
    @Override
    public double classifyInstance(Instance instance)
    {
        return this.classifyInstance(instance, 3);
    }
    
    /**
     * Looks at the k closest known instances to try and guess which letter is in
     * an image.
     * @param instance - The instance to classify
     * @param k - The number of neighbors to look at when determining the class
     * @return A double value representing the letter in the image
     */
    public double classifyInstance(Instance instance, int k)
    {
        int size = trainingData.numInstances();
        int attributes = trainingData.numAttributes() - 1;
        float dist;
        Map<Float, Instance> neighbors = new TreeMap<>();
        
        Instance test;

        for (int i = 0; i < size; i++)
        {
            dist = 0;
            test = trainingData.instance(i);
            
            for (int j = 0; j < attributes; j++)
            {   
                dist += Math.abs(test.value(test.attribute(j)) - instance.value(test.attribute(j)));
            }

            neighbors.put(dist, test);
        }
        
        return findMostCommon(neighbors, k);
    }
    
    /**
     * Find the most common class from a list of neighbors
     * @param neighbors - A list of neighbors and distances
     * @param k - The number of neighbors to look at in order to guess
     * @return The most common class among the k nearest neighbors
     */
    private double findMostCommon(Map<Float, Instance> neighbors, int k)
    {
        int count = 0;
        int[] classCount = new int[80];
        double classGuess = -1;
        int classSize = 0;
        int index = trainingData.firstInstance().classIndex();
        
        for (Map.Entry<Float,Instance> entry : neighbors.entrySet()) 
        {
            if (count == k) 
                break;

            classCount[(int)entry.getValue().value(index)]++;
            
            if (classCount[(int)entry.getValue().value(index)] > classSize)
            {
                classGuess = entry.getValue().value(index);
                classSize = classCount[(int)entry.getValue().value(index)];
            }

            count++;
        }
        
        return classGuess;
    }

    /**
     * Adds new instances to the training data
     * @param newData - The new instances to add to the training data
     */
    public void addInstances(Instances newData)
    {
        for (int i = 0; i < newData.numInstances(); i++) 
        {
            Instance nInst = newData.instance(i);
            trainingData.add(nInst);
        }
    }
}
