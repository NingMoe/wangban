package action.oauth2;

import java.util.Map;

import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;

public class logout extends BaseAction {
	private static final long serialVersionUID = 1L;
	private static String cookieName = SecurityConfig.getString("COOKIE_NAME");
	
	private enum ActionType {
		out
	};
 
	public boolean handler(Map<String, Object> data) {
		ActionType actionType = ActionType.valueOf(this.getParameter("action"));
		boolean result = false;
		
		switch (actionType) {
		case out:
			result = this.outAction(data);
			break;
		default:
			break;
		}
		return result;
	}
	
	private boolean outAction(Map<String, Object> data){
		
		DataSet ds = new DataSet();
		
		CacheManager.evict(Constant.SESSIONID,this.getCookie(Constant.SESSIONID));
	    CacheManager.clear(cookieName);
	    this.setCookie(cookieName, "");
	    
		this.write(ds.toJson());
		return false;
	}
}
