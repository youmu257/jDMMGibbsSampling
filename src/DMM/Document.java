package DMM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import util.IO;

public class Document {
	private String data_path = "Data//";
	//Stopword list
	private ArrayList<String> stopword = new ArrayList<String>();
	//Distinct words in all document (word : word_id)
	public HashMap<String, Integer> distinct_words = new HashMap<String, Integer>();
	//Distinct words in all document (word_id : word)
	public BiMap distinct_words_inverse = HashBiMap.create();
	//Contain all words in each document(*document *distinct word frequency)
	public ArrayList<HashMap<String,Integer>> wordInDocument = new ArrayList<HashMap<String,Integer>>();
	//Label for each document
	public ArrayList<String> label = new ArrayList<String>();
	//Snowball Stemming
	
	public static void main(String[] args) throws IOException {
		convert2json("all.txt");

	}
	
	public String filtering(String input_str)
	{
		input_str = input_str.toLowerCase().trim();
		
		// Stemming
//		if (stemmer != null) {
//			stemmer.setCurrent(input_str);
//			stemmer.stem();
//			input_str = stemmer.getCurrent();
//		}
		
		//Filtering email
		String email_regex = "^(.+)@(.+)$";
		input_str = input_str.replaceAll(email_regex, "");
		
		//Filtering url
		String url_regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		input_str = input_str.replaceAll(url_regex, "");
		
		//remove all punctuation
//		input_str = input_str.replaceAll("[^a-zA-Z0-9]", "");
		
		//remove only number
		String number_regex = "^[0-9]+$";
		input_str = input_str.replaceAll(number_regex, "");
		
		//remove stopword
		if(stopword.contains(input_str))
			return "";
		
		return input_str;
	}
	
	/**
	 * @parm path : input file format is text spilt by space and last word is topic
	 */
	public static void convert2json(String path) throws IOException
	{
		BufferedReader br = IO.Reader("data//DataInput//"+path);
		BufferedWriter bw = IO.Writer("data//DataInput//"+path.replace(".txt", "_new.txt"));
		String lin = "";
		while((lin=br.readLine()) != null){
			String[] spli = lin.split(" ");
			String text = spli[0];
			if(spli.length < 4)//lower than 3 words
				continue;
			for(int i=1;i<spli.length-1;i++)
				text+=" "+spli[i];
			bw.write("{\"text\": \""+text+"\", \"cluster\": "+spli[spli.length-1]+"}\n");
			bw.flush();
		}
		br.close();
		bw.close();
	}
	
	/**
	 * Read all document. Then setting wordset and wordInDocument array.
	 * @param directory : file path
	 * @throws JSONException 
	 */
	public void ReadCorpusJson(String file_path) throws IOException, JSONException
	{
		long stime = System.currentTimeMillis();
		readStopWord();
		
		IO.mkdir("data//Result//");
		BufferedWriter bw = IO.Writer("data//Result//label.txt");
    	BufferedReader br = IO.Reader(file_path);
    	
    	int word_id = 0;
    	String lin = "";
    	while((lin = br.readLine()) != null)
    	{
    		HashMap<String,Integer> tmp_map = new HashMap<String,Integer>();
//    		lin = lin.replaceAll("[#(\\[<>\\])]", " ");
    		JSONObject obj = new JSONObject(lin);
			String text = obj.getString("text");
    		for(String word_origin : text.split(" "))
    		{
    			String word = filtering(word_origin);
//    			String word = word_origin;
    			if(word.length() == 0)
    				continue;
    			else if(!distinct_words.containsKey(word)){
    				distinct_words.put(word, word_id);
    				word_id++;
    			}
    			
    			if(tmp_map.containsKey(word))
    				tmp_map.put(word, tmp_map.get(word)+1);
    			else
    				tmp_map.put(word, 1);
    		}
    		wordInDocument.add(tmp_map);
    		String l="";
    		try{
    			l = obj.getString("cluster");
    		}catch(JSONException e){
    			l = obj.getInt("cluster")+"";
    		}
    		bw.write(l+"\n");
    		
    		label.add(l);
    	}
    	br.close();
    	bw.close();
    	
    	distinct_words_inverse.putAll(distinct_words);
		distinct_words_inverse = distinct_words_inverse.inverse();
    	System.out.println("Read Json file done!\nSpend "+(System.currentTimeMillis()-stime)/1000+" s");
	}
	
	public void readStopWord() throws IOException
	{
		BufferedReader br = IO.Reader(data_path + "stopword.txt");
		String lin = "";
		while((lin = br.readLine()) != null){
			stopword.add(lin);
		}
		br.close();
		
		stopword.add("ax");
		stopword.add("re");
		stopword.add("am");
		stopword.add("im");
		stopword.add("dont");
	}
}
