package app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import net.sf.json.JSONObject;
import cn.org.bjca.uams.rest.spi.BjcaRestSdk;

import com.icore.StateType;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.bean.DataSet;

/**
 * 广东省统一认证_工具类
 * 
 */
public class OAuthUtil {
	private  static String serverUrl = "";
	private  static String baseUrl = "";
	private  static String accessTokenUrl = "";
	// 定义请求范围 uid:登录名 cn:真实姓名 userIdCode：用户唯一标识 extProperties：扩展属性 等
	private final static String scope = "all";
	// OAuth认证完之后重定向的地址：若重定向地址含有多个参数，使用英文逗号","将参数隔开；
//	private final static String redirectUri = "http://sxsl.sz.gov.cn/icity/app/uc/login?action=callback";
	// 客户机标识(定义在服务端)
	private  static String clientId = "";
	// 客户机Secret参数(定义在服务端)
	private  static String clientSecret = "";
	// 授权类型(使用授权码方式)
	private final static String grantType = "authorization_code";
	// 授权码(从OAuth服务端获取)
	//private final static String code = "";
	// 获取用户信息 请求地址(参数：access_token)
	private  static String tokenInfoUrl = "";

	static{
		serverUrl = SecurityConfig.getString("OAuthServerUrl", "http://tyrz.gdbs.gov.cn/am");
		clientId = SecurityConfig.getString("OAuthClientId", "gdbshuizhoubl");
		clientSecret = SecurityConfig.getString("OAuthClientSecret", "hzbl161031");
		baseUrl = serverUrl + "/oauth2";
		accessTokenUrl = baseUrl + "/access_token?";
		tokenInfoUrl = baseUrl + "/tokeninfo?access_token=";
	}
	/**
	 * 通过授权码获取最终的userCodeId
	 * 
	 * @param OAuth_code
	 *          OAuth授权码
	 * @return
	 */
	public static DataSet getOAuthUserCodeId(String redirectUri,String OAuth_code) {
		DataSet ds = new DataSet();
		String userInfoDetail = null, accessTokenJSON = null;
		// 通过授权码得到access_token
		String accessTokenUrlTemp = null;
		try {
			accessTokenUrlTemp = accessTokenUrl + "client_id=" + clientId + "&redirect_uri=" + URLEncoder.encode(redirectUri,"UTF-8") + "&scope=" + scope + "&client_secret=" + clientSecret + "&grant_type=" + grantType + "&code=" + OAuth_code;
			System.out.println("accessTokenUrlTemp：" + accessTokenUrlTemp);
			accessTokenJSON = getAccessToken(accessTokenUrlTemp);
			System.out.println(" >>>>> accessTokenJSON = " + accessTokenJSON);
			// {"expires_in":59,"token_type":"Bearer","access_token":"cb5f1298-4c4a-45f7-ae9e-0a67c80c1697"}
			JSONObject jsonObj = JSONObject.fromObject(accessTokenJSON);
			// 通过返回的对象判断授权码是否已经过期
			String errorCode = jsonObj.optString("error_code");
			String errorMsg = jsonObj.optString("error_msg");
			if (StringUtil.isNotEmpty(errorCode) || StringUtil.isNotEmpty(errorMsg)) {
				ds.setState(StateType.FAILT);
				ds.setMessage(errorMsg);
			} else {
				String accessToken = jsonObj.optString("access_token");
				// 根据access_token获取用户信息
				String tokenInfoUrlTemp = tokenInfoUrl + accessToken;
				System.out.println("tokenInfoUrlTemp：" + tokenInfoUrlTemp);
				userInfoDetail = getUserInfo(tokenInfoUrlTemp);
				JSONObject userInfoJson = JSONObject.fromObject(userInfoDetail);
				String userInfoErrorCode = userInfoJson.optString("error_code");
				String userInfoErrorMsg = userInfoJson.optString("error_msg");
				if (StringUtil.isNotEmpty(userInfoErrorCode) || StringUtil.isNotEmpty(userInfoErrorMsg)) {
					ds.setState(StateType.FAILT);
					ds.setMessage(userInfoErrorMsg);
				} else {
					System.out.println(">>>>> OAuth userInfoDetail = " + userInfoDetail);
					JSONObject userInfo = changeUserInfo2JSON(userInfoDetail);
					System.out.println("userInfo:"+userInfo.toString());
					ds.setTotal(1);
					ds.setRawData(userInfo);
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return ds;
	}

	/**
	 * 根据access_token获取用户信息 
	 * @throws UnsupportedEncodingException 
	 */
	public static String getUserInfo(String accessTokenUrl) throws UnsupportedEncodingException{
		InputStream isrUserInfo = getContentByGET(accessTokenUrl);
		StringBuilder buf = new StringBuilder();
		
		if(isrUserInfo == null){ //对access_token过期的处理
			buf.append("{\"error_code\":\"500\",\"error_msg\":\"服务器内部错误，可能access_token已经过期或client_id不可用或重定向地址通信异常.\"}");
			return buf.toString();
		}
		
		 InputStreamReader isr  = new InputStreamReader(isrUserInfo,"UTF-8");
		 BufferedReader in = new BufferedReader(isr);
	        try {
	            for (String str; (str = in.readLine()) != null;) {
	                buf.append(str);
	            }
	        } catch (IOException ioe) {
	        	System.out.println("OAuthUtil.getUserInfo:IOException:" + ioe.getMessage());
	        } finally {
	            try {
	                in.close();
	            } catch (IOException ioe) {
	                System.out.println("finally OAuthUtil.getUserInfo:IOException:" + ioe.getMessage());
	            }
	        }
	        return buf.toString();
	}

	/**
	 * 处理传递的URL参数 
	 */
	public static String getParamValue(String query, String param) {

        String paramValue = "";
        if (query != null && query.length() != 0) {
            String[] paramsArray = query.split("\\&");
            for (String parameter : paramsArray) {
                if (parameter.startsWith(param)) {
                    paramValue = parameter.substring(parameter.indexOf("=") + 1);
                    break;
                }
            }
        }
        return paramValue;
    }

	/**
	 * 根据授权码获取access_token 
	 * @throws UnsupportedEncodingException 
	 */
	public static String getAccessToken(String serviceUrl) throws UnsupportedEncodingException{
		InputStream isr = getContentByGET(serviceUrl);
		StringBuilder buf = new StringBuilder();
		
		if(isr == null){ //对授权码过期的处理
			buf.append("{\"error_code\":\"500\",\"error_msg\":\"服务器内部错误，可能授权码已经过期或client_id不可用或重定向地址通信异常.\"}");
			return buf.toString();
		}
		
        BufferedReader in = new BufferedReader(new InputStreamReader(isr,"UTF-8"));
       
        try {
            for (String str; (str = in.readLine()) != null;) {
                buf.append(str);
            }
        } catch (IOException ioe) {
        	System.out.println("OAuthUtil.getAccessToken:IOException:" + ioe.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException ioe) {
            	System.out.println("finally OAuthUtil.getAccessToken:IOException:" + ioe.getMessage());
            }
        }
        return buf.toString();
	}

	/** 
	 * 通过Get方式发起请求
	 */
	public static InputStream getContentByGET(String serviceUrl){
   	 	InputStream is = null;
        try {
            HttpURLConnection connection = getURLConnection(serviceUrl,"GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_METHOD) {
            	System.out.println("OAuthUtil.getContentByGET : IT was NOT-OK:" + connection.getResponseCode() + " " + connection.getResponseMessage() + " 切换Post请求方式");
                try {
					is = getContentByPOST(serviceUrl);
				} catch (LoginException e) {
					//忽略异常
				}
            } else {
                String data[] = {String.valueOf(connection.getResponseCode())};
                //System.out.println("OAuthUtil.getContentByGET : httpErrorCode :" + data.toString());
            }
        } catch (MalformedURLException mfe) {
        	System.out.println("OAuthUtil.getContentByGET : MalformedURLException :" + mfe);
        } catch (IOException ioe) {
        	System.out.println("OAuthUtil.getContentByGET : IOException :" + ioe);
        }
        return is;
   }

	/** 
	 * 通过Post方式
	 */
    public static InputStream getContentByPOST(String serviceUrl) throws LoginException {
        InputStream is = null;
        try {
        	HttpURLConnection connection = getURLConnection(serviceUrl,"POST");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else { 
            	System.out.println("OAuthUtil.getContentByPOST : IT was NOT-OK:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }
        } catch (MalformedURLException mfe) {
        	System.out.println("OAuthUtil.getContentByPOST : MalformedURLException :" + mfe);
        } catch (IOException ioe) {
        	System.out.println("OAuthUtil.getContentByPOST : IOException :" + ioe);
        }
        return is;
    }

	/***
	 * 获取HttpURLConnection连接
	 */
	public static HttpURLConnection getURLConnection(String serviceUrl, String method) {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(serviceUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			// 设置超时时间(单位：毫秒)：10秒
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setRequestMethod(method);
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("contentType", "utf-8");
		} catch (IOException e) {
			System.out.println("OAuthUtil.getURLConnection : IOException :" + e);
		}
		return connection;
	}
	
	public static Map getAllUserAttributes(String tokenId,String random) throws Exception{
		Map map = BjcaRestSdk.getInstance().setServerUrl(serverUrl);
		Map result = null;
		String errorMsg = "省厅单点登录失败！";
		if(null != map && map.size()>0 && map.containsKey("status") && "0".equals(map.get("status").toString())){
			result = BjcaRestSdk.getInstance().getAllUserAttributes(tokenId, random);
			if(!"0".equals(result.get("status").toString())){
				errorMsg += "错误信息：" + result.get("message").toString();
				throw new Exception(errorMsg);
			}
		}else{
			if(null != map && map.size()>0 && map.containsKey("message")){
				errorMsg += "错误信息：" + map.get("message").toString();
				throw new Exception(errorMsg);
			}
		}
		return result;
	}
	
	public static JSONObject getAllUserAttributesJSON(String tokenId,String random) throws Exception{
		Map userInfo = getAllUserAttributes(tokenId,random);
		return changeUserInfo2JSON(userInfo);
	}
	
	public static String getAllUserAttributesJSON(String tokenId,String random,String nextgoto) throws Exception{
		String uu="",outUrl="",virtualPath="";
		//第一步：设置服务路径  
		Map map = BjcaRestSdk.getInstance().setServerUrl(serverUrl);
		Map result = null;
		String errorMsg = "省厅单点登录失败！";
		if(null != map && map.size()>0 && map.containsKey("status") && "0".equals(map.get("status").toString())){
			// 第二步：判断用户登录状态
			result = BjcaRestSdk.getInstance().getAllUserAttributes(tokenId, random);
			String status = (String) result.get("status");
			if(!status.endsWith("0")){
				errorMsg += "错误信息：" + result.get("message").toString();
				throw new Exception(errorMsg);
			}
			// 第三步：返回"使用广东省网厅账号登陆链接"
			uu=SecurityConfig.getString("WsbsDomain");
			outUrl=SecurityConfig.getString("SERVICE_OUTER_URL");
			virtualPath = SecurityConfig.getString("VirtualPath");
//			String redirectUri="http://wsbs.sz.gov.cn/shenzhen"+"/app/uc/login?action=callback&goto="+nextgoto;
//			String redirectUri="http://sxsl.sz.gov.cn/icity"+"/app/uc/login?action=callback&goto="+nextgoto;
			
			String redirectUri=outUrl+virtualPath+"/app/uc/login?action=gdbscallback&goto="+nextgoto;

//			String redirectUri="http://sxsl.sz.gov.cn/AppOAuthDemo/OAuthSvtClient?goto="+nextgoto;
//			String redirectUri=SecurityConfig.getString("OAuthUtilPath")+"/app/uc/login?action=callback&goto="+nextgoto;
			uu=uu+URLEncoder.encode(redirectUri,"UTF-8");
			return uu;
			
		}else{
			if(null != map && map.size()>0 && map.containsKey("message")){
				errorMsg += "错误信息：" + map.get("message").toString();
				throw new Exception(errorMsg);
			}
		}
		return uu;
	}
	
	/**
	 * 转换从单点登录接口获取的用户信息，返回json格式
	 * @param userInfo
	 * @return
	 */
	private static JSONObject changeUserInfo2JSON(Map userInfo){
		Map userObj = (Map)userInfo.get("userobj");
		Map extProperties = (Map)userObj.get("extproperties");
		userObj.remove("extproperties");
		userObj.putAll(extProperties);
		
		//临时转换，等待省网厅接口修改后删除
		String linkPersonName = (String)userObj.get("linkpersonname");
		userObj.remove("linkpersonname");
		userObj.put("link_person_name", linkPersonName);
		
		userInfo.remove("userobj");
		userInfo.putAll(userObj);
		return JSONObject.fromObject(userInfo);
	}
	
	/**
	 * 转换从OAthor2认证接口获取的用户信息，返回json格式
	 * 将所有key全部转小写，与单点登录接口保持一致
	 * @param userInfo
	 * @return
	 */
	private static JSONObject changeUserInfo2JSON(String userInfo){
		JSONObject obj = JSONObject.fromObject(userInfo);
		Object pareobj =obj.get("pareobj");
		if(!pareobj.equals("null")){
			//将pareobj数据取出来
			JSONObject userObj = (JSONObject)obj.get("pareobj");
			//将userobj中的tokenid取出来
			if(obj.containsKey("userobj")){
				userObj.put("tokenid",((JSONObject)(obj.get("userobj"))).get("tokenid"));
			}
			if(userObj.containsKey("extproperties")){
				JSONObject extpropertiesObj = formatExtProperties(userObj.get("extproperties"));
				userObj.remove("extproperties");
				userObj.putAll(extpropertiesObj);
			}
			
			obj.remove("userobj");
			obj.putAll(userObj);
			
		}else{
			//将userobj数据取出来
			if(obj.containsKey("userobj")){
				JSONObject userObj = (JSONObject)obj.get("userobj");
				if(userObj.containsKey("extproperties")){
					JSONObject extpropertiesObj = formatExtProperties(userObj.get("extproperties"));
					userObj.remove("extproperties");
					userObj.putAll(extpropertiesObj);
				}
				
				obj.remove("userobj");
				obj.putAll(userObj);
			}
		}
		
		//兼容省厅接口修改前
		if(obj.containsKey("extproperties")){
			JSONObject extpropertiesObj = formatExtProperties(obj.get("extproperties"));
			obj.remove("extproperties");
			obj.putAll(extpropertiesObj);
		}
		
//		JSONObject result = new JSONObject();
//		Iterator<String> keys = obj.keys();
//		while(keys.hasNext()){
//			String key = keys.next();
//			result.put(key.toLowerCase(), obj.get(key));
//		}
		return obj;
	}
	
	private static JSONObject formatExtProperties(Object extProperties){
		JSONObject extPropertiesObj = new JSONObject();
		List<String> extPropertiesList = (List<String>) extProperties;
		//int size = extPropertiesList.size();
		for(String property:extPropertiesList){
			String[] arr = property.split("=");
			extPropertiesObj.put(arr[0], arr[1]);
		}
		return extPropertiesObj;
	}
	
	public static void main(String[] args) throws Exception{
//		try {
//			String tokendId = "AQIC5wM2LY4SfcxogfgRrvMTti6jkCm9RoxlXzokRhMicog.*AAJTSQACMDIAAlNLABQtMTIwMDE0MTE1MTI2Njk2OTM1NgACUzEAAjAx*";
//			String random = "YjYzMDgzZjY5NjVhNDVlNWFhMTIzYzdhMmNlODVjMWY=";
//			OAuthUtil.getAllUserAttributes(tokendId, random);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		Map map = BjcaRestSdk.getInstance().setServerUrl("http://tyrztest.gdbs.gov.cn/am");
		   
        
	      //模拟用户登录（业务系统不需要调用）
//	    	  Map map1 = BjcaRestSdk.getInstance().loginByPWD("gdwttest8","123qwe");
//	    	  Map map1 = BjcaRestSdk.getInstance().loginByPWD("szfttest1","!23qaz");
	    	  Map map1 = BjcaRestSdk.getInstance().loginByPWD("gdwtdw1","123qwe");

	        System.out.println("map1="+map1);
	     // //获取tokenId（实际环境为从省网办大厅传过来的参数中进行解析）
	        String tokenId = (String) map1.get("tokenId");
	        //获取随机数（实际环境为从省网办大厅传过来的参数中进行解析）
	       Map map2 = BjcaRestSdk.getInstance().generateRandom(tokenId);
	        String random = (String) map2.get("random");
	       System.out.println("http://127.0.0.1/icity/project?tokenId="+tokenId+"&random="+random);
	       
//	       Map userMap = OAuthUtil.getAllUserAttributes(tokenId, random);
	       JSONObject userObj = OAuthUtil.getAllUserAttributesJSON(tokenId, random);
//	       System.out.println("userMap = " + userMap);
	       System.out.println("userJSONObject = " + userObj);
	}
	
}
