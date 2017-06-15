package linyi.app.icity.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class ProjectIndexDao extends BaseJdbcDao {
	protected static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	protected static String queueDataSource = "queueDataSource";//临沂排队等待数据源
	public ProjectIndexDao() {
		this.setDataSourceName(queueDataSource);
	}
	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao) DaoFactory.getDao(ProjectIndexDao.class.getName());
	}
	/**
	 * 排队列表-部门
	 * @param pset
	 * @return
	 */
	public DataSet getQueueDeptList(ParameterSet pSet) {
		DataSet ds;;
		String order = (String)pSet.get("order");
		String sql = "select distinct t.deptname,t.deptno from queue_business t order by t.deptno ";
		if (!StringUtil.isNotEmpty(order)) {
			order="desc";
		}
		sql+=order;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql,null,queueDataSource);
		} else {
			ds = this.executeDataset(sql, start, limit, null,this.getDataSourceName());
		}
		return ds;
	}
	/**
	 * 排队列表-排队数
	 * @param pset
	 * @return
	 */
	public DataSet getQueueBusinessList(ParameterSet pSet) {
		DataSet ds;
		String deptName = (String)pSet.get("deptName");
		String sql = "select s.BUSINESSNO,s.businessname,(select t.mq_iwaits from queue_waits t where t.mq_ijlid = s.businessno and t.MQ_VNETNUMBER=s.NETNUMBER) mq_iwaits from QUEUE_BUSINESS s where s.deptname = ?";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql,new Object[]{deptName},queueDataSource);
		} else {
			ds = this.executeDataset(sql, start, limit, new Object[]{deptName},this.getDataSourceName());
		}
		return ds;
	}
}