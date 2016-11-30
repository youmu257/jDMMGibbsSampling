package util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MyJavaUtil {
	
	/**
	 * calculated cosine similarity by two word vector
	 * @param word1, word2 : word vector
	 */
	public static double cosine_similarity(double[] word1, double[] word2)
	{
		double product = 0.0;
		double sum1 = 0.0, sum2 = 0.0;
		
		for(int i=0; i < word1.length; i++)
		{
			product += word1[i] * word2[i];
			sum1 += Math.pow(word1[i], 2);
			sum2 += Math.pow(word2[i], 2);
		}
		
		return product / (Math.sqrt(sum1) * Math.sqrt(sum2));
	}
	
	/**
	 * Round half up k bits after decimal point
	 */
	public static double round(double num, int k){
		return new BigDecimal(num).setScale(k, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	//這個sort 使用完後建議在新建一個LinkedHashMap ,用putAll 的方式把排序結果放進去,不然有時再用get 會出問題
	/**
	 * Descending order HashMap by value
	 * @param map : HashMap<String, Double>
	 */
	public static Map<String, Double> sortByComparatorDouble(final Map<String, Double> map) {
    	List<Entry<String,Double>> map_arr = new LinkedList<Entry<String,Double>>( map.entrySet() );
        
        Collections.sort( map_arr , new Comparator<Entry<String, Double>>() {
            public int compare(Entry<String,Double> v1 , Entry<String,Double> v2 )
            {
                return v2.getValue().compareTo( v1.getValue() );//descending
            }
        });
        
        LinkedHashMap<String,Double> sortedByComparator = new LinkedHashMap<String,Double>();
        for(Entry<String,Double> e : map_arr)
        {
        	sortedByComparator.put(e.getKey() , e.getValue() );
        }
	    return sortedByComparator;
	}
	
	//這個sort 使用完後建議在新建一個LinkedHashMap ,用putAll 的方式把排序結果放進去,不然有時再用get 會出問題
	/**
	 * Descending order HashMap by value
	 * @param map : HashMap<String, Integer>
	 */
	public static Map<String, Integer> sortByComparatorInt(final Map<String, Integer> map) {
    	List<Entry<String,Integer>> map_arr = new LinkedList<Entry<String,Integer>>( map.entrySet() );
        
        Collections.sort( map_arr , new Comparator<Entry<String, Integer>>() {
            public int compare(Entry<String,Integer> v1 , Entry<String,Integer> v2 )
            {
                return v2.getValue().compareTo( v1.getValue() );//descending
            }
        });
        
        LinkedHashMap<String,Integer> sortedByComparator = new LinkedHashMap<String,Integer>();
        for(Entry<String,Integer> e : map_arr)
        {
        	sortedByComparator.put(e.getKey() , e.getValue() );
        }
	    return sortedByComparator;
	}
}
