package app.pmi.dict;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class DictDao extends BaseJdbcDao {
	private DictDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static DictDao getInstance() {
		return DaoFactory.getDao(DictDao.class.getName());
	}

	public DataSet getList(ParameterSet pset) {
		String sql = "SELECT DICT_CODE,DICT_CODE AS ID,DICT_NAME,NOTE,IN_USE,DICT_CODE AS DICT_CODE_TEMP FROM PUB_DICT";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " ORDER BY CREATE_TIME DESC ";
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

}
