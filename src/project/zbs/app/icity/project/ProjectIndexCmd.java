package zbs.app.icity.project;

import zbs.app.icity.project.ProjectIndexDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class ProjectIndexCmd extends BaseQueryCommand{
	public DataSet getInfo_market(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getInfo_market(pSet);
	}
}
