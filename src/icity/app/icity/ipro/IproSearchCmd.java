package app.icity.ipro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.BooleanClause.Occur;

import app.icity.search.engine.SearchEngine;
import app.icity.search.engine.impl.SearchEngineImpl;
import app.icity.search.pojo.AnalyzerFactory;
import app.icity.search.pojo.SearchQuery;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

public class IproSearchCmd extends BaseQueryCommand {
	private SearchEngine engine = new SearchEngineImpl();// 调用接口方法实现创建索引
	private static String index_path = "solr";

	// 获取索引结果
	public DataSet getIndex(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			SearchQuery searchQuery = new SearchQuery();// 查询条件
			String key = (String) pSet.getParameter("key");
			String type = (String) pSet.getParameter("type");
			if (!StringUtils.isNotEmpty(key)) {
				key = "*:*";
			}
			int start = 1, rows = 5;
			String s = (String) pSet.getParameter("start");
			if (StringUtils.isNotEmpty(s)) {
				start = Integer.valueOf(s);
			}
			start = (start - 1) * rows;
			String l = (String) pSet.getParameter("limit");
			if (StringUtils.isNotEmpty(l)) {
				rows = Integer.valueOf(l);
			}
			searchQuery.setQuery(new String[] { key, key }, new String[] { "NAME", "TITLE_NAME" },
					new Occur[] { Occur.SHOULD, Occur.SHOULD });
			if (StringUtils.isNotEmpty(type)) {
				searchQuery.setType(type);
			}
			searchQuery.setStart(start);
			searchQuery.setRows(rows);
			searchQuery.setHighlight(true);
			searchQuery.setHighlightSimplePre("<span style=\"color:#DD4B39;\">");
			searchQuery.setHighlightSimplePost("</span>");

			// 排序方式：相关度（默认）、时间
			SortField scoreSort = new SortField(null, SortField.SCORE, false);
			SortField typeSort = new SortField("TYPE", SortField.STRING, false);
			Sort sort = new Sort(new SortField[] { scoreSort, typeSort });
			ds = engine.searchIndex(index_path, searchQuery, sort);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/*private static Pattern p_script = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE);
	private static Pattern p_style = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>", Pattern.CASE_INSENSITIVE);
	private static Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);*/
}
