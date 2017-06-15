package app.pmi.dict;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class DictCmd extends BaseQueryCommand {
	public DataSet getList(ParameterSet pset){
		return DictDao.getInstance().getList(pset);
	}
}
