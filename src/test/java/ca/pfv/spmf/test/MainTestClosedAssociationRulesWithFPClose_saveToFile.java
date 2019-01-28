package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.algorithms.associationrules.closedrules.AlgoClosedRules_UsingFPClose;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPClose;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetsArrayIntegerWithCount;

/**
 * Example of how to mine closed association rules from the source code.
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestClosedAssociationRulesWithFPClose_saveToFile extends MainTestBase {

	public static void main(String [] arg) throws IOException{
		// input and output file paths
		String input = new MainTestBase().fileToPath("contextZart.txt");
		String output = ".//output.txt";
		
		// the threshold
		double minsupp = 0.60;
		double  minconf = 0.60;
		
		// By changing the following lines to some other values
		// it is possible to restrict the number of items in the antecedent and
		// consequent of rules
		int maxConsequentLength = 40;
		int maxAntecedentLength = 40;
		
		// Loading the transaction database
		TransactionDatabase database = new TransactionDatabase();
		try {
			database.loadFile(input);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// STEP 1: Applying the Charm algorithm to find frequent closed itemsets
		AlgoFPClose algo = new AlgoFPClose();
		// Run the algorithm
		// Note that here we use "null" as output file path because we want to keep the results into memory instead of saving to a file
		GenericResults patterns = algo.runAlgorithm(input, null, minsupp);
		
		// Show the CFI-Tree for debugging!
//		System.out.println(algo.cfiTree);
		
		// STEP 2: Generate all rules from the set of frequent itemsets (based on Agrawal & Srikant, 94)
		AlgoClosedRules_UsingFPClose algoClosedRules = new AlgoClosedRules_UsingFPClose();
		algoClosedRules.setMaxConsequentLength(maxConsequentLength);
		algoClosedRules.setMaxAntecedentLength(maxAntecedentLength);
		algoClosedRules.runAlgorithm(patterns, output, database.size(), minconf);
		algoClosedRules.printStats();

	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestClosedAssociationRulesWithFPClose_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
