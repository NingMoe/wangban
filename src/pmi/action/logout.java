package action;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.icore.util.URLEncode;
import com.inspur.base.BaseAction;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class logout extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}

	public boolean handler(Map<String, Object> data) {
		CacheManager.evict(Constant.SESSIONID,
				this.getCookie(Constant.SESSIONID));
		setCookie(Constant.SESSIONID, Tools.getUUID32());
		String nextgo = "";
		try {
			nextgo = URLDecoder.decode(this.getParameter("goto"), "UTF-8");
			if(isContainsChinese(nextgo)){
				String[] nestgos = nextgo.split("\\?");
				nextgo = nestgos[0] + "?" + URLEncode.encodeURL(nestgos[1]);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!StringUtils.isNotEmpty(nextgo)) {
			nextgo = this.getContextPath() + "/"
					+ SecurityConfig.getString("MainPage");
		}
		this.sendRedirect(nextgo);
		return false;
	}
	static String regEx = "[\u4e00-\u9fa5]";
	static Pattern pat = Pattern.compile(regEx);
	public static boolean isContainsChinese(String str) {
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find()) {
			flg = true;
		}
		return flg;
	}

}
