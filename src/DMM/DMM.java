package DMM;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import util.IO;
import util.MyJavaUtil;

class word_class{
	String word;
	int word_id;
	int freq;
	public word_class(String word, int word_id, int freq){
		this.word = word;
		this.word_id = word_id;
		this.freq = freq;
	}
}

class doc_class{
	int topic;
	int doc_size;
	String label;
	ArrayList<word_class> word_list = new ArrayList<word_class>();
	public doc_class(int topic){
		this.topic = topic;
	}
}

public class DMM extends TopicModel{
	// K is topic size
	private int K = this.topicSize;
	// V is number of distinct words
	private int V;
	// M is number of document
	private int M;
	// z is topic of word in document
	ArrayList<doc_class> z = new ArrayList<doc_class>();
	// ntw is topic-word dice(words in topic)
	private int[][] ntw;
	// ntdSum is number of document assigned to topic k
	private int[] ntdSum;
	// ntwSum is sum of word in topic
	private int[] ntwSum;
	//theta in all document
	private double[][] theta;
	//phi in all document
	private double[][] phi;
	//global variable
	public Document doc;
	//Cumulative Topic Result in each last 50 iteration
	private HashMap<Integer, HashMap<String,Integer>> CumulativeTopicResult = new HashMap<Integer,HashMap<String,Integer>>();
	
	
	
	public void DMM_Gibbs_Sampling(Document doc)
	{
		long stime = System.currentTimeMillis();
		this.doc = doc;
		InitParameter(doc.distinct_words.size(), doc.wordInDocument.size());
		BuildMatrix();
		ModelInference();
		
		// update theta and phi
		updateTheta();
		updatePhi();
		System.out.println("DMM done!\nSpend "+(System.currentTimeMillis()-stime)/1000+" s");
	}
	
	public void InitParameter(int V, int M)
	{
		this.V = V;
		this.M = M;
		this.K = this.topicSize;
		ntw = new int[K][V];
		ntdSum = new int[K];
		ntwSum = new int[K];
		theta = new double[M][K];
		phi = new double[K][V];
	}
	
	public void BuildMatrix()
	{
		//random initial topic of word
		int doc_flag = 0;
		for(HashMap<String, Integer> word_in_doc : doc.wordInDocument)
		{
			//random topic of document 
			int random_topic = (int)(Math.random()*K);
			
			doc_class tmp_z = new doc_class(random_topic);
			int word_count_in_doc = 0;
			for(Map.Entry<String, Integer> entry : word_in_doc.entrySet())
			{
				word_class new_word = new word_class(entry.getKey(), doc.distinct_words.get(entry.getKey()), entry.getValue());
				tmp_z.word_list.add(new_word);
				word_count_in_doc += entry.getValue();
			}
			tmp_z.doc_size = word_count_in_doc;
			tmp_z.label = doc.label.get(doc_flag);
			z.add(tmp_z);
			ntdSum[random_topic]++;
			doc_flag++;
		}
		
		//building word-topic matrix and doc-topic matrix
		for(doc_class tmp_doc : z)
		{
			for(word_class tmp_word : tmp_doc.word_list)
			{
				ntw[tmp_doc.topic][tmp_word.word_id]+= tmp_word.freq;
			}
			ntwSum[tmp_doc.topic]+=tmp_doc.doc_size;
		}
	}

	public void UpdateNormalCounter(int doc_id, int topic, int flag)
	{
		for(word_class tmp_word : z.get(doc_id).word_list)
			ntw[topic][tmp_word.word_id] += flag * tmp_word.freq;
		ntdSum[topic] += flag;
		ntwSum[topic] += flag * z.get(doc_id).doc_size;
	}
	
	public void ModelInference()
	{
		//Running model inference until iteration finish
		for(int iter = 0; iter < this.iteration; iter++)
		{
			for(int doc_index = 0; doc_index < this.M; doc_index++)
			{
				//remove word in document now
				int origin_topic = z.get(doc_index).topic;
				UpdateNormalCounter(doc_index, origin_topic, -1);
				int newtopic = samplingNewTopic_my(doc_index);
				
				//update matrix by new topic
				z.get(doc_index).topic = newtopic;
				UpdateNormalCounter(doc_index, newtopic, 1);
			}
			
			if(iter > (this.iteration-50))
			{
				updateCumulativeTopicResult();
				updateTheta();
			}
		}
	}

