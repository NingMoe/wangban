package plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;

public class AutoEvaluationPlugin extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				DataSet ds;
				ds = getEvaluation();
				if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
					int len = ds.getTotal();
					for (int i = 0; i < len; i++) {
						JSONObject list;
						list = ds.getJAData().getJSONObject(i);
						insertEvaluation(list);
					}
				}
			}
		};
		long tt = 1000 * 86400;// 每86400秒进行操作/24小时
		_taskTimer.schedule(task, tt, tt);
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		_taskTimer.cancel();
		_taskTimer = null;
		return true;
	}

	/**
	 * 获取待评价列表
	 * 
	 * @return
	 */
	public DataSet getEvaluation() {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "select * from BUSINESS_INDEX  where  "
					+ "to_char(bjsj+7,'yyyy-MM-dd')<to_char(sysdate,'yyyy-MM-dd') and "
					+ "STATE in ('02', '13','96', '97', '98', '99') and "
					+ "not exists  (select 1 from star_level_evaluation where serial_number = sblsh)";
			ds = DbHelper.query(sql, new Object[] {}, conn);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 插入评价
	 * 
	 * @param id
	 * @param status
	 * @return
	 */
	public void insertEvaluation(JSONObject list) {
		// 生成一个UUID
		String strId = Tools.getUUID32();
		String strBSRName = list.getString("SQRMC");// 申请人名称
		String strSerialNumber = list.getString("SBLSH");// 申办流水号
		int strStarLevel = 5;// 星级
		String strEvaluateContent = "";// 评价内容
		String remoteAddr = "";// 评价者ip
		String strCreatorId = "";// 如果有登录，则直接用网办账户
		String ITEM_CODE = list.getString("SXBM");// 服务事项编码
		String ITEM_ID = list.getString("SXID");// 服务事项ID
		String ITEM_NAME = list.getString("SXMC");// 服务事项NAME
		String DEPT_ID = list.getString("SJDWDM");// 事项主管部门组织机构代码
		String DEPT_NAME = list.getString("SJDW");// 事项主管部门组织机构名称
		String REGION_ID = list.getString("XZQHDM");// 业务所属区划
		String APPLY_SUBJECT = list.getString("SBXMMC");// 申报主题
		int strTimeStarLevel = 5;// 办件时间评价
		int strQuueryStarLevel = 5;// 服务质量评价
		int strMajorStarLevel = 5;// 业务专业评价
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String sql = "insert into STAR_LEVEL_EVALUATION(ID,BSR_NAME,SERIAL_NUMBER,STAR_LEVEL,EVALUATE_CONTENT,"
				+ "CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_CODE,SERVICE_ID,SERVICE_NAME,SERVICE_ORG_ID,"
				+ "SERVICE_ORG_NAME,REGION_CODE,APPLY_NAME,TIME_STAR_LEVEL,QUALITY_STAR_LEVEL,MAJOR_STAR_LEVEL) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i;
			i = DbHelper.update(sql,
					new Object[] { strId, strBSRName, strSerialNumber, strStarLevel, strEvaluateContent, remoteAddr,
							strCreatorId,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							ITEM_CODE, ITEM_ID, ITEM_NAME, DEPT_ID, DEPT_NAME, REGION_ID, APPLY_SUBJECT,
							strTimeStarLevel, strQuueryStarLevel, strMajorStarLevel },
					conn);

			if (0 == i) {
				ds.setState(StateType.FAILT);
				ds.setMessage("添加内容失败！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}
	}
}
