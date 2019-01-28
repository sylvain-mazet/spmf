package ca.pfv.spmf.algorithms.frequentpatterns.zart;
/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.pfv.spmf.algorithms.DbScanner;
import ca.pfv.spmf.algorithms.GenericAlgorithmBase;
import ca.pfv.spmf.algorithms.frequentpatterns.FrequentPatternsResults;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.Itemsets;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ListOfItemsetArrayIntegerWithCount;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * This is an implementation of Zart, an algorithm for mining frequent closed itemsets
 * and their associated generators at the same time. The Zart algorithm is described in the article : 
 * <br/><br/>
 * 
 * "Zart : a Multifunctional Itemset Mining Algorithm" de Laszlo Szathmary et al.
 * ZART finds all the frequent closed itemsets in a binary context, their associated
 * minimal generator(s) and their support. 
 * <br/><br/>
 * 
 * This algorithm could be optimized in various way as described in the article by Szathmary,
 * for example, by using the Trie data structure and by removing unfrequent items, but this was not done here.
 * 
 * @see TransactionDatabase
 * @see ItemsetArrayImplWithCount
 * @author Philippe Fournier-Viger
 */

public class AlgoZart extends GenericAlgorithmBase {
	
	// relative minimum support threshold
	private int minsupRelative =0;
	
	// the TZ, TF and TC structures as described in the paper
	private TZTableClosed tableClosed = null;  // table of closed itemsets and their generators
	private TFTableFrequent tableFrequent = null; // table of frequent itemsets
	private TCTableCandidate tableCandidate = null; // table of candidates
	
	// The list of frequent generators FG
	private List<ItemsetArrayImplWithCount> frequentGeneratorsFG = null; // 2

	/**
	 * Default constructor
	 */
	public AlgoZart() {
		super(null);
	}

	/***
	 * Run the algorithm as in the original code
	 * @param database  a transaction database
	 * @param minsupp   the minimum support threshold
	 * @return  a set of closed itemsets and their associated generator(s)
	 */
	public FrequentPatternsResults runAlgorithm(TransactionDatabase database, double minsupp) {

		TransactionDatabaseDbScanner dbScanner = new TransactionDatabaseDbScanner(database);
		return runAlgorithm(dbScanner, minsupp);

	}



