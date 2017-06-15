package app.icity.pro;

import app.icity.project.ProjectBusinessStatDao;
import app.icity.project.ProjectIndexDao;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import app.icity.pro.ProDao;

/**
 * 投资项目审批 lhy 15 11 27
 * @author lenovo
 *
 */
public class ProCmd extends BaseQueryCommand {
	/**
	 * 获取字典信息
	 * @return
	 */
	public DataSet getDictInfo(ParameterSet pSet){
		return ProDao.getInstance().getDictInfo(pSet);
	}
	/**
	 * 获取事项信息
	 * @return
	 */
	public DataSet getRule(ParameterSet pSet){
		return ProDao.getInstance().getRule(pSet);
	}
	/**
	 * 提交项目信息
	 * @return
	 */
	public DataSet submitProjectMessage(ParameterSet pSet){
		return ProDao.getInstance().submitProjectMessage(pSet);
	}
	/**
	 * 业务申请
	 * @return
	 */
	public DataSet applyBusiness(ParameterSet pSet){
		return ProDao.getInstance().applyBusiness(pSet);
	}
	/**
	 * 获取事项基本信息
	 * @return
	 */
	public DataSet getAllItemInfo(ParameterSet pSet){
		return ProDao.getInstance().getAllItemInfo(pSet);
	}
	/**
	 * 获取项目列表
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectList(ParameterSet pSet){
		return ProDao.getInstance().getProjectList(pSet);
	}
	/**
	 * 获取项目列表 调投资接口
	 * @param pSet
	 * @return
	 */
	public DataSet getTZProjectList(ParameterSet pSet){
		return ProDao.getInstance().getTZProjectList(pSet);
	}
	/**
	 * 注册后存表
	 * @param pSet
	 * @return
	 */
	public DataSet submitProjectMessageSave(ParameterSet pSet){
		UserInfo user = this.getUserInfo(pSet);
		pSet.put("uid", user.getUid()+"");
		pSet.put("ucname", user.getUserName());
		return ProDao.getInstance().submitProjectMessageSave(pSet);
	}
	/**
	 * 我的项目
	 * @param pSet
	 * @return
	 */
	public DataSet getUserJDCX(ParameterSet pSet){
		return ProDao.getInstance().getUserJDCX(pSet);
	}
	/**
	 * 我的项目
	 * @param pSet
	 * @return
	 */
	public DataSet getJDCX(ParameterSet pSet){
		return ProDao.getInstance().getJDCX(pSet);
	}
	/**
	 * 进度查询
	 * @return
	 */
	public DataSet queryBusiness(ParameterSet pSet){
		return ProDao.getInstance().queryBusiness(pSet);
	}
	/**
	 * 查询上传过报告的项目列表
	 * @return
	 */
	public DataSet queryIproReport(ParameterSet pSet){
		UserInfo userinfo = this.getUserInfo(pSet);
		pSet.put("uid", userinfo.getUid()+"");
		return ProDao.getInstance().queryIproReport(pSet);
	}
	/**
	 * 获取项目根据sblsh
	 * @param pSet
	 * @return
	 */
	public DataSet getContent(ParameterSet pSet){
		return ProDao.getInstance().getContent(pSet);
	}
	
