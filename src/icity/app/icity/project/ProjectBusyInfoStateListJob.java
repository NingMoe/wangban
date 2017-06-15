package app.icity.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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

public class ProjectBusyInfoStateListJob extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "ProjectBusinessStatCmd";
	private static final String icityDataSource = "icityDataSource";

	public void execute(ParameterSet pSet) throws Exception {
		DataSet ds;
		String webRegion = SecurityConfig.getString("WebRegion");
		Object[] param = null;
		String timerange = (String) pSet.remove("timerange");
		String sql = "select b.sblsh,b.sxbm,b.sbsj,b.slsj,b.sbxmmc,b.slztdm,b.bjsj,b.bjjgdm,b.lqsj,b.state,b.sxmc,b.sjdw as dept_name,b.sjdw as dept_name_short from business_index b where b.xzqhdm = ? order by b.sbsj desc";
		param = new Object[] { webRegion };
		if ("on".equalsIgnoreCase(timerange)) {
			sql = "select b.sblsh,b.sxbm,b.sbsj,b.slsj,b.sbxmmc,b.slztdm,b.bjsj,b.bjjgdm,b.lqsj,b.state,b.sxmc,b.sjdw as dept_name,b.sjdw as dept_name_short from business_index b where b.sbsj >= ? and b.sbsj < ? and b.xzqhdm = ? order by b.sbsj desc";
			Calendar cr = Calendar.getInstance();
			cr.set(Calendar.HOUR, 0);
			cr.set(Calendar.MINUTE, 0);
			cr.set(Calendar.SECOND, 0);
			Timestamp tDate = new Timestamp(cr.getTimeInMillis());
			cr.add(Calendar.MONTH, -1);
			Timestamp fDate = new Timestamp(cr.getTimeInMillis());
			param = new Object[] { fDate, tDate, webRegion };
		}

		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			conn.setAutoCommit(false);
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			start = start == -1 ? 0 : start;
			limit = limit == -1 ? 50 : limit;
			ds = DbHelper.query(sql, start, limit, param, conn, "icityDataSource");
			if (ds != null && ds.getTotal() > 0) {
				sql = "delete from project_busyinfo where xzqhdm = ? and type = ?";
				DbHelper.update(sql, new Object[] { webRegion, "getBusinessListTab" }, conn);

				sql = "insert into project_busyinfo(id,stattime,xzqhdm,type,content) values(?,?,?,?,?)";
				int j = DbHelper.update(sql,
						new Object[] { Tools.getUUID32(),
								CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS),
								webRegion, "getBusinessListTab", ds.getJAData().toString() },
						conn);
				if (j > 0) {
					String key = "getBusinessListTab_" + webRegion;
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
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
