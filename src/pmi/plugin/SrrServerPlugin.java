package plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.pmi.srr.SrrAppBean;
import app.pmi.srr.SrrServerBean;

import com.icore.plugin.IPlugin;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Command;
import com.inspur.util.Tools;

public class SrrServerPlugin implements IPlugin {
	private final static Map<String, SrrServerBean> _serverMap = new ConcurrentHashMap<String, SrrServerBean>();// 所有数据

	public static SrrServerPlugin getInstance() {
		return DaoFactory.getDao(SrrServerPlugin.class.getName());
	}

	private Timer _taskTimer;

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (_serverMap.size() == 0) {// 如果_serverMap为空则去查询数据库
					initServerMap();
				}
				// 为应用检测次数加1，检测应用是否异常，若异常，设置应用对应的服务器异常
				Iterator<SrrServerBean> it = _serverMap.values().iterator();
				while (it.hasNext()) {
					SrrServerBean serverBean = it.next();
					if (serverBean.getAppList().size() > 0 && "1".equals(serverBean.getIsInUse())) {
						for (int i = 0; i < serverBean.getAppList().size(); i++) {
							if ("1".equals(serverBean.getAppList().get(i).getIsInUse())) {// 应用的可用状态为可用
								serverBean.getAppList().get(i)
										.setTestTimes(serverBean.getAppList().get(i).getTestTimes() + 1);// 为所有应用检测次数+1
								if (serverBean.getAppList().get(i).getTestTimes() > 3) {
									serverBean.getAppList().get(i).setStatus("0");// 为检测次数大于或等于3的应用设置状态为异常
									serverBean.setStatus("0");// 为检测次数大于或等于3的应用设置对应的服务器状态为异常
								}
							}
						}
					}
					_serverMap.put(serverBean.getId(), serverBean);// 重新把bean再Set回map
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

	/**
	 * 数据发生变化时清空_serverMap
	 * 
	 * @return
	 */
	public boolean reset() {
		if (_serverMap != null) {
			_serverMap.clear();
		}
		return true;
	}

	/**
	 * 远程;应用端调用，设置服务器状态为正常，应用检测次数为0
	 */
	public DataSet heartbeat(ParameterSet pSet) {
		String appId = (String) pSet.getParameter("appId");// 应用ID
		Map<String, Object> monitor = (Map<String, Object>) pSet.remove("monitor");
		if (StringUtil.isNotEmpty(appId)) {
			Iterator<SrrServerBean> it = _serverMap.values().iterator();
			while (it.hasNext()) {
				SrrServerBean serverBean = it.next();
				int appCount = serverBean.getAppList().size();
				if (appCount > 0 && "1".equals(serverBean.getIsInUse())) {
					for (int i = 0; i < appCount; i++) {
						if (appId.equals(serverBean.getAppList().get(i).getId())
								&& "1".equals(serverBean.getAppList().get(i).getIsInUse())) {
							serverBean.getAppList().get(i).setTestTimes(0);// 检测次数为0
							if (!monitor.isEmpty()) {
								serverBean.getAppList().get(i).setMonitor(monitor);// 设置monitor监控信息
							}
							serverBean.getAppList().get(i).setStatus("1");// 设置应用正常
							serverBean.setStatus("1");// 服务器正常
						}
					}
				}
				_serverMap.put(serverBean.getId(), serverBean);// 重新把bean再Set回map
			}
		}
		return new DataSet();
	}

	/**
	 * 获取主机信息
	 * 
	 * @return
	 */
	public DataSet getServerStatus() {
		if (_serverMap.size() == 0) {
			this.initServerMap();// 页面初始化数据
		}
		DataSet dSet = new DataSet();
		JSONArray newJArray = new JSONArray();
		Iterator<SrrServerBean> it = _serverMap.values().iterator();
		int i = 0;
		while (it.hasNext()) {// 页面显示15个服务器
			SrrServerBean serverBean = it.next();
			int normalApp = 0;
			int abnormalApp = 0;
			serverBean.setSort(i);// 目前是随机排序
			int size = serverBean.getAppList().size();
			if (size > 0) {
				for (int k = 0; k < size; k++) {
					if ("0".equals(serverBean.getAppList().get(k).getStatus())) {
						abnormalApp++;
					} else {
						normalApp++;
					}
				}
			}
			serverBean.setNormalApp(normalApp);// 正常应用个数
			serverBean.setAbnormalApp(abnormalApp);// 异常应用个数
			newJArray.add(serverBean);
			i++;
			if (i == 15) {
				break;
			} // 页面显示15个服务器
		}
		dSet.setData(Tools.stringToBytes(newJArray.toString()));
		return dSet;
	}

	/**
	 * 获取某台主机下的应用信息
	 * 
	 * @return
	 */
	public DataSet getAppsOfServer(ParameterSet pSet) {
		JSONArray jArray;
		DataSet dSet = new DataSet();
		String sid = (String) pSet.getParameter("server_id");
		String status = (String) pSet.getParameter("status");// 应用的状态
		String aname = (String) pSet.getParameter("name");// 应用的名称
		JSONArray sc = (JSONArray) pSet.getParameter("serviceCount");// 应用的服务个数
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		List<SrrAppBean> appsList = new ArrayList<SrrAppBean>();// 查询出的所有符合条件的数据
		int total = 0;
		if (StringUtil.isNotEmpty(sid)) {
			if (_serverMap.containsKey(sid)) {
				SrrServerBean serverBean = _serverMap.get(sid);
				List<SrrAppBean> apps = serverBean.getAppList();
				if (apps != null && apps.size() > 0) {
					if (StringUtil.isNotEmpty(status) && StringUtil.isNotEmpty(aname)) {
						for (int i = 0; i < apps.size(); i++) {
							if (status.equals(apps.get(i).getStatus()) && apps.get(i).getName().indexOf(aname) != -1) {
								appsList.add(apps.get(i));
							}
						}
					} else if (StringUtil.isNotEmpty(status) && StringUtil.isEmpty(aname)) {
						for (int i = 0; i < apps.size(); i++) {
							if (status.equals(apps.get(i).getStatus())) {
								appsList.add(apps.get(i));
							}
						}
					} else if (StringUtil.isEmpty(status) && StringUtil.isNotEmpty(aname)) {
						for (int i = 0; i < apps.size(); i++) {
							if (apps.get(i).getName().indexOf(aname) != -1) {
								appsList.add(apps.get(i));
							}
						}
					} else {
						appsList.addAll(apps);
					}
				}
			}
		} else {// 查询所有
			Iterator<SrrServerBean> it = _serverMap.values().iterator();
			while (it.hasNext()) {
				SrrServerBean serverBean = it.next();
				List<SrrAppBean> apps = serverBean.getAppList();
				if (apps != null && apps.size() > 0) {
					if (StringUtil.isNotEmpty(status) && StringUtil.isNotEmpty(aname)) {
						for (int i = 0; i < apps.size(); i++) {
							if (status.equals(apps.get(i).getStatus()) && apps.get(i).getName().indexOf(aname) != -1) {
								appsList.add(apps.get(i));
							}
						}
					} else if (StringUtil.isNotEmpty(status) && StringUtil.isEmpty(aname)) {
						for (int i = 0; i < apps.size(); i++) {
							if (status.equals(apps.get(i).getStatus())) {
								appsList.add(apps.get(i));
							}
						}
					} else if (StringUtil.isEmpty(status) && StringUtil.isNotEmpty(aname)) {
						for (int i = 0; i < apps.size(); i++) {
							if (apps.get(i).getName().indexOf(aname) != -1) {
								appsList.add(apps.get(i));
							}
						}
					} else {
						appsList.addAll(apps);
					}
				}
			}
		}
		total = appsList.size();
		// 查询应用的服务个数
		int on = 0, off = 0, ab = 0;
		for (int i = 0; i < total; i++) {
			if (sc != null && sc.size() > 0) {
				for (int j = 0; j < sc.size(); j++) {
					JSONObject jo = (JSONObject) sc.get(j);
					if (jo != null) {
						if (jo.getString("APP_ID").equals(appsList.get(i).getId())) {
							appsList.get(i).setServiceCount(jo.getInt("SERVICECOUNT"));
						}
					}
				}
			}
			if ("1".equals(appsList.get(i).getStatus())) {
				on++;
			} else if ("0".equals(appsList.get(i).getIsInUse())) {
				off++;
			} else {
				ab++;
			}
		}
		if (start == -1 || limit == -1) {
			appsList.get(0).setOnService(on);
			appsList.get(0).setAbnormalService(ab);
			appsList.get(0).setOffService(off);
		} else {
			appsList.get(start).setOnService(on);
			appsList.get(start).setAbnormalService(ab);
			appsList.get(start).setOffService(off);
		}
		if (start == -1 || limit == -1) {
			jArray = JSONArray.fromObject(appsList);
		} else if (total < (start + limit)) {
			jArray = JSONArray.fromObject(appsList.subList(start, total));
		} else {
			jArray = JSONArray.fromObject(appsList.subList(start, start + limit));
		}
		dSet.setData(Tools.stringToBytes(jArray.toString()));
		dSet.setTotal(total);
		return dSet;
	}

	/**
	 * 获取某个应用下的监控信息
	 * 
	 * @return
	 */
	public DataSet getMonitorOfApp(ParameterSet pSet) {
		DataSet dSet = new DataSet();
		JSONArray jArray = new JSONArray();
		Map<String, Object> monitor;
		String aid = (String) pSet.getParameter("app_id");
		if (StringUtil.isNotEmpty(aid)) {
			Iterator<SrrServerBean> it = _serverMap.values().iterator();
			while (it.hasNext()) {
				SrrServerBean serverBean = it.next();
				if (serverBean.getAppList() != null && serverBean.getAppList().size() > 0) {
					int size = serverBean.getAppList().size();
					for (int k = 0; k < size; k++) {
						SrrAppBean appBean = serverBean.getAppList().get(k);
						if (aid.equals(appBean.getId()) && "1".equals(appBean.getStatus())) {
							monitor = appBean.getMonitor();
							jArray.add(JSONObject.fromObject(monitor));
							dSet.setData(Tools.stringToBytes(jArray.toString()));
						}
					}
				}
			}
		}
		return dSet;
	}

	/**
	 * 初始化serverMap
	 */
	public boolean initServerMap() {
		try {
			Command cmd = new Command("app.pmi.srr.SrrServerCmd");
			DataSet ds = cmd.execute("initServerMap");
			JSONArray array = ds.getJAData();
			if (array != null && array.size() > 0) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject jo = (JSONObject) array.get(i);
					if (jo != null) {
						SrrServerBean serverBean = new SrrServerBean();
						List<SrrAppBean> appBeans = new ArrayList<SrrAppBean>();
						serverBean.setId(jo.getString("ID"));
						serverBean.setName(jo.getString("NAME"));
						serverBean.setIp(jo.getString("IP"));
						serverBean.setIsInUse(jo.getString("IS_IN_USE"));
						serverBean.setStatus("0");// 服务器状态-默认异常
						if (jo.get("APPLIST") != null) {
							JSONArray appArray = JSONArray.fromObject(jo.get("APPLIST"));
							if (appArray.size() > 0) {
								for (int j = 0; j < appArray.size(); j++) {
									JSONObject ajo = (JSONObject) appArray.get(j);
									if (ajo != null) {
										SrrAppBean appBean = new SrrAppBean();
										appBean.setId(ajo.getString("ID"));
										appBean.setName(ajo.getString("NAME"));
										appBean.setServerId(ajo.getString("SERVER_ID"));
										appBean.setServerName(jo.getString("NAME"));
										appBean.setIsInUse(ajo.getString("IS_IN_USE"));
										appBean.setStatus("0");// 应用状态-默认异常,实时判断
										appBean.setType(ajo.getString("TYPE"));
										appBean.setTestTimes(0);// 检测次数-默认0
										appBean.setServiceCount(0);
										appBean.setOnService(0);
										appBean.setOffService(0);
										appBean.setAbnormalService(0);
										appBeans.add(appBean);
									}
								}
							}
						}
						serverBean.setAppList(appBeans);
						_serverMap.put(jo.getString("ID"), serverBean);
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
