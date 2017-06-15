package km.app.icity.webservice.testweb;

import km.app.icity.webservice.IPubWebService;
import km.app.icity.webservice.entity.Complaint;
import km.app.icity.webservice.entity.ComplaintsSupervisio;
import km.app.icity.webservice.interceptor.AddLoginMsgHeaderInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * 
 * ClassName: SuperviseTest <br/>
 * date: 2015年10月14日 上午10:58:09 <br/>
 * @author lw
 */
public class SuperviseTest {
    private JaxWsProxyFactoryBean factory;
    private static String serviceUrl = "http://220.163.118.112/ws";//接口地址
    private static String username = "ynwsdt";//用户名
    private static String password = "ynwsdt";//密码

    @org.junit.Test
    public void testCommint(){
        //创建客户端代理工厂
        factory = new JaxWsProxyFactoryBean();
        //注册WebService接口
        factory.setServiceClass(IPubWebService.class);
        //设置WebService地址
        factory.setAddress(serviceUrl+"/pubweb");//接口地址

        //向请求头中添加用户登录信息
        factory.getOutInterceptors().add(new AddLoginMsgHeaderInterceptor(username, password));
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        IPubWebService pubWebService = (IPubWebService)factory.create();
        ComplaintsSupervisio comp = new ComplaintsSupervisio();
        comp.setWsdtCode("20150700000000048701");//投诉对应ID
        comp.setReplyContent("测试督办回复内容"); // 回复内容
        comp.setXzspSupervisioId("1BEB5D23FDB10895E050007F010009D2"); // 督办识别号

        String message = pubWebService.replyComplaintSupervisio(comp);
        System.out.println(message);
        //String status = message.substring(0,message.indexOf(";"));
        String emsg = message.substring(message.indexOf(";")+1);
        System.out.println("message："+emsg);
    }
}
