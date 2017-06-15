package app.icity.project;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class ServiceInstitutionDao extends BaseJdbcDao {

	protected static String icityDataSource = "icityDataSource";

	protected ServiceInstitutionDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static ServiceInstitutionDao getInstance() {
		return DaoFactory.getDao(ServiceInstitutionDao.class.getName());
	}

	public DataSet query(ParameterSet pSet) {
		String sql = "select * from SERVICE_INSTITUTION_BASEINFO t where 1=1";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		DataSet ds;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql);
		} else {
			ds = this.executeDataset(sql, start, limit);
		}
		return ds;
	}

	public DataSet queryBasic(ParameterSet pSet) {
		String sql = "select t.ID,t.INSTITUTION_NAME,t.INSTITUTION_LEVEL,t.OFFICEHOURS,"
				+ "t.GEOGRAPHIC_COORDINATE,t.SERVICE_PHONE,t.EMAIL,t.COMPLAINT_CALL,t.FAX,t.OUTERADDRESS,t.LOCATION_DETAIL,"
				+ "t.ZIPCODE,t.UNIT_STATIONED_AMOUNT,t.FLOOR_AMOUNT,t.WINDOW_AMOUNT,t.OUTERWINDOW_AMOUNT,t.SQUARE,"
				+ "t.SERVICE_TOTALITY,t.SERVICE_PEOPLE,t.PERMANENT_STAFF,t.SUPERNUMERARY,t.PHOTO_ID,t.INTRODUCTION,"
				+ "t.RATING,t.ORGAN_CODE,t.DIVISION_CODE,t.CENTER_NAME,t.SERVICE_ITEM,t.APPROVAL_STANDARD,t.SERVICE_SPECIFICATION,"
				+ "t.INFORMATION_SYSTEM,t.SUPERVISION_EVALUATION,t.AUTHORIZAT_ID,"
				+ "p.PHOTO_URL from SERVICE_INSTITUTION_BASEINFO t left join service_institution_photo p on t.photo_id = p.id where 1=1";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		DataSet ds;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql);
		} else {
			ds = this.executeDataset(sql, start, limit);
		}
		return ds;
	}

	public DataSet queryPhoto(ParameterSet pSet) {
		String sql = "select * from SERVICE_INSTITUTION_PHOTO t where 1=1";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		System.out.println("pSet  " + pSet);
		DataSet ds;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql);
		} else {
			ds = this.executeDataset(sql, start, limit);
		}
		return ds;
	}

	public DataSet queryTraffic(ParameterSet pSet) {
		String sql = "select * from SERVICE_INSTITUTION_TRAFFIC t where 1=1";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		DataSet ds;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql);
		} else {
			ds = this.executeDataset(sql, start, limit);
		}
		return ds;
	}

	public DataSet queryWorktime(ParameterSet pSet) {
		String sql = "select * from SERVICE_INSTITUTION_WORKTIME t where 1=1";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		DataSet ds;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql);
		} else {
			ds = this.executeDataset(sql, start, limit);
		}
		return ds;
	}

	public DataSet queryMiddleDept(ParameterSet pSet) {
		String sql = "select * from SERVICE_INSTITUTION_MIDDLE t where 1=1";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		DataSet ds;
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql);
		} else {
			ds = this.executeDataset(sql, start, limit);
		}
		return ds;
	}
}
