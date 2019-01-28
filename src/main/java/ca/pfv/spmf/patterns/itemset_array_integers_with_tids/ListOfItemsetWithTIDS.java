package ca.pfv.spmf.patterns.itemset_array_integers_with_tids;

import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.Itemsets;

import java.util.ArrayList;

/**
 * Created by smazet on 22/01/19.
 */
public class ListOfItemsetWithTIDS extends ArrayList<AbstractItemset> implements Itemsets.ListOfItemset {
    @Override
    public Itemsets.ItemsetType getTypeOfItemset() {
        return Itemsets.ItemsetType.ARRAY_INTEGERS_WITH_TIDS;
    }
}
