package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.AlgoFournierViger08;
import ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase;

/**
 * Example of sequential pattern mining with time constraints.
 * @author Philippe Fournier-Viger
 */
public class MainTestSequentialPatternMining1_saveToFile {

	public static void main(String [] arg) throws IOException{    
		//In this example, the result is saved to a file
		String outputFilePath = ".//output.txt";
		
		// Load a sequence database
		SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
		sequenceDatabase.loadFile(new MainTestBase().fileToPath("contextSequencesTimeExtended.txt"));
		// Create an instance of the algorithm
		AlgoFournierViger08 algo 
		  = new AlgoFournierViger08(0.55,
				0, 2, 0, 2, null,  false, false);
		
		// Set this parameter to true to show the sequence identifiers
		// for each pattern found.
//		algo.setOutputSequenceIdentifiers(true);
		
		// execute the algorithm
		algo.runAlgorithm(sequenceDatabase, outputFilePath);    
		algo.printStatistics();
	}
	
	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestSequentialPatternMining2_saveToMemory.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}




