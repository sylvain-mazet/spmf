package ca.pfv.spmf.test;

import java.io.IOException;

import ca.pfv.spmf.algorithms.frequentpatterns.aprioriTID.AlgoAprioriTID;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids.ItemsetsWithTIDS;

/**
 * Example of how to use the AprioriTID algorithm from the source code.
 * @author Philippe Fournier-Viger 
 */
public class MainTestAprioriTID extends MainTestBase {

	public static void main(String [] arg) throws NumberFormatException, IOException{
		MainTestBase main = new MainTestBase();

		// Loading the binary context
		String inputfile = main.fileToPath("contextPasquier99.txt");
		
		// Applying the AprioriTID algorithm
		AlgoAprioriTID algo = new AlgoAprioriTID();
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset)
//		algo.setMaximumPatternLength(3);
		
		// We run the algorithm.
		// Note: we pass a null value for the output file 
		//      because we want to keep the result into memory
		//      instead of writing it to an output file.
		ItemsetsWithTIDS patterns = algo.runAlgorithm(inputfile, null, 0.4);
		patterns.printItemsets(algo.getDatabaseSize());
		algo.printStats();
	}
	
}
