package fz.action;

import iop.util.URLEncodeUtils;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.icore.http.adapter.HttpAdapter;
import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.Command;
import com.inspur.util.Constant;

public class login extends BaseAction {
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
			String website = this.getPageContext().getWebSite();
			String gotoURL = this.getParameter("goto");
			String ticket = this.getCookie(cookieName);
			if (StringUtils.isEmpty(gotoURL))
				gotoURL = UrlHelper.index(this.getHttpAdapter());
			UserInfo ui = this.getUserInfo();
			if (ui != null) {
				this.sendRedirect(gotoURL);
			} else {
				String URL = HttpUtil.formatUrl(ssoService
						+ "?action=preLogin&setCoAddress=" + website
						+ "/setCo&gotoURL=" + URLEncodeUtils.encodeURL(gotoURL)
						+ "&deptCode=1010003&jumpLogin=");// 系统编码（需根据实际的系统编码）
				authCookie(ticket, gotoURL, URL);
			}
		}else{
			String url = this.getContextPath() + "/uc/login";
			String next = this.getParameter("goto");
			if (StringUtils.isNotEmpty(next)) {
				url += "?goto=" + URLEncodeUtils.encodeURL(next);
			}
			this.sendRedirect(HttpUtil.formatUrl(url));
		}
		
		return false;

	}

	public void authCookie(String ticket, String gotoURL, String URL) {
		if (StringUtils.isEmpty(ticket)) {
			this.sendRedirect(URL);
			return;
		}
		HttpAdapter httpAdapter = this.getHttpAdapter();
		NameValuePair[] params = new NameValuePair[4];
		params[0] = new NameValuePair("action", "authTicket");
		params[1] = new NameValuePair("cookieName", ticket);

		String userAgent = httpAdapter.getHeader("User-Agent").toLowerCase();
		StringTokenizer st = new StringTokenizer(userAgent, ";");
		st.nextToken();
		String brower = "";
		try {
			brower = st.nextToken().trim();
		} catch (Exception e) {
			brower = "未知浏览器";
		}
		params[2] = new NameValuePair("brower", brower);
		params[3] = new NameValuePair("url", URL);
		try {
			net.sf.json.JSONObject uInfo = new net.sf.json.JSONObject();
			JSONObject result = post(params);
			if (result == null || result.getBoolean("error")) {
				this.sendRedirect(URL);
				return;
			} else {
				String userType = "0".equals(result.getString("usertype")) ? "11"
						: "21";
				uInfo.put("ACCOUNT", result.getString("username"));
				uInfo.put("NAME", result.getString("alias"));
				if ("21".equals(userType)) {
					uInfo.put("ORG_NAME", result.getString("alias"));}
				uInfo.put("TYPE", userType);
				uInfo.put("CARD_NO", result.getString("certificate"));
				uInfo.put("PHONE", "null".equals(result.getString("phone"))?""
							:result.getString("phone"));
                uInfo.put("SEX", "null".equals(result.getString("sex"))?""
						:result.getString("sex"));
                uInfo.put("ADDRESS", "null".equals(result.getString("address"))?""
						:result.getString("address"));
                uInfo.put("STATUS", result.getString("stauts"));
                uInfo.put("BIRTHDAY", "null".equals(result.getString("birthday"))?""
						:result.getString("birthday"));
				// Ca登录参数
				uInfo.put("PROVINCE", "null".equals(result.getString("province")) ? "" 
							: result.getString("province"));
				uInfo.put("CITY", "null".equals(result.getString("city")) ? ""
							: result.getString("city"));
				uInfo.put("CREDIT_CODE", "null".equals(result.getString("uniformCreditCode")) ? ""
						: result.getString("uniformCreditCode"));
				uInfo.put("ICREGNUMBER", "null".equals(result.getString("icregnumber")) ? ""
						: result.getString("icregnumber"));
				uInfo.put("ORG_NO", "null".equals(result.getString("organizationCode")) ? ""
						: result.getString("organizationCode"));
				uInfo.put("SSNUMBER", "null".equals(result.getString("ssnumber")) ? ""
							: result.getString("ssnumber"));
				Command cmd = new Command("app.uc.GetUserMapDao");
				cmd.setParameter("user_id_map", result.getString("uid"));
				cmd.setParameter("uInfo", uInfo);
				DataSet ds = cmd.execute("GetUid");
				if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
					String ID = ds.getJAData().getJSONObject(0)
							.getString("USER_ID");
					uInfo.put("ID", ID);
					cmd = new Command("app.uc.LoginCmd");
					cmd.setParameter("jo", uInfo);
					cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
					ds = cmd.execute("setUcUserInfo");
					if (ds.getState() == StateType.FAILT) {
						// 为防止重定向，死循环，出错直接返回首页
						this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
					} else {
						this.sendRedirect(gotoURL);
					}
				} else {
					// 为防止重定向，死循环，出错直接返回首页
					this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// 为防止重定向，死循环，出错直接返回首页
			this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
		}
	}

	private JSONObject post(NameValuePair[] params) {
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(ssoService);
		postMethod.addParameters(params);
		try {
			switch (httpClient.executeMethod(postMethod)) {
			case HttpStatus.SC_OK:
				return new JSONObject(postMethod.getResponseBodyAsString());
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
