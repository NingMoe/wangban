package heilongjiang_ggfw.app.icity.govservice;

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
import org.eclipse.jetty.util.log.Log;
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
	public DataSet getMattersList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getItemListByPage");
			Map<String, String> data = new HashMap<String, String>();
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("TYPE");
			String subType = (String) pSet.getParameter("SUB_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String parent=(String)pSet.getParameter("parent");
			String kind_name = (String)pSet.getParameter("kind_name");
			String org_participate_name = (String)pSet.getParameter("org_participate_name");
			String sub_type = (String)pSet.getParameter("sub_type");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			
			if (!"".equals(kind_name) && kind_name != null) {//服务领域
				whereValue.append(" and kind_name like ? ");
				paramValue.add("%"+kind_name+"%");
			}			
			if(!"".equals(org_participate_name) && org_participate_name != null){//责任主体
				whereValue.append(" and ORG_PARTICIPATE_NAME like ? ");
				paramValue.add("%"+org_participate_name+"%");
			}
			if(!"".equals(sub_type) && sub_type != null){//事项类别
				if("ZH".equals(sub_type)){
					whereValue.append(" and sub_type=? ");
					paramValue.add(sub_type);
				}else{
					whereValue.append(" and (sub_type!=? or sub_type is null) ");
					paramValue.add("ZH");
				}			
			}
			if("parent".equals(parent)){//父项 或者 没有子项的事项
				whereValue.append(" and e.PARENT_ITEM_CODE is null ");				
			}
			if ("person".equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");				
			} else if ("ent".equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			} else if ("bmfw".equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%4%");
			}
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
			/*
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
			}*/

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
				String parent_code = column.getJSONObject("columns").getString("CODE");
				Map<String, String> m_data = new HashMap<String, String>();
				m_data.put("page", "1");
				m_data.put("rows", "1000");
				m_data.put("whereValue", " and e.PARENT_ITEM_CODE=? ");
				JSONArray pcode = new JSONArray();
				pcode.add(parent_code);
				m_data.put("paramValue", pcode.toString());
				Object m_s = RestUtil.postData(url, m_data);				
				JSONObject m_obj = new JSONObject();
				m_obj = JSONObject.fromObject(m_s);
				JSONArray m_pageList = m_obj.getJSONArray("pageList");
				
				JSONObject columns = column.getJSONObject("columns");
				int pagesize = m_pageList.size();
				columns.put("child", pagesize+"");
				if(pagesize>0){					
					columns.put("children", m_pageList);
				}
				rows.add(columns);
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
}