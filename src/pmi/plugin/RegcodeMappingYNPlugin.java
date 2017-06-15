package plugin;

import java.util.Timer;
import java.util.TimerTask;

import km.app.icity.guestbook.RegcodeMappingDao;

import app.icity.onlineapply.ApplyQZDao;

import com.icore.plugin.IPlugin;

/**
 * 昆明市与省大厅对接，映射区划代码
 * 
 * @author kongweisong
 * @version 1
 */
public class RegcodeMappingYNPlugin implements IPlugin {

	private Timer _taskTimer;

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				RegcodeMappingDao.getInstance().handleData();
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
