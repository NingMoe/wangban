package yt.app.icity.engineering;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

/**
 * 工程建设投资 lhy
 * 
 * @author lenovo
 * 
 */
public class EngineeringCmd extends BaseQueryCommand {
	/**
	 * 2.17	根据区划和阶段获取流程
	 * @param pSet
	 * @return
	 */
	public DataSet getItemInfoByProjectId(ParameterSet pSet){
		return EngineeringDao.getInstance().getItemInfoByProjectId(pSet);
	}	
}
