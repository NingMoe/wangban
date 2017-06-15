package app.icity.project;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class ServiceInstitutionCmd extends BaseQueryCommand {

	public DataSet query(ParameterSet pSet){
		return ServiceInstitutionDao.getInstance().query(pSet);
	}
	public DataSet queryPhoto(ParameterSet pSet){
		return ServiceInstitutionDao.getInstance().queryPhoto(pSet);
	}
	public DataSet queryTraffic(ParameterSet pSet){
		return ServiceInstitutionDao.getInstance().queryTraffic(pSet);
	}
	public DataSet queryWorktime(ParameterSet pSet){
		return ServiceInstitutionDao.getInstance().queryWorktime(pSet);
	}
	public DataSet queryMiddleDept(ParameterSet pSet){
		return ServiceInstitutionDao.getInstance().queryMiddleDept(pSet);
	}
	
	public DataSet queryBasic(ParameterSet pSet){
		return ServiceInstitutionDao.getInstance().queryBasic(pSet);
	}
}
