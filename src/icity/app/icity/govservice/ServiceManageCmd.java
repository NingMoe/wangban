package app.icity.govservice;

import app.icity.guestbook.WriteDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

/**
 * 服务管理
 * @author lenovo
 *
 */
public class ServiceManageCmd extends BaseQueryCommand {
	/**
	 * 相关服务推荐
	 * @param pSet
	 * @return
	 */
	public DataSet getBusyInfoRelatedServices(ParameterSet pSet){
		UserInfo uinfo = this.getUserInfo(pSet);
		pSet.put("uid", uinfo.getUid()+"");
		return ServiceManageDao.getInstance().getBusyInfoRelatedServices(pSet);
	}
	/**
	 * 近期办件
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessRecentDo(ParameterSet pSet){
		UserInfo uinfo = this.getUserInfo(pSet);
		pSet.put("uid", uinfo.getUid()+"");
		return ServiceManageDao.getInstance().getBusinessRecentDo(pSet);
	}
	/**
	 * 近期咨询投诉信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessRecentGuestbook(ParameterSet pSet){
		UserInfo uinfo = this.getUserInfo(pSet);
		pSet.put("uid", uinfo.getUid()+"");
		return ServiceManageDao.getInstance().getBusinessRecentGuestbook(pSet);
	}
	/**
	 * 我的消息
	 * @param pSet
	 * @return
	 */
	public DataSet getNewsPubmynews(ParameterSet pSet){
		UserInfo uinfo = this.getUserInfo(pSet);
		pSet.put("ucid", uinfo.getUid()+"");
		return ServiceManageDao.getInstance().getNewsPubmynews(pSet);
	}
	/**
	 * 我的消息状态跟新为已读
	 * @param pSet
	 * @return
	 */
	public DataSet updateNewsPubmynews(ParameterSet pSet){
		UserInfo uinfo = this.getUserInfo(pSet);
		pSet.put("ucid", uinfo.getUid()+"");
		return ServiceManageDao.getInstance().updateNewsPubmynews(pSet);
	}
}