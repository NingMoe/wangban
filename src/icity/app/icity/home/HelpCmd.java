/**  
 * @Title: HelpCmd.java 
 * @Package icity.home 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-6-13 上午11:16:49 
 * @version V1.0  
 */ 
package app.icity.home;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
/** 
 * @ClassName: HelpCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-6-13 上午11:16:49  
 */

public class HelpCmd extends BaseQueryCommand{
	public DataSet getChannelList(ParameterSet pSet){
		return HelpDao.getInstance().getChannelList(pSet);
	}
	
	public DataSet getContentList(ParameterSet pSet){
		return HelpDao.getInstance().getContentList(pSet);
	}
}
