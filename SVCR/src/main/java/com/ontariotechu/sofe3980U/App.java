package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main( String[] args )
    {	String[] fileNames = {"model_1.csv", "model_2.csv", "model_3.csv"};
		float lowestMSE = Float.MAX_VALUE;
		float lowestMAE = Float.MAX_VALUE;
		float lowestMARE = Float.MAX_VALUE;
		String lowestMSE_CSV = "";
		String lowestMAE_CSV = "";
		String lowestMARE_CSV = "";

		for (int i = 0; i < 3; i++) {
			FileReader filereader;
			List<String[]> allData;
			try{
				filereader = new FileReader(fileNames[i]); 
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
				allData = csvReader.readAll();
			}
			catch(Exception e){
				System.out.println( "Error reading the CSV file" );
				return;
			}
			
			int count=0;
			float error = 0;
			float errorSQ = 0;
			float errorABS = 0;
			float errorMARE = 0;
			for (String[] row : allData) { 
				float y_true=Float.parseFloat(row[0]);
				float y_predicted=Float.parseFloat(row[1]);
				error = y_true - y_predicted;
				errorSQ += Math.pow(error, 2);
				errorABS += Math.abs(error);
				errorMARE += Math.abs(error)/(Math.abs(y_true));
				// System.out.print(y_true + "  \t  "+y_predicted); 
				// System.out.println(); 
				count++;
				if (count==10000){
					break;
				}
			}
			float mse = errorSQ/count;
			float mae = errorABS/count;
			float mare = errorMARE/count;
			if (mse < lowestMSE) {
				lowestMSE = mse;
				lowestMSE_CSV = fileNames[i];
			}
			if (mae < lowestMAE) {
				lowestMAE = mae;
				lowestMAE_CSV = fileNames[i];
			}
			if (mare < lowestMARE) {
				lowestMARE = mare;
				lowestMARE_CSV = fileNames[i];
			}

			System.out.println("For " + fileNames[i] +": \nMSE: " + mse + "\nMAE: " + mae + "\nMARE:" +
			mare);
		}
		// A model becomes better with the decrease of the MSE, MAE, or MARE values.
		System.out.println("According to the lowest MSE: " + lowestMSE_CSV);
		System.out.println("According to the lowest MAE: " + lowestMAE_CSV);
		System.out.println("According to the lowest MARE: " + lowestMARE_CSV);
		
    }
}
