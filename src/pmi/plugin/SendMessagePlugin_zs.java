package plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;

public class SendMessagePlugin_zs extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;
	private static Logger log = LoggerFactory.getLogger(BaseQueryCommand.class);

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
							str = sendGet(telephone, smscontent);

							CharSequence cstr = "1000";

							// 根据发送状态 更新短信状态
							if (str.contains(cstr)) {
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

	/**
	 * 舟山调短信接口
	 * 
	 * @param phonestr
	 * @param message
	 * @return
	 */
	public static String sendGet(String phonestr, String message) {
		String result = "";
		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url") + "sendMessage");
		Map<String, String> map=new HashMap<String, String>();
		map.put("phoneList", phonestr);
		map.put("mesConent", message);
		try {
			RestUtil.postData(url, map);
			result = "1000";
		} catch (Exception e) {
			log.error("短信发送失败！");
		} finally {
			return result;
		}

	}
}
