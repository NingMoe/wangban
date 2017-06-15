package action.icity;

import java.util.Map;

import com.inspur.base.BaseAction;

public class hotAd extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	public boolean handler(Map<String,Object> data){
		this.setContentType("text/javascript");
		return true;
	}
}
