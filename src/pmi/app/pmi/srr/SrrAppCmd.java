package app.pmi.srr;

import plugin.SrrServerPlugin;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class SrrAppCmd extends BaseQueryCommand{
	public DataSet getAppList(ParameterSet pSet) {
		return SrrAppDao.getInstance().getAppList(pSet);
	}
	
	public DataSet getAppOfServerList(ParameterSet pSet) {
		return SrrAppDao.getInstance().getAppOfServerList(pSet);
	}
	
	public DataSet addApp(ParameterSet pSet) {
		DataSet ds = SrrAppDao.getInstance().addApp(pSet);
		SrrServerPlugin.getInstance().reset();
		return ds;
	}
	
	public DataSet updateApp(ParameterSet pSet) {
		DataSet ds = SrrAppDao.getInstance().updateApp(pSet);
		SrrServerPlugin.getInstance().reset();
		return ds;
	}
	
	public DataSet delete(ParameterSet pSet) {
		DataSet ds = SrrAppDao.getInstance().deleteApp(pSet);
		SrrServerPlugin.getInstance().reset();
		return ds;
	}
}
