/**  
 * @Title: BusinessEvaluateCmd.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-30 下午2:20:11 
 * @version V1.0  
 */ 
package app.icity.center.active;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/** 
 * @ClassName: business_evaluateCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-5-30 下午2:20:11  
 */

public class BusinessEvaluateCmd extends BaseQueryCommand{
	public DataSet getMyEvaluateList(ParameterSet pSet){
		return BusinessEvaluateDao.getInstance().getMyEvaluateList(pSet);
	}
	
	public DataSet getEvaluateToMeList(ParameterSet pSet){
		return BusinessEvaluateDao.getInstance().getEvaluateToMeList(pSet);
	}
}
