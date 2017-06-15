package api.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jdo.conf.Database;

import app.icity.ServiceCmd;
import app.icity.guestbook.WriteDao;
import app.icity.onlineapply.ApplyDao;
import app.icity.sync.UniteUserInterface;
import app.uc.GetUserMapDao;
import app.uc.UserDao;
import app.util.RestUtil;

import com.commnetsoft.proxy.SsoClient;
import com.commnetsoft.proxy.model.CallResult;
import com.commnetsoft.proxy.model.UserInfo;
import com.commnetsoft.proxy.model.ValidationResult;
import com.commnetsoft.proxy.util.MD5;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.IdGenereator;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.timevale.SecureUtils;

import core.util.CommonUtils;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class WechatImpl extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(WechatImpl.class);
	private static api.tools.Tools mytools = new api.tools.Tools();
	public static WechatImpl getInstance() {
		return DaoFactory.getDao(WechatImpl.class.getName());
	}
	//region 微信登录舟山统一认证平台 gr wechatLoginGr
	/**
	 * 微信登录舟山统一认证平台 gr
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings({ "static-access", "deprecation" })
	public DataSet wechatLoginGr(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SsoClient client = SsoClient.getInstance();
		client.initConfig(SecurityConfig.getString("servicecode"),
				SecurityConfig.getString("servicepwd"),
				SecurityConfig.getString("serviceurl"));
		CallResult loginR = client.login((String) pSet.get("loginname"), "",
				(String) pSet.get("password"));
		if ("0".equals(loginR.getResult())) {
			ValidationResult tvm = new ValidationResult();
			if (loginR instanceof ValidationResult) {
				tvm = (ValidationResult) loginR;
			}
			String token = tvm.getToken();
			UserInfo uinfo;
			uinfo = client.getUser(token);
			// _log.info("userInfo-----"+uinfo);
			String ID = uinfo.getUserid();
			Command cmdd = new Command("app.uc.GetUserMapDao");
			cmdd.setParameter("user_id_map", ID);
			DataSet dsd = cmdd.execute("GetUid");
			if (dsd.getState() == StateType.SUCCESS) {
				ID = dsd.getJAData().getJSONObject(0).getString("USER_ID");
			}
			JSONArray users = new JSONArray();
			JSONObject user = new JSONObject();
			user.put("ACCOUNT", uinfo.getLoginname());
			user.put("PHONE", uinfo.getMobile());
			user.put("CARD_NO", uinfo.getIdnum());
			user.put("ID", ID);
			user.put("NAME", uinfo.getUsername());
			users.add(user);
			ds.setRawData(users);
			ds.setState(StateType.SUCCESS);
		} else {
			System.out.println("调用返回结果 result：" + loginR.getResult()
					+ "；错误详细信息：" + loginR.getErrmsg());
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	//endregion
	
	//region 微信登录舟山统一认证平台 fr  wechatLoginFr
	/**
	 * 微信登录舟山统一认证平台 fr
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public DataSet wechatLoginFr(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("login_fr_opensso"))
					+ "?Username="
					+ (String) pSet.get("loginname")
					+ "&Password="
					+ (String) pSet.get("password");
			HttpClientUtil client = new HttpClientUtil();
			String ret = client
					.getResult(url, "")
					.split("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\">")[1];
			Map<String, Object> attrs = SecureUtils.VerifyData(ret);
			String companyName = attrs.get("CompanyName").toString(); // 企业名称
			// 企业登录账户分企业法人、企业非法人
			Object companyRegNumber = attrs.get("CompanyRegNumber"); // 工商注册号
			Object organizationNumber = attrs.get("OrganizationNumber"); // 机构代码
			String account = "";
			String userid = attrs.get("userId").toString(); // 标识
			if (companyRegNumber != null) {
				account = (String) companyRegNumber;
			}
			if (organizationNumber != null) {
				account = (String) organizationNumber;
			}
			JSONArray jos = new JSONArray();
			JSONObject jo = new JSONObject();
			jo.put("ACCOUNT", account);
			jo.put("PHONE", "");
			jo.put("CARD_NO", account);
			jo.put("ID", userid);
			jo.put("ORG_NAME", companyName);
			jo.put("TYPE", 21);
			jos.add(jo);
			ds.setRawData(jos);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("调用法人登录接口失败");
		}
		return ds;
	}
	//endregion
	
	//region 山东根据用户名,手机号，邮箱，密码登录login
	/**
	 * 山东根据用户名,手机号，邮箱，密码登录
	 */
	public DataSet loginSd(ParameterSet pSet) {
		String loginname = (String) pSet.get("loginname");
		String password = (String) pSet.get("password");
		String m_password = (String) pSet.get("password");
		password = mytools.toMD5(password);		
		DataSet ds = new DataSet();		
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String setUserType = pSet.containsKey("setUserType") ? (String) pSet
					.get("setUserType") : "gr";// 个人1法人2
			UniteUserInterface unitUI = new UniteUserInterface();
			unitUI.setUserType("gr");
			JSONObject retVal = unitUI.getTokenInfoByUser(loginname,
					m_password);
			if (!retVal.containsKey("token")) {
				unitUI.setUserType("fr");
				unitUI.getTokenInfoByUser(loginname, m_password);
			}
			JSONObject userInfo = unitUI.getUserInfo();
			System.out.print("userInfo1：" + userInfo);
			if (userInfo == null) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名或密码错误！");
			} else {
				JSONObject uInfo = new JSONObject();
				uInfo.put("ACCOUNT", userInfo.get("loginname"));
				uInfo.put("NAME", userInfo.get("name"));
				uInfo.put("TYPE",
						"2".equals(userInfo.get("usertype")) ? "21"
								: "11");
				uInfo.put("CARD_NO", "2".equals(userInfo
						.get("usertype")) ? userInfo.get("idcard")
						: userInfo.get("cardid"));
				uInfo.put("ORG_NAME", "2".equals(userInfo
						.get("usertype")) ? userInfo.get("name")
						: userInfo.get("workunit"));
				uInfo.put(
						"ORG_NO",
						"2".equals(userInfo.get("usertype")) ? userInfo
								.get("orgnumber") : "");
				uInfo.put("PHONE", userInfo.get("mobile"));
				uInfo.put("CREDIT_CODE", "");
				uInfo.put("ICREGNUMBER", "");

				String select = "SELECT * FROM UC_USER WHERE ACCOUNT =? or PHONE=? or EMAIL=?";
				DataSet m_ds_select = DbHelper
						.query(select, new Object[] { loginname,
								loginname, loginname }, conn);
				if (m_ds_select.getTotal() > 0) {
					uInfo.put("ID", m_ds_select.getJAData()
							.getJSONObject(0).getInt("ID")
							+ "");
				} else {
					Command cmd = new Command("app.uc.GetUserMapDao");
					cmd.setParameter("user_id_map",
							userInfo.getString("uuid"));
					cmd.setParameter("uInfo", uInfo);
					DataSet m_ds = cmd.execute("GetUid");
					int ID;
					if (m_ds.getState() == StateType.SUCCESS) {
						ID = m_ds.getJAData().getJSONObject(0)
								.getInt("USER_ID");
						uInfo.put("ID", ID + "");
					}
				}					
				ds.setRawData(uInfo);						
				ds.setState(StateType.SUCCESS);
				ds.setMessage("登录成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion	
	
	//region 获取微信咨询列表getGuestBookList
	/**
	 * 获取微信咨询列表
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet getGuestBookList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String user_id = (String) pSet.get("user_id");
			String ly = (String) pSet.get("ly");
			String isall = (String) pSet.get("isall");
			String sql = null;
			String sqlAll = null;
			sql = "select t.id,t.title,t.type,t.content,t.status,t.write_date,t.deal_result,t.deal_date from GUESTBOOK t where t.user_id = ? and t.ly = ? order by t.write_date desc";
			if(StringUtils.isNotEmpty(isall)&&"1".equals(isall)){
				//微信可以查看网办咨询的问题 忽略来源这个条件
				sqlAll = "select t.id,t.title,t.type,t.content,t.status,t.write_date,t.deal_result,t.deal_date from GUESTBOOK t where t.user_id = ?  order by t.write_date desc";
				ds = DbHelper.query(sqlAll, new Object[] { user_id}, conn);
			}else{
				ds = DbHelper.query(sql, new Object[] { user_id, ly }, conn);
			}
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 获取咨询投诉详情getGuestBookDetail
	/**
	 * 获取咨询投诉详情
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet getGuestBookDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String id = (String) pSet.get("id");
			String sql = null;
			sql = "select t.id,t.depart_name,t.username,t.phone,t.email,t.address,t.title,t.type,t.content,t.status,t.user_ip,t.write_date,t.replayer,t.deal_result,t.deal_date,t.reply_ip,t.checks,t.open,t.warn,t.busi_id,t.busi_type,t.satisfy,t.useful,t.notuseful,t.depart_id,t.isanonymous,t.give_status,t.give_dept_id,t.give_dept_name,t.user_id,t.sms_alert_already, t.sxbm,t.region_id,t.sxmc,t.sxid,t.url from GUESTBOOK t where t.id = ?";
			ds = DbHelper.query(sql, new Object[] { id }, conn);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 保存咨询、投诉saveGuestBook
	/**
	 * 保存咨询、投诉
	 * @param pSet
	 * @return
	 */
	public DataSet saveGuestBook(ParameterSet pSet) throws ParseException {
		// String ID = Tools.getUUID32();
		String ID = "";
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String sql = "INSERT INTO GUESTBOOK (ID,DEPART_NAME,USERNAME,USER_ID,PHONE,EMAIL,ADDRESS,TITLE,TYPE,CONTENT,STATUS,WRITE_DATE,DISPENSETIMEOUT,ACCEPTTIMEOUT,ACCEPTTIMEWARN,DEPART_ID,ISANONYMOUS,URL,SXBM,SXMC,SXID,REGION_ID,LY,OPENID)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String DEPART_NAME = (String) pSet.getParameter("DEPART_NAME");
		String LY = (String) pSet.getParameter("LY");
		Date WRITE_DATE = ChangeDateFormat(new Date(),
				CommonUtils.YYYY_MM_DD_HH_mm_SS);
		Date DISPENSETIMEOUT = null;
		Date ACCEPTTIMEOUT = null;
		Date ACCEPTTIMEWARN = null;

		/*
		 * Calendar ThisDay = Calendar.getInstance(); ThisDay.setTime(new
		 * Date()); ThisDay.add(Calendar.DAY_OF_YEAR,3);//加一天（申请当天不计入预警和超时期限）
		 * String StartTimqe =
		 * Tools.formatDate(ThisDay.getTime(),CommonUtils.YYYY_MM_DD);
		 */

		String StartTime = Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD);
		if (StringUtil.isEmpty(DEPART_NAME)) {
			DISPENSETIMEOUT = CommonUtils.getInstance().parseStringToTimeStamp(
					GetWorkDate(StartTime, "2"), CommonUtils.YYYY_MM_DD);
		} else {
			ACCEPTTIMEWARN = CommonUtils.getInstance().parseStringToTimeStamp(
					GetWorkDate(StartTime, "2"), CommonUtils.YYYY_MM_DD);
			ACCEPTTIMEOUT = CommonUtils.getInstance().parseStringToTimeStamp(
					GetWorkDate(StartTime, "3"), CommonUtils.YYYY_MM_DD);
		}

		try {
			conn.setAutoCommit(false);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			ID = sdf.format(new Date());
			int i = DbHelper.update(
					sql,
					new Object[] {
							ID,
							URLDecoder.decode(DEPART_NAME, "utf-8"),
							URLDecoder.decode(
									(String) pSet.getParameter("USERNAME"),
									"utf-8"),
							(String) pSet.getParameter("USER_ID"),
							(String) pSet.getParameter("PHONE"),
							(String) pSet.getParameter("EMAIL"),
							(String) pSet.getParameter("ADDRESS"),
							URLDecoder.decode(
									(String) pSet.getParameter("TITLE"),
									"utf-8"),
							(String) pSet.getParameter("TYPE"),
							URLDecoder.decode(
									(String) pSet.getParameter("CONTENT"),
									"utf-8"),
							(String) pSet.getParameter("STATUS"), WRITE_DATE,
							DISPENSETIMEOUT, ACCEPTTIMEOUT, ACCEPTTIMEWARN,
							(String) pSet.getParameter("DEPART_ID"),
							(String) pSet.getParameter("ISANONYMOUS"),
							(String) pSet.getParameter("URL"),
							(String) pSet.getParameter("SXBM"),
							(String) pSet.getParameter("SXMC"),
							(String) pSet.getParameter("SXID"),
							(String) pSet.getParameter("REGION_ID"), LY,
							(String) pSet.getParameter("OPENID") }, conn);
			conn.commit();
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	//endregion
	
	//region   ChangeDateFormat
	private Date ChangeDateFormat(Date OriginalDate, String Format) {
		String OriginalDateString = Tools.formatDate(OriginalDate, Format);
		return CommonUtils.getInstance().parseStringToTimeStamp(
				OriginalDateString, Format);
	}
	//endregion
	
	//region 获取工作日GetWorkDate
	private String GetWorkDate(String StartTime, String TimeOut) {
		HttpClientUtil HCU = new HttpClientUtil();
		String UrlPath = HttpUtil.formatUrl(SecurityConfig
				.getString("webSiteUrl")
				+ "/web/c/getWorkDate?startTime="
				+ StartTime + "&timeout=" + TimeOut);
		JSONObject JsonObj = JSONObject.fromObject(HCU.getResult(UrlPath, ""));
		return JsonObj.getString("data");
	}
	//endregion

	//region 咨询、投诉 回复updateGuestBook
	/**
	 * 咨询、投诉 回复 status CHAR(1) Y '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理
	 * replayer 回复部门 dealResult 回复内容 id type VARCHAR2(10 CHAR) Y
	 * 信件类别，分为咨询-2、投诉-3、意见建议-10、纠错-11、求助-12、其他
	 */
	public DataSet updateGuestBook(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String replayer = (String) pSet.get("replayer");
			String dealResult = (String) pSet.get("dealResult");
			String status = (String) pSet.get("status");
			String id = (String) pSet.get("id");
			String type = (String) pSet.get("type");
			String sql = "update guestbook set REPLAYER=?, DEAL_RESULT = ? , DEAL_DATE = SYSDATE ,STATUS = ? where id=? and type=?";
			int i = DbHelper.update(sql, new Object[] { replayer, dealResult,
					status, id, type }, conn);
			conn.commit();
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	//endregion
	
	//region 获取微信咨询列表getGuestBookListByOpenid
	/**
	 * 获取微信咨询列表
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet getGuestBookListByOpenid(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String openid = (String) pSet.get("openid");
			String ly = (String) pSet.get("ly");
			String sql = null;
			sql = "select t.id,t.title,t.type,t.content,t.status,t.write_date,t.deal_result,t.deal_date from GUESTBOOK t where t.openid = ? and t.ly = ? order by t.write_date desc";
			ds = DbHelper.query(sql, new Object[] { openid, ly }, conn);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}

		return ds;
	}
	//endregion
	
	//region 根据用户名密码查询此用户，使用省统一身份认证方法GetUserUniteIdentifyDetail
	/**
	 * 根据用户名密码查询此用户，使用省统一身份认证方法
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
    public DataSet GetUserUniteIdentifyDetail(ParameterSet pSet)  {
		DataSet ds = new DataSet();
		String loginname = (String) pSet.get("loginname");
		String password = (String) pSet.get("password");
		String type = (String) pSet.get("type");
		if("11".equals(type)){
			type="gr";
		}else{
			type="fr";
		}
		UniteUserInterface unitUI = new UniteUserInterface();
		unitUI.setUserType(type);
		try {
			JSONObject retVal = unitUI.getTokenInfoByUser(loginname,password);
			if (!retVal.containsKey("token")) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名或密码错误!");
				return ds;
			}else{
				JSONObject userInfo = unitUI.getUserInfo();
				if (userInfo == null) {
					ds.setState(StateType.FAILT);
					ds.setMessage("用户名或密码错误！");
					return ds;
				} else {
					JSONObject uInfo = new JSONObject();
					uInfo.put("ACCOUNT", userInfo.get("loginname"));
					uInfo.put("NAME", userInfo.get("name"));
					String type1="11";//默认是个人用户
					if("2".equals(userInfo.get("usertype"))){
						//法人类型：1-企业法人,2-非企业法人
						type1="1".equals(userInfo.getString("type"))?"21":"31";
					}
					uInfo.put("TYPE", type1);
					uInfo.put("SEX", userInfo.get("sex"));
					uInfo.put("EMAIL",userInfo.get("email"));
					uInfo.put("ADDRESS", userInfo.get("address"));
					uInfo.put("STATUS", userInfo.get("isauth"));
					uInfo.put("PHONE", userInfo.get("mobile"));
					uInfo.put("CARD_NO", "2".equals(userInfo.get("usertype"))?userInfo.get("idcard"):userInfo.get("cardid"));
					uInfo.put("ORG_NAME", "2".equals(userInfo.get("usertype"))?userInfo.get("name"):"");
					uInfo.put("ORG_NO", "2".equals(userInfo.get("usertype"))?userInfo.get("orgnumber"):"");
					uInfo.put("ORG_BOSS_NAME", userInfo.containsKey("realname")?userInfo.get("realname"):"");
					uInfo.put("ICREGNUMBER", "2".equals(userInfo.get("usertype"))?userInfo.get("regnumber"):"");
					uInfo.put("CREDIT_CODE", "");
					Command cmd = new Command("app.uc.GetUserMapDao");
		    		cmd.setParameter("user_id_map", userInfo.getString("uuid"));
		    		cmd.setParameter("uInfo", uInfo);
		    		DataSet dsGetUid = cmd.execute("GetUid");
		    		int ID;
		    		if (dsGetUid.getState() == StateType.SUCCESS) {
		    			ID = dsGetUid.getJAData().getJSONObject(0).getInt("USER_ID");
		    			uInfo.put("USERID", ID);
		    		}
					JSONArray jsonArrays=new JSONArray();
					jsonArrays.add(uInfo);
					ds.setRawData(jsonArrays);
				}
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("用户名或密码错误！");
			return ds;
		}
		return ds;
	}
	//endregion 
	
	//region 根据用户名密码查询此用户，如果有此用户，存一条数据到wechat_user表（外网用户与微信用户的绑定关系表）GetUserDetailLogin
	/**
	 * 根据用户名密码查询此用户，如果有此用户，存一条数据到wechat_user表（外网用户与微信用户的绑定关系表）
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet GetUserDetailLogin(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String loginname = (String) pSet.get("loginname");
		String password = (String) pSet.get("password");
		password=MD5.MD5Encode(password);//质保商丘出现登录错误，改的时候需要和微信协调同时改！！
		String type = (String) pSet.get("type");
		if ("zs".equals(SecurityConfig.getString("AppId"))) {
			if ("11".equals(type)) {
				return wechatLoginGr(pSet);
			} else if ("21".equals(type)) {
				return wechatLoginFr(pSet);
			} else {
				ds.setState(StateType.FAILT);
				return ds;
			}
		}
		String id = Tools.getUUID32();
		DataSet ds_select = new DataSet();
		DataSet ds_userinfo;
		Connection conn = DbHelper.getConnection("icityDataSource");
		String select = "SELECT ID USERID,ACCOUNT,STATUS,CREATION_TIME,PHONE,CARD_NO,EMAIL,ADDRESS,ORG_NAME,NAME,photo_uri FROM UC_USER WHERE ACCOUNT =? and PASSWORD = ? and type=?";
		String sql_insert = "INSERT INTO wechat_user (ID,OPENID,LOGINNAME,USERID,STATUS,ACCESSTIME)VALUES(?,?,?,?,?,?)";
		try {
			conn.setAutoCommit(false);
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			int i = 0;
			ds_select = DbHelper.query(select, new Object[] { loginname,
					password, type }, conn);
			JSONObject record = ds_select.getRecord(0);
			String userId = record.getString("USERID");
			String select_userinfo = "SELECT USERID FROM wechat_user WHERE  USERID = ?";
			ds_userinfo = DbHelper.query(select_userinfo,
					new Object[] { userId }, conn);
			JSONObject record_userinfo = ds_userinfo.getRecord(0);
			if (userId != null) {
				if (record_userinfo == null) {
					String status = record.getString("STATUS");
					i = DbHelper.update(sql_insert, new Object[] { id, userId,
							loginname, userId, status, now }, conn);
				} else {
					i = 1;
				}
			}
			conn.commit();
			if (i == 0) {
				ds_select.setState(StateType.FAILT);
				ds_select.setMessage("数据库操作失败！");
			} else {
				ds_select.setState(StateType.SUCCESS);
				ds_select.setMessage("OK");
			}
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
	
	//region 根据用户名密码查询此用户，如果有此用户，存一条数据到wechat_user表（外网用户与微信用户的绑定关系表）GetUserDetail
	/**
	 * 根据用户名密码查询此用户，如果有此用户，存一条数据到wechat_user表（外网用户与微信用户的绑定关系表）
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet GetUserDetail(ParameterSet pSet) {
		String loginname = (String) pSet.get("loginname");
		String password = (String) pSet.get("password");
		String openid = (String) pSet.get("openid");
		String id = Tools.getUUID32();
		DataSet ds_select = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String select = "SELECT ID USERID,ACCOUNT,STATUS,CREATION_TIME,PHONE,CARD_NO,EMAIL,ADDRESS,ORG_NAME,NAME FROM UC_USER WHERE ACCOUNT =? and PASSWORD = ?";
		String sql_insert = "INSERT INTO wechat_user (ID,OPENID,LOGINNAME,USERID,STATUS,ACCESSTIME)VALUES(?,?,?,?,?,?)";
		try {
			conn.setAutoCommit(false);
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			int i = 0;
			ds_select = DbHelper.query(select, new Object[] { loginname,
					password }, conn);
			JSONObject record = ds_select.getRecord(0);
			String userId = record.getString("USERID");
			if (userId != null) {
				String status = record.getString("STATUS");
				i = DbHelper.update(sql_insert, new Object[] { id, openid,
						loginname, userId, status, now }, conn);
			}
			conn.commit();
			if (i == 0) {
				ds_select.setState(StateType.FAILT);
				ds_select.setMessage("数据库操作失败！");
			} else {
				ds_select.setState(StateType.SUCCESS);
				ds_select.setMessage("OK");
			}
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
	
	//region根据openid查询wechat_user表，是否有此数据，判断是否绑定了ExistOpenid
	/**
	 * 根据openid查询wechat_user表，是否有此数据，判断是否绑定了
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet ExistOpenid(ParameterSet pSet) {
		String openid = (String) pSet.get("openid");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = null;
			sql = "SELECT ID,OPENID,LOGINNAME,USERID,STATUS FROM wechat_user WHERE  OPENID = ?";
			ds = DbHelper.query(sql, new Object[] { openid }, conn);
			JSONObject record = ds.getRecord(0);
			if (record != null) {
				ds.setMessage("true");
			} else {
				ds.setMessage("false");
			}
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region根据openid查询wechat_user表，查询UserId   GetUserId
	/**
	 * 根据openid查询wechat_user表，查询UserId
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	public DataSet GetUserId(ParameterSet pSet) {
		String openid = (String) pSet.get("openid");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = null;
			sql = "SELECT ID,OPENID,LOGINNAME,USERID,STATUS FROM wechat_user WHERE  OPENID = ?";
			ds = DbHelper.query(sql, new Object[] { openid }, conn);
			JSONObject record = ds.getRecord(0);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region获取满意度评价查询结果getPjResult
	/**
	 * 获取满意度评价查询结果
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet getPjResult(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sbrxm = (String) pSet.get("sbrxm");
			String sblsh = (String) pSet.get("sblsh");
			String sql = "select sxbm from business_index where sblsh= ? and sqrmc = ? and state='99' ";
			ds = DbHelper.query(sql, new Object[] { sblsh, sbrxm }, conn);
			JSONObject record = ds.getRecord(0);
			if (record != null) {
				String sql1 = "select a.SBLSH,a.SXBM,a.SXMC,a.SQRMC,a.SBXMMC,a.SLSJ,a.SJDWDM,a.SJDW,a.STATE,a.SQRZJHM from business_index a where sblsh = ? ";
				ds = DbHelper.query(sql1, new Object[] { sblsh }, conn);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage("false");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 确认评价insert_pl
	/**
	 * 确认评价
	 * @param pSet
	 * @return
	 */
	public DataSet insert_pl(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String sql = "insert into STAR_LEVEL_EVALUATION(ID, SERVICE_CODE, BSR_NAME, BSR_IDENTITY_NUMBER,SERIAL_NUMBER, STAR_LEVEL, EVALUATE_CONTENT, NOTES, CREATOR_DATE) values(?,?,?,?,?,?,?,?,?)";
		try {
			conn.setAutoCommit(false);
			int i = DbHelper
					.update(sql,
							new Object[] {
									Tools.getUUID32(),
									(String) pSet.getParameter("sxbm"),
									(String) pSet.getParameter("sbrxm"),
									(String) pSet.getParameter("sqrzjhm"),
									(String) pSet.getParameter("sblsh"),
									Integer.parseInt(pSet.getParameter("py")
											.toString()),
									(String) pSet.getParameter("py"),
									"",
									CommonUtils
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils.YYYY_MM_DD_HH_mm_SS)

							}, conn);
			conn.commit();
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setMessage("OK");
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	//endregion
	
	//region 获取微信咨询列表（手机审批客户端，分页）getGuestBookListByPage
	/**
	 * 获取微信咨询列表（手机审批客户端，分页）
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet getGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String deptid = (String) pSet.get("deptid");
			String ly = (String) pSet.get("ly");
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			int start = (page - 1) * limit;
			String type = "2";
			if ("0".equals(ly)) {
				type = "2";
			} else if ("1".equals(ly)) {
				type = "3";
			}
			String sql = null;
			sql = "select t.id,t.depart_name,t.username,t.phone,t.email,t.address,t.title,t.type,t.content,t.status,t.user_ip,t.write_date,t.replayer,t.deal_result,t.deal_date,t.reply_ip,t.checks,t.open,t.warn,t.busi_id,t.busi_type,t.satisfy,t.useful,t.notuseful,t.depart_id,t.isanonymous,t.give_status,t.give_dept_id,t.give_dept_name,t.user_id,t.sms_alert_already, t.sxbm,t.region_id,t.sxmc,t.sxid,t.url from GUESTBOOK t where t.depart_id = ? and t.type = ? order by t.write_date desc";
			ds = DbHelper.query(sql, start, limit,
					new Object[] { deptid, type }, conn, "icityDataSource");//
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("pageindex:" + page + ",pagesize:" + limit);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 短信通知（舟山微信申报和大厅一体机申报调用）sendMessageForHallAndWeChat
	/**
	 * 短信通知（舟山微信申报和大厅一体机申报调用）
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet sendMessageForHallAndWeChat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String phonestr = WriteDao.getInstance().getPhoneStr(pSet);
			String[] array = phonestr.split(",");
			String message = "";
			message = SecurityConfig.getString("message_wwsb");
			message = message.replace("{name}", (String) pSet.get("userName"));
			message = message.replace("{itemname}",
					(String) pSet.get("itemName"));
			String channel = (String) pSet.get("channel");
			String sql = "insert into pub_sms(ID, SMSCONTENT, SENDTIME,CHANNEL, STATUS,TELEPHONE, RID) values(?,?,?,?,?,?,?)";
			for (int i = 0; i < array.length; i++) {
				int j = DbHelper
						.update(sql,
								new Object[] {
										Tools.getUUID32(),
										message,
										CommonUtils
												.getInstance()
												.parseDateToTimeStamp(
														new Date(),
														CommonUtils.YYYY_MM_DD_HH_mm_SS),
										channel, "0", array[i],
										(String) pSet.getParameter("WebRegion") },
								conn);
			}
			ds.setState(StateType.SUCCESS);
			ds.setMessage("发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage("发送失败");
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 爱城市网获取咨询投诉列表iCityGuestBookListByPage
	/**
	 * 爱城市网获取咨询投诉列表 type(2咨询，3投诉)，ly（来源2为爱城市，1为微信咨询2为微信投诉 3为手机app） STATUS
	 * CHAR(1) Y '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet iCityGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String where = (String) pSet.get("where");
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			JSONArray p = JSONArray.fromObject(pSet.get("params"));
			Object[] params = new Object[p.size()];
			for (int i = 0; i < p.size(); i++) {
				params[i] = p.get(i);
			}
			int start = (page - 1) * limit;
			String sql = null;
			sql = "select t.* from GUESTBOOK t where 1=1 " + where;
			ds = DbHelper.query(sql, start, limit, params, conn,
					"icityDataSource");
			ds.setState(StateType.SUCCESS);
			ds.setMessage("pageindex:" + page + ",pagesize:" + limit);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 爱城市网提交咨询投诉 iCityAddGuestBook
	/**
	 * 爱城市网提交咨询投诉 type(2咨询，3投诉)，ly（来源2为爱城市，0为微信咨询1为微信投诉） STATUS CHAR(1) Y '0'
	 * 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet iCityAddGuestBook(ParameterSet pSet) {
		String ID = Tools.getUUID32();
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String where = "ID,WRITE_DATE," + (String) pSet.get("where");
			StringBuffer keys = new StringBuffer("?,sysdate,");
			JSONArray p = JSONArray.fromObject(pSet.get("params"));
			Object[] params = new Object[p.size() + 1];
			params[0] = ID;
			for (int i = 0; i < p.size(); i++) {
				params[i + 1] = p.get(i);
				keys.append("?,");
			}
			String keysStr = keys.toString();
			keysStr = keysStr.substring(0, keysStr.length() - 1);
			String sql = null;
			sql = "insert into GUESTBOOK t(" + where + ") values (" + keysStr
					+ ")";
			int k = DbHelper.update(sql, params, conn);
			if (k > 0) {
				ds.setState(StateType.SUCCESS);
				JSONObject j_id = new JSONObject();
				j_id.put("ID", ID);
				ds.setRawData(j_id.toString());
				ds.setMessage("");
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("");
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
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 舟山，给微信和大厅提供在线申报后短信提示接口onlineapplySendMessage
	/**
	 * 舟山，给微信和大厅提供在线申报后短信提示接口
	 * @param pSet
	 *            {organ_code:"",itemCode:"",TYPE:"20",userName:"",userName:""}[
	 *            外网申报type 20]
	 * @param ds
	 */
	@SuppressWarnings("deprecation")
	public DataSet onlineapplySendMessage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String message = "";
			String phonestr = WriteDao.getInstance().getPhoneStr(pSet);
			message = SecurityConfig.getString("message_wwsb");
			message = message.replace("{name}",
					(String) pSet.getParameter("userName"));
			message = message.replace("{itemname}",
					(String) pSet.getParameter("itemName"));
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("message_url") + "sendMessage");
			Map<String, String> data = new HashMap<String, String>();
			data.put("phoneList", phonestr);
			data.put("mesConent", message);
			String ret = RestUtil.postData(url, data);
			ds.setMessage(ret);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	//endregion
	
	//region爱城市网查询办件进度(列表)爱城市网查询办件进度(列表)
	/**
	 * 爱城市网查询办件进度(列表)
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String phoneNum = (String) pSet.remove("phone");
		String page = (String) pSet.remove("page"); // 当前页数
		String rows = (String) pSet.remove("rows"); // 每页条数
		System.out.println("调用微信接口");
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getBusinessList");
			Map<String, String> data = new HashMap<String, String>();
			data.put("phone", phoneNum);
			data.put("page", page);
			data.put("rows", rows);
			JSONObject json = JSONObject.fromObject(RestUtil
					.postData(url, data));
			if ("300".equals(json.getString("state"))) {
				ds.setState(StateType.FAILT);
				ds.setMessage(json.getString("error"));
			} else {
				JSONArray m_data = json.getJSONArray("LIST");				
				DataSet ds_collection= new DataSet();
				if(m_data.size()>0){
					Connection conn = DbHelper.getConnection("icityDataSource");
					String sql = "select * from star_level_evaluation t where t.creator_id = (select s.creator_id from star_level_evaluation s where s.serial_number=?)";
					ds_collection = DbHelper.query(sql,new Object[] { m_data.getJSONObject(0).getJSONObject("INDEX").getString("RECEIVE_NUMBER") }, conn);
				}
				int len = ds_collection.getTotal();
				int m_len = m_data.size();
				for(int i=0;i<m_len;i++){
					m_data.getJSONObject(i).getJSONObject("INDEX").put("IS_EVALUATION", "0");//初始化0为未评价
					for(int j=0;j<len;j++){
						if(m_data.getJSONObject(i).getJSONObject("INDEX").getString("RECEIVE_NUMBER").equals(ds_collection.getJAData().getJSONObject(j).getString("SERIAL_NUMBER"))){
							m_data.getJSONObject(i).getJSONObject("INDEX").put("IS_EVALUATION", "1");//1为已评价
						}
					}
				}
				ds.setTotal(Integer.parseInt(json.getString("totalRow")));
				JSONObject message = new JSONObject();
				message.put("pageNumber", json.getString("pageNumber"));
				message.put("pageSize", json.getString("pageSize"));
				message.put("totalPage", json.getString("totalPage"));
				message.put("totalRow", json.getString("totalRow"));
				ds.setMessage(message.toString());
				ds.setRawData(m_data);
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	//endregion
	
	//region爱城市网查询办件进度(列表)//根据身份证//威海getBusinessListByIdCard
	/**
	 * 爱城市网查询办件进度(列表)//根据身份证//威海
	 * @param pset
	 * @return
	 */
	public DataSet getBusinessListByIdCard(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String idCard = (String) pSet.remove("idCard");
		String page = (String) pSet.remove("page"); // 当前页数
		String rows = (String) pSet.remove("rows"); // 每页条数
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getBusinessListByIdCard?idCard="+idCard+"&page="+page+"&rows="+rows);
			System.out.println("getBusinessListByIdCard"+url);
			String s = RestUtil.getData(url);
			//System.out.println("getBusinessListByIdCard"+s);
			JSONObject json = JSONObject.fromObject(s);
			if ("300".equals(json.getString("state"))) {
				ds.setState(StateType.FAILT);
				ds.setMessage(json.getString("error"));
			} else {
				JSONArray m_data = json.getJSONArray("LIST");
				ds.setTotal(Integer.parseInt(json.getString("totalRow")));
				JSONObject message = new JSONObject();
				//message.put("pageNumber", json.getString("pageNumber"));
				//message.put("pageSize", json.getString("pageSize"));
				//message.put("totalPage", json.getString("totalPage"));
				message.put("totalRow", json.getString("totalRow"));
				ds.setMessage(message.toString());
				ds.setRawData(m_data);
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	//endregion
	
	//region微信个人用户注册接口 ly=2 微信register_per
	/**
	 * 微信个人用户注册接口 ly=2 微信
	 * @param pset
	 * @return
	 */
	public DataSet register_per(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String username = (String) pSet.getParameter("username");
		String phone = (String) pSet.getParameter("phone");
		String email = (String) pSet.getParameter("email");
		String pass_per = (String) pSet.getParameter("pass_per");
		String code_per = (String) pSet.getParameter("code_per");
		String name_per = (String) pSet.getParameter("name_per");
		String code_type_per = (String) pSet.getParameter("code_type_per");
		String address_per = (String) pSet.getParameter("address_per");
		String ly = (String)pSet.get("ly");
		Date now = CommonUtils.getInstance().parseStringToTimeStamp(
				Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
				CommonUtils.YYYY_MM_DD_HH_mm_SS);
		int type = 11;// 个人
		String is_inuse = "1";// 账号状态
		try {
			// 证件类型 10
			String sql_account = "select 1 from uc_user where account=?";// and
																			// ly='2'";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}

			String sql_phone = "select 1 from uc_user where phone=?";// and
																		// ly='2'";
			DataSet ds_phone = DbHelper.query(sql_phone,
					new Object[] { phone }, conn);
			if (ds_phone.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("手机号已注册！");
				return ds;
			}

			String sql_card_no = "select 1 from uc_user where card_no=? and card_type=? and type='11'";// and
																										// ly='2'";
			DataSet ds_card_no = DbHelper.query(sql_card_no, new Object[] {
					code_per, code_type_per }, conn);
			if (ds_card_no.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("身份证号已注册！");
				return ds;
			}

			// String sql1 = "select max(id) as id from uc_user ";
			// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
			long id = IdGenereator.getInstance("usercenter").getId();// Integer.parseInt((String)
																		// j.getJOData().get("ID"))
																		// + 1;
			String sql = "insert into uc_user (id,account,name,email,password,card_type,card_no,creation_time,address,type,phone,is_inuse,status,ly) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_per,
					email, pass_per, code_type_per, code_per, now, address_per,
					type, phone, is_inuse, "1", ly }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	//endregion
	
	//region微信企业用户注册接口register_org
	/**
	 * 微信企业用户注册接口
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet register_org(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String username = (String) pSet.getParameter("username");
		String phone_org = (String) pSet.getParameter("phone");
		String email = (String) pSet.getParameter("email");
		String pass_org = (String) pSet.getParameter("pass_org");
		String code_org = (String) pSet.getParameter("code_org");
		String name_org = (String) pSet.getParameter("name_org");
		String code_type_org = (String) pSet.getParameter("code_type_org");
		String address_org = (String) pSet.getParameter("address_org");
		String org_org = (String) pSet.getParameter("org_org");
		String org_all_org = (String) pSet.getParameter("org_all_org");
		String legal_person_org = (String) pSet
				.getParameter("legal_person_org");
		String code_type_legal_person_org = (String) pSet
				.getParameter("code_type_legal_person_org");
		String code_legal_person_org = (String) pSet
				.getParameter("code_legal_person_org");
		String ly = (String)pSet.get("ly");
		int type = 21;// 企业
		String is_inuse = "1";// 账号状态
		try {
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			// 证件类型 10
			String sql_account = "select 1 from uc_user where account=?";// and
																			// ly='2'";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}

			String sql_phone = "select 1 from uc_user where phone=?";// and
																		// ly='2'";
			DataSet ds_phone = DbHelper.query(sql_phone,
					new Object[] { phone_org }, conn);
			if (ds_phone.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("手机号已注册！");
				return ds;
			}

			String sql_card_no = "select 1 from uc_user where card_no=? and card_type=? and type='21'";// and
																										// ly='2'";
			DataSet ds_card_no = DbHelper.query(sql_card_no, new Object[] {
					code_org, code_type_org }, conn);
			if (ds_card_no.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("身份证号已注册！");
				return ds;
			}
			// String sql1 = "select max(id) as id from uc_user ";
			// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
			// int id = Integer.parseInt((String) j.getJOData().get("ID")) + 1;
			long id = IdGenereator.getInstance("usercenter").getId();
			String sql = "insert into uc_user (id,account,name,email,password,card_type,card_no,creation_time,address,type,org_no,"
					+ "org_name,org_boss_no,org_boss_type,org_boss_name,phone,is_inuse,status,ly) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_org,
					email, pass_org, code_type_org, code_org, now, address_org,
					type, org_org, org_all_org, code_legal_person_org,
					code_type_legal_person_org, legal_person_org, phone_org,
					is_inuse, "1", ly }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	//endregion
	
	//region在线办理接口submitSP
	/**
	 * 在线办理接口
	 * @param pSet
	 * @return
	 */
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONObject data = JSONObject.fromObject(pSet.get("data"));
			Command cmd = new Command("app.icity.onlineapply.ApplyCmd");
			cmd.setParameter("data", data);
			ds = cmd.execute("submitSP");
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败！");
		}
		return ds;
	}
	//endregion
	
	//region根据单位获取已发布事项列表getItemList
	/**
	 * 根据单位获取已发布事项列表
	 * @param pSet//逐步舍弃这个接口，太费劲
	 * @return
	 */
	public DataSet getItemList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String orgCode = (String) pSet.getParameter("orgCode");
			String whereValue = (String) pSet.getParameter("whereValue");
			String paramValue = (String) pSet.getParameter("paramValue");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl") + "/getItemList");			
			 url+="?orgCode="+orgCode+"&whereValue="+java.net.URLEncoder.encode
			 (whereValue,"utf-8")
			 +"&paramValue="+java.net.URLEncoder.encode(paramValue,"utf-8");
			 String result = RestUtil.getData(url);
			JSONArray obj = JSONArray.fromObject(result);
			ds.setRawData(result);
			ds.setTotal(obj.size());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	//endregion
	
	//region根据单位分页已发布事项列表getItemListByPage
	/**
	 * 根据单位分页已发布事项列表
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getItemListByPage");
			Map<String, String> data = new HashMap<String, String>();
			String orgCode = (String) pSet.getParameter("orgCode");
			String rows = (String) pSet.getParameter("rows");
			String page = (String) pSet.getParameter("page");
			String whereValue = (String) pSet.getParameter("whereValue");
			String paramValue = (String) pSet.getParameter("paramValue");
			System.out.println("====getItemListByPage1:"+rows+"|");
			data.put("rows", rows);
			System.out.println("====getItemListByPage2:"+page+"|");
			data.put("page", page);
			System.out.println("====getItemListByPage3:"+orgCode+"|");
			data.put("orgCode", orgCode);
			data.put("whereValue", whereValue);
			data.put("paramValue", paramValue);
			System.out.println("====getItemListByPage4:"+url+"|");
			String result = RestUtil.postData(url, data);
			System.out.println("====getItemListByPageresult:"+result+"|");
			JSONObject jsonResult = JSONObject.fromObject(result);
			JSONArray obj = jsonResult.getJSONArray("pageList");
			obj = JSONArray.fromObject(obj);
			ds.setRawData(obj.toString());
			ds.setTotal(obj.size());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	//endregion
	
	//region根据材料code获取事项材料详情getMaterialDetail
	/**
	 * 根据材料code获取事项材料详情
	 * @param pSet
	 * @return
	 */
	public DataSet getMaterialDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String materialCode = (String) pSet.getParameter("materialCode");
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getMaterialDetail");
			url += "?materialCode=" + materialCode;
			HttpClientUtil client = new HttpClientUtil();
			String result = client.getResult(url, "");
			JSONArray obj = JSONArray.fromObject(result);
			ds.setRawData(obj.toString());
			ds.setTotal(obj.size());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	//endregion
	
	//region根据事项ID分类获取事项相关信息getItemInfoByItemID
	/**
	 * 根据事项ID分类获取事项相关信息
	 * @param pSet
	 * @return
	 */
	public DataSet getItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String type = (String) pSet.getParameter("type");
			String itemId = (String) pSet.getParameter("itemId");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl")
					+ "/getItemInfoByItemID");
			url += "?type=" + type + "&itemId=" + itemId;
			HttpClientUtil client = new HttpClientUtil();
			String result = client.getResult(url, "");
			JSONObject json = JSONObject.fromObject(result);
			if ("1".equals(json.getString("state"))) {
				JSONArray ja = json.getJSONArray("ItemInfo");
				ds.setRawData(ja.toString());
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	//endregion
	
	//region 获取变更前事项getChangeItem
	/**
	 * 获取变更前事项
	 * @param pSet
	 * @return
	 */
	public DataSet getChangeItem(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String itemId = (String) pSet.getParameter("itemId");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl") + "/getChangeItem");
			url += "?itemId=" + itemId;
			HttpClientUtil client = new HttpClientUtil();
			String result = client.getResult(url, "");
			JSONObject obj = JSONObject.fromObject(result);
			if ("1".equals(obj.getString("state"))) {
				JSONArray ja = obj.getJSONArray("data");
				ds.setRawData(ja.toString());
				ds.setTotal(ja.size());
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
	//endregion
	
	//region 获取外网、大厅收件、便民收件办理业务信息getBusinessInfo
	/**
	 * 获取外网、大厅收件、便民收件办理业务信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String receiveNumber = (String) pSet.getParameter("receiveNumber");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getBusinessInfo");
			url += "?receiveNumber=" + receiveNumber;
			HttpClientUtil client = new HttpClientUtil();
			String result = client.getResult(url, "");
			JSONObject obj = JSONObject.fromObject(result);
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.getJSONArray("info").toString());
				ds.setTotal(obj.getJSONArray("info").size());
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取外网、大厅收件、便民收件办理业务信息失败！");
		}
		return ds;
	}
	//endregion
	
	//region 获取所有办理业务信息getAllBusinessInfo
	/**
	 * 获取所有办理业务信息
	 * @param pSet
	 * @return
	 */
	public DataSet getAllBusinessInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String receiveNumber = (String) pSet.getParameter("receiveNumber");
			String password = (String) pSet.getParameter("password");
			String whereValue = (String) pSet.getParameter("whereValue");
			String paramValue = (String) pSet.getParameter("paramValue");
			receiveNumber = StringUtils.isNotEmpty(receiveNumber) ? receiveNumber
					: "";
			password = StringUtils.isNotEmpty(password) ? password : "";
			whereValue = StringUtils.isNotEmpty(whereValue) ? whereValue : "";
			paramValue = StringUtils.isNotEmpty(paramValue) ? paramValue : "";
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getAllBusinessInfo");
			Map<String, String> data = new HashMap<String, String>();
			data.put("receiveNumber", receiveNumber);
			data.put("password", password);
			data.put("whereValue", whereValue);
			data.put("paramValue", paramValue);
			String s = RestUtil.postData(url, data);
			System.out.println(s);
			JSONObject obj = JSONObject
					.fromObject(s);
			ds.setRawData(obj);
			ds.setTotal(obj.size());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("获取所有办理业务信息失败！");
		}
		return ds;
	}
	//endregion
	
	//region根据事项ID获取表单信息getFormInfo
	/**
	 * 根据事项ID获取表单信息
	 * @param pSet
	 * @return
	 */
	public DataSet getFormInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String itemId = (String) pSet.getParameter("itemId");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getFormInfo");
			url += "?itemId=" + itemId;
			HttpClientUtil client = new HttpClientUtil();
			String result = client.getResult(url, "");
			JSONObject obj = JSONObject.fromObject(result);
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.getJSONObject("info"));
				ds.setTotal(1);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setRawData(new JSONObject());
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("根据事项ID获取表单信息失败！");
		}
		return ds;
	}
	//endregion
	
	//region根据uid获取用户办件列表getBusinessIndexList
	/**
	 * 根据uid获取用户办件列表
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String uid = (String) pSet.getParameter("uid");
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			int start = (page - 1) * limit;
			String sql = "select * from business_index t where t.ucid=?";
			ds = DbHelper.query(sql, start, limit, new Object[] { uid }, conn,
					"icityDataSource");
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	//endregion
	
	//region从附件表获取当前用户下的材料列表getAttachListByPhone
	/**
	 * 从附件表获取当前用户下的材料列表
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachListByPhone(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		int page = Integer.parseInt(pSet.getParameter("page").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		int start = (page - 1) * limit;
		String phone = (String) pSet.get("phone");
		String order = pSet.containsKey("order")?(String) pSet.get("order"):"desc";
		try {
			String sql = "select * from business_attach where ucid in (select id from uc_user s where s.phone=?)  order by uploadtime "+order;
			ds = DbHelper.query(sql, start, limit, new Object[] { phone }, conn,
					"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region热点事项 getProjectBusyInfoHotStat
	/**
	 * 热点事项 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectBusyInfoHotStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		int page = Integer.parseInt(pSet.getParameter("page").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		int start = (page - 1) * limit;
		String region_code = (String) pSet.get("region_code");
		String starttime = (String) pSet.get("starttime");
		String endtime = (String) pSet.get("endtime");
		java.sql.Timestamp dstart = null;
		java.sql.Timestamp dend = null;
		try {
			Object[] params = new Object[3];
			String sql = "select b.sxmc name, b.sxbm code, count(1) sum, b.sjdw dept_name_short, '' guide_url from business_index b where 1=1 ";
			if (StringUtil.isNotEmpty(region_code)) {
				sql+=" and b.XZQHDM=?";
				params[0] = region_code;
			}else{
				sql+=" and 1=?";
				params[0] = "1";
			}
			if (StringUtil.isNotEmpty(starttime)) {
				dstart = CommonUtils_api.getInstance().parseStringToTimestamp(starttime,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				sql+=" and b.sbsj >=?";
				params[1] = dstart;
			}else{
				sql+=" and 1=?";
				params[1] = "1";
			}
			if (StringUtil.isNotEmpty(endtime)) {
				dend = CommonUtils_api.getInstance().parseStringToTimestamp(endtime,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
				sql+=" and b.sbsj < ?";
				params[2] = dend;
			}else{
				sql+=" and 1=?";
				params[2] = "1";
			}
			sql+=" group by b.sxmc, b.sxbm,b.sjdw order by count(1) desc";
			ds = DbHelper.query(sql, start, limit, params, conn,"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region获取用户信息 getUserInfoByUserId
	/**
	 * 获取用户信息 
	 * @param pSet
	 * @return
	 */
	public DataSet getUserInfoByUserId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String userId = (String) pSet.get("uid");
		
		try {
			String sql = "select t.id,t.account,t.name,t.email,t.phone,t.password,t.photo_uri,t.type,t.status,t.card_type,t.card_no,t.org_name,t.org_no,t.org_boss_type,t.org_boss_no,t.org_boss_name,t.address,t.region_id,t.is_inuse,t.credit_code,t.ly,r.sex,r.nativeplace,r.nation,r.birthday,r.education,r.politicalstatus,r.homeaddress,r.postcode,r.orgcodeawardorg,r.orgenglishname,r.orgtype,r.orgactuality,r.enterprisesortcode,r.enterprisesortname,r.orgcodeawarddate_str,r.registerdate_str,r.business_scope from uc_user t left join uc_user_ext r on t.id = r.id where t.id = ?";
			ds = DbHelper.query(sql, new Object[] { userId }, conn);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		
		return ds;
	}
	//endregion
	
	/**
	 * 获取用户信息 
	 * @param pSet
	 * @return
	 */
	public DataSet getPerUserInfoByCardId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String userId = (String) pSet.get("cardId");
		
		try {
			String sql = "select t.id,t.account,t.name,t.email,t.phone,t.password,t.photo_uri,t.type,t.status,t.card_type,t.card_no,t.org_name,t.org_no,t.org_boss_type,t.org_boss_no,t.org_boss_name,t.address,t.region_id,t.is_inuse,t.credit_code,t.ly,r.sex,r.nativeplace,r.nation,r.birthday,r.education,r.politicalstatus,r.homeaddress,r.postcode,r.orgcodeawardorg,r.orgenglishname,r.orgtype,r.orgactuality,r.enterprisesortcode,r.enterprisesortname,r.orgcodeawarddate_str,r.registerdate_str,r.business_scope from uc_user t left join uc_user_ext r on t.id = r.id where t.card_no = ? and type='11' ";
			ds = DbHelper.query(sql, new Object[] { userId }, conn);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		
		return ds;
	}
	
	/**
	 * 获取用户信息 
	 * @param pSet
	 * @return
	 */
	public DataSet getOrgUserInfoByCardId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String userId = (String) pSet.get("cardId");
		
		try {
			String sql = "select t.id,t.account,t.name,t.email,t.phone,t.password,t.photo_uri,t.type,t.status,t.card_type,t.card_no,t.org_name,t.org_no,t.org_boss_type,t.org_boss_no,t.org_boss_name,t.address,t.region_id,t.is_inuse,t.credit_code,t.ly,r.sex,r.nativeplace,r.nation,r.birthday,r.education,r.politicalstatus,r.homeaddress,r.postcode,r.orgcodeawardorg,r.orgenglishname,r.orgtype,r.orgactuality,r.enterprisesortcode,r.enterprisesortname,r.orgcodeawarddate_str,r.registerdate_str,r.business_scope from uc_user t left join uc_user_ext r on t.id = r.id where t.card_no = ? and type='21' ";
			ds = DbHelper.query(sql, new Object[] { userId }, conn);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		
		return ds;
	}
	//region查询当前 指导区划 指定部门下的 科室getItemAgentList
	/**
	 * 查询当前 指导区划 指定部门下的 科室
	 * @param pset
	 * @return
	 */
	public DataSet getItemAgentList(ParameterSet pset) {
		DataSet ds = new DataSet();
		String region = "";
		if(pset.containsKey("region_code")){
			region = (String)pset.get("region_code");
		}		
		String url = HttpUtil.formatUrl(
				SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemAgentList");
		String s="";
		try {
			s = RestUtil.getData(url+"?regionCode="+region);
			JSONArray resultJson = JSONArray.fromObject(s);
			ds.setState(StateType.SUCCESS);
			ds.setRawData(resultJson);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(s);
			e.printStackTrace();
			System.out.println("获取部门科室出错！");
		}
		return ds;
	}
	//endregion
	
	//region漳州获取用户id 并落地用户信息getUserId
	/**
	 * 漳州获取用户id 并落地用户信息
	 * 微信和平台对接，将获取的用户信息传递过来落地到网办
	 * @param pset
	 * @return
	 */
	public DataSet getUserId(ParameterSet pSet){
		DataSet ds =  new DataSet();
		try{
			JSONObject jo = JSONObject.fromObject(pSet.get("user"));			
			JSONObject user = new JSONObject();
			if("11".equals(jo.getString("TYPE"))){
				user.put("login_name", jo.getString("ACCOUNT"));
				user.put("mobile_phone", jo.getString("PHONE"));
				user.put("email", "");
				user.put("certificate_number", jo.getString("CARD_NO"));
				user.put("name", jo.getString("NAME"));
				user.put("certificate_type", "10");
				user.put("address", "");
				user.put("type", "11");
			}else if("21".equals(jo.getString("TYPE"))){
				user.put("login_name", jo.getString("ACCOUNT"));
				user.put("org_law_mobile", jo.getString("PHONE"));
				user.put("jbr_email", "");
				user.put("jbr_certificateNumber", jo.getString("CARD_NO"));
				user.put("name", jo.getString("NAME"));
				user.put("org_address", "");
				user.put("org_code", "");
				user.put("org_name", jo.getString("ORG_NAME"));
				user.put("law_name", "");
				user.put("org_law_idcard", jo.getString("CARD_NO"));
				user.put("type", "21");
			}			
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("ACCOUNT", jo.getString("ACCOUNT"));		
			DataSet user_ds = UserDao.getInstance().getList(m_pSet);
			if(user_ds.getState()==StateType.SUCCESS){
				if(user_ds.getTotal()==0){
					GetUserMapDao um = GetUserMapDao.getInstance();
					DataSet user_load_ds = um.insertIntoUcUser(user);
					if(user_load_ds.getState()==StateType.SUCCESS){
						jo.put("ID", user_load_ds.getRawData());
					}
					System.out.println(user_load_ds.getMessage());
				}else{
					jo.put("ID", user_ds.getJAData().getJSONObject(0).getString("ID"));
				}
				ds.setRawData(jo);
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	//endregion
	
	/**
	 * 根据身份证号码获取微信办件记录
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexByICard(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String sqrzjhm = (String) pSet.getParameter("sqrzjhm");	
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			int start = (page - 1) * limit;
			String sql = "select t.sblsh,t.sxbm,t.sxmc,t.sbsj,t.sqrmc,t.lxrsj,sbxmmc,t.bjjgdm,t.state from business_index t where t.ly='wechat' and t.sqrzjhm=? ";
			ds = DbHelper.query(sql, start, limit, new Object[] {sqrzjhm},conn,"icityDataSource");	
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 根据流水号获取补齐补正材料的code
	 * @param pSet
	 * @return
	 */
	public DataSet getBqbzCodeBySblsh(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String sblsh = (String) pSet.getParameter("sblsh");	
			String sql = "select bzclqd from business_index t where  t.sblsh=? ";
			ds = DbHelper.query(sql, new Object[] {sblsh},conn);	
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 根据流水号获取补齐补正材料的code
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexBySblsh(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String sblsh = (String) pSet.getParameter("sblsh");	
			String sql = "select basecontent from sub_for_ex_app_information t where  t.sblsh=? ";
			ds = DbHelper.query(sql, new Object[] {sblsh},conn);	
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	public Object getNBusinessIndex(ParameterSet pSet){
		DataSet ds = new DataSet();
		String count = pSet.getParameter("count").toString();
		String region = pSet.getParameter("region").toString();
		HttpClientUtil client = new HttpClientUtil();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getDisplayList?count="+count+"&region="+region);
		Object strItemList = new Object();
		try{
			strItemList= client.getResult(url,"");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }	
		return strItemList;
	}
	
	/**
	 * 根据大汉统一身份认证的uuid获取用户的办件列表
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessListByUuid(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String uuid = (String) pSet.getParameter("uuid");	
			String sql = "select t.sblsh, t.sxbm, t.sxmc, t.sxid,t.sbsj, t.sqrmc, t.sbxmmc, t.bjsj, m.user_id_map uuid from BUSINESS_INDEX t left join uc_user_map m on m.user_id=t.ucid where m.user_id_map = ? order by t.sbsj desc";
			ds = DbHelper.query(sql, new Object[] {uuid},conn);	
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
}
