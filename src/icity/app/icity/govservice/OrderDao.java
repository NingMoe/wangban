package app.icity.govservice;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

import app.util.RestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;  
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;  
import org.jdom.Element;  
import org.jdom.JDOMException;  
import org.jdom.output.Format;  
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;  
  
import org.xml.sax.InputSource; 

public class OrderDao extends BaseJdbcDao{
	 private static final String salt = com.icore.util.SecurityConfig.getString("uniteCodeSalt"); //秘钥串、、统一编码服务配置的salt
	public static OrderDao getInstance() {
		return DaoFactory.getDao(OrderDao.class.getName());
	}

	
	public DataSet getOrder() {
		DataSet ds = new DataSet();
		//String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestDictList");
		try {
			//JSONObject receiveNum = JSONObject.fromObject(RestUtil.getData(url));
			JSONObject receiveNum = new JSONObject();
			receiveNum.put("MERID", 1);
			receiveNum.put("ORDERID", 2);
			receiveNum.put("CUSTOMNAME", 3);
			receiveNum.put("ORDERDATE", 4);
			receiveNum.put("AMOUNT", 0.1);
			receiveNum.remove("XMHYFLML");
			ds.setRawData(receiveNum);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getInvestDictList！");
			return ds;
		}
		return ds;
	}

    //将提交信息组织成XML格式
    public DataSet getOrderXMLDoc() throws IOException, JDOMException { 
    	DataSet ds = new DataSet();
        // 创建根节点 并设置它的属性 ;     
        Element root = new Element("REQDATA");     
        // 将根节点添加到文档中；     
        Document Doc = new Document(root);     
             
           // 给 根节点添加子节点并赋值；     
        root.addContent(new Element("MERID").setText(getOrder().getRecord(0).getString("MERID")));    
        root.addContent(new Element("ORDERID").setText(getOrder().getRecord(0).getString("ORDERID")));       
        root.addContent(new Element("CUSTOMNAME").setText(getOrder().getRecord(0).getString("CUSTOMNAME")));
        root.addContent(new Element("ORDERDATE").setText(getOrder().getRecord(0).getString("ORDERDATE")));
        root.addContent(new Element("AMOUNT").setText(getOrder().getRecord(0).getString("AMOUNT")));
        
        // 输出 xml 文件；    
        // 使xml文件 缩进效果  
        Format format = Format.getPrettyFormat();  
        XMLOutputter XMLOut = new XMLOutputter(format);  
        
        String base64String =  XMLOut.outputString(Doc); 
         byte[] reqmsg0 = Base64.encodeBase64(base64String.getBytes());
         String reqmsg =new String(reqmsg0);
         
         JSONObject result = new JSONObject();
         result.put("reqmsg", reqmsg);
         String token = this.getToken(base64String);
         byte[] signstr0 = Base64.encodeBase64(token.getBytes());
         String signstr =new String(signstr0);
         
         result.put("signstr", signstr);
         ds.setRawData(result);
        System.err.println("@@@@"+ds);
		return ds;
    }
    public String getToken(String itemId){
        String token = DigestUtils.md5Hex(itemId+salt);
        if(StringUtils.isNotEmpty(token)){
            return token;
        }else {
            return "";
        }
    }

//接收订单反馈
	public DataSet getOrderFeedback() {
		Connection connpre = DbHelper.getConnection("xcDataSource");
		DataSet ds = new DataSet();
		//String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestDictList");
		try {
			//JSONObject receiveNum = JSONObject.fromObject(RestUtil.getData(url));
			JSONObject receiveNum = new JSONObject();
			//签名前
			String reqmsg=(String) getOrderXMLDoc().getRecord(0).get("reqmsg");
			    //String reqmsg=(String) data.get("resmsg");
			byte[] resmsg0=new BASE64Decoder().decodeBuffer(reqmsg);
			String resmsg =new String(resmsg0);
			//签名后
			String signstr=(String) getOrderXMLDoc().getRecord(0).get("signstr");
			     //String signstr=(String)data.get("resmsgsign");
			byte[] resmsgsign0=new BASE64Decoder().decodeBuffer(signstr);
			String resmsgsign =new String(resmsgsign0);
			
			String resmsg1=getToken(resmsg);
			if (resmsg1.equals(resmsgsign)) {//如果反馈回的数据符合签名规则
			DataSet dst=ReadXmlFile(resmsg);
			//将数据写入数据库
				try {
					String sql = "insert into order_feedback(merid,merorderid,customname,merorderdate) values(?,?,?,?)";
					int j = DbHelper.update(sql,new Object[] {
							dst.getRecord(0).get("MERID"),dst.getRecord(0).get("ORDERID"),
							dst.getRecord(0).get("CUSTOMNAME"),dst.getRecord(0).get("ORDERDATE")
											 }, connpre);
					if (j > 0) {
					connpre.commit();
				} else {
					connpre.rollback();
				}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					connpre.rollback();
				}
			
			}
			receiveNum.put("MERID", 1111);
			receiveNum.put("ORDERID", 1111);
			receiveNum.put("CUSTOMNAME", 1111);
			receiveNum.put("ORDERDATE", 1111);
			receiveNum.put("AMOUNT", 0.1);
			receiveNum.remove("XMHYFLML");
			ds.setRawData(receiveNum);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getInvestDictList！");
			return ds;
		}
		return ds;
	}
	
