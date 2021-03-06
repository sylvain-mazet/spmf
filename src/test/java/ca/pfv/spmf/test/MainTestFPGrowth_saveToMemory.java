package ca.pfv.spmf.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetsArrayIntegerWithCount;


/**
 * Example of how to use FPGrowth from the source code.
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestFPGrowth_saveToMemory extends MainTestBase {

	public static void main(String [] arg) throws FileNotFoundException, IOException{
		// Loading the transaction database
		String input = new MainTestBase().fileToPath("contextPasquier99.txt");  // the database

		// the minimum support threshold
		double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)

		// Applying the FPGROWTH algorithmMainTestFPGrowth.java
		AlgoFPGrowth algo = new AlgoFPGrowth();
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset, e.g. 3 )
//		algo.setMaximumPatternLength(3);
		
		// Run the algorithm
		// Note that here we use "null" as output file path because we want to keep the results into memory instead of saving to a file
		ItemsetsArrayIntegerWithCount patterns = (ItemsetsArrayIntegerWithCount)algo.runAlgorithm(input, null, minsup);
		// show the execution time and other statistics
		algo.printStats();
		// print the patterns to System.out
		patterns.printItemsets(algo.getDatabaseSize());
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFPGrowth_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
