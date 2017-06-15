package app.icity.search.pojo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Similarity;

public abstract class SearchAnalyzer {
	protected Analyzer analyzer; // 分词器

	protected Similarity similarity;// 相似度评估器

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public Similarity getSimilarity() {
		return similarity;
	}
}
