package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;

import ca.pfv.spmf.patterns.AbstractItemset;

/**
 * Created by smazet on 11/10/18.
 */
public class FIBNode extends FICNode {

    double deltaSupportOut;
    double deltaSupportIn;
    double deltaLengthOut;
    double deltaLengthIn;
    private double lift;
    private int coverage;

    /**
     * for tests
     */
    public FIBNode() {
        super();
    }
    public FIBNode(int itemID, AbstractItemset itemset) {
        super(itemID,itemset);
    }

    public double getDeltaSupportOut() {
        return deltaSupportOut;
    }

    public void setDeltaSupportOut(double deltaSupportOut) {
        this.deltaSupportOut = deltaSupportOut;
    }

    public double getDeltaSupportIn() {
        return deltaSupportIn;
    }

    public void setDeltaSupportIn(double deltaSupportIn) {
        this.deltaSupportIn = deltaSupportIn;
    }

    public double getDeltaLengthOut() {
        return deltaLengthOut;
    }

    public void setDeltaLengthOut(double deltaLengthOut) {
        this.deltaLengthOut = deltaLengthOut;
    }

    public double getDeltaLengthIn() {
        return deltaLengthIn;
    }

    public void setDeltaLengthIn(double deltaLengthIn) {
        this.deltaLengthIn = deltaLengthIn;
    }

    /**
     * Method for getting a string representation of this node and descendants
     * (to be used for debugging purposes).
     * @param indent indentation
     * @return a string
     */
    @Override
    public String toString(String indent) {
        StringBuilder output = new StringBuilder();
        output.append(""+ itemID);
        output.append(" (delta Support in = "+ deltaSupportIn+" / out = "+deltaSupportOut);
        output.append(" delta Length in = "+ deltaLengthIn+" / out = "+deltaLengthOut);
        output.append(")\n");
        String newIndent = indent + "   ";
        for (FINode child : children) {
            output.append(newIndent+ child.toString(newIndent));
        }
        return output.toString();
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
