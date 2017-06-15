package hlj_qqheNew.app.icity.project;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;


public class ZxyyCmd extends BaseQueryCommand{
	
	public DataSet getQueryDept(ParameterSet pSet){ 
		return ZxyyDao.getInstance().getQueryDept(pSet);
	}
	
	public DataSet getQueryDeptTime(ParameterSet pSet){ 
		return ZxyyDao.getInstance().getQueryDeptTime(pSet);
	}
	
	public DataSet getQueryDeptDate(ParameterSet pSet){ 
		return ZxyyDao.getInstance().getQueryDeptDate(pSet);
	}
	
	public DataSet submitYy(ParameterSet pSet){ 
		return ZxyyDao.getInstance().submitYy(pSet);
	}
	
	public DataSet sendPhoneCode(ParameterSet pSet){ 
		return ZxyyDao.getInstance().sendPhoneCode(pSet);
	}

	
}
