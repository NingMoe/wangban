package plugin;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.icore.log.Logger;
import com.icore.plugin.IPlugin;
import com.icore.util.CacheManager;
import com.icore.util.DaoFactory;
import com.icore.util.cache.channel.IChannel;
import com.inspur.util.Constant;

public class UserNumPlugin implements IPlugin {
	private static final Logger _log = Logger.getLogger(UserNumPlugin.class);
	private Timer _taskTimer;
	private IChannel _iCacheChannel;
	public static int onlineUserNum = 0;
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		if(_taskTimer == null){
			_iCacheChannel = (IChannel)DaoFactory.getDao("com.icore.util.cache.channel.GroupChannel");
			_taskTimer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					List<String> it = (List<String>)_iCacheChannel.keys(Constant.SESSIONID);
					if(it != null){
						onlineUserNum = it.size();
						_log.info("前用户数为"+onlineUserNum);
					}
				}
			};
			long tt = 1000*5;//每5秒进行操作
			_taskTimer.schedule(task, tt, tt);
			return true;
		}
		return false;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		if(_taskTimer != null){
			_taskTimer.cancel();
			_taskTimer = null;
		}
		return true;
	}
}
