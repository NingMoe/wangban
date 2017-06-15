package app.icity.search.pojo;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.Version;

public class SearchQuery {
	private BooleanQuery booleanQuery; // 搜索查询query
	private TermRangeQuery rangeQuery=null;
	private int start;// 开始分页

	private int rows;// 每页条数

	private boolean highlight = false;// 是否高亮

	private String highlightSimplePre;// 高亮代码前缀

	private String highlightSimplePost;// 高亮代码后缀

	private String[] fields;// 关键字，高亮显示

	public SearchQuery() {
		booleanQuery = new BooleanQuery();
	}

	public BooleanQuery getQuery() {
		return booleanQuery;
	}
	public TermRangeQuery getRangeQuery() {
		return rangeQuery;
	}
	public int getStart() {
		return start;
	}

	public int getRows() {
		return rows;
	}

	public String[] getFields() {
		return fields;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public String getHighlightSimplePre() {
		return highlightSimplePre;
	}

	public String getHighlightSimplePost() {
		return highlightSimplePost;
	}

	
	public void setType(String type) {
		TermQuery termQuery = new TermQuery(new Term("TYPE", type));
		booleanQuery.add(termQuery, Occur.MUST);
	}
	

	public void setStart(int start) {
		this.start = start;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public void setHighlightSimplePre(String highlightSimplePre) {
		this.highlightSimplePre = highlightSimplePre;
	}

	public void setHighlightSimplePost(String highlightSimplePost) {
		this.highlightSimplePost = highlightSimplePost;
	}

	public void setFilterQueries(String key) {
		TermQuery termQuery = null;
		String[] str = key.split(":");
		if (str.length == 1) {
			termQuery = new TermQuery(new Term("NAME", str[0]));
		} else if (str.length == 2) {
			termQuery = new TermQuery(new Term(str[0], str[1]));
		}
		booleanQuery.add(termQuery, Occur.MUST);
	}
	//分词查询
	public void setQuery(String[] keys,String[] ifields,Occur[] occurs) {	
		try {
			Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer();
			fields=ifields;
			Query ikquery = MultiFieldQueryParser.parse(Version.LUCENE_36, keys, fields, occurs, analyzer);
			booleanQuery.add(ikquery, Occur.MUST);
		} catch (ParseException e) {
			e.printStackTrace();
		}		
	}
	public void setBooleanQuery(BooleanQuery bq) {
		booleanQuery.add(bq, Occur.MUST);
	}
	public void setRangeQuery(String field, String lowerTerm, String upperTerm,Boolean includeLower,Boolean includeUpper)  {	
		rangeQuery=new TermRangeQuery(field, lowerTerm, upperTerm, includeLower, includeUpper);		
	}

	// 对特殊字符进行转义
	public static String escapeQueryChars(String key) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == ';' || c == '/' || Character.isWhitespace(c)) {
			if(Character.isWhitespace(c)){
				continue;
			}
				sb.append('\\');
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
