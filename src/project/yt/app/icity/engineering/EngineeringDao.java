package yt.app.icity.engineering;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.icity.govservice.GovProjectDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * 工程建设 lhy
 * 
 * @author lenovo
 * 
 */
public class EngineeringDao extends BaseJdbcDao {
	
	private static Logger logger = LoggerFactory.getLogger(EngineeringDao.class);
	
	private EngineeringDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static EngineeringDao getInstance() {
		return DaoFactory.getDao(EngineeringDao.class.getName());
	}
	/**
	 * 2.17	根据projectid获取事项
	 * @param pSet
	 * @return
	 */
	public DataSet getItemInfoByProjectId(ParameterSet pSet){
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "/getItemInfoByProjectId");
		String projectId = (String)pSet.get("projectId");
		String flowId = (String)pSet.get("flowId");	
		Map<String, String> data=new HashMap<String, String>();
		data.put("projectId", projectId);
		data.put("flowId", flowId);	
		
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if("200".equals(obj.getString("retCode"))){
				pSet.put("state", "1");
				ds.setRawData(obj.getJSONArray("notHandled"));
				ds.setTotal(obj.getJSONArray("notHandled").size());
				ds.setState(StateType.SUCCESS);
			}else{
				pSet.put("state", "0");
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
}
