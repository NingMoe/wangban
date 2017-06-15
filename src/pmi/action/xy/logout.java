package action.xy;

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
		String nextgo = SecurityConfig.getString("logout_xy_url");
		this.sendRedirect(nextgo);
		return false;
	}
}
