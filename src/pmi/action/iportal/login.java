package action.iportal;

import iop.oauth.OpenApi;
import iop.org.json.JSONObject;
import iop.util.Config;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.icore.util.CacheManager;
import com.icore.util.Constant;
import com.inspur.base.BaseAction;
import com.inspur.bean.UserInfo;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		String website = this.getPageContext().getWebSite();
		String token=getParameter("token");
		UserInfo ui = this.getUserInfo();
		try {
			if(StringUtils.isNotEmpty(token))//code方式
			{
						String client_ID = Config.getValue("client_ID");
						String client_SERCRET = Config.getValue("client_SERCRET");
						OpenApi api = new OpenApi(client_ID,client_SERCRET);
						//设置access_token
						api.client.setToken(token);
						JSONObject jo = api.get_user_info();
						if(jo != null && jo.length()>0){
							String error = jo.getString("error");
							if(StringUtils.isNotEmpty(error)){
								this.write(jo.toString());
								return false;
							}
							//{"BIRTHDAY":"2015-03-23","NAME":"icity","IDENTITY_NUM":"","LAST_LOGIN_TIME":1432871486000,"ORG_NAME":"山东省发展和改革委员会","ROLE_VALUE":"ROLE_SYSTEM,","ROLE_CODE":"30107A4F45B04C71B964231F8AF0101D,","ORG_SHORT_CODE":null,"IS_ADMIN":0,"ACCOUNT":"icity","PHONE":"","REGION_NAME":"山东省","MOBILE":"31f0f259a3a1af06cb6ec9","GENDER":"0","ID":"AD51871DA7244BD5B22ECC99B749D124","ORG_CODE":"370000004502403","EMAIL":"c5c368a3d0af19993095","REGION_CODE":"370000000000","PASSWORD":"b44e4e83ea6c7f842e1c06d33eeed3f2","POSITION":"领导","CODE":"12345678","TYPE_CODE":"GWY"}
							JSONObject user = jo.getJSONObject("result");
							//String uid = user.getString("ID");
							ui = new UserInfo();
							ui.setUserId(user.getString("ACCOUNT"));
							ui.setUserName(user.getString("NAME"));
							ui.setDeptId(user.getString("ORG_CODE"));
							ui.setDeptName(user.getString("ORG_NAME"));
							ui.setRegionId(user.getString("REGION_CODE"));
							ui.setRegionName(user.getString("REGION_NAME"));
							CacheManager.set(Constant.SESSION_ID, getCookie(Constant.SESSION_ID), ui);
							//登录后，需判断用户的属性（进驻中心、未进驻中心），若是属于进驻中心的则进入到内网的界面
							//角色代码为ROLE_A的属于进驻中心的
							boolean isA=false;
							String igoto=website+"/iportal/index";
							String[] role_value=user.getString("ROLE_VALUE").split(",");
							for (String role : role_value) {
								if("ROLE_A".equalsIgnoreCase(role)){
									isA=true;
									break;
								}
							}
							if(!isA){
								igoto=Config.getValue("app.login.url");
							}
							this.sendRedirect(igoto);
							return false;
						}
			}
			String origUrl=Config.getValue("client_ID");
			String auth_url=Config.getValue("app.login.url")+"?origUrl="+origUrl;
			this.sendRedirect(auth_url);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
