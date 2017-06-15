package action.oauth2;

import iop.util.URLEncodeUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.Command;
import com.inspur.util.Constant;

public class login extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	String client_id = SecurityConfig.getString("CLIENT_ID");
	private static String auth_url = SecurityConfig.getString("AUTH_URL");
	private static String cookieName = SecurityConfig.getString("COOKIE_NAME");

	@Override
	public boolean handler(Map<String, Object> data) throws Exception {
		
		String gotoUrl = this.getParameter("goto");
		String postResult = this.getCookie(cookieName);
		if (StringUtils.isEmpty(gotoUrl))
			gotoUrl = UrlHelper.index(this.getHttpAdapter());
		UserInfo ui = this.getUserInfo();
		if (ui != null) {
			this.sendRedirect(gotoUrl);
		} else {
			String url = HttpUtil.formatUrl(auth_url+"login?client_id="+client_id+"&utype=0&goto=" + URLEncodeUtils.encodeURL(gotoUrl));
			authCookie(postResult, gotoUrl, url);
		}
			
		return false;
	}

	public void authCookie(String postResult, String gotoUrl, String url) {
		if (StringUtils.isEmpty(postResult)) {
			this.sendRedirect(url);
			return;
		}
		try {
			net.sf.json.JSONObject uInfo = new net.sf.json.JSONObject();
			Map<String,String> map = mapStringToMap(postResult);  
			String userType = "1".equals(map.get("USER_TYPE")) ? "11": "21";
			if("11".equals(userType)){
				uInfo.put("NAME", map.get("USER_NAME"));
				uInfo.put("ACCOUNT", map.get("LOGIN_NAME"));
				uInfo.put("CARD_NO", map.get("CARD_NO"));
				uInfo.put("BIRTHDAY", "null".equals(map.get("BIRTHDAY"))?"":map.get("BIRTHDAY"));
				uInfo.put("SEX", "null".equals(map.get("SEX"))?"":map.get("SEX"));
				uInfo.put("ORG_NO", "");
				uInfo.put("ORG_NAME", "");
				uInfo.put("CREDIT_CODE", "");
				uInfo.put("ICREGNUMBER", "");
			}else if ("21".equals(userType)) {
				uInfo.put("NAME", map.get("CORPORATE_NAME"));
				uInfo.put("ACCOUNT", map.get("CORPORATE_LOGIN_NAME"));
				uInfo.put("CARD_NO", "");
				uInfo.put("ORG_NO", "null".equals(map.get("USER_ID"))?"":map.get("USER_ID"));
				uInfo.put("ORG_NAME", map.get("CORPORATE_NAME"));
				uInfo.put("ORG_BOSS_NO", map.get("REPRESENTATIVE_CARD_NO"));
				uInfo.put("ORG_BOSS_NAME", map.get("REPRESENTATIVE_NAME"));
				uInfo.put("CREDIT_CODE", "null".equals(map.get("CORPORATE_CODE"))?"":map.get("CORPORATE_CODE"));
				uInfo.put("ICREGNUMBER", "null".equals(map.get("CORPORATE_CODE"))?"":map.get("CORPORATE_CODE"));
			}
			uInfo.put("TYPE", userType);
			uInfo.put("PHONE", "null".equals(map.get("MOBILE"))?"":map.get("MOBILE"));
			uInfo.put("EMAIL", "null".equals(map.get("EMAIL"))?"":map.get("EMAIL"));
            uInfo.put("STATUS", map.get("STATUS"));

            Command cmd = new Command("app.uc.GetUserMapDao");
			cmd.setParameter("user_id_map", map.get("USER_ID"));
			cmd.setParameter("uInfo", uInfo);
			DataSet ds = cmd.execute("GetUid");
			if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
				String ID = ds.getJAData().getJSONObject(0).getString("USER_ID");
				uInfo.put("ID", ID);
				cmd = new Command("app.uc.LoginCmd");
				cmd.setParameter("jo", uInfo);
				cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
				ds = cmd.execute("setUcUserInfo");
				if (ds.getState() == StateType.FAILT) {
					// 为防止重定向，死循环，出错直接返回首页
					this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
				} else {
					this.sendRedirect(gotoUrl);
				}
			} else {
				// 为防止重定向，死循环，出错直接返回首页
				this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 为防止重定向，死循环，出错直接返回首页
			this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
		}
	}
	
	public static Map<String,String> mapStringToMap(String str){
		String str1 = str.replaceAll("\\{|\\}", "");
        String str2 = str1.replaceAll(" ", "");  
        String str3 = str2.replaceAll(",", "&");  
		Map<String, String> map = null;  
		if ((null != str3) && (!"".equals(str3.trim()))){  
		  String[] resArray = str3.split("&");  
		  if (0 != resArray.length){  
		    map = new HashMap(resArray.length);  
		    for (String arrayStr : resArray) {  
		      if ((null != arrayStr) && (!"".equals(arrayStr.trim()))){  
		        int index = arrayStr.indexOf("=");  
		        if (-1 != index) {  
		          map.put(arrayStr.substring(0, index), arrayStr.substring(index + 1));  
		        }  
		      }  
		    }  
		  }  
		}  
		return map; 
	}
}
