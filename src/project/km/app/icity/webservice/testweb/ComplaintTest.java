package km.app.icity.webservice.testweb;

import km.app.icity.webservice.IPubWebService;
import km.app.icity.webservice.entity.Complaint;
import km.app.icity.webservice.interceptor.AddLoginMsgHeaderInterceptor;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * ClassName: ComplaintTest <br/>
 * date: 2015年10月14日 上午10:58:03 <br/>
 * @author lw
 */
public class ComplaintTest {

    private JaxWsProxyFactoryBean factory;
    private static String serviceUrl = "http://192.168.20.117:9999";//接口地址
    private static String username = "ynwsdt";//用户名
    private static String password = "ynwsdt";//密码

//    @Ignore
    @Test
    //调用接口提交投诉信息
    public void testCommitComplaint() {
        //创建客户端代理工厂
        factory = new JaxWsProxyFactoryBean();
        //注册WebService接口
        factory.setServiceClass(IPubWebService.class);
        //设置WebService地址
        factory.setAddress(serviceUrl+"/xzspservice/pubweb");//接口地址

        //向请求头中添加用户登录信息
        factory.getOutInterceptors().add(new AddLoginMsgHeaderInterceptor(username, password));
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        IPubWebService pubWebService = (IPubWebService)factory.create();
        Complaint comp = new Complaint();
        comp.setAddress("测试地址");
        comp.setContent("测试内容");
        comp.setDepCode("015100397");   //015100397:省水利厅
        comp.setEmail("test@qq.com");
        comp.setOrgCode("530000");
        comp.setPerson("inspur");
        comp.setPhone("13767567468");
        comp.setTheme("test");
        comp.setWsdtCode("4535159");//投诉ID

        String message = (String)pubWebService.commitComplaint(comp);
        String status = message.substring(0,message.indexOf(";"));
        System.out.println("status:"+status);
        System.out.println(message);


    }

    @Ignore
    @Test
    //调用接口提交投诉回复信息
    public void testCommitComplaintReply() {
        //创建客户端代理工厂
        factory = new JaxWsProxyFactoryBean();
        //注册WebService接口
        factory.setServiceClass(IPubWebService.class);
        //设置WebService地址
        factory.setAddress(serviceUrl+"/xzspservice/pubweb");//接口地址

        //向请求头中添加用户登录信息
        factory.getOutInterceptors().add(new AddLoginMsgHeaderInterceptor(username, password));
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        IPubWebService pubWebService = (IPubWebService)factory.create();
        Complaint comp = new Complaint();
        comp.setReplyOpinion("回复意见。。。。");//回复内容
        comp.setWsdtCode("4535159");

        String message = (String)pubWebService.replyComplaint(comp);
        String status = message.substring(0,message.indexOf(";"));
        System.out.println("status:"+status);
        System.out.println(message);


    }
}
