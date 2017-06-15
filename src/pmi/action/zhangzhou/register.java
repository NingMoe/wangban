package action.zhangzhou;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import net.sf.json.JSONObject;

import java.util.Map;

public class register extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("null")
	public boolean handler(Map<String, Object> data) {
		JSONObject access_token_json = (JSONObject)CacheManager.get(CacheManager.EhCacheType, "zhangzhoulogin", "zhangzhoulogin");
		String next = this.getParameter("goto");
		next = HttpUtil.formatUrl(next);
		JSONObject obj=null;
		String access_token="";
		String user_type = this.getParameter("user_type");	
		try{
			if(access_token_json==null){
				String url = HttpUtil.formatUrl(SecurityConfig.getString("ca_1_serviceurl"));
				obj = JSONObject.fromObject(RestUtil.getData(url));
				access_token = obj.getString("access_token");
			}else{
				access_token = access_token_json.getString("access_token");
			}
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ca_3_serviceurl")+"?access_token="+access_token+
					"&user_type="+user_type+"&return_url="+next);
			obj = JSONObject.fromObject(RestUtil.getData(url));
		}catch(Exception e){
			e.printStackTrace();
		}
		if(obj!=null&&obj.getBoolean("result")){			
			this.sendRedirect(HttpUtil.formatUrl(obj.getString("registered_url")));
		}else{
			this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
		}	
		return false;
	}	
}
