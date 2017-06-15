package xy.app.icity.govservice;

import xy.app.icity.govservice.GovProjectDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd extends app.icity.govservice.GovProjectCmd{ 

	public DataSet BusinessSearchQuery(ParameterSet pSet) {
		return GovProjectDao.getInstance().BusinessSearchQuery(pSet);
	}
	/**
	 * 新余三单
	 * @param pSet
	 * @return
	 */
	public DataSet getContentList(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentList(pSet);
	}
	/**
	 * 新余三单 数量
	 * @param pSet
	 * @return
	 */
	public DataSet getContentListCount(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentListCount(pSet);
	}
	//新余底部
	public DataSet getContentInfoXy(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentInfoXy(pSet);
	}
	
	/**
	 * 新余三单部门
	 * @param pSet
	 * @return
	 */
	public DataSet getContentDept(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentDept(pSet);
	}
}