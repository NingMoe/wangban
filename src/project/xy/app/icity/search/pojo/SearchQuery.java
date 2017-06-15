package xy.app.icity.search.pojo;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import app.icity.search.pojo.AnalyzerFactory;

public class SearchQuery {
	private BooleanQuery query; // 搜索查询query

	private int start;// 开始分页

	private int rows;// 每页条数

	private boolean highlight = false;// 是否高亮

	private String highlightSimplePre;// 高亮代码前缀

	private String highlightSimplePost;// 高亮代码后缀

	private String[] fields;// 关键字，高亮显示

	public SearchQuery() {
		query = new BooleanQuery();
	}

	public BooleanQuery getQuery() {
		return query;
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

	public void setQuery(String key, Analyzer analyzer) {
		try {
			// 查询条件
			if (StringUtils.isEmpty(key)) {
				key = "*:*";
			} else {
				key = escapeQueryChars(key);
			}
			String[] queries = key.split(" ");
			fields = new String[queries.length];
			for (int i = 0; i < queries.length; i++) {
				String[] str = queries[i].split(":");
				if (str.length == 1) {
					fields[i] = "NAME";
					queries[i] = str[0];					
				} else if (str.length == 2) {
					fields[i] = str[0];					
					queries[i] = str[1];
				}
			}
			Query ikquery = MultiFieldQueryParser.parse(Version.LUCENE_36, queries, fields, analyzer);
			query.add(ikquery, Occur.SHOULD);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void setQuery(String key,String[] ifields,Occur[] occurs,Analyzer analyzer) {
		try {
			// 查询条件
			if (StringUtils.isEmpty(key)) {
				key = "*:*";
			} else {
				key = escapeQueryChars(key);
			}
			fields=ifields;
			Query ikquery = MultiFieldQueryParser.parse(Version.LUCENE_36, key, fields, occurs, analyzer);
			query.add(ikquery, Occur.MUST);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setType(String type) {
		TermQuery termQuery = new TermQuery(new Term("TYPE", type));
		query.add(termQuery, Occur.MUST);
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
		query.add(termQuery, Occur.MUST);
	}
	public void setFilterQueriesShould(String key) {
		TermQuery termQuery = null;
		String[] str = key.split(":");
		if (str.length == 1) {
			termQuery = new TermQuery(new Term("NAME", str[0]));
		} else if (str.length == 2) {
			if("0".equals(str[1])){
				return;
			}else if("1".equals(str[1])){
				TermQuery termQuery1 = new TermQuery(new Term("SERVICE_OBJECT", "0"));
				query.add(termQuery1, Occur.MUST);
				termQuery = new TermQuery(new Term("TYPE", "project"));
			}else if("2".equals(str[1])){
				TermQuery termQuery1 = new TermQuery(new Term("SERVICE_OBJECT", "1"));
				query.add(termQuery1, Occur.MUST);
				termQuery = new TermQuery(new Term("TYPE", "project"));
			}else if("3".equals(str[1])){
				termQuery = new TermQuery(new Term("TYPE", "project"));
			}else if("4".equals(str[1])){
				TermQuery termQuery1 = new TermQuery(new Term("SERVICE_OBJECT", "4"));
				query.add(termQuery1, Occur.MUST);
				termQuery = new TermQuery(new Term("TYPE", "project"));
			}else if("5".equals(str[1])){
				termQuery = new TermQuery(new Term("TYPE", "content"));
			}			
		}
		query.add(termQuery, Occur.MUST);
	}
	public void setFilterQueriesKeys(String[] key,String[] fileds,Occur[] occurs) {	
		try {
			Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer();
			Query ikquery = MultiFieldQueryParser.parse(Version.LUCENE_36, key, fileds, occurs, analyzer);
			query.add(ikquery, Occur.MUST);
		} catch (ParseException e) {
			e.printStackTrace();
		}		
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
