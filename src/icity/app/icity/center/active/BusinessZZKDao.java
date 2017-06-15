/**  
 * @Title: BusinessAskDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:23:07 
 * @version V1.0  
 */
package app.icity.center.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.Timestamp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * 政证快（快递信息服务）-漳州使用
 * @author yanhao
 * @date 2017-4-6
 *
 */

public class BusinessZZKDao extends BaseJdbcDao {
	private static Logger log = LoggerFactory.getLogger(BusinessZZKDao.class);

	protected BusinessZZKDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static BusinessZZKDao getInstance() {
		return DaoFactory.getDao(BusinessZZKDao.class.getName());
	}
	public DataSet getZzk(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet sblshSet = new DataSet();
		try {
			String isEMS = (String) pSet.getParameter("isEMS");
			String p_sblsh = (String) pSet.getParameter("sblsh");
			String eType = (String) pSet.getParameter("eType");
			String geturl =SecurityConfig.getString("approval_url")+"getZzk?regionCode="+SecurityConfig.getString("WebRegion");
			JSONObject getret =null ;
			JSONArray zzklist = new JSONArray();
			if(!StringUtils.isNotEmpty(p_sblsh)){//申办流水号为空
				if((StringUtils.isNotEmpty(isEMS)&&"1".equals(isEMS))){//EMS用户
					geturl =geturl+"&receiveNumber="+p_sblsh;
					getret = JSONObject.fromObject(RestUtil.getData(geturl));
					log.error("EMS用户政证快："+getret);
					JSONArray listtem = (JSONArray) getret.get("data");
					log.error("EMS用户政证快："+listtem);
					zzklist.addAll(listtem);
				}else{
					Connection conn = DbHelper.getConnection("icityDataSource");
					String sql = "select sblsh from business_express t where 1=1 ";
					String uid = (String) pSet.getParameter("uid");
					sql +="and t.operator = '"+uid+"' group by sblsh"; 
					List list = this.queryRaw(sql, null);
					for(int i = 0;list!=null&&i<list.size();i++){
						HashMap obj = (HashMap) list.get(i);
						String sblsh = (String) obj.get("SBLSH");
						geturl =geturl+"&receiveNumber="+sblsh;
						log.error("政证快："+geturl);
						getret = JSONObject.fromObject(RestUtil.getData(geturl));
						log.error("政证快1a："+getret);
						JSONArray listtem = (JSONArray) getret.get("data");
						log.error("政证快2a："+listtem);
						zzklist.addAll(listtem);
					}
				}
			}else{//有申办流水号查询条件
				geturl =geturl+"&receiveNumber="+p_sblsh;
				getret = JSONObject.fromObject(RestUtil.getData(geturl));
				log.error("政证快2b："+getret);
				JSONArray listtem = (JSONArray) getret.get("data");
				log.error("政证快2b："+listtem);
				zzklist.addAll(listtem);
			}
			log.error("政证快3："+zzklist);
			ds.setRawData(zzklist);
			ds.setTotal(zzklist.size());
			return ds;
		} catch (Exception e) {
			e.printStackTrace();
			return ds;
		}
	}

	public DataSet updateZzk(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url =SecurityConfig.getString("approval_url")+"updateZzk";
			String id = (String) pSet.getParameter("id");
			String bsnum = (String) pSet.getParameter("bsnum");
			String receiveNumber = (String) pSet.getParameter("receiveNumber");
			String eType = (String) pSet.getParameter("eType");
			String expressNumber = (String) pSet.getParameter("expressNumber");
			String userName = (String) pSet.getParameter("userName");
			String phone = (String) pSet.getParameter("phone");
			String address = (String) pSet.getParameter("address");
			String status = (String) pSet.getParameter("status");
			String context = (String) pSet.getParameter("context");
			String remark = (String) pSet.getParameter("remark");
			JSONObject parms = new JSONObject();
			parms.put("id", id);
			parms.put("bsnum", bsnum);
			parms.put("receiveNumber", receiveNumber);
			parms.put("eType", eType);//R-上门取件;C-送达上门
			parms.put("expressNumber", expressNumber);
			parms.put("userName", userName);
			parms.put("phone", phone);
			parms.put("address", address);
			parms.put("status", status);
			parms.put("context", context);
			parms.put("remark", remark);
			Map<String, String> map = new HashMap<String, String>();
			map.put("postdata", parms.toString());
			log.error("上门取件政证快："+url);
			log.error("上门取件政证快：参数："+map);
			JSONObject ret = JSONObject.fromObject(RestUtil.postData(url, map));
			log.error("上门取件政证快：返回结果："+ret);
			if("200".equals(ret.get("state"))){
				ds.setMessage("success");
				return ds;
			}else{
				ds.setMessage(ret.get("msg").toString());
				return ds;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			return ds;
		}
	}
}
