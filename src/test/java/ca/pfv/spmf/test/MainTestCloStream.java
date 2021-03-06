package ca.pfv.spmf.test;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import ca.pfv.spmf.algorithms.frequentpatterns.clostream.AlgoCloSteam;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;

/**
 * Example of how to use the CloStream algorith, from the source code.
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestCloStream extends MainTestBase {  

	public static void main(String [] arg){
		
		// Creating an instance of the CloStream algorithm
		AlgoCloSteam cloStream = new AlgoCloSteam();
		
		// Now we add 5 transactions
		long startTime = System.currentTimeMillis();
		ItemsetArrayImplWithCount transaction0 = new ItemsetArrayImplWithCount(new int[] {1,3,4});
		cloStream.processNewTransaction(transaction0);
		
		ItemsetArrayImplWithCount transaction1 = new ItemsetArrayImplWithCount(new int[] {2,3,5});
		cloStream.processNewTransaction(transaction1);
		
		ItemsetArrayImplWithCount transaction2 = new ItemsetArrayImplWithCount(new int[] {1,2,3,5});
		cloStream.processNewTransaction(transaction2);
		
		ItemsetArrayImplWithCount transaction3 = new ItemsetArrayImplWithCount(new int[] {2,5});
		cloStream.processNewTransaction(transaction3);

		ItemsetArrayImplWithCount transaction4 = new ItemsetArrayImplWithCount(new int[] {1,2,3,5});
		cloStream.processNewTransaction(transaction4);
		
		// We print the patterns found
		List<ItemsetArrayImplWithCount> list = cloStream.getClosedItemsets();
		System.out.println("Closed itemsets count : " + list.size());
		for(ItemsetArrayImplWithCount itemset : list){
			System.out.println("  " + itemset.toString() + " absolute support : " + itemset.getAbsoluteSupport());
		}

		long endTime = System.currentTimeMillis();
		System.out.println("total Time : " + (endTime - startTime) + "ms");
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestCloStream.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
