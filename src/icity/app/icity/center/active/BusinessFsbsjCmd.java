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
import com.inspur.bean.UserInfo;

/** 
 * @ClassName: BusinessAskCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:22:03  
 */

public class BusinessFsbsjCmd extends BaseQueryCommand {
	public DataSet getBusinessFsbsjList(ParameterSet pSet){
		return BusinessFsbsjDao.getInstance().getBusinessFsbsjList(pSet);
	} 
	
	public DataSet delete(ParameterSet pSet){
		return BusinessFsbsjDao.getInstance().delete(pSet);
	}
	
	public DataSet getBusinessFsbsj(ParameterSet pSet){
		return BusinessFsbsjDao.getInstance().getBusinessFsbsj(pSet);
	}
	
	public DataSet update(ParameterSet pSet){
		return BusinessFsbsjDao.getInstance().update(pSet);
	}	
}
