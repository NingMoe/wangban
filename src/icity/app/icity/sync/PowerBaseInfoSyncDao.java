/**  
 * @Title: PowerBaseInfoSyncDao.java 
 * @Package app.icity.sync 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-9-23 上午10:57:14 
 * @version V1.0  
 */ 
package app.icity.sync;

import app.pmi.BspDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/** 
 * @ClassName: PowerBaseInfoSyncDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-9-23 上午10:57:14  
 */

public class PowerBaseInfoSyncDao extends BaseJdbcDao {
	
	private static Log log_ = LogFactory.getLog(PowerBaseInfoSyncDao.class);
	
	private static PowerBaseInfoSyncDao _instance; 
	
	private PowerBaseInfoSyncDao(){
		this.setDataSourceName("icityDataSource");
	}
	
	public static PowerBaseInfoSyncDao getInstance() {
		_instance= (PowerBaseInfoSyncDao)DaoFactory.getDao(PowerBaseInfoSyncDao.class.getName());
		return _instance;
	}
	public DataSet DelInsSyncData(ParameterSet pSet){
		HttpClientUtil client = new HttpClientUtil();
		HttpClientUtil clientForm = new HttpClientUtil();

		DataSet ds = new DataSet();
		//String VERSION = (String)pSet.getParameter("VERSION");
		String ids = (String)pSet.getParameter("ids");
		String deptid = (String)pSet.getParameter("DEPTID");
		String ItemId = (String)pSet.getParameter("ItemId");
		String[] codes = null;
		//String[] VERSIONS = null;
		String[] ItemIds = null;

		if(!StringUtils.isEmpty(ids)){
			codes =  ids.split(",");
			//VERSIONS =  VERSION.split(",");
			ItemIds =  ItemId.split(",");
		}
		/*String _code = "";
		int _len = codes.length;
		for(int i=0;i<_len;i++){
			if(i==_len-1){
				_code +="'"+codes[i]+"'";	
			}else{
				_code +="'"+codes[i]+"',";
			}
		}*/
		//同步就全删
		Connection conndel = DbHelper.getConnection(this.getDataSourceName());
		try{
			conndel.setAutoCommit(false);
			String sql_del_all="delete from POWER_BASE_INFO t where t.DEPT_ID = '"+deptid+"'";//where t.code not exists (select 1 from POWER_BASE_INFO s where s.code in ("+_code+")) ";
			DbHelper.update(sql_del_all,new Object[]{},conndel);
			conndel.commit();
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			DBSource.closeConnection(conndel);
		}
		//删完全同步
		for(int i=0;codes!=null&&i<codes.length;i++){
			if (StringUtils.isEmpty(codes[i])) {
				continue;
			}
			Connection conn = DbHelper.getConnection(this.getDataSourceName());
			String busiCode = codes[i];
			String busiId = ItemIds[i];

			//String url = HttpUtil.formatUrl(getSecurityItem("ItemSynchronizationUrl")+"/Inspur.Ecgap.PowerManager/main/power/getItemInfo/info/"+busiCode+"/"+VERSIONS[i]);//deptid;
			//String url = HttpUtil.formatUrl(getSecurityItem("ItemSynchronizationUrl")+"/Inspur.Ecgap.PowerManager/main/power/getItemInfoByItemID/792FAD780BC244EFA77E8F3ACC6A9519");//deptid;
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getAllItemInfoByItemID?itemId="+busiId);//deptid;
			String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+busiId);//deptid;

			String strItemList="";
			JSONArray joItemListArray;//基本要素"info"(基本要素),
			JSONArray materialArray;//"material"(申请材料),
			JSONObject joFormItem ;
			//JSONArray documentArray = new JSONArray();//"document"(标准文书),
			//JSONArray legalbasisArray = new JSONArray();//"legalbasis"(法律依据),\
			//JSONArray handlingprocessArray = new JSONArray();//"handlingprocess"(办理流程),
			//JSONArray chargeArray = new JSONArray();//"charge"(事项收费)			
			//大字段
			JSONObject joContent  = new JSONObject();//汇总的大字段
			JSONArray jaindividuations  = new JSONArray();//大字段中的
			JSONArray jawindows  = new JSONArray();//大字段中的窗口信息
			JSONArray jaattachments  = new JSONArray();//大字段中的附件信息
			JSONArray jalawinfos  = new JSONArray();//大字段中的法律法规
			JSONArray jacatalogs  = new JSONArray();//大字段中的情形
			JSONObject jodept  = new JSONObject();//大字段中的部门信息 
			JSONObject joregion  = new JSONObject();//大字段中的行政区划信息
			
			/*HttpClient clients = new HttpClient();

			PostMethod postMethod = new PostMethod(urlForm);
			clients.getParams().setContentCharset("UTF-8");		
			postMethod.setParameter("postdata", busiId);	*/
			try{
				strItemList= client.getResult(url,"");
				joItemListArray = JSONObject.fromObject(strItemList).getJSONArray("ItemInfo");//基本信息
				materialArray = JSONObject.fromObject(strItemList).getJSONArray("material");//申请材料
				//documentArray = JSONObject.fromObject(strItemList).getJSONArray("document");//标准文书？？？？
				//legalbasisArray = JSONObject.fromObject(strItemList).getJSONArray("legalbasis");//法律依据
				//handlingprocessArray = JSONObject.fromObject(strItemList).getJSONArray("handlingprocess");//办理流程
				//chargeArray = JSONObject.fromObject(strItemList).getJSONArray("charge");//事项收费
				joFormItem = JSONObject.fromObject(clientForm.getResult(urlForm,""));//表单信息
				joFormItem = joFormItem.getJSONObject("info");

			}catch(Exception e){
				ds.setState(StateType.FAILT);
				ds.setMessage("同步失败！");
				return ds;
			}
			JSONObject joServiceItem =  joItemListArray.getJSONObject(0);
			joServiceItem.put("POWER_PROCESS", "申办-受理-承办-办结");
			
			JSONObject joserviceitem  = serviceItem(joServiceItem);//大字段中的事项信息
			//jawindows  = windowsItem(joServiceItem);//大字段中的窗口信息
			JSONArray jaapply_info = apply_info(materialArray);//大字段中的材料信息
			//jaattachments  = xxx();//大字段中的附件信息
			//jalawinfos  = xxx();//大字段中的法律法规
			//jacatalogs  = xxx();//大字段中的情形
			jodept.put("NAME", joServiceItem.get("AGENT_NAME").equals(null)?"":joServiceItem.get("AGENT_NAME"));//大字段中的部门信息 
			//joregion  =xxx();//大字段中的行政区划信息
			joregion.put("NAME", "");

			joContent.put("individuations", jaindividuations);
			joContent.put("serviceitem", joserviceitem);
			joContent.put("windows", jawindows);
			joContent.put("apply_info", jaapply_info);
			joContent.put("attachments", jaattachments);
			joContent.put("lawinfos", jalawinfos);
			joContent.put("catalogs", jacatalogs);
			joContent.put("dept", jodept);
			joContent.put("region", joregion);			
			//JSONObject dateObj = (JSONObject)joServiceItem.get("LAST_TIME");
			//Date date = new Date(dateObj.getInt("time"));
			try{
				conn.setAutoCommit(false);
				String sql_del="delete from POWER_BASE_INFO WHERE CODE=? ";
				DbHelper.update(sql_del,new Object[]{busiCode},conn);
				CacheManager.set("icity_permission", busiCode, null); //把办事指南页面的缓存清楚
				String sql = "INSERT INTO POWER_BASE_INFO " +
						"(CODE,ITEM_ID,NAME,DEPT_ID,TIME_COST,TIME_COST_DATETYPE,OBJECT_TYPE,CONTENT,STATUS,ZF_FLAG,WL_FLAG,PROVIDE_CONSULT,PROVIDE_FORMS,PROVIDE_RESULT,PROVIDE_APPLY,SUIT_ONLINE" +
						",ONLINE_SERVICE_URL,ITEM_SORT_ORDER,FOLDER_NAME,FOLDER_SORT_ORDER,NEED_CHARGE,RID,DEPT_NAME,CATALOGS" +
						",ITEM_TYPE,DEPT_ICON,DEPT_NAME_SHORT,RNAME,COMPLAINT_PHONE,DEPTNO,MODIFICATION,STAGE,ITEM_LEVEL,INVEST,IS_INVESTMENT_PROJECT,DECLARE_SERVICE_LEVEL,APPROVE_SERVICE_LEVEL,LAST_MODIFICATION_TIME"+
						",SERVICE_OBJECT,orgName,ASSORT) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				//System.out.print(joServiceItem.get("TITLE_NAME"));
				DbHelper.update(sql, new Object[]{
						busiCode,        //事项编码（唯一,对应事项BUSI_CODE）
						joServiceItem.get("ID"),  //事项ID
						joServiceItem.get("NAME"),    //事项名称
						deptid,//网办系统部门id
						joServiceItem.get("ACCEPT_TIME").equals(null)?0:joServiceItem.get("ACCEPT_TIME"),//办事时间
						"G",//办事时间类型。G：工作日，Z：自然日
						getJsonString(joFormItem, "objectType"),//j//办理对象类型
						joContent.toString(), //事项内容
						"1",//状态：1，正常；2，废置
						joServiceItem.get("PAY_ONLINE").equals(null)?"0":joServiceItem.get("PAY_ONLINE"),		//是否支持在线支付
						"0",//是否支持物流
						joServiceItem.get("CONSULT_ONLINE").equals(null)?"0":joServiceItem.get("CONSULT_ONLINE"), //是否支持咨询
						"0",//是否支持表格下载
						"0",//joServiceItem.get("PROVIDE_RESULT"),//提供结果反馈服务情况
						"2",//joServiceItem.get("IS_ONLINE").equals("1")?"2":"0",//joServiceItem.get("PROVIDE_APPLY"),//提供在线申办服务情况
						joServiceItem.get("IS_ONLINE").equals(null)?"0":joServiceItem.get("IS_ONLINE"),//是否适合网上办理 0否1是
						"",//在线申办服务网址
						joServiceItem.get("SORT_ORDER").equals(null)?"0":joServiceItem.get("SORT_ORDER"),     // 事项排序
						"", //目录名称
						"", //目录排序
						joServiceItem.get("IS_CHARGE").equals(null)?"0":joServiceItem.get("IS_CHARGE"),    //是否收费0：否，1：是
						joServiceItem.get("REGION_CODE").equals(null)?"0":joServiceItem.get("REGION_CODE"),            //行政区划代码
						joServiceItem.get("AGENT_NAME").equals(null)?"0":joServiceItem.get("AGENT_NAME"),          //部门名称
						joServiceItem.get("TITLE_NAME").equals(null)?"0":joServiceItem.get("TITLE_NAME"),//catalogs,//分类，前后加；号
						joServiceItem.get("TYPE").equals(null)?"0":joServiceItem.get("TYPE"),    //事项类型
						"",//部门图标
						joServiceItem.get("AGENT_NAME").equals(null)?"0":joServiceItem.get("AGENT_NAME"),    //部门简称
						joServiceItem.get("REGION_NAME").equals(null)?"0":joServiceItem.get("REGION_NAME"),       //行政区划名称
						joServiceItem.get("COMPLAIN_PHONE").equals(null)?"0":joServiceItem.get("COMPLAIN_PHONE"),  //监督咨询电话
						joServiceItem.get("ORG_CODE"),//部门统一编号
						"0",		 		 //网办操作修改 0：否，1：是
						"0",//joServiceItem.get("STAGE"),  //所属投资审批阶段
						"1",//joServiceItem.get("ITEM_LEVEL"),  //事项等级 1：A，2：B，3：C，0：待定
						"0",//joServiceItem.get("INVEST"),//是否投资审批事项
						"0",//joServiceItem.get("IS_INVESTMENT_PROJECT"),
						"0",//joServiceItem.get("DECLARE_SERVICE_LEVEL"),//申报网上办事深度
						"0",//joServiceItem.get("APPROVE_SERVICE_LEVEL"),//核准网上办事深度						
						(joServiceItem.containsKey("LAST_TIME") && joServiceItem.getJSONObject("LAST_TIME") != null)
						?CommonUtils.getInstance().parseStringToTimeStamp(CommonUtils.getInstance().
								formatJsonToString(joServiceItem.getJSONObject("LAST_TIME"), 
										CommonUtils.YYYY_MM_DD_HH_mm_SS),CommonUtils.YYYY_MM_DD_HH_mm_SS):CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
						joServiceItem.get("SERVICE_OBJECT"),  //SERVICE_OBJECT 0个人1法人
						joServiceItem.get("ORG_NAME"),
						joServiceItem.get("ASSORT")//审批办件审批板件类型 1承诺件2即办件3上报件4联办件

				}, conn);
				String sqlEx = "SELECT * FROM POWER_BASE_INFO_OF_FORM WHERE ITEM_CODE = ?";
				DataSet result = DbHelper.query(sqlEx, new Object[]{busiCode}, conn);
				if (result.getTotal() > 0) {
					String uSql = "UPDATE POWER_BASE_INFO_OF_FORM SET FORM_CODE=?,FORM_NAME=?,FORM_SAMPLE_ID=? WHERE ITEM_CODE=?";
					DbHelper.update(uSql, new Object[]{getJsonString(joFormItem, "formId"),getJsonString(joFormItem, "formName"),getJsonString(joFormItem, "formId"),busiCode}, conn);
				}else{
					String iSql = "INSERT INTO POWER_BASE_INFO_OF_FORM(ITEM_CODE,FORM_CODE,FORM_NAME,FORM_SAMPLE_ID)VALUES(?,?,?,?)";
					DbHelper.update(iSql, new Object[]{busiCode,getJsonString(joFormItem, "formId"),getJsonString(joFormItem, "formName"),getJsonString(joFormItem, "formId")}, conn);
				}
				conn.commit();
			}catch(Exception e){
				log_.error("事项编码["+busiCode+"]"+":"+e.getMessage(),e);
				ds.setState(StateType.FAILT);
				ds.setMessage("同步过程中操作出错！");
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return ds;
			}finally{
				DBSource.closeConnection(conn);
			}
		}
		if(log_.isInfoEnabled()){
			log_.info("同步事项数据成功！");
		}
		ds.setState(StateType.SUCCESS);
		ds.setMessage("同步事项数据成功！");
		return ds;
	} 
	
	public String getJsonString(JSONObject json,String key){
		if (json == null) {
			return "";
		}
		if (!json.containsKey(key)) {
			return "";
		}
		return json.getString(key);
	}
	
	public DataSet DeleteSyncData(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = null;
		try {
			conn = DbHelper.getConnection("icityDataSource");
			String busiCode = (String)pSet.getParameter("CODE@IN");
			if(!StringUtils.isNotEmpty(busiCode)){
				if(log_.isInfoEnabled()){
					log_.info("同步事项数据失败！");
				}
				ds.setState(StateType.FAILT);
				ds.setMessage("同步事项数据失败！");
				return ds;
			}
			String sql_del2="delete from POWER_BASE_INFO_OF_FORM ";
			ParameterSet p = new ParameterSet();
			p.setParameter("ITEM_CODE@in", busiCode);
			sql_del2 = SqlCreator.getSimpleQuerySql(p, sql_del2, this.getDataSource(this.getDataSourceName()));
			DbHelper.update(sql_del2,null,conn);
			String sql_del="UPDATE POWER_BASE_INFO SET LAST_MODIFICATION_TIME = sysdate,STATUS=0 ";
			sql_del=SqlCreator.getSimpleQuerySql(pSet, sql_del, this.getDataSource(this.getDataSourceName()));
			int ret = this.executeUpdate(sql_del,null,this.getDataSourceName());
			if(ret>0){
				if(log_.isInfoEnabled()){
					log_.info("同步事项数据成功！");
				}
				ds.setState(StateType.SUCCESS);
				ds.setMessage("同步事项数据成功！");
				return ds;
			}else{
				if(log_.isInfoEnabled()){
					log_.info("同步事项数据失败！");
				}
				ds.setState(StateType.FAILT);
				ds.setMessage("同步事项数据失败！");
				return ds;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbHelper.closeConnection(conn);
		}
		return ds;
	} 
	
	
	public DataSet DeleteData(ParameterSet pSet){
		DataSet ds = new DataSet();
		//String busiCode = (String)pSet.getParameter("CODE@IN");

		String sql_del=" delete from POWER_BASE_INFO ";
		sql_del=SqlCreator.getSimpleQuerySql(pSet, sql_del, this.getDataSource(this.getDataSourceName()));
		int ret = this.executeUpdate(sql_del,null,this.getDataSourceName());
		if(ret>0){
			if(log_.isInfoEnabled()){
				log_.info("删除事项数据成功！");
			}
			ds.setState(StateType.SUCCESS);
			ds.setMessage("删除事项数据成功！");
			return ds;
		}else{
			if(log_.isInfoEnabled()){
				log_.info("删除事项数据失败！");
			}
			ds.setState(StateType.FAILT);
			ds.setMessage("删除事项数据失败！");
			return ds;
		}
		
	} 
	
	
	public DataSet deleteGabige(ParameterSet pSet){
		String icp = BspDao.getInstance().getDbName("icpDataSource");
		String sql = "DELETE FROM POWER_BASE_INFO A WHERE NOT EXISTS(SELECT 0 FROM "+icp+".POWER_base_inFO b WHERE a.code = b.busi_code AND b.status = 'PUBLISHED')";
		this.executeUpdate(sql);
		return new DataSet();
	}
	
	
	/**
	 * 通过调用事项管理获取事项基本信息
	 * @param pSet
	 * @return
	 */
	public DataSet PublishPreviewDataRest(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject jo =new JSONObject();	
		JSONObject jobase = new JSONObject();
		String ROOT_URL = SecurityConfig.getString("RestDomain");
		HttpClientUtil client = new HttpClientUtil();
		JSONObject j = new JSONObject();
		j.put("access_token", "zwb376C078E4972453C847AF1AF1C955DC6");
		j.put("code",(String)pSet.get("code"));
		String strItemContent = client.getResult(ROOT_URL+"api.item/getItemInfo",j.toString());
		JSONObject joContent = JSONObject.fromObject(strItemContent).getJSONObject("data");
		//获取事项信息
		JSONObject serviceitem = joContent.getJSONObject("serviceitem");
		//获取基本信息
		jobase.put("CODE", (String)serviceitem.get("CODE"));   //事项编码（唯一,对应事项BUSI_CODE）
		jobase.put("NAME", (String)serviceitem.get("NAME"));   //事项名称
		jobase.put("TIME_COST", serviceitem.get("TIME_COST"));   //办事时间
		jobase.put("TIME_COST_DATETYPE", (String)serviceitem.get("TIME_COST_DATETYPE"));    //办事时间类型。G：工作日，Z：自然日
		jobase.put("ITEM_TYPE", (String)serviceitem.get("ITEM_TYPE"));     //事项类型
		jobase.put("NEED_CHARGE", (String)serviceitem.get("NEED_CHARGE"));  //是否收费0：否，1：是
		jobase.put("WL_FLAG", "0");    //是否支持物流
		jobase.put("PROVIDE_CONSULT", (String)serviceitem.get("PROVIDE_CONSULT"));  //是否支持咨询
		jobase.put("ZF_FLAG", (String)serviceitem.get("PAY_ONLINE"));   //是否支持在线支付
		jobase.put("SUIT_ONLINE", (String)serviceitem.get("SUIT_ONLINE"));
		jobase.put("DEPT_ID", (String)serviceitem.get("ADMIN_ORG_CODE"));
		jobase.put("ONLINE_SERVICE_URL", (String)serviceitem.get("ONLINE_SERVICE_URL"));
		jobase.put("ITEM_LEVEL", (String)serviceitem.get("ITEM_LEVEL"));
		
		
		JSONObject joDept = joContent.getJSONObject("dept");
		jobase.put("DEPT_NAME", (String)joDept.get("NAME"));
		jobase.put("DEPT_ICON", (String)joDept.get("ICON"));
		jobase.put("DEPT_NAME_SHORT", joDept.get("NAME_SHORT"));
		
		JSONObject joRegion = joContent.getJSONObject("region");
		jobase.put("RNAME", joRegion.get("NAME"));
		
		
		JSONArray joCatalogs = joContent.getJSONArray("catalogs");
		
		StringBuffer catalogs = new StringBuffer(",");
		for(int k =0;k<joCatalogs.size();k++){
			catalogs.append(joCatalogs.getJSONObject(k).get("CODE")).append(",");
		}
		jobase.put("CATALOGS", catalogs.toString());
		
		/*String catalogs = ",";
		for(int k =0;k<joCatalogs.size();k++){
			catalogs +=joCatalogs.getJSONObject(k).get("CODE")+",";
		}
		jobase.put("CATALOGS", catalogs);*/
		

		//拆分办理对象
		JSONArray ags=new JSONArray();
		JSONObject ag=new JSONObject();
		String agents=serviceitem.getString("AGENT");
		String[] agent=agents.split("\r\n");
		for(int i=0;i<agent.length;i++){
			ag.put("NAME", agent[i]);
			ags.add(ag);
		}
		serviceitem.remove("AGENT");
		serviceitem.put("AGENT", ags);
		//拆分办理条件
		JSONArray sections=new JSONArray();//办理条件段落
		JSONObject section=new JSONObject();
		String conditions=serviceitem.getString("CONDITIONS");
		String[] condition=conditions.split("\r\n");
		for(int i=0;i<condition.length;i++){				
			section.put("NR", condition[i]);
			sections.add(section);			
		}
		serviceitem.remove("CONDITIONS");
		serviceitem.put("CONDITIONS", sections);
		//拆分窗口办理流程
		JSONArray winpros=new JSONArray();
		JSONObject winpro=new JSONObject();
		String window_processes=serviceitem.getString("WINDOW_PROCESS");
		String[] window_process=window_processes.split("\r\n");
		for(int i=0;i<window_process.length;i++){
			winpro.put("NAME", window_process[i]);
			winpros.add(winpro);
		}
		serviceitem.remove("WINDOW_PROCESS");
		serviceitem.put("WINDOW_PROCESS", winpros);
		//拆分网上办理流程
		JSONArray olpros=new JSONArray();
		JSONObject olpro=new JSONObject();
		String online_processes=serviceitem.getString("ONLINE_PROCESS");
		String[] online_process=online_processes.split("\r\n");
		for(int i=0;i<online_process.length;i++){
			olpro.put("NAME", online_process[i]);
			olpros.add(olpro);
		}
		serviceitem.remove("ONLINE_PROCESS");
		serviceitem.put("ONLINE_PROCESS", olpros);
		//将办事窗口拆分为初始化显示和点击后显示
		JSONArray windows=(JSONArray)joContent.get("windows");
		JSONArray shows=new JSONArray();
		if(windows.size()>3){
			for(int i=0;i<3;i++){
				shows.add(windows.remove(0));				
			}
			joContent.put("shows", shows);
			joContent.put("mores", windows);
		}else{
			joContent.put("shows",windows);
		}
		//获取常见问题信息
		JSONArray ques=new JSONArray();
		JSONObject que=new JSONObject();
		String faqs=serviceitem.getString("FAQ");
		String[] faq=faqs.split("\r\n");
		for(int i=0;i<faq.length;i++){
			que.put("NR", faq[i]);
			ques.add(que);
		}
		serviceitem.remove("FAQ");
		serviceitem.put("FAQ", ques);
		//移除多余内容，提升数据传送效率
		joContent.remove("folder");
		joContent.remove("catalogs");	
		joContent.remove("dept");
		joContent.remove("region");
		//返回转换后数据
		jo.put("content",joContent.toString());
		
		jobase.remove("CONTENT");			
		jo.put("pitem",jobase);
		ds.setData(Tools.stringToBytes(jo.toString()));
		return ds;
	}
	
	
	/*
	 * ServiceItem 事项基本信息
	 */
	public JSONObject serviceItem(JSONObject iteminfo){
		JSONObject joServiceItem = new JSONObject();
		joServiceItem.put("OBJECT", "0".equals(iteminfo.get("SERVICE_OBJECT"))?"个人":"法人");//办理对象
		joServiceItem.put("CONDITIONS", iteminfo.get("CONDITIONS"));//办理条件 
		 //所需材料  从  申请材料里面获取
		joServiceItem.put("ONLINE_PROCESS", iteminfo.get("ONLINE_PROCESS"));//办理流程
		joServiceItem.put("WINDOW_PROCESS", iteminfo.get("WINDOW_PROCESS") == null || iteminfo.get("WINDOW_PROCESS").equals("null")?"申办-受理-承办-办结":iteminfo.get("WINDOW_PROCESS"));//窗口办理流程
		 //办理时限
		joServiceItem.put("PROMISED_PERIOD", iteminfo.get("AGREE_TIME"));//承诺期限: 未承诺期限
		joServiceItem.put("LEGAL_PROCESS", iteminfo.get("LAW_TIME"));//法定期限: 未规定期限
		 //办事窗口 办事窗口里面取
		joServiceItem.put("NEED_CHARGE", iteminfo.get("IS_CHARGE"));//收费标准 不收费 
		 //主管部门 在dept里面
		joServiceItem.put("AGENT", iteminfo.get("AGENT_NAME"));//受理机构 
		 //joServiceItem("WINDOW_PROCESS", iteminfo.get("WINDOW_PROCESS"));//办理依据 
		joServiceItem.put("DESCRIPTION", iteminfo.get("REMARK"));//备注 
		joServiceItem.put("ADMIN_ORG_CODE", iteminfo.get("REMARK"));
		joServiceItem.put("FAQ", iteminfo.get("REMARK"));
		joServiceItem.put("NAME", iteminfo.get("NAME"));
		//joServiceItem.put("SUIT_ONLINE",iteminfo.get("IS_ONLINE").equals(null)?"0":iteminfo.get("IS_ONLINE"));	
		joServiceItem.put("SUIT_ONLINE",iteminfo.get("IS_ONLINE")==null?"0":iteminfo.get("IS_ONLINE"));//1 支持 0 	不支持
		joServiceItem.put("DESCRIPTION", iteminfo.get("DESCRIPTION")==null?"":iteminfo.get("DESCRIPTION"));//备注
		JSONObject joBusiness_Data = new JSONObject();
			 joBusiness_Data.put("OBJECT", "0".equals(iteminfo.get("SERVICE_OBJECT"))?"个人":"法人");//办理对象
			 joBusiness_Data.put("CONDITIONS", iteminfo.get("CONDITIONS")==null?"":iteminfo.get("CONDITIONS"));//办理条件 
			 //所需材料  从  申请材料里面获取
			 joBusiness_Data.put("ONLINE_PROCESS", iteminfo.get("ONLINE_PROCESS")==null?"":iteminfo.get("ONLINE_PROCESS"));//办理流程
			 joBusiness_Data.put("WINDOW_PROCESS", iteminfo.get("WINDOW_PROCESS") == null || iteminfo.get("WINDOW_PROCESS").equals("null")?"申办-受理-承办-办结":iteminfo.get("WINDOW_PROCESS"));//窗口办理流程
			 //办理时限
			 joBusiness_Data.put("PROMISED_PERIOD", iteminfo.get("AGREE_TIME"));//承诺期限: 未承诺期限
			 joBusiness_Data.put("LEGAL_PROCESS", iteminfo.get("LAW_TIME"));//法定期限: 未规定期限
			 //办事窗口 办事窗口里面取
			 joBusiness_Data.put("NEED_CHARGE", iteminfo.get("IS_CHARGE"));//收费标准 不收费 
			 //主管部门 在dept里面
			 joBusiness_Data.put("AGENT", iteminfo.get("AGENT_NAME"));//受理机构 
			 //joBusiness_Data.put("WINDOW_PROCESS", iteminfo.get("WINDOW_PROCESS"));//办理依据 
			 joBusiness_Data.put("DESCRIPTION", iteminfo.get("DESCRIPTION")==null?"":iteminfo.get("DESCRIPTION"));//备注 
		joServiceItem.put("BUSINESS_DATA", joBusiness_Data);
		return joServiceItem;
	}
	/*
	 * apply_info 申请材料   将新事项系统的申请材料信息与网办系统申请材料信息相对应
	 */
	public JSONArray apply_info(JSONArray ja){
		int len = ja.size();//ja包含多个申请材料
		JSONArray jaapply_info = new JSONArray();
		JSONObject jo = new JSONObject();
		for(int i=0;i<len;i++){
			jo.put("CODE",ja.getJSONObject(i).get("CODE") == null || ja.getJSONObject(i).get("CODE").equals("null")?"":ja.getJSONObject(i).get("CODE"));//	VARCHAR2(50)N附件代码。标识属性
			jo.put("ORG_CODE",ja.getJSONObject(i).get("ORG_CODE") == null || ja.getJSONObject(i).get("ORG_CODE").equals("null")?"":ja.getJSONObject(i).get("ORG_CODE"));	//VARCHAR2(50)N部门编码
			jo.put("PUBTIME",ja.getJSONObject(i).get("CREATE_TIME"));	//DATE出具时间
			jo.put("PUBLISHER",ja.getJSONObject(i).get("CREATOR") == null || ja.getJSONObject(i).get("CREATOR").equals("null")?"":ja.getJSONObject(i).get("CREATOR"));	//VARCHAR2(255)Y出具机构
			jo.put("TYPE",ja.getJSONObject(i).get("TYPE") == null || ja.getJSONObject(i).get("TYPE").equals("null")?"":ja.getJSONObject(i).get("TYPE"));	//VARCHAR2(100)Y材料类型编码，参看ApplyType
			jo.put("ORIGINAL_TYPE",ja.getJSONObject(i).get("TYPE") == null || ja.getJSONObject(i).get("TYPE").equals("null")?"":ja.getJSONObject(i).get("TYPE"));	//VARCHAR2(50)Y原件类型，参看ApplyOriginalType
			jo.put("FILE_URL",ja.getJSONObject(i).get("URL") == null || ja.getJSONObject(i).get("URL").equals("null")?"":ja.getJSONObject(i).get("URL"));	//VARCHAR2(500)Y附件在文件系统中的位置
			jo.put("SORT_ORDER",ja.getJSONObject(i).get("SORT_ORDER") == null || ja.getJSONObject(i).get("SORT_ORDER").equals("null")?"":ja.getJSONObject(i).get("SORT_ORDER"));	//VARCHAR2(50)N0默认为0，值越大越排在前
			jo.put("CREATOR",ja.getJSONObject(i).get("CREATOR") == null || ja.getJSONObject(i).get("CREATOR").equals("null")?"":ja.getJSONObject(i).get("CREATOR"));	//VARCHAR2(100)N创建者
			jo.put("CREATION_TIME",ja.getJSONObject(i).get("CREATE_TIME"));	//DATEN创建时间
			jo.put("LAST_MODIFICATOR",ja.getJSONObject(i).get("LAST_EDITOR") == null||ja.getJSONObject(i).get("LAST_EDITOR").equals("null")?"":ja.getJSONObject(i).get("LAST_EDITOR"));	//VARCHAR2(100)Y最后修改者
			jo.put("LAST_MODIFICATION_TIME",ja.getJSONObject(i).get("LAST_TIME"));	//DATEY最后修改时间
			jo.put("INFO_SOURCE","");	//VARCHAR2(500)Y材料来源
			jo.put("INFO_BASIS","");	//VARCHAR2(4000)Y材料依据
			jo.put("STRATEGY","");	//VARCHAR2(4000)Y办事攻略
			jo.put("NAME",ja.getJSONObject(i).get("NAME") == null || ja.getJSONObject(i).get("NAME").equals("null")?"":ja.getJSONObject(i).get("NAME"));	//CLOBN材料名称
			jo.put("DESCRIPTION",ja.getJSONObject(i).get("REMARK") == null || ja.getJSONObject(i).get("REMARK").equals("null")?"":ja.getJSONObject(i).get("REMARK"));	//CLOBY			
			jo.put("COUNT","0");
			jaapply_info.add(jo);
		}
		return jaapply_info;
	}
	public void submitData(JSONObject receiveNum,JSONObject data) throws Exception{
		String sblsh = receiveNum.getString("receiveNum");
		String o_sblsh=data.getString("receiveNum");
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String baseContent = data.toString();
		String sql="";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
			if(data.getString("state").equals("sp")){
				if(!"".equals(o_sblsh)){
					sql="update business_index set sblsh=?,state='11',sbsj=? where sblsh=? ";
					DbHelper.update(sql, new Object[]{sblsh,CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_mm_SS),o_sblsh},conn);
					sql = " update SUB_FOR_EX_APP_INFORMATION set SBLSH=?, OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
					DbHelper.update(sql, new Object[]{sblsh,objectType,baseContent,formId,dataId,o_sblsh}, conn);
				}
				else{
					sql = "insert into business_index (SBLSH,SXBM,SXID,"+
					"SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"+
					"LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"+
					"YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"+
					"SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"+
					"SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"+
					"TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"+
					"BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"+
					"STATE,URL,LIMIT_NUM,ASSORT) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					DbHelper.update(sql, new Object[]{
							sblsh,//申办流水号类似的编码来确定事项 
							data.get("itemCode"),
							data.get("itemId"),
							data.get("itemName"),
							data.get("ucid"),
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
							data.get("objectType"),
							data.get("zjhm"),
							data.get("sqrxm"),
							data.get("lxrxm"),
							data.get("lxrphone"),
							data.get("itemName"),
							sblsh,
							"",//BLFS办理方式
							data.get("regionName"),
							data.get("regionCode"),
							null,//YSLSJ业务受理时间
							"",//YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
							"",//YSLSBYY预受理失败原因
							null,//SLSJ预受理失败原因
							"",//YWLSH业务流水号
							data.get("orgName"),
							data.get("orgCode"),//收件单位代码
							"",//SLZTDM受理状态代码(1受理，2不受理)
							"",//BSLYY不受理原因
							"",//SLHZH受理回执号
							"",//CXMM查询密码
							null,//SPSJ审批时间
							"",//SPHJDM审批环节代码(1承办，2审核，3批准)
							"",//SPHJMC审批环节名称
							null,//BZGZSJ补正告知时间
							"",//BZGZYY补正告知原因
							"",//BZCLQD补正材料清单
							null,//BZSJ补正时间
							null,//TBCXKSRQ特别程序开始日期
							"",//TBCXQDLY特别程序启动理由或依据
							"",//SQNR申请内容
							null,//TBCXJSRQ特别程序结束日期
							"",//TBCXJG特别程序结果
							null,//BJSJ办结时间
							"",//BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
							"",//BJJGMS办结结果描述
							"",//ZFHTHYY作废或退回原因
							null,//LQSJ领取时间
							"",//REMARK备注信息
							"11",//STATE业务状态
							"",//URL跳转地址
							"0",//limit_num 补正补齐次数
							data.get("assort")
						}, conn);
					sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values (?,?,?,?,?)";
					DbHelper.update(sql, new Object[]{sblsh,objectType,baseContent,formId,dataId}, conn);
				}
				 
			}else if(data.getString("state").equals("bqbz")){
				//可进行多次补齐补正
				 sql = "select to_char(BZGZSJ,'yyyy-MM-dd HH24:mi:ss') BZGZSJ, BZGZYY from business_index where sblsh = ?";
				DataSet result = DbHelper.query(sql, new Object[]{sblsh}, conn);
				sql = "insert into BUSINESS_BZBQ_LOG (SBLSH,BZBQSJ,BZBQYY) values(?,to_date(?,'yyyy-MM-dd HH24:MI:ss'),?)";
				DbHelper.update(sql, new Object[]{
						sblsh,
						result.getRecord(0).getString("BZGZSJ"),
						result.getRecord(0).getString("BZGZYY")		
				}, conn);
				 sql= "update business_index set BZSJ=?,state='11',limit_num=to_number(limit_num)+1 where sblsh=?";
				DbHelper.update(sql, new Object[]{
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
						sblsh,
						}, conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[]{objectType,baseContent,formId,dataId,sblsh}, conn);
			}
		    conn.commit();
	    }catch(Exception e){
	    	conn.rollback();
	    	throw e;
	    }finally{
	    	DBSource.closeConnection(conn);
	    }		
	}
	public String saveData(JSONObject data) throws Exception{
		String o_sblsh=data.getString("receiveNum");
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String baseContent = data.toString();
		String sql="";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
				if(!"".equals(o_sblsh)){
					sql="update business_index set sbsj=?,state='00' where sblsh=? ";
					DbHelper.update(sql, new Object[]{CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_mm_SS),o_sblsh},conn);
					sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
					DbHelper.update(sql, new Object[]{objectType,baseContent,formId,dataId,o_sblsh}, conn);
				}
				else{
					o_sblsh=Tools.getUUID32();
					sql = "insert into business_index (SBLSH,SXBM,SXID,"+
					"SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"+
					"LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"+
					"YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"+
					"SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"+
					"SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"+
					"TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"+
					"BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"+
					"STATE,URL,LIMIT_NUM,ASSORT) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					DbHelper.update(sql, new Object[]{
							o_sblsh ,//申办流水号类似的编码来确定事项 
							data.get("itemCode"),
							data.get("itemId"),
							data.get("itemName"),
							data.get("ucid"),
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
							data.get("objectType"),
							data.get("zjhm"),
							data.get("sqrxm"),
							data.get("lxrxm"),
							data.get("lxrphone"),
							data.get("itemName"),
							o_sblsh,
							"",//BLFS办理方式
							data.get("regionName"),
							data.get("regionCode"),
							null,//YSLSJ业务受理时间
							"",//YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
							"",//YSLSBYY预受理失败原因
							null,//SLSJ预受理失败原因
							"",//YWLSH业务流水号
							data.get("orgName"),
							data.get("orgCode"),
							"",//SLZTDM受理状态代码(1受理，2不受理)
							"",//BSLYY不受理原因
							"",//SLHZH受理回执号
							"",//CXMM查询密码
							null,//SPSJ审批时间
							"",//SPHJDM审批环节代码(1承办，2审核，3批准)
							"",//SPHJMC审批环节名称
							null,//BZGZSJ补正告知时间
							"",//BZGZYY补正告知原因
							"",//BZCLQD补正材料清单
							null,//BZSJ补正时间
							null,//TBCXKSRQ特别程序开始日期
							"",//TBCXQDLY特别程序启动理由或依据
							"",//SQNR申请内容
							null,//TBCXJSRQ特别程序结束日期
							"",//TBCXJG特别程序结果
							null,//BJSJ办结时间
							"",//BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
							"",//BJJGMS办结结果描述
							"",//ZFHTHYY作废或退回原因
							null,//LQSJ领取时间
							"",//REMARK备注信息
							"00",//STATE业务状态
							"",//URL跳转地址
							"0",//limit_num 补正补齐次数
							data.get("assort")
						}, conn);
					sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values (?,?,?,?,?)";
					DbHelper.update(sql, new Object[]{o_sblsh,objectType,baseContent,formId,dataId}, conn);
				}
		    conn.commit();
		    return o_sblsh;
	    }catch(Exception e){
			conn.rollback();
			throw e;
	    }finally{
	    	DBSource.closeConnection(conn);
	    }		
	}
	//更新business_index，通过审批流水号类似的编码来更新
	public DataSet updatebaseinfo(ParameterSet pSet){
		DataSet ds = new DataSet();
		String sblsh = (String)pSet.get("sblsh");//
		HttpClientUtil client = new HttpClientUtil();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getBusinessInfo?receiveNumber="+sblsh);//;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
			Object strItemList= client.getResult(url,"");
			JSONArray info = JSONObject.fromObject(strItemList).getJSONArray("info");		
			//int len = info.size();
			String getPreSql = " select * from business_index where sblsh='"+sblsh+"'";
			this.executeDataset(getPreSql);
			//DataSet PreDs = 
			/*String preContent = PreDs.getRecord(0).getString("PRECONTENT");
			if(!preContent.equals("")){
				if(len==0){
					//info.add(JSONObject.fromObject(preContent));
				}else{
					//info.add(0, JSONObject.fromObject(preContent));
				}
			}	*/		
			String sql = "update business_index t set t.content = ? where t.sblsh = ?";
			DbHelper.update(sql, new Object[]{
					info.toString(),					
					sblsh
					}, conn);
		    conn.commit();
	    }catch(Exception e){
	    	e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}	
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	public DataSet selectbusiness_log(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
			String sqlEx = "SELECT * FROM business_log WHERE sblsh = ?";
			DataSet result = DbHelper.query(sqlEx, new Object[]{(String)pSet.get("sblsh")}, conn);
			ds = result;
			conn.commit();
		}catch(Exception e){
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}	
		}finally{
	    	DBSource.closeConnection(conn);
	    }
		
		return ds;
	}
	public void SUB_FOR_EX_APP_INFORMATION(JSONObject item,JSONObject jo){
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
			String sblsh = item.getString("receiveNum");
			String objectType = jo.getString("objectType");
			String formId = jo.getString("formId");
			String dataId = jo.getString("dataId");
			
			String baseContent = jo.toString();
			String sqlEx = "select * FROM SUB_FOR_EX_APP_INFORMATION WHERE sblsh = ?";
			DataSet result = DbHelper.query(sqlEx, new Object[]{sblsh}, conn);
			if (result.getTotal() > 0) {
				String sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[]{objectType,baseContent,formId,dataId,sblsh}, conn);
				/*String sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = '"+objectType+"',basecontent = '"+baseContent
				+"',FORMID='"+formId+"',DATAID='"+dataId+"' where sblsh = '"+sblsh+"'";
				this.executeDataset(sql);*/
			}else{
				String sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values (?,?,?,?,?)";
				DbHelper.update(sql, new Object[]{sblsh,objectType,baseContent,formId,dataId}, conn);
				/*String sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values " +
						"('"+sblsh+"','"+objectType+"','"+baseContent+"','"+formId+"','"+dataId+"')";
				this.executeDataset(sql);*/
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}	
		}finally{
	    	DBSource.closeConnection(conn);
	    }
	}
	public DataSet select_SUB_FOR_EX_APP_INFORMATION(ParameterSet pSet){
		String sblsh = (String)pSet.get("SBLSH");
		//String baseContent = "";
		DataSet result = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{			
			String sqlEx = "select * FROM SUB_FOR_EX_APP_INFORMATION WHERE sblsh = ?";
			result = DbHelper.query(sqlEx, new Object[]{sblsh}, conn);
			/*if (result.getTotal() > 0) {
				baseContent = result.getRecord(0).getString("BASECONTENT");
			}else{
				
			}*/			
			conn.commit();
		}catch(Exception e){
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}	
		}finally{
	    	DBSource.closeConnection(conn);
	    }
		return result;
	}

	/*
	 * 预审
	 */
	public DataSet setPreList(ParameterSet pSet){
		String state = (String) pSet.getParameter("state");
		String sblsh = (String) pSet.getParameter("SBLSH");
		DataSet ds = null;	    
	    Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String precontent = pSet.getParameter("precontent").toString();
		if(state.equals("7")){
			precontent = "";
		}
		try{			
			String sql="update BUSINESS_INDEX set state=?,precontent=? where sblsh=?";
			DbHelper.update(sql, new Object[]{state,precontent,sblsh}, conn);					
			conn.commit();
		}catch(Exception e){
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}	
		}finally{
	    	DBSource.closeConnection(conn);
	    }
		return ds;
	}
}
