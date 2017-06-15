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


public class HotWordCmd extends BaseQueryCommand{
	
	public DataSet HotWord(ParameterSet pSet){ 
		return HotWordDao.getInstance().HotWord(pSet);
	}
	
	public DataSet getHotWord(ParameterSet pSet){ 
		return HotWordDao.getInstance().getHotWord(pSet);
	}

	
}
