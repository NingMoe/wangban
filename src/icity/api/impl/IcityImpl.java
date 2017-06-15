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

@SuppressWarnings("deprecation")
public class IcityImpl extends BaseQueryCommand {
	@SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(IcityImpl.class);
	private static final String ACCESS_TOKEN = "access_token";
	private static final String CACHE_KEY_FLAG = "AuthControl";
	private static final String TOKEN_SCOPE_GUEST = "guest";
	private static final String TOKEN_SCOPE_LOGINUSER = "login";
	private static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };
	private static api.tools.Tools mytools = new api.tools.Tools();
	public static IcityImpl getInstance() {
		return DaoFactory.getDao(IcityImpl.class.getName());
	}

	//region过滤authController
	/**
	 * 过滤
	 * @param pSet
	 * @return
	 */
	public DataSet authController(ParameterSet pSet) {
		String access_token = (String) pSet.get(ACCESS_TOKEN);
		Command cmd = new Command("app.pmi.accesstoken.AuthControl");
		cmd.setParameter(ACCESS_TOKEN, access_token);
		DataSet ds_auth = cmd.execute("authController");
		return ds_auth;
	}
	//endregion
	
	//region 检测是否过期checkAccessToken
	/**
	 * 检测是否过期 
	 * @param pSet
	 * @return
	 */
	public DataSet checkAccessToken(ParameterSet pSet) {
		String access_token = (String) pSet.get(ACCESS_TOKEN);
		Command cmd = new Command("app.pmi.accesstoken.AuthControl");
		cmd.setParameter(ACCESS_TOKEN, access_token);
		return cmd.execute("checkAccessToken");
	}
	//endregion
	
	//region 添加或者更新设备信息，及添加或更新token  makeAccessToken
	/**
	 * 添加或者更新设备信息，及添加或更新token
	 * deviceToken=ceshi&pushToken=&os=&model=&osVersion=&appVersion=
	 * @param pSet
	 * @return
	 */
	public DataSet makeAccessToken(ParameterSet pSet) {
		String deviceToken = (String) pSet.get("deviceToken"); // 设备标识
		String pushToken = (String) pSet.get("pushToken"); // 推送标识
		String os = (String) pSet.get("os"); // ios或 Android
		String model = (String) pSet.get("model"); // 手机类型
		String osVersion = (String) pSet.get("osVersion"); // 手机版本号
		String appVersion = (String) pSet.get("appVersion"); // 用户下载APP的版本号
		Command cmd = new Command("app.pmi.accesstoken.AuthControl");
		cmd.setParameter("deviceToken", deviceToken);
		cmd.setParameter("pushToken", pushToken);
		cmd.setParameter("os", os);
		cmd.setParameter("model", model);
		cmd.setParameter("osVersion", osVersion);
		cmd.setParameter("appVersion", appVersion);
		DataSet ds = cmd.execute("makeAccessToken");
		if (ds.getState() == StateType.SUCCESS) {
			// CacheManager.
		}
		return ds;
	}
	//endregion
	
	//region 威海根据手机号检测用户 checkPhone
	/**
	 * 威海根据手机号检测用户
	 * @param pSet
	 * @return
	 */
	public DataSet checkPhone(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		String accessToken="";
		ds.setState(StateType.FAILT);
		ds.setRawData(accessToken);
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String phone = (String) pSet.get("phone");
			String sql = "select t.accesstoken from cust_accesstoken t where t.custid = (select s.id from uc_user s where s.phone=? and s.type='11')";
			DataSet ds_select = DbHelper.query(sql, new Object[] { phone }, conn);
			if(ds.getTotal()>0){
				accessToken = ds_select.getJOData().getString("ACCESSTOKEN");
				ParameterSet m_pSet = new ParameterSet();
				m_pSet.put(ACCESS_TOKEN, accessToken);
				//获取token后验证token
				DataSet ds_authController = authController(m_pSet);
				if(ds_authController.getState()==StateType.SUCCESS){
					ds.setRawData(accessToken);
					ds.setState(StateType.SUCCESS);
				}				
			}else{
				//注册
				ParameterSet pSet_registerOfPer = new ParameterSet();
				JSONObject registerOfPer = JSONObject.fromObject(pSet.get("registerOfPer"));
				Iterator set_registerOfPer = registerOfPer.keys();
				while(set_registerOfPer.hasNext()){
					pSet_registerOfPer.put(set_registerOfPer.next().toString(), registerOfPer.getString(set_registerOfPer.next().toString()));
				}
				DataSet ds_regionsterOfPer = ServiceImpl.getInstance().registerOfPer(pSet_registerOfPer);
				if(ds_regionsterOfPer.getState()==StateType.SUCCESS){
					//获取新token
					ParameterSet pSet_makeAccessToken = new ParameterSet();;
					JSONObject makeAccessToken = JSONObject.fromObject(pSet.get("makeAccessToken"));
					Iterator set_makeAccessToken = makeAccessToken.keys();
					while(set_makeAccessToken.hasNext()){
						pSet_makeAccessToken.put(set_makeAccessToken.next().toString(), makeAccessToken.getString(set_makeAccessToken.next().toString()));
					}
					DataSet ds_new = makeAccessToken(pSet_makeAccessToken);
					if(ds_new.getState()==StateType.SUCCESS){
						accessToken = ds_new.getJOData().getString("access_token");
						//用新token登录
						String username = (String) pSet_registerOfPer.getParameter("username");// 用户名
						String pass_per = (String) pSet_registerOfPer.getParameter("pass_per");// 密码
						ParameterSet pSet_login = new ParameterSet();
						pSet_login.put(ACCESS_TOKEN, accessToken);
						pSet_login.put("loginname", username);
						pSet_login.put("password", pass_per);
						DataSet ds_login = login(pSet_login);
						if(ds_login.getState()==StateType.SUCCESS){
							ds.setRawData(accessToken);
							ds.setState(StateType.SUCCESS);
						}
					}
				}
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
	
	//region 获取咨询投诉详情getGuestBookDetail
	/**
	 * 获取咨询投诉详情
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String id = (String) pSet.get("id");
			String sql = "select t.id,t.depart_name,t.username,t.phone,t.email,t.address,t.title,t.type,t.content,t.status,t.user_ip,t.write_date,t.replayer,t.deal_result,t.deal_date,t.reply_ip,t.checks,t.open,t.warn,t.busi_id,t.busi_type,t.satisfy,t.useful,t.notuseful,t.depart_id,t.isanonymous,t.give_status,t.give_dept_id,t.give_dept_name,t.user_id,t.sms_alert_already, t.sxbm,t.region_id,t.sxmc,t.sxid,t.url from GUESTBOOK t where t.id = ?";
			ds = DbHelper.query(sql, new Object[] { id }, conn);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
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
	
	//region 获取咨询投诉列表getGuestBookListByPage
	/**
	 * 获取咨询投诉列表 type(2咨询，3投诉)，ly（来源2为爱城市，0为微信咨询1为微信投诉,3手机app） STATUS CHAR(1) Y
	 * '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理 where = "ly = ? and status=?";
	 * params = ['3','1'];
	 */
	public DataSet getGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String where = " and user_id=? and " + (String) pSet.get("where");
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			JSONArray p = JSONArray.fromObject(pSet.get("params"));
			Object[] params = new Object[p.size() + 1];
			for (int i = 0; i < p.size() + 1; i++) {
				if (i == 0) {
					params[i] = (String) pSet.get("ucid");
				} else {
					params[i] = p.get(i - 1);
				}
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
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 网提交咨询投诉addGuestBook
	/**
	 * 网提交咨询投诉 type(2咨询，3投诉)，ly（来源2为爱城市，0为微信咨询1为微信投诉,3为手机app,4为西宁广电app） STATUS CHAR(1) Y
	 * '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理 where =
	 * "dept_name,dept_id,content"; params = ['部门','ID','内容'];
	 */
	public DataSet addGuestBook(ParameterSet pSet) {
		String ID = Tools.getUUID32();
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String where = "ID,WRITE_DATE,user_id,status,"
					+ (String) pSet.get("where");
			StringBuffer keys = new StringBuffer("?,sysdate,?,'0',");
			JSONArray p = JSONArray.fromObject(pSet.get("params"));
			Object[] params = new Object[p.size() + 2];
			params[0] = ID;
			params[1] = ucid;
			for (int i = 0; i < p.size(); i++) {
				params[i + 2] = p.get(i);
				keys.append("?,");
			}
			String keysStr = keys.toString();
			keysStr = keysStr.substring(0, keysStr.length() - 1);
			String sql = null;
			sql = "insert into GUESTBOOK t (" + where + ") values (" + keysStr
					+ ")";
			int k = DbHelper.update(sql, params, conn);
			if (k > 0) {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("");
				ds.setRawData(new JSONArray());
			} else {
				ds.setState(StateType.FAILT);
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
	
	//region 查询办件进度(列表)getBusinessSearchQuery
	/**
	 * 爱城市网查询办件进度(列表)
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String phoneNum = (String) pSet.remove("phone");
		String page = (String) pSet.remove("page"); // 当前页数
		String rows = (String) pSet.remove("limit"); // 每页条数
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
				ds.setRawData(new JSONArray());
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
				ds.setMessage("pageNumber:" + json.getString("pageNumber")
						+ ",pageSize:" + json.getString("pageSize"));
				ds.setRawData(m_data.toString());
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 根据用户名,手机号，邮箱，密码登录login
	/**
	 * 根据用户名,手机号，邮箱，密码登录
	 */
	public DataSet login(ParameterSet pSet) {
		String loginname = (String) pSet.get("loginname");
		String password = (String) pSet.get("password");
		String type = (String) pSet.get("type");
		String m_password = (String) pSet.get("password");
		password = mytools.toMD5(password);
		String access_token = (String) pSet.get(ACCESS_TOKEN);
		DataSet ds_login = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_login.getState() == StateType.SUCCESS) {
			DataSet ds_select;
			Connection conn = DbHelper.getConnection("icityDataSource");
			String select = "SELECT * FROM UC_USER WHERE (ACCOUNT =? or PHONE=? or EMAIL=?) and PASSWORD = ? and type=?";
			try {
				ds_select = DbHelper.query(select, new Object[] { loginname,
						loginname, loginname, password,type }, conn);
				if (ds_select.getTotal() > 0) {
					String update_CUST_ACCESSTOKEN = "update CUST_ACCESSTOKEN t set t.CUSTID=?,t.SCOPE=? where t.accesstoken=?";
					int i = DbHelper
							.update(update_CUST_ACCESSTOKEN, new Object[] {
									ds_select.getJOData().getString("ID"),
									TOKEN_SCOPE_LOGINUSER, access_token }, conn);
					if (i == 0) {
						ds.setState(StateType.FAILT);
						ds.setMessage("登录失败！");
					} else {
						JSONObject record = ds_select.getRecord(0);
						JSONObject jo_token = ds_login.getJOData();
						jo_token.put("scope", TOKEN_SCOPE_LOGINUSER);
						jo_token.put("userinfo", record);
						ds_login.setRawData(jo_token);
						CacheManager.set(CacheManager.EhCacheType,
								CACHE_KEY_FLAG, access_token, ds_login);
						ds.setState(StateType.SUCCESS);
						ds.setMessage("登录成功！");
					}
				} else {
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

						select = "SELECT * FROM UC_USER WHERE (ACCOUNT =? or PHONE=? or EMAIL=?) and type=?";
						DataSet m_ds_select = DbHelper
								.query(select, new Object[] { loginname,
										loginname, loginname,type }, conn);
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

						JSONObject jo_token = ds_login.getJOData();
						jo_token.put("scope", TOKEN_SCOPE_LOGINUSER);
						jo_token.put("userinfo", uInfo);
						ds_login.setRawData(jo_token);
						CacheManager.set(CacheManager.EhCacheType,
								CACHE_KEY_FLAG, access_token, ds_login);
						ds.setState(StateType.SUCCESS);
						ds.setMessage("登录成功！");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage(e.toString());
			} finally {
				DBSource.closeConnection(conn);
			}
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_login.getMessage());
		}
		return ds;
	}
	//endregion
	
	//region 退出logout
	/**
	 * 退出
	 */
	public DataSet logout(ParameterSet pSet) {
		String access_token = (String) pSet.get(ACCESS_TOKEN);
		DataSet ds = authController(pSet);
		DataSet ds_logout = new DataSet();
		if (ds.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds.getJOData().getString("scope"))) {
				ds_logout.setState(StateType.SUCCESS);
				ds_logout.setMessage("未登录！");
				return ds_logout;
			}
			try {
				Connection conn = DbHelper.getConnection("icityDataSource");
				String update_CUST_ACCESSTOKEN = "update CUST_ACCESSTOKEN t set t.SCOPE=? where t.accesstoken=?";
				int i = DbHelper.update(update_CUST_ACCESSTOKEN, new Object[] {
						TOKEN_SCOPE_GUEST, access_token }, conn);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("登出失败！");
				} else {
					JSONObject jo_token = ds.getJOData();
					jo_token.put("scope", TOKEN_SCOPE_GUEST);
					jo_token.put("userinfo", "");
					ds.setRawData(jo_token.toString());
					CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG,
							access_token, ds);
					ds_logout.setState(StateType.SUCCESS);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ds_logout.setState(StateType.FAILT);
				ds_logout.setMessage(e.toString());
			}
		} else {
			ds_logout.setState(StateType.FAILT);
			ds_logout.setMessage(ds.getMessage());
		}
		return ds_logout;
	}
	//endregion 
		
	//region 在线办理submitSP
	/**
	 * 在线办理接口
	 * @param pSet
	 * @return
	 */
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONObject data = JSONObject.fromObject(pSet.get("data"));
			data.put("ucid", (String) pSet.get("ucid"));
			data.put("userName", (String) pSet.get("userName"));
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
	
	//region 部门区划列表getDeptList
	/**
	 * 部门区划列表
	 * @param pset
	 * @return
	 */
	public JSONObject getDeptList(ParameterSet pSet) {
		JSONObject obj = new JSONObject();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl")
				+ "/web/c/getDeptList");
		Map<String, String> data = new HashMap<String, String>();
		data.put("region_code", (String) pSet.getParameter("region_code"));
		try {
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			// JSONArray region = obj.getJSONArray("region");
			JSONArray organ = obj.getJSONArray("organ");
			JSONArray m_organ = new JSONArray();
			int len = organ.size();
			JSONObject organ_jo = new JSONObject();
			for (int i = 0; i < len; i++) {
				if ("1".equals(organ.getJSONObject(i).getString("IS_HALL"))) {
					organ_jo.put("CODE",
							organ.getJSONObject(i).getString("CODE"));
					organ_jo.put("ID", organ.getJSONObject(i).getString("ID"));
					organ_jo.put("NAME",
							organ.getJSONObject(i).getString("NAME"));
					organ_jo.put("SHORT_NAME", organ.getJSONObject(i)
							.getString("SHORT_NAME"));
					m_organ.add(organ_jo);
				}
			}
			obj.put("organ", m_organ);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("code", "300");
		}
		return obj;
	}
	//endregion
	
	//region 三级区划组合getWebSite
	/**
	 * 三级区划
	 * @param pset
	 * @return
	 */
	public JSONObject getWebSite(ParameterSet pSet) {
		JSONObject obj = new JSONObject();
		try {
			JSONArray ja = new JSONArray();
			pSet.put("region_code", (String) pSet.getParameter("region_code"));
			JSONObject jo_1 = getDeptList(pSet);
			int jo_1_size = jo_1.getJSONArray("region").size();
			for (int i = 0; i < jo_1_size; i++) {
				if ("1".equals(jo_1.getJSONArray("region").getJSONObject(i)
						.getString("TYPE"))) {
					if (((String) pSet.getParameter("region_code")).equals(jo_1
							.getJSONArray("region").getJSONObject(i)
							.getString("CODE"))) {
						JSONObject s = new JSONObject();
						s.put("CODE", jo_1.getJSONArray("region")
								.getJSONObject(i).getString("CODE"));
						s.put("NAME", jo_1.getJSONArray("region")
								.getJSONObject(i).getString("NAME"));
						s.put("region", new JSONArray());
						JSONArray o = new JSONArray();
						o.add(s);
						s.put("region", o);
						ja.add(s);
					} else {
						pSet.put("region_code", jo_1.getJSONArray("region")
								.getJSONObject(i).getString("CODE"));
						JSONObject s1 = new JSONObject();
						JSONObject s2 = new JSONObject();
						JSONObject s3 = new JSONObject();
						JSONObject jo_2 = getDeptList(pSet);
						JSONArray ja_2 = new JSONArray();
						int jo_2_size = jo_2.getJSONArray("region").size();
						String region_code_2 = (String) pSet.getParameter("region_code");
						for (int j = 0; j < jo_2_size; j++) {
							if ("1".equals(jo_2.getJSONArray("region").getJSONObject(j).getString("TYPE"))&& !region_code_2.equals(jo_2.getJSONArray("region").getJSONObject(j).getString("CODE"))) {
								pSet.put(
										"region_code",
										jo_2.getJSONArray("region")
												.getJSONObject(j)
												.getString("CODE"));
								JSONObject jo_3 = getDeptList(pSet);
								JSONArray ja_3 = new JSONArray();
								int jo_3_size = jo_3.getJSONArray("region")
										.size();
								String region_code_3 = (String) pSet.getParameter("region_code");
								for (int k = 0; k < jo_3_size; k++) {
									if ("1".equals(jo_3.getJSONArray("region")
											.getJSONObject(k).getString("TYPE"))
											&& !region_code_3.equals(jo_3.getJSONArray("region").getJSONObject(k).getString("CODE"))) {
										s3.put("CODE",
												jo_3.getJSONArray("region")
														.getJSONObject(k)
														.getString("CODE"));
										s3.put("NAME",
												jo_3.getJSONArray("region")
														.getJSONObject(k)
														.getString("NAME"));
										ja_3.add(s3);
									}
								}								
								s2.put("CODE", jo_2.getJSONArray("region")
										.getJSONObject(j).getString("CODE"));
								s2.put("NAME", jo_2.getJSONArray("region")
										.getJSONObject(j).getString("NAME"));
								s2.put("region", ja_3);
								ja_2.add(s2);
							}
						}						
						s1.put("CODE", jo_1.getJSONArray("region")
								.getJSONObject(i).getString("CODE"));
						s1.put("NAME", jo_1.getJSONArray("region")
								.getJSONObject(i).getString("NAME"));
						s1.put("region", ja_2);
						ja.add(s1);
					}
				}
			}
			obj.put("region", ja);
			obj.put("code", "200");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("code", "300");
		}
		return obj;
	}
	//endregion
	
	//region 根据栏目名称获取内容列表getContentListByChannelName
	/**
	 * 根据栏目名称获取内容列表
	 * 
	 * @param pSet
	 *            key 查询
	 *            orderBy= order by name desc,ctime desc
	 * @return
	 */
	public DataSet getContentListByChannelName(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String key = (String) pSet.get("key");
			if ("".equals(key) || key == null) {
				key = "%%";
			} else {
				key = "%" + key + "%";
			}
			String type = (String)pSet.get("type");//0文字1图片2视频3链接
			if ("".equals(type) || type == null) {
				type = "%%";
			} else {
				type = "%" + type + "%";
			}
			String channelname = (String) pSet.get("channelname");
			String region_code = (String) pSet.get("region_code");
			String orderBy = (String) pSet.get("orderBy");
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			
			int start = (page - 1) * limit;
			String sql = null;
			sql = "select t.*,to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ctime_char from pub_content t where t.cid in (select id from pub_channel s  where s.rid = ? and s.name = ?) and t.name like ? and  t.type like ? ";
			if(StringUtil.isNotEmpty(orderBy)){
				sql+= orderBy;
			}else{
				sql+=" order by ctime desc,t.id desc";
			}
			ds = DbHelper.query(sql, start, limit, new Object[] { region_code,
					channelname, key,type }, conn, "icityDataSource");
			int ds_size = ds.getJAData().size();
			JSONArray ds_ja = new JSONArray();
			for (int k = 0; k < ds_size; k++) {
				JSONArray ja = new JSONArray();
				String id = ds.getJAData().getJSONObject(k).getString("ID");
				String sql_attach = "select * from attach t where t.conid = ?";
				DataSet dsAttach = DbHelper.query(sql_attach,
						new Object[] { id }, conn);
				int totla = dsAttach.getTotal();
				if (totla > 0) {
					// 这里加入下载代码
					JSONArray upfile = dsAttach.getJAData();
					for (int i = 0; i < upfile.size(); i++) {
						JSONObject jo = new JSONObject();
						// ---下载文件到项目路径本地 start
						String fileName = (String) ((JSONObject) upfile.get(i))
								.get("NAME");
						String fileType = (String) ((JSONObject) upfile.get(i))
								.get("TYPE");
						String doc_id = (String) ((JSONObject) upfile.get(i))
								.get("DOCID");
						jo.put("ATTACHTYPE", fileType);
						jo.put("ATTACHNAME", fileName);
						jo.put("ATTACHURL",
								"3".equals(fileType) ? "iWebSite/static/data/"
										+ fileName : "file/upload/" + fileName);
						ja.add(jo);
						UploadUtil.downloadFile(fileName, doc_id, fileType);
					}
				}
				JSONObject jo_ds = ds.getJAData().getJSONObject(k);
				jo_ds.put("ATTACHARRAY", ja);
				ds_ja.add(jo_ds);
			}
			conn.commit();
			ds.setRawData(ds_ja.toString());
			ds.setState(StateType.SUCCESS);
			ds.setMessage("pageindex:" + page + ",pagesize:" + limit);
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
	
	//region 根据内容id获取内容详情getContentDetailById
	/**
	 * 根据内容id获取内容详情
	 * @param pSet
	 * @return
	 */
	public DataSet getContentDetailById(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String conid = (String) pSet.get("conid");
			String sql = null;
			sql = "select t.*,to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ctime_char from pub_content t where t.id=?";
			ds = DbHelper.query(sql, new Object[] { conid }, conn);

			int ds_size = ds.getJAData().size();
			JSONArray ds_ja = new JSONArray();
			if (ds_size > 0) {
				JSONArray ja = new JSONArray();
				String id = ds.getJAData().getJSONObject(0).getString("ID");
				String sql_attach = "select * from attach t where t.conid = ?";
				DataSet dsAttach = DbHelper.query(sql_attach,
						new Object[] { id }, conn);
				int totla = dsAttach.getTotal();
				if (totla > 0) {
					// 这里加入下载代码
					JSONArray upfile = dsAttach.getJAData();
					for (int i = 0; i < upfile.size(); i++) {
						JSONObject jo = new JSONObject();
						// ---下载文件到项目路径本地 start
						String fileName = (String) ((JSONObject) upfile.get(i))
								.get("NAME");
						String fileType = (String) ((JSONObject) upfile.get(i))
								.get("TYPE");
						String doc_id = (String) ((JSONObject) upfile.get(i))
								.get("DOCID");
						jo.put("ATTACHTYPE", fileType);
						jo.put("ATTACHNAME", fileName);
						jo.put("ATTACHURL",
								"3".equals(fileType) ? "iWebSite/static/data/"
										+ fileName : "file/upload/" + fileName);
						ja.add(jo);
						UploadUtil.downloadFile(fileName, doc_id, fileType);
					}
				}
				JSONObject jo_ds = ds.getJAData().getJSONObject(0);
				jo_ds.put("ATTACHARRAY", ja);
				ds_ja.add(jo_ds);
			}
			conn.commit();
			ds.setRawData(ds_ja.toString());
			ds.setState(StateType.SUCCESS);
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
	
	//region 获取收藏列表getBusinessFavoriteList
	/**
	 * 获取收藏列表
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessFavoriteList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "SELECT * FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? ";
			int limit = Integer.parseInt((String) pSet.get("limit"));
			int page = Integer.parseInt((String) pSet.get("page"));
			int start = (page - 1) * limit;
			ds = DbHelper.query(sql, start, limit, new Object[] { ucid }, conn,
					"icityDataSource");
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
	
	//region 添加收藏记录addFaveriot
	/**
	 * 添加收藏记录
	 * @param pSet
	 * @return
	 */
	public DataSet addFavorite(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			java.sql.Timestamp date = CommonUtils.getInstance()
					.parseDateToTimeStamp(new java.util.Date(),
							CommonUtils.YYYY_MM_DD_HH_MM_SS_SSS);
			String sql_select = "SELECT ID FROM POWER_BASE_INFO_FAVORITE WHERE SXBM=? AND USID=? ";
			DataSet ret = DbHelper.query(sql_select, new Object[] {
					(String) pSet.get("sxbm"), ucid }, conn);
			if (ret.getState() == 1) {
				if (ret.getTotal() > 0) {
					String sql = "UPDATE POWER_BASE_INFO_FAVORITE SET SXID=?,SXMC=?, STATUS=1 WHERE SXBM=? AND USID=? ";
					int i = DbHelper.update(
							sql,
							new Object[] { (String) pSet.get("sxid"),
									(String) pSet.get("sxmc"),
									(String) pSet.get("sxbm"), ucid }, conn);
					if (i == 0) {
						ds.setState(StateType.FAILT);
						ds.setMessage("数据库操作失败！");
					} else {
						ds.setState(StateType.SUCCESS);
					}
				} else {
					String sql = "INSERT INTO POWER_BASE_INFO_FAVORITE(ID,SXID, SXBM,SXMC, USID, FTIME, STATUS)VALUES(?,?,?,?,?,?,?)";
					int i = DbHelper
							.update(sql,
									new Object[] { Tools.getUUID32(),
											(String) pSet.get("sxid"),
											(String) pSet.get("sxbm"),
											(String) pSet.get("sxmc"), ucid,
											date, "1" }, conn);
					if (i == 0) {
						ds.setState(StateType.FAILT);
						ds.setMessage("数据库操作失败！");
					} else {
						ds.setState(StateType.SUCCESS);
					}
				}
				conn.commit();
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
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
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	//endregion
	
	//region 删除收藏updateStatus
	/**
	 * 删除收藏
	 * @param pSet
	 * @return
	 */
	public DataSet updateStatus(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String id = (String) pSet.get("id");
			String sql = "UPDATE POWER_BASE_INFO_FAVORITE SET STATUS='0' where id=?";
			int i = DbHelper.update(sql, new Object[] { id }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("删除收藏成功！");
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
	
	//region 从附件表获取当前用户下的材料列表getAttachList
	/**
	 * 从附件表获取当前用户下的材料列表
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		int page = Integer.parseInt(pSet.getParameter("page").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		int start = (page - 1) * limit;
		String ucid = (String) pSet.get("ucid");
		try {
			String sql = "select * from business_attach where ucid=?";
			ds = DbHelper.query(sql, start, limit, new Object[] { ucid }, conn,
					"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 根据事项id获取事项的基本信息getPermission
	/**
	 * 根据事项id获取事项的基本信息
	 * @param pSet
	 *            add 属性 IS_COLLECTION 是否收藏 1已收藏0未收藏
	 * @return
	 */
	public DataSet getPermission(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet ds_permission;
		Command cmd = new Command("app.icity.ServiceCmd");
		cmd.setParameter("id", pSet.get("itemId"));
		ds_permission = cmd.execute("getPermission");
		if (ds_permission.getState() == StateType.SUCCESS) {
			//System.out.println("ds_permission:"+ds_permission.toString());
			ds.setTotal(ds_permission.getTotal());
			ds.setState(ds_permission.getState());
			ds.setMessage(ds_permission.getMessage());
			JSONObject m_data = ds_permission.getJOData();
			JSONObject itemBasicInfo = new JSONObject();
			JSONObject basic =  m_data.getJSONObject("itemBasicInfo");
			itemBasicInfo.put("IS_ONLINE", basic.containsKey("IS_ONLINE") ?basic.getString("IS_ONLINE"): "");// 是否在线办理
			itemBasicInfo.put("REGION_NAME",basic.containsKey("REGION_NAME") ?basic.getString("REGION_NAME"): "");
			itemBasicInfo.put("REGION_CODE",basic.containsKey("REGION_CODE") ?basic.getString("REGION_CODE"): "");
			itemBasicInfo.put("ITEM_CODE", basic.containsKey("CODE") ?basic.getString("CODE"): "");// 事项CODE
			itemBasicInfo.put("ITEM_ID", basic.containsKey("ID") ?basic.getString("ID"): "");// 事项ID
			itemBasicInfo.put("ITEM_NAME",basic.containsKey("NAME") ?basic.getString("NAME"): "");// 事项名称
			itemBasicInfo.put("CODE", basic.containsKey("CODE")?basic.getString("CODE"):"");// 事项CODE
			itemBasicInfo.put("ID", basic.containsKey("ID")?basic.getString("ID"):"");// 事项ID
			itemBasicInfo.put("NAME", basic.containsKey("NAME")?basic.getString("NAME"):"");// 事项名称
			itemBasicInfo.put("TYPE", basic.containsKey("TYPE") ?basic.getString("TYPE"): "");// 事项类型
			itemBasicInfo.put("ORG_NAME", basic.containsKey("ORG_NAME") ?basic.getString("ORG_NAME"): "");// 实施主体
			itemBasicInfo.put("ORG_CODE", basic.containsKey("ORG_CODE") ?basic.getString("ORG_CODE"): "");// 实施主体
			itemBasicInfo.put("ORG_NAME_ITEM",basic.containsKey("ORG_NAME_ITEM") ?basic.getString("ORG_NAME_ITEM"): "");// 共同实施部门
			itemBasicInfo.put("LAW_TIME", basic.containsKey("LAW_TIME") ?basic.getString("LAW_TIME"): "");// 法定期限
			itemBasicInfo.put("CONSULT_PHONE",basic.containsKey("CONSULT_PHONE") ?basic.getString("CONSULT_PHONE"): "");// 咨询电话
			itemBasicInfo.put("COMPLAIN_PHONE",basic.containsKey("COMPLAIN_PHONE") ?basic.getString("COMPLAIN_PHONE"): "");// 投诉电话
			itemBasicInfo.put("IS_CHARGE", basic.containsKey("IS_CHARGE") ?basic.getString("IS_CHARGE"): "");// 是否收费
			itemBasicInfo.put("AGREE_TIME",basic.containsKey("AGREE_TIME") ?basic.getString("AGREE_TIME"): ""); // 承诺期限
			itemBasicInfo.put("AGENT_NAME",basic.containsKey("AGENT_NAME") ?basic.getString("AGENT_NAME"): ""); // 承办单位
			itemBasicInfo.put("SUB_TYPE",basic.containsKey("SUB_TYPE") ? basic.getString("SUB_TYPE") : ""); // 便民子项
			itemBasicInfo.put("LAW_TIME_UNIT_VALUE",basic.containsKey("LAW_TIME_UNIT_VALUE") ? basic.getString("LAW_TIME_UNIT_VALUE") : ""); // 法定期限单位
			itemBasicInfo.put("AGREE_TIME_UNIT_VALUE",basic.containsKey("AGREE_TIME_UNIT_VALUE") ? basic.getString("AGREE_TIME_UNIT_VALUE") : ""); // 承诺期限单位

			itemBasicInfo.put("IS_COLLECTION", "0");// 是否已收藏
			String urlForm = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/getFormInfo?itemId="
					+ basic.getString("ID"));
			System.out.println("urlForm:"+urlForm);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject form = JSONObject.fromObject(client.getResult(urlForm,
					""));
			JSONObject formSettingInfo = form.getJSONObject("info");
			if (!formSettingInfo.isNullObject()) {
				String formid = formSettingInfo.getString("formId");
				String formName = formSettingInfo.getString("formName");
				itemBasicInfo.put("FORM_ID", formid);
				itemBasicInfo.put("FORM_NAME", formName);
			}else{
				itemBasicInfo.put("FORM_ID", "");
				itemBasicInfo.put("FORM_NAME", "");
			}
			JSONArray window = new JSONArray();
			int window_len = m_data.getJSONArray("window").size();
			for (int i = 0; i < window_len; i++) {
				JSONObject m_window = new JSONObject();
				JSONObject win = m_data.getJSONArray("window").getJSONObject(i);
				m_window.put("CODE", win.containsKey("CODE")?win.get("CODE"):"");// 窗口CODE
				m_window.put("NAME", win.containsKey("NAME")?win.get("NAME"):"");// 窗口名称
				m_window.put("PHONE", win.containsKey("PHONE")?win.get("PHONE"):"");// 窗口电话
				m_window.put("COMPLAINT_PHONE", win.containsKey("COMPLAINT_PHONE")?win.get("COMPLAINT_PHONE"):"");// 投诉电话				
				m_window.put("ADDRESS", win.containsKey("ADDRESS")?win.get("ADDRESS"):"");// 窗口地址
				m_window.put("OFFICE_HOUR", win.containsKey("OFFICE_HOUR")?win.get("OFFICE_HOUR"):"");// 工作时间
				m_window.put("WINDOW_NUM", win.containsKey("WINDOW_NUM")?win.get("WINDOW_NUM"):"");// 窗口序号
				m_window.put("WINDOW_NAME", win.containsKey("WINDOW_NAME")?win.get("WINDOW_NAME"):"");
				window.add(m_window);
			}
			JSONArray materials = new JSONArray();
			JSONArray materials_service = m_data.containsKey("materials")?m_data.getJSONArray("materials"):m_data.getJSONArray("material");
			int materials_len = materials_service.size();
			for (int i = 0; i < materials_len; i++) {
				JSONObject m_materials = new JSONObject();
				JSONObject mater = materials_service.getJSONObject(i);
				m_materials.put("MATERIAL_CODE",mater.containsKey("MATERIAL_CODE")?mater.get("MATERIAL_CODE"):"");// 材料编码
				m_materials.put("NAME", mater.containsKey("NAME")?mater.get("NAME"):"");// 材料名称
				m_materials.put("TYPE_VALUE", mater.containsKey("TYPE_VALUE")?mater.get("TYPE_VALUE"):"");
				m_materials.put("BUSINESS_TYPE_VALUE",mater.containsKey("BUSINESS_TYPE_VALUE")?mater.get("BUSINESS_TYPE_VALUE"):"");
				m_materials.put("BUSINESS_TYPE",mater.containsKey("BUSINESS_TYPE")?mater.get("BUSINESS_TYPE"):"");// 材料类型：
																// Apply申报提交材料;
																// Approve审批过程材料
				m_materials.put("MUST", mater.containsKey("MUST")?mater.get("MUST"):"");// 是否必须材料
				m_materials.put("FLAG", mater.containsKey("FLAG")?mater.get("FLAG"):"");
				m_materials.put("TYPE", mater.containsKey("TYPE")?mater.get("TYPE"):"");
				m_materials.put("COPY", mater.containsKey("COPY")?mater.get("COPY"):"");
				m_materials.put("EXPLAIN", mater.containsKey("EXPLAIN")?mater.get("EXPLAIN"):"");// 材料详细要求
				m_materials.put("REMARK", mater.containsKey("REMARK")?mater.get("REMARK"):"");
				m_materials.put("HOW_TRANSACT", mater.containsKey("HOW_TRANSACT")?mater.get("HOW_TRANSACT"):"");//如何获取

				materials.add(m_materials);
			}
			JSONArray condition = new JSONArray();
			int condition_len = m_data.getJSONArray("condition").size();
			for (int i = 0; i < condition_len; i++) {
				JSONObject m_condition = new JSONObject();
				JSONObject cond = m_data.getJSONArray("condition").getJSONObject(i);
				m_condition.put("CODE", cond.containsKey("CODE")?cond.get("CODE"):"");// 办理条件CODE
				m_condition.put("NAME", cond.containsKey("NAME")?cond.get("NAME"):"");// 办理条件
				condition.add(m_condition);
			}
			JSONObject data = new JSONObject();
			data.put("itemBasicInfo", itemBasicInfo);
			data.put("window", window);
			data.put("materials", materials);
			data.put("condition", condition);
			JSONArray charge = m_data.getJSONArray("charge");// 收费标准
			data.put("charge", charge);
			JSONArray legalbasis = m_data.getJSONArray("legalbasis");// 法律依据
			data.put("legalbasis", legalbasis);
			JSONArray outmap = m_data.getJSONArray("outmap");// 办理流程图
			data.put("outmap", outmap);
			JSONArray handlingprocess = m_data.getJSONArray("handlingprocess");//办理流程
			data.put("handlingprocess", handlingprocess);
			String ucid = (String) pSet.get("ucid");
			if (!"".equals(ucid)) {
				Connection conn = DbHelper.getConnection("icityDataSource");
				try {
					String sql = "SELECT 1 FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? ";
					DataSet ds_collection = DbHelper.query(sql,
							new Object[] { ucid }, conn);
					if (ds_collection.getTotal() > 0) {
						data.getJSONObject("itemBasicInfo").put(
								"IS_COLLECTION", "1");
					}
				} catch (Exception e) {
					e.printStackTrace();
					ds.setState(StateType.FAILT);
					ds.setMessage(e.toString());
					ds.setRawData(new JSONArray());
				} finally {
					DBSource.closeConnection(conn);
				}
			}
			ds.setRawData(data.toString());
		} else {
			ds.setState(ds_permission.getState());
			ds.setMessage(ds_permission.getMessage());
		}
		return ds;
	}
	//endregion
	
	//region 根据事项code获取事项的基本信息getPermissionAll
	/**
	 * 根据事项code获取事项的基本信息
	 * @param pSet
	 *            add 属性 IS_COLLECTION 是否收藏 1已收藏0未收藏
	 * @return
	 */
	public DataSet getPermissionAll(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet ds_permission;
		Command cmd = new Command("app.icity.ServiceCmd");
		cmd.setParameter("code", pSet.get("itemCode"));
		ds_permission = cmd.execute("getPermissionAll");
		if (ds_permission.getState() == StateType.SUCCESS) {
			ds.setTotal(ds_permission.getTotal());
			ds.setState(ds_permission.getState());
			ds.setMessage(ds_permission.getMessage());
			JSONObject m_data = ds_permission.getJOData();
			JSONObject itemBasicInfo = new JSONObject();
			JSONObject basic = m_data.getJSONObject("itemBasicInfo");
			itemBasicInfo.put("IS_ONLINE", basic.containsKey("IS_ONLINE")?basic.getString("IS_ONLINE"):"");// 是否在线办理
			itemBasicInfo.put("REGION_NAME", basic.containsKey("REGION_NAME")?basic.getString("REGION_NAME"):"");
			itemBasicInfo.put("REGION_CODE",basic.containsKey("REGION_CODE")?basic.getString("REGION_CODE"):"");
			itemBasicInfo.put("ITEM_CODE", basic.containsKey("CODE")?basic.getString("CODE"):"");// 事项CODE
			itemBasicInfo.put("ITEM_ID", basic.containsKey("ID")?basic.getString("ID"):"");// 事项ID
			itemBasicInfo.put("ITEM_NAME", basic.containsKey("NAME")?basic.getString("NAME"):"");// 事项名称
			itemBasicInfo.put("CODE", basic.containsKey("CODE")?basic.getString("CODE"):"");// 事项CODE
			itemBasicInfo.put("ID", basic.containsKey("ID")?basic.getString("ID"):"");// 事项ID
			itemBasicInfo.put("NAME", basic.containsKey("NAME")?basic.getString("NAME"):"");// 事项名称
			itemBasicInfo.put("TYPE", basic.containsKey("TYPE")?basic.getString("TYPE"):"");// 事项类型
			itemBasicInfo.put("ORG_NAME", basic.containsKey("ORG_NAME")?basic.getString("ORG_NAME"):"");// 实施主体
			itemBasicInfo.put("ORG_CODE", basic.containsKey("ORG_CODE")?basic.getString("ORG_CODE"):"");// 实施主体
			itemBasicInfo.put("ORG_NAME_ITEM",basic.containsKey("ORG_NAME_ITEM")?basic.getString("ORG_NAME_ITEM"):"");// 共同实施部门
			itemBasicInfo.put("LAW_TIME", basic.containsKey("LAW_TIME")?basic.getString("LAW_TIME"):"");// 法定期限
			itemBasicInfo.put("CONSULT_PHONE",basic.containsKey("CONSULT_PHONE")?basic.getString("CONSULT_PHONE"):"");// 咨询电话
			itemBasicInfo.put("COMPLAIN_PHONE",basic.containsKey("COMPLAIN_PHONE")?basic.getString("COMPLAIN_PHONE"):"");// 投诉电话
			itemBasicInfo.put("IS_CHARGE", basic.containsKey("IS_CHARGE")?basic.getString("IS_CHARGE"):"");// 是否收费
			itemBasicInfo.put("AGREE_TIME",basic.containsKey("AGREE_TIME")?basic.getString("AGREE_TIME"):""); // 承诺期限
			itemBasicInfo.put("AGENT_NAME",basic.containsKey("AGENT_NAME")?basic.getString("AGENT_NAME"):""); // 承办单位
			itemBasicInfo.put("SUB_TYPE",basic.containsKey("SUB_TYPE") ? basic.getString("SUB_TYPE") : ""); // 便民子项
			itemBasicInfo.put("LAW_TIME_UNIT_VALUE",basic.containsKey("LAW_TIME_UNIT_VALUE") ? basic.getString("LAW_TIME_UNIT_VALUE") : ""); // 法定期限单位
			itemBasicInfo.put("AGREE_TIME_UNIT_VALUE",basic.containsKey("AGREE_TIME_UNIT_VALUE") ? basic.getString("AGREE_TIME_UNIT_VALUE") : ""); // 承诺期限单位

			itemBasicInfo.put("IS_COLLECTION", "0");// 是否已收藏
			String urlForm = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/getFormInfo?itemId="
					+ basic.getString("ID"));
			HttpClientUtil client = new HttpClientUtil();
			JSONObject form = JSONObject.fromObject(client.getResult(urlForm,
					""));
			JSONObject formSettingInfo = form.getJSONObject("info");
			if (!formSettingInfo.isNullObject()) {
				String formid = formSettingInfo.containsKey("formId")?formSettingInfo.getString("formId"):"";
				String formName = formSettingInfo.containsKey("formName")?formSettingInfo.getString("formName"):"";
				itemBasicInfo.put("FORM_ID", formid);
				itemBasicInfo.put("FORM_NAME", formName);
			}else{
				itemBasicInfo.put("FORM_ID", "");
				itemBasicInfo.put("FORM_NAME", "");
			}
			JSONArray window = new JSONArray();
			System.out.print(m_data.getJSONArray("window"));
			int window_len = m_data.getJSONArray("window").size();
			for (int i = 0; i < window_len; i++) {
				JSONObject m_window = new JSONObject();
				JSONObject win = m_data.getJSONArray("window").getJSONObject(i);
				m_window.put("CODE", win.containsKey("CODE")?win.getString("CODE"):"");// 窗口CODE
				m_window.put("NAME", win.containsKey("NAME")?win.getString("NAME"):"");// 窗口名称
				m_window.put("PHONE", win.containsKey("PHONE")?win.getString("PHONE"):"");// 窗口电话
				m_window.put("COMPLAINT_PHONE", win.containsKey("COMPLAINT_PHONE")?win.getString("COMPLAINT_PHONE"):"");// 投诉电话				
				m_window.put("ADDRESS", win.containsKey("ADDRESS")?win.getString("ADDRESS"):"");// 窗口地址
				m_window.put("OFFICE_HOUR", win.containsKey("OFFICE_HOUR")?win.getString("OFFICE_HOUR"):"");// 工作时间
				m_window.put("WINDOW_NUM", win.containsKey("WINDOW_NUM")?win.getString("WINDOW_NUM"):"");// 窗口序号
				m_window.put("WINDOW_NAME", win.containsKey("WINDOW_NAME")?win.getString("WINDOW_NAME"):"");
				window.add(m_window);
			}
			JSONArray materials = new JSONArray();
			JSONArray materials_service = m_data.containsKey("materials")?m_data.getJSONArray("materials"):m_data.getJSONArray("material");
			int materials_len = materials_service.size();
			for (int i = 0; i < materials_len; i++) {
				JSONObject m_materials = new JSONObject();
				JSONObject mater = materials_service.getJSONObject(i);
				m_materials.put("MATERIAL_CODE",mater.containsKey("MATERIAL_CODE")?mater.getString("MATERIAL_CODE"):"");// 材料编码
				m_materials.put("NAME", mater.containsKey("NAME")?mater.getString("NAME"):"");// 材料名称
				m_materials.put("TYPE_VALUE", mater.containsKey("TYPE_VALUE")?mater.getString("TYPE_VALUE"):"");
				m_materials.put("BUSINESS_TYPE_VALUE",mater.containsKey("BUSINESS_TYPE_VALUE")?mater.getString("BUSINESS_TYPE_VALUE"):"");
				m_materials.put("BUSINESS_TYPE",mater.containsKey("BUSINESS_TYPE")?mater.getString("BUSINESS_TYPE"):"");// 材料类型：
																// Apply申报提交材料;
																// Approve审批过程材料
				m_materials.put("MUST", mater.containsKey("MUST")?mater.getString("MUST"):"");// 是否必须材料
				m_materials.put("FLAG", mater.containsKey("FLAG")?mater.getString("FLAG"):"");
				m_materials.put("TYPE", mater.containsKey("TYPE")?mater.getString("TYPE"):"");
				m_materials.put("COPY", mater.containsKey("COPY")?mater.getString("COPY"):"");
				m_materials.put("EXPLAIN", mater.containsKey("EXPLAIN")?mater.getString("EXPLAIN"):"");// 材料详细要求
				m_materials.put("REMARK", mater.containsKey("REMARK")?mater.getString("REMARK"):"");
				m_materials.put("HOW_TRANSACT", mater.containsKey("HOW_TRANSACT")?mater.getString("HOW_TRANSACT"):"");//如何获取

				materials.add(m_materials);
			}
			JSONArray condition = new JSONArray();
			int condition_len = m_data.getJSONArray("condition").size();
			for (int i = 0; i < condition_len; i++) {
				JSONObject m_condition = new JSONObject();
				JSONObject cond = m_data.getJSONArray("condition").getJSONObject(i);
				m_condition.put("CODE", cond.containsKey("CODE")?cond.getString("CODE"):"");// 办理条件CODE
				m_condition.put("NAME", cond.containsKey("NAME")?cond.getString("NAME"):"");// 办理条件
				condition.add(m_condition);
			}
			JSONObject data = new JSONObject();
			data.put("itemBasicInfo", itemBasicInfo);
			data.put("window", window);
			data.put("materials", materials);
			data.put("condition", condition);
			
			JSONArray charge = m_data.getJSONArray("charge");// 收费标准
			data.put("charge", charge);

			JSONArray legalbasis = m_data.getJSONArray("legalbasis");// 法律依据
			data.put("legalbasis", legalbasis);

			JSONArray outmap = m_data.getJSONArray("outmap");// 办理流程图
			data.put("outmap", outmap);
			
			JSONArray handlingprocess = m_data.getJSONArray("handlingprocess");//办理流程
			data.put("handlingprocess", handlingprocess);
			String ucid = (String) pSet.get("ucid");
			if (!"".equals(ucid)) {
				Connection conn = DbHelper.getConnection("icityDataSource");
				try {
					String sql = "SELECT 1 FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? ";
					DataSet ds_collection = DbHelper.query(sql,
							new Object[] { ucid }, conn);
					if (ds_collection.getTotal() > 0) {
						data.getJSONObject("itemBasicInfo").put(
								"IS_COLLECTION", "1");
					}
				} catch (Exception e) {
					e.printStackTrace();
					ds.setState(StateType.FAILT);
					ds.setMessage(e.toString());
					ds.setRawData(new JSONArray());
				} finally {
					DBSource.closeConnection(conn);
				}
			}
			ds.setRawData(data.toString());
		} else {
			ds.setState(ds_permission.getState());
			ds.setMessage(ds_permission.getMessage());
		}
		return ds;
	}
	//endregion
	
	//region 获取事项列表（分页）getMattersList
	/**
	 * 获取事项列表（分页）
	 * @param pSet
	 *            add 属性 IS_COLLECTION 是否收藏
	 * @return 手机app和爱城市、微信调用
	 */
	public DataSet getMattersList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet ds_matterslist;
		Command cmd = new Command("app.icity.govservice.GovProjectCmd");
		cmd.setParameter("region_code", (String) pSet.get("region_code"));
		cmd.setParameter("page", (String) pSet.get("page"));
		cmd.setParameter("limit", (String) pSet.get("limit"));
		cmd.setParameter("SearchName", (String) pSet.get("SearchName"));
		cmd.setParameter("CAT", (String) pSet.get("cat"));// theme dept
		cmd.setParameter(
				"ID",
				"dept".equals((String) pSet.get("cat")) ? (String) pSet
						.get("dept_id") : (String) pSet.get("title_name"));
		cmd.setParameter("PAGEMODEL", (String) pSet.get("pagemodel"));// person
																		// ent
																		// bmfw
		cmd.setParameter("SUIT_ONLINE", (String) pSet.get("online"));// 1在线办理的
		cmd.setParameter("TYPE", (String) pSet.get("type"));// 事项类型 XK BM 等
		ds_matterslist = cmd.execute("getMattersList");
		try {
			if (ds_matterslist.getState() == StateType.SUCCESS
					&& ds_matterslist.getTotal() > 0) {
				ds.setTotal(ds_matterslist.getTotal());
				ds.setState(ds_matterslist.getState());
				ds.setMessage(ds_matterslist.getMessage());
				JSONArray data = ds_matterslist.getJAData();
				int size = data.size();
				String sxids = "";
				StringBuffer sxidsBuffer = new StringBuffer();
				for (int i = 0; i < size; i++) {
					data.getJSONObject(i).put("ITEM_CODE",data.getJSONObject(i).containsKey("CODE") ?data.getJSONObject(i).getString("CODE"):"");
					data.getJSONObject(i).put("ITEM_ID",data.getJSONObject(i).containsKey("ITEM_ID") ?data.getJSONObject(i).getString("ITEM_ID"):"");
					data.getJSONObject(i).put("ITEM_NAME",data.getJSONObject(i).containsKey("NAME") ?data.getJSONObject(i).getString("NAME"):"");
					data.getJSONObject(i).put("IS_COLLECTION", "0");
					sxidsBuffer
							.append("'"
									+ data.getJSONObject(i)
											.getString("ITEM_ID") + "',");
				}
				sxids = sxidsBuffer.toString();
				sxids = sxids.substring(0, sxids.length() - 1);
				String ucid = (String) pSet.get("ucid");
				if (!"".equals(ucid)) {
					Connection conn = DbHelper.getConnection("icityDataSource");
					try {
						String sql = "SELECT PF.sxid FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? and PF.sxid in ("
								+ sxids + ")";
						DataSet ds_collection = DbHelper.query(sql,
								new Object[] { ucid }, conn);
						if (ds_collection.getTotal() > 0) {
							int len = ds_collection.getTotal();
							for (int i = 0; i < len; i++) {
								for (int j = 0; j < size; j++) {
									if (data.getJSONObject(j)
											.getString("ITEM_ID")
											.equals(ds_collection.getJAData()
													.getJSONObject(i)
													.getString("SXID"))) {
										data.getJSONObject(j).put(
												"IS_COLLECTION", "1");
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						ds.setState(StateType.FAILT);
						ds.setMessage(e.toString());
						ds.setRawData(new JSONArray());
					} finally {
						DBSource.closeConnection(conn);
					}
				}
				ds.setRawData(data.toString());
			} else {
				System.out.print("出错了？");
				ds.setRawData(new JSONArray());
				ds.setState(ds_matterslist.getState());
				ds.setMessage(ds_matterslist.getMessage());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ds.setRawData(new JSONArray());
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.toString());
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
		String content = "【"+SecurityConfig.getString("SubAppTitle")+"】您的短信验证码为：" + sRand.toString();
		Boolean type = sendMessage(phoneNum, content, "others");
		if (type) {
			ds.setState(StateType.SUCCESS);
			ds.setRawData(sRand.toString());
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
	
	//region 获取当前用户评论列表getStarLevelEvaluation
	/**
	 * 获取当前用户评论列表
	 * @param pSet
	 * @return
	 */
	public DataSet getStarLevelEvaluation(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		int page = Integer.parseInt(pSet.getParameter("page").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		int start = (page - 1) * limit;
		String ucid = (String) pSet.get("ucid");
		try {
			String sql = "select * from star_level_evaluation where CREATOR_ID=?";
			ds = DbHelper.query(sql, start, limit, new Object[] { ucid }, conn,
					"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 获取事项办事指南评论列表getStarLevelEvaluationByItem
	/**
	 * 获取事项办事指南评论列表
	 * @param pSet
	 * @return
	 */
	public DataSet getStarLevelEvaluationByItem(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String star_level = (String) pSet.get("star_level");
		int page = Integer.parseInt(pSet.getParameter("page").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		int start = (page - 1) * limit;
		String itemId = (String) pSet.get("itemId");
		String region_id = (String) pSet.get("region_id");
		String evaluate_type = (String) pSet.get("evaluate_type");
		String[] evaluate_types = evaluate_type.split(",");
		evaluate_type = "";
		StringBuffer eBuffer = new StringBuffer();
		for (int i = 0; i < evaluate_types.length; i++) {
			eBuffer.append("'" + evaluate_types[i] + "',");
		}
		evaluate_type = eBuffer.toString();
		evaluate_type = evaluate_type.substring(0, evaluate_type.length() - 1);
		try {
			String sql = "select t.*,s.name from star_level_evaluation t left join uc_user s on t.creator_id = s.id where evaluate_type in ("
					+ evaluate_type
					+ ") and service_id = ? and region_code = ?";
			if (StringUtil.isNotEmpty(star_level)) {
				sql += " and star_level=" + Integer.parseInt(star_level);
			}
			ds = DbHelper.query(sql, start, limit, new Object[] { itemId,
					region_id }, conn, "icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 满意度评价统计getEvaluationStat
	/**
	 * 满意度评价统计
	 * @param pSet
	 * @return
	 */
	public DataSet getEvaluationStat(ParameterSet pSet) {
		StringBuffer sql = new StringBuffer();
		sql.append("select o.rate||'%' DORATE,o.rate1||'%' DORATE1,o.rate2||'%' DORATE2,o.rate3||'%' DORATE3,o.rate4||'%' DORATE4,o.rate5||'%' DORATE5,o.comp DOSCORE,");
		sql.append("p.rate||'%' GUIDERATE,p.rate1||'%' GUIDERATE1,p.rate2||'%' GUIDERATE2,p.rate3||'%' GUIDERATE3,p.rate4||'%' GUIDERATE4,p.rate5||'%' GUIDERATE5,p.comp GUIDECORE,");
		sql.append("(o.rate+p.rate)/2||'%' COMPRATE,(o.rate1+p.rate1)/2||'%' COMPRATE1,(o.rate2+p.rate2)/2||'%' COMPRATE2,(o.rate3+p.rate3)/2||'%' COMPRATE3,");
		sql.append("(o.rate4+p.rate4)/2||'%' COMPRATE4,(o.rate5+p.rate5)/2||'%' COMPRATE5,(o.comp+p.comp)/2 COMPSCORE from (");
		sql.append("select round((count3+count4+count5)/count*100,0) rate,round(count1/count*100,0) rate1,round(count2/count*100,0) rate2,round(count3/count*100,0) rate3,");
		sql.append("round(count4/count*100,0) rate4,round(count5/count*100,0) rate5, round((sum1+sum2+sum3+sum4+sum5)/count,2) comp from (");
		sql.append("select case when count(1)=0 then 1 else count(1) end count,count(case when t.STAR_LEVEL='1' then id else null end) count1,count(case when t.STAR_LEVEL='2' then id else null end) count2,");
		sql.append("count(case when t.STAR_LEVEL='3' then id else null end) count3,count(case when t.STAR_LEVEL='4' then id else null end) count4,");
		sql.append("count(case when t.STAR_LEVEL='5' then id else null end) count5,sum(case when t.STAR_LEVEL='1' then 1 else 0 end) sum1,");
		sql.append("sum(case when t.STAR_LEVEL='2' then 2 else 0 end) sum2,sum(case when t.STAR_LEVEL='3' then 3 else 0 end) sum3,");
		sql.append("sum(case when t.STAR_LEVEL='4' then 4 else 0 end) sum4,sum(case when t.STAR_LEVEL='5' then 5 else 0 end) sum5");
		sql.append(" from star_level_evaluation t  where t.service_id=? and t.evaluate_type='1')) o,");
		sql.append("(select round((count3+count4+count5)/count*100,0) rate,round(count1/count*100,0) rate1,round(count2/count*100,0) rate2,");
		sql.append("round(count3/count*100,0) rate3,round(count4/count*100,0) rate4,round(count5/count*100,0) rate5, round((sum1+sum2+sum3+sum4+sum5)/count,2) comp from (");
		sql.append("select  case when count(1)=0 then 1 else count(1) end count,count(case when t.STAR_LEVEL='1' then id else null end) count1,count(case when t.STAR_LEVEL='2' then id else null end) count2,");
		sql.append("count(case when t.STAR_LEVEL='3' then id else null end) count3,count(case when t.STAR_LEVEL='4' then id else null end) count4,");
		sql.append("count(case when t.STAR_LEVEL='5' then id else null end) count5,sum(case when t.STAR_LEVEL='1' then 1 else 0 end) sum1,");
		sql.append("sum(case when t.STAR_LEVEL='2' then 2 else 0 end) sum2,sum(case when t.STAR_LEVEL='3' then 3 else 0 end) sum3,");
		sql.append("sum(case when t.STAR_LEVEL='4' then 4 else 0 end) sum4,sum(case when t.STAR_LEVEL='5' then 5 else 0 end) sum5");
		sql.append(" from star_level_evaluation t  where t.service_id=? and t.evaluate_type='3')) p");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String itemId = (String) pSet.get("itemId");
			ds = DbHelper.query(sql.toString(),
					new Object[] { itemId, itemId }, conn);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 插入办事指南评价insertGuideEvaluation
	/**
	 * 插入办事指南评价
	 * 
	 * @param pSet
	 *            EVALUATE_TYPE 评价类型 1办件评价 2部门评价3办事指南评价 ITEM_ID 事项id
	 *            EVALUATE_CONTENT 评价内容 STAR_LEVEL 星级 1-5 ITEM_NAME 事项名称
	 *            DEPT_NAME 部门名称 REGION_ID 行政区划代码
	 * @return
	 */
	public DataSet insertGuideEvaluation(ParameterSet pSet) {
		DataSet ds;
		String CreatorId = (String) pSet.get("ucid");
		Command cmd = new Command(
				"app.icity.interactive.satisfaction.SatisfactionEvaluationDao");
		cmd.setParameter("EVALUATE_TYPE", (String) pSet.get("evaluate_type"));
		cmd.setParameter("ITEM_ID", (String) pSet.get("item_id"));
		cmd.setParameter("EVALUATE_CONTENT",
				(String) pSet.get("evaluate_content"));
		cmd.setParameter("STAR_LEVEL", (String) pSet.get("star_level"));
		cmd.setParameter("ITEM_NAME", (String) pSet.get("item_name"));
		cmd.setParameter("DEPT_NAME", (String) pSet.get("dept_name"));
		cmd.setParameter("REGION_ID", (String) pSet.get("region_id"));
		cmd.setParameter("CreatorId", CreatorId);
		ds = cmd.execute("insertGuideEvaluation");
		return ds;
	}
	//endregion
	
	//region 获取事项主题列表getPowerCatalog
	/**
	 * 获取事项主题列表
	 * 
	 * @param pSet
	 *            region_id class_type 个人 法人 其他
	 * @return
	 */
	public DataSet getPowerCatalog(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String region_code = (String) pSet.get("region_id");
			String class_type = (String) pSet.get("class_type");
			String sql = "select id,name,parent_id,icon_url,class_type from power_catalog t where t.is_use='1' ";
			if (!"".equals(region_code) && region_code != null) {
				sql += " and t.region_code=?";
				if (!"".equals(class_type) && class_type != null) {
					sql += " and t.class_type=?";
					ds = DbHelper.query(sql, new Object[] { region_code,
							class_type }, conn);
				} else {
					ds = DbHelper
							.query(sql, new Object[] { region_code }, conn);
				}
			} else {
				if (!"".equals(class_type) && class_type != null) {
					sql += " and t.class_type=?";
					ds = DbHelper.query(sql, new Object[] { class_type }, conn);
				} else {
					ds = DbHelper.query(sql, new Object[] {}, conn);
				}
			}
			JSONArray ja = new JSONArray();
			int size = ds.getJAData().size();
			for (int i = 0; i < size; i++) {
				String parent = ds.getJAData().getJSONObject(i)
						.getString("PARENT_ID");
				// System.out.println("parent_id:"+i+":"+parent+"；bool："+(StringUtils.isEmpty(parent)
				// || "null".equals(parent)||parent==null));
				if ("\"null\"".equals(parent) || StringUtils.isEmpty(parent)
						|| "null".equals(parent) || parent == null) {
					JSONObject jo = ds.getJAData().getJSONObject(i);
					ja.add(jo);
				}
			}
			// System.out.println("ja:"+ja);
			ja = getChild(ja, ds);
			ds.setRawData(ja);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 获取下级主题getChild
	/**
	 * 获取下级主题
	 * 
	 * @return
	 */
	private JSONArray getChild(JSONArray ja, DataSet ds) {
		int size = ja.size();
		int len = ds.getJAData().size();
		for (int i = 0; i < size; i++) {
			JSONObject jo = ja.getJSONObject(i);
			String id = jo.getString("ID");
			JSONArray m_ja = new JSONArray();
			for (int j = 0; j < len; j++) {
				if (id.equals(ds.getJAData().getJSONObject(j)
						.getString("PARENT_ID"))) {
					m_ja.add(ds.getJAData().getJSONObject(j));
				}
			}
			m_ja = getChild(m_ja, ds);
			jo.put("child", m_ja);
		}
		return ja;
	}
	//endregion
	
	//region 获取事项主题列表 分为个人和法人getPowerCatalogByClassType
	/**
	 * 获取事项主题列表 分为个人和法人
	 * @param pSet
	 * @return
	 */
	public DataSet getPowerCatalogByClassType(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String region_code = (String) pSet.get("region_id");
			String class_type = (String) pSet.get("class_type");
			String sql = "select id,name,parent_id,icon_url,class_type from power_catalog t where t.is_use='1' ";
			if (!"".equals(region_code) && region_code != null) {
				sql += " and t.region_code=?";
				if (!"".equals(class_type) && class_type != null) {
					sql += " and t.class_type=?";
					ds = DbHelper.query(sql, new Object[] { region_code,
							class_type }, conn);
				} else {
					ds = DbHelper
							.query(sql, new Object[] { region_code }, conn);
				}
			} else {
				if (!"".equals(class_type) && class_type != null) {
					sql += " and t.class_type=?";
					ds = DbHelper.query(sql, new Object[] { class_type }, conn);
				} else {
					ds = DbHelper.query(sql, new Object[] {}, conn);
				}
			}
			JSONArray ja = new JSONArray();
			JSONArray ja_gr = new JSONArray();
			JSONArray ja_fr = new JSONArray();
			int size = ds.getJAData().size();
			for (int i = 0; i < size; i++) {
				JSONObject jo = ds.getJAData().getJSONObject(i);
				String classType = jo.getString("CLASS_TYPE");			
				if ("个人".equals(classType)) {					
					ja_gr.add(jo);
				}else if("法人".equals(classType)){
					ja_fr.add(jo);
				}
			}
			JSONObject jo_gr = new JSONObject();
			jo_gr.put("NAME", "个人办事");
			jo_gr.put("child", ja_gr);
			ja.add(jo_gr);
			JSONObject jo_fr = new JSONObject();
			jo_fr.put("NAME", "企业办事");
			jo_fr.put("child", ja_fr);
			ja.add(jo_fr);
			ds.setRawData(ja);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 智能问答jrobotSearch
	/**
	 * 智能问答
	 * 
	 * @param pSet
	 *            limit page key
	 * @return
	 */
	public DataSet jrobotSearch(ParameterSet pSet) {
		DataSet ds;
		int page = Integer.parseInt(pSet.getParameter("page").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		int start = (page - 1) * limit + 1;
		Command cmd = new Command("app.icity.search.SearchCmd");
		cmd.setParameter("limit", String.valueOf(limit));
		cmd.setParameter("start", String.valueOf(start));
		cmd.setParameter("key", (String) pSet.get("key"));
		ds = cmd.execute("getIndex");
		StringBuffer sxidsBuffer = new StringBuffer();
		JSONArray ja_ds = ds.getJAData();
		int size = ja_ds.size();
		int m_size = 0;
		for(int s=0;s<size;s++){
			JSONObject jo = ja_ds.getJSONObject(s);
			if("project".equals(jo.getString("TYPE"))){
				sxidsBuffer.append("'"	+ jo.getString("ITEM_ID") + "',");
				ja_ds.getJSONObject(s).put("IS_COLLECTION", "0");
				m_size++;
			}			
		}
		String sxids = sxidsBuffer.toString();
		sxids = sxids.substring(0, sxids.length() - 1);
		String ucid = (String) pSet.get("ucid");
		if (!"".equals(ucid)) {
			Connection conn = DbHelper.getConnection("icityDataSource");
			try {
				String sql = "SELECT PF.sxid FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1' and PF.usid=? and PF.sxid in ("
						+ sxids + ")";
				DataSet ds_collection = DbHelper.query(sql,
						new Object[] { ucid }, conn);
				if (ds_collection.getTotal() > 0) {
					int len = ds_collection.getTotal();
					for (int i = 0; i < len; i++) {
						for (int j = 0; j < m_size; j++) {
							if (ja_ds.getJSONObject(j).getString("ITEM_ID").equals(ds_collection.getJAData().getJSONObject(i).getString("SXID"))) {
								ja_ds.getJSONObject(j).put("IS_COLLECTION", "1");
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage(e.toString());
				ds.setRawData(new JSONArray());
			} finally {
				DBSource.closeConnection(conn);
			}
		}
		ds.setRawData(ja_ds);		
		return ds;
	}
	//endregion
	
	//region 大厅预约makeAppointment
	/**
	 * 大厅预约
	 * 
	 * @param pSet
	 *            {"map":"{'Service':'','':''}"} 预约部门列表 JSONObject map = new
	 *            JSONObject(); map.put("Service", "Queue.GetBizDepts");
	 *            map.put("Reserve","true");
	 * 
	 **            预约部门业务列表 JSONObject map = new JSONObject(); map.put("Service",
	 *            "Reserve.ListBusiness"); map.put("DeptName","部门名称");
	 * 
	 **            获取业务已预约数 JSONObject map = new JSONObject(); map.put("Service",
	 *            "Reserve.GetRecordCount"); map.put("YYDate","预约日期");
	 *            map.put("BizID","业务ID");
	 * 
	 **            填写完信息后进行预约 JSONObject map = new JSONObject();
	 *            map.put("Service", "Reserve.AddRecord");
	 *            map.put("BizID","业务ID"); map.put("Date","日期");
	 *            map.put("Time","时间"); map.put("IDCard","身份证号码");
	 *            map.put("Phone","手机号");
	 * 
	 * @return
	 * 
	 *         手机app和爱城市、微信调用
	 */
	public DataSet makeAppointment(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = SecurityConfig.getString("synchronousDept4dt");
		JSONObject map = JSONObject.fromObject(pSet.get("map"));
		String service = map.getString("Service");
		StringBuffer YYDatestr = new StringBuffer();
		/*String YYDate = "";
		if ("Reserve.GetRecordCount".equals(service)) {
			String[] YYDates = map.getString("YYDate").split(",");
			for (String rq : YYDates) {
				YYDatestr.append("'" + rq + "',");
			}
			YYDate = YYDatestr.toString();
			if (StringUtils.isNotEmpty(YYDate)) {
				YYDate = YYDate.substring(0, YYDate.length() - 1);
			}
			map.put("YYDate", YYDate);
		}*/
		String param = JSONObject.fromObject(map).toString();
		String result = new HttpClientUtil().getResult(url, param, true);
		JSONObject jResult = JSONObject.fromObject(result);
		if ("1".equals(jResult.getString("Succ"))) {
			if ("Reserve.AddRecord".equals(service)) {
				String Phone = map.getString("Phone");
				String Date = map.getString("Date");
				String Time = map.getString("Time");
				String sRand = "您已预约成功，请于" + Date + " " + Time
						+ "，到行政服务中心取预约号办理!";
				Boolean type = sendMessage(Phone, sRand, "others");
				if (type) {
					ds.setState(StateType.SUCCESS);
					ds.setMessage("预约信息已发送！");
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("预约信息发送失败！");
				}
			}
		}
		ds.setState(StateType.SUCCESS);
		ds.setRawData(result);
		return ds;
	}
	//endregion
	
	//region附件上传uploadify
	/**
	 * 附件上传
	 * 
	 * @param pSet
	 *            [{"fileName":"文件名","fileType":"扩展名","fileContent":"文件流",
	 *            "fullName":"全名"}]
	 * @return 手机app和爱城市调用
	 */
	public DataSet uploadify(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONArray attachment = JSONArray.fromObject(pSet.get("attachment"));
		List<String> m_path = new ArrayList<String>();
		List<String> m_url = new ArrayList<String>();
		int size = attachment.size();
		for (int i = 0; i < size; i++) {
			String fileName = (String) attachment.getJSONObject(i)
					.getString("fileName").trim()
					+ "."
					+ (String) attachment.getJSONObject(i)
							.getString("fileType").trim();
			String path = PathUtil.getWebPath() + "file" + File.separator
					+ "upload" + File.separator + fileName;
			FileOutputStream fos = null;
			try {
				byte[] Getresult = new BASE64Decoder()
						.decodeBuffer((String) attachment.getJSONObject(i)
								.getString("fileContent").trim());
				fos = new FileOutputStream(path);
				fos.write(Getresult);
				m_path.add(path);
				m_url.add(fileName);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		JSONArray att_data = new JSONArray();
		String __uid__ = (String) pSet.get("ucid");
		String uploadUrl = SecurityConfig.getString("NetDiskAddress");
		int len = m_path.size();
		for (int i = 0; i < len; i++) {
			try {
				Map<String, String> params = new HashMap<String, String>();
				if (!"".equals(__uid__) && null != __uid__) {
					params.put("uid", __uid__);
				} else {
					params.put("uid", SecurityConfig.getString("NetDiskUid"));
				}
				params.put("type", "doc");
				params.put("folder_name", "//");
				String scc = UploadUtil.startUploadService(params,
						m_path.get(i), uploadUrl);
				JSONObject jo = new JSONObject();
				JSONObject o = JSONObject.fromObject(scc);
				String docid = o.getString("docid");
				jo.put("path", docid);
				jo.put("scc", o);
				jo.put("url", m_url.get(i));
				jo.put("error", 0);
				att_data.add(jo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ds.setRawData(att_data);
		ds.setState(StateType.SUCCESS);
		return ds;
	}
	//endregion
	
	//region 获取网点列表getPubHallListByPage
	/**
	 * 获取网点列表
	 * 
	 * @param pSet
	 *            REGION;--region WORK_INTERVAL--work_interval NAME--name
	 *            CATAGORY--catagory ID--id
	 * @return
	 */
	public DataSet getPubHallListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String orderby = pSet.containsKey("orderby")?(String) pSet.get("orderby"):"";
			String region = (String) pSet.get("region_code");
			String work_interval = (String) pSet.get("work_interval");
			String name = (String) pSet.get("name");
			String catagory = (String) pSet.get("catagory");
			String id = (String) pSet.get("id");
			String where = "";
			int page = Integer.parseInt(pSet.getParameter("page").toString());
			int limit = Integer.parseInt(pSet.getParameter("limit").toString());
			int start = (page - 1) * limit;
			if (region == null || "".equals(region)) {
				where += " and region like ?";
				region = "%%";
			} else {
				where += " and region = ?";
			}
			if (work_interval == null || "".equals(work_interval)) {
				where += " and work_interval like ?";
				work_interval = "%%";
			} else {
				where += " and work_interval = ?";
			}
			if (name == null || "".equals(name)) {
				where += " and name like ?";
				name = "%%";
			} else {
				where += " and name like ?";
			}
			if (catagory == null || "".equals(catagory)) {
				where += " and catagory like ?";
				catagory = "%%";
			} else {
				where += " and catagory = ?";
			}
			if (id == null || "".equals(id)) {
				where += " and id like ?";
				id = "%%";
			} else {
				where += " and id = ?";
			}
			where += " "+orderby;
			String sql = "select * from pub_hall t where t.state='01' " + where;
			ds = DbHelper.query(sql, start, limit, new Object[] { region,
					work_interval, name, catagory, id }, conn,
					"icityDataSource");

			String longitude = (String) pSet.get("longitude");
			String latitude = (String) pSet.get("latitude");
			String distance = (String) pSet.get("distance");
			int total = 0;
			if (longitude != null && !"".equals(longitude)) {
				if (latitude != null && !"".equals(latitude)) {
					if (distance != null && !"".equals(distance)) {
						int len = ds.getTotal();
						JSONArray ja = ds.getJAData();
						JSONArray m_ja = new JSONArray();
						JSONObject m_jo = new JSONObject();
						for (int i = 0; i < len; i++) {
							Double m_len = Distance(
									Double.parseDouble(longitude),
									Double.parseDouble(latitude),
									Double.parseDouble(ja.getJSONObject(i)
											.getString("LONGITUDE")),
									Double.parseDouble(ja.getJSONObject(i)
											.getString("LATITUDE")));
							if (m_len < Double.parseDouble(distance)) {
								m_jo.put("ADDRESS", ja.getJSONObject(i)
										.getString("ADDRESS"));
								m_jo.put("ID",
										ja.getJSONObject(i).getString("ID"));
								m_jo.put("LATITUDE", ja.getJSONObject(i)
										.getString("LATITUDE"));
								m_jo.put("LONGITUDE", ja.getJSONObject(i)
										.getString("LONGITUDE"));
								m_jo.put("NAME",
										ja.getJSONObject(i).getString("NAME"));
								m_jo.put("TELPHONE", ja.getJSONObject(i)
										.getString("TELPHONE"));
								m_jo.put("WORK_INTERVAL", ja.getJSONObject(i)
										.getString("WORK_INTERVAL"));
								m_jo.put("WORK_TIME", ja.getJSONObject(i)
										.getString("WORK_TIME"));
								m_ja.add(m_jo);
								total++;
							}
						}
						ds.setState(StateType.SUCCESS);
						ds.setRawData(m_ja);
						ds.setTotal(total);
					}
				}
			} else {
				int len = ds.getTotal();
				JSONArray ja = ds.getJAData();
				JSONArray m_ja = new JSONArray();
				JSONObject m_jo = new JSONObject();
				for (int i = 0; i < len; i++) {
					m_jo.put("ADDRESS", ja.getJSONObject(i)
							.getString("ADDRESS"));
					m_jo.put("ID", ja.getJSONObject(i).getString("ID"));
					m_jo.put("LATITUDE",
							ja.getJSONObject(i).getString("LATITUDE"));
					m_jo.put("LONGITUDE",
							ja.getJSONObject(i).getString("LONGITUDE"));
					m_jo.put("NAME", ja.getJSONObject(i).getString("NAME"));
					m_jo.put("TELPHONE",
							ja.getJSONObject(i).getString("TELPHONE"));
					m_jo.put("WORK_INTERVAL",
							ja.getJSONObject(i).getString("WORK_INTERVAL"));
					m_jo.put("WORK_TIME",
							ja.getJSONObject(i).getString("WORK_TIME"));
					m_ja.add(m_jo);
					total++;
				}
				ds.setState(StateType.SUCCESS);
				ds.setRawData(m_ja);
				ds.setTotal(total);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 获取收藏网点列表getPubHallFavoriteByPage
	/**
	 * 获取收藏网点列表
	 * @param pSet
	 * @return
	 */
	public DataSet getPubHallFavoriteByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			int page = Integer.parseInt(pSet.getParameter("page").toString());
			int limit = Integer.parseInt(pSet.getParameter("limit").toString());
			int start = (page - 1) * limit;
			String sql = "select * from pub_hall_favorite t where t.status='1' and t.ucid=?";
			ds = DbHelper.query(sql, start, limit, new Object[] { ucid }, conn,
					"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 收藏网点addPubHallFavorite
	/**
	 * 收藏网点
	 * 
	 * @param pSet
	 *            name 网点名册 remark 备注 hallid 网点ID
	 * @return
	 */
	public DataSet addPubHallFavorite(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String hallid = (String) pSet.get("hallid");
			String name = (String) pSet.get("name");
			String remark = (String) pSet.get("remark");
			String sql = "select 1 from pub_hall_favorite t where t.ucid=? and t.hallid = ?";
			DataSet m_ds = DbHelper.query(sql, new Object[] { ucid, hallid },
					conn);
			int i = 0;
			if (m_ds.getTotal() > 0) {
				sql = "update pub_hall_favorite t set t.status='1' where t.ucid=? and t.hallid=?";
				i = DbHelper.update(sql, new Object[] { ucid, hallid }, conn);
			} else {
				sql = "insert into pub_hall_favorite t (id,name,ucid,hallid,remark,ctime,status) values "
						+ "(?,?,?,?,?,sysdate,?)";
				i = DbHelper.update(sql, new Object[] { Tools.getUUID32(),
						name, ucid, hallid, remark, "1" }, conn);
			}
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("收藏失败！");
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("收藏成功！");
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
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
	
	//region 取消收藏网点delPubHallFavorite
	/**
	 * 取消收藏网点
	 * 
	 * @param pSet
	 *            hallid 网点ID
	 * @return
	 */
	public DataSet delPubHallFavorite(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String ucid = (String) pSet.get("ucid");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String hallid = (String) pSet.get("hallid");
			String sql = "select 1 from pub_hall_favorite t where t.ucid=? and t.hallid = ?";
			DataSet m_ds = DbHelper.query(sql, new Object[] { ucid, hallid },
					conn);
			int i = 0;
			if (m_ds.getTotal() > 0) {
				sql = "update pub_hall_favorite t set t.status='0' where t.ucid=? and t.hallid=?";
				i = DbHelper.update(sql, new Object[] { ucid, hallid }, conn);
			}
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("取消失败！");
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("取消成功！");
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
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
	
	//region 发送短信sendMessage
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
	
	//region 获取办件信息getBusinessState
	/**
	 * 获取办件信息
	 * @param pSet
	 * @return 手机app和爱城市调用
	 */
	public DataSet getBusinessState(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sblsh = (String) pSet.get("sblsh");
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sql = "select * from business_index t where t.sblsh=?";
			ds = DbHelper.query(sql, new Object[] { sblsh }, conn);
			if ("".equals(ds.getJOData().getString("SLSJ"))) {
				System.out.print("空");
			}
			System.out.print(ds.getJOData().getString("SBSJ"));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 计算地球上任意两点(经纬度)距离Distance
	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static double Distance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return d;
	}
	//endregion 
	
	//region 昆明盘龙主题分类查询getPowerCatalogKM
	/**
	 * 昆明盘龙主题分类查询
	 * 
	 * @param pSet
	 *            region_id 
	 *            class_type 个人 法人 其他
	 *            them_type 1 按主题  2:按事件 3:按待定对象
	 * @return
	 */
	public DataSet getPowerCatalogKM(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String region_code = (String) pSet.get("region_id");
			String class_type = (String) pSet.get("class_type");
			String them_type = (String) pSet.get("them_type");
			List<String> params  = new ArrayList<String>();
			String sql = "select id,name,parent_id,icon_url,class_type from power_catalog t where t.is_use='1' ";
			String inSql="";
			if (!"".equals(region_code) && region_code != null) {
				sql += " and t.region_code=?";
				params.add(region_code);
			} 
			if (!"".equals(class_type) && class_type != null) {
				sql += " and t.class_type=?";
				params.add(class_type);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("类别：class_type不能为空");
				ds.setRawData(new JSONArray());
				return ds;
			} 
			if (!"".equals(them_type) && them_type != null) {
				//1 按主题  2:按事件 3:按待定对象
				if(them_type.equals("1")){
					inSql ="select id from power_catalog where (parent_id ='null' or parent_id is null) and class_type =? and sort_order='00001'";
					params.add(class_type);
				}else if(them_type.equals("2")){
					inSql ="select id from power_catalog where (parent_id ='null' or parent_id is null) and class_type =? and sort_order='00002'";
					params.add(class_type);
				}else if(them_type.equals("3")){
					params.add(class_type);
					inSql ="select id from power_catalog where (parent_id ='null' or parent_id is null) and class_type =? and sort_order='00003'";
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage("主题分类：them_type不正确");
					ds.setRawData(new JSONArray());
					return ds;
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("主题分类：them_type不能为空");
				ds.setRawData(new JSONArray());
				return ds;
			} 
			sql = sql +" and t.parent_id in ("+inSql+")";
			ds = DbHelper.query(sql, params.toArray(), conn);
			ds.setMessage("success");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		}
		return ds;
	}
	//endregion
	
	//region 获取所有中介分类getMedclassify
	/**
	 * 获取所有中介分类
	 * @param pSet	 * 
	 * @return
	 */
	public DataSet getMedclassify(ParameterSet pSet) {		
		DataSet ds = new DataSet();
		String regionCode = (String) pSet.getParameter("regionCode");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("Zjfw_href") + "/main/imng/getMedclassify?regionCode=" + regionCode);
		try {
			JSONObject resultJson = JSONObject.fromObject(RestUtil.getData(url));
			ds.setRawData(resultJson.toString());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
			System.out.println();
		}
		return ds;
	}
	//endregion
	
	//region 获取中介列表getIntermedairyList
	/**
	 * 获取中介列表
	 * @param pSet	 * 
	 * @return
	 */
	public DataSet getIntermedairyList(ParameterSet pSet) {		
		DataSet ds = new DataSet();
		String regionCode = (String) pSet.getParameter("regionCode");
		String itemCode = (String) pSet.getParameter("itemCode");
		String classifyId = (String) pSet.getParameter("classifyId");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("Zjfw_href") + "/main/imng/getIntermedairyList?regionCode=" + regionCode+"&itemCode="+itemCode+"&classifyId="+classifyId);
		try {
			JSONObject resultJson = JSONObject.fromObject(RestUtil.getData(url));
			ds.setRawData(resultJson.toString());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
			System.out.println();
		}
		return ds;
	}
	//endregion
	
	//region 中介详情接口getIntermedairyInfo
	/**
	 * 中介详情接口
	 * @param pSet	 * 
	 * @return
	 */
	public DataSet getIntermedairyInfo(ParameterSet pSet) {		
		DataSet ds = new DataSet();
		String id = (String) pSet.getParameter("id");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("Zjfw_href") + "/main/imng/getIntermedairyInfo?id=" + id);
		try {
			JSONObject resultJson = JSONObject.fromObject(RestUtil.getData(url));
			ds.setRawData(resultJson.toString());
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
			System.out.println();
		}
		return ds;
	}
	//endregion
	
	//region发送邮件sendEmail
	/**
	 * 发送邮件
	 */
	public DataSet sendEmail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String receiveAddress = (String) pSet.getParameter("mail");			
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost(SecurityConfig
					.getString("mail_server_host"));
			mailInfo.setMailServerPort(SecurityConfig
					.getString("mail_server_port"));
			mailInfo.setValidate(true);
			mailInfo.setUserName(SecurityConfig.getString("mail_username"));// 用户名
			mailInfo.setPassword(SecurityConfig.getString("mail_password"));// 用户密码
			mailInfo.setFromAddress(SecurityConfig
					.getString("mail_from_address"));
			String title = (String) pSet.getParameter("title");
			String content = (String) pSet.getParameter("content");
			mailInfo.setToAddress(receiveAddress);
			mailInfo.setSubject(title);
			mailInfo.setContent(content);
			boolean success = SimpleMailSender.sendHtmlMail(mailInfo);
			if (success) {
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("邮件发送失败");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	//endregion
}
