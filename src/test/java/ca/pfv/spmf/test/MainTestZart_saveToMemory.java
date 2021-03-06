package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import ca.pfv.spmf.algorithms.frequentpatterns.FrequentPatternsResults;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.AlgoZart;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.TFTableFrequent;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.TZTableClosed;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;
/**
 * Example of how to use the Zart Algorithm in source code.
 * @author Philippe Fournier-Viger, 2013
 *
 */
public class MainTestZart_saveToMemory extends MainTestBase {

	public static void main(String[] args) throws IOException {

		// Load a binary context
		TransactionDatabase context = new TransactionDatabase();
		context.loadFile(new MainTestBase().fileToPath("contextZart.txt"));

		// Apply the Zart algorithm
		double minsup = 0.4;
		AlgoZart zart = new AlgoZart();
		FrequentPatternsResults patterns = zart.runAlgorithm(context, minsup);
		TZTableClosed results = (TZTableClosed)patterns.getItemsets();
		TFTableFrequent frequents = zart.getTableFrequent();
		zart.printStats();
		
		// PRINTING RESULTS
		int countClosed=0;
		int countGenerators=0;
		System.out.println("======= List of closed itemsets and their generators ============");
		for(int i=0; i< results.levels.size(); i++){
			System.out.println("LEVEL (SIZE) : " + i);
			for(AbstractItemset closedAbs : results.levels.get(i)){
				ItemsetArrayImplWithCount closed = (ItemsetArrayImplWithCount) closedAbs;
				System.out.println(" CLOSED : \n   " + closed.toString() + "  supp : " + closed.getAbsoluteSupport());
				countClosed++;
				System.out.println("   GENERATORS : ");
				
				List<ItemsetArrayImplWithCount> generators = results.mapGenerators.get(closed);
				// if there are some generators
				if(generators.size()!=0) { 
					for(ItemsetArrayImplWithCount generator : generators){
						countGenerators++;
						System.out.println("     =" + generator.toString());
					}
				}else {
					// otherwise the closed itemset is a generator
					countGenerators++;
					System.out.println("     =" + closed.toString());
				}
			}
		}
		System.out.println(" NUMBER OF CLOSED : " + countClosed +  " NUMBER OF GENERATORS : " + countGenerators );
		
		// SECOND, WE PRINT THE LIST OF ALL FREQUENT ITEMSETS
		System.out.println("======= List of all frequent itemsets ============");
		int countFrequent =0;
		for(int i=0; i< frequents.levels.size(); i++){
			System.out.println("LEVEL (SIZE) : " + i);
			for(ItemsetArrayImplWithCount itemset : frequents.levels.get(i)){
				countFrequent++;
				System.out.println(" ITEMSET : " + itemset.toString() + "  supp : " + itemset.getAbsoluteSupport());
			}
		}
		System.out.println("NB OF FREQUENT ITEMSETS : " + countFrequent);
		
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestZart_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
