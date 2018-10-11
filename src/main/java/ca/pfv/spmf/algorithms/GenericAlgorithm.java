package ca.pfv.spmf.algorithms;

import java.io.IOException;

/**
 * Created by smazet on 10/10/18.
 */
public interface GenericAlgorithm {

    GenericResults runAlgorithm(DbScanner dbScanner, double minsupp) throws IOException;

}
