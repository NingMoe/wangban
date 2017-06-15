/**  
 * @Title: HelpDao.java 
 * @Package icity.home 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-6-13 上午11:17:25 
 * @version V1.0  
 */
package app.icity.home;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;

/**
 * @ClassName: HelpDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-6-13 上午11:17:25
 */

public class HelpDao extends BaseJdbcDao {
	private HelpDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static HelpDao getInstance() {
		return DaoFactory.getDao(HelpDao.class.getName());
	}

	public DataSet getChannelList(ParameterSet pSet) {
		String sql = "SELECT ID, NAME, PARENT, CTIME, REMARK, STATUS, RID, CORDER, URL, TYPE"
				+ " FROM PUB_CHANNEL WHERE  STATUS = 1 AND RID='" + SecurityConfig.getString("WebRegion") + "'";

		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet getContentList(ParameterSet pSet) {
		String sql = "SELECT ID, CID, RID, NAME, CONTENT, CHECKS, URL, CTIME, CREATOR, BLANK, SUMMARY, TYPE, REMARK, STATUS, ATTACH"
				+ " FROM PUB_CONTENT WHERE  STATUS = 1 AND RID='" + SecurityConfig.getString("WebRegion") + "'";

		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}
}
