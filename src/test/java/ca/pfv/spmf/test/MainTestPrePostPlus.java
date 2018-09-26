package ca.pfv.spmf.test;



import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.fin_prepost.PrePost;

/**
 * Example of how to use PrePost+ algorithm from the source code.
 * @author Philippe Fournier-Viger, 2015
 */
public class MainTestPrePostPlus extends MainTestBase {

	public static void main(String [] arg) throws IOException{

		MainTestPrePostPlus main = new MainTestPrePostPlus();

		String input = main.fileToPath("contextPasquier99.txt");
		String output = "./output.txt";  // the path for saving the frequent itemsets found
		
		double minsup = 0; // means a minsup of 2 transaction (we used a relative support)
		
		// Applying the algorithm
		PrePost prepost = new PrePost();
		// this line is to indicate that we want PrePost+ instead of PrePost
		prepost.setUsePrePostPlus(true);
		prepost.runAlgorithm(input, minsup, output);
		prepost.printStats();
	}
	
}
