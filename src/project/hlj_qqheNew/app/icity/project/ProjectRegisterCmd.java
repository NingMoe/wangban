package hlj_qqheNew.app.icity.project;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class ProjectRegisterCmd extends BaseQueryCommand{
	
	public DataSet register_per(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().register_per(pSet);
	}
	
	public DataSet register_org(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().register_org(pSet);
	}
	
	public DataSet message_send(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().message_send(pSet);
	}
	public DataSet password_cz(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().password_cz(pSet);
	}
	
	public DataSet check_username(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().check_username(pSet);
	}
	
	public DataSet check_cardno(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().check_cardno(pSet);
	}
 
	public DataSet isTrue(ParameterSet pSet){
		return ProjectRegisterDao.getInstance().isTrue(pSet);
	}
	
	public static DataSet sendGet(String id, String phonestr, String message, String sendtime){
		return SendMessage.getInstance().sendGet(id,phonestr,message,sendtime);
	}
	

}
