package app.util;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by res100 on 16/7/27.
 */
public class RestUtil {
	private final static int BUFFER_SIZE = 1024;
	private static Logger log = LoggerFactory.getLogger(RestUtil.class);

	private static String inputStreamToString(InputStream in) {
		ByteArrayOutputStream outStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] data = new byte[BUFFER_SIZE];
			int count;
			while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
				outStream.write(data, 0, count);
			}
			return new String(outStream.toByteArray(), "UTF-8");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (Exception exx) {
				log.error(exx.getMessage());
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception exx) {
				log.error(exx.getMessage());
			}
		}
		return null;
	}

	public static String postData(String url) throws Exception {
		return postData(url, null);
	}

	public static String postData(String url, Map<String, String> data)
			throws Exception {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		try {
			client.getParams().setContentCharset("UTF-8");
			client.getParams().setBooleanParameter(
					"http.protocol.expect-continue", false);
			postMethod.addRequestHeader("Connection", "close");

			if (data != null && data.size() > 0) {
				Iterator<Entry<String, String>> it = data.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					postMethod.setParameter(entry.getKey(), entry.getValue());
				}
			}
			client.executeMethod(postMethod);

			return inputStreamToString(postMethod.getResponseBodyAsStream());
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				postMethod.releaseConnection();
			} catch (Exception ex) {
			}
		}
	}
	
	public static InputStream postDataInput(String url, Map<String, String> data)
			throws Exception {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		try {
			client.getParams().setContentCharset("UTF-8");
			client.getParams().setBooleanParameter(
					"http.protocol.expect-continue", false);
			postMethod.addRequestHeader("Connection", "close");

			if (data != null && data.size() > 0) {
				Iterator<Entry<String, String>> it = data.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					postMethod.setParameter(entry.getKey(), entry.getValue());
				}
			}
			client.executeMethod(postMethod);

			return postMethod.getResponseBodyAsStream();
		} catch (Exception ex) {
			throw ex;
		} finally {
			
		}
	}

	@SuppressWarnings("deprecation")
	public static String postJson(String url, JSONObject jsonParam)	throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost postMethod = new HttpPost(url);
		try {
			StringEntity entity = new StringEntity(jsonParam.toString(),"UTF-8");//解决中文乱码问题    
            entity.setContentEncoding("UTF-8");    
            entity.setContentType("application/json");    
            postMethod.setEntity(entity);    
            HttpResponse result = client.execute(postMethod);
			String resData = EntityUtils.toString(result.getEntity(),"utf-8"); 
			return resData;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				postMethod.releaseConnection();
			} catch (Exception ex) {
			}
		}
	}
	/**
	 * 自定义汉字编码
	 * @param url
	 * @param jsonParam
	 * @param coding
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static String postJsonCoding(String url, JSONObject jsonParam,String coding)	throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost postMethod = new HttpPost(url);
		try {
			if(coding==""){
				coding = "UTF-8";
			}
			StringEntity entity = new StringEntity(jsonParam.toString(),coding);//解决中文乱码问题    
            entity.setContentEncoding(coding);    
            entity.setContentType("application/json");    
            postMethod.setEntity(entity);    
            HttpResponse result = client.execute(postMethod);
			String resData = EntityUtils.toString(result.getEntity(),coding); 
			return resData;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				postMethod.releaseConnection();
			} catch (Exception ex) {
			}
		}
	}
	public static String getData(String url) throws Exception {
		return getData(url, null);
	}

	public static String getData(String url, Map<String, String> data)
			throws Exception {
		HttpClient client = new HttpClient();
		GetMethod getMethod = null;
		try {
			client.getParams().setContentCharset("UTF-8");
			client.getParams().setBooleanParameter(
					"http.protocol.expect-continue", false);
			if (data != null && data.size() > 0) {
				Iterator<Entry<String, String>> it = data.entrySet().iterator();
				String params = "";
				StringBuffer paramsBuffer = new StringBuffer();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					String key = entry.getKey();
					String value = data.get(key);
					if (params.length() > 0) {
						paramsBuffer.append("&");
					}
					paramsBuffer.append(key + "="
							+ URLEncoder.encode(value, "utf-8"));
				}
				params = paramsBuffer.toString();
				if (url.indexOf("?") == -1) {
					url += "?" + params;
				} else {
					url += "&" + params;
				}
			}
			getMethod = new GetMethod(url);
			getMethod.addRequestHeader("Connection", "close");
			client.executeMethod(getMethod);
			return inputStreamToString(getMethod.getResponseBodyAsStream());
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				getMethod.releaseConnection();
			} catch (Exception ex) {
			}
		}
	}
	public static String getDataWithHeader(String url, Map<String, String> data)
			throws Exception {
		HttpClient client = new HttpClient();
		GetMethod getMethod = null;
		try {
			client.getParams().setContentCharset("UTF-8");
			client.getParams().setBooleanParameter(
					"http.protocol.expect-continue", false);
			getMethod = new GetMethod(url);
			if (data != null && data.size() > 0) {
				Iterator<Entry<String, String>> it = data.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					String key = entry.getKey();
					String value = data.get(key);
					getMethod.setRequestHeader(key, value);
				}
			}
			getMethod.addRequestHeader("Connection", "close");
			client.executeMethod(getMethod);
			return inputStreamToString(getMethod.getResponseBodyAsStream());
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				getMethod.releaseConnection();
			} catch (Exception ex) {
			}
		}
	}
}
