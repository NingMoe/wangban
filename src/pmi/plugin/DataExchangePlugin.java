package plugin;

import java.util.Timer;
import java.util.TimerTask;

import km.app.icity.onlineapply.ApplyKMDataChangeDao;
import app.icity.onlineapply.ApplyQZDao;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;

public class DataExchangePlugin implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				//昆明项目、盘龙项目部署一套，共用数据表。盘龙办件徐推送到云受理（一窗），昆明办件推送到审批，为避免产品推送代码混杂，因此建立本分支。
				if("kms".equals(SecurityConfig.getString("AppId"))){
					ApplyKMDataChangeDao.getInstance().handleData();
				}else{
					ApplyQZDao.getInstance().handleData();
				}
			}
		};
		long tt = 1000 * 60;// 每60秒进行操作
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

}
