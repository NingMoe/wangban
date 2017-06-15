package km.app.icity.webservice.interceptor;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 报头添加用户及密码 
 * ClassName: AddLoginMsgHeaderInterceptor <br/>
 * date: 2015年10月14日 上午10:57:56 <br/>
 * 
 * @author lw
 */
public class AddLoginMsgHeaderInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
	// 验证用户名
	private String username;
	// 验证用户密码
	private String pwd;

	public AddLoginMsgHeaderInterceptor(String username, String pwd) {
		super(Phase.PREPARE_SEND); // 在发送前拦截
		this.username = username;
		this.pwd = pwd;
	}

	@Override
	public void handleMessage(SoapMessage msg) throws Fault {
		// System.out.println("==============>>>进入客户端拦截器<<<=============");
		List<Header> headers = msg.getHeaders();
		// System.out.println("===========" + headers);
		Document doc = DOMUtils.createDocument();
		Element hele = doc.createElement("authHeader");
		Element uele = doc.createElement("username");
		uele.setTextContent(username);
		Element pele = doc.createElement("password");
		pele.setTextContent(pwd);
		hele.appendChild(uele);
		hele.appendChild(pele);
		headers.add(new Header(new QName("tom"), hele));
		// System.out.println("===========" + headers);
		// System.out.println("==============>>>客户端拦截器执行完成<<<=============");
	}
}
