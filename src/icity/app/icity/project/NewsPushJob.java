package app.icity.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import net.sf.json.JSONObject;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class NewsPushJob extends BaseQueryCommand {
	private static final String icityDataSource = "icityDataSource";

	public void execute(ParameterSet pSet) throws Exception {
		//扫描办件库中需要推送的消息
		pushBussiness(pSet);
		//扫描咨询投诉库中需要推送的消息
		pushGuestbook(pSet);
		//更新pub_mynews表中消息的状态
		updateMynewsStatus(pSet);
		
	}
	private void pushBussiness(ParameterSet pSet) throws Exception {
		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			String sql_business = "select t.sblsh,t.sbxmmc,t.sxid,t.url,t.ucid,t.state from business_index t where t.pushstate = '1' ";
			conn.setAutoCommit(false);
			DataSet ds = DbHelper.query(sql_business, new Object[] {}, conn);
			if (ds != null && ds.getTotal() > 0) {
				String sql_news = "insert into pub_mynews(ID,NEWSID,NAME,URL,STATUS,UCID,TYPE,ITEMID,WRITETIME) values(?,?,?,?,?,?,?,?,?)";
				for (int i = 0; i < ds.getTotal(); i++) {
					JSONObject data = ds.getJAData().getJSONObject(i);
					int j = 0;
					j = DbHelper.update(sql_news,
									new Object[] {
											Tools.getUUID32(),
											data.getString("SBLSH"),
											data.getString("SBXMMC"),
											data.getString("URL") == null ? "": data.getString("URL"),
											"0",
											data.getString("UCID"),
											"01".equals(data.getString("STATE")) ? "05" : data.getString("STATE"),
											data.getString("SXID"),
											CommonUtils.getInstance().parseStringToTimeStamp(
													Tools.formatDate(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),
															CommonUtils.YYYY_MM_DD_HH_mm_SS) },
									conn);
					if(j>0){
						String sql_update = "update business_index t set t.pushstate = '2' where t.sblsh = ?";
						j = DbHelper.update(sql_update, new Object[] {data.get("SBLSH")}, conn);
					}
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} finally {
			DBSource.closeConnection(conn);
		}
	}
	private void pushGuestbook(ParameterSet pSet) throws Exception {
		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			String sql_business = "select t.id,t.title,t.user_id,t.type from guestbook t where pushstate='1' and type in(2,3)";
			conn.setAutoCommit(false);
			DataSet ds = DbHelper.query(sql_business, new Object[] {}, conn);
			if (ds != null && ds.getTotal() > 0) {
				String sql_news = "insert into pub_mynews(ID,NEWSID,NAME,URL,STATUS,UCID,TYPE,WRITETIME) values(?,?,?,?,?,?,?,?)";
				for (int i = 0; i < ds.getTotal(); i++) {
					JSONObject data = ds.getRecord(i);
					int j = 0;
					j = DbHelper.update(sql_news, new Object[] {
							Tools.getUUID32(),data.get("ID"),data.get("TITLE"),"","0",data.get("USER_ID"),
							"2".equals(data.get("TYPE"))?"01":"04",
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS)}, conn);
					if(j>0){
						String sql_update = "update guestbook t set t.pushstate = '2' where t.id = ?";
						j = DbHelper.update(sql_update, new Object[] {data.get("ID")}, conn);
					}
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} finally {
			DBSource.closeConnection(conn);
		}
	}
	private void updateMynewsStatus(ParameterSet pSet) throws Exception {
		Connection conn = DBSource.getConnection(icityDataSource);
		try {
			String sql_news = "select p.newsid from business_index t, pub_mynews p where (t.sblsh=p.newsid and t.pushstate = '3' and p.status ='0')";

			conn.setAutoCommit(false);
			DataSet ds = DbHelper.query(sql_news, new Object[] {}, conn);
			if (ds != null && ds.getTotal() > 0) {
				String sql_update = "update pub_mynews set status = '1' where newsid = ?";
				for (int i = 0; i < ds.getTotal(); i++) {
					JSONObject data = ds.getRecord(i);
					int j = 0;
					j = DbHelper.update(sql_update, new Object[] {data.get("NEWSID")}, conn);
					if(j>0){
						sql_update = "update business_index t set t.pushstate = '2' where t.sblsh = ?";
						j = DbHelper.update(sql_update, new Object[] {data.get("NEWSID")}, conn);
					}
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} finally {
			DBSource.closeConnection(conn);
		}
	}
}

