package action.bsp;

import java.util.List;
import java.util.Map;

import com.inspur.base.BaseAction;
import com.inspur.bean.rest.RestInfo;
import com.inspur.bean.rest.RestMethod;
import com.inspur.core.RestFactory;
import com.inspur.util.DaoFactory;

public class api_detail extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	@Override
	public boolean handler(Map<String, Object> data) {
		// TODO 自动生成的方法存根
		String type = this.getParameter("type");
		String method = this.getParameter("method");
		RestInfo rInfo = RestFactory.getInstance().getRestInfo(DaoFactory.getClass(type));
		if(rInfo != null){
			data.put("type", rInfo.getType());
			List<RestMethod> methods = rInfo.getMethod();
			for(int i=0;i<methods.size();i++){
				if(methods.get(i).name().equals(method)){
					data.put("method", methods.get(i));
				}
			}
		}
		
		return true;
	}

}
