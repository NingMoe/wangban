package app.icity.center.active;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.db.SqlCreator;

public class BusinessAppointDao extends BaseJdbcDao {
	
	protected BusinessAppointDao(){
		this.setDataSourceName("icityDataSource");
	}
	
	public static BusinessAppointDao getInstance() {
		return DaoFactory.getDao(BusinessAppointDao.class.getName());
	}
	
	public DataSet getBusinessAppointList(ParameterSet pSet,UserInfo userInfo){
		DataSet ds = new DataSet();
		pSet.setParameter("app_user", userInfo.getUserId());
		try {
			String sql = "select t.id,t.sblsh,t.sbxmmc,sib.INSTITUTION_NAME,date(t.APP_START_TIME) app_date,concat('[',DATE_FORMAT(t.app_start_time,'%H:%i'),'~',DATE_FORMAT(t.app_end_time,'%H:%i'),']') app_time, t.APP_STATUS,t.last_modify_time,t.qymc from business_appoint t,service_institution_baseinfo sib where sib.id = t.institution_id";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			sql += " order by t.last_modify_time desc";
			int start =pSet.getPageStart();
			int limit =pSet.getPageLimit();
			if(start==-1 || limit ==-1){
				return this.executeDataset(sql, null);
			}else{
				return this.executeDataset(sql,start,limit,null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
}
