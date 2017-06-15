package action;

import java.util.Map;

import net.sf.json.JSONObject;

import com.inspur.base.BaseAction;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;

public class verify extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		String code = this.getSessionId();
		UserInfo ui =(UserInfo)CacheManager.get(Constant.SESSIONID,code);
		JSONObject result = new JSONObject();
		//没有登录
		if (ui == null) {
			result.put("error", "system_error");
			result.put("message", "登录已失效!");
			result.put("state", "0");
		}else{
			result.put("error", "");
			result.put("message", "已登录!");
			result.put("state", "1");
		}
		this.write(result.toString());
		return false;
	}
}
