package km.app.icity.onlineapply;

import km.app.icity.webservice.WebServiceBase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import core.util.HttpClientUtil;

public class ApplyCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ApplyCmd.class);
	
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject data = ((JSONObject) pSet.get("data"));
		try {
			//昆明项目/盘龙项目部署一套 is_cloud_accept 暂不使用
			//昆明项目/盘龙项目部署一套。昆明办件走向审批、盘龙办件走云受理（一窗）
			//盘龙项目走云受理
			/*if("1".equals(SecurityConfig.getString("is_cloud_accept"))){*/
			if("panlongqu".equals(SecurityConfig.getString("PAGE_MODE"))){
				data = ApplyCloudDao.getInstance().submitData(data);
			}else{
				data = ApplyDao.getInstance().submitData(data);
			}
				//data=ApplyDao.getInstance().submitData(data);
				// 方便第三方公司存储，读取配置文件security.properties的NetDiskDownloadAddress路径
				data.put("filepath", SecurityConfig.getString("NetDiskDownloadAddress"));
				//获取业务表单数据
				String formId = data.getString("formId");
				String formDataId=data.getString("dataId");
				String url1=SecurityConfig.getString("Form_url"); 
				url1+="/cform/getFormStandardData?formId="+formId+"&formDataId="+formDataId;
				HttpClientUtil client1 = new HttpClientUtil();
				String html= client1.getResult(url1,"");
				//将业务表单信息添加到data
				data.put("html", html);
             	try{
					//是否调用接口同步申办数据至新锐和达效能监督平台
					String SyncToSupervision = SecurityConfig.getString("SyncToSupervision");
					if(!StringUtil.isEmpty(SyncToSupervision)&&"1".equals(SyncToSupervision)){
						int state = WebServiceBase.getInstance().commitTransactionInfo(data.toString());
						if (200!=state) {
							ds.setMessage("调用第三方推送申报接口失败！");
							ds.setState((byte)3);
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
					_log.error(ex.getMessage());
				}
				//提交综合受理系统失败，转为暂存
				if(data.containsKey("retstate")){
					if("0".equals(data.getString("retstate"))){
						ds.setState((byte)4);
					}
				}
				ds.setRawData(data);
				//盘龙在线申报数据通知
				String PAGE_MODE = SecurityConfig.getString("PAGE_MODE");
				if ("panlongqu".equals(PAGE_MODE)) {
					String noticeStyle =data.getString("noticeStyle");
					String sblsh =data.getString("receiveNum");
					String password =data.getString("password");
					String phone = data.getString("lxrphone");
					String itemname = data.getString("itemName");
					String name = data.getString("userName");
					String ucid = data.getString("ucid");
					String orgCode = data.getString("orgCode");
					
					//盘龙使用 通知内容模板
					String smsContent_user = SecurityConfig.getString("pl_online_notice_user");
					String smsContent_dept = SecurityConfig.getString("pl_online_notice_dept");
					smsContent_user = smsContent_user.replace("{itemname}", itemname);
					smsContent_user = smsContent_user.replace("{sblsh}", sblsh);
					smsContent_user = smsContent_user.replace("{password}", password);
					smsContent_dept =smsContent_dept.replace("{itemname}", itemname);
					smsContent_dept =smsContent_dept.replace("{name}", name);
					String channel ="onlineApply";//在线申报
					ApplyDao.getInstance().sendMessageKmsPl(smsContent_user,smsContent_dept,ucid, channel, phone,orgCode,noticeStyle);
				}
		} catch (Exception e) {
			_log.error(e.getMessage());
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("网办处理数据失败submitData！");
		}
		return ds;
	}
	
	public DataSet saveDraft(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
		try{
			String o_sblsh = ApplyDao.getInstance().saveData(data);
			ds.setRawData(o_sblsh);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("保存成功！");
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("保存失败！");
			return ds;
		}
		return ds;
		
	}
	//获取情形
	public DataSet getCaseByItemId(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String itemId=(String)pSet.get("itemId");
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getCaseByItemId?itemId="+itemId);
			Object ret= client.getResult(url,"");
			JSONObject info;
			info = JSONObject.fromObject(ret);			
			ds.setRawData(info);
			ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
			_log.error(e.getMessage());
		}
		return ds;
	}
	//获取情形材料
	public DataSet getMaterilByCaseCode(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String itemId=(String)pSet.get("itemId");//itemId（事项ID）
			String caseCode=(String)pSet.get("caseCode");//caseCode（情形code）
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getMaterilByCaseCode?itemId="+itemId+"&caseCode="+caseCode);
			Object ret= client.getResult(url,"");
			ds.setRawData(ret);
			ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
			_log.error(e.getMessage());
		}
		return ds;
	}
	public DataSet getCaseCode(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String sblsh=(String)pSet.get("sblsh");
			ds = ApplyDao.getInstance().getCaseCode(sblsh);
			ds.setState(StateType.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("未查询到该业务！");
			_log.error(e.getMessage());
		}
		return ds;
	}
}
