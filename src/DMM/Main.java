package DMM;

import java.io.IOException;

import org.json.JSONException;


public class Main {
	private static String path = "data//DataInput//";
	private static String path_output = "data//Result//";
	private static int topci_num = 5;
	
	public static void main(String[] args) throws IOException, JSONException
	{
		//input data
		Document doc = new Document();
//		doc.setStemming(new englishStemmer());
		doc.ReadCorpusJson(path+"S");
		
		/////////////////////////////////////////////////////////////////////////////////
		//DMM gibbs sampling
		DMM dmm = new DMM();
		/**
		 * If you need to reset parameter.
		 * Using lda.setParameter(alpha, beta, topicSize, iteration) to reset.
		 */
		dmm.setParameter(50.0/topci_num , 0.1, topci_num, 1000);
		
		dmm.DMM_Gibbs_Sampling(doc);
		dmm.printWordInTopic(5);
		dmm.saveResult(path_output, 5);
	}
}
