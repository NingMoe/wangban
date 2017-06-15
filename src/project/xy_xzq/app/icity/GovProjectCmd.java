package xy_xzq.app.icity;

import xy_xzq.app.icity.GovProjectDao;

import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd extends app.icity.govservice.GovProjectCmd{ 

	
	public DataSet getRdbs(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getRdbs(pSet);
	}
	
	public DataSet getChannelList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getChannelList(pSet);
	}
	
	public DataSet getContentList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getContentList(pSet);
	}
	public DataSet getContenttolList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getContenttolList(pSet);
	}
	public DataSet getContenttplList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getContenttplList(pSet);
	}
	
	public DataSet getContent(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getContent(pSet);
	}
	
	public DataSet getInitList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getPowerList(pSet);
}
	
}