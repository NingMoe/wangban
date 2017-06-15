package cq.app.icity.govservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectDao extends BaseJdbcDao {
	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
	}

	public DataSet getPowerList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String deptid = (String) pSet.getParameter("deptid");
			String item_type = (String) pSet.getParameter("ITEM_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String is_online = (String) pSet.getParameter("IS_ONLINE");
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
			if (StringUtils.isNotEmpty(is_online)) {
				whereValue.append(" and IS_ONLINE=? ");
				paramValue.add(is_online);
			}
			map.put("page", pSet.getParameter("page").toString());
			map.put("rows", pSet.getParameter("limit").toString());
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
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
}