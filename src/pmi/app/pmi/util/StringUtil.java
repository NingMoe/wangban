/**   
* @Title: StringUtil 
* @Package app.icity.util.common 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhuxiaoyue   
* @date 2014-4-1 下午06:04:02 
* @version V1.0   
*/
package app.pmi.util;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.inspur.bean.ParameterSet;

import net.sf.json.JSONObject;

/** 
 * @ClassName: StringUtil 
 * @Description: TODO(字符串相关处理类) 
 * @author zhuxiaoyue 
 * @date 2014-4-1 下午06:04:02 
 *  
 */
public  class StringUtil 
{
	public static final String DEFAULT_FORMAT="yyyy-MM-dd";
	/**
	 * JSONObject获取某个健的值
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static String getValue(JSONObject jsonObject,String key)
	{
		if(null == jsonObject)
		{
			return "";
		}
		else
		{
			if(null == jsonObject.get(key))
			{
				return "";
			}
			else if("null".equals(jsonObject.get(key).toString()))
			{
				return "";
			}
			else
			{
				return jsonObject.get(key).toString();
			}
		}
	}
	
	/**
	 * JSONObject获取某个健的值
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static JSONObject getJsonValue(JSONObject jsonObject,String key)
	{
		if(null == jsonObject)
		{
			return null;
		}
		else
		{
			if(null == jsonObject.get(key))
			{
				return null;
			}
            else if (("null".equals(jsonObject.get(key).toString()))
					|| ("".equals(jsonObject.get(key).toString())))
			{
				return null;
			}
			else
			{
				return (JSONObject)jsonObject.get(key);
			}
		}
	}
	
	/**
	 * 
	 * @param pSet
	 * @param key
	 * @return String
	 */
	public  static String getParamString(ParameterSet pSet,String key){
		if (pSet.get(key) != null) {
			return (String)pSet.getParameter(key);
		}
		return "";
	}
	
	public  static Object getParameter(ParameterSet pSet,String key)
	{
		if (pSet.get(key) != null)
		{
			return pSet.getParameter(key);
		}
		return "";
	}
	
	/**
	 * 
	 * @param pSet
	 * @param key
	 * @return Integer
	 */
	public static Integer getParamInt(ParameterSet pSet,String key){
		Object o = pSet.get(key);
		if (o != null) {
			if(o instanceof String && StringUtils.isNotEmpty((String)o)){
				return Integer.valueOf((String)pSet.getParameter(key));
			}else if(o instanceof Integer){
				return (Integer)o;
			}
		}
		return 0;
	}
	
	
	/** 日期转换为字符串
	* @param date 日期
	* @param format 日期格式
	* @return 字符串
	*/
	public static String date2Str(Date date, String format) {
	if (null == date) {
	return null;
	}
	SimpleDateFormat sdf = new SimpleDateFormat(format);
	return sdf.format(date);
	}

	/**
	* 时间戳转换为字符串
	* @param time
	* @return
	*/
	public static String timestamp2Str(Timestamp time) {
	Date date = null;
	if(null != time){
	date = new Date(time.getTime());
	}
	return date2Str(date, DEFAULT_FORMAT);
	}
      
	
	/**
	* 时间戳转换为字符串
	* @param time
	* @return
	*/
	public static String timestamp2Str(JSONObject json,String param,String format)
	{
	 Date date = null;
	 if(null != json.get(param))
	 {
		 if (json.get(param) instanceof String && StringUtils.isEmpty(json.getString(param))) {
			return "";
		}	
		 JSONObject time=JSONObject.fromObject(json.get(param));	
		 if(null == time || "null".equalsIgnoreCase(time.toString()))
		 {
			 return "";
		 }
		  date=new Date(Long.parseLong(time.get("time").toString()));	
	 }
	 return date2Str(date,format);
	}
	
	
	/**
	* 时间戳转换为字符串
	* @param time
	* @return
	*/
	public static Date timestamp2Str(JSONObject json,String param)
	{
	 Date date = null;
	 if(null != json)
	 {
		 JSONObject time=JSONObject.fromObject(json.get(param));	
		 if(null == time)
		 {
			 return null;
		 }
		  date=new Date(Long.parseLong(time.get("time").toString()));	
	 }
	 return date;
	}
	
	public static String addParam2Url(String url,String paramKey,String paramValue){
		String[] temp = url.split("[?]");
		if(temp.length>1){
			url += "&" + paramKey + "=" + paramValue;
		}else{
			url += "?" + paramKey + "=" + paramValue;
		}
		return url;
	}
	
	
	public static void main(String[] args) {
		java.sql.Timestamp sbsj = new java.sql.Timestamp(System.currentTimeMillis());
		String temp = timestamp2Str(sbsj);
		System.out.println("temp >>> " + temp);
	}
}
