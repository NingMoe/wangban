/**  
 * @Title: BusinessFavoriteDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-30 下午6:31:30 
 * @version V1.0  
 */
package app.icity.project;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;

/**
 * @ClassName: BusinessFavoriteDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-5-30 下午6:31:30
 */

public class BusinessFavoriteDao extends BaseJdbcDao {

	private BusinessFavoriteDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static BusinessFavoriteDao getInstance() {
		return DaoFactory.getDao(BusinessFavoriteDao.class.getName());
	}
}
