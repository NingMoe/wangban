package app.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.Record;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;


public class CommonUtils {
	
	public static final String YYYY_MM_DD_HH_mm_SS = "yyyy-MM-dd hh:mm:ss";
	public static final String YYYY_MM_DD_HH_mm = "yyyy-MM-dd hh:mm";
	public static final String YYYY_MM_DD_HH = "yyyy-MM-dd hh";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY_MM = "yyyy-MM";
	public static final String YYYY = "yyyy";
	public static final String YYYY_MM_DD_HH_MM_SS_SSS="yyyy-MM-dd HH:mm:ss.SSS";
	public static final String YYYYMMDD="yyyyMMdd";
	public static final String YYYY_M_D="yyyy-M-d";
	public static final String CN_YYYY_MM_DD = "yyyy年MM月dd日"; 
	private CommonUtils(){};
	
	private volatile static CommonUtils _commonUtils = null;
	
	public static CommonUtils getInstance(){
		if (_commonUtils == null) {
			synchronized (CommonUtils.class) {
				if(_commonUtils == null){
					_commonUtils = new CommonUtils();
				}
			}
		}
		return _commonUtils;
	}
	
	public Timestamp parseStringToTimeStamp(String date,String patten){
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			try {
				return new Timestamp(sdf.parse(date).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Date parseStringToDate(String date,String patten){
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			try {
				return new Date(sdf.parse(date).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public java.sql.Date parseStringToSqlDate(String date,String patten){
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			try {
				return new java.sql.Date(sdf.parse(date).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Timestamp parseDateToTimeStamp(Date date,String patten){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(patten,Locale.ENGLISH);
			try {
				String str = sdf.format(date);
				return new Timestamp(sdf.parse(str).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String parseDateToFormatString(Date date,String patten){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			try {
				return sdf.format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String formatJsonToString(JSONObject json,String patten){
		long time = json.getLong("time");
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		return sdf.format(date);
	}
	
	/**
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public String getJsonString(JSONObject json, String key) {
		if (json.containsKey(key)) {
			return json.getString(key);
		} else {
			return "";
		}
	}
	
	/**
	 * 向目标DataSet中添加Field
	 * @param ds
	 * @return
	 */
	public DataSet makeSubValDataSet(DataSet ds){
		int totalCount=0;
		int count = 0;
		DataSet sv = new DataSet();
		List<JSONObject> list = new ArrayList<JSONObject>();
		if(null!=ds&&ds.getTotal()>0){
			totalCount = ds.getTotal();
			JSONArray jsonArray = ds.getJAData();
			count = jsonArray.size();
			for (int i = 0; i < count; i++) {	
				JSONObject record = jsonArray.getJSONObject(i);
				//解决webservice服务器端 采用jdom解析时对象读取xml出错的错误
				String s = record.get("XMLVALUE").toString();
				if(s.equals("")){
					break;
				}
				Document xmlv = xmlTransToJDom(record.get("XMLVALUE").toString());				
				JSONObject rs = new JSONObject();
				record.accumulate("XMLVALUE", "");
				List childNodes = xmlv.selectNodes("//c");
				for(Object obj:childNodes){
					Node childNode = (Node)obj; 
					rs.accumulate(childNode.valueOf("@n"), childNode.getText());
					//rs.accumulate("SN", record.get("SN"));
				}
				if(record.get("SEGMENTID") != null){
				    rs.accumulate("SEGMENTID", record.get("SEGMENTID"));
				}
				if(record.get("SEGMENTNAME") != null){
				    rs.accumulate("SEGMENTNAME", record.get("SEGMENTNAME"));
				}
				
				list.add(rs);
			}
		}
		try{
			sv.setData(list.toString().getBytes("UTF-8"));
			sv.setTotal(totalCount);
		}catch(Exception e){
			e.printStackTrace();
		}
		return sv;
	}
	
	/**
	 * 向目标DataSet中添加Field
	 * @param ds
	 * @return
	 */
	public DataSet makeFieldDataSet(DataSet ds){
		DataSet result = new DataSet();
		if(null!=ds&&ds.getTotal()>0){
			JSONObject record = ds.getRecord(0);
			Document xmlv = xmlTransToJDom(record.get("XMLVALUE").toString());
			record.accumulate("XMLVALUE", "");
			List childNodes = xmlv.selectNodes("//c");
			for(Object obj:childNodes){
				Node childNode = (Node)obj; 
				record.accumulate(childNode.valueOf("@n"), childNode.getText());
			}
			try{
				result.setData(record.toString().getBytes("UTF-8"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 解析XML数据
	 * @param pstr
	 * @return
	 */
	public Document xmlTransToJDom(String pstr){
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(pstr);
		} catch (DocumentException e) {
			System.out.println("webservice服务器端 采用jdom解析时对象读取xml出错。错误描述为：" + e.getMessage());
			e.printStackTrace();
		}
		return doc;
	}
	
	//判断字符串是否为空
	public boolean isNotNull(String str){
		if(str==null||"".equalsIgnoreCase(str)||"null".equalsIgnoreCase(str))
			return false;
		return true;
	}
	
	private  UserInfo intiUcUserInfo(JSONObject jo,String tokenid){
		UserInfo ui = new UserInfo();
		ui.setUserId(jo.getString("ACCOUNT"));
		ui.setMobile(jo.getString("PHONE"));
		ui.setNo(jo.getString("CARD_NO"));
		ui.setUid(jo.getInt("ID"));
		ui.setUserName(jo.getString("NAME"));
		ui.setType(jo.getInt("TYPE"));
		ui.setTickId(tokenid);
		return ui;
	}
	
	/**
	 * 
	 * @param uds
	 *            用户信息结果集
	 * @param tokenid
	 *            从CA服务端返回的信令
	 * @return 将用户信息写入缓存
	 */
	public  DataSet setUcUserInfo(JSONObject jo,String sessionKey, String tokenid) {
		DataSet ds = new DataSet();
		try {
			UserInfo ui = this.intiUcUserInfo(jo, tokenid);
			CacheManager.evict(Constant.SESSIONID, sessionKey);
			CacheManager.set(Constant.SESSIONID, sessionKey, ui);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}
	
	/**
	 * 
	 * @param uds
	 *            用户信息结果集
	 *            
	 * @return 将用户信息写入缓存
	 */
	public  DataSet setUcUserInfo(JSONObject jo,String sessionKey) {
		DataSet ds = new DataSet();
		try {
			UserInfo ui = this.intiUcUserInfo(jo, null);
			CacheManager.evict(Constant.SESSIONID, sessionKey);
			CacheManager.set(Constant.SESSIONID, sessionKey, ui);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}
	
	public static void main(String[] args) {
		System.out.println(CommonUtils.getInstance().parseDateToTimeStamp(new Date(),YYYY_MM_DD_HH_MM_SS_SSS));
		System.out.println(CommonUtils.getInstance().parseStringToTimeStamp("2012-12-12 23",CommonUtils.YYYY_MM_DD_HH));
	}
	
}
