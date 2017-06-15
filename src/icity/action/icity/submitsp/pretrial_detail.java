package action.icity.submitsp;

import java.util.Map;
import com.inspur.base.BaseAction;
import com.inspur.util.SecurityConfig;

public class pretrial_detail extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){	
		data.put("NetDiskAddress", SecurityConfig.getString("NetDiskAddress").split("upload")[0]+"doc?doc_id=");
		return true;
	}
}
