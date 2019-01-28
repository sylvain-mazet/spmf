package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.apriori_inverse.AlgoAprioriInverse;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetsArrayIntegerWithCount;

/**
 * Example of how to use the APRIORI-INVERSE algorithm from the source
 * code.
 * 
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestAprioriInverse_saveToMemory extends MainTestBase {

	public static void main(String [] arg) throws IOException{
		// Loading a binary context
		String inputFilePath = new MainTestBase().fileToPath("contextInverse.txt");
		String outputFilePath = null;  
		// Note that we set the output file path to null because
		// we want to keep the result in memory instead of saving them
		// to an output file in this example.
		
		// the thresholds that we will use:
		double minsup = 0.001;
		double maxsup = 0.6;
		
		// Applying the APRIORI-Inverse algorithm to find sporadic itemsets
		AlgoAprioriInverse apriori2 = new AlgoAprioriInverse();
		// apply the algorithm
		ItemsetsArrayIntegerWithCount patterns = (ItemsetsArrayIntegerWithCount) apriori2.runAlgorithm(minsup, maxsup, inputFilePath, outputFilePath).getItemsets();
		int databaseSize = apriori2.getDatabaseSize();
		patterns.printItemsets(databaseSize); // print the result
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestAprioriInverse_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
