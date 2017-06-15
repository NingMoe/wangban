package app.uc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.icore.util.sms.MailSenderInfo;
import com.icore.util.sms.SimpleMailSender;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.IdGenereator;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import action.icity.intermediary.intermed;
import app.icity.sync.UniteUserInterface;
import app.uc.bean.UserBean;
import app.util.RestUtil;
import core.util.CommonUtils;
import net.sf.json.JSONObject;

public class UserDao extends BaseJdbcDao {
	public UserDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static UserDao getInstance() {
		return DaoFactory.getDao(UserDao.class.getName());
	}

	/**
	 * 生成四位随机数验证码
	 */
	private String generateVerifyCode() {
		char[] CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L',
				'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
				'Z', '2', '3', '4', '5', '6', '7', '8', '9' };// 用来生成手机短信校验码
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			String rand = String
					.valueOf(CHARS[Tools.random(CHARS.length - 1)]);// 从字符数组中随机产生一个字符
			sRand.append(rand);
		}
		return sRand.toString();
	}

	/**
	 * 默认为用户注册时手机短信
	 */
	public DataSet sendMessage(ParameterSet pSet) {
		String rand = "1234";
		DataSet ds = new DataSet();
		try {
			String phone = (String) pSet.getParameter("PHONE");
			Date cookieDate = (Date) CacheManager
					.get("register_sendsms", phone);
			CacheManager.evict("register_sendsms", phone);
			CacheManager.set("register_sendsms", phone, new Date());
			if (null != cookieDate) {
				Date now = new Date();
				long between = (now.getTime() - cookieDate.getTime()) / 1000;
				long minute = between / 60;
				if (minute < 1) {
					ds.setState(StateType.FAILT);
					ds.setMessage("60秒内不能重复发送短信");
					return ds;
				}
			}
			rand = this.generateVerifyCode();
			ds.setRoute(rand);
			CacheManager.set("MessageCode", phone, rand);
			String smsContent = "", message = "";
			if (pSet.containsKey("SMS")) {
				// 如果配置了SMS，则从security.properties加载对应短信模板，否则从参数中获取短信内容
				String sms = (String) pSet.getParameter("SMS");
				message = SecurityConfig.getString(sms);
			} else {
				message = (String) pSet.getParameter("MESSAGE");
			}
			smsContent = (message == null ? "" : message) + rand;
			String CHANNEL = "register";
			if(StringUtils.isNotEmpty((String)pSet.getParameter("CHANNEL"))){
				CHANNEL = (String)pSet.getParameter("CHANNEL");
			}
			String sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone) values(?,?,?,?,?,?)";
			this.executeUpdate(
					sql,
					new Object[] {
							Tools.getUUID32(),
							smsContent,
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(),
											CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CHANNEL, "0", phone });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return ds;
	}

	// 发送邮件
	public DataSet sendEmail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String receiveAddress = (String) pSet.getParameter("mail");
			Date cookieDate = (Date) CacheManager.get("register_sendemail",
					receiveAddress);
			CacheManager.evict("register_sendemail", receiveAddress);
			CacheManager.set("register_sendemail", receiveAddress, new Date());
			if (null != cookieDate) {
				Date now = new Date();
				long between = (now.getTime() - cookieDate.getTime()) / 1000;
				long minute = between / 60;
				if (minute < 1) {
					ds.setState(StateType.FAILT);
					ds.setMessage("60秒内不能重复发送邮件");
					return ds;
				}
			}
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

	/**
	 * 获取用户列表
	 */
	public DataSet getList(ParameterSet pset) {
		//0表示pset无参数
		try {
			int para = 0;
			String sql ="";
			if(pset.containsKey("phone")&&StringUtil.isNotEmpty(pset.getParameter("phone").toString()))
			{
				sql = "select PHONE,IS_PHONEBIND from uc_user";
				 para++;
			}
			if(pset.containsKey("PHONE")&&StringUtil.isNotEmpty(pset.getParameter("PHONE").toString()))
			{
				sql = "select PHONE,IS_PHONEBIND from uc_user";
				para++;
			}
			if(pset.containsKey("id@=")&&StringUtil.isNotEmpty(pset.getParameter("id@=").toString()))
			{
				sql = "select ID,QUESTION from uc_user";
				para++;
			}
			if(pset.containsKey("ID@!=")&&StringUtil.isNotEmpty(pset.getParameter("ID@!=").toString()))
			{
				sql = "select ID,QUESTION from uc_user";
				para++;
			}
			if(pset.containsKey("CARD_NO")&&StringUtil.isNotEmpty(pset.getParameter("CARD_NO").toString()))
			{
				sql = "select ID,NAME,ACCOUNT,CARD_NO,TYPE from uc_user";
				para++;
			}
			
			if(pset.containsKey("EMAIL")&&StringUtil.isNotEmpty(pset.getParameter("EMAIL").toString()))
			{
				sql = "select EMAIL,IS_EMAILBIND from uc_user";
				para++;
			}
			if(pset.containsKey("ORG_NO")&&StringUtil.isNotEmpty(pset.getParameter("ORG_NO").toString()))
			{
				sql = "select ORG_NO from uc_user";
				para++;
			}
			if(pset.containsKey("ACCOUNT")&&StringUtil.isNotEmpty(pset.getParameter("ACCOUNT").toString()))
			{
				sql = "select ID,ACCOUNT from uc_user";
				para++;
			}
			if(pset.containsKey("id")&&StringUtil.isNotEmpty(pset.getParameter("id").toString()))
			{
				sql = "select ACCOUNT,Id,EMAIL,ADDRESS,TYPE,NAME,CARD_TYPE,CARD_NO,PHONE,ADDRESS"+
					",EMAIL,STATUS,ORG_NAME,ORG_NO,ORG_BOSS_TYPE,ORG_BOSS_NO,ORG_BOSS_NAME,CREDIT_CODE from uc_user";
				para++;
			}
			if(para!=0){
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				int start = pset.getPageStart();
				int limit = pset.getPageLimit();
				if (start == -1 || limit == -1) {
					return this.executeDataset(sql, null);
				} else {
					return this.executeDataset(sql, start, limit, null);
				}
			}else{
				return new DataSet();
			}
		}catch(RuntimeException e){
			e.printStackTrace();
			return new DataSet();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new DataSet();
		}
	}
	/**
	 * 登录验证成功，将用户信息写入换成，后台使用
	 */
	public DataSet getUser(ParameterSet pset) {
			String sql = "select * from uc_user";
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			return this.executeDataset(sql, null);
	}
	/**
	 * 获取用户拓展信息列表
	 */
	public DataSet getListExt(String id) {
		String sql = "select t.*,r.* from uc_user t left join uc_user_ext r on t.id = r.id where t.id = ?";
		return this.executeDataset(sql, new Object[] { id });
	}
	/**
	 * 获取隐藏手机号
	 */
	public DataSet getHidePhone(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "select phone from uc_user where id=?";
			ds = this.executeDataset(sql,
					new Object[] { (String) pSet.getParameter("id") });
			if (ds != null && ds.getTotal() > 0) {
				String phone = ds.getRecord(0).getString("PHONE");
				phone = hidePhone(phone);
				ds.setData(Tools.stringToBytes(phone));
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 通过id 获取邮箱
	 */
	public DataSet getEmail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "select email from uc_user where id=?";
			ds = this.executeDataset(sql,
					new Object[] { (String) pSet.getParameter("id") });
			if (ds != null && ds.getTotal() > 0) {
				String email = ds.getRecord(0).getString("EMAIL");
				ds.setData(Tools.stringToBytes(email));
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 获取邮箱后缀
	 */
	public DataSet getEmailLast(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "select email from uc_user where id=?";
			ds = this.executeDataset(sql,
					new Object[] { (String) pSet.getParameter("id") });
			if (ds != null && ds.getTotal() > 0) {
				String email = ds.getRecord(0).getString("EMAIL");
				String com = "";
				if (StringUtils.isNotEmpty(email)) {
					int index = email.lastIndexOf("@");
					if (index != -1) {
						com = email.substring(index);
					}
				}
				ds.setData(Tools.stringToBytes(com));
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 重新发送邮件时的邮箱账号
	 */
	public DataSet getValidEmail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String uid = (String) pSet.getParameter("uid");
			Object obj = CacheManager.get("mailIndex", uid);
			if (obj != null) {
				JSONObject jo = (JSONObject) obj;
				String mailKey = jo.getString("key");
				Object sobj = CacheManager.get("ValidMail", mailKey);
				if (sobj != null) {
					ParameterSet sSet = (ParameterSet) sobj;// 发送邮件参数
					String email = (String) sSet.getParameter("mail");
					ds.setData(Tools.stringToBytes(email));
				}
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 验证用户登录信息，可以为用户名，身份证号码，手机号
	 */
	public DataSet verify(String account) {
		DataSet ds = new DataSet();
		try {
			String sql = "select * from uc_user where (account=? or (card_no=? and card_type='10' and STATUS = '2') or phone=?) and status is not null and password is not null";
			ds = this.executeDataset(sql, new Object[] { account, account,
					account });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 用户注册
	 */
	public DataSet register(ParameterSet pSet) {
		DataSet ds = new DataSet();
		intermed inter = new intermed();
		//String webRank = SecurityConfig.getString("WebRank");
		String webRegion = SecurityConfig.getString("WebRegion");
		String appId = SecurityConfig.getString("AppId");
		String password_DES = SecurityConfig.getString("DES_password");
		try {

			UserBean user = new UserBean(
					IdGenereator.getInstance("usercenter").getId(),
					(String) pSet.getParameter("ACCOUNT"),
					(String) pSet.getParameter("NAME"),
					(String) pSet.getParameter("EMAIL"),
					(String) pSet.getParameter("PHONE"),
					(String) pSet.getParameter("PASSWORD"), // 密码MD5加密
					(String) pSet.getParameter("TYPE"),
					(String) pSet.getParameter("STATUS"),
					(String) pSet.getParameter("CARD_TYPE"),
					(String) pSet.getParameter("CARD_NO"),
					(String) pSet.getParameter("ORG_NAME"),
					(String) pSet.getParameter("ORG_NO"),
					(String) pSet.getParameter("ORG_BOSS_TYPE"),
					(String) pSet.getParameter("ORG_BOSS_NO"),
					(String) pSet.getParameter("ORG_BOSS_NAME"),
					(String) pSet.getParameter("CA_ID"),
					(String) pSet.getParameter("UserCert"),
					(String) pSet.getParameter("PARENT_ID"), CommonUtils
							.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.getInstance().parseDateToTimeStamp(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					Tools.getUUID32(), (String) pSet.getParameter("ADDRESS"),
					(String) pSet.getParameter("USERIDCODE"), webRegion, "1");

			// 检查是否有已经注册的信息
			StringBuilder checkBuilder = new StringBuilder();
			checkBuilder.append("SELECT * FROM UC_USER");
			if (StringUtils.isNotEmpty(user.getAccount())) {
				Object[] params = new Object[10];
				checkBuilder.append(" WHERE ACCOUNT = ?");
				params[0] = user.getAccount();
				int paramsIndex = 1;
				if (StringUtils.isNotEmpty(user.getEmail())) {
					checkBuilder.append(" OR EMAIL = ?");
					params[paramsIndex++] = user.getEmail();
				}
				if (StringUtils.isNotEmpty(user.getPhone())) {
					checkBuilder.append(" OR PHONE = ?");
					params[paramsIndex++] = user.getPhone();
				}
				if (StringUtils.isNotEmpty(user.getOrg_no())) {
					checkBuilder.append(" OR ORG_NO = ?");
					params[paramsIndex++] = user.getOrg_no();
				}
				Object[] rp = new Object[paramsIndex];
				for (int i = 0; i < params.length; i++) {
					if (StringUtils.isNotEmpty((String) params[i])) {
						rp[i] = params[i];
					} else {
						break;
					}
				}
				String checkSql = checkBuilder.toString();
				ds = this.executeDataset(checkSql, rp);
				if (ds.getTotal() > 0) {
					ds.setState(StateType.FAILT);
					JSONObject jsonobj = (JSONObject) ds.getJAData().get(0);
					String prompt = "";
					//if ("km".equals(appId)) {
						if (user.getAccount().equals(jsonobj.get("ACCOUNT"))) {
							prompt = "用户名";
						} else if (user.getEmail().equals(
								jsonobj.get("EMAIL"))) {
							prompt = "邮箱";
						} else if (user.getPhone().equals(
								jsonobj.get("PHONE"))) {
							prompt = "电话号码";
						} else if (user.getCard_no().equals(
								jsonobj.get("CARD_NO"))) {
							prompt = "身份证号";
						} else if (user.getOrg_no().equals(
								jsonobj.get("ORG_NO"))) {
							prompt = "组织机构代码";
						}
					//}
					ds.setMessage(prompt + "信息已注册");
					return ds;
				} else {
					String sql = "INSERT INTO UC_USER(ID,ACCOUNT,NAME,EMAIL,PHONE,PASSWORD,TYPE,STATUS,CARD_TYPE,CARD_NO,ORG_NAME,ORG_NO,ORG_BOSS_TYPE,ORG_BOSS_NO,ORG_BOSS_NAME,CA_ID,PUBKEY,PARENT_ID,CREATION_TIME,LAST_MODIFICATION_TIME,VERSION_CODE,ADDRESS,useridcode,region_id,IS_INUSE,IS_PHONEBIND,IS_EMAILBIND) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					int i = this
							.executeUpdate(
									sql,
									new Object[] {
											user.getId(),
											user.getAccount(),
											user.getName(),
											user.getEmail(),
											user.getPhone(),
											user.getPassword(),
											user.getType(),
											StringUtil.isNotEmpty(user.getName())?"2":"1", // 1表示普通注册
											user.getCard_type(),
											user.getCard_no(),
											user.getOrg_name(),
											user.getOrg_no(),
											user.getOrg_boss_type(),
											user.getOrg_boss_no(),
											user.getOrg_boss_name(),
											user.getCa_id(),
											user.getPubkey(),
											StringUtils.isNotEmpty(user
													.getParent_id()) ? user
													.getParent_id() : "0",
											user.getCreation_time(),
											user.getLast_modification_time(),
											user.getVersion_code(),
											user.getAddress(),
											user.getUserIdCode(),
											user.getRegionId(),
											user.getIsInuse(),
											StringUtil.isNotEmpty(user.getPhone())?"1":"0",
											StringUtil.isNotEmpty(user.getEmail())?"1":"0"
									});
					if (i > 0) {
						if ("chq".equals(appId)) {
							String url = HttpUtil.formatUrl(SecurityConfig
									.getString("investment_register"));
							Map<String, String> data=new HashMap<String, String>();
							data.put("NAME",
									(String) pSet.getParameter("ACCOUNT_DES"));
							data.put("PASSWORD",
									(String) pSet.getParameter("PWD_DES"));
							data.put("PHONE",
									(String) pSet.getParameter("PHONE_DES"));
							data.put("SYBMOL", inter.strEnc(
									"inspur", password_DES, "", ""));
							try {
								JSONObject obj;
								obj = JSONObject.fromObject(RestUtil.postData(url, data));
								ds.setRawData(obj);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						JSONObject json =  new JSONObject();
						json.put("ACCOUNT", user.getAccount());
						json.put("PHONE", user.getPhone());
						json.put("CARD_NO", user.getCard_no());
						json.put("ID", user.getId());
						json.put("NAME", user.getName());
						json.put("TYPE", user.getType());
						ds.setRawData(json);
					} else {
						ds.setState(StateType.FAILT);
						ds.setMessage("插入注册信息失败");
					}
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("请填写完整的信息");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 修改密码
	 */
	public DataSet changePwd(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String newpass = (String) pSet.getParameter("newPassWord");
			pSet.remove("newPassWord");
			String id = (String) pSet.getParameter("id");
			String pass = (String) pSet.getParameter("password");
			DataSet hds = this.checkUcUser(id, pass);
			if (hds != null && hds.getTotal() > 0) {
				String sql = "update uc_user set password=? where id=?";
				this.executeUpdate(sql, new Object[] { newpass, id },
						"icityDataSource");
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("当前密码输入错误");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 修改手机号
	 */
	public DataSet changePhone(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String phone = (String) pSet.remove("phone");
			String vcode = (String) pSet.remove("vcode");
			String ccode = (String) CacheManager.get("MessageCode", phone);// 缓存短信验证码
			if (ccode == null) {
				ds.setState(StateType.FAILT);
				ds.setMessage("短信验证码已超时失效");
				return ds;
			} else {
				if (!ccode.equalsIgnoreCase(vcode)) {
					ds.setState(StateType.FAILT);
					ds.setMessage("短信验证码输入错误");
					return ds;
				}
			}
			String id = (String) pSet.getParameter("id");
			String pass = (String) pSet.getParameter("password");
			DataSet hds = this.checkUcUser(id, pass);
			if (hds != null && hds.getTotal() > 0) {
				String sql = "update uc_user set phone=? where id=?";
				this.executeUpdate(sql, new Object[] { phone, id });
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("登录密码输入错误");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 修改密保邮箱激活链接
	 */
	public DataSet rebindValidMail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String uid = (String) pSet.getParameter("id");
			String pass = (String) pSet.getParameter("password");
			DataSet uds = this.checkUcUser(uid, pass);
			if (uds != null && uds.getTotal() > 0) {
				String mail = (String) pSet.getParameter("mail");
				if (StringUtils.isEmpty(mail)) {
					ds.setState(StateType.FAILT);
					ds.setMessage("新安全邮箱不能为空");
					return ds;
				}
				String ymail = (String) pSet.getParameter("ymail");
				String cmail = uds.getRecord(0).getString("EMAIL");// 数据库中记录密保邮箱
				if (StringUtils.isNotEmpty(cmail)) {
					if (!cmail.equals(ymail)) {
						ds.setState(StateType.FAILT);
						ds.setMessage("当前安全邮箱输入错误");
						return ds;
					}
				}
				// 发送验证邮件
				ParameterSet sSet = new ParameterSet();
				sSet.put("mail", mail);
				sSet.put("title", "欢迎访问深圳网上办事大厅，请点击邮件正文中的激活链接为您的账号绑定邮箱。");
				String mailKey = Tools.getUUID32();
				String url = (String) pSet.getParameter("url");
				String validUrl = url + "app/uc/user?action=bind&mailkey="
						+ mailKey + "&uid=" + uid;
				String content = "正在为您的深圳网上办事大厅账号绑定安全邮箱!<br/>请点击以下链接，完成绑定！<br/>"
						+ validUrl
						+ "<br/>如果通过点击以上链接无法访问，"
						+ "请将该网址复制并粘贴至浏览器地址栏进行访问。如果您错误地收到了此电子邮件，你无需执行任何操作来取消帐号！此帐号将不会启动。 ";
				sSet.put("content", content);
				ds = this.sendEmail(sSet);
				// 保存邮件索引
				JSONObject jo = new JSONObject();
				jo.put("mail", mail);
				jo.put("key", mailKey);
				CacheManager.set("mailIndex", uid, jo);
				CacheManager.set("ValidMail", mailKey, pSet);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("登录密码输入错误");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 注册时邮箱验证码
	 */
	public DataSet registerValidEmail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String email = (String) pSet.getParameter("mail");
			pSet.put("title", SecurityConfig.getString("EmailTitle"));
			String rand = this.generateVerifyCode();
			String content = SecurityConfig.getString("RegisterEmailContent")
					+ rand;
			pSet.put("content", content);
			ds = this.sendEmail(pSet);
			if (ds.getState() == StateType.SUCCESS) {
				CacheManager.set("EmailCode", email, rand);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 修改密保问题
	 */
	public DataSet changeAsk(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String uid = (String) pSet.getParameter("id");
			String pass = (String) pSet.getParameter("password");
			DataSet uds = this.checkUcUser(uid, pass);
			if (uds != null && uds.getTotal() > 0) {
				String question = (String) pSet.getParameter("question");
				String answer = (String) pSet.getParameter("answer");
				if (StringUtils.isEmpty(question)
						|| StringUtils.isEmpty(answer)) {
					ds.setState(StateType.FAILT);
					ds.setMessage("新密保问题和密保答案不能为空");
					return ds;
				}
				String canswer = uds.getRecord(0).getString("ANSWER");// 数据库密保答案
				if (StringUtils.isNotEmpty(canswer)) {
					String yanswer = (String) pSet.getParameter("oldanswer");// 输入原密保答案
					if (!canswer.equals(yanswer)) {
						ds.setState(StateType.FAILT);
						ds.setMessage("旧密保答案输入错误");
						return ds;
					}
				}
				String sql = "update uc_user set question=?,answer=? where id=?";
				int i = this.executeUpdate(sql, new Object[] { question,
						answer, uid });
				if (i > 0) {
					ds.setState(StateType.SUCCESS);
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("修改密保信息失败");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("登录密码输入错误");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 获取用户信息
	 */
	private DataSet checkUcUser(String uid, String pass) {
		DataSet ds = new DataSet();
		try {
			String sql = "select email,question,answer from uc_user where id=? and password=?";
			ds = this.executeDataset(sql, new Object[] { uid, pass });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 通过链接绑定新邮箱
	 */
	public DataSet bindEmail(String mailKey) {
		DataSet ds = new DataSet();
		try {
			Object obj = CacheManager.get("ValidMail", mailKey);
			if (obj == null) {
				ds.setState(StateType.FAILT);
				ds.setMessage("链接已失效");
				return ds;
			} else {
				ParameterSet pSet = (ParameterSet) obj;
				String sql = "update uc_user set status=?,email=? where id=?";
				int i = this.executeUpdate(sql,
						new Object[] { "1", (String) pSet.getParameter("mail"),
								(String) pSet.getParameter("id") },
						"icityDataSource");
				if (i > 0) {
					CacheManager.evict("ValidMail", mailKey);
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("绑定邮箱失败");
				}
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 重新发送邮件
	 */
	public DataSet resendMail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String uid = (String) pSet.getParameter("uid");
			Object robj = CacheManager.get("mailIndex", uid);
			if (robj != null) {
				JSONObject jo = (JSONObject) robj;
				String mailKey = jo.getString("key");
				Object obj = CacheManager.get("ValidMail", mailKey);
				if (obj != null) {
					ParameterSet sSet = (ParameterSet) obj;
					return this.rebindValidMail(sSet);
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("邮件已失效");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("链接已失效");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 获取密码方式
	 */
	public DataSet getPwdSecInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject ret = new JSONObject();
		try {
			String loginName = (String) pSet.getParameter("loginName");
			ds = this.verify(loginName);
			if (ds != null && ds.getTotal() > 0) {
				JSONObject jo = ds.getRecord(0);

				// 设置用户名
				ret.put("ACCOUNT", loginName);

				// 获取邮箱后缀
				String email = jo.getString("EMAIL");
				if (StringUtils.isNotEmpty(email)) {
					ret.put("EMAIL", hideEmail(email));
				}

				// 获取手机号
				String phone = jo.getString("PHONE");
				if (StringUtils.isNotEmpty(phone)) {
					ret.put("PHONE", hidePhone(phone));
				}

				// 获取密保问题
				String ask = jo.getString("QUESTION");
				if (StringUtils.isNotEmpty(ask)) {
					ret.put("ASK", ask);
				}

				// 返回数据
				ds.setData(Tools.jsonToBytes(ret));
			} else if (ds != null) {
				ds.setState(StateType.FAILT);
				ds.setMessage("登录用户名不存在！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 重置密码时根据密保类型发送短信或邮件验证码
	 */
	public DataSet resetPassCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String account = (String) pSet.getParameter("account");
			ds = this.verify(account);
			if (ds != null && ds.getTotal() > 0) {
				JSONObject jo = ds.getRecord(0);

				String type = (String) pSet.getParameter("type");
				ParameterSet sSet = new ParameterSet();

				if ("sms".equals(type)) {
					// 通过手机号校验密保信息
					String phone = jo.getString("PHONE");
					sSet.put("PHONE", phone);
					sSet.put("CHANNEL", (String) pSet.getParameter("CHANNEL"));
					sSet.put("SMS", "SecuritySms");
					this.sendMessage(sSet);
				} else if ("mail".equals(type)) {
					// 通过邮件校验密保信息
					String email = jo.getString("EMAIL");
					sSet.put("mail", email);
					sSet.put("title", SecurityConfig.getString("EmailTitle"));
					String rand = this.generateVerifyCode();
					sSet.put("content",
							SecurityConfig.getString("SecurityEmailContent")
									+ rand);
					ds = this.sendEmail(sSet);
					if (ds.getState() == StateType.SUCCESS) {
						CacheManager.set("EmailCode", email, rand);
					}
				}
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 重置密码时根据密保类型发送短信或邮件验证码
	 */
	public DataSet resetPass(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String account = (String) pSet.getParameter("account");
			String newpass = (String) pSet.getParameter("newpass");
			String sql = "update uc_user set password=? where account=? or phone=? or email=?";
			this.executeUpdate(sql, new Object[] { newpass, account, account,
					account });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 隐藏电子邮箱信息
	 */
	private String hideEmail(String email) {
		if (StringUtils.isNotEmpty(email)) {
			int index = email.lastIndexOf("@");
			if (index != -1) {
				String user = email.substring(0, index);
				String last = email.substring(index, email.length());
				if (user.length() > 2) {
					email = user.substring(0, 2) + "************" + last;
				} else {
					email = user + "************" + last;
				}
			}
		}
		return email;
	}

	/**
	 * 隐藏手机号信息
	 */
	private String hidePhone(String phone) {
		if (StringUtils.isNotEmpty(phone)) {
			if (phone.length() == 11) {
				phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
			}
		}
		return phone;
	}

	public DataSet updateProfile(ParameterSet pset) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String user_type = (String) pset.getParameter("USER_TYPE");
			String ProfileOnly = (String) pset.getParameter("ProfileOnly");
			//获取邮寄地址
			String homeMailAddress = (String) pset.getParameter("HomeMailAddress");
			String companyMailAddress = (String) pset.getParameter("CompanyMailAddress");
			JSONObject mailAddress = new JSONObject();
			mailAddress.put("HomeMailAddress", homeMailAddress);
			mailAddress.put("CompanyMailAddress", companyMailAddress);
			// 检查是否已有用户扩展信息
			String sql_ext = "SELECT * FROM UC_USER_EXT WHERE ID = ?";
			ds = DbHelper.query(sql_ext,
					new Object[] { (String) pset.getParameter("ID")},conn);
			int i = 0;
			int j = 0;
			int h = ds.getTotal();
			if ("11".equals(user_type) || "12".equals(user_type)) {
				if(StringUtils.isBlank(ProfileOnly)){
					String sql = "UPDATE UC_USER SET ADDRESS=?, EMAIL=?,PHONE=?, NAME=?,CARD_TYPE=?,CARD_NO=?,LAST_MODIFICATION_TIME=? WHERE ID=?";
					i = DbHelper.update(
							sql,
							new Object[] {
									(String) pset.getParameter("ADDRESS"),
									(String) pset.getParameter("EMAIL"),
									(String) pset.getParameter("PHONE"),
									(String) pset.getParameter("NAME"),
									(String) pset.getParameter("CARD_TYPE"),
									(String) pset.getParameter("CARD_NO"),
									CommonUtils.getInstance().parseDateToTimeStamp(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),
									(String) pset.getParameter("ID") },conn);
				}
				if (h == 0) {
					sql_ext = "INSERT INTO UC_USER_EXT(ID,SEX,NATIVEPLACE,NATION,BIRTHDAY,EDUCATION,POLITICALSTATUS,HOMEADDRESS,POSTCODE,MAILADDRESS) VALUES(?,?,?,?,?,?,?,?,?,?)";
					j = DbHelper.update(
							sql_ext,
							new Object[] {
									(String) pset.getParameter("ID"),
									(String) pset.getParameter("SEX"),
									(String) pset.getParameter("NATIVEPLACE"),
									(String) pset.getParameter("NATION"),
									(String) pset.getParameter("BIRTHDAY"),
									(String) pset.getParameter("EDUCATION"),
									(String) pset.getParameter("POLITICALSTATUS"),
									(String) pset.getParameter("HOMEADDRESS"),
									(String) pset.getParameter("POSTCODE"),
									mailAddress.toString()},conn);
				} else {
					sql_ext = "UPDATE UC_USER_EXT SET SEX=?,NATIVEPLACE=?,NATION=?,BIRTHDAY=?,EDUCATION=?,POLITICALSTATUS=?,HOMEADDRESS=?,POSTCODE=?,MAILADDRESS=? WHERE ID=?";
					j = DbHelper.update(
							sql_ext,
							new Object[] {
									(String) pset.getParameter("SEX"),
									(String) pset.getParameter("NATIVEPLACE"),
									(String) pset.getParameter("NATION"),
									(String) pset.getParameter("BIRTHDAY"),
									(String) pset.getParameter("EDUCATION"),
									(String) pset.getParameter("POLITICALSTATUS"),
									(String) pset.getParameter("HOMEADDRESS"),
									(String) pset.getParameter("POSTCODE"),
									mailAddress.toString(),
									(String) pset.getParameter("ID") },conn);
				}
			} else if ("21".equals(user_type) || "22".equals(user_type)) {
				String changeflag = (String) pset.getParameter("CHANGE_FLAG");
				if ("0".equals(changeflag)) {
					String sql = "UPDATE UC_USER SET ORG_NO=?,ORG_NAME=?,ORG_BOSS_NAME=?,ORG_BOSS_TYPE=?,ORG_BOSS_NO=?,NAME=?,CARD_TYPE=?,CARD_NO=?,LAST_MODIFICATION_TIME=?,PHONE=?,STATUS=?,IDENTIFY_STATUS=? WHERE ID=?";
					i = DbHelper.update(
							sql,
							new Object[] {
									(String) pset.getParameter("ORG_NO"),
									(String) pset.getParameter("ORG_NAME"),
									(String) pset.getParameter("ORG_BOSS_NAME"),
									(String) pset.getParameter("ORG_BOSS_TYPE"),
									(String) pset.getParameter("ORG_BOSS_NO"),
									(String) pset.getParameter("NAME"),
									(String) pset.getParameter("CARD_TYPE"),
									(String) pset.getParameter("CARD_NO"),
									CommonUtils.getInstance().parseDateToTimeStamp(new Date(),
													CommonUtils.YYYY_MM_DD_HH_mm_SS),
									(String) pset.getParameter("PHONE"), "1","", (String) pset.getParameter("ID") },conn);
				} else {
					if(StringUtils.isBlank(ProfileOnly)){
						String sql = "UPDATE UC_USER SET ORG_NO=?,ORG_NAME=?,ORG_BOSS_NAME=?,ORG_BOSS_TYPE=?,ORG_BOSS_NO=?,NAME=?,CARD_TYPE=?,CARD_NO=?,LAST_MODIFICATION_TIME=?,PHONE=? WHERE ID=?";
						i = DbHelper.update(
								sql,
								new Object[] {
										(String) pset.getParameter("ORG_NO"),
										(String) pset.getParameter("ORG_NAME"),
										(String) pset.getParameter("ORG_BOSS_NAME"),
										(String) pset.getParameter("ORG_BOSS_TYPE"),
										(String) pset.getParameter("ORG_BOSS_NO"),
										(String) pset.getParameter("NAME"),
										(String) pset.getParameter("CARD_TYPE"),
										(String) pset.getParameter("CARD_NO"),
										CommonUtils.getInstance().parseDateToTimeStamp(new Date(),
														CommonUtils.YYYY_MM_DD_HH_mm_SS),
										(String) pset.getParameter("PHONE"),
										(String) pset.getParameter("ID") },conn);
					}
					if (h == 0) {
						sql_ext = "INSERT INTO UC_USER_EXT(ID,ORGCODEAWARDORG,ORGCODEVALIDPERIODSTART_STR,ORGCODEVALIDPERIODEND_STR,ORGENGLISHNAME,ORGTYPE,ORGACTUALITY,ENTERPRISESORTCODE,ENTERPRISESORTNAME,ORGCODEAWARDDATE_STR,REGISTERDATE_STR) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
						j = DbHelper.update(
								sql_ext,
								new Object[] {
										(String) pset.getParameter("ID"),
										(String) pset.getParameter("ORGCODEAWARDORG"),
										(String) pset.getParameter("ORGCODEVALIDPERIODSTART_STR"),
										(String) pset.getParameter("ORGCODEVALIDPERIODEND_STR"),
										(String) pset.getParameter("ORGENGLISHNAME"),
										(String) pset.getParameter("ORGTYPE"),
										(String) pset.getParameter("ORGACTUALITY"),
										(String) pset.getParameter("ENTERPRISESORTCODE"),
										(String) pset.getParameter("ENTERPRISESORTNAME"),
										(String) pset.getParameter("ORGCODEAWARDDATE_STR"),
										(String) pset.getParameter("REGISTERDATE_STR") },conn);
					} else {
						sql_ext = "UPDATE UC_USER_EXT SET ORGCODEAWARDORG=?,ORGCODEVALIDPERIODSTART_STR=?,ORGCODEVALIDPERIODEND_STR=?,ORGENGLISHNAME=?,ORGTYPE=?,ORGACTUALITY=?,ENTERPRISESORTCODE=?,ENTERPRISESORTNAME=?,ORGCODEAWARDDATE_STR=?,REGISTERDATE_STR=? WHERE ID=?";
						j = DbHelper.update(
								sql_ext,
								new Object[] {
										(String) pset.getParameter("ORGCODEAWARDORG"),
										(String) pset.getParameter("ORGCODEVALIDPERIODSTART_STR"),
										(String) pset.getParameter("ORGCODEVALIDPERIODEND_STR"),
										(String) pset.getParameter("ORGENGLISHNAME"),
										(String) pset.getParameter("ORGTYPE"),
										(String) pset.getParameter("ORGACTUALITY"),
										(String) pset.getParameter("ENTERPRISESORTCODE"),
										(String) pset.getParameter("ENTERPRISESORTNAME"),
										(String) pset.getParameter("ORGCODEAWARDDATE_STR"),
										(String) pset.getParameter("REGISTERDATE_STR"),
										(String) pset.getParameter("ID") },conn);

					}
				}
			}

			if ("11".equals(user_type) || "12".equals(user_type)) {
				if ((StringUtils.isBlank(ProfileOnly)&&i == 0) || j == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据更新失败");
				}else {
					ds.setState(StateType.SUCCESS);
				}
				if(StringUtils.isNotBlank(ProfileOnly) && j != 0){
					ds.setState(StateType.SUCCESS);
				}
			} else if ("21".equals(user_type) || "22".equals(user_type)) {
				if (i == 0 || j == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据更新失败");
				}else {
					ds.setState(StateType.SUCCESS);
				}
				if(StringUtils.isNotBlank(ProfileOnly) && j != 0){
					ds.setState(StateType.SUCCESS);
				}
			}
			conn.commit();
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		} finally {
			try {
				DBSource.closeConnection(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return ds;
	}

	/**
	 * 账号信息 2016-2-22添加用户扩展信息
	 */
	public DataSet showUserInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		DataSet ds_ext;
		try {
			String sql = "select * from uc_user";
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			ds = this.executeDataset(sql);
			if (ds != null && ds.getTotal() > 0) {
				JSONObject jo = ds.getRecord(0);
				if (jo != null) {
					String sql_ext = "select * from uc_user_ext";
					sql_ext = SqlCreator.getSimpleQuerySql(pset, sql_ext,
							this.getDataSource());
					ds_ext = this.executeDataset(sql_ext);
					/*
					 * String phone = jo.getString("PHONE"); jo.put("PHONE",
					 * hidePhone(phone)); String email = jo.getString("EMAIL");
					 * jo.put("EMAIL", hideEmail(email));
					 */
					if (ds_ext.getTotal() > 0) {
						JSONObject jo_ext = ds_ext.getRecord(0);
						jo.putAll(jo_ext);
					}
					ds.setData(Tools.jsonToBytes(jo));
				}
			} else if (ds != null) {
				ds.setState(StateType.FAILT);
				ds.setMessage("查询用户信息失败");
			}

		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet updateRealName(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String user_type = (String) pset.getParameter("USERTYPE");
			int i = 0;
			if ("11".equals(user_type) || "12".equals(user_type)) {
				String sql = "update uc_user set name= ?, card_type= ?, card_no= ?, last_modification_time= ?,IDENTIFY_STATUS = ?,IDENTIFY_PIC_URL = ?,FRONT_PIC_URL = ?,AFTER_PIC_URL = ? where id= ?";
				i = this.executeUpdate(
						sql,
						new Object[] {
								(String) pset.getParameter("NAME"),
								(String) pset.getParameter("CARD_TYPE"),
								(String) pset.getParameter("CARD_NO"),
								CommonUtils.getInstance().parseDateToTimeStamp(
										new Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS), "0",
								(String) pset.getParameter("IDENTIFY_PIC_URL"),
								(String) pset.getParameter("FRONT_PIC_URL"),
								(String) pset.getParameter("AFTER_PIC_URL"),
								(String) pset.getParameter("ID") });
			} else if ("21".equals(user_type) || "22".equals(user_type)) {
				String sql = "update uc_user set ORG_NAME= ?, ORG_NO= ?, last_modification_time= ?,IDENTIFY_STATUS = ?,IDENTIFY_PIC_URL = ?,FRONT_PIC_URL = ?,AFTER_PIC_URL = ? where id= ?";
				i = this.executeUpdate(
						sql,
						new Object[] {
								(String) pset.getParameter("ORG_NAME"),
								(String) pset.getParameter("ORG_NO"),
								CommonUtils.getInstance().parseDateToTimeStamp(
										new Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS), "0",
								(String) pset.getParameter("IDENTIFY_PIC_URL"),
								(String) pset.getParameter("FRONT_PIC_URL"),
								(String) pset.getParameter("AFTER_PIC_URL"),
								(String) pset.getParameter("ID") });
			}
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("更新实名信息失败");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	public DataSet identification(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			//String user_type = (String) pset.getParameter("USERTYPE");
			String user_type = "11";
			int i = 0;
			if ("11".equals(user_type)) {
				String sql = "update uc_user set name= ?, card_type= ?, card_no= ?, status=?, last_modification_time= ? where id= ?";
				i = this.executeUpdate(
						sql,
						new Object[] {
								(String) pset.getParameter("NAME"),
								//身份证类型：10
								"10",
								(String) pset.getParameter("CARD_NO"),
								//实名认证
								"2",
								CommonUtils.getInstance().parseDateToTimeStamp(
										new Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS),
										(String) pset.getParameter("ID") });
				if(i>0){
					//验证扩展表中是否有记录
					sql = "select 1 from uc_user_ext where id = ?";
					i = this.executeUpdate(sql, new Object[] {(String) pset.getParameter("ID")});
					if(i>0){
						sql = "update uc_user_ext set sex= ?, nation= ? where id= ?";
						i = this.executeUpdate(
								sql,
								new Object[] {
										(String) pset.getParameter("SEX"),
										(String) pset.getParameter("NATION"),
										(String) pset.getParameter("ID")
										});
					}else{
						sql = "insert into uc_user_ext(id,sex,nation) values(?,?,?)";
						i = this.executeUpdate(
								sql,
								new Object[] {
										(String) pset.getParameter("ID"),
										(String) pset.getParameter("SEX"),
										(String) pset.getParameter("NATION"),
								});
						
					}
				}
			}
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("实名认证失败");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet updatePhotoUri(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String sql = "update uc_user set PHOTO_URI= ?, last_modification_time= ? where id= ?";
			int i = this.executeUpdate(
					sql,
					new Object[] {
							(String) pset.getParameter("photo_uri"),
							CommonUtils.getInstance()
									.parseDateToTimeStamp(new Date(),
											CommonUtils.YYYY_MM_DD_HH_mm_SS),
							(String) pset.getParameter("ID") });
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("更新头像信息失败");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet resetPassword(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String account = (String) pset.getParameter("account");
			String phone = (String) pset.getParameter("phone");
			String is_phone = (String) pset.getParameter("is_phone");
			pset.remove("phone");
			pset.remove("is_phone");
			String email = (String) pset.getParameter("email");
			String is_email = (String) pset.getParameter("is_email");
			pset.remove("is_email");
			pset.remove("email");
			String password = (String) pset.getParameter("password");
			pset.remove("password");
			String sql = "update uc_user set password = ?";
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			int i = this
					.executeUpdate(sql, new Object[] { Tools.getMD5(Tools
							.stringToBytes(password)) });
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("重置密码失败");
			} else {
				ds.setState(StateType.SUCCESS);
			}
			if ("1".equals(is_phone)) {
				String smsContent = "您用户名为[" + account + "]的账户，密码已被重置为["
						+ password + "]，请尽快登录并修改密码!["
						+ SecurityConfig.getString("AppTitle") + "]";
				sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone) values(?,?,?,?,?,?)";
				this.executeUpdate(
						sql,
						new Object[] {
								Tools.getUUID32(),
								smsContent,
								CommonUtils
										.getInstance()
										.parseStringToTimeStamp(
												Tools.formatDate(
														new Date(),
														CommonUtils.YYYY_MM_DD_HH_mm_SS),
												CommonUtils.YYYY_MM_DD_HH_mm_SS),
								"notice", "0", phone });
			}
			if ("1".equals(is_email)) {
				ParameterSet aP = new ParameterSet();
				aP.setParameter("mail", email);
				aP.setParameter("title", "重置密码");
				aP.setParameter(
						"content",
						"您用户名为[" + account + "]的账户，密码已被重置为[" + password
								+ "]，请尽快登录并修改密码!["
								+ SecurityConfig.getString("AppTitle") + "]");
				sendEmail(aP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 根据账号、手机号或邮箱查询用户信息
	 * 
	 * @param account
	 *            字段参数
	 */
	public DataSet getUserByAccount(String account) {
		DataSet ds = new DataSet();
		try {
			String sql = "select * from uc_user";

			Pattern emailPattern = Pattern
					.compile("^(\\w)+(\\.\\w+)*@(\\w)+(\\.\\w+)+$"); // 邮箱正则表达式
			Matcher emailMatcher = emailPattern.matcher(account);
			Pattern phonePattern = Pattern.compile("^[1][358]\\d{9}$"); // 手机号正则表达式
			Matcher phoneMatcher = phonePattern.matcher(account);

			if (phoneMatcher.matches()) {
				sql += " where phone=?";
			} else if (emailMatcher.matches()) {
				sql += " where email=?";
			} else {
				sql += " where account=?";
			}
			ds = this.executeDataset(sql, new Object[] { account });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getUserById(String userId) {
		ParameterSet pset = new ParameterSet();
		pset.setParameter("id", userId);
		return this.getList(pset);
	}

	/**
	 * 用户信息修改
	 * 
	 * @param pSet
	 *            待修改信息
	 * @param userBean
	 *            被修改用户
	 * @return
	 */
	public DataSet update(ParameterSet pSet, UserBean userBean) {
		DataSet ds = new DataSet();
		try {
			long userId = userBean.getId();
			/**
			 * 可变更的信息：
			 * name,email,phone,password,status,card_type,card_no,org_name,
			 * org_no,org_boss_type,
			 * org_boss_no,org_boss_name,last_modification_time(sysdate),
			 * question,answer
			 */
			String name = (String) pSet.getParameter("NAME");
			String email = (String) pSet.getParameter("EMAIL");
			String phone = (String) pSet.getParameter("PHONE");
			String password = (String) pSet.getParameter("PASSWORD");
			String status = (String) pSet.getParameter("STATUS");
			String card_type = (String) pSet.getParameter("CARD_TYPE");
			String card_no = (String) pSet.getParameter("CARD_NO");
			String org_name = (String) pSet.getParameter("ORG_NAME");
			String org_no = (String) pSet.getParameter("ORG_NO");
			String org_boss_type = (String) pSet.getParameter("ORG_BOSS_TYPE");
			String org_boss_no = (String) pSet.getParameter("ORG_BOSS_NO");
			String org_boss_name = (String) pSet.getParameter("ORG_BOSS_NAME");
			String question = (String) pSet.getParameter("QUESTION");
			String answer = (String) pSet.getParameter("ANSWER");
			// 检查邮箱、手机号码、机构代码是否已被其他账户注册
			StringBuilder checkBuilder = new StringBuilder();
			StringBuilder orBuilder = new StringBuilder();
			checkBuilder.append("SELECT * FROM UC_USER");
			if (StringUtils.isNotEmpty(email) || StringUtils.isNotEmpty(phone)
					|| StringUtils.isNotEmpty(org_no)) {
				Object[] params = new Object[10];
				checkBuilder.append(" WHERE id != ? and (");
				params[0] = userId;
				int paramsIndex = 1;
				if (StringUtils.isNotEmpty(email)) {
					if (orBuilder.length() > 0) {
						orBuilder.append(" OR ");
					}
					orBuilder.append("EMAIL = ?");
					params[paramsIndex++] = email;
				}
				if (StringUtils.isNotEmpty(phone)) {
					if (orBuilder.length() > 0) {
						orBuilder.append(" OR ");
					}
					orBuilder.append("PHONE = ?");
					params[paramsIndex++] = phone;
				}
				if (StringUtils.isNotEmpty(org_no)) {
					if (orBuilder.length() > 0) {
						orBuilder.append(" OR ");
					}
					orBuilder.append("ORG_NO = ?");
					params[paramsIndex++] = org_no;
				}
				Object[] rp = new Object[paramsIndex];
				for (int i = 0; i < params.length; i++) {
					if (StringUtils.isNotEmpty((String) params[i])) {
						rp[i] = params[i];
					} else {
						break;
					}
				}
				String checkSql = checkBuilder.append(orBuilder).append(")")
						.toString();
				ds = this.executeDataset(checkSql, rp);
				if (ds.getTotal() > 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("信息已被注册");
					return ds;
				}
			}

			// 变更
			StringBuffer updateSb = new StringBuffer();
			StringBuffer setSb = new StringBuffer();
			updateSb.append("update uc_user set ");
			ArrayList paramsList = new ArrayList();
			int paramsIndex = 0;
			/**
			 * 可变更的信息：
			 * name,email,phone,password,status,card_type,card_no,org_name,
			 * org_no,org_boss_type,
			 * org_boss_no,org_boss_name,last_modification_time(sysdate),
			 * question,answer
			 */
			if (StringUtils.isNotEmpty(name)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("name = ?");
				paramsList.add(name);
			}
			if (StringUtils.isNotEmpty(email)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("email = ?");
				paramsList.add(email);
			}
			if (StringUtils.isNotEmpty(phone)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("phone = ?");
				paramsList.add(phone);
			}
			if (StringUtils.isNotEmpty(password)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("password = ?");
				paramsList.add(password);
			}
			if (StringUtils.isNotEmpty(status)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("status = ?");
				paramsList.add(status);
			}
			if (StringUtils.isNotEmpty(card_type)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("card_type = ?");
				paramsList.add(card_type);
			}
			if (StringUtils.isNotEmpty(card_no)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("card_no = ?");
				paramsList.add(card_no);
			}
			if (StringUtils.isNotEmpty(org_name)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("org_name = ?");
				paramsList.add(org_name);
			}
			if (StringUtils.isNotEmpty(org_no)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("org_no = ?");
				paramsList.add(org_no);
			}
			if (StringUtils.isNotEmpty(org_boss_type)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("org_boss_type = ?");
				paramsList.add(org_boss_type);
			}
			if (StringUtils.isNotEmpty(org_boss_no)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("org_boss_no = ?");
				paramsList.add(org_boss_no);
			}
			if (StringUtils.isNotEmpty(org_boss_name)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("org_boss_name = ?");
				paramsList.add(org_boss_name);
			}
			if (StringUtils.isNotEmpty(question)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("question = ?");
				paramsList.add(question);
			}
			if (StringUtils.isNotEmpty(answer)) {
				if (setSb.length() > 0) {
					setSb.append(",");
				}
				setSb.append("answer = ?");
				paramsList.add(answer);
			}
			if (setSb.length() > 0) {
				setSb.append(",");
			}
			setSb.append("last_modification_time = ?");
			paramsList.add(new java.sql.Date(new java.util.Date().getTime()));

			updateSb.append(setSb).append(" where id = ?");
			paramsList.add(userId);

			String updateSql = updateSb.toString();
			int i = this.executeUpdate(updateSql, paramsList.toArray());
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("修改用户信息失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	public DataSet editPhone(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sql = "update uc_user set PHONE=? where ID=?";
		String uid = (String) pset.getParameter("USERID");
		String phone = (String) pset.getParameter("NEW_PHONE");
		int i = this.executeUpdate(sql, new Object[] { phone, uid });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("更换手机号码失败");
		} else {
			ds.setState(StateType.SUCCESS);
		}

		return ds;
	}

	public DataSet changeInuse(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sql = "update uc_user set is_inuse=? where ID=?";
		String uid = (String) pset.getParameter("id");
		String isInuse = (String) pset.getParameter("is_inuse");
		int i = this.executeUpdate(sql, new Object[] { isInuse, uid });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("更换手机号码失败");
		} else {
			ds.setState(StateType.SUCCESS);
		}

		return ds;
	}

	public DataSet identify(ParameterSet pset) {
		DataSet ds = new DataSet();
		String sql = "update uc_user t set t.IDENTIFY_STATUS=? , t.IDENTIFY_FAIL_REASON = ? , t.STATUS = ? , t.LAST_MODIFICATION_TIME=? where t.id=?";
		String id = (String) pset.getParameter("id");
		String identifyStatus = (String) pset.getParameter("identifyStatus");
		String failReason = (String) pset.getParameter("failReason");
		Date lastModifiedTime = CommonUtils.getInstance().parseDateToTimeStamp(
				new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS);
		String status = "1";
		if (identifyStatus.equals("2")) {
			status = "2";
		}

		int i = this.executeUpdate(sql, new Object[] { identifyStatus,
				failReason, status, lastModifiedTime, id });
		if (i == 1) {
			ds.setState(StateType.SUCCESS);
			ds.setMessage("修改成功");
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据修改失败，请联系管理员！");
		}
		return ds;
	}

	public DataSet bindPhoneOrEmail(String type, String link, String id) {
		DataSet ds = new DataSet();
		int i = 0;
		if ("PHONE".equals(type)) {
			String sql = "update uc_user set phone = ?,is_phonebind = '1' where ID = ?";
			i = this.executeUpdate(sql, new Object[] { link, id });
		} else if ("EMAIL".equals(type)) {
			String sql = "update uc_user set email = ?,is_emailbind = '1' where ID = ?";
			i = this.executeUpdate(sql, new Object[] { link, id });
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("绑定失败");
		}
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("绑定失败");
		} else {
			ds.setState(StateType.SUCCESS);
			ds.setMessage("绑定成功");
		}
		return ds;
	}

	/**
	 * 默认为用户登录手机短信验证
	 */
	public DataSet sendMessage4Login(ParameterSet pSet) {
		String rand = "1234";
		DataSet ds = new DataSet();
		try {
			String phone = (String) pSet.getParameter("PHONE");
			Date cookieDate = (Date) CacheManager.get("login_sendsms", phone);
			CacheManager.evict("login_sendsms", phone);
			CacheManager.set("login_sendsms", phone, new Date());
			if (null != cookieDate) {
				Date now = new Date();
				long between = (now.getTime() - cookieDate.getTime()) / 1000;
				long minute = between / 60;
				if (minute < 1) {
					ds.setState(StateType.FAILT);
					ds.setMessage("60秒内不能重复发送短信");
					return ds;
				}
			}
			rand = this.generateVerifyCode();
			ds.setRoute(rand);
			CacheManager.set("MessageCode", phone, rand);
			String smsContent = "", message = "";
			if (pSet.containsKey("SMS")) {
				// 如果配置了SMS，则从security.properties加载对应短信模板，否则从参数中获取短信内容
				String sms = (String) pSet.getParameter("SMS");
				message = SecurityConfig.getString(sms);
			} else {
				message = (String) pSet.getParameter("MESSAGE");
			}
			smsContent = (message == null ? "" : message) + rand;
			String sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone) values(?,?,?,?,?,?)";
			this.executeUpdate(
					sql,
					new Object[] {
							Tools.getUUID32(),
							smsContent,
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(),
											CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
							"register", "0", phone });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return ds;
	}
	/**
	 * 重庆编辑
	 * @param id2 
	 */
	public DataSet updateEmail(ParameterSet pset) {
		DataSet ds = new DataSet();
		String ORGCODEAWARDORG = (String) pset.getParameter("ORGCODEAWARDORG");
		String ORGCODEVALIDPERIODSTART_STR = (String) pset.getParameter("ORGCODEVALIDPERIODSTART_STR");
		String ORGCODEVALIDPERIODEND_STR = (String)pset.getParameter("ORGCODEVALIDPERIODEND_STR");
		String ENTERPRISESORTCODE = (String)pset.getParameter("ENTERPRISESORTCODE");
		String ORGENGLISHNAME = (String)pset.getParameter("ORGENGLISHNAME");
		String ENTERPRISESORTNAME = (String)pset.getParameter("ENTERPRISESORTNAME");
		String ORGCODEAWARDDATE_STR = (String)pset.getParameter("ORGCODEAWARDDATE_STR");
		String REGISTERDATE_STR = (String)pset.getParameter("REGISTERDATE_STR");
		String ORGTYPE = (String)pset.getParameter("ORGTYPE");
		String ORGACTUALITY = (String)pset.getParameter("ORGACTUALITY");
		
		
		String email = (String) pset.getParameter("EMAIL");
		String ID = (String) pset.getParameter("ID");
		String ORG_NAME = (String)pset.getParameter("ORG_NAME");
		String ORG_BOSS_NAME = (String)pset.getParameter("ORG_BOSS_NAME");
		String ORG_BOSS_NO = (String)pset.getParameter("ORG_BOSS_NO");
		String SEX = (String)pset.getParameter("SEX");
		String NATION = (String)pset.getParameter("NATION");
		String BUSINESS_SCOPE = (String)pset.getParameter("BUSINESS_SCOPE");
		//String sql = "update uc_user as usersr,UC_USER_EXT as usersext set usersext.ORGCODEAWARDORG =?,usersext.ORGCODEVALIDPERIODSTART_STR=?,usersext.ORGCODEVALIDPERIODEND_STR=?,usersext.ORGENGLISHNAME=?,usersext.ENTERPRISESORTCODE=?,usersext.ENTERPRISESORTNAME=?,usersext.ORGCODEAWARDDATE_STR=?,usersext.REGISTERDATE_STR=?,usersext.SEX=?,usersr.email=?,usersr.ORG_NAME=?,usersr.ORG_BOSS_NAME=?,usersr.CARD_NO=?,usersext.NATION=? where ID=?";
		String sql2="update uc_user set email=?,ORG_NAME=?,ORG_BOSS_NAME=?,ORG_BOSS_NO=? where ID=?";
		int j1 = this.executeUpdate(sql2, new Object[] {email,ORG_NAME,ORG_BOSS_NAME,ORG_BOSS_NO,ID});
		//校验扩展表中是否有这条信息
		String sql = "select 1 from uc_user_ext where id = ?";
		int i = this.executeUpdate(sql, new Object[] {ID});
		if(i>0){
			sql="update UC_USER_EXT SET ORGCODEAWARDORG =?,ORGCODEVALIDPERIODSTART_STR=?,ORGCODEVALIDPERIODEND_STR=?,ORGENGLISHNAME=?,ENTERPRISESORTCODE=?,ENTERPRISESORTNAME=?,ORGCODEAWARDDATE_STR=?,REGISTERDATE_STR=?,SEX=?,NATION=?,ORGTYPE=?,ORGACTUALITY=?,BUSINESS_SCOPE=? where ID=?";	
		}else{
			sql="insert into uc_user_ext (ORGCODEAWARDORG,ORGCODEVALIDPERIODSTART_STR,ORGCODEVALIDPERIODEND_STR,ORGENGLISHNAME,ENTERPRISESORTCODE,ENTERPRISESORTNAME,ORGCODEAWARDDATE_STR,REGISTERDATE_STR,SEX,NATION,ORGTYPE,ORGACTUALITY,BUSINESS_SCOPE,ID) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}
		//	邮箱 机构名称 机构代码 机构法人证件类型 机构法人编码 机构法人名称
		int j = this.executeUpdate(sql, new Object[] {ORGCODEAWARDORG,ORGCODEVALIDPERIODSTART_STR,ORGCODEVALIDPERIODEND_STR,ORGENGLISHNAME,ENTERPRISESORTCODE,ENTERPRISESORTNAME,ORGCODEAWARDDATE_STR,REGISTERDATE_STR,SEX,NATION,ORGTYPE,ORGACTUALITY,BUSINESS_SCOPE,ID});
		//int i = this.executeUpdate(sql, new Object[] {ORGCODEAWARDORG,ORGCODEVALIDPERIODSTART_STR,ORGCODEVALIDPERIODEND_STR,ORGENGLISHNAME,ENTERPRISESORTCODE,ENTERPRISESORTNAME,ORGCODEAWARDDATE_STR,REGISTERDATE_STR,SEX,email,ORG_NAME,ORG_BOSS_NAME,CARD_NO,NATION,ID});
		if (j == 0||j1==0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("信息更换失败");
		} else {
			ds.setState(StateType.SUCCESS);
		}
				
		return ds;
	}
	
	/**
	 * 大汉统一身份认证用户注册
	 */
	public DataSet UniteIndentifyRegister(ParameterSet pSet) {
		DataSet ds = new DataSet();
//		intermed inter = new intermed();
		//String webRank = SecurityConfig.getString("WebRank");
//		String webRegion = SecurityConfig.getString("WebRegion");
//		String appId = SecurityConfig.getString("AppId");
		
		try {
			System.out.println("=======jinlai:"+pSet);
			String userType = (String) pSet.getParameter("TYPE");
			String loginname = (String) pSet.getParameter("ACCOUNT");
			String pwd = (String) pSet.getParameter("PASSWORD");
			String name = (String) pSet.getParameter("NAME");
//			String phone = "";
			String mobile = (String) pSet.getParameter("PHONE");
			String email = (String) pSet.getParameter("EMAIL");
//			String qq = "";
//			String msn = "";
//			String age = "";
//			String sex = "";
//			String address = "";
//			String post = "";
//			String degree = "";
			String paperstype = "1";
			String papersnumber = (String) pSet.getParameter("CARD_NO");
			String workunit = (String) pSet.getParameter("ORG_NAME");
//			String birthdate = "";
//			String headship = "";
//			String fax = "";
//			String authstate = "";
			
			
			JSONObject jo = new JSONObject();
			jo.put("loginname", loginname);
			jo.put("pwd", pwd);
			jo.put("name", name);
			jo.put("mobile", mobile);
			jo.put("email", email);
			jo.put("paperstype", paperstype);
			jo.put("papersnumber", papersnumber);
			jo.put("workunit", workunit);
			
			UniteUserInterface unitUI = new UniteUserInterface();
			unitUI.setUserType("11".equals(userType)?"1":"2");
			JSONObject resultJo = unitUI.register(jo);
			String resultCode = resultJo.getString("result");
			ds.setData(Tools.jsonToBytes(resultJo));
			ds.setState("0".equals(resultCode)?StateType.SUCCESS:StateType.FAILT );
			String message;
			if("0".equals(resultCode)){
				message = "注册成功！";
			} else if("204".equals(resultCode)){
				message = "登录名不能为空!";
			} else if("205".equals(resultCode)){
				message = "登录名格式不正确!";
			} else if("206".equals(resultCode)){
				message = "已存在相同的登录名!";
			} else if("207".equals(resultCode)){
				message = "身份证号码已存在!";
			} else if("208".equals(resultCode)){
				message = "手机号码格式不正确!";
			} else if("209".equals(resultCode)){
				message = "手机号码重复!";
			} else if("210".equals(resultCode)){
				message = "邮箱格式不正确!";
			} else if("211".equals(resultCode)){
				message = "邮箱重复!";
			} else{
				message = "系统错误!";
			}
			ds.setMessage(message);
			
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}
	/**
	 * 通过id 获取IS_EMS
	 */
	public DataSet getISEMS(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "select IS_EMS from uc_user where id=?";
			ds = this.executeDataset(sql,
					new Object[] { (String) pSet.getParameter("id") });
			if (ds != null && ds.getTotal() > 0) {
				String IS_EMS = ds.getRecord(0).getString("IS_EMS");
				ds.setData(Tools.stringToBytes(IS_EMS));
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	
	/**
	 * 通过userId获取外网用户映射ID及用户类型，身份证等信息
	 * 
	 */
	public DataSet getMapAndUserInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		String uid = (String)pset.getParameter("uid");
		try {
			String sql = "select a.USER_ID_MAP,b.TYPE,b.CARD_NO,b.CREDIT_CODE from uc_user_map a,uc_user b where a.user_id = b.id and b.id = ?";
			ds = this.executeDataset(sql, new Object[] {uid});
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
}