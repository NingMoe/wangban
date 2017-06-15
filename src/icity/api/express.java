package api;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;

import app.icity.onlineapply.ApplyDao;
import app.util.RestUtil;

import com.icore.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;
/**
 * 
 * @description：ems数据交互接口
 * @author: Administrator
 * @date： 2016-11-10  下午10:19:59
 * @version 1.0
 */
@RestType(name = "api.express", descript = "ems数据交互接口")
public class express  extends BaseQueryCommand{
    private static Log log = LogFactory.getLog(express.class);
	/**
	 * 上门取件状态反馈接口（威海：由EMS方主动发起）
	 * @param pset
	 * @return
	 */
	public DataSet accessStateReturn(ParameterSet pset){
		/**
			String sign = (String)pset.getParameter("sign");
			String timestamp = (String)pset.getParameter("timestamp");
			String partner_id = (String)pset.getParameter("partner_id");
			String desc = (String)pset.getParameter("desc");
			String authorization = (String)pset.getParameter("authorization");
			String app_key = (String)pset.getParameter("app_key");
			String method = (String)pset.getParameter("method");
			String format = (String)pset.getParameter("format");
			String version = (String)pset.getParameter("version");
		 */
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String status = (String)pset.getParameter("status");
		String txLogisticID = (String)pset.getParameter("txLogisticID");
		String mailNum = (String)pset.getParameter("mailNum");
		try {
			String sql = "UPDATE BUSINESS_EXPRESS SET EXPRESS_ID=?,SEND_DATE=?,TAKESTATE=? WHERE ORDER_NUMBER=?";
			int i = DbHelper.update(sql, new Object[]{
						mailNum,
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
						status,txLogisticID
						}, conn);
			if(i>0){
				ds.setMessage("更新成功");
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setMessage("更新失败");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setMessage(e.getMessage());
			ds.setState(StateType.FAILT);
			log.error(e.getMessage());
		}
		try{
			String sql = "SELECT SBLSH FROM BUSINESS_EXPRESS T WHERE ORDER_NUMBER = ?";
			DataSet dss = DbHelper.query(sql, new Object[]{txLogisticID
					}, conn);
			if(dss.getTotal()>0){
				Map<String,String> map = new HashMap<String,String>();
				map.put("receiveNumber", dss.getJOData().getString("SBLSH"));
				map.put("emstoNo", mailNum);
				String url = SecurityConfig.getString("approval_url")+"/emstotysp";
				RestUtil.postData(url, map);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return ds;
	}
	/**
	 * 政务服务中心向申请者寄件，寄件单号由审批同步到网办（威海：由审批发起）
	 * @param pset（申办编号和EMS单号）
	 * @return
	 */
	public DataSet approvalExpressIdReturn(ParameterSet pset){
		DataSet ds = new DataSet();
		try {
			String sblsh = (String)pset.getParameter("receiveNumber");//申办流水号
			String expressId = (String)pset.getParameter("expressId");//ems单号
			Connection conn = DbHelper.getConnection("icityDataSource");
			String sql = "UPDATE BUSINESS_EXPRESS SET EXPRESSID_RETURN=? WHERE SBLSH=?";
			int i = DbHelper.update(sql, new Object[]{expressId,sblsh}, conn);
			if(i>0){
				ds.setMessage("更新成功");
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setMessage("更新失败");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setMessage(e.getMessage());
			ds.setState(StateType.FAILT);
			log.error(e.getMessage());
		}
		return ds;
	}
	/***
	 * 为审批提供查询邮寄件物流信息（威海、福州）
	 * （调用ems接口）
	 * @param pSet
	 * @return
	 */
	public DataSet changeLogisticsDetailToApproval(ParameterSet pSet){
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
			log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}
	
	/**
	 * 上门取件状态反馈接口（福州：由EMS方主动发起）
	 * @param pset
	 * @return
	 */
	public DataSet accessStateReturn4Fz(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String status = (String)pset.getParameter("status");
		String txLogisticID = (String)pset.getParameter("txLogisticID");
		String mailNum = (String)pset.getParameter("mailNum");
		
		String sql = "SELECT SBLSH FROM BUSINESS_EXPRESS T WHERE ORDER_NUMBER = ?";
		DataSet dss = DbHelper.query(sql, new Object[]{txLogisticID}, conn);
		if(dss.getTotal()>0){
			try {
				sql = "UPDATE BUSINESS_EXPRESS SET EXPRESS_ID=?,SEND_DATE=?,TAKESTATE=? WHERE ORDER_NUMBER=?";
				int i = DbHelper.update(sql, new Object[]{
							mailNum,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							status,
							txLogisticID
							}, conn);
				if(i>0){
					ds.setMessage("S");
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setMessage("F");
					ds.setState(StateType.FAILT);
				}
			} catch (Exception e) {
				ds.setMessage(e.getMessage());
				ds.setState(StateType.FAILT);
			}
		}else{
			ds.setMessage("F");
			ds.setState(StateType.FAILT);
		}
		
		if("S".equals(ds.getMessage())){
			try{
				String url = SecurityConfig.getString("approval_url")+"/updateExpressNum?expressNum="+mailNum+"&orderNum="+txLogisticID;
				RestUtil.getData(url);
			}catch(Exception e){
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
		return ds;
	}
	
	/**
	 * 回寄结果材料快递单号反馈：由审批同步到网办（舟山：由审批发起）
	 * @param pset（申办编号和EMS单号）
	 * @return
	 */
	public DataSet updateMailNumReturn(ParameterSet pset){
		DataSet ds = new DataSet();
		try {
			String sblsh = (String)pset.getParameter("receiveNum");//申办流水号
			String expressId = (String)pset.getParameter("mailNum");//ems单号
			Connection conn = DbHelper.getConnection("icityDataSource");
			String sql = "UPDATE BUSINESS_EXPRESS SET EXPRESSID_RETURN=? WHERE SBLSH=?";
			int i = DbHelper.update(sql, new Object[]{expressId,sblsh}, conn);
			if(i>0){
				ds.setMessage("更新成功");
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setMessage("更新失败");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setMessage(e.getMessage());
			ds.setState(StateType.FAILT);
			log.error(e.getMessage());
		}
		return ds;
	}
}
