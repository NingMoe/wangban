package zs_szw.app.project;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

public class ProjectIndexCmd extends app.icity.project.ProjectIndexCmd{	
	
	/**
	 *舟山市办件统计
	 * @return
	 */
	public DataSet getStateTotalZS(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getStateTotalZS(pSet);
	}
	
	
}