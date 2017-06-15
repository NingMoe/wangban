package plugin;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.mascloud.sdkclient.Client;

public class SendMessage4XiNingPluginBy10086eCloud extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;
	final Client client = Client.getInstance();

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		// 正式环境IP，登录验证URL，用户名，密码，集团客户名称
		client.login(SecurityConfig.getString("MasWsLoginUrl"), SecurityConfig.getString("SDKAccount"),
				SecurityConfig.getString("SDKPassword"), SecurityConfig.getString("Customer"));
		TimerTask task = new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// 获取短信内容及手机号
				Plugin_Util plugin_Util = Plugin_Util.getInstance();
				DataSet ds = plugin_Util.getToSendList();
				if (ds != null) {
					String id = "", address = "", message = "",modelid = "";
					int sendResult = 0;
					for (int i = 0; i < ds.getTotal(); i++) {
						try {
							id = ds.getRecord(i).getString("ID");
							address = ds.getRecord(i).getString("TELEPHONE");
							message = ds.getRecord(i).getString("SMSCONTENT");
							modelid = ds.getRecord(i).getString("CHANNEL");
							if("register".equals(modelid)){
								modelid = "1339";
							}else if("resetpwd".equals(modelid)){
								modelid = "1340";
							}else if("onlineApply".equals(modelid)){//在线申报
								modelid = "2855";
							}else {
								modelid = "1339";
							}
							sendResult = sendSms(message, address,modelid);
							if (sendResult == 1) {
								plugin_Util.updateSms(id, "1");
							}else
								plugin_Util.updateSms(id, "2");
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
		_taskTimer.cancel();
		_taskTimer = null;
		return true;
	}

	private int sendSms(String message, String address,String modelid) {
		int sendResult = 0;
		try {
			// 测试环境IP
			/*
			 * client.login("http://112.33.1.13/app/sdk/login", "ssh42",
			 * "ssh421234","光谷信息");
			 */
			/*
			 * sendResult = client. sendDSMS (new String[] {address}, message,
			 * "", 1,SecurityConfig.getString("Sign"),
			 * UUID.randomUUID().toString(),true);
			 */
			String[] parms;
			if(modelid.equals("2855")){//两个参数的模板-在线申报
				parms = message.split(",");
			}else{
				parms= new String[]{ message};
			}
			sendResult = client.sendTSMS(new String[] { address }, modelid, parms, "", 5,
					SecurityConfig.getString("Sign"), UUID.randomUUID().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendResult;
	}
}
