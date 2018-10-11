package ca.pfv.spmf.algorithms;

import java.util.Iterator;

/**
 * Created by smazet on 08/10/18.
 *
 * A factory for iterators of the database
 */
public interface DbScanner {

    /**
     * This iterates through the items in the "current" transaction being scanned
     */
    interface TransactionIterator extends Iterator<Integer> {
    }

    /**
     * This iterates through all transactions in the data base
     */
    interface DbIterator extends Iterator<TransactionIterator> {
    }

    DbIterator dbIterator();

}
