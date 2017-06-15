package fz.app.icity;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;

import app.uc.UserDao;

import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import com.inspur.util.PathUtil;
import com.kinsec.PKISvrTcpJavaClt;

public class PKISvrTcpJavaCltCmd extends BaseQueryCommand {
	private static Logger _log = LoggerFactory.getLogger(BaseQueryCommand.class);

	public DataSet getUKInfo(ParameterSet pSet) {
		return PKISvrTcpJavaClt.getInstance().getUKInfo(pSet);
	}

	/**
	 * 登录
	 */
	public String Authorization() {
		String URL = SecurityConfig.getString("FuzhouWebServiceUrl");
		if (StringUtils.isBlank(URL)) {
			return "";
		}
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(URL));
			call.setOperationName(new QName("http://tempuri.org/", "Authorization"));
			call.addParameter(new QName("http://tempuri.org/", "username"), XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter(new QName("http://tempuri.org/", "password"), XMLType.XSD_STRING, ParameterMode.IN);
			call.setUseSOAPAction(true);
			call.setSOAPActionURI("http://tempuri.org/Authorization");
			call.setReturnType(XMLType.XSD_STRING);
			String result = String.valueOf(call.invoke(
					new Object[] { SecurityConfig.getString("FLUserName"), SecurityConfig.getString("FLPassword") }));
			System.out.println("返回值 ： " + result);
			return result;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 调webservice获取证照信息
	 */
	public DataSet getLicenseData(ParameterSet pSet) {
		if ("".equals((String) pSet.get("zzjgdm")) || pSet.get("zzjgdm") == null) {
			JSONObject userInfo = new JSONObject();
			DataSet dSet = UserDao.getInstance().getUserById("" + this.getUserInfo(pSet).getUid());
			if (dSet.getTotal() > 0) {
				userInfo = dSet.getRecord(0);
			}
			String CREDIT_CODE = userInfo.getString("CREDIT_CODE");
			String ORG_NO = userInfo.getString("ORG_NO");
			String zzjgcode = "";
			if ("".equals(CREDIT_CODE) || CREDIT_CODE == null) {
				zzjgcode = ORG_NO;
			}else{
				zzjgcode = CREDIT_CODE;
			}
			pSet.put("zzjgdm", "11".endsWith(userInfo.getString("TYPE")) ? userInfo.getString("CARD_NO")
					: zzjgcode);
			pSet.put("type", userInfo.getString("TYPE"));
			pSet.put("name", userInfo.getString("NAME"));
		}
		DataSet ds = new DataSet();
		String URL = SecurityConfig.getString("FuzhouWebServiceUrl");
		if (StringUtils.isBlank(URL)) {
			_log.error("WebService地址未配置");
			ds.setMessage("WebService地址不正确");
			ds.setState(StateType.FAILT);
			return ds;
		}
		try {
			String Getresult = "";
			Service service = new Service();
			Call _call = (Call) service.createCall();
			_call.setTargetEndpointAddress(new java.net.URL(URL));
			if ("11".equals((String) pSet.get("type"))) {
				_call.setOperationName(new QName("http://tempuri.org/", "GetDocumentListBySFZ"));
				_call.addParameter(new QName("http://tempuri.org/", "guid"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "appID"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "businessID"), XMLType.XSD_STRING,
						ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "sfz"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "name"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.setUseSOAPAction(true);
				_call.setSOAPActionURI("http://tempuri.org/GetDocumentListBySFZ");
				_call.setReturnType(XMLType.XSD_STRING);
				Getresult = String.valueOf(_call.invoke(new Object[] { Authorization(),
						SecurityConfig.getString("FuzhouAppId"), (String) pSet.get("businessid"),
						(String) pSet.get("zzjgdm"), (String) pSet.get("name") }));
			} else if ("21".equals((String) pSet.get("type"))) {
				_call.setOperationName(new QName("http://tempuri.org/", "GetDocumentListByCode"));
				_call.addParameter(new QName("http://tempuri.org/", "guid"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "appID"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "businessID"), XMLType.XSD_STRING,
						ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "companyCode"), XMLType.XSD_STRING,
						ParameterMode.IN);

				_call.setUseSOAPAction(true);
				_call.setSOAPActionURI("http://tempuri.org/GetDocumentListByCode");
				_call.setReturnType(XMLType.XSD_STRING);
				Getresult = String
						.valueOf(_call.invoke(new Object[] { Authorization(), SecurityConfig.getString("FuzhouAppId"),
								(String) pSet.get("businessid"), (String) pSet.get("zzjgdm") }));
			} else {
				ds.setMessage("未知用户类型！");
				ds.setState(StateType.FAILT);
				return ds;
			}
			JSONArray resultList = xmlElements(Getresult);
			ds.setRawData(resultList);
			System.out.println("返回值 ： " + Getresult);
		} catch (Exception e) {
			_log.error(e.getMessage());
			ds.setMessage(e.getMessage());
			ds.setState(StateType.FAILT);
			return ds;
		}
		return ds;
	}

	/**
	 * 调webservice获取电子证照文件获取接口用于从政务信息资源交换系统获取具体的电子证照文件 public byte[]
	 * GetLicenseFileByFileNumberTwo(string guid,String licenseFileNumber )
	 * guid是身份验证串——必填 licenseFileNumber 是电子证照或电子批文的文件编号——必填
	 */
	public DataSet GetLicenseFileByFileNumberTwo(ParameterSet pSet) {
		if ("".equals((String) pSet.get("zzjgdm")) || pSet.get("zzjgdm") == null) {
			JSONObject userInfo = new JSONObject();
			DataSet dSet = UserDao.getInstance().getUserById("" + this.getUserInfo(pSet).getUid());
			if (dSet.getTotal() > 0) {
				userInfo = dSet.getRecord(0);
			}
			String CREDIT_CODE = userInfo.getString("CREDIT_CODE");
			String ORG_NO = userInfo.getString("ORG_NO");
			String zzjgcode = "";
			if ("".equals(CREDIT_CODE) || CREDIT_CODE == null) {
				zzjgcode = ORG_NO;
			}else{
				zzjgcode = CREDIT_CODE;
			}
			pSet.put("zzjgdm", "11".endsWith(userInfo.getString("TYPE")) ? userInfo.getString("CARD_NO")
					: zzjgcode);
			pSet.put("type", userInfo.getString("TYPE"));
		}

		DataSet ds = new DataSet();
		String URL = SecurityConfig.getString("FuzhouWebServiceUrl");
		if (StringUtils.isBlank(URL)) {
			_log.error("WebService地址未配置");
			ds.setMessage("WebService地址不正确");
			ds.setState(StateType.FAILT);
			return ds;
		}
		FileOutputStream fos = null;
		try {
			Object obj = null;
			Service service = new Service();
			Call _call = (Call) service.createCall();
			_call.setTargetEndpointAddress(new java.net.URL(URL));
			if ("11".equals((String) pSet.get("type"))) {
				_call.setOperationName(new QName("http://tempuri.org/", "GetLicenseFileByQueryOfPerson"));
				_call.addParameter(new QName("http://tempuri.org/", "guid"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "requestXml"), XMLType.XSD_STRING,
						ParameterMode.IN);
				_call.setUseSOAPAction(true);
				_call.setSOAPActionURI("http://tempuri.org/GetLicenseFileByQueryOfPerson");
				obj = _call.invoke(new Object[] { Authorization(),
						getRequestXml((String) pSet.get("LicenseType"), (String) pSet.get("licenseFileNumber")) });
			} else if ("21".equals((String) pSet.get("type"))) {
				// 服务名
				_call.setOperationName(new QName("http://tempuri.org/", "GetLicenseFileByFileNumberTwo"));
				// 定义入口参数和参数类型
				_call.addParameter(new QName("http://tempuri.org/", "guid"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "appID"), XMLType.XSD_STRING, ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "businessID"), XMLType.XSD_STRING,
						ParameterMode.IN);
				_call.addParameter(new QName("http://tempuri.org/", "licenseFileNumber"), XMLType.XSD_STRING,
						ParameterMode.IN);
				_call.setUseSOAPAction(true);
				// Action地址
				_call.setSOAPActionURI("http://tempuri.org/GetLicenseFileByFileNumberTwo");
				// 调用服务获取返回值
				obj = _call.invoke(new Object[] { Authorization(), SecurityConfig.getString("FuzhouAppId"),
						(String) pSet.get("businessid"), (String) pSet.get("licenseFileNumber") });
			} else {
				ds.setMessage("未知用户类型！");
				ds.setState(StateType.FAILT);
				return ds;
			}
			byte[] Getresult = new BASE64Decoder().decodeBuffer((String) obj);
			ds.setState(StateType.SUCCESS);
			String path1 = "file" + File.separator + "upload" + File.separator + (String) pSet.get("licenseFileNumber")
					+ ".edc";
			String path = PathUtil.getWebPath() + path1;
			fos = new FileOutputStream(path);
			fos.write(Getresult);
			ds.setMessage(File.separator + path1);
		} catch (Exception e) {
			_log.error(e.getMessage());
			ds.setMessage(e.getMessage());
			ds.setState(StateType.FAILT);
			return ds;
		}finally{
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ds;
	}

	public String getRequestXml(String LicenseType, String FileID) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<Request>" + "<Message >"
				+ "<BusinessInfo AppID=“A001” BusinessID=“002”></ BusinessInfo >" + "<Info Type=\"电子证照\">"
				+ "<TypeCode>" + "<Node  LicenseType=\"" + LicenseType + "\"  FileID=\"" + FileID + "\"></Node>"
				+ "</TypeCode>" + "</Info>" + "</Message>" + "</Request>";
		return xml;
	}

