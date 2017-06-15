package syan.app.icity.onlineapply;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.guestbook.WriteDao;
import app.util.RestUtil;

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
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/accpet");
		if("zs".equals(SecurityConfig.getString("AppId"))){
			String power_type = (String)data.get("power_type");
			if(!("XK".equals(power_type)||"BM".equals(power_type))){
				url = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/accpet");
			}
		}
		String o_sblsh=data.getString("receiveNum");
		//暂存数据提交，将申办流水号置空
		if("sp".equals(data.getString("state"))){
			data.put("receiveNum", "");
		}
		Map<String, String> map=new HashMap<String, String>();
		map.put("postdata", data.toString());
		JSONObject receiveNum;
		try{
				receiveNum =  JSONObject.fromObject(RestUtil.postData(url, map));
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
		Map<String, String> map=new HashMap<String, String>();
		map.put("phoneList",phonestr);
		map.put("mesConent",message);
		
		try {
			RestUtil.postData(url, map);
		} catch (Exception e) {
			ds.setMessage("短信发送失败！");
		} 
	}
}
