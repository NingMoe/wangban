package app.icity.ipro;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.util.RestUtil;
import core.util.HttpClientUtil;
import io.netty.util.CharsetUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;

public class IproCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(IproCmd.class);

	/*
	 * 字典
	 */
	public DataSet getInvestDictList(ParameterSet pSet) {
		return IproDao.getInstance().getInvestDictList(pSet);
	}
	public DataSet getXmnb(ParameterSet pSet){
		return  IproDao.getInstance().getXmnb(pSet);
		
	}
	/*
	 * 提交
	 */
	public DataSet saveInvestInfo(ParameterSet pSet) {
		return IproDao.getInstance().saveInvestInfo(pSet);
	}
	/*
	 * 检查项目年报的项目代码
	 */
	public DataSet checkXmnbCode(ParameterSet pSet){
		return IproDao.getInstance().checkXmnbCode(pSet);
	}
	/**
	 * 根据事项code获取事项详情
	 * @param pSet
	 * @return
	 */
	public DataSet getItemInfoByCode(ParameterSet pSet) {
		return IproDao.getInstance().getItemInfoByCode(pSet);
	}
	/**
	 * 根据行业和区划获得目录信息
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectDirectory(ParameterSet pSet) {
		return IproDao.getInstance().getProjectDirectory(pSet);
	}	
	//项目
	public DataSet queryMatters(ParameterSet pSet) {
		return IproDao.getInstance().queryMatters(pSet);
	}
	public DataSet queryByMatterName(ParameterSet pSet) {
		return IproDao.getInstance().queryByMatterName(pSet);
	}
	public DataSet queryByMatterCode(ParameterSet pSet) {
		return IproDao.getInstance().queryByMatterCode(pSet);
	}
	public DataSet delMatter(ParameterSet pSet) {
		return IproDao.getInstance().delMatter(pSet);
	}
	public DataSet detailMatter(ParameterSet pSet) {
		return IproDao.getInstance().detailMatter(pSet);
	}
	//子事项
	public DataSet queryChildMatter(ParameterSet pSet) {
		return IproDao.getInstance().queryChildMatter(pSet);
	}
	public DataSet queryByReceiveNumber(ParameterSet pSet) {
		return IproDao.getInstance().queryByReceiveNumber(pSet);
	}
	public DataSet queryByItemName(ParameterSet pSet) {
		return IproDao.getInstance().queryByItemName(pSet);
	}
	public DataSet queryAllChildMatter(ParameterSet pSet) {
		return IproDao.getInstance().queryAllChildMatter(pSet);
	}
	public DataSet delChildMatter(ParameterSet pSet) {
		return IproDao.getInstance().delChildMatter(pSet);
	}
	public DataSet detailedChildMatter(ParameterSet pSet) {
		return IproDao.getInstance().detailedChildMatter(pSet);
	}
	public DataSet getMattersCount(ParameterSet pSet) {
		return IproDao.getInstance().getMattersCount(pSet);
	}
	public DataSet getMCount(ParameterSet pSet) {
		return IproDao.getInstance().getMCount(pSet);
	}
	public DataSet getNewsMattersCount(ParameterSet pSet) {
		return IproDao.getInstance().getNewsMattersCount(pSet);
	}	
	public DataSet getProjectModify(ParameterSet pSet){
		return IproDao.getInstance().getProjectModify(pSet);
	}
	public DataSet searchsblsh(ParameterSet pSet){
		return IproDao.getInstance().searchsblsh(pSet);
	}
	public String updateorinput(ParameterSet pSet){
		return IproDao.getInstance().updateorinput(pSet);
	}
	public DataSet getItemListByPermitCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String code = (String) pSet.getParameter("code");
		// 目录下的事项列表
		String url = HttpUtil
				.formatUrl(SecurityConfig.getString("ipro_url") + "/getItemListByPermitCode?permitCode=" + code);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
		// 目录基本信息
		String urlDir = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getPermitItemInfo?code=" + code);
		HttpClientUtil clientDir = new HttpClientUtil();
		JSONObject itemDir = JSONObject.fromObject(clientDir.getResult(urlDir, ""));

		JSONObject itemInfo = itemDir.getJSONArray("info").getJSONObject(0);
		item.accumulate("info", itemInfo);
		ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
		return ds;
	}

	public DataSet getUserJDCX(ParameterSet pSet) {
		return IproDao.getInstance().getUserJDCX(pSet);
	}
	
	public DataSet getJD(ParameterSet pSet){
		return IproDao.getInstance().getJD(pSet);
	}

	public DataSet getContent(ParameterSet pSet) {
		return IproDao.getInstance().getContent(pSet);
	}
	public DataSet getCompanyContent(ParameterSet pSet) {
		return IproDao.getInstance().getCompanyContent(pSet);
	}
	public DataSet getNData(ParameterSet pSet) {
		return IproDao.getInstance().getNData(pSet);
	}
	public Boolean dele(ParameterSet pSet) throws Exception {
		return IproDao.getInstance().dele(pSet);
	}
	
	public Boolean deles(ParameterSet pSet) throws Exception {
		return IproDao.getInstance().deles(pSet);
	}

	// 根据代码获取赋码信息
	public DataSet getProjectInfoByIds(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String investId = (String) pSet.getParameter("investId");
		String seqId = (String) pSet.getParameter("seqId");
		// 目录下的事项列表
		String url = HttpUtil.formatUrl(
				SecurityConfig.getString("ipro_url") + "/getProjectInfoByIds?investId=" + investId + "&seqId=" + seqId);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
		System.err.println("获取赋码信息：" + item);
		ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
		return ds;
	}

	// 从bsp接口 根据区划代码获取下级区划列表
	public DataSet getDeptList(ParameterSet pset) {
		DataSet ds = new DataSet();
		String url =
				 HttpUtil.formatUrl(SecurityConfig.getString("synchronousDept")+"/web/organ");
		//String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/getDeptList");
		Map<String, String> data=new HashMap<String, String>();
		data.put("region_code", (String) pset.getParameter("region_code"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			ds.setRawData(obj);
		} catch (Exception e) {
			_log.error("获取部门列表出错！");
		}
		return ds;
	}
	
	// 从bsp接口 根据区划代码获取下级区划列表
	public DataSet getDeptTree(ParameterSet pset) {
		DataSet ds = new DataSet();
		JSONArray regionTree;
		try {
			regionTree = getDeptNode((String) pset.getParameter("region_code"), true);
			ds.setRawData(regionTree);
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("获取部门树出错");
		}
		return ds;
	}
	
	public JSONArray getDeptNode(String regionCode, boolean isParent){
		JSONArray ja = new JSONArray();
		try {
			/*String url =
					 HttpUtil.formatUrl(SecurityConfig.getString("synchronousDept")+"/web/organ");*/
			String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/getDeptList");
			Map<String, String> data=new HashMap<String, String>();
			data.put("region_code", regionCode);
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			JSONArray jalist = obj.getJSONArray("region");
			for(int i=0; i<jalist.size();i++){
				JSONObject node = jalist.getJSONObject(i);
				if ("1".equals(node.getString("TYPE"))) {
					if(regionCode.equals(node.getString("CODE"))){
						if(isParent){
							ja.add(node);
						}
						continue;
					}else{
						ja.add(node);
					}
					if(node.getInt("CHILDS")>1){
						JSONArray nodelist = getDeptNode(node.getString("CODE"), false);
						for(int j=0;j<nodelist.size();j++){
							ja.add(nodelist.get(j));
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("获取部门树出错");
		}
		return ja;
	}
	
	// 从bsp接口 根据区划代码获取当前行政区划内容
	public DataSet getDept(ParameterSet pset) {
		DataSet ds = new DataSet();
		 String url =
				 HttpUtil.formatUrl(SecurityConfig.getString("synchronousDept")+"/web/organ");
		//String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/getDeptList");
		Map<String, String> data=new HashMap<String, String>();
		String region_code = (String) pset.getParameter("region_code");
		data.put("region_code", region_code);
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			System.err.println(obj.getJSONArray("region").toString());
			JSONArray regionList = obj.getJSONArray("region");
			for(int i=0;i<regionList.size();i++){
				if(region_code.equals(regionList.getJSONObject(i).getString("CODE") )){
					ds.setRawData(regionList.getJSONObject(i));
					break;
				}
			}
		} catch (Exception e) {
			_log.error("获取部门列表出错！");
		}
		return ds;
	}
	
	public DataSet saveXMNB(ParameterSet pSet){
		return IproDao.getInstance().saveXMNB(pSet);
	};
	
	public DataSet checkCode(ParameterSet pSet){
		return IproDao.getInstance().checkCode(pSet);
	};
	
	public DataSet checkCode2(ParameterSet pSet){
		return IproDao.getInstance().checkCode(pSet);
	};
	
	public DataSet searchProject(ParameterSet pSet){
		return IproDao.getInstance().searchProject(pSet);
	}
	
	public DataSet searchProjects(ParameterSet pSet){
		return IproDao.getInstance().searchProjects(pSet);
	}
	
	public DataSet searchP(ParameterSet pSet){
		return IproDao.getInstance().searchP(pSet);
	}
}
