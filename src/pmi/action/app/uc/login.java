package action.app.uc;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import app.uc.LoginDao;
import app.uc.UserDao;
import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.Tools;
import com.inspur.util.URLEncode;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tokenid = "";
	private String random = "";
	
	private enum ActionType {
		login, //登录action
		fail, //登录失败action
	};

	public boolean handler(Map<String, Object> data) {
		ActionType actionType = ActionType.valueOf(this.getParameter("action"));
		boolean result = false;
		
		switch (actionType) {
		case login:
			result = this.loginAction(data);
			break;
		case fail:
			result = this.failAction(data);
			break;
		default:
			break;
		}
		return result;
	}
	
	private boolean loginAction(Map<String, Object> data){
		/**
		 * 登录验证
		 */
		Map<String, String> postData = this.getPostData();
		
		Command cmd = new Command("app.uc.LoginCmd");
		cmd.setParameter("type", postData.get("type"));
		cmd.setParameter("sessionId", this.getSessionId());
		cmd.setParameter("account", postData.get("account"));
		cmd.setParameter("pwd", postData.get("pwd"));
		cmd.setParameter("verify_code", postData.get("verify_code"));
		cmd.setParameter("remoteAddr", this.getRemoteAddr());
		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
		DataSet ds = cmd.execute("checkLogin");

		
		// 记录登录失败次数
		if (ds.getState() == StateType.FAILT) {
			Object fail_time = CacheManager.get("login_fail_time", this.getRemoteAddr());
			int login_fail_time = 1; // 登录失败次数默认为1
			if (fail_time != null) {
				login_fail_time = (Integer) fail_time + 1;
			}
			CacheManager.set("login_fail_time", this.getRemoteAddr(), login_fail_time);
			
			JSONObject rjo = new JSONObject();
			rjo.put("login_fail_time", login_fail_time);
			ds.setData(Tools.jsonToBytes(rjo));
		} else {
			CacheManager.evict("login_fail_time", this.getRemoteAddr());
			String gotoUrl = postData.get("goto");
			if (StringUtils.isNotEmpty(gotoUrl)) {
				gotoUrl = URLEncode.decodeURL(gotoUrl);
			} else {
				gotoUrl = UrlHelper.index(this.getHttpAdapter());;
			}
			JSONObject rjo = new JSONObject();
			rjo.put("nextGo", HttpUtil.formatUrl(gotoUrl));
			rjo.put("tickId", this.getUserInfo().getTickId());
			ds.setData(Tools.jsonToBytes(rjo));
			
		}

		this.write(ds.toJson());
		return false;
	}

	private boolean failAction(Map<String, Object> data){
		DataSet ds = new DataSet();
		JSONObject jo = new JSONObject();
		Object fail_time = CacheManager.get("login_fail_time", this.getRemoteAddr());
		if (fail_time == null) {
			jo.put("login_fail_time", 0);
		} else {
			jo.put("login_fail_time", (Integer) fail_time + 1);
		}
		ds.setData(Tools.jsonToBytes(jo));
		this.write(ds.toJson());
		return false;
	}
}
