package hlj_qqheNew.app.icity.project;

import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.uc.UserDao;





import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.HttpClientUtil;

public class ProjectQueryDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	  protected static String icityDataSource = "icityDataSource";
	private ProjectQueryDao() {
		this.setDataSourceName("icityDataSource");
	}

	private static ProjectQueryDao _instance = null;

	public static ProjectQueryDao getInstance() {
		return DaoFactory.getDao(ProjectQueryDao.class.getName());
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
		JSONArray jsArray;
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAcceptQuantity?regionCode="+region_code);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject sum=JSONObject.fromObject(client.getResult(url,""));
		JSONObject json=new JSONObject();
		ds.setData(Tools.jsonToBytes(sum));
		jsArray = ds.getJAData();
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
		JSONObject ItemList;	
		try{
			ItemList =  JSONObject.fromObject(client.getResult(url, ""));
			ds.setData(Tools.jsonToBytes(ItemList));
		}catch(Exception e){
			e.printStackTrace();
		}
	return ds;
	}
	
	public DataSet getCatalog(ParameterSet pSet) {
		String type=(String)pSet.getParameter("type");
		String flage=(String)pSet.getParameter("flage");
//		String sql="select id,parent_id,name from power_catalog where parent_id in (select id from power_catalog where parent_id in (select id from power_catalog where parent_id ='null')) "
//		+"and parent_id=(select id from power_catalog where parent_id in (select id from power_catalog where parent_id ='null') and name='"+flage+"' "
//		+"and parent_id=(select id from power_catalog where parent_id ='null' and name='"+type+"'))";
		String sql="select id,parent_id,name from power_catalog t where class_type='"+type+"' and parent_id=(select id from power_catalog where parent_id ='null' and name='"+flage+"' and class_type='"+type+"' )";
		return this.executeDataset(sql, null, icityDataSource);
	}
	
	public DataSet getCatalogTitle(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getProjectTitle");

		HttpClientUtil client = new HttpClientUtil();
		JSONObject ItemList;	
		try{
			ItemList =  JSONObject.fromObject(client.getResult(url, ""));
			ds.setData(Tools.jsonToBytes(ItemList));
		}catch(Exception e){
			e.printStackTrace();
		}
	return ds;
	}
	
}
