package app.icity.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class StarLevelStatJob extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "GovProjectCmd";
	private static final String icityDataSource = "icityDataSource";

	public void execute(ParameterSet pSet) throws Exception {
		String sql = "select max(SERVICE_CODE) SERVICE_CODE,max(SERVICE_ORG_ID) SERVICE_ORG_ID,count(1) ACCOUNT,"
				+ "count(case when t.STAR_LEVEL=1 then id else null end) ACCOUNT1,"
				+ "count(case when t.STAR_LEVEL>1 and t.STAR_LEVEL<=2 then id else null end) ACCOUNT2,"
				+ "count(case when t.STAR_LEVEL>2 and t.STAR_LEVEL<=3 then id else null end) ACCOUNT3,"
				+ "count(case when t.STAR_LEVEL>3 and t.STAR_LEVEL<=4 then id else null end) ACCOUNT4,"
				+ "count(case when t.STAR_LEVEL>4 and t.STAR_LEVEL<=5 then id else null end) ACCOUNT5,"
				+ "round((count(case when t.STAR_LEVEL=1 then id else null end)+"
				+ "count(case when t.STAR_LEVEL>1 and t.STAR_LEVEL<=2 then id else null end)*2+"
				+ "count(case when t.STAR_LEVEL>2 and t.STAR_LEVEL<=3 then id else null end)*3+"
				+ "count(case when t.STAR_LEVEL>3 and t.STAR_LEVEL<=4 then id else null end)*4+"
				+ "count(case when t.STAR_LEVEL>4 and t.STAR_LEVEL<=5 then id else null end)*5)/count(1),1) STAR_LEVL "
				+ "from STAR_LEVEL_EVALUATION t where t.IS_EVALUATE = '0' group by SERVICE_CODE ";

		Connection conn = DBSource.getConnection(icityDataSource);
		String webRegion = SecurityConfig.getString("WebRegion");
		try {
			conn.setAutoCommit(false);
			DataSet ds = DbHelper.query(sql, new Object[] {}, conn);
			if (ds != null && ds.getTotal() > 0) {
				int len = ds.getTotal();
				String str = "";
				String _sql = "select * from star_level_stat where service_code = ?";
				for (int i = 0; i < len; i++) {
					DataSet _ds = new DataSet();
					_ds = DbHelper.query(_sql,
							new Object[] { ds.getJAData().getJSONObject(i).getString("SERVICE_CODE") }, conn);
					if (_ds.getState() == StateType.SUCCESS && _ds.getTotal() > 0) {
						int ACCOUNT = _ds.getJAData().getJSONObject(0).getInt("ACCOUNT")
								+ ds.getJAData().getJSONObject(i).getInt("ACCOUNT");
						int ACCOUNT1 = _ds.getJAData().getJSONObject(0).getInt("ACCOUNT1")
								+ ds.getJAData().getJSONObject(i).getInt("ACCOUNT1");
						int ACCOUNT2 = _ds.getJAData().getJSONObject(0).getInt("ACCOUNT2")
								+ ds.getJAData().getJSONObject(i).getInt("ACCOUNT2");
						int ACCOUNT3 = _ds.getJAData().getJSONObject(0).getInt("ACCOUNT3")
								+ ds.getJAData().getJSONObject(i).getInt("ACCOUNT3");
						int ACCOUNT4 = _ds.getJAData().getJSONObject(0).getInt("ACCOUNT4")
								+ ds.getJAData().getJSONObject(i).getInt("ACCOUNT4");
						int ACCOUNT5 = _ds.getJAData().getJSONObject(0).getInt("ACCOUNT5")
								+ ds.getJAData().getJSONObject(i).getInt("ACCOUNT5");

						DecimalFormat df = new DecimalFormat("0.0");// 格式化小数，不足的补0
						String STAR_LEVEL = df.format(
								(ACCOUNT1 + ACCOUNT2 * 2 + ACCOUNT3 * 3 + ACCOUNT4 * 4 + ACCOUNT5 * 5) / ACCOUNT);

						sql = "update star_level_stat set STAR_LEVEL=?,STAR_DATE=?,ACCOUNT=?,ACCOUNT1=?,ACCOUNT2=?,ACCOUNT3=?,ACCOUNT4=?,ACCOUNT5=? where SERVICE_CODE=?";
						int a = DbHelper.update(sql,
								new Object[] { Float.parseFloat(STAR_LEVEL),
										CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),
												CommonUtils.YYYY_MM_DD_HH_mm_SS),
										ACCOUNT, ACCOUNT1, ACCOUNT2, ACCOUNT3, ACCOUNT4, ACCOUNT5,
										ds.getJAData().getJSONObject(i).getString("SERVICE_CODE") },
								conn);
						if (a > 0) {
							String key = "starLevelStat_" + webRegion;
							CacheManager.evict(CACHE_KEY_FLAG, key);
						}
						conn.commit();
					} else {

						sql = "insert into star_level_stat (id,star_date,service_code,service_org_id,ACCOUNT,account1,account2,account3,account4,account5,star_level) values(?,?,?,?,?,?,?,?,?,?,?)";
						System.out.println("starLevelStat ： " + ds.getRecord(0).toString());
						int b = DbHelper.update(sql,
								new Object[] { Tools.getUUID32(),
										CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),
												CommonUtils.YYYY_MM_DD_HH_mm_SS),
								ds.getJAData().getJSONObject(i).getString("SERVICE_CODE"),
								ds.getJAData().getJSONObject(i).getString("SERVICE_ORG_ID"),
								ds.getJAData().getJSONObject(i).getInt("ACCOUNT"),
								ds.getJAData().getJSONObject(i).getInt("ACCOUNT1"),
								ds.getJAData().getJSONObject(i).getInt("ACCOUNT2"),
								ds.getJAData().getJSONObject(i).getInt("ACCOUNT3"),
								ds.getJAData().getJSONObject(i).getInt("ACCOUNT4"),
								ds.getJAData().getJSONObject(i).getInt("ACCOUNT5"),
								ds.getJAData().getJSONObject(i).getInt("STAR_LEVL") }, conn);
						if (b > 0) {
							String key = "starLevelStat_" + webRegion;
							CacheManager.evict(CACHE_KEY_FLAG, key);
						}
						conn.commit();
					}

					if (i == len - 1) {
						str += ds.getJAData().getJSONObject(i).getString("SERVICE_CODE");
					} else {
						str += ds.getJAData().getJSONObject(i).getString("SERVICE_CODE") + ",";
					}
				}

				String sql1 = "update star_level_evaluation t set t.is_evaluate = '1' where t.service_code in(?)";

				int a = DbHelper.update(sql1, new Object[] { str }, conn);
				if (a > 0) {
					String key = "starLevelStat_" + webRegion;
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
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
