package action.uc;

import java.util.Map;

import net.sf.json.JSONObject;

import app.uc.UserDao;

import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;

public class accmanager extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		DataSet dSetExt = UserDao.getInstance().getListExt("" + this.getUserInfo().getUid());
		JSONObject uInfo = new JSONObject();
		if (dSetExt.getTotal() > 0) {
			uInfo = dSetExt.getRecord(0);
		}
		data.put("userInfo", uInfo);
		//CacheManager.set(Constant.SESSIONID, this.getCookie(getSessionId()), uInfo);
		//this.write(uInfo.toString());
		return true;
	}
}
