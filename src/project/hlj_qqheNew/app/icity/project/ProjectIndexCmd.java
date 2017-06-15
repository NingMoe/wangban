package hlj_qqheNew.app.icity.project;

import hlj_qqheNew.app.icity.project.ProjectIndexDao;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.sync.PowerBaseInfoSyncDao;
import app.uc.UserDao;

import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

//import hlj_qqheNew.app.icity.project.ZmhdTsDao;

public class ProjectIndexCmd extends BaseQueryCommand{
	
	public DataSet getMenuContent(ParameterSet pset){
		return ProjectIndexDao.getInstance().getMenuContent(pset);
	}
	
	public DataSet getMenuContentMore(ParameterSet pset){
		return ProjectIndexDao.getInstance().getMenuContentMore(pset);
	}
	
	public DataSet getMenuContentById(ParameterSet pset){
		return ProjectIndexDao.getInstance().getMenuContentById(pset);
	}
	
	public DataSet getRedian(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getRedian(pSet);
	}
	
	public DataSet getBusinessIndexList(ParameterSet pSet){
		//pSet.put("SQRZJHM@=","430421198401246133");  查询时关联证件号码，暂时屏蔽
		return ProjectIndexDao.getInstance().getBusinessIndexList(pSet);
	}
	
	public DataSet BusinessQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexAllList(pSet);
	}
	
	public DataSet getMess(){
		return ProjectIndexDao.getInstance().getMess();
	}
	
	//获取企业设立列表
	public DataSet getBusinessEnterList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessEnterList(pSet);
	}
	
	/**
	 * @Description: 根据申办流水号和申办人查询表单信息
	 * @return DataSet    返回类型
	 * @since 2015-8-10
	 * @author liuyq
	 */
	public DataSet formInfoQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().formInfoQueryByNameAndId(pSet);
	}
	
	/**
	 * @Description: 根据申办流水号和申办人查询流程信息
	 * @return DataSet    返回类型
	 * @since 2015-8-13
	 * @author liuyq
	 */
	public DataSet materialListQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().materialListQueryByNameAndId(pSet);
	}
	
	public DataSet updatebaseinfo(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updatebaseinfo(pSet);
	}
	
	public DataSet showPage(ParameterSet pSet){
		return ProjectIndexDao.getInstance().showPage(pSet);
	}
}
