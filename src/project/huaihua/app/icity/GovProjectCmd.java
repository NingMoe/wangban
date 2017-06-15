package huaihua.app.icity;

import huaihua.app.icity.GovProjectDao;

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
	
	public DataSet getChannelListByName(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getChannelListByName(pSet);
	}
	
	public DataSet getContentList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getContentList(pSet);
	}
	
	public DataSet getContent(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getContent(pSet);
	}
	
	public DataSet getInitList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getPowerList(pSet);
	}
	public DataSet getQuestion(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getQuestion(pSet);
	}
	public DataSet getQuestionById(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getQuestionById(pSet);
	}
	
	//党务公开
	public DataSet getDwgk(ParameterSet pSet){
		return GovProjectDao.getInstance().getDwgk(pSet);
	}
	public DataSet getContent_hczw(ParameterSet pSet){
		return GovProjectDao.getInstance().getContent_hczw(pSet);
	}
	
}