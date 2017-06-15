package app.icity.enterprises;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * 企业设立 lhy
 * @author lenovo
 *
 */
public class EnterprisesDao extends BaseJdbcDao {
  
  private EnterprisesDao(){
    this.setDataSourceName("icityDataSource");
  }
  
  public static EnterprisesDao getInstance(){
		return DaoFactory.getDao(EnterprisesDao.class.getName());
  }
  
  public DataSet getList(ParameterSet pSet){    
    DataSet ds = null;    
    return ds;
  }
  public DataSet getNamePreCheckLoad(ParameterSet pSet){    
    DataSet ds = null;    
    return ds;
  }
  /**
   * 工商预核准
   * @param pSet
   * @return
   */
  public DataSet namePreCheckLoad(ParameterSet pSet){
    DataSet ds = new DataSet();
    String namePreCheckSwitch=SecurityConfig.getString("entname.precheck.swith","off");
    if("off".equals(namePreCheckSwitch)){
      JSONArray entList=new JSONArray();
      JSONObject ent=new JSONObject();
      ent.put("pripid", "developer-1");
      ent.put("entname", "developer-企业");
      entList.add(ent);
      ds.setRawData(entList);
      return ds;
      
    }
    String url = HttpUtil.formatUrl(SecurityConfig.getString("ICAuditUrl")+"/namePreCheckLoad");
    Map<String, String> data=new HashMap<String, String>();   
    data.put("adviceNoteNum", (String)pSet.get("num"));
    JSONObject receive = new JSONObject();
    try{
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      if("200".equals(receive.get("state"))){
        Object rawData = receive.get("companyList");
        ds.setRawData(rawData);
      }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  /**
   * 获取工商企业信息
   * @param pSet
   * @return
   */
  public DataSet _namePreCheckLoad(ParameterSet pSet){
/*    DataSet ds = new DataSet();
    JSONObject j = new JSONObject();
    String applyNumber = String.valueOf( pSet.get("applyNumber") );
    String s = "{"+
        "\"state\":\"200\","+
        "\"error\":\"当state为300时，提示错误信息\","+
        "\" NBXH\":\"企业内部序号，企业主体的唯一标识\","+
        "\" ZCH\":\"注册号\","+
        "\" QYMC\":\"企业名称\","+
        "\" FDDBR\":\"法定代表人\","+
        "\" SFZJHM\":\"身份证件号码\","+
        "\" ZS\": \"住所\","+
        "\" JYCS\": \"经营场所\","+
        "\" YZBM\": \"邮政编码\","+
        "\" JYFW\": \"经营范围\","+
        "\" ZCZB\": \"注册资本\","+
        "\" BZ\": \"注册资本币种\","+
        "\" CLRQ\":\"成立日期\","+
        "\" JYQSRQ\":\"经营起始日期\"  , "+
        "\" JYJZRQ\":\"经营截止日期\" ,  "+ 
        "\" DJJG\":\"登记机关\","+
        "\" GXDW\":\"管辖单位\","+
        "\" QYLXDL\":\"企业类型大类\","+
        "\" QYLX\":\"企业类型\","+
        "\" ZT\":\"状态\","+
        "\" HY\":\"行业\","+
        "\" CYRS\":\"从业人数\" ,"+ 
        "\" ZCH_OLD\":\"注册号老的\","+   
        "\" LXRXM\":\"联系人姓名\","+
        "\" LXRDH\":\"联系人电话\","+
        "\" LXRSFZJHM\":\"联系人身份证件号码\","+
        "}";
    j = JSONObject.fromObject(s);
    if("200".equals(j.get("state"))){
      Object rawData = j;
      ds.setRawData(rawData);
    }else{
      String error = (String)j.get("error");
      ds.setMessage(error);
    }
    return ds;*/
    
    DataSet ds = new DataSet();    
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/namePreCheckLoad");
    Map<String, String> data=new HashMap<String, String>();    
    data.put("approveNum", (String)pSet.get("approveNum"));
    data.put("password", (String)pSet.get("password"));
    data.put("flowId", (String)pSet.get("flowId"));
    JSONObject receive = new JSONObject();
    try{
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      if("200".equals(receive.get("state"))){
        Object rawData = receive;
        ds.setRawData(rawData);
      }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  /**
   * 获取业务规则信息
   * @param pSet参数名称  参数说明
        applyNumber  申报的大类的类别
              0公司设立
              1公司变更
              2公司注销
              3联审联办
        regionCode  区划编码
   * @return
   */
  public DataSet getBusinessRulesInfo(ParameterSet pSet, String region){
    
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getBusinessRulesInfo");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("applyNumber", (String)pSet.get("applyNumber"));
    data.put("regionCode", SecurityConfig.getString("WebRegion"));
    JSONObject receive = new JSONObject();
    try{
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      if("200".equals(receive.get("state"))){
        Object rawData = receive.get("info");
        ds.setRawData(rawData);
      }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  
  /**
   * 获取联办说明-舟山
   * @param pSet
   * @return
   */
  public DataSet zs_getSeecGuide(ParameterSet pSet){
    
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("EAParallelUrl") + "/getSeecGuide");
		Map<String, String> data=new HashMap<String, String>();;
		data.put("flowId", (String) pSet.get("flowId"));
		JSONObject receive = new JSONObject();
		try {
			// _log.info("调用获取表单id及材料接口前："+url);
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			// _log.info("返回值："+receive);
			if ("200".equals(receive.get("state"))) {
				
				String str = receive.getString("guide").replace("\r\n", "<br/>");
				receive.put("guide", str);
				
				ds.setRawData(receive);
			}
		} catch (Exception e) {
			String error = (String) receive.get("error");
			ds.setMessage(error);
		}
		return ds;
  }
  
  /**
   * 获取申报信息-舟山
   * @param pSet
   * @return
   */
  public DataSet zs_getApplicationInfo(ParameterSet pSet){
    
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getApplicationInfo");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("flowId", (String)pSet.get("flowId"));    
    JSONObject receive = new JSONObject();
    try{
      //_log.info("调用获取表单id及材料接口前："+url);
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      //_log.info("返回值："+receive);
      if("200".equals(receive.get("state"))){
          Object rawData = receive.get("info");
          ds.setRawData(rawData);
        }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  
  /**
   * 获取办事流程-舟山
   * @param pSet
   * @return
   */
  public DataSet zs_getFlowImg(ParameterSet pSet){
    
	  DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl") + "/getFlowImg");
		Map<String, String> data=new HashMap<String, String>();
		data.put("flowId", (String) pSet.get("flowId"));
		JSONObject receive = new JSONObject();
		try {
			// _log.info("调用获取表单id及材料接口前："+url);
			receive = JSONObject.fromObject(RestUtil.postData(url, data));
			// _log.info("返回值："+receive);
			if ("200".equals(receive.get("state"))) {
				Object rawData = receive.get("info");
				ds.setRawData(rawData);
			}
		} catch (Exception e) {
			String error = (String) receive.get("error");
			ds.setMessage(error);
		}
		return ds;
  }
  
  /**
   * 获取联办事项材料及时限-舟山
   * @param pSet
   * @return
   */
  public DataSet zs_getSeecItem(ParameterSet pSet){
    
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getSeecItem");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("flowId", (String)pSet.get("flowId"));    
    JSONObject receive = new JSONObject();
    try{
      //_log.info("调用获取表单id及材料接口前："+url);
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      //_log.info("返回值："+receive);
      if("200".equals(receive.get("state"))){
          Object rawData = receive.get("info");
          ds.setRawData(rawData);
        }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  
  /**
   * 获取申报信息
   * @param pSet
   * @return
   */
  public DataSet getApplicationInfo(ParameterSet pSet){
/*    DataSet ds = new DataSet();
    JSONObject j = new JSONObject();
    String s = "{\"state\": \"200\",\"error\": \"当state为300时，提示错误信息\"," +
        "\"info\": {\"formId\":\"QiYeSheLiShenPiBiao\",\"flowId\":\"流转规则id\"," +
        "\"itemArray\":[{\"itemId\":\"事项ID1\",\"itemName\":\"事项名称1\"," +
        "\"resourceArray\":[" +
        "{\"resourceCode\":\"材料编码1\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称1\"}," +
        "{\"resourceCode\":\"材料编码2\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称2\"}," +
        "{\"resourceCode\":\"材料编码3\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称3\"}" +
        "]},{\"itemId\":\"事项ID2\",\"itemName\":\"事项名称2\"," +
        "\"resourceArray\":[" +
        "{\"resourceCode\":\"材料编码4\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称4\"}," +
        "{\"resourceCode\":\"材料编码5\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称5\"}," +
        "{\"resourceCode\":\"材料编码6\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称6\"}" +
        "]}]}}";
    j = JSONObject.fromObject(s);
    if("200".equals(j.get("state"))){
      Object rawData = j.get("info");
      ds.setRawData(rawData);
    }else{
      String error = (String)j.get("error");
      ds.setMessage(error);
    }
    ds.setTotal(2);
    return ds;*/
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getApplicationInfo");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("enterpriseType", (String)pSet.get("enterpriseType"));
    data.put("regionCode", SecurityConfig.getString("WebRegion"));
    JSONObject receive = new JSONObject();
    try{
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      //_log.info("返回值："+receive);
      if("200".equals(receive.get("state"))){
          Object rawData = receive.get("info");
          ds.setRawData(rawData);
        }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  /**
   * 获取工商材料   获取之后与之前的合并
   * @param pSet企业类别0:公司1:分公司2:非公司企业法人3:非公司企业法人分支机构4:合伙企业5:合伙企业分支机构6:个人独资企业7:个人独资企业分支机构
   * @return
   */
  public DataSet getMaterialByCompanyType(ParameterSet pSet){
/*    DataSet ds = new DataSet();
    JSONObject jo = new JSONObject();
    String s = "{\"state\": \"200\",\"error\": \"当state为300时，提示错误信息\"," +
        "\"info\": [ " +
        "{\"itemId\":\"事项ID1\"," +
        "\"materialArray\":[{\"resourceCode\":\"材料编码2\",\"resourceType\":\"材料类型\",\"resourceName\":\"材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2材料名称2\"}" +
        "]}" +
        "]}";
    jo = JSONObject.fromObject(s);
    if("200".equals(jo.get("state"))){
      JSONArray itemArray = getApplicationInfo(pSet).getJOData().getJSONArray("itemArray");
      int len = itemArray.size();
      
      JSONArray info = jo.getJSONArray("info");
      int mlen = info.size();
      
      JSONArray rawData = new JSONArray();
      for(int i=0;i<len;i++){
        boolean bl = true;
        for(int j=0;j<mlen;j++){
          if((itemArray.getJSONObject(i).getString("itemId")).equals(info.getJSONObject(j).getString("itemId"))){
            JSONObject jinfo = info.getJSONObject(j);
            JSONObject jitemArray = itemArray.getJSONObject(i);
            jinfo.put("itemName", jitemArray.getString("itemName"));
            jinfo.put("resourceArray", jinfo.getJSONArray("materialArray"));
            rawData.add(jinfo);
            bl = false;
          }
        }
        if(bl){
          rawData.add(itemArray.getJSONObject(i));
          bl = true;
        }
      }
      ds.setRawData(rawData);
    }else{
      String error = (String)jo.get("error");
      ds.setMessage(error);
    }
    return ds;*/
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getMaterialByCompanyType");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("companyType", (String)pSet.get("companyType"));
    JSONObject receive = new JSONObject();
    //_log.info("调用获取工商接口："+url);
    //_log.info("调用接口参数companyType："+pSet.get("companyType"));
    try{
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      //_log.info("返回值："+receive);
      if("200".equals(receive.get("state"))){
          JSONArray itemArray = getApplicationInfo(pSet).getJOData().getJSONArray("itemArray");
          int len = itemArray.size();
          
          JSONArray info = receive.getJSONArray("info");
          int mlen = info.size();
        JSONArray rawData = new JSONArray();
        for(int i=0;i<len;i++){
          boolean bl = true;
          for(int j=0;j<mlen;j++){
            if((itemArray.getJSONObject(i).getString("itemId")).equals(info.getJSONObject(j).getString("itemId"))){
              JSONObject jinfo = info.getJSONObject(j);
              JSONObject jitemArray = itemArray.getJSONObject(i);
              jinfo.put("itemName", jitemArray.getString("itemName"));
              jinfo.put("resourceArray", jinfo.getJSONArray("materialArray"));
              rawData.add(jinfo);
              bl = false;
            }
          }
          if(bl){
            rawData.add(itemArray.getJSONObject(i));
            bl = true;
          }
        }
        ds.setRawData(rawData);
      }
    }catch(Exception e){
      String error = (String)receive.get("error");
      ds.setMessage(error);
    }
    return ds;
  }
  /**
   * 提交并联审批
   * @param pSet
   * @return
   */
  public DataSet submitApplicationInfo(ParameterSet pSet){
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/submitApplicationInfo");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("dataId", (String)pSet.get("dataId"));
    data.put("flowId", (String)pSet.get("flowId"));
    data.put("material", pSet.get("material").toString());
    JSONObject receive = new JSONObject();
    try{
      //_log.info("提交material："+pSet.get("material").toString());
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      //_log.info("提交返回值："+receive);
      if(receive.get("state").equals("200")){
          //这里的错误通常是dcid没有获取到。
      	Object dcId = "";
	    if("zs".equals(SecurityConfig.getString("AppId"))){
    	    dcId = receive.get("bizId");
        }else{
        	dcId = receive.get("dcId");
        }
        pSet.setParameter("dcId", dcId);
        DataSet _ds = saveEnterprise_business_index(pSet);
        if(_ds.getState()==1){
          ds.setState(StateType.SUCCESS);
          ds.setMessage(_ds.getMessage());
        }else{
          ds.setState(StateType.SUCCESS);
          ds.setMessage("提交成功，写入数据库失败");
        }        
      }
    }catch(Exception e){
      ds.setState(StateType.FAILT);
      ds.setMessage((String)receive.get("error"));
    }
    return ds;
  }
  /**
   *1  ID  VARCHAR2(50)  N      
  2  BIZ_ID  VARCHAR2(50)  N      实例ID,对应ENTERPRISE_BUSINESS_INDEX中的ID
  3  ITEM_ID  VARCHAR2(50)  N      
  4  ITEM_CODE  VARCHAR2(50)  Y      
  5  ITEM_NAME  VARCHAR2(200)  Y      
  6  ORG_CODE  VARCHAR2(50)  Y      
  7  ORG_NAME  VARCHAR2(50)  Y      
  8  RECEIVE_TIME  DATE  Y      
  9  FINISH_TIME  DATE  Y      
  0  PARALLEL_STAGE_CODE  VARCHAR2(20)  N      
  11  HANDLE_STATE  VARCHAR2(10)  N      "办理状态：未办、在办[已分发]、已办    "
  12  REGION_CODE  VARCHAR2(50)  Y      
  13  REGION_NAME  VARCHAR2(50)  Y      
  14  SECOND_GRADE_CODE  VARCHAR2(50)  Y      二级码
  15  FIRST_GRADE_CODE  VARCHAR2(50)  Y      一级码
   * @param pSet
   * @return
   */
  public DataSet saveEnterprise_business_course(ParameterSet pSet){
    String sql="insert into enterprise_business_course t (ID,BIZ_ID,ITEM_ID,ITEM_CODE,ITEM_NAME,ORG_CODE,ORG_NAME," +
        "RECEIVE_TIME,FINISH_TIME,PARALLEL_STAGE_CODE,HANDLE_STATE,REGION_CODE,REGION_NAME,SECOND_GRADE_CODE," +
        "FIRST_GRADE_CODE,IS_LHTK) values  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    DataSet ds = new DataSet();
    
    String IS_LHTK = (String)pSet.get("IS_LHTK");
    
    try{
      DataSet _ds = new DataSet();
      if("zs".equals(SecurityConfig.getString("AppId"))){
    	  _ds = zs_getApplicationInfo(pSet);
      }else{
    	  _ds = getApplicationInfo(pSet);
      }
      if(_ds.getState()==1){
        JSONArray ja = _ds.getJOData().getJSONArray("itemArray");
        int len = ja.size();
        for(int i=0;i<len;i++){
          String ID = Tools.getUUID32();
          int j = this.executeUpdate(sql, new Object[]{
              ID,
              pSet.get("BIZ_ID"),
              ja.getJSONObject(i).get("itemId"),
              ja.getJSONObject(i).get("itemId"),
              ja.getJSONObject(i).get("itemName"),
              "",
              "",
              CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
              null,
              "",
              "10",//办理状态：未办10、在办11[已分发]、已办99 
              SecurityConfig.getString("WebRegion"),
              "",
              "",
              "",
              0
          });
          if(j>0){
            ds.setState(StateType.SUCCESS);
            ds.setMessage("");
          }else{
            ds.setState(StateType.FAILT);
            ds.setMessage("事项信息写入数据库失败");
          }
        }
        
        if("1".equals(IS_LHTK)){
        	String ID = Tools.getUUID32();
            int k = this.executeUpdate(sql, new Object[]{
                ID,
                pSet.get("BIZ_ID"),
                "lianhetakan",
                "lianhetakan",
                "联合踏勘",
                "",
                "",
                CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
                null,
                "",
                "10",//办理状态：未办10、在办11[已分发]、已办99 
                SecurityConfig.getString("WebRegion"),
                "",
                "",
                "",
                IS_LHTK
            });
            
            if(k>0){
                ds.setState(StateType.SUCCESS);
                ds.setMessage("");
              }else{
                ds.setState(StateType.FAILT);
                ds.setMessage("联合踏勘事项信息写入数据库失败");
              }
        }
        
      }else{
        ds.setState(StateType.FAILT);
        ds.setMessage("获取事项信息失败");
      }      
    }catch(Exception e){
      ds.setState(StateType.FAILT);
      ds.setMessage("");
    }
    return ds;
  }
  /**
   *1  ID  VARCHAR2(50)  N      主键
  2  RECEIVE_ID  VARCHAR2(50)  N      申办编号
  3  BUSINESS_LICENSE  VARCHAR2(50)  Y      工商注册号
  4  APPLY_SUBJECT  VARCHAR2(200)  N      申办主题
  5  FORM_ID  VARCHAR2(100)  N      表单id
  6  FORM_NAME  VARCHAR2(200)  N      表单名称
  7  DATA_ID  VARCHAR2(50)  N      表单数据id
  8  REGION_CODE  VARCHAR2(50)  N      区划代码
  9  REGION_NAME  VARCHAR2(50)  N      区划名称
  0  APPLY_TIME  DATE  N      申请时间
  11  ACCEPT_TIME  DATE  Y      受理时间
  12  ACTUAL_FINISH_TIME  DATE  Y      实际办结时间
  13  APPLICANT  VARCHAR2(100)  Y      申请人
  14  ENTERPRISE_TYPE  VARCHAR2(10)  N      "企业申报类型
                          1注册登记(内资)
                          2注册登记(外资)
                          3全流程注册登记(内资)
                          4全流程注册登记(外资)  "
  15  ORG_ACTUALITY  VARCHAR2(20)  N      "业务类型Register( 设立 ) Change( 变更 ) Deregister( 注销 ) Revoke (吊销)"
  16  CURRENT_STATE  VARCHAR2(20)  N      当前办理状态 00暂存  11 提交
  17  FIRST_GRADE_CODE  VARCHAR2(50)  Y      一级码
  18  UCID  VARCHAR2(50)  Y      申请人id
  19  APPLY_DATA  CLOB  Y      申报数据

   * @param pSet
   * @return
   */
  public DataSet saveEnterprise_business_index(ParameterSet pSet){
    
    DataSet ds = new DataSet();
    String sql=null;
    String ID=null;  //enterprise_business_index表中ID
    int i=0;  //记录操作数据库受影响行数
    String tag=(String) pSet.get("ID_index");
    if(!"no".equals(tag)){  //如果不为no，证明数据已经暂存或提交过
	      ID=(String) pSet.get("ID_index");
	      try{// ,APPLY_DATA=?
		      sql="update enterprise_business_index set DATA_ID=?, CURRENT_STATE=?,APPLY_DATA=? where ID=?";
		      i=this.executeUpdate(sql, new Object[]{
		          pSet.get("dataId"),  //表单数据id
		          //pSet.get("material").toString(),  //上传材料信息
		          pSet.get("state"),    //状态，01为提交。
		          pSet.get("material").toString(),
		          ID});
	      }catch(Exception e){
	        ds.setState(StateType.FAILT);
	        ds.setMessage("");
	      }
     }else{
		      sql="insert into enterprise_business_index t (ID,RECEIVE_ID,BUSINESS_LICENSE,APPLY_SUBJECT,FORM_ID," +
		          "FORM_NAME,DATA_ID,REGION_CODE,REGION_NAME,APPLY_TIME,ACCEPT_TIME,ACTUAL_FINISH_TIME,APPLICANT,ENTERPRISE_TYPE," +
		          "ORG_ACTUALITY,CURRENT_STATE,FIRST_GRADE_CODE,UCID,APPLY_DATA) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    
      		ID = Tools.getUUID32();
		    try{
		          i = this.executeUpdate(sql, new Object[]{
		          ID,
		          pSet.get("dcId"),
		          "",
		          pSet.get("APPLY_SUBJECT"),
		          pSet.get("formId"),
		          "",
		          pSet.get("dataId"),
		          "",
		          "",
		          CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
		          null,
		          null,
		          "",
		          "1",
		          "Register",
		            //"01",//当前办理状态 00暂存  01 提交  02预审通过  99  办结 ：改为如下
		          pSet.get("state"),
		          "",
		          pSet.get("uid"),
		          pSet.get("material").toString()
		      });
		    }catch(Exception e){
		      ds.setState(StateType.FAILT);
		      ds.setMessage("");
		    }
     	}
      if(i>0){
        try{
          if(tag.equals("no")){
            ParameterSet _pSet = new ParameterSet();
            _pSet.setParameter("enterpriseType", pSet.get("enterpriseType"));
            _pSet.setParameter("IS_LHTK", pSet.get("IS_LHTK"));
            //_pSet.setParameter("FIRST_GRADE_CODE", pSet.get("FIRST_GRADE_CODE"));
            _pSet.setParameter("BIZ_ID", ID);
            _pSet.setParameter("flowId", pSet.get("flowId"));
            DataSet _ds = saveEnterprise_business_course(_pSet);
            if(_ds.getState()==1){
              ds.setState(StateType.SUCCESS);
            }else{
              ds.setState(StateType.FAILT);
              ds.setMessage("");
            }
          }
        }catch(Exception e){
            ds.setState(StateType.FAILT);
            ds.setMessage("");
        }
      }else{
        ds.setState(StateType.FAILT);
        ds.setMessage("");
      }
      ds.setData(ID.getBytes());
      return ds;
  }
  /**
   *1  ID  VARCHAR2(50)  N      主键 
  2  RECEIVE_ID  VARCHAR2(50)  N      申办编号
  3  BUSINESS_LICENSE  VARCHAR2(50)  Y      工商注册号
  4  APPLY_SUBJECT  VARCHAR2(200)  N      申办主题
  5  FORM_ID  VARCHAR2(100)  N      表单id
  6  FORM_NAME  VARCHAR2(200)  N      表单名称
  7  DATA_ID  VARCHAR2(50)  N      表单数据id
  8  REGION_CODE  VARCHAR2(50)  N      区划代码
  9  REGION_NAME  VARCHAR2(50)  N      区划名称
  0  APPLY_TIME  DATE  N      申请时间
  11  ACCEPT_TIME  DATE  Y      受理时间
  12  ACTUAL_FINISH_TIME  DATE  Y      实际办结时间
  13  APPLICANT  VARCHAR2(100)  Y      申请人
  14  ENTERPRISE_TYPE  VARCHAR2(10)  N      "企业申报类型
                          1注册登记(内资)
                          2注册登记(外资)
                          3全流程注册登记(内资)
                          4全流程注册登记(外资)  "
  15  ORG_ACTUALITY  VARCHAR2(20)  N      "业务类型Register( 设立 ) Change( 变更 ) Deregister( 注销 ) Revoke (吊销)"
  16  CURRENT_STATE  VARCHAR2(20)  N      当前办理状态 00暂存  11 提交
  17  FIRST_GRADE_CODE  VARCHAR2(50)  Y      一级码
  18  UCID  VARCHAR2(50)  Y      申请人id
  19  APPLY_DATA  CLOB  Y      申报数据
*/
  //用于在表单页面存入少量数据。
  public DataSet fist_saveEnterprise_business_index(ParameterSet pSet) {
    DataSet ds = new DataSet();    
    String sql=null;
    String ID=null;  //enterprise_business_index表中ID
    int i=0;  //记录操作数据库受影响行数
    String tag=(String) pSet.get("ID_index");
    if(!tag.equals("no"))
    {  //如果不为空，证明数据已经暂存或提交过。
      ID=(String) pSet.get("ID_index");
      try{
      sql="update enterprise_business_index set DATA_ID=? where ID=?";
      i=this.executeUpdate(sql, new Object[]{
          pSet.get("dataId"),  //表单数据id
          ID});
      }catch(Exception e){
        ds.setState(StateType.FAILT);
        ds.setMessage("");
    }
    }else{
      sql="insert into enterprise_business_index t (ID,RECEIVE_ID,BUSINESS_LICENSE,APPLY_SUBJECT,FORM_ID," +
          "FORM_NAME,DATA_ID,REGION_CODE,REGION_NAME,APPLY_TIME,ACCEPT_TIME,ACTUAL_FINISH_TIME,APPLICANT,ENTERPRISE_TYPE," +
          "ORG_ACTUALITY,CURRENT_STATE,FIRST_GRADE_CODE,UCID,APPLY_DATA) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
      ID = Tools.getUUID32();
    try{
      //String dd=(String) pSet.get("dataId");
          i = this.executeUpdate(sql, new Object[]{
          ID,
          pSet.get("dcId"),
          "",
          pSet.get("APPLY_SUBJECT"),
          pSet.get("formId"),
          "",
          pSet.get("dataId"),
          "",
          "",
          CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
          null,
          null,
          "",
          "1",
          "Register",
            //"01",//当前办理状态 00暂存  01 提交  02预审通过  99  办结 ：改为如下
          "00",
          "",
          pSet.get("uid"),
          ""
      });
    }catch(Exception e){
      ds.setState(StateType.FAILT);
      ds.setMessage("");
    }
    }
    if (i > 0) {
      //为了保证不把数据重复写入course表中。只在第一次存库时写入。之后将不再重复
      if (tag.equals("no")) {
        ParameterSet _pSet = new ParameterSet();
        _pSet.setParameter("enterpriseType", pSet.get("enterpriseType"));
        // _pSet.setParameter("FIRST_GRADE_CODE",
        // pSet.get("FIRST_GRADE_CODE"));
        _pSet.setParameter("BIZ_ID", ID);
        DataSet _ds = saveEnterprise_business_course(_pSet);
        if (_ds.getState() == 1) {
          ds.setState(StateType.SUCCESS);
          ds.setData(ID.getBytes());
        } else {
          ds.setState(StateType.FAILT);
          ds.setMessage("");
        }
      } else {
        ds.setState(StateType.SUCCESS);
        ds.setData(ID.getBytes());
      }
    } else {
      ds.setState(StateType.FAILT);
      ds.setMessage("");
    }
    return ds;
  }

  //获取上传附近存于数据库中的信息。
  public DataSet getResourceList(ParameterSet pSet) {
      DataSet ds=new DataSet();
      String sql="select APPLY_DATA from ENTERPRISE_BUSINESS_INDEX";
      try{
        sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
        ds = this.executeDataset(sql);
      }catch(Exception e){
        ds.setMessage("ERROR!");
      }
    return ds;
  }
  
  	//联合踏勘事项查询
	public DataSet queryLhtkById(ParameterSet pset) {
		
		String sql = "SELECT t.ITEM_NAME,t.ITEM_ID,t.BIZ_ID,t.MATERIAL,t.APPLY_DATA FROM ENTERPRISE_BUSINESS_COURSE t left outer join ENTERPRISE_BUSINESS_INDEX b on b.ID = t.BIZ_ID  WHERE t.is_lhtk ='1' and 1=1 ";
		DataSet ds = new DataSet();
		try {
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			int start = pset.getPageStart();
			int limit = pset.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	
	//联合踏勘通知查询
	public DataSet getTaKanMessage(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"getTaKanMessage");
		Map<String, String> data=new HashMap<String, String>();;
		data.put("bizId", (String)pset.getParameter("bizId"));
		try {
			Object obj =  RestUtil.postData(url, data);
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ds;
	}
	
	//联合踏勘会签意见查询
	public DataSet getTaKanSign(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"getTaKanSign");
		Map<String, String> data=new HashMap<String, String>();;
		data.put("bizId", (String)pset.getParameter("bizId"));
		try {
			Object obj =  RestUtil.postData(url, data);
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ds;
	}
	
	//一次性告知
	public DataSet getTaKanItem2(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"getTaKanItem2");
		Map<String, String> data=new HashMap<String, String>();;
		data.put("bizId", (String)pset.getParameter("bizId"));
		try {
			Object obj =  RestUtil.postData(url, data);
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	
	//获取上传附近存于数据库中的信息。
	  public DataSet getLhtkResourceList(ParameterSet pSet) {
	      DataSet ds=new DataSet();
	      String sql="select APPLY_DATA from ENTERPRISE_BUSINESS_COURSE ";
	      try{
	        sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
	        ds = this.executeDataset(sql);
	      }catch(Exception e){
	        ds.setMessage("ERROR!");
	      }
	    return ds;
	  }
  
  /**
   * 提交并联审批
   * @param pSet
   * @return
   */
  public DataSet submitLhtkInfo(ParameterSet pSet){
    DataSet ds = new DataSet();
    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"submitResourceObjectInfo");
    Map<String, String> data=new HashMap<String, String>();;
    data.put("bizId", (String)pSet.get("bizId"));
    data.put("Material", pSet.get("material").toString());
    JSONObject receive = new JSONObject();
    try{
      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
      //_log.info("提交返回值："+receive);
      if(receive.get("state").equals("200")){
          //这里的错误通常是dcid没有获取到。
          
        DataSet _ds = saveEnterprise_lhtk_course(pSet);
        if(_ds.getState()==1){
          ds.setState(StateType.SUCCESS);
          ds.setMessage(_ds.getMessage());
        }else{
          ds.setState(StateType.SUCCESS);
          ds.setMessage("提交成功，写入数据库失败");
        }        
      }
    }catch(Exception e){
      ds.setState(StateType.FAILT);
      ds.setMessage((String)receive.get("error"));
    }
    return ds;
  }
  
  public DataSet saveEnterprise_lhtk_course(ParameterSet pSet){
    
	  DataSet ds = new DataSet();
	  String sql = null;
	  String BIZ_ID = null;
	  String itemID = null;//enterprise_business_course表中BIZID
	  
      BIZ_ID = (String) pSet.get("bizId");
      JSONArray array = JSONArray.fromObject(pSet.get("material"));
      
      itemID = array.getJSONObject(0).getString("itemId");
      
      try{
	      sql=" update enterprise_business_course set APPLY_DATA=? where BIZ_ID=? and ITEM_ID=?";
	      this.executeUpdate(sql, new Object[]{
	    		  pSet.get("material").toString(),
	    		  BIZ_ID,  //表单数据id
	    		  itemID});
	      
	      ds.setMessage("提交成功！");
	      }catch(Exception e){
	        ds.setState(StateType.FAILT);
	        ds.setMessage("");
      }
      ds.setData(BIZ_ID.getBytes());
      return ds;
  }

  	/**
	 * 联办进度查询
	 * @author liuyq
	 * @param pSet  acceptanceNum：受理面好   queryPsw：查询密码
	 * @return
	 */
	public DataSet queryProgress(ParameterSet pSet) {
	    DataSet ds = new DataSet();
	    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getBusinessState");
	    Map<String, String> data=new HashMap<String, String>();;
	    data.put("approveNum", (String)pSet.get("acceptanceNum"));
	    data.put("password", (String)pSet.get("queryPsw"));
	    JSONObject receive = new JSONObject();
	    try{
	      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
	      //_log.info("返回值："+receive);
	      if("200".equals(receive.get("state"))){
		        //Object rawData = receive.get("info");
		        ds.setRawData(receive);
		        ds.setTotal(1);
	      }else if("400".equals(receive.get("state"))){
	    	  String error = (String)receive.get("error");
		      ds.setMessage(error);
	      }
	    }catch(Exception e){
	      String error = (String)receive.get("error");
	      ds.setMessage(error);
	    }
	    return ds;
	}

	/**
	 * 办件公示
	 * @param pSet regionCode   区划编码 
	 * @return
	 */
	public DataSet getPublicity(ParameterSet pSet, UserInfo ui) {
		
		DataSet ds = new DataSet();
	    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"getPublicity");
	    Map<String, String> data=new HashMap<String, String>();;
	    data.put("regionCode", SecurityConfig.getString("WebRegion"));
	    JSONObject receive = new JSONObject();
	    try{
	      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
	      //_log.info("返回值："+receive);
	      if("200".equals(receive.get("state"))){
		        Object rawData = receive.get("opinion");
		        ds.setRawData(rawData);
	      }else{
	      	String error = (String)receive.get("error");
	      	ds.setMessage(error);
	      }
	    }catch(Exception e){
	      String error = (String)receive.get("error");
	      ds.setMessage(error);
	    }
	    return ds;
	}

	/**
	 * 区划办件统计
	 * @param pSet ui-->regionCode   区划编码 
	 * @return
	 */
	public DataSet getRegionBusiness(ParameterSet pSet) {		
		
		DataSet ds = new DataSet();
	    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getRegionBusiness");
	    Map<String, String> data=new HashMap<String, String>();;
	    data.put("time", (String)pSet.get("time"));
	    data.put("region", SecurityConfig.getString("WebRegion"));
	    data.put("type", (String)pSet.get("type"));
	    JSONObject receive = new JSONObject();
	    try{
	      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
	      //_log.info("返回值："+receive);
	      if("200".equals(receive.get("state"))){
		        Object rawData = receive.get("opinion");
		        ds.setRawData(rawData);
	      }else{
	      	String error = (String)receive.get("error");
	      	ds.setMessage(error);
	      }
	    }catch(Exception e){
	      String error = (String)receive.get("error");
	      ds.setMessage(error);
	    }
	    return ds;
	}
	/**
	 * 获取业务规则列表
	 * @param pSet
	 * @return
	 */
	
	public DataSet getFlow(ParameterSet pSet){
		DataSet ds = new DataSet();
	    String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl")+"/getFlow");
	    Map<String, String> data=new HashMap<String, String>();
	    
	    data.put("regionCode", SecurityConfig.getString("WebRegion"));    
	    JSONObject receive = new JSONObject();
	    try{
	      //_log.info("调用获取表单id及材料接口前："+url);
	      receive =  JSONObject.fromObject(RestUtil.postData(url, data));
	      //_log.info("返回值："+receive);
	      if("200".equals(receive.get("state"))){
	          Object rawData = receive.get("info");
	          ds.setRawData(rawData);
	        }
	    }catch(Exception e){
	      String error = (String)receive.get("error");
	      ds.setMessage(error);
	    }
	    return ds;
	}
}
