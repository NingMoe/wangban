package cq.app.icity.govservice;

import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd extends app.icity.govservice.GovProjectCmd{ 
	//获取权力清单
	public DataSet getInitList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getPowerList(pSet);
}
}