	/**
	 * @param m : document id
	 * @param n : word position in document(not word id)
	 * @return new topic
	 */
	public int samplingNewTopic_my(int m)
	{
		//calculate document's topic distribution
		double[] p = new double[K];
		for(int topic_index = 0; topic_index < K ; topic_index++)
		{
			//_theta are represented co-occurrence influences
			double _theta = (ntdSum[topic_index] + this.alpha) / (M - 1 + K * this.alpha);
			
			//_phi are represented the probability that a word will appear under each topic 
			double word_p = 1.0;
			double doc_p = 1.0;

			int i = 0;
			for(word_class tmp_word : z.get(m).word_list)
			{
				int wid = tmp_word.word_id;
				for(int j = 0; j < tmp_word.freq; j++){
					word_p *= (ntw[topic_index][wid] + this.beta + j);
					doc_p *= (ntwSum[topic_index] + V * this.beta + i);
					i++;
				}
			}
			double _phi = word_p/doc_p;

			p[topic_index] = _theta * _phi ;
		}
		return this.sampleMultinomial(p);
	}
	
	public void updateTheta()
	{
		for(int doc_index = 0; doc_index < this.M; doc_index++)
		{
			int topic = z.get(doc_index).topic;
			theta[doc_index][topic]++;
		}
	}
	
	public void updatePhi()
	{
		for(int topic_index = 0; topic_index < K; topic_index++)
			for(int word_index = 0;word_index < V; word_index++)
				phi[topic_index][word_index] = (ntw[topic_index][word_index] + this.beta) / (ntwSum[topic_index] + V * this.beta);
	}
	
	public double findMaxTopicProbabilityGivenWord(int word_id)
	{
		double max = Double.MIN_VALUE;
		for(int topic_index = 0; topic_index < K; topic_index++)
			if(max < ntw[topic_index][word_id])
				max = ntw[topic_index][word_id];
		return max;
	}
	
	/**
	 * cumulative topic result to count that can reduce random sampling influence.
	 */
	public void updateCumulativeTopicResult()
	{
		updatePhi();
		for(int topic_index = 0; topic_index < phi.length; topic_index++)	
		{	
			if(!CumulativeTopicResult.containsKey(topic_index))
				CumulativeTopicResult.put(topic_index, new HashMap<String,Integer>());
			Map<String,Double> tmp_map = new HashMap<String,Double>();
			for(int word_index = 0; word_index < phi[topic_index].length; word_index++)
			{
				tmp_map.put((String)doc.distinct_words_inverse.get(word_index), phi[topic_index][word_index]);
			}
			LinkedHashMap<String, Double> tmp = new LinkedHashMap<String, Double>();
			tmp.putAll(MyJavaUtil.sortByComparatorDouble(tmp_map));
			
			//only cumulative top 20
			int flag = 0;
			for(Map.Entry<String, Double> e : tmp.entrySet())
			{
				String key = e.getKey();
				if(CumulativeTopicResult.get(topic_index).containsKey(key))
					CumulativeTopicResult.get(topic_index).put(key, CumulativeTopicResult.get(topic_index).get(key)+1);
				else
					CumulativeTopicResult.get(topic_index).put(key, 1);

				flag++;
				if(flag>20) break;
			}
		}
	}
	
	/**
	 * Print top k word in each topic
	 * @param top : top k number 
	 */
	public void printWordInTopic(int top)
	{
		for(int topic_index = 0; topic_index < phi.length; topic_index++)	
		{	
			System.out.print(topic_index+":\t");
			Map<String,Double> tmp_map = new HashMap<String,Double>();
			for(int word_index = 0; word_index < phi[topic_index].length; word_index++)
			{
				tmp_map.put((String)doc.distinct_words_inverse.get(word_index), phi[topic_index][word_index]);
			}
			LinkedHashMap<String, Double> tmp = new LinkedHashMap<String, Double>();
			tmp.putAll(MyJavaUtil.sortByComparatorDouble(tmp_map));
			
			int flag = 1;
			for(Map.Entry<String, Double> e : tmp.entrySet())
			{
				System.out.print(e.getKey()+"\t");//+","+e.getValue()
				flag++;
				if(flag>top) break;
			}
			System.out.println();
		}
	}
	