	/**
	 * Run the algorithm on a dbScanner
	 * @param dbScanner  a database scanner
	 * @param minsupp   the minimum support threshold
	 * @return  a set of closed itemsets and their associated generator(s)
	 */
	@Override
	public FrequentPatternsResults runAlgorithm(DbScanner dbScanner, double minsupp) {
		// record the start time
		startTimestamp = System.currentTimeMillis();
		// reset the utility for recording the memory usage
		MemoryLogger.getInstance().reset();

		// Initialize the FG, TZ,TF and TC structure
		// used by the algorithm (as described in the paper)
		frequentGeneratorsFG = new ArrayList<>(); // 2
		tableClosed = new TZTableClosed();   // tabled of closed itemsets
		tableFrequent = new TFTableFrequent();  // table of frequent itemsets
		tableCandidate = new TCTableCandidate();  // table of candidates

		// (1) Scan the database and count the support of each item (in a map)
		// for this map : key = item value = support
		Map<Integer, Integer> mapItemSupport = scanDatabaseToDetermineFrequencyOfSingleItems(dbScanner);

		// convert the minimum support from absolute to relative by
		// multiplying by the database size
		minsupRelative =  (int) Math.ceil(minsupp * getTransactionCount());

		// (0) Remove infrequent items  from each transaction.
		DbScanner.DbIterator dbIterator = dbScanner.dbIterator();
		while (dbIterator.hasNext()) {

			DbScanner.TransactionIterator transactionIterator = dbIterator.next();

			while (transactionIterator.hasNext()) {
				Integer item = transactionIterator.next();
			}
		}
		/* affreux: ne pas appeler remove() sur un Iterator
		// et je ne veux pas modifier la base de donnees
		// For each transaction
		for(List<Integer> transaction : database.getTransactions()){
			// for each item
			Iterator<Integer> it = transaction.iterator();
			while (it.hasNext()) {
				// get the item
				Integer item = (Integer) it.next();
				// if infrequent, then remove it
				if(mapItemSupport.get(item) < minsupRelative){
					it.remove();
				}
			}
		}
		*/

		// (1) fill candidates with 1-itemsets (single items)
		tableCandidate.levels.add(new ArrayList<>());
		for(Integer item : mapItemSupport.keySet()){

			if (isItemInfrequent(item, mapItemSupport)) {
				// skip infrequent items
				continue;
			}

			// create an itemset for the item and set its support
			ItemsetArrayImplWithCount itemset = new ItemsetArrayImplWithCount(item);
			itemset.setAbsoluteSupport(mapItemSupport.get(item));
			// if the support is higher than minsup
			if(mapItemSupport.get(item) >= minsupRelative){
				// add it to frequent itemsets and candidates table
				tableFrequent.addFrequentItemset(itemset);
				tableCandidate.levels.get(0).add(itemset);
			}
		}

//		// sort candidates
//		Collections.sort(tableCandidate.levels.get(0), new Comparator<Itemset>() {
//			public int compare(Itemset i1, Itemset i2) {
//				return i1.getItems().get(0) - i2.getItems().get(0);
//			}
//		});
//
		// if there are frequent items
		if(tableFrequent.levels.size() != 0) {

			//This variable will be used to indicate if a full column is set to
			// 1 in the binary context, which means that a non-empty itemset is shared
			// by all transactions
			boolean fullCollumn = false; // 1

			// 6 : Loops over frequent itemsets of size 1
			for(ItemsetArrayImplWithCount l : tableFrequent.getLevelForZart(0)){
				// assign the value true to l in the map for closed itemsets
				tableFrequent.mapClosed.put(l, true); // 8
				// If L has the support equal to the number of transactions in the database
				if(l.getAbsoluteSupport() == getTransactionCount()){ // 9
					// 10  The empty set is its generator (IMPORTANT)
					tableFrequent.mapKey.put(l, false);
					// there is an itemset shared by all transactions
					fullCollumn = true; // 11
				}else{
					// otherwise, put l into the table  of frequent itemsets
					tableFrequent.mapKey.put(l, true); // 13
				}
			}

			// create the empty set
			ItemsetArrayImplWithCount emptyset = new ItemsetArrayImplWithCount(new int[]{});

			// 15 if there is an itemset shared by all transactions
			if(fullCollumn){
				// add the empty set as a generator
				frequentGeneratorsFG.add(emptyset);
			}else{
				// Otherwise, the empty set is closed and it is its own generator
				// So we add it to the tables accordingly
				tableFrequent.addFrequentItemset(emptyset);  // add to table of frequent itemsets
				tableFrequent.mapClosed.put(emptyset, true);  // add to table of closed itemsets
				tableFrequent.mapPredSupp.put(emptyset, getTransactionCount());
				tableClosed.addClosedItemset(emptyset);
				tableClosed.mapGenerators.put(emptyset, new ArrayList<>());
				// we set its support as the database size
				emptyset.setAbsoluteSupport(getTransactionCount());
			}

			// Now, Zart will recursively  generate candidates of larger size i+1
			// by using itemsets of size i to discover all frequent itemsets, closed itemsets
			// and their generator.
			// This process is based on the Apriori algorithm but modified.
			int i=1;

			for(; true; i++){  // 16
				zartGen(i); // 18   Ci+1 = ZartGen(Fi);

				// if there is no candidate, then
				// the algorithm stops
				if(tableCandidate.levels.get(i).size() == 0){ // 19
					break;
				}

				// if there is an itemset of size i with its key value to true
				if(tableCandidate.thereisARowKeyValueIsTrue(i)){ // 20
					/* // 22 for each transaction
					for(List<Integer> o : database.getTransactions()){ //22
						// for each subset of the candidate
						for(Itemset s : subset(tableCandidate.levels.get(i), o)){ // 23, 24
							if(tableCandidate.mapKey.get(s)){
								// increase its support count
								s.increaseTransactionCount(); //25
							}
						}
					}
					*/

					dbIterator = dbScanner.dbIterator();
					List<Integer> dbItemset = new ArrayList<>();
					// 22 for each transaction
					while (dbIterator.hasNext()) {
						dbItemset.clear();
						// build the sub set
						DbScanner.TransactionIterator transactionIterator = dbIterator.next();
						while (transactionIterator.hasNext()) {
							Integer item = transactionIterator.next();
							dbItemset.add(item);
						}
						// for each subset of the candidate
						for(ItemsetArrayImplWithCount s : subset(tableCandidate.levels.get(i), dbItemset)){ // 23, 24
							if(tableCandidate.mapKey.get(s)){
								// increase its support count
								s.increaseTransactionCount(); //25
							}
						}

					}

				}

				// for each candidate itemset of size i
				for(ItemsetArrayImplWithCount c : tableCandidate.levels.get(i)){ //28
					// if it is a frequent itemset
					if(c.getAbsoluteSupport() >= minsupRelative){
						//31
						// if c is set to true in mapKey and its support is
						// equal to the one of predSup
						if(tableCandidate.mapKey.get(c) == true && c.getAbsoluteSupport() == tableCandidate.mapPredSupp.get(c)){
							// set its key to false!
							tableCandidate.mapKey.put(c, false); //32
						}
						// add the itemset to the list of frequent itemset
						tableFrequent.addFrequentItemset(c); // 33
						// put c in the maps of TF
						// Note that this step was not explicit in the original algorithm.
						tableFrequent.mapKey.put(c, tableCandidate.mapKey.get(c));
						tableFrequent.mapPredSupp.put(c, tableCandidate.mapPredSupp.get(c));
					}
				}

				// for each frequent itemset of size i
				for(ItemsetArrayImplWithCount l : tableFrequent.getLevelForZart(i)){ // 36
					// add it as closed to the map of closed itemsets by assuming
					// that it is closed until now
					tableFrequent.mapClosed.put(l, true); //37
					// for all suset of l of size i-1
					for(ItemsetArrayImplWithCount s : subset(tableFrequent.getLevelForZart(i-1), l)){ // 38, 39
						// if it has the same support as l, that means
						// that l is not closed so we mark it as such.
						if(s.getAbsoluteSupport() == l.getAbsoluteSupport()){ // 40
							tableFrequent.mapClosed.put(s, false);
						}
					}
				}

				// 42
				tableClosed.levels.add(new ListOfItemsetArrayIntegerWithCount());
				// for each frequent itemsets of size i-1
				for(ItemsetArrayImplWithCount l : tableFrequent.getLevelForZart(i-1)){
					//  if it is marked as closed, then we add it to
					// the table of closed itemsets.
					if(tableFrequent.mapClosed.get(l) == true){
						tableClosed.getLevelForZart(i-1).add(l);
					}
				}

				// find the generators for closed itemsets of size i-1
				findGenerators(tableClosed.getLevelForZart(i-1), i); // 43

				// check the memory usage
				MemoryLogger.getInstance().checkMemory();
			}

			//  ....  45
			tableClosed.levels.add(new ListOfItemsetArrayIntegerWithCount());
			for(ItemsetArrayImplWithCount l : tableFrequent.getLevelForZart(i-1)){
				tableClosed.getLevelForZart(i-1).add(l);
			}

			// Call the find generator method to find the generators.
			// This is line 46 in the pseudo code of Zart.
			findGenerators(tableClosed.getLevelForZart(i-1),  i);

		}

		// check the memory usage
		MemoryLogger.getInstance().checkMemory();
		// record the end time
		endTime = System.currentTimeMillis();

		// return a table containing the closed itemsets and their associatied generator(s)
		return new FrequentPatternsResults(tableClosed,null);
	}

