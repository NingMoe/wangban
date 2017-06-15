package action.xy;

import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import core.util.HttpClientUtil;
import iop.util.URLEncodeUtils;
import net.sf.json.JSONObject;
import java.util.Map;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String CACHE_KEY_FLAG = "getXyReturnUrl";

	@SuppressWarnings("null")
	public boolean handler(Map<String, Object> data) {
		// _log.info("检测是否访问到该页面！！！！");
		String code = this.getParameter("code"); // 2d8df2a6b616a47421b4db36a8a67
		String url = SecurityConfig.getString("login_xy_token")
				+ URLEncodeUtils.encodeURL(SecurityConfig.getString("login_xy_url")) + "&code=" + code;
		HttpClientUtil client = new HttpClientUtil();
		JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
		String access_token = (String) item.get("access_token");
		String urlUser = SecurityConfig.getString("login_xy_user") + access_token;
		JSONObject itemUser = JSONObject.fromObject(client.getResult(urlUser, ""));
		System.out.println("itemUser:"+itemUser.toString());
		String ACCOUNT = (String) itemUser.get("userName");
		String PHONE = (String) itemUser.get("tel");
		String CARD_NO = (String) itemUser.get("creditID");
		String ID = (String) itemUser.get("userId");
		String trueName = (String) itemUser.get("trueName");
		String lastName = (String) itemUser.get("lastName");		
		
		Command cmd = new Command("app.uc.LoginCmd");
		JSONObject jo = new JSONObject();
		jo.put("TYPE", "2".equals(lastName) ? "21" : "11");
		if("2".equals(lastName)){
			trueName = (String) itemUser.get("IDSEXT_enterpriseName");
			CARD_NO = (String) itemUser.get("IDSEXT_regNumber");
			jo.put("ORG_NAME", (String) itemUser.get("IDSEXT_enterpriseName"));
		}
		jo.put("ACCOUNT", ACCOUNT == null ? "" : ACCOUNT);
		jo.put("PHONE", PHONE == null ? "" : PHONE);		
		jo.put("ID", ID == null ? "" : ID);
		jo.put("NAME", trueName == null ? "" : trueName);		
		jo.put("CARD_NO", CARD_NO == null ? "" : CARD_NO);		
		cmd.setParameter("jo", jo);
		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
		DataSet ds = cmd.execute("setUcUserInfo");
		if (ds.getState() == StateType.FAILT) {
			System.out.println("setUcUserInfo失败");
		}
		String key = "getXyReturnUrl" + this.getSessionId();
		System.out.println("key:"+key);
		String returnUrl = (String) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
		System.out.println("returnUrl:"+returnUrl);
		if (returnUrl == null || "".equals(returnUrl) || "null".equals(returnUrl)) {
			this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
		} else {
			this.sendRedirect(HttpUtil.formatUrl(returnUrl));
		}
		return false;
	}
}
