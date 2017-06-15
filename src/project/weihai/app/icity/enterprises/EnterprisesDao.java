package weihai.app.icity.enterprises;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * 企业设立 lhy
 * 
 * @author lenovo
 * 
 */
public class EnterprisesDao extends BaseJdbcDao {

	private EnterprisesDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static EnterprisesDao getInstance() {
		return DaoFactory.getDao(EnterprisesDao.class.getName());
	}

	/**
	 * 获取经营范围树（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getScopeTree(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("EAParallelUrl") + "/getScopeTree");
		JSONObject receive = new JSONObject();		     
		Map<String, String> data = new HashMap<String, String>();
		data.put("entName", (String) pSet.getParameter("entName"));
		data.put("appNum", (String) pSet.getParameter("appNum"));
		data.put("scopeName", (String) pSet.getParameter("scopeName"));
		try {
			receive = JSONObject.fromObject(RestUtil.postData(url,data));
			if ("200".equals(receive.get("state"))) {
				Object rawData = receive.getJSONArray("info");
				ds.setRawData(rawData);
			} else {
				ds.setState(StateType.FAILT);
				String error = (String) receive.get("error");
				ds.setMessage(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			String error = (String) receive.get("error");
			ds.setMessage(error);
		}
		return ds;
	}

	/**
	 * 根据经营范围获取前后置事项（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getItemByScope(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("EAParallelUrl") + "/getItemByScope");
		Map<String, String> data = new HashMap<String, String>();
		data.put("scopeCode", (String) pSet.getParameter("scopeCode"));
		data.put("regionCode", (String) pSet.getParameter("regionCode"));
		data.put("webRank", (String) pSet.getParameter("WebRank"));
		JSONObject receive = new JSONObject();
		try {
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(receive.get("state"))) {
				Object rawData = receive.getJSONArray("info");
				ds.setRawData(rawData);
			} else {
				ds.setState(StateType.FAILT);
				String error = (String) receive.get("error");
				ds.setMessage(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			String error = (String) receive.get("error");
			ds.setMessage(error);
		}
		return ds;
	}

	/**
	 * 获取企业基本信息（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getEnterpriseBaseInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("EAParallelUrl") + "/getEnterpriseBaseInfo");
		Map<String, String> data = new HashMap<String, String>();
		data.put("regionCode", (String) pSet.getParameter("regionCode"));
		data.put("entName", (String) pSet.getParameter("entName"));
		data.put("appNum", (String) pSet.getParameter("appNum"));		
		JSONObject receive = new JSONObject();
		try {
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(receive.get("state"))) {
				Object rawData = receive;
				ds.setRawData(rawData);
			} else {
				ds.setState(StateType.FAILT);
				String error = (String) receive.get("error");
				ds.setMessage(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			String error = (String) receive.get("error");
			ds.setMessage(error);
		}
		return ds;
	}

	/**
	 * 获取企业设立业务 *
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getEnterprisesProjectId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String id = (String) pSet.getParameter("id");
			String sql = "select t.* from ENTERPRISE_BUSINESS_INDEX t where t.ID = ?";
			ds = this.executeDataset(sql, new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 提交申报信息（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitApplicationInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("EAParallelUrl") + "/submitApplicationInfo");
		JSONObject submit_data = JSONObject.fromObject(pSet.get("submit_data"));
		String selectItem = submit_data.getString("selectItem");
		selectItem = selectItem.substring(0, selectItem.length() - 1);
		String scopeCode = submit_data.getString("scopeCode");
		scopeCode = scopeCode.substring(0, scopeCode.length() - 1);
		System.out.println(submit_data.toString());
		Map<String, String> data = new HashMap<String, String>();
		data.put("flowId", submit_data.getJSONObject("flow")
				.getString("flowId"));
		data.put("selectItem", selectItem);
		data.put("scopeCode", scopeCode);
		data.put("seqId", submit_data.getJSONObject("base").getString("seqId"));
		data.put("material", submit_data.getJSONArray("resource").toString());
		data.put("windows",submit_data.getJSONArray("windows").toString());
		JSONObject receive = new JSONObject();
		try {
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(receive.get("state"))) {
				pSet.put("state", "01");
				pSet.put("bizId", receive.getString("bizId"));
				Object rawData = receive;
				ds.setRawData(rawData);
			} else {
				pSet.put("state", "00");
				pSet.put("submit_data", submit_data);
				ds.setState(StateType.FAILT);
				String error = (String) receive.get("error");
				ds.setMessage(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			pSet.put("state", "00");
			ds.setState(StateType.FAILT);
			String error = (String) receive.get("error");
			ds.setMessage(error);
		} finally {
			saveEnterprises_business_index(pSet);
		}
		return ds;
	}

	/**
	 * 业务保存
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet saveEnterprises_business_index(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = "";
		JSONObject submit_data = JSONObject.fromObject(pSet.get("submit_data"));
		String id = submit_data.containsKey("id") ? (String) submit_data
				.get("id") : Tools.getUUID32();// id
		String state = pSet.containsKey("state") ? (String) pSet.get("state")
				: "00";// 0暂存1提交
		int i = 0; // 记录操作数据库受影响行数
		try {
			if (submit_data.containsKey("id")) {// 更新业务类型
				sql = "update ENTERPRISE_BUSINESS_INDEX t set t.RECEIVE_ID=?,t.CURRENT_STATE=?,t.APPLY_TIME=sysdate,t.APPLY_DATA=? where t.id=?";
				i = this.executeUpdate(
						sql,
						new Object[] {
								pSet.containsKey("bizId") ? (String) pSet
										.get("bizId") : "", state,
								submit_data.toString(), id });
			} else {// 插入
				sql = "insert into ENTERPRISE_BUSINESS_INDEX t (id,RECEIVE_ID,APPLY_SUBJECT,FORM_ID,DATA_ID,REGION_CODE,REGION_NAME,"
						+ "APPLY_TIME,APPLICANT,ENTERPRISE_TYPE,ORG_ACTUALITY,CURRENT_STATE,UCID,APPLY_DATA) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				i = this.executeUpdate(
						sql,
						new Object[] {
								id,
								pSet.containsKey("bizId") ? (String) pSet
										.get("bizId") : "",
								"关于"+submit_data.getJSONObject("base").getString(
										"orgName")+"的设立申请",
								"formId",
								"dataId",
								submit_data.getString("region_code"),
								submit_data.getString("region_name"),
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
								pSet.containsKey("uname") ? (String) pSet
										.get("uname") : "", "1", "Register",
								state, (String) pSet.get("uid"),
								submit_data.toString() });
			}
			if (i > 0) {
				if (pSet.containsKey("bizId")) {
					JSONArray checkitems = submit_data
							.getJSONArray("checkitems");
					int len = checkitems.size();
					String sql_item = "insert into ENTERPRISE_BUSINESS_COURSE t (ID,BIZ_ID,ITEM_ID,ITEM_CODE,ITEM_NAME,HANDLE_STATE) values (?,?,?,?,?,?)";
					for (int j = 0; j < len; j++) {
						String m_id = Tools.getUUID32();
						this.executeUpdate(sql_item, new Object[] { m_id, id,
								checkitems.getJSONObject(j).getString("ID"),
								checkitems.getJSONObject(j).getString("CODE"),
								checkitems.getJSONObject(j).getString("NAME"),
								"10" });
					}
				}
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("业务信息写入数据库失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		}
		return ds;
	}

	/**
	 * 业务中心查询企业设立列表
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getEnterprisesList(ParameterSet pSet) {
		DataSet ds;
		try {
			String sql = "select * from ENTERPRISE_BUSINESS_INDEX t where 1=1 ";
			// 处理查询条件
			List<Object> param = new ArrayList<Object>();
			String item_name = (String) pSet.get("ITEM_NAME");
			if (StringUtil.isNotEmpty(item_name)) {
				param.add("%" + item_name + "%");
				sql += " AND APPLY_SUBJECT like ?";
			}
			String SBSJ_s = (String) pSet.get("SUBMIT_TIME_S");
			String SBSJ_e = (String) pSet.get("SUBMIT_TIME_E");
			Timestamp d = null;
			if (StringUtil.isNotEmpty(SBSJ_s)) {
				d = CommonUtils_api.getInstance().parseStringToTimestamp(
						SBSJ_s, CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql += " AND APPLY_TIME >= ?";
			}
			if (StringUtil.isNotEmpty(SBSJ_e)) {
				d = CommonUtils_api.getInstance().parseStringToTimestamp(
						SBSJ_e, CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql += " AND APPLY_TIME <= ?";
			}
			String status = (String) pSet.get("STATE");
			if (StringUtil.isNotEmpty(status)) {
				param.add(status);
				sql += " AND CURRENT_STATE = ?";
			}			
			if (StringUtil.isNotEmpty((String) pSet.get("uid"))) {
				String ucid = (String) pSet.remove("uid");
				sql += " AND ucid = " + ucid;
			}
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql, param.toArray(),
						this.getDataSourceName());
			} else {
				ds = this.executeDataset(sql, start, limit, param.toArray(),
						this.getDataSourceName());
			}
			return ds;
		} catch (Exception e) {
			e.printStackTrace();
			return new DataSet();
		}
	}
	/**
	 * 获取单体事项
	 * @param pSet
	 * @return
	 */
	public DataSet queryItem(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			getBusinessState(pSet);
			String id = (String) pSet.getParameter("id");
			String sql = "select t.* from enterprise_business_course t where t.biz_ID = ?";
			ds = this.executeDataset(sql, new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}
	/**
	 * 查询办件状态 
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessState(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl") + "/getBusinessState");		
		Map<String, String> data = new HashMap<String, String>();
		String bizId = (String) pSet.getParameter("bizId");
		String id = (String) pSet.getParameter("id");
		data.put("bizId",bizId);
		JSONObject receive = new JSONObject();
		try {
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(receive.get("state"))) {
				String sql = "update ENTERPRISE_BUSINESS_INDEX t set t.CURRENT_STATE=? where t.id=?";
				int a = this.executeUpdate(
						sql,
						new Object[] {
								receive.get("status"), id });
				JSONArray info = receive.getJSONArray("info");
				int len = info.size();
				for(int i=0;i<len;i++){
					sql = "update ENTERPRISE_BUSINESS_COURSE t set t.HANDLE_STATE=?,t.ORG_NAME=? where t.BIZ_ID=? and t.ITEM_ID=?";
					int b = this.executeUpdate(
							sql,
							new Object[] {info.getJSONObject(i).getString("status"),
									info.getJSONObject(i).getString("ORG_NAME"),
									bizId,
									info.getJSONObject(i).getString("itemId")
							});
				}				
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
}
