/**  
 * @Title: BusinessEvaluateDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-30 下午2:25:50 
 * @version V1.0  
 */
package app.icity.center.active;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

/**
 * @ClassName: BusinessEvaluateDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-5-30 下午2:25:50
 */

public class BusinessEvaluateDao extends BaseJdbcDao {
	public static BusinessEvaluateDao getInstance() {
		return DaoFactory.getDao(BusinessEvaluateDao.class.getName());
	}

	public DataSet getMyEvaluateList(ParameterSet pSet) {
		String sql = "SELECT BP.BID,APPLYTIME,NAME,ORGID,VALUE,REMARK,CREATETIME FROM"
				+ "( SELECT BID,PID,APPLYTIME,NAME,ORGID FROM BUSINESS_INDEX BI ,PROJECT_ITEM PI"
				+ " WHERE BI.PID=PI.ID AND BI.STATUS=1 AND PI.STATUS=1 ) BP ,EVALUATE_REMARK ER "
				+ " WHERE ER.BID=BP.BID ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null);
		} else {
			ds = this.executeDataset(sql, start, limit, null);
		}
		return ds;
	}

	public DataSet getEvaluateToMeList(ParameterSet pSet) {
		// 被评价人表还没界定暂不处理对我的评价的查询
		String sql = "SELECT * FROM DUAL WHERE 0=1";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null);
		} else {
			ds = this.executeDataset(sql, start, limit, null);
		}
		return ds;
	}

}
