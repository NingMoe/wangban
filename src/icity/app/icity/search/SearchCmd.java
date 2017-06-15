package app.icity.search;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.TermQuery;
import app.icity.search.engine.SearchEngine;
import app.icity.search.engine.impl.SearchEngineImpl;
import app.icity.search.pojo.SearchQuery;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

public class SearchCmd extends BaseQueryCommand {
	private SearchEngine engine = new SearchEngineImpl();// 调用接口方法实现创建索引
	private static String index_path="solr";
	public static SearchCmd getInstance(){
		return DaoFactory.getDao(SearchCmd.class.getName());
	}
	// 获取索引结果
	public DataSet getIndex(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			SearchQuery searchQuery = new SearchQuery();// 查询条件
 			String key = (String) pSet.getParameter("key");
 			if (StringUtils.isEmpty(key)) { 
 				key = "*:*";
			}
			String type=(String) pSet.getParameter("type");
			String theme = (String) pSet.getParameter("theme");
			String power_type = (String) pSet.getParameter("power_type");
			String org_code = (String) pSet.getParameter("org_code");
			String starTime = (String) pSet.getParameter("starTime");
			String endTime = (String) pSet.getParameter("endTime");
			String region_code = (String) pSet.getParameter("region_code");
			BooleanQuery bq=new BooleanQuery();
			if(StringUtils.isNotEmpty(theme)){
				for (String o : theme.split(":")) {
					bq.add(new TermQuery(new Term("THEME", o)),Occur.SHOULD);
				}
				searchQuery.setBooleanQuery(bq);
			}
			if (StringUtils.isNotEmpty(power_type)) {
				searchQuery.setFilterQueries("POWER_TYPE:" + power_type);
			}
			if (StringUtils.isNotEmpty(org_code)) {
				searchQuery.setFilterQueries("ORG_CODE:" + org_code);
			}
			//舟山模式新增所属网站属性  websitetype 0 默认外网 【1舟山审招委 2舟山内网 3公共】
			String websitetype = (String)pSet.getParameter("websitetype");
			if(StringUtils.isNotEmpty(websitetype)){
				searchQuery.setQuery(
						new String[]{websitetype,"3"},
						new String[]{"WEBSITETYPE","WEBSITETYPE"},
						new Occur[]{Occur.SHOULD,Occur.SHOULD});
			}
			
			//舟山模式是否全局查询 不分区区划
			String webrank = (String)pSet.getParameter("webrank");
			if(StringUtil.isEmpty(webrank)){
				if(StringUtil.isNotEmpty(region_code)){
					searchQuery.setFilterQueries("REGION_CODE:" + region_code);
				}else{
					searchQuery.setFilterQueries("REGION_CODE:" + SecurityConfig.getString("WebRegion"));
				}
			}
			searchQuery.setQuery(
					new String[]{key,key},
					new String[]{"NAME","TITLE_NAME"},
					new Occur[]{Occur.SHOULD,Occur.SHOULD});
			if (StringUtils.isNotEmpty(type)) {
				searchQuery.setType(type);
			}
			//按时间段进行查询
			if (StringUtils.isNotEmpty(starTime)) {
				searchQuery.setRangeQuery("DAY", starTime,endTime, true, true);
			}
			int start = 1, rows = 5;
			String s = (String) pSet.getParameter("start");
			if (StringUtils.isNotEmpty(s)) {
				start = Integer.valueOf(s);
			}
			String l = (String) pSet.getParameter("limit");
			if (StringUtils.isNotEmpty(l)) {
				rows = Integer.valueOf(l);
			}
			start=(start-1)*rows;
			searchQuery.setStart(start);
			searchQuery.setRows(rows);
			
			String sortBy=(String) pSet.getParameter("sortBy");
			//排序方式：相关度（默认）、时间
			SortField scoreSort = new SortField(null, SortField.SCORE, false);
			SortField timeSort=new SortField("DAY", SortField.STRING, true);
			SortField typeSort=new SortField("TYPE", SortField.STRING, false);
			Sort sort = new Sort(new SortField[]{scoreSort,typeSort});
			if("t".equals(sortBy)){
			sort = new Sort(new SortField[]{timeSort,typeSort});
			}
			searchQuery.setHighlight(true);
			searchQuery.setHighlightSimplePre("<span style=\"color:#DD4B39;\">");
			searchQuery.setHighlightSimplePost("</span>");
			String m=(String) pSet.getParameter("m");
			String groupBy=(String) pSet.getParameter("groupBy");
			//group 分组查询
			if("group".equals(m)){
				if(StringUtils.isNotEmpty(groupBy)){
				ds = engine.searchIndexByGroup(index_path,searchQuery,groupBy.toUpperCase());
			}}
			else{
				ds = engine.searchIndex(index_path, searchQuery,sort);
			}
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
