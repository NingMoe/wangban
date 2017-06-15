package action.icity.enterprises;

import java.util.Map;

import com.inspur.base.BaseAction;

public class onlineDeal extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
		/*String itemId=this.getParameter("itemId");
		String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemId);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
		JSONObject fieldSetting=new JSONObject();
			JSONObject formInfo=form.getJSONObject("info");
			if(!formInfo.isNullObject()){
				fieldSetting=formInfo.getJSONObject("fieldSetting");
			}
			data.put("fieldSetting", fieldSetting);*/
			return true;
	}
}
