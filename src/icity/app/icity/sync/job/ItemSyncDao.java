package app.icity.sync.job;

import org.apache.commons.lang.StringUtils;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/**
 * @Description: TODO(实现后台事项同步操作同步到网上办事平台)
 * @author zhuxiaoyue
 * @date 2014-3-28
 */
public class ItemSyncDao extends BaseJdbcDao {

	private ItemSyncDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ItemSyncDao getInstance() {
		return DaoFactory.getDao(ItemSyncDao.class.getName());
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
			// log_.info("同步事项数据失败！");
			return ds;
		}

		String busiCodeArray[] = busiCode.split(",");

		String sql = "UPDATE POWER_BASE_INFO SET STATUS=0 WHERE CODE=?";
		Object[][] obj = new Object[busiCodeArray.length][1];
		for (int i = 0, n = busiCodeArray.length; i < n; i++) {
			obj[i][0] = busiCodeArray[i];
		}

		try {
			this.batchUpdate(sql, obj);
			// log_.info("同步事项数据成功！");
			return ds;
		} catch (Exception e) {
			// log_.info("同步事项数据失败！");
			return ds;
		}

	}

}
