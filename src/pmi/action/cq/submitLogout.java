package action.cq;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;




import com.icore.http.util.HttpUtil;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

public class submitLogout extends BaseAction {
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
		String returnUrl = this.getParameter("returnUrl");
		if (returnUrl == null || "".equals(returnUrl) || "null".equals(returnUrl)) {
			this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
		} else {
			this.sendRedirect(HttpUtil.formatUrl(returnUrl));
		}
		return false;
	}
}
