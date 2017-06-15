package app.icity.engineering;

import java.net.URLDecoder;
import java.sql.Timestamp;
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
	 * 获取字典信息
	 * 
	 * @return
	 */
	public DataSet getDictInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getDictInfo");
		String dictCode = (String) pSet.getParameter("dictCode");
		url+="?dictCode=" + dictCode;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			System.out.println("获取字典信息:app.icity.engineering.EngineeringDao.getDictInfo");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}

	/**
	 * 投资获取阶段
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getStage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getStage");
		Map<String, String> data=new HashMap<String, String>();
		data.put("investType",
				(String) pSet.getParameter("investType"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			//String obj1 ="{'retCode':'200','errors':'error information','stage':[{'stageId':1,'stageName':'立项阶段'},{'stageId':2,'stageName':'用地审批'},{'stageId':3,'stageName':'规划报建'} ,{'stageId':4,'stageName':'施工许可'} ,{'stageId':5,'stageName':'竣工验收'}]}";
			//obj = JSONObject.fromObject(obj1);
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			System.out.println("投资获取阶段:app.icity.engineering.EngineeringDao.getStage");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 投资获取阶段流程
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getFlow(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getFlow");
		Map<String, String> data=new HashMap<String, String>();
		data.put("investType",
				(String) pSet.getParameter("investType"));
		data.put("stageId",
				(String) pSet.getParameter("stageId"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			System.out.println("投资获取阶段流程:app.icity.engineering.EngineeringDao.getFlow");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}

	/**
	 * 投资获取阶段流程 (聊城项目)
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getFlowByFlowType(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getFlowByFlowType");
		Map<String, String> data=new HashMap<String, String>();
		data.put("investType",
				(String) pSet.getParameter("investType"));
		data.put("stageId",
				(String) pSet.getParameter("stageId"));
		data.put("flowType",
				(String) pSet.getParameter("flowType"));
		data.put("regionCode",
				(String) pSet.getParameter("regionCode"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			//String obj1="{'retCode':'200','errors':'error information','flow':[{'flowId':'50d8d075-a11c-11e4-9259-005056996065','flowName':'立项阶段'},{'flowId':'50d8d075-a11c-11e4-9259-005056996066','flowName':'用地审批'},{'flowId':'50d8d075-a11c-11e4-9259-005056996067','flowName':'规划报建'} ,{'flowId':'50d8d075-a11c-11e4-9259-005056996068','flowName':'施工许可'} ,{'flowId':'50d8d075-a11c-11e4-9259-005056996069','flowName':'竣工验收'}]}";
			//obj = JSONObject.fromObject(obj1);
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			System.out.println("投资获取阶段流程 (聊城项目):app.icity.engineering.EngineeringDao.getFlowByFlowType");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 2.5	获取项目信息	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectDetails(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getProjectDetails");
		url+="?projectId="+ (String) pSet.getParameter("projectId");		
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if("200".equals(obj.getString("retCode"))){
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}			
		} catch (Exception e) {
			System.out.println("投资获取阶段流程 (聊城项目):app.icity.engineering.EngineeringDao.getFlowByFlowType");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 获取申报信息(表单、事项)
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getReport(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "getReport");
		Map<String, String> data=new HashMap<String, String>();
		data.put("flowId", (String) pSet.getParameter("flowId"));
		try {
			String s = RestUtil.postData(url, data);
			JSONObject obj;
			obj = JSONObject.fromObject(java.net.URLDecoder.decode(s,"utf-8"));
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			System.out.println("获取申报信息(表单、事项):app.icity.engineering.EngineeringDao.getReport");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}

	/**
	 * 工程建设--企业投资--提交项目资料
	 */
	public DataSet saveProject(ParameterSet pSet) {
		String sql = "insert into PARALLEL_PROJECT t (ID,PROJECT_NAME,GOVERNMENT_INVESTMET_ID,TOTAL_INVESTMENT," +
				"INVESTMENT_SOURCE,PROJECT_CATEGORY,REGION,CONSTRUCTION_UNIT,REGISTERED_CAPITAL,UNIT_PHONE,UNIT_CODE," +
				"LEGAL_MAN,CONTACT_MAN,CONTACT_MAN_ID,CONTACTS,CREATE_TIME,LAND_AREA,PROJECT_ADDRESS,UCID, " +
				"projectType,mainProjectCode,constructPer,industry,industryName,startYear,endYear,projectContent," +
				"permitIndustry,isDeArea,applyDate,deAreaName,permitItemCode,placeCode,projectStage,lerepCerttype," +
				"lerepNo,contactEmail) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		JSONObject project = JSONObject.fromObject(pSet.get("project"));
		try {
			String ID = Tools.getUUID32();
			int i = this.executeUpdate(
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
									project.get("contactEmail")
								});
			if (i > 0) {
				JSONObject passpro = new JSONObject();
				passpro.put("project", project);
				passpro.put("unit", project);
				// 以下为调用接口
				String url = HttpUtil.formatUrl(SecurityConfig.getString("ECParallelUrl") + "submitPorjectDetails");
				Map<String, String> data=new HashMap<String, String>();
				data.put("project", pSet.getParameter("project").toString());
				data.put("unit", pSet.getParameter("project").toString());
				try {
					JSONObject receive;
					receive = JSONObject.fromObject(RestUtil.postData(url, data));
					if (receive.get("retCode").equals("200")) {
						String projectId = (String) receive.get("projectId");
						try {
							String sqla = "update PARALLEL_PROJECT t set t.PROJECT_ID =? where t.ID =?";
							this.executeUpdate(sqla, new Object[] { projectId,ID });
							ds.setState(StateType.SUCCESS);
							ds.setData(projectId.getBytes());
						} catch (Exception e) {
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
	// 获取工程建设项目列表----project表
	public DataSet getProject(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ucid = pSet.remove("ucid").toString();
			String sql = "select t.*,t.CREATE_TIME CREATE_TIME_CHAR from PARALLEL_PROJECT t where t.ucid = '"+ ucid + "'";
			
			//处理时间查询条件
			String CREATE_TIME_s = (String) pSet.remove("t.CREATE_TIME@>@Date");
			String CREATE_TIME_e = (String) pSet.remove("t.CREATE_TIME@<@Date");
			List<Object> param=new ArrayList<Object>();
			Timestamp d =null;
			if(StringUtil.isNotEmpty(CREATE_TIME_s)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(CREATE_TIME_s,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.CREATE_TIME >= ?";
			}
			if(StringUtil.isNotEmpty(CREATE_TIME_e)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(CREATE_TIME_e,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.CREATE_TIME <= ?";
			}
			
			int limit = pSet.getPageLimit();
			int start = pSet.getPageStart();
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			if(limit==-1||start==-1){
				ds = this.executeDataset(sql,param.toArray());
			}else{
				ds = this.executeDataset(sql, start, limit,param.toArray());
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}
	/**
	 * 删除项目
	 * @param pSet
	 * @return
	 */
	public DataSet delProject(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "delete from PARALLEL_PROJECT";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("id@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
				int i = this.executeUpdate(sql);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("请选择您需要删除的内容！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据删除失败...");
		}
		return ds;
	}
	// 获取工程建设项目列表----base表
	public DataSet getEnginProject(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ucid = (String) pSet.getParameter("ucid");
			String sql = "select PROJECT_ID,PROJECT_NAME from PARALLEL_BIZ_BASE t where t.UCID = '"
					+ ucid + "' group by (PROJECT_ID,PROJECT_NAME)";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	public DataSet getEnginProjecta(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String jgm = (String) pSet.getParameter("jgm");
			String sql = "select PROJECT_ID,PROJECT_NAME from PARALLEL_BIZ_BASE t where t.FIRST_CODE = ? group by (PROJECT_ID,PROJECT_NAME)";
			ds = this.executeDataset(sql,new Object[]{jgm});
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	// 获取工程建设业务列表----base表
	public DataSet getEnginBase(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String PROJECT_ID = (String) pSet.getParameter("PROJECT_ID");
			String STAGE_ID = (String) pSet.getParameter("STAGE_ID");
			String sql = "select to_char(t.APPLY_TIME,'YYYY-MM-dd') as APPLY_TIME,"
					+ "to_char(t.ACCEPT_TIME,'YYYY-MM-dd') as ACCEPT_TIME,"
					+ "to_char(t.ACTUAL_FINISH_TIME,'YYYY-MM-dd') as ACTUAL_FINISH_TIME,"
					+ "t.BIZ_ID,t.APPLY_SUBJECT,t.APPLY_SERIAL_NO,t.PROJECT_ID,t.PROJECT_BIZ_ID,"
					+ "t.STAGE_ID,t.BIZ_STATE,t.PROJECT_NAME,t.FORM_ID,t.STAGE_NAME,t.FLOW_ID,t.FLOW_NAME,"
					+ "t.DATA_ID from PARALLEL_BIZ_BASE t where t.PROJECT_ID = ? and t.STAGE_ID = ? order by BIZ_ID";
			ds = this.executeDataset(sql, new Object[] { PROJECT_ID, STAGE_ID });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 工程建设 提交申报信息到接口
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitReportDetails(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "submitReportDetails");
		Map<String, String> data=new HashMap<String, String>();
		data.put("dataId", (String) pSet.get("dataId"));
		data.put("formId", (String) pSet.get("formId"));
		data.put("flowId", (String) pSet.get("flowId"));
		data.put("projectId", (String) pSet.get("projectId"));
		data.put("resource", pSet.get("resource").toString());
		JSONObject receive;
		try {
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			if (receive.get("retCode").equals("200")) {
				pSet.setParameter("bizId", receive.get("bizId"));
				pSet.setParameter("STEP", "3"); // step=3标识是由提交触发的savePararallil_biz_base
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
			System.out.print("工程建设 提交申报信息到接口:app.icity.engineering.EngineeringDao.submitReportDetails");
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败");
		}
		return ds;
	}
	/**
	 * 提交信息到库 pan 
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
					sql = "update parallel_biz_base set DATA_ID=?,APPLY_TIME=? where BIZ_ID=?";
					i = this.executeUpdate(
							sql,
							new Object[] { pSet.get("dataId"), // 表单数据id
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),// 申请时间
									ID });
				} else if ("2".equals(pSet.get("STEP"))) {
					// 2是上传页面暂存
					sql = "update parallel_biz_base set DATA_ID=?,APPLY_DATA=?,APPLY_TIME=? where BIZ_ID=?";
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
									ID });
				} else if ("3".equals(pSet.get("STEP"))) {
					// 3是提交：提交得把bizId换成接口返回的[业务标识]，更新dataid，APPLY_DATA等值。
					if (ID.length() == 32) {// 说明是已经提交的，再次提交时将不更新审请时间
						sql = "update parallel_biz_base set DATA_ID=?,APPLY_DATA=?,BIZ_ID=? where BIZ_ID=?";
						i = this.executeUpdate(
								sql,
								new Object[] {
										pSet.get("dataId"), // 表单数据id
										JSONObject.fromObject(pSet.get("resource")).toString(),
										pSet.get("bizId"), ID });
					} else if (ID.length() == 34) { // 说明是暂存时的记录，则更新申请时间
						sql = "update parallel_biz_base set DATA_ID=?,APPLY_DATA=?,BIZ_ID=?,APPLY_TIME=? where BIZ_ID=?";
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
										ID });
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
			if ("3".equals(pSet.get("STEP")))
				ZCUUID = (String) pSet.get("bizId");
			sql = "insert into parallel_biz_base t (BIZ_ID,APPLY_SUBJECT,APPLY_SERIAL_NO,PROJECT_ID,PROJECT_BIZ_ID,ENTERPRISES_ID,ENTERPRISES_NAME,APPLY_TIME,ACCEPT_TIME,"
					+ "ACTUAL_FINISH_TIME,LEADER_ORG_CODE,LEADER_ORG_NAME,FLOW_ID,FLOW_NAME,STAGE_ID,"
					+ "STAGE_NAME,FORM_ID,DATA_ID,BIZ_STATE,REGION_CODE,FIRST_CODE,APPLY_DATA,UCID,PROJECT_NAME"
					+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
								resource.toString(),
								pSet.get("uid"), pSet.get("projectName") });
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

	// 获取上传附件存于数据库中的信息。
	public DataSet getResourceList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = "select APPLY_DATA from PARALLEL_BIZ_BASE";
		try {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}

	/**
	 * 获取项目信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectInfoById(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String PROJECT_ID = (String) pSet.getParameter("PROJECT_ID");
			String sql = "select * from PARALLEL_PROJECT t where t.PROJECT_ID = ?";
			ds = this.executeDataset(sql, new Object[] { PROJECT_ID });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return ds;
	}

	/**
	 * 获取暂存信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getStagingInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String PROJECT_ID = (String) pSet.getParameter("projectId");
			String ucid = (String) pSet.getParameter("uid");
			String sql = "select * from parallel_biz_base t where project_id = ? and instr(biz_id,'ZC')>0 and length(biz_id)=34 and ucid=?";
			ds = this.executeDataset(sql, new Object[] { PROJECT_ID, ucid });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取暂存信息...");
		}
		return ds;
	}
	/**
	 * 获取投资项目业务列表
	 * @param pSet
	 * @return
	 */
	public DataSet getEnginBaseList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ucid = (String)pSet.remove("uid");
			String sql = "select APPLY_TIME,"
					+ "ACCEPT_TIME,"
					+ "ACTUAL_FINISH_TIME,"
					+ "t.BIZ_ID,t.APPLY_SUBJECT,t.APPLY_SERIAL_NO,t.PROJECT_ID,t.PROJECT_BIZ_ID,"
					+ "t.STAGE_ID,t.BIZ_STATE,t.PROJECT_NAME,t.FORM_ID,t.STAGE_NAME,t.FLOW_ID,t.FLOW_NAME,t.STATE,"
					+ "t.DATA_ID,t.apply_data,t.ENTERPRISES_NAME from PARALLEL_BIZ_BASE t where ucid=? ";
			
			//处理时间查询条件
			String apply_time_s = (String) pSet.remove("t.apply_time@>@Date");
			String apply_time_e = (String) pSet.remove("t.apply_time@<@Date");
			List<Object> param=new ArrayList<Object>();
			param.add(ucid);
			Timestamp d =null;
			if(StringUtil.isNotEmpty(apply_time_s)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(apply_time_s,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.apply_time >= ?";
			}
			if(StringUtil.isNotEmpty(apply_time_e)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(apply_time_e,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.apply_time <= ?";
			}
			
			int limit = pSet.getPageLimit();
			int start = pSet.getPageStart();
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			sql+=" order by t.APPLY_TIME desc";
			if(limit==-1||start==-1){
				ds = this.executeDataset(sql,param.toArray());
			}else{
				ds = this.executeDataset(sql, start, limit,param.toArray());
			}
			//ds.setRawData(ds.getJAData());
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}
}
