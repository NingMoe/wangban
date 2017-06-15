package api;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hanweb.sso.ldap.util.MD5;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;

import app.uc.GetUserMapDao;
import app.uc.UserDao;

import net.sf.json.JSONObject;

@RestType(name = "api.userInterface", descript = "统一身份认证相关操作")
public class userInterface extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(businessInterface.class);
	/**
	 * 用户绑定
	 * @param pSet
	 * @return
	 */
	public JSONObject bindUser(ParameterSet pSet){
		String uuid = (String) pSet.getParameter("uuid");
		String OldAcccount = (String) pSet.getParameter("loginname");
		String password = (String) pSet.getParameter("password");
		
		String uid = GetUserMapDao.getInstance().GetUid(uuid).getRecord(0).getString("USER_ID");
		//Map<String, String> parMap = this.getPostData();
		JSONObject resultJson = new JSONObject();
		
		String appword = SecurityConfig.getString("UserInfoappword");
		String OldPassword = "";
		try{
			MD5 md5= new MD5();
			String Password=md5.decrypt(password, appword);
			OldPassword = DigestUtils.md5Hex(Password);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		DataSet ds = new DataSet();
		try {
			if(StringUtils.isEmpty(OldAcccount)||StringUtils.isEmpty(OldPassword)){
				resultJson.put("errormsg", "用户名或密码不可为空！");
				return resultJson;
			}
			ds = UserDao.getInstance().verify(OldAcccount);
			if (ds.getTotal() > 0) {
				if (!OldPassword.equals(ds.getRecord(0).getString("PASSWORD"))) {
					resultJson.put("errormsg", "用户名或密码错误，请重试！");
					return resultJson;
				}
			} else {
				resultJson.put("errormsg", "用户名或密码错误，请重试！");
				return resultJson;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.put("errormsg", "用户验证异常！");
			return resultJson;
		}
		if(ds.getState()==StateType.SUCCESS){
			DataSet dsBind = GetUserMapDao.getInstance().userBind(uid, ds.getRecord(0).getString("ID"));
			if(dsBind.getState()==StateType.FAILT){
				resultJson.put("errormsg", dsBind.getMessage());
				return resultJson;
			}
			resultJson.put("userid", ds.getRecord(0).getString("ID"));
			resultJson.put("loginname", ds.getRecord(0).getString("ACCOUNT"));
			resultJson.put("username", ds.getRecord(0).getString("NAME"));
			return resultJson;
		} else
		{
			resultJson.put("errormsg", "用户验证失败！");
			return resultJson;
		}
	}

}