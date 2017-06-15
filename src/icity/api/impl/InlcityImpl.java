package api.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jdo.conf.Database;

import app.icity.ServiceCmd;
import app.icity.guestbook.WriteDao;
import app.icity.onlineapply.ApplyDao;
import app.util.RestUtil;

import com.commnetsoft.proxy.SsoClient;
import com.commnetsoft.proxy.model.CallResult;
import com.commnetsoft.proxy.model.UserInfo;
import com.commnetsoft.proxy.model.ValidationResult;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.timevale.SecureUtils;

import core.util.CommonUtils;
import core.util.HttpClientUtil;

public class InlcityImpl extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(InlcityImpl.class);
	/**
	 * 在线办理分两类，一类是只通过is_onlie判断，另一类是通过深度判断	
	 * 通过深度判断的分支列表
	 * 通过深度判断 根据代码看还有沈阳和平区，辽阳 但都说没设置	
	 */
	private static List<String> type_conduct_depth_list = new ArrayList();
	
	public static InlcityImpl getInstance(){
		type_conduct_depth_list.add("weihai");//威海
		type_conduct_depth_list.add("dezhou");//德州
		type_conduct_depth_list.add("dy");//东营
		type_conduct_depth_list.add("huaihua");//怀化
		type_conduct_depth_list.add("liaocheng");//聊城
		type_conduct_depth_list.add("linyi");//临沂
		type_conduct_depth_list.add("weifang");//潍坊
		type_conduct_depth_list.add("yt");//烟台
		type_conduct_depth_list.add("zaozhuang");//枣庄
		type_conduct_depth_list.add("zb");//淄博
		return DaoFactory.getDao(InlcityImpl.class.getName());
	}
	//region 获取事项列表（分页）getMattersList
	/**
	 * 获取事项列表（分页）
	 * @param pSet
	 *            add 属性 IS_COLLECTION 是否收藏
	 * @return 
	 */
	public DataSet getMattersList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet ds_matterslist;
		Command cmd = new Command("app.icity.govservice.GovProjectCmd");
		cmd.setParameter("region_code", (String) pSet.get("region_code"));
		cmd.setParameter("page", (String) pSet.get("page"));
		cmd.setParameter("limit", (String) pSet.get("limit"));
		cmd.setParameter("SearchName", (String) pSet.get("SearchName"));
		cmd.setParameter("CAT", (String) pSet.get("cat"));// theme dept
		cmd.setParameter(
				"ID",
				"dept".equals((String) pSet.get("cat")) ? (String) pSet
						.get("dept_id") : (String) pSet.get("title_name"));
		cmd.setParameter("PAGEMODEL", (String) pSet.get("pagemodel"));// person
																		// ent
																		// bmfw
		cmd.setParameter("SUIT_ONLINE", (String) pSet.get("online"));// 1在线办理的
		cmd.setParameter("TYPE", (String) pSet.get("type"));// 事项类型 XK BM 等
		ds_matterslist = cmd.execute("getMattersList");
		try {
			if (ds_matterslist.getState() == StateType.SUCCESS
					&& ds_matterslist.getTotal() > 0) {
				ds.setTotal(ds_matterslist.getTotal());
				ds.setState(ds_matterslist.getState());
				ds.setMessage(ds_matterslist.getMessage());
				JSONArray data = ds_matterslist.getJAData();
				int size = data.size();
				String sxids = "";
				StringBuffer sxidsBuffer = new StringBuffer();
				for (int i = 0; i < size; i++) {
					data.getJSONObject(i).put("ITEM_CODE",data.getJSONObject(i).containsKey("CODE") ?data.getJSONObject(i).getString("CODE"):"");
					data.getJSONObject(i).put("ITEM_ID",data.getJSONObject(i).containsKey("ITEM_ID") ?data.getJSONObject(i).getString("ITEM_ID"):"");
					data.getJSONObject(i).put("ITEM_NAME",data.getJSONObject(i).containsKey("NAME") ?data.getJSONObject(i).getString("NAME"):"");
					data.getJSONObject(i).put("IS_COLLECTION", "0");//初始化为未收藏
					data.getJSONObject(i).put("IS_ONLINE_ICITY","0");//初始化为不可在线办理
					//个性化 是否可在线办理 根据不同的设置条件进行调整
					String appId = SecurityConfig.getString("AppId");//AppId
					//在线办理分两类，一类是只通过is_onlie判断，另一类是通过深度判断					
					//IS_ONLINE_ICITY = 1 可在线办理  =0 不可在线办理
					if(type_conduct_depth_list.contains(appId)){
						if("1,2".contains(data.getJSONObject(i).containsKey("CONDUCT_DEPTH") ?data.getJSONObject(i).getString("CONDUCT_DEPTH"):"0")){
							data.getJSONObject(i).put("IS_ONLINE_ICITY","1");
						}else{
							data.getJSONObject(i).put("IS_ONLINE_ICITY","0");
						}
					}else{
						data.getJSONObject(i).put("IS_ONLINE_ICITY",data.getJSONObject(i).containsKey("IS_ONLINE") ?data.getJSONObject(i).getString("IS_ONLINE"):"0");
					}
					sxidsBuffer
							.append("'"
									+ data.getJSONObject(i)
											.getString("ITEM_ID") + "',");
				}
				sxids = sxidsBuffer.toString();
				sxids = sxids.substring(0, sxids.length() - 1);
				String ucid = (String) pSet.get("ucid");
				if (!"".equals(ucid)) {
					String sql = "SELECT PF.sxid FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? and PF.sxid in ("
							+ sxids + ")";
					DataSet ds_collection = DbHelper.query(sql,
							new Object[] { ucid }, conn);
					if (ds_collection.getTotal() > 0) {
						int len = ds_collection.getTotal();
						for (int i = 0; i < len; i++) {
							for (int j = 0; j < size; j++) {
								if (data.getJSONObject(j)
										.getString("ITEM_ID")
										.equals(ds_collection.getJAData()
												.getJSONObject(i)
												.getString("SXID"))) {
									data.getJSONObject(j).put(
											"IS_COLLECTION", "1");
								}
							}
						}
					}					
				}
				ds.setRawData(data.toString());
			} else {
				System.out.print("获取事项列表失败，或者事项列表为空");
				ds.setRawData(new JSONArray());
				ds.setState(ds_matterslist.getState());
				ds.setMessage(ds_matterslist.getMessage());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ds.setRawData(new JSONArray());
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.toString());
		}finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 根据事项itemCode或者itemId获取事项的基本信息getPermissionAll
	/**
	 *  根据事项itemCode或者itemId获取事项的基本信息
	 * @param pSet
	 * add 属性  IS_COLLECTION 是否收藏 1已收藏0未收藏
	 * @return
	 * 爱城市调用
	 */
	public DataSet getPermissionAll(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		HttpClientUtil client = new HttpClientUtil();
		JSONObject data = new JSONObject();
		try{
			JSONObject item = new JSONObject();
			if(pSet.containsKey("itemCode")){
				String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemInfoByItemCode?itemCode=" + pSet.get("itemCode"));
				item = JSONObject.fromObject(client.getResult(url, ""));
			}else{
				String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")	+ "/getAllItemInfoByItemID?itemId=" + pSet.get("itemId"));
				item = JSONObject.fromObject(client.getResult(url, ""));
			}
			JSONObject itemBasicInfo = item.getJSONArray("ItemInfo").getJSONObject(0);// 基本信息
			itemBasicInfo.put("ITEM_NAME", itemBasicInfo.getString("NAME"));
			itemBasicInfo.put("ITEM_CODE", itemBasicInfo.getString("CODE"));
			itemBasicInfo.put("ITEM_ID", itemBasicInfo.getString("ID"));
			itemBasicInfo.put("IS_COLLECTION", "0");//是否已收藏。初始化为未收藏
			itemBasicInfo.put("IS_ONLINE_ICITY","0");//初始化为不可在线办理
			/**
			 * 判断是否可在线办理
			 */
			String appId = SecurityConfig.getString("AppId");//AppId
			JSONArray onlineconduct_ja = item.containsKey("onlineconduct")?item.getJSONArray("onlineconduct"):new JSONArray();
			JSONObject onlineconduct_1 = new JSONObject();////1 网上申报的深度设置
			for(int i=0;i<onlineconduct_ja.size();i++){
				if("1".equals(onlineconduct_ja.getJSONObject(i).getString("ONLINE_TYPE"))){//1 网上申报
					onlineconduct_1 = onlineconduct_ja.getJSONObject(i);
				}
			}
			//IS_ONLINE_ICITY = 1 可在线办理  =0 不可在线办理
			if(type_conduct_depth_list.contains(appId)){
				if("1,2".contains(onlineconduct_1.containsKey("CONDUCT_DEPTH") ?onlineconduct_1.getString("CONDUCT_DEPTH"):"0")){
					itemBasicInfo.put("IS_ONLINE_ICITY","1");
				}else{
					itemBasicInfo.put("IS_ONLINE_ICITY","0");
				}
			}else{
				itemBasicInfo.put("IS_ONLINE_ICITY",itemBasicInfo.containsKey("IS_ONLINE") ?itemBasicInfo.getString("IS_ONLINE"):"0");
			}
			String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemBasicInfo.getString("ID"));
			JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
			JSONObject formSettingInfo=form.getJSONObject("info");
			if(!formSettingInfo.isNullObject()){
				String formid=formSettingInfo.containsKey("formId")?formSettingInfo.getString("formId"):"";
				String formName=formSettingInfo.containsKey("formName")?formSettingInfo.getString("formName"):"";
				itemBasicInfo.put("FORM_ID", formid);//表单id
				itemBasicInfo.put("FORM_NAME", formName);//表单名称
				itemBasicInfo.put("OBJECTTYPE", formSettingInfo.containsKey("objectType")?formSettingInfo.getString("objectType"):"");//服务类型1人员2项目3企业
			}
			String ucid = (String)pSet.get("ucid");
			if (!"".equals(ucid)) {				
				String sql = "SELECT 1 FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? ";
				DataSet ds_collection = DbHelper.query(sql,new Object[] { ucid }, conn);
				if(ds_collection.getTotal()>0){
					itemBasicInfo.put("IS_COLLECTION", "1");
				}				
			}
			data.put("itemBasicInfo",itemBasicInfo);//事项基本信息
			data.put("materials",item.containsKey("material")?item.getJSONArray("material"):new JSONArray());// 申请材料			
			data.put("legalbasis",item.containsKey("legalbasis")?item.getJSONArray("legalbasis"):new JSONArray());// 法律依据	legalbasis
			data.put("handlingprocess",item.containsKey("handlingprocess")?item.getJSONArray("handlingprocess"):new JSONArray());// 办理流程	handlingprocess
			data.put("charge",item.containsKey("charge")?item.getJSONArray("charge"):new JSONArray());// 事项收费	charge
			data.put("condition",item.containsKey("condition")?item.getJSONArray("condition"):new JSONArray());// 受理条件	condition
			data.put("window",item.containsKey("window")?item.getJSONArray("window"):new JSONArray());// 办理地址	window
			data.put("onlineconduct",item.containsKey("onlineconduct")?item.getJSONArray("onlineconduct"):new JSONArray());// 办理深度
			data.put("outmap",item.containsKey("outmap")?item.getJSONArray("outmap"):new JSONArray());// 外部流程图	outmap

			ds.setRawData(data.toString());
			ds.setTotal(1);		
		}catch(Exception e){
			e.printStackTrace();
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
		}finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 获取咨询投诉列表iCityGuestBookListByPage
	/**
	 * 爱城市网获取咨询投诉列表 type(2咨询，3投诉)
	 * @throws ParseException 
	 */
	public DataSet iCityGuestBookListByPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String uid = (String) pSet.get("uid");
			String type = (String) pSet.get("type");
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			int start = (page-1)*limit;
			String sql = "select id,username,phone,email,title,content,region_id,depart_name,depart_id," +
					"sxmc,sxbm,sxid,type,user_id,ly,WRITE_DATE,status,replayer,deal_result,deal_date " +
					"from GUESTBOOK t where user_id=? and type=?";
			ds = DbHelper.query(sql, start, limit, new Object[]{uid,type}, conn,
					"icityDataSource");
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();			
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion 

	//region 提交咨询投诉iCityAddGuestBook
	/**
	 * 爱城市网提交咨询投诉 type(2咨询，3投诉)，
	 * @throws ParseException 
	 */
	public DataSet iCityAddGuestBook(ParameterSet pSet){
		String ID = Tools.getUUID32();
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String username	= pSet.containsKey("username")?(String)pSet.get("username"):"";//咨询投诉人
			String user_id= pSet.containsKey("user_id")?(String)pSet.get("user_id"):"";//咨询人用户id
			if("".equals(username)||"".equals(user_id)){
				ds.setState(StateType.FAILT);
				ds.setMessage("用户信息不可为空！");
				return ds;
			}
			String phone= pSet.containsKey("phone")?(String)pSet.get("phone"):"";//电话
			String email= pSet.containsKey("email")?(String)pSet.get("email"):"";//邮箱
			String title= pSet.containsKey("title")?(String)pSet.get("title"):"";//主题
			if("".equals(title)){
				ds.setState(StateType.FAILT);
				ds.setMessage("标题不可为空！");
				return ds;
			}
			String content= pSet.containsKey("content")?(String)pSet.get("content"):"";//内容
			if("".equals(content)){
				ds.setState(StateType.FAILT);
				ds.setMessage("内容不可为空！");
				return ds;
			}
			String region_id= pSet.containsKey("region_id")?(String)pSet.get("region_id"):"";//区划编码
			String depart_name= pSet.containsKey("depart_name")?(String)pSet.get("depart_name"):"";//部门名称
			String depart_id= pSet.containsKey("depart_id")?(String)pSet.get("depart_id"):"";//部门id
			if("".equals(depart_name)||"".equals(depart_id)){
				ds.setState(StateType.FAILT);
				ds.setMessage("部门不可为空！");
				return ds;
			}
			String sxmc= pSet.containsKey("sxmc")?(String)pSet.get("sxmc"):"";//事项名称			
			String sxbm= pSet.containsKey("sxbm")?(String)pSet.get("sxbm"):"";//事项编码
			String sxid= pSet.containsKey("sxid")?(String)pSet.get("sxid"):"";//事项id
			if("".equals(sxmc)||"".equals(sxbm)||"".equals(sxid)){
				ds.setState(StateType.FAILT);
				ds.setMessage("事项不可为空！");
				return ds;
			}
			String type= pSet.containsKey("type")?(String)pSet.get("type"):"";//咨询2  投诉3
			if("".equals(type)){
				ds.setState(StateType.FAILT);
				ds.setMessage("类别不可为空！");
				return ds;
			}			
			String ly= pSet.containsKey("ly")?(String)pSet.get("ly"):"";//来源
			if("".equals(ly)){
				ds.setState(StateType.FAILT);
				ds.setMessage("来源不可为空！");
				return ds;
			}
			String sql = "insert into GUESTBOOK t (id,username,phone,email,title,content,region_id," +
					"depart_name,depart_id,sxmc,sxbm,sxid,type,user_id,ly,WRITE_DATE) values " +
					"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdata)";
			int k = DbHelper.update(sql, new Object[]{
					ID,username,phone,email,title,content,region_id,depart_name,depart_id,sxmc,sxbm,sxid,type,user_id,ly
			}, conn);
			if (k > 0) {
				ds.setState(StateType.SUCCESS);
				JSONObject j_id = new JSONObject();
				j_id.put("ID", ID);
				ds.setRawData(j_id.toString());
				ds.setMessage("");
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("");
			}
			conn.commit();
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
	//endregion
	
	//region app版本更新 appVersion
	public DataSet appVersion(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "select VERSION,BUILD,URL,INSTRUCTIONS from CUST_VERSION where id='1'";
			ds = DbHelper.query(sql, new Object[] {}, conn);
			if (ds.getTotal() != 1) {
				ds.setState(StateType.FAILT);
				ds.setMessage("获取版本更新失败！");
				ds.setRawData(new JSONArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
}
