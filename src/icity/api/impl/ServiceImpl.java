package api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import app.icity.sync.UniteUserInterface;
import app.icity.sync.UploadUtil;
import app.util.RestUtil;

import com.commnetsoft.proxy.util.MD5;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.icore.util.sms.MailSenderInfo;
import com.icore.util.sms.SimpleMailSender;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.IdGenereator;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;
import core.util.HttpClientUtil;

/**
 * 通用接口实现
 * 登录注册相关；区划部门；附件上传；MD5
 * @author lihongyun
 */
@SuppressWarnings("deprecation")
public class ServiceImpl extends BaseQueryCommand {
	@SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(ServiceImpl.class);
	private static final String ACCESS_TOKEN = "access_token";
	private static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9' };
	private static api.tools.Tools mytools = new api.tools.Tools();
	public static ServiceImpl getInstance() {
		return DaoFactory.getDao(ServiceImpl.class.getName());
	}
	//region根据事项ID获取所有事项相关信息getAllItemInfoByItemID
	/**
	 * 根据事项ID获取所有事项相关信息
	 * @param pSet
	 * @return
	 */
	public DataSet getAllItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String itemId = (String) pSet.getParameter("itemId");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl")
					+ "/getAllItemInfoByItemID");
			url += "?itemId=" + itemId;
			HttpClientUtil client = new HttpClientUtil();
			String result = client.getResult(url, "");
			ds.setRawData(result);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setRawData(e);
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	//endregion
	
	//region 部门区划列表getDeptList
	/**
	 * 部门区划列表
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public JSONObject getDeptList(ParameterSet pset) {
		JSONObject obj = new JSONObject();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl")
				+ "/web/c/getDeptList");
		Map<String, String> data = new HashMap<String, String>();
		data.put("region_code", (String) pset.getParameter("region_code"));
		try {
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("code", "300");
			obj.put("error", e.toString());
		}
		return obj;
	}
	//endregion
	
	//region 主题查询getPowerCatalog
	/**
	 * 主题查询
	 * @param pSet
	 * @return
	 */
	public DataSet getPowerCatalog(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String class_type = (String)pSet.get("class_type");
		String region_code = (String)pSet.get("region_code");
		try {
			if("".equals(class_type)){
				class_type = "%%";
			}
			if("".equals(region_code)){
				String sql = "select * from power_catalog t where is_use = '1' and class_type like ? ";
				ds = DbHelper.query(sql, new Object[]{class_type}, conn);
			}else{
				String sql = "select * from power_catalog t where is_use = '1' and class_type like ? and region_code = ?";
				ds = DbHelper.query(sql, new Object[]{class_type,region_code}, conn);
			}			
		}catch (Exception e) {
			e.printStackTrace();						
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 根据用户名/手机号 密码登录login
	/**
	 * 根据用户名/手机号 密码登录
	 * @param pset
	 * @return
	 */
	public DataSet login(ParameterSet pSet) {
		String loginname = (String) pSet.get("loginname");
		String password = (String) pSet.get("password");
		password=MD5.MD5Encode(password);
		String type = (String) pSet.get("type");		
		DataSet ds_select = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String select = "SELECT ID USERID,ACCOUNT,STATUS,PHONE,CARD_NO,EMAIL,ORG_NAME,NAME,photo_uri FROM UC_USER WHERE (ACCOUNT =? or PHONE=?) and PASSWORD = ? and type=?";
		try {
			conn.setAutoCommit(false);			
			ds_select = DbHelper.query(select, new Object[] { loginname,loginname,
					password, type }, conn);			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds_select.setState(StateType.FAILT);
			ds_select.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds_select;
	}
	//endregion
	
	//region 个人用户注册接口registerOfPer
	/**
	 * 个人用户注册接口
	 * username=username&phone=1871111111&pass_per=123&code_per=123&name_per
	 * =姓名&code_type_per=10&address_per=地址email=邮箱
	 */
	public DataSet registerOfPer(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String username = (String) pSet.getParameter("username");// 用户名
			String phone = (String) pSet.getParameter("phone");// 手机号
			String pass_per = (String) pSet.getParameter("pass_per");// 密码
			String m_pass_per = (String) pSet.getParameter("pass_per");// 密码
			pass_per = mytools.toMD5(pass_per);
			String email = (String) pSet.getParameter("email");// 邮箱
			if (!StringUtil.isNotEmpty(email)){
				email="";
			}
			String code_per = (String) pSet.getParameter("code_per");// 证件号码			
			String name_per = (String) pSet.getParameter("name_per");// 姓名
			if (!StringUtil.isNotEmpty(name_per)){
				name_per="";
			}
			String code_type_per = (String) pSet.getParameter("code_type_per");// 证件类型
			if (!StringUtil.isNotEmpty(code_type_per)){
				code_type_per="";
			}
			String address_per = (String) pSet.getParameter("address_per");// 地址
			if (!StringUtil.isNotEmpty(address_per)){
				address_per="";
			}
			String ly = (String) pSet.getParameter("ly");// 来源
			if (!StringUtil.isNotEmpty(ly)){
				ly="1";
			}
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			int type = 11;// 个人
			String is_inuse = "1";// 账号状态
			String sql_phone = "select 1 from uc_user where phone=? and type=11";
			DataSet k = DbHelper.query(sql_phone, new Object[] { phone }, conn);
			if (k.getTotal() > 0) {
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
				ds.setMessage("该手机已注册！");
				return ds;
			}
			String sql_account = "select 1 from uc_user where account=?";// and
																			// ly='2'";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}
			
			if (StringUtil.isNotEmpty(code_per)){
				String sql_card_no = "select 1 from uc_user where card_no=? and type='11' ";// and
				// ly='2'";
				DataSet ds_card_no = DbHelper.query(sql_card_no,
				new Object[] { code_per }, conn);
				if (ds_card_no.getTotal() > 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("身份证号已注册！");
					return ds;
				}
			}else{
				code_per="";
			}
			
			/*
			 * JSONObject jo = new JSONObject(); jo.put("loginname", username);
			 * jo.put("pwd", m_pass_per); jo.put("name", name_per);
			 * jo.put("papersnumber", code_per); jo.put("mobile", phone);
			 * jo.put("email", email); UniteUserInterface unitUI = new
			 * UniteUserInterface(); unitUI.setUserType("gr"); JSONObject retVal
			 * = unitUI.userAdd(jo); if ("0".equals(retVal.getString("result")))
			 * {
			 */
			// 证件类型 10
			// String sql1 = "select max(id) as id from uc_user ";
			// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
			// int id = Integer.parseInt((String) j.getJOData().get("ID")) + 1;
			// String sql_map =
			// "insert into UC_USER_MAP (user_id_map,user_id) values (?,?)";
			// DbHelper.update(sql_map, new Object[] { retVal.getString("uuid"),
			// id },conn);
			long id = IdGenereator.getInstance("usercenter").getId();
			String sql = "insert into uc_user (id,account,name,password,email,card_type,card_no,creation_time,address,type,phone,is_inuse,status,ly) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_per,
					pass_per, email, code_type_per, code_per, now, address_per,
					type, phone, is_inuse, "0", ly }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("注册失败！");
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("注册成功！");
				conn.commit();
			}
			/*
			 * } else { String message = ""; if
			 * ("201".equals(retVal.getString("result"))) { message =
			 * "appmark参数不正确"; } else if
			 * ("202".equals(retVal.getString("result"))) { message =
			 * "sign参数不正确"; } else if ("203".equals(retVal.getString("result")))
			 * { message = "args参数不正确"; } else if
			 * ("204".equals(retVal.getString("result"))) { message =
			 * "登录名（或其他必填项）不能为空"; } else if
			 * ("205".equals(retVal.getString("result"))) { message =
			 * "登录名格式不正确"; } else if ("206".equals(retVal.getString("result")))
			 * { message = "已经存在相同登录名"; } else if
			 * ("207".equals(retVal.getString("result"))) { message =
			 * "证件号码格式不正确"; } else if ("208".equals(retVal.getString("result")))
			 * { message = "证件号码已存在"; } else if
			 * ("209".equals(retVal.getString("result"))) { message =
			 * "手机号码格式不正确"; } else if ("210".equals(retVal.getString("result")))
			 * { message = "手机号码重复"; } else if
			 * ("211".equals(retVal.getString("result"))) { message = "邮箱格式不正确";
			 * } else if ("212".equals(retVal.getString("result"))) { message =
			 * "邮箱重复"; } else if ("299".equals(retVal.getString("result"))) {
			 * message = "其他错误"; } ds.setState(StateType.FAILT);
			 * ds.setMessage("注册失败,"+message); }
			 */
			ds.setRawData(new JSONArray());
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 个人用户信息编辑 updateUserInfoOfPer
	/**
	 * 个人用户信息编辑 *
	 */
	public DataSet updateUserInfoOfPer(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String ly = (String) pSet.getParameter("ly");// 来源
			if (!StringUtil.isNotEmpty(ly)){
				ly="1";
			}
			String type = (String) pSet.getParameter("type");//类别11个人21法人
			if (!StringUtil.isNotEmpty(type)){
				type="11";
			}
			String username = (String) pSet.getParameter("username");// 用户名
			String email = (String) pSet.getParameter("email");// 邮箱			
			String code_per = (String) pSet.getParameter("code_per");// 证件号码
			String name_per = (String) pSet.getParameter("name_per");// 姓名
			String address_per = (String) pSet.getParameter("address_per");// 地址
			String phone = (String) pSet.getParameter("phone");// 手机
			// 证件类型 10
			String sql1 = "select 1 from uc_user where account=? and type=?";
			DataSet j = DbHelper.query(sql1, new Object[] { username, type },
					conn);
			if (j.getTotal() > 0) {
				String sql = "update uc_user t set t.email=?,card_no=?,t.address=?,t.name=? where t.account=? and t.type=?";
				int i = DbHelper.update(sql, new Object[] { email, code_per,
						address_per, name_per, username, type }, conn);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("修改失败！");
				} else {
					ds.setMessage("修改成功！");
					ds.setState(StateType.SUCCESS);
				}
				ds.setRawData(new JSONArray());
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名或密码错误！");
				ds.setRawData(new JSONArray());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 企业用户注册接口registerOfOrg
	/**
	 * 企业用户注册接口 username// 用户名 phone// 手机号 pass_org// 密码 code_org// 证件号码
	 * name_org// 姓名 code_type_org// 证件类型 address_org// 地址 org_org// 机构代码
	 * org_all_org// 机构名称 legal_person_org// 机构法人名称 code_type_legal_person_org//
	 * 机构法人证件类型 code_legal_person_org// 机构法人编码
	 */
	public DataSet registerOfOrg(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String ly = (String) pSet.getParameter("ly");// 来源
			if (!StringUtil.isNotEmpty(ly)){
				ly="1";
			}
			String username = (String) pSet.getParameter("username");// 用户名
			String phone_org = (String) pSet.getParameter("phone");// 手机号
			String pass_org = (String) pSet.getParameter("pass_org");// 密码
			String m_pass_org = (String) pSet.getParameter("pass_org");// 密码
			pass_org = mytools.toMD5(pass_org);
			String email = (String) pSet.getParameter("email");// 邮箱
			String code_org = (String) pSet.getParameter("code_org");// 证件号码
			String name_org = (String) pSet.getParameter("name_org");// 姓名
			String code_type_org = (String) pSet.getParameter("code_type_org");// 证件类型
			String address_org = (String) pSet.getParameter("address_org");// 地址
			String org_org = (String) pSet.getParameter("org_org");// 机构代码
			String org_all_org = (String) pSet.getParameter("org_all_org");// 机构名称
			String legal_person_org = (String) pSet
					.getParameter("legal_person_org");// 机构法人名称
			String code_type_legal_person_org = (String) pSet
					.getParameter("code_type_legal_person_org");// 机构法人证件类型
			String code_legal_person_org = (String) pSet
					.getParameter("code_legal_person_org");// 机构法人编码
			
			String credit_code=(String) pSet.getParameter("credit_code");//统一信用代码
			String type_str =(String) pSet.getParameter("type");//21企业31机构 必填
			int type = 21;// 企业
			if (!StringUtil.isNotEmpty(type_str)){
				type= Integer.parseInt(type_str);
			}			
			String is_inuse = "1";// 账号状态
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			String sql_phone = "select 1 from uc_user where phone=? and type=21";
			DataSet k = DbHelper.query(sql_phone, new Object[] { phone_org },
					conn);
			if (k.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("手机号已注册！");
				ds.setRawData(new JSONArray());
				return ds;
			}
			String sql_account = "select 1 from uc_user where account=?";// and
																			// ly='2'";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}
			String sql_card_no = "select 1 from uc_user where card_no=? and type=21";// and
																			// ly='2'";
			DataSet ds_card_no = DbHelper.query(sql_card_no,
					new Object[] { code_org }, conn);
			if (ds_card_no.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("身份证号已注册！");
				return ds;
			}
			/*
			 * JSONObject jo = new JSONObject(); jo.put("loginname", username);
			 * jo.put("pwd", m_pass_org); jo.put("realname", name_org);
			 * jo.put("paperstype", "1");//证件类型（默认为1） //1- 身份证 //2- 护照 //3-
			 * 港澳通行证 //4- 台湾通行证 //5- 香港身份证 //6- 澳门身份证 //7- 台湾身份证
			 * jo.put("cardnumber ", code_org); jo.put("mobile", phone_org);
			 * jo.put("email", email); jo.put("type", "1");//法人类型：1-企业法人
			 * 2-非企业法人/其他组织 jo.put("name", org_all_org);//法人名称（企业名称、机构名称）
			 * jo.put("regnumber", org_org);//企业工商注册号码（社会统一信用代码） jo.put("sex",
			 * "1");//企业负责人或机构负责人性别1：男0：女 jo.put("nation", "汉族");
			 * jo.put("scope", "经营范围"); UniteUserInterface unitUI = new
			 * UniteUserInterface(); unitUI.setUserType("fr"); JSONObject retVal
			 * = unitUI.userAdd(jo); if ("0".equals(retVal.getString("result")))
			 * {
			 */
			// 证件类型 10
			// String sql1 = "select max(id) as id from uc_user ";
			// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
			// int id = Integer.parseInt((String) j.getJOData().get("ID")) + 1;
			// String sql_map =
			// "insert into UC_USER_MAP (user_id_map,user_id) values (?,?)";
			// DbHelper.update(sql_map, new Object[] { retVal.getString("uuid"),
			// id },conn);
			long id = IdGenereator.getInstance("usercenter").getId();
			String sql = "insert into uc_user (id,account,name,password,email,card_type,card_no,creation_time,address,type,org_no,"
					+ "org_name,org_boss_no,org_boss_type,org_boss_name,phone,credit_code,is_inuse,status,ly) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_org,
					pass_org, email, code_type_org, code_org, now, address_org,
					type, org_org, org_all_org, code_legal_person_org,
					code_type_legal_person_org, legal_person_org, phone_org,credit_code,
					is_inuse, "0", ly }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("注册失败！");
			} else {
				conn.commit();
				ds.setMessage("注册成功！");
				ds.setState(StateType.SUCCESS);
			}
			/*
			 * }else{ String message = ""; if
			 * ("201".equals(retVal.getString("result"))) { message =
			 * "appmark参数不正确"; } else if
			 * ("202".equals(retVal.getString("result"))) { message =
			 * "sign参数不正确"; } else if ("203".equals(retVal.getString("result")))
			 * { message = "args参数不正确"; } else if
			 * ("204".equals(retVal.getString("result"))) { message =
			 * "登录名（或其他必填项）不能为空"; } else if
			 * ("205".equals(retVal.getString("result"))) { message =
			 * "登录名格式不正确"; } else if ("206".equals(retVal.getString("result")))
			 * { message = "已经存在相同登录名"; } else if
			 * ("207".equals(retVal.getString("result"))) { message =
			 * "证件号码格式不正确"; } else if ("208".equals(retVal.getString("result")))
			 * { message = "证件号码已存在"; } else if
			 * ("209".equals(retVal.getString("result"))) { message =
			 * "手机号码格式不正确"; } else if ("210".equals(retVal.getString("result")))
			 * { message = "手机号码重复"; } else if
			 * ("211".equals(retVal.getString("result"))) { message = "邮箱格式不正确";
			 * } else if ("212".equals(retVal.getString("result"))) { message =
			 * "邮箱重复"; } else if ("299".equals(retVal.getString("result"))) {
			 * message = "其他错误"; } ds.setState(StateType.FAILT);
			 * ds.setMessage("注册失败,"+message); }
			 */
			ds.setRawData(new JSONArray());
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 企业用户信息编辑updateUserInfoOfOrg
	/**
	 * 企业用户信息编辑 *
	 */
	public DataSet updateUserInfoOfOrg(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String ly = (String) pSet.getParameter("ly");// 来源
			if (!StringUtil.isNotEmpty(ly)){
				ly="1";
			}
			String type = (String) pSet.getParameter("type");//类别11个人21法人
			if (!StringUtil.isNotEmpty(type)){
				type="11";
			}
			String username = (String) pSet.getParameter("username");// 用户名
			String phone_org = (String) pSet.getParameter("phone");// 手机号
			// String pass_org = (String) pSet.getParameter("pass_org");// 密码
			String email = (String) pSet.getParameter("email");// 邮箱
			String code_org = (String) pSet.getParameter("code_org");// 证件号码
			String name_org = (String) pSet.getParameter("name_org");// 姓名
			// String code_type_org = (String)
			// pSet.getParameter("code_type_org");// 证件类型
			String address_org = (String) pSet.getParameter("address_org");// 地址
			// String org_org = (String) pSet.getParameter("org_org");// 机构代码
			// String org_all_org = (String) pSet.getParameter("org_all_org");//
			// 机构名称
			// String legal_person_org = (String)
			// pSet.getParameter("legal_person_org");// 机构法人名称
			// String code_type_legal_person_org = (String)
			// pSet.getParameter("code_type_legal_person_org");// 机构法人证件类型
			// String code_legal_person_org = (String)
			// pSet.getParameter("code_legal_person_org");// 机构法人编码
			String is_inuse = "1";// 账号状态
			// 证件类型 10
			String sql1 = "select 1 from uc_user where account=? and type=?";
			DataSet j = DbHelper.query(sql1, new Object[] { username, type },
					conn);
			if (j.getTotal() > 0) {
				String sql = "update uc_user t set t.email=?,card_no=?,t.address=?,t.name=?,t.phone=?"
						+ " where t.account=? and t.type=?";
				int i = DbHelper.update(sql, new Object[] { email, code_org,
						name_org, address_org, name_org,phone_org,username, type }, conn);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("修改失败！");
				} else {
					ds.setMessage("修改成功！");
					ds.setState(StateType.SUCCESS);
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名或密码错误！");
			}
			ds.setRawData(new JSONArray());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 通过手机号初始化密码modifyPasswordByPhone
	/**
	 * 通过手机号初始化密码 * password=&phone=
	 */
	public DataSet modifyPasswordByPhone(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String ly = (String) pSet.getParameter("ly");// 来源
			if (!StringUtil.isNotEmpty(ly)){
				ly="1";
			}
			String type = (String) pSet.getParameter("type");//类别11个人 21法人
			if (!StringUtil.isNotEmpty(type)){
				type="11";
			}
			String phone = (String) pSet.getParameter("phone");// 手机号
			String password = (String) pSet.getParameter("password");// 新密码
			String m_password = (String) pSet.getParameter("password");// 新密码
			password = mytools.toMD5(password);
			String sql1 = "select * from uc_user where phone=? and type=? and is_inuse='1'";
			DataSet j = DbHelper.query(sql1, new Object[] { phone,type }, conn);
			if (j.getTotal() == 1) {
				JSONObject j_jo = j.getJAData().getJSONObject(0);
				/*
				 * JSONObject jo = new JSONObject(); UniteUserInterface unitUI =
				 * new UniteUserInterface();
				 * if("21".equals(j_jo.getString("TYPE"))){ jo.put("loginname",
				 * j_jo.getString("ACCOUNT")); jo.put("pwd", m_password);
				 * jo.put("realname", j_jo.getString("NAME"));
				 * jo.put("paperstype", "1");//证件类型（默认为1） //1- 身份证 //2- 护照 //3-
				 * 港澳通行证 //4- 台湾通行证 //5- 香港身份证 //6- 澳门身份证 //7- 台湾身份证
				 * jo.put("cardnumber ", j_jo.getString("CARD_NO"));
				 * jo.put("mobile", j_jo.getString("PHONE")); jo.put("email",
				 * j_jo.getString("EMAIL")); jo.put("type", "1");//法人类型：1-企业法人
				 * 2-非企业法人/其他组织 jo.put("name",
				 * j_jo.getString("ORG_NAME"));//法人名称（企业名称、机构名称）
				 * jo.put("regnumber",
				 * j_jo.getString("ORG_NO"));//企业工商注册号码（社会统一信用代码） jo.put("sex",
				 * "1");//企业负责人或机构负责人性别1：男0：女 jo.put("nation", "汉族");
				 * jo.put("scope", "经营范围"); unitUI.setUserType("fr"); }else
				 * if("11".equals(j_jo.getString("TYPE"))){ jo.put("loginname",
				 * j_jo.getString("ACCOUNT")); jo.put("pwd", m_password);
				 * jo.put("name", j_jo.getString("NAME"));
				 * jo.put("papersnumber", j_jo.getString("CARD_NO"));
				 * jo.put("mobile", j_jo.getString("PHONE")); jo.put("email",
				 * j_jo.getString("EMAIL")); unitUI.setUserType("gr"); }else{
				 * ds.setState(StateType.FAILT);
				 * ds.setMessage("密码修改失败！未知账号类型！"); return ds; } JSONObject
				 * retVal = unitUI.userAdd(jo);
				 * if("0".equals(retVal.getString("result"))){
				 */
				// String sql_map =
				// "update UC_USER_MAP t set t.user_id_map=? where t.user_id=?";
				// DbHelper.update(sql_map, new Object[] {
				// retVal.getString("uuid"), j_jo.getString("ID") },conn);

				String sql = "update uc_user t set t.password=? where t.phone=? and t.type=? ";
				int i = DbHelper.update(sql, new Object[] { password, phone,type },
						conn);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("密码修改失败！");
				} else {
					ds.setMessage("密码修改成功！");
					ds.setState(StateType.SUCCESS);
				}
				/*
				 * }else{ String message = ""; if
				 * ("201".equals(retVal.getString("result"))) { message =
				 * "appmark参数不正确"; } else if
				 * ("202".equals(retVal.getString("result"))) { message =
				 * "sign参数不正确"; } else if
				 * ("203".equals(retVal.getString("result"))) { message =
				 * "args参数不正确"; } else if
				 * ("204".equals(retVal.getString("result"))) { message =
				 * "登录名（或其他必填项）不能为空"; } else if
				 * ("205".equals(retVal.getString("result"))) { message =
				 * "登录名格式不正确"; } else if
				 * ("206".equals(retVal.getString("result"))) { message =
				 * "已经存在相同登录名"; } else if
				 * ("207".equals(retVal.getString("result"))) { message =
				 * "证件号码格式不正确"; } else if
				 * ("208".equals(retVal.getString("result"))) { message =
				 * "证件号码已存在"; } else if
				 * ("209".equals(retVal.getString("result"))) { message =
				 * "手机号码格式不正确"; } else if
				 * ("210".equals(retVal.getString("result"))) { message =
				 * "手机号码重复"; } else if
				 * ("211".equals(retVal.getString("result"))) { message =
				 * "邮箱格式不正确"; } else if
				 * ("212".equals(retVal.getString("result"))) { message =
				 * "邮箱重复"; } else if ("299".equals(retVal.getString("result")))
				 * { message = "其他错误"; } ds.setState(StateType.FAILT);
				 * ds.setMessage("注册失败,"+message); }
				 */
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("账号异常！");
			}
			ds.setRawData(new JSONArray());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 根据用户名密码修改密码modifyPassword
	/**
	 * 根据用户名密码修改密码
	 * @param pSet
	 * @return
	 */
	public DataSet modifyPassword(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String username = (String) pSet.get("username");
		String type = (String) pSet.get("type");
		if (!StringUtil.isNotEmpty(type)){
			type="11";
		}
		String oldpwd = (String) pSet.get("oldpwd");
		oldpwd = mytools.toMD5(oldpwd);
		String newpwd = (String) pSet.get("newpwd");
		String m_password = (String) pSet.get("newpwd");
		newpwd = mytools.toMD5(newpwd);
		String ly = (String) pSet.getParameter("ly");// 来源
		if (!StringUtil.isNotEmpty(ly)){
			ly="1";
		}
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String sql1 = "select * from uc_user where account=? and password=? and type=? and is_inuse='1'";
			DataSet j = DbHelper.query(sql1, new Object[] { username, oldpwd,
					type }, conn);
			System.err.print(j.toString());
			if (j.getTotal() == 1) {
				JSONObject j_jo = j.getJAData().getJSONObject(0);
				/*
				 * JSONObject jo = new JSONObject(); UniteUserInterface unitUI =
				 * new UniteUserInterface();
				 * if("21".equals(j_jo.getString("TYPE"))){ jo.put("loginname",
				 * j_jo.getString("ACCOUNT")); jo.put("pwd", m_password);
				 * jo.put("realname", j_jo.getString("NAME"));
				 * jo.put("paperstype", "1");//证件类型（默认为1） //1- 身份证 //2- 护照 //3-
				 * 港澳通行证 //4- 台湾通行证 //5- 香港身份证 //6- 澳门身份证 //7- 台湾身份证
				 * jo.put("cardnumber ", j_jo.getString("CARD_NO"));
				 * jo.put("mobile", j_jo.getString("PHONE")); jo.put("email",
				 * j_jo.getString("EMAIL")); jo.put("type", "1");//法人类型：1-企业法人
				 * 2-非企业法人/其他组织 jo.put("name",
				 * j_jo.getString("ORG_NAME"));//法人名称（企业名称、机构名称）
				 * jo.put("regnumber",
				 * j_jo.getString("ORG_NO"));//企业工商注册号码（社会统一信用代码） jo.put("sex",
				 * "1");//企业负责人或机构负责人性别1：男0：女 jo.put("nation", "汉族");
				 * jo.put("scope", "经营范围"); unitUI.setUserType("fr"); }else
				 * if("11".equals(j_jo.getString("TYPE"))){ jo.put("loginname",
				 * j_jo.getString("ACCOUNT")); jo.put("pwd", m_password);
				 * jo.put("name", j_jo.getString("NAME"));
				 * jo.put("papersnumber", j_jo.getString("CARD_NO"));
				 * jo.put("mobile", j_jo.getString("PHONE")); jo.put("email",
				 * j_jo.getString("EMAIL")); unitUI.setUserType("gr"); }else{
				 * ds.setState(StateType.FAILT);
				 * ds.setMessage("密码修改失败！未知账号类型！"); return ds; } JSONObject
				 * retVal = unitUI.userAdd(jo);
				 * if("0".equals(retVal.getString("result"))){
				 */
				String sql = "update uc_user t set t.password=? where account=? and password=? and t.type=?";
				int i = DbHelper.update(sql, new Object[] { newpwd, username,
						oldpwd, type }, conn);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("密码修改失败！");
				} else {
					conn.commit();
					ds.setMessage("密码修改成功！");
					ds.setState(StateType.SUCCESS);
				}
				/*
				 * }else{ String message = ""; if
				 * ("201".equals(retVal.getString("result"))) { message =
				 * "appmark参数不正确"; } else if
				 * ("202".equals(retVal.getString("result"))) { message =
				 * "sign参数不正确"; } else if
				 * ("203".equals(retVal.getString("result"))) { message =
				 * "args参数不正确"; } else if
				 * ("204".equals(retVal.getString("result"))) { message =
				 * "登录名（或其他必填项）不能为空"; } else if
				 * ("205".equals(retVal.getString("result"))) { message =
				 * "登录名格式不正确"; } else if
				 * ("206".equals(retVal.getString("result"))) { message =
				 * "已经存在相同登录名"; } else if
				 * ("207".equals(retVal.getString("result"))) { message =
				 * "证件号码格式不正确"; } else if
				 * ("208".equals(retVal.getString("result"))) { message =
				 * "证件号码已存在"; } else if
				 * ("209".equals(retVal.getString("result"))) { message =
				 * "手机号码格式不正确"; } else if
				 * ("210".equals(retVal.getString("result"))) { message =
				 * "手机号码重复"; } else if
				 * ("211".equals(retVal.getString("result"))) { message =
				 * "邮箱格式不正确"; } else if
				 * ("212".equals(retVal.getString("result"))) { message =
				 * "邮箱重复"; } else if ("299".equals(retVal.getString("result")))
				 * { message = "其他错误"; } ds.setState(StateType.FAILT);
				 * ds.setMessage("注册失败,"+message); }
				 */
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("账号异常！");
			}
			ds.setRawData(new JSONArray());
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 获取用户信息getUserInfoByToken
	/**
	 * 获取用户信息
	 */
	public DataSet getUserInfoByToken(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		String ly = (String) pSet.get("ly");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "select phone,email,name,card_no,address from uc_user where id=?";
			ds = DbHelper.query(sql, new Object[] { ucid}, conn);
			if (ds.getTotal() != 1) {
				ds.setState(StateType.FAILT);
				ds.setMessage("账号异常！");
				ds.setRawData(new JSONArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region办件列表 getDisplayListByPage
	/**
	 * 办件列表getDisplayListByPage
	 */
	public DataSet getDisplayListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String region = (String) pSet.getParameter("region_code");
			if (!StringUtils.isNotEmpty(region)) {
				region = SecurityConfig.getString("WebRegion");
			}
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			
			Map<String, String> map = new HashMap<String, String>();
			String page = pSet.getParameter("page").toString();
			String rows = pSet.getParameter("rows").toString();			
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			String keyword = (String) pSet.remove("keyword");
			if (StringUtils.isNotEmpty(keyword)) {
				whereValue
						.append(" and ( ORG_NAME like ? or APPLY_SUBJECT like ? or APPLICANT like ?)");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
			}			
			String ORG_CODE = (String) pSet.getParameter("org_code");
			if (StringUtils.isNotEmpty(ORG_CODE)) {
				whereValue.append(" and org_code=?");
				paramValue.add(ORG_CODE);
			}
			String item_id = (String) pSet.getParameter("item_id");
			if (StringUtils.isNotEmpty(item_id)) {
				whereValue.append(" and item_id=?");
				paramValue.add(item_id);
			}
			String APPLICANT = (String) pSet.getParameter("APPLICANT");
			if (StringUtils.isNotEmpty(APPLICANT)) {
				whereValue.append(" and APPLICANT like ?");
				paramValue.add("%" + APPLICANT + "%");
			}
			String APPLY_SUBJECT = (String) pSet.getParameter("APPLY_SUBJECT");
			if (StringUtils.isNotEmpty(APPLY_SUBJECT)) {
				whereValue.append(" and APPLY_SUBJECT like ?");
				paramValue.add("%" + APPLY_SUBJECT + "%");
			}
			String startTime = (String) pSet.getParameter("startTime");
			if (StringUtils.isNotEmpty(startTime)) {
				map.put("startTime", startTime);
			}
			String endTime = (String) pSet.getParameter("endTime");
			if (StringUtils.isNotEmpty(endTime)) {
				map.put("endTime", endTime);
			}
			String power = (String) pSet.getParameter("power");
			if (StringUtils.isNotEmpty(power)) {
				whereValue.append(" and type = ?");
				paramValue.add(power);
			}
			String state = (String) pSet.getParameter("state");
			if (StringUtils.isNotEmpty(state)) {
				if("办结".equals(state)){
					whereValue.append(" and (state=? or state=? or state=?)");
					paramValue.add("98");
					paramValue.add("99");
					paramValue.add("90"); //添加state为90办结并出证状态
				}else{
					whereValue.append(" and (state!=? and state!=? and state!=?)");
					paramValue.add("98");
					paramValue.add("99");
					paramValue.add("90"); //添加state为90办结并出证状态
				}				
			}
			String item_code = (String) pSet.getParameter("item_code");
			if (StringUtils.isNotEmpty(item_code)) {
				whereValue.append(" and item.item_code = ?");
				paramValue.add(item_code);
			}			
			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			String order_mark = (String) pSet.getParameter("order_mark");
			if ("desc".equals(order_mark)) {
				map.put("orderby", "order by finish_time desc");
			}
			if ("asc".equals(order_mark)) {
				map.put("orderby", "order by finish_time asc");
			}
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	//endregion
	
	//region 根据流水号密码查询办件详情getBusinessProgressQueryByPassword
	public DataSet getBusinessProgressQueryByPassword(ParameterSet pset) {		
		DataSet ds = new DataSet();
		String password = (String) pset.remove("password");
		String receiveNum = (String) pset.remove("receiveNum");
		Object ret;
		try {
				HttpClientUtil client = new HttpClientUtil();
				String urlSp = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo?receiveNumber="+receiveNum+"&password="+password);//panyl
				//String urlQl = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")+"/getAllBusinessInfo?receiveNumber="+receiveNum+"&password="+password);
				ret = client.getResult(urlSp,"");
				if("200".equals(JSONObject.fromObject(ret).getString("state"))){
					JSONObject obj = new JSONObject();		
					obj =  JSONObject.fromObject(ret);
					ds.setRawData(obj);
					ds.setState(StateType.SUCCESS);
					ds.setTotal(1);
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage(JSONObject.fromObject(ret).getString("error"));
				}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	//endregion

	//region 生成验证码generateVerifyCode
	/**
	 * 生成验证码
	 * @param pSet
	 * @return
	 */
	public DataSet generateVerifyCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access_token = (String) pSet.get(ACCESS_TOKEN);
		SecureRandom random = new SecureRandom();
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < 6; i++) {
			String rand = String
					.valueOf(CHARS[random.nextInt(CHARS.length - 1)]);
			sRand.append(rand);
		}
		CacheManager.set("VerifyCode", "generateVerifyCode" + access_token,
				sRand.toString());
		CacheManager.set("VerifyCodeTime", "generateVerifyCodeTime"
				+ access_token, new Date().getTime() + "");

		String phoneNum = (String) pSet.get("phoneNum");
		String content = "您的短信验证码为：" + sRand.toString();
		Boolean type = sendMessage(phoneNum, content, "others");
		if (type) {
			ds.setState(StateType.SUCCESS);
			ds.setRawData(new JSONArray());
			ds.setMessage("已发送！");
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("验证码发送失败！");
		}
		return ds;
	}
	//endregion 
	
	//region 验证验证码verifyCode
	/**
	 * 验证验证码
	 * @param pSet
	 * @return
	 */
	public DataSet verifyCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access_token = (String) pSet.get(ACCESS_TOKEN);
		String verify = (String) CacheManager.get("VerifyCode",
				"generateVerifyCode" + access_token);
		String time = (String) CacheManager.get("VerifyCodeTime",
				"generateVerifyCodeTime" + access_token);
		Date verifyTime = new Date(Long.parseLong(time));
		long nowtimelong = System.currentTimeMillis();
		long ctimelong = verifyTime.getTime();
		long result = Math.abs(nowtimelong - ctimelong);
		if (result > 5 * 60000) {// 超过5*1分钟
			ds.setState(StateType.FAILT);
			ds.setMessage("验证码已超期！");
		} else {
			String VerifyCode = (String) pSet.get("VerifyCode");
			if (VerifyCode != null && !"".equals(VerifyCode)) {
				if (!VerifyCode.equalsIgnoreCase(verify)) {
					ds.setState(StateType.FAILT);
					ds.setMessage("验证码输入错误,请重新输入！");
				} else {
					ds.setState(StateType.SUCCESS);
					ds.setMessage("验证通过！");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误,请重新输入！");
			}
		}
		return ds;
	}
	//endregion
	/**
	 * 发送短信
	 * 
	 * @param phone
	 * @param content
	 * @param status
	 * @return
	 */
	public Boolean sendMessage(String phone, String content, String status) {
		Boolean type = false;
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String sql = "insert into pub_sms t (id,smscontent,sendtime,channel,status,telephone) values (?,?,sysdate,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { Tools.getUUID32(),
					content, status, "0", phone }, conn);
			if (i == 0) {
				type = false;
			} else {
				type = true;
			}
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			type = false;
		} finally {
			DBSource.closeConnection(conn);
		}
		return type;
	}
	//endregion
	
	//region 获取站点
	public DataSet getWebSite(ParameterSet pSet){
		Command cmd = new Command("app.pmi.config.cmd.WebsiteCmd");		
		return cmd.execute("getWebsiteConfig");		
	}
	//endregion
	
	
	public DataSet BusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String region = (String) pSet.getParameter("region_code");
			if (!StringUtils.isNotEmpty(region)) {
				region = SecurityConfig.getString("WebRegion");
			}
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			
			if("weihai".equals( SecurityConfig.getString("AppId"))){
				url = HttpUtil.formatUrl(SecurityConfig
						.getString("approval_url") + "/newgetDisplayListByPage");
			}
			
			Map<String, String> map = new HashMap<String, String>();

			String page = pSet.getPageStart() + "";
			String rows = pSet.getPageLimit() + "";

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			String keyword = (String) pSet.remove("keyword");
			if (StringUtils.isNotEmpty(keyword)) {
				whereValue
						.append(" and ( ORG_NAME like ? or APPLY_SUBJECT like ? or APPLICANT like ?)");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
			}
			//献县 需要查询所有数据，不需要区划条件 by：yanhao
			String pageMode = SecurityConfig.getString("PAGE_MODE");
			if(!"xianxian".equals(pageMode)){
				whereValue.append(" and region_code=?");
				paramValue.add(region);
			}
			String ORG_CODE = (String) pSet.getParameter("org_code");
			if (StringUtils.isNotEmpty(ORG_CODE)) {
				whereValue.append(" and org_code=?");
				paramValue.add(ORG_CODE);
			}

			String item_id = (String) pSet.getParameter("item_id");
			if (StringUtils.isNotEmpty(item_id)) {
				whereValue.append(" and item_id=?");
				paramValue.add(item_id);
			}

			String APPLICANT = (String) pSet.getParameter("APPLICANT");
			if (StringUtils.isNotEmpty(APPLICANT)) {
				whereValue.append(" and APPLICANT like ?");
				paramValue.add("%" + APPLICANT + "%");
			}

			String APPLY_SUBJECT = (String) pSet.getParameter("APPLY_SUBJECT");
			if (StringUtils.isNotEmpty(APPLY_SUBJECT)) {
				whereValue.append(" and APPLY_SUBJECT like ?");
				paramValue.add("%" + APPLY_SUBJECT + "%");
			}

			String startTime = (String) pSet.getParameter("startTime");
			if (StringUtils.isNotEmpty(startTime)) {
				map.put("startTime", startTime);
			}
			String endTime = (String) pSet.getParameter("endTime");
			if (StringUtils.isNotEmpty(endTime)) {
				map.put("endTime", endTime);
			}
			String power = (String) pSet.getParameter("power");
			if (StringUtils.isNotEmpty(power)) {
				whereValue.append(" and type = ?");
				paramValue.add(power);
			}
			String state = (String) pSet.getParameter("state");
			if (StringUtils.isNotEmpty(state)) {
				if("办结".equals(state)){
					whereValue.append(" and (state=? or state=? or state=?)");
					paramValue.add("98");
					paramValue.add("99");
					paramValue.add("90"); //添加state为90办结并出证状态
				}else{
					whereValue.append(" and (state!=? and state!=? and state!=?)");
					paramValue.add("98");
					paramValue.add("99");
					paramValue.add("90"); //添加state为90办结并出证状态
				}				
			}
			String result_mark = (String) pSet.getParameter("result_mark");
			if ("lc".equals(result_mark)) {
				whereValue.append(" and (state=? or state=? or state=?)");
				paramValue.add("98");
				paramValue.add("99");
				paramValue.add("90"); //添加state为90办结并出证状态
			}
			
			//聊城项目个性化需求
			String lc_mark = (String) pSet.getParameter("lc_mark");
			if ("liaocheng".equals(lc_mark)) {
				//聊城项目，人防办的事项：结合民用建筑修建防空地下室审批 屏蔽掉
				whereValue.append(" and item.item_code <> ? ");
				paramValue.add("371500-rfb-XK-001-01");
				whereValue.append(" and receive_number <> ? ");
				paramValue.add("151001129769");
			}
			
			String item_code = (String) pSet.getParameter("item_code");
			if (StringUtils.isNotEmpty(item_code)) {
				whereValue.append(" and item.item_code = ?");
				paramValue.add(item_code);
			}
			// 过滤掉申请人为空的情况在外网不进行展示
			//重庆不作过滤处理
			if(!"chq".equals( SecurityConfig.getString("AppId"))){
				whereValue.append(" and APPLICANT is not null ");
			}
			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			String order_mark = (String) pSet.getParameter("order_mark");
			if ("de".equals(order_mark)) {
				map.put("orderby", "order by finish_time desc");
				// map.put("order", "order by type desc,last_time desc");
			}
			if ("as".equals(order_mark)) {
				map.put("orderby", "order by finish_time asc");
			}
			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
}