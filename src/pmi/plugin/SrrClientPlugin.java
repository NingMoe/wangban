package plugin;

import java.sql.SQLException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.util.CommandProxy;

import com.icore.core.ThreadPoolManager;
import com.icore.http.bean.HttpLog;
import com.icore.http.bean.ServiceBean;
import com.icore.http.bean.ServiceLog;
import com.icore.plugin.IPlugin;
import com.icore.util.FrameConfig;
import com.icore.util.SecurityConfig;
import com.icore.util.VersionUtil;
import com.icore.util.db.DataSourceFactory;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;

public class SrrClientPlugin implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 通过http协议调用远程接口的方法，参数（类名+方法名）
				CommandProxy cmdProxy = new CommandProxy("app.pmi.srr.SrrServerCmd");
				Map<String, Object> monitor = new HashMap<String, Object>();
				String appId = FrameConfig.getAppId();// 获取AppId，命名由WebId+端口号
				cmdProxy.setParameter("appId", appId);
				// 服务器信息
				JSONObject webServer = new JSONObject();
				webServer.put("serverName", Tools.webPort);// 服务器名称
				webServer.put("startTime", Tools.formatDate(new Time(Tools.startTime)));// 启动时间
				webServer.put("runTime", (System.currentTimeMillis() - Tools.startTime) / 60000);// 运行时间
				webServer.put("vesion", VersionUtil.getInstance().toString());// 程序版本
				monitor.put("webServer", webServer);
				// 工作线程信息
				JSONArray threadInfo = new JSONArray();
				JSONObject thread = new JSONObject();
				Map<String, ThreadPoolManager> mapPool = ThreadPoolManager.getInstance().getInstanceName();
				Iterator<Entry<String, ThreadPoolManager>> itPool = mapPool.entrySet().iterator();
				while (itPool.hasNext()) {
					Entry<String, ThreadPoolManager> iterator = itPool.next();
					String key = iterator.getKey();
					ThreadPoolManager pool = iterator.getValue();
					long total = pool.getTotalTaskNumber();
					long finish = pool.getFinishTaskNumber();
					if ("__defaultKey__".equals(key)) {
						key = "默认";
					}
					if ("__core__".equals(key)) {
						key = "核心";
					}
					thread.put("ThreadName", key);// 工作线程
					thread.put("TotalThread", pool.getTotalThreadNum());// 总队工作队列数
					thread.put("WaitThread", pool.getWaitThreadNum());// 可用队列
					thread.put("TotalRequest", total);// 总请求数
					thread.put("FinishRequest", finish);// 已经处理数
					thread.put("LeftRequest", total - finish);// 未处理数
					threadInfo.add(thread); //
				}
				monitor.put("threadInfo", threadInfo);
				// 连接池信息(//连接池【icityDataSource】的总连接数为：2,正在工作的连接数：0,空闲的连接数：2)
				JSONArray datasourceInfo = new JSONArray();
				JSONObject datasource = new JSONObject();
				Map<String, DataSourceFactory> dbMap = DataSourceFactory.getInstanceMap();
				Iterator<Entry<String, DataSourceFactory>> it = dbMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, DataSourceFactory> iterator = it.next();
					// String sKey = iterator.getKey();
					DataSourceFactory s = iterator.getValue();
					if (s != null) {
						String str;
						try {
							str = s.statusInfo();
							String str1 = str.substring(str.indexOf("【") + 1, str.indexOf("】"));
							String str2 = str.substring(str.indexOf("：") + 1, str.indexOf(","));
							String strTemp = str.substring(str.indexOf(",") + 1);
							String str3 = strTemp.substring(strTemp.indexOf("：") + 1, strTemp.indexOf(","));
							String str4 = strTemp.substring(strTemp.lastIndexOf("：") + 1);

							datasource.put("DataSourceName", str1);// 连接池
							datasource.put("TotalConnect", str2);// 总连接数
							datasource.put("UsedConnect", str3);// 正在工作的连接数
							datasource.put("NotUsedConnect", str4);// 空闲的连接数
							datasourceInfo.add(datasource);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				monitor.put("datasourceInfo", datasourceInfo);
				// 传递内存、CPU、线程信息((memoryUsed":28811,"cpuUsed":0,"threadCount":40))
				Object[] f = Tools.getJvmMonitor().getFIFO();
				JSONArray jvm = JSONArray.fromObject(f);
				monitor.put("jvmInfo", jvm);
				// 在线用户实时统计
				monitor.put("onlineUserNum", UserNumPlugin.onlineUserNum);
				// url信息
				Object[] m = HttpLog.getFIFO();
				JSONArray url = JSONArray.fromObject(m);
				monitor.put("urlInfo", url);
				cmdProxy.setParameter("monitor", monitor);
				// 将服务信息存库
				List<ServiceBean> serviceBeans = ServiceLog.clear();
				cmdProxy.setParameter("params", JSONArray.fromObject(serviceBeans));
				cmdProxy.setUrl(SecurityConfig.getString("RestDomain"));// 从配置文件取"http://localhost/icity/c/"
				DataSet ds = cmdProxy.execute("heartbeat");
				if (ds.getState() == 0) {
					try {
						throw new Exception("心跳失败!");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		long tt = 1000 * 5;// 每5秒进行操作
		_taskTimer.schedule(task, tt, tt);
		return true;
	}

	@Override
	public boolean stop() {
		_taskTimer.cancel();
		_taskTimer = null;
		return true;
	}

}
