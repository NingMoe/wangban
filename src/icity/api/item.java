package api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import api.impl.ItemImpl;
import api.impl.WechatImpl;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;

@RestType(name = "api.item", descript = "状态更新相关接口")
public class item extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(item.class);
	public DataSet setReturn(ParameterSet pSet) {
		return ItemImpl.getInstance().setReturn(pSet);
	}
	/**
	 * 更新证照信息
	 * 
	 * @param pSet
	 *            state状态0提交1审核通过2审核不通过3挂起
	 * @return
	 */
	public DataSet setReturnLicense(ParameterSet pSet) {
		return ItemImpl.getInstance().setReturnLicense(pSet);
	}

	/**
	 * 重庆企业设立，并联审批推送状态数据
	 * 
	 * @param pSet
	 * @return//获取表单内某元素的value 
	 *                         formiframe.document.getElementById("BianGengXiangMu"
	 *                         ).value
	 */
	public DataSet setEnterpriseState(ParameterSet pSet) {
		return ItemImpl.getInstance().setEnterpriseState(pSet);
	}

	/**
	 * 重庆
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet setItemState(ParameterSet pSet) {
		return ItemImpl.getInstance().setItemState(pSet);
	}

	/**
	 * 重庆企业设立，设置一级码
	 * 
	 * @param dcid企业标识码
	 * @param firstcode一级码
	 * @return 
	 *         {"total":0,"message":"错误信息","zip":0,"route":"","encrypt":0,"data":
	 *         null,"state":0}
	 */
	public DataSet setFirstCode(ParameterSet pSet) {
		return ItemImpl.getInstance().setFirstCode(pSet);
	}

	/**
	 * 审批事项查询密码更新
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updateItemspsxcxmm(ParameterSet pSet) {
		return ItemImpl.getInstance().updateItemspsxcxmm(pSet);
	}

	/**
	 * 舟山 企业设立 更新业务状态
	 * 
	 * @param pSet
	 *            bizid 业务标识 state 业务状态 dealtime 办理时间（时间格式：yyyy-MM-ddHH:mm:ss）
	 *            dealopinion 办理意见（描述）
	 * @return
	 */
	public DataSet setEnterpriseState_zs(ParameterSet pSet) {
		return ItemImpl.getInstance().setEnterpriseState_zs(pSet);
	}

	/**
	 * 舟山 企业设立 更新事项状态
	 * 
	 * @param pSet
	 *            bizid 业务标识 itemid 事项id state 状态 dealtime
	 *            办理时间（时间格式：yyyy-MM-ddHH:mm:ss） dealopinion 办理意见（描述）
	 * @return
	 */
	public DataSet setItemState_zs(ParameterSet pSet) {
		return ItemImpl.getInstance().setItemState_zs(pSet);
	}

	/**
	 * 接收踏勘的材料信息的接口
	 * 
	 * @return
	 */
	public DataSet setMaterialInfo(ParameterSet pSet) {
		return ItemImpl.getInstance().setMaterialInfo(pSet);
	}

	/**
	 * 重庆,审批推送结果材料到网办 business_attach
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet insertBusinessAttach(ParameterSet pSet) {
		return ItemImpl.getInstance().insertBusinessAttach(pSet);
	}

	/**
	 * 同步审批系统缴费信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updatePayStatusAndContent(ParameterSet pSet) {
		return ItemImpl.getInstance().updatePayStatusAndContent(pSet);
	}

	/**
	 * 淄博：接口转换 获取办件列表
	 * 
	 * @param pset
	 * @return
	 */
	public String getDisplayListByPage(ParameterSet pSet) {
		return ItemImpl.getInstance().getDisplayListByPage(pSet);
	}
	/**
	 * 淄博：接口转换 获取联审公示列表
	 * 
	 * @param pset
	 * @return
	 */
	public String getDisplayLsListByPage(ParameterSet pSet) {
		return ItemImpl.getInstance().getDisplayLsListByPage(pSet);
	}
	
	/**
	 * 淄博：接口转换 获取办件统计信息
	 * 
	 * @param pset
	 * @return
	 */
	public String getAcceptQuantity(ParameterSet pSet) {
		return ItemImpl.getInstance().getAcceptQuantity(pSet);
	}
	/**
	 * 淄博：办件查询
	 * receiveNumber  password
	 * @param pset
	 * @return
	 */
	public DataSet getAllBusinessInfo(ParameterSet pSet) {
		return WechatImpl.getInstance().getAllBusinessInfo(pSet);
	}
	/**
	 * 投资项目抄告短信发送(舟山)
	 * 
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet cgSendMessage(ParameterSet pSet) {
		return ItemImpl.getInstance().cgSendMessage(pSet);
	}
	
	/**
	 * 联审联办申办编码与联审联办内网同步（舟山）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updateApproveNumByBizId(ParameterSet pSet){
		return ItemImpl.getInstance().updateApproveNumByBizId(pSet);
	}
	
	/**
	 * EMS取件（福州）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet sendEmsData(ParameterSet pSet) {
		return ItemImpl.getInstance().sendEmsData(pSet);
	}
	/**
	 * 審批調接口推送窗口收的辦件（威海）
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public DataSet synchronousData(ParameterSet pSet){		
		return ItemImpl.getInstance().synchronousData(pSet);		
	}
}
