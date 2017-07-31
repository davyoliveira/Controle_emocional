/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ControleEmocional_classificadores;

/**
 *
 * @author Davy
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.TwoClassStats;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificadorEmocional {

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    public static ObjectInputStream readModelDataFile(String filename) throws IOException {
        ObjectInputStream ois = null;

        try {
            //inputReader = new BufferedReader(new FileReader(filename));
            ois = new ObjectInputStream(new FileInputStream(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return ois;
    }

    public static Evaluation classify(Classifier model,
            Instances trainingSet, Instances testingSet) throws Exception {

        Evaluation evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);

        try ( //salva o algoritmo classificador em um arquivo na pasta raiz do nosso programa
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/ControleEmocional_Data_weka/classificador1.model"))) {
            oos.writeObject(model);
        }

        evaluation.evaluateModel(model, testingSet);

        return evaluation;
    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }
    
    public static double calculateFmeasure(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    public static void main(String[] args) throws Exception {
        
        Boolean firstTime = false;
        BufferedReader datafile = readDataFile("src/ControleEmocional_Data_weka/iris.arff");
        //ObjectInputStream ois = readModelDataFile("src/data/classificador1.model");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);

        // Do 10-split cross validation
        //Instances[][] split = crossValidationSplit(data, 10);
        if (firstTime) {
            Classifier models = new J48();
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(models, data, 10, new Random(1));

            //System.out.println("Estimated Accuracy: " + Double.toString(eval.pctCorrect()));
            
            //Classifier models2 = new J48();
            models.buildClassifier(data);
            
            //eval.evaluateModel(models, data);
            System.out.println("Estimated Accuracy: " + Double.toString(eval.pctCorrect()));
            System.out.println("Fmeasure : "+String.format("%.2f%%",100*eval.fMeasure(0)));
            
            try ( //salva o algoritmo classificador em um arquivo na pasta raiz do nosso programa
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/ControleEmocional_Data_weka/classificador1.model"))) {
            oos.writeObject(models);
            }
        }else{            
            
            ObjectInputStream ois = readModelDataFile("src/ControleEmocional_Data_weka/classificador1.model");
            
            Classifier J48 = (Classifier) ois.readObject();
            
            Evaluation eval2 = new Evaluation(data);
            eval2.evaluateModel(J48, data);
            //System.out.println("Fmeasure 2: "+String.format("%.2f%%",100*eval2.fMeasure(0)));
            //eval2.crossValidateModel(J48, data, 10, new Random(1));
            
            for (int i = 0; i < data.numInstances(); i++) {
                Instance instancia = data.instance(i);
                int classe = (int) J48.classifyInstance(instancia);
                //pega o nome do valor correspondente a classe classificada e colocqa em predClasse
                Attribute a = data.attribute(data.numAttributes() - 1);

                String predClasse = a.value((int) classe);
                System.out.println("predClasse: " + predClasse);
            }
            
            //System.out.println("Estimated Accuracy 2: " + Double.toString(eval2.pctCorrect()));
            //System.out.println("Fmeasure 2: "+String.format("%.2f%%",100*eval2.fMeasure(0)));
        }
    }
}
