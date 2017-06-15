package hlj_qqheNew.app.icity.project;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.parsers.DocumentBuilder;   
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;   

import javax.xml.parsers.DocumentBuilder;   
import javax.xml.parsers.DocumentBuilderFactory;   

import org.w3c.dom.Document;   
import org.w3c.dom.Element;   
import org.w3c.dom.Node;   
import org.w3c.dom.NodeList;   
import org.xml.sax.InputSource;   




import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;




import app.icity.project.ProjectQueryDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;
import cq.app.icity.govservice.GovProjectDao;

public class QueryEcGapXMLJXDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(QueryEcGapXMLJXDao.class);
	protected static String icityDataSource = "icityDataSource";
	private static QueryEcGapXMLJXDao _instance = null;

	public static QueryEcGapXMLJXDao getInstance() {
		return DaoFactory.getDao(QueryEcGapXMLJXDao.class.getName());
	}

	public QueryEcGapXMLJXDao() {
		this.setDataSourceName(icityDataSource);
	}
	public DataSet getGrOrQyCountsx(ParameterSet pSet) {
		// TODO Auto-generated method stub
		DataSet ds;
		String DEPTNAME=(String) pSet.getParameter("DEPTNAME");
		//String BUSINESSTYPE=(String) pSet.getParameter("BUSINESSTYPE");
	    String sql="select count(*) as total from  ywtype where openo in (select ywno from yytimeconfig t where ISENBLED='是' and ywno in (select openo from ywtype t where DEPTNAME like "+"'%"+DEPTNAME+"%'" +"))";
		ds = this.executeDataset(sql, null,"pddbgzDataSource-"+(String)pSet.getParameter("XZQHDM"));
		return ds;
	}
	public DataSet getBusinessIndex(ParameterSet pset) {
		String sql0 = "SELECT SBLSH, SXBM, SXMC, UCID,SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
				+ " SJDWDM, SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
				+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE FROM BUSINESS_INDEX B WHERE 1=1 ";
		sql0 = SqlCreator.getSimpleQuerySql(pset, sql0, this.getDataSource());
		DataSet ds = this.executeDataset(sql0);
		
		String XZQHDM = SecurityConfig.getString("WebRegion");
		String sblsh=(String) pset.getParameter("SBLSH");
        String sql = "select SerailNO from ZTEExchange WHERE zteno = '"+sblsh+"'";
		DataSet ds1 = executeDataset(sql, null, "NewEcGapDataSource-"+XZQHDM);
		
		String ApplySerialNo = ds1.getJOData().get("SERAILNO")+"";
		String sql1 = "select ActivityInstanceId,SEQNUM,APPLYSUBJECT,ACTIVITYMODELNAME,JOINUSERNAME,ApplyFullName,ACCEPTORGNAME,ACCEPTTIME,APPLYCURSTATE from Biz_ApplyBase where ApplySerialNo='"+ApplySerialNo+"'";
    	DataSet ds2 = this.executeDataset(sql1, null, "NewEcGapDataSource-"+XZQHDM);
    	String ACTIVITYINSTANCEID = ds2.getJOData()!=null?ds2.getJOData().getString("ACTIVITYINSTANCEID"):"";
    	
    	String str = createState(sblsh,XZQHDM);
  	Map<String,Object> map = getState(str);
    	String state ="";
    	String reason = "";
    	state = String.valueOf(map.get("state"));
    	reason = String.valueOf(map.get("reason"));
    	String[] array = state.split("\\|");//英文
        state = array[1];
    	
//    	String str = createState(sblsh);
//    DataSet data =	QueryEcGapXMLJXDao.getInstance().getState(str);
		DataSet resultds = new DataSet();
		if(!"".equals(ACTIVITYINSTANCEID)){
			String sql3 = "select ActivityModelStepId,ActivityModelStepName,convert(varchar(100),ReceiveTime,120) receivetime,Dept,ActivityTaskBatchId,ActorName,PrevStepName from ToDoWorkView where  ActivityInstanceId ='"+ACTIVITYINSTANCEID+"'";
			DataSet ds4 = this.executeDataset(sql3, null, "NewEcGapDataSource-"+XZQHDM);
			
        	JSONObject jo = ds2.getJOData();
        	if(null!=ds4.getJOData()&&ds4.getJOData().getString("PREVSTEPNAME").equals("开始")){
        		jo.put("CONTENT", ds4.getJAData());
        	}else{
        		String sqlLog = "select TransactPerson as ACTORNAME, convert(varchar(100),StateChangeTime,120) RECEIVETIME from in_ExportalReq WHERE SerailNO = '"+ApplySerialNo+"'";
        		DataSet dsLog = this.executeDataset(sqlLog, null, "NewEcGapDataSource-"+XZQHDM);
        		jo.put("CONTENT_START", dsLog.getJAData());
        		String sql2="select a.PrevStepName,a.currentstepname,a.ActivityModelStepId,b.Opinion as ActivityModelStepName,a.Dept,convert(varchar(100),ReceiveTime,120)as receivetime,a.ActivityTaskBatchID,a.ActorName  from In_CompleteWorkList a,formopinion b where a.ActivityInstanceID='"+ACTIVITYINSTANCEID+"' and a.ActivityInstanceID=b.ActivityInstanceID and a.ActivityModelStepId=b.ActivityModelStepId";
	        	DataSet ds3 = this.executeDataset(sql2, null, "NewEcGapDataSource-"+XZQHDM);
        		jo.put("CONTENT", ds3.getJAData());
        	}
			resultds.setData(Tools.jsonToBytes(jo));
			resultds.setTotal(ds2.getTotal());
			resultds.setState(ds2.getState());
			resultds.setRoute(state);
			resultds.setMessage(reason);
			return resultds;
		}else{
			ds.setRoute(state);
	    	ds.setMessage(reason);
			return ds;
		}
		
	}
	public DataSet getFormBqbz(ParameterSet pset) {
		String sblsh=(String) pset.getParameter("SBLSH");
		String region_code = (String)pset.getParameter("REGION_CODE");
		String str = createState(sblsh,region_code);
		JSONObject json;
    	json = getMaterials(str);
		DataSet ds = new DataSet();

		try {
		    ds.setData(Tools.jsonToBytes(json));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	public DataSet getBusinessIndexAllList(ParameterSet pset) throws ParseException {
		String sql0 = "SELECT SBLSH, SXBM, SXMC, UCID,SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
				+ " SJDWDM, SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
				+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE FROM BUSINESS_INDEX B WHERE 1=1 ";
		sql0 = SqlCreator.getSimpleQuerySql(pset, sql0, this.getDataSource());
		DataSet ds = this.executeDataset(sql0);
		
		String XZQHDM = SecurityConfig.getString("WebRegion");
		String sblsh=(String) pset.getParameter("SBLSH");
        String sql = "select SerailNO from ZTEExchange WHERE zteno = '"+sblsh+"'";
		DataSet ds1 = executeDataset(sql, null, "NewEcGapDataSource-"+XZQHDM);
		
		String ApplySerialNo = ds1.getJOData().get("SERAILNO")+"";
		String sql1 = "select ActivityInstanceId,SEQNUM,APPLYSUBJECT,ACTIVITYMODELNAME,JOINUSERNAME,ApplyFullName,ACCEPTORGNAME,ACCEPTTIME,APPLYCURSTATE " +
				"from Biz_ApplyBase where ApplySerialNo='"+ApplySerialNo+"'";
    	DataSet ds2 = this.executeDataset(sql1, null, "NewEcGapDataSource-"+XZQHDM);
    	String ACTIVITYINSTANCEID = ds2.getJOData()!=null?ds2.getJOData().getString("ACTIVITYINSTANCEID"):"";
    	
    	String str = createState(sblsh,XZQHDM);
    	Map<String,Object> map = getState(str);
    	String state ="";
    	String reason = "";
		state = String.valueOf(map.get("state"));
		reason = String.valueOf(map.get("reason"));
    	String[] array = state.split("\\|");//英文
        state = array[1];
		DataSet resultds = new DataSet();
	//	if(ACTIVITYINSTANCEID!=""||ACTIVITYINSTANCEID!=null){
		if(StringUtils.isNotBlank(ACTIVITYINSTANCEID)){	
			String sql3 = "select ActivityModelStepId,ActivityModelStepName,convert(varchar(100),ReceiveTime,120) receivetime,Dept,ActivityTaskBatchId,ActorName,PrevStepName from ToDoWorkView where  ActivityInstanceId ='"+ACTIVITYINSTANCEID+"'";
			DataSet ds4 = this.executeDataset(sql3, null, "NewEcGapDataSource-"+XZQHDM);
			
        	JSONObject jo = ds2.getJOData();
        	if(null!=ds4.getJOData()&&ds4.getJOData().getString("PREVSTEPNAME").equals("开始")){
        		jo.put("CONTENT", ds4.getJAData());
        	}else{
        		String sqlLog = "select TransactPerson as ACTORNAME, convert(varchar(100),StateChangeTime,120) RECEIVETIME from in_ExportalReq WHERE SerailNO = '"+ApplySerialNo+"'";
        		DataSet dsLog = this.executeDataset(sqlLog, null, "NewEcGapDataSource-"+XZQHDM);
        		jo.put("CONTENT_START", dsLog.getJAData());
        		String sql2="select a.PrevStepName,a.currentstepname,a.ActivityModelStepId,b.Opinion as ActivityModelStepName,a.Dept,convert(varchar(100),ReceiveTime,120)as receivetime,a.ActivityTaskBatchID,a.ActorName  from In_CompleteWorkList a,formopinion b where a.ActivityInstanceID='"+ACTIVITYINSTANCEID+"' and a.ActivityInstanceID=b.ActivityInstanceID and a.ActivityModelStepId=b.ActivityModelStepId";
	        	DataSet ds3 = this.executeDataset(sql2, null, "NewEcGapDataSource-"+XZQHDM);
        		jo.put("CONTENT", ds3.getJAData());
        	}
			resultds.setData(Tools.jsonToBytes(jo));
			resultds.setTotal(ds2.getTotal());
			resultds.setState(ds2.getState());
			resultds.setRoute(state);
			resultds.setMessage(reason);
			resultds.setTotal(1);
			return resultds;
		}else{
			ds.setRoute(state);
	    	ds.setMessage(reason);
	    	ds.setTotal(0);
		//	ds.setMessage("不符合要求！");
			return ds;
		}
		
	}
	  public String createState(String sblsh,String region_code){
          //调用接口，传递参数
          String serviceUrl=SecurityConfig.getString("EcGapServiceUrlB");
          String soapaction="http://tempuri.org/";   //域名，这是在server定义的
          Service service = new Service(); 
          String ret ="";
          Call call;
          //截取区划前六位
          region_code = region_code.substring(0,6);
  		try {
  			call = (Call) service.createCall();
  			call.setTargetEndpointAddress(serviceUrl); 
  	        call.setOperationName(new QName(soapaction,"GetCaseState")); //设置要调用哪个方法  
              call.addParameter(new QName(soapaction,"ZteNO"),org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN); 
              call.addParameter(new QName(soapaction,"districts"),org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);  
              call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
              call.setUseSOAPAction(true);  
              call.setSOAPActionURI(soapaction + "GetCaseState");   
  	         ret = (String)call.invoke(new Object[]{sblsh,region_code}); 
  	        System.out.println("看看到底返回了啥："+ret);
  		} catch (ServiceException e) {
  			e.printStackTrace();
  		} catch (RemoteException e) {
  			e.printStackTrace();
  		}
          return ret;
			
		}
      public static   Map<String,Object> getState(String xml) {   
    	  String state ="";
    	  String reason="";
    	  DataSet ds = new DataSet();
    	 Map<String,Object> map = new HashMap<String,Object>();
           try {   
        	   
                DocumentBuilderFactory factory = DocumentBuilderFactory   
                        .newInstance();   
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder builder = factory.newDocumentBuilder(); 
                Document doc = builder.parse(new InputSource(new StringReader(xml)));   
     
                Element root = doc.getDocumentElement();   
                NodeList books = root.getChildNodes();   
               if (books != null) {   
                   for (int i = 0; i < books.getLength(); i++) {   
                        Node book = books.item(i);   
                        System.out.println("节点=" + book.getNodeName() + "\ttext="  
                                + book.getFirstChild().getNodeValue());
                        if("State".equals(book.getNodeName())){
                        	state=book.getFirstChild().getNodeValue();
                        }else if("Reason".equals(book.getNodeName())){
                        	reason =  book.getFirstChild().getNodeValue();
                        }
                        
                    }   
                }   
            } catch (Exception e) {   
                e.printStackTrace();   
            }  
           ds.setMessage(state);
           map.put("state", state);
           map.put("reason", reason);
           return map;
        }   

      public  JSONObject getMaterials(String xml) {   
    	  JSONArray jsArray = new JSONArray();
    	  JSONObject json=new JSONObject();
  		  JSONObject jsonLast=new JSONObject();
    	  
           try {   
                DocumentBuilderFactory factory = DocumentBuilderFactory   
                        .newInstance();   
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder builder = factory.newDocumentBuilder();   
                Document doc = builder.parse(new InputSource(new StringReader(xml)));   
                NodeList listOfPersons = doc.getElementsByTagName("Materials");
                
                for(int s=0; s<listOfPersons.getLength() ; s++){
                    Node firstPersonNode = listOfPersons.item(s);
                    if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){
                        Element firstPersonElement = (Element)firstPersonNode;
                        //-------                   
                        NodeList firstNameList = firstPersonElement.getElementsByTagName("Name");
                        Element firstNameElement = (Element)firstNameList.item(0);

                        NodeList textFNList = firstNameElement.getChildNodes();
                        System.out.println("First Name : " +
                               ((Node)textFNList.item(0)).getNodeValue().trim());
                        json.put("Materials", ((Node)textFNList.item(0)).getNodeValue().trim());
                        jsArray.add(json);
                       
                    }//end of if clause
                }//end of for loop with s var
            } catch (Exception e) {   
                e.printStackTrace();   
            }  
           jsonLast.put("metail", jsArray);
           return jsonLast;
        }   
      public  JSONArray getBqbzMaterials(String xml) {   
    	  JSONArray jsArray = new JSONArray();
    	  JSONObject json=new JSONObject();
    	  
           try {   
                DocumentBuilderFactory factory = DocumentBuilderFactory   
                        .newInstance();   
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder builder = factory.newDocumentBuilder();   
                Document doc = builder.parse(new InputSource(new StringReader(xml)));   
                NodeList listOfPersons = doc.getElementsByTagName("Materials");
                
                for(int s=0; s<listOfPersons.getLength() ; s++){
                    Node firstPersonNode = listOfPersons.item(s);
                    if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){
                        Element firstPersonElement = (Element)firstPersonNode;
                        //-------                   
                        NodeList firstNameList = firstPersonElement.getElementsByTagName("Name");
                        Element firstNameElement = (Element)firstNameList.item(0);

                        NodeList textFNList = firstNameElement.getChildNodes();
                        System.out.println("First Name : " +
                               ((Node)textFNList.item(0)).getNodeValue().trim());
                        json.put("Materials", ((Node)textFNList.item(0)).getNodeValue().trim());
                        jsArray.add(json);
                       
                    }//end of if clause
                }//end of for loop with s var
            } catch (Exception e) {   
                e.printStackTrace();   
            }  
        //   jsonLast.put("metail", jsArray);
           return jsArray;
        }   
}


