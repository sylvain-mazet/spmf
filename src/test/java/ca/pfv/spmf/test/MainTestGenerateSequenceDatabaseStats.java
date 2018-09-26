package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.tools.dataset_stats.SequenceStatsGenerator;

/**
 * Example of how to generate statistics about a sequence database
 */
public class MainTestGenerateSequenceDatabaseStats extends MainTestBase {
	
	public static void main(String [] arg) throws IOException{

		MainTestBase main = new MainTestBase();

		String inputFile = main.fileToPath("contextPrefixSpan.txt");
		try{
			SequenceStatsGenerator sequenceDatabase = new SequenceStatsGenerator(); 
			sequenceDatabase.getStats(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
