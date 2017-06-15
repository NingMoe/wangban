package iop.oauth.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

import iop.org.json.JSONException;
import iop.http.ImageItem;
import iop.http.Response;
import iop.model.Configuration;
import iop.model.IopException;
import iop.model.MySSLSocketFactory;
import iop.model.Paging;
import iop.model.PostParameter;

public class HttpClient implements java.io.Serializable {

	private static final long serialVersionUID = -176092625883595547L;

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";

	private static final int OK = 200; // OK: Success!
	private static final int FORBIDDEN = 500;
	private static final int SERVICE_UNAVAILABLE = 503;
	private String proxyHost = Configuration.getProxyHost();
	private int proxyPort = Configuration.getProxyPort();
	private String proxyAuthUser = Configuration.getProxyUser();
	private String proxyAuthPassword = Configuration.getProxyPassword();
	private String token;

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = Configuration.getProxyHost(proxyHost);
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = Configuration.getProxyPort(proxyPort);
	}

	public String getProxyAuthUser() {
		return proxyAuthUser;
	}

	public void setProxyAuthUser(String proxyAuthUser) {
		this.proxyAuthUser = Configuration.getProxyUser(proxyAuthUser);
	}

	public String getProxyAuthPassword() {
		return proxyAuthPassword;
	}

	public void setProxyAuthPassword(String proxyAuthPassword) {
		this.proxyAuthPassword = Configuration
				.getProxyPassword(proxyAuthPassword);
	}

	public String setToken(String token) {
		this.token = token;
		return this.token;
	}

	private final static boolean DEBUG = Configuration.getDebug();
	static Logger log = Logger.getLogger(HttpClient.class.getName());
	static org.apache.commons.httpclient.HttpClient client = null;

	private static MultiThreadedHttpConnectionManager connectionManager;
	private int maxSize;

	public HttpClient() {
		this(150, 30000, 30000, 1024 * 1024);
	}

	public HttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs,
			int maxSize) {
		connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setDefaultMaxConnectionsPerHost(maxConPerHost);
		params.setConnectionTimeout(conTimeOutMs);
		params.setSoTimeout(soTimeOutMs);

		HttpClientParams clientParams = new HttpClientParams();
		// 忽略cookie 避免 Cookie rejected 警告
		clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		client = new org.apache.commons.httpclient.HttpClient(clientParams,
				connectionManager);
		Protocol myhttps = new Protocol("https", new MySSLSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		this.maxSize = maxSize;
		// 支持proxy
		if (proxyHost != null && !proxyHost.equals("")) {
			client.getHostConfiguration().setProxy(proxyHost, proxyPort);
			client.getParams().setAuthenticationPreemptive(true);
			if (proxyAuthUser != null && !proxyAuthUser.equals("")) {
				client.getState().setProxyCredentials(
						AuthScope.ANY,
						new UsernamePasswordCredentials(proxyAuthUser,
								proxyAuthPassword));
				log("Proxy AuthUser: " + proxyAuthUser);
				log("Proxy AuthPassword: " + proxyAuthPassword);
			}
		}
	}

	/**
	 * log调试
	 * 
	 */
	private static void log(String message) {
		if (DEBUG) {
			if(log.isDebugEnabled()){
				log.debug(message);
			}
		}
	}

	/**
	 * 处理http getmethod 请求
	 * 
	 */

	public Response get(String url) throws IopException {

		return get(url, null);

	}

	public Response get(String url, List<PostParameter> params)
			throws IopException {
		log("Request:");
		log("GET:" + url);
		if (params == null) {
			params = new ArrayList<PostParameter>();
		}
		params.add(new PostParameter("access_token", token));
		String encodedParams = HttpClient.encodeParameters(params);
		if (-1 == url.indexOf("?")) {
			url += "?" + encodedParams;
		} else {
			url += "&" + encodedParams;
		}
		GetMethod getmethod = new GetMethod(url);
		return httpRequest(getmethod);

	}

	public Response get(String url, List<PostParameter> params, Paging paging)
			throws IopException {
		List<PostParameter> pagingParams = null;
		if (null != paging) {
			pagingParams = new ArrayList<PostParameter>(4);
			if (-1 != paging.getMaxId()) {
				pagingParams.add(new PostParameter("max_id", String
						.valueOf(paging.getMaxId())));
			}
			if (-1 != paging.getSinceId()) {
				pagingParams.add(new PostParameter("since_id", String
						.valueOf(paging.getSinceId())));
			}
			if (-1 != paging.getPage()) {
				pagingParams.add(new PostParameter("page", String
						.valueOf(paging.getPage())));
			}
			if (-1 != paging.getCount()) {
				if (-1 != url.indexOf("search")) {
					// search api takes "rpp"
					pagingParams.add(new PostParameter("rpp", String
							.valueOf(paging.getCount())));
				} else {
					pagingParams.add(new PostParameter("count", String
							.valueOf(paging.getCount())));
				}
			}
		}
		if (params == null) {
			return get(url, pagingParams);
		} else {
			if (pagingParams != null) {
				params.addAll(pagingParams);
			}
			return get(url, params);
		}
	}

	/**
	 * 处理http deletemethod请求
	 */

	public Response delete(String url, List<PostParameter> params)
			throws IopException {
		if (0 != params.size()) {
			String encodedParams = HttpClient.encodeParameters(params);
			if (-1 == url.indexOf("?")) {
				url += "?" + encodedParams;
			} else {
				url += "&" + encodedParams;
			}
		}
		DeleteMethod deleteMethod = new DeleteMethod(url);
		return httpRequest(deleteMethod);

	}

	/**
	 * 处理http post请求
	 * 
	 */
	public Response post(String url, List<PostParameter> params)
			throws IopException {
		return post(url, params, true);

	}

	public Response post(String url, List<PostParameter> params,
			Boolean WithTokenHeader) throws IopException {
		log("Request:");
		log("POST" + url);
		PostMethod postMethod = new PostMethod(url);
		postMethod.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;;charset=UTF-8");
		if (params != null) {
			for (PostParameter param : params) {
				postMethod.addParameter(param.getName(), param.getValue());
			}
		}
		if (token != null) {
			postMethod.addParameter("access_token", token);
		}
		HttpMethodParams param = postMethod.getParams();
		param.setContentCharset("UTF-8");
		if (WithTokenHeader) {
			return httpRequest(postMethod);
		} else {
			return httpRequest(postMethod, WithTokenHeader);
		}
	}

	/**
	 * 支持multipart方式上传图片
	 * 
	 */
	public Response multPartURL(String url, PostParameter[] params,
			ImageItem item) throws IopException {
		PostMethod postMethod = new PostMethod(url);
		try {
			Part[] parts = null;
			if (params == null) {
				parts = new Part[1];
			} else {
				parts = new Part[params.length + 1];
			}
			if (params != null) {
				int i = 0;
				for (PostParameter entry : params) {
					parts[i++] = new StringPart(entry.getName(),
							(String) entry.getValue());
				}
				parts[parts.length - 1] = new ByteArrayPart(item.getContent(),
						item.getName(), item.getContentType());
			}
			postMethod.setRequestEntity(new MultipartRequestEntity(parts,
					postMethod.getParams()));
			return httpRequest(postMethod);

		} catch (Exception ex) {
			throw new IopException(ex.getMessage(), ex, -1);
		}
	}

	public Response multPartURL(String fileParamName, String url,
			PostParameter[] params, File file, boolean authenticated)
			throws IopException {
		PostMethod postMethod = new PostMethod(url);
		try {
			Part[] parts = null;
			if (params == null) {
				parts = new Part[1];
			} else {
				parts = new Part[params.length + 1];
			}
			if (params != null) {
				int i = 0;
				for (PostParameter entry : params) {
					parts[i++] = new StringPart(entry.getName(),
							(String) entry.getValue());
				}
			}
			FilePart filePart = new FilePart(fileParamName, file.getName(),
					file, new MimetypesFileTypeMap().getContentType(file),
					"UTF-8");
			filePart.setTransferEncoding("binary");
			parts[parts.length - 1] = filePart;

			postMethod.setRequestEntity(new MultipartRequestEntity(parts,
					postMethod.getParams()));
			return httpRequest(postMethod);
		} catch (Exception ex) {
			throw new IopException(ex.getMessage(), ex, -1);
		}
	}

	// 使用token
	public Response httpRequest(HttpMethod method) throws IopException {
		return httpRequest(method, true);
	}

	public Response httpRequest(HttpMethod method, Boolean WithTokenHeader)
			throws IopException {
		InetAddress ipaddr;
		int responseCode = -1;
		try {
			ipaddr = InetAddress.getLocalHost();
			List<Header> headers = new ArrayList<Header>();
			if (WithTokenHeader) {
				if (token != null) {
					headers.add(new Header("Authorization", "OAuth2 " + token));
					headers.add(new Header("API-RemoteIP", ipaddr
							.getHostAddress()));
					client.getHostConfiguration().getParams()
							.setParameter("http.default-headers", headers);
					for (Header hd : headers) {
						log(hd.getName() + ": " + hd.getValue());
					}
				}
			}

			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(3, false));
			client.executeMethod(method);
			Header[] resHeader = method.getResponseHeaders();
			responseCode = method.getStatusCode();
			log("Response:");
			log("https StatusCode:" + String.valueOf(responseCode));

			for (Header header : resHeader) {
				log(header.getName() + ":" + header.getValue());
			}
			Response response = new Response();
			response.setResponseAsString(method.getResponseBodyAsString());
			log(response.toString() + "\n");
			if (responseCode != OK)

			{
				try {
					throw new IopException(getCause(responseCode),
							response.asJSONObject(), method.getStatusCode());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return response;

		} catch (IOException ioe) {
			throw new IopException(ioe.getMessage(), ioe, responseCode);
		} finally {
			method.releaseConnection();
		}

	}

	/*
	 * 对parameters进行encode处理
	 */
	public static String encodeParameters(List<PostParameter> postParams) {
		StringBuffer buf = new StringBuffer();
		String value = null;
		for (int j = 0; j < postParams.size(); j++) {
			if (j != 0) {
				buf.append("&");
			}
			value = postParams.get(j).getValue();
			if (value != null) {
				try {
					buf.append(
							URLEncoder.encode(postParams.get(j).getName(),
									"UTF-8"))
							.append("=")
							.append(URLEncoder.encode(postParams.get(j)
									.getValue(), "UTF-8"));
				} catch (java.io.UnsupportedEncodingException neverHappen) {
					// do nonthing
				}
			}
		}
		return buf.toString();
	}

	private static class ByteArrayPart extends PartBase {
		private byte[] mData;
		private String mName;

		public ByteArrayPart(byte[] data, String name, String type)
				throws IOException {
			super(name, type, "UTF-8", "binary");
			mName = name;
			mData = data;
		}

		protected void sendData(OutputStream out) throws IOException {
			out.write(mData);
		}

		protected long lengthOfData() throws IOException {
			return mData.length;
		}

		protected void sendDispositionHeader(OutputStream out)
				throws IOException {
			super.sendDispositionHeader(out);
			StringBuilder buf = new StringBuilder();
			buf.append("; filename=\"").append(mName).append("\"");
			out.write(buf.toString().getBytes());
		}
	}

	private static String getCause(int statusCode) {
		String cause = null;
		switch (statusCode) {
		case FORBIDDEN:
			cause = "The request is forbidden";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}

	public String getToken() {
		return token;
	}

}
