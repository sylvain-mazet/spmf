package ca.pfv.spmf.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.cfpgrowth.AlgoCFPGrowth;

/**
 * Example of how to use the CFPGrowth++ algorithm, from the source code.
 */
public class MainTestCFPGrowth_saveToFile extends MainTestBase {

	public static void main(String[] arg) throws FileNotFoundException,
			IOException {
		String database = new MainTestBase().fileToPath("contextCFPGrowth.txt");
		String output = ".//output.txt";
		String MISfile = new MainTestBase().fileToPath("MIS.txt");

		// Applying the CFPGROWTH algorithmMainTestFPGrowth.java
		AlgoCFPGrowth algo = new AlgoCFPGrowth();
		algo.runAlgorithm(database, output, MISfile);
		algo.printStats();
	}

	public String fileToPath(String filename)
			throws UnsupportedEncodingException {
		URL url = MainTestCFPGrowth_saveToFile.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
	}
}
