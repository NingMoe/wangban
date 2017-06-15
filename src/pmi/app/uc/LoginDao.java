package app.uc;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.Constant;
import com.inspur.util.Tools;

public class LoginDao extends BaseJdbcDao {
	private LoginDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static LoginDao getInstance() {
		return DaoFactory.getDao(LoginDao.class.getName());
	}

	public DataSet checkLogin(ParameterSet pSet) {
		DataSet ds;
		if("2".equals(SecurityConfig.getString("UniteIdentifyFlag")))
		{
			ds = new DataSet();
			action.sd.login loger = new action.sd.login();
			ds.setRawData(loger.uniteIdentifyLoginLocal(pSet));
			ds.setState(StateType.SUCCESS);
		} else {
			ds = findUser(pSet);
			if (ds.getState() == StateType.SUCCESS) {
				ds = setUcUserInfo(ds.getJOData(), (String) pSet.getParameter("cookie"));
			}
		}
		return ds;
	}

	/**
	 * 查找用户
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet findUser(ParameterSet pSet) {
		DataSet reDs = new DataSet();// 返回结果

		String type = (String) pSet.getParameter("type"); // 验证方式，用户密码验证或CA登录验证
		String sessionId = (String) pSet.getParameter("sessionId");
		if ("YH".equals(type)) {
			String account = (String) pSet.getParameter("account");// 用户名
			String password = (String) pSet.getParameter("pwd");// 密码
			if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
				reDs.setMessage("请传递用户名和密码！");
				reDs.setState(StateType.FAILT);
			} else {
				Object fail_time = CacheManager.get("login_fail_time", (String) pSet.getParameter("remoteAddr"));
				if (fail_time != null && (Integer) fail_time > 3
						&& !checkVerifyCode((String) pSet.getParameter("verify_code"), sessionId)) {
					// 失败3次以上（出现验证码），并且验证码错误
					reDs.setMessage("验证码输入错误！");
					reDs.setState(StateType.FAILT);
				} else {
					reDs = getUcUserInfo(account, password, type);
				}
			}
		}

		if (reDs.getState() == StateType.SUCCESS) {
			if ("0".equals(reDs.getRecord(0).getString("IS_INUSE"))) {
				reDs.setState(StateType.FAILT);
				reDs.setMessage("您的账号被禁用，请联系管理员！");
				reDs.setData(null);
			}
		}
		return reDs;
	}

	private boolean checkVerifyCode(String code, String sessionId) {
		String verrify_code = (String) CacheManager.get("VerifyCode", sessionId);
		if (StringUtils.isEmpty(verrify_code)) {
			return false;
		} else {
			if (!verrify_code.equalsIgnoreCase(code)) {
				return false;
			}
		}
		return true;
	}

	private DataSet getUcUserInfo(String account, String password, String type) {
		DataSet ds = new DataSet();
		try {
			ds = UserDao.getInstance().verify(account);
			if (ds.getTotal() > 0) {
				if ("YH".equals(type) && !password.equals(ds.getRecord(0).getString("PASSWORD"))) {
					ds.setMessage("用户名或密码错误，请重试！");
					ds.setState(StateType.FAILT);
					ds.setData(null);
				}
			} else {
				ds.setMessage("用户名或密码错误，请重试！");
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 
	 * @param uds
	 *            用户信息结果集
	 * @param tokenid
	 *            从CA服务端返回的信令
	 * @return 将用户信息写入缓存
	 */
	public DataSet setUcUserInfo(JSONObject jo, String cookie) {
		DataSet ds = new DataSet();
		JSONObject reJo = new JSONObject();
		reJo.putAll(jo);
		String tokenid = null;
		reJo.put("tokenid", Tools.getUUID32());
		reJo.put("random", Tools.getUUID32());
		tokenid = reJo.getString("tokenid");
		UserInfo ui = intiUcUserInfo(jo, tokenid);
		CacheManager.evict(Constant.SESSIONID, cookie);
		CacheManager.set(Constant.SESSIONID, cookie, ui);
		ds.setTotal(1);
		ds.setRawData(reJo);
		return ds;
	}

	private UserInfo intiUcUserInfo(JSONObject jo, String tokenid) {
		UserInfo ui = new UserInfo();
		ui.setUserId(jo.getString("ACCOUNT"));
		ui.setMobile(jo.getString("PHONE"));
		ui.setNo(jo.getString("CARD_NO"));
		ui.setUid(jo.getInt("ID"));
		ui.setType(jo.getInt("TYPE"));
		ui.setTickId(tokenid);
		if (jo.getInt("TYPE") == 11 || jo.getInt("TYPE") == 12) {
			ui.setUserName(jo.getString("NAME"));
		} else if (jo.getInt("TYPE") == 21 || jo.getInt("TYPE") == 22||jo.getInt("TYPE") == 31) {
			ui.setUserName(jo.getString("ORG_NAME"));
		}
		return ui;
	}
}
