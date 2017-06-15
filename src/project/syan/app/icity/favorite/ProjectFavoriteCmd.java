/**  
 * @Title: ProjectFavoriteCmd.java 
 * @Package icity.admin.favorite 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-4-8 下午4:44:06 
 * @version V1.0  
 */ 
package syan.app.icity.favorite;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

/** 
 * @ClassName: ProjectFavoriteCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-4-8 下午4:44:06  
 */

public class ProjectFavoriteCmd extends BaseQueryCommand {	
	public DataSet getList(ParameterSet pSet){ 
		return ProjectFavoriteDao.getInstance().getList(pSet);
	}
	
	public DataSet getItemById(ParameterSet pSet){
		return ProjectFavoriteDao.getInstance().getItemById(pSet);
	}
	
	public DataSet insert(ParameterSet pSet){
		return ProjectFavoriteDao.getInstance().insert(pSet);
	}
	
	public DataSet update(ParameterSet pSet){
		return ProjectFavoriteDao.getInstance().update(pSet);
	}
	
	public DataSet delete(ParameterSet pSet){
		return ProjectFavoriteDao.getInstance().delete(pSet);
	}
	
	
	public DataSet getBusinessFavoriteList(ParameterSet pSet){
		pSet.setParameter("USID", String.valueOf(this.getUserInfo(pSet).getUid()));
		return ProjectFavoriteDao.getInstance().getBusinessFavoriteList(pSet);
	}
	
	public DataSet personaldelete(ParameterSet pSet){
		pSet.setParameter("USID", String.valueOf(this.getUserInfo(pSet).getUid()));
		return ProjectFavoriteDao.getInstance().delete(pSet);
	}
	
	/**
	 * 保存收藏信息
	 */
	public DataSet addFaveriot(ParameterSet pSet){
		pSet.setParameter("USID", String.valueOf(this.getUserInfo(pSet).getUid()));
		return ProjectFavoriteDao.getInstance().addFaveriot(pSet);
	}
	
	public DataSet updateRemark(ParameterSet pSet){
		pSet.setParameter("USID", String.valueOf(this.getUserInfo(pSet).getUid()));
		return ProjectFavoriteDao.getInstance().updateRemark(pSet);
	}
}
