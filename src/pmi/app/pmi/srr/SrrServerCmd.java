package app.pmi.srr;

import net.sf.json.JSONArray;
import plugin.SrrServerPlugin;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class SrrServerCmd extends BaseQueryCommand{
	public DataSet initServerMap(ParameterSet pSet) {
		return SrrServerDao.getInstance().getServerAndAppList(pSet);
	} 
	
	public DataSet getServerStatus(ParameterSet pSet){
		return SrrServerPlugin.getInstance().getServerStatus();
	}
	
	public DataSet getAppsOfServer(ParameterSet pSet) {
		DataSet dSet = SrrServiceDao.getInstance().getServiceCount(new ParameterSet());
		JSONArray sc = dSet.getJAData();
		pSet.setParameter("serviceCount", sc);
		return SrrServerPlugin.getInstance().getAppsOfServer(pSet);
	}
	
	public DataSet getMonitorOfApp(ParameterSet pSet) {
		return SrrServerPlugin.getInstance().getMonitorOfApp(pSet);
	}

	
	/**
	 * 1.更新APP（cpu，内存）、及server的信息
	 * 2.存储service的集合
	 * @param pSet
	 * @return
	 */
	public DataSet heartbeat(ParameterSet pSet){
		SrrServiceDao.getInstance().batchAddService(pSet);
		return SrrServerPlugin.getInstance().heartbeat(pSet);
	}
	/**
	 * 获取服务列表
	 * @param pSet
	 * @return
	 */
	public DataSet getServiceList(ParameterSet pSet) {
		return SrrServiceDao.getInstance().getServiceList(pSet);
	}
	
	public DataSet getServicesTime(ParameterSet pSet) {
		return SrrServiceDao.getInstance().getServicesTime(pSet);
	}
	
	public DataSet getServicesByTime(ParameterSet pSet) {
		return SrrServiceDao.getInstance().getServicesByTime(pSet);
	}
	
	public DataSet getServerList(ParameterSet pSet) {
		return SrrServerDao.getInstance().getServerList(pSet);
	}
	
	public DataSet addServer(ParameterSet pSet) {
		DataSet dSet = SrrServerDao.getInstance().addServer(pSet);
		SrrServerPlugin.getInstance().reset();
		return dSet;
	}
	
	public DataSet updateServer(ParameterSet pSet) {
		DataSet dSet = SrrServerDao.getInstance().updateServer(pSet);
		SrrServerPlugin.getInstance().reset();
		return dSet; 
	}
	
	public DataSet delete(ParameterSet pSet) {
		DataSet dSet = SrrServerDao.getInstance().deleteServer(pSet);
		SrrServerPlugin.getInstance().reset();
		return dSet;
	}
}
