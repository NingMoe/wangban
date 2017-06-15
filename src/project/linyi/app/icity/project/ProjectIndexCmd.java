package linyi.app.icity.project;

import com.icore.util.StringUtil;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class ProjectIndexCmd extends app.icity.project.ProjectIndexCmd{	
	/**
	 * 排队列表-部门
	 * @param pset
	 * @return
	 */
	public DataSet getQueueDeptList(ParameterSet pSet) {		
		return ProjectIndexDao.getInstance().getQueueDeptList(pSet);
	}
	/**
	 * 排队列表-排队数
	 * @param pset
	 * @return
	 */
	public DataSet getQueueBusinessList(ParameterSet pSet) {		
		return ProjectIndexDao.getInstance().getQueueBusinessList(pSet);
	}
}