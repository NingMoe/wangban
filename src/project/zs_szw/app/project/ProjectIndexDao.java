package zs_szw.app.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;

public class ProjectIndexDao extends BaseJdbcDao {
	protected final String STTYPE = SecurityConfig.getString("StType", "0");// 省部门类型
	protected final String DSTYPE = SecurityConfig.getString("DsType", "1");// 省直机关、市对应部门类型
	protected final String QXTYPE = SecurityConfig.getString("QxType", "2");// 市直机关、区、县对应部门类型
	protected final String BMTYPE = SecurityConfig.getString("BmType", "3");// 机构对应部门类型
	protected final String JDTYPE = SecurityConfig.getString("JdType", "7");// 街道对应部门类型
	protected final String SQTYPE = SecurityConfig.getString("SqType", "8");// 社区对应部门类型

	protected static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String mssqlDataSource = "mssqlDataSource";

	public ProjectIndexDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao)DaoFactory.getDao(ProjectIndexDao.class.getName());
	}

	
	/**
	 * 舟山市办件统计
	 * 
	 * @param pset
	 * @return
	 * @since 2013-8-28
	 */
	public DataSet getStateTotalZS(ParameterSet pset) {
		String tjsj = (String)pset.remove("tjsj");
		//String sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A,POWER_BASE_INFO B WHERE A.SXBM = B.CODE AND STATE IS NOT NULL ";//2015611 pan
		  String sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A WHERE STATE IS NOT NULL ";
		 // sql+=" and A.SBSJ >=TRUNC(SYSDATE, 'MM') and A.SBSJ<=last_day(SYSDATE)";//本月统计
		 // sql+=" and to_char(A.SBSJ,'yyyy')=to_char(sysdate,'yyyy')";  //本年
		 // sql+="and to_char(A.SBSJ,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";//当天统计
		  sql+=tjsj;
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " GROUP BY A.STATE ";
		return this.executeQuery(sql);
	}
}
