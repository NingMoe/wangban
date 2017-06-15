package weihai.app.icity.engineering;

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
	/**
	 * 2.17	根据区划和阶段获取流程
	 * @param pSet
	 * @return
	 */
	public DataSet getFlowByStageAndRegion(ParameterSet pSet){
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "/getFlowByStageAndRegion");
		String stageId = (String)pSet.get("stageId");
		String regionCode = (String)pSet.get("regionCode");
		url+="?stageId="+stageId+"&regionCode="+regionCode;
		try {
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			System.out.println(obj);
			//String obj1 ="{\"retCode \":\"200\",\"error\":\"当state为300时，提示错误信息\",\"flow\":[{\"flowId\":\"50d8d075-a11c-11e4-9259-005056996065\",\"flowName\":\"立项阶段（政府投资-非储备类用地）\",\"itemArray\":[{\"itemId\":\"775387A3FF2E41C892EC3111752F4708\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"},{\"itemId\":\"2305D3D2037D4E488FA374BAA04D27B5\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"}]},{\"flowId\":\"50d8d075-a11c-11e4-9259-005056996062\",\"flowName\":\"立项阶段（政府投资-非储备类用地）\",\"itemArray\":[{\"itemId\":\"E5C5759FE545460BB071C641A6A9E36C\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"},{\"itemId\":\"2305D3D2037D4E488FA374BAA04D27B5\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"}]}]}";
			//JSONObject obj = JSONObject.fromObject(obj1);
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("根据区划和阶段获取流程:getFlowByStageAndRegion");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 2.16	获取办事指南信息
	 * @return
	 */
	public DataSet getFlowService(ParameterSet pSet){
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "/getFlowService");
		String flowId = (String)pSet.get("flowId");		
		url+="?flowId="+flowId;
		try {
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			System.out.println(obj);
			//String obj1 ="{\"retCode\":\"200\",\"error\":\"当state为300时，提示错误信息\",\"info\":{\"acceptWindow\":\"受理窗口；\",\"cooperateOrgName\":\"受理单位\",\"limitDays\":\"办理时限\",\"isFare\":\"是否收费(1：是0：否)\",\"investmentSource\":\"投资来源\",\"consultationPhone\":\"咨询电话\",\"complaintPhone\":\"投诉电话\",\"isOnlineHandle\":\"是否线上办理(1：是0：否)\",\"handleTimePlace\":\"办理时间、地点\",\"handleFlow\":\"办理流程\",\"detailedGuide\":\"详细指导\",\"acceptanceCondition\":\"受理条件\",\"acceptanceBasis\":[{\"name\":\"名称\",\"number\":\"1文号\",\"money\":\"1款项\",\"content\":\"1内容\"}],\"handleFlowId\":\"办理流程（网盘ID）\",\"detailedGuideId\":\"详细指导（网盘ID\",\"cooperateOrgCode\":\"受理单位（代码）\",\"itemArray\":[{\"itemId\":\"2305D3D2037D4E488FA374BAA04D27B5\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"},{\"itemId\":\"775387A3FF2E41C892EC3111752F4708\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"}]}}{\"retCode\":\"200\",\"error\":\"当state为300时，提示错误信息\",\"info\":{\"acceptWindow\":\"受理窗口；\",\"cooperateOrgName\":\"受理单位\",\"limitDays\":\"办理时限\",\"isFare\":\"是否收费(1：是0：否)\",\"investmentSource\":\"投资来源\",\"consultationPhone\":\"咨询电话\",\"complaintPhone\":\"投诉电话\",\"isOnlineHandle\":\"是否线上办理(1：是0：否)\",\"handleTimePlace\":\"办理时间、地点\",\"handleFlow\":\"办理流程\",\"detailedGuide\":\"详细指导\",\"acceptanceCondition\":\"受理条件\",\"acceptanceBasis\":[{\"name\":\"名称\",\"number\":\"1文号\",\"money\":\"1款项\",\"content\":\"1内容\"}],\"handleFlowId\":\"办理流程（网盘ID）\",\"detailedGuideId\":\"详细指导（网盘ID\",\"cooperateOrgCode\":\"受理单位（代码）\",\"itemArray\":[{\"itemId\":\"2305D3D2037D4E488FA374BAA04D27B5\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"},{\"itemId\":\"775387A3FF2E41C892EC3111752F4708\",\"itemCode\":\"事项编码\",\"itemName\":\"事项名称\"}]}}";
			//JSONObject obj = JSONObject.fromObject(obj1);
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("获取办事指南信息:getFlowService");
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
				.getString("ECParallelUrl") + "/getProjectDetails");
		url+="?projectId="+ (String) pSet.getParameter("projectId");		
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			System.out.println(obj);
			if("200".equals(obj.getString("retCode"))){
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("投资获取阶段流程:app.icity.engineering.EngineeringDao.getProjectDetails");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 2.18	提交申报信息（威海办事事项单独传一下）
	 * @param pSet
	 * @return
	 */
	public DataSet submitReportDetailsWeiHai(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "/submitReportDetailsWeiHai");
		JSONObject submit_data = JSONObject.fromObject(pSet.get("submit_data"));		
		Map<String, String> data=new HashMap<String, String>();
		data.put("dataId", submit_data.getString("dataId"));
		data.put("flowId", submit_data.getString("flowId"));
		data.put("projectId", submit_data.getString("projectId"));
		data.put("itemArray", submit_data.getJSONArray("itemArray").toString());
		data.put("resource", submit_data.getJSONArray("resource").toString());
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			System.out.println(obj);
			if("200".equals(obj.getString("retCode"))){
				pSet.put("state", "1");//提交
				pSet.put("bizId", obj.getString("bizId"));//
				savePararallil_biz_base(pSet);//保存或者更新库
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				pSet.put("state", "0");//暂存
				savePararallil_biz_base(pSet);//保存或者更新库
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("提交申报信息（威海办事事项单独传一下）submitReportDetailsWeiHai");
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 提交信息到库  
	 * @param pSet
	 * @return Modify：
	 */
	public DataSet savePararallil_biz_base(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = "";
		String biz_id = pSet.containsKey("biz_id")?(String) pSet.get("biz_id"):Tools.getUUID32();//临时id
		String bizId = pSet.containsKey("bizId")?(String) pSet.get("bizId"):biz_id;//要插入的id
		String state = pSet.containsKey("state")?(String) pSet.get("state"):"0";//0暂存1提交
		JSONObject submit_data = JSONObject.fromObject(pSet.get("submit_data"));
		int i = 0; // 记录操作数据库受影响行数
		try {
			if(pSet.containsKey("biz_id")){//更新
				sql = "update parallel_biz_base t set t.BIZ_ID=?,t.APPLY_SUBJECT=?,PROJECT_ID=?,t.ENTERPRISES_NAME=?," +
						"t.LEADER_ORG_CODE=?,t.LEADER_ORG_NAME=?,t.FLOW_ID=?,t.FLOW_NAME=?,t.FORM_ID=?,t.DATA_ID=?," +
						"t.REGION_CODE=?,t.APPLY_DATA=?,t.UCID=?,t.PROJECT_NAME=?,t.STATE=?,t.APPLY_TIME=? where t.biz_id=?";
				i = this.executeUpdate(sql,new Object[] { 
						bizId,//业务id
						submit_data.getString("constructionUnit")+submit_data.getString("flowName"),
						submit_data.getString("projectId"),
						submit_data.getString("constructionUnit"),
						submit_data.getString("leaderOrgCode"),
						submit_data.getString("leaderOrgName"),
						submit_data.getString("flowId"),
						submit_data.getString("flowName"),
						submit_data.getString("formId"),
						submit_data.getString("dataId"),
						submit_data.getString("region_code"),
						submit_data.toString(),
						(String) pSet.get("uid"),
						submit_data.getString("projectName"),
						state,						
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
						biz_id
					});
			}else{//插入
				sql = "insert into parallel_biz_base t (BIZ_ID,APPLY_SUBJECT,PROJECT_ID,ENTERPRISES_NAME," +
				"LEADER_ORG_CODE,LEADER_ORG_NAME,FLOW_ID,FLOW_NAME,FORM_ID,DATA_ID,REGION_CODE,APPLY_DATA,UCID,PROJECT_NAME,STATE,APPLY_TIME)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				i = this.executeUpdate(sql,new Object[]{
						bizId,
						submit_data.getString("constructionUnit")+submit_data.getString("flowName"),
						submit_data.getString("projectId"),
						submit_data.getString("constructionUnit"),
						submit_data.getString("leaderOrgCode"),
						submit_data.getString("leaderOrgName"),
						submit_data.getString("flowId"),
						submit_data.getString("flowName"),
						submit_data.getString("formId"),
						submit_data.getString("dataId"),
						submit_data.getString("region_code"),
						submit_data.toString(),
						(String) pSet.get("uid"),
						submit_data.getString("projectName"),
						state,						
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS)
					});				
			}			
			if (i > 0) {			
				if(pSet.containsKey("bizId")){
					JSONArray itemArray = submit_data.getJSONArray("itemArray");
					int len = itemArray.size();
					String sql_item="insert into PARALLEL_BUSINESS_INDEX t (ID,BIZ_ID,SUBMIT_TIME,ITEM_ID,ITEM_NAME," +
							"REGION_CODE,REGION_NAME,PROJECTCODE,PROJECTNAME,CONTENT,STATUS,ITEM_CODE) values (?,?,?,?,?,?,?,?,?,?,?,?)";
					for(int j=0;j<len;j++){
						String id = Tools.getUUID32();
						this.executeUpdate(sql_item,new Object[]{
								id,bizId,CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
								itemArray.getJSONObject(j).getString("itemId"),
								itemArray.getJSONObject(j).getString("itemName"),
								submit_data.getString("region_code"),
								submit_data.getString("region_name"),
								submit_data.getString("projectId"),
								submit_data.getString("projectName"),
								submit_data.toString(),
								"3",								
								itemArray.getJSONObject(j).getString("itemCode")
						});
					}
				}
				ds.setState(StateType.SUCCESS);
				ds.setRawData(bizId);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("事项信息写入数据库失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		}
		return ds;
	}
	
	/**
	 * 获取工程
	 * @param pSet
	 * @return
	 */
	public DataSet getEnginProjectByBizId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String biz_id = (String) pSet.getParameter("biz_id");
			String sql = "select t.* from PARALLEL_BIZ_BASE t where t.BIZ_ID = ?";
			ds = this.executeDataset(sql,new Object[]{biz_id});
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}
}
