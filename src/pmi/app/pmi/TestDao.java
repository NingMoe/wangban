package app.pmi;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class TestDao extends BaseJdbcDao {
	private TestDao() {
	}

	public static TestDao getInstance() {
		return DaoFactory.getDao(TestDao.class.getName());
	}

	public DataSet getList(ParameterSet pSet) {
		String sql = "SELECT * FROM PROJECT_ITEM";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet delete(ParameterSet pSet) {
		String ids = (String) pSet.getParameter("ids");
		DataSet ds = new DataSet();
		if (StringUtils.isNotEmpty(ids)) {
			String sql = "DELETE FROM PROJECT_ITEM";
			ParameterSet tSet = new ParameterSet();
			tSet.setParameter("ID@in", ids);
			sql = SqlCreator.getSimpleQuerySql(tSet, sql, getDataSource());
			int i = this.executeUpdate(sql);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			}
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("参数ids的值为空！");
		}
		return ds;

	}
}
