package zb.app.icity.govservice;

import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd extends app.icity.govservice.GovProjectCmd{ 
	/**
	 * 获取事项
	 * @param pSet
	 * @return
	 */
	
	public DataSet getContentInfo(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentInfo(pSet);
	}
	 public DataSet getBusinessStat2(ParameterSet pSet){
			return GovProjectDao.getInstance().getBusinessStatDao2(pSet);
		}
	public DataSet getContentInfoOfEventPublic(ParameterSet pSet) {
		return GovProjectDao.getInstance().getContentInfoOfEventPublic(pSet);
	}
	public DataSet getContentInfoOfEventName(ParameterSet pSet) {
		return GovProjectDao.getInstance().getContentInfoOfEventName(pSet);
	}
	public DataSet getContentInfoOfGQPublic(ParameterSet pSet) {
		return GovProjectDao.getInstance(). getContentInfoOfGQPublic( pSet);
	}
	public DataSet getBusinessStatCmd(ParameterSet pSet){
		return GovProjectDao.getInstance().getBusinessStatDao(pSet);
	}
}