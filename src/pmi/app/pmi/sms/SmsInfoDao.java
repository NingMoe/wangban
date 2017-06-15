/**  
 * @Title: SmsInfoDao.java 
 * @Package sms 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @date 2013-7-29 下午4:08:46 
 * @version V1.0  
 */
package app.pmi.sms;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import app.pmi.id.IdDao;
import core.util.CommonUtils;

/**
 * @ClassName: SmsInfoDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2013-7-29 下午4:08:46
 */

public class SmsInfoDao extends BaseJdbcDao {

	private SmsInfoDao() {
		this.setDataSourceName("icityDataSource");
	}

	private static Log _log = LogFactory.getLog(SmsInfoDao.class);

	public static SmsInfoDao getInstance() {
		return DaoFactory.getDao(SmsInfoDao.class.getName());
	}

	public DataSet getList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "SELECT * FROM PUB_SMS where RID='" + SecurityConfig.getString("WebRegion") + "' ";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql, null);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error(e);
		}
		return ds;
	}

	public DataSet update(ParameterSet pSet) {
		String sql = "UPDATE PUB_SMS SET STATUS=? , UPDATETIME=to_date('"
				+ Tools.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss") + "' ,'yyyy-mm-dd hh24:mi:ss')  WHERE ID = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { (String) pSet.getParameter("STATUS"), (String) pSet.getParameter("ID") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("更新短信失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	public DataSet insert(ParameterSet pSet) {
		String sql = "INSERT INTO PUB_SMS (ID, RID, SMSCONTENT, SENDTIME, CHANNEL, STATUS,  TELEPHONE ) VALUES (?, ?, ?, ?, ?, ?, ?)";
		DataSet ds = new DataSet();
		String rid = "";
		if (pSet.containsKey("RID")) {
			rid = (String) pSet.getParameter("RID");
		} else {
			rid = SecurityConfig.getString("WebRegion");
		}
		int i = this.executeUpdate(sql, new Object[] { Tools.getUUID32(), rid, (String) pSet.getParameter("SMSCONTENT"),
				CommonUtils.getInstance().parseStringToTimeStamp(
						Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS),
				(String) pSet.getParameter("CHANNEL"), (String) pSet.getParameter("STATUS"),
				(String) pSet.getParameter("TELEPHONE") }, this.getDataSourceName());
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("插入短信失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
}
