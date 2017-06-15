package app.icity.govservice;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.icity.ServiceCmd;
import app.icity.sync.UploadUtil;
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
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class GovProjectDao extends BaseJdbcDao {

	private static Logger logger = LoggerFactory.getLogger(GovProjectDao.class);

	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
	}

	/**
	 * 获取事项
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getMattersList2(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getItemListByPage");
			String per = SecurityConfig.getString("subject.person.code");
			if (StringUtils.isEmpty(per)) {
				per = "0";
			}
			String ent = SecurityConfig.getString("subject.legalPerson.code");
			if (StringUtils.isEmpty(ent)) {
				ent = "1";
			}
			String bmfw = SecurityConfig.getString("subject.publicService.code");
			if (StringUtils.isEmpty(bmfw)) {
				bmfw = "4";
			}
			Map<String, String> data = new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("TYPE");
			String subType = (String) pSet.getParameter("SUB_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			if (!"xns".equals(SecurityConfig.getString("AppId"))) {
				whereValue.append(" and TYPE!=? ");
				paramValue.add("SP");	
			}
			if (!"zs".equals(SecurityConfig.getString("AppId"))) {
				if (!"ZJJG".equals(itemType) && !"".equals(itemType)
						&& itemType != null) {
					whereValue.append(" and TYPE!=? ");
					paramValue.add("ZJJG");
				}
			} else {
				whereValue.append(" and TYPE!=? ");
				paramValue.add("ZJJG");
			}
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%" + per + "%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and  service_object like ? ");
				paramValue.add("%" + ent + "%");
			} else if (("bmfw").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%" + bmfw + "%");
			}
			if (!"fz".equals(SecurityConfig.getString("AppId"))) {
				if ("approve".equals(pagemodel)) {
					whereValue.append(" and TYPE=? ");
					paramValue.add("XK");
				}
			}
			if ("dept".equals(cat)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);
			} else if ("theme".equals(cat)) {
				String[] themes = classtype.split(",");
				String sql = " and (";
				for (String o : themes) {
					sql += " title_name like ? or";
					paramValue.add("%" + o + "%");
				}
				sql = sql.substring(0, sql.length() - 2) + ") ";
				whereValue.append(sql);
			}
			if ("xz".equals(cat)) {
				whereValue.append(" and REGION_CODE= ? ");
				paramValue.add(region_code);
				pSet.put("region_code", region_code);
			} else {
				if ("".equals(region_code) || region_code == null) {
					pSet.put("region_code",
							SecurityConfig.getString("WebRegion"));
					whereValue.append(" and REGION_CODE= ? ");
					paramValue.add(SecurityConfig.getString("WebRegion"));
				} else {
					pSet.put("region_code", region_code);
					whereValue.append(" and REGION_CODE= ? ");
					paramValue.add(region_code);
				}
			}
			// 过滤设置为不进驻大厅部门下的事项
			DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
			JSONArray organs = dept.getJOData().getJSONArray("organ");
			if (organs.size() > 0) {
				JSONObject organ = null;
				int __count = 0;
				StringBuffer sqlBuff = new StringBuffer(" and org_code in ( ");
				String sql = "";
				for (int i = 0; i < organs.size(); i++) {
					organ = organs.getJSONObject(i);
					if ("1".equals(organ.get("IS_HALL"))) {
						__count++;
						sqlBuff.append("?,");
						paramValue.add(organ.get("CODE"));
					}
				}
				sql = sqlBuff.toString();
				if (__count > 0) {
					whereValue
							.append(sql.substring(0, sql.length() - 1) + ") ");
				}
			}

			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			if (!"".equals(itemType) && itemType != null) {
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			if (!"".equals(subType) && subType != null) {
				whereValue.append(" and SUB_TYPE= ?");
				paramValue.add(subType);
			}
			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			// data.put("orgCode", "37011400000003");
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());
			Object s = RestUtil.postData(url, data);
			JSONObject obj = new JSONObject();
			obj = JSONObject.fromObject(s);
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				Object columnObj = column.get("columns");
				net.sf.json.JSONObject json = JSONObject.fromObject(columnObj);
				if ("fz".equals(SecurityConfig.getString("AppId"))) {
					String item_id = JSONObject.fromObject(column.get("columns")).getString("ITEM_ID");
					String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+item_id);
					HttpClientUtil client = new HttpClientUtil();
					JSONObject form = JSONObject.fromObject(client.getResult(urlForm,""));
					
					if("200".equals(form.getString("state"))){
						JSONObject formSettingInfo = form.getJSONObject("info");
						String formid = (String)formSettingInfo.get("formId");
						String objectType = (String)formSettingInfo.get("objectType");
						
						if("".equals(formid) || "".equals(objectType)){
							json.put("ONLINE_FLAG", "0");
						}else{
							json.put("ONLINE_FLAG", "1");
						}
					}else{
						json.put("ONLINE_FLAG", "0");
					}
				}
				
				rows.add(json);
			}

			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("调事项报错："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 获取事项，添加便民的和非行政许可的也显示
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getMattersList3(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getItemListByPage");
			Map<String, String> data = new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("TYPE");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("ZJJG");
			/*
			 * whereValue.append(" and TYPE!=? "); paramValue.add("SP");
			 */
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			}
			if (("dept").equals(cat)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);
			} else if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			if (!"".equals(itemType) && itemType != null) {
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));

			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			// data.put("orgCode", "37011400000003");
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
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 获取事项，重庆市获取大项和普通项调用方式
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getmaxItemAndGeneralList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl")
					+ "/getmaxItemAndGeneralListByPage");
			Map<String, String> data = new HashMap<String, String>();
			String orgCode = (String) pSet.getParameter("org_code");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("itemTYPE");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String ciatype = (String) pSet.getParameter("ciatype");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			// 查询条件
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			if (!"".equals(itemType) && itemType != null) {
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue
						.append(" and (is_online=? OR EXISTS(SELECT p.id FROM PROJECT_ITEM p LEFT JOIN PROJECT_ITEM_EXT e ON p.ID=e.ITEM_ID WHERE e.PARENT_ITEM_ID=a.ID AND p.IS_ONLINE=?)) ");
				paramValue.add("1");
				paramValue.add("1");
			}
			// 处理重庆中央在渝部门问题
			if (StringUtil.isNotEmpty(ciatype)
					&& !StringUtil.isNotEmpty(orgCode)) {
				ParameterSet set = new ParameterSet();
				if ("1".equals(ciatype)) {
					whereValue.append(" and REGION_CODE= ? ");
					paramValue.add("500000");
					set.setParameter("WebRegion", "500000");
				} else if ("0".equals(ciatype)) {
					whereValue.append(" and REGION_CODE= ? ");
					paramValue.add(SecurityConfig.getString("WebRegion"));
					set.setParameter("WebRegion",
							SecurityConfig.getString("WebRegion"));
				}
				DataSet deptlist = ServiceCmd.getInstance().getDeptList(set);
				JSONArray depts = deptlist.getJOData().getJSONArray("organ");
				if (depts.size() > 0) {
					whereValue.append(" and org_code in( ");
					int i = 0;
					int len = depts.size();
					for (i = 0; i < len; i++) {
						String type_name = (String) JSONObject.fromObject(
								depts.get(i)).get("TYPE_NAME");
						String CODE = (String) JSONObject.fromObject(
								depts.get(i)).get("CODE");
						if ("1".equals(ciatype) && "中央在渝".equals(type_name)) {
							whereValue.append(" ?,");
							paramValue.add(CODE);
						} else if ("0".equals(ciatype)
								&& !"中央在渝".equals(type_name)) {
							whereValue.append(" ?,");
							paramValue.add(CODE);
						}
					}
					if (i > 0) {
						whereValue = new StringBuilder(whereValue.substring(0,
								whereValue.length() - 1));
					}
					whereValue.append(") ");
				}
			} else {
				whereValue.append(" and REGION_CODE= ? ");
				if (StringUtil.isNotEmpty(ciatype) && "1".equals(ciatype)) {
					paramValue.add("500000");
				} else {
					paramValue.add(SecurityConfig.getString("WebRegion"));
				}
			}
			// 结束
			data.put("orgCode", orgCode);
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
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 获取事项，重庆市根据大项id获取大项下的小项
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getminItemList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl")
					+ "/getminItemListByPage");
			Map<String, String> data = new HashMap<String, String>();
			String orgCode = (String) pSet.getParameter("org_code");
			String parentItemId = (String) pSet.getParameter("parentItemId");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("itemTYPE");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String ciatype = (String) pSet.getParameter("ciatype");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			// 按部门查询
			if (!"".equals(orgCode) && orgCode != null) {
				whereValue.append(" and org_code=? ");
				paramValue.add(orgCode);
			}
			// 查询条件
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			if (!"".equals(itemType) && itemType != null) {
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			// 处理重庆中央在渝部门问题
			if (StringUtil.isNotEmpty(ciatype)
					&& !StringUtil.isNotEmpty(orgCode)) {
				ParameterSet set = new ParameterSet();
				if ("1".equals(ciatype)) {
					whereValue.append(" and REGION_CODE= ? ");
					paramValue.add("500000");
					set.setParameter("WebRegion", "500000");
				} else if ("0".equals(ciatype)) {
					whereValue.append(" and REGION_CODE= ? ");
					paramValue.add(SecurityConfig.getString("WebRegion"));
					set.setParameter("WebRegion",
							SecurityConfig.getString("WebRegion"));
				}
				DataSet deptlist = ServiceCmd.getInstance().getDeptList(set);
				JSONArray depts = deptlist.getJOData().getJSONArray("organ");
				if (depts.size() > 0) {
					whereValue.append(" and org_code in( ");
					int i = 0;
					int len = depts.size();
					for (i = 0; i < len; i++) {
						String type_name = (String) JSONObject.fromObject(
								depts.get(i)).get("TYPE_NAME");
						String CODE = (String) JSONObject.fromObject(
								depts.get(i)).get("CODE");
						if ("1".equals(ciatype) && "中央在渝".equals(type_name)) {
							whereValue.append(" ?,");
							paramValue.add(CODE);
						} else if ("0".equals(ciatype)
								&& !"中央在渝".equals(type_name)) {
							whereValue.append(" ?,");
							paramValue.add(CODE);
						}
					}
					if (i > 0) {
						whereValue = new StringBuilder(whereValue.substring(0,
								whereValue.length() - 1));
					}
					whereValue.append(")");
				}
			} else {
				whereValue.append(" and REGION_CODE= ? ");
				if (StringUtil.isNotEmpty(ciatype) && "1".equals(ciatype)) {
					paramValue.add("500000");
				} else {
					paramValue.add(SecurityConfig.getString("WebRegion"));
				}
			}
			// 结束
			data.put("parentItemId", parentItemId);
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
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 根据事项ID分类获取事项相关信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getAllItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl")
					+ "/getAllItemInfoByItemID");
			String itemid = (String) pSet.getParameter("itemid");
			url += "?itemId=" + itemid;
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			String rows = obj.getString("material");
			if ("chq".equals(SecurityConfig.getString("AppId"))
					|| "weihai".equals(SecurityConfig.getString("AppId"))
					|| "manzhouli".equals(SecurityConfig.getString("AppId"))) {
				JSONObject docu = new JSONObject();
				String documents = obj.getString("document");
				docu.put("materials", rows);
				docu.put("documents", documents);
				ds.setRawData(docu.toString());
			} else {
				ds.setRawData(rows);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public String getCatalog(String classtype) {
		String sql = "select * from power_catalog t where id = '" + classtype
				+ "'";
		DataSet ds;
		ds = this.executeDataset(sql);
		// Object s = ds.getJOData().getString("NAME");
		return ds.getJOData().getString("NAME");
	}

	public DataSet showOnlineNum(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getItemListByPage");
			Map<String, String> data = new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String cat = (String) pSet.getParameter("cat");
			String classtype = (String) pSet.getParameter("ID");
			String deptid = (String) pSet.getParameter("deptid");
			String item_type = (String) pSet.getParameter("ITEM_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String folder_code = (String) pSet.getParameter("folder_code");
			String SearchName = (String) pSet.getParameter("SearchName");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String pagemodel = (String) pSet.getParameter("pagemodel");
			String theme = (String) pSet.getParameter("theme");
			String idList = (String) pSet.getParameter("ID_LIST");
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (StringUtils.isNotEmpty(SearchName)) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}

			if (StringUtils.isNotEmpty(region_code)) {
				whereValue.append(" and region_code=? ");
				paramValue.add(region_code);
			}
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			if (StringUtils.isNotEmpty(item_type)) {
				whereValue.append(" and type=? ");
				paramValue.add(item_type);
			}

			if (StringUtils.isNotEmpty(folder_code)) {
				whereValue.append(" and folder_code=? ");
				paramValue.add(folder_code);
			}
			if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			} else if (("bmfw").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%4%");
			} else if ("approve".equals(pagemodel)) {
				whereValue.append(" and TYPE= ?");
				paramValue.add("XK");
			}

			if (StringUtils.isNotEmpty(theme)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + theme + "%");
			}
			pSet.put("region_code", SecurityConfig.getString("WebRegion"));
			DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
			JSONArray organs = dept.getJOData().getJSONArray("organ");
			if (organs.size() > 0) {
				int __count = 0;
				JSONObject organ = null;
				StringBuffer sqlBuff = new StringBuffer(" and org_code in ( ");
				String sql = "";
				for (int i = 0; i < organs.size(); i++) {
					organ = organs.getJSONObject(i);
					if ("1".equals(organ.get("IS_HALL"))) {
						__count++;
						sqlBuff.append("?,");
						paramValue.add(organ.get("CODE"));
					}
				}
				sql = sqlBuff.toString();
				if (__count > 0) {
					whereValue
							.append(sql.substring(0, sql.length() - 1) + ") ");
				}
			}
			// 按照事项id列表查询，不会参数化拼接in查询条件。 by yenan
			if (StringUtils.isNotEmpty(idList)) {
				String[] idList_ = idList.split(",");
				String idwhere = "";
				StringBuffer idwhereBuff = new StringBuffer(" and id in (");
				for (String item : idList_) {
					idwhereBuff.append("?,");
					paramValue.add(item);
				}
				idwhere = idwhereBuff.toString();
				idwhere = idwhere.substring(0, idwhere.length() - 1) + ") ";
				whereValue.append(idwhere);
			}

			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());

			JSONObject obj = new JSONObject();
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column = new JSONObject();
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public DataSet getNoticeList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String deptid = (String) pSet.getParameter("deptid");
		try {
			if ("".equals(deptid)) {
				int limit = pSet.getPageLimit();
				int start = pSet.getPageStart();
				double count = limit + start;
				pSet.put("name", "通知公告");
				DataSet m_1_ds = getPoliciList(pSet);
				JSONArray m_1_ja = m_1_ds.getJAData();
				int total1 = getPoliciList(pSet).getTotal();
				int total2 = m_1_ds.getTotal();
				if (total1 < count) {
					if (count - total1 > limit) {
						int page = (int) Math.ceil((count - total1) / limit);
						pSet.put("page", page + "");
						JSONArray m_2_ja = getPowerList(pSet).getJAData();
						ds.setRawData(m_2_ja);
						ds.setTotal(total1 + total2);
					} else {
						ds.setRawData(m_1_ja);
						ds.setTotal(total1 + total2);
					}
				} else {
					ds.setRawData(m_1_ja);
					ds.setTotal(total1 + total2);
				}
			} else if ("pub".equals(deptid)) {
				pSet.put("name", "通知公告");
				ds = getPoliciList(pSet);
			} else {
				ds = getPowerList(pSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	public DataSet getPowerList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url ="";
			String flag = (String) pSet.getParameter("flag");
			if(StringUtils.isNotEmpty(flag)){
				url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl_gg") + "/getItemListByPage");
			}else{
				url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			}
			String per = SecurityConfig.getString("subject.person.code");
			if (StringUtils.isEmpty(per)) {
				per = "0";
			}
			String ent = SecurityConfig.getString("subject.legalPerson.code");
			if (StringUtils.isEmpty(ent)) {
				ent = "1";
			}
			String bmfw = SecurityConfig
					.getString("subject.publicService.code");
			if (StringUtils.isEmpty(bmfw)) {
				bmfw = "4";
			}
			Map<String, String> data = new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String cat = (String) pSet.getParameter("cat");
			String classtype = (String) pSet.getParameter("ID");
			String deptid = (String) pSet.getParameter("deptid");
			String item_type = (String) pSet.getParameter("ITEM_TYPE");
			String bm_type = (String) pSet.getParameter("BM_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String folder_code = (String) pSet.getParameter("folder_code");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String pagemodel = (String) pSet.getParameter("pagemodel");
			String theme = (String) pSet.getParameter("theme");
			String idList = (String) pSet.getParameter("ID_LIST");
			String page_mode = SecurityConfig.getString("PAGE_MODE");
			String qllist = (String) pSet.getParameter("qllist"); 
			String code = (String) pSet.getParameter("code"); 
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			String in_type = (String) pSet.getParameter("in_type");
			if (!"".equals(in_type) && in_type != null) {
				String[] m_types = in_type.split(",");
				int m_types_len = m_types.length;
				if (m_types_len > 0) {
					String type_str = "and TYPE in (";
					for (int j = 0; j < m_types_len; j++) {
						type_str += "?,";
						paramValue.add(m_types[j]);
					}
					type_str = type_str.substring(0, type_str.length()-1);
					type_str+=") ";
					whereValue.append(type_str);
				}
			}
			String out_type = (String) pSet.getParameter("out_type");
			if (!"".equals(out_type) && out_type != null) {
				String[] out_types = out_type.split(",");
				int out_types_len = out_types.length;
				for (int j = 0; j < out_types_len; j++) {
					whereValue.append(" and TYPE!= ?");
					paramValue.add(out_types[j]);
				}
			}
			//烟台公共服务事项
			String org_decide_email = (String) pSet.getParameter("org_decide_email");
			if (StringUtils.isNotEmpty(org_decide_email)) {
				whereValue.append(" and e.ORG_DECIDE_EMAIL=? ");
				paramValue.add(org_decide_email);
			}
			
			if (StringUtils.isNotEmpty(SearchName)) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}

			if (StringUtils.isNotEmpty(bm_type)) {
				whereValue.append(" and type=? ");
				paramValue.add("BM");
				whereValue.append(" and sub_type=? ");
				paramValue.add(bm_type);
			}

			if (!"".equals(region_code) && region_code != null) {
				whereValue.append(" and REGION_CODE= ? ");
				paramValue.add(region_code);
				pSet.put("region_code", region_code);
			}else{
				whereValue.append(" and REGION_CODE= ? ");
				paramValue.add(SecurityConfig.getString("WebRegion"));
				pSet.put("region_code", SecurityConfig.getString("WebRegion"));
			}
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			String agent_code = (String) pSet.getParameter("agent_code"); //辽阳-科室
			if (StringUtils.isNotEmpty(agent_code)) {
				whereValue.append(" and agent_code=? ");
				paramValue.add(agent_code);
			}
			if ("shangqiu".equals(SecurityConfig.getString("AppId"))) {
				whereValue.append(" and type !=? ");
				paramValue.add("ZJJG");
			}
			//烟台权力清单页面过滤掉权力类型为中介机构的事项
			if ("qllist".equals(qllist)) {
				whereValue.append(" and type !=? ");
				paramValue.add("ZJJG");
			}
			if (StringUtils.isNotEmpty(item_type)) {
				if (item_type.contains(",")) {
					String[] para = item_type.split(",");
					whereValue.append(" and type in (?,?)");
					paramValue.add(para[0]);
					paramValue.add(para[1]);
				} else {
					whereValue.append(" and type = ? ");
					paramValue.add(item_type);
				}
			}
			if (StringUtils.isNotEmpty(folder_code)) {
				whereValue.append(" and folder_code=? ");
				paramValue.add(folder_code);
			}
			if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%" + per + "%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and  service_object like ? ");
				paramValue.add("%" + ent + "%");
			} else if (("bmfw").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%" + bmfw + "%");
			}

			if (StringUtils.isNotEmpty(theme)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + theme + "%");
			}
			
			if (StringUtils.isNotEmpty(code)) {
				whereValue.append(" and code = ? ");
				paramValue.add(code);
			}

			DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
			JSONArray organs = dept.getJOData().getJSONArray("organ");
			if (organs.size() > 0) {
				int __count = 0;
				JSONObject organ = null;
				StringBuffer sqlBuff = new StringBuffer(" and org_code in ( ");
				String sql = "";
				for (int i = 0; i < organs.size(); i++) {
					organ = organs.getJSONObject(i);
					if ("yantai".equals(page_mode)) { // 区分烟台政务服务中心和政务服务网站
						if ("1".equals(organ.get("IS_CENTER"))) {
							__count++;
							sqlBuff.append("?,");
							paramValue.add(organ.get("CODE"));
						}
					} else {
						if ("1".equals(organ.get("IS_HALL"))) {
							__count++;
							sqlBuff.append("?,");
							paramValue.add(organ.get("CODE"));
						}
					}
				}
				sql = sqlBuff.toString();
				if (__count > 0) {
					whereValue
							.append(sql.substring(0, sql.length() - 1) + ") ");
				}
			}
			// 按照事项id列表查询，不会参数化拼接in查询条件。 by yenan
			if (StringUtils.isNotEmpty(idList)) {
				String[] idList_ = idList.split(",");
				StringBuffer idwhereBuff = new StringBuffer(" and id in (");
				String idwhere = "";
				for (String item : idList_) {
					idwhereBuff.append("?,");
					paramValue.add(item);
				}
				idwhere = idwhereBuff.toString();
				idwhere = idwhere.substring(0, idwhere.length() - 1) + ") ";
				whereValue.append(idwhere);
			}
			if (StringUtils.isNotEmpty((String) pSet.getParameter("startTime"))) {
				data.put("startTime", pSet.getParameter("startTime").toString());
			}
			if (StringUtils.isNotEmpty((String) pSet.getParameter("endTime"))) {
				data.put("endTime", pSet.getParameter("endTime").toString());
			}
			data.put("page", pSet.getParameter("page").toString());
			data.put("rows", pSet.getParameter("limit").toString());
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());
			// 目录下面的事项进行排序
			String orderBy = (String) pSet.getParameter("orderBy");
			if (!"".equals(orderBy) && orderBy != null) {
				data.put("orderBy", orderBy);
			} else {
				data.put("orderBy", "order by type desc,last_time desc");
			}		
			JSONObject obj = new JSONObject();
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column = new JSONObject();
				column = (JSONObject) pageList.get(i);
				Object columnObj = column.get("columns");
				net.sf.json.JSONObject json = JSONObject.fromObject(columnObj);
				if ("fz".equals(SecurityConfig.getString("AppId"))) {
					String item_id = JSONObject.fromObject(column.get("columns")).getString("ITEM_ID");
					String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+item_id);
					HttpClientUtil client = new HttpClientUtil();
					JSONObject form = JSONObject.fromObject(client.getResult(urlForm,""));
					
					if("200".equals(form.getString("state"))){
						JSONObject formSettingInfo = form.getJSONObject("info");
						String formid = (String)formSettingInfo.get("formId");
						String objectType = (String)formSettingInfo.get("objectType");
						
						if("".equals(formid) || "".equals(objectType)){
							json.put("ONLINE_FLAG", "0");
						}else{
							json.put("ONLINE_FLAG", "1");
						}
					}else{
						json.put("ONLINE_FLAG", "0");
					}
				}
				
				rows.add(json);
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
			ds = getStarLevel(ds, pageList.size());
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public DataSet getStarLevel(DataSet pSet, int j) {
		DataSet ds = pSet;
		for (int i = 0; i < j; i++) {
			try {
				String code = (String) ds.getRecord(i).get("CODE");
				String sql = "select STAR_LEVEL from star_level_stat s where s.SERVICE_CODE = ? ";
				DataSet star = this.executeDataset(sql, new Object[] { code });
				String starlevel = "0";
				if (star.getState() == StateType.SUCCESS && star.getTotal() > 0) {
					starlevel = star.getRecord(0).get("STAR_LEVEL").toString();
				}
				ds.getRecord(i).put("STAR_LEVEL", starlevel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ds;
	}

	public DataSet onQueryProgress(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String deptid = (String) pSet.getParameter("deptid");
			String NUM = (String) pSet.getParameter("NUM");
			int rownum = Integer.parseInt(NUM);
			String sql = "select rownum,t.* from business_index t where t.sjdwdm = ? and rownum<?";
			ds = this.executeDataset(sql, new Object[] { deptid, rownum });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	// 舟山通知公告等小模块
	public DataSet getContentInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		String picModel = (String) pSet.get("picModel");
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");// 舟山项目，是否显示市级数据
			
			String str = " and t.rid = '"
					+ rid
					+ "'"					
					+ " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			
			String deptid = (String)pSet.get("deptid");
			if(StringUtils.isNotEmpty(deptid)){
				str += " and t.dept_id='"
				+ deptid
				+ "'";
			}
			if ("1".equals(open)) {
				str = "and (t.rid = '"
						+ rid
						+ "'"
						+ " or t.rid like '330901000000%') and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid
						+ "' or s.rid like '330901000000%') and s.name = '"
						+ name + "')";
			}
			if ("1".equals(picModel)) {
				str += " and attach.docid !=' ' and attach.docid!='null' ";
			}

			String sql = "select ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,attach.DOCID,attach.NAME PICNAME,t.POWER_TYPE from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by ctime desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		try {
			if (StringUtils.isNotEmpty(picModel)) {
				// 这里加入下载代码
				if (ds.getTotal() > 0) {
					for (int j = 0; j < ds.getJAData().size(); j++) {
						String id = ds.getJAData().getJSONObject(j)
								.getString("ID");
						String sql = "select * from attach t where type='1' and  conid = '"
								+ id + "'";
						dsAttach = this.executeDataset(sql);
						int totla = dsAttach.getTotal();
						if (totla > 0) {
							// 这里加入下载代码
							JSONArray upfile = dsAttach.getJAData();
							for (int i = 0; i < upfile.size(); i++) {
								// ---下载文件到项目路径本地 start
								String fileName = (String) ((JSONObject) upfile
										.get(i)).get("NAME");
								String fileType = (String) ((JSONObject) upfile
										.get(i)).get("TYPE");
								String doc_id = (String) ((JSONObject) upfile
										.get(i)).get("DOCID");
								UploadUtil.downloadFile(fileName, doc_id,
										fileType);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 烟台常见问题等小模块
	public DataSet getQuestionInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		String picModel = (String) pSet.get("picModel");
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String startTime = (String) pSet.get("startTime");
			String endTime = (String) pSet.get("endTime");
			String searchName = (String) pSet.get("SearchName");
			String str = "and t.rid = '"
					+ rid
					+ "'"
					+ " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";

			if ("1".equals(picModel)) {
				str += " and attach.docid !=' ' and attach.docid!='null' ";
			}
			if (StringUtils.isNotEmpty(startTime)) {
				str += "and t.CTIME>to_date(" + startTime
						+ ",'yyyy-MM-dd HH:mm:ss') ";
			}
			if (StringUtils.isNotEmpty(endTime)) {
				str += "and t.CTIME<to_date(" + endTime
						+ ",'yyyy-MM-dd HH:mm:ss') ";
			}
			if (StringUtils.isNotEmpty(searchName)) {
				str += "and t.NAME like '%" + searchName + "%' ";
			}
			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,attach.DOCID,attach.NAME PICNAME,t.POWER_TYPE from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by ctime desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		try {
			if (StringUtils.isNotEmpty(picModel)) {
				// 这里加入下载代码
				if (ds.getTotal() > 0) {
					for (int j = 0; j < ds.getTotal(); j++) {
						String id = ds.getJAData().getJSONObject(j)
								.getString("ID");
						String sql = "select * from attach t where type='1' and  conid = '"
								+ id + "'";
						dsAttach = this.executeDataset(sql);
						int totla = dsAttach.getTotal();
						if (totla > 0) {
							// 这里加入下载代码
							JSONArray upfile = dsAttach.getJAData();
							for (int i = 0; i < upfile.size(); i++) {
								// ---下载文件到项目路径本地 start
								String fileName = (String) ((JSONObject) upfile
										.get(i)).get("NAME");
								String fileType = (String) ((JSONObject) upfile
										.get(i)).get("TYPE");
								String doc_id = (String) ((JSONObject) upfile
										.get(i)).get("DOCID");
								UploadUtil.downloadFile(fileName, doc_id,
										fileType);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getContentDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		JSONArray upfileAttach = new JSONArray();
		String id = (String) pSet.getParameter("id");
		try {
			String sql = "select t.*,t.ctime as CT from pub_content t where id = '"
					+ id + "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			// _log.info("getContentDetail："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		try {
			String sql = "select * from attach t where (type in ('1','2','3')) and  conid = '"
					+ id + "'";
			dsAttach = this.executeQuery(sql);
			int totla = dsAttach.getTotal();
			if (totla > 0) {
				// 这里加入下载代码
				JSONArray upfile = dsAttach.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i))
							.get("NAME");
					String fileType = (String) ((JSONObject) upfile.get(i))
							.get("TYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i))
							.get("DOCID");
					if ("1".equals(fileType) || "2".equals(fileType)) {
						upfileAttach.add(upfile.get(i));
					}
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}

			JSONObject rawData = ds.getJOData();
			rawData.element("upfiles", upfileAttach);
			ds.setRawData(rawData);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getContentDetailByName(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		JSONArray upfileAttach = new JSONArray();
		String name = (String) pSet.getParameter("name");
		try {
			String sql = "select t.*,to_char( t.ctime ,'YYYY-MM-DD') CT from pub_content t,pub_channel p where t.cid=p.id and p.name= '"
					+ name + "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			// _log.info("getContentDetail："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		if (ds.getTotal() > 0) {

			try {

				String id = ds.getJOData().getString("ID");

				String sql = "select * from attach t where (type='2' or type ='3') and  conid = '"
						+ id + "'";
				dsAttach = this.executeQuery(sql);
				int totla = dsAttach.getTotal();
				if (totla > 0) {
					// 这里加入下载代码
					JSONArray upfile = dsAttach.getJAData();
					for (int i = 0; i < upfile.size(); i++) {
						// ---下载文件到项目路径本地 start
						String fileName = (String) ((JSONObject) upfile.get(i))
								.get("NAME");
						String fileType = (String) ((JSONObject) upfile.get(i))
								.get("TYPE");
						String doc_id = (String) ((JSONObject) upfile.get(i))
								.get("DOCID");
						if (fileType.equals("2")) {
							upfileAttach.add(upfile.get(i));
						}
						UploadUtil.downloadFile(fileName, doc_id, fileType);
					}
				}

				JSONObject rawData = ds.getJOData();
				rawData.element("upfiles", upfileAttach);
				ds.setRawData(rawData);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ds;
	}

	public DataSet getChannel(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String pname = (String) pSet.getParameter("pname");
			String rid = (String) pSet.getParameter("rid");
			String sql = "select * from pub_channel where parent=(select id from pub_channel where NAME = '"
					+ pname
					+ "' and rid = '"
					+ rid
					+ "' and type='0') order by corder";
			ds = this.executeDataset(sql);

		} catch (Exception e) {
			// _log.info("getChannel："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	//
	public DataSet getList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.agent_code,content.agent_name,content.creator_id,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder
					.append(" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.TYPE as pictype,attach.DOCID,attach.CONID ");
			sqlBuilder
					.append(" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1' and content.submit_status = '1' ");
			String picModel = (String) pSet.get("PicModel");

			if (StringUtils.isNotEmpty(picModel)) {
				pSet.remove("PicModel");
				sqlBuilder
						.append(" and attach.docid !=' ' and attach.docid!='null' ");
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String localtime = df.format(new Date());
			String tzgg = (String) pSet.getParameter("channel.name@=");
			if ("通知公告".equals(tzgg))
				sqlBuilder.append(" and content.VALID_DATE_END>=  to_date('"
						+ localtime + "','yyyy-mm-dd hh24:mi:ss') ");
			String rid = (String) pSet.remove("content.rid@=");
			// 如果是工作时间栏目，只取本级的数据
			String workTime = (String) pSet.remove("workTime");
			String isDtyw = (String) pSet.get("isDtyw");
			pSet.remove("isDtyw");
			// if("1".equals(isDtyw)){
			// sqlBuilder.append(" and (content.rid ='"+rid+"'");
			// sqlBuilder.append(" or content.rid ='330901000000')
			// ");//市级内容也在县区显示
			// }else{
			// sqlBuilder.append(" and (content.rid ='"+rid+"') ");
			// }

			if (!"1".equals(workTime)) {
				if ("1".equals(isDtyw)) {
					if ("330901000000".equals(rid)) {
						sqlBuilder
								.append(" and (content.rid ='330901000000') ");
					} else {
						sqlBuilder.append(" and (content.rid ='" + rid + "'");
						sqlBuilder.append(" or content.rid !='330901000000') ");
					}
				} else {
					sqlBuilder.append(" and (content.rid ='" + rid + "'");
					sqlBuilder.append(" or content.rid ='330901000000') ");// 市级内容也在县区显示
				}
			} else {
				sqlBuilder.append(" and (content.rid ='" + rid + "') ");
			}
			String sql = SqlCreator.getSimpleQuerySql(pSet,
					sqlBuilder.toString(), this.getDataSource());
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
			if (StringUtils.isNotEmpty(picModel)) {
				// 这里加入下载代码
				JSONArray upfile = ds.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i))
							.get("PICNAME");
					String fileType = (String) ((JSONObject) upfile.get(i))
							.get("PICTYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i))
							.get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getListNew(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.agent_code,content.agent_name,content.creator_id,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder
					.append(" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.TYPE as pictype,attach.DOCID,attach.CONID ");
			sqlBuilder
					.append(" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1' and content.submit_status = '1' ");
			String picModel = (String) pSet.get("PicModel");
			if (StringUtils.isNotEmpty(picModel)) {
				pSet.remove("PicModel");
				sqlBuilder
						.append(" and attach.docid !=' ' and attach.docid!='null' ");
			}
			String rid = (String) pSet.remove("content.rid@=");
			// 如果是工作时间栏目，只取本级的数据
			String workTime = (String) pSet.remove("workTime");
			String isDtyw = (String) pSet.get("isDtyw");
			pSet.remove("isDtyw");
			if (!"1".equals(workTime)) {

				if ("1".equals(isDtyw)) {
					if ("330901000000".equals(rid)) {
						sqlBuilder.append(" and (content.rid ='330901000000') ");
					} else {
						sqlBuilder.append(" and (content.rid ='" + rid + "'");
						sqlBuilder.append(" or content.rid !='330901000000') ");
					}
				} else {
					sqlBuilder.append(" and (content.rid ='" + rid + "'");
					sqlBuilder.append(" or content.rid ='330901000000') ");// 市级内容也在县区显示
				}
			} else {
				sqlBuilder.append(" and (content.rid !='" + rid + "') ");
			}

			String sql = SqlCreator.getSimpleQuerySql(pSet,
					sqlBuilder.toString(), this.getDataSource());

			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
			if (StringUtils.isNotEmpty(picModel)) {
				// 这里加入下载代码
				JSONArray upfile = ds.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i))
							.get("PICNAME");
					String fileType = (String) ((JSONObject) upfile.get(i))
							.get("PICTYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i))
							.get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet MatterInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			Map<String, String> map = new HashMap<String, String>();
			String RECEIVE_NUMBER = (String) pSet
					.getParameter("RECEIVE_NUMBER");

			String page = pSet.getPageStart() + "";
			String rows = pSet.getPageLimit() + "";

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			// pSet.getParameter("limit");
			whereValue.append(" and receive_number=?");
			paramValue.add(RECEIVE_NUMBER);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());

			map.put("page", page);
			map.put("rows", rows);

			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}

		return ds;
	}

	public DataSet SelectByDept(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			Map<String, String> map = new HashMap<String, String>();
			String ORG_CODE = (String) pSet.getParameter("ORG_CODE");
			String TYPE = (String) pSet.getParameter("TYPE");

			String page = pSet.getPageStart() + "";
			String rows = pSet.getPageLimit() + "";

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			// pSet.getParameter("limit");
			whereValue.append(" and org_code=?");
			paramValue.add(ORG_CODE);
			if (StringUtils.isNotEmpty(TYPE)) {
				whereValue.append(" and type=?");
				paramValue.add(TYPE);
			}
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());

			map.put("page", page);
			map.put("rows", rows);

			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}

		return ds;
	}

	public DataSet BusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String region = (String) pSet.getParameter("region_code");
			if (!StringUtils.isNotEmpty(region)) {
				region = SecurityConfig.getString("WebRegion");
			}
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			
			if("weihai".equals( SecurityConfig.getString("AppId"))){
				url = HttpUtil.formatUrl(SecurityConfig
						.getString("approval_url") + "/newgetDisplayListByPage");
			}
			
			Map<String, String> map = new HashMap<String, String>();

			String page = pSet.getPageStart() + "";
			String rows = pSet.getPageLimit() + "";

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			String keyword = (String) pSet.remove("keyword");
			if (StringUtils.isNotEmpty(keyword)) {
				whereValue
						.append(" and ( ORG_NAME like ? or APPLY_SUBJECT like ? or APPLICANT like ?)");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
			}
			//献县 需要查询所有数据，不需要区划条件 by：yanhao
			String pageMode = SecurityConfig.getString("PAGE_MODE");
			if(!"xianxian".equals(pageMode)){
				whereValue.append(" and region_code=?");
				paramValue.add(region);
			}
			String ORG_CODE = (String) pSet.getParameter("org_code");
			if (StringUtils.isNotEmpty(ORG_CODE)) {
				whereValue.append(" and org_code=?");
				paramValue.add(ORG_CODE);
			}

			String item_id = (String) pSet.getParameter("item_id");
			if (StringUtils.isNotEmpty(item_id)) {
				whereValue.append(" and item_id=?");
				paramValue.add(item_id);
			}

			String APPLICANT = (String) pSet.getParameter("APPLICANT");
			if (StringUtils.isNotEmpty(APPLICANT)) {
				whereValue.append(" and APPLICANT like ?");
				paramValue.add("%" + APPLICANT + "%");
			}

			String APPLY_SUBJECT = (String) pSet.getParameter("APPLY_SUBJECT");
			if (StringUtils.isNotEmpty(APPLY_SUBJECT)) {
				whereValue.append(" and APPLY_SUBJECT like ?");
				paramValue.add("%" + APPLY_SUBJECT + "%");
			}

			String startTime = (String) pSet.getParameter("startTime");
			if (StringUtils.isNotEmpty(startTime)) {
				map.put("startTime", startTime);
			}
			String endTime = (String) pSet.getParameter("endTime");
			if (StringUtils.isNotEmpty(endTime)) {
				map.put("endTime", endTime);
			}
			String power = (String) pSet.getParameter("power");
			if (StringUtils.isNotEmpty(power)) {
				whereValue.append(" and type = ?");
				paramValue.add(power);
			}
			String state = (String) pSet.getParameter("state");
			if (StringUtils.isNotEmpty(state)) {
				if("办结".equals(state)){
					whereValue.append(" and (state=? or state=? or state=?)");
					paramValue.add("98");
					paramValue.add("99");
					paramValue.add("90"); //添加state为90办结并出证状态
				}else{
					whereValue.append(" and (state!=? and state!=? and state!=?)");
					paramValue.add("98");
					paramValue.add("99");
					paramValue.add("90"); //添加state为90办结并出证状态
				}				
			}
			String result_mark = (String) pSet.getParameter("result_mark");
			if ("lc".equals(result_mark)) {
				whereValue.append(" and (state=? or state=? or state=?)");
				paramValue.add("98");
				paramValue.add("99");
				paramValue.add("90"); //添加state为90办结并出证状态
			}
			
			//聊城项目个性化需求
			String lc_mark = (String) pSet.getParameter("lc_mark");
			if ("liaocheng".equals(lc_mark)) {
				//聊城项目，人防办的事项：结合民用建筑修建防空地下室审批 屏蔽掉
				whereValue.append(" and item.item_code <> ? ");
				paramValue.add("371500-rfb-XK-001-01");
				whereValue.append(" and receive_number <> ? ");
				paramValue.add("151001129769");
			}
			
			String item_code = (String) pSet.getParameter("item_code");
			if (StringUtils.isNotEmpty(item_code)) {
				whereValue.append(" and item.item_code = ?");
				paramValue.add(item_code);
			}
			// 过滤掉申请人为空的情况在外网不进行展示
			//重庆不作过滤处理
			if(!"chq".equals( SecurityConfig.getString("AppId"))){
				whereValue.append(" and APPLICANT is not null ");
			}
			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			String order_mark = (String) pSet.getParameter("order_mark");
			if ("de".equals(order_mark)) {
				map.put("orderby", "order by finish_time desc");
				// map.put("order", "order by type desc,last_time desc");
			}
			if ("as".equals(order_mark)) {
				map.put("orderby", "order by finish_time asc");
			}
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	public DataSet BusinessSearchQueryPower(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String region = (String) pSet.getParameter("region_code");
			if (!StringUtils.isNotEmpty(region)) {
				region = SecurityConfig.getString("WebRegion");
			}
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("PowerOperation_url") + "/getDisplayListByPage");
			Map<String, String> map = new HashMap<String, String>();

			String page = pSet.getPageStart() + "";
			String rows = pSet.getPageLimit() + "";

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			String keyword = (String) pSet.remove("keyword");
			if (StringUtils.isNotEmpty(keyword)) {
				whereValue
						.append(" and region_code=? and ( ORG_NAME like ? or APPLY_SUBJECT like ? or APPLICANT like ?)");
				paramValue.add(region);
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
			} else {
				whereValue.append(" and region_code=?");
				paramValue.add(region);
			}
			String ORG_CODE = (String) pSet.getParameter("org_code");
			if (StringUtils.isNotEmpty(ORG_CODE)) {
				whereValue.append(" and org_code=?");
				paramValue.add(ORG_CODE);
			}

			String item_id = (String) pSet.getParameter("item_id");
			if (StringUtils.isNotEmpty(item_id)) {
				whereValue.append(" and item_id=?");
				paramValue.add(item_id);
			}

			String APPLICANT = (String) pSet.getParameter("APPLICANT");
			if (StringUtils.isNotEmpty(APPLICANT)) {
				whereValue.append(" and APPLICANT like ?");
				paramValue.add("%" + APPLICANT + "%");
			}

			String APPLY_SUBJECT = (String) pSet.getParameter("APPLY_SUBJECT");
			if (StringUtils.isNotEmpty(APPLY_SUBJECT)) {
				whereValue.append(" and APPLY_SUBJECT like ?");
				paramValue.add("%" + APPLY_SUBJECT + "%");
			}

			String startTime = (String) pSet.getParameter("startTime");
			if (StringUtils.isNotEmpty(startTime)) {
				map.put("startTime", startTime);
			}
			String endTime = (String) pSet.getParameter("endTime");
			if (StringUtils.isNotEmpty(endTime)) {
				map.put("endTime", endTime);
			}
			String power = (String) pSet.getParameter("power");
			if (StringUtils.isNotEmpty(power)) {
				whereValue.append(" and type = ?");
				paramValue.add(power);
			}
			String result_mark = (String) pSet.getParameter("result_mark");
			if ("lc".equals(result_mark)) {
				whereValue.append(" and (state=? or state=?)");
				paramValue.add("98");
				paramValue.add("99");
			}
			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			String order_mark = (String) pSet.getParameter("order_mark");
			if ("de".equals(order_mark)) {
				map.put("orderby", "order by finish_time desc");
				// map.put("order", "order by type desc,last_time desc");
			}
			if ("as".equals(order_mark)) {
				map.put("orderby", "order by finish_time asc");
			}
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}

		return ds;
	}

	public DataSet search(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder
					.append(" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.DOCID,attach.CONID ");
			sqlBuilder
					.append(" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1'");

			String name = (String) pSet.remove("name");
			String rid = (String) pSet.remove("rid");
			sqlBuilder.append(" and content.name like '" + "%" + name + "%"
					+ "'");
			sqlBuilder.append(" and channel.rid = '" + rid + "'");
			sqlBuilder
					.append(" and (channel.name = '动态要闻' or channel.name = '改革创新' or channel.name = '专题专栏' or channel.name = '通知' or channel.name = '公告' or channel.name = '公示' or channel.name = '简报' or channel.name = '通报' or channel.name = '调查研究' or channel.name = '数据分析' or channel.name = '工作交流' or channel.name = '外地经验' or channel.name = '业务处室动态信息' or channel.name = '窗口动态')");

			String sql = SqlCreator.getSimpleQuerySql(pSet,
					sqlBuilder.toString(), this.getDataSource());

			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getChannelName(ParameterSet pSet) {
		String rid = SecurityConfig.getString("WebRegion");
		String name = (String) pSet.getParameter("cname");
		DataSet ds = new DataSet();
		try {
			String sql = "select id, name from PUB_CHANNEL where rid='" + rid
					+ "' and name = '" + name + "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;

	}

	/**
	 * 分页获取目录信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getFolderInfoByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String flag = (String) pSet.getParameter("flag");
			String url ="";
			if(StringUtils.isNotEmpty(flag)){
				 url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl_gg") + "/getFolderListByPage");	
			}else{
				 url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getFolderListByPage");
			}
			Map<String, String> data = new HashMap<String, String>();
			String page = "1", rows = "6";
			if (pSet.getParameter("page") != null)
				page = pSet.getParameter("page").toString();
			if (pSet.getParameter("rows") != null)
				rows = pSet.getParameter("rows").toString();
			String org_code = (String) pSet.getParameter("org_code");
			String SUIT_ONLINE = (String) pSet.getParameter("IS_ONLINE");
			String region_code = (String) pSet.getParameter("region_code");

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			/*String has_child = (String) pSet.getParameter("has_child");
			if(StringUtils.isNotEmpty(has_child)){
				whereValue.append(" and HAS_CHILD=? ");
				paramValue.add(has_child);
			}*/
			if (!"".equals(region_code) && region_code != null) {
				whereValue.append(" and REGION_CODE= ? ");
				paramValue.add(region_code);
				pSet.put("region_code", region_code);
			} else {
				whereValue.append(" and REGION_CODE= ? ");
				paramValue.add(SecurityConfig.getString("WebRegion"));
				pSet.put("region_code", SecurityConfig.getString("WebRegion"));
			}
			String SearchName = (String) pSet.getParameter("SearchName");
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and NAME like ?");
				paramValue.add("%"+SearchName+"%");
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and IS_ONLINE=? ");
				paramValue.add("1");
			}
			if (StringUtils.isNotEmpty(org_code)) {
				whereValue.append(" and ORG_CODE=? ");
				paramValue.add(org_code);
			}
			String type = (String) pSet.getParameter("type");
			if (StringUtils.isNotEmpty(type)) {
				whereValue.append(" and TYPE=? ");
				paramValue.add(type);
			}
			// 威海办事服务展示事项过滤
			if ("weihai".equals(SecurityConfig.getString("AppId"))) {
				whereValue.append("and TYPE in (?,?,?,?,?)");
				paramValue.add("XK");
				paramValue.add("BM");
				paramValue.add("QT");
				paramValue.add("QR");
				paramValue.add("SHZB");
			}			
			String in_type = (String) pSet.getParameter("in_type");
			if (!"".equals(in_type) && in_type != null) {
				String[] m_types = in_type.split(",");
				int m_types_len = m_types.length;
				if (m_types_len > 0) {
					String type_str = "and TYPE in (";
					for (int j = 0; j < m_types_len; j++) {
						type_str += "?,";
						paramValue.add(m_types[j]);
					}
					type_str = type_str.substring(0, type_str.length()-1);
					type_str+=") ";
					whereValue.append(type_str);
				}
			}
			String out_type = (String) pSet.getParameter("out_type");
			if (!"".equals(out_type) && out_type != null) {
				String[] out_types = out_type.split(",");
				int out_types_len = out_types.length;
				for (int j = 0; j < out_types_len; j++) {
					whereValue.append(" and TYPE!= ?");
					paramValue.add(out_types[j]);
				}
			}
			// 过滤设置为不进驻大厅部门下的事项
			DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
			JSONArray organs = dept.getJOData().getJSONArray("organ");
			if (organs.size() > 0) {
				JSONObject organ = null;
				int __count = 0;
				StringBuffer sqlBuff = new StringBuffer(" and org_code in ( ");
				String sql = "";
				for (int i = 0; i < organs.size(); i++) {
					organ = organs.getJSONObject(i);
					if ("1".equals(organ.get("IS_HALL"))) {
						__count++;
						sqlBuff.append("?,");
						paramValue.add(organ.get("CODE"));
					}
				}
				sql = sqlBuff.toString();
				if (__count > 0) {
					whereValue
							.append(sql.substring(0, sql.length() - 1) + ") ");
				}
			}
			data.put("page", page);
			data.put("rows", rows);
			// 目录排序
			String orderBy = (String) pSet.getParameter("orderBy");
			if (!"".equals(orderBy) && orderBy != null) {
				data.put("orderBy", orderBy);
			} else {
				data.put("orderBy", "order by org_code");
			}
			//潍坊项目，空目录不返回WSBS-3558
			data.put("showNoItems","yes");
			data.put("whereValue", whereValue.toString());
			data.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			System.out.println("地址："+url.toString());
			System.out.println("参数："+data.toString());
			System.out.println("返回值："+obj.toString());
			String pageList = obj.getString("pageList");
			ds.setRawData(pageList);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			logger.info(e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}

	/**
	 * 获取发布内容列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPubContentList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		// JSONArray upfileAttach=new JSONArray();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");
			String str = "and t.rid = '"
					+ rid
					+ "'"
					+ " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			if ("1".equals(open)) {
				str = "and (t.rid = '"
						+ rid
						+ "'"
						+ ") and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid + "') and s.name = '" + name + "')";
			}

			// add by liuyq 添加查询条件
			String startTime = (String) pSet.get("startTime");
			String endTime = (String) pSet.get("endTime");
			String keyWord = (String) pSet.get("keyWord");
			String dept = (String) pSet.get("dept");
			String searchCon = "";
			if (StringUtils.isNotBlank(startTime)) {
				searchCon += " and t.cTime >= to_date('" + startTime
						+ "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(endTime)) {
				searchCon += " and t.cTime <= to_date('" + endTime
						+ "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(keyWord)) {
				searchCon += " and t.name like '%" + keyWord + "%' ";
			}
			if (StringUtils.isNotBlank(dept)) {
				searchCon += " and t.dept_id = '" + dept + "' ";
			}

			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + searchCon + " order by CORDER desc,ctime desc,t.id";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 政策法规
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPoliciList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		// JSONArray upfileAttach=new JSONArray();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String wr = (String) pSet.getParameter("WebRegion");
			if (StringUtil.isNotEmpty(wr)) {
				rid = wr;
			}
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");
			String str = "and t.rid = '"
					+ rid
					+ "'"
					+ " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			if ("1".equals(open)) {
				str = "and (t.rid = '"
						+ rid
						+ "'"
						+ ") and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid + "') and s.name = '" + name + "')";
			}

			// add by liuyq 添加查询条件
			String startTime = (String) pSet.get("startTime");
			String endTime = (String) pSet.get("endTime");
			String keyWord = (String) pSet.get("keyWord");
			String dept = (String) pSet.get("dept");
			String searchCon = "";
			if (StringUtils.isNotBlank(startTime)) {
				searchCon += " and t.cTime >= to_date('" + startTime
						+ "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(endTime)) {
				searchCon += " and t.cTime <= to_date('" + endTime
						+ "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(keyWord)) {
				searchCon += " and t.name like '%" + keyWord + "%' ";
			}
			if (StringUtils.isNotBlank(dept)) {
				searchCon += " and t.dept_id = '" + dept + "' ";
			}

			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + searchCon + " order by t.corder DESC,ctime desc,t.id";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		try {
			if (ds.getTotal() > 0) {
				JSONArray arry = ds.getJAData();
				for (int j = 0; j < arry.size(); j++) {
					String id = arry.getJSONObject(j).getString("ID");
					String sql = "select * from attach t where (type='2' or type ='3') and  conid = '"
							+ id + "'";
					dsAttach = this.executeDataset(sql);
					int totla = dsAttach.getTotal();
					JSONArray upfileAttach = new JSONArray();
					if (totla > 0) {
						// 这里加入下载代码
						JSONArray upfile = dsAttach.getJAData();
						for (int i = 0; i < upfile.size(); i++) {
							// ---下载文件到项目路径本地 start
							String fileName = (String) ((JSONObject) upfile
									.get(i)).get("YNAME");
							String fileType = (String) ((JSONObject) upfile
									.get(i)).get("TYPE");
							String doc_id = (String) ((JSONObject) upfile
									.get(i)).get("DOCID");
							if ("1".equals(fileType) || "2".equals(fileType)) {
								upfileAttach.add(upfile.get(i));
							}
							UploadUtil.downloadFile(fileName, doc_id, fileType);
						}
					}
					arry.getJSONObject(j).put("upfile", upfileAttach);
				}
				ds.setRawData(arry);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 获取重点事项公布的内容
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentInfoOfEventPublic(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		// JSONArray upfileAttach = new JSONArray();
		String cid = (String) pSet.getParameter("cid");
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String str = "and t.rid = '"
					+ rid
					+ "'"
					+ " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and id='" + cid + "' )";
			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by ctime desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo：" + e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		try {
			String id = ds.getJOData().getString("ID");
			String sql = "select * from attach t where (type='2' or type ='3') and  conid = '"
					+ id + "'";
			dsAttach = this.executeDataset(sql);
			int totla = dsAttach.getTotal();
			if (totla > 0) {
				// 这里加入下载代码
				JSONArray upfile = dsAttach.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i))
							.get("NAME");
					String fileType = (String) ((JSONObject) upfile.get(i))
							.get("TYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i))
							.get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 获取结果材料
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getResultMaterial(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String itmeId = (String) pSet.get("itemId");
			String sblsh = (String) pSet.get("sblsh");
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+ "/getMaterial?itemId="+ itmeId + "&receiveNumber=" + sblsh);
			HttpClientUtil client = new HttpClientUtil();
			Object ret = client.getResult(url, "");
			JSONArray obj;
			JSONArray __obj = new JSONArray();
			obj = JSONObject.fromObject(ret).getJSONArray("data");
			System.out.println("材料返回值："+obj.toString());
			int len = obj.size();
			int _len = 0;
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					System.out.println("类别："+obj.getJSONObject(i).getString("BUSINESS_TYPE"));					
					if ("Finish".equals(obj.getJSONObject(i).getString("BUSINESS_TYPE"))) {
						__obj.add(obj.get(i));
						_len++;
					}
				}
				ds.setRawData(__obj);
				ds.setState(StateType.SUCCESS);
				ds.setTotal(_len);
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setTotal(0);
			}
		} catch (Exception e) {
			ds.setTotal(0);
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		System.out.println("结果返回值："+ds.toString());
		return ds;
	}

	/**
	 * 新余三单
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String power_type = (String) pSet.get("power_type");
			String deptid = (String) pSet.get("deptid");
			String str = "";
			if (StringUtils.isNotEmpty(name)) {
				str = "and t.rid = '"
						+ rid
						+ "'"
						+ " and cid in (select id from pub_channel s  where s.rid = '"
						+ rid + "' and s.name = '" + name + "')";
			} else {
				str = "and t.rid = '"
						+ rid
						+ "'"
						+ " and cid in (select id from pub_channel s  where s.rid = '"
						+ rid + "')";
			}
			if (StringUtils.isNotEmpty(power_type)) {
				str += " and t.power_type='" + power_type + "'";
			}
			if (StringUtils.isNotEmpty(deptid)) {
				str += " and t.dept_id='" + deptid + "'";
			}
			String sql = "select to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,attach.DOCID,attach.NAME PICNAME,t.POWER_TYPE from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by t.CTIME desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentList："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 新余三单 数量
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentListCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String power_type = (String) pSet.get("power_type");
			String deptid = (String) pSet.get("deptid");
			String str = "";
			if (StringUtils.isNotEmpty(name)) {
				str = "and t.rid = '"
						+ rid
						+ "'"
						+ " and cid in (select id from pub_channel s  where s.rid = '"
						+ rid + "' and s.name = '" + name + "')";
			} else {
				str = "and t.rid = '"
						+ rid
						+ "'"
						+ " and cid in (select id from pub_channel s  where s.rid = '"
						+ rid + "')";
			}
			if (StringUtils.isNotEmpty(power_type)) {
				str += " and t.power_type='" + power_type + "'";
			}
			if (StringUtils.isNotEmpty(deptid)) {
				str += " and t.dept_id='" + deptid + "'";
			}
			String sql = "select sumxzxk+sumxzcf+sumxzqz+sumxzzs+sumxzcj+sumxzqr+sumxzjl+sumqtxzql+sumxzjf+sumbmfw+sumgxql count,"
					+ "sumxzxk,sumxzcf,sumxzqz,sumxzzs,sumxzcj,sumxzqr,sumxzjl,sumqtxzql,sumxzjf,sumbmfw,sumgxql  from "
					+ "(select sum(case when t.power_type ='行政许可' then 1 else 0 end) sumxzxk,"
					+ "sum(case when t.power_type ='行政处罚' then 1 else 0 end) sumxzcf,"
					+ "sum(case when t.power_type ='行政强制' then 1 else 0 end) sumxzqz,"
					+ "sum(case when t.power_type ='行政征收' then 1 else 0 end) sumxzzs,"
					+ "sum(case when t.power_type ='行政裁决' then 1 else 0 end) sumxzcj,"
					+ "sum(case when t.power_type ='行政确认' then 1 else 0 end) sumxzqr,"
					+ "sum(case when t.power_type ='行政奖励' then 1 else 0 end) sumxzjl,"
					+ "sum(case when t.power_type ='其他行政权力' then 1 else 0 end) sumqtxzql,"
					+ "sum(case when t.power_type ='行政给付' then 1 else 0 end) sumxzjf,"
					+ "sum(case when t.power_type ='共性权力' then 1 else 0 end) sumgxql,"
					+ "sum(case when t.power_type ='便民服务' then 1 else 0 end) sumbmfw from "
					+ "pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' "
					+ "where t.submit_status='1' and t.status='1' and t.checks='1'"
					+ str + " order by ctime desc)";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentList："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 子栏目
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getChannelList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String sql = "select * from pub_channel t where t.parent in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			sql += " order by t.corder asc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	public DataSet getzbCatalog(ParameterSet pSet) {
		DataSet ds;
		String classType = (String) pSet.getParameter("ID");
		String sql = "select * from power_catalog t where id = ?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { classType }, conn);
		ds = result;
		return ds;
	}

	/**
	 * 根据内容ID获取评论内容
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getCommentById(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String cid = (String) pSet.getParameter("cid");
		try {
			String sql = "select t.CONTENT,to_char( t.CTIME,'YYYY-MM-DD') CTIME,t.CREATOR_NAME from PUB_COMMENT t where t.status=0 and t.cid = '"
					+ cid + "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return ds;
	}

	public DataSet saveComment(ParameterSet pSet) {

		String sql = "insert into pub_comment (ID,CID,CONTENT,CTIME,CREATOR,STATUS,CREATOR_NAME,REGION_CODE) values  (?,?,?,?,?,?,?,?)";

		DataSet ds = new DataSet();

		try {
			String ID = Tools.getUUID32();
			int j = this.executeUpdate(
					sql,
					new Object[] {
							ID,
							pSet.get("CID"),
							pSet.get("CONTENT"),
							CommonUtils_api.getInstance().parseDateToTimeStamp(
									new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							pSet.get("CREATOR"), "0", pSet.get("CREATOR_NAME"),
							SecurityConfig.getString("WebRegion") });
			if (j > 0) {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("");
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("事项信息写入数据库失败");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		}
		return ds;
	}

	/**
	 * 获取信息公示列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPublicityList(ParameterSet pSet) {
		DataSet ds = new DataSet();

		try {
			String region_id = SecurityConfig.getString("WebRegion");

			String keyword = (String) pSet.remove("keyword");
			String deptName = (String) pSet.remove("deptName");
			String startTime = (String) pSet.remove("startTime");
			String endTime = (String) pSet.remove("endTime");
			String str = " where region_id = '" + region_id + "' ";
			if (StringUtils.isNotEmpty(keyword)) {
				str += " and ( DEPT_NAME like '%" + keyword
						+ "%' or TITLE like '%" + keyword + "%') ";
			}
			if (StringUtils.isNotEmpty(deptName)) {
				str += " and ( DEPT_NAME like '%" + deptName + "%') ";
			}
			if (StringUtils.isNotEmpty(startTime)) {
				str += " and to_char(RELEASEDATE,'yyyy-mm-dd') > '" + startTime
						+ "'";
			}
			if (StringUtils.isNotEmpty(endTime)) {
				str += " and to_char(RELEASEDATE,'yyyy-mm-dd') < '" + endTime
						+ "'";
			}

			// str += " and startdate <= sysdate and enddate >= sysdate-1 ";

			String sql = "select ID,TITLE,DEPT_NAME,to_char(STARTDATE,'yyyy-mm-dd') STARTDATE,to_char(ENDDATE,'yyyy-mm-dd') ENDDATE,to_char(RELEASEDATE,'yyyy-mm-dd') RELEASEDATE,DOC_ID,FILE_NAME FROM PUBLICITY_BASICINFO "
					+ str + " ORDER BY RELEASEDATE DESC";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return ds;
	}

	/**
	 * 获取信息公示明细
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPublicityDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();

		try {
			String id = (String) pSet.remove("id");

			String sql = "select TITLE,DEPT_NAME,to_char(STARTDATE,'yyyy-mm-dd') STARTDATE,to_char(ENDDATE,'yyyy-mm-dd') ENDDATE,to_char(RELEASEDATE,'yyyy-mm-dd') RELEASEDATE,DEPT_NAME,CONTENT FROM PUBLICITY_BASICINFO WHERE ID= "
					+ id;
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return ds;
	}

	public DataSet getItemsByItemId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl") + "/getItemsByItemId");
		String itemId = (String) pSet.getParameter("itemId");
		url += "?itemId=" + itemId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 查询办件进度(列表)
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String phoneNum = (String) pSet.remove("phone");
		String page = (String) pSet.remove("page"); // 当前页数
		String rows = (String) pSet.remove("rows"); // 每页条数
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getBusinessList");
			Map<String, String> map = new HashMap<String, String>();
			map.put("phone", phoneNum);
			map.put("page", page);
			map.put("rows", rows);
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			if ("300".equals(json.getString("state"))) {
				ds.setState(StateType.FAILT);
				ds.setMessage(json.getString("error"));
			} else {
				JSONArray data = json.getJSONArray("LIST");
				// "pageNumber":1,"pageSize":10,"state":"200","totalPage":65,"totalRow":642}
				ds.setTotal(Integer.parseInt(json.getString("totalRow")));
				JSONObject message = new JSONObject();
				ds.setMessage(message.toString());
				ds.setRawData(data);
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 通过手机查询咨询投诉件
	 * 
	 * @param pSet
	 * @return
	 */

	public DataSet guestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			int page = Integer.parseInt((String) pSet.getParameter("page"));
			String phone = (String) pSet.getParameter("phone");
			int rows = Integer.parseInt((String) pSet.getParameter("rows"));
			String sql = null;
			sql = "select t.* from GUESTBOOK t where 1=1 and type in(2,3) and phone = ?";
			ds = DbHelper.query(sql, page, rows, new Object[] { phone }, conn,
					"icityDataSource");
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("pageindex:" + page + ",pagesize:" + rows);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 通过registernum查询业务具体办理进度
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet BusinessProgressQueryByRegisternum(ParameterSet pset) {

		DataSet ds = new DataSet();
		String registernum = (String) pset.remove("registernum");
		Object ret;
		try {
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/getEr?registernum="
					+ registernum);
			ret = client.getResult(url, "");
			JSONObject obj = JSONObject.fromObject(ret);
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	// 廉政格言
	public DataSet getList4Lzgy(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.agent_code,content.agent_name,content.creator_id,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder
					.append(" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.TYPE as pictype,attach.DOCID,attach.CONID ");
			sqlBuilder
					.append(" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1' and content.submit_status = '1' ");

			String rid = (String) pSet.remove("content.rid@=");
			sqlBuilder.append(" and (content.rid ='" + rid + "'");
			sqlBuilder.append(" or content.rid ='330901000000') ");// 市级内容也在县区显示
			String sql = SqlCreator.getSimpleQuerySql(pSet,
					sqlBuilder.toString(), this.getDataSource());

			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			ds = this.executeDataset(sql, start, limit);

			if (ds.getTotal() > 0) {
				SecureRandom rand = new SecureRandom();
				int i = Math.abs(rand.nextInt(ds.getTotal() - 1));
				JSONObject o = ds.getRecord(i);
				ds.setTotal(1);
				ds.setRawData("[" + o + "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 通过申办流水号获取通知书
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet getPrintHistory(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sblsh = (String) pset.getParameter("sblsh");
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/cq/getprinthistory?receiveNumber=" + sblsh);
			JSONObject ret = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(ret.getString("state"))) {
				ds.setRawData(ret.getJSONArray("result"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage("获取通知书失败！");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			logger.info(e.getMessage());
		}
		return ds;
		/*DataSet ds = new DataSet();
		String sblsh = (String) pset.getParameter("sblsh");
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")+ "/getPrintHistory");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			Map<String, String> map = new HashMap<String, String>();
			map.put("receiveNumber", sblsh);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			System.out.print("getPrintHistory-url："+url);
			System.out.print("getPrintHistory-param："+map.toString());
			Object m_ret = RestUtil.postData(url, map);
			System.out.print("getPrintHistory："+m_ret);
			JSONObject ret = JSONObject.fromObject(m_ret);
			if ("200".equals(ret.getString("state"))) {
				ds.setRawData(ret.getJSONArray("info"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage("获取通知书失败！");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			logger.info(e.getMessage());
		}
		return ds;*/
	}
	
	/**
	 * 根据材料空表或样表查询（福州）
	 * @param pSet
	 * @return
	 */
	public DataSet getMaterialList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			
			Integer page = (Integer) pSet.getParameter("page");
			Integer rows = (Integer) pSet.getParameter("rows");
			String key = (String) pSet.getParameter("key");
			String url = SecurityConfig.getString("ItemSynchronizationUrl") + "getMaterialList?material_name="+URLEncoder.encode(key, "UTF-8")+"&page="+page+"&rows="+rows;
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			
//			String jsonContent = "{'pageList':[{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}},{'columns':{'MATERIAL_NAME':'镇居民参加医疗保险申请表','SAMPLE_NAME':'测镇居民参加医疗保险申请表.doc','URL':''}}],'code':'200','totalPage':'2','totalRow':'11'}";
//			JSONObject obj = JSONObject.fromObject(jsonContent);
			
			if ("200".equals(obj.getString("code"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage("查询事项系统失败！");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	
	/**
	 * 漳州使用
	 * 分页获取事项信息
	 * 
	 * @param pSet
	 * @return
	 * @throws Exception 
	 */
	public JSONObject getItemListByPage(ParameterSet pSet)
			throws Exception {
		String OrgCode = (String) pSet.getParameter("org_code");
		String SearchName = (String) pSet.getParameter("SearchName");
		String TitleName = (String) pSet.getParameter("TitleName");
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl") + "/getItemListByPage");
		Map<String, String> map=new HashMap<String, String>();
		map.put("page",pSet.getParameter("page").toString());
		map.put("rows",pSet.getParameter("rows").toString());
		StringBuilder whereValue = new StringBuilder();
		JSONArray paramValue=new JSONArray();
		if(StringUtils.isNotEmpty(OrgCode)){
			whereValue.append(" and org_code = ? ");
			paramValue.add(OrgCode);
		}
		if(StringUtils.isNotEmpty(SearchName)){
			whereValue.append(" and NAME like ? ");
			paramValue.add("%" + SearchName + "%");
		}
		if(StringUtils.isNotEmpty(TitleName)){//主题
			whereValue.append(" and TITLE_NAME like ? ");
			paramValue.add("%" + TitleName + "%");
		}
		//过滤中介机构 
		whereValue.append(" and type not in (?) ");
		paramValue.add("ZJJG");
		whereValue.append(" and REGION_CODE = ? ");
		paramValue.add(SecurityConfig.getString("WebRegion"));
		map.put("whereValue", whereValue.toString());
		map.put("paramValue", paramValue.toString());
		JSONObject JsonObj = JSONObject.fromObject(RestUtil.postData(url,map));
		return JsonObj;
	}
	/**
	 * 根据单位获取有电子材料的事项
	 * @param pSet
	 * @return
	 */
	public DataSet getHaveSampleItem(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String orgCode = (String) pSet.getParameter("orgCode");
			String url = SecurityConfig.getString("ItemSynchronizationUrl") + "getHaveSampleItem?orgCode="+orgCode;
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("1".equals(obj.getString("state"))) {
				ds.setRawData(obj.getJSONArray("data").toString());
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage("查询事项系统失败！"+obj);
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！"+e.toString());
		}
		return ds;
	}
}