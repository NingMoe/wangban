package iop.oauth;

import java.util.ArrayList;
import java.util.List;

import iop.oauth.http.AccessToken;
import iop.org.json.JSONException;
import iop.org.json.JSONObject;
import iop.util.Config;
import iop.model.IopException;
import iop.model.PostParameter;

public class Oauth extends Iop {

	private static final long serialVersionUID = 7003420545330439247L;
	public String access_token;
	public String user_id;

	public String getToken() {
		return access_token;
	}

	/*
	 * 处理解析后的json解析
	 */
	public String ts(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			access_token = jsonObject.getString("access_token");
			user_id = jsonObject.getString("user_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return access_token;

	}

	/*----------------------------Oauth接口--------------------------------------*/

	public AccessToken getAccessTokenByCode(String code, String state)
			throws IopException {
		List<PostParameter> params = new ArrayList<PostParameter>();
		params.add(new PostParameter("client_id", Config.getValue("client_ID")));
		params.add(new PostParameter("client_secret", Config
				.getValue("client_SERCRET")));
		params.add(new PostParameter("grant_type", "authorization_code"));
		params.add(new PostParameter("code", code));
		params.add(new PostParameter("state", state));
		params.add(new PostParameter("redirect_uri", Config
				.getValue("redirect_URI")));
		return new AccessToken(client.post(Config.getValue("accessTokenURL"),
				params, false));
	}

	public AccessToken getAccessTokenByCredentials() throws IopException {
		List<PostParameter> params = new ArrayList<PostParameter>();
		params.add(new PostParameter("client_id", Config.getValue("client_ID")));
		params.add(new PostParameter("client_secret", Config
				.getValue("client_SERCRET")));
		params.add(new PostParameter("grant_type", "client_credentials"));
		if (Config.getValue("redirect_URI") != null) {
			params.add(new PostParameter("redirect_uri", Config
					.getValue("redirect_URI")));
		}
		return new AccessToken(client.post(Config.getValue("accessTokenURL"),
				params, false));
	}

	public String authorize(String response_type, String state,
			boolean forcelogin, boolean widget) throws IopException {
		if (widget) {
			return Config.getValue("authorizeURL").trim() + "?client_id="
					+ Config.getValue("client_ID").trim() + "&redirect_uri="
					+ Config.getValue("redirect_URI").trim()
					+ "&response_type=" + response_type + "&state=" + state
					+ "&forcelogin=" + forcelogin + "&app_url="
					+ Config.getValue("widget_URL").trim();
		} else {
			return Config.getValue("authorizeURL").trim() + "?client_id="
					+ Config.getValue("client_ID").trim() + "&redirect_uri="
					+ Config.getValue("redirect_URI").trim()
					+ "&response_type=" + response_type + "&state=" + state
					+ "&forcelogin=" + forcelogin;
		}
	}

	public String authorize(String response_type, String state,
			boolean forcelogin) throws IopException {
		return this.authorize(response_type, state, forcelogin, false);
	}
}
