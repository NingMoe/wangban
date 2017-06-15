package plugin;

import java.util.Timer;
import java.util.TimerTask;

import com.icore.plugin.IPlugin;

public class TestPlugin implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("xxxxx:" + System.currentTimeMillis());
			}
		};
		long tt = 1000 * 5;// 每5秒进行操作
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
