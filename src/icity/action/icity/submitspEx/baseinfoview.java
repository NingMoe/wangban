package action.icity.submitspEx;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.base.BaseAction;

public class baseinfoview extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactory.getLog(baseinfoview.class);
	public boolean handler(Map<String,Object> data){
		String itemCode = this.getParameter("itemCode");
		String sblsh = this.getParameter("sblsh");
		String rowguid = this.getParameter("rowguid");
		String domain = this.getParameter("domain");
		String userType = this.getParameter("userType");
		if("gr".equals(userType)){
			userType="1";
		}else {
			userType="2";
		}
		String gotoUrl = domain+"icity/submitsp/baseinfoView?itemCode="+itemCode+"&sblsh="+sblsh+"&rowguid="+rowguid+"&userType="+userType;
		gotoUrl = URLEncoder.encode(gotoUrl);
		
//		System.out.println("输出全部url开始");
//	    System.out.println("uri：" + getUri() + "url:" + getUrl());
//	    System.out.println("输出全部url结束");
//	    System.out.println("访问URL" + getPageContext().getWebSite());
	    //System.out.println("===gotoUrl:"+url);
		
		String ticket = this.getParameter("ticket");
		String url="/sd/login?userType="+userType+"&ticket="+ticket+"&gotoUrl=" + gotoUrl;
		System.out.println("========办件详细页重定向："+url);
		this.sendRedirect(url);
		return false;
	}
	
}
