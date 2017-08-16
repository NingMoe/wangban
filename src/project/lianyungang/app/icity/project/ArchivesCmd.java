package lianyungang.app.icity.project;

import lianyungang.app.icity.project.ArchivesDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;


public class ArchivesCmd extends BaseQueryCommand{
	
	public DataSet getIndexIntroduction(ParameterSet pSet){
		return ArchivesDao.getInstance().getIndexIntroduction(pSet);
	}
	
}
