package action.icity.submitsp;

import java.util.Map;

import org.eclipse.jetty.util.log.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import core.util.HttpClientUtil;

public class view extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
		if("fz".equals(SecurityConfig.getString("AppId"))){
			data.put("fieldSetting", new JSONObject());
			data.put("personMap", new JSONObject());
			return true;
		}else{
			String itemId=this.getParameter("itemId");
			String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemId);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
			JSONObject fieldSetting=new JSONObject();
			JSONArray personMap=new JSONArray();	
			JSONObject formInfo=form.getJSONObject("info");
				if(!formInfo.isNullObject()){
					fieldSetting=formInfo.getJSONObject("fieldSetting");
					if(formInfo.containsKey("personMap")){
						personMap = formInfo.getJSONArray("personMap");
					}
				}
				data.put("fieldSetting", fieldSetting);
				data.put("personMap", personMap);
				return true;
		}				
	}
}
