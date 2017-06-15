package app.icity.guestbook;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;

public class ReplyDao extends BaseJdbcDao {
	private ReplyDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ReplyDao getInstance() {
		return DaoFactory.getDao(ReplyDao.class.getName());
	}

	public DataSet getList(ParameterSet pSet) {
		pSet.remove("dept");
		pSet.remove("flag");
		String sql = "SELECT * FROM GUESTBOOK";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		sql += " ORDER BY WRITE_DATE DESC";

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

	public DataSet insert(ParameterSet pSet) {
		String sql = "INSERT INTO GUESTBOOK (CHECKS,ANONYMOUS,WARN)VALUES(?,?,?)";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, new Object[] { "0", (String) pSet.getParameter("ANONYMOUS"), "0", });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	public DataSet update(ParameterSet pSet) {
		DataSet ds = new DataSet();
		return ds;
	}

	public DataSet delete(ParameterSet pSet) {
		String ids = (String) pSet.getParameter("ids");
		DataSet ds = new DataSet();
		if (StringUtils.isNotEmpty(ids)) {
			ParameterSet tSet = new ParameterSet();
			tSet.setParameter("ID@in", ids);
			String sql = "DELETE FROM GUESTBOOK";
			sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
			int i = this.executeUpdate(sql);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			}
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("参数ids的值为空！");
		}
		return ds;
	}

	// 回复
	public DataSet reply(ParameterSet pSet) {
		String sql = "UPDATE GUESTBOOK  SET CHECKS=?, OPEN=? ,DEAL_DATE=? ,DEAL_RESULT=? ,STATUS=? ,REPLY_IP=? , REPLAYER=? WHERE ID = ?";
		DataSet ds = new DataSet();
		try {
			int i = this.executeUpdate(sql,
					new Object[] { (String) pSet.getParameter("CHECKS"), (String) pSet.getParameter("OPEN"),
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
							(String) pSet.getParameter("DEAL_RESULT"), "1", pSet.getRemoteAddr(),
							(String) pSet.getParameter("REPLAYER"), (String) pSet.getParameter("ID") });
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
				// isSendSms=1，咨询、投诉已处理。将回复内容短信通知给用户
				String isSendSms = SecurityConfig.getString("isSendSms2Guest");
				if ("1".equals(isSendSms)) {
					SmsDao.getInstance().sendMessage2Guest(pSet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;

	}

	// 审核
	public DataSet check(ParameterSet pSet) {
		String sql = "UPDATE GUESTBOOK  SET CHECKS=? WHERE ID = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { (String) pSet.getParameter("CHECKS"), (String) pSet.getParameter("ID") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;

	}

	// 分发
	public DataSet giveaway(ParameterSet pSet) {
		String dept_id = (String) pSet.getParameter("dept_id");
		String dept_name = (String) pSet.getParameter("dept_name");
		DataSet ds = new DataSet();
		try {
			String sql = "UPDATE GUESTBOOK SET GIVE_STATUS='1', DEPART_ID=?, DEPART_NAME=?, GIVE_DEPT_ID = ?, GIVE_DEPT_NAME= ? WHERE ID = ?";
			String[] ids = ((String) pSet.getParameter("ids")).split(",");
			for (int j = 0; j < ids.length; j++) {
				this.executeUpdate(sql, new Object[] { dept_id, dept_name, dept_id, dept_name, ids[j] });
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败!");
			e.printStackTrace();
		}
		return ds;

	}

	// 分发人直接回复
	public DataSet give_reply(ParameterSet pSet) {
		String sql = "UPDATE GUESTBOOK  SET CHECKS=?, OPEN=? ,DEAL_DATE=? ,DEAL_RESULT=? ,STATUS=? ,REPLY_IP=? , REPLAYER=?, DEPART_NAME=?, GIVE_DEPT_NAME=?, DEPART_ID=?, GIVE_DEPT_ID=?, GIVE_STATUS=? WHERE ID = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, new Object[] { "1", (String) pSet.getParameter("OPEN"),
				CommonUtils.getInstance().parseStringToTimeStamp(
						Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS),
				(String) pSet.getParameter("DEAL_RESULT"), "1", pSet.getRemoteAddr(),
				(String) pSet.getParameter("REPLAYER"), (String) pSet.getParameter("DEPART_NAME"),
				(String) pSet.getParameter("DEPART_NAME"), (String) pSet.getParameter("DEPART_ID"),
				(String) pSet.getParameter("DEPART_ID"), (String) pSet.getParameter("GIVE_STATUS"),
				(String) pSet.getParameter("ID") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;

	}

}
