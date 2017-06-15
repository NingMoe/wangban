package action.icity;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import core.util.HttpClientUtil;

public class ibook extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
		String url=SecurityConfig.getString("synchronousDept4dt");
		Map<String, String> map=this.getPostData();
		String service=map.get("Service");
		StringBuffer YYDatestr = new StringBuffer();
		String YYDate="";
		if("Reserve.GetRecordCount".equals(service)){
			String[] YYDates=map.get("YYDate").split(",");
			for (String rq : YYDates) {
				YYDatestr.append("'"+rq+"',");
			}
			YYDate = YYDatestr.toString();
			if(StringUtils.isNotEmpty(YYDate)){
				YYDate=YYDate.substring(0, YYDate.length()-1);
			}
			map.put("YYDate", YYDate);
		}
		String param=JSONObject.fromObject(map).toString();
		String result = new HttpClientUtil().getResult(url, param, true);
		this.setHeader("Content-Type", "application/json;charset=UTF-8");
		this.setHeader("Pragma", "No-cache");
        this.setHeader("Cache-Control", "no-cache");
        this.write(result);
		return false;
		
}}
