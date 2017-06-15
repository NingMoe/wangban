package api;

import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Date;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import action.icity.intermediary.intermed;
import core.util.CommonUtils;
import net.sf.json.JSONObject;

@RestType(name = "api.intermediary", descript = "转发改委数据到通用审批和投资审批库")
public class intermediary extends BaseQueryCommand {
	//private static Logger log = LoggerFactory.getLogger(intermediary.class);

	@SuppressWarnings("unused")
	public DataSet insert(ParameterSet pSet){
		
		//项目有效标识 0无效；1有效
		String validity_flag = (String) pSet.get("validity_flag");
		//项目审核标识 0未审，1已审核
		String audit_flag = (String) pSet.get("audit_flag");
		
		String isuse = "";
		String auditflag = "";
		String status = "";
		
		/*
		 项目无效情况下：
			项目未审核情况下：
				审批系统：DC_PROJECT表IS_USE字段为0、AUDIT_FLAG字段为1；
				投资系统：APPROVE_INVEST_INDEX表STATUS字段为97。
			项目已审核情况下：
				审批系统：DC_PROJECT表IS_USE字段为0、AUDIT_FLAG字段为0；
				投资系统：APPROVE_INVEST_INDEX表STATUS字段为97。
		项目有效情况下
			项目未审核情况下：
				审批系统：DC_PROJECT表IS_USE字段为1、AUDIT_FLAG字段为1；
				投资系统：APPROVE_INVEST_INDEX表 STATUS 字段为00。
			项目已审核情况下：
				审批系统：DC_PROJECT表IS_USE字段为1、AUDIT_FLAG字段为0；
				投资系统：APPROVE_INVEST_INDEX表 STATUS 字段为99。
		*/
		if("0".equals(validity_flag)){
			if("0".equals(audit_flag)){
				isuse = "0";
				auditflag = "1";
				status = "97";
			}else if("1".equals(audit_flag)){
				isuse = "0";
				auditflag = "0";
				status = "97";
			}
		}else if("1".equals(validity_flag)){
			if("0".equals(audit_flag)){
				isuse = "1";
				auditflag = "1";
				status = "00";
			}else if("1".equals(audit_flag)){
				isuse = "1";
				auditflag = "0";
				status = "99";
			}
		}
		
		DataSet ds = new DataSet();
		String password_des = SecurityConfig.getString("DES_password");
		JSONObject obj = new JSONObject();
		if (null == pSet) {
			ds.setState(StateType.FAILT);
			ds.setMessage("error:data is null !");
			return ds;
		}
		String symbol = new intermed().strDec((String) pSet.get("sybmol"),password_des, "", "");
		if (!"inspur".equals(symbol)) {
			ds.setState(StateType.FAILT);
			ds.setMessage("error:nonlicet interlink !");
			return ds;
		}
		//log.info("投资项目数据推送已接收数据：pSet：" + pSet);
		String hylbbm = stringFill("A" + (String) pSet.get("hylbbm"), 6, 'X',false);
		Connection connt = DbHelper.getConnection("tyspDataSource");
		Connection conn = DbHelper.getConnection("tzspDataSource");
		String sql;
		int i=0;
		try {
			connt.setAutoCommit(false);
			conn.setAutoCommit(false);
			sql = "SELECT 1 FROM DC_PROJECT WHERE PROJECT_CODE = ?";
			int j = DbHelper.query(sql, new Object[] { (String) pSet.get("xmbm") },connt).getTotal();
			if (j == 0) {
				sql = "INSERT INTO DC_PROJECT(SEQ_ID,PROJECT_NAME,PROJECT_CODE,LOCATION,LINK_MAN,LINK_PHONE,"
						+ "PROJECT_CONTENT,AREA_ALL,AREA_BUILD,INVESTMENT,PROJECT_ALLOWED_NO,VERSION,IS_IMPORTANT,"
						+ "SCALE,CONSTRUCT_TYPE,CONSTRUCT_PER,MOBILE_PHONE,CONTRACTOR,IS_USE,DIVISION_CODE,PROJECT_TYPE,"
						+ "LEREP_CERTTYPE,LEREP_CERTNO,START_YEAR,END_YEAR,PLACE_CODE,PLACE_CODE_DETAIL,IS_DE_AREA,"
						+ "DE_AREA_NAME,INDUSTRY,APPLY_DATE,CONTACT_EMAIL,PERMIT_INDUSTRY,PROJECT_STAGE,PLACE_NAME,"
						+ "DIVISION_NAME,PERMIT_ITEM_CODE,PLACE_AREA_DETAIL,PROJECT_SORT,"
						+ "FOREIGN_ABROAD_FLAG,TOTAL_MONEY_EXPLAIN,THE_INDUSTRY,IS_COUNTRY_SECURITY,SECURITY_APPROVAL_NUMBER,INVESTMENT_MODE,"
						+ "TOTAL_MONEY_DOLLAR,TOTAL_MONEY_DOLLAR_RATE,PROJECT_CAPITAL_MONEY,PROJECT_CAPITAL_MONEY_DOLLAR,"
						+ "CAPITAL_MONEY_DOLLAR_RATE,INDUSTRIAL_POLICY_TYPE,INDUSTRIAL_POLICY,OTHER_INVESTMENT_APPLY_INFO, "
						+ "TRANSACTION_BOTH_INFO,MERGER_PLAN,MERGER_MANAGEMENT_MODE_SCOPE,GET_LAND_MODE,LAND_AREA,BUILT_AREA,"
						+ "IS_ADD_DEVICE,IMPORT_DEVICE_NUMBER_MONEY,PROJECT_SITE,CHINA_TOTAL_MONEY,AUDIT_FLAG) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				i = DbHelper.update(sql,
						new Object[] {
								Tools.getUUID32(),
								URLDecoder.decode((String) pSet.get("xmmc"),"UTF-8"),
								(String) pSet.get("xmbm"),
								URLDecoder.decode((String) pSet.get("jsdz"),"UTF-8"),
								(String) pSet.get("xmfzrxm"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("jsnr"),"UTF-8"),
								(String) pSet.get("area_all"),
								(String) pSet.get("area_build"),
								(String) pSet.get("ztz"),
								(String) pSet.get("project_allowed_no"),
								1,// Double.parseDouble((String)
									// pSet.get("VERSION")),
								(String) pSet.get("is_important"),
								(String) pSet.get("scale"),
								(String) pSet.get("construct_type"),
								(String) pSet.get("jsxz"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("dwmc"),"UTF-8"),
								isuse,
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("xmlx"),
								(String) pSet.get("frzzlx"),
								(String) pSet.get("frzzhm"),
								Integer.parseInt(((String) pSet.get("jsgqks"))
										.substring(0, 4)),
								Integer.parseInt(((String) pSet.get("jsgqjz"))
										.substring(0, 4)),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("ssqxbm"),
								"0",// (String) pSet.get("IS_DE_AREA"),
								(String) pSet.get("de_area_name"),
								hylbbm,// (String) pSet.get("HYLBBM"),
								(String) pSet.get("djrq"),
								(String) pSet.get("email"),
								(String) pSet.get("permit_industry"),
								(String) pSet.get("project_stage"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("permit_item_code"),
								(String) pSet.get("ssqxmc"), "TZXM",
								//后期补齐的字段
								(String) pSet.get("foreign_abroad_flag"),
								URLDecoder.decode((String) pSet.get("total_money_explain"),"UTF-8"),
								(String) pSet.get("the_industry"),
								(String) pSet.get("sjgjaq"),
								(String) pSet.get("aqwh"),
								(String) pSet.get("tzfs"),
								(String) pSet.get("xtze"),
								(String) pSet.get("xmjehl"),
								(String) pSet.get("xmzbj"),
								(String) pSet.get("xmzbj_my"),
								(String) pSet.get("xmzbj_hl"),
								(String) pSet.get("cymllx"),
								(String) pSet.get("cyzztm"),
								(String) pSet.get("tzsbqk"),
								(String) pSet.get("jyqk"),
								(String) pSet.get("bgqk"),
								(String) pSet.get("jjfw"),
								(String) pSet.get("tdhqfs"),
								(String) pSet.get("zmj"),
								(String) pSet.get("zjzmj"),
								(String) pSet.get("xzsb"),
								(String) pSet.get("jksl"),
								(String) pSet.get("xmszd"),
								(String) pSet.get("zftze"),
								auditflag}, connt);
				
				if(i == 0){
					connt.rollback();
				}
			} else {
				sql = "UPDATE DC_PROJECT SET PROJECT_NAME=?,LOCATION=?,LINK_MAN=?,LINK_PHONE=?,"
						+ "PROJECT_CONTENT=?,AREA_ALL=?,AREA_BUILD=?,INVESTMENT=?,PROJECT_ALLOWED_NO=?,VERSION=?,IS_IMPORTANT=?,"
						+ "SCALE=?,CONSTRUCT_TYPE=?,CONSTRUCT_PER=?,MOBILE_PHONE=?,CONTRACTOR=?,IS_USE=?,DIVISION_CODE=?,PROJECT_TYPE=?,"
						+ "LEREP_CERTTYPE=?,LEREP_CERTNO=?,START_YEAR=?,END_YEAR=?,PLACE_CODE=?,PLACE_CODE_DETAIL=?,IS_DE_AREA=?,"
						+ "DE_AREA_NAME=?,INDUSTRY=?,APPLY_DATE=?,CONTACT_EMAIL=?,PERMIT_INDUSTRY=?,PROJECT_STAGE=?,PLACE_NAME=?,"
						+ "DIVISION_NAME=?,PERMIT_ITEM_CODE=?,PLACE_AREA_DETAIL=?,PROJECT_SORT=?,"
						+ "FOREIGN_ABROAD_FLAG=?,TOTAL_MONEY_EXPLAIN=?,THE_INDUSTRY=?,IS_COUNTRY_SECURITY=?,SECURITY_APPROVAL_NUMBER=?,INVESTMENT_MODE=?,"
						+ "TOTAL_MONEY_DOLLAR=?,TOTAL_MONEY_DOLLAR_RATE=?,PROJECT_CAPITAL_MONEY=?,PROJECT_CAPITAL_MONEY_DOLLAR=?,"
						+ "CAPITAL_MONEY_DOLLAR_RATE=?,INDUSTRIAL_POLICY_TYPE=?,INDUSTRIAL_POLICY=?,OTHER_INVESTMENT_APPLY_INFO=?, "
						+ "TRANSACTION_BOTH_INFO=?,MERGER_PLAN=?,MERGER_MANAGEMENT_MODE_SCOPE=?,GET_LAND_MODE=?,LAND_AREA=?,BUILT_AREA=?,"
						+ "IS_ADD_DEVICE=?,IMPORT_DEVICE_NUMBER_MONEY=?,PROJECT_SITE=?,CHINA_TOTAL_MONEY=?,AUDIT_FLAG=? WHERE PROJECT_CODE = ? ";
				i = DbHelper.update(
						sql,
						new Object[] {
								URLDecoder.decode((String) pSet.get("xmmc"),"UTF-8"),
								// (String) pSet.get("xmbm"),
								URLDecoder.decode((String) pSet.get("jsdz"),"UTF-8"),
								(String) pSet.get("xmfzrxm"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("jsnr"),"UTF-8"),
								(String) pSet.get("area_all"),
								(String) pSet.get("area_build"),
								(String) pSet.get("ztz"),
								(String) pSet.get("project_allowed_no"),
								1,// Double.parseDouble((String)
									// pSet.get("VERSION")),
								(String) pSet.get("is_important"),
								(String) pSet.get("scale"),
								(String) pSet.get("construct_type"),
								(String) pSet.get("jsxz"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("dwmc"),"UTF-8"),
								isuse,// (String) pSet.get("IS_USE"),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("xmlx"),
								(String) pSet.get("frzzlx"),
								(String) pSet.get("frzzhm"),
								Integer.parseInt(((String) pSet.get("jsgqks"))
										.substring(0, 4)),
								Integer.parseInt(((String) pSet.get("jsgqjz"))
										.substring(0, 4)),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("ssqxbm"),
								"0",// (String) pSet.get("IS_DE_AREA"),
								(String) pSet.get("de_area_name"),
								hylbbm,// (String) pSet.get("HYLBBM"),
								(String) pSet.get("djrq"),
								(String) pSet.get("email"),
								(String) pSet.get("permit_industry"),
								(String) pSet.get("project_stage"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("permit_item_code"),
								(String) pSet.get("ssqxmc"), "TZXM",
								(String) pSet.get("foreign_abroad_flag"),
								URLDecoder.decode((String) pSet.get("total_money_explain"),"UTF-8"),
								(String) pSet.get("the_industry"),
								(String) pSet.get("sjgjaq"),
								(String) pSet.get("aqwh"),
								(String) pSet.get("tzfs"),
								(String) pSet.get("xtze"),
								(String) pSet.get("xmjehl"),
								(String) pSet.get("xmzbj"),
								(String) pSet.get("xmzbj_my"),
								(String) pSet.get("xmzbj_hl"),
								(String) pSet.get("cymllx"),
								(String) pSet.get("cyzztm"),
								(String) pSet.get("tzsbqk"),
								(String) pSet.get("jyqk"),
								(String) pSet.get("bgqk"),
								(String) pSet.get("jjfw"),
								(String) pSet.get("tdhqfs"),
								(String) pSet.get("zmj"),
								(String) pSet.get("zjzmj"),
								(String) pSet.get("xzsb"),
								(String) pSet.get("jksl"),
								(String) pSet.get("xmszd"),
								(String) pSet.get("zftze"),
								auditflag,
								(String) pSet.get("xmbm") }, connt);
			}
			
			if(i == 0){
				connt.rollback();
			}
			
			sql = "SELECT SEQ_ID FROM DC_PROJECT WHERE PROJECT_CODE = ?";
			DataSet tzsp_data = DbHelper.query(sql,new Object[] { (String) pSet.get("xmbm") }, conn);
			int h = tzsp_data.getTotal();
			String id_ = Tools.getUUID32();
			if (h == 0) {
				sql = "INSERT INTO DC_PROJECT(SEQ_ID,PROJECT_NAME,PROJECT_CODE,LOCATION,LINK_MAN,LINK_PHONE,"
						+ "PROJECT_CONTENT,AREA_ALL,AREA_BUILD,INVESTMENT,PROJECT_ALLOWED_NO,VERSION,IS_IMPORTANT,"
						+ "SCALE,CONSTRUCT_TYPE,CONSTRUCT_PER,MOBILE_PHONE,CONTRACTOR,IS_USE,DIVISION_CODE,PROJECT_TYPE,"
						+ "LEREP_CERTTYPE,LEREP_CERTNO,START_YEAR,END_YEAR,PLACE_CODE,PLACE_CODE_DETAIL,IS_DE_AREA,"
						+ "DE_AREA_NAME,INDUSTRY,APPLY_DATE,CONTACT_EMAIL,PERMIT_INDUSTRY,PROJECT_STAGE,PLACE_NAME,"
						+ "DIVISION_NAME,PERMIT_ITEM_CODE,PLACE_AREA_DETAIL,PROJECT_SORT,"
						+ "FOREIGN_ABROAD_FLAG,TOTAL_MONEY_EXPLAIN,THE_INDUSTRY,IS_COUNTRY_SECURITY,SECURITY_APPROVAL_NUMBER,INVESTMENT_MODE,"
						+ "TOTAL_MONEY_DOLLAR,TOTAL_MONEY_DOLLAR_RATE,PROJECT_CAPITAL_MONEY,PROJECT_CAPITAL_MONEY_DOLLAR,"
						+ "CAPITAL_MONEY_DOLLAR_RATE,INDUSTRIAL_POLICY_TYPE,INDUSTRIAL_POLICY,OTHER_INVESTMENT_APPLY_INFO, "
						+ "TRANSACTION_BOTH_INFO,MERGER_PLAN,MERGER_MANAGEMENT_MODE_SCOPE,GET_LAND_MODE,LAND_AREA,BUILT_AREA,"
						+ "IS_ADD_DEVICE,IMPORT_DEVICE_NUMBER_MONEY,PROJECT_SITE,CHINA_TOTAL_MONEY,AUDIT_FLAG) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				i = DbHelper.update(
						sql,
						new Object[] {
								id_,
								URLDecoder.decode((String) pSet.get("xmmc"),"UTF-8"),
								(String) pSet.get("xmbm"),
								URLDecoder.decode((String) pSet.get("jsdz"),"UTF-8"),
								(String) pSet.get("xmfzrxm"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("jsnr"),"UTF-8"),
								(String) pSet.get("area_all"),
								(String) pSet.get("area_build"),
								(String) pSet.get("ztz"),
								(String) pSet.get("project_allowed_no"),
								1,// Double.parseDouble((String)
									// pSet.get("VERSION")),
								(String) pSet.get("is_important"),
								(String) pSet.get("scale"),
								(String) pSet.get("construct_type"),
								(String) pSet.get("jsxz"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("dwmc"),"UTF-8"),
								isuse,// (String) pSet.get("IS_USE"),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("xmlx"),
								(String) pSet.get("frzzlx"),
								(String) pSet.get("frzzhm"),
								Integer.parseInt(((String) pSet.get("jsgqks"))
										.substring(0, 4)),
								Integer.parseInt(((String) pSet.get("jsgqjz"))
										.substring(0, 4)),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("ssqxbm"),
								"0",// (String) pSet.get("IS_DE_AREA"),
								(String) pSet.get("de_area_name"),
								hylbbm,// (String) pSet.get("HYLBBM"),
								(String) pSet.get("djrq"),
								(String) pSet.get("email"),
								(String) pSet.get("permit_industry"),
								(String) pSet.get("project_stage"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("permit_item_code"),
								(String) pSet.get("ssqxmc"), "TZXM",
								(String) pSet.get("foreign_abroad_flag"),
								URLDecoder.decode((String) pSet.get("total_money_explain"),"UTF-8"),
								(String) pSet.get("the_industry"),
								(String) pSet.get("sjgjaq"),
								(String) pSet.get("aqwh"),
								(String) pSet.get("tzfs"),
								(String) pSet.get("xtze"),
								(String) pSet.get("xmjehl"),
								(String) pSet.get("xmzbj"),
								(String) pSet.get("xmzbj_my"),
								(String) pSet.get("xmzbj_hl"),
								(String) pSet.get("cymllx"),
								(String) pSet.get("cyzztm"),
								(String) pSet.get("tzsbqk"),
								(String) pSet.get("jyqk"),
								(String) pSet.get("bgqk"),
								(String) pSet.get("jjfw"),
								(String) pSet.get("tdhqfs"),
								(String) pSet.get("zmj"),
								(String) pSet.get("zjzmj"),
								(String) pSet.get("xzsb"),
								(String) pSet.get("jksl"),
								(String) pSet.get("xmszd"),
								(String) pSet.get("zftze"),
								auditflag}, conn);
				if(i == 0){
					conn.rollback();
				}
				sql = "INSERT INTO APPROVE_INVEST_INDEX(ID,PROJECTID,FLOW_CODE,FLOW_DEFINEDID,CURNODEID,CURNODENAME,STATUS,"
						+ "CREATERID,CREATERNAME,CREATETIME,UPDATETIME,ORG_CODE,ORG_NAME,APPLY_FROM,REGION_CODE,REGION_NAME)"
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				i = DbHelper.update(
						sql,
						new Object[] {
								Tools.getUUID32(),
								id_,// (String) pSet.get("PROJECTID"),
								(String) pSet.get("FLOW_CODE"),
								(String) pSet.get("FLOW_DEFINEDID"),
								(String) pSet.get("CURNODEID"),
								(String) pSet.get("CURNODENAME"),
								status,// (String) pSet.get("STATUS"),
								(String) pSet.get("CREATERID"),
								(String) pSet.get("CREATERNAME"),
								CommonUtils.getInstance()
										.parseDateToTimeStamp(new Date(),
												CommonUtils.YYYY_MM_DD_HH_mm_SS),
								CommonUtils.getInstance()
										.parseDateToTimeStamp(new Date(),
												CommonUtils.YYYY_MM_DD_HH_mm_SS),// (String)
								// pSet.get("UPDATETIME"),
								(String) pSet.get("ORG_CODE"),
								(String) pSet.get("ORG_NAME"),
								(String) pSet.get("APPLY_FROM"),
								(String) pSet.get("REGION_CODE"),
								(String) pSet.get("REGION_NAME") }, conn);
				if(i == 0){
					conn.rollback();
				}
				sql = "INSERT INTO APPROVE_PROJECT_LEREP_INFO(PROJECT_ID,ENTERPRISE_ID,PROJECT_CODE,ENTERPRISE_NAME,LEREP_CERTTYPE,"
						+ "LEREP_CERTNO,CONTACT_NAME,CONTACT_TEL,CONTACT_EMAIL,ENTERPRISE_PLACE,ENTERPRISE_NATURE,"
						+ "CHINA_FOREIGN_SHARE_RATIO,BUSINESS_SCOPE,CONTACT_PHONE,CONTACT_FAX,CORRESPONDENCE_ADDRESS) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				i = DbHelper.update(sql,
								new Object[] {
										id_,
										Tools.getUUID32(),// (String)
															// pSet.get("enterprise_id"),
										(String) pSet.get("xmbm"),
										URLDecoder.decode((String) pSet.get("enterprise_name"),"UTF-8"),
										(String) pSet.get("lerep_certtype"),
										(String) pSet.get("lerep_certno"),
										(String) pSet.get("contact_name"),
										(String) pSet.get("contact_tel"),
										(String) pSet.get("contact_email"),
										(String) pSet.get("enterprise_place"),
										(String) pSet.get("enterprise_nature"),
										(String) pSet
												.get("china_foreign_share_ratio"),
										(String) pSet.get("business_scope"),
										(String) pSet.get("contact_phone"),
										(String) pSet.get("contact_fax"),
										URLDecoder.decode((String) pSet.get("correspondence_address"),"UTF-8") },conn);
				if(i == 0){
					conn.rollback();
				}
				// 新加表二
				if ("1".equals((String) pSet.get("foreign_abroad_flag"))) {
					sql = "INSERT INTO APPROVE_CONTRIBUTION_INFO(PROJECT_ID,INVESTMENT_ID,BUSINESS_TYPE,PROJECT_CODE,INVESTMENT_NAME,"
							+ "REG_COUNTRY,CONTRIBUTION_LIMIT,CONTRIBUTION_RATIO,CONTRIBUTION_MODE) "
							+ "VALUES(?,?,?,?,?,?,?,?,?)";
					i = DbHelper.update(
							sql,
							new Object[] {
									id_,
									Tools.getUUID32(),// (String)
									// pSet.get("investment_id"),
									(String) pSet.get("business_type"),
									(String) pSet.get("xmbm"),
									(String) pSet.get("investment_name"),
									(String) pSet.get("reg_country"),
									(String) pSet.get("contribution_limit"),
									(String) pSet.get("contribution_ratio"),
									(String) pSet.get("contribution_mode") }, conn);
					if(i == 0){
						conn.rollback();
					}
				}
			} else {
				String projectid = tzsp_data.getJOData().getString("SEQ_ID");
				sql = "UPDATE DC_PROJECT SET PROJECT_NAME=?,LOCATION=?,LINK_MAN=?,LINK_PHONE=?,"
						+ "PROJECT_CONTENT=?,AREA_ALL=?,AREA_BUILD=?,INVESTMENT=?,PROJECT_ALLOWED_NO=?,VERSION=?,IS_IMPORTANT=?,"
						+ "SCALE=?,CONSTRUCT_TYPE=?,CONSTRUCT_PER=?,MOBILE_PHONE=?,CONTRACTOR=?,IS_USE=?,DIVISION_CODE=?,PROJECT_TYPE=?,"
						+ "LEREP_CERTTYPE=?,LEREP_CERTNO=?,START_YEAR=?,END_YEAR=?,PLACE_CODE=?,PLACE_CODE_DETAIL=?,IS_DE_AREA=?,"
						+ "DE_AREA_NAME=?,INDUSTRY=?,APPLY_DATE=?,CONTACT_EMAIL=?,PERMIT_INDUSTRY=?,PROJECT_STAGE=?,PLACE_NAME=?,"
						+ "DIVISION_NAME=?,PERMIT_ITEM_CODE=?,PLACE_AREA_DETAIL=?,PROJECT_SORT=?,"
						+ "FOREIGN_ABROAD_FLAG=?,TOTAL_MONEY_EXPLAIN=?,THE_INDUSTRY=?,IS_COUNTRY_SECURITY=?,SECURITY_APPROVAL_NUMBER=?,INVESTMENT_MODE=?,"
						+ "TOTAL_MONEY_DOLLAR=?,TOTAL_MONEY_DOLLAR_RATE=?,PROJECT_CAPITAL_MONEY=?,PROJECT_CAPITAL_MONEY_DOLLAR=?,"
						+ "CAPITAL_MONEY_DOLLAR_RATE=?,INDUSTRIAL_POLICY_TYPE=?,INDUSTRIAL_POLICY=?,OTHER_INVESTMENT_APPLY_INFO=?, "
						+ "TRANSACTION_BOTH_INFO=?,MERGER_PLAN=?,MERGER_MANAGEMENT_MODE_SCOPE=?,GET_LAND_MODE=?,LAND_AREA=?,BUILT_AREA=?,"
						+ "IS_ADD_DEVICE=?,IMPORT_DEVICE_NUMBER_MONEY=?,PROJECT_SITE=?,CHINA_TOTAL_MONEY=?,AUDIT_FLAG=? WHERE PROJECT_CODE=?";
				i = DbHelper.update(
						sql,
						new Object[] {
								URLDecoder.decode((String) pSet.get("xmmc"),"UTF-8"),
								URLDecoder.decode((String) pSet.get("jsdz"),"UTF-8"),
								(String) pSet.get("xmfzrxm"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("jsnr"),"UTF-8"),
								(String) pSet.get("area_all"),
								(String) pSet.get("area_build"),
								(String) pSet.get("ztz"),
								(String) pSet.get("project_allowed_no"),
								1,// Double.parseDouble((String)
									// pSet.get("VERSION")),
								(String) pSet.get("is_important"),
								(String) pSet.get("scale"),
								(String) pSet.get("construct_type"),
								(String) pSet.get("jsxz"),
								(String) pSet.get("sjhm"),
								URLDecoder.decode((String) pSet.get("dwmc"),"UTF-8"),
								isuse,// (String) pSet.get("IS_USE"),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("xmlx"),
								(String) pSet.get("frzzlx"),
								(String) pSet.get("frzzhm"),
								Integer.parseInt(((String) pSet.get("jsgqks"))
										.substring(0, 4)),
								Integer.parseInt(((String) pSet.get("jsgqjz"))
										.substring(0, 4)),
								(String) pSet.get("ssqxbm"),
								(String) pSet.get("ssqxbm"),
								"0",// (String) pSet.get("IS_DE_AREA"),
								(String) pSet.get("de_area_name"),
								hylbbm,// (String) pSet.get("HYLBBM"),
								(String) pSet.get("djrq"),
								(String) pSet.get("email"),
								(String) pSet.get("permit_industry"),
								(String) pSet.get("project_stage"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("ssqxmc"),
								(String) pSet.get("permit_item_code"),
								(String) pSet.get("ssqxmc"), "TZXM",
								(String) pSet.get("foreign_abroad_flag"),
								URLDecoder.decode((String) pSet.get("total_money_explain"),"UTF-8"),
								(String) pSet.get("the_industry"),
								(String) pSet.get("sjgjaq"),
								(String) pSet.get("aqwh"),
								(String) pSet.get("tzfs"),
								(String) pSet.get("xtze"),
								(String) pSet.get("xmjehl"),
								(String) pSet.get("xmzbj"),
								(String) pSet.get("xmzbj_my"),
								(String) pSet.get("xmzbj_hl"),
								(String) pSet.get("cymllx"),
								(String) pSet.get("cyzztm"),
								(String) pSet.get("tzsbqk"),
								(String) pSet.get("jyqk"),
								(String) pSet.get("bgqk"),
								(String) pSet.get("jjfw"),
								(String) pSet.get("tdhqfs"),
								(String) pSet.get("zmj"),
								(String) pSet.get("zjzmj"),
								(String) pSet.get("xzsb"),
								(String) pSet.get("jksl"),
								(String) pSet.get("xmszd"),
								(String) pSet.get("zftze"),
								auditflag,
								(String) pSet.get("xmbm") }, conn);
				if(i == 0){
					conn.rollback();
				}
				sql = "UPDATE APPROVE_INVEST_INDEX SET FLOW_CODE=?,FLOW_DEFINEDID=?,CURNODEID=?,CURNODENAME=?,STATUS=?,"
						+ "CREATERID=?,CREATERNAME=?,CREATETIME=?,UPDATETIME=?,ORG_CODE=?,ORG_NAME=?,APPLY_FROM=?,REGION_CODE=?,REGION_NAME=? WHERE PROJECTID=?";
				i = DbHelper.update(sql,
								new Object[] {
										(String) pSet.get("FLOW_CODE"),
										(String) pSet.get("FLOW_DEFINEDID"),
										(String) pSet.get("CURNODEID"),
										(String) pSet.get("CURNODENAME"),
										status,// (String) pSet.get("STATUS"),
										(String) pSet.get("CREATERID"),
										(String) pSet.get("CREATERNAME"),
										CommonUtils.getInstance().parseDateToTimeStamp(
														new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),
										CommonUtils.getInstance().parseDateToTimeStamp(
														new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),// (String)
										// pSet.get("UPDATETIME"),
										(String) pSet.get("ORG_CODE"),
										(String) pSet.get("ORG_NAME"),
										(String) pSet.get("APPLY_FROM"),
										(String) pSet.get("REGION_CODE"),
										(String) pSet.get("REGION_NAME"), projectid },conn);
				if(i == 0){
					conn.rollback();
				}
				sql = "SELECT PROJECT_CODE FROM APPROVE_PROJECT_LEREP_INFO WHERE PROJECT_ID = ?";
				DataSet lerep_info_data = DbHelper.query(sql,new Object[] {projectid}, conn);
				if(lerep_info_data.getTotal()>0){
					// 添加的表
					sql = "UPDATE APPROVE_PROJECT_LEREP_INFO SET PROJECT_CODE=?,ENTERPRISE_NAME=?,LEREP_CERTTYPE=?,"
							+ "LEREP_CERTNO=?,CONTACT_NAME=?,CONTACT_TEL=?,CONTACT_EMAIL=?,ENTERPRISE_PLACE=?,ENTERPRISE_NATURE=?,"
							+ "CHINA_FOREIGN_SHARE_RATIO=?,BUSINESS_SCOPE=?,CONTACT_PHONE=?,CONTACT_FAX=?,CORRESPONDENCE_ADDRESS=? WHERE PROJECT_ID=?";
					i = DbHelper.update(
							sql,
							new Object[] { (String) pSet.get("xmbm"),
									URLDecoder.decode((String) pSet.get("enterprise_name"),"UTF-8"),
									(String) pSet.get("lerep_certtype"),
									(String) pSet.get("lerep_certno"),
									(String) pSet.get("contact_name"),
									(String) pSet.get("contact_tel"),
									(String) pSet.get("contact_email"),
									(String) pSet.get("enterprise_place"),
									(String) pSet.get("enterprise_nature"),
									(String) pSet.get("china_foreign_share_ratio"),
									(String) pSet.get("business_scope"),
									(String) pSet.get("contact_phone"),
									(String) pSet.get("contact_fax"),
									URLDecoder.decode((String) pSet.get("correspondence_address"),"UTF-8"),
									projectid }, conn);
					if(i == 0){
						conn.rollback();
					}
				}else{
					sql = "INSERT INTO APPROVE_PROJECT_LEREP_INFO(PROJECT_ID,ENTERPRISE_ID,PROJECT_CODE,ENTERPRISE_NAME,LEREP_CERTTYPE,"
							+ "LEREP_CERTNO,CONTACT_NAME,CONTACT_TEL,CONTACT_EMAIL,ENTERPRISE_PLACE,ENTERPRISE_NATURE,"
							+ "CHINA_FOREIGN_SHARE_RATIO,BUSINESS_SCOPE,CONTACT_PHONE,CONTACT_FAX,CORRESPONDENCE_ADDRESS) "
							+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					i = DbHelper.update(sql,
									new Object[] {
											projectid,
											Tools.getUUID32(),// (String)
																// pSet.get("enterprise_id"),
											(String) pSet.get("xmbm"),
											URLDecoder.decode((String) pSet.get("enterprise_name"),"UTF-8"),
											(String) pSet.get("lerep_certtype"),
											(String) pSet.get("lerep_certno"),
											(String) pSet.get("contact_name"),
											(String) pSet.get("contact_tel"),
											(String) pSet.get("contact_email"),
											(String) pSet.get("enterprise_place"),
											(String) pSet.get("enterprise_nature"),
											(String) pSet
													.get("china_foreign_share_ratio"),
											(String) pSet.get("business_scope"),
											(String) pSet.get("contact_phone"),
											(String) pSet.get("contact_fax"),
											URLDecoder.decode((String) pSet.get("correspondence_address"),"UTF-8") },conn);
					if(i == 0){
						conn.rollback();
					}
				}
				// 新加表二
				if ("1".equals((String) pSet.get("foreign_abroad_flag"))) {
					sql = "UPDATE APPROVE_CONTRIBUTION_INFO SET BUSINESS_TYPE=?,PROJECT_CODE=?,INVESTMENT_NAME=?,"
							+ "REG_COUNTRY=?,CONTRIBUTION_LIMIT=?,CONTRIBUTION_RATIO=?,CONTRIBUTION_MODE=? WHERE PROJECT_ID=? ";
					i = DbHelper.update(
							sql,
							new Object[] { (String) pSet.get("business_type"),
									(String) pSet.get("xmbm"),
									(String) pSet.get("investment_name"),
									(String) pSet.get("reg_country"),
									(String) pSet.get("contribution_limit"),
									(String) pSet.get("contribution_ratio"),
									(String) pSet.get("contribution_mode"),
									projectid }, conn);
					if(i == 0){
						conn.rollback();
					}
				}
				// 删除插入记录信息
				sql = "DELETE FROM APPROVE_EXCHANGE_RECORD WHERE SNO=? AND EXCHANGETYPE='TZSP_PROJECT'";
				i = DbHelper.update(sql,
						new Object[] { (String) pSet.get("xmbm") }, conn);
				if(i == 0){
					conn.rollback();
				}
			}
			connt.commit();
			conn.commit();
		}catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}finally{
			DBSource.closeConnection(connt);
			DBSource.closeConnection(conn);
		}
		ds.setState(StateType.SUCCESS);
		ds.setRawData(obj.toString());
		//log.info("投资项目数据推送返回结果：" + ds);
		return ds;
	}

	/**
	 * 数据处理
	 */
	public static String stringFill(String source, int fillLength,
			char fillChar, boolean isLeftFill) {
		if (source == null || source.length() >= fillLength)
			return source;

		StringBuilder result = new StringBuilder(fillLength);
		int len = fillLength - source.length();
		if (isLeftFill) {
			for (; len > 0; len--) {
				result.append(fillChar);
			}
			result.append(source);
		} else {
			result.append(source);
			for (; len > 0; len--) {
				result.append(fillChar);
			}
		}
		return result.toString();
	}

	public String stringFill2(String source, int fillLength, char fillChar,
			boolean isLeftFill) {
		if (source == null || source.length() >= fillLength)
			return source;

		char[] c = new char[fillLength];
		char[] s = source.toCharArray();
		int len = s.length;
		if (isLeftFill) {
			int fl = fillLength - len;
			for (int i = 0; i < fl; i++) {
				c[i] = fillChar;
			}
			System.arraycopy(s, 0, c, fl, len);
		} else {
			System.arraycopy(s, 0, c, 0, len);
			for (int i = len; i < fillLength; i++) {
				c[i] = fillChar;
			}
		}
		return String.valueOf(c);
	}
}
