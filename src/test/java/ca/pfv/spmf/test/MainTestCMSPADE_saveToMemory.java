package ca.pfv.spmf.test;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;

/**
 * Example of how to use the algorithm CMSPADE, saving the results in the 
 * main  memory
 * 
 * @author Philippe Fournier-Viger
 */
public class MainTestCMSPADE_saveToMemory extends MainTestBase {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Load a sequence database
        double support = 0.50;

        boolean keepPatterns = true;
        boolean verbose = false;

        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
        boolean dfs=true;
        
     // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        boolean outputSequenceIdentifiers = false; 

        IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();
                
        CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative.getInstance();
        
        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);

        sequenceDatabase.loadFile(new MainTestBase().fileToPath("contextPrefixSpan.txt"), support);
        
        System.out.println(sequenceDatabase.toString());

        AlgoCMSPADE algorithm = new AlgoCMSPADE(support,dfs,abstractionCreator);
        
        algorithm.runAlgorithm(sequenceDatabase, candidateGenerator,keepPatterns,verbose,null, outputSequenceIdentifiers);
        System.out.println("Minimum relative support = "+ support + "  minimum absolute support: " + algorithm.minSupAbsolute);
        System.out.println(algorithm.getNumberOfFrequentPatterns()+ " frequent patterns.");
        
        System.out.println(algorithm.printStatistics());
    }

    public String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestSPADE_AGP_FatBitMap_saveToFile.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }
}
