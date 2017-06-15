package action;

import java.io.IOException;
import java.util.Map;

import net.asfun.jangod.base.ResourceManager;

import org.apache.commons.lang.StringUtils;

import com.icore.http.HttpResponseStatus;
import com.icore.http.adapter.HttpAdapter;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import com.inspur.util.DaoFactory;

public class ipmi extends BaseAction {
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) throws Exception {
		HttpAdapter ha = this.getHttpAdapter();
		String referer = ha.getHeader("referer");
		String host = ha.getHeader("Host");
		boolean flag = false;
		if (host.contains("localhost")) {
			flag = true;
		} else {
			if (StringUtils.isNotBlank(referer)) {
				String tmp = "";
				String accessWhiteList = SecurityConfig.getString(
						"accessWhiteList");
				for (String li : accessWhiteList.split(",")) {
					tmp = referer.substring(7, li.length() + 7);
					if (tmp.equals(li)) {
						flag = true;
						break;
					}
				}
			}
		}
		if (!flag) {
			this.write("禁止访问");
			return false;
		}
		String m = this.getParameter("m");
		if (StringUtils.isNotEmpty(m)) {
			if (m.lastIndexOf(".htm") > -1) {
				String curFileName = m.substring(0, m.lastIndexOf(".htm"));
				String className = curFileName.replace("/", ".");
				if (className.indexOf(".") == 0) {
					className = className.substring(1);
				}
				this.getPageContext().setHtmlName(m);
				// **.js是否存在（可以将此信息加入缓存，减少每次判断）
				try {
					String curFileJs = curFileName + ".js";
					ResourceManager.getPagesName(curFileJs);
					this.getPageContext().setJsName(curFileJs);
				} catch (IOException jsIO) {
				}
				BaseAction ba = (BaseAction) DaoFactory.getDao("action."
						+ className);
				if (ba != null) {
					ba.setPageContext(this.getPageContext());
					if (!ba.init(this.getHttpAdapter()) || !ba.handler(data)) {
						return false;
					}
				}
			}
		} else {
			this.getHttpAdapter().setStatus(HttpResponseStatus.NOT_FOUND);
			return false;
		}
		return true;
	}
}
