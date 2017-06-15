package action.sd;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.hanweb.sso.ldap.util.MD5;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;

import app.uc.GetUserMapDao;
import app.uc.UserDao;

public class bindUser extends BaseAction{

	private static final long serialVersionUID = 1L;
	
	public boolean handler(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		userBind();
		this.write(userBind().getData());
		return false;
	}
		
		public DataSet userBind(){
			String uid = this.getParameter("uuid");
			String OldAcccount = this.getParameter("loginname");
			String password = this.getParameter("password");
			String appword = SecurityConfig.getString("UserInfoappword");
			MD5 md5= new MD5();
			String Password=md5.decrypt(appword, password);
			String OldPassword = DigestUtils.md5Hex(Password);
		DataSet ds = new DataSet();
		try {
			if(StringUtils.isEmpty(OldAcccount)||StringUtils.isEmpty(OldPassword)){
				ds.setMessage("用户名或密码不可为空！");
				ds.setState(StateType.FAILT);
				ds.setData(null);
				return ds;
			}
			ds = UserDao.getInstance().verify(OldAcccount);
			if (ds.getTotal() > 0) {
				if (!OldPassword.equals(ds.getRecord(0).getString("PASSWORD"))) {
					ds.setMessage("用户名或密码错误，请重试！");
					ds.setState(StateType.FAILT);
					ds.setData(null);
				}
			} else {
				ds.setMessage("用户名或密码错误，请重试！");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		if(ds.getState()==StateType.SUCCESS){
			GetUserMapDao um = GetUserMapDao.getInstance();
			return um.userBind(uid, ds.getRecord(0).getString("ID"));
		} else
		{
			return ds;
		}
	}
}
