package fz.app.icity.engineering;

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
	/**
	 *  提交申报信息到接口  * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitReportDetails(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ECParallelUrl") + "submitReportDetails");
		JSONObject ob = new JSONObject();	
		ob.put("projectCode", (String) pSet.get("projectCode"));
		ob.put("flowItemId", (String) pSet.get("flowItemId"));
		ob.put("resource", pSet.get("resource").toString());
		String businessId = (String)pSet.get("businessId");
		try {
			pSet.setParameter("status", "0");
			pSet.setParameter("bizId", "");
			pSet.setParameter("content", ob);
			DataSet m_ds = new DataSet();
			if(businessId==null){
				m_ds = saveParallel_business_index(pSet);
			}else{
				m_ds.setState(StateType.SUCCESS);
			} 
			if (m_ds.getState() == 1) {
				Object obj = RestUtil.postJson(url, ob);
				JSONObject receive = JSONObject.fromObject(URLDecoder.decode((String) obj,"UTF-8"));
				if ("200".equals(receive.get("retCode"))) {
					pSet.setParameter("status", "3");
					pSet.setParameter("bizId", receive.get("bizId"));
					updateParallel_business_index(pSet);
				} else {
					pSet.setParameter("status", "-2");
					updateParallel_business_index(pSet);
					ds.setState(StateType.FAILT);
					ds.setMessage((String) receive.get("error"));
				}
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("提交成功，写入数据库失败");
			}			
		} catch (Exception e) {
			System.out.print("工程建设 提交申报信息到接口:app.icity.engineering.EngineeringDao.submitReportDetails");
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败");
		}
		return ds;
	}
	/**
	 * 查询业务数据
	 * @param pSet
	 * @return
	 */
	public DataSet selectParallel_business_index(ParameterSet pSet){
		DataSet ds = new DataSet();
		return ds;
	}
	/**
	 * 保存业务数据
	 * @param pSet
	 * ID	VARCHAR2(50)	N			
	   BIZ_ID	VARCHAR2(50)	N			业务主体标识编号
	   SUBMIT_TIME	DATE	Y			提交时间
	   UPDATE_TIME	DATE	Y			状态更新时间
	   ITEM_ID	VARCHAR2(50)	Y			事项ID
	   ITEM_NAME	VARCHAR2(500)	Y			节点事项名称
	   REGION_CODE	VARCHAR2(50)	Y			区划代码
	   REGION_NAME	VARCHAR2(12)	Y			区划名称
	   PROJECTCODE	VARCHAR2(50)	Y			项目编码
	   PROJECTNAME	VARCHAR2(200)	Y			项目名称
	   CONTENT	CLOB	Y			提交数据
	   STATUS	VARCHAR2(10)	Y			状态 -2提交失败 -1不流转 0暂存 1办结 2在办 3已提交
	   ITEM_CODE	VARCHAR2(50)	Y			事项编码
	 * @return
	 */
	public DataSet saveParallel_business_index(ParameterSet pSet){
		DataSet ds = new DataSet();
		Timestamp d =null;
		try{
			String id = Tools.getUUID32();
			String bizId = (String)pSet.get("bizId");
			d = CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS);// 申请时间
			String ITEM_ID = (String) pSet.get("flowItemId");
			String ITEM_NAME = (String) pSet.get("flowItemName");
			String REGION_CODE = SecurityConfig.getString("WebRegion");
			String REGION_NAME = SecurityConfig.getString("AppCity");
			String PROJECTCODE = (String) pSet.get("projectCode");
			String PROJECTNAME = (String) pSet.get("projectName");
			String CONTENT = (String) pSet.get("content");
			String STATUS = (String) pSet.get("status");
			String ITEM_CODE = "";
			String sql = "insert into PARALLEL_BUSINESS_INDEX (ID,BIZ_ID,SUBMIT_TIME,UPDATE_TIME,ITEM_ID,ITEM_NAME," +
					"REGION_CODE,REGION_NAME,PROJECTCODE,PROJECTNAME,CONTENT,STATUS,ITEM_CODE) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";			
			int i = this.executeUpdate(sql,new Object[]{id,bizId,d,d,ITEM_ID,ITEM_NAME,REGION_CODE,REGION_NAME,
					PROJECTCODE,PROJECTNAME,CONTENT,STATUS,ITEM_CODE});
			if(i>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("业务信息插入成功！");
				ds.setRawData(new JSONArray());
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("业务信息插入失败！");
				ds.setRawData(new JSONArray());
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("业务信息插入失败！");
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	/**
	 * 更新业务数据
	 * @param pSet
	 * @return
	 */
	public DataSet updateParallel_business_index(ParameterSet pSet){
		DataSet ds = new DataSet();
		Timestamp d =null;
		try{
			d = CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS);// 申请时间
			String PROJECTCODE = (String) pSet.get("projectCode");
			String STATUS = (String) pSet.get("status");
			String sql = "update PARALLEL_BUSINESS_INDEX t set t.STATUS=?,t.UPDATE_TIME=? where t.REGION_CODE=?";			
			int i = this.executeUpdate(sql,new Object[]{STATUS,d,PROJECTCODE});
			if(i>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("业务信息更新成功！");
				ds.setRawData(new JSONArray());
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("业务信息更新失败！");
				ds.setRawData(new JSONArray());
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("业务信息更新失败！");
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
}
