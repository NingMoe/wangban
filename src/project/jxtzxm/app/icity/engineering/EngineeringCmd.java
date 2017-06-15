package jxtzxm.app.icity.engineering;

import jxtzxm.app.icity.engineering.EngineeringDao;

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
	 * 江西项目提交项目资料
	 * @param pSet
	 * @return
	 */
	public DataSet saveProject(ParameterSet pSet) {
		return EngineeringDao.getInstance().saveProject(pSet);
	}
	/**
	 * 工程建设暂存
	 * @param pSet
	 * @return
	 */
	public DataSet saveDraftReportDetails(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		DataSet ds = EngineeringDao.getInstance().savePararallil_biz_base(pSet);		
		return ds;
	}
	/**
	 * 工程建设 提交申报信息 江西
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
	/**
	 * 并联审批办件详情
	 * @param pSet
	 * @return
	 */
	public DataSet getEnginBaseInfo(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		String uname = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUserName());
		pSet.setParameter("uid", uid);
		pSet.setParameter("uname", uname);
		return EngineeringDao.getInstance().getEnginBaseInfo(pSet);
	}
	/**
	 * 咨询信息推送接口
	 * @param pSet
	 * @return
	 */
	public DataSet consultmsg(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		return EngineeringDao.getInstance().consultmsg(pSet);
	}	
	/**
	 * 获取咨询列表
	 * @param pSet
	 * @return
	 */
	public DataSet getParallelConsultlist(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("ucid", uid);
		return EngineeringDao.getInstance().getParallelConsultlist(pSet);
	}
}
