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
	
	public DataSet getPersonGzyjContentById(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonGzyjContentById(pSet);
	}
	
	public DataSet getPersonBlajById(ParameterSet pSet){
		return ArchivesDao.getInstance().getPersonBlajById(pSet);
	}
	
	public DataSet getNdbgYear(ParameterSet pSet){
		return ArchivesDao.getInstance().getNdbgYear(pSet);
	}
	
	public DataSet getNdbgContent(ParameterSet pSet){
		return ArchivesDao.getInstance().getNdbgContent(pSet);
	}
	
	public DataSet getDsjYear(ParameterSet pSet){
		return ArchivesDao.getInstance().getDsjYear(pSet);
	}
	
	public DataSet getDsjMonth(ParameterSet pSet){
		return ArchivesDao.getInstance().getDsjMonth(pSet);
	}
	
	public DataSet getDsjContent(ParameterSet pSet){
		return ArchivesDao.getInstance().getDsjContent(pSet);
	}
	
	public DataSet getDept(ParameterSet pSet){
		return ArchivesDao.getInstance().getDept(pSet);
	}
	
}
