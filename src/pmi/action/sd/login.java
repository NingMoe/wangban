package action.sd;

import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.Tools;

import app.icity.sync.UniteUserInterface;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import java.util.Map;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean handler(Map<String,Object> data){
		String ticket = this.getParameter("ticket");
		String userType = StringUtils.isEmpty(this.getParameter("userType")) ? "1"
				: this.getParameter("userType");
		String url = StringUtils.isEmpty(this.getParameter("gotoUrl"))?
				UrlHelper.index(this.getHttpAdapter()):this.getParameter("gotoUrl");
		if(this.getParameter("ticket").isEmpty()){
			CacheManager.evict(Constant.SESSIONID,this.getCookie(Constant.SESSIONID));
			setCookie(Constant.SESSIONID, Tools.getUUID32());
			this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
			return false;
		}else {
			uniteIdentifyLogin(ticket, userType);
			this.sendRedirect(url);
			return false;
		}
	}
	
	private JSONObject uniteIdentifyLogin(String ticket, String userType)
	{
		try{
			UniteUserInterface unitUI = new UniteUserInterface();
			unitUI.setUserType(userType);
			unitUI.setTicket(ticket);
			 unitUI.getToken();
			JSONObject userInfo = unitUI.getUserInfo();
			JSONObject uInfo = new JSONObject();
			uInfo.put("ACCOUNT", userInfo.get("loginname"));
			uInfo.put("NAME", userInfo.get("name"));
			String type="11";//默认是个人用户
			if("2".equals(userInfo.get("usertype"))){
				//法人类型：1-企业法人,2-非企业法人
				type="1".equals(userInfo.getString("type"))?"21":"31";
			}
			
			uInfo.put("TYPE", type);
			uInfo.put("SEX", userInfo.get("sex"));
			uInfo.put("EMAIL",userInfo.get("email"));
			uInfo.put("ADDRESS", userInfo.get("address"));
			uInfo.put("STATUS", userInfo.get("isauth"));
			uInfo.put("PHONE", userInfo.get("mobile"));
			uInfo.put("CARD_NO", "2".equals(userInfo.get("usertype"))?userInfo.get("idcard"):userInfo.get("cardid"));
			uInfo.put("ORG_NAME", "2".equals(userInfo.get("usertype"))?userInfo.get("name"):"");
			uInfo.put("ORG_NO", "2".equals(userInfo.get("usertype"))?userInfo.get("orgnumber"):"");
			uInfo.put("ORG_BOSS_NAME", userInfo.containsKey("realname")?userInfo.get("realname"):"");
			uInfo.put("ICREGNUMBER", "2".equals(userInfo.get("usertype"))?userInfo.get("regnumber"):"");
			uInfo.put("CREDIT_CODE", "");
			Command cmd = new Command("app.uc.GetUserMapDao");
    		cmd.setParameter("user_id_map", userInfo.getString("uuid"));
    		cmd.setParameter("uInfo", uInfo);
    		DataSet ds = cmd.execute("GetUid");
    		int ID;
    		if (ds.getState() == StateType.SUCCESS) {
    			ID = ds.getJAData().getJSONObject(0).getInt("USER_ID");
    			uInfo.put("ID", ID);
    		}
    		cmd = new Command("app.uc.LoginCmd");
    		cmd.setParameter("jo", uInfo);
    		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
    		ds = cmd.execute("setUcUserInfo");
    		return ds.getJOData();
		} catch(Exception ex ){
			ex.printStackTrace();
			return JSONObject.fromObject("{'error':'登录异常'}");
		}
	}
	
	public DataSet uniteIdentifyLoginLocal(ParameterSet pSet){
		try{
			String userType = (String) pSet.getParameter("userType");
			String loginname = (String) pSet.getParameter("account");
			String password = (String) pSet.getParameter("pwd");
			UniteUserInterface unitUI = new UniteUserInterface();
			unitUI.setUserType("fr".equals(userType)?"2":"1");
			 unitUI.getTokenInfoByUser(loginname, password);
			JSONObject userInfo = unitUI.getUserInfo();
			JSONObject uInfo = new JSONObject();
			uInfo.put("ACCOUNT", userInfo.get("loginname"));
			uInfo.put("NAME", userInfo.get("name"));
			uInfo.put("TYPE", "2".equals(userInfo.get("usertype"))?"21":"11");
			uInfo.put("CARD_NO", "2".equals(userInfo.get("usertype"))?userInfo.get("idcard"):userInfo.get("cardid"));
			uInfo.put("ORG_NAME", "2".equals(userInfo.get("usertype"))?userInfo.get("name"):userInfo.get("workunit"));
			uInfo.put("ORG_NO", "2".equals(userInfo.get("usertype"))?userInfo.get("orgnumber"):"");
			uInfo.put("PHONE", userInfo.get("mobile"));
			uInfo.put("CREDIT_CODE", "");
			uInfo.put("ICREGNUMBER", "2".equals(userInfo.get("usertype"))?userInfo.get("regnumber"):"");
			Command cmd = new Command("app.uc.GetUserMapDao");
    		cmd.setParameter("user_id_map", userInfo.getString("uuid"));
    		cmd.setParameter("uInfo", uInfo);
    		DataSet ds = cmd.execute("GetUid");
    		int ID;
    		if (ds.getState() == StateType.SUCCESS) {
    			ID = ds.getJAData().getJSONObject(0).getInt("USER_ID");
    			uInfo.put("ID", ID);
    		}
    		cmd = new Command("app.uc.LoginCmd");
    		cmd.setParameter("jo", uInfo);
    		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
    		ds = cmd.execute("setUcUserInfo");
    		return ds;
		} catch(Exception ex ){
			ex.printStackTrace();
			DataSet ds = new DataSet();
			ds.setState(StateType.FAILT);
			return ds;
		}
		
	}
}
