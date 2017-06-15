package app.pmi.id;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class IdCmd extends BaseQueryCommand {
	public DataSet getList(ParameterSet pset){
		return IdDao.getInstance().getList(pset);
	}
	
	public DataSet insert(ParameterSet pset){
		return IdDao.getInstance().insert(pset);
	} 
	public DataSet update(ParameterSet pset){
		return IdDao.getInstance().update(pset);
	} 
	
	public DataSet delete(ParameterSet pset){
		return IdDao.getInstance().delete(pset);
	}
}
