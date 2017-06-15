package app.icity;

import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import app.icity.sync.UploadUtil;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.PathUtil;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class ServiceCmd extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "ServiceCmd";

	private static Logger log = LoggerFactory.getLogger(BaseQueryCommand.class);

	public static ServiceCmd getInstance() {
		return DaoFactory.getDao(ServiceCmd.class.getName());
	}

	/**
	 * 根据区划代码获取下级区划列表
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getDeptList(ParameterSet pset) {
		String webRegion = SecurityConfig.getString("WebRegion");
		String regioncode = (String) pset.getParameter("WebRegion");
		String region_code = (String) pset.getParameter("region_code");
		if(StringUtil.isNotEmpty(regioncode)){
			webRegion = regioncode;
		}
		if(StringUtil.isNotEmpty(region_code)){
			webRegion = region_code;
		}
		String key = "getDeptList" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					if(region_code == null||"".equals(region_code)){
						pset.put("region_code", webRegion);
						}
					ds = getDeptListT(pset);
					if ("200".equals(ds.getJOData().getString("code"))) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		return ds;
	}

	public DataSet getDeptListT(ParameterSet pset) {
		DataSet ds = new DataSet();
		// String url =
		// HttpUtil.formatUrl(SecurityConfig.getString("synchronousDept")+"/web/organ");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/getDeptList");
		Map<String, String> data=new HashMap<String, String>();
		data.put("region_code", (String) pset.getParameter("region_code"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ds;
	}
	/**
	 * 根据部门code获取部门详细信息
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getDeptListInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		String orgCode = (String) pset.getParameter("orgCode");
		String url = HttpUtil.formatUrl(
				SecurityConfig.getString("ItemSynchronizationUrl") + "/getWindowByOrgCode?orgCode=" + orgCode);
		try {
			JSONObject resultJson = JSONObject.fromObject(RestUtil.getData(url));
			Object obj = resultJson.get("data");
			ds.setRawData(obj.toString());
		} catch (Exception e) {
			log.error("获取部门详细信息出错！");
		}
		return ds;
	}
	/**
	 * 查询当前 指导区划 指定部门下的 科室
	 * @param pset
	 * @return
	 */
	public DataSet getItemAgentList(ParameterSet pset) {
		DataSet ds = new DataSet();
		/*String orgCode = (String) pset.getParameter("orgCode");*/
		String region = SecurityConfig.getString("WebRegion");
		String url = HttpUtil.formatUrl(
				SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemAgentList");
		try {
			/*Map<String, String> data=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			if (StringUtils.isNotEmpty(orgCode)) {
				whereValue.append(" and orgCode=? ");
				paramValue.add(orgCode); 
			}
			if (StringUtils.isNotEmpty(region)) {
				whereValue.append(" and region_code=? ");
				paramValue.add(region);
			}
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());*/
			JSONArray resultJson = JSONArray.fromObject(RestUtil.getData(url+"?regionCode="+region));
			ds.setRawData(resultJson);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取部门科室出错！");
		}
		return ds;
	}
	/**
	 * 根据事项CODE获取事项的基本信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPermissionAll(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String itemCode = (String) pSet.getParameter("code");
		String url = HttpUtil.formatUrl(
				SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemInfoByItemCode?itemCode=" + itemCode);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
		JSONArray itemInfo = item.getJSONArray("ItemInfo");// 基本信息
		JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
		item.put("itemBasicInfo", itemBasicInfo);
		JSONArray materials = item.getJSONArray("material");// 申请材料
		item.put("materials", materials);
		// log.info(item.toString());
		// JSONArray document= item.getJSONArray("document");//标准文书
		JSONArray charge = item.getJSONArray("charge");// 收费标准
		item.put("charge", charge);
		// JSONArray legalbasis = item.getJSONArray("legalbasis");//法律依据

		// 2016/3/9展示办理流程图
		JSONArray outmap = item.getJSONArray("outmap");// 办理流程
		item.put("outmap", outmap);
		JSONArray condition = item.getJSONArray("condition");// 受理条件
		item.put("condition", condition);
		JSONArray window = item.getJSONArray("window");// 办事地址
		item.put("windows", window);
		ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
		return ds;

	}
	/**
	 * 根据事项id获取事项的基本信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPermission(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String itemId = (String) pSet.getParameter("id");
		String url ="";
		String flag = (String) pSet.getParameter("flag");
		if(StringUtils.isNotEmpty(flag)){
			url = HttpUtil.formatUrl(
					SecurityConfig.getString("ItemSynchronizationUrl_gg") + "/getAllItemInfoByItemID?itemId=" + itemId);
		}else{
			url = HttpUtil.formatUrl(
					SecurityConfig.getString("ItemSynchronizationUrl") + "/getAllItemInfoByItemID?itemId=" + itemId);
		}
		HttpClientUtil client = new HttpClientUtil();
		JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
		JSONArray itemInfo = item.getJSONArray("ItemInfo");// 基本信息
		JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
		item.put("itemBasicInfo", itemBasicInfo);
		JSONArray materials = item.getJSONArray("material");// 申请材料
		item.put("materials", materials);
		// log.info(item.toString());
		// JSONArray document= item.getJSONArray("document");//标准文书
		JSONArray charge = item.getJSONArray("charge");// 收费标准
		item.put("charge", charge);
		// JSONArray legalbasis = item.getJSONArray("legalbasis");//法律依据

		// 2016/3/9展示办理流程图
		JSONArray outmap = item.getJSONArray("outmap");// 办理流程
		item.put("outmap", outmap);
		JSONArray condition = item.getJSONArray("condition");// 受理条件
		item.put("condition", condition);
		JSONArray window = item.getJSONArray("window");// 办事地址
		item.put("windows", window);
		ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
		return ds;

	}

	public DataSet getDuty(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseOrgDutyList");
			Map<String, String> data=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String deptid = (String) pSet.getParameter("deptid");
			String duty_id = (String) pSet.getParameter("id");
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			if (StringUtils.isNotEmpty(duty_id)) {
				whereValue.append(" and id=? ");
				paramValue.add(duty_id);
			}
			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public DataSet getBoundary(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseBoundaryItemList");
			Map<String, String> data=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String deptid = (String) pSet.getParameter("deptid");
			String duty_id = (String) pSet.getParameter("id");
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			if (StringUtils.isNotEmpty(duty_id)) {
				whereValue.append(" and id=? ");
				paramValue.add(duty_id);
			}
			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	// 查询边界部门
	public DataSet getBoundaryItemInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String itemId = (String) pSet.getParameter("id");
		String url = HttpUtil.formatUrl(
				SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseBoundaryItemInfo?id=" + itemId);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
		JSONObject itemInfo = item.getJSONObject("BOUNDARYITEM");
		JSONArray itemBasicInfo = item.getJSONArray("BOUNDARYDUTY");// 基本信息BOUNDARYITEM
		// JSONObject itemBasicInfo=info.getJSONObject(0);
		item.put("itemInfo", itemInfo);
		item.put("itemBasicInfo", itemBasicInfo);
		ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
		return ds;

	}

	public DataSet getSupervise(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseSuperviseSystemList");
			Map<String, String> data=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String deptid = (String) pSet.getParameter("deptid");
			String duty_id = (String) pSet.getParameter("id");
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			if (StringUtils.isNotEmpty(duty_id)) {
				whereValue.append(" and id=? ");
				paramValue.add(duty_id);
			}
			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	// 获取办理意见
	public DataSet getEnterPrise(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl") + "/getHandleOpinion");
		Map<String, String> data=new HashMap<String, String>();
		data.put("bizId", (String) pset.getParameter("receiveid"));
		data.put("itemId", (String) pset.getParameter("iid"));

		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setData(obj.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 获取工程建设企业投资阶段
	public DataSet getEnginState(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ECParallelUrl") + "getStage");
		Map<String, String> data=new HashMap<String, String>();
		data.put("investType", (String) pset.getParameter("investType"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 获取工程建设企业业务进度（事项）
	public DataSet getEnginProcess(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ECParallelUrl") + "getBusinessProcess");
		Map<String, String> data=new HashMap<String, String>();
		data.put("bizId", (String) pset.getParameter("bizId"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 获取工程建设企业投资办理意见
	public DataSet getHandleOpinion(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ECParallelUrl") + "getHandleOpinion");
		Map<String, String> data=new HashMap<String, String>();
		data.put("itemId", (String) pset.getParameter("itemId"));
		data.put("bizId", (String) pset.getParameter("bizId"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 通讯录查询
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getUserInfoByUserName(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/getUserInfoByUserName");
		Map<String, String> data=new HashMap<String, String>();

		if (pset.getParameter("name") != null) {
			data.put("userName", (String) pset.getParameter("name"));
		} else {
			data.put("userName", "");
		}

		if (pset.getParameter("dept") != null) {
			data.put("dept", (String) pset.getParameter("dept"));
		} else {
			data.put("dept", "");
		}
		try {
			JSONObject obj;
			Object sss = RestUtil.postData(url, data);
			obj = JSONObject.fromObject(sss);
			JSONArray jsonarray = obj.getJSONArray("list");
			JSONObject temp;
			for (int i = 0; i < jsonarray.size(); i++) {
				temp = jsonarray.getJSONObject(i);
				if (temp.get("PHONE_NET") == null || "null".equals(temp.get("PHONE_NET").toString())) {
					temp.put("PHONE_NET", "");
					
					jsonarray.getJSONObject(i).put("PHONE_NET","");
				}
				if (temp.get("POSITION") == null || "null".equals(temp.get("POSITION").toString())) {
					temp.put("POSITION", "");
					
					jsonarray.getJSONObject(i).put("POSITION","");
				}
				if (temp.get("MOBILE_NET") == null || "null".equals(temp.get("MOBILE_NET").toString())) {
					temp.put("MOBILE_NET", "");
					
					jsonarray.getJSONObject(i).put("MOBILE_NET","");
				}
				if (temp.get("EMAIL") == null || "null".equals(temp.get("EMAIL").toString())) {
					temp.put("EMAIL", "");
					
					jsonarray.getJSONObject(i).put("EMAIL","");
				}
				if (temp.get("STATUS") == null || "null".equals(temp.get("STATUS").toString())) {
					temp.put("STATUS", "");
					
					jsonarray.getJSONObject(i).put("STATUS","");
				}
				if (temp.get("ORG_CODE") == null || "null".equals(temp.get("ORG_CODE").toString())) {
					temp.put("ORG_CODE", "");
					
					jsonarray.getJSONObject(i).put("ORG_CODE","");
				}
				if (temp.get("ORG_NAME") == null || "null".equals(temp.get("ORG_NAME").toString())) {
					temp.put("ORG_NAME", "");
					
					jsonarray.getJSONObject(i).put("ORG_NAME","");
				}
			}
			
			obj.put("list", jsonarray);
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;

	}

	/**
	 * 舟山统计缓存
	 * 
	 * @param pset
	 * @return
	 */
	public String getBjtjCacheZs(ParameterSet pset) {
		String webRegion = SecurityConfig.getString("WebRegion");
		String type = (String) pset.getParameter("type");
		String key = "getBjtjCacheZs" + webRegion + type;
		String ds = (String) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
		if (ds == null) {
			synchronized (key.intern()) {
				ds = (String) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds = getBjtj(pset);
					CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
				}
			}
		}
		return ds;
	}

	public String getBjtj(ParameterSet pset) {
		String type = (String) pset.getParameter("type");
		String islogin = (String) pset.getParameter("islogin");
		String date = "";
		String region = "";
		if ("true".equals(islogin)) {
			UserInfo user = this.getUserInfo(pset);
			region = user.getRegionId();
		} else {
			region = SecurityConfig.getString("WebRegion");
		}
		JSONArray data = new JSONArray();
		try {
			if ("year".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY);
			} else if ("month".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY_MM);
			} else if ("day".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY_MM_DD);
			}

			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/statisticsByPeriodQuery?start="
					+ date + "&timeType=" + type + "&region=" + region);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject json = JSONObject.fromObject(client.getResult(url, ""));

			String url_ql = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")
					+ "/statisticsByPeriodQuery?start=" + date + "&timeType=" + type + "&region=" + region);
			HttpClientUtil client_ql = new HttpClientUtil();
			JSONObject json_ql = JSONObject.fromObject(client_ql.getResult(url_ql, ""));

			if ("200".equals(json.getString("state"))) {
				data = json.getJSONArray("data");
				if ("200".equals(json_ql.getString("state"))) {
					JSONObject _data = new JSONObject();
					_data.put("ACCEPT", data.getJSONObject(0).getInt("ACCEPT")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("ACCEPT"));
					_data.put("CHENGNUO", data.getJSONObject(0).getInt("CHENGNUO")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("CHENGNUO"));
					_data.put("COMPLETE", data.getJSONObject(0).getInt("COMPLETE")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("COMPLETE"));
					_data.put("CONSULT", data.getJSONObject(0).getInt("CONSULT")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("CONSULT"));
					_data.put("CORRECTION", data.getJSONObject(0).getInt("CORRECTION")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("CORRECTION"));
					_data.put("EARLYFINISH", data.getJSONObject(0).getInt("EARLYFINISH")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("EARLYFINISH"));
					_data.put("INTERNET", data.getJSONObject(0).getInt("INTERNET")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("INTERNET"));
					_data.put("JIBAN", data.getJSONObject(0).getInt("JIBAN")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("JIBAN"));
					_data.put("OVERTIME", data.getJSONObject(0).getInt("OVERTIME")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("OVERTIME"));
					_data.put("SUSPEND", data.getJSONObject(0).getInt("SUSPEND")
							+ json_ql.getJSONArray("data").getJSONObject(0).getInt("SUSPEND"));
					_data.put("time", data.getJSONObject(0).getString("time"));
					data.set(0, _data);
				}
			}
		} catch (Exception e) {
			log.error("获取办件统计信息出错！");
		}
		return data.toString();
	}

	public String getBjtj1(ParameterSet pset) {
		String type = (String) pset.getParameter("type");
		String region = SecurityConfig.getString("WebRegion");
		String date = "";
		JSONArray data = new JSONArray();
		try {
			if ("year".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY);
			} else if ("month".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY_MM);
			} else if ("day".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY_MM_DD);
			}
			String url="";
			if(StringUtil.isNotEmpty((String)pset.getParameter("count"))){
				for(int i=6;i>-1;i--){
					Calendar c = Calendar.getInstance();
					c.add(Calendar.MONTH, -i);
					date = CommonUtils_api.getInstance().parseDateToString(c.getTime(), CommonUtils_api.YYYY_MM);
					url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/statisticsByPeriodQuery?start="
							+ date + "&timeType=" + type + "&region=" + region);
					HttpClientUtil client = new HttpClientUtil();
					JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
					if ("200".equals(json.getString("state"))) {
						data.add(json.getJSONArray("data").getJSONObject(0));
					}
				}
			}else{
				url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/statisticsByPeriodQuery?start="
						+ date + "&timeType=" + type + "&region=" + region);
				HttpClientUtil client = new HttpClientUtil();
				JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
				if ("200".equals(json.getString("state"))) {
					data = json.getJSONArray("data");
				}
			}
		} catch (Exception e) {
			log.error("获取办件统计信息出错！");
		}
		return data.toString();
	}

	// 昨日、月、年
	public String getBjtj2(ParameterSet pset) {
		String type = (String) pset.getParameter("type");
		String region = SecurityConfig.getString("WebRegion");
		String orgCode = (String) pset.getParameter("orgCode");
		String date = "";
		Date yes = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(yes);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
		yes = cal.getTime();
		JSONArray data = new JSONArray();
		try {
			if ("year".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY);
			} else if ("month".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY_MM);
			} else if ("day".equals(type)) {
				date = CommonUtils_api.getInstance().parseDateToString(yes, CommonUtils_api.YYYY_MM_DD);
			}

			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/statisticsByPeriodQuery?start="
					+ date + "&timeType=" + type + "&region=" + region+"&orgCode="+orgCode);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject json = JSONObject.fromObject(client.getResult(url, ""));

			if ("200".equals(json.getString("state"))) {
				data = json.getJSONArray("data");

			}
		} catch (Exception e) {
			log.error("获取办件统计信息出错！");
		}
		return data.toString();
	}

	/**
	 * 根据负面清单
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getBinList(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseNegativeList");
		try {
			Object obj = RestUtil.getData(url);
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 *办件报数
	 */
	public DataSet countoffstatistics(ParameterSet pSet){
		DataSet ds = new DataSet();
		String region = SecurityConfig.getString("WebRegion");
		int ACCEPT_COUNT = 0;// 报数受理数
		int ACCEPT_COUNT_LASTMONTH = 0;// 报数上月受理数
		int ACCEPT_COUNT_LASTSEASON = 0;// 报数上季度受理数
		int ACCEPT_COUNT_LASTYEAR = 0;// 报数上年受理数
		int ACCEPT_COUNT_THISMONTH = 0;
		int ACCEPT_COUNT_YESTERDAY=0;

		int FINISH_COUNT = 0;// 报数办结总数
		int FINISH_COUNT_LASTMONTH = 0;// 报数上月办结数
		int FINISH_COUNT_LASTSEASON = 0;// 报数上季度办结数
		int FINISH_COUNT_LASTYEAR = 0;// 报数上年办结数
		int FINISH_COUNT_THISMONTH=0;
		int FINISH_COUNT_YESTERDAY=0;
		JSONArray data = new JSONArray();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/countoffstatistics?region_code="+region);
			HttpClientUtil client = new HttpClientUtil();			
			JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
			data = json.getJSONArray("data");
			ds.setTotal(data.size());
			/*for (int i = 0; i < data.size(); i++) {
				ACCEPT_COUNT += data.getJSONObject(i).getInt("ACCEPT_COUNT");
				ACCEPT_COUNT_LASTMONTH += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTMONTH");
				ACCEPT_COUNT_LASTSEASON += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTSEASON");
				ACCEPT_COUNT_LASTYEAR += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTYEAR");
				ACCEPT_COUNT_THISMONTH+=data.getJSONObject(i).getInt("ACCEPT_COUNT_THISMONTH");
				ACCEPT_COUNT_YESTERDAY+=data.getJSONObject(i).getInt("ACCEPT_COUNT_YESTERDAY");
				
				FINISH_COUNT += data.getJSONObject(i).getInt("FINISH_COUNT");
				FINISH_COUNT_LASTMONTH += data.getJSONObject(i).getInt("FINISH_COUNT_LASTMONTH");
				FINISH_COUNT_LASTSEASON += data.getJSONObject(i).getInt("FINISH_COUNT_LASTSEASON");
				FINISH_COUNT_LASTYEAR += data.getJSONObject(i).getInt("FINISH_COUNT_LASTYEAR");
				FINISH_COUNT_THISMONTH += data.getJSONObject(i).getInt("FINISH_COUNT_THISMONTH");
				FINISH_COUNT_YESTERDAY += data.getJSONObject(i).getInt("FINISH_COUNT_YESTERDAY");
			}	*/
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	/**
	 * 办件统计（十堰）+(天门)+(福州cqs)
	 * 
	 * @param pset
	 * @return
	 */
	public String getBjtj3(ParameterSet pset) {
		String region = (String) pset.getParameter("region_code");
		if (!StringUtils.isNotEmpty(region)) {
			region = SecurityConfig.getString("WebRegion");
		}
		JSONArray data = new JSONArray();
		try {

			String url = HttpUtil
					.formatUrl(SecurityConfig.getString("approval_url") + "/getAcceptQuantity?regionCode=" + region);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject json = JSONObject.fromObject(client.getResult(url, ""));

			if ("200".equals(json.getString("state"))) {
				data = JSONArray.fromObject(json);
				;

			}
		} catch (Exception e) {
			log.error("获取办件统计信息出错！");
		}
		return data.toString();
	}
	/**
	 *累计办理 统计
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getAcceptQuantity(ParameterSet pset) {
		DataSet ds = new DataSet();
		String region = (String) pset.getParameter("region_code");
		if (!StringUtils.isNotEmpty(region)) {
			region = SecurityConfig.getString("WebRegion");
		}
		JSONArray data = new JSONArray();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getAcceptQuantity?regionCode=" + region);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
			if ("200".equals(json.getString("state"))) {
				data = JSONArray.fromObject(json);
			}
			ds.setState(StateType.SUCCESS);
			ds.setRawData(data);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			log.error("获取办件统计信息出错！");
		}
		return ds;
	}
	// 报数+办件数 办件统计
	public String getBjtjNew(ParameterSet pset) {
		String region = (String) pset.getParameter("region_code");
		if (!StringUtils.isNotEmpty(region)) {
			region = SecurityConfig.getString("WebRegion");
		}
		int ACCEPT_COUNT = 0;// 报数受理数
		int ACCEPT_COUNT_LASTMONTH = 0;// 报数上月受理数
		int ACCEPT_COUNT_LASTSEASON = 0;// 报数上季度受理数
		int ACCEPT_COUNT_LASTYEAR = 0;// 报数上年受理数

		int FINISH_COUNT = 0;// 报数办结总数
		int FINISH_COUNT_LASTMONTH = 0;// 报数上月办结数
		int FINISH_COUNT_LASTSEASON = 0;// 报数上季度办结数
		int FINISH_COUNT_LASTYEAR = 0;// 报数上年办结数
		JSONArray data = new JSONArray();
		JSONArray data1;
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/countoffstatistics?regionCode=" + region);
			HttpClientUtil client = new HttpClientUtil();
			try {
				JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
				data = json.getJSONArray("data");
				for (int i = 0; i < data.size(); i++) {
					ACCEPT_COUNT += data.getJSONObject(i).getInt("ACCEPT_COUNT");
					ACCEPT_COUNT_LASTMONTH += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTMONTH");
					ACCEPT_COUNT_LASTSEASON += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTSEASON");
					ACCEPT_COUNT_LASTYEAR += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTYEAR");

					FINISH_COUNT += data.getJSONObject(i).getInt("FINISH_COUNT");
					FINISH_COUNT_LASTMONTH += data.getJSONObject(i).getInt("FINISH_COUNT_LASTMONTH");
					FINISH_COUNT_LASTSEASON += data.getJSONObject(i).getInt("FINISH_COUNT_LASTSEASON");
					FINISH_COUNT_LASTYEAR += data.getJSONObject(i).getInt("FINISH_COUNT_LASTYEAR");
				}
			} catch (Exception e) {
 
			}
			String url_ql = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/businessstatistics?region_code=" + region);
			HttpClientUtil client_ql = new HttpClientUtil();
			try {
				JSONObject json_ql = JSONObject.fromObject(client_ql.getResult(url_ql, ""));
				data1 = json_ql.getJSONArray("data");
				for (int i = 0; i < data1.size(); i++) {
					ACCEPT_COUNT_LASTSEASON += data1.getJSONObject(i).getInt("ACCEPT_LASTSEASON");
					ACCEPT_COUNT_LASTYEAR += data1.getJSONObject(i).getInt("ACCEPT_LASTYEAR");

					FINISH_COUNT_LASTSEASON += data1.getJSONObject(i).getInt("COMPLETE_LASTSEASON");
					FINISH_COUNT_LASTYEAR += data1.getJSONObject(i).getInt("COMPLETE_LASTYEAR");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			JSONObject _data = new JSONObject();
			_data.put("ACCEPT_COUNT", ACCEPT_COUNT);
			_data.put("ACCEPT_COUNT_LASTMONTH", ACCEPT_COUNT_LASTMONTH);
			_data.put("ACCEPT_COUNT_LASTSEASON", ACCEPT_COUNT_LASTSEASON);
			_data.put("ACCEPT_COUNT_LASTYEAR", ACCEPT_COUNT_LASTYEAR);
			_data.put("FINISH_COUNT", FINISH_COUNT);
			_data.put("FINISH_COUNT_LASTMONTH", FINISH_COUNT_LASTMONTH);
			_data.put("FINISH_COUNT_LASTSEASON", FINISH_COUNT_LASTSEASON);
			_data.put("FINISH_COUNT_LASTYEAR", FINISH_COUNT_LASTYEAR);
			data.set(0, _data);
		} catch (Exception e) {
			log.error("获取办件统计信息出错！");
		}
		return data.toString();
	}

	// 排队预约短信发送
	public DataSet sendMessage(ParameterSet pSet) {

		DataSet ds = new DataSet();

		String date = (String) pSet.getParameter("date");
		String time = (String) pSet.getParameter("time");
		String bizname = (String) pSet.getParameter("bizname");
		String phonestr = (String) pSet.getParameter("phonestr");
		String address = (String) pSet.getParameter("address");
		String message = "";

		message = SecurityConfig.getString("message_pdyy");
		message = message.replace("{date}", date).replace("{time}", time)
				.replace("{bizname}", bizname.replace("业务", "")).replace("{address}", address);

		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url") + "sendMessage");
		Map<String, String> data=new HashMap<String, String>();
		data.put("phoneList", phonestr);
		data.put("mesConent", message);

		try {
			Object obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
		} catch (Exception e) {
			log.error("短信发送失败！");
		}

		return ds;
	}
	
	/**
	 * 办件报数+审批直报 办件统计
	 * 办件报数=countoffstatistics
	 * {
    	"data": [
        {
            "ACCEPT_COUNT": 308,
            "ACCEPT_COUNT_LASTMONTH": 0,
            "ACCEPT_COUNT_LASTSEASON": 0,
            "ACCEPT_COUNT_LASTYEAR": 5,
			"ACCEPT_COUNT_THISMONTH": 0,
			"ACCEPT_COUNT_TIMESECTION": 5,
            "ACCEPT_COUNT_YESTERDAY": 0,
            "FINISH_COUNT": 86,
            "FINISH_COUNT_LASTMONTH": 0,
            "FINISH_COUNT_LASTSEASON": 0,
            "FINISH_COUNT_LASTYEAR": 5,
			"FINISH_COUNT_THISMONTH": 0,
			"FINISH_COUNT_TIMESECTION": 5,
            "FINISH_COUNT_YESTERDAY": 0,
            "ORG_CODE": "370000004502489",
            "ORG_NAME": "山东省工商局"
        }
    ],
    "state": "200"
	}
	 * 累计办理业务信息=getAcceptQuantity
	 * {
		  "accept": 15518,//累计受理数
		  "completed": 12512,// //累计办结数
		  "handle": 3006, //累计办件数
		  "monthAccept": 37, //当月受理数
		  "monthCompleted": 18, //当月办结数
		  "monthApply": 5, //当月申报
		  "nowAccept": 4,//当天受理数
		  "nowApply": 5, //当天申报
		  "nowCompleted": 1, //当天办结
		  "nowDoing": 6, //当天在办
		  "preMonthAccept": 323,//上月受理数
		  "preMonthCompleted": 183,//上月办结数
		  "state": "200",
		  "yesterdayAccept": 16//昨日受理数
		  “yesterdayCompleted”:32//昨日办结
		  "yesterdayApply": 0, //昨日申报
		  "yearAccept": 1427, //本年收件
		  "yearApply": 809, //本年申报
		  "yearCompleted": 833, //本年办结
		  "yearDoing": 594, //本年在办
		}
	 */
	public DataSet getAlltj(ParameterSet pSet){
		DataSet ds = new DataSet();
		String region = SecurityConfig.getString("WebRegion");
		int ACCEPT_COUNT = 0;// 报数受理数
		int ACCEPT_COUNT_LASTMONTH = 0;// 报数上月受理数
		int ACCEPT_COUNT_LASTSEASON = 0;// 报数上季度受理数
		int ACCEPT_COUNT_LASTYEAR = 0;// 报数上年受理数
		int ACCEPT_COUNT_THISMONTH = 0;
		int ACCEPT_COUNT_YESTERDAY=0;

		int FINISH_COUNT = 0;// 报数办结总数
		int FINISH_COUNT_LASTMONTH = 0;// 报数上月办结数
		int FINISH_COUNT_LASTSEASON = 0;// 报数上季度办结数
		int FINISH_COUNT_LASTYEAR = 0;// 报数上年办结数
		int FINISH_COUNT_THISMONTH=0;
		int FINISH_COUNT_YESTERDAY=0;
		JSONArray data = new JSONArray();
		JSONArray data1;
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/countoffstatistics");
			HttpClientUtil client = new HttpClientUtil();
			try {
				JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
				data = json.getJSONArray("data");
				for (int i = 0; i < data.size(); i++) {
					ACCEPT_COUNT += data.getJSONObject(i).getInt("ACCEPT_COUNT");
					ACCEPT_COUNT_LASTMONTH += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTMONTH");
					ACCEPT_COUNT_LASTSEASON += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTSEASON");
					ACCEPT_COUNT_LASTYEAR += data.getJSONObject(i).getInt("ACCEPT_COUNT_LASTYEAR");
					ACCEPT_COUNT_THISMONTH+=data.getJSONObject(i).getInt("ACCEPT_COUNT_THISMONTH");
					ACCEPT_COUNT_YESTERDAY+=data.getJSONObject(i).getInt("ACCEPT_COUNT_YESTERDAY");
					
					FINISH_COUNT += data.getJSONObject(i).getInt("FINISH_COUNT");
					FINISH_COUNT_LASTMONTH += data.getJSONObject(i).getInt("FINISH_COUNT_LASTMONTH");
					FINISH_COUNT_LASTSEASON += data.getJSONObject(i).getInt("FINISH_COUNT_LASTSEASON");
					FINISH_COUNT_LASTYEAR += data.getJSONObject(i).getInt("FINISH_COUNT_LASTYEAR");
					FINISH_COUNT_THISMONTH += data.getJSONObject(i).getInt("FINISH_COUNT_THISMONTH");
					FINISH_COUNT_YESTERDAY += data.getJSONObject(i).getInt("FINISH_COUNT_YESTERDAY");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String m_url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getAcceptQuantity?regionCode=" + region);
			HttpClientUtil m_client = new HttpClientUtil();
			try {
				JSONObject json = JSONObject.fromObject(m_client.getResult(m_url, ""));
				json.put("accept", json.getInt("accept")+ACCEPT_COUNT);//累计受理数
				json.put("completed", json.getInt("completed")+FINISH_COUNT);//"completed": 12512,// //累计办结数
				json.put("handle", json.getInt("handle"));//"handle": 3006, //累计办件数
				json.put("monthAccept", json.getInt("monthAccept")+ACCEPT_COUNT_THISMONTH);//"monthAccept": 37, //当月受理数
				json.put("monthCompleted", json.getInt("monthCompleted")+FINISH_COUNT_THISMONTH);//"monthCompleted": 18, //当月办结数
				json.put("monthApply", json.getInt("monthApply"));//"monthApply": 5, //当月申报
				json.put("nowAccept", json.getInt("nowAccept"));//"nowAccept": 4,//当天受理数
				json.put("nowApply", json.getInt("nowApply"));//"nowApply": 5, //当天申报
				json.put("nowCompleted", json.getInt("nowCompleted"));//"nowCompleted": 1, //当天办结
				json.put("nowDoing", json.getInt("nowDoing"));//"nowDoing": 6, //当天在办
				json.put("preMonthAccept", json.getInt("preMonthAccept")+ACCEPT_COUNT_LASTMONTH);//"preMonthAccept": 323,//上月受理数
				json.put("preMonthCompleted", json.getInt("preMonthCompleted")+FINISH_COUNT_LASTMONTH);//"preMonthCompleted": 183,//上月办结数
				json.put("yesterdayAccept", json.getInt("yesterdayAccept")+ACCEPT_COUNT_YESTERDAY);//"yesterdayAccept": 16//昨日受理数
				json.put("yesterdayCompleted", json.getInt("yesterdayCompleted")+FINISH_COUNT_YESTERDAY);//“yesterdayCompleted”:32//昨日办结
				json.put("yesterdayApply", json.getInt("yesterdayApply"));//"yesterdayApply": 0, //昨日申报
				json.put("yearAccept", json.getInt("yearAccept"));//"yearAccept": 1427, //本年收件
				json.put("yearApply", json.getInt("yearApply"));//"yearApply": 809, //本年申报
				json.put("yearCompleted", json.getInt("yearCompleted"));//"yearCompleted": 833, //本年办结
				json.put("yearDoing", json.getInt("yearDoing"));//"yearDoing": 594, //本年在办
				ds.setState(StateType.SUCCESS);
				JSONArray ja = new JSONArray();
				ja.add(json);
				ds.setRawData(ja);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取办件统计信息出错！");
		}
		return ds;
	}
	
	public DataSet uploadMaterial(ParameterSet pset) {
		DataSet ds = new DataSet();
		String base_value = (String) pset.getParameter("base_value");
		String uploadUrl = SecurityConfig.getString("NetDiskAddress");
		String name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String path = PathUtil.getWebPath() + "file" + File.separator + "temp" + File.separator + name +".edc";
		try {
			decoderBase64File(base_value,path);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		UserInfo user = this.getUserInfo(pset);
		try {
			// 将临时文件转换为真实文件名，然后再上传到网盘,File.separator在linux部署环境下为/
			File file = new File(path);
			Map<String, String> params = new HashMap<String, String>();
			if (!"".equals(user.getUserId()) && null != user.getUserId()) {
				params.put("uid", user.getUserId());
			} else {
				params.put("uid", SecurityConfig.getString("NetDiskUid"));
			}
			params.put("type", "doc");
			params.put("folder_name", "//");
			String scc = UploadUtil.startUploadService(params, path, uploadUrl);
			JSONObject jo = new JSONObject();
			JSONObject o = JSONObject.fromObject(scc);
			String docid = o.getString("docid");
			file.delete();
			jo.put("path", docid);
			jo.put("scc", o);
			jo.put("url", docid);
			jo.put("error", 0);
			
			ds.setRawData(jo);
		} catch (Exception e) {
			System.out.print("上传网盘失败！");
			e.printStackTrace();
		}

		return ds;
	}
	
	private void moveFile(File oldFile, String newPath) {
		FileInputStream inStream = null;
		FileOutputStream fs = null;
		try {
			if (oldFile.exists()) {
				// 文件存在时
				inStream = new FileInputStream(oldFile);
				File newFile = new File(newPath);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				int byteread = 0;
				byte[] buffer = new byte[1024];
				fs = new FileOutputStream(newFile);
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}

				oldFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	/**
	  * 将base64字符解码保存文件
	  * @param base64Code
	  * @param targetPath
	  * @throws Exception
	  */

	 public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
		 byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		 FileOutputStream out = new FileOutputStream(targetPath);
		 out.write(buffer);
		 out.close();
	 }
}