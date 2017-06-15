package hlj_qqheNew.app.icity.engineering;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hlj_qqheNew.app.icity.engineering.EngineeringDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/**
 * 工程建设投资 lhy
 * 
 * @author lenovo
 * 
 */
public class EngineeringCmd extends BaseQueryCommand {
	/**
	 * 获取字典信息
	 * @return
	 */
	public DataSet getDictInfo(ParameterSet pSet){
		return EngineeringDao.getInstance().getDictInfo(pSet);
	}
	/**
	 * 提交项目资料
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet saveProject(ParameterSet pSet) {
		return EngineeringDao.getInstance().saveProject(pSet);
	}
	public DataSet getProject(ParameterSet pSet) {
		return EngineeringDao.getInstance().getProject(pSet);
	}
	/**
	 * 删除项目
	 * @param pSet
	 * @return
	 */
	public DataSet delProject(ParameterSet pSet) {
		return EngineeringDao.getInstance().delProject(pSet);
	}
	public DataSet getEnginProject(ParameterSet pSet) {
		return EngineeringDao.getInstance().getEnginProject(pSet);
	}

	public DataSet getEnginProjecta(ParameterSet pSet) {
		return EngineeringDao.getInstance().getEnginProjecta(pSet);
	}

	public DataSet getEnginBase(ParameterSet pSet) {
		return EngineeringDao.getInstance().getEnginBase(pSet);
	}

	/**
	 * 投资获取阶段
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getStage(ParameterSet pSet) {
		return EngineeringDao.getInstance().getStage(pSet);
	}
	/**
	 * 投资获取阶段流程 
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getFlow(ParameterSet pSet) {
		return EngineeringDao.getInstance().getFlow(pSet);
	}
	/**
	 * 投资获取阶段流程 (聊城项目)
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getFlowByFlowType(ParameterSet pSet) {
		return EngineeringDao.getInstance().getFlowByFlowType(pSet);
	}
	/**
	 * 2.5	获取项目信息	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectDetails(ParameterSet pSet) {
		return EngineeringDao.getInstance().getProjectDetails(pSet);
	}
	/**
	 * 获取申报信息(表单、事项)
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getReport(ParameterSet pSet) {
		return EngineeringDao.getInstance().getReport(pSet);
	}

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
	/**
	 * 工程建设暂存 by kongweisong
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet saveDraftReportDetails(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		DataSet _ds = EngineeringDao.getInstance()
				.savePararallil_biz_base(pSet);
		if (_ds.getState() == 1) {
			ds.setMessage(_ds.getMessage());
			ds.setData(_ds.getData());
		} else {
			ds.setMessage(null);
		}
		return ds;
	}

	// 获取上传附件存于数据库中的信息。
	public DataSet getResourceList(ParameterSet pSet) {
		return EngineeringDao.getInstance().getResourceList(pSet);
	}

	/**
	 * 获取项目信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectInfoById(ParameterSet pSet) {
		return EngineeringDao.getInstance().getProjectInfoById(pSet);
	}
	/**
	 * 获取暂存信息	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getStagingInfo(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		return EngineeringDao.getInstance().getStagingInfo(pSet);
	}
	/**
	 * 获取投资项目业务列表
	 * @param pSet
	 * @return
	 */
	public DataSet getEnginBaseList(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(
				(String) pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		return EngineeringDao.getInstance().getEnginBaseList(pSet);
	}
	
	public DataSet getFlowImageMessage(ParameterSet pSet){ 
		return EngineeringDao.getInstance().getFlowImageMessage(pSet);
	}
}
