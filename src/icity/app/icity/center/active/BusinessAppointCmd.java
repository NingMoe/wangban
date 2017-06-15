package app.icity.center.active;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

public class BusinessAppointCmd extends BaseQueryCommand {
	public DataSet getBusinessAppointList(ParameterSet pSet){
		UserInfo userInfo = this.getUserInfo(pSet);
		return BusinessAppointDao.getInstance().getBusinessAppointList(pSet,userInfo);
	}
}
