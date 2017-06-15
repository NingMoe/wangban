package app.pmi.id;

import org.apache.commons.lang.StringUtils;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class IdDao extends BaseJdbcDao {
	private IdDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static IdDao getInstance() {
		return DaoFactory.getDao(IdDao.class.getName());
	}

	public DataSet getList(ParameterSet pset) {
		String sql = "SELECT ID,NAME,VALUE,PREFIX,SUFFIX,IN_USE,ID AS ID_TEMP,LENGTH FROM PUB_ID";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet insert(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sql = "INSERT INTO PUB_ID(ID,NAME,VALUE,PREFIX,SUFFIX,IN_USE,LENGTH) VALUES(?,?,?,?,?,?,?)";
		int i = this.executeUpdate(sql,
				new Object[] { (String) pset.getParameter("ID"), (String) pset.getParameter("NAME"),
						Integer.valueOf((String) pset.getParameter("VALUE")), (String) pset.getParameter("PREFIX"),
						(String) pset.getParameter("SUFFIX"), (String) pset.getParameter("IN_USE"),
						Integer.valueOf((String) pset.getParameter("LENGTH")) });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	public DataSet delete(ParameterSet pset) {
		String ids = (String) pset.getParameter("ids");
		DataSet ds = new DataSet();
		if (StringUtils.isNotEmpty(ids)) {
			String sql = "DELETE FROM PUB_ID";
			ParameterSet tset = new ParameterSet();
			tset.setParameter("ID@in", ids);
			sql = SqlCreator.getSimpleQuerySql(tset, sql, this.getDataSource());
			int i = this.executeUpdate(sql);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败!");
			}
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("参数ids的值为空!");
		}
		return ds;
	}

	public DataSet update(ParameterSet pset) {
		String sql = "UPDATE PUB_ID SET NAME=?,VALUE=?,PREFIX=?,SUFFIX=?,IN_USE=?,LENGTH=? WHERE ID =?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { (String) pset.getParameter("NAME"), Integer.valueOf((String) pset.getParameter("VALUE")),
						(String) pset.getParameter("PREFIX"), (String) pset.getParameter("SUFFIX"),
						(String) pset.getParameter("IN_USE"), Integer.valueOf((String) pset.getParameter("LENGTH")),
						(String) pset.getParameter("ID") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
}
