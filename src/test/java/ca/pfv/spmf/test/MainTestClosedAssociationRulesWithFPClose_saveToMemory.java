package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.algorithms.associationrules.closedrules.AlgoClosedRules_UsingFPClose;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPClose;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.GenericFPGrowthAlgorithmBase;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetsArrayIntegerWithCount;

/**
 * Example of how to mine closed association rules using FPClose from the source code and save the result to memory
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestClosedAssociationRulesWithFPClose_saveToMemory extends MainTestBase {

	public static void main(String [] arg) throws IOException{
		// input file path
		String input = new MainTestBase().fileToPath("bad.txt");
		
		// the threshold
		double minsup = 0.20;
		double minconf = 0.40;
		
		// Loading the transaction database
		TransactionDatabase database = new TransactionDatabase();
		try {
			database.loadFile(input);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		database.printDatabase();
		
		// STEP 1: Applying the Charm algorithm to find frequent closed itemsets
		AlgoFPClose algo = new AlgoFPClose();
		// Run the algorithm
		// Note that here we use "null" as output file path because we want to keep the results into memory instead of saving to a file
		GenericResults patterns = algo.runAlgorithm(input, null, minsup);
		
		// Show the CFI-Tree for debugging!
//		System.out.println(algo.cfiTree);
		
		// show the execution time and other statistics
		algo.printStats();
		// print the patterns to System.out
		patterns.getItemsets().printItemsets(algo.getDatabaseSize());
		
		// STEP 2: Generate all rules from the set of frequent itemsets (based on Agrawal & Srikant, 94)
		AlgoClosedRules_UsingFPClose algoClosedRules = new AlgoClosedRules_UsingFPClose();
		AssocRules rules = algoClosedRules.runAlgorithm(patterns, null, database.size(), minconf);
		algoClosedRules.printStats();
		rules.printRules(database.size());
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestClosedAssociationRulesWithFPClose_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
