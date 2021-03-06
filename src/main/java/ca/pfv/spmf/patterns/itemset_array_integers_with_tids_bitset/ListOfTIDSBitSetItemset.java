package ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset;

import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.Itemsets;

import java.util.ArrayList;

/**
 * Created by smazet on 11/10/18.
 */
public class ListOfTIDSBitSetItemset extends ArrayList<AbstractItemset> implements Itemsets.ListOfItemset {
    @Override
    public Itemsets.ItemsetType getTypeOfItemset() {
        return Itemsets.ItemsetType.ARRAY_INTEGERS_WITH_TIDS_BITSET;
    }
}
