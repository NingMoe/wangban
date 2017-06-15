package action.cq;

import app.pmi.validation.DesUtil;

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
import iop.http.BASE64Encoder;
import iop.util.URLEncodeUtils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import sun.misc.BASE64Decoder;

public class submitLogin extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String CACHE_KEY_FLAG = "getXyReturnUrl";

	@SuppressWarnings("null")
	public boolean handler(Map<String, Object> data) {
		String id = this.getParameter("UserID");
		try{
			id = DesUtil.decrypt(id);
		}catch(Exception e){
			e.printStackTrace();
		}		
		String returnUrl = this.getParameter("returnUrl");
		Command cmd = new Command("app.uc.UserCmd");
		cmd.setParameter("id", id);
		DataSet ds = cmd.execute("getList");
		if(ds.getState()==StateType.SUCCESS&&ds.getTotal()>0){
			Command m_cmd = new Command("app.uc.LoginCmd");
			JSONObject jo = new JSONObject();
			jo.put("ACCOUNT", ds.getJAData().getJSONObject(0).getString("ACCOUNT"));
			jo.put("PHONE", ds.getJAData().getJSONObject(0).getString("PHONE"));
			jo.put("CARD_NO", ds.getJAData().getJSONObject(0).getString("CARD_NO"));
			jo.put("ID", ds.getJAData().getJSONObject(0).getString("ID"));
			jo.put("NAME", ds.getJAData().getJSONObject(0).getString("NAME"));
			jo.put("TYPE", "11");
			m_cmd.setParameter("jo", jo);
			m_cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
			DataSet m_ds = m_cmd.execute("setUcUserInfo");
			if (returnUrl == null || "".equals(returnUrl) || "null".equals(returnUrl)) {
				this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
			} else {
				this.sendRedirect(HttpUtil.formatUrl(returnUrl));
			}
		}else{
			this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
		}
		return false;
	}	
}
