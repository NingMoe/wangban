package action.icity.engineering;

import java.util.Map;

import com.inspur.base.BaseAction;

public class detailInfo extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean handler(Map<String, Object> data) throws Exception {
		String type = this.getParameter("type");
		
		data.put("type", type);
		return true;
	}

}
