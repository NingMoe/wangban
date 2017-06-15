package app.icity.search.pojo.impl;

import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKSimilarity;

import app.icity.search.pojo.SearchAnalyzer;

public class IKSearchAnalyzer extends SearchAnalyzer {
	public IKSearchAnalyzer() {
		this.analyzer = new IKAnalyzer(false);
		this.similarity = new IKSimilarity();
	}
}