	private boolean isItemInfrequent(Integer item, Map<Integer,Integer> mapItemSupport) {
		return mapItemSupport.get(item) < minsupRelative;
	}

	/**
	 * Find generators for each itemset of a list of closed itemsets
	 * @param zi a list of itemset zi of size i
	 * @param i  the size i
	 */
	private void findGenerators(Itemsets.ListOfItemset zi, int i) {
		// for each itemset in the list
		for(AbstractItemset zAbs : zi){ // 1
			ItemsetArrayImplWithCount z = (ItemsetArrayImplWithCount) zAbs;
			// get the list of all frequent generators contained in z 
			List<ItemsetArrayImplWithCount> s = subset(frequentGeneratorsFG, z);  // 3
			// register them in the map associating closed itemsets to their generators
			tableClosed.mapGenerators.put(z, s);  // 4
			// remove the generators from the list of generators 
			// because a generator is member of only one equivalence class
			frequentGeneratorsFG.removeAll(s); // 5
		}  
		// for each frequent itemsets of size i-1
		for(ItemsetArrayImplWithCount l : tableFrequent.getLevelForZart(i-1)){
			// if the key value is set to true and it is not closed
			if(tableFrequent.mapKey.get(l) == true && tableFrequent.mapClosed.get(l) == false){
				// then add it to the list of generators
				frequentGeneratorsFG.add(l);
			}
		}
	}

