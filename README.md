# jDMMGibbsSampling
Dirichlet Multinomial Mixture(DMM) is a unsupersived machine learning mathod that distil topic of word in short text.

#Data
  Dataset resource : https://github.com/jackyin12/GSDMM/tree/master/data
  * Google News
		Time : November 27, 2013
		Description : 
				jackyin12 crawled the titles and sinppets of 11,109 news articles belonging to 152 clusters.
				And divided the dataset into three dataset :
				* S (SnippetSet) : only contain snippets
				* T (TitleSet) : only contain titles
				* TS (TitleSnippetSet) : contain both the titles and snippets

  * TweetSet
		From : Text REtrieval Conference(TREC) 2011 and 2012
		Description : Removing the queries with lowly-relevant tweets.
						   New dataset contain 89 clusters and totally 2,472 tweets.
						   * Tweet : Short text from Twitter
  * Data format:
	  The datasets are in format of JSON like follows:
	{"text": "centrepoint winter white gala london", "cluster": 65}
	{"text": "mourinho seek killer instinct", "cluster": 96}
	{"text": "roundup golden globe won seduced johansson voice", "cluster": 72}
	{"text": "travel disruption mount storm cold air sweep south florida", "cluster": 140}
	{"text": "wes welker blame costly turnover", "cluster": 89}
	......
		
#Data Preprocessing
   1. Stopword list download from [[link](http://www.lextek.com/manuals/onix/stopwords1.html)].
   This stopword list is probably the most widely used stopword list. 
   It covers a wide number of stopwords without getting too aggressive and including too many words which a user might search upon. 
   This wordlist contains 429 words.
   
   2. Stemming using [Snowball Stemmer](http://snowball.tartarus.org/download.html) java version
   
#Reference
  1. [Jianhua Yin's GSDMM](https://github.com/jackyin12/GSDMM) .
  2. J. Yin and J. Wang. A dirichlet multinomial mixture model-based approach for short text clustering. In SIGKDD, 2014.[[pdf](http://dbgroup.cs.tsinghua.edu.cn/wangjy/papers/KDD14-GSDMM.pdf)]
  

 