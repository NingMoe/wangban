package app.icity.sync;

import org.apache.commons.lang.StringUtils;

import com.icore.StateType;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import app.uc.GetUserMapDao;
import net.sf.json.JSONObject;

public class UniteUserLogin extends BaseQueryCommand {

	public DataSet loginByTicket(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ticket = (String) pSet.getParameter("ticket");
		String userType = (String) pSet.getParameter("userType");
		String loginUrl = SecurityConfig.getString("LoginUrl") + "&appmark=" + SecurityConfig.getString("UserInfoappmark");
		JSONObject failtJson = new JSONObject();
		failtJson.accumulate("loginUrl", loginUrl);
		try {
			if (StringUtils.isEmpty(ticket)) {
				ds.setState(StateType.FAILT);
				ds.setRawData(failtJson);
				ds.setMessage("缺少票据！");
				return ds;
			}
			UniteUserInterface ui = new UniteUserInterface();
			ui.setTicket(ticket);
			ui.setUserType(userType);
			ui.getToken();
			JSONObject userJson = ui.getUserInfo();
			ds.setRawData(userJson);
			ds.setState(StateType.SUCCESS);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setRawData(failtJson);
			ds.setMessage("缺少票据！");
			return ds;
		}

		return ds;
	}
	
	public DataSet getLoginUrl(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject loginUrl = new JSONObject();
		loginUrl.accumulate("loginUrl", SecurityConfig.getString("LoginUrl") + "&appmark=" + SecurityConfig.getString("UserInfoappmark"));
		ds.setRawData(loginUrl);
		return ds;
	}
	public DataSet getRegisterUrl(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject registerUrl = new JSONObject();
		registerUrl.accumulate("registerUrl", "http://zwfw.sd.gov.cn/sdjis/front/register/corregister.do?appmark=" + SecurityConfig.getString("UserInfoappmark"));
		ds.setRawData(registerUrl);
		System.err.println(ds);
		return ds;
	}
	public DataSet getTicket(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
		UniteUserInterface unitUI = new UniteUserInterface();
		
		String type=(String) pSet.get("type");
		if("11".equals(type))
		{
			type = "gr";
		} else 
		{
			type = "fr";
		}
		
		unitUI.setUserType(type);
		
		
		String uid=(String) pSet.get("uid");
		String uuid=GetUserMapDao.getInstance().GetMapid(uid).getRecord(0).getString("USER_ID_MAP");
		JSONObject userInfo = unitUI.getTicketByUuid(uuid);
		ds.setRawData(userInfo);
		} catch(Exception e){
			e.printStackTrace();
		}
		return ds;
	}
	
}
