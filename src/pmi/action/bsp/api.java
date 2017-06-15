package action.bsp;


import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.inspur.base.BaseAction;
import com.inspur.bean.rest.RestInfo;
import com.inspur.core.RestFactory;

public class api extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactory.getLog(api.class);
	protected boolean IsVerify() {
		return false;
	}
	@Override
	public boolean handler(Map<String, Object> data) {
		// TODO 自动生成的方法存根
		
		List<RestInfo> list = null;
		try {
			list = RestFactory.getInstance().getRestInfo("api");
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			_log.error(e);
		}catch(Exception ex1){
			_log.error(ex1);
		}
		data.put("apiList",list);
		return true;
	}

}
