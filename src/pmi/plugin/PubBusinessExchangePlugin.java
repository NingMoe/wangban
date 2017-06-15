package plugin;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;
import com.icore.plugin.IPlugin;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import app.icity.govservice.GovProjectCmd;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PubBusinessExchangePlugin extends BaseQueryCommand implements IPlugin{
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
		Connection connpre = DbHelper.getConnection("xcDataSource");
		GovProjectCmd  govProjectCmd=new GovProjectCmd();
		try {
			conn.setAutoCommit(false);
			String sql="select b.sblsh,b.cxmm,b.sxbm,b.sxmc,b.sbxmmc,b.assort,b.sqrmc,b.state,b.sqrzjhm,b.sxid,"
					+ "b.lxrxm,b.lxrsj,b.sjdwdm,b.sjdw,b.ucid,b.ly,to_char(b.sbsj,'yyyy-mm-dd hh24:mi:ss') sbsj,"
			        + "b.xzqhdm,u.card_type,u.card_no,u.org_boss_name,u.name,e.postcode,u.address,m.user_id_map uuid "
			        + "from business_index b, uc_user_map m,uc_user u "
			        + "left join uc_user_ext e on  u.id=e.id "
			        + "where  b.bjbzw=? and b.ucid=u.id and b.ucid = m.user_id";
					DataSet ds = DbHelper.query(sql, new Object[] {"0"}, conn);
					JSONArray data = ds.getJAData();
					JSONObject obj;
					JSONObject objt;
					String type;
					String bjzt;
					ParameterSet pSet =new ParameterSet();
					for (int i = 0; i < ds.getTotal(); i++) {
						int dataver=1;
						String acceptlist =null;
						try {
							obj = JSONObject.fromObject(data.get(i));
							pSet.setParameter("itemid", obj.get("SXID"));
							DataSet dst=govProjectCmd.getAllItemInfoByItemID(pSet);
							JSONArray datat= dst.getJAData();
							for (int j = 0; j < datat.size(); j++) {
								objt = JSONObject.fromObject(datat.get(j));
								if (j==0) {
									acceptlist=objt.get("CODE")+":"+objt.get("NAME");
								}else {
									acceptlist= acceptlist+","+objt.get("CODE")+":"+objt.get("NAME");
								}
								 
							}
							sql="select max(dataver) dataver from pre_apasinfo where projid=?";
						    DataSet pre = DbHelper.query(sql,
										new Object[] { obj.get("SBLSH") }, connpre);
						    if (!"".equals(pre.getRecord(0).get("DATAVER"))) {
						    	 dataver=pre.getRecord(0).getInt("DATAVER")+1;
							}
							String num=(String) obj.get("CARD_TYPE");
							int number=Integer.parseInt(num);
							switch (number) {
							case 10:
								type="1";
								break;
							case 11:
								type="2";
								break;
							case 20:
								type="3";
								break;
							case 50:
								type="4";
								break;
							case 51:
								type="5";
								break;
							case 60:
								type="6";
								break;
							default:
								type="1";
								break;
							}
							if ("00".equals(obj.get("STATE"))) {
								bjzt="01";
							}
							else if ("11".equals(obj.get("STATE"))) {
								bjzt="02";
							}else {
								bjzt="01";
							}
							
							sql="insert into pre_apasinfo(projid,projpwd,ITEMREGIONID,ITEM_ROWGUID,itemno,itemname,projectname,projectstate,infotype,acceptlist,"
									+ "applyname,apply_cardtype,apply_cardtypenumber,contactman,contactman_cardtype,contactman_cardnumber,telphone,postcode,"
									+ "address,legalman,deptid,deptname,receive_useid,receive_name,applyfrom,receivetime,approve_type,region_id,"
									+ "create_time,dataver,maketime,signstate,is_tongjian) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,SYSDATE,?,SYSDATE,?,?)";
							int j = DbHelper.update(sql, new Object[] {
									obj.get("SBLSH"),obj.get("CXMM"),obj.get("XZQHDM"),obj.get("SXID"),obj.get("SXBM"),obj.get("SXMC"),obj.get("SBXMMC"),bjzt,
									obj.get("ASSORT"),acceptlist,obj.get("SQRMC"),obj.get("CARD_TYPE"),obj.get("SQRZJHM"),obj.get("LXRXM"),
									type,obj.get("CARD_NO"),obj.get("LXRSJ"),obj.get("POSTCODE"),
									obj.get("ADDRESS"),obj.get("ORG_BOSS_NAME"),
									obj.get("SJDWDM"),obj.get("SJDW"),obj.get("UUID"),obj.get("NAME"),"网上",obj.get("SBSJ"),"01",obj.get("XZQHDM"),dataver,
									"0","2"
							}, connpre);
					int h = 0;
					if(j>0){
						sql = "update business_index set bjbzw='1' where sblsh=?";
						h = DbHelper.update(sql, new Object[] { obj.get("SBLSH") }, conn);
					}
					if(h > 0){
						conn.commit();
						connpre.commit();
					}else{
						conn.rollback();
						connpre.rollback();
					}
				} catch (Exception e) {
					e.printStackTrace();
					conn.rollback();
					connpre.rollback();
				}
			}
		} catch (Exception e) {
			//log_.error("数据交换失败");
		} finally {
			//System.err.println("失败");
			DBSource.closeConnection(conn);
			DBSource.closeConnection(connpre);
		}
	}

}
