package app.icity.govservice;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;


public class AESCmd extends BaseQueryCommand{ 

	public DataSet getData(ParameterSet pSet){
		return AESDao.getInstance().aes(pSet);
	}
	
}