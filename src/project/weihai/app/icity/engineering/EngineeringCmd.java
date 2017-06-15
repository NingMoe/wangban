package weihai.app.icity.engineering;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

/**
 * 工程建设投资 lhy
 * 
 * @author lenovo
 * 
 */
public class EngineeringCmd extends BaseQueryCommand {
	/**
	 * 2.17	根据区划和阶段获取流程
	 * @param pSet
	 * @return
	 */
	public DataSet getFlowByStageAndRegion(ParameterSet pSet){
		return EngineeringDao.getInstance().getFlowByStageAndRegion(pSet);
	}
	/**
	 * 2.16	获取办事指南信息
	 * @return
	 */
	public DataSet getFlowService(ParameterSet pSet){
		return EngineeringDao.getInstance().getFlowService(pSet);
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
	 * 2.18	提交申报信息（威海办事事项单独传一下）
	 * @param pSet
	 * @return
	 */
	public DataSet submitReportDetailsWeiHai(ParameterSet pSet) {
		UserInfo userInfo = this.getUserInfo(pSet);
		String uid = userInfo.getUid()+"";		
		pSet.setParameter("uid", uid);
		return EngineeringDao.getInstance().submitReportDetailsWeiHai(pSet);
	}
	/**
	 * 提交信息到库  
	 * @param pSet
	 * @return Modify：
	 */
	public DataSet savePararallil_biz_base(ParameterSet pSet) {
		UserInfo userInfo = this.getUserInfo(pSet);
		String uid = userInfo.getUid()+"";		
		pSet.setParameter("uid", uid);
		return EngineeringDao.getInstance().savePararallil_biz_base(pSet);
	}
	/**
	 * 获取工程
	 * @param pSet
	 * @return
	 */
	public DataSet getEnginProjectByBizId(ParameterSet pSet) {
		return EngineeringDao.getInstance().getEnginProjectByBizId(pSet);
	}
}
