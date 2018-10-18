package ca.pfv.spmf.algorithms;

import ca.pfv.spmf.patterns.AbstractItemset;

import java.util.List;

/**
 * Created by smazet on 10/10/18.
 */
public interface GenericResults {

    enum ItemsetType {
        ARRAY_INTEGERS_WITH_COUNT,
        ARRAY_INTEGERS_FOR_DELTA,
        ARRAY_INTEGERS_WITH_TIDS_BITSET;
    }

    interface ListOfItemset extends List<AbstractItemset> {
        ItemsetType getTypeOfItemset();
    }

    List<ListOfItemset> getLevels();
}
