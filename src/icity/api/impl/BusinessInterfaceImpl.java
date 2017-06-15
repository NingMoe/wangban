package api.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import app.icity.guestbook.WriteCmd;
import app.icity.guestbook.WriteDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

@RestType(name = "api.businessInterface", descript = "业务相关接口")
public class BusinessInterfaceImpl extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(BusinessInterfaceImpl.class);
	public static BusinessInterfaceImpl getInstance(){
		return DaoFactory.getDao(BusinessInterfaceImpl.class.getName());
	}
	/**
	 * 插入评价
	 * @param pSet
	 * @return
	 */
	public DataSet insertNewEvaluation(ParameterSet pSet){
		Connection conn = DbHelper.getConnection("icityDataSource");
		String spsxcxmm = (String) pSet.remove("SPSXCXMM");
		//获取申办流水号
		String sblsh = (String) pSet.remove("SBLSH");
		//获取评价等级
		String level = (String)pSet.getParameter("STAR_LEVEL")==null?"0":(String)pSet.getParameter("STAR_LEVEL");
		Integer strStarLevel = Integer.valueOf("".equals(level)?"0":level);		
		//获取办事人名字
		String strBSRName = (String)pSet.getParameter("SQRMC");
		//评价内容
		String strEvaluateContent = (String)pSet.getParameter("EVALUATE_CONTENT");
		//获取远端ip
		String remoteAddr = pSet.getRemoteAddr();
		//获取创建者id
		String strCreatorId = "";//(String)pSet.getParameter("CreatorId");
		//事项编码
		//String SXBM = (String)pSet.getParameter("SXBM");
		//事项ID
		//String SXID = (String)pSet.getParameter("SXID");
		//收件单位
		//String SJDW = (String)pSet.getParameter("SJDW");
		//收件单位编码
		//String SJDWDM = (String)pSet.getParameter("SJDWDM");
		//行政区划
		//String REGION_CODE = (String)pSet.getParameter("REGION_CODE");
		String QUALITY_STAR_LEVEL=(String)pSet.getParameter("QUALITY_STAR_LEVEL");//			服务质量评价
		String TIME_STAR_LEVEL=(String)pSet.getParameter("TIME_STAR_LEVEL");//		办件时间评价
		String MAJOR_STAR_LEVEL=(String)pSet.getParameter("MAJOR_STAR_LEVEL");//			业务专业评价、操作体验评价
		String CONVENIENCE_STAR_LEVEL=(String)pSet.getParameter("CONVENIENCE_STAR_LEVEL");//		便捷性评价
		if(!StringUtils.isNotEmpty(QUALITY_STAR_LEVEL)){
			QUALITY_STAR_LEVEL = "0";
		}
		if(!StringUtils.isNotEmpty(TIME_STAR_LEVEL)){
			TIME_STAR_LEVEL = "0";
		}
		if(!StringUtils.isNotEmpty(MAJOR_STAR_LEVEL)){
			MAJOR_STAR_LEVEL = "0";
		}
		if(!StringUtils.isNotEmpty(CONVENIENCE_STAR_LEVEL)){
			CONVENIENCE_STAR_LEVEL = "0";
		}
		//查询数据库中事项是否已被评价
		DataSet ds;
		DataSet __ds = new DataSet();
		
		//判空
		if(null==sblsh||null==strStarLevel){
			__ds.setState(StateType.FAILT);
			__ds.setMessage("参数不完整！");
			return __ds;
		}		
		if("".equals(sblsh)||0>strStarLevel||5<strStarLevel){
			__ds.setState(StateType.FAILT);
			__ds.setMessage("参数不正确！");
			return __ds;
		}
		String sql = "select 1 from STAR_LEVEL_EVALUATION where SERIAL_NUMBER = ?";
		ds = DbHelper.query(sql, new Object[]{sblsh},conn);
		if( ds.getTotal()>0 ){
			//评价过了
			__ds.setState(StateType.FAILT);
			__ds.setMessage("当前事项已评！");
			return __ds;
		}
		
		Object ret;		
		try {
			HttpClientUtil client = new HttpClientUtil();
			String urlSp = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo?receiveNumber="+sblsh);//panyl
			String urlQl = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/getAllBusinessInfo?receiveNumber="+sblsh+"&password="+spsxcxmm);
			//String urlSp = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo?receiveNumber="+sblsh+"&password="+spsxcxmm);//panyl
			//String urlQl = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/getAllBusinessInfo?receiveNumber="+sblsh+"&password="+spsxcxmm);
			ret = client.getResult(urlSp,"");
			if("200".equals(JSONObject.fromObject(ret).getString("state"))){
				 JSONObject obj =  JSONObject.fromObject(ret);
				ds.setRawData(obj);
			}else{
				if("zs".equals(com.icore.util.SecurityConfig.getString("AppId"))){
					ret = client.getResult(urlQl,"");			
					if("200".equals(JSONObject.fromObject(ret).getString("state"))){
						 JSONObject obj =  JSONObject.fromObject(ret);
						ds.setRawData(obj);					
					}else{
						__ds.setState(StateType.FAILT);
						__ds.setMessage("业务不存在！");
						return __ds;
					}	
				}else{
					__ds.setState(StateType.FAILT);
					__ds.setMessage("业务不存在！");
					return __ds;
				}
				
			}
		} catch (Exception e) {
			__ds.setState(StateType.FAILT);
			__ds.setMessage(e.getMessage());
			e.printStackTrace();
			__ds.setMessage("业务不存在！");
			return __ds;
		}		
		//生成一个UUID
		String strId = Tools.getUUID32();		
		sql = "insert into STAR_LEVEL_EVALUATION(EVALUATE_TYPE,ID,BSR_NAME,SERIAL_NUMBER,STAR_LEVEL,EVALUATE_CONTENT," +
				"CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_CODE,SERVICE_ID,SERVICE_NAME,SERVICE_ORG_ID," +
				"SERVICE_ORG_NAME,REGION_CODE,REGION_NAME,APPLY_NAME,QUALITY_STAR_LEVEL,TIME_STAR_LEVEL," +
				"MAJOR_STAR_LEVEL,CONVENIENCE_STAR_LEVEL) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i;
			i = DbHelper.update(sql, new Object[] {"1",strId,strBSRName,sblsh,strStarLevel,
					strEvaluateContent,remoteAddr,strCreatorId,
					CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
					ds.getRecord(0).getString("ITEM_CODE"),ds.getRecord(0).getString("ITEM_ID"),
					ds.getRecord(0).getString("ITEM_NAME"),ds.getRecord(0).getString("DEPT_ID"),
					ds.getRecord(0).getString("DEPT_NAME"),ds.getRecord(0).getString("REGION_ID"),
					ds.getRecord(0).getString("REGION_NAME"),ds.getRecord(0).getString("APPLY_SUBJECT"),
					QUALITY_STAR_LEVEL,TIME_STAR_LEVEL,MAJOR_STAR_LEVEL,CONVENIENCE_STAR_LEVEL},conn);
			if( 0 == i ){
				__ds.setState(StateType.FAILT);
				__ds.setMessage("添加内容失败！");
			}else{
				__ds.setState(StateType.SUCCESS);
				__ds.setMessage("评价成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			__ds.setState(StateType.FAILT);
			__ds.setMessage("添加内容失败！");
		}finally{
			if(conn != null)
				try {
					conn.setAutoCommit(true);
				}catch (SQLException e) {
					e.printStackTrace();
					__ds.setState(StateType.FAILT);
					__ds.setMessage("添加内容失败！");
				}
			DbHelper.closeConnection(conn);
		}
		return __ds;
	}
	/**
	 * 更新投资项目在线审批：调该接口更新项目编码 
	 * @param pSet
	 * @return
	 */
	public DataSet updateInvestmentProject(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String sblsh = (String)pSet.get("sblsh");
			String seqid = (String)pSet.get("seqid");
			String code = (String)pSet.get("code");	
			String msg = (String)pSet.get("msg");	
			int i=0;
			String sql = "update ipro_index set sblsh=? where SEQID = ?";
			if("200".equals(code)){
				sql = "update ipro_index set sblsh=?,status='2',msg=? where SEQID = ?";
				i = DbHelper.update(sql, new Object[]{sblsh,msg,seqid},conn);				
			}else if("300".equals(code)){
				sql = "update ipro_index set sblsh=?,status='3',msg=? where SEQID = ?";
				i = DbHelper.update(sql, new Object[]{sblsh,msg,seqid},conn);				
			}else if("400".equals(code)){
				sql = "update ipro_index set sblsh=?,status='4',msg=? where SEQID = ?";
				i = DbHelper.update(sql, new Object[]{sblsh,msg,seqid},conn);				
			}else if("401".equals(code)){
				sql = "update ipro_index set sblsh=?,status='5',msg=? where SEQID = ?";
				i = DbHelper.update(sql, new Object[]{sblsh,msg,seqid},conn);	
			}else{
				i = DbHelper.update(sql, new Object[]{sblsh,seqid},conn);				
			}
			if( i>0 ){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("");
			}else{
				ds.setState(StateType.FAILT);
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}finally{
			if(conn != null)
				try {
					conn.setAutoCommit(true);
				}catch (SQLException e) {
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}
		return ds;
	}
	/**
	 * 更新投资项目在线审批 报告上传 状态
	 * @param pSet
	 * @return
	 */
	public DataSet updateBuildReport(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String reportid = (String)pSet.get("reportid");
			String status = (String)pSet.get("status");
			int i=0;
			String sql = "update ipro_report t set status=? where REPORTID = ?";			
			i = DbHelper.update(sql, new Object[]{status,reportid},conn);				
			if( i>0 ){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("");
			}else{
				ds.setState(StateType.FAILT);
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}finally{
			if(conn != null)
				try {
					conn.setAutoCommit(true);
				}catch (SQLException e) {
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}
		return ds;
	}
	/**
	 * 投资项目在线审批--单体事项状态更新接口
	 * @param pSet
	 * @return
	 */
	public DataSet setReturn(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		Timestamp d = null;
		Timestamp pretreatmenttime = null;
		try {
			String state = (String) pSet.get("tag");
			String time = (String) pSet.get("pretreatmenttime");
			String opinion = (String) pSet.get("opinion");
			String receiveNum = (String) pSet.get("receiveNum");
			Object bzclqd = pSet.get("material");// 需要补齐的材料清单
			Object correctionTimes = pSet.get("correctionTimes");// 补齐时限
			//Object correctionMaterial = pSet.get("correctionMaterial");// 窗口补齐后的材料列表
			String sql = "";
			String id = Tools.getUUID32();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTime = sdf.format(new Date());
	        
			if (StringUtil.isNotEmpty(time)) {
				pretreatmenttime = CommonUtils_api.getInstance().parseStringToTimestamp(time,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			}
			d = CommonUtils_api.getInstance().parseStringToTimestamp(startTime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			sql = "update ipro_business_index t set t.state=? where receiveNum=?";
			int i = DbHelper.update(sql,new Object[] {state,receiveNum},conn);
			if(i>0){
				sql = "insert into ipro_business_process t (ID,SBLSH,STATE,CONTENT,OPINION,TIME,CORRECTEDTIMELIMIT,pretreatmenttime) " +
				"values (?,?,?,?,?,?,?,?)";
				i = DbHelper.update(sql,new Object[] {id,receiveNum,state,bzclqd,opinion,d,correctionTimes,pretreatmenttime},conn);
				if(i>0){
					ds.setState(StateType.SUCCESS);
					ds.setMessage("OK");
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage("状态更新失败！");
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("状态更新失败！");
			}			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	/**
	 * 非上班时间网上预约 pan
	 * 
	 */
	public DataSet wsyyinsert(ParameterSet pSet){
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet ds = new DataSet();
		try{
			ds = WriteDao.getInstance().wsyyinsert(pSet);
			if(ds.getState()==1){
				WriteCmd.class.newInstance().sendMessageToUser(pSet);
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
		}finally{
			if(conn != null)
				try {
					conn.setAutoCommit(true);
				}catch (SQLException e) {
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}		
		return ds;
	}
	/**
	 * 删除咨询投诉
	 * transactid  咨询投诉id
	 * platform 1咨询2投诉
	 */
	public JSONObject delGuestbookById(ParameterSet pSet){
		JSONObject jo = new JSONObject();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String transactid = (String)pSet.get("transactid");
			String platform = (String)pSet.get("platform");						
			String sql = "delete from guestbook t where t.id = ? and t.type=?";
			int i = DbHelper.update(sql, new Object[]{
					transactid,
					"1".equals(platform)?"2":"3"
				},conn);
			if( i>0 ){
				jo.put("status", "0");
			}else{
				jo.put("status", "1001");
			}
		}catch(Exception e){
			jo.put("status", "1001");
		}finally{
			if(conn != null)
				try {
					conn.setAutoCommit(true);
				}catch (SQLException e) {
					jo.put("status", "1001");
				}
			DbHelper.closeConnection(conn);
		}
		return jo;
	}
}