	//解析xml文件
	public DataSet ReadXmlFile(String xml) {
		DataSet ds = new DataSet();
		try {
			Reader rr=new StringReader(xml); 
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			//Document doc = (Document) builder.parse(xmlFile);
			org.w3c.dom.Document doc=builder.parse(new InputSource(rr)); 
			doc.getDocumentElement().normalize();
			System.out.println("Root element: " +  doc.getDocumentElement().getNodeName());
			NodeList nList =  doc.getElementsByTagName("REQDATA");

			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				System.out.println("Node name: " + node.getNodeName());
				org.w3c.dom.Element ele = (org.w3c.dom.Element) node;
					System.out.println("MERID: " + ele.getElementsByTagName("MERID").item(0).getTextContent());
					System.out.println("ORDERID: " +ele.getElementsByTagName("ORDERID").item(0).getTextContent());
					System.out.println("CUSTOMNAME:" +ele.getElementsByTagName("CUSTOMNAME").item(0).getTextContent());
					System.out.println("ORDERDATE: " +ele.getElementsByTagName("ORDERDATE").item(0).getTextContent());
				JSONObject result = new JSONObject();
			        result.put("MERID",ele.getElementsByTagName("MERID").item(0).getTextContent());
			        result.put("ORDERID",ele.getElementsByTagName("ORDERID").item(0).getTextContent());
			        result.put("CUSTOMNAME",ele.getElementsByTagName("CUSTOMNAME").item(0).getTextContent());
			        result.put("ORDERDATE",ele.getElementsByTagName("ORDERDATE").item(0).getTextContent());
			        ds.setRawData(result);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		System.err.println("WWW"+ds);
		return ds;

	}


	public DataSet getQueryOrder() {
		DataSet ds = new DataSet();
		//String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestDictList");
		try {
			//JSONObject receiveNum = JSONObject.fromObject(RestUtil.getData(url));
			JSONObject receiveNum = new JSONObject();
			receiveNum.put("MERID", 11);
			receiveNum.put("ORDERID", 22);
			receiveNum.put("MERORDERDATE", 33);
			receiveNum.remove("XMHYFLML");
			ds.setRawData(receiveNum);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getInvestDictList！");
			return ds;
		}
		return ds;
	}


	public DataSet getQueryXMLDoc() throws IOException, JDOMException { 
    	DataSet ds = new DataSet();
        // 创建根节点 并设置它的属性 ;     
        Element root = new Element("REQDATA");     
        // 将根节点添加到文档中；     
        Document Doc = new Document(root);     
             
           // 给 根节点添加子节点并赋值；     
        root.addContent(new Element("INTERFACENAME").setText("SDCZ_PAYGW_FUNC1002"));
        root.addContent(new Element("INTERFACEVERSION").setText("1.0.0"));
        root.addContent(new Element("MERID").setText(getQueryOrder().getRecord(0).getString("MERID")));    
        root.addContent(new Element("ORDERID").setText(getQueryOrder().getRecord(0).getString("ORDERID")));       
        root.addContent(new Element("MERORDERDATE").setText(getQueryOrder().getRecord(0).getString("MERORDERDATE")));
       
        
        // 输出 xml 文件；    
        // 使xml文件 缩进效果  
        Format format = Format.getPrettyFormat();  
        XMLOutputter XMLOut = new XMLOutputter(format);  
        XMLOut.output(Doc, new FileOutputStream("f:/query.xml"));  
        
        String base64String =  XMLOut.outputString(Doc); 
         byte[] reqmsg0 = Base64.encodeBase64(base64String.getBytes());
         String reqmsg =new String(reqmsg0);
         
         JSONObject result = new JSONObject();
         result.put("reqmsg", reqmsg);
         String token = this.getToken(base64String);
         byte[] signstr0 = Base64.encodeBase64(token.getBytes());
         String signstr =new String(signstr0);
         
         result.put("signstr", signstr);
         ds.setRawData(result);
        System.err.println(ds);
		return ds;
    
	}

}
