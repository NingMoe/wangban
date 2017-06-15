package plugin;

import java.sql.Connection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.plugin.IPlugin;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class PubContentExchangePlugin extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				sendData();
			}
		};
		long tt = 1000 * 5;// 每5秒进行操作
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
	private void sendData() {
		Connection conn = DbHelper.getConnection("icityDataSource");
		Connection connpre = DbHelper.getConnection("sdjrobotDataSource");
		try {
			conn.setAutoCommit(false);
			String sql = "select * from dt_indexcatalog t where ex_flag = ?";
			DataSet ds = DbHelper.query(sql, new Object[] {"0"}, conn);
			JSONArray data = ds.getJAData();
			JSONObject obj;
			for (int i = 0; i < ds.getTotal(); i++) {
				try {
					obj = JSONObject.fromObject(data.get(i));
					sql = "insert into dt_indexcatalog (CONTENT,VALIDITY_FLAG,EXCHANGE_TIME,ex_flag) values(?,?,?,?)";
					int j = DbHelper.update(sql, new Object[] {
						obj.get("COUNT"),obj.get("VALIDITY_FLAG"),
						CommonUtils.getInstance().parseDateToTimeStamp(
								new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
						"0"
					}, connpre);
					int h = 0;
					if(j>0){
						sql = "update dt_indexcatalog set ex_flag='1' where id=?";
						h = DbHelper.update(sql, new Object[] { obj.get("ID") }, conn);
					}
					if(h > 0){
						conn.commit();
						connpre.commit();
					}else{
						conn.rollback();
						connpre.rollback();
					}
				} catch (Exception e) {
					conn.rollback();
					connpre.rollback();
				}
			}
		} catch (Exception e) {
			//log_.error("数据交换失败");
		} finally {
			DBSource.closeConnection(conn);
			DBSource.closeConnection(connpre);
		}
	}

}
