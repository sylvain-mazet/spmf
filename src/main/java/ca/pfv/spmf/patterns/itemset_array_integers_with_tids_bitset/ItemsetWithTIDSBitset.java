package ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset;

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

import ca.pfv.spmf.patterns.AbstractOrderedItemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;

import java.util.BitSet;
import java.util.Vector;

/**
 * This class represents an itemset (a set of items) where the itemset is an array of integers 
 * sorted by lexical order, an item should not appear more than once, 
 * the ids of transactions/sequences containing this itemset is represented
 * as a bitset.
* 
* 
* @see AbstractOrderedItemset
 * @author Philippe Fournier-Viger
 */
public class ItemsetWithTIDSBitset extends ItemsetArrayImplWithCount {

	/** The list of transactions/sequences containing this itemset **/
	private BitSet transactionsIds;
	public int cardinality =0;  // the cardinality of the above bitset
	
	/**
	 * Constructor of an empty itemset
	 */
	public ItemsetWithTIDSBitset(){
		transactionsIds = new BitSet();
		itemset = new int[0];
	}
	
	/**
	 * Constructor of an empty itemset
	 * @param itemset the itemset
	 * @param bitset the tidset of the itemset
	 * @param support the tidset cardinality (support)
	 */
	public ItemsetWithTIDSBitset(int[] itemset, BitSet bitset, int support){
		this.transactionsIds = bitset;
		this.itemset = itemset;
		this.cardinality = support;
	}
	
	/**
	 * Constructor 
	 * @param item an item that should be added to the new itemset
	 */
	public ItemsetWithTIDSBitset(int item){
		itemset = new int[]{item};
	}
	
	/**
	 * Constructor 
	 * @param items an array of items that should be added to the new itemset
	 */
	public ItemsetWithTIDSBitset(int[] items){
		this.itemset = items;
	}

	/**
	 * Get the support of this itemset
	 * @return the support of this itemset
	 */
	public int getAbsoluteSupport(){
		return cardinality;
	}

	/**
	 * Set the list of transaction/sequence ids containing this itemset.
	 * @param listTransactionIds the list of transaction/sequence ids.
	 * @param cardinality the cardinality of the list.
	 */
	public void setTIDs(BitSet listTransactionIds, int cardinality) {
		this.transactionsIds = listTransactionIds;
		this.cardinality = cardinality;
	}

	/**
	 * Get the list of transactions/sequences containing this itemset.
	 * @return the list as a bitset.
	 */
	public BitSet getTransactionsIds() {
		return transactionsIds;
	}


}
