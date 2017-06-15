package hlj_qqheNew.action.icity;

import io.netty.util.CharsetUtil;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;

import core.util.HttpClientUtil;

public class cform extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7241691301759789670L;

	public boolean handler(Map<String,Object> data){
		/*DataSet ds = new DataSet();
		String url1 = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"getFlow");
		HttpClient client1 = new HttpClient();
		client1.getParams().setContentCharset("UTF-8");	
		
		PostMethod postMethod = new PostMethod(url1);
		postMethod.setParameter("regionCode","230601000000");
		try {
			client1.executeMethod(postMethod);
			JSONObject obj = new JSONObject();	
			//String obj ="{'retCode':'200','errors':'error information','stage':[{'stageId':1,'stageName':'立项阶段'},{'stageId':2,'stageName':'用地审批'},{'stageId':3,'stageName':'规划报建'} ,{'stageId':4,'stageName':'施工许可'} ,{'stageId':5,'stageName':'竣工验收'}]}";
			 obj =   JSONObject.fromObject(postMethod.getResponseBodyAsString());
			ds.setRawData(obj);
		} catch (Exception e) {
		e.printStackTrace();
		}*/
		
		String url=SecurityConfig.getString("Form_url"); 
		String action=this.getParameter("action");
		if("getFormUI".equals(action)){
			String formId = this.getParameter("formId");
			url+="/cform/getFormUI?formId="+formId;
			HttpClientUtil client = new HttpClientUtil();
			String html= client.getResult(url,"");
			this.write(html);
			return false;
		}
		if("getFormData".equals(action)){
			String formId = this.getParameter("formId");
			String formDataId=this.getParameter("formDataId");
			url+="/cform/getFormStandardData?formId="+formId+"&formDataId="+formDataId;
			HttpClientUtil client = new HttpClientUtil();
			String html= client.getResult(url,"");
			this.setHeader("Content-Type", "application/json;charset=UTF-8");
			this.setHeader("Pragma", "No-cache");
	        this.setHeader("Cache-Control", "no-cache");
			this.write(html);
			return false;
		}
		if("getDataBind".equals(action)){
			String formId = this.getParameter("formId");
			url+="/cform/getDataBind?formId="+formId;
			HttpClientUtil client = new HttpClientUtil();
			String html= client.getResult(url,"");
			this.setHeader("Content-Type", "application/json;charset=UTF-8");
			this.setHeader("Pragma", "No-cache");
	        this.setHeader("Cache-Control", "no-cache");
			this.write(html);
			return false;
		}
		if("saveData".equals(action)){
			String formId = this.getParameter("formId");
			String formData=this.getContent().toString(CharsetUtil.UTF_8);
			url+="/cform/saveData?formId="+formId;
			HttpClientUtil client = new HttpClientUtil();
			String html= client.getResult(url,formData);
			this.setHeader("Content-Type", "application/json;charset=UTF-8");
			this.setHeader("Pragma", "No-cache");
	        this.setHeader("Cache-Control", "no-cache");
			this.write(html);
			return false;
		}
		return false;
		
}}
