package app.pmi.srr;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.util.CommonUtils;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.icore.util.db.SqlCreator;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

public class SrrServiceDao extends BaseJdbcDao{
	protected SrrServiceDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static SrrServiceDao getInstance() {
		return DaoFactory.getDao(SrrServiceDao.class.getName());
	}
	
	/**
	 * 批量新增服务信息
	 */
	public DataSet batchAddService(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = null;
		try{
			JSONArray params = (JSONArray) pSet.remove("params");
			if(params!=null && params.size()>0){
				conn = DbHelper.getConnection("icityDataSource");
				conn.setAutoCommit(false); 
				String sql = "insert into SRR_SERVICE(ID,APP_ID,ACTION,METHOD,USE_TIME,CREATE_TIME,IS_SUCCESS,MESSAGE)" +
						" values (?,?,?,?,?,TO_DATE(?,'yyyy-mm-dd hh24:mi:ss'),?,?) ";
				for(int i=0;i<params.size();i++){
					JSONObject sb = params.getJSONObject(i);
					DbHelper.update(sql,new Object[]{Tools.getUUID32(),
							sb.get("appId"),sb.get("action"),sb.get("method"),sb.get("useTime"),
							CommonUtils.getInstance().formatJsonToString((JSONObject) sb.get("createTime"), CommonUtils.YYYY_MM_DD_HH_MM_SS_SSS),
							sb.get("success"),sb.get("message")},conn);
				}
				conn.commit();
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			if (conn != null) {
				try {
					conn.rollback();
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
		return ds;
	}
	
	public DataSet getServiceList(ParameterSet pSet) {
		StringBuffer sqlbBuffer = new StringBuffer();
		String sql = "select * from SRR_SERVICE t " ;
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		sqlbBuffer.append("select t.action,t.method,count(*) serviceCount from (").append(sql).append(") t group by t.action,t.method ");
		int start =pSet.getPageStart();
		int limit =pSet.getPageLimit();
		if(start==-1 || limit ==-1){
			return this.executeDataset(sqlbBuffer.toString(), null);
		}else{
			return this.executeDataset(sqlbBuffer.toString(),start,limit,null);
		}
	}
	
	public DataSet getServiceCount(ParameterSet pSet) {
		StringBuffer sqlbBuffer = new StringBuffer();
		String sql = " select s.app_id,count(*) from srr_service s ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		sqlbBuffer.append("select app_id,count(rownum) serviceCount from (").append(sql).append(" group by s.action,s.method,s.app_id ) group by app_id ");
		return this.executeDataset(sqlbBuffer.toString(), null);
	}
	
	/**
	 * 查询指定时间的最大、最小以及平均响应时间
	 * @param pSet
	 * @return
	 */
	public DataSet getServicesTime(ParameterSet pSet) {
		DataSet dSet;
		String app_id = (String) pSet.getParameter("app_id");
		String action = (String) pSet.getParameter("action");
		String method = (String) pSet.getParameter("method");
		int time = pSet.getParameter("time")==null?0:(Integer) pSet.remove("time");
		StringBuffer querybBuffer = new StringBuffer();
		if(time==-6 || time==-29){	querybBuffer.append(" WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE").append(time).append(",'yyyymmdd') ");}//最近7天/30天
		if(time==-1 || time==0){	querybBuffer.append(" WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE").append(time==0?"":time).append(") ");}//今天或昨天
		if(StringUtil.isNotEmpty(app_id)){	querybBuffer.append(" AND T.APP_ID = '"+app_id+"'");}
		if(StringUtil.isNotEmpty(action)){	querybBuffer.append(" AND T.ACTION = '"+action+"'");}
		if(StringUtil.isNotEmpty(method)){	querybBuffer.append(" AND T.METHOD = '"+method+"'");}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select case when maxtime is null then 0 else maxtime end as maxtime," ).append(
				" case when mintime is null then 0 else mintime end as mintime," ).append(
				" case when avgtime is null then 0 else round(AVGTIME,2) end as avgtime from( " ).append(
				" select max(t.use_time) maxtime,min(t.use_time) mintime,avg(t.use_time) avgtime from srr_service t " ).append(querybBuffer).append(" )");
		String sql = sqlBuffer.toString();
		dSet = this.executeDataset(sql, null);
		return dSet;
	}
	
	/**
	 * 按时间段统计服务响应时间，今天特殊处理
	 * @param pSet
	 * @return
	 */
	public DataSet getServicesByTime(ParameterSet pSet) {
		DataSet dSet;
		String app_id = (String) pSet.getParameter("app_id");
		String action = (String) pSet.getParameter("action");
		String method = (String) pSet.getParameter("method");
		int time = pSet.getParameter("time")==null?0:(Integer) pSet.remove("time");
		String timemode = (String) pSet.remove("timemode");
		StringBuffer querybBuffer = new StringBuffer();
		if(time==-6 || time==-29){	querybBuffer.append(" WHERE TO_CHAR(T.CREATE_TIME,'yyyymmdd') >= TO_CHAR(SYSDATE").append(time).append(",'yyyymmdd') ");}//最近7天/30天
		if(time==-1 || time==0){	querybBuffer.append(" WHERE TO_DATE(T.CREATE_TIME) = TRUNC(SYSDATE").append(time==0?"":time).append(") ");}//今天或昨天
		if(StringUtil.isNotEmpty(app_id)){	querybBuffer.append(" AND T.APP_ID = '"+app_id+"'");}
		if(StringUtil.isNotEmpty(action)){	querybBuffer.append(" AND T.ACTION = '"+action+"'");}
		if(StringUtil.isNotEmpty(method)){	querybBuffer.append(" AND T.METHOD = '"+method+"'");}
		StringBuffer sqlBuffer = new StringBuffer();
		//按小时统计
		if("byHour".equals(timemode)){
			sqlBuffer.append( " SELECT TM.TIME||':00-'||TM.TIME||':59' TIME,CASE WHEN TR.MAXTIME IS NULL THEN 0 ELSE TR.MAXTIME END AS MAXTIME, ").append(
					" CASE WHEN TR.MINTIME IS NULL THEN 0 ELSE TR.MINTIME END AS MINTIME, ").append(
					" CASE WHEN TR.AVGTIME IS NULL THEN 0 ELSE ROUND(TR.AVGTIME,2) END AS AVGTIME ").append(
					" FROM( SELECT TO_CHAR(SYSDATE +(25-ROWNUM) / 24,'HH24') AS TIME FROM DUAL CONNECT BY ROWNUM <= " ).append(
					time==0?" TO_NUMBER(TO_CHAR(SYSDATE, 'HH24'))+1 ":" 24 "); //TO_CHAR(SYSDATE,'HH24')-今天;//" 24 "-昨天或之前
			sqlBuffer.append(" )TM ").append(
					" LEFT OUTER JOIN (SELECT TIME,MAX(TT.USE_TIME) MAXTIME,MIN(TT.USE_TIME) MINTIME,AVG(TT.USE_TIME) AVGTIME " +
					" FROM (SELECT T.*,TO_CHAR(T.CREATE_TIME, 'HH24') TIME FROM SRR_SERVICE T " ).append(querybBuffer).append(
					" ) TT GROUP BY TT.TIME ) TR ON TM.TIME=TR.TIME ORDER BY TM.TIME DESC ");
		}
		//按天数统计
		if("byDay".equals(timemode)){
			sqlBuffer.append("SELECT TM.TIME TIME,CASE WHEN TR.MAXTIME IS NULL THEN 0 ELSE TR.MAXTIME END AS MAXTIME, ").append(
					" CASE WHEN TR.MINTIME IS NULL THEN 0 ELSE TR.MINTIME END AS MINTIME, ").append(
					" CASE WHEN TR.AVGTIME IS NULL THEN 0 ELSE ROUND(TR.AVGTIME,2) END AS AVGTIME ").append(
					" FROM (");
			if(time==-6) {			sqlBuffer.append(" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=7 ");}//最近七天
			else if(time==-29) {	sqlBuffer.append(" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=30 ");}//最近30天
			else if(time==-1){		sqlBuffer.append(" SELECT TO_CHAR(SYSDATE-ROWNUM, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=1 "); }//昨天
			else {					sqlBuffer.append(" SELECT TO_CHAR(SYSDATE-ROWNUM+1, 'yyyy-MM-dd') AS TIME FROM DUAL CONNECT BY ROWNUM <=1 "); }//今天
			sqlBuffer.append(" ) TM ").append(
					" LEFT OUTER JOIN (SELECT TIME,MAX(TT.USE_TIME) MAXTIME,MIN(TT.USE_TIME) MINTIME,AVG(TT.USE_TIME) AVGTIME " +
					" FROM (SELECT T.*,TO_CHAR(T.CREATE_TIME, 'yyyy-MM-dd') TIME FROM SRR_SERVICE T ").append(querybBuffer).append(
					" ) TT GROUP BY TT.TIME) TR ON TM.TIME = TR.TIME ORDER BY TM.TIME DESC ");
		}
		String sql = sqlBuffer.toString();
		dSet = this.executeDataset(sql, null);
		return dSet;
	}
}
