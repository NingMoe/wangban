package iop.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iop.org.json.JSONObject;
import iop.util.Config;
import iop.http.Response;
import iop.model.IopException;
import iop.model.PostParameter;

public class OpenApi extends Iop {
	public static final String baseURL = Config.getValue("baseURL");
	public static boolean isEndWithSlash = false;
	static {
		isEndWithSlash = baseURL.endsWith("/");
	}
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";

	private static final long serialVersionUID = 3816005087976772682L;
	public String client_id = Config.getValue("client_ID");
	public String client_secret = Config.getValue("client_SERCRET");

	public OpenApi() {
	}

	public OpenApi(String id, String secret) {
		client_id = id;
		client_secret = secret;
	}

	public void setAccessTokenString(String token) {
		super.setToken(token);
	}

	public static String time() {
		long time = System.currentTimeMillis();
		String t = String.valueOf(time);
		return t.substring(0, 10);
	}

	private static String getRequestURL(String command) {
		StringBuilder sb = new StringBuilder(baseURL);
		boolean commandStartWithSlash = command.startsWith("/");
		if (isEndWithSlash ^ commandStartWithSlash) {
			sb.append(command);
		} else if (isEndWithSlash) {
			sb.append(command.substring(1));
		} else {
			sb.append("/").append(command);
		}
		return sb.toString();
	}

	/**
	 * @deprecated
	 * @param Command
	 * @param params
	 * @return
	 * @throws PAEException
	 */
	public JSONObject sendCommand(String command, PostParameter[] params)
			throws IopException {
		List<PostParameter> paramsList = null;
		if (params != null) {
			paramsList = Arrays.asList(params);
		}
		return this.sendCommand(command, "1.0", null, paramsList);
	}

	public JSONObject sendCommand(String Command) throws IopException {
		return this.sendCommand(Command, "1.0", null, null);
	}

	/**
	 * 调用接口的通用方法
	 * 
	 * @param Command
	 *            ：接口名，组成为"服务上线文/服务版本名称"，例如:"/weather/bj/v1"
	 * @param params
	 *            ：该接口所需参数 PostParameter格式
	 * @return：json格式的数据
	 * @throws PAEException
	 */
	public JSONObject sendCommand(String command, List<PostParameter> params)
			throws IopException {
		return this.sendCommand(command, "1.0", null, params);
	}

	public JSONObject sendCommand(String command, String version,
			String httpMethod, List<PostParameter> params) throws IopException {
		List<PostParameter> allParams = new ArrayList<PostParameter>();
		allParams.add(new PostParameter("client_id", client_id));
		if (params != null) {
			allParams.addAll(params);
		}
		if (version != null && version.trim().length() > 0) {
			if (command.endsWith("/")) {
				command = command + version;
			} else {
				command = command + "/" + version;
			}
		}
		if (httpMethod != null) {
			if (httpMethod.equalsIgnoreCase("post")) {
				return client.post(getRequestURL(command), allParams)
						.asJSONObject();
			}

		}
		return client.get(getRequestURL(command), allParams).asJSONObject();
	}

	/****************** 以下是对接口的进一步封装示例 ***************************/
	/**
	 * 获取当前用户基本信息
	 * 
	 * @deprecated仅适用于版本1.0
	 * @return 
	 * 
	 */
	public JSONObject get_user_info() throws IopException {
		return this.sendCommand("basic_user_info/get_user_info");
	}
}
