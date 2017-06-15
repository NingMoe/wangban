/**   
* @Title: JXDataExchangeYNPlugin.java 
* @Package plugin 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2016-6-13 上午10:57:20 
* @version V1.0   
*/
package plugin;

import java.util.Timer;
import java.util.TimerTask;

import km.app.icity.guestbook.JXDataExchangeDao;

import com.icore.plugin.IPlugin;

import app.icity.onlineapply.ApplyQZDao;

/** 
 * @ClassName: JXDataExchangeYNPlugin 
 * @Description: 描述 :昆明与第三方绩效考评系统对接数据交换至汇总库
 * @author kongws
 * @date 2016-6-13 上午10:57:20 
 *  
 */
public class JXDataExchangeYNPlugin implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				JXDataExchangeDao.getInstance().handleData();
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
