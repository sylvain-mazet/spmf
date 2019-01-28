package ca.pfv.spmf.algorithms.frequentpatterns;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FITree;
import ca.pfv.spmf.patterns.Itemsets;

import java.util.List;

/**
 * Created by smazet on 22/01/19.
 */
public class FrequentPatternsResults implements GenericResults {

    FITree fiTree;
    Itemsets itemsets;

    public FrequentPatternsResults(Itemsets itemsets, FITree fiTree) {
        this.fiTree = fiTree;
        this.itemsets = itemsets;
    }

    @Override
    public Itemsets getItemsets() {
        return itemsets;
    }

    @Override
    public List<Itemsets.ListOfItemset> getLevels() {
        return itemsets.getLevels();
    }

    @Override
    public FITree getFITree() {
        return fiTree;
    }

}
