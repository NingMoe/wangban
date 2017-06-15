package app.pmi.holiday;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class HolidayCmd extends BaseQueryCommand {
	  public DataSet getDateSet(ParameterSet pSet){
//		  ParameterSet pset = getParameterSet();
		  return HolidayDao.getInstance().getDateSet(pSet);
	  }
	  
	  public DataSet modifyDay(ParameterSet pSet){
//		  ParameterSet pset = getParameterSet();
		  return HolidayDao.getInstance().modifyDay(pSet);
	  }
}
