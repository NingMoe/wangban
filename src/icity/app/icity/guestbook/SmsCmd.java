package app.icity.guestbook;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class SmsCmd extends BaseQueryCommand {	
	public DataSet getVerifyCode(ParameterSet pSet){
		return SmsDao.getInstance().getVerifyCode(pSet);
	}
}

