package action.bsp;

import java.sql.SQLException;
import java.util.Map;

import plugin.UserNumPlugin;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;

public class monitor_info extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	public boolean handler(Map<String,Object> data) throws SQLException{
		DataSet ds=new DataSet();
		
		/*JSONObject webServer = new JSONObject();
		webServer.put("serverName", Tools.webPort);//服务器名称
		webServer.put("startTime", Tools.formatDate(new Time(Tools.startTime)));//启动时间
		webServer.put("runTime", (System.currentTimeMillis()-Tools.startTime)/60000);//运行时间
		webServer.put("vesion", VersionUtil.getInstance().toString());//程序版本
		data.put("webServer", webServer);
		
		JSONArray threadInfo = new JSONArray();
		JSONObject thread = new JSONObject();
		Map<String,ThreadPoolManager> mapPool = ThreadPoolManager.getInstance().getInstanceName();
		Iterator<String> itPool = mapPool.keySet().iterator();
		while(itPool.hasNext()){
			String key = itPool.next();
			ThreadPoolManager pool = mapPool.get(key);
			long total = pool.getTotalTaskNumber();
			long finish = pool.getFinishTaskNumber();
			
			thread.put("ThreadName", key);
			thread.put("TotalThread", pool.getTotalThreadNum());
			thread.put("WaitThread", pool.getWaitThreadNum());
			thread.put("TotalRequest",total);
			thread.put("FinishRequest", finish);
			thread.put("LeftRequest", total-finish);
			threadInfo.add(thread);			
			//sb.append("工作线程 "+key+" 总队工作队列数为："+pool.getTotalThreadNum()+",可用队列为："+pool.getWaitThreadNum()+"，总请求数："+total+",已经处理了"+finish+",还剩下"+(total-finish)+"个任务没处理<br/>");			
		}
		data.put("threadInfo",threadInfo);//data中存放thread数据
		
		
		JSONArray datasourceInfo = new JSONArray();
		JSONObject datasource = new JSONObject();
		Map<String,DataSourceFactory> dbMap = DataSourceFactory.getInstanceMap();
		Iterator<String> it = (Iterator<String>) dbMap.keySet().iterator();
		while(it.hasNext()){
			String sKey = it.next();
			DataSourceFactory s = dbMap.get(sKey);
			if(s != null){
				String str=s.statusInfo();
				String str1=str.substring(str.indexOf("【")+1, str.indexOf("】"));
				String str2=str.substring(str.indexOf("：")+1, str.indexOf(","));
				String str3=str.substring(str.lastIndexOf(",")-1, str.lastIndexOf(","));
				String str4=str.substring(str.length()-1, str.length());
				
				datasource.put("DataSourceName", str1);
				datasource.put("TotalConnect", str2);
				datasource.put("UsedConnect", str3);
				datasource.put("NotUsedConnect", str4);
				datasourceInfo.add(datasource);
			}
		}
		data.put("datasourceInfo",datasourceInfo);//data中存放datasource数据
*/		
		Object[] f = Tools.getJvmMonitor().getFIFO();
		JSONArray jvm=JSONArray.fromObject(f);		
		data.put("jvmInfo",jvm);
		data.put("onlineUserNum",UserNumPlugin.onlineUserNum);
		
/*      Object[] m = HttpLog.getFIFO();		
		JSONArray url=JSONArray.fromObject(m);
		data.put("urlInfo",url);  //data中存放url数据
*/		
		JSONObject dataInfo = JSONObject .fromObject(data);
		ds.setData(Tools.jsonToBytes(dataInfo));
		this.write(ds.toJson());
		return false;
		
		//return true;
	}
}