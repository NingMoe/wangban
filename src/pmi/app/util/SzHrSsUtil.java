package app.util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.StateType;
import com.icore.util.StringUtil;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import exchange.adapter.base.IRequest;
import exchange.adapter.currency.AdapterComposite;
import exchange.adapter.currency.ResResponse;
import exchange.entity.apprequest.Condition;
import exchange.entity.apprequest.ConditionObject;
import exchange.entity.apprequest.Conditions;
import exchange.entity.apprequest.ResIndex;
import exchange.entity.apprequest.ResIndexs;
import exchange.entity.businessdata.BusinessData;
import exchange.entity.businessdata.RecordData;
import exchange.entity.businessdata.UnitData;

/**
 * 深圳人力资源和社会保障局接口工具类 目前包含以下4个接口 
 * 1、个人参保信息查询接口 
 * 2、个人缴费记录明细 
 * 3、单位参保信息 
 * 4、单位当前正常参保人员清单接口
 * 5、新参保单位清单接口
 * 
 */
public class SzHrSsUtil {
	// 访问类型 APPREQUEST_FETCHRESDATA 结构化资源获取
	private final static String REQUEST_TYPE = "APPREQUEST_FETCHRESDATA";
	private final static String ERROR_MESSAGE = "ERROR_MESSAGE";
	// 应用系统ID
	private final static String APP_ID = "29105A2APPSZ0120131014151427090172465";
	// 接口地址，即资源中心开放给网厅的前置机
	private final static String WSDL_ADDRESS = "203.175.146.154:8080";

	private static IRequest oRequest = null;
	private static ConditionObject personConditionObj = null;
	private static ConditionObject personDetailConditionObj = null;
	private static ConditionObject orgConditionObj = null;
	private static ConditionObject orgPersonConditionObj = null;

	static {
		initRequest();
		initPersonConditionObj();
		initPersonDetailConditionObj();
		initOrgConditionObj();
		initOrgPersonConditionObj();
	}

