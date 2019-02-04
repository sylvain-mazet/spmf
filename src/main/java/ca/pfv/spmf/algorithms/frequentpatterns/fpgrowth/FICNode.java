package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;

import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;

import java.util.Iterator;

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

    public String toString(String indent) {
        StringBuilder output = new StringBuilder();
        output.append(""+ itemID);
        output.append(" (count="+ counter);
        output.append(" level="+ level);
        output.append(") [");
        int i =1;
        Iterator<Integer> itemIterator = itemset.iterator();
        while (itemIterator.hasNext()) {
            Integer item = itemIterator.next();
            if (i>1) {
                output.append(",");
            }
            output.append(" "+item);
            i++;
        }
        output.append("]\n");
        String newIndent = indent + "   ";
        if (newIndent.length() > 160) {
            output.append(newIndent+"etc.....");
        } else {
            for (FINode child : children) {
                output.append(newIndent + child.toString(newIndent));
            }
        }
        return output.toString();
    }
}
