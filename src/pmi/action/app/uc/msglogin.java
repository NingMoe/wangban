package action.app.uc;

import java.util.Map;

import net.sf.json.JSONObject;

import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.Tools;

public class msglogin extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String random = "";
	
	private enum ActionType {
		login, //登录action
		fail, //登录失败action
	};
 
	protected boolean IsVerify() {
		return false;
	}


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
		
		String phone = postData.get("PHONE");
		
		String code = (String)CacheManager.get("MessageCode", phone);
		
		DataSet ds = new DataSet();
		
		if(code.equals(postData.get("VCODE"))){
			
			Command cmd = new Command("app.uc.UserCmd");
			cmd.setParameter("phone",phone);
			ds = cmd.execute("getUser");
			cmd = new Command("app.uc.LoginCmd");
    		cmd.setParameter("jo", ds.getJOData());
    		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));		
    		ds = cmd.execute("setUcUserInfo");
			
    		ds.setData(null);
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
