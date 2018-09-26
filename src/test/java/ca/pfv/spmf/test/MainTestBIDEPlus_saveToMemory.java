package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoBIDEPlus;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.SequentialPatterns;

/**
 * Example of how to use the BIDE+ algorithm, from the source code.
 * 
 * @author Philippe Fournier-Viger
 */
public class MainTestBIDEPlus_saveToMemory extends MainTestBase {

	public static void main(String [] arg) throws IOException{
		MainTestBase main = new MainTestBase();

		// Load a sequence database
		String inputfile = main.fileToPath("contextPrefixSpan.txt");
		// Create an instance of the algorithm
		AlgoBIDEPlus algo  = new AlgoBIDEPlus();
		
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        boolean showSequenceIdentifiers = true;
		
		// execute the algorithm
		SequentialPatterns patterns = algo.runAlgorithm(inputfile, null, 2);  
		patterns.printFrequentPatterns(algo.patternCount, showSequenceIdentifiers);  
		algo.printStatistics();
	}
	
}