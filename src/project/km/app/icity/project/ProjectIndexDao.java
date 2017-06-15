package km.app.icity.project;

import io.netty.util.CharsetUtil;

import java.net.URLEncoder;
import java.util.Calendar;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;
import core.util.HttpClientUtil;

public class ProjectIndexDao extends BaseJdbcDao {
	protected static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String qzDataSource = "qzDataSource";
	protected static String ylxfDataSource = "ylxfDataSource";

	public ProjectIndexDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao) DaoFactory.getDao(ProjectIndexDao.class
				.getName());
	}

	public DataSet getBusinessIndexList(ParameterSet pset) {

		DataSet ds;
		String searchTitle = (String) pset.remove("SEARCH_TITLE");
		String sql = "SELECT LIMIT_NUM,SBLSH,SXID,SXBM,SXMC,SJDW,SQRMC,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.id as starLevelId FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE where 1=1 ";
		if (StringUtil.isNotEmpty(searchTitle)) {
			sql += " AND (SBLSH like '%" + searchTitle + "%' OR SBXMMC like '%"
					+ searchTitle + "%' OR SXMC like '%" + searchTitle
					+ "%' ) ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null, "qzDataSource");
		} else {
			ds = this.executeDataset(sql, start, limit, null,
					this.getDataSourceName());
		}
		return ds;
	}

	/*
	 * 基出信息 select SBLSH,SBXMMC,SXMC,SQRMC,SPSJ,SBSJ,STATE from BUSINESS_INDEX t
	 * where SBLSH='01000000020151224100012'
	 * 
	 * 扭转时间 select PROJID,NODENAME,NODEPROCERNAME,OCCURTIME,NODEADV from
	 * EA_JC_STEP_PROC t where projid=530100691512112005073 ORDER BY SN DESC
	 */
	// 根据申办流水号和联系人手机查询事项基本信息
	public DataSet getBusinessIndexByNameAndPhone(ParameterSet pset) {
		DataSet ds;
		String sblsh = (String) pset.remove("SBLSH");
		String cxmm = (String) pset.remove("CXMM");
		String sql = "SELECT LIMIT_NUM,SBLSH,SXID,SXBM,SXMC,SJDW,SQRMC,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.id as starLevelId FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE where 1=1 ";
		if (StringUtil.isNotEmpty(sblsh)) {
			sql += " AND (SBLSH =" + sblsh + "and CXMM=" + cxmm + ") ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		ds = this.executeDataset(sql, null, this.getDataSourceName());
		return ds;
	}

	public DataSet getBusinessEnterList(ParameterSet pset) {
		String searchTitle = (String) pset.remove("SEARCH_TITLE");
		String sql = "SELECT a.receive_id,a.ID,a.APPLY_SUBJECT,a.FIRST_GRADE_CODE,to_char(a.APPLY_TIME,'yyyy-MM-dd hh24:mm:ss') APPLY_TIME,a.CURRENT_STATE,a.FORM_ID,a.ENTERPRISE_TYPE,a.DATA_ID,a.APPLY_DATA,(select sum(case when b.is_lhtk='1' then 1 else 0 end) is_lhtk from enterprise_business_course b where b.biz_id = a.id) IS_LHTK from ENTERPRISE_BUSINESS_INDEX a where 1=1 ";
		if (StringUtil.isNotEmpty(searchTitle)) {
			sql += " AND (SBLSH like '%" + searchTitle + "%' OR SBXMMC like '%"
					+ searchTitle + "%' OR SXMC like '%" + searchTitle
					+ "%' ) ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		DataSet ds;
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, new Object[] {},
					this.getDataSourceName());
		} else {
			ds = this.executeDataset(sql, start, limit);// .executeDataset(sql,
														// start, limit, new
														// Object[]{},
														// this.getDataSourceName());
		}
		return ds;
	}

	public DataSet BusinessENTERByNameAndId(ParameterSet pset) {
		String sql = "SELECT b.ORG_ACTUALITY,b.FIRST_GRADE_CODE,b.APPLY_SUBJECT,b.RECEIVE_ID,b.CURRENT_STATE,b.APPLY_TIME,t.SECOND_GRADE_CODE,t.ITEM_NAME,t.HANDLE_STATE,t.RECEIVE_TIME,t.ITEM_ID FROM ENTERPRISE_BUSINESS_COURSE t left outer join ENTERPRISE_BUSINESS_INDEX b on b.ID = t.BIZ_ID  WHERE 1=1 ";
		DataSet ds = new DataSet();
		try {
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			int start = pset.getPageStart();
			int limit = pset.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 更新业务列表备注信息
	 * 
	 * @param pset
	 * @return
	 * @since 2013-8-28
	 */
	public DataSet updateRemark(ParameterSet pset) {
		String sql = "UPDATE BUSINESS_INDEX SET REMARK = ? WHERE SBLSH = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { (String) pset.getParameter("REMARK"),
						(String) pset.getParameter("SBLSH") },
				this.getDataSourceName());
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	/**
	 * 按部门统计
	 * 
	 * @param pset
	 * @return
	 * @since 2013-8-28
	 */
	public DataSet getTotal(ParameterSet pset) {
		String sql = "SELECT SJDW AS DEPTNAME,COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " GROUP BY SJDW ";
		return this.executeQuery(sql);
	}

	/**
	 * 按事项类型统计
	 * 
	 * @param pset
	 * @return
	 * @since 2013-8-28
	 */
	public DataSet getTypeTotal(ParameterSet pset) {
		String sql = "SELECT B.ITEM_TYPE AS TYPE, COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A,POWER_BASE_INFO B WHERE A.SXBM = B.CODE ";

		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " GROUP BY B.ITEM_TYPE ";
		return this.executeQuery(sql);
	}

	/**
	 * 按业务状态统计
	 * 
	 * @param pset
	 * @return
	 * @since 2013-8-28
	 */
	/*public DataSet getStateTotal(ParameterSet pset) {
		String sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A WHERE STATE IS NOT NULL ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " GROUP BY A.STATE ";
		return this.executeQuery(sql);
	}*/
	public DataSet getStateTotal(ParameterSet pset) {
		String region_id=SecurityConfig.getString("WebRegion");
		String time = (String) pset.getParameter("time");
		pset.remove("time");
		String sql = "SELECT A.STATE , COUNT(A.PROJID) AS SUM FROM EA_JC_STEP_BASICINFO A WHERE STATE IS NOT NULL AND STATE<>'0' AND REGION_ID="+region_id;
		if("day".equals(time)){
			sql = "SELECT A.STATE , COUNT(A.PROJID) AS SUM FROM (Select * From EA_JC_STEP_BASICINFO a Where trunc(occurtime) = trunc(Sysdate)) A WHERE STATE IS NOT NULL AND STATE<>'0' AND REGION_ID="+region_id;
		}else if("month".equals(time)){
			sql = "SELECT A.STATE , COUNT(A.PROJID) AS SUM FROM (Select * From EA_JC_STEP_BASICINFO a Where to_char(occurtime,'yyyymm') = to_char(Sysdate,'yyyymm')) A WHERE STATE IS NOT NULL AND STATE<>'0' AND REGION_ID="+region_id;
		}else if("year".equals(time)){
			sql = "SELECT A.STATE , COUNT(A.PROJID) AS SUM FROM (Select * From EA_JC_STEP_BASICINFO a Where to_char(occurtime,'yyyy') = to_char(Sysdate,'yyyy')) A WHERE STATE IS NOT NULL AND STATE<>'0' AND REGION_ID="+region_id;
		}
		this.setDataSourceName(qzDataSource);
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " GROUP BY A.STATE ";
		DataSet a=this.executeQuery(sql);
		return a;
	}
	
	/**
	 * 云岭先锋专属网统计
	* @Title: getStateTotalLyxf 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param pset
	* @param @return    设定文件 
	* @return DataSet    返回类型 
	* @author kongws
	 */
	public DataSet getStateTotalLyxf_old(ParameterSet pset) {
		String time = (String) pset.getParameter("time");
		pset.remove("time");
		String sql =null;
		if("day".equals(time)){
			sql = "SELECT SUM(ACCEPT_DAY) AS ACCEPT,SUM(DORP_DAY) AS DORP,SUM(FINISH_DAY) AS FINISH FROM PRE_STATISTICS_REGION_YL T WHERE TO_CHAR(T.CREATE_TIME,'YYYY/MM/DD')=TO_CHAR(SYSDATE,'YYYY/MM/DD')";
		}else if("week".equals(time)){
			sql = "SELECT SUM(ACCEPT_WEEK) AS ACCEPT,SUM(DORP_WEEK) AS DORP,SUM(FINISH_WEEK) AS FINISH FROM PRE_STATISTICS_REGION_YL T WHERE TO_CHAR(T.CREATE_TIME,'YYYY/MM/DD')=TO_CHAR(SYSDATE,'YYYY/MM/DD')";
		}else if("mouth".equals(time)){
			sql = "SELECT SUM(ACCEPT_MOUTH) AS ACCEPT,SUM(DORP_MOUTH) AS DORP,SUM(FINISH_MOUTH) AS FINISH FROM PRE_STATISTICS_REGION_YL T WHERE TO_CHAR(T.CREATE_TIME,'YYYY/MM/DD')=TO_CHAR(SYSDATE,'YYYY/MM/DD')";
		}else if("year".equals(time)){
			sql = "SELECT SUM(ACCEPT_YEAR) AS ACCEPT,SUM(DORP_YEAR) AS DORP,SUM(FINISH_YEAR) AS FINISH FROM PRE_STATISTICS_REGION_YL T WHERE TO_CHAR(T.CREATE_TIME,'YYYY/MM/DD')=TO_CHAR(SYSDATE,'YYYY/MM/DD')";
		}else if("all".equals(time)){
			sql = "SELECT SUM(ACCEPT_ALL) AS ACCEPT,SUM(DORP_ALL) AS DORP,SUM(FINISH_ALL) AS FINISH FROM PRE_STATISTICS_REGION_YL T WHERE TO_CHAR(T.CREATE_TIME,'YYYY/MM/DD')=TO_CHAR(SYSDATE,'YYYY/MM/DD')";
		}
		this.setDataSourceName(ylxfDataSource);
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		DataSet a=this.executeQuery(sql);
		return a;
	}
	
	
	/**
	 * 云岭先锋专属网统计
	* @Title: getStateTotalLyxf 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param pset
	* @param @return    设定文件 
	* @return DataSet    返回类型 
	* @author kongws
	 */
	public DataSet getStateTotalLyxf(ParameterSet pset) {		
		String sql="SELECT SUM(ACCEPT_DAY) AS ACCEPTDAY ,SUM(DORP_DAY) AS DORPDAY ,SUM(FINISH_DAY) AS FINISHDAY,"+
 "SUM(ACCEPT_WEEK) AS ACCEPTWEEK,SUM(DORP_WEEK) AS DORPWEEK,SUM(FINISH_WEEK) AS FINISHWEEK ,"+
 "SUM(ACCEPT_MOUTH) AS ACCEPTMOUTH,SUM(DORP_MOUTH) AS DORPMOUTH,SUM(FINISH_MOUTH) AS FINISHMOUTH, "+
 "SUM(ACCEPT_YEAR) AS ACCEPTYEAR,SUM(DORP_YEAR) AS DORPYEAR,SUM(FINISH_YEAR) AS FINISHYEAR, " +
 "SUM(ACCEPT_ALL) AS ACCEPTALL,SUM(DORP_ALL) AS DORPALL,SUM(FINISH_ALL) AS FINISHALL "+
 "FROM PRE_STATISTICS_REGION_YL T WHERE t.create_time=(select MAX(create_time) from PRE_STATISTICS_REGION_YL) ";

		this.setDataSourceName(ylxfDataSource);
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		DataSet a=this.executeQuery(sql);
		return a;
	}
	
	
	public DataSet getHotLyxf(ParameterSet pset) {
		pset.remove("time");
		String sql =null;
		sql="select * from (select T.ITEM_NAME, T.ORG_NAME,  SUM(ACCEPT_DAY) AS ACCEPT, SUM(DORP_DAY) AS DORP,  SUM(FINISH_DAY) AS FINISH from PRE_STATISTICS_REGION_YL t GROUP BY T.ITEM_NAME, T.ORG_NAME)  where ACCEPT<>'0' or DORP<>'0' or FINISH<>'0' and rownum<20";
		this.setDataSourceName(ylxfDataSource);
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		DataSet a=this.executeQuery(sql);
		return a;
	}
	
	/**
	 * 根据申办流水号和查询密码进行查询(从前置库去查)
	 * @param pset
	 * @return
	 */
	public DataSet formInfoQueryByNameAndPassword(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String sblsh=String.valueOf(pset.getParameter("sblsh"));
		String cxmm=String.valueOf(pset.getParameter("cxmm"));
		String sql="select projid,projectname,itemname,APPLICANT,OCCURTIME,PROMISEETIME,STATUS from EA_JC_STEP_BASICINFO t where  projid=? and projpwd=?";
		try{
		ds=this.executeDataset(sql, new Object[]{sblsh,cxmm},qzDataSource);
		if(ds.getTotal()>0){
			ds.setState(StateType.SUCCESS);
			ds.setMessage("数据库查询成功！");	
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库查询失败！");	
		}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 根据申办流水号和电话号码进行查询(从前置库去查)
	 * @param pset
	 * @return
	 */
	public DataSet formInfoQueryByNameAndPhone(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String sblsh=String.valueOf(pset.getParameter("sblsh"));
		String cxmm=String.valueOf(pset.getParameter("cxhm"));
		String sql="select projid,projectname,itemname,APPLICANT,OCCURTIME,PROMISEETIME,STATUS from EA_JC_STEP_BASICINFO t where  projid=? and APPLICANTMOBILE=?";
		try{
		ds=this.executeDataset(sql, new Object[]{sblsh,cxmm},qzDataSource);
		if(ds.getTotal()>0){
			ds.setState(StateType.SUCCESS);
			ds.setMessage("数据库查询成功！");	
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库查询失败！");	
		}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 根据申办流水号查询(从前置库去查)
	 * @param pset
	 * @return
	 */
	public DataSet formInfoQueryBySBLSH(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String sblsh=String.valueOf(pset.getParameter("sblsh"));
		String sql="select projid,projectname,itemname,APPLICANT,to_char(occurtime,'yyyy-mm-dd hh24:mi:ss') occurtime,PROMISEETIME,STATUS from EA_JC_STEP_BASICINFO t where  projid=?";
		try{
		ds=this.executeDataset(sql, new Object[]{sblsh},qzDataSource);
		if(ds.getTotal()>0){
			ds.setState(StateType.SUCCESS);
			ds.setMessage("数据库查询成功！");	
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库查询失败！");	
		}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	
	/**
	 * 获取流转过程信息
	 * @param pset
	 * @return
	 * @author kongws
	 * @since 2015-12-24
	 */
	public DataSet GetBusinessProgressInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sblsh=String.valueOf(pset.getParameter("sblsh"));
		String sql="select t.PROJID,NODENAME,NODEPROCERNAME,t.OCCURTIME,NODEADV from EA_JC_STEP_PROC t where t.projid=? ORDER BY SN ASC"; //DESC
		try{
		ds=this.executeDataset(sql, new Object[]{sblsh},qzDataSource);
		if(ds.getTotal()>0){
			ds.setState(StateType.SUCCESS);
			ds.setMessage("数据库查询成功！");	
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库查询失败！");	
		}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	
	/**
	 * 
	 * @Description: 获取业务详细信息(往前置库去查基本信息)
	 * @param @param pset TYPE 0:业务列表 1：企业设立 2：工程建设
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2013-8-28
	 * @throws
	 */
	public DataSet getBusinessIndexAllList(ParameterSet pset) {
		DataSet ds = new DataSet();
		String type = null;
		if (pset.containsKey("TYPE")) {
			type = String.valueOf(pset.getParameter("TYPE"));
			pset.remove("TYPE");
		}
		if ("1".equals(type)) {

		} else if ("2".equals(type)) {

		} else { // 默认单体事项基本信息
			String sql = "SELECT SBLSH, SXBM, SXMC, UCID, SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
					+ " SJDWDM, SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
					+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE FROM BUSINESS_INDEX B WHERE 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				int start = pset.getPageStart();
				int limit = pset.getPageLimit();
				if (start == -1 || limit == -1) {
					ds = this.executeDataset(sql);
				} else {
					ds = this.executeDataset(sql, start, limit, null);
				}
				if (ds.getTotal() > 0) {
					JSONObject o = ds.getRecord(0);
					String sblsh = o.getString("SBLSH");
					HttpClientUtil client = new HttpClientUtil();
					String url = HttpUtil.formatUrl(SecurityConfig
							.getString("approval_url")
							+ "/getBusinessInfo?receiveNumber=" + sblsh);
					if ("ql".equals(o.getString("REMARK"))) {
						url = HttpUtil.formatUrl(SecurityConfig
								.getString("PowerOperation_url")
								+ "/getBusinessInfo?receiveNumber=" + sblsh);
					}
					Object ret = client.getResult(url, "");
					JSONArray info;
					info = JSONObject.fromObject(ret).getJSONArray("info");
					o.put("CONTENT", info);
					ds.setRawData(o);
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		return ds;
	}

	/**
	 * 
	 * @Description: 获取表单详细信息
	 * @param @param pset TYPE 0:业务列表 1：企业设立 2：工程建设
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2015-8-10
	 * @throws
	 */
	public DataSet formInfoQueryByNameAndId(ParameterSet pset) {

		DataSet ds = new DataSet();
		String type = null;
		if (pset.containsKey("TYPE")) {
			type = String.valueOf(pset.getParameter("TYPE"));
			pset.remove("TYPE");
		}
		if ("0".equals(type)) { // 业务列表
			String sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				ds = this.executeDataset(sql);
				if (ds != null && ds.getData() != null) {
					JSONArray ja = ds.getJAData();
					if (ja.size() > 0) {
						JSONObject jb = ja.getJSONObject(0);
						jb = jb.getJSONObject("BASECONTENT");
						String dataId = jb.getString("dataId");
						String formId = jb.getString("formId");
						String itemId = jb.getString("itemId");
						jb = new JSONObject();
						jb.put("dataId", dataId);
						jb.put("formId", formId);
						jb.put("itemId", itemId);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		} else if ("1".equals(type)) { // 企业设立
			if (pset.containsKey("SBLSH")) {
				String SBLSH = String.valueOf(pset.getParameter("SBLSH"));
				pset.remove("SBLSH");
				pset.put("ID", SBLSH);
			}
			String sql = "select ID as SBLSH, FORM_ID as formId, DATA_ID as dataId from enterprise_business_index WHERE 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				ds = this.executeDataset(sql);
				if (ds != null && ds.getData() != null) {
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
					if (ja.size() > 0) {
						JSONObject jb = ja.getJSONObject(0);
						jb.put("dataId", String.valueOf(jb.get("DATAID")));
						jb.put("formId", String.valueOf(jb.get("FORMID")));
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		} else if ("2".equals(type)) {
			if (pset.containsKey("SBLSH")) {
				String SBLSH = String.valueOf(pset.getParameter("SBLSH"));
				pset.remove("SBLSH");
				pset.put("BIZ_ID", SBLSH);
			}
			String sql = "select BIZ_ID as SBLSH, FORM_ID as formId, DATA_ID as dataId from parallel_biz_base where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				ds = this.executeDataset(sql);
				if (ds != null && ds.getData() != null) {
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
					if (ja.size() > 0) {
						JSONObject jb = ja.getJSONObject(0);
						jb.put("dataId", String.valueOf(jb.get("DATAID")));
						jb.put("formId", String.valueOf(jb.get("FORMID")));
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}

		return ds;
	}

	/**
	 * 
	 * @Description: 获取材料信息列表
	 * @param @param pset TYPE 0:业务列表 1：企业设立 2：工程建设
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2015-8-13
	 * @throws
	 */
	public DataSet materialListQueryByNameAndId(ParameterSet pset) {
		String type = null;
		DataSet ds = new DataSet();
		if (pset.containsKey("TYPE")) {
			type = String.valueOf(pset.getParameter("TYPE"));
			pset.remove("TYPE");
		}
		if ("0".equals(type)) { // 业务列表
			String sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				ds = this.executeDataset(sql);
				if (ds != null && ds.getData() != null) {
					JSONArray ja = ds.getJAData();
					if (ja.size() > 0) {
						JSONObject jb = ja.getJSONObject(0);
						jb = jb.getJSONObject("BASECONTENT");
						String metail = String.valueOf(jb.get("metail"));
						jb = new JSONObject();
						jb.put("metail", metail);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		} else if ("1".equals(type)) { // 企业
			if (pset.containsKey("SBLSH")) {
				String SBLSH = String.valueOf(pset.getParameter("SBLSH"));
				pset.remove("SBLSH");
				pset.put("ID", SBLSH);
			}
			String sql = "select APPLY_DATA from enterprise_business_index  where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				ds = this.executeDataset(sql);
				if (ds != null && ds.getData() != null) {
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
					if (ja.size() > 0) {
						JSONObject jb = ja.getJSONObject(0);
						String re = String.valueOf(jb.get("APPLY_DATA"));
						JSONArray materialArray = JSONArray.fromObject(re);
						JSONArray materialArrayResu = new JSONArray();
						for (int i = 0; i < materialArray.size(); i++) {
							jb = materialArray.getJSONObject(i);
							jb.put("DOCUMENT_ID", jb.get("resourceCode"));
							jb.put("DOCUMENT_NAME", jb.get("fileName"));
							jb.put("FILE_NAME", jb.get("name"));
							jb.put("FILE_PATH", jb.get("filePath"));
							jb.put("OPERATOR_NAME", jb.get("— —"));
							jb.put("SUBJECT", jb.get("subject"));
							materialArrayResu.add(jb);
						}
						jb = new JSONObject();
						jb.put("metail", materialArrayResu);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		} else if ("2".endsWith(type)) { // 工程设立
			if (pset.containsKey("SBLSH")) {
				String SBLSH = String.valueOf(pset.getParameter("SBLSH"));
				pset.remove("SBLSH");
				pset.put("BIZ_ID", SBLSH);
			}
			String sql = "select APPLY_DATA from parallel_biz_base where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql,
						this.getDataSource());
				ds = this.executeDataset(sql);
				if (ds != null && ds.getData() != null) {
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
					if (ja.size() > 0) {
						JSONObject jb = ja.getJSONObject(0);
						String re = String.valueOf(jb.get("APPLY_DATA"));
						re = getResourceStr(re);
						JSONArray materialArray = JSONArray.fromObject(re);
						JSONArray materialArrayResu = new JSONArray();
						for (int i = 0; i < materialArray.size(); i++) {
							jb = materialArray.getJSONObject(i);
							jb.put("DOCUMENT_ID", jb.get("resourceCode"));
							jb.put("DOCUMENT_NAME", jb.get("fileName"));
							jb.put("FILE_NAME", jb.get("--"));
							jb.put("FILE_PATH", jb.get("filePath"));
							jb.put("OPERATOR_NAME", jb.get("--"));
							jb.put("SUBJECT", jb.get("subject"));
							materialArrayResu.add(jb);
						}
						jb = new JSONObject();
						jb.put("metail", materialArrayResu);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}
		Calendar c = Calendar.getInstance();
		c.getTimeInMillis();
		return ds;
	}

	/**
	 * 截取resource字符串
	 * 
	 * @param applydata
	 * @return
	 */
	public String getResourceStr(String applydata) {
		String result = "";
		if (applydata != null) {
			int i = applydata.indexOf("[");
			int j = applydata.indexOf("]");
			result = applydata.substring(i, j + 1);
		}
		return result;
	}

	public DataSet BusinessProgressQueryByPassword(ParameterSet pset) {

		DataSet ds = new DataSet();
		String spsxcxmm = (String) pset.remove("spsxcxmm");
		String sblsh = (String) pset.remove("sblsh");
		Object ret;
		try {
			HttpClientUtil client = new HttpClientUtil();
			String urlSp = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/getAllBusinessInfo?receiveNumber="
					+ sblsh
					+ "&password=" + spsxcxmm);// panyl
			String urlQl = HttpUtil.formatUrl(SecurityConfig
					.getString("PowerOperation_url")
					+ "/getAllBusinessInfo?receiveNumber="
					+ sblsh
					+ "&password=" + spsxcxmm);
			ret = client.getResult(urlSp, "");
			if ("zs".equals(SecurityConfig.getString("AppId"))) {
				if ("300".equals(JSONObject.fromObject(ret).getString("state"))) {
					ret = client.getResult(urlQl, "");
				}
				if ("200".equals(JSONObject.fromObject(ret).getString("state"))) {
					JSONObject obj  = JSONObject.fromObject(ret);
					ds.setRawData(obj);
					ds.setState(StateType.SUCCESS);
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage(JSONObject.fromObject(ret).getString("error"));
				}
			} else {
				if ("200".equals(JSONObject.fromObject(ret).getString("state"))) {
					JSONObject obj = JSONObject.fromObject(ret);
					ds.setRawData(obj);
					ds.setState(StateType.SUCCESS);
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage(JSONObject.fromObject(ret).getString("error"));
				}
			}

		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet BusinessProgressQuery(ParameterSet pset) {

		DataSet ds = new DataSet();
		String sblsh = (String) pset.remove("sblsh");
		//String lxrsj = (String) pset.remove("lxrsj");
		Object ret;
		//写到此处
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getAllBusinessInfo");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue
					.append(" and ( company.org_name=? or person.name=? or project.project_name=? ) ");
			whereValue.append(" and receive_number=? ");
			paramValue.add(sblsh);
			String s = url + "?whereValue="
					+ URLEncoder.encode(whereValue.toString(), "UTF-8")
					+ "&paramValue="
					+ URLEncoder.encode(paramValue.toString(), "UTF-8");
			HttpClientUtil client = new HttpClientUtil();
			ret = client.getResult(s, "");

			JSONObject obj = JSONObject.fromObject(ret);
			ds.setState(StateType.SUCCESS);
			ds.setRawData(obj);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet UnionSearch(ParameterSet pset) {
		String jgm = (String) pset.remove("jgm");
		String sql = "SELECT * FROM rollinglist t WHERE t.jgm= '" + jgm + "'";
		DataSet ds;
		ds = this.executeDataset(sql);
		if (ds.getTotal() > 0) {
			JSONObject o = ds.getRecord(0);
			ds.setRawData(o);
			ds.setMessage((String) o.get("TYPE"));
		}
		return ds;
	}
	
//办件结果统计
	/*public DataSet BusinessNoticeQuery(ParameterSet pset) {
		String sql = "SELECT SBLSH, SXBM, SXMC, UCID, SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
				+ "  SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
				+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE FROM BUSINESS_INDEX B WHERE 1=1 ";
		DataSet ds = new DataSet();
		try {
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			int start = pset.getPageStart();
			int limit = pset.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}*/
	
	public DataSet BusinessNoticeQuery(ParameterSet pset) {
		String region_id=SecurityConfig.getString("WebRegion");
		String sql = "SELECT PROJID,SUBITEMNO,ITEMNAME,APPLICANT,PROJECTNAME,ACCEPTDEPTID,"
				+"ACCEPTDEPTNAME,OCCURTIME,STATE FROM EA_JC_STEP_BASICINFO WHERE 1=1 AND STATE<>'0' AND REGION_ID="+region_id;
		String projid=(String) pset.get("sblsh");
		pset.remove("sblsh");
		pset.put("projid", projid);
		DataSet ds = new DataSet();
		try {
				this.setDataSourceName(qzDataSource);
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				//按时间排序
				sql+=" order by EXCHANGETIME DESC ";
				int start = pset.getPageStart();
				int limit = pset.getPageLimit();
				if (start == -1 || limit == -1) {
					ds = this.executeDataset(sql);
				} else {
					ds = this.executeDataset(sql, start, limit, null);
				}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	// 重庆滚动办件公告
	public DataSet GetRollingList(ParameterSet pset) {
		String sql = "select * from rollinglist t";
		DataSet ds = new DataSet();
		try {
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			int start = pset.getPageStart();
			int limit = pset.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusinessIndexCount(ParameterSet pSet) {
		String sql = "";
		StringBuffer sqlBuff = new StringBuffer();
		String parm = "";
		String ucid = (String) pSet.remove("ucid");
		for (int i = 0; i < 5; i++) {
			parm = (String) pSet.remove("type" + i);
			sqlBuff.append("SELECT count(*) count FROM BUSINESS_INDEX t, POWER_BASE_INFO WHERE SXBM = CODE and UCID = '"
					+ ucid + "' and t.state in (" + parm + ")");
			if (i != 4) {
				sqlBuff.append("union all ");
			}
		}
		sql = sqlBuff.toString();
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql);
	}

	public DataSet deleteBusiness(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "delete from BUSINESS_INDEX ";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("SBLSH@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql,
						this.getDataSource());
				int i = this.executeUpdate(sql);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("参数ids的值为空！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBzbqLog(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sblsh = (String) pSet.remove("SBLSH");
			String sql = "select sblsh,to_char(BZBQSJ,'yyyy-mm-dd hh24:mi:ss') BZBQSJ,BZBQYY from BUSINESS_BZBQ_LOG where sblsh='"
					+ sblsh + "' order by BZBQSJ DESC";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	// 王德龙 10.23 修改为调用接口
	public DataSet updateBusinessIndex(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = "UPDATE BUSINESS_INDEX SET STATE = '03' WHERE SBLSH = ?";
		try {
			String sblsh = (String) pSet.getParameter("SBLSH");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/businessCancel?receiveNum="
					+ sblsh);
			HttpClientUtil client = new HttpClientUtil();
			Object ret = client.getResult(url, "");
			if ("200".equals(JSONObject.fromObject(ret).getString("state"))) {
				int i = this.executeUpdate(sql,
						new Object[] { (String) pSet.getParameter("SBLSH") },
						this.getDataSourceName());
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据库操作失败！");
				} else {
					ds.setState(StateType.SUCCESS);
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("该业务已经受理，不允许撤销！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}
		return ds;
	}

}
