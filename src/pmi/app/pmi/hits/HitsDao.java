package app.pmi.hits;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.util.UserAgentTool;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

public class HitsDao extends BaseJdbcDao {
	// 设置默认数据库
	private HitsDao() {
		this.setDataSourceName("icityDataSource");
	}

	// 单例模式
	public static HitsDao getInstance() {
		return DaoFactory.getDao(HitsDao.class.getName());
	}
	// 根据IP获取地域信息
	// private static IPSeeker ipSeeker = new
	// IPSeeker("qqwry.dat","WebRoot/public/other/");

	// 批量新增
	public DataSet batchAddHits(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = null;
		try {
			List<HitsBean> hitsBeans = (ArrayList<HitsBean>) pSet.remove("hitsBeans");
			if (hitsBeans != null && hitsBeans.size() > 0) {
				int size = hitsBeans.size();
				Object[][] params = new Object[size][12];
				for (int i = 0; i < size; i++) {
					String user_agent = hitsBeans.get(i).getUser_agent();
					params[i][0] = Tools.getUUID32();
					params[i][1] = hitsBeans.get(i).getAppid();
					params[i][2] = hitsBeans.get(i).getWebname();
					params[i][3] = hitsBeans.get(i).getWebregion();
					params[i][4] = hitsBeans.get(i).getwebrank();
					params[i][5] = hitsBeans.get(i).getVisittime();
					params[i][6] = UserAgentTool.getInstance().checkBrowse(user_agent);// 浏览器
					params[i][7] = hitsBeans.get(i).getVisitedtitle();
					params[i][8] = hitsBeans.get(i).getReferer();
					params[i][9] = hitsBeans.get(i).getUserid();
					params[i][10] = hitsBeans.get(i).getCatalog();
					params[i][11] = hitsBeans.get(i).getIp();
				}
				String sql = "insert into PUB_HITS(ID,APPID,WEBNAME,WEBREGION,WEBRANK,VISITTIME,BROWSER,VISITEDTITLE,VISITEDURL,USERID,CATALOG,IP)"
						+ " values (?,?,?,?,?,?,?,?,?,?,?,?) ";
				conn = DbHelper.getConnection("icityDataSource");
				conn.setAutoCommit(false);
				DbHelper.batch(sql, params, conn);
				conn.commit();
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
		return ds;
	}

	public DataSet getList(ParameterSet pSet) {
		String sql = "select * from pub_hits ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	/**
	 * 浏览量 访客数 IP数（今天、昨天）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getStastictList(ParameterSet pSet) {
		String app_id = (String) pSet.getParameter("app_id");
		String query = "";
		if (StringUtil.isNotEmpty(app_id)) {
			query = " AND T.APP_ID='" + app_id + "'";
		}
		String sql = "select '今天' TIME,count(*) VISITS,count(distinct t.userid) USERCOUNT,count(distinct t.ip) IPS "
				+ " from PUB_HITS t WHERE to_date(t.create_time) = trunc(SYSDATE)" + query + " union all "
				+ " select '昨天' TIME,count(*) VISITS,count(distinct t.userid) USERCOUNT,count(distinct t.ip) IPS "
				+ " from PUB_HITS t WHERE to_date(t.create_time) = trunc(SYSDATE-1) " + query;
		// " union all "+
		// " select '本月' TIME,count(*) VISITS,count(distinct t.userid)
		// USERCOUNT,count(distinct t.ip) IPS "+
		// " from PUB_HITS t WHERE to_char(to_date(t.create_time),'yyyymm')=
		// to_char(trunc(Sysdate),'yyyymm') ";
		return this.executeDataset(sql, null);
	}

	// 按小时统计某特定天的数据
	public DataSet getHitsListByOneDay(ParameterSet pSet) {
		DataSet dSet;
		String app_id = (String) pSet.getParameter("app_id");
		String query = "";
		if (StringUtil.isNotEmpty(app_id)) {
			query = " AND T.APP_ID='" + app_id + "'";
		}
		int time = pSet.getParameter("time") == null ? 0 : (Integer.parseInt(pSet.remove("time").toString()));
		StringBuffer sqlBuffer = new StringBuffer();
		// 按小时统计
		sqlBuffer.append(" SELECT TM.TIME TIME,CASE WHEN TR.VISITS IS NULL THEN 0 ELSE TR.VISITS END AS VISITS, ")
				.append(" CASE WHEN TR.USERCOUNT IS NULL THEN 0 ELSE TR.USERCOUNT END AS USERCOUNT, ")
				.append(" CASE WHEN TR.IPS IS NULL THEN 0 ELSE TR.IPS END AS IPS FROM( ")
				.append(" SELECT TO_CHAR(SYSDATE +(25-ROWNUM) / 24,'HH24') AS TIME FROM DUAL ")
				.append(" CONNECT BY ROWNUM <= 24 )TM LEFT OUTER JOIN (SELECT TIME,COUNT(*) VISITS,COUNT(DISTINCT TT.USERID) USERCOUNT,COUNT(DISTINCT TT.IP) IPS FROM ( ")
				.append(" SELECT T.*,TO_CHAR(T.CREATE_TIME,'HH24') TIME FROM PUB_HITS T WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') = TO_CHAR(SYSDATE")
				.append(time == 0 ? "" : time).append(" ,'yyyymmdd') ").append(query)
				.append(") TT GROUP BY TT.TIME ) TR  ON TM.TIME=TR.TIME ORDER BY TM.TIME DESC ");
		String sql = sqlBuffer.toString();
		dSet = this.executeDataset(sql, null);
		return dSet;
	}

	/**
	 * 浏览量 访客数 IP数(按小时/按天)---图表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getHitsListByTimeAll(ParameterSet pSet) {
		DataSet dSet;
		String app_id = (String) pSet.getParameter("app_id");
		int time = pSet.getParameter("time") == null ? 0 : (Integer.parseInt(pSet.remove("time").toString()));
		String timemode = (String) pSet.remove("timemode");
		StringBuffer querybBuffer = new StringBuffer();
		if (time == -6 || time == -29) {
			querybBuffer.append(" WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE").append(time)
					.append(",'yyyymmdd') ");
		} // 最近7天/30天
		if (time == -1 || time == 0) {
			querybBuffer.append(" WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE").append(time == 0 ? "" : time)
					.append(") ");
		} // 今天或昨天
		if (StringUtil.isNotEmpty(app_id)) {
			querybBuffer.append(" AND T.APP_ID = '" + app_id + "'");
		}
		StringBuffer sqlBuffer = new StringBuffer();
		// 按小时统计
		if ("byHour".equals(timemode)) {
			sqlBuffer.append(" SELECT TM.TIME TIME,CASE WHEN TR.VISITS IS NULL THEN 0 ELSE TR.VISITS END AS VISITS, ")
					.append(" CASE WHEN TR.USERCOUNT IS NULL THEN 0 ELSE TR.USERCOUNT END AS USERCOUNT, ")
					.append(" CASE WHEN TR.IPS IS NULL THEN 0 ELSE TR.IPS END AS IPS ").append(" FROM( ")
					.append(" SELECT TO_CHAR(SYSDATE +(25-ROWNUM) / 24,'HH24') AS TIME FROM DUAL ")
					.append(" CONNECT BY ROWNUM <= 24 )TM LEFT OUTER JOIN (SELECT TIME,COUNT(*) VISITS,COUNT(DISTINCT TT.USERID) USERCOUNT,COUNT(DISTINCT TT.IP) IPS FROM ( ")
					.append(" SELECT T.*,TO_CHAR(T.CREATE_TIME,'HH24') TIME FROM PUB_HITS T ").append(querybBuffer)
					.append(" ) TT GROUP BY TT.TIME ) TR  ON TM.TIME=TR.TIME ORDER BY TM.TIME DESC ");
		}
		// 按天统计
		if ("byDay".equals(timemode)) {
			sqlBuffer.append("SELECT TM.TIME TIME,CASE WHEN TR.VISITS IS NULL THEN 0 ELSE TR.VISITS END AS VISITS, ")
					.append(" CASE WHEN TR.USERCOUNT IS NULL THEN 0 ELSE TR.USERCOUNT END AS USERCOUNT, ")
					.append(" CASE WHEN TR.IPS IS NULL THEN 0 ELSE TR.IPS END AS IPS ").append(" FROM (");
			if (time == -6) {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=7 ");
			} // 最近七天
			else if (time == -29) {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=30 ");
			} // 最近30天
			else if (time == -1) {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=1 ");
			} // 昨天
			else {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=1 ");
			} // 今天
			sqlBuffer.append(" ) TM ")
					.append(" LEFT OUTER JOIN (SELECT TIME,COUNT(*) VISITS,COUNT(DISTINCT TT.USERID) USERCOUNT,COUNT(DISTINCT TT.IP) IPS ")
					.append(" FROM (SELECT T.*, TO_CHAR(T.CREATE_TIME, 'yyyy-MM-dd') TIME FROM PUB_HITS T ")
					.append(querybBuffer)
					.append(" ) TT GROUP BY TT.TIME) TR ON TM.TIME = TR.TIME ORDER BY TM.TIME DESC ");
		}
		String sql = sqlBuffer.toString();
		dSet = this.executeDataset(sql, null);
		return dSet;
	}

	/**
	 * 浏览量 访客数 IP数（按小时/按天，今天的时间段特殊处理）----表格
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getHitsListByTime(ParameterSet pSet) {
		DataSet dSet;
		String app_id = (String) pSet.getParameter("app_id");
		int time = pSet.getParameter("time") == null ? 0 : (Integer) pSet.remove("time");
		String timemode = (String) pSet.remove("timemode");
		StringBuffer querybBuffer = new StringBuffer();
		if (time == -6 || time == -29) {
			querybBuffer.append(" WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE").append(time)
					.append(",'yyyymmdd') ");
		} // 最近7天/30天
		if (time == -1 || time == 0) {
			querybBuffer.append(" WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE").append(time == 0 ? "" : time)
					.append(") ");
		} // 今天或昨天
		if (StringUtil.isNotEmpty(app_id)) {
			querybBuffer.append(" AND T.APP_ID = '" + app_id + "'");
		}
		StringBuffer sqlBuffer = new StringBuffer();
		// 按小时统计
		if ("byHour".equals(timemode)) {
			sqlBuffer
					.append(" SELECT TM.TIME||':00-'||TM.TIME||':59' TIME,CASE WHEN TR.VISITS IS NULL THEN 0 ELSE TR.VISITS END AS VISITS, ")
					.append(" CASE WHEN TR.USERCOUNT IS NULL THEN 0 ELSE TR.USERCOUNT END AS USERCOUNT, ")
					.append(" CASE WHEN TR.IPS IS NULL THEN 0 ELSE TR.IPS END AS IPS ")
					.append(" FROM( SELECT TO_CHAR(SYSDATE +(25-ROWNUM) / 24,'HH24') AS TIME FROM DUAL CONNECT BY ROWNUM <= ")
					.append(time == 0 ? " TO_NUMBER(TO_CHAR(SYSDATE, 'HH24'))+1 " : " 24 "); // TO_CHAR(SYSDATE,'HH24')-今天;//"
																								// 24
																								// "-昨天或之前
			sqlBuffer.append(" )TM ")
					.append(" LEFT OUTER JOIN (SELECT TIME,COUNT(*) VISITS,COUNT(DISTINCT TT.USERID) USERCOUNT,COUNT(DISTINCT TT.IP) IPS ")
					.append(" FROM ( SELECT T.*,TO_CHAR(T.CREATE_TIME,'HH24') TIME FROM PUB_HITS T ")
					.append(querybBuffer)
					.append(" ) TT GROUP BY TT.TIME ) TR ON TM.TIME=TR.TIME ORDER BY TM.TIME DESC ");
		}
		// 按天数统计
		if ("byDay".equals(timemode)) {
			sqlBuffer.append("SELECT TM.TIME TIME,CASE WHEN TR.VISITS IS NULL THEN 0 ELSE TR.VISITS END AS VISITS, ")
					.append(" CASE WHEN TR.USERCOUNT IS NULL THEN 0 ELSE TR.USERCOUNT END AS USERCOUNT, ")
					.append(" CASE WHEN TR.IPS IS NULL THEN 0 ELSE TR.IPS END AS IPS ").append(" FROM (");
			if (time == -6) {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=7 ");
			} // 最近七天
			else if (time == -29) {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=30 ");
			} // 最近30天
			else if (time == -1) {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=1 ");
			} // 昨天
			else {
				sqlBuffer.append(
						" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=1 ");
			} // 今天
			sqlBuffer.append(" ) TM ")
					.append(" LEFT OUTER JOIN (SELECT TIME,COUNT(*) VISITS,COUNT(DISTINCT TT.USERID) USERCOUNT,COUNT(DISTINCT TT.IP) IPS ")
					.append(" FROM (SELECT T.*, TO_CHAR(T.CREATE_TIME, 'yyyy-MM-dd') TIME FROM PUB_HITS T ")
					.append(querybBuffer)
					.append(" ) TT GROUP BY TT.TIME) TR ON TM.TIME = TR.TIME ORDER BY TM.TIME DESC ");
		}
		String sql = sqlBuffer.toString();
		dSet = this.executeDataset(sql, null);
		JSONArray array = dSet.getJAData();
		if (array.size() > 0) {
			int pv = 0, uv = 0, ips = 0;
			for (int i = 0; i < array.size(); i++) {
				JSONObject jo = (JSONObject) array.get(i);
				if (jo != null) {
					pv += jo.getInt("VISITS");
					uv += jo.getInt("USERCOUNT");
					ips += jo.getInt("IPS");
				}
			}
			JSONObject cjo = new JSONObject();
			cjo.put("TIME", "汇总");
			cjo.put("VISITS", pv);
			cjo.put("USERCOUNT", uv);
			cjo.put("IPS", ips);
			array.add(cjo);
		}
		dSet.setData(Tools.stringToBytes(array.toString()));
		return dSet;
	}

	/**
	 * 浏览器 浏览量占比 访问次数 访客数(UV) 新访客数 新访客比率 IP数
	 */
	public DataSet getStastictListByVisit(ParameterSet pSet) {
		DataSet ds;
		String app_id = (String) pSet.getParameter("app_id");
		String stastictItem = pSet.remove("stastictitem").toString();
		String whereQuery = "";
		if (pSet.getParameter("time") != null) {
			int time = (Integer) pSet.remove("time");
			if (time == -6 || time == -29) {
				whereQuery = " WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE" + time + ",'yyyymmdd')";
			} // 最近7天/30天
			else if (time == -1) {
				whereQuery = " WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE-1)";
			} // 昨天
			else if (time == 0) {
				whereQuery = " WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE)";
			} // 今天
			else {
				whereQuery = "WHERE 1=1 ";
			}
		}
		if (StringUtil.isNotEmpty(app_id)) {
			whereQuery += " AND T.APP_ID='" + app_id + "'";
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT T.").append(stastictItem)
				.append(" STASTICTITEM,TO_CHAR(ROUND(COUNT(*) /" + "(SELECT COUNT(1) FROM PUB_HITS T ")
				.append(whereQuery).append(")*100,2),'FM990.09')||'%' AS PERCENT,")
				.append("COUNT(*) TOTAL,COUNT(DISTINCT T.USERID) USERTOTAL,COUNT(DISTINCT T.IP) IPTOTAL FROM PUB_HITS T ")
				.append(whereQuery).append(" GROUP BY T.").append(stastictItem).append(" ORDER BY PERCENT DESC");
		String sql = sqlBuffer.toString();
		ds = this.executeDataset(sql, null);
		JSONArray array = ds.getJAData();
		if (array != null && array.size() > 0) {
			int pv = 0, uv = 0, ips = 0;
			for (int i = 0; i < array.size(); i++) {
				JSONObject jo = (JSONObject) array.get(i);
				if (jo != null) {
					pv += jo.getInt("TOTAL");
					uv += jo.getInt("USERTOTAL");
					ips += jo.getInt("IPTOTAL");
				}
			}
			JSONObject cjo = new JSONObject();
			cjo.put("STASTICTITEM", "汇总");
			cjo.put("PERCENT", "100%");
			cjo.put("TOTAL", pv);
			cjo.put("USERTOTAL", uv);
			cjo.put("IPTOTAL", ips);
			array.add(cjo);
		}
		ds.setData(Tools.stringToBytes(array!=null?array.toString():""));
		return ds;
	}

	/**
	 * 来源 浏览量占比 访问次数
	 */
	public DataSet getSummaryByVisit(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String app_id = (String) pSet.getParameter("app_id");
		String sql = "";
		String isSummary = pSet.getParameter("isSummary").toString();
		String stastictItem = pSet.remove("stastictitem").toString();
		StringBuffer queryBuffer = new StringBuffer();
		if (pSet.getParameter("time") != null) {
			int time = (Integer) pSet.getParameter("time");
			if (time == -6 || time == -29) {
				queryBuffer
						.append(" WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE" + time + ",'yyyymmdd')");
			} // 最近7天/30天
			else if (time == -1) {
				queryBuffer.append(" WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE-1)");
			} // 昨天
			else if (time == 0) {
				queryBuffer.append(" WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE)");
			} // 今天
			else {
				queryBuffer.append("WHERE 1=1 ");
			}
		}
		if (StringUtil.isNotEmpty(app_id)) {
			queryBuffer.append(" AND T.APP_ID='" + app_id + "'");
		}
		StringBuffer sqlBuffer = new StringBuffer();
		if ("1".equals(isSummary)) {
			sqlBuffer.append("SELECT * FROM ( ");
			sqlBuffer.append("SELECT ").append(stastictItem)
					.append(" STASTICTITEM,TO_CHAR(ROUND(COUNT(*) /" + "(SELECT COUNT(1) FROM PUB_HITS T ")
					.append(queryBuffer).append(")*100,2),'FM990.09')||'%' AS PERCENT,COUNT(*) TOTAL ")
					.append(" FROM PUB_HITS T ").append(queryBuffer).append(" GROUP BY ").append(stastictItem)
					.append(" ORDER BY PERCENT DESC");
			sqlBuffer.append(" ) WHERE ROWNUM<=5");
			sql = sqlBuffer.toString();
			ds = this.executeDataset(sql, null);
		} else {
			sqlBuffer.append("SELECT ").append(stastictItem)
					.append(" STASTICTITEM,TO_CHAR(ROUND(COUNT(*) /" + "(SELECT COUNT(1) FROM PUB_HITS T ")
					.append(queryBuffer).append(")*100,2),'FM990.09')||'%' AS PERCENT,COUNT(*) TOTAL ")
					.append(" FROM PUB_HITS T ").append(queryBuffer).append(" GROUP BY ").append(stastictItem)
					.append(" ORDER BY PERCENT DESC");
			sql = sqlBuffer.toString();
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql, null);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		}
		return ds;
	}

	/**
	 * 地域 访问次数 访客数(UV) 新访客数 新访客比率 IP数
	 */
	public DataSet getStastictListByArea(ParameterSet pSet) {
		DataSet ds;
		String app_id = (String) pSet.getParameter("app_id");
		String whereQuery = "";
		if (pSet.getParameter("time") != null) {
			int time = (Integer) pSet.remove("time");
			if (time == -6 || time == -29) {
				whereQuery = " WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE" + time + ",'yyyymmdd')";
			} // 最近7天/30天
			else if (time == -1) {
				whereQuery = " WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE-1)";
			} // 昨天
			else if (time == 0) {
				whereQuery = " WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE)";
			} // 今天
			else {
				whereQuery = "WHERE 1=1 ";
			}
		}
		if (StringUtil.isNotEmpty(app_id)) {
			whereQuery += " AND T.APP_ID='" + app_id + "'";
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" SELECT DISTINCT A.COUNTRY,A.CITY,A.TOTAL,A.USERTOTAL,A.IPTOTAL FROM (");
		sqlBuffer
				.append(" SELECT T.COUNTRY,'' CITY,COUNT(*) TOTAL,COUNT(DISTINCT T.USERID) USERTOTAL,COUNT(DISTINCT T.IP) IPTOTAL FROM PUB_HITS T ")
				.append(whereQuery).append(" GROUP BY T.COUNTRY ");
		sqlBuffer.append(" UNION ALL ");
		sqlBuffer
				.append(" SELECT T.COUNTRY,T.CITY,COUNT(*) TOTAL,COUNT(DISTINCT T.USERID) USERTOTAL,COUNT(DISTINCT T.IP) IPTOTAL FROM PUB_HITS T ")
				.append(whereQuery).append(" GROUP BY T.COUNTRY,T.CITY ");
		sqlBuffer.append(" )A ORDER BY A.COUNTRY ASC,A.CITY DESC ");
		String sql = sqlBuffer.toString();
		ds = this.executeDataset(sql, null);
		JSONArray array = ds.getJAData();
		if (array != null && array.size() > 0) {
			int pv = 0, uv = 0, ips = 0;
			for (int i = 0; i < array.size(); i++) {
				JSONObject jo = (JSONObject) array.get(i);
				if (jo != null) {
					if ("".equals(jo.getString("CITY"))) {
						pv += jo.getInt("TOTAL");
						uv += jo.getInt("USERTOTAL");
						ips += jo.getInt("IPTOTAL");
					}
				}
			}
			JSONObject cjo = new JSONObject();
			cjo.put("COUNTRY", "汇总");
			cjo.put("CITY", "");
			cjo.put("TOTAL", pv);
			cjo.put("USERTOTAL", uv);
			cjo.put("IPTOTAL", ips);
			array.add(cjo);
		}
		ds.setData(Tools.stringToBytes(array!=null?array.toString():""));
		return ds;
	}

	/**
	 * 来源 访问次数 访客数(UV) 新访客数 新访客比率 IP数
	 */
	public DataSet getStastictListBySource(ParameterSet pSet) {
		DataSet ds;
		String app_id = (String) pSet.getParameter("app_id");
		String whereQuery = "";
		if (pSet.getParameter("time") != null) {
			int time = (Integer) pSet.remove("time");
			if (time == -6 || time == -29) {
				whereQuery = " WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE" + time + ",'yyyymmdd')";
			} // 最近7天/30天
			else if (time == -1) {
				whereQuery = " WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE-1)";
			} // 昨天
			else if (time == 0) {
				whereQuery = " WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE)";
			} // 今天
			else {
				whereQuery = "WHERE 1=1 ";
			}
		}
		if (StringUtil.isNotEmpty(app_id)) {
			whereQuery += " AND T.APP_ID='" + app_id + "'";
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" SELECT DISTINCT A.REFER_TYPE,A.REFERER,A.TOTAL,A.USERTOTAL,A.IPTOTAL FROM (");
		sqlBuffer
				.append(" SELECT T.REFER_TYPE,'' REFERER,COUNT(*) TOTAL,COUNT(DISTINCT T.USERID) USERTOTAL,COUNT(DISTINCT T.IP) IPTOTAL FROM PUB_HITS T ")
				.append(whereQuery).append(" GROUP BY T.REFER_TYPE ");
		sqlBuffer.append(" UNION ALL ");
		sqlBuffer
				.append(" SELECT T.REFER_TYPE,T.REFERER REFERER,COUNT(*) TOTAL,COUNT(DISTINCT T.USERID) USERTOTAL,COUNT(DISTINCT T.IP) IPTOTAL FROM PUB_HITS T ")
				.append(whereQuery).append(" GROUP BY T.REFER_TYPE,T.REFERER ");
		sqlBuffer.append(" )A ORDER BY A.REFER_TYPE ASC,A.REFERER DESC ");
		String sql = sqlBuffer.toString();
		ds = this.executeDataset(sql, null);
		JSONArray array = ds.getJAData();
		if (array != null && array.size() > 0) {
			int pv = 0, uv = 0, ips = 0;
			for (int i = 0; i < array.size(); i++) {
				JSONObject jo = (JSONObject) array.get(i);
				if (jo != null) {
					if ("".equals(jo.getString("REFERER"))) {
						pv += jo.getInt("TOTAL");
						uv += jo.getInt("USERTOTAL");
						ips += jo.getInt("IPTOTAL");
					}
				}
			}
			JSONObject cjo = new JSONObject();
			cjo.put("REFER_TYPE", "汇总");
			cjo.put("REFERER", "");
			cjo.put("TOTAL", pv);
			cjo.put("USERTOTAL", uv);
			cjo.put("IPTOTAL", ips);
			array.add(cjo);
		}
		ds.setData(Tools.stringToBytes(array == null ? "" : array.toString()));
		return ds;
	}
}
