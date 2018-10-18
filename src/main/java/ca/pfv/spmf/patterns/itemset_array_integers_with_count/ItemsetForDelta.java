package ca.pfv.spmf.patterns.itemset_array_integers_with_count;

import java.util.Collection;

/**
 * Created by smazet on 17/10/18.
 */
public class ItemsetForDelta extends ItemsetArrayImplWithCount {

    private double deltaSupportIn;
    private double deltaSupportOut;
    private double deltaLengthIn;
    private double deltaLengthOut;
    private double lift;
    private int coverage;

    /**
     * Constructor
     * @param item an item that should be added to the new itemset
     */
    public ItemsetForDelta(int item){
        super(item);
    }

    /**
     * Constructor
     * @param items an array of items that should be added to the new itemset
     */
    public ItemsetForDelta(int[] items){
        super(items);
    }

    /**
     * Constructor
     * @param itemset a list of Integer representing items in the itemset
     * @param support the support of the itemset
     */
    public ItemsetForDelta(Collection<Integer> itemset, int support){
        super(itemset,support);
    }

    /**
     * Constructor
     * @param itemset a list of Integer representing items in the itemset
     */
    public ItemsetForDelta(Collection<Integer> itemset){
        super(itemset);
    }

    public double getDeltaSupportIn() {
        return deltaSupportIn;
    }

    public void setDeltaSupportIn(double deltaSupportIn) {
        this.deltaSupportIn = deltaSupportIn;
    }

    public double getDeltaSupportOut() {
        return deltaSupportOut;
    }

    public void setDeltaSupportOut(double deltaSupportOut) {
        this.deltaSupportOut = deltaSupportOut;
    }

    public double getDeltaLengthIn() {
        return deltaLengthIn;
    }

    public void setDeltaLengthIn(double deltaLengthIn) {
        this.deltaLengthIn = deltaLengthIn;
    }

    public double getDeltaLengthOut() {
        return deltaLengthOut;
    }

    public void setDeltaLengthOut(double deltaLengthOut) {
        this.deltaLengthOut = deltaLengthOut;
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public double getLift() {
        return lift;
    }

    public void setCoverage(int coverage) {
        this.coverage = coverage;
    }

    public int getCoverage() {
        return coverage;
    }
}
