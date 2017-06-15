package km.app.icity.webservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.icore.util.DaoFactory;
import com.inspur.bean.ParameterSet;

import iop.org.json.JSONException;
import iop.org.json.JSONObject;
import km.app.icity.webservice.entity.Complaint;
import km.app.icity.webservice.interceptor.AddLoginMsgHeaderInterceptor;

/**
 * 
 * ClassName: WebServiceBase <br/>
 * date: 2015年10月14日 上午10:58:19 <br/>
 * 
 * @author lw
 */
public class WebServiceBase {
	protected static Log _log = LogFactory.getLog(WebServiceBase.class);

	public static WebServiceBase getInstance() {
		return DaoFactory.getDao(WebServiceBase.class.getName());
	}

	private static JaxWsProxyFactoryBean factory;
	// 接口地址
	private static String serviceUrl = "http://172.30.3.189";
	// private static String serviceUrl = "http://192.168.1.104:8081";
	// 用户名
	private static String username = "ynwsdt";
	// 密码
	private static String password = "ynwsdt";

	/**
	 * 
	 * commitTransactionInfo:(第三方网办申报接口推送)
	 * 
	 * @author lw
	 * @param data
	 */

	public int commitTransactionInfo(String data) {
		// 初始化连接
		init();
		IPubWebService pubWebService = (IPubWebService) factory.create();
		String message = pubWebService.commitTransactionInfo(data);
		try {
			JSONObject arr = new JSONObject(message);
			int i = arr.getInt("state");
			// _log.info(arr);
			return i;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * commitComplaint:(第三方网办投诉信息接口推送)
	 * 
	 * @author lw
	 * @param pSet
	 * @return
	 */
	public int commitComplaint(ParameterSet pSet) {
		// 初始化连接
		init();
		IPubWebService pubWebService = (IPubWebService) factory.create();
		// 数据转换
		Complaint comp = new Complaint();
		comp.setAddress((String) pSet.getParameter("ADDRESS"));
		comp.setContent((String) pSet.getParameter("CONTENT"));
		comp.setDepCode((String) pSet.getParameter("DEPART_ID")); // 部门组织机构代码
		comp.setDepId((String) pSet.getParameter("DEPART_ID"));
		comp.setEmail((String) pSet.getParameter("EMAIL"));
		// comp.setOrgCode(SecurityConfig.getString("WebRegion")); //区划编码
		comp.setPerson((String) pSet.getParameter("USERNAME"));
		comp.setPhone((String) pSet.getParameter("PHONE"));
		comp.setTheme((String) pSet.getParameter("TITLE"));
		comp.setWsdtCode((String) pSet.getParameter("id"));
		String message = (String) pubWebService.commitComplaint(comp);
		try {
			JSONObject arr = new JSONObject(message);
			int i = arr.getInt("state");
			// _log.info(arr);
			return i;
		} catch (JSONException e) {
			e.printStackTrace();

		}
		return 0;

	}

	/**
	 * 
	 * init:(初始化wsdl连接)
	 * 
	 * @author lw
	 */
	private void init() {

		// 创建客户端代理工厂
		factory = new JaxWsProxyFactoryBean();
		// 注册WebService接口
		factory.setServiceClass(IPubWebService.class);
		// 设置WebService地址
		// factory.setAddress(serviceUrl + "/xzspwebservice/pubweb?wsdl");//
		// 接口地址
		factory.setAddress(serviceUrl + "/xzspwebservice/pubweb");// 接口地址
		// 向请求头中添加用户登录信息
		factory.getOutInterceptors().add(new AddLoginMsgHeaderInterceptor(username, password));
		factory.getOutInterceptors().add(new LoggingOutInterceptor());

	}

}
