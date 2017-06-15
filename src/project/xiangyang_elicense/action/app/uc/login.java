package xiangyang_elicense.action.app.uc;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import app.uc.LoginDao;
import app.uc.UserDao;
import app.util.RestUtil;
import core.util.HttpClientUtil;
import iop.util.URLEncodeUtils;

import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.URLEncode;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tokenid = "";
	private String random = "";
	
	private enum ActionType {
		login, //登录action
		fail, //登录失败action
	};

	public boolean handler(Map<String, Object> data) {
		ActionType actionType = ActionType.valueOf(this.getParameter("action"));
		boolean result = false;
		
		switch (actionType) {
		case login:
			result = this.loginAction(data);
			break;
		case fail:
			result = this.failAction(data);
			break;
		default:
			break;
		}
		return result;
	}
	
	private boolean loginAction(Map<String, Object> data){
		/**
		 * 登录验证
		 */
		Map<String, String> postData = this.getPostData();
		DataSet ds = new DataSet();
		try{
			String account = postData.get("account");
			String pwd = postData.get("password");
			String url = SecurityConfig.getString("login_xy_elicense_url");
			String url_tokenid = url + SecurityConfig.getString("login_xy_elicense_tokenid") + account + "&password=" + pwd;		
			HttpClientUtil client = new HttpClientUtil();
			JSONObject itemTokenid = JSONObject.fromObject(client.getResult(url_tokenid, ""));
			if("0".equals(itemTokenid.get("errorCode"))){
				String tokenId = (String) itemTokenid.get("data");
				String url_random = url + SecurityConfig.getString("login_xy_elicense_random") + tokenId;
				JSONObject itemRandom = JSONObject.fromObject(client.getResult(url_random, ""));
				if("0".equals(itemRandom.get("errorCode"))){
					String random = (String) itemRandom.get("data");
					String url_user = url + SecurityConfig.getString("login_xy_elicense_user") + tokenId + "&random=" + random;
					JSONObject itemUser = JSONObject.fromObject(client.getResult(url_user, ""));
					if("0".equals(itemUser.get("errorCode"))){
						JSONObject  userInfo = itemUser.getJSONObject("data");//用户信息
						JSONArray  credenceList = userInfo.getJSONArray("credenceList");
						JSONObject userInfo_loginName=credenceList.getJSONObject(0);
						String ACCOUNT = (String) userInfo_loginName.get("loginName"); //登录名不变
						String PHONE = (String) userInfo.get("userPhone");  //手机
						String CARD_NO = (String) userInfo.get("userIdcardNum");//身份证号（个人）
						String userIdCode = (String) userInfo.get("userIdCode");//用户id
						String Name = (String) userInfo.get("userName");//个人时真实姓名，企业时公司名称
						String unitName = (String) userInfo.get("unitName");//企业时法人姓名
						String TYPE =userInfo.get("userType").toString(); //用户类型 1个人2企业 
						JSONObject jo = new JSONObject();
						if("2".equals(TYPE)){ //企业时获取法人身份证号
							JSONArray  userAttrList = userInfo.getJSONArray("userAttrList");
							JSONObject userInfo_replenish=userAttrList.getJSONObject(4);
							JSONArray  valueList = userInfo_replenish.getJSONArray("valueList");
							JSONObject userInfo_frIDcard = valueList.getJSONObject(0);	
							String avCode = (String) userInfo_frIDcard.get("avCode");//法人身份证号（企业）
							jo.put("NAME", unitName == null ? "" : unitName);
							jo.put("CARD_NO", avCode == null ? "" : avCode);
							jo.put("ORG_NAME", Name == null ? "" : Name);
						}else{
							jo.put("NAME", Name == null ? "" : Name);
							jo.put("CARD_NO", CARD_NO == null ? "" : CARD_NO);
						}
						jo.put("TYPE", "2".equals(TYPE) ? "21" : "11");
						jo.put("ACCOUNT", ACCOUNT == null ? "" : ACCOUNT);
						jo.put("PHONE", PHONE == null ? "" : PHONE);	
						Command cmd = new Command("app.uc.GetUserMapDao");
						cmd.setParameter("user_id_map", userIdCode);
						ds = cmd.execute("GetUid");
						if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
							String ID = ds.getJAData().getJSONObject(0).getString("USER_ID");
							cmd = new Command("app.uc.LoginCmd");
							jo.put("ID", ID == null ? "" : ID);
							cmd.setParameter("jo", jo);
							cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
							 ds = cmd.execute("setUcUserInfo");
							if (ds.getState() == StateType.FAILT) {
								System.out.println("setUcUserInfo失败");
							} 
						} else {
							System.out.println("获取ID失败");
						}
						String gotoUrl = postData.get("goto");
						if (StringUtils.isNotEmpty(gotoUrl)) {
							gotoUrl = URLEncode.decodeURL(gotoUrl);
						} else {
							gotoUrl = UrlHelper.index(this.getHttpAdapter());;
						}
						JSONObject rjo = new JSONObject();
						rjo.put("nextGo", HttpUtil.formatUrl(gotoUrl));
						rjo.put("tickId", this.getUserInfo().getTickId());
						ds.setData(Tools.jsonToBytes(rjo));
					}else{
						ds.setState(StateType.FAILT);
						ds.setMessage((String) itemUser.get("errorMsg"));
					}	
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage((String) itemRandom.get("errorMsg"));
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage((String) itemTokenid.get("errorMsg"));
			}	
		}catch(RuntimeException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.write(ds.toJson());
		return false;
	}

	private boolean failAction(Map<String, Object> data){
		DataSet ds = new DataSet();
		JSONObject jo = new JSONObject();
		Object fail_time = CacheManager.get("login_fail_time", this.getRemoteAddr());
		if (fail_time == null) {
			jo.put("login_fail_time", 0);
		} else {
			jo.put("login_fail_time", (Integer) fail_time + 1);
		}
		ds.setData(Tools.jsonToBytes(jo));
		this.write(ds.toJson());
		return false;
	}
}
