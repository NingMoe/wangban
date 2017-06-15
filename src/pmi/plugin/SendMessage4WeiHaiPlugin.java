package plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import net.sf.json.JSONObject;

public class SendMessage4WeiHaiPlugin extends BaseQueryCommand implements
		IPlugin {
	private static Log log_ = LogFactory.getLog(SendMessage4WeiHaiPlugin.class);
	private Timer _taskTimer;

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// 获取短信内容及手机号
				Plugin_Util plugin_Util = Plugin_Util.getInstance();
				DataSet ds = plugin_Util.getToSendList();
				if (ds != null) {
					String id = "", address = "", message = "";
					JSONObject requestIdentifier;
					// 错误日志信息
					String error_name = "";// 数据信息
					String content = "";// 日志信息
					String error_type = "威海短信推送";
					for (int i = 0; i < ds.getTotal(); i++) {
						content = "";
						try {
							id = ds.getRecord(i).getString("ID");
							address = ds.getRecord(i).getString("TELEPHONE");
							message = ds.getRecord(i).getString("SMSCONTENT");
							requestIdentifier = sendSms(message, address);
							error_name = message + "，发送到：" + address;
							if (requestIdentifier.containsKey("restr")&&
									"error".equals(requestIdentifier
									.getString("restr"))) {
								content = "短信推送时异常！"
										+ requestIdentifier.getString("eInfo");
							} else {
								if (requestIdentifier.containsKey("code")
										&& "200".equals(requestIdentifier
												.getString("code"))) {
									plugin_Util.updateSms(id, "1");
								} else {
									plugin_Util.updateSms(id, "2");
									content = "短信推送时失败，返回结果状态码不成功或者不存在";
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (!content.equals("")) {
							int count = plugin_Util.insertErrorLog(error_type,
									error_name, id, content);
							if (count >= 3) {// count==3
								// 更新状态,推送错误超过3次，不在推送
								plugin_Util.updateSms(id, "3");
							}
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

	private JSONObject sendSms(String message, String address) {
		JSONObject json;
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "sendWangbanMessage");
			Map<String, String> map = new HashMap<String, String>();
			map.put("mesConent", message);
			map.put("phoneNum", address);
			Object ret = RestUtil.postData(url, map);
			json = JSONObject.fromObject(ret);
			json.put("restr", "success");
		} catch (Exception e) {
			json = new JSONObject();
			log_.error("短信推送失败");
			json.put("restr", "error");
			json.put("eInfo", e.toString());
		}
		return json;
	}

}
