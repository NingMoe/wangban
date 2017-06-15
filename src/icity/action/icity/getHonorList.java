package action.icity;

import io.netty.util.CharsetUtil;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.params.HttpClientParams;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import core.util.HttpClientUtil;

public class getHonorList extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
		String url=SecurityConfig.getString("syncGetHonorList")+"/web/queueCity/serviceInfo";
		String param=JSONObject.fromObject(this.getPostData()).toString();
		String result = new HttpClientUtil().getResult(url, param, true);
		this.setHeader("Content-Type", "application/json;charset=UTF-8");
		this.setHeader("Pragma", "No-cache");
        this.setHeader("Cache-Control", "no-cache");
        this.write(result);
		return false;
		
}}
