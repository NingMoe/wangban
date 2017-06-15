package action;

import iop.util.URLEncodeUtils;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import app.util.RestUtil;

import com.icore.bean.PageContext;
import com.icore.http.HttpResponseStatus;
import com.icore.http.adapter.HttpAdapter;
import com.icore.http.util.HttpUtil;
import com.icore.util.FrameConfig;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;

public class login extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}

	public boolean handler(Map<String, Object> data) {
		if ("zs".equals(SecurityConfig.getString("AppId"))) {
			String url = this.getContextPath() + "/bsp/check";
			String next = this.getParameter("goto");
			if (StringUtils.isNotEmpty(next)) {
				url += "?goto=" + URLEncodeUtils.encodeURL(next);
			}
			this.sendRedirect(HttpUtil.formatUrl(url));
			return false;
		} else if ("chq".equals(SecurityConfig.getString("AppId"))) {
			this.getHttpAdapter().sendError(HttpResponseStatus.BAD_REQUEST, "禁止访问");
			return false;
		}else if("1".equals(SecurityConfig.getString("UniteIdentifyFlag"))){
			String url = UrlHelper.formatUrl(SecurityConfig.getString("LoginUrl")
					+"&appmark="+SecurityConfig.getString("UserInfoappmark"));
			String website = getWebSite();
			String next = this.getParameter("goto");
			if (StringUtils.isNotEmpty(next)) {
				if(!next.contains(website)){
					next=website+next;
				}
				//在next中截取出用户类型字段userType 0：个人 1：法人
				String[] str=next.split("service_object=");
				if(str.length>1){
					//1 本地是：法人    对应大汉：2
					//法人
					if(str[1].equals("1")){
						url +="&userType=2";
					}else{
						if(str[1].equals("0")){
							//个人
							url +="&userType=1";
						}else{
							url +="&userType=0";
						}
					}
				}
				url += "&gotoUrl=" + URLEncodeUtils.encodeURL(next);
			}
			this.sendRedirect(HttpUtil.formatUrl(url));
			return false;
		}else if ("zhangzhou".equals(SecurityConfig.getString("AppId"))) {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ca_1_serviceurl"));
			JSONObject obj = null;
			String next = this.getParameter("goto");
			String getWebSite = getWebSite();
			if (StringUtils.isNotEmpty(next)) {
				next = HttpUtil.formatUrl(getWebSite + "/zhangzhou/login?goto=" + URLEncodeUtils.encodeURL(next));
			}
			try{
				obj = JSONObject.fromObject(RestUtil.getData(url));
				String access_token = obj.getString("access_token");
				if(obj.getBoolean("result")){
					url = HttpUtil.formatUrl(SecurityConfig.getString("ca_2_serviceurl")+"?access_token="+access_token+"&return_url="+next);
					obj = JSONObject.fromObject(RestUtil.getData(url));
					if(obj.getBoolean("result")){
						url=obj.getString("login_url");
						JSONObject access = new JSONObject();
						access.put("access_token", access_token);
						CacheManager.set(CacheManager.EhCacheType, "zhangzhoulogin", "zhangzhoulogin", access);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}			
			this.sendRedirect(HttpUtil.formatUrl(url));
			return false;
		}else{
			String url = this.getContextPath() + "/uc/login";
			String next = this.getParameter("goto");
			if (StringUtils.isNotEmpty(next)) {
				url += "?goto=" + URLEncodeUtils.encodeURL(next);
			}
			this.sendRedirect(HttpUtil.formatUrl(url));
			return false;
		}
	}
	public String getWebSite(){
		HttpAdapter ha=this.getHttpAdapter();
		String host = ha.getHeader("Host");
		host = StringUtil.replace(host, "http://", "");
		String protocol = "http://";
		return protocol+host;
	}
}
