package km.app.icity.govservice;
/**
@Author： 赵宏明
@Version：
@Since:
@Create at: 2015年10月6日 上午10:44:46 
@Description:
**/


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovServiceCmd extends BaseQueryCommand{
	
	public DataSet getInvestmentList(ParameterSet pSet){
		return GovServiceDao.getInstance().getInvestmentList(pSet);
	}

}
