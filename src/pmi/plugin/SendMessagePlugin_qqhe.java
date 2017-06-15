package plugin;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;
import com.icore.plugin.IPlugin;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.util.db.DbHelper;
import hlj_qqheNew.app.icity.project.ProjectRegisterCmd;;

public class SendMessagePlugin_qqhe extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Plugin_Util plugin_Util = Plugin_Util.getInstance();
				// 获取短信内容及手机号
				DataSet ds = plugin_Util.getToSendList();
				if (ds != null) {

					String id = "";
					String telephone = "";
					String smscontent = "";
					String str = "";
					String sendtime = "";

					for (int i = 0; i < ds.getTotal(); i++) {

						id = ds.getRecord(i).getString("ID");
						telephone = ds.getRecord(i).getString("TELEPHONE");
						smscontent = ds.getRecord(i).getString("SMSCONTENT");
						sendtime = ds.getRecord(i).getString("SENDTIME");
						try {
							//str = sendGet(id,telephone, smscontent,sendtime);
							DataSet d = ProjectRegisterCmd.sendGet(id,telephone, smscontent,sendtime);
							str = d.getMessage().toString();
							System.out.println("------------------"+str);
							plugin_Util.updateSms(id, str);//更新短信状态
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
	 * 齐齐哈尔调短信接口
	 * 
	 * @param phonestr
	 * @param message
	 * @param sendtime
	 * @return
	 */
	public String sendGet(String id, String phonestr, String message, String sendtime) {
		Connection msconn = DbHelper.getConnection("mssqlDataSource");
		String flage = "0";
		String[] d = sendtime.split(" ");
		String sql = "insert into InBox(mbno,Msg,ArriveDate,ArriveDateTime,ArriveTime) values ('"+ phonestr+ "','"+message+"','"+ d[0]+ "','"+ sendtime+ "','" + d[1] + "');";
		int i = DbHelper.update(sql, new Object[] {}, msconn);
		//int j = this.executeUpdate(sql, null, mssqlDataSource);
		if(i==0){
			flage="0";
			//Plugin_Util.getInstance().insertErrorLog("0", "插入数据库失败",id, message);//保存发送失败日志
		}else{
			flage="1";
		}
        return flage;
	}
	

}