	public JSONArray xmlElements(String xmlDoc) {
		JSONArray ja = new JSONArray();
		// 创建一个新的字符串
		StringReader read = new StringReader(xmlDoc);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		// 创建一个新的SAXBuilder
		SAXBuilder sb = new SAXBuilder();
		try {
			// 通过输入源构造一个Document
			org.jdom.Document doc = sb.build(source);
			// 取的根元素
			org.jdom.Element root = doc.getRootElement();
			System.out.println(root.getName());// 输出根元素的名称（测试）
			// 得到根元素所有子元素的集合
			List jiedian = root.getChildren();
			// 获得XML中的命名空间（XML中未定义可不写）
			//org.jdom.Namespace ns = root.getNamespace();
			Element et = null;
			et = (Element) jiedian.get(0);
			Attribute type = et.getAttribute("Type");
			String t_value = type.getValue();
			if ("电子证照".equals(t_value)) {
				List zjiedian = ((org.jdom.Element) et).getChildren();
				for (int j = 0; j < zjiedian.size(); j++) {
					JSONObject jo = new JSONObject();
					Element xet = (Element) zjiedian.get(j);
					Attribute FileID = xet.getAttribute("FileID");
					jo.put("FileID", FileID.getValue());
					Attribute LicenseID = xet.getAttribute("LicenseID");
					jo.put("LicenseID", LicenseID.getValue());
					Attribute Name = xet.getAttribute("Name");
					jo.put("Name", Name.getValue());
					Attribute TypeCode = xet.getAttribute("TypeCode");
					jo.put("TypeCode", TypeCode.getValue());
					Attribute DataTime = xet.getAttribute("DataTime");
					jo.put("DataTime", DataTime.getValue());
					Attribute IsExpired = xet.getAttribute("IsExpired");
					jo.put("IsExpired", IsExpired.getValue());
					ja.add(jo);
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ja;
	}

	/**
	 * 对象转数组
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 数组转对象
	 * 
	 * @param bytes
	 * @return
	 */
	public Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}
}