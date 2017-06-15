package km.app.icity.guestbook;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class ReplyJob extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "ReplyJob";
	private static final String icityDataSource = "icityDataSource";

	public void execute(ParameterSet pSet) throws Exception {
		String temp_str = "";
		Date dt = new Date();
		//String webRegion = SecurityConfig.getString("WebRegion");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 最后的aa表示“上午”或“下午” HH表示24小时制 如果换成hh表示12小时制
		temp_str = sdf.format(dt);
		// 2天前
		HttpClientUtil client = new HttpClientUtil();
		String url2 = HttpUtil.formatUrl(
				SecurityConfig.getString("webSiteUrl") + "/web/c/getWorkDate?startTime=" + temp_str + "&timeout=-2");
		JSONObject json2 = JSONObject.fromObject(client.getResult(url2, ""));

		// 3天前
		String url3 = HttpUtil.formatUrl(
				SecurityConfig.getString("webSiteUrl") + "/web/c/getWorkDate?startTime=" + temp_str + "&timeout=-3");
		JSONObject json3 = JSONObject.fromObject(client.getResult(url3, ""));

		Timestamp j2 = null;
		try {
			j2 = CommonUtils_api.getInstance().parseStringToTimestamp(json2.getString("data"),
					CommonUtils_api.YYYY_MM_DD);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Timestamp j3 = null;
		try {
			j3 = CommonUtils_api.getInstance().parseStringToTimestamp(json3.getString("data"),
					CommonUtils_api.YYYY_MM_DD);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			conn.setAutoCommit(false);
			String sql = "update guestbook t set t.warn='2' where to_char(t.write_date,'yyyy-MM-dd')=to_char(?,'yyyy-MM-dd') and t.status='0' and t.warn!='3' and t.type in ('2','3')";
			DbHelper.update(sql, new Object[] { j2 }, conn);
			sql = "update guestbook t set t.warn='3' where to_char(t.write_date,'yyyy-MM-dd')<=to_char(?,'yyyy-MM-dd') and t.status='0' and t.warn!='3' and t.type in ('2','3')";
			DbHelper.update(sql, new Object[] { j3 }, conn);
			conn.commit();
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
