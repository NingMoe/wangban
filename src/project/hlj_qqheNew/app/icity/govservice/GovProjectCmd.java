package hlj_qqheNew.app.icity.govservice;

//import app.icity.govservice.GovProjectDao;

import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd{ 
	//获取权力清单
	public DataSet getInitList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getPowerList(pSet);
	}
	
	public DataSet getPermissionList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getMattersList2(pSet);
	}
	
	public DataSet getMattersList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getMattersList2(pSet);
	}
}