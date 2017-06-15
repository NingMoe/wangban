package app.pmi.srr;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.db.SqlCreator;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class SrrServerDao extends BaseJdbcDao {
	protected SrrServerDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static SrrServerDao getInstance() {
		return DaoFactory.getDao(SrrServerDao.class.getName());
	}

	public DataSet getServerList(ParameterSet pSet) {
		String sql = "select * from SRR_SERVER t ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet getServerAndAppList(ParameterSet pSet) {
		DataSet ds;
		String sql = "select s.* from SRR_SERVER s ";// 查询所有服务器
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		ds = this.executeDataset(sql);
		JSONArray serverArray = ds.getJAData();

		String appSql = "select a.* from SRR_APP a ";// 查询服务器下所有应用程序
		DataSet ads = this.executeDataset(appSql);
		JSONArray appArray = ads.getJAData();

		// 封装应用列表到服务器内
		if (serverArray != null && serverArray.size() > 0) {
			if (appArray != null && appArray.size() > 0) {
				for (int i = 0; i < serverArray.size(); i++) {
					JSONObject jo = (JSONObject) serverArray.get(i);// 遍历服务器
					if (jo != null) {
						String sid = jo.getString("ID");
						JSONArray appList = new JSONArray();// 将应用程序按照服务器ID分组
						for (int j = 0; j < appArray.size(); j++) {
							JSONObject ajo = (JSONObject) appArray.get(j);// 遍历应用程序
							if (ajo != null) {
								if (sid.equals(ajo.getString("SERVER_ID"))) {
									appList.add(ajo);
								}
							}
						}
						if (appList.size() > 0) {
							jo.put("APPLIST", appList);
						}
					}
				}
			}
		}
		ds.setData(Tools.stringToBytes(serverArray != null ? serverArray.toString() : ""));
		return ds;
	}

	public DataSet addServer(ParameterSet pSet) {
		DataSet dataSet = new DataSet();
		String sql = "insert into SRR_SERVER t (t.id,t.name,t.ip,t.is_in_use) values (?,?,?,?)  ";// t.test_times默认0
		Object[] params = { Tools.getUUID32(), (String) pSet.getParameter("NAME"), (String) pSet.getParameter("IP"),
				(String) pSet.getParameter("IS_IN_USE") };
		try {
			int i = this.executeUpdate(sql, params);
			if (i == 0) {
				dataSet.setState(StateType.FAILT);
				dataSet.setMessage("添加字段失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSet;
	}

	public DataSet updateServer(ParameterSet pSet) {
		DataSet dataSet = new DataSet();
		String sql = "update SRR_SERVER t set t.name=?,t.ip=?,t.is_in_use=? where t.id=?  ";
		Object[] params = { (String) pSet.getParameter("NAME"), (String) pSet.getParameter("IP"),
				(String) pSet.getParameter("IS_IN_USE"), (String) pSet.getParameter("ID") };
		int i = this.executeUpdate(sql, params);
		if (i == 0) {
			dataSet.setState(StateType.FAILT);
			dataSet.setMessage("字段修改失败！");
		}
		return dataSet;
	}

	public DataSet deleteServer(ParameterSet pSet) {
		DataSet dataSet = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "delete from SRR_SERVER ";
				ParameterSet tset = new ParameterSet();
				tset.setParameter("id@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tset, sql, this.getDataSource());
				int i = this.executeUpdate(sql);
				if (i == 0) {
					dataSet.setState(StateType.FAILT);
					dataSet.setMessage("删除字段失败!");
				}
			} else {
				dataSet.setState(StateType.FAILT);
				dataSet.setMessage("字段id的值为空!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSet;
	}
}
