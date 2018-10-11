package ca.pfv.spmf.algorithms.associationrules.closedrules;

/* This file is copyright (c) 2008-2012 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.pfv.spmf.algorithms.ArraysAlgos;
import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.CFITree;
import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;

/**
 * This is an implementation of the "faster algorithm" for generating association rules,
 * described in Agrawal &
 * al. 1994, IBM Research Report RJ9839, June 1994.
 * Here it is adapted for mining closed rules using the results obtained from the FPClose algorithm (the frequent
 * closed itemsets). 
 * The main difference with AlgoAgrawalFaster94.java, is that this implementation takes
 * a different type of Itemset object as input. Moreover, the method CALCULATESUPPORT() is 
 * different because if an itemset is not closed, we must find its closure to determine
 * its support. 
 * To implement the calculation of the support efficiently, this class uses the CFI-tree
 * generated by CFPClose. It allows to calculate very quickly the support of non closed 
 * frequent itemsets. This is the main difference with "AlgoClosedRules.java"
 * To avoid redundancy as much
 * as possible, this class is a subclass of AlgoAgrawalFaster94.java and share
 * several methods.
 * <br/><br/>
 * 
 * This implementation saves the result to a file
 * or can alternatively keep it into memory if no output 
 * path is provided by the user when the runAlgorithm()
 * method is called.
 * 
 *  @see   AssocRule
 *  @see   AssocRules
 *  @author Philippe Fournier-Viger
 **/

public class AlgoClosedRules_UsingFPClose extends AlgoAgrawalFaster94{

	/** the frequent closed itemsets stored in a CFITree. This allows to efficiently count the support of their subsets. */
	private CFITree cfiTree;

	/**
	 * Default constructor
	 */
	public AlgoClosedRules_UsingFPClose(){
		
	}

	/**
	 * Run the algorithm
	 * @param patterns  a set of frequent itemsets
	 * @param output an output file path for writing the result or null if the user want this method to return the result
	 * @param databaseSize  the number of transactions in the database
	 * @param minconf  the minconf threshold
	 * @param cfiTree the cfiTree generated by FPClose to store frequent closed itemsets
	 * @return  the set of association rules if the user wished to save them into memory
	 * @throws IOException exception if error writing to the output file
	 */
	public AssocRules runAlgorithm(Itemsets patterns, String output, int databaseSize, double minconf, CFITree cfiTree) throws IOException {
		if(maxAntecedentLength < 1 || maxConsequentLength < 1){
			throw new IllegalArgumentException("The maximum length must be at least 1.");
		}
		// save the parameters
		this.minconf = minconf;
		this.minlift = 0;
		this.cfiTree = cfiTree;
		usingLift = false;
		
		// start the algorithm
		return runAlgorithm(patterns, output, databaseSize);
	}

	/**
	 * Run the algorithm
	 * @param patterns  a set of frequent itemsets
	 * @param output an output file path for writing the result or null if the user want this method to return the result
	 * @param databaseSize  the number of transactions in the database
	 * @param minconf  the minconf threshold
	 * @param minlift  the minlift threshold
	 * @param cfiTree the cfiTree generated by FPClose to store frequent closed itemsets
	 * @return  the set of association rules if the user wished to save them into memory
	 * @throws IOException exception if error writing to the output file
	 */
	public AssocRules runAlgorithm(Itemsets patterns, String output, int databaseSize, double minconf, double minlift,
			CFITree cfiTree) throws IOException {
		// save the parameters
		this.minconf = minconf;
		this.cfiTree = cfiTree;
		usingLift = true;
		
		// start the algorithm
		return runAlgorithm(patterns, output, databaseSize);
	}

