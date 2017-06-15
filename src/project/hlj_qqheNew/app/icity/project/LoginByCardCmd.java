package hlj_qqheNew.app.icity.project;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;


public class LoginByCardCmd extends BaseQueryCommand{
	
	public DataSet queryCard(ParameterSet pSet){ 
		return LoginByCardDao.getInstance().queryCard(pSet);
	}
	

	
}
