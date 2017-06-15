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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import syan.app.icity.govservice.GovProjectDao;

import java.util.Map;

public class blogin extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactory.getLog(login.class);
	private final static String CACHE_KEY_FLAG = "getXyReturnUrl";		

	@SuppressWarnings("null")
	public boolean handler(Map<String,Object> data){
		String gotoUrl = this.getParameter("gotoUrl");
		String returnUrl = this.getParameter("returnUrl");
		//String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getXyReturnUrl" + this.getSessionId();
		CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, returnUrl);
		this.sendRedirect(HttpUtil.formatUrl(gotoUrl)); 		
		return false;
	}
}
