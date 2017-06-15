package fz.app.icity.project;


import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

import core.util.HttpClientUtil;

public class ProjectIndexCmd extends app.icity.project.ProjectIndexCmd{	
	/**
	 * 企业个人中心手动添加证照信息到 表UC_LICENSE
	 * @param pSet（）
	 * @return
	 */
	public DataSet saveLicense(ParameterSet pSet){
		long uid = this.getUserInfo(pSet).getUid();
		pSet.put("uid", uid);
		return ProjectIndexDao.getInstance().saveLicense(pSet);
	}
	/**
	 * get UC_LICENSE
	 * @param pSet（）
	 * @return
	 */
	public DataSet getLicense(ParameterSet pSet){
		long uid = this.getUserInfo(pSet).getUid();
		pSet.put("uid", uid);
		return ProjectIndexDao.getInstance().getLicense(pSet);
	}
	/**
	 * 编辑和删除
	 * @param pSet
	 * @return
	 */
	public DataSet editLicense(ParameterSet pSet){
		long uid = this.getUserInfo(pSet).getUid();
		pSet.put("uid", uid);
		return ProjectIndexDao.getInstance().editLicense(pSet);
	}
}