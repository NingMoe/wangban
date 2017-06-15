/**  
 * @Title: PowerBaseInfoQZCmd.java 
 * @Package app.icity.sync 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-9-23 上午10:54:21 
 * @version V1.0  
 */ 
package app.icity.sync;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import app.uc.GetUserMapDao;
import app.util.RestUtil;
import core.util.HttpClientUtil;

/** 
 * @ClassName: PowerBaseInfoQZCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-9-23 上午10:54:21  
 */

public class PowerBaseInfoQZCmd extends BaseQueryCommand {
	
//	public DataSet CheckSyncPowerBaseInfo(ParameterSet pSet){
//		return PowerBaseInfoQZDao.getInstance().CheckSyncPowerBaseInfo(pSet);
//	}
//	
//	public DataSet DelInsSyncData(ParameterSet pSet){
//		return PowerBaseInfoQZDao.getInstance().DelInsSyncData(pSet);
//	}
//	
//	public DataSet DeleteSyncData(ParameterSet pSet){
//		return PowerBaseInfoQZDao.getInstance().DeleteSyncData(pSet);
//	}
//	
//	public DataSet DeleteData(ParameterSet pSet){
//		return PowerBaseInfoQZDao.getInstance().DeleteData(pSet);
//	}
//	
//	public DataSet PublishPreviewDataRest(ParameterSet pSet){
//		return PowerBaseInfoQZDao.getInstance().PublishPreviewDataRest(pSet);
//	}
//	
//	public DataSet deleteGabige(ParameterSet pSet){
//		return PowerBaseInfoQZDao.getInstance().deleteGabige(pSet);
//	}
//	
//	public DataSet updatebaseinfo(ParameterSet pSet){
//		return PowerBaseInfoSyncDao.getInstance().updatebaseinfo(pSet);
//	}
//	public DataSet selectbusiness_log(ParameterSet pSet){
//		return PowerBaseInfoSyncDao.getInstance().selectbusiness_log(pSet);
//	}
	public DataSet submit(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
		try{
					String o_sblsh=PowerBaseInfoQZDao.getInstance().saveData(data);
					ds.setRawData(o_sblsh);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("保存成功！");
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("保存失败submit！");
			return ds;
		}
		return ds;
		
	}
	
	/**
	 * 在线办理省里传数据接口调用
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet usesubmitSL(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String action = (String) pSet.get("action");
		JSONObject data =(JSONObject)pSet.get("data");
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("SubmitUrl"));
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data.toString());
			map.put("action",action);
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			if (json.getString("state").equals("1")) {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("提交成功！");
			}else {
				ds.setRawData(json);
				ds.setState(StateType.FAILT);
				ds.setMessage("提交失败submitSL！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败submitSL！");
			return ds;
		}
		return ds;
		
	}
	
	/**
	 * 资料上传至省资料库接口调用
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet setBusinessAttach(ParameterSet pSet) {
		DataSet ds = new DataSet();
		
		JSONObject data =new JSONObject();
		try {
			String uid = (String) pSet.get("UID");
			String uuid=GetUserMapDao.getInstance().GetMapid(uid).getRecord(0).getString("USER_ID_MAP");
			data.put("type", pSet.get("TYPE"));
			data.put("state", pSet.get("STATE"));
			data.put("uuid", uuid);
			data.put("uid", uid);
			data.put("WebRegion", pSet.get("WebRegion"));
			data.put("obj", JSONObject.fromObject(pSet.get("obj")));
			
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("BusinessAttachUrl")+"/setBusinessAttach");
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data.toString());
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			if (json.getString("state").equals("1")) {
				ds.setRawData(json.getString("tyId"));
				ds.setState(StateType.SUCCESS);
				ds.setMessage("资料提交成功！");
			}else {
				ds.setRawData(json);
				ds.setState(StateType.FAILT);
				ds.setMessage("资料提交失败setBusinessAttach！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("资料提交失败setBusinessAttach！");
			return ds;
		}
		return ds;
	}
	/**
	 * 查询省级资料库获取当前用户下的资料接口调用
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		
		JSONObject data =new JSONObject();
		try {
			String uid = (String) pSet.get("ucid");
			String uuid=GetUserMapDao.getInstance().GetMapid(uid).getRecord(0).getString("USER_ID_MAP");
			String start = pSet.get("start").toString();
			String limit = (String) pSet.get("limit").toString();
			data.put("title", pSet.get("title"));
			data.put("start", start);
			data.put("uuid", uuid);
			data.put("limit", limit);
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("BusinessAttachUrl")+"/getAttachList");
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data.toString());
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			if (json.getString("state").equals("1")) {
				ds.setTotal(json.getJSONArray("js").size());
				ds.setRawData(json.getJSONArray("js"));
				ds.setState(StateType.SUCCESS);
				ds.setMessage("资料查询成功！");
			}else {
				ds.setRawData(json);
				ds.setState(StateType.FAILT);
				ds.setMessage("资料查询失败getAttachList！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("资料查询失败getAttachList！");
			return ds;
		}
		return ds;
	}
	/**
	 * 查询省级资料库根据上传文件获取与其相似文件接口调用
	 * 
	 * @param pSet
	 * @return
	 */
	public JSONObject getSimilarFile(ParameterSet pSet) {
		JSONObject data =new JSONObject();
		JSONObject json = new JSONObject();
		try {
			String uid = (String)pSet.get("UID");
			String uuid=GetUserMapDao.getInstance().GetMapid(uid).getRecord(0).getString("USER_ID_MAP");
			String name = (String)pSet.get("name");			
			data.put("name", name);
			data.put("uuid", uuid);
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("BusinessAttachUrl")+"/getSimilarFile");
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data.toString());
			Object ret = RestUtil.postData(url, map);
			json = JSONObject.fromObject(ret);
			
		} catch(Exception e){
			e.printStackTrace();
			json.put("state", "0");
			json.put("message", "资料查询失败getSimilarFile！:"+e.getMessage());
		}
		
		return json;
	}
	/**
	 * 
	 * 根据统一id获取省资料库信息接口调用
	 * @param pSet
	 * @return
	 */
	public JSONObject getFileByTyid(String tyId) {
		JSONObject data =new JSONObject();
		JSONObject json = new JSONObject();
		try {
			
			data.put("tyId", tyId);
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("BusinessAttachUrl")+"/getFileByTyid");
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data.toString());
			Object ret = RestUtil.postData(url, map);
			json = JSONObject.fromObject(ret);
		} catch(Exception e){
			e.printStackTrace();
			json.put("state", "0");
			json.put("message", "资料查询失败getFileByTyid！:"+e.getMessage());
		}
		
		return json;
	}
	
	/**
	 * 
	 * 根据统一id省资料库记录本地存放信息接口调用
	 * @param pSet
	 * @return
	 */
	public JSONObject setWebRegions(ParameterSet pSet) {
		JSONObject data =new JSONObject();
		JSONObject json = new JSONObject();
		try {
			data.put("WebRegion", pSet.get("WebRegion"));
			data.put("tyId", pSet.get("tyId"));
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("BusinessAttachUrl")+"/setWebRegions");
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data.toString());
			Object ret = RestUtil.postData(url, map);
			json = JSONObject.fromObject(ret);
		} catch(Exception e){
			e.printStackTrace();
			json.put("state", "0");
			json.put("message", "资料查询失败setWebRegions！:"+e.getMessage());
		}
		
		return json;
	}
	
	
}
