package plugin;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.govservice.GovProjectCmd;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SendMessagePlugin_weifang extends BaseQueryCommand implements	IPlugin {
	private static Log log_ = LogFactory.getLog(SendMessagePlugin_weifang.class);
	private Timer _taskTimer;
	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Plugin_Util plugin_Util = Plugin_Util.getInstance();
				// 获取短信内容及手机号
				DataSet ds = plugin_Util.getToSendList();
				if (ds != null) {

					String id = "";
					String telephone = "";
					String smscontent = "";
					String str = "";

					for (int i = 0; i < ds.getTotal(); i++) {
						id = ds.getRecord(i).getString("ID");
						telephone = ds.getRecord(i).getString("TELEPHONE");
						smscontent = ds.getRecord(i).getString("SMSCONTENT");
						try {
							str = sendData(telephone, smscontent);
							CharSequence cstr = "1";
							// 根据发送状态 更新短信状态
							if ("1".equals(str)) {
								plugin_Util.updateSms(id, "1");
							} else {
								plugin_Util.updateSms(id, "2");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		long tt = 10000 * 1;// 每10秒进行操作
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

	private String sendData(String telephone, String smscontent) {
		String str="0";
		Connection connpre = DbHelper.getConnection("wfdxDataSource");
		try {
			connpre.setAutoCommit(false);			
			String sql="";
			sql = "insert into T_SMSDATA t (F_RECEIVEPHONE,F_SMSDATA) values (?,?)";
			int j = DbHelper.update(sql, new Object[] {telephone,smscontent}, connpre);
			if(j > 0){
				connpre.commit();
				str = "1";
			}else{
				connpre.rollback();
			}
		} catch (Exception e) {
			System.err.println("短信发送失败");
			e.printStackTrace();
		} finally {
			//System.err.println("失败");
			DBSource.closeConnection(connpre);
		}
		return str;
	}
}
