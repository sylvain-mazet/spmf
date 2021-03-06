 package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;
 
 /* This file is copyright (c) 2008-2015 Philippe Fournier-Viger
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


import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetsArrayIntegerWithCount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

 /**
 * This is an implementation of the FPMax algorithm (Grahne et al., 2004).
 * FPGrowth is described here:
 * <br/><br/>
 * 
 * High Performance Mining of Maximal Frequent Itemsets (with J. Zhu).
 *  6th International Workshop on High Performance Data Mining (HPDM '03)  * <br/><br/>
 * 
 * This is an optimized version that saves the result to a file
 * or keep it into memory if no output path is provided
 * by the user to the runAlgorithm method().
 *
 * @see FPTree
 * @see ItemsetArrayImplWithCount
 * @see ItemsetsArrayIntegerWithCount
 * @author Philippe Fournier-Viger, 2015
 */
public class AlgoFPMax extends GenericFPGrowthAlgorithmBase {

	// buffer for storing the current itemset that is mined when performing mining
	// the idea is to always reuse the same buffer to reduce memory usage.
	private int[] itemsetBuffer = null;
	
	// This is the MFI tree for storing maximal itemsets
	public FITree mfiTree = null;
	
	/**
	 * Constructor
	 */
	public AlgoFPMax() {
		super(null);
	}