	/**
	 * Run the algorithm for generating association rules from a set of itemsets.
	 * @param patterns the set of itemsets
	 * @param output the output file path. If null the result is saved in memory and returned by the method.
	 * @param databaseSize  the number of transactions in the original database
	 * @return the set of rules found if the user chose to save the result to memory
	 * @throws IOException exception if error while writting to file
	 */
	private AssocRules runAlgorithm(Itemsets patterns, String output, int databaseSize)
			throws IOException {
		
		// if the user want to keep the result into memory
		if(output == null){
			writer = null;
			rules =  new AssocRules("ASSOCIATION RULES");
	    }else{ 
	    	// if the user want to save the result to a file
	    	rules = null;
			writer = new BufferedWriter(new FileWriter(output)); 
		}

		this.databaseSize = databaseSize;
		
		// record the time when the algorithm starts
		startTimestamp = System.currentTimeMillis();
		// initialize variable to count the number of rules found
		ruleCount = 0;
		// save itemsets in a member variable
		this.patterns = patterns;
		
		// SORTING
		// First, we sort all itemsets having the same size by lexical order
		// We do this for optimization purposes. If the itemsets are sorted, it allows to
		// perform two optimizations:
		// 2) When combining itemsets to generate candidate, we can use the
		//    lexical order to avoid comparisons (in the method "generateCandidates()").
		
		// For itemsets of the same size
		for(GenericResults.ListOfItemset itemsetsSameSize : patterns.getLevels()){
			// Sort by lexicographical order using a Comparator
			Collections.sort(itemsetsSameSize, new Comparator<AbstractItemset>() {
				@Override
				public int compare(AbstractItemset o1, AbstractItemset o2) {
					// The following code assume that itemsets are the same size
					//  we know we have tids bitset itemset's.... TODO remove this down cast
					return ArraysAlgos.comparatorItemsetSameSize.compare(((Itemset)o1).getItems(), ((Itemset)o2).getItems());
				}
			});
		}
		// END OF SORTING
		
		// Now we will generate the rules.
		
		// For each frequent itemset of size >=2 that we will name "lk"
		for (int k = 2; k < patterns.getLevels().size(); k++) {
			for (AbstractItemset lkAbs : patterns.getLevels().get(k)) {
				Itemset lk = (Itemset) lkAbs;

				// create a variable H1 for recursion
				List<int[]> H1_for_recursion = new ArrayList<int[]>();
				
				// For each itemset "itemsetSize1" of size 1 that is member of lk
				for(int item : lk.getItems()) {
					int itemsetHm_P_1[] = new int[] {item};
					
					if(lk.size() - 1 <= maxAntecedentLength){
	
						// make a copy of  lk without items from  hm_P_1
						int[] itemset_Lk_minus_hm_P_1 = ArraysAlgos.cloneItemSetMinusOneItem(lk.getItems(), item);
						
						// Now we will calculate the support and confidence
						// of the rule: itemset_Lk_minus_hm_P_1 ==>  hm_P_1
						int support = calculateSupport(itemset_Lk_minus_hm_P_1); // THIS COULD BE
																	// OPTIMIZED ?
						double supportAsDouble = (double) support;
						
						// calculate the confidence of the rule : itemset_Lk_minus_hm_P_1 ==>  hm_P_1
						double conf = lk.getAbsoluteSupport() / supportAsDouble;
	
						// if the confidence is lower than minconf
						if(conf < minconf || Double.isInfinite(conf)){
							continue;
						}
						
						double lift = 0;
						int supportHm_P_1 = 0;
						// if the user is using the minlift threshold, we will need
						// to also calculate the lift of the rule:  itemset_Lk_minus_hm_P_1 ==>  hm_P_1
						if(usingLift){
							// if we want to calculate the lift, we need the support of hm_P_1
							supportHm_P_1 = calculateSupport(itemsetHm_P_1);  // if we want to calculate the lift, we need to add this.
							// calculate the lift
							double term1 = ((double)lk.getAbsoluteSupport()) /databaseSize;
							double term2 = supportAsDouble /databaseSize;
							double term3 = ((double)supportHm_P_1 / databaseSize);
							lift = term1 / (term2 * term3);
							
							// if the lift is not enough
							if(lift < minlift){
								continue;
							}
						}
						
						// If we are here, it means that the rule respect the minconf and minlift parameters.
						// Therefore, we output the rule.
						saveRule(itemset_Lk_minus_hm_P_1, support, itemsetHm_P_1, supportHm_P_1, lk.getAbsoluteSupport(), conf, lift);

					}
					// Then we keep the itemset  hm_P_1 to find more rules using this itemset and lk.
					if(1 < maxConsequentLength ){
						H1_for_recursion.add(itemsetHm_P_1);
					}
						// ================ END OF WHAT I HAVE ADDED
				}
				
				// Finally, we make a recursive call to continue explores rules that can be made with "lk"
				apGenrules(k, 1, lk, H1_for_recursion);
			}
		}

		// close the file if we saved the result to a file
		if(writer != null){
			writer.close();
		}
		// record the end time of the algorithm execution
		endTimeStamp = System.currentTimeMillis();
		
		// Return the rules found if the user chose to save the result to memory rather than a file.
		// Otherwise, null will be returned
		return rules;
	}

