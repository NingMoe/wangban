package action.icity;

import io.netty.util.CharsetUtil;

import java.util.Map;

import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import core.util.HttpClientUtil;

public class cform extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
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
