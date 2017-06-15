package plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;

public class Plugin_Util extends BaseQueryCommand {
	private static Log log_ = LogFactory.getLog(Plugin_Util.class);

	public static Plugin_Util getInstance() {
		return DaoFactory.getDao(Plugin_Util.class.getName());
	}

	/**
	 * 获取待发短信列表
	 * 
	 * @return
	 */
	public DataSet getToSendList() {

		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "select t.ID,t.TELEPHONE,t.SMSCONTENT,t.CHANNEL,t.EMAIL,t.NOTICETYPE,to_char(t.SENDTIME,'yyyy-mm-dd hh24:mi:ss') as SENDTIME from PUB_SMS t where t.STATUS in ('0','2') "
					+ "AND not exists(select e.error_sign from PUB_DATAEXCHANGE_ERROR_LOG e "
					+ "where e.error_sign = t.id AND e.send_count>=3)";
			ds = DbHelper.query(sql, new Object[] {}, conn);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				DbHelper.closeConnection(conn);
		}

		return ds;
	}

	/**
	 * 更新短信状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 */
	public void updateSms(String id, String status) {

		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "update PUB_SMS set STATUS =?,UPDATETIME=? where id = ?";
			DbHelper.update(sql, new Object[] { 
					status, 
					CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
					id }, conn);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				DbHelper.closeConnection(conn);
		}
	}

	/**
	 * 提供一个插入推送数据推送失败日志记录的方法
	 * 
	 * @param error_type
	 *            错误数据类型
	 * @param error_name
	 *            错误数据名称
	 * @param error_sign
	 *            错误数据标志（为推送数据项的ID）
	 * @param content
	 *            错误日志内容
	 * @return 返回结果表示当前数据是第 n 次推送失败
	 */
	public int insertErrorLog(String error_type, String error_name,
			String error_sign, String content) {
		int count = 0;
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			content = content.replaceAll("\"", "“");
			content = content.replaceAll("\'", "‘");
			String queryCount = "select t.SEND_COUNT from pub_dataexchange_error_log t where t.error_sign=?";
			DataSet ds = DbHelper.query(queryCount,
					new Object[] { error_sign }, conn);
			String sql = "";
			Object[] param = null;
			if (ds.getTotal() > 0) {
				count = Integer.parseInt(ds.getRecord(0)
						.getString("SEND_COUNT"));
				count = count + 1;
				if(count<=3){
					sql = "UPDATE PUB_DATAEXCHANGE_ERROR_LOG set CONTENT=?,CREATE_TIME=?,SEND_COUNT=? where ERROR_SIGN=?";
					param = new Object[] {
							content,
							CommonUtils_api.getInstance().parseDateToTimeStamp(
									new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							count + "", error_sign };
					DbHelper.update(sql, param, conn);
				}
			} else {
				sql = "INSERT INTO PUB_DATAEXCHANGE_ERROR_LOG (ID,ERROR_TYPE,ERROR_NAME,ERROR_SIGN,CONTENT,CREATE_TIME,SEND_COUNT) VALUES (?,?,?,?,?,?,?)";
				param = new Object[] {
						Tools.getUUID32(),
						error_type,
						error_name,
						error_sign,
						content,
						CommonUtils_api.getInstance().parseDateToTimeStamp(
								new Date(),
								CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), "1" };
				count = 1;
				DbHelper.update(sql, param, conn);
			}
			conn.commit();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			log_.error("保存错误日志失败");
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return 0;
		} finally {
			DBSource.closeConnection(conn);
		}
	}
}
