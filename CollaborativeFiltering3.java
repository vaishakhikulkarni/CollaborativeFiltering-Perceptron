/*
Vaishakhi Kulkarni
Net Id:vpk140230
*/

//package DAY1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollaborativeFiltering3 {

	// HashMap to give index to movie and user
	static HashMap<Integer, Integer> movieIndex = new HashMap<>();
	static HashMap<Integer, Integer> userIndex = new HashMap<>();

	// To store the counter
	static int moviecount = 0, usercount = 0;

	// Matrix to store the rating
	static double rating[][];

	// Average of rating
	static HashMap<Integer, Double> useravg = new HashMap<>();

	// static index
	static int uindex = 0, mindex = 0;

	// calculate average
	static double averageRating[];

	public static void main(String[] args) {
		// Folder Path
		// C:\Users\Vaishakhi\Documents\Vibhav\Assignment3
		String folderpath = args[0];

		// File path of training and test files
		File trainingpath = new File(folderpath + "/TrainingRatings.txt");
		File testpath = new File(folderpath + "/TestingRatings.txt");

		BufferedReader br = null;
		String line = null;
		String[] data;

		System.out.println("*****Collaborative Filtering*****");
		System.out.println();

		try {

			// Access List of files one after other from Ham folder
			br = new BufferedReader(new FileReader(trainingpath));
			while ((line = br.readLine()) != null) {
				data = line.split(",");

				if (!movieIndex.containsKey(Integer.parseInt(data[0]))) {
					movieIndex.put(Integer.parseInt(data[0]), mindex);
					mindex++;
				}
				if (!userIndex.containsKey(Integer.parseInt(data[1]))) {
					userIndex.put(Integer.parseInt(data[1]), uindex);
					uindex++;
				}
			}
			br.close();

			usercount = userIndex.size();
			moviecount = movieIndex.size();

			// create final array of user and movie
			rating = new double[usercount + 1][moviecount + 1];

			// store the rating
			br = new BufferedReader(new FileReader(trainingpath));
			while ((line = br.readLine()) != null) {
				data = line.split(",");
				rating[userIndex.get(Integer.parseInt(data[1]))][movieIndex
						.get(Integer.parseInt(data[0]))] = Double
						.parseDouble(data[2]);
			}
			br.close();

			// Average rating array
			averageRating = new double[usercount];
			double zero = 0.0;

			// Calculating average of individual
			for (int i = 0; i < usercount; i++) {
				double average = 0;
				int count = 0;
				for (int j = 0; j < moviecount; j++) {
					if (rating[i][j] != zero) {
						average = average + rating[i][j];
						count++;
					}
				}
				averageRating[i] = average / count;
			}

			// Test data calculation
			double absmean = 0;
			double rootabserr = 0;
			int count = 0;
			br = new BufferedReader(new FileReader(testpath));

			// Test data
			while ((line = br.readLine()) != null) {
				data = line.split(",");
				double w = calculateW(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]));

				double k = calculateK(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]));

				double pa = averageRating[userIndex.get(Integer
						.parseInt(data[1]))] + k * w;

				absmean = absmean + Math.abs(pa - Double.parseDouble(data[2]));

				rootabserr = rootabserr + (pa - Double.parseDouble(data[2]))
						* (pa - Double.parseDouble(data[2]));

				count++;
			}

			absmean = (double) (absmean / count) * 0.1;
			rootabserr = (double) Math.sqrt(rootabserr / count) * 0.1;

			System.out.println("Absolute Error Mean : " + absmean);
			System.out.println("Root Absolute Error Mean : " + rootabserr);

			br.close();

		} catch (FileNotFoundException e) // Handle File not Found error
		{
			System.out
					.println("Please make sure the directory file is actually there.");
		} catch (IOException ex) // Handle IO exception error
		{
			Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE,
					null, ex);
		} finally {
			try {
				br.close(); // Close BufferReader
			} catch (IOException ex) // IO Exception error
			{
				Logger.getLogger(BufferedReader.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}

	//Calculate K value
	public static double calculateK(int movie, int activeuser) {
		
		double partone = 0, partsecond = 0;
		double numerator = 0;
		double demopartone = 0, demopartsecond = 0;
		double denomenator = 0;
		double value = 0;
		double sum = 0;
		double finalsum = 0;

		for (int i = 0; i < usercount; i++) // Other User
		{
			for (int j = 0; j < moviecount; j++) // Movies
			{
				if (rating[userIndex.get(activeuser)][j] != 0
						&& rating[i][j] != 0 && i != userIndex.get(activeuser)) // Check
																				// condition
				{
					partone = rating[userIndex.get(activeuser)][j]
							- averageRating[userIndex.get(activeuser)];
					partsecond = rating[i][j] - averageRating[i];
					if (partone == 0)
						partone = averageRating[userIndex.get(activeuser)];
					numerator = numerator + partone * partsecond;
					demopartone = partone * partone;
					demopartsecond = partsecond * partsecond;
					denomenator = denomenator + demopartsecond * demopartone;
				}
			}
			value = (double) numerator / Math.sqrt(denomenator);
			sum = rating[i][movieIndex.get(movie)] - averageRating[i];
			finalsum = finalsum + Math.abs((double) value * sum);
		}		
		double abs = 0;
		abs = (double) 1 / finalsum;
		return abs;
	}

	//Calculate the sum 
	public static double calculateW(int movie, int activeuser) {
		double partone = 0, partsecond = 0;
		double numerator = 0;
		double demopartone = 0, demopartsecond = 0;
		double denomenator = 0;
		double value = 0;
		double sum = 0;
		double finalsum = 0;

		for (int i = 0; i < usercount; i++) // Other User
		{
			for (int j = 0; j < moviecount; j++) // Movies
			{
				if (rating[userIndex.get(activeuser)][j] != 0
						&& rating[i][j] != 0 && i != userIndex.get(activeuser)) // Check
																				// condition
				{
					partone = rating[userIndex.get(activeuser)][j]
							- averageRating[userIndex.get(activeuser)];
					partsecond = rating[i][j] - averageRating[i];
					if (partone == 0)
						partone = averageRating[userIndex.get(activeuser)];
					numerator = numerator + partone * partsecond;
					demopartone = partone * partone;
					demopartsecond = partsecond * partsecond;
					denomenator = denomenator + demopartsecond * demopartone;
				}
			}
			value = (double) numerator / Math.sqrt(denomenator);
			sum = rating[i][movieIndex.get(movie)] - averageRating[i];
			finalsum = finalsum + (double) value * sum;
		}

		return finalsum;
	}

}
