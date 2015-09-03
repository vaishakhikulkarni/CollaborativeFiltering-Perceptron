//package DAY1;

/*
Vaishakhi kulkarni
Net Id:vpk140230*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PerceptronFinal {

	// HashMap to store distinct words along with index value for each word
	static HashMap<String, Integer> distinctWords = new HashMap<String, Integer>();
	static HashMap<String, Integer> distinctWordsFilter = new HashMap<String, Integer>();

	// Store Spam and Ham count
	static int hamCounts = 0, spamCounts = 0;

	// Data taken for user for calculations
	static double Eta;
	static int iterations;

	// To store Stopwords
	static HashSet<String> stopWords = new HashSet<String>();

	// To ignore special characters
	static HashSet<String> specialcharacters = new HashSet<String>();

	// To store the weight for calculations
	static double[] weight;
	static double[] weightFilter;

	static int index = 1;
	static int indexfilter = 1;

	// Whole matrix to store data
	static int[][] wordCount;
	static int[][] wordCountFilter;

	// Count of Ham and Spam files
	static int hamfiles = 0, spamfiles = 0;

	public static HashSet<String> addspecialcharacters(
			HashSet<String> characters) {
		specialcharacters.add("!");
		specialcharacters.add("_");
		specialcharacters.add("#");
		specialcharacters.add("$");
		specialcharacters.add("%");
		specialcharacters.add("^");
		specialcharacters.add("&");
		specialcharacters.add("*");
		specialcharacters.add("(");
		specialcharacters.add(")");
		specialcharacters.add("_");
		specialcharacters.add("-");
		specialcharacters.add("=");
		specialcharacters.add("+");
		specialcharacters.add("<");
		specialcharacters.add(">");
		specialcharacters.add("?");
		specialcharacters.add("/");
		specialcharacters.add("[");
		specialcharacters.add("]");
		specialcharacters.add("{");
		specialcharacters.add("}");
		specialcharacters.add("|");
		specialcharacters.add("\"");
		specialcharacters.add("  ");
		specialcharacters.add(" ");
		return characters;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Main folder path from command line -
		// C:\Users\Vaishakhi\Documents\Vibhav\Assignment3\old\
		String folderpath = args[0];

		iterations = Integer.parseInt(args[1]);
		Eta = Double.parseDouble(args[2]);

		// Store special characters in specialcharacters hashmap
		specialcharacters = addspecialcharacters(specialcharacters);

		// File path of Ham and Spam folders in training set
		File hampath = new File(folderpath + "/train/ham");
		File spampath = new File(folderpath + "/train/spam");

		// List of Ham and Spam files in training set
		File[] listofHamFiles = hampath.listFiles();
		File[] listofSpamFiles = spampath.listFiles();

		// File path of Ham and Spam folders in training set
		File hamtestpath = new File(folderpath + "/test/ham");
		File spamtestpath = new File(folderpath + "/test/spam");

		// List of ham and spam test files in test set
		File[] listofHamtestFiles = hamtestpath.listFiles();
		File[] listofSpamtestFiles = spamtestpath.listFiles();

		// To store the count of training set file
		hamfiles = listofHamFiles.length;
		spamfiles = listofSpamFiles.length;

		BufferedReader br = null;
		String line = null;
		String[] data;

		System.out.println("*****Perceptron*****");
		System.out.println();

		try {
			br = null;
			String line2 = null;
			File stopwords = new File(folderpath + "/train/stopWords.txt");
			br = new BufferedReader(new FileReader(stopwords));

			while ((line2 = br.readLine()) != null) {
				stopWords.add(line2);
			}

			// Access List of files one after other from Ham folder
			for (File file : listofHamFiles) {
				hamCounts++;
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
					data = line.split(" ");
					for (String token : data) {
						if (!specialcharacters.contains(token)
								&& !token.isEmpty()) {
							if (!distinctWords.containsKey(token.toLowerCase())) {
								distinctWords.put(token.toLowerCase(), index++);
							}
							if (!stopWords.contains(token.toLowerCase())) {
								if (!distinctWordsFilter.containsKey(token
										.toLowerCase())) {
									distinctWordsFilter.put(
											token.toLowerCase(), indexfilter++);
								}
							}
						}
					}
				}
			}

			// Access List of files one after other from Spam folder
			for (File file1 : listofSpamFiles) {
				spamCounts++;
				br = new BufferedReader(new FileReader(file1));
				while ((line = br.readLine()) != null) {
					data = line.split(" ");
					for (String token : data) {
						if (!specialcharacters.contains(token)
								&& !token.isEmpty()) {
							if (!distinctWords.containsKey(token.toLowerCase())) {
								distinctWords.put(token.toLowerCase(), index++);
							}
							if (!stopWords.contains(token.toLowerCase())) {
								if (!distinctWordsFilter.containsKey(token
										.toLowerCase())) {
									distinctWordsFilter.put(
											token.toLowerCase(), indexfilter++);
								}
							}
						}
					}
				}
			}

			br.close();

			// Matrix Population
			wordCount = new int[hamfiles + spamfiles][distinctWords.size() + 1];
			wordCountFilter = new int[hamfiles + spamfiles][distinctWordsFilter
					.size() + 1];

			int fileindex = 0;
			// Construct Matrix
			// Ham Files
			for (File file : listofHamFiles) {
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
					String[] datahm = line.split(" ");
					for (String token : datahm) {
						if (!specialcharacters.contains(token)
								&& !token.isEmpty()
								&& distinctWords.containsKey(token
										.toLowerCase())) {
							wordCount[fileindex][distinctWords.get(
									token.toLowerCase()).intValue()] = wordCount[fileindex][distinctWords
									.get(token.toLowerCase()).intValue()] + 1;
						}
						if (!specialcharacters.contains(token)
								&& !token.isEmpty()
								&& distinctWordsFilter.containsKey(token
										.toLowerCase())) {
							wordCountFilter[fileindex][distinctWordsFilter.get(
									token.toLowerCase()).intValue()] = wordCountFilter[fileindex][distinctWordsFilter
									.get(token.toLowerCase()).intValue()] + 1;
						}
					}
				}
				fileindex++;
			}

			// Spam Files
			for (File file : listofSpamFiles) {
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
					String[] datahm = line.split(" ");
					for (String token : datahm) {
						if (!specialcharacters.contains(token)
								&& !token.isEmpty()
								&& distinctWords.containsKey(token
										.toLowerCase())) {
							wordCount[fileindex][distinctWords.get(
									token.toLowerCase()).intValue()] = wordCount[fileindex][distinctWords
									.get(token.toLowerCase()).intValue()] + 1;
						}
						if (!specialcharacters.contains(token)
								&& !token.isEmpty()
								&& distinctWordsFilter.containsKey(token
										.toLowerCase())) {
							wordCountFilter[fileindex][distinctWordsFilter.get(
									token.toLowerCase()).intValue()] = wordCountFilter[fileindex][distinctWordsFilter
									.get(token.toLowerCase()).intValue()] + 1;
						}
					}
				}
				fileindex++;
			}

			br.close();

			// Random value weight
			Random random = new Random();
			weight = new double[distinctWords.size() + 1];
			weightFilter = new double[distinctWordsFilter.size() + 1];
			double initialvalue = 0 + (1 - 0) * random.nextDouble();

			// Initialize the weights
			for (int i = 0; i < weight.length; i++) {
				weight[i] = initialvalue;
			}

			//Initialize the stopword weights
			for (int i = 0; i < weightFilter.length; i++) {
				weightFilter[i] = initialvalue;
			}
			
			// Calculate the weight and store in temp
			double[] tempWeight;
			double[] tempWeightFilter;

			// Calculate weight for every iterations

			for (int i = 0; i < iterations; i++) {
				tempWeight = new double[distinctWords.size() + 1];
				tempWeightFilter = new double[distinctWordsFilter.size() + 1];
				
				//Calculate without stopwords
				for (int h = 0; h < hamCounts; h++) {
					double z = calculateZ(h,0);
					if (z <= 0) {
						for (int j = 1; j < distinctWords.size() + 1; j++) {
							tempWeight[j] = Eta * (1 + 1) * wordCount[h][j];
							weight[j] += tempWeight[j];
						}
					}
				}
				for (int s = hamCounts; s < spamCounts + hamCounts; s++) {
					double z = calculateZ(s,0);
					if (z > 0) {
						for (int j = 1; j < distinctWords.size() + 1; j++) {
							tempWeight[j] = Eta * (-1 - 1) * wordCount[s][j];
							weight[j] += tempWeight[j];
						}
					}
				}
				
				//Calculate with stopwords
				for (int h = 0; h < hamCounts; h++) {
					double z = calculateZ(h,1);
					if (z <= 0) {
						for (int j = 1; j < distinctWordsFilter.size() + 1; j++) {
							tempWeightFilter[j] = Eta * (1 + 1) * wordCountFilter[h][j];
							weightFilter[j] += tempWeightFilter[j];
						}
					}
				}
				for (int s = hamCounts; s < spamCounts + hamCounts; s++) {
					double z = calculateZ(s,1);
					if (z > 0) {
						for (int j = 1; j < distinctWordsFilter.size() + 1; j++) {
							tempWeightFilter[j] = Eta * (-1 - 1) * wordCountFilter[s][j];
							weightFilter[j] += tempWeightFilter[j];
						}
					}
				}
			}

			int Phamcount = 0, Pnhamcount = 0, Pspamcount = 0, Pnspamcount = 0;
			int PhamcountF = 0, PnhamcountF = 0, PspamcountF = 0, PnspamcountF = 0;
			String line1 = " ";

			//double[] testweight;

			// Calculate Ham Accuracy on Test Set
			for (File file : listofHamtestFiles) {
				br = new BufferedReader(new FileReader(file));
				double prob = weight[0];
				double probf = weightFilter[0];
				while ((line1 = br.readLine()) != null) {
					data = line1.split(" ");
					for (String token : data) {
						if (!specialcharacters.contains(token.toLowerCase())
								&& !token.isEmpty()
								&& distinctWords.containsKey(token
										.toLowerCase())) {
							prob = prob + weight[distinctWords.get(token.toLowerCase())];
						}
						if (!specialcharacters.contains(token.toLowerCase())
								&& !token.isEmpty() && !specialcharacters.contains(token
										.toLowerCase()) && distinctWordsFilter.containsKey(token
										.toLowerCase())) {
							probf = probf + weightFilter[distinctWordsFilter.get(token.toLowerCase())];
						}
					}
				}

				if (prob > 0)
					Phamcount++;
				else
					Pnhamcount++;
				
				if (probf > 0)
					PhamcountF++;
				else
					PnhamcountF++;
			}

			// Calculate Spam Accuracy on Test set
			for (File file : listofSpamtestFiles) {
				br = new BufferedReader(new FileReader(file));
				double prob = weight[0];
				double probf = weightFilter[0];

				while ((line1 = br.readLine()) != null) {
					data = line1.split(" ");
					for (String token : data) {
						if (!specialcharacters.contains(token.toLowerCase())
								&& !token.isEmpty()
								&& distinctWords.containsKey(token
										.toLowerCase())) {
							prob = prob + weight[distinctWords.get(token.toLowerCase())];
						}
						if (!specialcharacters.contains(token.toLowerCase())
								&& !token.isEmpty() && !specialcharacters.contains(token
										.toLowerCase()) && distinctWordsFilter.containsKey(token
										.toLowerCase())) {
							probf = probf + weightFilter[distinctWordsFilter.get(token.toLowerCase())];
						}
					}
				}

				if (prob < 0)
					Pspamcount++;
				else
					Pnspamcount++;
				
				if (probf < 0)
					PspamcountF++;
				else
					PnspamcountF++;
				
			}

			// Print Ham and Spam accuracy
			System.out.println("***Accuracy of Ham***"
					+ ((float) Phamcount / (Phamcount + Pnhamcount)) * 100);
			System.out.println();
			System.out.println("***Accuracy of Spam***"
					+ ((float) Pspamcount / (Pnspamcount + Pspamcount)) * 100);
			System.out.println();
			// Print Ham and Spam accuracy with stopwords
						System.out.println("***Accuracy of Ham with stopwords***"
								+ ((float) PhamcountF / (PhamcountF + PnhamcountF)) * 100);
						System.out.println();
						System.out.println("***Accuracy of Spam with stopwords***"
								+ ((float) PspamcountF / (PnspamcountF + PspamcountF)) * 100);

						
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

//Calculate sum
	public static double calculateZ(int fileindex,int check) {
		double z = 0;
		if(check==0)
		{
		for (int i = 1; i < distinctWords.size() + 1; i++) {
			z = z + wordCount[fileindex][i] * weight[i];
		}
		z = z + weight[0];
		}
		else
		{
			for (int i = 1; i < distinctWordsFilter.size() + 1; i++) {
				z = z + wordCountFilter[fileindex][i] * weightFilter[i];
			}
			z = z + weightFilter[0];
		}
		return z;
	}

}