	/**
	 * This returns the list of itemsets from a list of itemsets s that
	 * are included in a given itemset l.
	 * @param s  a list of itemsets S of the same size
	 * @param l  an itemset L.
	 * @return the list of itemsets from S that are contained in L
	 */
	private List<ItemsetArrayImplWithCount> subset(List<ItemsetArrayImplWithCount> s, ItemsetArrayImplWithCount l) {
		// Initialize the list of subsets
		List<ItemsetArrayImplWithCount> retour = new ArrayList<>();
		// for each itemset in S
		for(ItemsetArrayImplWithCount itemsetS : s){
			boolean allIncluded = true;
			// for each item of this itemset,
			for(int i=0; i<itemsetS.size(); i++){
				// if that item is not contained in the itemset l
				// then the itemset S is not included in l and we note it
				if(!l.contains(itemsetS.get(i))){
					allIncluded = false;
				}
			}
			// if s is  included in l
			if(allIncluded){
				// then add it to the list of subsets
				retour.add(itemsetS);
			}
		}
		// return the list
		return retour;
	}
	
	/**
	 * This returns the list of itemsets from a list of itemsets s that
	 * are included in a given itemset l.
	 * @param s  a list of itemsets S of the same size
	 * @param l  an itemset L.
	 * @return the list of itemsets from S that are contained in L
	 */
	private List<ItemsetArrayImplWithCount> subset(List<ItemsetArrayImplWithCount> s, List<Integer> l) {
		// Initialize the list of subsets
		List<ItemsetArrayImplWithCount> subset = new ArrayList<>();
		// for each itemset in S
		for(ItemsetArrayImplWithCount itemsetS : s){
			boolean allIncluded = true;
			// for each item of this itemset,
			for(int i=0; i<itemsetS.size(); i++){
				// if that item is not contained in the itemset l
				// then the itemset S is not included in l and we note it
				if(!l.contains(itemsetS.get(i))){
					allIncluded = false;
				}
			}
			// if s is  included in l
			if(allIncluded){
				// then add it to the list of subsets
				subset.add(itemsetS);
			}
		}
		// return the list
		return subset;
	}

	/**
	 * Method for the generation of candidates of size i.
	 * @param i  the size i.
	 */
	private void zartGen(int i) {
		// This method generates the candidates of size i 
		// (similar to apriori-gen).
		prepareCandidateSizeI(i);
		
		// Then, for each candidate found in the previous step
		// we check if all the subsets of size i-1 (also named k-1 here) are frequents.
		// If one subset is infrequent, then the candidate is infrequent
		// and we don't need to consider it anymore.
		
		// for each candidate
		for(ItemsetArrayImplWithCount c : new ArrayList<>(tableCandidate.levels.get(i))){ // 2
			// set the key to true
			tableCandidate.mapKey.put(c, true); // 4
			//  set the support to database size +1.
			tableCandidate.mapPredSupp.put(c, getTransactionCount() + 1);
			// 7
			// To generate all sets of size k-1: S, we will proceed
			// by removing each element one by one.
			
			// for each element
			for(int j=0; j<c.size(); j++){
				// we copy the itemset without the current item
				ItemsetArrayImplWithCount s = c.cloneItemSetMinusOneItem(c.get(j));
				boolean found = false;
				// now for each frequent itemsets of size i-1
				for(ItemsetArrayImplWithCount itemset2 : tableFrequent.getLevelForZart(i-1)){
					// if we have found the subset, then we stop this loop 
					// and set the variale to true to remember that we found it
					if(itemset2.isEqualTo(s)){
						found = true;
						break;
					}
				}
				// if the current subset of S is not frequent
				if(found == false){ 
					// then we remove it from the candidates
					tableCandidate.levels.get(i).remove(c);
				}else{
					// if the current subset is frequent,
					// then get the previous occurence in the table of candidates
					ItemsetArrayImplWithCount occurenceS = getPreviousOccurenceOfItemset(s, tableCandidate.levels.get(i-1));  // AJOUT N�CESSAIRE
					// if the support of that occurence is lower
					if(occurenceS.getAbsoluteSupport() < tableCandidate.mapPredSupp.get(c)){ // 11
						// then we will use that support for this subset
						tableCandidate.mapPredSupp.put(c, occurenceS.getAbsoluteSupport()); 
					}else{
						//otherwise, we use the support of c
						tableCandidate.mapPredSupp.put(c, tableCandidate.mapPredSupp.get(c));
					}
					// After that, if the previous occurrence has the key set to false
					if(tableFrequent.mapKey.get(occurenceS) == false){  // 12 
						// we will also set it to false in the table of candidates
						tableCandidate.mapKey.put(c, false); 
					}
				}
			}

			// 15
			// finally, if the key of the candidate c has been set to false
			// then we will set its support to the support stored in the
			// table of candidates.
			if(tableCandidate.mapKey.get(c) == false){
				c.setAbsoluteSupport(tableCandidate.mapPredSupp.get(c));
			}
		}
	}
	
