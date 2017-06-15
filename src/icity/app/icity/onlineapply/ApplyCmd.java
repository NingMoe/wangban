package app.icity.onlineapply;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.govservice.GovProjectDao;
import app.icity.guestbook.WriteDao;
import app.icity.sync.PowerBaseInfoQZCmd;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;
import core.util.CommonUtils_api;

public class ApplyCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ApplyCmd.class);

	public DataSet submitSP(ParameterSet pSet) {
		System.out.println("参数pSet:"+pSet.toString());
		DataSet ds = new DataSet();
		JSONObject data = JSONObject.fromObject(pSet.get("data"));
		try {
			String oldNum = "";
			if(data.containsKey("receiveNum")&& !"".equals(data.getString("receiveNum"))  ){
				oldNum=data.getString("receiveNum");
			}
			if("1".equals(SecurityConfig.getString("is_cloud_accept"))){
				data = ApplyCloudDao.getInstance().submitData(data);
			}else{
				data = ApplyDao.getInstance().submitData(data);
			}
			if ("1".equals(SecurityConfig.getString("useSubmitInterface"))) {
				System.out.println("提交失败1");
				JSONObject da = ApplyDao.getInstance().getSbxx(data.getString("receiveNum"));
				if(!"".equals(oldNum)&&!oldNum.equals(data.getString("receiveNum"))){
					da.put("oldReceiveNum",oldNum);
				}
				pSet.put("data", da);
				pSet.put("action", "02");
				System.out.println("提交失败2");
				PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
				DataSet dst = powerBaseInfoQZCmd.usesubmitSL(pSet);
				System.out.println("提交失败3");
				System.out.println("提交失败"+dst);
				if (dst.getState()==1) {
					System.out.println("提交失败4");
					ApplyDao.getInstance().submitUpData(data);
					System.out.println("提交失败5");
				}
			}
			ApplyDao.getInstance().updateBusinessAttach(data);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("提交成功！申办流水号" + data.getString("receiveNum"));
			ds.setRawData(data);
			// ds.setData(Tools.stringToBytes(data.getString("receiveNum")));
			String AppId = SecurityConfig.getString("AppId");
			if ("zs".equals(AppId)) {
				sendMessage(pSet, ds);
			}
			//西宁 
			if ("xns".equals(AppId)) {
				String sblsh =data.getString("receiveNum");
				String password =data.getString("password");
				String phone = data.getString("lxrphone");
				//西宁使用 模板短信 只保存参数
				String smsContent =sblsh+","+password;
				String channel ="onlineApply";//在线申报
				ApplyDao.getInstance().sendMessageToUser(smsContent,channel,phone);
			}
			//提交综合受理系统失败，转为暂存
			if(data.containsKey("retstate")){
				if("0".equals(data.getString("retstate"))){
					ds.setState((byte)4);
				}
			}
		} catch (Exception e) {
			System.out.println("提交失败");
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败！");
			return ds;
		}
		return ds;
	}

	public DataSet saveDraft(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject data = JSONObject.fromObject(pSet.get("data"));
		try {
			String o_sblsh = ApplyDao.getInstance().saveData(data);
			if ("1".equals(SecurityConfig.getString("useSubmitInterface"))) {
				JSONObject da = ApplyDao.getInstance().getSbxx(o_sblsh);
				pSet.put("data", da);
				pSet.put("action", "01");
				PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
				DataSet dst = powerBaseInfoQZCmd.usesubmitSL(pSet);
				if (dst.getState()==1) {
					ApplyDao.getInstance().submitUpData(data);
				}
			}
			ds.setRawData(o_sblsh);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("保存成功！");

		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("保存失败！");
			return ds;
		}
		return ds;
	}

	// 短信发送-舟山
	public void sendMessage(ParameterSet pSet, DataSet ds) {

		JSONObject data = JSONObject.fromObject(pSet.get("data"));
		pSet.setParameter("organ_code", data.getString("orgCode"));
		pSet.setParameter("itemCode", data.getString("itemCode"));
		// 外网申报type 20
		pSet.setParameter("TYPE", "20");
		String message = "";

		String phonestr = WriteDao.getInstance().getPhoneStr(pSet);

		message = SecurityConfig.getString("message_wwsb");
		message = message.replace("{name}", data.getString("userName"));
		message = message.replace("{itemname}", data.getString("itemName"));

		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url")
				+ "sendMessage");
		Map<String, String> map=new HashMap<String, String>();
		map.put("phoneList", phonestr);
		map.put("mesConent", message);

		try {
			RestUtil.postData(url, map);
		} catch (Exception e) {
			ds.setMessage("短信发送失败！");
		}
	}
	
	/**
	 * 向重庆市司法局提供表单数据
	 * @param pset
	 * @return
	 */
	public DataSet formDataExchangeToJudicial(ParameterSet pset){
		String UserID = (String) pset.getParameter("UserID");
		String Key = (String) pset.getParameter("Key");
		String DataId = (String) pset.getParameter("DataId");
		String FormId = (String) pset.getParameter("FormId");
		String FormData = pset.getParameter("FormData").toString();
		String Param = (String) pset.getParameter("Param");
		String metail = pset.getParameter("metail").toString();
		DataSet ds = new DataSet();
		String url = SecurityConfig.getString("judicialDataExchangeUrl");
		Map<String,String> map = new HashMap<String,String>();
		map.put("UserID", UserID);
		map.put("Key", Key);
		map.put("DataId", DataId);
		map.put("FormId", FormId);
		map.put("FormData", FormData);
		map.put("Param", Param);
		map.put("metail", metail);
		try {
			Object oj = RestUtil.postData(url, map);
			 JSONObject obj = JSONObject.fromObject(oj);
			 if("200".equals(obj.getString("code"))){
				 ds.setState(StateType.SUCCESS);
			 }else{
				 ds.setState(StateType.FAILT);
				 ds.setMessage(obj.getString("error"));
			 }
		} catch (Exception e) {
			if(_log.isInfoEnabled()){
				 _log.info("formDataExchangeToJudicial错误信息："+e.getMessage());
			 }
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	/**
	 * 历史办件数据推送至省系统
	 */
	public DataSet sendData(){
		return ApplyDao.getInstance().sendData();
	}
}
