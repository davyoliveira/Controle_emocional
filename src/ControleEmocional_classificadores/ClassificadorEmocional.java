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
import ControleEmocional_classes.EmocaoRisco;
import ControleEmocional_interfaces.JFrameMedicao_1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.TwoClassStats;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
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
    
    public static EmocaoRisco begin() throws Exception {
        
        EmocaoRisco retorno = new EmocaoRisco();
        retorno.setEmocao("Modelo treinado com sucesso!");
        retorno.setRisco("");
        String linha = null;
        FileReader arq;
        File file;
        try {
            arq = new FileReader("src/Data/UsuarioAtual.txt");
            BufferedReader lerArq = new BufferedReader(arq);
            linha = lerArq.readLine();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFrameMedicao_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JFrameMedicao_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        file = new File("src/ControleEmocional_Data_weka/" + linha + ".model");
        
        BufferedReader datafile = readDataFile("src/ControleEmocional_Data_weka/" + linha + "dados.arff");
        //ObjectInputStream ois = readModelDataFile("src/data/classificador1.model");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);

        // Do 10-split cross validation
        //Instances[][] split = crossValidationSplit(data, 10);
        if (!file.exists()) {
            Classifier models = new RandomForest();
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(models, data, 10, new Random(1));

            //System.out.println("Estimated Accuracy: " + Double.toString(eval.pctCorrect()));
            //Classifier models2 = new J48();
            models.buildClassifier(data);

            //eval.evaluateModel(models, data);
            System.out.println("Estimated Accuracy: " + Double.toString(eval.pctCorrect()));
            System.out.println("Fmeasure : " + String.format("%.2f%%", 100 * eval.fMeasure(0)));
            
            try ( //salva o algoritmo classificador em um arquivo na pasta raiz do nosso programa
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/ControleEmocional_Data_weka/" + linha + ".model"))) {
                oos.writeObject(models);
            }
            JOptionPane.showMessageDialog(null, "Dados treinados com sucesso! Agora nosso sistema aprendeu sobre seus batimentos e pode dizer qual emoção eles representam!");
            
        } else {            
            
            ObjectInputStream ois = readModelDataFile("src/ControleEmocional_Data_weka/" + linha + ".model");
            
            Classifier RandomForest = (Classifier) ois.readObject();
            
            Evaluation eval2 = new Evaluation(data);
            eval2.evaluateModel(RandomForest, data);
            //System.out.println("Fmeasure 2: "+String.format("%.2f%%",100*eval2.fMeasure(0)));
            //eval2.crossValidateModel(J48, data, 10, new Random(1));
            int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
            int count = 0, tnuoc = 0;
            
            for (int i = 0; i < data.numInstances(); i++) {
                Instance instancia = data.instance(i);
                int classe = (int) RandomForest.classifyInstance(instancia);
                //pega o nome do valor correspondente a classe classificada e colocqa em predClasse
                String aux = data.get(i) + "";
                String[] batimento = aux.split(",");
                int numero = Integer.parseInt(batimento[0]);
                if (numero > 83) {
                    count++;
                } else {
                    tnuoc++;
                }
                Attribute atribut = data.attribute(data.numAttributes() - 1);
                
                String predClasse = atribut.value((int) classe);
                //System.out.println("predClasse: " + predClasse);
                if (predClasse.equals("Alegria")) {
                    a++;
                }
                if (predClasse.equals("Desgosto")) {
                    b++;
                }
                if (predClasse.equals("Medo")) {
                    c++;
                }
                if (predClasse.equals("Raiva")) {
                    d++;
                }
                if (predClasse.equals("Surpresa")) {
                    e++;
                }
                if (predClasse.equals("Tristeza")) {
                    f++;
                }
            }
            
            if (a > b && a > c && a > d && a > e && a > f) {
                retorno.setEmocao("Alegria");
            } else if (b > a && b > c && b > d && b > e && b > f) {
                retorno.setEmocao("Desgosto");
            } else if (c > b && c > a && c > d && c > e && c > f) {
                retorno.setEmocao("Medo");
            } else if (d > b && d > c && d > a && d > e && d > f) {
                retorno.setEmocao("Raiva");
            } else if (e > b && e > c && e > d && e > a && e > f) {
                retorno.setEmocao("Surpresa");
            } else if (f > b && f > c && f > d && f > e && f > a) {
                retorno.setEmocao("Tristeza");
            } else {
                retorno.setEmocao("Duas ou mais emoções foram classificadas igualmente!");
            }
            
            if (count > tnuoc) {
                retorno.setRisco("Seu nível de batimentos por minuto está alto!");
            } else {
                retorno.setRisco("Seu nível de batimentos por minuto está bom!");
            }
        }
        return retorno;
    }
}
