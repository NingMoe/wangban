package action.app.uc;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import app.util.CommonUtils;

import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.URLEncode;
import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class user extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}

	public boolean handler(Map<String, Object> data) {
		String action = this.getParameter("action");
		if ("bind".equals(action)) {
			bind();
		} else if ("rebind".equals(action)) {
			rebind();
		} else if ("register".equals(action)) {
			register();
		} else if ("getPwdSec".equals(action)) {
			getPwdSec();
		} else if ("getPwdMethod".equals(action)) { 
			getPwdMethod();
		} else if ("resetPass".equals(action)) {
			resetPass();
		}else if("editPhone".equals(action)) {
			editPhone();
		}
		return false;
	}

	// 绑定邮箱
	public void bind() {
		String uid = this.getParameter("uid");
		Object obj = CacheManager.get("mailIndex", uid);
		if (obj != null) {
			JSONObject jo = (JSONObject) obj;
			String key = (String) this.getParameter("mailkey");
			String mailKey = jo.getString("key");
			if (mailKey != null && mailKey.equals(key)) {
				Command cmd = new Command("app.uc.UserCmd");
				cmd.setParameter("mailKey", mailKey);
				DataSet ds = cmd.execute("bindEmail");
				if (ds.getState() == StateType.SUCCESS) {
					CacheManager.evict("mailIndex", uid);
					this.sendRedirect(this.getContextPath() + "/uc/index?action=bind&result=1");
				}
			}
		}
		this.sendRedirect(this.getContextPath() + "/uc/index?action=bind&result=0");
	}

	// 重置密保邮箱
	public void rebind() {
		Map<String, String> post = this.getPostData();
		String uid = post.get("uid");
		Object obj = CacheManager.get("mailIndex", uid);
		if (obj == null) {
			this.sendRedirect(this.getContextPath() + "/src?m=uc/secBindMailW.html");
		} else {
			this.sendRedirect(this.getContextPath() + "/src?m=uc/reSendEmail.html");
		}
	}

	// 用户注册
	public void register() {
		DataSet result = new DataSet();
		ParameterSet pSet = this.transParam();
		String gotoUrl = (String)pSet.remove("goto");
		if (StringUtils.isNotEmpty(gotoUrl)) {
			gotoUrl = URLEncode.decodeURL(gotoUrl);
		} else {
			gotoUrl = UrlHelper.index(this.getHttpAdapter());
		}
		
		Command cmd = new Command("app.uc.UserCmd");
		this.transParam(cmd);
		DataSet ds = cmd.execute("register");
		if(ds.getState() == StateType.FAILT){
			result.setState(StateType.FAILT);
			result.setMessage(ds.getMessage());
		}else{
			//注册成功后，处于登录状态 ---by wq
			result.setState(StateType.SUCCESS);
			CommonUtils.getInstance().setUcUserInfo(ds.getJOData(), this.getCookie(Constant.SESSIONID));
				try {
					JSONObject obj = new JSONObject();
					obj.put("nextGo", gotoUrl);
					result.setData(obj.toString().getBytes("UTF-8"));
					result.setTotal(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		this.write(result.toJson());
	}

	// 校验用户名是否存在，同时将密保信息放到cache缓存中
	public void getPwdSec() {
		Command cmd = new Command("app.uc.UserCmd");
		this.transParam(cmd);
		DataSet ds = cmd.execute("getPwdSecInfo");
		CacheManager.set("icity_pwd_secinfo", this.getSessionId(), ds);
		this.write(ds.toJson());
	}

	// 获取密保信息
	public void getPwdMethod() {
		Object obj = CacheManager.get("icity_pwd_secinfo", this.getSessionId());
		if (obj != null) {
			DataSet ds = (DataSet) obj;
			this.write(ds.toJson());
		}
	}

	// 重置密保信息
	public void resetPass() {
		Command cmd = new Command("app.uc.UserCmd");
		this.transParam(cmd);
		DataSet ds = cmd.execute("resetPass");
		this.write(ds.toJson());
	}
	
	//修改手机号
	public void editPhone() {
		DataSet result = new DataSet();
		Command cmd = new Command("app.uc.UserCmd");
		this.transParam(cmd);
		UserInfo userInfo = this.getUserInfo();
		cmd.setParameter("USERID", "" + userInfo.getUid() );
		cmd.setParameter("PHONE",userInfo.getMobile());
		DataSet ds = cmd.execute("editPhone");
		if(ds.getState() == StateType.FAILT){
			result.setState(StateType.FAILT);
			result.setMessage(ds.getMessage());
		}else{
			result.setState(StateType.SUCCESS);
		}
		this.write(result.toJson());
	}

	private ParameterSet transParam() {
		ParameterSet pSet = new ParameterSet();
		try {
			Map<String, String> post = this.getPostData();
			Iterator<Entry<String, String>> entryKeyIterator = post.entrySet().iterator();  
		    while (entryKeyIterator.hasNext()) {  
		        Entry<String, String> e = entryKeyIterator.next();  
		        String value=e.getValue();  
		        String key=e.getKey();  
		        pSet.put(key, value);
		    }  
			pSet.setSessionId(this.getSessionId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pSet;
	}
	
	private void transParam(Command cmd) {
		try {
			Map<String, String> post = this.getPostData();
			Set<Map.Entry<String,String>> entry = post.entrySet();
			Iterator<Entry<String, String>> iterator = entry.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> it = iterator.next();
				String key = it.getKey();
				//注册时，将PASSWORD参数以PWD的形式进行传递
				if("PWD".equals(key)){
					key="PASSWORD";
				}
				String value = it.getValue();
				cmd.setParameter(key, value);
			}
			cmd.setSessionId(this.getSessionId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
