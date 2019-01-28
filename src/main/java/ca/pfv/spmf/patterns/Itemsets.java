package ca.pfv.spmf.patterns;

import java.util.List;

/**
 * Created by smazet on 22/01/19.
 */
public interface Itemsets {

    /**
     * Print all itemsets to System.out, ordered by their size.
     * @param nbObject The number of transaction/sequence in the database where
     * there itemsets were found.
     */
    void printItemsets(int nbObject);

    List<ListOfItemset> getLevels();

    enum ItemsetType {
        ARRAY_INTEGERS_WITH_COUNT,
        ARRAY_INTEGERS_FOR_DELTA,
        ARRAY_INTEGERS_WITH_TIDS_BITSET,
        ARRAY_INTEGERS_WITH_TIDS;
    }

    interface ListOfItemset extends List<AbstractItemset> {
        ItemsetType getTypeOfItemset();
    }
}
