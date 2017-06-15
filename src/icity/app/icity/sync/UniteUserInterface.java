/**  
 * @Title: UniteUserInterface.java 
 * @Package app.icity.sync 
 * @Description: 统一用户身份认证接口 
 * @author  yenan
 * @date 2015-9-14
 * @version V1.0  
 */ 
package app.icity.sync;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;
import org.castor.cpa.persistence.sql.driver.SybaseQueryExpression;

import com.hanweb.sso.ldap.util.MD5;
import com.icore.util.SecurityConfig;

import net.sf.json.JSONObject;

public class UniteUserInterface {

	private String ticket = "";
	public String token = "";
	private JSONObject userInfo;
	private JSONObject tokenInfo;
	private String WebServiceUrl = ""; //WebServices地址
	
	public UniteUserInterface()
	{
		
	}
	
	public void setTicket(String data)
	{
		ticket = data;
	}
	
	public void setUserType(String userType)
	{
		if("gr".equals(userType)||"1".equals(userType))
		{
			WebServiceUrl = SecurityConfig.getString("UserInfoService");
		} else if ("fr".equals(userType)||"2".equals(userType))
		{
			WebServiceUrl = SecurityConfig.getString("UserInfoServiceEnterprise");
		}
	}
	
	public JSONObject getToken() throws Exception
	{
		if(StringUtils.isEmpty(ticket))
			throw new Exception("need ticket");
		// 命名空间
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		// 方法名
		String METHOD = "ticketValidate";
		JSONObject retVal;
		//try {
			Service serivce = new Service();
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String appmark= SecurityConfig.getString("UserInfoappmark");
			String appword = SecurityConfig.getString("UserInfoappword");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(new Date());
			MD5 md5= new MD5();
			String sign = md5.encrypt(appmark +appword+time, time);
			
			Object[] param = new Object[] { appmark, ticket, time, sign };
			// 	webservice接口返回值
			String value = (String) call.invoke(param);
			tokenInfo = JSONObject.fromObject(value);
			token = tokenInfo.getString("token");
			retVal = tokenInfo;
		//} catch(Exception ex)
		//{
		//	retVal.accumulate("ERROR", ex.getMessage());
		//	retVal.accumulate("INFO", "ERROR");
		//}
		return retVal;
	}
	
	public JSONObject getTicketByUuid(String uuid) throws Exception
	{
		
		// 命名空间
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		// 方法名
		String METHOD = "generateTicketByUuid";
		JSONObject retVal;
			Service serivce = new Service();
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String appmark= SecurityConfig.getString("UserInfoappmark");
			String appword = SecurityConfig.getString("UserInfoappword");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(new Date());
			MD5 md5= new MD5();
			String sign = md5.encrypt(appmark +appword+time, time);
			Object[] param = new Object[] { appmark,  time, sign ,uuid };
			// 	webservice接口返回值
			String value = (String) call.invoke(param);
			tokenInfo = JSONObject.fromObject(value);
			return tokenInfo;
	}
	
	public JSONObject getUserInfo() throws Exception
	{
		if(StringUtils.isEmpty(token))
			throw new Exception("need token");
		// 命名空间
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		// 方法名
		String METHOD = "findUserByToken";
		JSONObject retVal;
		//try {
			Service serivce = new Service();
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String appmark= SecurityConfig.getString("UserInfoappmark");
			String appword = SecurityConfig.getString("UserInfoappword");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(new Date());
			MD5 md5= new MD5();
			String sign = md5.encrypt(appmark +appword+time, time);
					
			Object[] param = new Object[] { appmark, token, time, sign };
			// 	webservice接口返回值
			String value = (String) call.invoke(param);
			userInfo = JSONObject.fromObject(value);
			retVal = userInfo;
		//} catch(Exception ex)
		//{
		//	retVal.accumulate("ERROR", ex.getMessage());
		//	retVal.accumulate("INFO", "ERROR");
		//}
		return retVal;
	}
	