	/**
	 * Get the previous occurence of an itemset in a list of itemset.
	 * @param itemset  the given itemset
	 * @param list  the list of itemsets
	 * @return  the previous occurence or null if there is not such previous occurence
	 */
	private ItemsetArrayImplWithCount getPreviousOccurenceOfItemset(ItemsetArrayImplWithCount itemset, List<ItemsetArrayImplWithCount> list){
		// for each itemset in the list
		for(ItemsetArrayImplWithCount itemset2 : list){
			// if it is equal to the itemset that is searched, then 
			// return it
			if(itemset2.isEqualTo(itemset)){
				return itemset2;
			}
		}
		// otherwise, it was not found, so return null
		return null;
	}
	
	/**
	 * This is the method to generate candidate itemsets of size i. 
	 * It is similar to the Apriori candidate generation.
	 * @param size  the size i
	 */
	protected void prepareCandidateSizeI(int size) {
		// add a new list in candidates to store the candidates of size i
		tableCandidate.levels.add(new ArrayList<ItemsetArrayImplWithCount>());
		
		// For each frequent itemset I1 and  I2 of size i-1
		for(ItemsetArrayImplWithCount itemset1 : tableFrequent.getLevelForZart(size-1)){
			for(ItemsetArrayImplWithCount itemset2 : tableFrequent.getLevelForZart(size-1)){
				// If I1 is smaller than I2 according to lectical order
				// and that they have only one element that is different
//				Integer missing = itemset2.haveOneItemDifferent(itemset1);
				Integer missing = itemset2.allTheSameExceptLastItem(itemset1);
				if(missing != null){
					
					// Create a new candidate by combining itemset1 and itemset2
					int union[] = new int[itemset1.size()+1];
					System.arraycopy(itemset2.itemset, 0, union, 0, itemset2.size());
					union[itemset2.size()] = missing;

					// add the resulting itemset
					// to the table of candidates of size i
					tableCandidate.levels.get(size).add(new ItemsetArrayImplWithCount(union));
				}
			}
		}
	}
	

	/**
	 * Get the table of frequent itemsets
	 * @return the table of frequent itemsets
	 */
	public TFTableFrequent getTableFrequent() {
		return tableFrequent;
	}

	/**
	 * Print statistics about the latest execution of the algorithm.
	 */
	@Override
	public void printStats() {
		super.printStats("Zart","v??");
	}

	/**
	 * Save the results found to a file
	 * @param output the path of an output file
	 * @throws IOException exception if error while writing to the file
	 */
	public void saveResultsToFile(String output) throws IOException {
		//prepare the output file
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write("======= List of closed itemsets and their generators ============");
		writer.newLine();
		
		// for each level in the table of closed itemset
		// (the level i is the closed itemsets of size i)
		for(int i=0; i< tableClosed.levels.size(); i++){
			// for each closed itemsets of size i
			for(AbstractItemset closedAbs : tableClosed.levels.get(i)){
				ItemsetArrayImplWithCount closed = (ItemsetArrayImplWithCount) closedAbs;
				// write the itemset
				writer.write(" CLOSED : \n   " + closed.toString() + " #SUP: " + closed.getAbsoluteSupport());
				writer.newLine();
				// write the generators
				writer.write("   GENERATOR(S) :");
				writer.newLine();
				/// for each generator of that closed itemset
				List<ItemsetArrayImplWithCount> generators = tableClosed.mapGenerators.get(closed);
				// if there is no generators, it means that the closed itemset is a generator
				if(generators.size() == 0) {
					writer.write("    " + closed.toString() );
					writer.newLine();
				}else {
					// otherwise we write the generators
					for(ItemsetArrayImplWithCount generator : generators){
						// write the generator
						writer.write("     " + generator.toString());
						writer.newLine();
					}
				}
			}
		}
		
		// We then print the list of frequent itemsets
		writer.write("======= List of frequent itemsets ============");
		writer.newLine();
		// for itemsets of size i from 0 to the largest itemsets
		for(int i=0; i< tableFrequent.levels.size(); i++){
			// for each frequent itemset of size i
			for(ItemsetArrayImplWithCount itemset : tableFrequent.levels.get(i)){
				// write the itemset
				writer.write(" ITEMSET : " + itemset.toString() + " #SUP: " + itemset.getAbsoluteSupport());
				writer.newLine();
			}
		}
		// Finally, the output file is closed
		writer.close();
	}	
	
}