	/**
	 * Write phi matrix, theta matrix and top k topic words
	 * @param path_result : output folder path
	 * @param top : top k number
	 */
	public void saveResult(String path_result, int top) throws IOException
	{
		IO.mkdir(path_result);
		//write phi matrix
		BufferedWriter bw = IO.Writer(path_result + "phi.txt");
		for(int topic_index = 0; topic_index < phi.length; topic_index++)
		{
			StringBuilder sb = new StringBuilder();
			for(int word_index = 0; word_index < phi[topic_index].length; word_index++)
			{
				sb.append(MyJavaUtil.round(phi[topic_index][word_index], 5)+"\t");
			}
			bw.write(sb.toString()+"\n");
		}
		bw.close();
		
		//write theta matrix
		bw = IO.Writer(path_result + "theta.txt");
		for(int doc_index = 0; doc_index < theta.length; doc_index++)
		{
			StringBuilder sb = new StringBuilder();
			for(int topic_index = 0; topic_index < K; topic_index++)
			{
				sb.append(MyJavaUtil.round(theta[doc_index][topic_index], 5)+"\t");
			}
			bw.write(sb.toString()+"\n");
		}
		bw.close();
		
		//write parameter matrix
		bw = IO.Writer(path_result + "parameter.txt");
		bw.write("alpha:"+alpha+"\n");
		bw.write("beta:"+beta+"\n");
		bw.write("topicSize:"+topicSize+"\n");
		bw.write("iteration:"+iteration+"\n"); 
		bw.close();
		
		//write word in each topic
		bw = IO.Writer(path_result + "result.txt");
		for(int topic_index = 0; topic_index < phi.length; topic_index++)	
		{	
			bw.write(topic_index+":\t");
			Map<String,Double> tmp_map = new HashMap<String,Double>();
			for(int word_index = 0; word_index < phi[topic_index].length; word_index++)
			{
				tmp_map.put((String)doc.distinct_words_inverse.get(word_index), phi[topic_index][word_index]);
			}
			LinkedHashMap<String, Double> tmp = new LinkedHashMap<String, Double>();
			tmp.putAll(MyJavaUtil.sortByComparatorDouble(tmp_map));
			
			int flag = 1;
			for(Map.Entry<String, Double> e : tmp.entrySet())
			{
				bw.write(e.getKey()+"\t");//+","+e.getValue()
				flag++;
				if(flag>top) break;
			}
			bw.write("\n");
		}
		bw.close();
		
		//write word in each topic over last 50 iteration
		bw = IO.Writer(path_result + "CumulativeTopicResult.txt");
		for(int topic_index = 0; topic_index < CumulativeTopicResult.size(); topic_index++)	
		{	
			LinkedHashMap<String, Integer> tmp = new LinkedHashMap<String, Integer>();
			tmp.putAll(MyJavaUtil.sortByComparatorInt(CumulativeTopicResult.get(topic_index)));
		
			bw.write(topic_index+":\t");
			int flag = 1;
			for(Map.Entry<String, Integer> e: tmp.entrySet())
			{
				bw.write(e.getKey()+"\t");
				flag++;
				if(flag>top) break;
			}
			bw.write("\n");
		}
		
		bw.close();
		
		//write summation over words
		bw = IO.Writer(path_result + "SW.csv");
		//title
		for(int topic_index = 0; topic_index < K; topic_index++)	
			bw.write(topic_index+",");
		bw.write("label\n");
		
		for(int doc_index = 0; doc_index < M; doc_index++)
		{
			ArrayList<word_class> wlist = z.get(doc_index).word_list;
			for(int topic_index = 0; topic_index < K; topic_index++)	
			{	
				double doc_topic_p = 0.0;
				for(word_class w : wlist)
				{
					double word_topic = (double)ntw[topic_index][w.word_id]/ntwSum[topic_index];
					double relative_freq = (double)w.freq / z.get(doc_index).doc_size;
					doc_topic_p += word_topic * relative_freq * w.freq;
				}

				bw.write(MyJavaUtil.round(doc_topic_p, 5)+",");
			}
			bw.write(z.get(doc_index).label+"\n");
		}
		bw.close();
	}
}
