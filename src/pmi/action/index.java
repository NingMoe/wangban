package action;

import java.util.Map;

import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.SecurityConfig;

public class index extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	public boolean handler(Map<String,Object> data){
		this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
		return false;
	}
}
