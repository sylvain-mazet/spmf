package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.upgrowth_ihup.AlgoUPGrowthPlus;

/**
 * Example of how to use the UPGrowth+ algorithm 
 * from the source code.
 * @author (c) Prashant Barhate, 2014
 */
public class MainTestUPGrowthPlus extends MainTestBase {

	public static void main(String [] arg) throws IOException{
		
		String input = new MainTestBase().fileToPath("DB_Utility.txt");
		String output = ".//output.txt";

		int min_utility = 30;  //
		
		// Applying the HUIMiner algorithm
		AlgoUPGrowthPlus algo = new AlgoUPGrowthPlus();
		algo.runAlgorithm(input, output, min_utility);
		algo.printStats();

	}

	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestUPGrowthPlus.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
