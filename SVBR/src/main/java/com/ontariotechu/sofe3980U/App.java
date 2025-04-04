package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.*;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        String[] fileNames = {"model_1.csv", "model_2.csv", "model_3.csv"};

        for (String filePath : fileNames) {
            List<String[]> allData;

            try {
                FileReader filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }

            // BCE
            double bce = 0.0;

            // Confusion matrix variables
            int TP = 0, TN = 0, FP = 0, FN = 0;

            // ROC arrays
            int n = allData.size();
            double[] y_true = new double[n];
            double[] y_pred = new double[n];

            int index = 0;
            for (String[] row : allData) {
                int trueLabel = Integer.parseInt(row[0]);
                double predicted = Double.parseDouble(row[1]);

                y_true[index] = trueLabel;
                y_pred[index] = predicted;
                index++;

                // BCE computation
                predicted = Math.max(1e-15, Math.min(1 - 1e-15, predicted)); // avoid log(0)
                bce += trueLabel * Math.log(predicted) + (1 - trueLabel) * Math.log(1 - predicted);

                // Threshold = 0.5
                int predictedClass = (predicted >= 0.5) ? 1 : 0;

                if (trueLabel == 1 && predictedClass == 1) TP++;
                else if (trueLabel == 0 && predictedClass == 0) TN++;
                else if (trueLabel == 0 && predictedClass == 1) FP++;
                else if (trueLabel == 1 && predictedClass == 0) FN++;
            }

            bce = -bce / n;
            double accuracy = (TP + TN) / (double)(TP + TN + FP + FN);
            double precision = (TP + FP) == 0 ? 0 : TP / (double)(TP + FP);
            double recall = (TP + FN) == 0 ? 0 : TP / (double)(TP + FN);
            double f1 = (precision + recall) == 0 ? 0 : 2 * precision * recall / (precision + recall);

            // AUC-ROC computation
            double[] rocX = new double[101];
            double[] rocY = new double[101];
            int n_positive = 0, n_negative = 0;

            for (int i = 0; i < n; i++) {
                if (y_true[i] == 1) n_positive++;
                else n_negative++;
            }

            for (int t = 0; t <= 100; t++) {
                double threshold = t / 100.0;
                int tTP = 0, tFP = 0;

                for (int i = 0; i < n; i++) {
                    int predClass = (y_pred[i] >= threshold) ? 1 : 0;
                    if (y_true[i] == 1 && predClass == 1) tTP++;
                    if (y_true[i] == 0 && predClass == 1) tFP++;
                }

                rocY[t] = (n_positive == 0) ? 0 : (double)tTP / n_positive; // TPR
                rocX[t] = (n_negative == 0) ? 0 : (double)tFP / n_negative; // FPR
            }

            double auc = 0.0;
            for (int i = 1; i <= 100; i++) {
                auc += (rocY[i - 1] + rocY[i]) * Math.abs(rocX[i - 1] - rocX[i]) / 2.0;
            }

            // Output
            System.out.println("Results for: " + filePath);
            System.out.printf("BCE: %.6f\n", bce);
            System.out.println("Confusion Matrix:");
            System.out.println("TP: " + TP + ", TN: " + TN + ", FP: " + FP + ", FN: " + FN);
            System.out.printf("Accuracy: %.4f\n", accuracy);
            System.out.printf("Precision: %.4f\n", precision);
            System.out.printf("Recall: %.4f\n", recall);
            System.out.printf("F1 Score: %.4f\n", f1);
            System.out.printf("AUC-ROC: %.6f\n", auc);
            System.out.println("-------------------------");
        }
    }
}
