package jxtzxm.app.icity.ipro;

import io.netty.util.CharsetUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.common.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.icore.util.Tools;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class IproDao extends BaseJdbcDao {

	private IproDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static IproDao getInstance() {
		return DaoFactory.getDao(IproDao.class.getName());
	}
	/**
	 * 根据目录编码获取事项列表江西
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByPermitCodeJX(ParameterSet pSet) {
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
		JSONArray gcjd = itemDir.getJSONArray("GCJD");
		int len = gcjd.size();
		JSONArray itemList = item.getJSONArray("itemList");
		int m_len = itemList.size();
		JSONArray m_gcjd = new JSONArray();
		for(int i=0;i<len;i++){
			JSONObject m_item = gcjd.getJSONObject(i);
			JSONArray m_itemList = new JSONArray();
			for(int j=0;j<m_len;j++){
				if(m_item.getString("CODE").equals(itemList.getJSONObject(j).getString("PROJECT_STAGE"))){
					m_itemList.add(itemList.getJSONObject(j));
				}
			}
			m_item.put("itemlist", m_itemList);
			m_gcjd.add(m_item);
		}
		JSONObject itemInfo = itemDir.getJSONArray("info").getJSONObject(0);
		item.accumulate("info", itemInfo);
		item.put("itemList",m_gcjd);
		ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
		return ds;
	}	
	/**
	 * 江西投资项目提交
	 */
	public DataSet saveInvestInfoJX(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		// 过滤金额
		String investment = baseInfo.getString("investment");
		boolean strResult = investment.matches("[0-9]+.?[0-9]*");
		if (!(strResult)) {
			System.out.println(investment + "：不是数字");
			String value1 = investment.replaceAll("[\u4E00-\u9FA5]", "");
			String value2 = value1.replaceAll("[a-zA-Z]", "");
			String value3 = value2.replaceAll("[ \t]", "");
			String value = value3.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]", "");
			baseInfo.put("investment", value);
			System.out.println("过滤后：" + value);
		}
		// 过滤tab字符
		/*String baseInfos = baseInfo.toString();
		if (baseInfos.contains("\\t") || baseInfos.contains(" ")) {
			String projectName = baseInfo.getString("projectName");
			String lerepCertno = baseInfo.getString("lerepCertno");
			String projectContent = baseInfo.getString("projectContent");
			String linkMan = baseInfo.getString("linkMan");
			String linkPhone = baseInfo.getString("linkPhone");
			String contactEmail = baseInfo.getString("contactEmail");
			String deAreaName = baseInfo.getString("deAreaName");
			projectName = projectName.replaceAll("[ \t]", "");
			lerepCertno = lerepCertno.replaceAll("[ \t]", "");
			projectContent = projectContent.replaceAll("[ \t]", "");
			linkMan = linkMan.replaceAll("[ \t]", "");
			linkPhone = linkPhone.replaceAll("[ \t]", "");
			contactEmail = contactEmail.replaceAll("[ \t]", "");
			deAreaName = deAreaName.replaceAll("[ \t]", "");
			System.out.println("申办的项目：'" + baseInfo.getString("projectName") + "'中有tab字符");
			baseInfo.put("projectName", projectName);
			baseInfo.put("lerepCertno", lerepCertno);
			baseInfo.put("projectContent", projectContent);
			baseInfo.put("linkMan", linkMan);
			baseInfo.put("linkPhone", linkPhone);
			baseInfo.put("contactEmail", contactEmail);
			baseInfo.put("deAreaName", deAreaName);
		}*/		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/saveInvestInfo");
		Map<String, String> data=new HashMap<String, String>();
		try {
			data.put("baseInfo", baseInfo.toString());
			// //编码一层
			System.err.println("提交baseInfo表单：" + baseInfo);
			String oldseqid = (String) pSet.getParameter("oldseqid");
			if (!(oldseqid == null || "".equals(oldseqid))) {				
				data.put("oldInvestId", (String) pSet.getParameter("oldInvestId"));
				data.put("oldSeqId", (String) pSet.getParameter("oldseqid"));
			}
			String type = (String) pSet.getParameter("type");
			if (!(type == null || "".equals(type))) {				
				data.put("type", (String) pSet.getParameter("type"));
			}
			data.put("region_code", (String) pSet.getParameter("region_code"));
			data.put("region_name", (String) pSet.getParameter("region_name"));
			JSONObject obj = new JSONObject();
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			System.err.println("返回json串：" + obj);
			pSet.put("retValue", obj);
			if("200".equals(obj.getString("code"))){
				if (!StringUtils.isEmpty(oldseqid)) {
					updateData(pSet);
					ds.setState(StateType.SUCCESS);
				} else {
					if (submitDataJX(pSet)) {
						ds.setState(StateType.SUCCESS);
					} else {
						ds.setState(StateType.FAILT);
					}
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage(obj.getString("error"));
			}			
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 插入数据库
	 * @param pSet
	 * @return
	 * @throws Exception
	 */
	public Boolean submitDataJX(ParameterSet pSet) throws Exception {
		Boolean l = true;
		JSONObject retValue = (JSONObject) pSet.getParameter("retValue");
		String SXBM = (String) pSet.getParameter("SXBM");
		String SXMC = (String) pSet.getParameter("SXMC");
		String region_code = (String) pSet.getParameter("region_code");
		String region_name = (String) pSet.getParameter("region_name");
		JSONObject userInfo = (JSONObject) pSet.getParameter("userInfo");
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			int i=0;
			sql = "insert into ipro_index(foreignAbroadFlag,version,INVESTID,status,sxbm, sxmc, uuid, ucname, region_code, region_name, SBSJ, content, projectname, seqid) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			i = DbHelper.update(sql,
					new Object[] {baseInfo.getString("foreignAbroadFlag"),"1",retValue.getString("investId"),"1",SXBM, SXMC, userInfo.get("uuid"), userInfo.get("name"),
							region_code, region_name,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							baseInfo.toString(), baseInfo.getString("projectName"), retValue.getString("seqId") },
					conn);
			if(i>0){
				l = true;
			}else{
				l = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			l = false;
		}

		return l;
	}
	
	public Boolean updateData(ParameterSet pSet) throws Exception {
		Boolean l = true;
		JSONObject retValue = (JSONObject) pSet.getParameter("retValue");
		String seqid = (String) pSet.getParameter("oldseqid");
		String newInvestId = retValue.getString("investId");
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			int i=0;
			String type = (String) pSet.getParameter("type");
			if (!(type == null || "".equals(type))) {				
				if("Update".equals(type)){
					sql = "update ipro_index set CHANGECONTENT=?,status=?,changeseqid=?,changeinvestid=? " + "where seqid=?";
					i = DbHelper.update(sql,
							new Object[] { baseInfo.toString(),(String) pSet.getParameter("status"),retValue.getString("seqId"),newInvestId,seqid },conn);
				}else{
					sql = "update ipro_index set INVESTID=?,SBSJ=?,content=?,projectname=?,seqid=? " + "where seqid=?";
					i = DbHelper.update(sql,
							new Object[] { newInvestId,
									CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
											CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
									baseInfo.toString(), baseInfo.getString("projectName"), retValue.getString("seqId"),
									seqid },
							conn);
				}
			}		
			if(i>0){
				l = true;
			}else{
				l = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			l = false;
		}
		return l;
	}
	/**
	 * 作废
	 * @param pSet
	 * @return
	 */
	public DataSet voidInvestInfo(ParameterSet pSet){
		DataSet ds = new DataSet();
		String projectCode = (String) pSet.get("sblsh");		
		String reason = (String) pSet.get("reason");	
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/voidInvestInfo");
		Map<String, String> data=new HashMap<String, String>();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			data.put("projectCode", projectCode);
			data.put("reason", reason);
			JSONObject obj = JSONObject.fromObject(RestUtil.postData(url, data));
			System.err.println("返回json串：" + obj);			
			if("200".equals(obj.getString("code"))){
				String sql = "update ipro_index set STATUS='6' " + "where sblsh=?";
				int i = DbHelper.update(sql,
						new Object[] {projectCode},conn);
				if(i>0){
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage(obj.getString("error"));
			}			
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 根据项目代码获得项目登记信息
	 */
	public DataSet getInvestInfoByCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String projectCode = (String) pSet.getParameter("projectCode");
		String appCode = SecurityConfig.getString("ipro_appCode");
		String salt = SecurityConfig.getString("ipro_salt");
		String token = DigestUtils.md5Hex(projectCode+ salt);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestInfoByCode?projectCode="+projectCode+"&appCode="+appCode+"&token="+token);
		try {
			JSONObject project = JSONObject.fromObject(RestUtil.getData(url));
			if("200".equals(project.getString("state"))){
				JSONObject pt = project.getJSONObject("project");
				ds.setState(StateType.SUCCESS);
				ds.setRawData(pt);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("处理数据失败getProjectCount！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getProjectCount！");
			return ds;
		}
		return ds;
	}
	/**
	 * 根据虚拟事项ID 获得虚拟事项下的真实事项
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByVirtualCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getItemListByVirtualCode");
		String region_code = (String) pSet.getParameter("region_code");	
		String id = (String) pSet.getParameter("id");	
		url += "?virtualCode=" + id+"&divisionCode="+region_code;
		try {
			Object obj = RestUtil.getData(url);
			JSONObject jo = JSONObject.fromObject(obj);
			if("200".equals(jo.getString("state"))){
				JSONArray ja = JSONObject.fromObject(obj).getJSONArray("itemList");
				ds.setRawData(ja);
			}else{
				ds.setRawData(new JSONArray());
			}			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getItemListByVirtualCode！");
			return ds;
		}
		return ds;
	}
	
	/**
	 * 全省申报项目动态
	 */
	public DataSet getProjectCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String countYear = (String) pSet.getParameter("countYear");
		String appCode = SecurityConfig.getString("ipro_appCode");
		String salt = SecurityConfig.getString("ipro_salt");
		String token = DigestUtils.md5Hex(appCode+ salt);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getProjectCount?countYear="+countYear+"&appCode="+appCode+"&token="+token);
		try {
			JSONObject projectCount = JSONObject.fromObject(RestUtil.getData(url));
			//String a = "{\"code\":\"200\",\"list\":[{\"BA\":0,\"GRADE\":\"2\",\"HZ\":2,\"INVESTMENT\":68993.98,\"NUMS\":3,\"PARENT_CODE\":\"0\",\"REGION_CODE\":\"360000000000\",\"REGION_NAME\":\"江西省\",\"SP\":1},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":2,\"INVESTMENT\":68993.98,\"NUMS\":3,\"REGION_CODE\":\"360000000000\",\"REGION_NAME\":\"江西省级项目\",\"SP\":1},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360100000000\",\"REGION_NAME\":\"南昌市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":110,\"REGION_CODE\":\"360200000000\",\"REGION_NAME\":\"景德镇市\",\"SP\":10},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360300000000\",\"REGION_NAME\":\"萍乡市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360400000000\",\"REGION_NAME\":\"九江市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360500000000\",\"REGION_NAME\":\"新余市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360600000000\",\"REGION_NAME\":\"鹰潭市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360700000000\",\"REGION_NAME\":\"赣州市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360800000000\",\"REGION_NAME\":\"吉安市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"360900000000\",\"REGION_NAME\":\"宜春市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"361000000000\",\"REGION_NAME\":\"抚州市\",\"SP\":0},{\"BA\":0,\"GRADE\":\"3\",\"HZ\":0,\"INVESTMENT\":0,\"NUMS\":0,\"REGION_CODE\":\"361100000000\",\"REGION_NAME\":\"上饶市\",\"SP\":0}],\"state\":1,\"total\":13}";
			//JSONObject projectCount = JSONObject.fromObject(a);
			if("200".equals(projectCount.getString("code"))){
				JSONArray pt = projectCount.getJSONArray("list");
				ds.setState(StateType.SUCCESS);
				ds.setRawData(pt);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("处理数据失败getProjectCount！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getProjectCount！");
			return ds;
		}
		return ds;
	}
	/**
	 * 全省申报项目行业分布
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectTopFive(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String countYear = (String) pSet.getParameter("countYear");
		String regionCode = (String) pSet.getParameter("regionCode");		
		String appCode = SecurityConfig.getString("ipro_appCode");
		String salt = SecurityConfig.getString("ipro_salt");
		String token = DigestUtils.md5Hex(appCode+ salt);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getProjectTopFive?countYear="+countYear+"&regionCode="+regionCode+"&appCode="+appCode+"&token="+token);
		try {
			JSONObject projectCount = JSONObject.fromObject(RestUtil.getData(url));
			//String a = "{\"code\":\"200\",\"list\":[{\"COUNTNUM\":43,\"ID\":\"A\",\"NAME\":\"A 农、林、牧、渔业\",\"PERCENT\":\"72.88%\",\"TOTALNUM\":59},{\"COUNTNUM\":4,\"ID\":\"A\",\"NAME\":\"A 农、林、牧、渔业\",\"PERCENT\":\"6.78%\",\"TOTALNUM\":59},{\"COUNTNUM\":2,\"ID\":\"B\",\"NAME\":\"B 采矿业\",\"PERCENT\":\"3.39%\",\"TOTALNUM\":59},{\"COUNTNUM\":1,\"ID\":\"L\",\"NAME\":\"L 租赁和商业服务业\",\"PERCENT\":\"1.69%\",\"TOTALNUM\":59},{\"COUNTNUM\":1,\"ID\":\"O\",\"NAME\":\"O 居民服务、修理和其他服务业\",\"PERCENT\":\"1.69%\",\"TOTALNUM\":59},{\"COUNTNUM\":8,\"ID\":\"other\",\"NAME\":\"其他\",\"PERCENT\":\"13.56%\",\"TOTALNUM\":59}],\"state\":1}";
			//JSONObject projectCount = JSONObject.fromObject(a);
			if("200".equals(projectCount.getString("code"))){
				JSONArray pt = projectCount.getJSONArray("list");
				ds.setState(StateType.SUCCESS);
				ds.setRawData(pt);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("处理数据失败getProjectTopFive！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getProjectTopFive！");
			return ds;
		}
		return ds;
	}	
	/**
	 * 获取FormId
	 * @param pSet
	 * @return
	 */
	public DataSet getFormInfo(ParameterSet pSet){
		DataSet ds = new DataSet();
		String itemId = (String) pSet.getParameter("itemId");
		String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url")+"/getFormInfo?itemId="+itemId);
	    urlForm = urlForm.replace("web/investApproval","web/approval");
		try {
			Object itemInfo = RestUtil.getData(urlForm);
			JSONObject item = JSONObject.fromObject(itemInfo);
			if("200".equals(item.getString("state"))){
				ds.setState(StateType.SUCCESS);
				ds.setRawData(item.getJSONObject("info"));
				ds.setTotal(1);
			}else{
				ds.setState(StateType.FAILT);
				ds.setRawData(new JSONArray());
				ds.setMessage("获取表单信息失败！");
			}			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("获取表单信息失败！");
			return ds;
		}
		return ds;
	}
	/**
	 * 获取审批核准备案类的目录信息
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectDirectoryList(ParameterSet pSet) {
		DataSet m_ds = new DataSet();
		JSONObject projectlist = new JSONObject();		
		JSONArray projectsp = new JSONArray();
		JSONArray projecthz = new JSONArray();
		JSONArray projectba = new JSONArray();
		try{
			String region_code = (String) pSet.getParameter("region_code");
			JSONArray TZSPXMHYFL = JSONArray.fromObject(pSet.getParameter("TZSPXMHYFL"));
			JSONArray TZSPXMLX = JSONArray.fromObject(pSet.getParameter("TZSPXMLX"));
			ParameterSet m_pset = new ParameterSet();
			int length = TZSPXMLX.size();
			int m_length = TZSPXMHYFL.size();
			for(int i=0;i<length;i++){
				if(TZSPXMLX.getJSONObject(i).getString("NAME").contains("核准")){
					for(int j=0;j<m_length;j++){
						if("".equals(TZSPXMHYFL.getJSONObject(j).getString("PARENT_CODE"))){
							m_pset.put("region_code", region_code);
							m_pset.put("industryCode", TZSPXMHYFL.getJSONObject(j).getString("CODE"));
							m_pset.put("projectType", TZSPXMLX.getJSONObject(i).getString("CODE"));
							DataSet ds = app.icity.ipro.IproDao.getInstance().getProjectDirectory(m_pset);
							JSONObject m_list = new JSONObject();
							m_list.put("region_code", region_code);
							m_list.put("industryCode", TZSPXMHYFL.getJSONObject(j).getString("CODE"));
							m_list.put("industryName", TZSPXMHYFL.getJSONObject(j).getString("NAME"));
							m_list.put("projectType", TZSPXMLX.getJSONObject(i).getString("CODE"));
							m_list.put("projectName", TZSPXMLX.getJSONObject(i).getString("NAME"));
							m_list.put("list", ds.getJOData().getJSONArray("list"));
							projecthz.add(m_list);
						}					
					}
				}else{
					m_pset.put("industryCode", "");
					m_pset.put("region_code", region_code);
					m_pset.put("projectType", TZSPXMLX.getJSONObject(i).getString("CODE"));
					DataSet ds = app.icity.ipro.IproDao.getInstance().getProjectDirectory(m_pset);
					JSONObject m_list = new JSONObject();
					m_list.put("region_code", region_code);
					m_list.put("projectType", TZSPXMLX.getJSONObject(i).getString("CODE"));
					m_list.put("projectName", TZSPXMLX.getJSONObject(i).getString("NAME"));
					m_list.put("list", ds.getJOData().getJSONArray("list"));
					if(TZSPXMLX.getJSONObject(i).getString("NAME").contains("审批")){
						projectsp.add(m_list);
					}else if(TZSPXMLX.getJSONObject(i).getString("NAME").contains("备案")){
						projectba.add(m_list);
					}
				}			
			}
			projectlist.put("sp", projectsp);
			projectlist.put("hz", projecthz);
			projectlist.put("ba", projectba);			
			m_ds.setRawData(projectlist);
			m_ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			m_ds.setState(StateType.FAILT);
		}
		return m_ds;		
	}
	/**
	 * 单体事项在线办理，存库
	 * @param pSet
	 * @return
	 */
	public DataSet saveIproBusiness(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String ITEMID = (String) pSet.getParameter("ITEMID");
		String ITEMNAME = (String) pSet.getParameter("ITEMNAME");
		String PROJECTNAME = (String) pSet.getParameter("PROJECTNAME");
		String PROJECTID = (String) pSet.getParameter("PROJECTID");
		String USERNAME = (String) pSet.getParameter("USERNAME");
		String UCID = pSet.getParameter("UCID").toString();
		String STATE = (String) pSet.getParameter("STATE");
		String CONTENT = pSet.getParameter("CONTENT").toString();
		String FORMID = (String) pSet.getParameter("FORMID");
		String DATAID = (String) pSet.getParameter("DATAID");
		String STEP = (String) pSet.getParameter("STEP");
		String ID = (String) pSet.getParameter("ID");
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			int i=0;
			sql = "select id from ipro_business_index where id = ?";
			DataSet set = DbHelper.query(sql, new Object[]{ID}, conn);
			i = set.getTotal();
			if(i==0){
				sql = "insert into ipro_business_index t (id,itemId,itemName,projectid,projectname,username," +
						"ucid,state,content,formid,dataid,step,ctime) values (?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
				i = DbHelper.update(sql,
						new Object[] {Tools.getUUID32(),ITEMID,ITEMNAME,PROJECTID,PROJECTNAME,USERNAME,UCID
						,STATE,CONTENT,FORMID,DATAID,STEP},
						conn);
			}else{
				sql = "update ipro_business_index t set t.state=?,t.content=?,t.formid=?,t.dataid=?,t.step=?,t.ctime=sysdate where t.id=?";
				i = DbHelper.update(sql,
						new Object[] {STATE,CONTENT,FORMID,DATAID,STEP,ID},
						conn);
			}
			if(i>0){
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			return ds;
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ds;
	}
	/**
	 * 单体事项在线办理，推送
	 * @param pSet
	 * @return
	 */
	public DataSet accept(ParameterSet pSet){		
		DataSet ds = new DataSet();
		Map<String,String> map = new HashMap<String,String>();
		map.put("postdata",pSet.getParameter("subdata").toString());
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url")+"/accept");
	    url = url.replace("web/investApproval","web/approval");
	    try {
			JSONObject obj = JSONObject.fromObject(RestUtil.postData(url, map));
			if("200".equals(obj.get("state"))){
				Connection conn = DbHelper.getConnection(this.getDataSourceName());
				String sql = "update ipro_business_index t set state=?,receivenum=?,ctime=sysdate where id=?";
				DbHelper.update(sql,new Object[] {"11",obj.getString("receiveNum"),pSet.getParameter("ID")},conn);
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	
	/**
	 * 办件公示
	 * @param pSet
	 * @return
	 */
	public DataSet getDisplayList(ParameterSet pSet) {
		DataSet ds = new DataSet();//web/investApproval/getDisplayList
		String count = (String) pSet.getParameter("count");
		String region = (String) pSet.getParameter("region");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getDisplayList?count="+count+"&region="+region);
		try {
			JSONObject projectCount = JSONObject.fromObject(RestUtil.getData(url));
			//String a = "{\"data\":[{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000},{\"APPLY_SUBJECT\":\"关于让222015年11月23日123产业政策意见的业务\",\"ITEM_NAME\":\"市级项目填报\",\"ORG_NAME\":\"济南市发展和改革委员会\",\"PROJECT_CODE\":\"2015-370100-01-01-000042\",\"PROJECT_NAME\":\"2015年11月23日123\",\"RECEIVE_NUMBER\":\"37010037160902001315\",\"SEQ_ID\":\"BE2B7017A8084C40A542C7F5AD309F9D\",\"STATE\":\"准予许可\",\"SUBMIT_TIME\":1472801586000,\"TIME_LIMIT\":1473696000000}],\"error\":\"\",\"state\":\"200\"}";
			//JSONObject projectCount = JSONObject.fromObject(a);
			if("200".equals(projectCount.getString("state"))){
				JSONArray pt = projectCount.getJSONArray("data");
				ds.setState(StateType.SUCCESS);
				ds.setTotal(pt.size());
				ds.setRawData(pt);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("处理数据失败getDisplayList！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getDisplayList！");
			return ds;
		}
		return ds;
	}
	/**
	 * 公开公示分页
	 * @param pSet
	 * @return
	 */
	public DataSet getDisplayListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String page = (String) pSet.getParameter("page");
		String rows = (String) pSet.getParameter("rows");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getDisplayListByPage");
		Map<String, String> map=new HashMap<String, String>();
		map.put("page", page);
		map.put("rows", rows);
		//map.put("projectName", projectName);
		try {
			Object s = RestUtil.postData(url,map);
			JSONObject project = JSONObject.fromObject(RestUtil.postData(url,map));
			if("200".equals(project.getString("code"))){
				//"totalPage":45,"totlaRow":89
				JSONArray pageList = project.getJSONArray("pageList");
				ds.setState(StateType.SUCCESS);
				ds.setTotal(project.getInt("totlaRow"));
				ds.setRawData(pageList.toString());
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("处理数据失败getDisplayListByPage！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getDisplayListByPage！");
			return ds;
		}
		return ds;
	}
	/**
	 * 获取项目报告
	 * @param pSet
	 * @return
	 */
	public DataSet getIproReport(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			String uuid = (String) pSet.getParameter("uuid");
			String projectCode = (String)pSet.getParameter("projectCode");
			String sqlEx = "select * from ipro_report t where t.PROJECTCODE=?";
			try {
				ds = DbHelper.query(sqlEx, new Object[] { projectCode }, conn);
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("getIproReport查询错误");
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("getIproReport查询错误");
		}
		return ds;
	}	
	/**
	 * 项目报告上传
	 * @param pSet
	 * @return
	 */
	public DataSet buildReport(ParameterSet pSet){
		DataSet ds = new DataSet();
		String reportType = (String) pSet.getParameter("reportType");
		String reportTypeName = (String) pSet.getParameter("reportTypeName");
		String seqId = (String) pSet.getParameter("seqId");	
		String reportYear = (String) pSet.getParameter("reportYear");	
		String projectCode = (String) pSet.getParameter("projectCode");	
		String projectName = (String) pSet.getParameter("projectName");	
		String remark = (String) pSet.getParameter("remark");	
		String metail = (String) pSet.getParameter("metail");
		String ucid = (String) pSet.getParameter("ucid");
		JSONObject postdata = new  JSONObject();
		postdata.put("reportType", reportType);
		postdata.put("seqId", seqId);
		postdata.put("reportYear", reportYear);
		postdata.put("projectCode", projectCode);
		postdata.put("remark", remark);
		postdata.put("metail", metail);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/buildReport");
		Map<String, String> map=new HashMap<String, String>();
		map.put("postdata", postdata.toString());
		try {
			Timestamp nowTimestamp = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS);
			String sql = "";
			Connection conn = DbHelper.getConnection(this.getDataSourceName());
			try {
				String id = Tools.getUUID32();
				sql = "insert into ipro_report t (ID,PROJECTCODE,PROJECTSTATUSID,PROJECTSTATUSNAME," +
						"NODENAME,METAIL,INSERTTIME,UUID,NODEID,PROJECTNAME,REPORTTYPE,REPORTTYPENAME," +
						"STATUS,REPORTYEAR,REMARK,REPORTID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				int i = DbHelper.update(sql,
						new Object[] {id,projectCode,"","","",metail,nowTimestamp,ucid,
						"",projectName,reportType,reportTypeName,"0",reportYear,remark,""},
						conn);
				if(i>0){
					JSONObject result = JSONObject.fromObject(RestUtil.postData(url,map));
					if("200".equals(result.getString("code"))){						
						String reportId = result.getString("reportId");
						sql = "update ipro_report t set REPORTID=? where id = ?";			
						i = DbHelper.update(sql, new Object[]{reportId,id},conn);
						if(i>0){
							ds.setState(StateType.SUCCESS);
							ds.setRawData("{\"reportId\":"+reportId+"}");
						}else{
							ds.setState(StateType.FAILT);
							ds.setMessage("处理数据失败buildReport！");
						}
					}else{
						ds.setState(StateType.FAILT);
						ds.setMessage("处理数据失败buildReport！");
					}
				}else{
					ds.setState(StateType.FAILT);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				return ds;
			} finally {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败buildReport！");
		}
		return ds;
	}
	/**
	 * 项目进度查询
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectSchedule(ParameterSet pSet){
		DataSet ds = new DataSet();
		String projectCode = (String) pSet.getParameter("projectCode");
		String appCode = SecurityConfig.getString("ipro_appCode");
		String salt = SecurityConfig.getString("ipro_salt");
		String token = DigestUtils.md5Hex(projectCode+ salt);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getProjectSchedule");
		Map<String, String> map=new HashMap<String, String>();
		map.put("projectCode", projectCode);
		map.put("appCode", appCode);
		map.put("token", token);
		try {
			JSONObject result = JSONObject.fromObject(RestUtil.postData(url,map));
			if("200".equals(result.getString("code"))){				
				ds.setState(StateType.SUCCESS);
				ds.setRawData(result.toString());
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("处理数据失败getProjectTopFive！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getProjectTopFive！");
			return ds;
		}
		return ds;
	}
	/**
	 * 我的项目列表1
	 * @param pSet
	 * @return
	 */
	public DataSet getIproindexList(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
			ds = getIproindexListSql(pSet);
			if(ds.getTotal()>0){
				for(int i=0;i<ds.getJAData().size();i++){
					String seqId = ds.getJAData().getJSONObject(i).getString("SEQID");	
					String investId = ds.getJAData().getJSONObject(i).getString("INVESTID");
					String sblsh = ds.getJAData().getJSONObject(i).getString("SBLSH");
					ParameterSet m_pSet = new ParameterSet();
					m_pSet.put("seqId", seqId);
					DataSet m_ds = getInvestInfoByCodeForOut(m_pSet);
					if(m_ds.getState()==StateType.SUCCESS){
						JSONObject obj = m_ds.getJOData();
						if("200".equals(obj.getString("code"))&&"0".equals(obj.getString("shStatus"))){
							String version = obj.getJSONObject("baseInfo").getString("version");
							//更新状态 status:状态(00:待预审,97:作废,98:驳回,99:审核通过)
							//状态0暂存1已提交2赋码成功3赋码失败4驳回5作废6发起作废申请7发起变更申请
							StringBuilder sqlbuilder = new StringBuilder();
							sqlbuilder.append("update ipro_index t set t.sblsh=?,t.STATUS=?");
							String status = "1";
							if("97".equals(obj.getString("status"))){
								status="5";
							}else if("98".equals(obj.getString("status"))){
								status="4";
							}else if("99".equals(obj.getString("status"))){
								status="2";
							}
							int flag = 0;
							JSONObject m_project = obj.getJSONObject("baseInfo");
							if(!version.equals(ds.getJAData().getJSONObject(i).getString("VERSION"))){								
								//不相等就整体更新
								sqlbuilder.append(",t.CONTENT=?,t.VERSION=?,t.INVESTID=?,t.SEQID=?,t.PROJECTNAME=? ");
								sqlbuilder.append(" where t.sblsh=? ");
								flag = DbHelper.update(sqlbuilder.toString(), new Object[]{
									m_project.getString("projectCode"),
									status,
									m_project.toString(),
									m_project.getString("version"),
									m_project.containsKey("investId")?m_project.getString("investId"):"",
									m_project.getString("seqId"),
									m_project.getString("projectName"),
									sblsh
								}, conn);								
							}else{
								sqlbuilder.append(" where t.seqid=? ");
								flag = DbHelper.update(sqlbuilder.toString(), new Object[]{m_project.getString("projectCode"),status,seqId}, conn);
							}
						}
					}					
				}
				ds = getIproindexListSql(pSet);
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}finally{			
			try{
				conn.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}	
		return ds;
	}
	/**
	 * 我的项目列表1-1
	 * @param pSet
	 * @return
	 */
	public DataSet getIproindexListSql(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		try{
			int start = (Integer)pSet.getPageStart();
			int limit = (Integer)pSet.getPageLimit();
			String sql = "select sblsh,sbsj,projectname,seqid,project_type,status,INVESTID,VERSION,changeinvestid,changeseqid from ipro_index t where 1=1 ";
			//处理时间查询条件
			String CREATE_TIME_s = (String) pSet.remove("t.sbsj@>@Date");
			String CREATE_TIME_e = (String) pSet.remove("t.sbsj@<@Date");
			List<Object> param=new ArrayList<Object>();
			Timestamp d =null;
			if(StringUtil.isNotEmpty(CREATE_TIME_s)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(CREATE_TIME_s,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.sbsj >= ?";
			}
			if(StringUtil.isNotEmpty(CREATE_TIME_e)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(CREATE_TIME_e,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.sbsj <= ?";
			}
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			sql+=" order by sbsj desc, seqId asc";
			if(limit==-1||start==-1){
				ds = this.executeDataset(sql,param.toArray());
			}else{
				ds = this.executeDataset(sql, start, limit,param.toArray());
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	/**
	 * 投资项目 项目信息获取（网办使用）//1-2
	 * @param pSet
	 * @return
	 */
	public DataSet getInvestInfoByCodeForOut(ParameterSet pSet){
		DataSet ds = new DataSet();
		String seqId = (String) pSet.get("seqId");	
		String appCode = SecurityConfig.getString("ipro_appCode");
		String salt = SecurityConfig.getString("ipro_salt");
		String token = DigestUtils.md5Hex(seqId+ salt);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestInfoByCodeForOut?projectCode=&seqId="+seqId+"&appCode="+appCode+"&token="+token);
		try {			
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			System.err.println("返回json串：" + obj);			
			if("1".equals(obj.getString("state"))&&"200".equals(obj.getString("code"))){
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage(obj.getString("error"));
			}			
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 投资项目 调接口获取业务信息
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectInfoByIds(ParameterSet pSet){
		DataSet ds = new DataSet();
		String seqId = (String) pSet.get("seqId");	
		String investId = (String) pSet.get("investId");		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getProjectInfoByIds?seqId="+seqId+"&investId="+investId);
		try {			
			JSONObject obj = JSONObject.fromObject(RestUtil.getData(url));
			System.err.println("返回json串：" + obj);			
			if("1".equals(obj.getString("state"))&&"200".equals(obj.getString("code"))){
				ds.setRawData(obj);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage(obj.getString("error"));
			}			
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 我的项目详情seqid
	 * @param pSet
	 * @return
	 */
	public DataSet getIproInfoBySeqid(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("seqid");
		String uuid = (String) pSet.getParameter("uid");
		String sql = "select * from ipro_index where seqid = ? and uuid=?";
		ds = DbHelper.query(sql,new Object[] {seqid,uuid}, conn);
		return ds;
	}
	/**
	 * 我的项目详情SBLSH
	 * @param pSet
	 * @return
	 */
	public DataSet getIproIndexByProjectCode(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String sblsh = (String) pSet.getParameter("sblsh");
		String sql = "select * from ipro_index where sblsh = ?";
		ds = DbHelper.query(sql,new Object[] {sblsh}, conn);
		return ds;
	}
	/**
	 * 通过ID查询投资办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getIproDetailByID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = "select id,itemid,itemname,projectname,projectid,formid,dataid,content,to_char(ctime,'yyyy-mm-dd') ctime,step,receivenum from ipro_business_index where id=?";
		try {
			ds = this.executeDataset(sql,new Object[]{pSet.getParameter("ID")});
			ds.setRawData(ds.getJAData());
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 办件信息列表
	 * @param pSet
	 * @return
	 */
	public DataSet getIproBusinessList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try{
			int start = (Integer)pSet.getPageStart();
			int limit = (Integer)pSet.getPageLimit();
			String sql = "select id,itemid,itemname,state,projectname,projectid,formid,dataid,ctime,step,receivenum from ipro_business_index t where 1=1 ";
			//处理时间查询条件
			String apply_time_s = (String) pSet.remove("t.ctime@>@Date");
			String apply_time_e = (String) pSet.remove("t.ctime@<@Date");
			List<Object> param=new ArrayList<Object>();
			Timestamp d =null;
			if(StringUtil.isNotEmpty(apply_time_s)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(apply_time_s,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.ctime >= ?";
			}
			if(StringUtil.isNotEmpty(apply_time_e)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(apply_time_e,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				param.add(d);
				sql +=" AND t.ctime <= ?";
			}		
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			sql+=" order by ctime desc";
			if(limit==-1||start==-1){
				ds = this.executeDataset(sql,param.toArray());
			}else{
				ds = this.executeDataset(sql, start, limit,param.toArray());
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}		
		return ds;
	}
	/**
	 * 江西投资项目 窗口待办
	 */
	public DataSet getWindowProject(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String projectCode = (String) pSet.getParameter("projectCode");
		String appCode = SecurityConfig.getString("ipro_appCode");
		String salt = SecurityConfig.getString("ipro_salt");
		String token = DigestUtils.md5Hex(projectCode+ salt);
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestInfoByCodeForOut?seqId=&projectCode="+projectCode+"&appCode="+appCode+"&token="+token);
		
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			JSONObject project = JSONObject.fromObject(RestUtil.getData(url));
			if("1".equals(project.getString("state"))&&"200".equals(project.getString("code"))){
				String sql1= "select 1 from ipro_index t where t.sblsh=? and t.uuid=?";
				DataSet ds1 = DbHelper.query(sql1, new Object[]{projectCode,pSet.get("uid")}, conn);
				String applyDate = project.getJSONObject("baseInfo").getString("applyDate");
				Timestamp d =null;
				if(StringUtil.isNotEmpty(applyDate)){
					d = CommonUtils_api.getInstance().parseStringToTimestamp(applyDate,
							CommonUtils_api.YYYY_MM_DD);
				}
				String shStatus = project.containsKey("shStatus")?project.getString("shStatus"):"0";
				String status="2";
				if("UPDATE_1".equals(shStatus)){
					status="6";
				}else if("VOID_1".equals(shStatus)){
					status="7";
				}else if("0".equals(shStatus)){
					status="2";
				}
				if(ds1.getTotal()>0){//更新
					String sql2 = "update ipro_index t set t.sbsj=?,t.CONTENT=?,t.REGION_CODE=?," +
							"t.REGION_NAME=?,t.PROJECTNAME=?,t.SEQID=?,t.PROJECT_TYPE=?,t.STATUS=?,t.VERSION=?," +
							"INVESTID=?,t.FOREIGNABROADFLAG=? where sblsh=?";
					DbHelper.update(sql2, new Object[]{
							d,							
							project.getJSONObject("baseInfo").toString(),
							project.getJSONObject("baseInfo").getString("divisionCode"),
							project.getJSONObject("baseInfo").getString("divisionName"),
							project.getJSONObject("baseInfo").getString("projectName"),
							project.getJSONObject("baseInfo").getString("seqId"),
							project.getJSONObject("baseInfo").getString("projectType"),
							status,
							project.getJSONObject("baseInfo").getString("version"),
							project.containsKey("investId")?project.getString("investId"):"",
							project.getJSONObject("baseInfo").getString("foreignAbroadFlag"),
							project.getString("projectCode")
					}, conn);
				}else{//插入
					String sql2 = "insert into ipro_index t (sbsj,UUID,CONTENT,UCNAME,REGION_CODE,REGION_NAME," +
							"PROJECTNAME,SEQID,PROJECT_TYPE,STATUS,VERSION,INVESTID,FOREIGNABROADFLAG,sblsh) values " +
							"(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					DbHelper.update(sql2, new Object[]{
							d,
							pSet.get("uid"),
							project.getJSONObject("baseInfo").toString(),
							pSet.get("uname"),
							project.getJSONObject("baseInfo").getString("divisionCode"),
							project.getJSONObject("baseInfo").getString("divisionName"),
							project.getJSONObject("baseInfo").getString("projectName"),
							project.getJSONObject("baseInfo").getString("seqId"),
							project.getJSONObject("baseInfo").getString("projectType"),
							status,
							project.getJSONObject("baseInfo").getString("version"),
							project.containsKey("investId")?project.getString("investId"):"",
							project.getJSONObject("baseInfo").getString("foreignAbroadFlag"),							
							project.getString("projectCode")
					}, conn);
				}
				String sql3= "select * from ipro_index t where t.sblsh=? and t.uuid=?";
				ds = DbHelper.query(sql3, new Object[]{projectCode,pSet.get("uid")}, conn);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("查询失败，未查询到该项目！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			return ds;
		}
		return ds;
	}
}
