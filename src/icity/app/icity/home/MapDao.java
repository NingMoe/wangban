/**  
 * @Title: MapDao.java 
 * @Package icity.home 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-6-8 下午12:51:25 
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
 * @ClassName: MapDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-6-8 下午12:51:25
 */

public class MapDao extends BaseJdbcDao {

	public static MapDao getInstance() {
		return DaoFactory.getDao(MapDao.class.getName());
	}

	public DataSet getChannelList(ParameterSet pSet) {
		pSet.setSortDir("CORDER");
		String parent = "";
		if (!(pSet.get("PARENT") == null)) {
			parent = pSet.get("PARENT").toString();
			pSet.remove("PARENT");
		}
		String sql = "SELECT * FROM PUB_CHANNEL WHERE  STATUS = 1 AND RID='" + SecurityConfig.getString("WebRegion")
				+ "'" + " AND PARENT = '" + parent + "'";

		sql = SqlCreator.getSimpleQuerySql(pSet, sql, getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

}
