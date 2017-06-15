package plugin;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.icity.ServiceCmd;
import app.icity.onlineapply.ApplyQZDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;

public class GetItem4ThemePlugin implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				getItem4Theme();
			}
		};
		long tt = 1000 * 60;// 每60秒进行操作
		_taskTimer.schedule(task, tt, tt);
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		_taskTimer.cancel();
		_taskTimer = null;
		return true;
	}
	
	public void getItem4Theme(){
		DataSet ds = new DataSet();
		String region_code = SecurityConfig.getString("WebRegion");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "SELECT ID,NAME,CLASS_TYPE FROM power_catalog where class_type in ('个人','法人') "
				+ "and region_code='"+region_code+ "'";
			ds = DbHelper.query(sql, new Object[] {}, conn);
			
			int length = ds.getTotal();
			if(length>0){
				for(int i=0;i<length;i++){
					ParameterSet pSet = new ParameterSet();
					pSet.setParameter("ID", ds.getRecord(i).get("NAME").toString());
					pSet.setParameter("MODEL_NAME", ds.getRecord(i).get("CLASS_TYPE").toString());
					
					DataSet ds_ = getMattersList(pSet);
					int total = ds_.getTotal();
					if(total > 0){
						updatePowerCataLog(ds.getRecord(i).get("ID").toString(),"1");
					}else{
						updatePowerCataLog(ds.getRecord(i).get("ID").toString(),"0");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				DbHelper.closeConnection(conn);
		}
	}
	
	public DataSet getMattersList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			Map<String, String> data = new HashMap<String, String>();
			String classtype = (String) pSet.getParameter("ID");
			String model_name = (String) pSet.getParameter("MODEL_NAME");
			String pagemodel = "";
			if("个人".equals(model_name)){
				pagemodel = "person";
			}else if("法人".equals(model_name)){
				pagemodel = "ent";
			}
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("SP");

			if ("person".equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");

				whereValue.append(" and (SUB_TYPE =? or SUB_TYPE is null or SUB_TYPE =? or SUB_TYPE =? or SUB_TYPE =?)");
				paramValue.add("1");
				paramValue.add("");
				paramValue.add("SP");
				paramValue.add("XK");
			} else if ("ent".equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");

				whereValue.append(" and (SUB_TYPE =? or SUB_TYPE is null or SUB_TYPE =? or SUB_TYPE =? or SUB_TYPE =?)");
				paramValue.add("1");
				paramValue.add("");
				paramValue.add("SP");
				paramValue.add("XK");
			} 
			
			String[] themes = classtype.split(",");
			String sql = " and (";
			for (String o : themes) {
				sql += " title_name like ? or";
				paramValue.add("%" + o + "%");
			}
			sql = sql.substring(0, sql.length() - 2) + ") ";
			whereValue.append(sql);
			
			pSet.put("region_code",SecurityConfig.getString("WebRegion"));
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));
			
			// 过滤设置为不进驻大厅部门下的事项
			DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
			JSONArray organs = dept.getJOData().getJSONArray("organ");
			if (organs.size() > 0) {
				JSONObject organ = null;
				int __count = 0;
				StringBuffer sqlBuff = new StringBuffer(" and org_code in ( ");
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
					whereValue.append(sql.substring(0, sql.length() - 1) + ") ");
				}
			}
			data.put("page", "1");
			data.put("rows", "1000");
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
	
	/**
	 * 更新主题状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 */
	public void updatePowerCataLog(String id, String status) {

		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "update power_catalog set is_use =? where id = ?";
			DbHelper.update(sql, new Object[] { status, id }, conn);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				DbHelper.closeConnection(conn);
		}
	}
}
