package huaihua.app.icity.guestbook;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.guestbook.SmsDao;
import app.uc.UserDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

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

	// 聊城办事咨询查询
	public DataSet getList2(ParameterSet pSet) {
		String sql = "SELECT * FROM GUESTBOOK G where 1=1 ";
		String REGION_ID = SecurityConfig.getString("WebRegion");
		String TYPE = (String) pSet.getParameter("TYPE");
		String DEPART_NAME = (String) pSet.getParameter("DEPART_NAME");
		String TITLE = (String) pSet.getParameter("TITLE");
		int start = (Integer) pSet.getParameter("start");
		int limit = (Integer) pSet.getParameter("limit");
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			sql += "AND (G.TYPE='" + TYPE + "')";
			sql += "AND (G.REGION_ID='" + REGION_ID + "')";
			if (StringUtil.isNotEmpty(DEPART_NAME)) {
				sql += "AND (G.DEPART_NAME ='" + DEPART_NAME + "')";
			}
			if (StringUtil.isNotEmpty(TITLE)) {
				sql += "AND (G.TITLE ='" + TITLE + "')";
			}
			sql += " ORDER BY WRITE_DATE DESC";
			DataSet ds = this.executeDataset(sql, start, limit, null);
			return ds;
		}
	}

	// 聊城
	public DataSet getSurpervise(ParameterSet pSet) {
		// String searchTitle = (String)pSet.remove("SEARCH_TITLE");
		String sxmc = (String) pSet.getParameter("sxmc");
		String title = (String) pSet.getParameter("title");
		String deptId = (String) pSet.getParameter("deptid");
		String deptName = (String) pSet.getParameter("DEPART_NAME");
		String sql = "SELECT * FROM GUESTBOOK G where 1=1 and G.open='1' ";
		if (StringUtil.isNotEmpty(title)) {
			sql += "AND (G.CONTENT like '%" + title + "%' OR G.TITLE like '%" + title + "%')";
		}
		if (StringUtil.isNotEmpty(deptId)) {
			sql += "AND (G.DEPART_ID like '%" + deptId + "%')";
		}
		if (StringUtil.isNotEmpty(deptName)) {
			sql += "AND (G.DEPART_NAME like '%" + deptName + "%')";
		}
		if (StringUtil.isNotEmpty(sxmc)) {
			sql += "AND (G.SXMC like '%" + sxmc + "%')";
		}
		String REGION_ID = SecurityConfig.getString("WebRegion");
		sql += "AND (G.REGION_ID like '%" + REGION_ID + "%')";
		sql += "AND G.TYPE in (2,3) ";
		// sql+=" AND STATUS ="+status;
		sql += " ORDER BY G.WRITE_DATE DESC";
		int start = pSet.getPageStart();//(Integer) pSet.getParameter("start");
		int limit = pSet.getPageLimit();//(Integer) pSet.getParameter("limit");
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			DataSet ds = this.executeDataset(sql, start, limit, null);
			return ds;
		}
	}

	// 聊城按照部门id查询我的咨询
	public DataSet getListByDept(ParameterSet pSet) {
		String sql = "SELECT * FROM GUESTBOOK where GUESTBOOK.TYPE=? AND GUESTBOOK.DEPART_NAME=? AND  REGION_ID=?";
		sql += " ORDER BY WRITE_DATE DESC";
		int start = (Integer) pSet.getParameter("start");
		int limit = (Integer) pSet.getParameter("limit");
		String REGION_ID = SecurityConfig.getString("WebRegion");
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			DataSet ds = this.executeDataset(sql, start, limit, new Object[] { (String) pSet.getParameter("TYPE"),
					(String) pSet.getParameter("DEPART_NAME"), REGION_ID });
			return ds;
		}
	}

	// 聊城按照id查询具体的咨询信息
	public DataSet getMatterInfo(ParameterSet pSet) {
		String sql = "SELECT * FROM GUESTBOOK where GUESTBOOK.ID=? ";
		DataSet ds = this.executeDataset(sql, new Object[] { (String) pSet.getParameter("ID") });
		return ds;
	}

	public DataSet insertInfo(ParameterSet pSet) {
		String sql = "INSERT INTO GUESTBOOK "
				+ "(ID, TYPE, DEPART_NAME, USERNAME, PHONE, ADDRESS, TITLE, CONTENT,REGION_ID,WRITE_DATE,USER_ID,"
				+ "DEPART_ID,SXID,SXBM) " + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		String id = Tools.getNewID("mutual");
		int i = this.executeUpdate(sql, new Object[] { id, // id生成器生成
				(String) pSet.getParameter("TYPE"), (String) pSet.getParameter("DEPART_NAME"), (String) pSet.getParameter("USERNAME"),
				(String) pSet.getParameter("PHONE"), (String) pSet.getParameter("ADDRESS"),
				(String) pSet.getParameter("TITLE"), (String) pSet.getParameter("CONTENT"),
				(String) SecurityConfig.getString("WebRegion"),
				CommonUtils.getInstance().parseStringToTimeStamp(
						Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS),
				 (String) pSet.getParameter("USER_ID"),
				(String) pSet.getParameter("DEPART_ID") != null ? (String) pSet.getParameter("DEPART_ID") : "",
				(String) pSet.getParameter("SXID") != null ? (String) pSet.getParameter("SXID") : "",
				(String) pSet.getParameter("SXBM") != null ? (String) pSet.getParameter("SXBM") : "" });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
			ds.setMessage("提交成功");
		}
		return ds;
	}

	public DataSet insertInfo2(ParameterSet pSet) {
		String sql = "INSERT INTO GUESTBOOK "
				+ "(ID, TYPE, DEPART_NAME, USERNAME, PHONE, ADDRESS, TITLE, CONTENT,REGION_ID,WRITE_DATE) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		String id = Tools.getNewID("mutual");
		int i = this.executeUpdate(sql,
				new Object[] { id, // id生成器生成
						"3", (String) pSet.getParameter("DEPART_NAME"), (String) pSet.getParameter("USERNAME"),
						(String) pSet.getParameter("PHONE"), (String) pSet.getParameter("ADDRESS"),
						(String) pSet.getParameter("TITLE"), (String) pSet.getParameter("CONTENT"),
						(String) SecurityConfig.getString("WebRegion"),
						CommonUtils.getInstance().parseStringToTimeStamp(
								Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
								CommonUtils.YYYY_MM_DD_HH_mm_SS) });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
			ds.setMessage("提交成功");
		}
		return ds;
	}

	public DataSet insertInfo3(ParameterSet pSet) {
		String sql = "INSERT INTO GUESTBOOK "
				+ "(ID, TYPE,SXID,SXMC, BUSI_ID,USER_ID, USERNAME, PHONE, ADDRESS, TITLE, CONTENT,REGION_ID,WRITE_DATE,DEPART_NAME,OPEN,COMPLAIN_TYPE) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		String id = Tools.getNewID("mutual");
		int i = this.executeUpdate(sql,
				new Object[] { id, // id生成器生成
						"3", (String) pSet.getParameter("SXID"),
						(String) pSet.getParameter("SXMC"), (String) pSet.getParameter("BUSI_ID"), pSet.getParameter(
								"user_id"),
						(String) pSet.getParameter("USERNAME"), (String) pSet.getParameter("PHONE"),
						(String) pSet.getParameter("ADDRESS"), (String) pSet.getParameter("TITLE"),
						(String) pSet.getParameter("CONTENT"), (String) SecurityConfig.getString("WebRegion"),
						CommonUtils.getInstance().parseStringToTimeStamp(
								Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
								CommonUtils.YYYY_MM_DD_HH_mm_SS),
						(String) pSet.getParameter("DEPART_NAME"),
						(String) pSet.getParameter("OPEN"),
						(String) pSet.getParameter("COMPLAIN_TYPE")});
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
			ds.setMessage("提交成功");
		}
		return ds;
	}

	public DataSet insert(ParameterSet pSet) {
		String temp_str = "";
		Date dt = new Date();

		// 最后的aa表示“上午”或“下午” HH表示24小时制 如果换成hh表示12小时制
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		temp_str = sdf.format(dt);

		// 获取超期2天的日期
		HttpClientUtil client = new HttpClientUtil();
		String url2 = HttpUtil.formatUrl(
				SecurityConfig.getString("webSiteUrl") + "/web/c/getWorkDate?startTime=" + temp_str + "&timeout=2");
		JSONObject json2 = JSONObject.fromObject(client.getResult(url2, ""));

		Timestamp j2 = null;
		try {
			j2 = CommonUtils_api.getInstance().parseStringToTimestamp(json2.getString("data"),
					CommonUtils_api.YYYY_MM_DD);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "INSERT INTO GUESTBOOK "
				+ "(ID, WARN, CHECKS, DEPART_ID, DEPART_NAME, USERNAME, PHONE, EMAIL, ADDRESS, TITLE, TYPE, CONTENT, "
				+ "USER_IP, WRITE_DATE,BUSI_ID, GIVE_STATUS, GIVE_DEPT_ID, GIVE_DEPT_NAME,USER_ID,ISANONYMOUS,"
				+ "STATUS,REGION_ID,SXID,SXBM,SXMC,URL,DESIGN,PLEASE,ZJHM,ACCEPT,ACCEPTTIMEOUT) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		String id = Tools.getNewID("mutual");// Tools.getUUID32();//Tools.getNewID("mutual");
		int i = 0;
		try {
			i = this.executeUpdate(sql,
					new Object[] { id, // id生成器生成
							"0", "0", (String) pSet.getParameter("DEPART_ID"),
							(String) pSet.getParameter("DEPART_NAME"), (String) pSet.getParameter("USERNAME"),
							(String) pSet.getParameter("PHONE"), (String) pSet.getParameter("EMAIL"),
							(String) pSet.getParameter("ADDRESS"), (String) pSet.getParameter("TITLE"),
							(String) pSet.getParameter("TYPE"), (String) pSet.getParameter("CONTENT"),
							pSet.getRemoteAddr(),
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
							(String) pSet.getParameter("BUSI_ID"), "0", (String) pSet.getParameter("GIVE_DEPT_ID"),
							(String) pSet.getParameter("GIVE_DEPT_NAME"), pSet.getParameter("USER_ID"),
							(String) pSet.getParameter("ISANONYMOUS"),
							(String) pSet.getParameter("STATUS") != null ? (String) pSet.getParameter("STATUS") : "0",
							SecurityConfig.getString("WebRegion"), (String) pSet.getParameter("SXID"),
							(String) pSet.getParameter("SXBM"), (String) pSet.getParameter("SXMC"),
							(String) pSet.getParameter("URL"), (String) pSet.getParameter("DESIGN"),
							(String) pSet.getParameter("PLEASE"), (String) pSet.getParameter("ZJHM"), "0", j2 });
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public DataSet insert(ParameterSet pSet, UserInfo user) {
		DataSet ds = new DataSet();
		ParameterSet userPset = new ParameterSet();
		userPset.setParameter("id", user.getUid());
		DataSet userDs = UserDao.getInstance().getList(userPset);
		//JSONObject obj;
		if (userDs.getState() == StateType.SUCCESS && userDs.getTotal() > 0) {
			//obj = userDs.getRecord(0);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("无用户数据");
			return ds;
		}
		pSet.setParameter("USER_ID", user.getUid());
		pSet.setParameter("USERNAME", user.getUserName());
		pSet.setParameter("PHONE", user.getMobile());
		/*
		 * 暂时屏蔽，如果登录的邮箱为空，则会清空原有邮箱 pSet.setParameter("ADDRESS", address);
		 * pSet.setParameter("EMAIL", email);
		 */
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

	/**
	 * 获取咨询投诉列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String region_id = SecurityConfig.getString("WebRegion");
			String type = (String) pSet.get("type");
			String sql = "select t.id,t.title,t.status,t.write_date,open from guestbook t where t.type = '" + type + "'"
					+ " and t.region_id = '" + region_id + "' order by t.write_date desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return ds;
	}

	public DataSet getGuestBookList_huifu(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String region_id = SecurityConfig.getString("WebRegion");
			String type = (String) pSet.get("type");
			String id = (String) pSet.get("id");
			String sql = "select t.id,t.title,t.status,t.write_date,deal_result from guestbook t where t.type = '"
					+ type + "'" + " and t.region_id = '" + region_id + "' and t.id='" + id
					+ "' order by t.write_date desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return ds;
	}

	/**
	 * 获取手机列表（舟山）
	 * 
	 * @param pSet
	 * @return
	 */
	public String getPhoneStr(ParameterSet pSet) {
		// String str = new String();
		StringBuffer str = new StringBuffer();
		DataSet ds = new DataSet();
		try {
			String region_code = "";

			if (pSet.get("channel") != null) {
				region_code = (String) pSet.get("WebRegion");
			} else {
				region_code = SecurityConfig.getString("WebRegion");
			}

			String type_code = (String) pSet.get("TYPE");
			String organ_code = (String) pSet.get("organ_code");
			String sql = "select t.phone from pub_msg_config t where t.type_code = '" + type_code + "'"
					+ " and t.organ_code = '" + organ_code + "'" + " and t.region_code = '" + region_code + "'";

			// 外网申报根据事项ID筛选
			if ("20".equals(type_code)) {
				String itemCode = (String) pSet.get("itemCode");
				sql += " and t.item_code like '%" + itemCode + "%' ";
			}
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}

			if (ds != null) {

				for (int i = 0; i < ds.getTotal(); i++) {

					if (i == ds.getTotal() - 1) {
						str.append(ds.getRecord(i).getString("PHONE"));
					} else {
						str.append(ds.getRecord(i).getString("PHONE") + ",");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return str.toString();
	}

	/*
	 * 淄博插入投诉评价
	 */
	public DataSet insertStarLevel(ParameterSet pSet) {
		String sql = "update GUESTBOOK set satisfy=?,SATISFYCONTENT=?,SATISFYTIME=sysdate where id=?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, new Object[] { (Integer) pSet.getParameter("satisfy"),
				(String) pSet.getParameter("satisfycontent"), (String) pSet.getParameter("id") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
			_log.error(ds.getMessage());
		} else {
			ds.setState(StateType.SUCCESS);
			ds.setMessage("评价成功");
		}
		return ds;
	}
	
	public DataSet getBusinessAsk(ParameterSet pSet){
		String sql="select g.type,g.username,g.replayer,g.sxmc,g.phone,g.email,g.address,g.write_date,g.open,g.checks,g.warn,g.title,g.content,g.status,g.deal_result,g.deal_date,(case when g.deal_result is null then 0 else 1 end) reply from guestbook g where g.type in (2,3) ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if(start==-1||limit==-1){
			return this.executeDataset(sql);
		}else{
			return this.executeDataset(sql, start, limit,null);
		}
	}	
	
	public DataSet checkidcard(ParameterSet pSet) {
		DataSet ds = new DataSet();
			String sql = "select * from uc_user where card_no=? and account=? ";
			int i = this.executeUpdate(sql, new Object[] { (String) pSet.getParameter("idcard"),(String) pSet.getParameter("account") });
			if (i == 0) {
				ds.setState(StateType.FAILT);
			} else {
				ds.setState(StateType.SUCCESS);
			}
		return ds;
	}
	
	public DataSet updateidcard(ParameterSet pSet) {
		DataSet ds = new DataSet();
			String sql = "UPDATE uc_user SET password=? WHERE card_no=? and account=?";
			int i = this.executeUpdate(sql, new Object[] { (String) pSet.getParameter("pass"),(String) pSet.getParameter("idcard"),(String) pSet.getParameter("account") });
			if (i == 0) {
				ds.setState(StateType.FAILT);
			} else {
				ds.setState(StateType.SUCCESS);
			}
		return ds;
	}
}
