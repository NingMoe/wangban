package hlj_qqheNew.action.icity.submitsp;

import hlj_qqheNew.app.icity.project.QueryEcGapXMLJXDao;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;

import sun.security.action.PutAllAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.uc.UserDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.HttpClientUtil;

public class baseinfo extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3018249382226679757L;
	public boolean handler(Map<String,Object> data){
		if("sync".equals(SecurityConfig.getString("PAGE_MODE"))){
			return syncMode(data);
		}
		else{
			return interfaceMode(data);
		}
		
	}
	public boolean syncMode(Map<String, Object> data) {
		String code = this.getParameter("code");
		String sblsh = this.getParameter("sblsh");
		String state = "sp";
		if(!sblsh.equals("")){
			state="bqbz";
		}
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet result = null;
		DataSet resultbase = null;
		DataSet resultsub = null;

		String formid = "";
		String itemid = "";
		String itemname ="";
		String orgCode = "";
		String orgName = "";
		String Assort = "1";
		net.sf.json.JSONArray apply_info;
		String OBJECT_TYPE = "";//办理对象类型 新事项分为 个人 企业 项目  先假设0为个人1为企业2为项目 
		try{
			conn.setAutoCommit(false);
			String sql="select * from POWER_BASE_INFO_OF_FORM t where t.item_code = ?";
			result = DbHelper.query(sql, new Object[]{code}, conn);
			
			String sqlbase="select * from POWER_BASE_INFO t where t.code = ?";
			resultbase = DbHelper.query(sqlbase, new Object[]{code}, conn);
			
			String sql_sub = "select * from sub_for_ex_app_information t where sblsh = ?";
			resultsub = DbHelper.query(sql_sub, new Object[]{sblsh}, conn);
			
			conn.commit();
		}catch(Exception e){
			
		}finally{
			DBSource.closeConnection(conn);
		}
		if (result!=null&&result.getTotal() > 0) {
			formid = result.getRecord(0).getString("FORM_CODE");
		}
		//String apply = "";
		StringBuffer apply = new StringBuffer();
		JSONObject jsonApply= new JSONObject();
		if (resultbase!=null&&resultbase.getTotal() > 0) {
			OBJECT_TYPE = resultbase.getRecord(0).getString("OBJECT_TYPE");
			itemid = resultbase.getRecord(0).getString("ITEM_ID");
			itemname = resultbase.getRecord(0).getString("NAME");
			orgCode = resultbase.getRecord(0).getString("DEPT_ID");
			orgName = resultbase.getRecord(0).getString("ORGNAME");
			Assort = resultbase.getRecord(0).getString("ASSORT");

			apply_info = resultbase.getRecord(0).getJSONObject("CONTENT").getJSONArray("apply_info");
			String[] infos = apply_info.toString().split("\"");
			int len = infos.length;
			/*for(int i=0;i<len;i++){				
				if(i==len-1){
					apply+=infos[i];

				}else{
					apply+=infos[i]+"'";
				}
			}*/
			
			for(int i=0;i<len;i++){				
				if(i==len-1){
					apply.append(infos[i]);
				}else{
					apply.append(infos[i]).append("'");
				}
			}
		}
		String sjm = "";	
		String baseContent = "\"\"";
		if (resultsub!=null&&resultsub.getTotal() > 0) {
			sjm = "&dataId="+resultsub.getRecord(0).getString("DATAID");
			formid =resultsub.getRecord(0).getString("FORMID");
			OBJECT_TYPE = resultsub.getRecord(0).getString("OBJECTTYPE");
			baseContent = resultsub.getRecord(0).getString("BASECONTENT");
		}
		data.put("dataId", sjm); 
		data.put("formid", formid);//表单id
		data.put("code", code);//事项code
		data.put("OBJECT_TYPE", OBJECT_TYPE);//审批类型  个人 企业  项目 
		data.put("apply_info", apply.toString());//所有附件  jsonarray
		data.put("itemid", itemid);//事项id
		data.put("itemname", itemname);//事项名称
		data.put("jsonApply", jsonApply);//附件code name  为了方便给审批提交事项名称
		data.put("orgcode", orgCode);//新事项部门编码
		data.put("orgname", orgName);//新事项部门名称
		data.put("baseContent", baseContent);
		data.put("Assort", Assort);

		data.put("sblsh", sblsh);//新事项部门名称
		data.put("state", state);//新事项部门名称

		return true;
		
	}
	@SuppressWarnings("deprecation")
	public boolean interfaceMode(Map<String, Object> data) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
		JSONArray bqbzmaterials = new JSONArray();
		String flag = this.getParameter("flag");
		String itemId = this.getParameter("itemId");
		String sblsh = this.getParameter("sblsh");
		String bzclqd="";
		String[] Abzclqd={};
		DataSet resultsub = null;
		UserInfo ui=this.getUserInfo();
		String formid = "",dataId="",_dataId="",baseContent = "\"\"";
		String state = "00";
		if(sblsh.length()>0){
			state = "01";
		}else{
			state = "00";
		}
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getAllItemInfoByItemID?itemId="+itemId);
		String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemId);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
		JSONObject item=JSONObject.fromObject(client.getResult(url,""));
		String objectType = "";//办理对象类型 新事项分为 个人 企业 项目  先假设0为个人1为企业2为项目 
			JSONObject formInfo=form.getJSONObject("info");
			if(!formInfo.isNullObject()){
				formid=formInfo.getString("formId");
				objectType =formInfo.getString("objectType");
			}
			JSONArray itemInfo = item.getJSONArray("ItemInfo");//基本信息
			JSONObject itemBasicInfo=itemInfo.getJSONObject(0);
			JSONArray materials = item.getJSONArray("material");//申请材料
			if("bqbz".equals(flag)){
				String bqbz_sblsh=this.getParameter("bqbz_sblsh");
				String str = createState(bqbz_sblsh);
				 bqbzmaterials = QueryEcGapXMLJXDao.getInstance().getBqbzMaterials(str);//补齐补正材料
				
			}
			String sql = "select s.*,i.state,i.BZCLQD from business_index i,sub_for_ex_app_information s where i.sblsh=s.sblsh ";
			if(StringUtils.isEmpty(sblsh)){
				sql+="and (i.sxid=? and i.ucid=? and i.state='00')";
				resultsub = DbHelper.query(sql, new Object[]{itemId,ui.getUid()}, conn);
			}
			else {
				sql+=" and i.sblsh =?";
				resultsub = DbHelper.query(sql, new Object[]{sblsh}, conn);
			}
			conn.commit();
		if (resultsub.getTotal() > 0) {
			_dataId=resultsub.getRecord(0).getString("DATAID");
			if(!"".equals(_dataId)){
				dataId = "&dataId="+_dataId;
			}
			sblsh=resultsub.getRecord(0).getString("SBLSH");
			formid =resultsub.getRecord(0).getString("FORMID");
			objectType = resultsub.getRecord(0).getString("OBJECTTYPE");
			baseContent = resultsub.getRecord(0).getString("BASECONTENT");
			state=resultsub.getRecord(0).getString("STATE");
			bzclqd=resultsub.getRecord(0).getString("BZCLQD").equals("")?bzclqd:resultsub.getRecord(0).getString("BZCLQD");
			Abzclqd=bzclqd.equals("")?Abzclqd:bzclqd.split(",");
		}
			data.put("dataId", dataId); 
			data.put("_dataId", _dataId); 
			data.put("formid", formid);//表单id
			data.put("OBJECT_TYPE", objectType);//审批类型  个人 企业  项目 
			data.put("formInfo", formInfo);
			
			for(int i=0;i<materials.size();i++){
				for(int j=0;j<Abzclqd.length;j++){
					if(materials.getJSONObject(i).containsValue(Abzclqd[j])){
						materials.getJSONObject(i).put("BQBZ", "1");
					}
				}
			}
			//过滤，只展示申报提交材料
			for(int i=0;i<materials.size();i++){
				if(!("Apply").equals(materials.getJSONObject(i).get("BUSINESS_TYPE"))){
					materials.remove(i);
					i--;
				}else{
					if("null".equals(materials.getJSONObject(i).getString("SAMPLE_NAME"))){
						materials.getJSONObject(i).put("SAMPLE_NAME","");
					}
				}
			}
			data.put("materials", materials);//所有附件
			if("bqbz".equals(flag)){
				data.put("materials", bqbzmaterials);//补齐补正材料
			}
			data.put("itemInfo", itemBasicInfo);
			data.put("baseContent", baseContent);
			data.put("sblsh", sblsh);
			data.put("state", state);
			DataSet dSet = UserDao.getInstance().getUserById("" + this.getUserInfo().getUid());
			if(dSet.getTotal() > 0) {
				JSONObject tempObj = dSet.getRecord(0);
				JSONObject infoObj = new JSONObject();
				
				String type = tempObj.getString("TYPE");
				//个人
				if("11".equals(type)) {
					infoObj.accumulate("TYPE", tempObj.getString("TYPE"));//类型
					infoObj.accumulate("NAME", tempObj.getString("NAME"));//姓名
					infoObj.accumulate("CARD_TYPE", tempObj.getString("CARD_TYPE"));//证件类型
					infoObj.accumulate("CARD_NO", tempObj.getString("CARD_NO"));//证件编号
					infoObj.accumulate("PHONE", tempObj.getString("PHONE"));//联系电话
					infoObj.accumulate("ADDRESS", tempObj.getString("ADDRESS"));//联系地址
					infoObj.accumulate("EMAIL", tempObj.getString("EMAIL"));//邮箱
				}
				//单位
				else if("21".equals(type)) {
					infoObj.accumulate("TYPE", tempObj.getString("TYPE"));//类型
					infoObj.accumulate("ORG_NAME", tempObj.getString("NAME"));//机构名称
					infoObj.accumulate("ORG_NO", tempObj.getString("ORG_NO"));//机构代码
					infoObj.accumulate("ORG_BOSS_TYPE", tempObj.getString("ORG_BOSS_TYPE"));//法人证件类型
					infoObj.accumulate("ORG_BOSS_NO", tempObj.getString("ORG_BOSS_NO"));//法人证件号码
					infoObj.accumulate("ORG_BOSS_NAME", tempObj.getString("ORG_BOSS_NAME"));//法人姓名
					
					infoObj.accumulate("NAME", tempObj.getString("NAME"));//姓名
					infoObj.accumulate("CARD_TYPE", tempObj.getString("CARD_TYPE"));//证件类型
					infoObj.accumulate("CARD_NO", tempObj.getString("CARD_NO"));//证件编号
					infoObj.accumulate("ADDRESS", tempObj.getString("ADDRESS"));//联系地址
				}
				data.put("userInfo", infoObj);
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			DBSource.closeConnection(conn);
		}
		
		return true;
		
	}
	public String createState(String sblsh){
        //调用接口，传递参数
        String serviceUrl=SecurityConfig.getString("EcGapServiceUrlB");
        String soapaction="http://tempuri.org/";   //域名，这是在server定义的
        Service service = new Service(); 
        String ret ="";
        Call call;
		try {
			call = (Call) service.createCall();
			call.setTargetEndpointAddress(serviceUrl); 
	        call.setOperationName(new QName(soapaction,"GetCaseState")); //设置要调用哪个方法  
            call.addParameter(new QName(soapaction,"ZteNO"),org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);  
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            call.setUseSOAPAction(true);  
            call.setSOAPActionURI(soapaction + "GetCaseState");   
	         ret = (String)call.invoke(new Object[]{sblsh}); 
	        System.out.println("看看到底返回了啥："+ret);
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
        return ret;
			
		}
}
