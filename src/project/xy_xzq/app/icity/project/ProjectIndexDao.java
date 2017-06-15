package xy_xzq.app.icity.project;

import io.netty.util.CharsetUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.ServiceCmd;
import app.icity.guestbook.WriteDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class ProjectIndexDao extends BaseJdbcDao {
	protected static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	protected static String icityDataSource = "icityDataSource";

	public ProjectIndexDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao)DaoFactory.getDao(ProjectIndexDao.class.getName());
	}

	public DataSet getBusinessIndexList(ParameterSet pset) {
		try{
		if("zs".equals(SecurityConfig.getString("AppId"))){
			//查询并更新缴费状态
			queryAndUpdatePayStatus(pset);
		}
		
		DataSet ds;
		String searchTitle = (String) pset.remove("SEARCH_TITLE");
		String sql = "SELECT paystatus,paycontent,LIMIT_NUM,SBLSH,SXID,SXBM,SXMC,SJDW,SQRMC,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.id as starLevelId FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE where 1=1 ";
		
		if("chq".equals(SecurityConfig.getString("AppId"))){
			sql = "SELECT paystatus,paycontent,LIMIT_NUM,t.SBLSH,t.SXID,SXBM,SXMC,SJDW,SQRMC,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,t.STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.id as starLevelId,b.doc_id,b.file_name,c.state billstate FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE left join BANJIE_ATTACH b on t.sblsh=b.sblsh left join BUSINESS_BILL_PAYMENT c on t.sblsh=c.sblsh where 1=1 ";
		}
		
		if (StringUtil.isNotEmpty(searchTitle)) {
			sql += " AND (t.SBLSH like '%" + searchTitle + "%' OR t.SBXMMC like '%" + searchTitle + "%' OR t.SXMC like '%"
					+ searchTitle + "%' ) ";
		}
		sql = sql.replace("TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss')", "");
		//String DEFAULT_DB_TYPE = DataSourceConfig.getString("jdbc.dataType","orcl");
		//处理时间查询条件
		String SBSJ_s = (String) pset.remove("SBSJ@>=@Date");
		String SBSJ_e = (String) pset.remove("SBSJ@<=@Date");
		List<Object> param=new ArrayList<Object>();
		Timestamp d =null;
		if(StringUtil.isNotEmpty(SBSJ_s)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_s,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND SBSJ >= ?";
		}
		if(StringUtil.isNotEmpty(SBSJ_e)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_e,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND SBSJ <= ?";
		}
		//处理投诉评价 默认查询结果：最近一周办结的件和正在处理的件
		String complaint_evaluation = (String) pset.remove("complaint_evaluation");
		if(StringUtil.isNotEmpty(complaint_evaluation)&&complaint_evaluation.equals("1")){
			String bjsj_s = (String) pset.remove("bjsj_s");
			String state = (String) pset.get("STATE@IN");
			if(!state.equals("01,16")&&(state.contains("01")||state.contains("06"))){//包含待搜索办理中的件
				pset.remove("STATE@IN");
				d = CommonUtils_api.getInstance().parseStringToTimestamp(bjsj_s,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND (STATE in ('01','16') OR (STATE in ('02','13','14','96','97','98','99') AND BJSJ >=?)) ";
			}
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, param.toArray(), this.getDataSourceName());
		} else {
			ds = this.executeDataset(sql, start, limit, param.toArray(), this.getDataSourceName());
		}
		return ds;
		}catch(Exception e){
			e.printStackTrace();
			return new DataSet();
		}
		
	}
	
	public DataSet getBusinessEnterList(ParameterSet pset) {
		String searchTitle = (String)pset.remove("SEARCH_TITLE");
		String sql="SELECT a.receive_id,a.ID,a.APPLY_SUBJECT,a.FIRST_GRADE_CODE,to_char(a.APPLY_TIME,'yyyy-MM-dd hh24:mm:ss') APPLY_TIME,a.CURRENT_STATE,a.FORM_ID,a.ENTERPRISE_TYPE,a.DATA_ID,a.APPLY_DATA,(select sum(case when b.is_lhtk='1' then 1 else 0 end) is_lhtk from enterprise_business_course b where b.biz_id = a.id) IS_LHTK from ENTERPRISE_BUSINESS_INDEX a where 1=1 ";
		if(StringUtil.isNotEmpty(searchTitle)){
			sql+=" AND (SBLSH like '%"+searchTitle+"%' OR SBXMMC like '%"+searchTitle+"%' OR SXMC like '%"+searchTitle+"%' ) ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		DataSet ds;
		if (start == -1 || limit == -1) {
			ds =  this.executeDataset(sql, new Object[]{}, this.getDataSourceName());
		}else {
			ds = this.executeDataset(sql, start, limit);//.executeDataset(sql, start, limit, new Object[]{}, this.getDataSourceName());
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
		int i = this.executeUpdate(sql, new Object[] { (String) pset.getParameter("REMARK"), (String) pset.getParameter("SBLSH") }, this.getDataSourceName());
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
	
	public DataSet getTotal(ParameterSet pSet) {
		pSet.put("region_code", SecurityConfig.getString("WebRegion"));
		DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
		JSONArray organs = dept.getJOData().getJSONArray("organ");
		String tempsql = "";
		if (organs.size() > 0) {
			JSONObject organ = null;
			StringBuffer sqlBuff = new StringBuffer(" A.SJDW in ( ");
			for (int i = 0; i < organs.size(); i++) {
				organ = organs.getJSONObject(i);
				if ("1".equals(organ.get("IS_HALL"))) {
					sqlBuff.append("'"+organ.get("NAME")+"'");
						sqlBuff.append(",");
				}
			}
			tempsql = sqlBuff.toString();
			tempsql = tempsql.substring(0,tempsql.length()-1);
		}
		pSet.remove("region_code");
		String sql = "SELECT A.SJDW AS DEPTNAME,COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A WHERE "+tempsql+")";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		sql += " GROUP BY A.SJDW ";
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
	public DataSet getStateTotal(ParameterSet pset) {
		String time = (String) pset.getParameter("time");
		pset.remove("time");
		String sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A WHERE STATE IS NOT NULL ";
		if("day".equals(time)){
			sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM (Select * From BUSINESS_INDEX a Where trunc(sbsj) = trunc(Sysdate)) A WHERE STATE IS NOT NULL ";
		}else if("month".equals(time)){
			sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM (Select * From BUSINESS_INDEX a Where to_char(sbsj,'yyyymm') = to_char(Sysdate,'yyyymm')) A WHERE STATE IS NOT NULL ";
		}else if("year".equals(time)){
			sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM (Select * From BUSINESS_INDEX a Where to_char(sbsj,'yyyy') = to_char(Sysdate,'yyyy')) A WHERE STATE IS NOT NULL ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		sql += " GROUP BY A.STATE ";
		return this.executeQuery(sql);
	}

	/**
	 * 
	 * @Description: 获取业务详细信息
	 * @param @param pset   TYPE 0:业务列表      1：企业设立        2：工程建设 
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2013-8-28
	 * @throws
	 */
	public DataSet getBusinessIndexAllList(ParameterSet pset) {
		DataSet ds = new DataSet();
		String type = null;
		if(pset.containsKey("TYPE")){
			type = String.valueOf( pset.getParameter("TYPE") );
			pset.remove("TYPE");
		}
		if("1".equals(type)){
			
		}else if("2".equals(type)){
			
		}else{       //默认单体事项基本信息
			String sql = "SELECT SBLSH, SXBM, SXMC,SXID, UCID, SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
					+ " SJDWDM, SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
					+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.EVALUATE_CONTENT,"
					+ "l.id as starLevelId FROM BUSINESS_INDEX B left join star_level_evaluation l on B.Sblsh=l.serial_number WHERE 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				int start = pset.getPageStart();
				int limit = pset.getPageLimit();
				if (start == -1 || limit == -1) {
					ds = this.executeDataset(sql);
				} else {
					ds = this.executeDataset(sql, start, limit, null);
				}
				if(ds.getTotal()>0){
					JSONObject o=ds.getRecord(0);
					String sblsh=o.getString("SBLSH");
					HttpClientUtil client = new HttpClientUtil();
					String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getBusinessInfo?receiveNumber="+sblsh);
					if("ql".equals(o.getString("REMARK"))){
						url = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/getBusinessInfo?receiveNumber="+sblsh);
					}
					Object ret= client.getResult(url,"");
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
	 * @param @param pset    TYPE 0:业务列表      1：企业设立        2：工程建设 
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2015-8-10
	 * @throws
	 */
	public DataSet formInfoQueryByNameAndId(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String type = null;
		if(pset.containsKey("TYPE")){
			type = String.valueOf( pset.getParameter("TYPE") );
			pset.remove("TYPE");
		}
		if("0".equals(type)){    //业务列表
			String sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb=jb.getJSONObject("BASECONTENT");
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
		}else if("1".equals(type)){     //企业设立
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("ID", SBLSH);
			}
			String sql = "select ID as SBLSH, FORM_ID as formId, DATA_ID as dataId from enterprise_business_index WHERE 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb.put("dataId", String.valueOf( jb.get("DATAID")));
						jb.put("formId", String.valueOf( jb.get("FORMID")));
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("2".equals(type)){
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("BIZ_ID", SBLSH);
			}
			String sql = "select BIZ_ID as SBLSH, FORM_ID as formId, DATA_ID as dataId from parallel_biz_base where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb.put("dataId", String.valueOf( jb.get("DATAID")));
						jb.put("formId", String.valueOf( jb.get("FORMID")));
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
	 * @param @param pset  TYPE 0:业务列表      1：企业设立        2：工程建设 
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2015-8-13
	 * @throws
	 */
	public DataSet materialListQueryByNameAndId(ParameterSet pset) {
		String type = null;
		DataSet ds = new DataSet();
		if(pset.containsKey("TYPE")){
			type = String.valueOf( pset.getParameter("TYPE") );
			pset.remove("TYPE");
		}
		if("0".equals(type)){      //业务列表
			String sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb = jb.getJSONObject("BASECONTENT");
						String metail = String.valueOf( jb.get("metail") );
						String windowsub = String.valueOf(jb.get("windowsub"));
						jb = new JSONObject();
						jb.put("metail", metail);
						jb.put("windowsub", windowsub);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("1".equals(type)){          //企业
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("ID", SBLSH);
			}
			String sql = "select APPLY_DATA from enterprise_business_index  where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){				
                     JSONArray ja = ds.getJAData();                    
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						String re = String.valueOf( jb.get("APPLY_DATA") );
						JSONArray materialArray = JSONArray.fromObject(re);
						JSONArray materialArrayResu = new JSONArray();
						for(int i=0; i<materialArray.size(); i++){
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
						ds.setRawData(jb);
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("2".endsWith(type)){        //工程设立 
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("BIZ_ID", SBLSH);
			}
			String sql = "select APPLY_DATA from parallel_biz_base where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						String re = String.valueOf( jb.get("APPLY_DATA") );
						re = getResourceStr(re);
						JSONArray materialArray = JSONArray.fromObject(re);
						JSONArray materialArrayResu = new JSONArray();
						for(int i=0; i<materialArray.size(); i++){
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
	 * @param applydata
	 * @return
	 */
	public String getResourceStr(String applydata){
		String result = "";
		if(applydata != null){
			int i = applydata.indexOf("[");
			int j = applydata.indexOf("]");
			result = applydata.substring(i, j+1);
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
				String urlSp = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo?receiveNumber="+sblsh+"&password="+spsxcxmm);//panyl
				String urlQl = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/getAllBusinessInfo?receiveNumber="+sblsh+"&password="+spsxcxmm);
				ret = client.getResult(urlSp,"");
				if("zs".equals(SecurityConfig.getString("AppId"))){
					if("300".equals(JSONObject.fromObject(ret).getString("state"))){
						ret = client.getResult(urlQl,"");
					}
					if("200".equals(JSONObject.fromObject(ret).getString("state"))){
						JSONObject obj =  JSONObject.fromObject(ret);
						ds.setRawData(obj);
						ds.setState(StateType.SUCCESS);
					}else{
						ds.setState(StateType.FAILT);
						ds.setMessage(JSONObject.fromObject(ret).getString("error"));
					}
				}else{
					if("200".equals(JSONObject.fromObject(ret).getString("state"))){
					JSONObject obj =  JSONObject.fromObject(ret);
					ds.setRawData(obj);
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage(JSONObject.fromObject(ret).getString("error"));
				}}
								
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	public DataSet BusinessProgressQuery(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String sqrmc = (String) pset.remove("sqrmc");
		String sblsh = (String) pset.remove("sblsh");
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo");
			StringBuilder whereValue=new StringBuilder();
			JSONArray paramValue=new JSONArray();
			whereValue.append(" and ( company.org_name=? or person.name=? or project.project_name=? ) ");
			paramValue.add(sqrmc);			
			paramValue.add(sqrmc);
			paramValue.add(sqrmc);
			whereValue.append(" and receive_number=?");
			paramValue.add(sblsh);
			Map<String, String> data=new HashMap<String, String>();
			data.put("whereValue", whereValue.toString());
			data.put("paramValue",paramValue.toString());
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, data));
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
		String jgm = (String)pset.remove("jgm");
		String sql = "SELECT * FROM rollinglist t WHERE t.jgm= '"+jgm+"'";
		DataSet ds;
				ds = this.executeDataset(sql);
			if(ds.getTotal()>0){
				JSONObject o=ds.getRecord(0);
				ds.setRawData(o);
				ds.setMessage((String)o.get("TYPE"));
			}
		return ds;
	}
	public DataSet BusinessNoticeQuery(ParameterSet pset) {
		String sql = "SELECT SBLSH, SXBM, SXMC, UCID, SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
				+ "  SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
				+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE,LICENSE FROM BUSINESS_INDEX B WHERE 1=1 ";
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
	//重庆滚动办件公告
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
			sqlBuff.append("SELECT count(*) count FROM BUSINESS_INDEX t, POWER_BASE_INFO WHERE SXBM = CODE and UCID = '" + ucid + "' and t.state in (" + parm + ")");
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
		try{
			String ids = (String)pSet.getParameter("ids");
			if(StringUtils.isNotEmpty(ids)){
				String sql = "delete from BUSINESS_INDEX ";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("SBLSH@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
				int i= this.executeUpdate(sql);
				if(i==0){
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("参数ids的值为空！");
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return ds;
	}
	
	public DataSet getBzbqLog(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String sblsh = (String) pSet.remove("SBLSH");
			String sql = "select sblsh,to_char(BZBQSJ,'yyyy-MM-dd HH:mm:ss') BZBQSJ,BZBQYY from BUSINESS_BZBQ_LOG where sblsh='"+sblsh+"' order by BZBQSJ DESC";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	
	@SuppressWarnings("deprecation")
	public DataSet updateBusinessIndex(ParameterSet pSet){
		DataSet ds = new  DataSet();
		String enter_situation = "";
		String itemCode = "";
		String itemName = "";
		String orgCode = "";
		String url = "";
		String sql = "UPDATE BUSINESS_INDEX SET STATE = '03' WHERE SBLSH = ?";
		try{
			String sblsh =(String)pSet.getParameter("SBLSH");
			String itemid =(String)pSet.getParameter("ITEMID");	
			HttpClientUtil client = new HttpClientUtil();
			if ("zs".equals(SecurityConfig.getString("AppId"))) {
				String itemurl = SecurityConfig.getString("ItemSynchronizationUrl")+"/getAllItemInfoByItemID?itemId="+itemid;
				itemurl=HttpUtil.formatUrl(itemurl);				
				JSONObject item=JSONObject.fromObject(client.getResult(itemurl,""));
				JSONArray itemInfo = item.getJSONArray("ItemInfo");//基本信息
				JSONObject itemBasicInfo=itemInfo.getJSONObject(0);
				itemCode = itemBasicInfo.getString("CODE");
				itemName = itemBasicInfo.getString("NAME");
				orgCode = itemBasicInfo.getString("ORG_CODE");
				if(itemBasicInfo.containsKey("ENTER_SITUATION")){
					enter_situation = itemBasicInfo.getString("ENTER_SITUATION");
				}
				if ("1".equals(enter_situation)) {
					url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/businessCancel?receiveNum="+sblsh);
				}else{
					url = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/businessCancel?receiveNum="+sblsh);
				}
			}else{
				url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/businessCancel?receiveNum="+sblsh);
			}
			Object ret= client.getResult(url,"");
			if("200".equals(JSONObject.fromObject(ret).getString("state"))){
				int i = this.executeUpdate(sql, new Object[] {(String) pSet.getParameter("SBLSH")}, this.getDataSourceName());
				if (i == 0){
					ds.setState(StateType.FAILT);
					ds.setMessage("数据库操作失败！");
				} else {
					ds.setState(StateType.SUCCESS);
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("该业务已经受理，不允许撤销！");
			}
			
			//撤回短信发送
			String AppId = SecurityConfig.getString("AppId");
			if ("zs".equals(AppId)) {
				
				pSet.setParameter("organ_code", orgCode);
				pSet.setParameter("itemCode", itemCode);
				pSet.setParameter("itemName", itemName);
				pSet.setParameter("userName", pSet.get("userName").toString());
				
				pSet.setParameter("TYPE", "20");
				
				sendMessage(pSet, ds);
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}	
		return ds;
	}
	/**
	 * 根据申办流水号从审批获取办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfoFromApprovalBySblsh(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String sblsh=(String)pSet.get("SBLSH");
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo?receiveNumber="+sblsh);
			/**/
			Object ret= client.getResult(url,"");
			JSONObject info;
			info = JSONObject.fromObject(ret);			
			ds.setRawData(info);
			ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}
		return ds;
	}
	/**
	 * 根据申办流水号从审批获取办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfoFromApproval(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			JSONObject o = new JSONObject();
			String sblsh=(String)pSet.get("SBLSH");
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getBusinessInfo?receiveNumber="+sblsh);
			/**/
			Object ret= client.getResult(url,"");
			JSONArray info;
			info = JSONObject.fromObject(ret).getJSONArray("info");	
			String sql = "select t.*,to_char(sbsj,'yyyy-mm-dd hh24:mi:ss') mysbsj from business_index t where sblsh=?";
			DataSet _ds = this.executeDataset(sql, new Object[]{sblsh});
			ds.setTotal(0);
			if(_ds.getTotal()>0){
				JSONObject jds = _ds.getRecord(0);
				o.put("SXID", jds.getString("SXID"));
				o.put("SXBM", jds.getString("SXBM"));
				o.put("SXMC", jds.getString("SXMC"));
				o.put("SBXMMC", jds.getString("SBXMMC"));
				o.put("SQRMC", jds.getString("SQRMC"));
				o.put("SBSJ", jds.getString("MYSBSJ"));
				o.put("STATE", jds.getString("STATE"));
				ds.setTotal(1);
			}			
			o.put("CONTENT", info);
			ds.setRawData(o);
			ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}
		return ds;
	}
	
	/**
	 * 访问量统计-部门
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsDept(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{			
			String sql = "select visitedtitle name,count(1) count from pub_hits where " +
					"appid='"+SecurityConfig.getString("AppId")+"' and webname != visitedtitle and " +
					"catalog ='0' group by visitedtitle order by count desc";
			ds = this.executeDataset(sql, new Object[]{});			
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("统计失败！");
		}
		return ds;
	}
	/**
	 * 访问量统计-分厅
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsHall(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{			
			String WebRank = (String)pSet.get("WebRank");
			String sql = "select webname name,count(1) count from pub_hits where " +
					"appid='"+SecurityConfig.getString("AppId")+"' and webname = visitedtitle and catalog ='0' " +
					" and WebRank=? group by webname order by count desc";
			ds = this.executeDataset(sql, new Object[]{WebRank});	
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("统计失败！");
		}
		return ds;
	}
	/**
	 * 访问量统计-内容
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsContent(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{			
			String sql = "select t.*,to_char(sbsj,'yyyy-mm-dd hh24:mi:ss') mysbsj from business_index t where sblsh=?";
			ds = this.executeDataset(sql, new Object[]{});				
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}
		return ds;
	}
	/**
	 * 访问量统计-当前分厅数量
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsHallNow(ParameterSet pSet){
		String where = " where 1=1 ";
		String region = (String) pSet.getParameter("region_code");
		String visittime = (String) pSet.getParameter("visittime");
		if(!StringUtils.isNotEmpty(region)){
			region= SecurityConfig.getString("WebRegion");
		}	
		if(StringUtils.isNotEmpty(visittime)){
			where += " and to_char(visittime,'yyyy-mm-dd')='"+visittime+"'";
		}
		DataSet ds = new DataSet();
		try{			
			String sql = "select webname name,count(1) count from pub_hits "+where+" and webname = visitedtitle and catalog ='0' " +
					" and webregion='"+region+"' group by webname order by count desc";
			ds = this.executeDataset(sql, new Object[]{});	
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("统计失败！");
		}
		return ds;
	}
	/**
	 * 新余热门服务
	 * @param pSet
	 * @return
	 */
	public DataSet getPopularServices(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{			
			String sql = "select * from website_hotservice t";
			ds = this.executeDataset(sql, new Object[]{});				
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}
		return ds;
	}
	
	/***
	 * 获取缴费单列表(重庆)
	 * @param pSet
	 * @return
	 */
	public DataSet getBillPaymentList(ParameterSet pSet){
		DataSet ds = new DataSet();
		
		String sblsh = (String)pSet.get("sblsh");
		
		try{			
			String sql = "select sblsh,state,billdetail,suspendid,paytime,source from business_bill_payment where sblsh=?";
			ds = this.executeDataset(sql, new Object[]{sblsh});				
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到缴费信息！");
		}
		return ds;
	}
	
	/**
	 * 缴费前检查是否已缴费（重庆：避免推送数据不同步）
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet checkchargestate(ParameterSet pSet) {
		
		DataSet ds = new DataSet();
		
		String suspendId = (String) pSet.getParameter("suspendId");
		String receiveNum = (String) pSet.getParameter("receiveNum");
		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/checkchargestate");
		Map<String, String> data=new HashMap<String, String>();
		data.put("suspendId", suspendId);
		data.put("receiveNum", receiveNum);

		try {
			JSONObject obj = JSONObject.fromObject(RestUtil.postData(url, data));
			
			if("200".equals(obj.getString("state"))){
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		}catch (Exception e) {
			ds.setMessage("缴费状态查询失败！");
		}
		
		return ds;
	}
	
	/**
	 * 确认缴费（重庆缴费）
	 * 
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet confirmPay(ParameterSet pSet) {
		
		//TOKEN、TIMESTAMP、NONCE、SRC
		Date date = new Date();
		String token = "CHQPAY";
		String timestamp = String.valueOf(date.getTime());
		String nonce = generateString(32);
		String src = "INSPURICITY";
		String signature = "";
		StringBuffer signatureBuffer = new StringBuffer();
		
		//字典排序并加密
		String [] tmpArr  = new String []{token,timestamp,nonce};
		Arrays.sort(tmpArr);
		for(int i=0;i<3;i++){
			signatureBuffer.append(tmpArr[i]);
		}
		signature = signatureBuffer.toString();
		signature = SHA(signature, "SHA-256");
		
		String totalAmount = (String) pSet.getParameter("sum");
		String sblsh = (String) pSet.getParameter("sblsh");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
	    String fillDate = formatter.format(new Date());  
	    
	    DataSet ds;
	    String sql = "select BILLDETAIL from business_bill_payment where sblsh=?";
		ds = this.executeDataset(sql, new Object[]{sblsh});
		JSONObject record = ds.getRecord(0);
		JSONArray detail = record.getJSONArray("BILLDETAIL");
		
		//封装数据
		String bossorgcode = JSONObject.fromObject(detail.get(0)).getString("bossOrgCode");
		String bossregioncode = JSONObject.fromObject(detail.get(0)).getString("bossRegionCode");
		String bossregionname = JSONObject.fromObject(detail.get(0)).getString("bossRegionName");
		String bossorgname = JSONObject.fromObject(detail.get(0)).getString("bossOrgName");
		String payAcctName = JSONObject.fromObject(detail.get(0)).getString("chargeUnit");
		String recAcctName = JSONObject.fromObject(detail.get(0)).getString("bankaccountname");
		String recAcctBank = JSONObject.fromObject(detail.get(0)).getString("bank");
		String recAcct = JSONObject.fromObject(detail.get(0)).getString("account");
		String orgcode = JSONObject.fromObject(detail.get(0)).getString("orgcode");
		JSONArray rows = new JSONArray();
		for (int i = 0; i < detail.size(); i++) {
			JSONObject row = JSONObject.fromObject(detail.get(i));
			
			JSONObject obj = new JSONObject();  
	        obj.put("sortNo", i+1+"");
	        obj.put("itemCode", row.get("charge_itemid"));
	        obj.put("itemName", row.get("charge_itemname"));
	        obj.put("number", row.get("num"));
	        obj.put("std", row.get("real"));
	        obj.put("amt", row.get("should"));
	        rows.add(obj) ;
		}
		
	    String bizId = Tools.getUUID32();
	    JSONObject param = new JSONObject();
	    param.put("bizID", bizId);
		param.put("bizPayCode", bizId);
		param.put("fillDate", fillDate);
		param.put("exeAgencyCode", bossorgcode);
		param.put("exeAgencyName", bossorgname);
		param.put("exeRegionCode",bossregioncode);
		param.put("exeRegionName",bossregionname);
		param.put("payAcctName",payAcctName);
		param.put("recAcctName",recAcctName);
		param.put("recAcctBank",recAcctBank);
		param.put("recAcct",recAcct);
		param.put("totalAmount",totalAmount);
		param.put("payBookItems",rows);
	    
		String url = HttpUtil.formatUrl(SecurityConfig.getString("bosiPayUrl"));
		url = url+"?timestamp="+timestamp+"&nonce="+nonce+"&signature="+signature+"&src="+src;
		
		String bosi_str = "";
		JSONObject bosi_jsonobj;
		String payCode = "";
		
		try {
			
			HttpClientUtil client = new HttpClientUtil();
			bosi_str = client.getResult(url,param.toString(),true);
			bosi_jsonobj = JSONObject.fromObject(bosi_str);  
			
			if("SUCCESS".equals(bosi_jsonobj.get("resultCode").toString())){
				
				//电子缴款书号
				payCode = JSONObject.fromObject(bosi_jsonobj.get("data")).getString("payCode");
				String reqReserved = "CZFSDZZW|"+payCode+"|"+orgcode+"|"+recAcct;

				ds.setRawData(reqReserved);
			}else{
				ds.setMessage(bosi_jsonobj.get("resultCode").toString());
			}
			
		}catch (Exception e) {
			ds.setMessage("缴费失败！");
		}
		
		return ds;
	}
	
	/***
	 * 推送审批缴费成功信息
	 * @param pSet
	 * @return
	 */
	public DataSet confirmcharge(ParameterSet pSet) {
		
		DataSet ds = new DataSet();
		
		String suspendId = (String) pSet.getParameter("suspendId");
		String receiveNum = (String) pSet.getParameter("receiveNum");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    String payTime = formatter.format(new Date());  
		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/confirmcharge");
		Map<String, String> data=new HashMap<String, String>();
		data.put("suspendId", suspendId);
		data.put("receiveNum", receiveNum);
		data.put("payTime", payTime);
		try {
			JSONObject obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if("200".equals(obj.getString("state"))){
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		}catch (Exception e) {
			ds.setMessage("缴费信息推送失败！");
		}
		
		return ds;
	}
	
	//生成32位数字和字母
	public String generateString(int length) { 
		
		String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyz";
		
        StringBuffer sb = new StringBuffer();  
        SecureRandom random = new SecureRandom();  
        for (int i = 0; i < length; i++) {  
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));  
        }  
        return sb.toString();  
    } 
	
	/**
	 * 字符串 SHA 加密
	 * 
	 * @param strSourceText
	 * @return
	 */
	private String SHA(final String strText, final String strType) {
		// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest
						.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 類型结果
				byte byteBuffer[] = messageDigest.digest();

				// 將 byte 轉換爲 string
				StringBuffer strHexString = new StringBuffer();
				// 遍歷 byte buffer
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return strResult;
	}
	
	//缴款成功更新外网数据及推送审批状态
	public DataSet updateBillState(ParameterSet pset) {
		
		DataSet ds;
		String orderid = (String)pset.get("ORDERID");
		String sql = "select sblsh,suspendid from business_bill_payment where orderid=?";
		ds = this.executeDataset(sql, new Object[]{orderid});				
		
		String sblsh = ds.getRecord(0).getString("SBLSH");
		String suspendid = ds.getRecord(0).getString("SUSPENDID");
		
		ParameterSet p = new ParameterSet();
		p.setParameter("suspendId", suspendid);
		p.setParameter("receiveNum", sblsh);
		
		//推送审批
		confirmcharge(p);
		
		String sql1 = "UPDATE BUSINESS_BILL_PAYMENT SET STATE = '1' WHERE ORDERID = ?";
		DataSet ds1 = new DataSet();
		int i = this.executeUpdate(sql1, new Object[] { orderid }, this.getDataSourceName());
		if (i == 0) {
			ds1.setState(StateType.FAILT);
			ds1.setMessage("数据库操作失败！");
		} else {
			ds1.setState(StateType.SUCCESS);
		}
		return ds1;
	}
	
	//更新商户订单号
	public DataSet updateOrderId(ParameterSet pset) {
		String sql = "UPDATE BUSINESS_BILL_PAYMENT SET ORDERID = ? WHERE SBLSH = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, new Object[] { (String) pset.getParameter("ORDERID"),(String) pset.getParameter("SBLSH") }, this.getDataSourceName());
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
	
	// 短信发送
	public void sendMessage(ParameterSet pSet, DataSet ds) {

		String message = "";

		String phonestr = WriteDao.getInstance().getPhoneStr(pSet);

		message = SecurityConfig.getString("message_wwsbch");
		message = message.replace("{itemname}", pSet.get("itemName").toString()).replace("{name}", pSet.get("userName").toString());

		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url")
				+ "sendMessage");
		Map<String, String> map=new HashMap<String, String>();
		map.put("phoneList", phonestr);
		map.put("mesConent", message);

		try {
			RestUtil.postData(url, map);
		} catch (Exception e) {
			ds.setMessage("短信发送失败！");
		}
	}
	
	//查询并更新缴费状态
	public void queryAndUpdatePayStatus(ParameterSet pSet){
		
		StringBuffer str = new StringBuffer();
		DataSet ds = new DataSet();
		String ucid = pSet.get("ucid").toString();
		try {
			String sql = "select t.sblsh from business_index t where t.paystatus='1' and t.ucid = '" + ucid + "'";
			ds = this.executeDataset(sql);
			if (ds != null) {
				for (int i = 0; i < ds.getTotal(); i++) {
					if (i == ds.getTotal() - 1) {
						str.append(ds.getRecord(i).getString("SBLSH"));
					} else {
						str.append(ds.getRecord(i).getString("SBLSH") + ",");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		
		//调用审批接口更新状态
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "chargePayStatusCheck");
		Map<String, String> data = new HashMap<String, String>();
		data.put("postdata", str.toString());
		
		try {
			if(!"".equals(str.toString())){
				JSONObject obj = JSONObject.fromObject(RestUtil.postData(url, data));
//				String jsonContent = "{'info':[{'receiveNumber':'370000370160125003474','paystatus':'1'},{'receiveNumber':'370000370160829006356','paystatus':'1'},{'receiveNumber':'370000370160126003481','paystatus':'1'},{'receiveNumber':'370000370160329004134','paystatus':'1'},{'receiveNumber':'370000370160714005589','paystatus':'2'}],'state':'200'}";
//				JSONObject obj = JSONObject.fromObject(jsonContent);
				
				JSONArray jsonArray = JSONArray.fromObject(obj.get("info"));
				String sql = "UPDATE BUSINESS_INDEX SET PAYSTATUS = ? WHERE SBLSH = ?";
				for (int i = 0; i < jsonArray.size(); i++){
				   JSONObject jsonObject = jsonArray.getJSONObject(i);
				   String receiveNumber = jsonObject.getString("receiveNumber");
				   String paystatus = jsonObject.getString("paystatus");
				   
				   //非查询失败的更新状态
				   if(!"0".equals(paystatus)){
					   int j = this.executeUpdate(sql, new Object[] { paystatus, receiveNumber }, this.getDataSourceName());
					   if (j == 0) {
						   ds.setMessage("数据库操作失败！");
					   } else {
						   ds.setState(StateType.SUCCESS);
					   }
				   }
				}
			}
		} catch (Exception e) {
			_log.error("缴费状态更新失败！");
		}
		
	}
}
