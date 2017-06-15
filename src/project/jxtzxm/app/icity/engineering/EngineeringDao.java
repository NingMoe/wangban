package jxtzxm.app.icity.engineering;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * 工程建设 lhy
 * 
 * @author lenovo
 * 
 */
public class EngineeringDao extends BaseJdbcDao {

	private EngineeringDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static EngineeringDao getInstance() {
		return DaoFactory.getDao(EngineeringDao.class.getName());
	}

	public DataSet getList(ParameterSet pSet) {
		DataSet ds = null;
		return ds;
	}

	/**
	 * 江西省投资项目监管平台（并联审批新建项目）
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet saveProject(ParameterSet pSet) {
		String sql = "insert into PARALLEL_PROJECT t (ID,PROJECT_NAME,GOVERNMENT_INVESTMET_ID,TOTAL_INVESTMENT,"
				+ "INVESTMENT_SOURCE,PROJECT_CATEGORY,REGION,CONSTRUCTION_UNIT,REGISTERED_CAPITAL,UNIT_PHONE,UNIT_CODE,"
				+ "LEGAL_MAN,CONTACT_MAN,CONTACT_MAN_ID,CONTACTS,CREATE_TIME,LAND_AREA,PROJECT_ADDRESS,UCID, "
				+ "projectType,mainProjectCode,constructPer,industry,industryName,startYear,endYear,projectContent,"
				+ "permitIndustry,isDeArea,applyDate,deAreaName,permitItemCode,placeCode,projectStage,lerepCerttype,"
				+ "lerepNo,contactEmail) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		JSONObject project = JSONObject.fromObject(pSet.get("project"));
		try {
			String ID = Tools.getUUID32();
			int i = this
					.executeUpdate(
							sql,
							new Object[] {
									ID,
									project.get("projectName"),
									project.get("investmentId"),
									project.get("totalInvestment"),
									project.get("investmentSource"),
									project.get("projectCategory"),
									project.get("region"),
									project.get("constructionUnit"),
									project.get("registeredCapital"),
									project.get("unitPhone"),
									project.get("unitCode"),
									project.get("legalMan"),
									project.get("contactMan"),
									project.get("contactManId"),
									project.get("contacts"),
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
									project.get("langArea"),
									project.get("projectaddress"),
									project.get("UCID"),
									project.get("projectType"),
									project.get("mainProjectCode"),
									project.get("constructPer"),
									project.get("industry"),
									project.get("industryName"),
									project.get("startYear"),
									project.get("endYear"),
									project.get("projectContent"),
									project.get("permitIndustry"),
									project.get("isDeArea"),
									project.get("applyDate"),
									project.get("deAreaName"),
									project.get("permitItemCode"),
									project.get("placeCode"),
									project.get("projectStage"),
									project.get("lerepCerttype"),
									project.get("lerepNo"),
									project.get("contactEmail") });
			if (i > 0) {
				JSONObject passpro = new JSONObject();
				passpro.put("project", project);
				passpro.put("unit", project);
				// 以下为调用接口
				String url = HttpUtil.formatUrl(SecurityConfig
						.getString("ECParallelUrl") + "submitPorjectDetails");
				Map<String, String> data = new HashMap<String, String>();
				data.put("unit", pSet.getParameter("project").toString());
				JSONObject obj = JSONObject.fromObject(pSet
						.getParameter("project"));
				JSONObject objs = JSONObject.fromObject(pSet
						.getParameter("project"));
				objs.accumulate("project", obj);
				objs.accumulate("unit", obj);
				data.put("project", objs.toString());
				try {
					JSONObject receive;
					Object ret = RestUtil.postData(url, data);

					receive = JSONObject.fromObject(URLDecoder.decode(
							ret.toString(), "utf-8"));
					if ("SUCCESS".equals(receive.get("retCode"))) {
						String projectId = (String) receive.get("project_id");
						String formid = (String) receive.get("formId");
						try {
							String sqla = "update PARALLEL_PROJECT t set t.PROJECT_ID =?,formid=? where t.ID =?";
							this.executeUpdate(sqla, new Object[] { projectId,
									formid, ID });
							ds.setState(StateType.SUCCESS);
							ds.setData(projectId.getBytes());
						} catch (Exception e) {
							e.printStackTrace();
							ds.setState(StateType.FAILT);
							ds.setMessage("更新PROJECT_ID失败");
						}
					} else {
						ds.setState(StateType.FAILT);
						ds.setMessage("接口端写入失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("写入数据库失败！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("操作失败！");
		}
		return ds;
	}

	/**
	 * 工程建设 提交申报信息到接口 江西
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitReportDetails(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "submitReportDetails");
		Map<String, String> data = new HashMap<String, String>();
		JSONObject ob = new JSONObject();
		ob.put("dataId", (String) pSet.get("dataId"));
		ob.put("flowId", (String) pSet.get("flowId"));
		ob.put("projectId", (String) pSet.get("projectId"));
		ob.put("formId", (String) pSet.get("formId"));
		ob.put("resource", pSet.get("resource").toString());
		data.put("formInfoJson", ob.toString());
		JSONObject receive;
		try {
			Object obj = RestUtil.postData(url, data);
			receive = JSONObject.fromObject(URLDecoder.decode((String) obj,
					"UTF-8"));
			if ("SUCCESS".equals(receive.get("retCode"))) {
				pSet.setParameter("bizId", receive.get("bizId"));
				pSet.setParameter("STEP", "3"); // step=3，标识是由提交触发的savePararallil_biz_base
				DataSet _ds = savePararallil_biz_base(pSet);
				if (_ds.getState() == 1) {
					ds.setState(StateType.SUCCESS);
					ds.setMessage("提交成功");
				} else {
					ds.setState(StateType.SUCCESS);
					ds.setMessage("提交成功，写入数据库失败");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage((String) receive.get("error"));
			}
		} catch (Exception e) {
			System.out
					.print("工程建设 提交申报信息到接口:app.icity.engineering.EngineeringDao.submitReportDetailsJX");
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败");
		}
		return ds;
	}

	/**
	 * 提交信息到库 pan
	 * 
	 * @param pSet
	 * @return Modify： by kongweisong
	 */
	public DataSet savePararallil_biz_base(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = null;
		String ID = (String) pSet.get("ID"); // 用于将主键带到前台或从前台带回来。
		JSONArray resource = JSONArray.fromObject(pSet.get("resource"));
		int i = 0; // 记录操作数据库受影响行数
		if (ID != null && !ID.equals("no")) { // 如果不为no，证明数据已经暂存过,则执行更新即可。
			try {
				if ("1".equals(pSet.get("STEP"))) {
					sql = "update parallel_biz_base set DATA_ID=?,APPLY_TIME=?,STATE=? where BIZ_ID=?";
					i = this.executeUpdate(
							sql,
							new Object[] { pSet.get("dataId"), // 表单数据id
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),// 申请时间
									"0", ID });
				} else if ("2".equals(pSet.get("STEP"))) {
					// 2是上传页面暂存
					sql = "update parallel_biz_base set DATA_ID=?,APPLY_DATA=?,APPLY_TIME=?,STATE=? where BIZ_ID=?";
					i = this.executeUpdate(
							sql,
							new Object[] {
									pSet.get("dataId"), // 表单数据id
									resource.toString(),
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),// 申请时间
									"0", ID });
				} else if ("3".equals(pSet.get("STEP"))) {
					// 3是提交：提交得把bizId换成接口返回的[业务标识]，更新dataid，APPLY_DATA等值。
					if (ID.length() == 32) {// 说明是已经提交的，再次提交时将不更新审请时间
						sql = "update parallel_biz_base set DATA_ID=?,APPLY_DATA=?,BIZ_ID=?,STATE=? where BIZ_ID=?";
						i = this.executeUpdate(
								sql,
								new Object[] {
										pSet.get("dataId"), // 表单数据id
										JSONObject.fromObject(
												pSet.get("resource"))
												.toString(), pSet.get("bizId"),
										"1", ID });
					} else if (ID.length() == 34) { // 说明是暂存时的记录，则更新申请时间
						sql = "update parallel_biz_base set DATA_ID=?,APPLY_DATA=?,BIZ_ID=?,APPLY_TIME=?,STATE=? where BIZ_ID=?";
						i = this.executeUpdate(
								sql,
								new Object[] {
										pSet.get("dataId"), // 表单数据id
										resource.toString(),
										pSet.get("bizId"),
										CommonUtils_api
												.getInstance()
												.parseDateToTimeStamp(
														new Date(),
														CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),// 申请时间
										"1", ID });
					}
				}
				if (i > 0) {
					ds.setState(StateType.SUCCESS);
					ds.setData(ID.getBytes());
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("事项信息写入数据库失败");
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage("");
			}

		} else { // 否则证明是首次插入值
			String ZCUUID = "ZC" + Tools.getUUID32(); // 暂存时bizId为自动生成码。
			String state = "0";
			if ("3".equals(pSet.get("STEP"))) {
				ZCUUID = (String) pSet.get("bizId");
				state = "1";
			}
			sql = "insert into parallel_biz_base t (BIZ_ID,APPLY_SUBJECT,APPLY_SERIAL_NO,PROJECT_ID,PROJECT_BIZ_ID,ENTERPRISES_ID,ENTERPRISES_NAME,APPLY_TIME,ACCEPT_TIME,"
					+ "ACTUAL_FINISH_TIME,LEADER_ORG_CODE,LEADER_ORG_NAME,FLOW_ID,FLOW_NAME,STAGE_ID,"
					+ "STAGE_NAME,FORM_ID,DATA_ID,BIZ_STATE,REGION_CODE,FIRST_CODE,APPLY_DATA,UCID,PROJECT_NAME,STATE"
					+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ds = new DataSet();
			try {
				i = this.executeUpdate(
						sql,
						new Object[] {
								ZCUUID,
								pSet.get("APPLY_SUBJECT"),
								null,
								pSet.get("projectId"),
								null,
								null,
								pSet.get("uname"),
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),// 申请时间
								"", "", null, null, pSet.get("flowId"),
								pSet.get("flowName"), pSet.get("stageId"),
								pSet.get("stageName"), pSet.get("formId"),
								pSet.get("dataId"), "",
								SecurityConfig.getString("WebRegion"), "",
								resource.toString(), pSet.get("uid"),
								pSet.get("projectName"), state });
				if (i > 0) {
					ds.setState(StateType.SUCCESS);
					ds.setData(ZCUUID.getBytes());
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("事项信息写入数据库失败");
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage("事项信息写入数据库失败");
			}
		}
		return ds;
	}

	public DataSet getEnginBaseInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ucid = pSet.remove("uid").toString();
			String BIZ_ID = pSet.remove("BIZ_ID").toString();
			String sql = "select to_char(t.APPLY_TIME,'YYYY-MM-dd') as APPLY_TIME,"
					+ "to_char(t.ACCEPT_TIME,'YYYY-MM-dd') as ACCEPT_TIME,"
					+ "to_char(t.ACTUAL_FINISH_TIME,'YYYY-MM-dd') as ACTUAL_FINISH_TIME,"
					+ "t.BIZ_ID,t.APPLY_SUBJECT,t.APPLY_SERIAL_NO,t.PROJECT_ID,t.PROJECT_BIZ_ID,"
					+ "t.STAGE_ID,t.BIZ_STATE,t.PROJECT_NAME,t.FORM_ID,t.STAGE_NAME,t.FLOW_ID,t.FLOW_NAME,t.STATE,"
					+ "t.DATA_ID,t.apply_data,t.ENTERPRISES_NAME from PARALLEL_BIZ_BASE t where ucid=? and BIZ_ID=?";
			sql += " order by t.APPLY_TIME desc";
			ds = this.executeDataset(sql, new Object[] { ucid, BIZ_ID });
			ds.setRawData(ds.getJAData());
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 咨询信息推送接口 *
	 * 
	 * @param pSet
	 *            ID VARCHAR2(50) Y consultId业务咨询ID，咨询业务受理的唯一标示 CONSULTFLOW
	 *            VARCHAR2(50) Y 咨询的流程ID CONSULTSUBJECT VARCHAR2(100) Y 咨询主题
	 *            FORMINFOJSON CLOB Y 提交的表单信息 ITEMINFOJSON CLOB Y 反馈或者请求返回的结果集
	 *            SUBMITDATE DATE Y 提交时间 UPDATEDATE DATE Y 更新时间 REMARK
	 *            VARCHAR2(100) Y 备注 STATUS VARCHAR2(10) Y 状态 1已提交 2已回复
	 * @return
	 */
	public DataSet consultmsg(ParameterSet pSet) {
		String sql = "insert into PARALLEL_CONSULTMSG t (ID,CONSULTFLOW,CONSULTSUBJECT,FORMINFOJSON,ITEMINFOJSON,SUBMITDATE,REMARK,STATUS,ucid) "
				+ " values (?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		JSONObject data_info = JSONObject.fromObject(pSet.get("data_info"));
		String consultFlow = (String) pSet.get("consultFlow");
		String consultSubject = (String) pSet.get("consultSubject");
		String uid = (String) pSet.get("uid");
		Timestamp d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTime = sdf.format(new Date());
			d = CommonUtils_api.getInstance().parseStringToTimestamp(startTime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String ID = Tools.getUUID32();
			JSONObject formInfoJson = new JSONObject();
			formInfoJson.put("DATA_INFO", data_info);
			int i = this.executeUpdate(sql, new Object[] { ID, consultFlow,
					consultSubject, formInfoJson.getString("DATA_INFO"), "", d, "", "0",
					uid });
			if (i > 0) {
				// 以下为调用接口
				String url = HttpUtil.formatUrl(SecurityConfig
						.getString("ECParallelUrl") + "consultmsg");
				url = url.replace("/main/parallel/", "/web/parallel/");
				Map<String, String> data = new HashMap<String, String>();
				data.put("consultId", ID);
				data.put("consultFlow", consultFlow);
				data.put("consultSubject", consultSubject);
				data.put("formInfoJson", formInfoJson.toString());
				try {
					JSONObject receive;
					Object ret = RestUtil.postData(url, data);
					receive = JSONObject.fromObject(URLDecoder.decode(
							ret.toString(), "utf-8"));
					if ("200".equals(receive.get("state"))) {
						try {
							sql = "update PARALLEL_CONSULTMSG t set t.STATUS = '1' where t.ID=?";
							int j = this
									.executeUpdate(sql, new Object[] { ID });
							if (j > 0) {
								ds.setState(StateType.SUCCESS);
							} else {
								ds.setState(StateType.FAILT);
								ds.setMessage("本地状态更新失败");
							}
						} catch (Exception e) {
							e.printStackTrace();
							ds.setState(StateType.FAILT);
							ds.setMessage("本地状态更新失败");
						}
					} else {
						ds.setState(StateType.FAILT);
						ds.setMessage("接口端写入失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("写入数据库失败！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("操作失败！");
		}
		return ds;
	}

	/**
	 * 获取咨询列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getParallelConsultlist(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try{
			int start = (Integer)pSet.getPageStart();
			int limit = (Integer)pSet.getPageLimit();
			String sql = "select ID,CONSULTFLOW,CONSULTSUBJECT,FORMINFOJSON,ITEMINFOJSON,"+
					"SUBMITDATE,UPDATEDATE,REMARK,STATUS,UCID from PARALLEL_CONSULTMSG t where 1=1 ";
			//处理时间查询条件
			String apply_time_s = (String) pSet.remove("t.SUBMITDATE@>@Date");
			String apply_time_e = (String) pSet.remove("t.SUBMITDATE@<@Date");
			List<Object> param=new ArrayList<Object>();
			Timestamp d =null;
			if(StringUtil.isNotEmpty(apply_time_s)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(apply_time_s,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.SUBMITDATE >= ?";
			}
			if(StringUtil.isNotEmpty(apply_time_e)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(apply_time_e,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.SUBMITDATE <= ?";
			}		
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			sql+=" order by SUBMITDATE desc";
			if(limit==-1||start==-1){
				ds = this.executeDataset(sql,param.toArray());
			}else{
				ds = this.executeDataset(sql, start, limit,param.toArray());
			}
			JSONArray result=new JSONArray();
			JSONObject jo_result;
			if(ds.getState()==StateType.SUCCESS&&ds.getTotal()>0){
				int len = ds.getTotal();
				for(int i=0;i<len;i++){
					if("1".equals(ds.getJAData().getJSONObject(i).getString("STATUS"))){
						JSONObject m_jo_result = getConsultResult(ds.getJAData().getJSONObject(i).getString("ID"));
						if("200".equals(m_jo_result.getString("state"))){
							jo_result = ds.getJAData().getJSONObject(i);
							jo_result.put("ITEMINFOJSON", m_jo_result.getJSONArray("DATA_INFO"));
							result.add(jo_result);
						}
					}else{
						result.add(ds.getJAData().getJSONObject(i));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}		
		return ds;
	}
	/**
	 * 咨询结果查询
	 * 参数consultId=业务咨询ID，咨询业务受理的唯一标示
	 */
	public JSONObject getConsultResult(String consultId){
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getConsultResult");
		url = url.replace("/main/parallel/", "/web/parallel/");
		Map<String, String> data = new HashMap<String, String>();
		data.put("consultId", consultId);
		
		JSONObject returnResult = new JSONObject();
		try {
			JSONObject receive;
			Object ret = RestUtil.postData(url, data);
			receive = JSONObject.fromObject(URLDecoder.decode(
					ret.toString(), "utf-8"));
			if ("200".equals(receive.get("state"))) {
				JSONArray DATA_INFO = receive.getJSONArray("DATA_INFO");
				if(DATA_INFO.size()>0){
					String sql = "update PARALLEL_CONSULTMSG t set t.STATUS = '2',t.ITEMINFOJSON=? where t.ID=?";
					this.executeUpdate(sql, new Object[] { DATA_INFO.toString(),consultId });					
				}
				returnResult.put("DATA_INFO",DATA_INFO);
				returnResult.put("state", "200");
			} else {
				returnResult.put("state", "300");				
				returnResult.put("error", receive.getString("error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnResult.put("state", "300");
			returnResult.put("error", e.toString());
		}
		return returnResult;
	}
}
