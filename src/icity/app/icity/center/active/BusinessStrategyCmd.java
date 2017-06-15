package app.icity.center.active;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;


public class BusinessStrategyCmd extends BaseQueryCommand {
	/**
	 * 我的攻略
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessStrategy(ParameterSet pSet) {
		UserInfo user = this.getUserInfo(pSet);		
		if(null!=user){
			pSet.put("CREATOR", user.getUid()+"");
			pSet.put("CREATOR_NAME", user.getUserName());
		}else{
			pSet.put("CREATOR", "");
			pSet.put("CREATOR_NAME", "");
		}
		return BusinessStrategyDao.getInstance().getBusinessStrategy(pSet);
	}
	/**
	 * 更新我的攻略
	 * @param pSet
	 * @return
	 */
	public DataSet updateBusinessStrategy(ParameterSet pSet) {
		UserInfo user = this.getUserInfo(pSet);		
		if(null!=user){
			pSet.put("CREATOR", user.getUid()+"");
			pSet.put("CREATOR_NAME", user.getUserName());
		}else{
			pSet.put("CREATOR", "");
			pSet.put("CREATOR_NAME", "");
		}
		return BusinessStrategyDao.getInstance().updateBusinessStrategy(pSet);
	}
}