	@Override
	protected void bottomHalf(FPTree tree) throws IOException {

		// Create the MFI Tree
		mfiTree = new FITree();
		mfiTree.setRoot(new FINode());

		// We create the header table for the tree using the calculated support of single items
		tree.createHeaderList(getOriginalMapSupport());
		

		// (5) We start to mine the FP-Tree by calling the recursive method.
		// Initially, the prefix alpha is empty.
		// if at least an item is frequent
		if(tree.headerList.size() > 0) {
			// initialize the buffer for storing the current itemset
			itemsetBuffer = new int[BUFFERS_SIZE];
			// Next we will recursively generate frequent itemsets using the fp-tree
			fpMax(tree, itemsetBuffer, 0, getTransactionCount(), getOriginalMapSupport());
		}
		
	}

	
	/**
	 * Mine an FP-Tree having more than one path.
	 * @param tree  the FP-tree
	 * @param prefix  the current prefix, named "alpha"
	 * @param mapSupport the frequency of items in the FP-Tree
	 * @throws IOException  exception if error writing the output file
	 */
	private void fpMax(FPTree tree, int [] prefix, int prefixLength, int prefixSupport, Map<Integer, Integer> mapSupport) throws IOException {
//		======= DEBUG ========
		if(DEBUG) {
			System.out.print("###### Prefix: ");
			for(int k=0; k< prefixLength; k++) {
				System.out.print(prefix[k] + "  ");
			}
			System.out.println("\n");
			System.out.println(tree);
		}	//========== END DEBUG =======
		
		// We first check if the FPtree contains a single path
		// We will check if the FPtree contains a single path
		boolean singlePath = true;
		// We will use a variable to keep the support of the single path if there is one
		int singlePathSupport = 0;
		// This variable is used to count the number of items in the single path  (if there
		// is one + the prefix length
		int position = prefixLength;
		if(tree.root.childs.size() > 1) {
			// if the root has more than one child, than it is not a single path
			singlePath = false;
		}else {
			// if the root has exactly one child, we need to recursively check childs
			// of the child to see if they also have one child
			FPNode currentNode = tree.root.childs.get(0);
			while(true){
				// if the current child has more than one child, it is not a single path!
				if(currentNode.childs.size() > 1) {
					singlePath = false;
					break;
				}
				// otherwise, we copy the current item in the buffer and move to the child
				itemsetBuffer[position] = currentNode.itemID;
				singlePathSupport = currentNode.counter;
				position++;
				// if this node has no child, that means that this is the end of this path
				// and it is a single path, so we break
				if(currentNode.childs.size() == 0) {
					break;
				}
				currentNode = currentNode.childs.get(0);
			}
		}
		
		// Case 1: the FPtree contains a single path
		if(singlePath && singlePathSupport >= getMinSupportRelative()){
			// We save the path, because it is a maximal itemset
			saveItemset(mfiTree, itemsetBuffer, position, singlePathSupport);
		}else {
			// Case 2: There are multiple paths.
			
			// For each frequent item in the header table list of the tree in reverse order. (in decreasing order of support...)
			for(int i = tree.headerList.size()-1; i>=0; i--){
				// get the item
				Integer item = tree.headerList.get(i);
				
				// get the item support
				int support = mapSupport.get(item);

				// Create Beta by concatening item to the current prefix  alpha
				prefix[prefixLength] = item;
				
				// calculate the support of the new prefix beta
				int betaSupport = (prefixSupport < support) ? prefixSupport: support;

				
				// === (A) Construct beta's conditional pattern base ===
				// It is a subdatabase which consists of the set of prefix paths
				// in the FP-tree co-occuring with the prefix pattern.
				List<List<FPNode>> prefixPaths = new ArrayList<List<FPNode>>();
				FPNode path = tree.mapItemNodes.get(item);
				
				// Map to count the support of items in the conditional prefix tree
				// Key: item   Value: support
				Map<Integer, Integer> mapSupportBeta = new HashMap<Integer, Integer>();
				
				while(path != null){
					// if the path is not just the root node
					if(path.parent.itemID != -1){
						// create the prefixpath
						List<FPNode> prefixPath = new ArrayList<FPNode>();
						// add this node.
						prefixPath.add(path);   // NOTE: we add it just to keep its support,
						// actually it should not be part of the prefixPath
						
						// ####
						int pathCount = path.counter;
						
						//Recursively add all the parents of this node.
						FPNode parent = path.parent;
						while(parent.itemID != -1){
							prefixPath.add(parent);
							
							// FOR EACH PATTERN WE ALSO UPDATE THE ITEM SUPPORT AT THE SAME TIME
							// if the first time we see that node id
							if(mapSupportBeta.get(parent.itemID) == null){
								// just add the path count
								mapSupportBeta.put(parent.itemID, pathCount);
							}else{
								// otherwise, make the sum with the value already stored
								mapSupportBeta.put(parent.itemID, mapSupportBeta.get(parent.itemID) + pathCount);
							}
							parent = parent.parent;
						}
						// add the path to the list of prefixpaths
						prefixPaths.add(prefixPath);
					}
					// We will look for the next prefixpath
					path = path.nodeLink;
				}

				
				// ===== FPMAX ======
				// concatenate Beta with all the frequent itemsets in the pattern base
				// to get head U P
				List<Integer> headWithP = new ArrayList<Integer>(mapSupportBeta.size() + prefixLength+1);
				// concatenate the prefix
				for(int z=0; z < prefixLength+1; z++) {
					headWithP.add(prefix[z]);
				}
				// concatenate the other FREQUENT items in the pattern base
				// for each item
				for(Entry<Integer,Integer> entry: mapSupportBeta.entrySet()) {
					// if the item is frequent
					if(entry.getValue() >= getMinSupportRelative()) {
						headWithP.add(entry.getKey());
					}
				}
				
				// Sort Head U P according to the original header list total order on items
				// sort item in the transaction by descending order of support
				Collections.sort(headWithP, comparatorOriginalOrder);

				//======= DEBUG ========
				if(DEBUG) {
					System.out.println(" CHECK2 : " + headWithP);
				}
				//========== END DEBUG =======
				
				// CHECK IF HEAD U P IS A SUBSET OF A MFI ACCORDING TO THE MFI-TREE
				if(mfiTree.passSubsetChecking(headWithP)) {
					
					if(DEBUG) {
						System.out.println("    passed!");
					}
					// (B) Construct beta's conditional FP-Tree using its prefix path
					// Create the tree.
					FPTree treeBeta = new FPTree();
					// Add each prefixpath in the FP-tree.
					for(List<FPNode> prefixPath : prefixPaths){
						treeBeta.addPrefixPath(prefixPath, mapSupportBeta, getMinSupportRelative());
					}  
					// Mine recursively the Beta tree if the root has child(s)
					if(treeBeta.root.childs.size() > 0){

						// Create the header list.
						treeBeta.createHeaderList(getOriginalMapSupport());
						
						// recursive call
						fpMax(treeBeta, prefix, prefixLength+1, betaSupport, mapSupportBeta);
					}
					
					// ======= After that, we still need to check if beta is a maximal itemset ====
					List<Integer> temp = new ArrayList<>(mapSupportBeta.size() + prefixLength+1);
					for(int z=0; z < prefixLength+1; z++) {
						temp.add(prefix[z]);
					}
					Collections.sort(temp, comparatorOriginalOrder);
					// if beta pass the test, we save it
					if(mfiTree.passSubsetChecking(temp)) {
						saveItemset(mfiTree, prefix, prefixLength+1, betaSupport);
					}
					//===========================================================
				}
				else if (DEBUG){
					System.out.println("     failed!");
				}
			}
		}
	}

	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	@Override
	public void printStats() {
		printStats("FP-Max","v0.96r14");
	}

	@Override
	public FITree getFiTree() {
		return mfiTree;
	}

}
