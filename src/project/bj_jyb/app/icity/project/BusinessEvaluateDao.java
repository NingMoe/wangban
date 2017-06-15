/**  
 * @Title: BusinessEvaluateDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-30 下午2:25:50 
 * @version V1.0  
 */
package bj_jyb.app.icity.project;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
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

	private BusinessEvaluateDao() {
		this.setDataSourceName("icityDataSource");
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

	public DataSet getEvaluateList(ParameterSet pSet) {
		String sql = "SELECT * from STAR_LEVEL_EVALUATION e ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		sql += " order by creator_date desc";
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

	public DataSet delete(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "delete from STAR_LEVEL_EVALUATION";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("serial_number@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
				int i = this.executeUpdate(sql);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("请选择您需要删除的内容！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

}
