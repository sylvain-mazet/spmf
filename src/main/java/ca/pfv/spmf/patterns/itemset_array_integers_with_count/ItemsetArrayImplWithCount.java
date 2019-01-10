package ca.pfv.spmf.patterns.itemset_array_integers_with_count;
import ca.pfv.spmf.algorithms.ArraysAlgos;
import ca.pfv.spmf.patterns.AbstractOrderedItemset;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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

/**
 * This class represents an itemset (a set of items) implemented as an array of integers with
 * a variable to store the support count of the itemset.
* 
 * @author Philippe Fournier-Viger
 */
public class ItemsetArrayImplWithCount extends AbstractOrderedItemset {

	protected final Integer INITIAL_CAPACITY = 10;

	/** the array of items **/
	public int[] itemset;

	/**  the support of this itemset */
	public int support = 0; 
	
	/**
	 * Get the items as vector
	 * @return the items
	 */
	@Override
	public int[] getItems() {
		return itemset;
	}
	
	/**
	 * Constructor
	 */
	public ItemsetArrayImplWithCount(){
		itemset = new int[INITIAL_CAPACITY];
	}

	/**
	 * Constructor, copy values
	 * @param setOfInts a set of Integer
	 * */
	public ItemsetArrayImplWithCount(Collection<Integer> setOfInts){
		itemset = new int[setOfInts.size()];
		int pos = 0;
		for (Integer i : setOfInts) {
			itemset[pos++] = i;
		}
		Arrays.sort(itemset);
	}

	/**
	 * Constructor 
	 * @param item an item that should be added to the new itemset
	 */
	public ItemsetArrayImplWithCount(int item){
		itemset = new int[]{item};
	}

	/**
	 * Constructor 
	 * @param items an array of items that should be added to the new itemset
	 */
	public ItemsetArrayImplWithCount(int[] items){
		this.itemset = items;
	}
	
	/**
	 * Constructor 
	 * @param itemset a list of Integer representing items in the itemset
	 * @param support the support of the itemset
	 */
	public ItemsetArrayImplWithCount(Collection<Integer> itemset, int support){
		this.itemset = new int[itemset.size()];
	    int i = 0;
	    for (Integer item : itemset) { 
	    	this.itemset[i++] = item.intValue();
	    }
	    this.support = support;
	}
	
	/**
	 * Get the support of this itemset
	 */
	@Override
	public int getAbsoluteSupport(){
		return support;
	}
	
	/**
	 * Get the size of this itemset 
	 */
	@Override
	public int size() {
		return itemset.length;
	}

	/**
	 * Get the item at a given position in this itemset
	 */
	public Integer get(int position) {
		return itemset[position];
	}

	/**
	 * Set the support of this itemset
	 * @param support the support
	 */
	public void setAbsoluteSupport(Integer support) {
		this.support = support;
	}

	/**
	 * Increase the support of this itemset by 1
	 */
	public void increaseTransactionCount() {
		this.support++;
	}

	/**
	 * Make a copy of this itemset but exclude a given item
	 * @param itemToRemove the given item
	 * @return the copy
	 */
	public ItemsetArrayImplWithCount cloneItemSetMinusOneItem(Integer itemToRemove) {
		int[] newItemset = ArraysAlgos.cloneItemSetMinusOneItem(this.itemset,itemToRemove);
		return new ItemsetArrayImplWithCount(newItemset); // return the copy
	}
	

	/**
	 * Make a copy of this itemset but exclude a set of items
	 * @param itemsetToNotKeep the set of items to be excluded
	 * @return the copy
	 */
	public ItemsetArrayImplWithCount cloneItemSetMinusAnItemset(ItemsetArrayImplWithCount itemsetToNotKeep) {
		int[] newItemset = ArraysAlgos.cloneItemSetMinusAnItemset(this.itemset, itemsetToNotKeep.itemset);
		return new ItemsetArrayImplWithCount(newItemset); // return the copy
	}
	
	/**
	 * This method return an itemset containing items that are included
	 * in this itemset and in a given itemset
	 * @param itemset2 the given itemset
	 * @return the new itemset
	 */
	public ItemsetArrayImplWithCount intersection(ItemsetArrayImplWithCount itemset2) {
		int[] intersection = ArraysAlgos.intersectTwoSortedArrays(this.getItems(), itemset2.getItems());
		return new ItemsetArrayImplWithCount(intersection);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(itemset);
	}

	@Override //TODO the iterator
	public Iterator<Integer> iterator() {
		return null;
	}
}