	public JSONObject getTokenInfoByUser(String loginName, String Password) throws Exception
	{
		// 命名空间
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		// 方法名
		String METHOD = "userValidate";
		JSONObject retVal;
		//try {
			Service serivce = new Service();
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String appmark= SecurityConfig.getString("UserInfoappmark");
			String appword = SecurityConfig.getString("UserInfoappword");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(new Date());
			MD5 md5= new MD5();
			String sign = md5.encrypt(appmark +appword+time, time);
			Password = md5.encrypt(Password, time);
			Object[] param = new Object[] { appmark, time, sign, loginName, Password};
			// 	webservice接口返回值
			String value = (String) call.invoke(param);
			System.out.println("=======WebServiceUrl:"+WebServiceUrl);
			System.out.println("======param:"+appmark+"|"+time+"|"+sign+"|"+loginName+"|"+Password);
			System.out.println("======="+value);
			tokenInfo = JSONObject.fromObject(value);
			if(tokenInfo.containsKey("token")){
				token = tokenInfo.getString("token");
			}			
			retVal = tokenInfo;
		//} catch(Exception ex)
		//{
		//	retVal.accumulate("ERROR", ex.getMessage());
		//	retVal.accumulate("INFO", "ERROR");
		//}
		return retVal;
	}
	
	public JSONObject register(JSONObject jo) throws Exception
	{
		// 命名空间
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		// 方法名
		String METHOD = "userSyn";
		JSONObject retVal;
			Service serivce = new Service();
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String appmark= SecurityConfig.getString("UserInfoappmark");
			String appword = SecurityConfig.getString("UserInfoappword");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(new Date());
			MD5 md5= new MD5();
			String sign = md5.encrypt(appmark +appword+time, time);
			
			Object[] param = new Object[] { appmark, time, sign, jo, "1" };
			System.out.println("======注册："+WebServiceUrl+"|||");
			// 	webservice接口返回值
			String value = (String) call.invoke(param);
			retVal = JSONObject.fromObject(value);
		return retVal;
	}
	/**
	 * 注册
	 * @param jo
	 * @return
	 * 正确：
		{"result": "0", "uuid": "法人用户唯一ID（例如：C201608151803245444826160）"}
		0-代表新增或修改成功
		错误：
		    {
		        "result": "结果代码"
		"errormsg": "错误信息"
		     }
		result返回的结果代码示意如下：
		201：appmark参数不正确
		202：sign参数不正确 
		203：args参数不正确
		204：登录名（或其他必填）参数不能为空
		205：登录名格式不正确
		206：已经存在相同登录名
		207：证件号码格式不正确。
		208：证件号码已存在。
		209：手机号码格式不正确
		210：手机号码重复
		211：邮箱格式不正确
		212：邮箱重复 
		299：其他错误
	 * @throws Exception
	 */
	public JSONObject userAdd(JSONObject jo) throws Exception
	{
		// 命名空间
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		// 方法名
		String METHOD = "userAdd";
		JSONObject retVal;
			Service serivce = new Service();
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String appmark= SecurityConfig.getString("UserInfoappmark");
			String appword = SecurityConfig.getString("UserInfoappword");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(new Date());
			MD5 md5= new MD5();
			String sign = md5.encrypt(appmark +appword+time, time);			
			Object[] param = new Object[] { appmark, time, sign, jo };
			System.out.println("======param:"+appmark+"|"+time+"|"+sign+"|"+jo.toString());
			System.out.println("======注册："+WebServiceUrl+"|||");
			// 	webservice接口返回值
			String value = (String) call.invoke(param);
			System.out.println("======注册value："+value+"|||");
			retVal = JSONObject.fromObject(value);
		return retVal;
	}
	public JSONObject getTicketByToken(String data){
		JSONObject jo = new JSONObject();
		
		return jo;
	}
}
