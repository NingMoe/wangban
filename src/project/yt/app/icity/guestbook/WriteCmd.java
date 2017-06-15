package yt.app.icity.guestbook;

import yt.app.icity.guestbook.WriteDao;

import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.bean.UserInfo;


public class WriteCmd extends app.icity.guestbook.WriteCmd {
	
	//private static Logger log = LoggerFactory.getLogger(BaseQueryCommand.class);	
	
	
	public DataSet insert(ParameterSet pSet){
		DataSet ds = new DataSet();
		String verify = (String)CacheManager.get("VerifyCode",pSet.getSessionId());	
		String VerifyCode = (String)pSet.get("VerifyCode");
		if(VerifyCode != null && !"".equals(VerifyCode)){
			if(!VerifyCode.equalsIgnoreCase(verify)){
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误,请重新输入！");
				return ds;
			}
		}	
		
		UserInfo user = this.getUserInfo(pSet);
		if(null!=user){
			return WriteDao.getInstance().insert(pSet,user);
		}else{
			return WriteDao.getInstance().insert(pSet);
		}
	}
}

