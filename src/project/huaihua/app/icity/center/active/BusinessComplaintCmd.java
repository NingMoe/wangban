/**  
 * @Title: BusinessComplainCmd.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 下午2:20:06 
 * @version V1.0  
 */ 
package huaihua.app.icity.center.active;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

/** 
 * @ClassName: BusinessComplainCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-5-31 下午2:20:06  
 */

public class BusinessComplaintCmd extends BaseQueryCommand {
	public DataSet getBusinessComplaintList(ParameterSet pSet){
		return BusinessComplaintDao.getInstance().getBusinessComplaintList(pSet);
	}
	
	public DataSet delete(ParameterSet pSet){
		return BusinessComplaintDao.getInstance().delete(pSet);
	}
	
	public DataSet getBusinessComplaint(ParameterSet pSet){
		return BusinessComplaintDao.getInstance().getBusinessComplaint(pSet);
	}
	
	public DataSet update(ParameterSet pSet){
		return BusinessComplaintDao.getInstance().update(pSet);
	}	
}
