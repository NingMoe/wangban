package action.zhangzhou;

import app.uc.GetUserMapDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import net.sf.json.JSONObject;

import java.util.Map;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("null")
	public boolean handler(Map<String, Object> data) {
		JSONObject access_token = (JSONObject)CacheManager.get(CacheManager.EhCacheType, "zhangzhoulogin", "zhangzhoulogin");
		JSONObject obj=null;
		String next = this.getParameter("goto");
		String user_token = this.getParameter("user_token");
		try{
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ca_4_serviceurl")+"?access_token="+access_token.getString("access_token")+
					"&user_token="+user_token);
			obj = JSONObject.fromObject(RestUtil.getData(url));
		}catch(Exception e){
			e.printStackTrace();
		}
		if(obj!=null&&obj.getBoolean("result")){			
			Command m_cmd = new Command("app.uc.LoginCmd");
			JSONObject user = null;
			JSONObject jo = new JSONObject();
			
			Command cmd = new Command("app.uc.UserCmd");			
			if("personal".equals(obj.getJSONObject("datas").getString("user_type"))){
				user = obj.getJSONObject("datas").getJSONObject("personal");
				cmd.setParameter("ACCOUNT", user.getString("login_name"));
				jo.put("ACCOUNT", user.getString("login_name"));
				jo.put("PHONE", user.getString("mobile_phone"));
				jo.put("CARD_NO", user.getString("certificate_number"));
				jo.put("ID", "11");
				jo.put("NAME", user.getString("name"));
				jo.put("TYPE", "11");
				user.put("type", "11");
			}else{
				user = obj.getJSONObject("datas").getJSONObject("enterprise");
				cmd.setParameter("ACCOUNT", user.getString("login_name"));
				jo.put("ACCOUNT", user.getString("login_name"));
				jo.put("PHONE", user.getString("org_law_mobile"));
				jo.put("CARD_NO", user.getString("org_law_idcard"));
				jo.put("ORG_NAME", user.getString("org_name"));
				jo.put("ID", "21");
				jo.put("NAME", user.getString("name"));
				jo.put("TYPE", "21");
				user.put("type", "21");
			}
			DataSet user_ds = cmd.execute("getList");
			if(user_ds.getState()==StateType.SUCCESS){
				if(user_ds.getTotal()==0){
					GetUserMapDao um = GetUserMapDao.getInstance();
					DataSet user_load_ds = um.insertIntoUcUser(user);
					if(user_load_ds.getState()==StateType.SUCCESS){
						jo.put("ID", user_load_ds.getRawData());
					}
					System.out.println(user_load_ds.getMessage());
				}else{
					jo.put("ID", user_ds.getJAData().getJSONObject(0).getString("ID"));
				}
				m_cmd.setParameter("jo", jo);
				m_cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
				m_cmd.execute("setUcUserInfo");
				if (next == null || "".equals(next) || "null".equals(next)) {
					this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
				} else {
					this.sendRedirect(HttpUtil.formatUrl(next));
				}	
			}else{
				this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
			}
		}else{
			this.sendRedirect(HttpUtil.formatUrl(UrlHelper.contextPath() + "/index"));
		}	
		return false;
	}	
}
