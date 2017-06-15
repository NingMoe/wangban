package app.uc;

import org.apache.commons.lang.StringUtils;

import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Constant;

import net.sf.json.JSONObject;

public class LoginCmd extends BaseQueryCommand {
	
	public DataSet checkLogin(ParameterSet pSet){
		return LoginDao.getInstance().checkLogin(pSet);
	}
	
	public DataSet setUcUserInfo(ParameterSet pSet){
		return LoginDao.getInstance().setUcUserInfo((JSONObject)pSet.getParameter("jo"),(String)pSet.getParameter("cookie"));
	}
	
	public DataSet userBind(ParameterSet pSet){
		String uid = ""+this.getUserInfo(pSet).getUid();
		String OldAcccount = (String) pSet.getParameter("account");
		String OldPassword = (String) pSet.getParameter("pwd");
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
