package km.app.icity.guestbook;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import app.icity.guestbook.SmsDao;
import app.uc.UserDao;
import core.util.CommonUtils;
import km.app.icity.webservice.WebServiceBase;
import net.sf.json.JSONObject;

public class WriteDao extends BaseJdbcDao {
	public WriteDao() {
		this.setDataSourceName("icityDataSource");
	}

	protected static Log _log = LogFactory.getLog(WriteDao.class);

	public static WriteDao getInstance() {
		return DaoFactory.getDao(WriteDao.class.getName());
	}

	public DataSet getList(ParameterSet pSet) {
		String sql = "SELECT * FROM GUESTBOOK";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, getDataSource());
		sql += " ORDER BY WRITE_DATE DESC";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet insert(ParameterSet pSet) {
		String sql = "INSERT INTO GUESTBOOK "
				+ "(ID, WARN, CHECKS, DEPART_ID, DEPART_NAME, USERNAME, PHONE, EMAIL, ADDRESS, TITLE, TYPE, CONTENT, USER_IP, WRITE_DATE,BUSI_ID, GIVE_STATUS, GIVE_DEPT_ID, GIVE_DEPT_NAME,USER_ID,ISANONYMOUS,STATUS,REGION_ID,SXID,SXBM,SXMC,URL) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		String id = Tools.getNewID("mutual");// Tools.getUUID32();//Tools.getNewID("mutual");
		pSet.put("id", id);
		int i = this.executeUpdate(sql, new Object[] { id, // id生成器生成
				"0", "0", (String) pSet.getParameter("DEPART_ID"), (String) pSet.getParameter("DEPART_NAME"),
				(String) pSet.getParameter("USERNAME"), (String) pSet.getParameter("PHONE"),
				(String) pSet.getParameter("EMAIL"), (String) pSet.getParameter("ADDRESS"),
				(String) pSet.getParameter("TITLE"), (String) pSet.getParameter("TYPE"),
				(String) pSet.getParameter("CONTENT"), pSet.getRemoteAddr(),
				CommonUtils.getInstance().parseStringToTimeStamp(
						Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS),
				(String) pSet.getParameter("BUSI_ID"), "0", (String) pSet.getParameter("GIVE_DEPT_ID"),
				(String) pSet.getParameter("GIVE_DEPT_NAME"), pSet.getParameter("USER_ID"),
				(String) pSet.getParameter("ISANONYMOUS"),
				(String) pSet.getParameter("STATUS") != null ? (String) pSet.getParameter("STATUS") : "0",
				SecurityConfig.getString("WebRegion"), (String) pSet.getParameter("SXID"),
				(String) pSet.getParameter("SXBM"), (String) pSet.getParameter("SXMC"),
				(String) pSet.getParameter("URL") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			JSONObject jo = new JSONObject();
			jo.put("id", id);
			ds.setState(StateType.SUCCESS);
			ds.setData(Tools.stringToBytes(jo.toString()));
			// isSendSms=1，有咨询、投诉时需要短信告知部门联系人
			String isSendSms = SecurityConfig.getString("isSendSms");
			if ("1".equals(isSendSms)) {
				pSet.put("sms", "SmsContent4Consult");
				SmsDao.getInstance().sendMessage(pSet);
			}
		}

		if (pSet.get("TYPE").equals("3")) {
			try {
				int state = WebServiceBase.getInstance().commitComplaint(pSet);
				if (200 != state) {
					ds.setMessage("调用第三方推送投诉接口失败！");
				} else {
					ds.setState(StateType.SUCCESS);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ds;
	}

	public DataSet insert(ParameterSet pSet, UserInfo user) {
		DataSet ds = new DataSet();
		ParameterSet userPset = new ParameterSet();
		userPset.setParameter("id", user.getUid());
		DataSet userDs = UserDao.getInstance().getList(userPset);
		JSONObject obj;
		String address = "";
		String email = "";
		if (userDs.getState() == StateType.SUCCESS && userDs.getTotal() > 0) {
			obj = userDs.getRecord(0);
			address = obj.getString("ADDRESS");
			email = obj.getString("EMAIL");
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("无用户数据");
			return ds;
		}
		pSet.setParameter("USERNAME", user.getUserName());
		pSet.setParameter("PHONE", user.getMobile());
		pSet.setParameter("ADDRESS", address);
		pSet.setParameter("EMAIL", email);
		return this.insert(pSet);
	}

	// 统计方法，用于在统计页面输出数据
	public DataSet getCount(ParameterSet pSet) {
		DataSet ds;
		String sql = "SELECT A.DEPART_NAME,COUNT(*) AS TOTAL,SUM(CASE WHEN A.STATUS='0' THEN 1 END) AS NOTDEAL,SUM(CASE WHEN A.STATUS='1' THEN 1 END) AS DEAL ,SUM(A.USEFUL) AS USEFUL_TOTAL,SUM(A.NOTUSEFUL) AS NOTUSEFUL_TOTAL,"
				+ "SUM(CASE WHEN A.STATUS='0' THEN 1 END) AS SATISFY_0,SUM(CASE WHEN A.STATUS='1' THEN 1 END) AS SATISFY_1,SUM(CASE WHEN A.STATUS='2' THEN 1 END) AS SATISFY_2 FROM GUESTBOOK A GROUP BY A.DEPART_NAME";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		ds = this.executeDataset(sql);
		return ds;
	}

	// 用户满意度评价
	public DataSet setSatisfy(ParameterSet pSet) {
		String sql = "UPDATE GUESTBOOK SET SATISFY=? WHERE ID=? AND PHONE=?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, new Object[] { (String) pSet.getParameter("SATISFY"),
				(String) pSet.getParameter("ID"), (String) pSet.getParameter("PHONE"), });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	// 其他用户评价操作
	public DataSet setUseful(ParameterSet pSet) {
		DataSet ds = new DataSet();
		// String id=String.valueOf(pSet.getParameter("ID"));
		// String is_useful=String.valueOf(pSet.getParameter("USE"));
		if (pSet.getParameter("USE").equals("1")) {
			String sql = "UPDATE GUESTBOOK SET USEFUL=USEFUL+1 WHERE ID=?";
			int i = this.executeUpdate(sql, new Object[] { (String) pSet.getParameter("ID") });
			if (i == 0) {
				ds.setState(StateType.FAILT);
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} else if (pSet.getParameter("USE").equals("0")) {
			String sql = "UPDATE GUESTBOOK SET NOTUSEFUL=NOTUSEFUL+1 WHERE ID=?";
			int i = this.executeUpdate(sql, new Object[] { (String) pSet.getParameter("ID") });
			if (i == 0) {
				ds.setState(StateType.FAILT);
			} else {
				ds.setState(StateType.SUCCESS);
			}
		}
		return ds;
	}

	// 获取热门咨询服务列表
	public DataSet getHotConsult(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "SELECT G.ID,G.TYPE,G.TITLE,G.CONTENT,G.WRITE_DATE,G.DEAL_RESULT,G.DEPART_ID,G.DEPART_NAME,G.STATUS"
					+ " FROM GUESTBOOK G LEFT OUTER JOIN (SELECT B.SXBM,COUNT(B.SXBM) CNT FROM BUSINESS_INDEX B RIGHT OUTER JOIN GUESTBOOK G ON G.BUSI_ID=B.SXBM GROUP BY B.SXBM) B"
					+ " ON G.BUSI_ID=B.SXBM LEFT OUTER JOIN POWER_BASE_INFO P ON G.BUSI_ID=P.CODE WHERE G.CHECKS='1' AND G.OPEN='1' AND G.TYPE='2' ORDER BY B.CNT DESC";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 非上班时间网上预约 pan
	public DataSet wsyyinsert(ParameterSet pSet, UserInfo user) {
		String sql = "INSERT INTO FSBSJYY "
				+ "(ID, YYDEPTNAME, USERNAME,USERID, YYDAY, YYTIME, YYRNAME, YYRCARDNO, YYCONTENT, YYRPHONE, DEPTPHONE,CODE,REGIONCODE) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		String id = Tools.getUUID32();// Tools.getNewID("mutual");
		int i = this.executeUpdate(sql,
				new Object[] { id, // id生成器生成
						(String) pSet.getParameter("YYDEPTNAME"), user.getUserName(), user.getUid(),
						(String) pSet.getParameter("YYDAY"), (String) pSet.getParameter("YYTIME"),
						(String) pSet.getParameter("YYRNAME"), (String) pSet.getParameter("YYRCARDNO"),
						(String) pSet.getParameter("YYCONTENT"), (String) pSet.getParameter("YYRPHONE"),
						(String) pSet.getParameter("DEPTPHONE"), (String) pSet.getParameter("CODE"),
						(String) pSet.getParameter("REGIONCODE") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			JSONObject jo = new JSONObject();
			jo.put("id", id);
			ds.setState(StateType.SUCCESS);
			ds.setData(Tools.stringToBytes(jo.toString()));
			// isSendSms=1，有咨询、投诉时需要短信告知部门联系人
			String isSendSms = SecurityConfig.getString("isSendSms");
			if ("1".equals(isSendSms)) {
				pSet.put("sms", "SmsContent4Consult");
				SmsDao.getInstance().sendMessage(pSet);
			}
		}
		return ds;
	}
}