	private final static String CACHE_KEY_FLAG = "ProCmd_showBusiness";
	/**
	 * 获取办件公示
	 * @param pSet
	 * @return
	 */
	public DataSet showBusiness(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "ProCmd_showBusiness_" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds =  ProDao.getInstance().showBusiness(pSet);
					if (ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		return ds;
	}
	/**
	 * 报告上传
	 * @param pSet
	 * @return
	 */
	public DataSet submitProjectProcess(ParameterSet pSet){
		UserInfo userinfo = this.getUserInfo(pSet);
		pSet.put("uid", userinfo.getUid()+"");
		return ProDao.getInstance().submitProjectProcess(pSet);
	}
	/**
	 * 获取投资项目行业目录permitItemCode
	 * @param pSet
	 * @return
	 */
	public DataSet getCatalogInfo(ParameterSet pSet){
		return ProDao.getInstance().getCatalogInfo(pSet);
	}
	public DataSet submitConsult(ParameterSet pSet){
		return ProDao.getInstance().submitConsult(pSet);
	}
	public DataSet getInfoByProjectCode(ParameterSet pSet){
		return ProDao.getInstance().getInfoByProjectCode(pSet);
	}
	
	/**
	 * 提交无偿代理
	 * @param pSet
	 * @return
	 */
	public DataSet submitWCDL(ParameterSet pSet){
		return ProDao.getInstance().submitWCDL(pSet);
	}
	/**
	 * 获取事项组信息（投资项目-联合审批）
	 * @return
	 */
	public DataSet queryItemGroup(ParameterSet pSet){
		return ProDao.getInstance().queryItemGroup(pSet);
	}
	
	/**
	 * 我的项目列表
	 * @param pSet
	 * @return
	 */
	public DataSet getMyProjectList(ParameterSet pSet){
		return ProDao.getInstance().getMyProjectList(pSet);
	}
	
	/**
	 * 联合审批提交申请
	 * @param pSet
	 * @return
	 */
	public DataSet applyUnionBusiness(ParameterSet pSet){
		
		UserInfo userinfo = this.getUserInfo(pSet);
		pSet.put("uid", userinfo.getUid()+"");
		
		return ProDao.getInstance().applyUnionBusiness(pSet);
	}
	
	/**
	 * 获取事项组列表
	 * @param pSet
	 * @return
	 */
	public DataSet getItemGroupList(ParameterSet pSet){
		return ProDao.getInstance().getItemGroupList(pSet);
	}
	
	/**
	 * 联审业务回复结果查询
	 * @param pSet
	 * @return
	 */
	public DataSet queryUnionIsHandle(ParameterSet pSet){
		return ProDao.getInstance().queryUnionIsHandle(pSet);
	}
	
	/**
	 * 联审业务材料提交
	 * @param pSet
	 * @return
	 */
	public DataSet submitUnionCailiao(ParameterSet pSet){
		return ProDao.getInstance().submitUnionCailiao(pSet);
	}
	
	/**
	 * 投资项目分类统计
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectCount(ParameterSet pSet){
		return ProDao.getInstance().getProjectCount(pSet);
	}
	
	/**
	 * 获取投资项目列表-业务中心
	 * @param pSet
	 * @return
	 */
	public DataSet getProList(ParameterSet pSet){
		return ProDao.getInstance().getProList(pSet);
	}
	/**
	 * 根据项目ID查询单体业务
	 * @param pSet
	 * @return
	 */
	public DataSet getBussinessByProjectId(ParameterSet pSet){
		return ProDao.getInstance().getBussinessByProjectId(pSet);
	}
	
	/**
	 * 根据项目ID查询联审业务
	 * @param pSet
	 * @return
	 */
	public DataSet geUnionBizByProjectId(ParameterSet pSet){
		return ProDao.getInstance().geUnionBizByProjectId(pSet);
	}
	/**
	 * 项目代理信息
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectAgent(ParameterSet pSet){
		return ProDao.getInstance().getProjectAgent(pSet);
	}
	/**
	 * 联合踏勘记录
	 * @param pSet
	 * @return
	 */
	public DataSet getReconnaissance(ParameterSet pSet){
		return ProDao.getInstance().getReconnaissance(pSet);
	}
	/**
	 * 联合踏勘通知反馈信息
	 * @param pSet
	 * @return
	 */
	public DataSet getFeedback(ParameterSet pSet){
		return ProDao.getInstance().getFeedback(pSet);
	}
	/**
	 * 联合踏勘意见反馈信息
	 * @param pSet
	 * @return
	 */
	public DataSet getFeedbackOpinion(ParameterSet pSet){
		return ProDao.getInstance().getFeedbackOpinion(pSet);
	}
	/**
	 * 联合踏勘会议纪要
	 * @param pSet
	 * @return
	 */
	public DataSet getSummary(ParameterSet pSet){
		return ProDao.getInstance().getSummary(pSet);
	}
	/**
	 * 项目报告
	 * @param pSet
	 * @return
	 */
	public DataSet queryProjectReport(ParameterSet pSet){
		return ProDao.getInstance().queryProjectReport(pSet);
	}
	/**
	 * 踏勘通知信息
	 * @param pSet
	 * @return
	 */
	public DataSet getReconInfoById(ParameterSet pSet){
		return ProDao.getInstance().getReconInfoById(pSet);
	}
	/**
	 * 获取材料信息
	 * @param pSet
	 * @return
	 */
	public DataSet getFileByBizId(ParameterSet pSet){
		return ProDao.getInstance().getFileByBizId(pSet);
	}
	/**
	 * 咨询结果信息
	 * @param pSet
	 * @return
	 */
	public DataSet queryConsultResult(ParameterSet pSet){
		return ProDao.getInstance().queryConsultResult(pSet);
	}
	
	/**
	 * 获取项目
	 * @param pSet
	 * @return
	 */
	public DataSet getProject(ParameterSet pSet){
		return ProDao.getInstance().getProject(pSet);
	}
	
	/**
	 * 项目关联插入
	 * @param pSet
	 * @return
	 */
	public DataSet insertIproIndex(ParameterSet pSet){
		UserInfo userinfo = this.getUserInfo(pSet);
		pSet.put("uuid", userinfo.getUid()+"");
		pSet.put("ucname", userinfo.getUserName()+"");
		return ProDao.getInstance().insertIproIndex(pSet);
	}
	
	/**
	 * 获取一次性告知
	 * @param pSet
	 * @return
	 */
	public DataSet queryOnceByProjectId(ParameterSet pSet){
		return ProDao.getInstance().queryOnceByProjectId(pSet);
	}
	/**
	 * 事项组办事指南
	 * @param pSet
	 * @return
	 */
	public DataSet getServiceGuideById(ParameterSet pSet){
		return ProDao.getInstance().getServiceGuideById(pSet);
	}
}
