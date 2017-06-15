package action.zs;

import com.commnetsoft.proxy.SsoClient;
import com.commnetsoft.proxy.model.CallResult;
import com.commnetsoft.proxy.model.UserInfo;
import com.commnetsoft.proxy.model.ValidationResult;
import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("null")
	public boolean handler(Map<String,Object> data){
		String ticket = this.getParameter("ticket");		
		String url = this.getParameter("sp");
		//_log.info("ticket-------"+ticket);
		//_log.info("sp-----------"+url);
		SsoClient client = SsoClient.getInstance();		
		client.initConfig(SecurityConfig.getString("servicecode"),SecurityConfig.getString("servicepwd"),SecurityConfig.getString("serviceurl"));
       	CallResult loginR = client.login(ticket);
       	if("0".equals(loginR.getResult())){
       		ValidationResult tvm = new ValidationResult();
    		if(loginR instanceof ValidationResult){
    			tvm = (ValidationResult)loginR;		
    		}
    		String token = tvm.getToken();
    		UserInfo uinfo;
    		uinfo = client.getUser(token);
    		String ACCOUNT = uinfo.getLoginname();
    		String PHONE = uinfo.getMobile();
    		String CARD_NO = uinfo.getUserid();
    		String ID = uinfo.getUserid();
    		String NAME = uinfo.getUsername();
    		String SEX = uinfo.getSex();
    		String BIRTHDAY = uinfo.getBirthday();
    		String STATUS = uinfo.getAuthLevel();
    		
    		net.sf.json.JSONObject uInfo = new net.sf.json.JSONObject();
			uInfo.put("ACCOUNT", ACCOUNT);
			uInfo.put("NAME", NAME);
			uInfo.put("TYPE", "11");
			uInfo.put("CARD_NO", CARD_NO);
			uInfo.put("PHONE", PHONE);
            uInfo.put("SEX", SEX);
            uInfo.put("STATUS", STATUS);
            uInfo.put("BIRTHDAY", BIRTHDAY);
            uInfo.put("ICREGNUMBER", "");

            Command cmd = new Command("app.uc.GetUserMapDao");
    		cmd.setParameter("user_id_map", ID);
    		cmd.setParameter("uInfo", uInfo);
    		DataSet ds = cmd.execute("GetUid");
    		
    		if (ds.getState() == StateType.SUCCESS) {
    			ID = ds.getJAData().getJSONObject(0).getString("USER_ID");
    		}
    		cmd = new Command("app.uc.LoginCmd");
    		JSONObject jo = new JSONObject();
    		jo.put("ACCOUNT", ACCOUNT);
    		jo.put("PHONE", PHONE);
    		jo.put("CARD_NO", CARD_NO);
    		jo.put("ID", ID);
    		jo.put("NAME", NAME);
    		jo.put("TYPE", 11);
    		jo.put("ORG_NAME","");
    		jo.put("ORG_NO","");
    		jo.put("CREDIT_CODE","");
    		
    		cmd.setParameter("jo", jo);
    		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));		
    		ds = cmd.execute("setUcUserInfo");
    		if (ds.getState() == StateType.FAILT) {
    			//_log.info("setUcUserInfo失败");
    		}
    		if("".equals(url)){
    			this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath()+"/index")); 
    		}else{
    			this.sendRedirect(HttpUtil.formatUrl(url)); 
    		}
    		//
       	}else{
    		System.out.println("调用返回结果 result："+loginR.getResult() +"；错误详细信息："+loginR.getErrmsg());
    		this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath()+"/index")); 
       	} 
		return false;
	}
}
