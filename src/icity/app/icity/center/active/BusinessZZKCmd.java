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
 * 政证快（快递信息服务）-漳州使用
 * @author yanhao
 * @date 2017-4-6
 *
 */

public class BusinessZZKCmd extends BaseQueryCommand {
	
	public DataSet getZzk(ParameterSet pSet){
		return BusinessZZKDao.getInstance().getZzk(pSet);
	} 
	
	public DataSet updateZzk(ParameterSet pSet){
		return BusinessZZKDao.getInstance().updateZzk(pSet);
	} 
}
