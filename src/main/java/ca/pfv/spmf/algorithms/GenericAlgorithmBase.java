package ca.pfv.spmf.algorithms;

import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.MemoryLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by smazet on 08/10/18.
 */
public abstract class GenericAlgorithmBase implements GenericAlgorithm {

    // parameters
    protected int minSupportRelative;// the relative minimum support
    private String output;

    // If set to true, the algorithm will show  information for debugging in the console
    // TODO REMOVE this, use a real logging system
    protected final boolean DEBUG = false;

    // internal state
    private int transactionCount; // transaction count in the database
    protected int itemsetCount; // number of freq. itemsets found TODO make private
    // The  patterns that are found
    // (if the user want to keep them into memory)
    protected Itemsets patterns = null;


    // for statistics, access granted to child classes
    protected long startTimestamp; // start time of the latest execution
    protected long endTime; // end time of the latest execution

    protected BufferedWriter writer = null; // object to write the output file

    // This variable is used to determine the size of buffers to store itemsets.
    // A value of 2000 should be enough
    protected final int BUFFERS_SIZE = 2000;

    protected GenericAlgorithmBase(String output) {
        this.output = output;
    }

    public abstract void printStats();

    /**
         * Method to run the FPXXX algorithms as in the original source code
         *
         * @param input   the path to an input file containing a transaction database.
         * @param output  the output file path for saving the result (if null, the result
         *                will be returned by the method instead of being saved).
         * @param minsupp the minimum support threshold.
         * @return the result if no output file path is provided.
         * @throws IOException exception if error reading or writing files
         */
    public GenericResults runAlgorithm(String input, String output, double minsupp) throws IOException {

        this.output = output;

        StringDbScanner dbScanner = new StringDbScanner(input);
        return runAlgorithm(dbScanner, minsupp);
    }

