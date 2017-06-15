package action.icity.business;

import java.util.Map;

import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;

public class xmgl extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	private enum ActionType {
		gl
	};
 
	protected boolean IsVerify() {
		return false;
	}

	public boolean handler(Map<String, Object> data) {
		ActionType actionType = ActionType.valueOf(this.getParameter("action"));
		boolean result = false;
		
		switch (actionType) {
		case gl:
			result = this.glAction(data);
			break;
		default:
			break;
		}
		return result;
	}
	
	private boolean glAction(Map<String, Object> data){
		Map<String, String> postData = this.getPostData();
		
		String phone = postData.get("PHONE");
		String code = (String)CacheManager.get("MessageCode", phone);
		DataSet ds = new DataSet();
		
		if(code.equals(postData.get("VCODE").toUpperCase())){
			
			Command cmd = new Command("app.icity.pro.ProCmd");
			cmd.setParameter("data",postData.get("DATA"));
			ds = cmd.execute("insertIproIndex");
			
    		ds.setData(null);
		}

		this.write(ds.toJson());
		return false;
	}
}
