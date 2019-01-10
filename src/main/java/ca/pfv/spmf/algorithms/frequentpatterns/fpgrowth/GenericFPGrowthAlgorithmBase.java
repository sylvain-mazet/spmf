package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;

import ca.pfv.spmf.algorithms.DbScanner;
import ca.pfv.spmf.algorithms.GenericAlgorithmBase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.MemoryLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by smazet on 10/10/18.
 */
public abstract class GenericFPGrowthAlgorithmBase extends GenericAlgorithmBase {

    // Map to store the support of single items in the original database
    private Map<Integer, Integer> originalMapSupport = null;

    protected GenericFPGrowthAlgorithmBase(String output) {
        super(output);
    }

    protected abstract void bottomHalf(FPTree tree) throws IOException;

    /**
     * return the CFI, much more information than in "patterns":
     * contains also the CFIs arranged in a tree structure
     */
    public abstract  FITree getFiTree();

    public Map<Integer, Integer> getOriginalMapSupport() {
        return originalMapSupport;
    }

    /**
     * Method to run the FPXXX algorithms
     * Note: the results are returned by the method. Moreover the CFITree is publicly accessible.
     *
     * @param dbScanner the DbScanner implementation, to scan the transaction database.
     * @param minsupp the minimum support threshold.
     * @return the results.
     * @throws IOException exception if error reading or writing files
     */
    @Override
    public Itemsets runAlgorithm(DbScanner dbScanner, double minsupp) throws IOException {

        // record start time
        startTimestamp = System.currentTimeMillis();

        // (1) PREPROCESSING: Initial database scan to determine the frequency of each item
        // The frequency is stored in a map:
        //    key: item   value: support
        originalMapSupport = scanDatabaseToDetermineFrequencyOfSingleItems(dbScanner);

        // convert the minimum support as percentage to a
        // relative minimum support
        this.minSupportRelative = (int) Math.ceil(minsupp * getTransactionCount());

        firstPassMillis = System.currentTimeMillis() - startTimestamp;

        // record start time
        startTimestamp = System.currentTimeMillis();

        // (2) Scan the database again to build the initial FP-Tree
        // Before inserting a transaction in the FPTree, we sort the items
        // by descending order of support.  We ignore items that
        // do not have the minimum support.
        FPTree tree = new FPTree();

        DbScanner.DbIterator dbIterator = dbScanner.dbIterator();
        List<Integer> transaction = new ArrayList<Integer>();
        while (dbIterator.hasNext()) {

            DbScanner.TransactionIterator transactionIterator = dbIterator.next();

            transaction.clear();

            // for each item in the transaction
            while (transactionIterator.hasNext()) {
                Integer item = transactionIterator.next();
                // only add items that have the minimum support
                if (originalMapSupport.get(item) >= minSupportRelative) {
                    transaction.add(item);
                }
            }
            // sort item in the transaction by descending order of support
            Collections.sort(transaction, comparatorOriginalOrder);
            // add the sorted transaction to the fptree.
            tree.addTransaction(transaction);

        }

        secondPassMillis = System.currentTimeMillis() - startTimestamp;

        // record start time
        startTimestamp = System.currentTimeMillis();
        // number of itemsets found
        itemsetCount = 0;

        //initialize tool to record memory usage
        MemoryLogger.getInstance().reset();
        MemoryLogger.getInstance().checkMemory();

        // if the user want to keep the result into memory
        if(getOutput() == null){
            writer = null;
            patterns =  new Itemsets("FREQUENT ITEMSETS");
        }else{ // if the user want to save the result to a file
            patterns = null;
            writer = new BufferedWriter(new FileWriter(getOutput()));
        }

        // bottom half
        bottomHalf(tree);

        // close the output file if the result was saved to a file
        if(writer != null){
            writer.close();
        }
        // record the execution end time
        endTime= System.currentTimeMillis();

        // check the memory usage
        MemoryLogger.getInstance().checkMemory();

        // return the result (if saved to memory)
        return patterns;
    }

    /**
     * Write a frequent itemset that is found to the output file or
     * keep into memory if the user prefer that the result be saved into memory.
     */
    protected void saveItemset(FITree tree, int [] itemset, int itemsetLength, int support) throws IOException {

        // copy the itemset in the output buffer and sort items according to the
        // order of decreasing support in the original database
        int[] itemsetCopy = new int[itemsetLength];
        System.arraycopy(itemset, 0, itemsetCopy, 0, itemsetLength);
        sortOriginalOrder(itemsetCopy, itemsetLength);

        if(DEBUG) {
            //		//======= DEBUG ========
            System.out.print(" ##### SAVING : ");
            for(int i=0; i< itemsetLength; i++) {
                System.out.print(itemsetCopy[i] + "  ");
            }
            System.out.println("\n");
            //		//========== END DEBUG =======
        }

        // add the itemset to the FI-TREE
        tree.addItemset(itemsetCopy, itemsetCopy.length, support);

        // increase the number of itemsets found for statistics purpose
        itemsetCount++;

        // if the result should be saved to a file
        if(writer != null){

            // Create a string buffer
            StringBuilder buffer = new StringBuilder();
            // write the items of the itemset
            for(int i=0; i< itemsetLength; i++){
                buffer.append(itemsetCopy[i]);
                if(i != itemsetLength-1){
                    buffer.append(' ');
                }
            }
            // Then, write the support
            buffer.append(" #SUP: ");
            buffer.append(support);
            // write to file and create a new line
            writer.write(buffer.toString());
            writer.newLine();
        }// otherwise the result is kept into memory
        else{

            // sort the itemset so that it is sorted according to lexical ordering before we show it to the user
            Arrays.sort(itemsetCopy);

            ItemsetArrayImplWithCount itemsetObj = new ItemsetArrayImplWithCount(itemsetCopy);
            itemsetObj.setAbsoluteSupport(support);
            patterns.addItemset(itemsetObj, itemsetLength);
        }
    }

    /**
     * Sort an array of items according to the total order of support
     * This has an average performance of O(n^2)
     *
     * @param a array of integers
     */
    protected void sortOriginalOrder(int[] a, int length) {
        // Perform a bubble sort
        for (int i = 0; i < length; i++) {
            for (int j = length - 1; j >= i + 1; j--) {
                boolean test = comparatorOriginalOrder.compare(a[j], a[j - 1]) < 0;
                if (test) {
                    int temp = a[j];
                    a[j] = a[j - 1];
                    a[j - 1] = temp;
                }
            }
        }

    }

    // Comparator to compare the items based on the order of decreasing support in the original DB.
    protected Comparator<Integer> comparatorOriginalOrder = new Comparator<Integer>() {
        public int compare(Integer item1, Integer item2) {
            // compare the frequency
            int compare = originalMapSupport.get(item2) - originalMapSupport.get(item1);
            // if the same frequency, we check the lexical ordering!
            if (compare == 0) {
                compare = (item1 - item2);
                return compare;
            }
            return compare;
        }
    };

}
