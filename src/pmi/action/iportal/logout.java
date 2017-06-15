package action.iportal;

import iop.util.Config;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;




import com.inspur.base.BaseAction;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

public class logout extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	public boolean handler(Map<String,Object> data){
		CacheManager.evict(Constant.SESSIONID,this.getCookie(Constant.SESSIONID));
		setCookie(Constant.SESSIONID, Tools.getUUID32());
/*		String state= Tools.random(10000000,99999999).toString();
		String logout=Config.getValue("logoutURL")
		+"?client_id="
		+ Config.getValue("client_ID").trim() 
		+ "&redirect_uri="
		+ Config.getValue("redirect_URI").trim()
		+ "&response_type=code"
		+ "&state="+state
		+ "&forcelogin=false";
		this.sendRedirect(logout);*/
		String origUrl=Config.getValue("client_ID");
		String logout_url=Config.getValue("app.logout.url")+"?origUrl="+origUrl;
		this.sendRedirect(logout_url);
		return false;
	}
}
