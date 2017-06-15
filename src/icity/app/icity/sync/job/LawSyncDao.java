package app.icity.sync.job;

import org.apache.commons.lang.StringUtils;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/**
 * 
 * @ClassName: LawSyncDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhuxiaoyue
 * @date 2014-4-1 下午02:47:55
 *
 */
public class LawSyncDao extends BaseJdbcDao {

	private LawSyncDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static LawSyncDao getInstance() {
		return DaoFactory.getDao(LawSyncDao.class.getName());
	}

	/**
	 * 将删除的数据同步
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet DeleteSyncData(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String busiCode = (String) pSet.getParameter("CODE@IN");
		if (!StringUtils.isNotEmpty(busiCode)) {
			// log_.info("同步删除法规数据失败！");
			return ds;
		}

		String busiCodeArray[] = busiCode.split(",");

		String sql = "DELETE FROM BASE_LAWINFO WHERE CODE=?";
		Object[][] obj = new Object[busiCodeArray.length][1];
		for (int i = 0, n = busiCodeArray.length; i < n; i++) {
			obj[i][0] = busiCodeArray[i];
		}

		try {
			this.batchUpdate(sql, obj);
			// log_.info("同步删除法规数据成功！");
			return ds;
		} catch (Exception e) {
			// log_.info("同步删除法规数据失败！");
			return ds;
		}

	}

}
