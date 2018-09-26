package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.tools.dataset_converter.Formats;
import ca.pfv.spmf.tools.dataset_converter.TransactionDatabaseConverter;

/**
 * Example of how to convert a transaction database from the CSV format
 * to the SPMF format.
 */
public class MainTestConvertTransactionDatabaseCSVtoSPMF extends MainTestBase {
	
	public static void main(String [] arg) throws IOException{
		
		String inputFile = new MainTestBase().fileToPath("contextCSV.txt");
		String outputFile = ".//output.txt";
		Formats inputFileformat = Formats.CSV_INTEGER;
		int sequenceCount = Integer.MAX_VALUE;
		
		TransactionDatabaseConverter converter = new TransactionDatabaseConverter();
		converter.convert(inputFile, outputFile, inputFileformat, sequenceCount);
	}

	

	public String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestConvertTransactionDatabaseCSVtoSPMF.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
