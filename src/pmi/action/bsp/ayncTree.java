package action.bsp;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;

public class ayncTree extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}

	public boolean handler(Map<String, Object> data) {
		String action = this.getParameter("action");
		String region_code = this.getPostData().get("CODE");
		String tag = "0";
		if (region_code == null) {
			tag = "1";
			region_code = SecurityConfig.getString("WebRegion");
			if ("zs".equals(SecurityConfig.getString("AppId"))) {
				tag = "2";
				region_code = "330901000000";
			}
		}
		if ("asyncNewOrg".equals(action)) {
			asyncNewOrg(region_code, tag);
		} else if ("asyncNewRegion".equals(action)) {
			asyncNewRegion(region_code, tag);
		}
		return false;
	}

	// 异步组织机构树
	public void asyncNewOrg(String region_code, String tag) {
		JSONArray obj = new JSONArray();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/asyncNewOrg");
		Map<String, String> map=new HashMap<String, String>();
		map.put("region_code", region_code);
		try {
			obj = JSONArray.fromObject(RestUtil.postData(url, map));
			if ("1".equals(tag)) {
				JSONObject AppCity = new JSONObject();
				AppCity.put("NAME", SecurityConfig.getString("AppCity"));
				AppCity.put("CODE", SecurityConfig.getString("WebRegion"));
				AppCity.put("PARENT_CODE", "0");
				AppCity.put("isParent", true);
				AppCity.put("nocheck", true);
				obj.add(AppCity);
			} else if ("2".equals(tag)) {
				JSONObject AppCity = new JSONObject();
				AppCity.put("NAME", "舟山市");
				AppCity.put("CODE", "330901000000");
				AppCity.put("PARENT_CODE", "0");
				AppCity.put("isParent", true);
				AppCity.put("nocheck", true);
				obj.add(AppCity);
			}
		} catch (Exception e) {
		}
		this.write(obj.toString());
	}

	// 异步区划
	public void asyncNewRegion(String region_code, String tag) {
		JSONArray obj = new JSONArray();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl") + "/web/c/asyncNewRegion");
		Map<String, String> map=new HashMap<String, String>();
		try {
			if ("1".equals(tag)) {
				map.put("region_code", region_code);
				obj = JSONArray.fromObject(RestUtil.postData(url, map));
				JSONObject AppCity = new JSONObject();
				AppCity.put("NAME", SecurityConfig.getString("AppCity"));
				AppCity.put("CODE", SecurityConfig.getString("WebRegion"));
				AppCity.put("PARENT_CODE", "0");
				AppCity.put("isParent", true);
				AppCity.put("nocheck", true);
				obj.add(AppCity);
			}else if("2".equals(tag)){//浙江 舟山 三级目录
				map.put("region_code", "330000000000");//浙江省
				obj = JSONArray.fromObject(RestUtil.postData(url, map));//获取市级
				JSONArray objTem = new JSONArray();
				for(int i = 0;i<obj.size();i++){
					JSONObject item = obj.getJSONObject(i);
					map.clear();
					map.put("region_code", item.get("CODE")+"");
					objTem.addAll(JSONArray.fromObject(RestUtil.postData(url, map)));//获取县级
				}
				for(int j = 0;j<objTem.size();j++){
					JSONObject objArea = objTem.getJSONObject(j);
					if("330902000000".equals(objArea.get("CODE"))){
						obj.add(objArea);
					}
					if("330903000000".equals(objArea.get("CODE"))){
						obj.add(objArea);
					}
					if("330921000000".equals(objArea.get("CODE"))){
						obj.add(objArea);
					}
					if("330922000000".equals(objArea.get("CODE"))){
						obj.add(objArea);
					}
				}
				JSONObject AppCity = new JSONObject();
				AppCity.put("NAME", "浙江省");
				AppCity.put("CODE", "330000000000");
				AppCity.put("PARENT_CODE", "0");
				AppCity.put("isParent", true);
				AppCity.put("nocheck", false);
				obj.add(AppCity);
			}
		} catch (Exception e) {
		}
		this.write(obj.toString());
	}

}