	/**
	 * The ApGenRules as described in p.14 of the paper by Agrawal.
	 * (see the Agrawal paper for more details).
	 * @param k the size of the first itemset used to generate rules
	 * @param m the recursive depth of the call to this method (first time 1, then 2...)
	 * @param lk the itemset that is used to generate rules
	 * @param Hm a set of itemsets that can be used with lk to generate rules
	 * @throws IOException exception if error while writing output file
	 */
	public void apGenrules(int k, int m, Itemset lk, List<int[]> Hm)
			throws IOException {
		
		// if the itemset "lk" that is used to generate rules is larger than the size of itemsets in "Hm"
		if (k > m + 1) {
			// Create a list that we will be used to store itemsets for the recursive call
			List<int[]> Hm_plus_1_for_recursion = new ArrayList<int[]>();
			
			// generate candidates using Hm
			List<int[]> Hm_plus_1 = generateCandidateSizeK(Hm);
			
			// for each such candidates
			for (int[] hm_P_1 : Hm_plus_1) {
				
				if(lk.size() - hm_P_1.length <= maxAntecedentLength){
				
					// We subtract the candidate from the itemset "lk"
					int[] itemset_Lk_minus_hm_P_1 =  ArraysAlgos.cloneItemSetMinusAnItemset(lk.getItems(), hm_P_1);
	
					// We will now calculate the support of the rule  Lk/(hm_P_1) ==> hm_P_1
					// we need it to calculate the confidence
					int support = calculateSupport(itemset_Lk_minus_hm_P_1); 
					
					double supportAsDouble = (double)support;
					
					// calculate the confidence of the rule Lk/(hm_P_1) ==> hm_P_1
					double conf = lk.getAbsoluteSupport() / supportAsDouble;
	
					// if the confidence is not enough than we don't need to consider
					// the rule  Lk/(hm_P_1) ==> hm_P_1 anymore so we continue 
					if(conf < minconf || Double.isInfinite(conf)){
						continue;
					}
					
					double lift = 0;
					int supportHm_P_1 = 0;
					// if the user is using the minlift threshold, then we will need to calculate the lift of the
					// rule as well and check if the lift is higher or equal to minlift.
					if(usingLift){
						// if we want to calculate the lift, we need the support of Hm+1
						supportHm_P_1 = calculateSupport(hm_P_1);  
						// calculate the lift of the rule:  Lk/(hm_P_1) ==> hm_P_1
						double term1 = ((double)lk.getAbsoluteSupport()) /databaseSize;
						double term2 = (supportAsDouble) /databaseSize;
						
						 lift = term1 / (term2 * ((double)supportHm_P_1 / databaseSize));
	
						// if the lift is not enough
						if(lift < minlift){
							continue;
						}
					}
				
					// The rule has passed the confidence and lift threshold requirements,
					// so we can output it
					saveRule(itemset_Lk_minus_hm_P_1, support, hm_P_1, supportHm_P_1, lk.getAbsoluteSupport(), conf, lift);
				}	
				// if k == m+1, then we cannot explore further rules using Lk since Lk will be too small.
				if(k != m+1 && m + hm_P_1.length <= maxConsequentLength) {
					Hm_plus_1_for_recursion.add(hm_P_1);
				}
			}
			// recursive call to apGenRules to find more rules using "lk"
			apGenrules(k, m + 1, lk, Hm_plus_1_for_recursion);
		}
	}
	
	/**
	 * Calculate the support of an itemset by looking at the frequent patterns
	 * of the same size.
	 * Because patterns are sorted by lexical order, we use a binary search.
	 * This is MUCH MORE efficient than just browsing the full list of patterns.
	 * An alternative would be to use a trie to store patterns but it may require a bit more memory.
	 * 
	 * @param itemset the itemset.
	 * @return the support of the itemset
	 */
	public int calculateSupport(int[] itemset) {
		//**************************************************************************************
		//  THIS IS THE MAIN DIFFERENCE WITH THE OTHER IMPLEMENTATION FOR MINING CLOSED RULES
		//  We will calculate the support of an itemset using the CFI-tree structure generated by
		// FPClose.
		//**************************************************************************************
		return cfiTree.calculateSupport(itemset);
	}
}
