package fz.action;

import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.icore.http.adapter.HttpAdapter;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;
import com.inspur.util.Tools;

public class logout extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String ssoService = SecurityConfig.getString("SSOService");
	private static String cookieName = SecurityConfig.getString("cookieName");

	@Override
	public boolean handler(Map<String, Object> data) throws Exception {
		
		String webRank = SecurityConfig.getString("WebRank");
		
		if("1".equals(webRank)){
			
			HttpAdapter httpAdapter = this.getHttpAdapter();
			NameValuePair[] params = new NameValuePair[4];
			params[0] = new NameValuePair("action", "logout");
			params[1] = new NameValuePair("cookieName", this.getCookie(cookieName));
			
			String userAgent = httpAdapter.getHeader("User-Agent").toLowerCase();
			StringTokenizer st = new StringTokenizer(userAgent, ";");
			st.nextToken();
			String brower = "";
			try{
				brower = st.nextToken().trim();
			}catch(Exception e){
				brower = "未知浏览器";
			}
			params[2] = new NameValuePair("brower", brower);
			params[3] = new NameValuePair("deptCode", "1010003");//系统编码（需根据实际的系统编码）
			try {
				post(params);
				CacheManager.evict(Constant.SESSIONID,this.getCookie(Constant.SESSIONID));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			} finally {
				this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
			}
		}else{
			
			CacheManager.evict(Constant.SESSIONID,this.getCookie(Constant.SESSIONID));
			setCookie(Constant.SESSIONID, Tools.getUUID32());
			String nextgo = "";
			try{
				nextgo=URLDecoder.decode(this.getParameter("goto"),"UTF-8");
			}catch(Exception e){
				e.printStackTrace();
			}
			if(!StringUtils.isNotEmpty(nextgo)){
				nextgo = this.getContextPath()+"/"+SecurityConfig.getString("MainPage");
			}
			this.sendRedirect(nextgo);
		}
		
		return false;

	}

	private JSONObject post(NameValuePair[] params) {
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(ssoService);
		postMethod.addParameters(params);
		try {
			switch (httpClient.executeMethod(postMethod)) {
			case HttpStatus.SC_OK:
				return JSONObject.parseObject(postMethod
						.getResponseBodyAsString());
			default:
				// 其它处理
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
