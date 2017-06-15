package fz.action.icity.submitsp;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.uc.UserDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.Command;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import core.util.HttpClientUtil;

public class baseinfo extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static Log _log = LogFactory.getLog(baseinfo.class);
	public boolean handler(Map<String,Object> data){
		return interfaceMode(data);
	}
	@SuppressWarnings("deprecation")
	public boolean interfaceMode(Map<String, Object> data) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			JSONObject userInfo = new JSONObject();
			DataSet dSet = UserDao.getInstance().getUserById("" + this.getUserInfo().getUid());
			if(dSet.getTotal() > 0) {
				JSONObject uInfo = dSet.getRecord(0);				
				String type = uInfo.getString("TYPE");
				//个人
				if("11".equals(type)) {
					userInfo.accumulate("TYPE", uInfo.getString("TYPE"));//类型
					userInfo.accumulate("NAME", uInfo.getString("NAME"));//姓名
					userInfo.accumulate("CARD_TYPE", uInfo.getString("CARD_TYPE"));//证件类型
					userInfo.accumulate("CARD_NO", uInfo.getString("CARD_NO"));//证件编号
					userInfo.accumulate("PHONE", uInfo.getString("PHONE"));//联系电话
					userInfo.accumulate("ADDRESS", uInfo.getString("ADDRESS"));//联系地址
					userInfo.accumulate("EMAIL", uInfo.getString("EMAIL"));//邮箱
					userInfo.accumulate("STATUS", uInfo.getString("STATUS"));//1普通2实名
				}
				//单位
				else if("21".equals(type)) {
					userInfo.accumulate("TYPE", uInfo.getString("TYPE"));//类型
					userInfo.accumulate("ORG_NAME", uInfo.getString("ORG_NAME"));//机构名称
					userInfo.accumulate("ORG_NO", uInfo.getString("ORG_NO"));//机构代码
					userInfo.accumulate("ORG_BOSS_TYPE", uInfo.getString("ORG_BOSS_TYPE"));//法人证件类型
					userInfo.accumulate("ORG_BOSS_NO", uInfo.getString("ORG_BOSS_NO"));//法人证件号码
					userInfo.accumulate("ORG_BOSS_NAME", uInfo.getString("ORG_BOSS_NAME"));//法人姓名
					
					userInfo.accumulate("NAME", uInfo.getString("NAME"));//姓名
					userInfo.accumulate("CARD_TYPE", uInfo.getString("CARD_TYPE"));//证件类型
					userInfo.accumulate("CARD_NO", uInfo.getString("CARD_NO"));//证件编号
					userInfo.accumulate("ADDRESS", uInfo.getString("ADDRESS"));//联系地址
					userInfo.accumulate("STATUS", uInfo.getString("STATUS"));//1普通2实名
					userInfo.accumulate("CREDIT_CODE", uInfo.getString("CREDIT_CODE"));//统一信用编码
				}
			}
			
			
		String itemId = this.getParameter("itemId");
		String sblsh = this.getParameter("sblsh");
		DataSet resultsub = null;
		UserInfo ui=this.getUserInfo();
		String formid = "",dataId="",_dataId="",baseContent = "\"\"";
		String state = "00";
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getAllItemInfoByItemID?itemId="+itemId);
		String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemId);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
		JSONObject item=JSONObject.fromObject(client.getResult(url,""));
		String objectType = "";//办理对象类型 新事项分为 个人 企业 项目  先假设0为个人1为企业2为项目 
		JSONArray License = new JSONArray();
			JSONObject formSettingInfo=form.getJSONObject("info");
			
			if(!formSettingInfo.isNullObject()){
				formid=formSettingInfo.getString("formId");
				objectType =formSettingInfo.getString("objectType");
				String type = this.getParameter("type");
				if((null==type||"".equals(type))&&objectType.indexOf("3")>-1){
					String usbkey = UrlHelper.contextPath()+"/icity/submitsp/usbkey?itemId="+itemId+"&sblsh="+sblsh;
					this.sendRedirect(HttpUtil.formatUrl(usbkey)); 
					return false;
				}else{	
					String zzjgdm = this.getParameter("zzjgdm");
					String __type = "21";
					if("".equals(zzjgdm)){
						__type = userInfo.getString("TYPE");
						//if("2".equals(userInfo.getString("STATUS"))){//实名
							if("21".equals(userInfo.getString("TYPE"))){
								if(!"".equals(userInfo.getString("CREDIT_CODE"))){
									zzjgdm = userInfo.getString("CREDIT_CODE");
								}else{
									zzjgdm = userInfo.getString("ORG_NO");
								}
							}else if("11".equals(userInfo.getString("TYPE"))){
								zzjgdm = userInfo.getString("CARD_NO");
							}
						//}						
					}
					if(!"".equals(zzjgdm)){
						Command cmd = new Command("app.icity.PKISvrTcpJavaCltCmd");		
						cmd.setParameter("businessid", "223");
						cmd.setParameter("type", __type);		
						cmd.setParameter("zzjgdm", zzjgdm);
						DataSet ds_license = cmd.execute("getLicenseData");
						if(ds_license.getState() != StateType.FAILT){
							JSONArray __jo = JSONArray.fromObject(ds_license.getRawData());
							License = __jo;								
						}else{
						}
					}
				}				
			}
			JSONArray itemInfo = item.getJSONArray("ItemInfo");//基本信息
			JSONObject itemBasicInfo=itemInfo.getJSONObject(0);
			JSONArray condition = item.getJSONArray("condition");//审批条件
			JSONArray document = item.getJSONArray("document");//文书
			JSONArray windows = item.getJSONArray("window");//窗口地址
			JSONObject window=new JSONObject();
			if(windows.size()>0){
				 window = windows.getJSONObject(0);
			}
			JSONArray materials = item.getJSONArray("material");//申请材料
			String bzclqd="";
			String[] Abzclqd={};
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
			objectType = resultsub.getRecord(0).getString("OBJECTTYPE");
			baseContent = resultsub.getRecord(0).getString("BASECONTENT");
			bzclqd=resultsub.getRecord(0).getString("BZCLQD").equals("")?bzclqd:resultsub.getRecord(0).getString("BZCLQD");
			Abzclqd=bzclqd.equals("")?Abzclqd:bzclqd.split(",");
			state=resultsub.getRecord(0).getString("STATE");
		}
		    data.put("License", License); 
			data.put("dataId", dataId); 
			data.put("_dataId", _dataId); 
			data.put("formid", formid);//表单id
			data.put("OBJECT_TYPE", objectType);//审批类型  个人 企业  项目 
			data.put("formSettingInfo", formSettingInfo);
			//org.eclipse.jetty.util.log.Log.info("申报提交材料："+materials);

			for(int i=0;i<materials.size();i++){
				for(int j=0;j<Abzclqd.length;j++){
					if(materials.getJSONObject(i).containsValue(Abzclqd[j])){
						materials.getJSONObject(i).put("BQBZ", "1");
					}
				}
			}
			//过滤，只展示申报提交材料
			for(int i=0;i<materials.size();i++){
					if(!materials.getJSONObject(i).get("BUSINESS_TYPE").equals("Apply")){
						materials.remove(i);
						i--;
					}
			}
			//福州测试，将第一个材料设为证照材料
			//materials.getJSONObject(0).put("TYPE", "3");
			///！！！！！！！！！！！！！！！！
			data.put("materials", materials);//所有附件
			data.put("window", window);
			data.put("condition", condition);
			data.put("document", document);
			data.put("itemInfo", itemBasicInfo);
			data.put("baseContent", baseContent);
			data.put("bzclqd", bzclqd);
			data.put("sblsh", sblsh);
			data.put("state", state);
			data.put("userInfo", userInfo);
			//org.eclipse.jetty.util.log.Log.info("所有附件："+materials);
			//org.eclipse.jetty.util.log.Log.info("所有内容："+data);

		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			DBSource.closeConnection(conn);
		}
		
		return true;
		
	}
}
