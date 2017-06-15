package bj_jyb.app.icity.project;

import io.netty.util.CharsetUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.util.RestUtil;

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

	public ProjectIndexDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao)DaoFactory.getDao(ProjectIndexDao.class.getName());
	}

	@SuppressWarnings("deprecation")
	public DataSet getBusinessIndexList(ParameterSet pset) {

		DataSet ds;
		String searchTitle = (String) pset.remove("SEARCH_TITLE");
		String sql = "SELECT paystatus,paycontent,LIMIT_NUM,SBLSH,SXID,SXBM,SXMC,SJDW,SQRMC,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,STATE,URL,"
				+ " YSLSJ,BJSJ,(select count(0) from business_express e where e.sblsh=t.sblsh) as expressCount,l.QUALITY_STAR_LEVEL,l.STAR_LEVEL,l.TIME_STAR_LEVEL,l.MAJOR_STAR_LEVEL,l.id as starLevelId FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE where 1=1 ";
		if (StringUtil.isNotEmpty(searchTitle)) {
			sql += " AND (SBLSH like '%" + searchTitle + "%' OR SBXMMC like '%" + searchTitle + "%' OR SXMC like '%"
					+ searchTitle + "%' ) ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null, this.getDataSourceName());
		} else {
			ds = this.executeDataset(sql, start, limit, null, this.getDataSourceName());
		}
		return ds;
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
	public DataSet getStateTotal(ParameterSet pset) {
		  String sql = "SELECT A.STATE , COUNT(A.SBLSH) AS SUM FROM BUSINESS_INDEX A WHERE STATE IS NOT NULL ";
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
			String sql = "SELECT SBLSH, SXBM, SXMC, UCID, SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
					+ " SJDWDM, SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
					+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE FROM BUSINESS_INDEX B WHERE 1=1 ";
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
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
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
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
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
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
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
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
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
					String f = new String(ds.getData());
					JSONArray ja = JSONArray.fromObject(f);
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
			Map<String, String> map=new HashMap<String, String>();
			StringBuilder whereValue=new StringBuilder();
			JSONArray paramValue=new JSONArray();
			whereValue.append(" and ( company.org_name=? or person.name=? or person.apply_info=? or project.project_name=? ) ");
			paramValue.add(sqrmc);			
			paramValue.add(sqrmc);
			paramValue.add(sqrmc);
			paramValue.add(sqrmc);
			whereValue.append(" and receive_number=?");
			paramValue.add(sblsh);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue",paramValue.toString());
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, map));
			ds.setState(StateType.SUCCESS);
			ds.setRawData(obj);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	
	public DataSet BusinessProgressQueryBySblsh(ParameterSet pset) {
			
		DataSet ds = new DataSet();
		String sblsh = (String) pset.remove("sblsh");
		Object ret;
		try {
			HttpClientUtil client = new HttpClientUtil();
			String urlSp = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getBusinessInfo?receiveNumber="+sblsh);
			ret = client.getResult(urlSp,"");
			
				if("200".equals(JSONObject.fromObject(ret).getString("state"))){
				JSONObject obj;		
				obj =  JSONObject.fromObject(ret);
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage(JSONObject.fromObject(ret).getString("error"));
			}
								
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
	
	//王德龙  10.23 修改为调用接口
	public DataSet updateBusinessIndex(ParameterSet pSet){
		DataSet ds = new  DataSet();
		String sql = "UPDATE BUSINESS_INDEX SET STATE = '03' WHERE SBLSH = ?";
		try{
			String sblsh =(String)pSet.getParameter("SBLSH");
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/businessCancel?receiveNum="+sblsh);
			HttpClientUtil client = new HttpClientUtil();
			Object ret= client.getResult(url,"");
			if("200".equals(JSONObject.fromObject(ret).getString("state"))){
				int i = this.executeUpdate(sql, new Object[] {(String) pSet.getParameter("SBLSH")}, this.getDataSourceName());
				if (i == 0){
					ds.setState(StateType.FAILT);
					ds.setMessage("数据库操作失败！");
				} else {
					ds.setState(StateType.SUCCESS);
				}
			}
			else
			{
				ds.setState(StateType.FAILT);
				ds.setMessage("该业务已经受理，不允许撤销！");
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
			String sql = "select t.*,to_char(sbsj,'yyyy-MM-dd HH:mm:ss') mysbsj from business_index t where sblsh=?";
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
		DataSet ds = new DataSet();
		try{			
			String sql = "select webname name,count(1) count from pub_hits where " +
					"appid='"+SecurityConfig.getString("AppId")+"' and webname = visitedtitle and catalog ='0' " +
					" and webregion='"+SecurityConfig.getString("WebRegion")+"' group by webname order by count desc";
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
}
