package hlj_qqheNew.app.icity.project;

import hlj_qqheNew.app.icity.project.GrzxDao;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.uc.UserDao;

import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

//import hlj_qqheNew.app.icity.project.ZmhdTsDao;

public class GrzxCmd extends BaseQueryCommand{
	
	public DataSet getUserById(ParameterSet pset){
		return GrzxDao.getInstance().getUserById(pset);
	}
	
	public DataSet queryZxTsCount(ParameterSet pset){
		return GrzxDao.getInstance().queryZxTsCount(pset);
	}
	
	public DataSet getBusinessCount(ParameterSet pset){
		return GrzxDao.getInstance().getBusinessCount(pset);
	}
	
	public DataSet getBusinessIndexList(ParameterSet pSet){
		//pSet.put("SQRZJHM@=","430421198401246133");  查询时关联证件号码，暂时屏蔽
		return ProjectIndexDao.getInstance().getBusinessIndexList(pSet);
	}
	
	public DataSet getBusinessAccept(ParameterSet pSet){
		return GrzxDao.getInstance().getBusinessAccept(pSet);
	}
	
	
	
}
