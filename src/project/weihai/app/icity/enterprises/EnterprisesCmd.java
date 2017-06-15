package weihai.app.icity.enterprises;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import app.icity.project.ProjectIndexDao;

import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.SecurityConfig;

import core.util.CommonUtils_api;

/**
 * 企业设立 lhy
 * 
 * @author lenovo
 * 
 */
public class EnterprisesCmd extends BaseQueryCommand {
	/**
	 * 获取经营范围树（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getScopeTree(ParameterSet pSet) {
		return EnterprisesDao.getInstance().getScopeTree(pSet);
	}

	/**
	 * 根据经营范围获取前后置事项（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getItemByScope(ParameterSet pSet) {
		return EnterprisesDao.getInstance().getItemByScope(pSet);
	}

	/**
	 * 获取企业基本信息（新增）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getEnterpriseBaseInfo(ParameterSet pSet) {
		return EnterprisesDao.getInstance().getEnterpriseBaseInfo(pSet);
	}

	/**
	 * 获取企业设立业务 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getEnterprisesProjectId(ParameterSet pSet) {
		return EnterprisesDao.getInstance().getEnterprisesProjectId(pSet);
	}
	/**
	 * 业务保存
	 * @param pSet
	 * @return
	 */
	public DataSet saveEnterprises_business_index(ParameterSet pSet) {
		UserInfo userInfo = this.getUserInfo(pSet);
		String uid = userInfo.getUid()+"";	
		String uname = userInfo.getUserName();		
		pSet.setParameter("uid", uid);
		pSet.setParameter("uname", uname);
		return EnterprisesDao.getInstance().saveEnterprises_business_index(pSet);
	}
	/**
	 * 提交申报信息（新增）
	 * @param pSet
	 * @return
	 */
	public DataSet submitApplicationInfo(ParameterSet pSet) {
		UserInfo userInfo = this.getUserInfo(pSet);
		String uid = userInfo.getUid()+"";	
		String uname = userInfo.getUserName();		
		pSet.setParameter("uid", uid);
		pSet.setParameter("uname", uname);
		return EnterprisesDao.getInstance().submitApplicationInfo(pSet);
	}
	/**
	 *  业务中心查询企业设立列表 
	 * @param pset
	 * @return
	 */
	public DataSet getEnterprisesList(ParameterSet pSet) {
		UserInfo userInfo = this.getUserInfo(pSet);
		String uid = userInfo.getUid()+"";	
		String uname = userInfo.getUserName();		
		pSet.setParameter("uid", uid);
		pSet.setParameter("uname", uname);
		return EnterprisesDao.getInstance().getEnterprisesList(pSet);
	}
	/**
	 * 获取单体事项
	 * @param pSet
	 * @return
	 */
	public DataSet queryItem(ParameterSet pSet){
		return EnterprisesDao.getInstance().queryItem(pSet);
	}
}
