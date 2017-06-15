package plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.icore.http.util.HttpUtil;
import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.mascloud.sdkclient.Client;

import app.util.RestUtil;
import net.sf.json.JSONObject;

public class SendMessage4ManZhouLiPlugin extends BaseQueryCommand implements IPlugin {
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
					JSONObject sendResult;  
					// 错误日志信息
					String error_name = "";// 数据信息
					String content = "";// 日志信息
					String error_type = "满洲里短信推送";
					for (int i = 0; i < ds.getTotal(); i++) {
						try {
							id = ds.getRecord(i).getString("ID");
							address = ds.getRecord(i).getString("TELEPHONE");
							message = ds.getRecord(i).getString("SMSCONTENT");
							sendResult = sendSms(address, message);
							error_name = message  + "，发送到：" + address;
						  if("2".equals(sendResult.get("code"))){
							  plugin_Util.updateSms(id, "1");
						  }else{
							  plugin_Util.updateSms(id, "2"); 
							  int code =sendResult.getInt("code");
							  String msg =sendResult.getString("msg");
							  switch (code) {
								case 0:
									content=msg;
									break;
								case 400:
									content=msg;
									break;
								case 401:
									content=msg;
									break;
								case 402:
									content=msg;
									break;
								case 403:
									content=msg;
									break;
								case 4030:
									content=msg;
									break;
								case 404:
									content=msg;
									break;
								case 405:
									content=msg;
									break;
								case 4050:
									content=msg;
									break;
								case 4051:
									content=msg;
									break;
								case 4052:
									content=msg;
									break;
								case 406:
									content=msg;
									break;
								case 407:
									content=msg;
									break;
								case 4070:
									content=msg;
									break;
								case 4071:
									content=msg;
									break;
								case 4072:
									content=msg;
									break;
								case 4073:
									content=msg;
									break;
								case 408:
									content=msg;
									break;
								default:
									content="其他错误";
									break;
							}
						  }
						} catch (RuntimeException  e) {	
							e.printStackTrace();
						}catch (Exception e){
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

	private JSONObject sendSms(String address, String message) {
		JSONObject json=new JSONObject();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("HuYi_Url"));
			Map<String, String> map = new HashMap<String, String>();
			map.put("account", SecurityConfig.getString("HuYi_Account"));
			map.put("password", SecurityConfig.getString("HuYi_Password"));
			map.put("mobile", address);
			map.put("content", message);
			Object ret = RestUtil.postData(url, map);
			Document doc = DocumentHelper.parseText((String) ret);
			Element root = doc.getRootElement();
			String code = root.elementText("code");
			String msg = root.elementText("msg");
			json.put("msg", msg);
			json.put("code",code);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return json;
	}
}
