package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoBIDEPlus;
/*
 * Example of how to use the BIDE+ algorithm, from the source code.
 */
public class MainTestBIDEPlus_saveToFile extends MainTestBase {

	public static void main(String [] arg) throws IOException{

		MainTestBIDEPlus_saveToFile main = new MainTestBIDEPlus_saveToFile();

		// Load a sequence database
		String inputfile = main.fileToPath("contextPrefixSpan.txt");
		
		int minsup = 2; // we use a minsup of 2 sequences (50 % of the database size)
		
		AlgoBIDEPlus algo  = new AlgoBIDEPlus();  //
		
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        algo.setShowSequenceIdentifiers(false);
		
		// execute the algorithm
		algo.runAlgorithm(inputfile, ".//output.txt", minsup);    
		algo.printStatistics();
	}
	
}