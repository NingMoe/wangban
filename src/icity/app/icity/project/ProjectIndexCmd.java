package app.icity.project;


import net.sf.json.JSONObject;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;

public class ProjectIndexCmd extends BaseQueryCommand{	
	
	public DataSet getBusinessIndexList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexList(pSet);
	}
	//获取待投诉的件-已投诉的件不再投诉
	public DataSet getBusinessComplaint(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessComplaint(pSet);
	}
	//获取企业设立列表
	public DataSet getBusinessEnterList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessEnterList(pSet);
	}
	
	public DataSet BusinessENTERByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessENTERByNameAndId(pSet);
	}
	//单体事项、企业设立、工程建设联合查询
	public DataSet UnionSearch(ParameterSet pSet){
		return ProjectIndexDao.getInstance().UnionSearch(pSet);
	}
	
	/***
	 * 获取各种状态的总条数
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexCount(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexCount(pSet);
	}
	
	/**
	 * 更新备注
	 */	
	public DataSet updateRemark(ParameterSet pset){
		return ProjectIndexDao.getInstance().updateRemark(pset);
	}
	
	/**
	 * 获取总数
	 * @return
	 */
	public DataSet getTotal(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTotal(pSet);
	}
	
	/**
	 *  按事项类型统计
	 * @return
	 */
	public DataSet getTypeTotal(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTypeTotal(pSet);
	}
	
	/**
	 *按业务状态统计
	 * @return
	 */
	public DataSet getStateTotal(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getStateTotal(pSet);
	}
	
	/**
	 * @Description: 根据申办流水号和申办人查询办件
	 * @return DataSet    返回类型
	 * @since 2013-8-22 
	 */
	public DataSet BusinessQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexAllList(pSet);
	}	
	
	/**
	 * @Description: 根据申办流水号和申办人查询表单信息
	 * @return DataSet    返回类型
	 * @since 2015-8-10
	 * @author liuyq
	 */
	public DataSet formInfoQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().formInfoQueryByNameAndId(pSet);
	}
	/**
	 * 通过申办流水号从审批获取ems业务单号
	 * @param pSet
	 * @return
	 */
	public DataSet getPostInfo(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getPostInfo(pSet);
	}
	/**
	 * @Description: 根据申办流水号和申办人查询流程信息
	 * @return DataSet    返回类型
	 * @since 2015-8-13
	 * @author liuyq
	 */
	public DataSet materialListQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().materialListQueryByNameAndId(pSet);
	}
	
	//办件查询，申办流水号+申请人名称、单位、申办主题
	public DataSet BusinessProgressQuery(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessProgressQuery(pSet);
	}
	//办件查询，申办流水号+密码
	public DataSet BusinessProgressQueryByPassword(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessProgressQueryByPassword(pSet);
	}
	//办件结果公示
	public DataSet BusinessNoticeQuery(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessNoticeQuery(pSet);
	}
	/**
	 * 根据申办流水号从审批获取办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfoFromApprovalBySblsh(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessInfoFromApprovalBySblsh(pSet);
	}
	
	public DataSet deleteBusiness(ParameterSet pSet) {
		return ProjectIndexDao.getInstance().deleteBusiness(pSet);
	}
	
	public DataSet getBzbqLog(ParameterSet pSet) {
		return ProjectIndexDao.getInstance().getBzbqLog(pSet);
	}
	//撤回
	public DataSet updateBusinessIndex(ParameterSet pSet){
		
		UserInfo user = this.getUserInfo(pSet);

		if(null!=user){
			pSet.put("userName", user.getUserName());
			pSet.put("usertype",user.getType());
			pSet.put("userid",user.getNo());
		}else{
			pSet.put("userName", "");
		}
		
		return ProjectIndexDao.getInstance().updateBusinessIndex(pSet);
	}
	/**
	 * 根据申办流水号从审批获取办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfoFromApproval(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessInfoFromApproval(pSet);
	}
	
	/**
	 * 访问量统计部门
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsDept(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTrafficStatisticsDept(pSet);
	}
	/**
	 * 访问量统计分厅
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsHall(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTrafficStatisticsHall(pSet);
	}
	/**
	 * 访问量统计分厅--当前分厅总数
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsHallNow(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTrafficStatisticsHallNow(pSet);
	}
	/**
	 * 新余热门服务
	 * @param pSet
	 * @return
	 */
	public DataSet getPopularServices(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getPopularServices(pSet);
	}
	
	/***
	 * 获取缴费单列表(重庆)
	 * @param pSet
	 * @return
	 */
	public DataSet getBillPaymentList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBillPaymentList(pSet);
	}
	/***
	 * 检测是否缴费(重庆)
	 * @param pSet
	 * @return
	 */
	public DataSet checkchargestate(ParameterSet pSet){
		return ProjectIndexDao.getInstance().checkchargestate(pSet);
	}
	
	/***
	 * 确认缴费(重庆)
	 * @param pSet
	 * @return
	 */
	public DataSet confirmPay(ParameterSet pSet){
		return ProjectIndexDao.getInstance().confirmPay(pSet);
	}
	
	/***
	 * 缴款成功更新外网数据状态
	 * @param pSet
	 * @return
	 */
	public DataSet updateBillState(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updateBillState(pSet);
	}
	/***
	 * 缴款日志记录
	 * @param pSet
	 * @return
	 */
	public DataSet updateBillLog(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updateBillLog(pSet);
	}
	
	/***
	 * 更新商户订单号
	 * @param pSet
	 * @return
	 */
	public DataSet updateOrderId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updateOrderId(pSet);
	}
	
	/***
	 * 推送审批缴费成功信息
	 * @param pSet
	 * @return
	 */
	public DataSet confirmcharge(ParameterSet pSet){
		return ProjectIndexDao.getInstance().confirmcharge(pSet);
	}
	
	/***
	 * 获取Mac
	 * @param pSet
	 * @return
	 */
	public DataSet getMacInfo(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getMacInfo(pSet);
	}
	/***
	 * 查询邮寄件单号列表（威海市）
	 * @param pSet
	 * @return
	 */
	public DataSet getMailInformationList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getMailInformationList(pSet);
	}
	/***
	 * 查询邮寄件物流信息（威海市）
	 * （调用ems接口）
	 * @param pSet
	 * @return
	 */
	public DataSet getLogisticsDetailByExpressId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getLogisticsDetailByExpressId(pSet);
	}
	/***
	 * 通过身份证号查询审批窗口收件列表（威海市）
	 * （调用审批接口）
	 * @param pSet
	 * @return
	 */
	public DataSet getWindowBusinessListByIdCard(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getWindowBusinessListByIdCard(pSet);
	}
	/**
	 * 查询联审联批 流程（项目查询）  威海使用
	 * @param pSet
	 * @return
	 */
	public DataSet getParallelList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getParallelList(pSet);
	}
	/**
	 * 查询联审联批 事项查询（事项查询）  威海使用
	 * @param pSet biz_id
	 * @return
	 */
	public DataSet getItemList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getItemList(pSet);
	}
	/**
	 * 获取联审联批 事项信息（威海）
	 * @param pSet id
	 * @return
	 */
	public DataSet parallelBusinessIndexById(ParameterSet pSet){
		return ProjectIndexDao.getInstance().parallelBusinessIndexById(pSet);
	}
	/**
	 * 联审联批 删除办件（威海）
	 * @param pSet
	 * @return
	 */
	public DataSet deleteLslpItem(ParameterSet pSet){
		return ProjectIndexDao.getInstance().deleteLslpItem(pSet);
	}
	/**
	 * 联审联批 撤回办件（威海）
	 * @param pSet
	 * @return
	 */
	public DataSet updateLslpBusinessIndex(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updateLslpBusinessIndex(pSet);
	}
	/**
	 * 获取审批表单数据信息(商丘)
	 * @param pSet 
	 * @return
	 */
	public DataSet getFormStandardData(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getFormStandardData(pSet);
	}
	/**
	 * 绑定运管处信息(威海)
	 * @param pSet 
	 * @return
	 */
	public DataSet updateBusinessInfo(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updateBusinessInfo(pSet);
	}
	/**
	 * 访问量统计
	 * @param pset
	 * @return
	 */
	public DataSet getPubHitsCount(ParameterSet pSet) {
		return ProjectIndexDao.getInstance().getPubHitsCount(pSet);
	}
	
	/***
	 * 查询邮寄件物流信息（舟山市）
	 * @param pSet
	 * @return
	 */
	public DataSet queryLogisticsDetail(ParameterSet pSet){
		UserInfo user = this.getUserInfo(pSet);

		if(null!=user){
			pSet.put("userId",user.getUserId());
		}else{
			pSet.put("userId", "");
		}
		return ProjectIndexDao.getInstance().queryLogisticsDetail(pSet);
	}
}