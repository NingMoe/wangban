/**  
 * @Title: SmsInfoCmd.java 
 * @Package icity.common 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-7-29 下午5:11:41 
 * @version V1.0  
 */ 
package app.pmi.sms;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

/** 
 * @ClassName: SmsInfoCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-7-29 下午5:11:41  
 */

public class SmsInfoCmd extends BaseQueryCommand {
	public DataSet getList(ParameterSet pSet){
		return SmsInfoDao.getInstance().getList(pSet);
	}
	
	public DataSet update(ParameterSet pSet){
		return SmsInfoDao.getInstance().update(pSet);
	}
	
	public DataSet insert(ParameterSet pSet){
		return SmsInfoDao.getInstance().insert(pSet);
	}
}
