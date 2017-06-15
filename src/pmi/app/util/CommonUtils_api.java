package app.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class CommonUtils_api {
	
	public static final String YYYY_MM_DD_HH_mm_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD_HH_mm = "yyyy-MM-dd HH:mm";
	public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY_MM = "yyyy-MM";
	public static final String YYYY = "yyyy";
	public static final String YYYY_MM_DD_HH_MM_SS_SSS="yyyy-MM-dd HH:mm:ss.SSS";
	
	private CommonUtils_api(){};
	
	private volatile static CommonUtils_api _commonUtils_api = null;
	
	public static CommonUtils_api getInstance(){
		if (_commonUtils_api == null) {
			synchronized (CommonUtils.class) {
				if (_commonUtils_api == null) {
					_commonUtils_api = new CommonUtils_api();
				}
			}
		}
		return _commonUtils_api;
	}
	
	public Timestamp parseStringToTimeStamp(String date,String patten) throws Exception{
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return new Timestamp(sdf.parse(date).getTime());
		}
		return null;
	}
	
	public Date parseStringToDate(String date,String patten) throws Exception{
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return new Date(sdf.parse(date).getTime());
		}
		return null;
	}
	
	public String parseDateToString(Date date,String patten) throws Exception{
		if (date!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return sdf.format(date);
		}
		return null;
	}
	
	
	public String parseStringToString(String date,String patten) throws Exception{
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return sdf.format(sdf.parse(date));
		}
		return null;
	}
	
	public java.sql.Date parseStringToSqlDate(String date,String patten) throws Exception{
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return new java.sql.Date(sdf.parse(date).getTime());
		}
		return null;
	}
	
	public java.sql.Timestamp parseStringToTimestamp(String date,String patten) throws Exception{
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return new java.sql.Timestamp(sdf.parse(date).getTime());
		}
		return null;
	}
	
	public Timestamp parseDateToTimeStamp(Date date,String patten) throws Exception{
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(patten,Locale.ENGLISH);
			String str = sdf.format(date);
			return new Timestamp(sdf.parse(str).getTime());
		}
		return null;
	}
	
	public String formatJsonToString(JSONObject json,String patten) throws Exception{
		long time = json.getLong("time");
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		return sdf.format(date);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),YYYY_MM_DD_HH_MM_SS_SSS));
			System.out.println(CommonUtils_api.getInstance().parseStringToTimeStamp("2012-12-12 23",CommonUtils.YYYY_MM_DD_HH));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
