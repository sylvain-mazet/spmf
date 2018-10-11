package ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.patterns.AbstractItemset;

import java.util.ArrayList;

/**
 * Created by smazet on 11/10/18.
 */
public class ListOfTIDSBitSetItemset extends ArrayList<AbstractItemset> implements GenericResults.ListOfItemset {
    @Override
    public GenericResults.ItemsetType getTypeOfItemset() {
        return GenericResults.ItemsetType.ARRAY_INTEGERS_WITH_TIDS_BITSET;
    }
}
