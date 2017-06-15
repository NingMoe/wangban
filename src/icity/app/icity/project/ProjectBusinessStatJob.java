package app.icity.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class ProjectBusinessStatJob extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "ProjectBusinessStatCmd";
	private static final String icityDataSource = "icityDataSource";
	private static final int FD_OFTHISMONTH = 1; // 本月第一天
	private static final int FD_OFLASTMONTH = 3; // 上月第一天
	private static final int LD_OFLASTMONTH = 4; // 上月最后一天
	private static final int TODAY = 5; // 今天
	private static final int YESTERDAY = 6; // 昨天

	public void execute(ParameterSet pSet) throws Exception {
		String sql = "SELECT COUNT(1) LJSJ," + " sum(case when BJSJ is not null then 1 else 0 end) LJBJ,"
				+ " sum(case when SBSJ >= ? and SBSJ <= ? then 1 else 0 end) SYSJ,"
				+ " sum(case when BJSJ >= ? and BJSJ <= ? then 1 else 0 end) SYBJ,"
				+ " sum(case when SBSJ >= ? and SBSJ <= ? then 1 else 0 end) BYSJ,"
				+ " sum(case when BJSJ >= ? and BJSJ <= ? then 1 else 0 end) BYBJ,"
				+ " sum(case when SBSJ >= ? and SBSJ <= ? then 1 else 0 end) ZRSJ,"
				+ " sum(case when BJSJ >= ? and BJSJ <= ? then 1 else 0 end) ZRBJ,"
				+ " sum(case when SBSJ >= ? and SBSJ <= ? then 1 else 0 end) JRSJ,"
				+ " sum(case when BJSJ >= ? and BJSJ <= ? then 1 else 0 end) JRBJ"
				+ " FROM BUSINESS_INDEX WHERE STATE > '-1' AND XZQHDM = ?";

		String webRegion = SecurityConfig.getString("WebRegion");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// 上个月第一天的日期
		Timestamp fdlm = new Timestamp(
				sdf.parse(ProjectBusinessStatCmd.getOneDay(Calendar.getInstance(), FD_OFLASTMONTH) + " 00:00:00")
						.getTime());
		// 上个月最后一天的日期
		Timestamp ldlm = new Timestamp(
				sdf.parse(ProjectBusinessStatCmd.getOneDay(Calendar.getInstance(), LD_OFLASTMONTH) + " 23:59:59")
						.getTime());

		// 本月第一天的日期
		Timestamp fdtm = new Timestamp(
				sdf.parse(ProjectBusinessStatCmd.getOneDay(Calendar.getInstance(), FD_OFTHISMONTH) + " 00:00:00")
						.getTime());
		// 今天日期
		String todayStr = ProjectBusinessStatCmd.getOneDay(Calendar.getInstance(), TODAY);
		Timestamp todayF = new Timestamp(sdf.parse(todayStr + " 00:00:00").getTime());
		Timestamp todayT = new Timestamp(sdf.parse(todayStr + " 23:59:59").getTime());
		// 昨天日期
		String yesterdayStr = ProjectBusinessStatCmd.getOneDay(Calendar.getInstance(), YESTERDAY);
		Timestamp yesterdayF = new Timestamp(sdf.parse(yesterdayStr + " 00:00:00").getTime());
		Timestamp yesterdayT = new Timestamp(sdf.parse(yesterdayStr + " 23:59:59").getTime());

		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			conn.setAutoCommit(false);
			DataSet ds = DbHelper.query(sql, new Object[] { fdlm, ldlm, fdlm, ldlm, fdtm, todayT, fdtm, todayT,
					yesterdayF, yesterdayT, yesterdayF, yesterdayT, todayF, todayT, todayF, todayT, webRegion }, conn);
			if (ds != null && ds.getTotal() > 0) {
				sql = "delete from project_busyinfo where xzqhdm = ? and type = ?";
				DbHelper.update(sql, new Object[] { webRegion, "getBusinessStat" }, conn);

				sql = "insert into project_busyinfo(id,stattime,xzqhdm,type,content) values(?,?,?,?,?)";
				System.out.println("businessStat ： " + ds.getRecord(0).toString());
				int j = DbHelper.update(sql,
						new Object[] { Tools.getUUID32(),
								CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS),
								webRegion, "getBusinessStat", ds.getRecord(0).toString() },
						conn);
				if (j > 0) {
					String key = "icity_businessStat_" + webRegion;
					CacheManager.evict(CACHE_KEY_FLAG, key);
				}
				conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
