package action.icity.submitsp;

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
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactory.getLog(baseinfo.class);
	public boolean handler(Map<String,Object> data){
		return interfaceMode(data);
	}
	@SuppressWarnings("deprecation")
	public boolean interfaceMode(Map<String, Object> data) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			boolean ql = false;//false 默认为审批  true 为权力
			String itemId = this.getParameter("itemId");
			String itemCode=this.getParameter("itemCode");
			String url = SecurityConfig.getString("ItemSynchronizationUrl");
			if(StringUtils.isNotEmpty(itemId)){
				url+="/getAllItemInfoByItemID?itemId="+itemId;
			}
			if(StringUtils.isNotEmpty(itemCode)){
				url+="/getItemInfoByItemCode?itemCode="+itemCode;
			}
			url=HttpUtil.formatUrl(url);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject item=JSONObject.fromObject(client.getResult(url,""));
			JSONArray  itemInfo = item.getJSONArray("ItemInfo");//基本信息
			JSONObject itemBasicInfo=itemInfo.getJSONObject(0);
			itemId=itemBasicInfo.getString("ID");
			String enter_situation="";
			if(itemBasicInfo.containsKey("ENTER_SITUATION")){
				enter_situation=itemBasicInfo.getString("ENTER_SITUATION");
			}
			String sblsh = this.getParameter("sblsh");
			DataSet resultsub = null;
			UserInfo ui=this.getUserInfo();
			String formid = "",formName="",dataId="",_dataId="",baseContent = "\"\"";
			String state = "00";
			String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemId);
			//重庆 食药监
			if("chq".equals(SecurityConfig.getString("AppId"))){
				urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getLoginFormByItemID?itemId="+itemId+"&systemCode=inspur_tysp");
			}
			String power_type = itemBasicInfo.getString("TYPE");//事项类型
			String power_source="";
			if(itemBasicInfo.containsKey("POWER_SOURCE")){
				power_source=itemBasicInfo.getString("POWER_SOURCE");
			}		
			power_source="null".equals(power_source)?"":power_source;
			if("zs".equals(SecurityConfig.getString("AppId"))){
				if ("0".equals(enter_situation)) {
					ql = true;
					state = "ql";
					urlForm = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/getFormInfo?itemId="+itemId);
				}
			}
			String objectType = "";//办理对象类型 新事项分为 个人 企业 项目  先假设0为个人1为企业2为项目
		
			JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
			JSONObject formSettingInfo=form.getJSONObject("info");
			JSONObject connectAddress=form.getJSONObject("connectAddress");
			if(!connectAddress.isNullObject()){
				//重庆食药监配置表单地址
				data.put("sybmol", connectAddress.getString("sybmol"));
				data.put("target", connectAddress.getString("target"));
				data.put("urlAddress", connectAddress.getString("urlAddress"));
			}
			if(!formSettingInfo.isNullObject()){
				formid=formSettingInfo.getString("formId");
				formName=formSettingInfo.getString("formName");
				objectType = formSettingInfo.containsKey("objectType")?formSettingInfo.getString("objectType"):"1";
			}
			if(ql){
				itemBasicInfo.put("ASSORT", "1");
			}
			JSONArray condition = item.getJSONArray("condition");//审批条件
			JSONArray document = item.getJSONArray("document");//文书
			JSONArray windows = item.getJSONArray("window");//窗口地址
			JSONObject window=new JSONObject();
			if(windows.size()>0){
				 window = windows.getJSONObject(0);
			}
			JSONArray materials = item.getJSONArray("material");//申请材料
			
			/**
			 * 查询加载之前的办件信息
			 */
			String bzclqd="";
			String[] Abzclqd={};
			String sql = "select s.*,i.state,i.BZCLQD from business_index i,sub_for_ex_app_information s where i.sblsh=s.sblsh ";
			if(StringUtils.isEmpty(sblsh)){
				sql+="and (i.sxid=? and i.ucid=? and i.state='00')";
				resultsub = DbHelper.query(sql, new Object[]{itemId,""+ui.getUid()}, conn);
				if("kms".equals(SecurityConfig.getString("AppId"))){
					resultsub.setTotal(0);
				}
			}else {
				sql+=" and i.sblsh =?";
				resultsub = DbHelper.query(sql, new Object[]{sblsh}, conn);
			}
			if (resultsub.getTotal() > 0) {
				_dataId=resultsub.getRecord(0).getString("DATAID");
				if(!"".equals(_dataId)){
					dataId = "&dataId="+_dataId;
				}
				sblsh=resultsub.getRecord(0).getString("SBLSH");
				objectType = resultsub.getRecord(0).getString("OBJECTTYPE");
				baseContent = resultsub.getRecord(0).getString("BASECONTENT");
				bzclqd=resultsub.getRecord(0).getString("BZCLQD").equals("")?bzclqd:resultsub.getRecord(0).getString("BZCLQD");
				Abzclqd="".equals(bzclqd)?Abzclqd:bzclqd.split(",");
				//昆明再次办理 state=
				String m_state = this.getParameter("state");
				if(StringUtils.isEmpty(m_state)){
					state=ql==false?resultsub.getRecord(0).getString("STATE"):"ql";				
				}else{
					sblsh = "";
					state=ql==false?"00":"ql";
				}
				//end
			}
			data.put("dataId", dataId); 
			data.put("_dataId", _dataId); 
			data.put("formid", formid);//表单id
			data.put("formName", formName);
			data.put("OBJECT_TYPE", objectType);//审批类型  个人 企业  项目 
			data.put("POWER_SOURCE", power_source);//接入等级
			data.put("enter_situation", enter_situation);//zs   enter_situation=0 权力
			data.put("formSettingInfo", formSettingInfo);
			for(int i=0;i<materials.size();i++){
				materials.getJSONObject(i).put("BQBZ", "0");//设置
				for(int j=0;j<Abzclqd.length;j++){
					if(materials.getJSONObject(i).containsValue(Abzclqd[j])){
						materials.getJSONObject(i).put("BQBZ", "1");
					}
				}
				//过滤，只展示申报提交材料       如果是补齐补正则不是申报提交也可以展示
				if(!("Apply").equals(materials.getJSONObject(i).get("BUSINESS_TYPE"))&&"0".equals(materials.getJSONObject(i).getString("BQBZ"))){
					materials.remove(i);
					i--;
				}else{
					if("null".equals(materials.getJSONObject(i).getString("SAMPLE_NAME"))){
						materials.getJSONObject(i).put("SAMPLE_NAME","");
					}
				}
			}			
			//电子证照测试，设第一个材料为证照材料
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
			data.put("power_type", power_type);
			data.put("state", state);
			DataSet dSetExt = UserDao.getInstance().getListExt("" + ui.getUid());
			JSONObject uInfo = new JSONObject();
			if (dSetExt.getTotal() > 0) {
				uInfo = dSetExt.getRecord(0);
			}
			data.put("userInfo", uInfo);
		}catch(RuntimeException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();			
		}finally{
			DBSource.closeConnection(conn);
		}
		return true;
	}
}
