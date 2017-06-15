package app.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import core.util.HttpClientUtil;
/**
 * 通过http协议调用远程接口的方法
 * @author hxk
 *
 */
public class CommandProxy {
	@SuppressWarnings("unused")
	private static final Log _log = LogFactory.getLog(CommandProxy.class);
	private ParameterSet  paramsObj = new ParameterSet();
	private String _action = "";
	private String _sessionId = "";
	private String _url = "";
	
	//是否需要用户认证
	private boolean _isVerify = true;
	
	public CommandProxy(String action) 
    {
        _action = action;
    }

	public void setParameter(String key, Object value)
    {
        paramsObj.setParameter(key, value);
    }
	
	public void setParameter(ParameterSet p){
		paramsObj = p;
		_sessionId = p.getSessionId();
	}
	
	public void setSessionId(String sessionId){
		_sessionId = sessionId;
	}
	
	public String getSessionId(){
		return _sessionId;
	}
	
	public void setIsVerify(boolean v){
		_isVerify = v;
	}
	
	public String getUrl() {
		return _url;
	}

	public void setUrl(String _url) {
		this._url = _url;
	}

	public DataSet execute(String method)
    {
        if(StringUtils.isNotEmpty(this.getSessionId())){
        	paramsObj.setSessionId(this.getSessionId());
        }
       
        try{
        	if(StringUtils.isBlank(this._url)){
        		this._url = SecurityConfig.getString("CommandProxy_RestUrl");
        	}
        	
        	//有配置远程的地址的则调用，否则放回
        	if(StringUtils.isNotEmpty(this._url)){
	        	//发起http请求调用服务
	        	HttpClientUtil  hc = new HttpClientUtil();
	        	//拼装服务的参数
	        	this._url+=_action + "/" + method;
	        	//调用服务
	        	String result=hc.getResult(this._url,JSONObject.fromObject(paramsObj).toString(), true);
	        	return jsonStringToDataSet(result);
        	}else{
        		DataSet ds  = new DataSet();
            	ds.setState(StateType.FAILT);
            	ds.setMessage("无配置远程服务");
            	return ds;
        	}
        }catch(Throwable ex){
        	DataSet ds  = new DataSet();
        	ds.setState(StateType.FAILT);
        	ds.setMessage(ex.getMessage());
        	return ds;
        }
    }
	
	//重构DataSet
	private DataSet jsonStringToDataSet(String r){
		DataSet ds = new DataSet();
		JSONObject jo =JSONObject.fromObject(r);
		if(jo.get("data")!=null){
			try {
				JSONArray data = jo.getJSONArray("data");
				ds.setRawData(data);
			} catch (Exception e2) {
				try {
					ds.setData(jo.getString("data").getBytes("utf-8"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		ds.setState((byte)jo.getInt("state"));
		ds.setTotal(jo.getInt("total"));
		ds.setMessage(jo.getString("message"));
		return ds;
	}

	
	public static void main(String[] args){
		String session_id = "202cb962ac59075b964b07152d234b70";
		CommandProxy cmdp= new CommandProxy("app.core.BspCmd");
		cmdp.setParameter("userId", "system");
		cmdp.setParameter("password", "202cb962ac59075b964b07152d234b70");
		cmdp.setSessionId(session_id);
		cmdp.execute("singleLogin");

		
		//String result=hc.getResult("http://127.0.0.1:8088/c/app.core.BspCmd/SingleLogin", paramsObj.toString(), true);
		//System.out.println(result);
	}
}
