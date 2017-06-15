package fz.app.icity.engineering;

import fz.app.icity.engineering.EngineeringDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/**
 * 工程建设投资 
 * 
 * @author lenovo
 * 
 */
public class EngineeringCmd extends BaseQueryCommand {
	/**
	 * 工程建设 提交申报信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitReportDetails(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		String uname = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUserName());
		pSet.setParameter("uid", uid);
		pSet.setParameter("uname", uname);
		return EngineeringDao.getInstance().submitReportDetails(pSet);
	}
}
