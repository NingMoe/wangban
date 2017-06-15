package api.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.StateType;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.DbHelper;


/**
 * 工具
 * @author lihongyun
 */
public class Tools{	
     String webUrl2 = "http://www.baidu.com";//百度
     String webUrl3 = "http://www.taobao.com";//淘宝
     String webUrl4 = "http://www.ntsc.ac.cn";//中国科学院国家授时中心
     String webUrl5 = "http://www.360.cn";//360
     String webUrl6 = "http://www.beijing-time.org";//beijing-time     
     private static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6','7', '8', '9' };
	 private static final String ACCESS_TOKEN = "access_token";
	 private static final String CACHE_KEY_FLAG = "Tools";
	//region验证
	/**
	 * 验证	 validation=1 token 验证   =0或者空 白名单验证
	 * 错误代码：
	 * 100   成功
	 * 101 时间戳超期 （30秒内）
	 * 102 随机码重复
	 * 103 ACCESS_TOKEN不可为空
	 * 104  非法ACCESS_TOKEN
	 * 105 ACCESS_TOKEN已过期
	 * 106 验证ACCESS_TOKEN是否过期时出错
	 * 107 错误的ACCESS_TOKEN
	 * 201 非法访问
	 */
	public DataSet validate(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject m_data = new JSONObject();
		m_data.put("code", "100");
		m_data.put("msg", "验证通过");
		ds.setRawData(m_data);		
		String validation=pSet.containsKey("validation")?(String)pSet.get("validation"):"0";//验证方式默认为0白名单1token
		if(validation.equals("1")){
			String access_token=pSet.containsKey(ACCESS_TOKEN)?(String)pSet.get(ACCESS_TOKEN):"";
			if(!"".equals(access_token)&&access_token!=null){
				ParameterSet m_pSet = new ParameterSet();
				m_pSet.put("data", access_token);				
				String m_access_token = decrypt(m_pSet);
				if(m_access_token.length()<38){
					ds.setMessage("");					
					m_data.put("code", "107");
					m_data.put("msg", "错误的ACCESS_TOKEN");
					ds.setRawData(m_data);
					ds.setState((byte)2);
					return ds;
				}
				access_token = m_access_token.substring(6, 38);					
				String dateTime = m_access_token.split(access_token)[1];
				String random = dateTime+m_access_token.substring(0, 6);
				Date date = getWebsiteDatetime(webUrl4);				
				if(date!=null){
					if(Math.abs(date.getTime()- Long.parseLong(dateTime))/1000<30){						
						//时间戳验证成功后先验证token是否合法
						Connection conn = DbHelper.getConnection("icityDataSource");        
				        String sql_token = "select t.*,to_char(t.EXPIRESTIME,'yyyy-MM-dd') EXPIRESTIME_CHAR from CUST_ACCESSTOKEN t where t.ACCESSTOKEN = ?";
				        DataSet ds_token = DbHelper.query(sql_token,new Object[] { access_token }, conn);
				        if(ds_token.getTotal()==0){
				        	ds.setMessage("");					
							m_data.put("code", "104");
							m_data.put("msg", "非法ACCESS_TOKEN");
							ds.setRawData(m_data);
				        	ds.setState((byte)2);
				        	return ds;
				        }else{       
					    	String expires_in = ds_token.getJOData().getString("EXPIRESTIME_CHAR");
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							Date m_date;
							try	{
								m_date = sdf.parse(expires_in);
								if(m_date.getTime() < new Date().getTime()){
									ds.setMessage("");					
									m_data.put("code", "105");
									m_data.put("msg", "ACCESS_TOKEN已过期");
									ds.setRawData(m_data);
									ds.setState((byte)2);
									return ds;
								}
							} catch (ParseException e)	{
								e.printStackTrace();
								ds.setMessage("");					
								m_data.put("code", "106");
								m_data.put("msg", e.toString());
								ds.setRawData(m_data);
					        	ds.setState((byte)2);
					        	return ds;
							}
				        }
						//时间戳验证成功后再验证随机码是否重复
						ArrayList m_randomArray = new ArrayList();
						synchronized (access_token.intern()) {
							ArrayList randomArray = (ArrayList) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, access_token);
							if (randomArray == null) {								
								m_randomArray.add(random);
							}else{
								int len = randomArray.size();
								if(len==100){									
									for(int i=0;i<len-1;i++){
										String m_random = randomArray.get(i+1).toString();
										if(m_random.equals(random)){
											ds.setMessage("");					
											m_data.put("code", "102");
											m_data.put("msg", "随机码重复");
											ds.setRawData(m_data);
								        	ds.setState((byte)2);
								        	return ds;
										}else{
											m_randomArray.add(i, randomArray.get(i+1));
										}
									}
									m_randomArray.add(len-1,random);
								}else{
									for(int i=0;i<len;i++){
										String m_random = randomArray.get(i).toString();
										if(m_random.equals(random)){
											ds.setMessage("");					
											m_data.put("code", "102");
											m_data.put("msg", "随机码重复");
											ds.setRawData(m_data);
								        	ds.setState((byte)2);
								        	return ds;
										}
									}
									randomArray.add(len, random);
									m_randomArray = (ArrayList)randomArray.clone();
								}
							}
							CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, access_token, m_randomArray);
						}
					}else{
						ds.setMessage("");					
						m_data.put("code", "101");
						m_data.put("msg", "时间戳超期");
						ds.setRawData(m_data);
			        	ds.setState((byte)2);
			        	return ds;
					}
				}						        
			}else{
				ds.setMessage("");					
				m_data.put("code", "103");
				m_data.put("msg", "ACCESS_TOKEN不可为空");
				ds.setRawData(m_data);
				ds.setState((byte)2);
				return ds;
			}
		}else{
			String access = pSet.getRemoteAddr();
			if("on".equals(SecurityConfig.getString("accessSwith"))){
				if(!SecurityConfig.getString("accessWhiteList").contains(access)){					
					ds.setMessage("");					
					m_data.put("code", "201");
					m_data.put("msg", access+"非法访问");
					ds.setRawData(m_data);
					ds.setState((byte)2);
				}
			}
		}
		return ds;
	}
	//endregion
	
	//region验证ip checkInfo
	/**
	 * 验证ip
	 * access：ip地址
	 */
	public boolean checkInfo(String access){
		boolean result = true;
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				result = true;
			}else{
				result = false;
			}
		}else{
			result = true;
		}
		return result;
	}
	//endregion
	
	//region错误提示errorInfo
	/**
	 * 错误提示
	 * code:错误代码；message：错误提示内容
	 */
	public DataSet errorInfo(int code,String message){
		DataSet ds = new DataSet();
		ds.setRawData(new JSONArray());		
		switch(code){
			case 0://失败
				ds.setState((byte)0);
				break;
			case 2://ip地址不在允许范围内
				ds.setState((byte)2);
				break;
			case 3://未登录或未传递用户id等用户信息
				ds.setState((byte)3);
				break;
		}
		ds.setMessage(message);
		return ds;
	}
	//endregion
	
	//region MD5加密 toMD5
	/**
	 * TO MD5
	 * @param plainText
	 * @return
	 */
	public String toMD5(String plainText) {
		String md5 = "";
		try {
			// 生成实现指定摘要算法的 MessageDigest 对象。
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 使用指定的字节数组更新摘要。
			md.update(plainText.getBytes());
			// 通过执行诸如填充之类的最终操作完成哈希计算。
			byte b[] = md.digest();
			// 生成具体的md5密码到buf数组
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			md5 = buf.toString();
			// System.out.println("32位: " + buf.toString());// 32位的加密
			// System.out.println("16位: " + buf.toString().substring(8, 24));//
			// 16位的加密，其实就是32位加密后的截取
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5;
	}
	//endregion

	//region 解密decrypt
	/**
	 * 解密
	 */
	public String decrypt(ParameterSet pSet){
		String data = (String)pSet.get("data");
		try	{
			data = java.net.URLDecoder.decode(data,"UTF-8");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		StringBuilder a = new StringBuilder();
		StringBuilder b = new StringBuilder();
		StringBuilder c = new StringBuilder();			
		char[] chars = data.toCharArray();
		for (int i = 0; i < chars.length; i++){
			if(i%2==0){
				a.append((char)((int) chars[i]-4));
			}else{
				a.append((char)((int) chars[i]-5));
			}
		}
		chars = a.toString().toCharArray();
		for (int i = chars.length-1; i >-1 ; i--){
			b.append((char)((int) chars[i]-3));
		}
		chars = b.toString().toCharArray();
		for (int i = 0; i < chars.length; i++){
			c.append((char)((int) chars[i]-2));
		}		
		return c.toString();
	}
	//endregion
	
	//region加密encrypt
	/**
	 *加密
	 */
	public String encrypt(ParameterSet pSet) {
		String data = (String)pSet.get("data");
		char[] chars = data.toCharArray();
		StringBuilder a = new StringBuilder();
		StringBuilder b = new StringBuilder();
		StringBuilder c = new StringBuilder();		
		for (int i = 0; i < chars.length; i++){
			a.append((char)((int) chars[i]+2));
		}
		chars = a.toString().toCharArray();
		for (int i = chars.length-1; i >-1 ; i--){
			b.append((char)((int) chars[i]+3));
		}
		chars = b.toString().toCharArray();
		for (int i = 0; i < chars.length; i++){
			if(i%2==0){
				c.append((char)((int) chars[i]+4));
			}else{
				c.append((char)((int) chars[i]+5));
			}
		}
		String result = "";
		try	{
			result = java.net.URLEncoder.encode(c.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return result;
	}
	//endregion
	
	//region获取指定网站的日期时间
	 /**
     * 获取指定网站的日期时间
     * @param webUrl
     * @return
     * @author SHANHY
     * @date   2015年11月27日
     */
    public Date getWebsiteDatetime(String webUrl){
        try {
            URL url = new URL(webUrl);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间            
            Date date = new Date(ld);// 转换为标准时间对象
            return date;
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 输出北京时间
            //return sdf.format(date);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion
    
    //region获取随机数getRandom
    /**
     * 获取随机数
     * @return
     */
    public String getRandom(){
    	SecureRandom random = new SecureRandom();
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < 6; i++) {
			String rand = String
					.valueOf(CHARS[random.nextInt(CHARS.length - 1)]);
			sRand.append(rand);
		}
		return sRand.toString();
    }
    //endregion
}