package km.app.icity.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause.Occur;

import km.app.icity.search.engine.SearchEngine;
import km.app.icity.search.engine.impl.SearchEngineImpl;
import app.icity.search.pojo.AnalyzerFactory;
import km.app.icity.search.pojo.SearchQuery;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

public class SearchCmd extends BaseQueryCommand {
	private SearchEngine engine = new SearchEngineImpl();// 调用接口方法实现创建索引
	private static String index_path="solr";
	// 获取索引结果
	public DataSet getIndex(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			SearchQuery searchQuery = new SearchQuery();// 查询条件
			String key = (String) pSet.getParameter("key");
			String type=(String) pSet.getParameter("type");
			if (!StringUtils.isNotEmpty(key)) {
				key = "*:*";
			}
			//舟山模式新增所属网站属性  websitetype 0 默认外网 【1舟山审招委 2舟山内网 3公共】
			String websitetype = (String)pSet.getParameter("websitetype");
			if(StringUtils.isNotEmpty(websitetype)){
				Occur[] __occur = {Occur.SHOULD,Occur.SHOULD};
				String[] __fileds = {"WEBSITETYPE","WEBSITETYPE"};
				String[] __key = {websitetype,"3"};
				searchQuery.setFilterQueriesKeys(__key,__fileds,__occur);
			}			
			searchQuery.setFilterQueries("REGION_CODE:" + SecurityConfig.getString("WebRegion"));
			searchQuery.setFilterQueriesShould("MARK:" + (String) pSet.getParameter("mark"));
			int start = 1, rows = 5;
			String s = (String) pSet.getParameter("start");
			if (StringUtils.isNotEmpty(s)) {
				start = Integer.parseInt(s);
			}
			start=(start-1)*rows;
			String l = (String) pSet.getParameter("limit");
			if (StringUtils.isNotEmpty(l)) {
				rows = Integer.parseInt(l);
			}
			Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer();
			//searchQuery.setQuery(key, analyzer);
			String[] fields=new String[]{"NAME","TITLE_NAME"};
				Occur[] occur = {Occur.SHOULD,Occur.SHOULD};
				searchQuery.setQuery(key,fields,occur,analyzer);
			if (StringUtils.isNotEmpty(type)) {
				searchQuery.setType(type);
			}
			searchQuery.setStart(start);
			searchQuery.setRows(rows);
			searchQuery.setHighlight(true);
			searchQuery.setHighlightSimplePre("<span style=\"color:#DD4B39;\">");
			searchQuery.setHighlightSimplePost("</span>");
			ds = engine.searchIndex(index_path, searchQuery);
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
