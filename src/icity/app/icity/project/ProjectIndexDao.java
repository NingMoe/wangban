package app.icity.project;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.icity.ServiceCmd;
import app.icity.guestbook.WriteDao;
import app.icity.onlineapply.ApplyDao;
import app.icity.sync.PowerBaseInfoQZCmd;
import app.uc.UserDao;
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
	private static Logger _log = LoggerFactory.getLogger(ProjectIndexDao.class);
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
		String sql = "SELECT paystatus,paycontent,LIMIT_NUM,SBLSH,SXID,SXBM,SXMC,SJDW,SQRMC,SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,t.STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.EVALUATE_CONTENT,l.CREATOR_DATE,l.id as starLevelId FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE where 1=1 ";
		
		if("chq".equals(SecurityConfig.getString("AppId"))){
			sql = "SELECT paystatus,paycontent,LIMIT_NUM,t.SBLSH,t.SXID,SXBM,SXMC,SJDW,SQRMC,SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,t.STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.EVALUATE_CONTENT,l.CREATOR_DATE,l.id as starLevelId,b.doc_id,b.file_name,c.state billstate FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE left join BANJIE_ATTACH b on t.sblsh=b.sblsh left join BUSINESS_BILL_PAYMENT c on t.sblsh=c.sblsh where 1=1 ";
		}
		
		if (StringUtil.isNotEmpty(searchTitle)) {
			sql += " AND (t.SBLSH like '%" + searchTitle + "%' OR t.SBXMMC like '%" + searchTitle + "%' OR t.SXMC like '%"
					+ searchTitle + "%' ) ";
		}
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
		//投诉 查询结果：最近一周办结的件
		if(StringUtil.isNotEmpty((String)pset.get("bjsj_s"))){
			String bjsj_s = (String) pset.remove("bjsj_s");
			d = CommonUtils_api.getInstance().parseStringToTimestamp(bjsj_s,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			//投诉 办结的件 办理成功 办理失败
			sql +=" AND t.BJSJ >=? ";
		}
		//评价：0待评价 1已评价查询条件
		if(StringUtil.isNotEmpty((String)pset.get("EVALUATE_STATUS"))){
			String state = (String) pset.remove("EVALUATE_STATUS");
			if(state.equals("0")){
				sql +=" AND l.star_level is  null ";
			}else if(state.equals("1")){
				sql +=" AND l.star_level is not null ";
			}
		}
		
		if(StringUtil.isNotEmpty((String)pset.get("STATE@IN"))){
			String state = (String) pset.remove("STATE@IN");
			pset.put("t.STATE@IN", state);
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
	public DataSet getBusinessComplaint(ParameterSet pset) {
		try{
		DataSet ds;
		String sql = "SELECT SBLSH,SXID,SXBM,SXMC,SBSJ,UCID,SBXMMC,t.STATE"
				+ " FROM BUSINESS_INDEX t where 1=1 AND not exists(select 1 from GUESTBOOK g where g.busi_id = t.sblsh AND g.type='3')";
		//处理时间查询条件
		String SBSJ_s = (String) pset.remove("SBSJ@>=@Date");
		String SBSJ_e = (String) pset.remove("SBSJ@<=@Date");
		List<Object> param=new ArrayList<Object>();
		Timestamp d =null;
		if(StringUtil.isNotEmpty(SBSJ_s)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_s,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND t.SBSJ >= ?";
		}
		if(StringUtil.isNotEmpty(SBSJ_e)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_e,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND t.SBSJ <= ?";
		}
		//投诉 查询结果：最近一周办结的件
		if(StringUtil.isNotEmpty((String)pset.get("bjsj_s"))){
			String bjsj_s = (String) pset.remove("bjsj_s");
			d = CommonUtils_api.getInstance().parseStringToTimestamp(bjsj_s,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			//投诉 办结的件 办理成功 办理失败
			sql +=" AND t.BJSJ >=? ";
		}
		if(StringUtil.isNotEmpty((String)pset.get("STATE@IN"))){
			String state = (String) pset.remove("STATE@IN");
			pset.put("t.STATE@IN", state);
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
		String sql="SELECT a.receive_id,a.ID,a.APPLY_SUBJECT,a.FIRST_GRADE_CODE,a.APPROVE_NUM,to_char(a.APPLY_TIME,'yyyy-MM-dd hh24:mm:ss') APPLY_TIME,a.CURRENT_STATE,a.FORM_ID,a.ENTERPRISE_TYPE,a.DATA_ID,a.APPLY_DATA,(select sum(case when b.is_lhtk='1' then 1 else 0 end) is_lhtk from enterprise_business_course b where b.biz_id = a.id) IS_LHTK from ENTERPRISE_BUSINESS_INDEX a where 1=1 ";
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
					+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,B.STATE,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.EVALUATE_CONTENT,"
					+ "l.id as starLevelId,l.CRYPTONYM FROM BUSINESS_INDEX B left join star_level_evaluation l on B.Sblsh=l.serial_number WHERE 1=1 ";
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
						String itemCode =jb.getString("itemCode");//重庆食药监判断条件
						String urlAddress = jb.containsKey("urlAddress")?jb.getString("urlAddress"):"";
						String account = jb.containsKey("account")?jb.getString("account"):"";
						String userName = jb.containsKey("userName")?jb.getString("userName"):"";
						String userTypeCode = jb.containsKey("userTypeCode")?jb.getString("userTypeCode"):"";
						String sybmol = jb.containsKey("sybmol")?jb.getString("sybmol"):"";
						jb = new JSONObject();
						jb.put("dataId", dataId);
						jb.put("formId", formId);
						jb.put("itemId", itemId);
						jb.put("itemCode", itemCode);
						jb.put("urlAddress", urlAddress);
						jb.put("account", account);
						jb.put("userName", userName);
						jb.put("userTypeCode", userTypeCode);
						jb.put("sybmol", sybmol);
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
						JSONObject obj = new JSONObject();		
						obj =  JSONObject.fromObject(ret);
						ds.setRawData(obj);
						ds.setState(StateType.SUCCESS);
					}else{
						ds.setState(StateType.FAILT);
						ds.setMessage(JSONObject.fromObject(ret).getString("error"));
					}
				}else{
					if("200".equals(JSONObject.fromObject(ret).getString("state"))){
						JSONObject obj = new JSONObject();		
						obj =  JSONObject.fromObject(ret);
						ds.setRawData(obj);
						ds.setState(StateType.SUCCESS);
					}else{
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
			//重庆12为编码 模糊查询
			if("chq".equals(SecurityConfig.getString("AppId"))){
				Pattern pattern = Pattern.compile("[0-9]{12}");
				Matcher matcher = pattern.matcher((CharSequence)sblsh);
				boolean result=matcher.matches();
				if(result){
					whereValue.append(" and SUBSTR(receive_number, 17, 12)=? ");
					paramValue.add(sblsh);
				}else{
					whereValue.append(" and receive_number = ?");
					paramValue.add(sblsh);
				}
			}else{
				whereValue.append(" and receive_number = ?");
				paramValue.add(sblsh);
			}
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
			String sql = "select sblsh,to_char(BZBQSJ,'yyyy-mm-dd hh24:mi:ss') BZBQSJ,BZBQYY from BUSINESS_BZBQ_LOG where sblsh='"+sblsh+"' order by BZBQSJ DESC";
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
		String userid= (String)pSet.getParameter("userid");
		String usertype= pSet.getParameter("usertype")+"";
		try{
			String sblsh =(String)pSet.getParameter("SBLSH");
			String itemid =(String)pSet.getParameter("ITEMID");	
			HttpClientUtil client = new HttpClientUtil();
			String ret = "";
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
				
				ret= client.getResult(url,"");
			}else{
				if("1".equals(SecurityConfig.getString("is_cloud_accept"))){
					url = HttpUtil.formatUrl(SecurityConfig .getString("cloud_accept")+"/Service/ApplyRevoke");
					JSONObject jo = new JSONObject();
					jo.put("ReceiveNumber", sblsh);
					jo.put("ApplicantType", usertype);
					jo.put("ApplicantID", userid);
					jo.put("Opinion", "网上撤回");
					Map<String, String> map = new HashMap<String, String>();
					map.put("postdata", jo.toString());
					ret = JSONObject.fromObject(RestUtil.postData(url, map)).toString();
				}else{
					url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/businessCancel?receiveNum="+sblsh);
					ret= client.getResult(url,"");
				}
			}
			
			if("200".equals(JSONObject.fromObject(ret).getString("state"))){
				int i = this.executeUpdate(sql, new Object[] {(String) pSet.getParameter("SBLSH")}, this.getDataSourceName());
				if ("1".equals(SecurityConfig.getString("useSubmitInterface"))) {
					JSONObject da = ApplyDao.getInstance().getSbxx((String) pSet.getParameter("SBLSH"));
					pSet.put("data", da);
					pSet.put("action", "14");
					PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
					DataSet dst = powerBaseInfoQZCmd.usesubmitSL(pSet);
					if (dst.getState()==1) {
						System.err.println("从省里撤回成功"+(String) pSet.getParameter("SBLSH"));
					}
				}
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
			String sql = "select sblsh,state,billdetail,suspendid,paytime,source,orderid from business_bill_payment where sblsh=?";
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
	        obj.put("amt", row.get("realall"));
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
			
			_log.error("正式环境调用博思参数>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+param.toString());
			
			bosi_jsonobj = JSONObject.fromObject(bosi_str);  
			
			_log.error("正式环境博思返回结果>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+bosi_jsonobj.toString());
			
			if("SUCCESS".equals(bosi_jsonobj.get("resultCode").toString())){
				
				//电子缴款书号
				payCode = JSONObject.fromObject(bosi_jsonobj.get("data")).getString("payCode");
				String reqReserved = "CZFSDZZW|"+payCode+"|"+orgcode+"|"+recAcct;
				ds.setRawData(reqReserved);
			}else{
				ds.setMessage(bosi_jsonobj.get("resultCode").toString());
				ds.setState(StateType.FAILT);
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
		DataSet ds1 = new DataSet();
		try {
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
			
			String sql1 = "UPDATE BUSINESS_BILL_PAYMENT SET STATE = '1',PAYTIME =? WHERE ORDERID = ?";
			int i = this.executeUpdate(sql1, new Object[] {CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_mm_SS),orderid }, this.getDataSourceName());
			if (i == 0) {
				ds1.setState(StateType.FAILT);
				ds1.setMessage("数据库操作失败！");
			} else {
				ds1.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ds1.setState(StateType.FAILT);
			ds1.setMessage("异常");
		}
		return ds1;
	}
	//缴款日志记录
	public DataSet updateBillLog(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String orderid = (String)pset.get("ORDERID");
			String log = (String)pset.get("LOG");
			String date = CommonUtils_api.getInstance().parseDateToString(new Date(), CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			log = "##["+date+"]"+log;
			String sql = "select LOG from business_bill_payment where orderid=?";
			ds = this.executeDataset(sql, new Object[]{orderid});				
			String logtem = ds.getRecord(0).getString("LOG");
			log = logtem +log;
			ds = new DataSet();
			String sql1 = "UPDATE BUSINESS_BILL_PAYMENT SET LOG = ? WHERE ORDERID = ?";
			int i= this.executeUpdate(sql1, new Object[] {log,orderid}, this.getDataSourceName());
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("异常");
		}
		return ds;
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
	
	@SuppressWarnings({ "deprecation" })
	public DataSet getMacInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		
		String MERCHANTID = SecurityConfig.getString("MERCHANTID");
		String POSID = SecurityConfig.getString("POSID");
		String BRANCHID = SecurityConfig.getString("BRANCHID");
		String ORDERID = (String) pSet.getParameter("ORDERID");
		String PAYMENT = (String) pSet.getParameter("PAYMENT");
		String CURCODE = SecurityConfig.getString("CURCODE");
		String TXCODE = SecurityConfig.getString("TXCODE");
		String REMARK1 = (String) pSet.getParameter("REMARK1");
		String REMARK2 = (String) pSet.getParameter("REMARK2");
		String TYPE = SecurityConfig.getString("TYPE");
		String PUB = SecurityConfig.getString("PUB");
		
		StringBuffer mac = new StringBuffer();
		mac.append("MERCHANTID=").append(MERCHANTID)
		.append("&POSID=").append(POSID)
		.append("&BRANCHID=").append(BRANCHID)
		.append("&ORDERID=").append(ORDERID)
		.append("&PAYMENT=").append(PAYMENT)
		.append("&CURCODE=").append(CURCODE)
		.append("&TXCODE=").append(TXCODE)
		.append("&REMARK1=").append(REMARK1)
		.append("&REMARK2=").append(REMARK2)
		.append("&TYPE=").append(TYPE)
		.append("&PUB=").append(PUB)
		.append("&GATEWAY=")
		.append("&CLIENTIP=")
		.append("&REGINFO=")
		.append("&PROINFO=")
		.append("&REFERER=");
		ds.setRawData(mac.toString());
		return ds;
	}
	/**
	 * 通过申办流水号从审批获取ems业务单号
	 * @param pSet
	 * @return
	 */
	public DataSet getPostInfo(ParameterSet pSet){
		DataSet ds = new DataSet();		
		String receiveNumber = (String) pSet.getParameter("receiveNumber");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getPostInfo?receiveNumber="+receiveNumber);	
		try {
			String s = RestUtil.getData(url);
			JSONObject obj = JSONObject.fromObject(s);			
			if("200".equals(obj.getString("state"))){
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		}catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
			ds.setMessage("业务单号查询失败！");
		}		
		return ds;
	}
	/**
	 * 查询邮寄件信息（威海市）
	 * @param pSet
	 * @return
	 */
	public DataSet getMailInformationList(ParameterSet pSet){
		String sblsh = (String) pSet.getParameter("sblsh");
		DataSet ds = new DataSet();
		try{
			String sql = "select * from (select b.*,s.basecontent from BUSINESS_EXPRESS b left join sub_for_ex_app_information s on s.sblsh = b.sblsh) where sblsh=?";
			ds = this.executeDataset(sql, new Object[]{sblsh});	
		}catch(Exception e){
			_log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}
	/***
	 * 查询邮寄件物流信息（威海市）
	 * （调用ems接口）
	 * @param pSet
	 * @return
	 */
	public DataSet getLogisticsDetailByExpressId(ParameterSet pSet){
		String express_id = (String) pSet.getParameter("express_id");
		DataSet ds = new DataSet();
		try{
			String url = SecurityConfig.getString("getLogisticsDetailUrl");
			url += express_id;
			//url +="1194259000208";
			Map<String ,String> map = new HashMap<String,String>();
			map.put("version", SecurityConfig.getString("ems_queryVersion"));
			map.put("authenticate", SecurityConfig.getString("ems_queryAuthenticate"));
			JSONObject obj = JSONObject.fromObject(RestUtil.getDataWithHeader(url, map));
			//JSONObject obj = JSONObject.fromObject("{\"traces\":[{\"acceptTime\":\"2011-11-24 17:55:00\",\"acceptAddress\":\"上海邮政速递物流长宁经营部\",\"remark\":\"收寄\"},{\"acceptTime\":\"2011-11-24 17:59:00\",\"acceptAddress\":\"上海邮政速递物流长宁经营部\",\"remark\":\"离开处理中心,发往上海市邮政公司邮政速递局\"},{\"acceptTime\":\"2011-11-24 23:54:38\",\"acceptAddress\":\"上海市\",\"remark\":\"到达处理中心,来自上海邮政速递物流长宁经营部\"},{\"acceptTime\":\"2011-11-25 00:17:42\",\"acceptAddress\":\"上海市\",\"remark\":\"离开处理中心,发往USSFOF\"},{\"acceptTime\":\"2011-12-05 07:41:00\",\"acceptAddress\":\"美国 94704\",\"remark\":\"到达投递局\"},{\"acceptTime\":\"2011-12-05 11:07:00\",\"acceptAddress\":\"美国 94703\",\"remark\":\"妥投\"}]}");
			JSONArray traces = obj.getJSONArray("traces");
			ds.setRawData(traces);
			ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			_log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}
	/***
	 * 通过身份证号查询审批窗口收件列表（威海市）
	 * （调用审批接口）
	 * @param pSet
	 * @return
	 */
	public DataSet getWindowBusinessListByIdCard(ParameterSet pSet){
		
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		start = (start+limit)/limit;
		String ucid = (String) pSet.getParameter("ucid");
		//通过ucid获取用户的身份证号
		ParameterSet ps = new ParameterSet();
		ps.put("id", ucid);
		DataSet user = UserDao.getInstance().getList(ps);
		String idCard = user.getJOData().getString("CARD_NO");
		DataSet ds = new DataSet();
		if(StringUtil.isEmpty(idCard)){
			ds.setMessage("请完善账号用户信息！");
			ds.setState(StateType.FAILT);
			return ds;
		}
		try{
			String url = SecurityConfig.getString("approval_url");
			url += "getBusinessListByIdCardck?idCard="+idCard+"&page="+start+"&rows="+limit;
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			//JSONObject obj = JSONObject.fromObject("{\"list\":[{\"columns\":{\"ACCEPT_USER_CODE\":\"A252A8BDEF8D4285A84E286ADE1EBC3A\",\"ACCEPT_USER_NAME\":\"肖莉\",\"APPLICANT\":\"范例水电费\",\"APPLY_FROM\":\"10\",\"APPLY_SUBJECT\":\"关于范例水电费权限内外商投资项目核准-1万亩以下的开荒项目核准的业务\",\"ASSORT\":\"1\",\"BSNUM\":\"371000-127-XK-1472023339122-11064\",\"BUSINESS_OBJECT_CODE\":\"20160810095341100000\",\"BUSINESS_OBJECT_TYPE\":\"1\",\"BUSI_TYPE\":\"1\",\"CURRENT_NODE_ID\":\"a3\",\"CURRENT_NODE_NAME\":\"受理\",\"DATA_ID\":\"20160824154007106100\",\"FLOW_CODE\":\"fa85bf02-69cd-11e6-8145-0025905b36e9\",\"FLOW_DEFINEID\":\"2a7ae857-645e-11e6-8145-0025905b36e9\",\"FORM_ID\":\"TongYongBiaoDan\",\"IDCARD_NO\":\"45345343531\",\"IS_DELETE\":\"0\",\"IS_SETUP\":\"0\",\"ITEM_ID\":\"49B47518EB364797A540DC45B610B3CF\",\"ITEM_NAME\":\"权限内外商投资项目核准-1万亩以下的开荒项目核准\",\"LINK_PHONE\":\"11111111111\",\"NAME\":\"范例水电费\",\"ORG_CODE\":\"371000127\",\"ORG_NAME\":\"发展改革委\",\"PASSWORD\":\"587216\",\"POST_TYPE\":\"0\",\"RECEIVE_NUMBER\":\"710241613710000837015081\",\"REGION_CODE\":\"371000000000\",\"REGION_NAME\":\"威海市\",\"ROWNUM_\":1,\"SCENE_TYPE\":\"0\",\"STATE\":\"01\",\"SUBMIT_TIME\":1472023339000,\"TIME_LIMIT\":1472659200000,\"TYPE\":\"XK\",\"WARNING_TIME\":1472572800000}},{\"columns\":{\"ACCEPT_USER_CODE\":\"A252A8BDEF8D4285A84E286ADE1EBC3A\",\"ACCEPT_USER_NAME\":\"肖莉\",\"APPLICANT\":\"范例水电费\",\"APPLY_FROM\":\"10\",\"APPLY_SUBJECT\":\"关于范例水电费权限内外商投资项目核准-1万亩以下的开荒项目核准的业务\",\"ASSORT\":\"1\",\"BSNUM\":\"371000-127-XK-1470794021586-10980\",\"BUSINESS_OBJECT_CODE\":\"20160810095341100000\",\"BUSINESS_OBJECT_TYPE\":\"1\",\"BUSI_TYPE\":\"1\",\"CURRENT_NODE_ID\":\"a3\",\"CURRENT_NODE_NAME\":\"受理\",\"FLOW_CODE\":\"a21c8df7-5e9f-11e6-ae91-0025905b36e9\",\"FLOW_DEFINEID\":\"0ccbebe2-5ba9-11e6-ae91-0025905b36e9\",\"IDCARD_NO\":\"45345343531\",\"IS_DELETE\":\"0\",\"IS_SETUP\":\"0\",\"ITEM_NAME\":\"权限内外商投资项目核准-1万亩以下的开荒项目核准\",\"LINK_PHONE\":\"11111111111\",\"NAME\":\"范例水电费\",\"ORG_CODE\":\"371000127\",\"ORG_NAME\":\"发展改革委\",\"PASSWORD\":\"700743\",\"POST_TYPE\":\"0\",\"RECEIVE_NUMBER\":\"710101613710000837015004\",\"REGION_CODE\":\"371000000000\",\"REGION_NAME\":\"威海市\",\"ROWNUM_\":2,\"SCENE_TYPE\":\"0\",\"STATE\":\"01\",\"SUBMIT_TIME\":1470794021000,\"TYPE\":\"XK\"}},{\"columns\":{\"ACCEPT_USER_CODE\":\"A252A8BDEF8D4285A84E286ADE1EBC3A\",\"ACCEPT_USER_NAME\":\"肖莉\",\"APPLICANT\":\"范例水电费\",\"APPLY_FROM\":\"10\",\"APPLY_SUBJECT\":\"关于范例水电费权限内外商投资项目核准-1万亩以下的开荒项目核准的业务\",\"ASSORT\":\"1\",\"BSNUM\":\"371000-127-XK-1471257415225-11015\",\"BUSINESS_OBJECT_CODE\":\"20160810095341100000\",\"BUSINESS_OBJECT_TYPE\":\"1\",\"BUSI_TYPE\":\"1\",\"CURRENT_NODE_ID\":\"a3\",\"CURRENT_NODE_NAME\":\"受理\",\"DATA_ID\":\"20160815185405101800\",\"FLOW_CODE\":\"957cdc08-62d6-11e6-8145-0025905b36e9\",\"FLOW_DEFINEID\":\"0ccbebe2-5ba9-11e6-ae91-0025905b36e9\",\"FORM_ID\":\"TongYongBiaoDan\",\"IDCARD_NO\":\"45345343531\",\"IS_DELETE\":\"0\",\"IS_SETUP\":\"0\",\"ITEM_ID\":\"357F790E107B4B369FCFAEB2C3F69948\",\"ITEM_NAME\":\"权限内外商投资项目核准-1万亩以下的开荒项目核准\",\"LINK_PHONE\":\"11111111111\",\"NAME\":\"范例水电费\",\"ORG_CODE\":\"371000127\",\"ORG_NAME\":\"发展改革委\",\"PASSWORD\":\"919475\",\"POST_TYPE\":\"0\",\"RECEIVE_NUMBER\":\"710151613710000837015038\",\"REGION_CODE\":\"371000000000\",\"REGION_NAME\":\"威海市\",\"ROWNUM_\":3,\"SCENE_TYPE\":\"0\",\"STATE\":\"01\",\"SUBMIT_TIME\":1471257415000,\"TIME_LIMIT\":1471881600000,\"TYPE\":\"XK\",\"WARNING_TIME\":1471795200000}}],\"state\":\"200\"}");
			String state = obj.getString("state");
			if("200".equals(state)){
				JSONArray list = obj.getJSONObject("list").getJSONArray("list");
				ds.setState(StateType.SUCCESS);
				ds.setRawData(list);
			}else{
				ds.setMessage(obj.getString("error"));
				ds.setState(StateType.FAILT);
			}
		}catch(Exception e){
			_log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}
	/**
	 *  查询联审联批 流程（项目查询）  威海使用
	 * @param pset
	 * @return
	 */
	public DataSet getParallelList(ParameterSet pset) {
		DataSet ds;
		try{
		String sql = "select b.id from parallel_business_index b where 1=1";
		//处理查询条件
		List<Object> param=new ArrayList<Object>();
		String item_name = (String) pset.get("ITEM_NAME");
		if(StringUtil.isNotEmpty(item_name)){
			param.add("%"+item_name+"%");
			sql +=" AND b.item_name like ?";
		}
		String SBSJ_s = (String) pset.get("SUBMIT_TIME_S");
		String SBSJ_e = (String) pset.get("SUBMIT_TIME_E");
		Timestamp d =null;
		if(StringUtil.isNotEmpty(SBSJ_s)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_s,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND b.submit_time >= ?";
		}
		if(StringUtil.isNotEmpty(SBSJ_e)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_e,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND b.submit_time <= ?";
		}
		String status = (String)pset.get("STATE");
		if(StringUtil.isNotEmpty(status)){
			param.add(status);
			sql +=" AND b.status = ?";
		}
		String sql2 = "select t.BIZ_ID,t.APPLY_SUBJECT,t.PROJECT_NAME from parallel_biz_base t where 1=1 and exists ("+sql+" And b.biz_id = t.biz_id) ";
		if(StringUtil.isNotEmpty((String)pset.get("ucid"))){
			String ucid = (String) pset.remove("ucid");
			sql2 +=" AND ucid = "+ucid;
		}
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql2, param.toArray(), this.getDataSourceName());
		} else {
			ds = this.executeDataset(sql2, start, limit, param.toArray(), this.getDataSourceName());
		}
		return ds;
		}catch(Exception e){
			e.printStackTrace();
			return new DataSet();
		}
	}
	/**
	 *  查询联审联批 流程（项目查询）  威海使用
	 * @param pset
	 * @return
	 */
	public DataSet getItemList(ParameterSet pset) {
		DataSet ds;
		try{
		String sql = "select ID,BIZ_ID,ITEM_NAME,SUBMIT_TIME,STATUS from parallel_business_index b where 1=1";
		//处理查询条件
		List<Object> param=new ArrayList<Object>();
		String biz_id = (String) pset.remove("BIZ_ID");
		if(StringUtil.isNotEmpty(biz_id)){
			param.add(biz_id);
			sql +=" AND b.biz_id = ?";
		}
		String item_name = (String) pset.get("ITEM_NAME");
		if(StringUtil.isNotEmpty(item_name)){
			param.add("%"+item_name+"%");
			sql +=" AND b.item_name like ?";
		}
		String SBSJ_s = (String) pset.remove("SUBMIT_TIME_S");
		String SBSJ_e = (String) pset.remove("SUBMIT_TIME_E");
		Timestamp d =null;
		if(StringUtil.isNotEmpty(SBSJ_s)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_s,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND b.submit_time >= ?";
		}
		if(StringUtil.isNotEmpty(SBSJ_e)){
			d = CommonUtils_api.getInstance().parseStringToTimestamp(SBSJ_e,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			param.add(d);
			sql +=" AND b.submit_time <= ?";
		}
		String status = (String)pset.get("STATE");
		if(StringUtil.isNotEmpty(status)){
			param.add(status);
			sql +=" AND b.status = ?";
		}
		
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
	/**
	 * 获取联审联批 事项信息(威海使用)
	 * @param pSet id
	 * @return
	 */
	public DataSet parallelBusinessIndexById(ParameterSet pset){
		DataSet ds;
		try{
			String sql = "select t.id,t.biz_id,t.submit_time,t.item_id,t.item_name,t.item_code,t.projectname,t.projectcode,t.content,t.STATUS,"
			+"b.apply_subject,b.project_id,b.flow_name,b.flow_id,b.form_id,b.data_id,u.name "
			+"from parallel_business_index t left join parallel_biz_base b on t.biz_id = b.biz_id left join uc_user u on u.id = b.ucid where 1=1 ";
		//处理查询条件
		List<Object> param=new ArrayList<Object>();
		String id = (String) pset.get("id");
		if(StringUtil.isNotEmpty(id)){
			param.add(id);
			sql +=" AND t.id = ?";
		}
			ds = this.executeDataset(sql, param.toArray(), this.getDataSourceName());
			if(ds.getTotal()>0){
				JSONObject o=ds.getRecord(0);
				ds.setRawData(o);
			}
			
			
			return ds;
		}catch(Exception e){
			e.printStackTrace();
			return new DataSet();
		}
	}

	/**
	 * 联审联批 删除办件
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet deleteLslpItem(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String id = (String) pSet.get("id");
		String sql = "delete from parallel_business_index where id='" + id
				+ "'";
		int i = this.executeUpdate(sql);
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据删除失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	/**
	 * 联审联批 撤回办件（威海）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updateLslpBusinessIndex(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "UPDATE parallel_business_index SET STATUS = '0' WHERE ID = ?";
			String id = (String) pSet.get("id");
			int i = this.executeUpdate(sql, new Object[] { id },
					this.getDataSourceName());
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}
		return ds;
	}
	/**
	 * 获取审批表单数据信息(商丘)
	 * @param pSet 
	 * @return
	 */
	public DataSet getFormStandardData(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try{
			String url = SecurityConfig.getString("Form_url");
			String formId = (String) pSet.get("formId");
			String formDataId = (String) pSet.get("formDataId");
			url+="/cform/getFormStandardData?formId="+formId+"&formDataId="+formDataId;
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			String state = obj.getString("state");
			if("200".equals(state)){
				JSONObject  formInfo = obj.getJSONObject("rows");
				ds.setState(StateType.SUCCESS);
				ds.setRawData(formInfo);
			}else{
				ds.setMessage("获取表单信息失败！");
				ds.setState(StateType.FAILT);
			}
		}catch(Exception e){
			_log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("获取表单信息失败！");
		}
		return ds;
	}

	public DataSet updateBusinessInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();;
		try{
			String sblsh = (String) pSet.remove("sblsh");
			String cxmm = (String) pSet.remove("cxmm");
			String ucid = (String) pSet.remove("ucid");
			String sql = "update business_index set ucid=? where sblsh= ? and cxmm= ?";
			int i = this.executeUpdate(sql, new Object[] { ucid, sblsh,cxmm }, this.getDataSourceName());
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
		}
		return ds;
	}
	
	/**
	 * 访问量统计
	 * @param pset
	 * @return
	 */
	public DataSet getPubHitsCount(ParameterSet pset) {
		String sql = "select count(1) COUNT from pub_hits t";
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
	
	/***
	 * 查询邮寄件物流信息（舟山市）
	 * @param pSet
	 * @return
	 */
	public DataSet queryLogisticsDetail(ParameterSet pSet){
		String mailNum = (String) pSet.getParameter("mailNum");
		String userId = (String) pSet.getParameter("userId");
		String mapId = GetMapid(userId);
		
		DataSet ds = new DataSet();
		try{
			String url = SecurityConfig.getString("ems_zs_url");
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", mapId);
			map.put("mailNum", mailNum);
			JSONObject obj = JSONObject.fromObject(RestUtil.postData(url));
			//url +="1194259000208";
			//JSONObject obj = JSONObject.fromObject("{\"traces\":[{\"acceptTime\":\"2011-11-24 17:55:00\",\"acceptAddress\":\"上海邮政速递物流长宁经营部\",\"remark\":\"收寄\"},{\"acceptTime\":\"2011-11-24 17:59:00\",\"acceptAddress\":\"上海邮政速递物流长宁经营部\",\"remark\":\"离开处理中心,发往上海市邮政公司邮政速递局\"},{\"acceptTime\":\"2011-11-24 23:54:38\",\"acceptAddress\":\"上海市\",\"remark\":\"到达处理中心,来自上海邮政速递物流长宁经营部\"},{\"acceptTime\":\"2011-11-25 00:17:42\",\"acceptAddress\":\"上海市\",\"remark\":\"离开处理中心,发往USSFOF\"},{\"acceptTime\":\"2011-12-05 07:41:00\",\"acceptAddress\":\"美国 94704\",\"remark\":\"到达投递局\"},{\"acceptTime\":\"2011-12-05 11:07:00\",\"acceptAddress\":\"美国 94703\",\"remark\":\"妥投\"}]}");
			
			String state = obj.getString("state");
			if("200".equals(state)){
				JSONObject content = obj.getJSONObject("content");
				JSONArray traces = content.getJSONArray("traces");
				ds.setState(StateType.SUCCESS);
				ds.setRawData(traces);
			}else{
				ds.setMessage("查询物流信息失败！");
				ds.setState(StateType.FAILT);
			}
		}catch(Exception e){
			_log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询物流信息失败！");
		}
		return ds;
	}
	
	public String GetMapid(String uid) {
		DataSet ds = new DataSet();
		String mapId = "";
		try {
			String sql = "select USER_ID_MAP from UC_USER_MAP t where user_id = ?";
			ds = this.executeDataset(sql, new Object[] { uid },"icityDataSource");
			
			if(ds.getTotal()>0){
				JSONObject o = ds.getRecord(0);
				mapId = o.getString("USER_ID_MAP");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapId;
	}
}
