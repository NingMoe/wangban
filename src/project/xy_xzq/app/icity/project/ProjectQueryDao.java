package xy_xzq.app.icity.project;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.project.ProjectIndexDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.HttpClientUtil;

public class ProjectQueryDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	  protected static String icityDataSource = "icityDataSource";
	private ProjectQueryDao() {
		this.setDataSourceName("icityDataSource");
	}

	private static ProjectQueryDao _instance = null;

	public static ProjectQueryDao getInstance() {
		_instance = (ProjectQueryDao)DaoFactory.getDao(ProjectQueryDao.class.getName());
		return _instance;
	}

	/**
	 * 按\r\n或\n拆分换行
	 * 
	 * @author XiongZhiwen
	 */
	protected static String replaceLine(String field) {
		StringBuilder ret = new StringBuilder();
		if (StringUtils.isNotEmpty(field)) {
			String[] t = field.split("(\r\n|\n)");
			for (int i = 0; i < t.length; i++) {
				t[i] = t[i].replaceAll("(^\\s+|\\s+$)", "");
				if (t[i].length() > 0) {
					ret.append("<p>" + t[i] + "</p>");
				}
			}
		}
		return ret.toString();
	}

	public DataSet getSummary(ParameterSet pSet) {
		String region_code=(String)pSet.getParameter("XZQHDM");
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAcceptQuantity?regionCode="+region_code);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject sum=JSONObject.fromObject(client.getResult(url,""));
		JSONObject json=new JSONObject();
		ds.setData(Tools.jsonToBytes(sum));
		JSONArray jsArray = ds.getJAData();
		json.put("ja", jsArray);
		ds.setData(Tools.jsonToBytes(json));
		return ds;
	}


	public DataSet getBjxx_sp(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String region_code=(String)pSet.getParameter("XZQHDM");
		String count=(String) pSet.getParameter("count");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getDisplayList?count="+count+"&region="+region_code);

		HttpClientUtil client = new HttpClientUtil();
		try{
			JSONObject ItemList =  JSONObject.fromObject(client.getResult(url, ""));
			ds.setData(Tools.jsonToBytes(ItemList));
		}catch(Exception e){
			e.printStackTrace();
		}
	return ds;
	}
	
	public DataSet getCatalog(ParameterSet pSet) {
		String type=(String)pSet.getParameter("type");
		String flage=(String)pSet.getParameter("flage");
		String sql="select id,parent_id,name from power_catalog where parent_id in (select id from power_catalog where parent_id in (select id from power_catalog where parent_id ='null')) "
		+"and parent_id=(select id from power_catalog where parent_id in (select id from power_catalog where parent_id ='null') and name='"+flage+"' "
		+"and parent_id=(select id from power_catalog where parent_id ='null' and name='"+type+"'))";
		return this.executeDataset(sql, null, icityDataSource);
	}
	public DataSet getCataloggr(ParameterSet pSet) {

		String sql="  select name as ztname,id from power_catalog   where  parent_id in (select code from power_catalog where name='个人')  order by ztname desc";
		return this.executeDataset(sql, null, icityDataSource);
	}
	public DataSet getCatalogfr(ParameterSet pSet) {

		String sql=" select name as ztname,id from power_catalog   where  parent_id in (select code from power_catalog where name='法人')  order by ztname desc ";
		return this.executeDataset(sql, null, icityDataSource);
	}
	public DataSet getCatalogTitle(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getProjectTitle");

		HttpClientUtil client = new HttpClientUtil();
		try{
			JSONObject ItemList =  JSONObject.fromObject(client.getResult(url, ""));
			ds.setData(Tools.jsonToBytes(ItemList));
		}catch(Exception e){
			e.printStackTrace();
		}
	return ds;
	}
	
}
