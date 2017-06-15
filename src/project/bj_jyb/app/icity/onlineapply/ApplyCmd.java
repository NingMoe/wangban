package bj_jyb.app.icity.onlineapply;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.guestbook.WriteDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class ApplyCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ApplyCmd.class);
	
	private WriteDao writeDao;
	
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
		HttpClient client = new HttpClient();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/accpet");
		if("zs".equals(SecurityConfig.getString("AppId"))){
			String power_type = (String)data.get("power_type");
			if(!("XK".equals(power_type)||"BM".equals(power_type))){
				url = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/accpet");
			}
		}
		PostMethod postMethod = new PostMethod(url);
		client.getParams().setContentCharset("UTF-8");	
		String o_sblsh=data.getString("receiveNum");
		//暂存数据提交，将申办流水号置空
		if("sp".equals(data.getString("state"))){
			data.put("receiveNum", "");
		}
		System.out.println(data.toString());
		postMethod.setParameter("postdata", data.toString());
		JSONObject receiveNum;
		try{
				client.executeMethod(postMethod);
				receiveNum =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
				System.out.println(receiveNum);
				//_log.info(receiveNum);
				if(receiveNum.get("state").equals("200")){
					ApplyDao.getInstance().updateBusinessAttach(receiveNum,data);
					ApplyDao.getInstance().submitData(o_sblsh,receiveNum,data);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("提交成功！申办流水号"+receiveNum.getString("receiveNum"));
					ds.setData(Tools.stringToBytes(receiveNum.getString("receiveNum")));
					
					String pageModel = SecurityConfig.getString("PAGE_MODE");
					if("zs_city".equals(pageModel)){
						sendMessage(pSet,ds);
					}
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage("调用审批接口失败！");
				}
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("网办处理数据失败submitData！");
			return ds;
		}
		return ds;
	}
	public DataSet saveDraft(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
		try{
					String o_sblsh=ApplyDao.getInstance().saveData(data);
					ds.setRawData(o_sblsh);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("保存成功！");
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("保存失败saveDraft！");
			return ds;
		}
		return ds;
		
	}
	
	//短信发送
	public void sendMessage(ParameterSet pSet,DataSet ds){
		
		JSONObject data =((JSONObject)pSet.get("data"));
		pSet.setParameter("organ_code", data.getString("orgCode"));
		//外网申报type 20
		pSet.setParameter("TYPE", "20");
		String message = "";
		
		String phonestr = WriteDao.getInstance().getPhoneStr(pSet);
		
		message = SecurityConfig.getString("message_wwsb");
		message = message.replace("{name}", data.getString("userName"));
		message = message.replace("{itemname}", data.getString("itemName"));
		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url")+"sendMessage");		
		HttpClient client = new HttpClient();
		client.getParams().setContentCharset("UTF-8");
		PostMethod postMethod = new PostMethod(url);
		postMethod.setParameter("phoneList",phonestr);
		postMethod.setParameter("mesConent",message);
		
		try {
			client.executeMethod(postMethod);
		} catch (Exception e) {
			ds.setMessage("短信发送失败！");
		} 
	}
}
