package lianyungang.app.icity.project;

import lianyungang.app.icity.project.ArchivesDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;


public class ArchivesCmd extends BaseQueryCommand{
	
	public DataSet getIndexIntroduction(ParameterSet pSet){
		return ArchivesDao.getInstance().getIndexIntroduction(pSet);
	}
	
	public DataSet getPersonInfo(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonInfo(pSet);
	}
	
	public DataSet getPersonInfoById(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonInfoById(pSet);
	}
	
	public DataSet getPersonGzxsById(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonGzxsById(pSet);
	}
	
	public DataSet getPersonGzrwById(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonGzrwById(pSet);
	}
	
	public DataSet getPersonGzyjYearById(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonGzyjYearById(pSet);
	}
	
}
