package app.pmi.accesstoken;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.icore.StateType;
import com.icore.util.SecurityConfig;
import com.icore.util.Tools;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class AuthControl extends BaseQueryCommand{ 
	//Logger logger = LoggerFactory.getLogger(getClass());
	private static final String ACCESS_TOKEN = "access_token";
    private static final String CACHE_KEY_FLAG = "AuthControl";
    private static final String TOKEN_SCOPE_GUEST = "guest";
	private static final String TOKEN_SCOPE_LOGINUSER = "login";
    
    @SuppressWarnings("deprecation")
	public DataSet authController(ParameterSet pSet) throws Exception {    	
        String token = (String)pSet.get(ACCESS_TOKEN);
        DataSet ds_return = new DataSet();
        ds_return.setState(StateType.FAILT);
        if("".equals(token)||token==null){        	
        	ds_return.setMessage("ACCESS_TOKEN不可为空！");
            return ds_return;
        }        
		Connection conn = DbHelper.getConnection("icityDataSource");        
        String sql_token = "select t.*,to_char(t.EXPIRESTIME,'yyyy-MM-dd') EXPIRESTIME_CHAR from CUST_ACCESSTOKEN t where t.ACCESSTOKEN = ?";
        DataSet ds_token = DbHelper.query(sql_token,new Object[] { token }, conn);
        if(ds_token.getTotal()==0){
        	ds_return.setMessage("非法ACCESS_TOKEN！");
            return ds_return;
        }       
    	String expires_in = ds_token.getJOData().getString("EXPIRESTIME_CHAR");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(expires_in);
		if(date.getTime() < new Date().getTime()){
			ds_return.setMessage("ACCESS_TOKEN已过期！");
			return ds_return;
		}
        DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, token);
		if (ds == null) {
			synchronized (token.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, token);
				if (ds == null) {
					if (StateType.SUCCESS == ds_token.getState()&&ds_token.getTotal()>0) {
						JSONObject jo = new JSONObject();					
						jo.put("access_token",ds_token.getJOData().getString("ACCESSTOKEN"));
						jo.put("expires_in", ds_token.getJOData().getString("EXPIRESTIME_CHAR"));
						jo.put("scope", ds_token.getJOData().getString("SCOPE"));
						jo.put("userinfo", "");
						jo.put("ucid", ds_token.getJOData().getString("CUSTID"));
						ds = new DataSet();
						ds.setState(StateType.SUCCESS);
						ds.setRawData(jo);
						if(TOKEN_SCOPE_LOGINUSER.endsWith(ds_token.getJOData().getString("SCOPE"))){
							loginByToken(ds_token.getJOData().getString("CUSTID"),jo);
						}else{
							CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, token, ds);
						}						
					}else{
						ds = new DataSet();
						ds.setState(StateType.FAILT);
			            ds.setMessage("非法ACCESS_TOKEN！");
					}
				}
			}
		}
		return ds;
    }
    /**
     * 添加或者更新设备信息，及添加或更新token
     * @param deviceToken 设备标识
     * @param pushToken 推送标识
     * @param os ios或 Android
     * @param model 手机类型
     * @param osVersion 手机版本号
     * @param appVersion 用户下载APP的版本号
     * @return
     */
    public DataSet makeAccessToken(ParameterSet pSet){
    	DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String ID = Tools.getUUID32();
			String DEVICETOKEN = (String)pSet.get("deviceToken");//设备标识
			String PUSHTOKEN = (String)pSet.get("pushToken");//推送标识
			String OS = (String)pSet.get("os");//android或ios
			String MODEL = (String)pSet.get("model");//手机类型
			String OSVERSION = (String)pSet.get("osVersion");//手机版本号
			String APPVERSION = (String)pSet.get("appVersion");//用户下载APP的版本号
			
			String sql_device = "select ID from cust_device t where t.DEVICETOKEN = ?";
			DataSet ds_device = DbHelper.query(sql_device,	new Object[] { (String)pSet.get("deviceToken") }, conn);
			if(ds_device.getTotal()>0){
				String DEVICEID = ds_device.getJOData().getString("ID");
				ParameterSet m_pSet = new ParameterSet();
				m_pSet.put("DEVICEID", DEVICEID);
				m_pSet.put("ACCESSTOKEN", ID);
				DataSet m_ds = updateToken(m_pSet);
				if(m_ds.getState()==StateType.SUCCESS){
					JSONObject jo = new JSONObject();
					jo.put("access_token", ID);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("更新成功！");
					ds.setRawData(jo);
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage("更新失败！");
				}
			}else{
				String sql_device_insert = "insert into cust_device t (ID,DEVICETOKEN,PUSHTOKEN,OS,MODEL," +
						"OSVERSION,APPVERSION,CREATETIME,UPDATETIME) values (?,?,?,?,?,?,?,sysdate,sysdate)";
				int i = DbHelper.update(sql_device_insert, 
						new Object[]{ID,DEVICETOKEN,PUSHTOKEN,OS,MODEL,OSVERSION,APPVERSION}, conn);
				if(i==0){
					ds.setState(StateType.FAILT);
					ds.setMessage("添加设备信息失败！");
				}else{
					ParameterSet m_pSet = new ParameterSet();
					m_pSet.put("DEVICEID", ID);
					m_pSet.put("ACCESSTOKEN", ID);
					DataSet m_ds = updateToken(m_pSet);
					if(m_ds.getState()==StateType.SUCCESS){
						JSONObject jo = new JSONObject();
						jo.put("access_token", ID);
						ds.setState(StateType.SUCCESS);
						ds.setMessage("添加成功！");
						ds.setRawData(jo);
					}else{
						ds.setState(StateType.FAILT);
						ds.setMessage("添加失败！");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("添加失败！");
		}
		return ds;
    }
    /**
     * 更新token
     * @param pSet
     * ID	VARCHAR2(50)	N			主键
	 * ACCESSTOKEN	VARCHAR2(45)	N			访问标记
	 * DEVICEID	VARCHAR2(20)	N			设备id
	 * CUSTID	VARCHAR2(20)	Y			用户id
	 * TOKENTYPE	VARCHAR2(45)	Y			标记类型
	 * SCOPE	VARCHAR2(45)	Y			范围
	 * STATE	VARCHAR2(45)	Y			状态
	 * DISABLED	VARCHAR2(20)	Y			是否可用
	 * EXPIRESTIME	DATE	Y			过期时间+90天
	 * CREATETIME	DATE	N			创建时间
	 * UPDATETIME	DATE	Y			更新时间	
     * @return
     */
    public DataSet updateToken(ParameterSet pSet){
    	DataSet ds = new DataSet();
    	Connection conn = DbHelper.getConnection("icityDataSource");
    	try{
    		String ID = Tools.getUUID32();
    		String ACCESSTOKEN = (String)pSet.get("ACCESSTOKEN"); 
    		String DEVICEID = (String)pSet.get("DEVICEID");
    		
    		String sql = "select * from CUST_ACCESSTOKEN t where t.DEVICEID = ?";
    		DataSet ds_token = DbHelper.query(sql, new Object[]{DEVICEID}, conn);
    		if(ds_token.getTotal()>0){
    			String sql_accesstoken_update = "update CUST_ACCESSTOKEN t set t.ACCESSTOKEN = ? ," +
    					"t.EXPIRESTIME = sysdate+90,t.UPDATETIME=sysdate where t.DEVICEID=?";
    			int i = DbHelper.update(sql_accesstoken_update, 
    					new Object[]{ACCESSTOKEN,DEVICEID}, conn);
    			if(i==0){
    				ds.setState(StateType.FAILT);
    				ds.setMessage("更新失败！");
    			}else{
    				ds.setState(StateType.SUCCESS);
    				ds.setMessage("更新成功！");
    			}
    		}else{
    			String sql_accesstoken_insert = "insert into CUST_ACCESSTOKEN t (ID,ACCESSTOKEN,DEVICEID,CUSTID," +
    			"TOKENTYPE,SCOPE,STATE,DISABLED,EXPIRESTIME,CREATETIME,UPDATETIME) values (?,?,?,?,?,?,?,?,sysdate+90,sysdate,sysdate)";
    			int i = DbHelper.update(sql_accesstoken_insert, 
    					new Object[]{ID,ACCESSTOKEN,DEVICEID,"","",TOKEN_SCOPE_GUEST,"1","1"}, conn);
    			if(i==0){
    				ds.setState(StateType.FAILT);
    				ds.setMessage("添加失败！");
    			}else{
    				ds.setState(StateType.SUCCESS);
    				ds.setMessage("添加成功！");
    			}
    		}    		
    	}catch(Exception e){
    		ds.setState(StateType.FAILT);
    		ds.setMessage(e.toString());
    	}
    	return ds;
    }
    /**
     * 检测是否过期,返回信息带上是否登录
     * @param token
     * @return
     */
    @SuppressWarnings("deprecation")
	public DataSet checkAccessToken(ParameterSet pSet){
    	DataSet ds_return = new DataSet();
    	try{
    		DataSet ds = authController(pSet);
        	if(ds.getState()==StateType.SUCCESS){
        		String expires_in = ds.getJOData().getString("expires_in");
        		String scope = ds.getJOData().getString("scope");
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            	Date date = sdf.parse(expires_in);
                if(ds.getJOData().getString("access_token")!= null && date.getTime() > new Date().getTime()){
                	String ucid = ds.getJOData().getString("ucid");
                	if(!"".equals(ucid)&&TOKEN_SCOPE_LOGINUSER.equals(scope)){
                		loginByToken(ucid,ds.getJOData());
                	}               	
                	ds_return.setState(StateType.SUCCESS);
                	ds_return.setMessage("ACCESS_TOKEN正常！"); 
                	JSONObject rawData = new JSONObject();
                	rawData.put("scope", scope);
                	ds_return.setRawData(rawData);
                }else{
                	ds_return.setState(StateType.FAILT);
                	ds_return.setMessage("ACCESS_TOKEN已超期！");
                }
        	}else{
        		ds_return.setState(StateType.FAILT);
        		ds_return.setMessage(ds.getMessage());
        	}        	
    	}catch(Exception e){
    		ds_return.setState(StateType.FAILT);
    		ds_return.setMessage("检测方法异常！");
    	}    	
        return ds_return;
    }
	/**
	 * 根据用ID登录
	 */
	private void loginByToken(String ucid,JSONObject jo_token) {
		DataSet ds_select;
		DataSet ds_login = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String select = "SELECT * FROM UC_USER WHERE ID = ?";
		try {
			ds_select = DbHelper.query(select, new Object[] { Integer.parseInt(ucid) }, conn);
			if(ds_select.getTotal()>0){
				JSONObject record = ds_select.getRecord(0);
				jo_token.put("scope", TOKEN_SCOPE_LOGINUSER);
				jo_token.put("userinfo", record);
				ds_login.setRawData(jo_token);
				CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG,jo_token.getString("access_token"), ds_login);				
			}				
		} catch (Exception e) {
			e.printStackTrace();			
			
		} finally {
			DBSource.closeConnection(conn);
		}
	}
}