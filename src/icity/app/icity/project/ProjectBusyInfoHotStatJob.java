package app.icity.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;
import core.util.HttpClientUtil;

/**
 * 统计最近一个月热门办理事项
 * 
 * @author li
 *
 */
public class ProjectBusyInfoHotStatJob extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "ProjectBusinessStatCmd";
	private static final String icityDataSource = "icityDataSource";

	public void execute(ParameterSet pSet) throws Exception {
		DataSet ds;
		String sql = "select b.sxmc name, b.sxid code, count(1) sum, b.sjdw dept_name_short, '' guide_url from business_index b where  b.sbsj >= ? and b.sbsj < ? and b.xzqhdm = ? group by b.sxmc, b.sxid,b.sjdw order by count(1) desc";
		
		//福州（按事项code进行统计）
		if("fz".equals(SecurityConfig.getString("AppId"))){
			sql = "select b.sxbm code, count(1) sum, b.sjdw dept_name_short, '' guide_url from business_index b where  b.sbsj >= ? and b.sbsj < ? and b.xzqhdm = ? group by b.sxbm,b.sjdw order by count(1) desc";
		}
		String webRegion = SecurityConfig.getString("WebRegion");
		Calendar cr = Calendar.getInstance();
		cr.set(Calendar.HOUR, 0);
		cr.set(Calendar.MINUTE, 0);
		cr.set(Calendar.SECOND, 0);
		Timestamp tDate = new Timestamp(cr.getTimeInMillis());
		cr.add(Calendar.MONTH, -1);
		Timestamp fDate = new Timestamp(cr.getTimeInMillis());

		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			conn.setAutoCommit(false);
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			start = start == -1 ? 0 : start;
			limit = limit == -1 ? 20 : limit;
			ds = DbHelper.query(sql, start, limit, new Object[] { fDate, tDate, webRegion }, conn, "icityDataSource");
			if (ds != null && ds.getTotal() > 0) {
				
				//福州（按事项code进行统计）
				if("fz".equals(SecurityConfig.getString("AppId"))){
					JSONArray array = new JSONArray();
					for (int i = 0; i < ds.getTotal(); i++) {
						String url = SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemInfoByItemCode?itemCode="+ds.getRecord(i).getString("CODE");
						url = HttpUtil.formatUrl(url);
						HttpClientUtil client = new HttpClientUtil();
						JSONObject item = JSONObject.fromObject(client.getResult(url,""));
						JSONArray itemInfo = item.getJSONArray("ItemInfo");
						JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
						String itemId = itemBasicInfo.getString("ID");
						String name = itemBasicInfo.getString("NAME");
						
						JSONObject record = ds.getRecord(i);
						record.put("ID", itemId);
						record.put("NAME", name);
						array.add(record);
					}
					ds.setRawData(array);
				}
				
				sql = "delete from project_busyinfo where xzqhdm = ? and type = ?";
				DbHelper.update(sql, new Object[] { webRegion, "getBusyInfoHotStat" }, conn);

				sql = "insert into project_busyinfo(id,stattime,xzqhdm,type,content) values(?,?,?,?,?)";
				int j = DbHelper.update(sql,
						new Object[] { Tools.getUUID32(),
								CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS),
								webRegion, "getBusyInfoHotStat", ds.getJAData().toString() },
						conn);
				if (j > 0) {
					String key = "icity_business_hotstat_" + webRegion;
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
