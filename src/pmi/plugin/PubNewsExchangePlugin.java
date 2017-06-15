package plugin;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.plugin.IPlugin;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class PubNewsExchangePlugin extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				sendData();
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
	
	private void sendData() {
		Connection conn = DbHelper.getConnection("icityDataSource");
		Connection connpre = DbHelper.getConnection("sdzwDataSource");
		try {
			conn.setAutoCommit(false);
			//String sql = "select t.id,t.name,t.url,t.ucid,to_char(t.writetime,'yyyy-mm-dd hh24:mi:ss') writetime,t.newsid,t.itemid,case when type='01' then '咨询'  when type='12' then '受理' when type='03' then '待评价' when type='04' then '投诉'  when type='02' then '不予受理' when type='14' then '已驳回' when type='21' then '补交告知' when type='97' then '作废' when type='98' then '不予许可' when type='99' then '准予许可' end type from pub_mynews t where ex_flag=? and type not in('01','03','04') and t.ucid in (select s.user_id from uc_user_map s)";
			String sql = "select t.id,t.name,t.url,t.ucid,to_char(t.writetime,'yyyy-mm-dd hh24:mi:ss') writetime,t.newsid,t.itemid,case when type='01' then '咨询'  when type='12' then '受理' when type='03' then '待评价' when type='04' then '投诉'  when type='02' then '不予受理' when type='14' then '已驳回' when type='21' then '补交告知' when type='97' then '作废' when type='98' then '不予许可' when type='99' then '准予许可' end type,s.* from pub_mynews t,uc_user_map s where ex_flag=? and type not in('01','03','04') and t.ucid in (s.user_id)";
			DataSet ds = DbHelper.query(sql, new Object[] { "0" }, conn);
			JSONArray data = ds.getJAData();
			JSONObject obj;
			for (int i = 0; i < ds.getTotal(); i++) {
				try {
					obj = JSONObject.fromObject(data.get(i));
					// 根据用户id查询用户名
					sql = "select name from uc_user where id = ?";
					DataSet user = DbHelper.query(sql,
							new Object[] { obj.get("UCID") }, conn);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
					String now = df.format(new Date());// new Date()为获取当前系统时间
					sql = "insert into dt_message(messageid,userid,username,message,messagetime,validity_flag,exchange_time,exchange_flag) values(?,?,?,?,?,?,?,?)";
					int j = DbHelper
							.update(sql,
									new Object[] {
											Tools.getUUID32(),
											obj.get("USER_ID_MAP"),//obj.get("UCID"),
											user.getJOData().get("NAME"),
											obj.get("NAME")+":"+obj.get("TYPE"),
											obj.getString("WRITETIME"),
											"1",
											now,
											"0" }, connpre);
					int h = 0;
					if (j > 0) {
						sql = "update pub_mynews set ex_flag='1' where id=?";
						h = DbHelper.update(sql,
								new Object[] { obj.get("ID") }, conn);
					}
					if (h > 0) {
						conn.commit();
						connpre.commit();
					} else {
						conn.rollback();
						connpre.rollback();
					}
				} catch (Exception e) {
					System.out.println("dt_message");
					e.printStackTrace();
					conn.rollback();
					connpre.rollback();
				}
			}
		} catch (Exception e) {
			//log_.error("数据交换失败");
		} finally {
			DBSource.closeConnection(conn);
			DBSource.closeConnection(connpre);
		}
	}

}
