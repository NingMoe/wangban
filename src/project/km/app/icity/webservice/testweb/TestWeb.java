package km.app.icity.webservice.testweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.inspur.util.Tools;

import app.util.CommonUtils;
import km.app.icity.webservice.IPubWebService;
import km.app.icity.webservice.entity.CommitTranResponse;
import km.app.icity.webservice.entity.Transaction;
import km.app.icity.webservice.entity.TransactionMaterial;
import km.app.icity.webservice.interceptor.AddLoginMsgHeaderInterceptor;
import net.sf.json.JSONObject;
/**
 * 
 * ClassName: TestWeb <br/>
 * date: 2015年10月14日 上午10:58:14 <br/>
 * @author lw
 */
public class TestWeb {
	 private static JaxWsProxyFactoryBean factory;	
		
	 public static void main(String[] args) {		 
			//创建客户端代理工厂
			factory = new JaxWsProxyFactoryBean();
			//注册WebService接口
			factory.setServiceClass(IPubWebService.class);
			//设置WebService地址
		 	factory.setAddress("http://172.30.2.165:8081/xzspservice/pubweb");//互联网接口地址

			//向请求头中添加用户登录信息  
			factory.getOutInterceptors().add(new AddLoginMsgHeaderInterceptor("ynwsdt", "ynwsdt"));
			factory.getOutInterceptors().add(new LoggingOutInterceptor());
			IPubWebService pubWebService = (IPubWebService)factory.create();
			//调用远程方法
			//测试数据的保存
			Transaction tranB = new Transaction();
			tranB.setTranCode("12818035300000031404153hejindong");
			tranB.setTranNumber(2);
			tranB.setTranContent("测试附件交换111"); // 方便测试，建议每测试一次都修改信息，通过系统可以更容易识别出数据
			tranB.setTranSource("inspur");
			tranB.setApplicantName("inspur");  // 方便测试，建议每测试一次都修改信息
			tranB.setApplicantType("1");
			tranB.setAppCardType("SYNDA-CT-JMSFZ");
			tranB.setAppCardNumber("370782198701564778");
			tranB.setApplicantEmail("inspur@qq.com");
			tranB.setApplicantAddress("云南省投资审批中心");
			tranB.setApplicantPhone("15887866547");
//			tranB.setRepCardType("SYNDA-CT-JMSFZ");
//			tranB.setRepCardNumber("370782198701564778");
			tranB.setAgentName("hejindong");
			tranB.setAgentCardType("SYNDA-CT-JMSFZ");
			tranB.setAgentCardNumber("370782198701564778");
			tranB.setAgentAddress("测试地址");
			tranB.setAgentEmail("test@qq.com");
			tranB.setAgentFax("0871-21313");
			tranB.setAgentPhone("1587647635");
			tranB.setProceedCode("1000013532600002");
			tranB.setProceedVersion(1);

			Date currentTime_2 = CommonUtils.getInstance().parseStringToTimeStamp(Tools.formatDate(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),CommonUtils.YYYY_MM_DD_HH_mm_SS);
			tranB.setTranAppTime(currentTime_2);

		 	//办件材料信息
		 	List<TransactionMaterial> tmList = new ArrayList<TransactionMaterial>();
		 	TransactionMaterial tm = new TransactionMaterial();
		 	tm.setTranId("12818035300000031404153inspur");  //办件编号
		 	tm.setMaterialId("20141100000000193196");
		 	tm.setMaterialName("附件上传");  //材料名称
		 	tm.setMaterialType("E");  //材料种类
		 	tm.setMaterialNumber("1");   //材料数量
		 	tm.setTrueFileName("新建文本文档.txt");  //真实文件名
		 	tm.setLicenseNumber("5300001");
		 	tm.setAppMaterialFile("http://192.168.20.33/sx/file/upload/4b0602c048f742eb8be5922143e53287.txt");  //电子材料文件
		 	tmList.add(tm);
		 	tranB.setTranMaterialList(tmList);
		 	JSONObject jsonStu = JSONObject.fromObject(tranB);  
			pubWebService.commitTransactionInfo(jsonStu.toString());
		 /*	if(ctr.getStatus().equals("1")){
			 	System.out.println("推送成功！ 状态代码："+ctr.getStatus());
		 	}else{
			 	System.out.println("推送失败！ 状态代码："+ctr.getStatus()+" ，失败原因："+ctr.getMessage());
		 	}*/

  }
	
	
	
}