    /**
     * This method scans the input database to calculate the support of single items
     *
     * @param dbScanner a constructor for iterators of the database
     * @return a map for storing the support of each item (key: item, value: support)
     */
    protected Map<Integer, Integer> scanDatabaseToDetermineFrequencyOfSingleItems(DbScanner dbScanner){
        transactionCount = 0;
        // a map for storing the support of each item (key: item, value: support)
        Map<Integer, Integer> mapSupport = new HashMap<>();
        DbScanner.DbIterator dbIterator = dbScanner.dbIterator();
        while (dbIterator.hasNext()) {

            DbScanner.TransactionIterator transactionIterator = dbIterator.next();

            while (transactionIterator.hasNext()) {
                Integer item = transactionIterator.next();

                // increase the support count of the item
                Integer count = mapSupport.get(item);
                if (count == null) {
                    mapSupport.put(item, 1);
                } else {
                    mapSupport.put(item, ++count);
                }
            }
            // increase the transaction count
            transactionCount++;
        }
        return mapSupport;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    protected void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public int getMinSupportRelative() {
        return minSupportRelative;
    }

    /**
     * Get the number of transactions in the last transaction database read.
     * Backwards compatibility
     * @return the number of transactions.
     */
    public int getDatabaseSize() {
        return getTransactionCount();
    }

    public String getOutput() {
        return output;
    }

    /**
     * Utility method:
     * this method scans the input string as if it were a database
     *
     * @param input the path of the input file
     * @return a map for storing the support of each item (key: item, value: support)
     * @throws IOException exception if error while writing the file
     */
    protected Map<Integer, Integer> scanDatabaseToDetermineFrequencyOfSingleItems(String input)
            throws FileNotFoundException, IOException {
        return scanDatabaseToDetermineFrequencyOfSingleItems(new StringDbScanner(input));
    }

    /**
     * Print statistics about the algorithm execution to System.out.
     */
    protected void printStats(String algoName, String algoVersion) {
        System.out.println("=============  "+algoName+" "+algoVersion+"  - STATS =============");
        long temps = endTime - startTimestamp;
        System.out.println(" Transactions count from database : " + getTransactionCount());
        System.out.print(" Max memory usage: " + MemoryLogger.getInstance().getMaxMemory() + " mb \n");
        System.out.println(" Frequent itemsets count : " + itemsetCount);
        System.out.println(" Total time ~ " + temps + " ms");
        System.out.println("===================================================");
    }

    /**
     * the db scanner when the db is a string
     */
    class StringDbScanner implements DbScanner {

        final String input;
        Integer numberOfTransactions;

        public StringDbScanner(String input) {
            this.input = input;
        }

        /* REMOVE ?
        public Integer getNumberOfTransactions() {
            if (null!=numberOfTransactions) {
                return numberOfTransactions;
            }

            DbIterator dbIterator = dbIterator();
            Integer count = 0;
            while (dbIterator.hasNext()) {
                count++;
                dbIterator.next();
            }
            this.numberOfTransactions = count;
            return count;
        }
        */

        @Override
        public DbIterator dbIterator() {
            DbIterator dbIterator = null;
            try {
                dbIterator = new StringDbIterator(input);
            } catch (IOException e) {
                // TODO: a logging system, use Log4j
                System.out.println("I/O error while accessing file: "+input);
                e.printStackTrace();
            }
            return dbIterator;
        }

    }

    /**
     * the db iterator when the db is a string
     */
    class StringDbIterator implements DbScanner.DbIterator {

        final BufferedReader reader;
        String currentLine;

        public StringDbIterator(String input) throws IOException {
            //Create object for reading the input file
            reader = new BufferedReader(new FileReader(input));
            advanceToNextMeaningfulLine();
        }

        private void advanceToNextMeaningfulLine() throws IOException {
            currentLine = reader.readLine();
            while ( currentLine!=null && skipLine(currentLine)) {
                currentLine = reader.readLine();
            }
        }

        private boolean skipLine(String line) {
            return line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@';
        }


        @Override
        public boolean hasNext() {
            return currentLine != null;
        }

        @Override
        public DbScanner.TransactionIterator next() {

            if (null==currentLine) {
                throw new NoSuchElementException();
            }

            String returnValue = currentLine;
            try {
                advanceToNextMeaningfulLine();
                if (null==currentLine) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new NoSuchElementException("Unexpected end of input file");
            }
            return new StringTransactionIterator(returnValue);
        }
    }

    /**
     * the transaction iterator when the db is a string
     */
    class StringTransactionIterator implements DbScanner.TransactionIterator {

        final String inputLine;
        Iterator<String> itemIterator;

        public StringTransactionIterator(String inputLine) {
            this.inputLine = inputLine;
            // split the line into items
            List<String> splitLine = Arrays.asList(inputLine.split(" "));
            itemIterator = splitLine.iterator() ;
        }

        @Override
        public boolean hasNext() {
            return itemIterator.hasNext();
        }

        @Override
        public Integer next() {
            return Integer.parseInt(itemIterator.next());
        }
    }


    /**
     * the db scanner when the db is a TransactionDatabase
     */
    public class TransactionDatabaseDbScanner implements DbScanner {

        private final TransactionDatabase database;

        public TransactionDatabaseDbScanner(TransactionDatabase database) {
            this.database = database;
        }

        @Override
        public TransactionDatabaseDbIterator dbIterator() {
            return new TransactionDatabaseDbIterator(database.getTransactions().iterator());
        }

    }
    class TransactionDatabaseDbIterator implements DbScanner.DbIterator {
        private final Iterator<List<Integer>> dbIterator;

        private TransactionDatabaseDbIterator(Iterator<List<Integer>> iterator) {
            this.dbIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return dbIterator.hasNext();
        }

        @Override
        public DbScanner.TransactionIterator next() {
            return new DatabaseTransactionIterator(dbIterator.next());
        }
    }
    class DatabaseTransactionIterator implements DbScanner.TransactionIterator {

        private final Iterator<Integer> iterator;

        private DatabaseTransactionIterator(List<Integer> transaction) {
            this.iterator = transaction.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Integer next() {
            return iterator.next();
        }
    }
}
