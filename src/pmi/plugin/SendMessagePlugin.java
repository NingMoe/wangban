package plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;

public class SendMessagePlugin extends BaseQueryCommand implements IPlugin {
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

					String id = "";
					String telephone = "";
					String smscontent = "";
					String url = "";
					String str = "";

					for (int i = 0; i < ds.getTotal(); i++) {

						id = ds.getRecord(i).getString("ID");
						telephone = ds.getRecord(i).getString("TELEPHONE");
						smscontent = ds.getRecord(i).getString("SMSCONTENT");
						url = SecurityConfig.getString("ytMessageUrl");
						try {
							url = url.replace("{phone}", telephone).replace("{msg}", java.net.URLEncoder.encode(smscontent,"UTF-8"));
						} catch (Exception e1) {
							e1.printStackTrace();
						}

						try {
							str = sendGet(url);

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
	 * post方式请求http服务
	 * 
	 * @param urlStr
	 * @param params
	 *            name=yxd&age=25
	 * @return
	 * @throws Exception
	 */
	public static String sendGet(String url) throws IOException {
		StringBuffer result = new StringBuffer();
		// 创建URL
		URL httpUrl = new URL(url);
		// 创建连接
		URLConnection connection = httpUrl.openConnection();
		BufferedReader bufferedReader = null;
		connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("connection", "keep-alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
		connection.connect();
		// 接受连接返回参数
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bufferedReader!=null){
				bufferedReader.close();
			}
		}
		return result.toString();
	}
}
