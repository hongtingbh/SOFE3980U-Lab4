package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.*;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;

        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        int numClasses = 5;
        int[][] confusionMatrix = new int[numClasses][numClasses];
        double ce = 0.0;
        int totalSamples = 0;

        // Print first 10 rows
        int count = 0;
        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]) - 1; // 0-based index
            float[] y_predicted = new float[numClasses];
            for (int i = 0; i < numClasses; i++) {
                y_predicted[i] = Float.parseFloat(row[i + 1]);
            }

            // Find predicted class (argmax)
            int predicted = 0;
            float maxProb = y_predicted[0];
            for (int i = 1; i < numClasses; i++) {
                if (y_predicted[i] > maxProb) {
                    maxProb = y_predicted[i];
                    predicted = i;
                }
            }

            // Cross Entropy contribution
            ce += -Math.log(Math.max(1e-15, y_predicted[y_true]));

            // Confusion matrix update
            confusionMatrix[y_true][predicted]++;
            totalSamples++;

            // Print first 10
            if (count < 10) {
                System.out.print("True: " + (y_true + 1) + "\tPredicted: " + (predicted + 1) + "\tProbs: ");
                for (float p : y_predicted) {
                    System.out.printf("%.4f ", p);
                }
                System.out.println();
            }
            count++;
        }

        ce /= totalSamples;

        // Output results
        System.out.printf("\nCross-Entropy (CE): %.6f\n\n", ce);
        System.out.println("Confusion Matrix:");
        System.out.print("        ");
        for (int i = 1; i <= numClasses; i++) {
            System.out.printf("Pred%-4d", i);
        }
        System.out.println();
        for (int i = 0; i < numClasses; i++) {
            System.out.printf("True%-2d  ", i + 1);
            for (int j = 0; j < numClasses; j++) {
                System.out.printf("%-8d", confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}
