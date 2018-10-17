package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;

import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;

/**
 * Created by smazet on 11/10/18.
 *
 * A FINode with Itemset, for reduced trees
  */
public class FICNode extends FINode {

    AbstractItemset itemset;

    public FICNode() {}
    public FICNode(int itemID, ItemsetArrayImplWithCount itemset) {
        super(itemID, itemset.getAbsoluteSupport(), itemset.size());
        this.itemset = itemset;
    }

    public FICNode(int itemID, AbstractItemset itemset) {
        super(itemID, itemset.getAbsoluteSupport(), itemset.size());
        this.itemset = itemset;
    }

    public AbstractItemset getItemset() {
        return itemset;
    }
}
