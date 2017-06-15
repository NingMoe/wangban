package action.iportal;

import iop.oauth.OpenApi;
import iop.org.json.JSONObject;
import iop.util.Config;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.icore.util.CacheManager;
import com.icore.util.Constant;
import com.inspur.base.BaseAction;
import com.inspur.bean.UserInfo;


public class index2 extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("null")
	public boolean handler(Map<String,Object> data){
		//String website = this.getPageContext().getWebSit();
		UserInfo ui = this.getUserInfo();
		ui = new UserInfo();
		ui.setUid(459);
		ui.setUserId("459");
		ui.setUserName("杨寅圣");
		ui.setDeptId("111");
		ui.setDeptName("某部");
		ui.setRegionId("330901000000");
		ui.setRegionName("舟山");
		ui.setMobile("18950471304");
		CacheManager.set(Constant.SESSION_ID, getCookie(Constant.SESSION_ID), ui);
		return true;
	}
}