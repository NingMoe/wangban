package app.pmi.srr;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.db.SqlCreator;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class SrrAppDao extends BaseJdbcDao {
	protected SrrAppDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static SrrAppDao getInstance() {
		return DaoFactory.getDao(SrrAppDao.class.getName());
	}

	public DataSet getAppList(ParameterSet pSet) {
		String sql = "select * from SRR_APP t ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet getAppOfServerList(ParameterSet pSet) {
		String sql = "select t.*,t.id APP_ID,(select name from srr_server tt where tt.id=t.server_id) server_name  from SRR_APP t where 1=1 ";// 查询所有应用
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet addApp(ParameterSet pSet) {
		DataSet dataSet = new DataSet();
		String sql = "insert into SRR_APP t (t.id,t.name,t.server_id,t.type,t.is_in_use) values (?,?,?,?,?)  ";
		Object[] params = { (String) pSet.getParameter("ID"), (String) pSet.getParameter("NAME"),
				(String) pSet.getParameter("SERVER_ID"), (String) pSet.getParameter("TYPE"),
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

	public DataSet updateApp(ParameterSet pSet) {
		DataSet dataSet = new DataSet();
		String sql = "update SRR_APP t set t.name=?,t.server_id=?,t.type=?,t.is_in_use=? where t.id=?  ";
		Object[] params = { (String) pSet.getParameter("NAME"), (String) pSet.getParameter("SERVER_ID"),
				(String) pSet.getParameter("TYPE"), (String) pSet.getParameter("IS_IN_USE"),
				(String) pSet.getParameter("ID") };
		int i = this.executeUpdate(sql, params);
		if (i == 0) {
			dataSet.setState(StateType.FAILT);
			dataSet.setMessage("字段修改失败！");
		}
		return dataSet;
	}

	public DataSet deleteApp(ParameterSet pSet) {
		DataSet dataSet = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "delete from SRR_APP ";
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
