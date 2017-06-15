package app.db.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DbProvider {
	/// <summary>
    /// 根据ResultSet获取数据集合
    /// </summary>
    /// <param name="rs">数据库查询出来的记录集</param>
	/// <param name="bFirst">是否只返回第一条记录</param>
    /// <returns>数据集合</returns>
	public static ArrayList<HashMap<String, String>> rsToArrayList(ResultSet rs,Boolean bFirst)
	{
		ArrayList<HashMap<String, String>> re = new ArrayList<HashMap<String,String>>();
		
		try
		{
			HashMap<String,String> map;
			ResultSetMetaData rsmd = rs.getMetaData();
			String v ="";
			while (rs.next()) {
				
				map = new HashMap<String, String>();
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{
					v = rs.getString(i);
					if(v== null || v.equals("null"))
					{
						v = "";
					}
					map.put(rsmd.getColumnName(i).toLowerCase(), v);
				}
				re.add(map);
				if(bFirst)
				{
					break;
				}
			}
		}
		catch(Exception ex)
		{}
		return re;
	}

	/// <summary>
    /// 格式化需要提交的字符串
    /// </summary>
    /// <param name="para">参数集合</param>
	/// <param name="key">提交的关键字</param>
	/// <param name="def">默认值</param>
	/// <param name="bMust">是否必须有值</param>
    /// <returns>符合要求的字符串</returns>
	public static String formatPostString(JSONObject para,String key,String def,Boolean bMust) throws Exception
	{
		if(para == null || para.size()<1)
		{
			if(bMust)
			{
				throw new Exception("参数为『"+key+"』未定义");
			}
			return def;
		}
		String str = null;
		Iterator<String> keys = para.keySet().iterator();
		while(keys.hasNext()){
			String tKey = keys.next();
			if(tKey.toUpperCase().equals(key.toUpperCase())){
				str = para.getString(tKey);
				break;
			}
		}
		//if(str != null){
			//str = str.toLowerCase();
		//}
		
		if(str == null || str.length()==0)
		{
			if(bMust)
			{
				throw new Exception("参数为『"+key+"』未定义");
			}
			return def;
		}
		else
		{
			return str;
		}
	}
	/// <summary>
    /// 格式化需要提交的Boolean类型
    /// </summary>
    /// <param name="para">参数集合</param>
	/// <param name="key">提交的关键字</param>
	/// <param name="def">默认值</param>
    /// <returns>符合要求的Boolean类型</returns>
	public static Boolean formatPostBoolean(JSONObject para,String key,Boolean def)
	{
		if(para == null || para.size()<1)
		{
			return def;
		}
		String str = null;
		Iterator<String> keys = para.keySet().iterator();
		while(keys.hasNext()){
			String tKey = keys.next();
			if(tKey.toUpperCase().equals(key.toUpperCase())){
				str = para.getString(tKey);
				break;
			}
		}
		if(str != null){
			str = str.toLowerCase();
		}
		if(str == null || str.length()==0)
		{
			return def;
		}
		if(str.equals("1") || str.toLowerCase().equals("true"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/// <summary>
    /// 格式化需要提交的Integer类型
    /// </summary>
    /// <param name="para">参数集合</param>
	/// <param name="key">提交的关键字</param>
	/// <param name="def">默认值</param>
	/// <param name="bMust">是否必须有值</param>
    /// <returns>符合要求的Integer类型</returns>
	public static Integer formatPostNumber(JSONObject para,String key,Integer def,Boolean bMust) throws Exception
	{
		if(para == null || para.size()<1)
		{
			if(bMust)
			{
				throw new Exception("参数为『"+key+"』未定义");
			}
			return def;
		}
		String str = null;
		Iterator<String> keys = para.keySet().iterator();
		while(keys.hasNext()){
			String tKey = keys.next();
			if(tKey.toUpperCase().equals(key.toUpperCase())){
				str = para.getString(tKey);
				break;
			}
		}
		if(str != null){
			str = str.toLowerCase();
		}
		if(str == null || str.length()==0)
		{
			if(bMust)
			{
				throw new Exception("参数为『"+key+"』未定义");
			}
			return def;
		}
		try
		{
			return Integer.parseInt(str);
		}
		catch(Exception ex)
		{
			return def;
		}
		
	}
	
	/// <summary>
    /// 格式化需要提交的Integer类型
    /// </summary>
    /// <param name="para">参数集合</param>
	/// <param name="key">提交的关键字</param>
	/// <param name="def">默认值</param>
	/// <param name="bMust">是否必须有值</param>
    /// <returns>符合要求的Integer类型</returns>
	public static Long formatPostLong(JSONObject para,String key,Long def,Boolean bMust) throws Exception
	{
		if(para == null || para.size()<1)
		{
			if(bMust)
			{
				throw new Exception("参数为『"+key+"』未定义");
			}
			return def;
		}
		String str = null;
		Iterator<String> keys = para.keySet().iterator();
		while(keys.hasNext()){
			String tKey = keys.next();
			if(tKey.toUpperCase().equals(key.toUpperCase())){
				str = para.getString(tKey);
				break;
			}
		}
		if(str != null){
			str = str.toLowerCase();
		}
		if(str == null || str.length()==0)
		{
			if(bMust)
			{
				throw new Exception("参数为『"+key+"』未定义");
			}
			return def;
		}
		try
		{
			return Long.parseLong(str);
		}
		catch(Exception ex)
		{
			return def;
		}
	}
	
	/**
	 * 获得PreparedStatement向数据库提交的SQL语句
	 * @param sql
	 * @param params
	 * @return
	*/
	public static String getPreparedSQL(String sql, Object[] params){
		//1 如果没有参数，说明是不是动态SQL语句
		int paramNum = 0;
		if (null != params)  paramNum = params.length;
		if (1 > paramNum) return sql;
			//2 如果有参数，则是动态SQL语句
		StringBuffer returnSQL = new StringBuffer();
		String[] subSQL = sql.split("\\?");
		for (int i = 0; i < paramNum; i++) {
			if (params[i] instanceof Integer) {
				returnSQL.append(subSQL[i]).append(" ").append(params[i]);
			} else {
				returnSQL.append(subSQL[i]).append(" '").append(params[i]).append("' ");
			}
		}
		if (subSQL.length > params.length) {
			returnSQL.append(subSQL[subSQL.length - 1]);
		}
		return returnSQL.toString();
	}
}