	/**
	 * 1、个人参保信息查询接口 获取的指标
	 * @param pSet
	 * @return
	 */
	public static DataSet person(ParameterSet pSet) {
		DataSet resultDs = new DataSet();

		Conditions oConditions = new Conditions();// 创建Conditions节点，此节点表示所有查询指标项信息
		Condition oCondition = new Condition(); // 创建Condition节点，此节点表示单个查询指标项信息
		// 参保人员身份证号
		String id_no = checkData("ID_NO",pSet,resultDs);
		if(StringUtil.isEmpty(id_no)) return resultDs;
		oCondition.setResIndexName("ID_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(id_no);// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 参保人员姓名
		String name = checkData("NAME",pSet,resultDs);
		if(StringUtil.isEmpty(name)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("NAME");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(name);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 参保人员社保电脑号
		String s_no = checkData("S_NO",pSet,resultDs);
		if(StringUtil.isEmpty(s_no)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("S_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(s_no);// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 交互码 （固定值，每个接口一个）
		oCondition = new Condition();
		oCondition.setResIndexName("PROCESS_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("IF01_PERSON");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 系统代码
		oCondition = new Condition();
		oCondition.setResIndexName("SYS_CODE");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("szzwfw");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 访问密码
		oCondition = new Condition();
		oCondition.setResIndexName("PASSWORD");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("98752834");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 查询用户
		oCondition = new Condition();
		oCondition.setResIndexName("QUERY_BY");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("wbdt");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 是否输出PDF报表
		oCondition = new Condition();
		oCondition.setResIndexName("REPORT_FLAG");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("N");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		personConditionObj.setConditions(oConditions);
		oRequest.setCondition(personConditionObj, APP_ID);

		resultDs = getResult(oRequest);
		return resultDs;
	}
	
	/**
	 * 2、个人缴费记录明细 获取的指标
	 * @param pSet
	 * @return
	 */
	public static DataSet personDetail(ParameterSet pSet) {
		DataSet resultDs = new DataSet();

		Conditions oConditions = new Conditions();// 创建Conditions节点，此节点表示所有查询指标项信息
		Condition oCondition = new Condition(); // 创建Condition节点，此节点表示单个查询指标项信息
		// 参保人员身份证号
		String id_no = checkData("ID_NO",pSet,resultDs);
		if(StringUtil.isEmpty(id_no)) return resultDs;
		oCondition.setResIndexName("ID_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(id_no);// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 参保人员姓名
		String name = checkData("NAME",pSet,resultDs);
		if(StringUtil.isEmpty(name)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("NAME");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(name);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 参保人员社保电脑号
		String s_no = checkData("S_NO",pSet,resultDs);
		if(StringUtil.isEmpty(s_no)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("S_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(s_no);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 查询开始年 FROM_YEAR
		String from_year = checkData("FROM_YEAR",pSet,resultDs);
		if(StringUtil.isEmpty(s_no)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("FROM_YEAR");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(from_year);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 查询开始月 FROM_MONTH
		String from_month = checkData("FROM_MONTH",pSet,resultDs);
		if(StringUtil.isEmpty(s_no)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("FROM_MONTH");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(from_month);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 查询开始年 TO_YEAR
		String to_year = checkData("TO_YEAR",pSet,resultDs);
		if(StringUtil.isEmpty(s_no)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("TO_YEAR");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(to_year);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 查询开始月 TO_MONTH
		String to_month = checkData("TO_MONTH",pSet,resultDs);
		if(StringUtil.isEmpty(s_no)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("TO_MONTH");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(to_month);// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 交互码 （固定值，每个接口一个）
		oCondition = new Condition();
		oCondition.setResIndexName("PROCESS_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("IF03_DETAIL");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 系统代码
		oCondition = new Condition();
		oCondition.setResIndexName("SYS_CODE");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("szzwfw");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 访问密码
		oCondition = new Condition();
		oCondition.setResIndexName("PASSWORD");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("98752834");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 查询用户
		oCondition = new Condition();
		oCondition.setResIndexName("QUERY_BY");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("wbdt");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 是否输出PDF报表
		oCondition = new Condition();
		oCondition.setResIndexName("REPORT_FLAG");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("N");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		personDetailConditionObj.setConditions(oConditions);
		oRequest.setCondition(personDetailConditionObj, APP_ID);

		resultDs = getResult(oRequest);
		return resultDs;
	}
	
	/**
	 * 3、单位参保信息
	 * @param pSet
	 * @return
	 */
	public static DataSet org(ParameterSet pSet){
		DataSet resultDs = new DataSet();

		Conditions oConditions = new Conditions();// 创建Conditions节点，此节点表示所有查询指标项信息
		Condition oCondition = new Condition(); // 创建Condition节点，此节点表示单个查询指标项信息
		// 单位社保编号
		String unit_sno = checkData("UNIT_SNO",pSet,resultDs);
		if(StringUtil.isEmpty(unit_sno)) return resultDs;
		oCondition.setResIndexName("UNIT_SNO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(unit_sno);// 设置RightValue节点
		oConditions.addCondition(oCondition);
		
		// 查询时间 6位
		String query_date = checkData("QUERY_DATE",pSet,resultDs);
		if(StringUtil.isEmpty(query_date)) return resultDs;
		oCondition = new Condition();
		oCondition.setResIndexName("QUERY_DATE");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(query_date);// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 交互码 （固定值，每个接口一个）
		oCondition = new Condition();
		oCondition.setResIndexName("PROCESS_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("IF02_UNIT");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 系统代码
		oCondition = new Condition();
		oCondition.setResIndexName("SYS_CODE");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("szzwfw");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 访问密码
		oCondition = new Condition();
		oCondition.setResIndexName("PASSWORD");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("98752834");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 查询用户
		oCondition = new Condition();
		oCondition.setResIndexName("QUERY_BY");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("wbdt");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 是否输出PDF报表
		oCondition = new Condition();
		oCondition.setResIndexName("REPORT_FLAG");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("N");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		orgConditionObj.setConditions(oConditions);
		oRequest.setCondition(orgConditionObj, APP_ID);

		resultDs = getResult(oRequest);
		return resultDs;
	}
	
	/**
	 * 4、单位当前正常参保人员清单接口
	 * @param pSet
	 * @return
	 */
	public static DataSet orgPerson(ParameterSet pSet){
		DataSet resultDs = new DataSet();

		Conditions oConditions = new Conditions();// 创建Conditions节点，此节点表示所有查询指标项信息
		Condition oCondition = new Condition(); // 创建Condition节点，此节点表示单个查询指标项信息
		// 单位社保编号
		String unit_sno = checkData("UNIT_SNO",pSet,resultDs);
		if(StringUtil.isEmpty(unit_sno)) return resultDs;
		oCondition.setResIndexName("UNIT_SNO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue(unit_sno);// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 交互码 （固定值，每个接口一个）
		oCondition = new Condition();
		oCondition.setResIndexName("PROCESS_NO");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("IF07_UNIT_PERSON_LIST");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 系统代码
		oCondition = new Condition();
		oCondition.setResIndexName("SYS_CODE");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("szzwfw");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 访问密码
		oCondition = new Condition();
		oCondition.setResIndexName("PASSWORD");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("98752834");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		// 查询用户
		oCondition = new Condition();
		oCondition.setResIndexName("QUERY_BY");// 设置ResIndexName节点
		oCondition.setOperation("=");// 设置Operation节点
		oCondition.setRightValue("wbdt");// 设置RightValue节点
		oConditions.addCondition(oCondition);

		orgPersonConditionObj.setConditions(oConditions);
		oRequest.setCondition(orgPersonConditionObj, APP_ID);

		resultDs = getResult(oRequest);
		return resultDs;
	}

	private static void initRequest() {
		AdapterComposite composite = new AdapterComposite(REQUEST_TYPE);
		oRequest = composite.createRequest(APP_ID, WSDL_ADDRESS);
	}

	private static void initPersonConditionObj() {
		Map<String, String> personIndexsMap = new HashMap();
		personIndexsMap.put("NAME", "NAME");
		personIndexsMap.put("ID_NO", "ID_NO");
		personIndexsMap.put("S_NO", "S_NO");
		personIndexsMap.put("STATUS", "STATUS");
		personIndexsMap.put("PERSON_IDENTITY", "PERSON_IDENTITY");
		personIndexsMap.put("BIRTHDAY", "BIRTHDAY");
		personIndexsMap.put("REPORT_FLAG", "REPORT_FLAG");
		personIndexsMap.put("PROCESS_NO", "PROCESS_NO");
		personIndexsMap.put("LAST_UNIT_SNO", "LAST_UNIT_SNO");
		personIndexsMap.put("GONGS_CTMS_START_DATE", "GONGS_CTMS_START_DATE");
		personIndexsMap.put("YUL_CTMS_START_DATE", "YUL_CTMS_START_DATE");
		personIndexsMap.put("GONGS_TMS", "GONGS_TMS");
		personIndexsMap.put("GONGS_CTMS", "GONGS_CTMS");
		personIndexsMap.put("SHIY_TMS", "SHIY_TMS");
		personIndexsMap.put("SHIY_CTMS_START_DATE", "SHIY_CTMS_START_DATE");
		personIndexsMap.put("SHENGY_TMS", "SHENGY_TMS");
		personIndexsMap.put("SHENGY_CTMS", "SHENGY_CTMS");
		personIndexsMap.put("PHONE", "PHONE");
		personIndexsMap.put("FIRST_DATE", "FIRST_DATE");
		personIndexsMap.put("SYS_CODE", "SYS_CODE");
		personIndexsMap.put("PASSWORD", "PASSWORD");
		personIndexsMap.put("SEX", "SEX");
		personIndexsMap.put("LAST_DATE", "LAST_DATE");
		personIndexsMap.put("YUL_TMS", "YUL_TMS");
		personIndexsMap.put("YUL_CTMS", "YUL_CTMS");
		personIndexsMap.put("YUL_CTMS_END_DATE", "YUL_CTMS_END_DATE");
		personIndexsMap.put("GONGS_CTMS_END_DATE", "GONGS_CTMS_END_DATE");
		personIndexsMap.put("SHIY_CTMS", "SHIY_CTMS");
		personIndexsMap.put("SHIY_CTMS_END_DATE", "SHIY_CTMS_END_DATE");
		personIndexsMap.put("SHENGY_CTMS_START_DATE", "SHENGY_CTMS_START_DATE");
		personIndexsMap.put("SHENGY_CTMS_END_DATE", "SHENGY_CTMS_END_DATE");
		personIndexsMap.put("PDF_STREAM", "PDF_STREAM");
		personIndexsMap.put("QUERY_BY", "QUERY_BY");
		personIndexsMap.put("LAST_UNIT_NAME", "LAST_UNIT_NAME");
		personIndexsMap.put("YANGL_TMS", "YANGL_TMS");
		personIndexsMap.put("YANGL_CTMS", "YANGL_CTMS");
		personIndexsMap.put("YANGL_CTMS_START_DATE", "YANGL_CTMS_START_DATE");
		personIndexsMap.put("YANGL_CTMS_END_DATE", "YANGL_CTMS_END_DATE");
		personIndexsMap.put("ERROR_CODE", "ERROR_CODE");
		personIndexsMap.put("ERROR_MESSAGE", "ERROR_MESSAGE");
		personIndexsMap.put("UNIT_KIND", "UNIT_KIND");
		personIndexsMap.put("AREA_NUMBER", "AREA_NUMBER");
		personIndexsMap.put("RETIRE_DATE", "RETIRE_DATE");
		personIndexsMap.put("RETIRE_TYPE", "RETIRE_TYPE");
		personIndexsMap.put("HUKOU_PLACE_CODE", "HUKOU_PLACE_CODE");
		personIndexsMap.put("START_WORK_DATE", "START_WORK_DATE");
		personIndexsMap.put("STATUS_FLAG", "STATUS_FLAG");

		personConditionObj = new ConditionObject();
		personConditionObj.setResCode("201081203SZ0120140613104812934560087"); // 资源编号ID
		personConditionObj.setOrgId("SZ011213"); // 提供方单位ID
		personConditionObj.setResName("个人参保信息表接口"); // 资源名称
		personConditionObj.setIsAllSelect(false);// 是否获取全部获取指标
		personConditionObj.setResComment("个人参保信息表接口");// 设置ResComment节点

		// 相关资源指标项信息 可以有多个指标项
		ResIndexs oResIndexs = new ResIndexs();
		ResIndex oResIndex = null;
		for (Map.Entry<String, String> entry : personIndexsMap.entrySet()) {
			oResIndex = new ResIndex();
			oResIndex.setResIndexName(entry.getKey());
			oResIndex.setResInexCode(entry.getValue());
			oResIndexs.addResIndex(oResIndex);
		}
		personConditionObj.setResIndexs(oResIndexs);
	}

	private static void initPersonDetailConditionObj() {
		Map<String, String> personDetailIndexsMap = new HashMap();
		personDetailIndexsMap.put("ID_NO", "ID_NO");
		personDetailIndexsMap.put("TO_YEAR", "TO_YEAR");
		personDetailIndexsMap.put("SYS_CODE", "SYS_CODE");
		personDetailIndexsMap.put("FROM_MONTH", "FROM_MONTH");
		personDetailIndexsMap.put("PROCESS_NO", "PROCESS_NO");
		personDetailIndexsMap.put("PASSWORD", "PASSWORD");
		personDetailIndexsMap.put("QUERY_BY", "QUERY_BY");
		personDetailIndexsMap.put("REPORT_FLAG", "REPORT_FLAG");
		personDetailIndexsMap.put("FROM_YEAR", "FROM_YEAR");
		personDetailIndexsMap.put("TO_MONTH", "TO_MONTH");
		personDetailIndexsMap.put("NAME", "NAME");
		personDetailIndexsMap.put("PHONE", "PHONE");
		personDetailIndexsMap.put("YANGL_CTMS", "YANGL_CTMS");
		personDetailIndexsMap.put("YUL_TMS", "YUL_TMS");
		personDetailIndexsMap.put("YUL_CTMS", "YUL_CTMS");
		personDetailIndexsMap.put("GONGS_CTMS", "GONGS_CTMS");
		personDetailIndexsMap.put("SHIY_TMS", "SHIY_TMS");
		personDetailIndexsMap.put("SHIY_CTMS", "SHIY_CTMS");
		personDetailIndexsMap.put("SHENGY_TMS", "SHENGY_TMS");
		personDetailIndexsMap.put("PDF_STREAM", "PDF_STREAM");
		personDetailIndexsMap.put("RESULT", "RESULT");
		personDetailIndexsMap.put("ERROR_CODE", "ERROR_CODE");
		personDetailIndexsMap.put("SHENGY_CTMS", "SHENGY_CTMS");
		personDetailIndexsMap.put("UNIT_LIST", "UNIT_LIST");
		personDetailIndexsMap.put("S_NO", "S_NO");
		personDetailIndexsMap.put("SEX", "SEX");
		personDetailIndexsMap.put("FIRST_DATE", "FIRST_DATE");
		personDetailIndexsMap.put("YANGL_TMS", "YANGL_TMS");
		personDetailIndexsMap.put("GONGS_TMS", "GONGS_TMS");
		personDetailIndexsMap.put("ERROR_MESSAGE", "ERROR_MESSAGE");
		
		personDetailConditionObj = new ConditionObject();
		personDetailConditionObj.setResCode("201081203SZ0120140613104857550585746"); // 资源编号ID
		personDetailConditionObj.setOrgId("SZ011213"); // 提供方单位ID
		personDetailConditionObj.setResName("个人缴费记录明细表接口"); // 资源名称
		personDetailConditionObj.setIsAllSelect(false);// 是否获取全部获取指标
		personDetailConditionObj.setResComment("个人缴费记录明细表接口");// 设置ResComment节点

		// 相关资源指标项信息 可以有多个指标项
		ResIndexs oResIndexs = new ResIndexs();
		ResIndex oResIndex = null;
		for (Map.Entry<String, String> entry : personDetailIndexsMap.entrySet()) {
			oResIndex = new ResIndex();
			oResIndex.setResIndexName(entry.getKey());
			oResIndex.setResInexCode(entry.getValue());
			oResIndexs.addResIndex(oResIndex);
		}
		personDetailConditionObj.setResIndexs(oResIndexs);
	}
	
	private static void initOrgConditionObj() {
		Map<String, String> orgPersonIndexsMap = new HashMap();
		orgPersonIndexsMap.put("PROCESS_NO","PROCESS_NO");
		orgPersonIndexsMap.put("SYS_CODE","SYS_CODE");
		orgPersonIndexsMap.put("PASSWORD","PASSWORD");
		orgPersonIndexsMap.put("QUERY_BY","QUERY_BY");
		orgPersonIndexsMap.put("REPORT_FLAG","REPORT_FLAG");
		orgPersonIndexsMap.put("UNIT_SNO","UNIT_SNO");
		orgPersonIndexsMap.put("QUERY_DATE","QUERY_DATE");
		orgPersonIndexsMap.put("UNIT_NAME","UNIT_NAME");
		orgPersonIndexsMap.put("UNIT_ADDR","UNIT_ADDR");
		orgPersonIndexsMap.put("UNIT_ORGNO","UNIT_ORGNO");
		orgPersonIndexsMap.put("OPERATOR","OPERATOR");
		orgPersonIndexsMap.put("PHONE","PHONE");
		orgPersonIndexsMap.put("LEGAL","LEGAL");
		orgPersonIndexsMap.put("POST_CODE","POST_CODE");
		orgPersonIndexsMap.put("BANK_NO","BANK_NO");
		orgPersonIndexsMap.put("ACCT_NO","ACCT_NO");
		orgPersonIndexsMap.put("STATUS","STATUS");
		orgPersonIndexsMap.put("LAST_PAYDATE","LAST_PAYDATE");
		orgPersonIndexsMap.put("YANGL_TOTAL","YANGL_TOTAL");
		orgPersonIndexsMap.put("YULIAO_TOTAL","YULIAO_TOTAL");
		orgPersonIndexsMap.put("GONGS_TOTAL","GONGS_TOTAL");
		orgPersonIndexsMap.put("SHIY_TOTAL","SHIY_TOTAL");
		orgPersonIndexsMap.put("SHENGY_TOTAL","SHENGY_TOTAL");
		orgPersonIndexsMap.put("PDF_STREAM","PDF_STREAM");
		orgPersonIndexsMap.put("ERROR_CODE","ERROR_CODE");
		orgPersonIndexsMap.put("ERROR_MESSAGE","ERROR_MESSAGE");
		
		orgConditionObj = new ConditionObject();
		orgConditionObj.setResCode("201081203SZ01201403071138175124554800RFN"); // 资源编号ID
		orgConditionObj.setOrgId("SZ011213"); // 提供方单位ID
		orgConditionObj.setResName("单位参保信息表接口"); // 资源名称
		orgConditionObj.setIsAllSelect(false);// 是否获取全部获取指标
		orgConditionObj.setResComment("单位参保信息表接口");// 设置ResComment节点

		// 相关资源指标项信息 可以有多个指标项
		ResIndexs oResIndexs = new ResIndexs();
		ResIndex oResIndex = null;
		for (Map.Entry<String, String> entry : orgPersonIndexsMap.entrySet()) {
			oResIndex = new ResIndex();
			oResIndex.setResIndexName(entry.getKey());
			oResIndex.setResInexCode(entry.getValue());
			oResIndexs.addResIndex(oResIndex);
		}
		orgConditionObj.setResIndexs(oResIndexs);
	}
	
	private static void initOrgPersonConditionObj() {
		Map<String, String> orgPersonIndexsMap = new HashMap();
		orgPersonIndexsMap.put("SYS_CODE","SYS_CODE"); 
		orgPersonIndexsMap.put("PASSWORD","PASSWORD"); 
		orgPersonIndexsMap.put("ERROR_MESSAGE","ERROR_MESSAGE"); 
		orgPersonIndexsMap.put("QUERY_BY","QUERY_BY"); 
		orgPersonIndexsMap.put("UNIT_SNO","UNIT_SNO"); 
		orgPersonIndexsMap.put("ERROR_CODE","ERROR_CODE"); 
		orgPersonIndexsMap.put("PROCESS_NO","PROCESS_NO"); 
		orgPersonIndexsMap.put("ID_NO","ID_NO"); 
		orgPersonIndexsMap.put("NAME","NAME"); 
		orgPersonIndexsMap.put("S_NO","S_NO");
		
		orgPersonConditionObj = new ConditionObject();
		orgPersonConditionObj.setResCode("201081203SZ0120140613104736554700373"); // 资源编号ID
		orgPersonConditionObj.setOrgId("SZ011213"); // 提供方单位ID
		orgPersonConditionObj.setResName("单位当前正常参保人员清单接口"); // 资源名称
		orgPersonConditionObj.setIsAllSelect(false);// 是否获取全部获取指标
		orgPersonConditionObj.setResComment("单位当前正常参保人员清单接口");// 设置ResComment节点

		// 相关资源指标项信息 可以有多个指标项
		ResIndexs oResIndexs = new ResIndexs();
		ResIndex oResIndex = null;
		for (Map.Entry<String, String> entry : orgPersonIndexsMap.entrySet()) {
			oResIndex = new ResIndex();
			oResIndex.setResIndexName(entry.getKey());
			oResIndex.setResInexCode(entry.getValue());
			oResIndexs.addResIndex(oResIndex);
		}
		orgPersonConditionObj.setResIndexs(oResIndexs);
	}
	
	public static String checkData(String code, ParameterSet pSet, DataSet dsResult) {
		String value = (String) pSet.get(code);
		if (!StringUtils.isNotEmpty(value)) {
			dsResult.setState(StateType.FAILT);
			dsResult.setMessage("请传递" + code + "参数！");
			return "";
		}
		return value;
	}
	
	
	/**
	 * 加工xml格式的多结果集
<?xml version="1.0" encoding="UTF-8"?>
<DataSet>
	<DataRow>
		<DataCol>
			<ResIndex>UNIT_NAME</ResIndex>
			<Value>值</Value>
		</DataCol>
		<DataCol>
			<ResIndex>UNIT_NO</ResIndex>
			<Value>值</Value>
		</DataCol>
	</DataRow>
</DataSet>
	 * @param xmlStr
	 * @return
	 * @throws DocumentException 
	 */
	private static JSONArray formatXlist(String xmlStr) throws DocumentException{
		if(StringUtil.isEmpty(xmlStr) || "null".equalsIgnoreCase(xmlStr)) return null;
		JSONArray ja = new JSONArray();
		JSONObject jo = null;
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new ByteArrayInputStream(xmlStr.getBytes()));
		List<Node> rowList = doc.selectNodes("//DataRow");
		List<Node> colList = null;
		for(Node rowNode : rowList){
			colList = rowNode.selectNodes("DataCol");
			jo = new JSONObject();
			for(Node colNode : colList){
				jo.put(colNode.selectSingleNode("ResIndex").getText(),colNode.selectSingleNode("Value").getText().trim());
			}
			ja.add(jo);
		}
		return ja;
	}
	
	private static DataSet getResult(IRequest request){
		DataSet resultDs = new DataSet();
		try {
			// 开始发送获取请求
			ResResponse rsbase = (ResResponse) request.request();
			// 获取对象类型，获取请求成功返回有两种类型：1.ErrorMessage错误信息类型 2.ResResult获取的结果集类型
			String sBodyType = rsbase.getOBodyType();
			if (sBodyType == null) {
				throw new Exception("返回字符串为空，传输的对象有错误");
			}
			// 若返回ErrorMessage错误信息类型
			if (sBodyType.equals("ErrorMessage")) {
				// * 则可以输出相关错误信息，错误信息为：错误编号+错误内容
				// * getErrorInfo()只获取错误内容
				// * getErrorNum()只获取错误编号
				throw new Exception(rsbase.getError());
			} else {
				// 获取国标对象BusinessData
				BusinessData bd = rsbase.getOBusinessData();
				// 获取结果集
				exchange.entity.businessdata.DataSet ds = bd.getDataSet();
				// 开始遍历结果集进行展示
				RecordData[] rds = ds.getRecordData();
				UnitData[] ud = null;
				JSONArray ja = new JSONArray();
				JSONObject jo = null;
				// 遍历获取的每行记录
				for (int i = 0; i < rds.length; i++) {
					jo = new JSONObject();
					ud = rds[i].getUnitData();
					// 遍历获取的每行的每个字段信息
					for (int j = 0; j < ud.length; j++) {
						String englishName = ud[j].getUnitEnglishName();
						String stringValue = ((org.exolab.castor.types.AnyNode)ud[j].getUnitValue()).getStringValue();
						//测试发现查询失败时，sBodyType依然是ResResult【需自行捕获错误】ERROR_CODE（错误代码）、ERROR_MESSAGE（错误信息）
						if(ERROR_MESSAGE.equals(englishName) && StringUtil.isNotEmpty(stringValue) && !"null".equalsIgnoreCase(stringValue)){
							resultDs.setState(StateType.FAILT);
							resultDs.setMessage(stringValue);
							return resultDs;
						}
						if("RESULT".equalsIgnoreCase(englishName) || "UNIT_LIST".equalsIgnoreCase(englishName)){
							jo.put(englishName, formatXlist(stringValue));
						}else{
							jo.put(englishName, stringValue);
						}
					}
					ja.add(jo);
				}
				resultDs.setTotal(ja.size());
				resultDs.setRawData(ja);
			}
		} catch (Exception e) {
			resultDs.setState(StateType.FAILT);
			resultDs.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return resultDs;
	}
	

}
