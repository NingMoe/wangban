package api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import app.icity.sync.UniteUserInterface;
import app.icity.sync.UploadUtil;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.IdGenereator;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;
import core.util.HttpClientUtil;

@SuppressWarnings("deprecation")
public class XngdappImpl extends BaseQueryCommand {
	@SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(XngdappImpl.class);

	public static XngdappImpl getInstance() {
		return DaoFactory.getDao(XngdappImpl.class.getName());
	}
	/**
	 * 获取微信咨询投诉列表 
	 * @param pSet
	 * @return
	 */	
	@SuppressWarnings("deprecation")
	public DataSet getGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String where = (String)pSet.get("where");
			JSONArray params = JSONArray.fromObject(pSet.get("params"));			
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			int start = (page - 1) * limit;
			String sql = null;
			sql = "select t.id,t.depart_name,t.username,t.phone,t.email,t.address,t.title,t.type,t.content,t.status,t.user_ip,t.write_date,t.replayer,t.deal_result,t.deal_date,t.reply_ip,t.checks,t.open,t.warn,t.busi_id,t.busi_type,t.satisfy,t.useful,t.notuseful,t.depart_id,t.isanonymous,t.give_status,t.give_dept_id,t.give_dept_name,t.user_id,t.sms_alert_already, t.sxbm,t.region_id,t.sxmc,t.sxid,t.url from GUESTBOOK t where "+where+" order by t.write_date desc";
			ds = DbHelper.query(sql, start, limit,params.toArray(), conn, "icityDataSource");//
			ds.setState(StateType.SUCCESS);
			ds.setMessage("pageindex:" + page + ",pagesize:" + limit);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

}
