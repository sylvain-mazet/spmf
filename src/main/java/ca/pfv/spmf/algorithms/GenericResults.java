package ca.pfv.spmf.algorithms;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FITree;
import ca.pfv.spmf.patterns.Itemsets;

import java.util.List;

/**
 * Created by smazet on 10/10/18.
 */
public interface GenericResults {

    Itemsets getItemsets();

    List<Itemsets.ListOfItemset> getLevels();

    FITree getFITree();
}
