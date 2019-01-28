package ca.pfv.spmf.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.algorithms.frequentpatterns.cfpgrowth.AlgoCFPGrowth;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetsArrayIntegerWithCount;

/**
 * Example of how to use the CFPGrowth++ algorithm, from the source code and save the result to 
 * memory instead of into a file.
 */
public class MainTestCFPGrowth_saveToMemory extends MainTestBase {

	public static void main(String[] arg) throws FileNotFoundException,
			IOException {
		
		String database = new MainTestBase().fileToPath("contextCFPGrowth.txt");
		String output = null;  // because we want to indicate
		// that we want to keep the result into memory instead of 
		// saving it to a file
		String MISfile = new MainTestBase().fileToPath("MIS.txt");

		// Applying the CFPGROWTH algorithmMainTestFPGrowth.java
		AlgoCFPGrowth algo = new AlgoCFPGrowth();
		GenericResults result = algo.runAlgorithm(database, output, MISfile);
		algo.printStats();
		
		result.getItemsets().printItemsets(algo.getDatabaseSize());
	}

	public String fileToPath(String filename)
			throws UnsupportedEncodingException {
		URL url = MainTestCFPGrowth_saveToMemory.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
	}
}
