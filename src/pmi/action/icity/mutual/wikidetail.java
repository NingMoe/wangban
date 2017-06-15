package action.icity.mutual;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inspur.base.BaseAction;

public class wikidetail extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		String website = this.getPageContext().getWebSite();
		String url = this.getUrl();
		try {
			boolean hasspec = false;// 是否存在特殊字符
			String regex = "(&lt;|&gt;|%7C|%3C|%3E|script|iframe|<|>)+";// 特殊字符
			Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);

			String uri = this.getUri();
			if (uri.contains("?")) {
				String param = uri.substring(uri.indexOf("?") + 1, uri.length());
				Matcher matcher = pattern.matcher(param);
				if (matcher.find()) {
					hasspec = true;
					String replace = matcher.replaceAll("");
					url += "?" + replace;
				}
			}
			if (hasspec) {
				website += url;
				this.sendRedirect(website);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
