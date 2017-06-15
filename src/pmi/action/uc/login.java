package action.uc;

import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.inspur.base.BaseAction;
import com.inspur.bean.UserInfo;
import com.inspur.http.util.UrlHelper;

public class login extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		UserInfo ui = this.getUserInfo();
		try {
			if (ui != null) {
				String gotoUrl = this.getParameter("goto");
				if (StringUtils.isEmpty(gotoUrl)) {
					gotoUrl = UrlHelper.index(this.getHttpAdapter());
				}
				this.sendRedirect(gotoUrl);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
