package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;

import ca.pfv.spmf.algorithms.frequentpatterns.defme.AlgoDefMe;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.Itemsets;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.ItemsetWithTIDSBitset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.ItemsetsWithTIDSBitset;


/**
 * Example of how to use DefMe algorithm from the source code.
 * @author Philippe Fournier-Viger - 2009
 */
public class MainTestDefMe_saveToMemory extends MainTestBase {

	public static void main(String [] arg) throws IOException{
		// Loading the binary context
		String input = new MainTestBase().fileToPath("contextZart.txt");  // the database
		
		double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)
		
		TransactionDatabase database = new TransactionDatabase();
		try {
			database.loadFile(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Applying the DefMe algorithm
		AlgoDefMe algo = new AlgoDefMe();
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset)
//		algo.setMaximumPatternLength(2);
		
		ItemsetsWithTIDSBitset generators = algo.runAlgorithm(null, database, minsup);
		algo.printStats();
		for(Itemsets.ListOfItemset genSizeK : generators.getLevels()) {
			for(AbstractItemset itemsetAbs : genSizeK) {
				ItemsetWithTIDSBitset itemset = (ItemsetWithTIDSBitset) itemsetAbs;
				System.out.println(Arrays.toString(itemset.getItems()) + " #SUP: " + itemset.getAbsoluteSupport());
			}
		}
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestDefMe_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
