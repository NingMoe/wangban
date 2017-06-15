package bl.action.app.uc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.uc.LoginDao;
import app.uc.UserDao;
import app.util.OAuthUtil;

import com.icore.http.adapter.HttpAdapter;
import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.Tools;
import com.inspur.util.URLEncode;

public class login extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	//private static Logger log = LoggerFactory.getLogger(BaseQueryCommand.class);
	
	//private final static String layout = "uc/oauthLayout.html";
	
	private enum ActionType {
		login, //登录action
		fail, //登录失败action
		loginBind, //登录绑定
		getuser,
		callback, //单点登录/省厅OAuth2认证登录 的回调action
		createBidingUser, // 省厅OAuth2认证 【立即创建】用户的回调action 目前只应用于省厅与深圳分厅的单点登录；各区与市分厅的单点登录未有此需求 
		guestLogin, //游客登录
		bindPovAcc,  //使用业务系统绑定省网厅账号
		bindBusinessAcc, //使用省网厅账号绑定业务系统账号
		clearForbidTime, //清除IP锁
		gdbsCallback  //省统一认证回调
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
		case loginBind:
			result = this.loginBind(data);
			break;
		case getuser:
			result = getuserAction(data);
			break;
		case callback:
			result = callbackAction(data);
			break;
		case gdbsCallback:
			result = gdbsCallbackAction(data);
			break;
		case createBidingUser:
			result = createBindUserAction(data);
			break;
		case guestLogin:
			result = guestLoginAction(data);
			break;
		case bindPovAcc:
			result = bindPovAccAction(data);
			break;
		case bindBusinessAcc:
			result = bindBusinessAccAction(data);
			break;
		case clearForbidTime:
			result = clearForbidTime(data);
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
		
		Command cmd = new Command("app.uc.LoginCmd");
		cmd.setParameter("type", postData.get("type"));
		cmd.setParameter("sessionId", this.getSessionId());
		cmd.setParameter("account", postData.get("account"));
		cmd.setParameter("pwd", postData.get("pwd"));
		cmd.setParameter("verify_code", postData.get("verify_code"));
		cmd.setParameter("remoteAddr", this.getRemoteAddr());
		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
		DataSet ds = cmd.execute("checkLogin");
		
		// 记录登录失败次数
		if (ds.getState() == StateType.FAILT) {
			Object fail_time = CacheManager.get("login_fail_time", this.getRemoteAddr());
			int login_fail_time = 1; // 登录失败次数默认为1
			if (fail_time != null) {
				login_fail_time = (Integer) fail_time + 1;
			}
			CacheManager.set("login_fail_time", this.getRemoteAddr(), login_fail_time);
			
			JSONObject rjo = new JSONObject();
			rjo.put("login_fail_time", login_fail_time);
			ds.setData(Tools.jsonToBytes(rjo));
		} else {
			CacheManager.evict("login_fail_time", this.getRemoteAddr());
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
	
	private boolean loginBind(Map<String, Object> data){
		Map<String, String> postData = this.getPostData();
		DataSet ui=UserDao.getInstance().verify(postData.get("ACCOUNT"));
		LoginDao.getInstance().setUcUserInfo(ui.getJOData(), getCookie(Constant.SESSIONID));
		return false;
	}
	
	private boolean getuserAction(Map<String, Object> data){
		DataSet ds = new DataSet();
		//网办的tokenid
		String tokenid = this.getParameter("tokenid");
//		UserInfo ui = new UserInfo();
		if (StringUtils.isEmpty(tokenid)) {
			ds.setState(StateType.FAILT);
			ds.setMessage("未获取到tokenid参数!");
			this.write(ds.toJson());
		} else {
			/**
			 * 获取用户信息只与网办系统有关（UserInfo）
			 * myToken--->sessionid--->ui--ui.getTickId--->CA
			 */
			UserInfo ui = (UserInfo)CacheManager.get(Constant.SESSIONID, (String)CacheManager.get(Constant.SESSIONID+"_token", tokenid));
			if(ui == null){
				ds.setState(StateType.FAILT);
				ds.setMessage("用户登录已失效！ ");
			}else{
				ds.setState(StateType.SUCCESS);
				ds.setData(Tools.jsonToBytes(JSONObject.fromObject(ui)));
			}
			this.write(ds.toJson());
		}
		return false;
	}
	
	public boolean callbackAction(Map<String, Object> data) {
		// TODO Auto-generated method stub
		String tokenid = this.getParameter("tokenid");
		String random = this.getParameter("random");
		String gotoUrl = this.getParameter("goto");
		
		String OAuth_code = this.getParameter("code");
		
		if (StringUtils.isNotEmpty(gotoUrl)) {
			gotoUrl = URLEncode.decodeURL(gotoUrl);
		} else {
			gotoUrl = this.getPageContext().getWebSite() + "public/index";
		}
		
		JSONObject obj;
		//String userIdCode = "";
		try {
			//如果获取到OAuth_code，为统一认证，采用oauth2方法，参数：code；否则，为单点登录，参数：tokenId,random
			if (StringUtils.isNotEmpty(OAuth_code)) {
				//提供给OAuth跳转的网办地址
				String redirectUri = this.getPageContext().getWebSite() + "app/uc/login?action=callback";
				DataSet result = OAuthUtil.getOAuthUserCodeId(redirectUri, OAuth_code);
				if (result.getState() == StateType.SUCCESS) {
					obj = result.getJOData();
					
					//userIdCode = obj.getString("useridcode");
				} else {
					throw new Exception("OAuthUtil error:" + result.getMessage());
				}
			} else {
				String uu = OAuthUtil.getAllUserAttributesJSON(tokenid, random,gotoUrl);
				this.sendRedirect(uu);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.sendRedirect(this.getContextPath() + "/uc/login?callbackMessage=" + URLEncode.encodeURL("用户验证不通过！") + "&goto=" + URLEncode.encodeURL(gotoUrl));
			return false;
		}
		
		//将省网厅用户数据结构转为分厅的结构
		ParameterSet pset = this.initUserPset(obj);
		
		//用户验证通过后,在服务器缓冲中添加一个值用于区分 省网厅账号认证
		String randomString = Tools.getUUID32();
		CacheManager.set("oauth",this.getCookie(Constant.SESSIONID), randomString);
		CacheManager.set("oauth",randomString,  "true");
		this.setBindingUser(pset);
		
		obj.put("goto",gotoUrl); 
		obj.put("tokenid",tokenid);
//		obj.put("useridcode", userIdCode);
//		obj.put("uversion", obj.get("uversion"));
//		obj.put("origin", obj.get("origin"));
//		return hasCAUser(obj,data);
		
		//将用户信息保存进session
		authCookie(obj);
		return doLogin(obj);
	}
	
	/**
	 * 登录操作
	 * 生成登录状态，并跳转页面
	 * @param obj
	 * @return
	 */
	protected boolean doLogin(JSONObject obj){
		//DataSet ds = this.checkLoginSuccess(obj);
		String nextGo = obj.getString("goto");
		nextGo = HttpUtil.formatUrl(nextGo);
		this.sendRedirect(nextGo);
		return false;
	}
	
	protected DataSet checkLoginSuccess(JSONObject obj){
		Command cmd = new Command("app.uc.LoginCmd");
		cmd.setParameter("jo", obj);
		cmd.setParameter("type", "YH");
		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
		DataSet ds = cmd.execute("checkLoginSuccess");
		return ds;
	}
	
	/**
	 * 将省网厅用户数据结构转为深圳分厅的结构
	 * @param obj
	 * @return
	 */
	protected ParameterSet initUserPset(JSONObject obj){
		ParameterSet pset = new ParameterSet();
		pset.put("ACCOUNT", obj.get("useridcode"));
		pset.put("PASSWORD", Tools.getMD5(obj.get("useridcode").toString()));
		String userType = this.changeUserType(obj.getString("usertype"));
		pset.put("TYPE", userType);
		pset.put("ADDRESS", obj.get("address"));
		pset.put("PHONE", obj.get("telephonenumber"));
		pset.put("USERIDCODE", obj.get("useridcode"));
		pset.put("UVERSION", obj.get("uversion"));
		pset.put("EMAIL", "-1".equals(obj.optString("mail")) ? "": obj.optString("mail"));
		pset.put("ORIGIN", obj.get("origin"));
		pset.put("IS_REAL",obj.get("isreal"));
		pset.put("tokenId", obj.get("tokenid")); //添加Oauth2认证用户 tokenId
		
		if("11".equals(userType)){
			//个人用户
			pset.put("NAME", obj.get("cn"));
			pset.put("CARD_TYPE", this.changeCardType(obj.getString("idcardtype")));
			pset.put("CARD_NO", obj.get("idcardnumber"));
			
		}else if("21".equals(userType)){
			pset.put("ORG_TYPE", obj.get("idcardtype"));
			pset.put("ORG_NO", obj.get("idcardnumber"));
			pset.put("ORG_NAME", obj.get("cn"));
			pset.put("NAME", obj.get("link_person_name")); 
			pset.put("CARD_TYPE", obj.getString("link_person_type"));
			pset.put("CARD_NO", obj.getString("link_person_code"));
			
			pset.put("ORG_BOSS_TYPE", obj.optString("legal_id_type"));
			pset.put("ORG_BOSS_NO", obj.optString("legal_code"));
			pset.put("ORG_BOSS_NAME", obj.optString("legal_person"));
//			pset.put("EMAIL", "null");
		}
		pset.put("STATUS", "1");
		return pset;
	}
	
	private String changeUserType(String userType){
		String result = "";
		if("1".equals(userType)){
			result = "11";
		}else if("2".equals(userType) || "3".equals(userType)){
			result = "21";
		}
		return result;
	}
	
	protected void setBindingUser(ParameterSet pset){
		String useridcode = (String)pset.getParameter("USERIDCODE");
		String sessionId = getCookie(Constant.SESSIONID);
		CacheManager.set("bindingUser", sessionId + "_" + useridcode, pset);
	}
	
	protected boolean hasCAUser(JSONObject obj,Map<String, Object> data){
		
		String nextGo = obj.getString("goto");
		String useridcode = obj.getString("useridcode");
		
		//判断是否绑定
		boolean isBinding = false;
		try {
			isBinding = this.isBinding(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.put("oauth","true");
		
		if(isBinding){
			//如果绑定
			//判断版本号
			String localUVersion = obj.optString("UVERSION");
			ParameterSet pset = this.getBindingUser(useridcode);
			String remoteUVersion = (String)pset.getParameter("UVERSION");
			if(StringUtils.isNotEmpty(localUVersion) && StringUtils.isNotEmpty(remoteUVersion) && localUVersion.equals(remoteUVersion)){
				return this.doLogin(obj); //版本号一致，直接登录
			}else{
				JSONObject remoteObj = new JSONObject();
				remoteObj.putAll(pset);
				return this.gotoComparePage(obj, remoteObj, data);
			}
			
		}else{
			data.put("dialogType", "not_binding_tips");
			data.put("nextGo", nextGo);
			data.put("useridcode", useridcode);
			return this.gotoLoginPage(data);
		}
	}
	
//	protected String hasCAUserReturningString(JSONObject obj) {
//		
//		String nextGo = "";
//		String gotoUrl = obj.getString("goto");
//		String tokenid = obj.getString("tokenid");
//		boolean isBinding = false;
//		try {
//			isBinding = this.isBinding(obj);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if(isBinding){
//			DataSet ds = this.checkLoginSuccess(obj);
//			nextGo = gotoUrl;
//		}else{
//			nextGo = this.noUserProcedure(obj);
//		}
//		
//		return HttpUtil.formatUrl(nextGo) ;
//	}

	/**
	 * 判断在分厅中是否有绑定深圳 用户
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	protected boolean isBinding(JSONObject obj) throws Exception{
		DataSet ds = this.getUserByUserIdCode(obj);
		if(ds.getState() == StateType.FAILT){
			throw new Exception(ds.getMessage());
		}
		
		if(ds.getTotal()>0){
			List<HashMap> list = (List<HashMap>)ds.getRawData();
			obj.putAll(list.get(0));
			return true;
		}
		return false;
	}
	
	/**
	 * 根据useridcode获取用户数据
	 * 如果有数据，表示已经绑定用户，否则未绑定
	 * @param obj
	 * @return
	 */
	protected DataSet getUserByUserIdCode(JSONObject obj){
		String uid = obj.getString("useridcode");
		Command cmd = new Command("app.uc.UserCmd");
		cmd.setParameter("USERIDCODE", uid);
		DataSet ds = cmd.execute("getUserByUserIdCode");
		return ds;
	}
	
	/**
	 * 转换证件类型
	 * 省厅的【其他】值为40
	 * 其他证件类型与标准相同
	 * @param cardType
	 * @return
	 */
	private String changeCardType(String cardType){
		String result = "";
		if("40".equals(cardType)){
			result = "90";
		}else{
			result = cardType;
		}
		return result;
	}
	
	protected ParameterSet getBindingUser(String useridcode){
		String sessionId = getCookie(Constant.SESSIONID);
		return (ParameterSet)CacheManager.get("bindingUser", sessionId + "_" + useridcode);
	}
	
	/**
	 * 跳转到用户信息比对页面，满足跳转条件(之一)：
	 * 1、第一次登陆分厅账户实现绑定；
	 * 2、以省网厅账号登录时，省网厅账号版本号（UVERSION）与本地版本不一致；
	 * 3、单点登录时，省网厅账号版本号（UVERSION）与本地版本不一致；
	 * @param localUserObj
	 * @param oauthUserObj
	 * @param data
	 * @return
	 */
	private boolean gotoComparePage(JSONObject localUserObj,JSONObject oauthUserObj,Map<String, Object> data){
		data.put("oauthUser", oauthUserObj);
		data.put("localUser", localUserObj);
		data.put("dialogType", "compare");
		String nextGo = localUserObj.getString("goto");
		data.put("nextGo", nextGo);
		data.put("useridcode", localUserObj.getString("useridcode"));
		CacheManager.set(this.getCookie(Constant.SESSIONID), "compareUser", localUserObj);
		return this.gotoLoginPage(data);
	}
	
	private boolean gotoLoginPage(Map<String, Object> data){
		String primeInfoHtml = "/uc/uniformLogin.html";
		this.getPageContext().setHtmlName(primeInfoHtml);
		//this.getHttpAdapter().setVm(this.layout);
		return true;
	}
	
//	protected String noUserProcedure(JSONObject obj){
//		String useridcode = obj.getString("useridcode");
//		String gotoUrl = obj.getString("goto");
//		
//		ParameterSet pset = this.getBindingUser(useridcode);
//		DataSet ds = this.getUserByPrimeInfo(pset);
//		
//		return this.getPageContext().getWebSite() + "/uc/login?uuid="+useridcode+"&goto="+ URLEncode.encodeURL(gotoUrl);
//	}
	
//	protected DataSet getUserByPrimeInfo(ParameterSet pset){
//		Command cmd = new Command("app.uc.UserCmd");
//		Iterator<String> it = pset.keySet().iterator();
//		while(it.hasNext()){
//			String key = it.next();
//			cmd.setParameter(key, pset.get(key));
//		}
//		DataSet ds = cmd.execute("getUserByPrimeInfo");
//		return ds;
//	}
	
	public boolean gdbsCallbackAction(Map<String, Object> data) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 触发条件：以省网厅账号登录成功 + 弹出框询问是否在深圳分厅有绑定用户时点击【否】
	 * 步骤：
	 * 1、通过接口获取账号信息
	 * 2、调用UserDao.register创建本地账号（内含同步数据到北京CA，如果符合条件）
	 * 3、重新调用 hasCAUser 方法
	 * @param data
	 * @return
	 */
	public boolean createBindUserAction(Map<String,Object> data){
		JSONObject returnObj = new JSONObject();
		String uuid = this.getPostData().get("uuid");
		ParameterSet userPset = this.getBindingUser(uuid);
		userPset.setSessionId(this.getCookie(Constant.SESSION_ID));
		DataSet ds = this.doRegister(userPset);
		if(ds.getState() == StateType.SUCCESS){
			JSONObject obj = new JSONObject();
			obj.put("useridcode", userPset.get("USERIDCODE"));
			obj.put("uversion", userPset.get("UVERSION"));
			obj.put("origin", userPset.get("ORIGIN"));
			obj.put("goto", this.getPostData().get("gotoUrl"));
			obj.put("tokenid", "");
			//String nextGo =  this.hasCAUserReturningString(obj);
			//returnObj.put("nextGo", nextGo);
			ds.setRawData(returnObj);
		}
		this.write(ds.toJson());
		return false;
	}
	
	/**
	 * 默认调用基础版本的注册方法，如果子版本有自己的注册方法，需覆盖此方法
	 * @param pset
	 * @return
	 */
	protected DataSet doRegister(ParameterSet pset){
		return UserDao.getInstance().register(pset);
	}
	
	private boolean guestLoginAction(Map<String, Object> data){
		//游客登录，使用游客账号
		Map<String, String> postData = this.getPostData();
		if("11".equals(postData.get("type"))){
			postData.put("account", "guest_p");
			postData.put("pwd", Tools.getMD5("12345678"));
		}else if("21".equals(postData.get("type"))){
			postData.put("account", "guest_c");
			postData.put("pwd", Tools.getMD5("12345678"));
		} 
		postData.put("type", "YH");
		return loginAction(data);
	}
	
	/**
	 * 业务系统绑定省网厅账号
	 */
	public boolean bindPovAccAction(Map<String, Object> data){
		//String nextGo = "";
		String tokenid = this.getParameter("tokenid");
		//String random = this.getParameter("random");
		//String gdbsAccessToken = this.getParameter("gdbsAccessToken");
		String gotoUrl = this.getParameter("goto");
		String OAuth_code = this.getParameter("code");
		if (StringUtils.isNotEmpty(gotoUrl)) {
			gotoUrl = URLEncode.decodeURL(gotoUrl);
		} else {
			gotoUrl = this.getPageContext().getWebSite() + "uc/index";
		}
		JSONObject obj = new JSONObject();
		//String userIdCode = "";
		try {
			//如果获取到OAuth_code，为统一认证，采用oauth2方法，参数：code；否则，为单点登录，参数：tokenId,random
			if (StringUtils.isNotEmpty(OAuth_code)) {
				//提供给OAuth跳转的网办地址
				String redirectUri = this.getPageContext().getWebSite() + "app/uc/login?action=bindPovAcc";
//				String redirectUri = "http://sxsl.sz.gov.cn/AppOAuthDemo/OAuthSvtClient";
				
				DataSet result = OAuthUtil.getOAuthUserCodeId(redirectUri, OAuth_code);
//				System.out.println(" >>>>>>>>> callback result >> " + result.toJson());
				if (result.getState() == StateType.SUCCESS) {
					obj = result.getJOData();
					//userIdCode = obj.getString("useridcode");
				} else {
					this.sendRedirect(this.getContextPath() + "/uc/login?callbackMessage=" + URLEncode.encodeURL("用户验证不通过！") + "&goto=" + URLEncode.encodeURL(gotoUrl));
					throw new Exception("OAuthUtil error:" + result.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.sendRedirect(this.getContextPath() + "/uc/login?callbackMessage=" + URLEncode.encodeURL("用户验证不通过！") + "&goto=" + URLEncode.encodeURL(gotoUrl));
			return false;
		}
		ParameterSet pset = this.initUserPset(obj);
		this.setBindingUser(pset);
		System.out.println(">>>>> set cache psetUser = " + pset);
		
		obj.put("goto",gotoUrl); 
		obj.put("tokenid",tokenid);
		return hasCAUserBindPov(obj,data);
	}
	
	/**
	 * 使用省网厅账号绑定业务系统账号
	 * @param data
	 * @return
	 */
	public boolean bindBusinessAccAction(Map<String, Object> data) {
		//String nextGo = "";
		String tokenid = this.getParameter("tokenid");
		//String random = this.getParameter("random");
		//String gdbsAccessToken = this.getParameter("gdbsAccessToken");
		String gotoUrl = this.getParameter("goto");
		String OAuth_code = this.getParameter("code");
		if (StringUtils.isNotEmpty(gotoUrl)) {
			gotoUrl = URLEncode.decodeURL(gotoUrl);
		} else {
			gotoUrl = this.getPageContext().getWebSite() + "uc/index";
		}
		JSONObject obj = new JSONObject();
		//String userIdCode = "";
		try {
			//如果获取到OAuth_code，为统一认证，采用oauth2方法，参数：code；否则，为单点登录，参数：tokenId,random
			if (StringUtils.isNotEmpty(OAuth_code)) {
				//提供给OAuth跳转的网办地址
				String redirectUri = this.getPageContext().getWebSite() + "app/uc/login?action=bindBusinessAcc";
//				String redirectUri = "http://sxsl.sz.gov.cn/AppOAuthDemo/OAuthSvtClient";
				
				DataSet result = OAuthUtil.getOAuthUserCodeId(redirectUri, OAuth_code);
//				System.out.println(" >>>>>>>>> callback result >> " + result.toJson());
				if (result.getState() == StateType.SUCCESS) {
					obj = result.getJOData();
					//userIdCode = obj.getString("useridcode");
				} else {
					this.sendRedirect(this.getContextPath() + "/uc/login?callbackMessage=" + URLEncode.encodeURL("用户验证不通过！") + "&goto=" + URLEncode.encodeURL(gotoUrl));
					throw new Exception("OAuthUtil error:" + result.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.sendRedirect(this.getContextPath() + "/uc/login?callbackMessage=" + URLEncode.encodeURL("用户验证不通过！") + "&goto=" + URLEncode.encodeURL(gotoUrl));
			return false;
		}
		ParameterSet pset = this.initUserPset(obj);
		this.setBindingUser(pset);
		System.out.println(">>>>> set cache psetUser = " + pset);
		
		obj.put("goto",gotoUrl); 
		obj.put("tokenid",tokenid);
		return hasCAUserBindBusiness(obj,data);
	}
	
	protected boolean hasCAUserBindPov(JSONObject obj,Map<String, Object> data){
		//String nextGo = obj.getString("goto");
		String useridcode = obj.getString("useridcode");
		
		//判断是否绑定
		boolean isBinding = false;
		try {
			isBinding = this.isBinding(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(isBinding){
			//如果绑定
			ParameterSet pset = this.getBindingUser(useridcode);
			JSONObject remoteObj = new JSONObject();
			remoteObj.putAll(pset);
			return this.gotoBindPagePov(obj, remoteObj, data);
		}else{
			//没有绑定则直接与当前登陆用户绑定
			UserInfo userinfo = this.getUserInfo();
			String localuid = String.valueOf(userinfo.getUid());
			Command cmd = new Command("app.uc.UserCmd");
			cmd.setParameter("ID", localuid);
			DataSet ds = cmd.execute("getUserInfoById");
			List<HashMap> list = (List<HashMap>)ds.getRawData();
			obj.putAll(list.get(0));
			ParameterSet pset = this.getBindingUser(useridcode);
			JSONObject remoteObj = new JSONObject();
			remoteObj.putAll(pset);
			return this.gotoComparePage(obj, remoteObj, data);
		}
	}
	
	private boolean gotoBindPagePov(JSONObject localUserObj,JSONObject oauthUserObj,Map<String, Object> data) {
		data.put("oauthUser", oauthUserObj);
		data.put("localUser", localUserObj);
		data.put("dialogType", "hasbindPovAcc");
		String nextGo = localUserObj.getString("goto");
		data.put("nextGo", nextGo);
		data.put("useridcode", localUserObj.getString("useridcode"));
		CacheManager.set(this.getCookie(Constant.SESSIONID), "compareUser", localUserObj);
		return this.gotoLoginPage(data);
	}
	
	private boolean hasCAUserBindBusiness(JSONObject obj,Map<String, Object> data) {
		//String nextGo = obj.getString("goto");
		String useridcode = obj.getString("useridcode");
		ParameterSet pset = this.getBindingUser(useridcode);
		JSONObject remoteObj = new JSONObject();
		remoteObj.putAll(pset);
		return this.gotoBindPageBusiness(obj, remoteObj, data);
	}
	
	private boolean clearForbidTime(Map<String, Object> data){
		String account = this.getParameter("account");
		CacheManager.evict("forbid_login_time", account);
		this.write("账号解锁成功！");
		return false;
	}
	
	private boolean gotoBindPageBusiness(JSONObject localUserObj, JSONObject oauthUserObj,Map<String, Object> data) {
		data.put("oauthUser", oauthUserObj);
		data.put("localUser", localUserObj);
		data.put("dialogType", "bindBusinessAcc");
		String nextGo = localUserObj.getString("goto");
		data.put("nextGo", nextGo);
		data.put("useridcode", localUserObj.getString("useridcode"));
		CacheManager.set(this.getCookie(Constant.SESSIONID), "compareUser", localUserObj);
		return this.gotoLoginPage(data);
	}
	
	public void authCookie(JSONObject obj) {
		net.sf.json.JSONObject uInfo = new net.sf.json.JSONObject();
		
		uInfo.put("ACCOUNT", obj.get("uid"));
		uInfo.put("PASSWORD", Tools.getMD5(obj.get("useridcode").toString()));
		String userType = this.changeUserType(obj.getString("usertype"));
		uInfo.put("TYPE", userType);
		uInfo.put("ADDRESS", obj.get("address"));
		uInfo.put("PHONE", obj.get("telephonenumber"));
		uInfo.put("USERIDCODE", obj.get("useridcode"));
		uInfo.put("UVERSION", obj.get("uversion"));
		uInfo.put("EMAIL", "");//暂不取邮箱值 省厅返回-1
		uInfo.put("ORIGIN", obj.get("origin"));
		uInfo.put("IS_REAL",obj.get("isreal"));
		uInfo.put("tokenId", obj.get("tokenid")); //添加Oauth2认证用户 tokenId
		
		if("11".equals(userType)){
			//个人用户
			uInfo.put("NAME", obj.get("cn"));
			uInfo.put("CARD_TYPE", this.changeCardType(obj.getString("idcardtype")));
			uInfo.put("CARD_NO", obj.get("idcardnumber"));
			
		}else if("21".equals(userType)){
			uInfo.put("ORG_TYPE", obj.get("idcardtype"));
			uInfo.put("ORG_NO", obj.get("idcardnumber"));
			uInfo.put("ORG_NAME", obj.get("cn"));
			uInfo.put("NAME", obj.get("link_person_name")); 
			uInfo.put("CARD_TYPE", obj.getString("link_person_type"));
			uInfo.put("CARD_NO", obj.getString("link_person_code"));
			
			uInfo.put("ORG_BOSS_TYPE", obj.optString("legal_id_type"));
			uInfo.put("ORG_BOSS_NO", obj.optString("legal_code"));
			uInfo.put("ORG_BOSS_NAME", obj.optString("legal_person"));
//			uInfo.put("EMAIL", "null");
		}
		uInfo.put("STATUS", "1");
		
		Command cmd = new Command("app.uc.GetUserMapDao");
		cmd.setParameter("user_id_map", obj.getString("useridcode"));
		cmd.setParameter("uInfo", uInfo);
		DataSet ds = cmd.execute("GetUid4Bl");
		
		if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
			String ID = ds.getJAData().getJSONObject(0).getString("USER_ID");
			uInfo.put("ID", ID);
			cmd = new Command("app.uc.LoginCmd");
			
			cmd.setParameter("jo", uInfo);
			cmd.setParameter("cookie", getCookie(Constant.SESSIONID));
			cmd.execute("setUcUserInfo");
			
			if (ds.getState() == StateType.FAILT) {
				// 为防止重定向，死循环，出错直接返回首页
				this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
			} 
		} else {
			// 为防止重定向，死循环，出错直接返回首页
			this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
		}
	}
}
