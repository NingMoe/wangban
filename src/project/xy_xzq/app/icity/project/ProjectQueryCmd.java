package xy_xzq.app.icity.project;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;

public class ProjectQueryCmd extends BaseQueryCommand {

	
	public DataSet getSummary(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getSummary(pSet);
	}
	
	public DataSet getBjxx_sp(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getBjxx_sp(pSet);
	}
	
	public DataSet getCatalog(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getCatalog(pSet);
	}
	public DataSet getCataloggr(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getCataloggr(pSet);
	}
	public DataSet getCatalogfr(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getCatalogfr(pSet);
	}
	
	public DataSet getCatalogTitle(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getCatalogTitle(pSet);
	}
	
}
