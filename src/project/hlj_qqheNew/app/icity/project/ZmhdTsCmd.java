package hlj_qqheNew.app.icity.project;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.project.ProjectQueryDao;

import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import hlj_qqheNew.app.icity.project.ZmhdTsDao;

public class ZmhdTsCmd extends BaseQueryCommand{
	
	public DataSet insert_ts(ParameterSet pSet){
		return ZmhdTsDao.getInstance().insert(pSet);
	}

	public DataSet PublishMessage(ParameterSet pSet){
		return ZmhdTsDao.getInstance().PublishMessage(pSet);
	}
	
	public DataSet insert_pl(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().insert_pl(pSet);
	}
	
	public DataSet insert_pl_dq(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().insert_pl_dq(pSet);
	}
	
	public DataSet check_pj(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().check_pj(pSet);
	}
	
	public DataSet getMypj(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMypj(pSet);
	}
	public DataSet getMypjxx(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMypjxx(pSet);
	}
	
	public DataSet getProjectInfo(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getProjectInfo(pSet);
	}

	public DataSet getName(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getName(pSet);
	}
	
	public DataSet getMyTs(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMyTs(pSet);
	}
	
	public DataSet getMyZx(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMyZx(pSet);
	}
	public DataSet getMyQz(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMyQz(pSet);
	}
	public DataSet getMytsXx(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMytsXx(pSet);
	}
	
	public DataSet getMyzxXx(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMyzxXx(pSet);
	}
	public DataSet getMyqzXx(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMyqzXx(pSet);
	}
	
	public DataSet getMyzxXx_dq(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getMyzxXx_dq(pSet);
	}
	public DataSet getIndexzxZx_dq(ParameterSet pSet){ 
		return ZmhdTsDao.getInstance().getIndexzxZx_dq(pSet);
	}

	
}
