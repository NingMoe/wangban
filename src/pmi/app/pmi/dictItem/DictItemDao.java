package app.pmi.dictItem;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class DictItemDao extends BaseJdbcDao {
	private DictItemDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static DictItemDao getInstance() {
		return DaoFactory.getDao(DictItemDao.class.getName());
	}

	public DataSet getList(ParameterSet pset) {
		String sql = "SELECT ITEM.ITEM_CODE,ITEM.DICT_CODE,ITEM.ITEM_VALUE,ITEM.XH,"
				+ "ITEM.PARENT_CODE,ITEM.NOTE,ITEM.IN_USE,ITEM.ITEM_CODE AS ID,DICT.DICT_NAME,"
				+ "ITEM.ITEM_CODE AS ITEM_CODE_TEMP,ITEM1.ITEM_VALUE AS PARENT_CODE_NAME FROM PUB_DICT_ITEM ITEM "
				+ "LEFT JOIN PUB_DICT DICT ON DICT.DICT_CODE = ITEM.DICT_CODE "
				+ "LEFT JOIN PUB_DICT_ITEM ITEM1 ON ITEM1.ITEM_CODE = ITEM.PARENT_CODE AND ITEM1.DICT_CODE = ITEM.DICT_CODE ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " ORDER  BY ITEM.XH ASC,ITEM.ITEM_CODE ASC";
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
		String sql = "INSERT INTO PUB_DICT_ITEM(ITEM_CODE,DICT_CODE,ITEM_VALUE,XH,PARENT_CODE,NOTE,IN_USE) VALUES(?,?,?,?,?,?,?)";
		int i = this.executeUpdate(sql,
				new Object[] { (String) pset.getParameter("ITEM_CODE"), (String) pset.getParameter("DICT_CODE"),
						(String) pset.getParameter("ITEM_VALUE"), Integer.valueOf((String) pset.getParameter("XH")),
						(String) pset.getParameter("PARENT_CODE"), (String) pset.getParameter("NOTE"),
						(String) pset.getParameter("IN_USE") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	public DataSet delete(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sql = "DELETE FROM PUB_DICT_ITEM";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int i = this.executeUpdate(sql);
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败!");
		}
		return ds;
	}

	public DataSet update(ParameterSet pset) {
		String sql = "UPDATE PUB_DICT_ITEM SET ITEM_CODE=?,DICT_CODE=?,ITEM_VALUE=?,XH=?,PARENT_CODE=?,NOTE=?,IN_USE=? WHERE ITEM_CODE =? and DICT_CODE=?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { (String) pset.getParameter("ITEM_CODE"), (String) pset.getParameter("DICT_CODE"),
						(String) pset.getParameter("ITEM_VALUE"), (String) pset.getParameter("XH"),
						(String) pset.getParameter("PARENT_CODE"), (String) pset.getParameter("NOTE"),
						(String) pset.getParameter("IN_USE"), (String) pset.getParameter("ITEM_CODE_TEMP"),
						(String) pset.getParameter("DICT_CODE"), });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
}
