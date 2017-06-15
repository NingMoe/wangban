/**  
 * @Title: BusinessAskCmd.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:22:03 
 * @version V1.0  
 */ 
package app.icity.center.active;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/** 
 * @ClassName: BusinessAskCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:22:03  
 */

public class BusinessAskCmd extends BaseQueryCommand {
	public DataSet getBusinessAskList(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessAskList(pSet);
	} 
	
	//聊城按照 时间和标题进行查询
	public DataSet getBusinessAskList2(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessAskList2(pSet);
	} 
	
	public DataSet delete(ParameterSet pSet){
		return BusinessAskDao.getInstance().delete(pSet);
	}
	/**
	 * 查询主任信箱详细（漳州）
	 * @param pSet
	 * @return
	 */
	public DataSet getMessage(ParameterSet pSet){
		return BusinessAskDao.getInstance().getMessage(pSet);
	}
	
	public DataSet getBusinessAsk(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessAsk(pSet);
	}
	/**
	 * 查询 追问-回复记录
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessAgainAsk(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessAgainAsk(pSet);
	}
	public DataSet getBusinessJycx(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessJycx(pSet);
	}
	
	public DataSet update(ParameterSet pSet){
		return BusinessAskDao.getInstance().update(pSet);
	}
	
	//根据咨询类型进行查询   投资咨询
	public DataSet getBusinessTzzx(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessTzzx(pSet);
	}
	/**
	 * 查询建议详细
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessSuggest(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessSuggest(pSet);
	}
	/**
	 * 查询纠错详细
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessCorrection(ParameterSet pSet){
		return BusinessAskDao.getInstance().getBusinessCorrection(pSet);
	}
}
