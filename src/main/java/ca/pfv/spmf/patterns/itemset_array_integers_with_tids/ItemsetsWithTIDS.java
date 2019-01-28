package ca.pfv.spmf.patterns.itemset_array_integers_with_tids;

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


import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.Itemsets;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a set of itemsets where an itemset is an array of integer with a tid list
 * represented by a list of integers. Itemsets are ordered by size. For
 * example, level 1 means itemsets of size 1 (that contains 1 item).
* 
 * @author Philippe Fournier-Viger
 */
public class ItemsetsWithTIDS implements Itemsets {
	/** We store the itemsets in a list named "levels".
	 Position i in "levels" contains the list of itemsets of size i */
	private final List<ListOfItemsetWithTIDS> levels = new ArrayList<>();
	/** the total number of itemsets */
	private int itemsetsCount = 0;
	/** a name that we give to these itemsets (e.g. "frequent itemsets") */
	private final String name;

	/**
	 * Constructor
	 * @param name the name of these itemsets
	 */
	public ItemsetsWithTIDS(String name) {
		this.name = name;
		levels.add(new ListOfItemsetWithTIDS()); // We create an empty level 0 by
												// default.
	}

	/**
	 * Print all itemsets to System.out, ordered by their size.
	 * @param nbObject The number of transaction/sequence in the database where
	 * there itemsets were found.
	 */
	@Override
	public void printItemsets(int nbObject) {
		System.out.println(" ------- " + name + " -------");
		int patternCount = 0;
		int levelCount = 0;
		// for each level (a level is a set of itemsets having the same number of items)
		for (ListOfItemsetWithTIDS level : levels) {
			// print how many items are contained in this level
			System.out.println("  L" + levelCount + " ");
			// for each itemset
			for (AbstractItemset itemset : level) {
				// print the itemset
				System.out.print("  pattern " + patternCount + ":  ");
				itemset.print();
				// print the support of this itemset
				System.out.print("support :  "
						+ itemset.getRelativeSupportAsString(nbObject));
				patternCount++;
				System.out.println("");
			}
			levelCount++;
		}
		System.out.println(" --------------------------------");
	}

	/** 
	 * Add an itemset to this structure
	 * @param itemset the itemset
	 * @param k the number of items contained in the itemset
	 */
	public void addItemset(ItemsetWithTIDS itemset, int k) {
		while (levels.size() <= k) {
			levels.add(new ListOfItemsetWithTIDS());
		}
		levels.get(k).add(itemset);
		itemsetsCount++;
	}

	/**
	 * Get all itemsets.
	 * @return A list of list of itemsets.
	 * Position i in this list is the list of itemsets of size i.
	 */
	@Override
	public List<ListOfItemset> getLevels() {
		/*
		 * @todo fix the getLevels() for this itemset type...
		 */
		throw new UnsupportedOperationException("Not implemented");
		//return levels;
	}

	/**
	 * Get the total number of itemsets
	 * @return the number of itemsets.
	 */
	public int getItemsetsCount() {
		return itemsetsCount;
	}
}
