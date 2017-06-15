package hlj_qqheNew.app.icity.project;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import hlj_qqheNew.app.icity.project.ZwgkDao;
import hlj_qqheNew.app.icity.project.ZmhdTsDao;

public class ZwgkCmd extends BaseQueryCommand{
	
	//政务公开信息指南
	public DataSet pubcontent(ParameterSet pSet){
		return ZwgkDao.getInstance().select(pSet);
	}
	//政务公开信息目录
	public DataSet getProjectInfo(ParameterSet pSet){
		DataSet data = ZwgkDao.getInstance().selectgkml(pSet);
		return data;
	}
	//政务公开年度报告
	public DataSet ndbgcontent(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selectndbg(pSet);
	}
	//政务公开信箱
	public DataSet dzxxcontent(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selectdzxx(pSet);
	}
	//政务公开办事部门
	public DataSet bmbscontent(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selectbmbs(pSet);
	}
	//政务公开信息显示
	public DataSet Infoview(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selectview(pSet);
	}
	public DataSet ztxwcontent(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selectztxw(pSet);
	}
	public DataSet selecttzgg(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selecttzgg(pSet);
	}
	
	public DataSet wzsm(ParameterSet pSet){ 
		return ZwgkDao.getInstance().wzsm(pSet);
	}
	
	public DataSet bzhjscontent(ParameterSet pSet){ 
		return ZwgkDao.getInstance().selectbzhjs(pSet);
	}
	
	public DataSet getTzgg(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getTzgg(pSet);
	}
	
	public DataSet getTzgg_xx(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getTzgg_xx(pSet);
	}
	
	
	public DataSet getZxjj(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getZxjj(pSet);
	}
	
	public DataSet getWzjs(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getWzjs(pSet);
	}
	
	public DataSet getWzsm(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getWzsm(pSet);
	}
	
	public DataSet getLxwm(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getLxwm(pSet);
	}
	public DataSet getDown(ParameterSet pSet){ 
		return ZwgkDao.getInstance().getDown(pSet);
	}

}
