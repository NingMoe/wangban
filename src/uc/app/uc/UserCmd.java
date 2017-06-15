package app.uc;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;

public class UserCmd extends BaseQueryCommand {
	/***
	 * 用户注册
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet register(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String validType = SecurityConfig.getString("ValidType");
			String vcode = (String) pset.get("VCODE");
			if (StringUtils.isEmpty(validType)) {
				String verifyCode = (String) CacheManager.get("VerifyCode", pset.getSessionId());
				DataSet rds = this.verifyCode(verifyCode, vcode);
				if (rds.getState() == StateType.FAILT) {
					return rds;
				} 
			} else {
				if ("mail".equals(validType)) {
					String email = (String) pset.getParameter("EMAIL");
					String emailCode = (String) CacheManager.get("EmailCode", email);
					DataSet rds = this.verifyCode(emailCode, vcode);
					if (rds.getState() == StateType.FAILT) {
						return rds;
					}
				} else if ("sms".equals(validType)) {
					String phone = (String) pset.getParameter("PHONE");
					String messageCode = (String) CacheManager.get("MessageCode", phone);
					DataSet rds = this.verifyCode(messageCode, vcode);
					if (rds.getState() == StateType.FAILT) {
						return rds;
					}
				}
			}
			if("2".equals(SecurityConfig.getString("UniteIdentifyFlag"))){
				ds = UserDao.getInstance().UniteIndentifyRegister(pset);
			} else {
				ds = UserDao.getInstance().register(pset);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	
	/**
	 * 修改用户手机号
	 * @param pSet
	 * @return
	 */
	public DataSet editPhone(ParameterSet pset) {
		String phone = (String) pset.getParameter("PHONE");
		if(!StringUtils.isEmpty(phone)) {
			String vcode = (String) pset.get("VCODE");
			String messageCode = (String) CacheManager.get("MessageCode", phone);
			DataSet rds = this.verifyCode(messageCode, vcode);
			if (rds.getState() == StateType.FAILT) {
				return rds;
			}
		}
		return UserDao.getInstance().editPhone(pset);
	}

	// 对验证码进行校验
	private DataSet verifyCode(String ccode, String vcode) {
		DataSet ds = new DataSet();
		if (StringUtils.isEmpty(ccode)) {
			ds.setState(StateType.FAILT);
			ds.setMessage("验证码已超时失效");
		} else {
			if (!ccode.equalsIgnoreCase(vcode)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误");
			}
		}
		return ds;
	}


	/**
	 * 获取用户列表
	 * 
	 * @author HuangXiaoke
	 */
	public DataSet getList(ParameterSet pset) {
		return UserDao.getInstance().getList(pset);
	}
	
	/**
	 * 登录验证成功，将用户信息写入换成，后台使用
	 * 
	 */
	public DataSet getUser(ParameterSet pset){
		return UserDao.getInstance().getUser(pset);
	}
	/**
	 * 修改用户密码
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet changePwd(ParameterSet pset) {
		UserInfo ui = this.getUserInfo(pset);
		if(ui != null) {
			pset.setParameter("id", ""+ui.getUid());
		}
		return UserDao.getInstance().changePwd(pset);
	}

	/**
	 * 修改用户密码
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet changePhone(ParameterSet pset) {
		return UserDao.getInstance().changePhone(pset);
	}

	/**
	 * 发送邮件信息
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet rebindValidMail(ParameterSet pset) {
		return UserDao.getInstance().rebindValidMail(pset);
	}

	/**
	 * 修改密保问题
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet changeAsk(ParameterSet pset) {
		UserInfo ui = this.getUserInfo(pset);
		if(ui != null) {
			pset.setParameter("id", ""+ui.getUid());
		}
		return UserDao.getInstance().changeAsk(pset);
	}

	/**
	 * 通过用户Id 获取邮箱
	 * 
	 * @author yanhao
	 */
	public DataSet getEmail(ParameterSet pset) {
		return UserDao.getInstance().getEmail(pset);
	}
	/**
	 * 获取邮箱后缀
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet getEmailLast(ParameterSet pset) {
		return UserDao.getInstance().getEmailLast(pset);
	}

	/**
	 * 获取校验邮件邮箱服务器名
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet getValidEmail(ParameterSet pset) {
		return UserDao.getInstance().getValidEmail(pset);
	}

	/**
	 * 重新发送邮件
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet resendMail(ParameterSet pset) {
		return UserDao.getInstance().resendMail(pset);
	}

	/**
	 * 注册时邮箱验证码
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet registerValidEmail(ParameterSet pset) {
		return UserDao.getInstance().registerValidEmail(pset);
	}

	/**
	 * 更新身份资料
	 * 
	 */
	public DataSet updateProfile(ParameterSet pset) {
		String uid = String.valueOf(this.getUserInfo(pset).getUid());
		pset.put("ID", uid);
		return UserDao.getInstance().updateProfile(pset);
	}

	/**
	 * 根据用户id读取用户信息
	 * 
	 * @author HeJindong
	 */
	public DataSet showUserInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String uid = String.valueOf(this.getUserInfo(pset).getUid());
			pset.put("ID@=", uid);
			ds = UserDao.getInstance().showUserInfo(pset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 发送短信
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet sendMessage(ParameterSet pset) {
		return UserDao.getInstance().sendMessage(pset);
	}
	
	public DataSet sendEditPhoneMessage(ParameterSet pset) {
		String userId = (String) pset.getParameter("USERID");
		DataSet users =  UserDao.getInstance().getUserById(userId);
		if (users != null && users.getTotal() > 0) {
			JSONArray array = users.getJAData();
			if (array != null) {
				JSONObject jo = (JSONObject) array.get(0);
				pset.setParameter("PHONE", jo.get("PHONE"));
			}
		}
		DataSet ds = sendMessage(pset);
		return ds;
	}
	

	/**
	 * 发送邮件
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet sendEmail(ParameterSet pset) {
		return UserDao.getInstance().sendEmail(pset);
	}

	/**
	 * 获取密码方式
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet getPwdSecInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String step = (String) pset.getParameter("step");
			if ("first".equals(step)) {
				String vcode = (String) pset.getParameter("verifyCode");
				String verifyCode = (String) CacheManager.get("VerifyCode", pset.getSessionId());
				DataSet rds = this.verifyCode(verifyCode, vcode);
				if (rds.getState() == StateType.FAILT) {
					return rds;
				}
			}
			ds = UserDao.getInstance().getPwdSecInfo(pset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 重置密码手机或邮箱验证码
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet resetPassCode(ParameterSet pset) {
		return UserDao.getInstance().resetPassCode(pset);
	}

	/**
	 * 重置密码手机或邮箱验证码
	 * 
	 * @author XiongZhiwen
	 */
	public DataSet resetPass(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String vcode = (String) pset.getParameter("vcode");
			String ccode = ""; // 系统缓存的验证码
			String account = (String) pset.getParameter("account");
			DataSet uds = UserDao.getInstance().verify(account);
			if (uds != null && uds.getTotal() > 0) {
				JSONObject jo = uds.getRecord(0);
				if (jo != null) {
					// 比较验证码
					String method = (String) pset.getParameter("method");
					if ("email".equals(method)) {
						String email = jo.getString("EMAIL");
						ccode = (String) CacheManager.get("EmailCode", email);
					} else if ("phone".equals(method)) {
						String phone = jo.getString("PHONE");
						ccode = (String) CacheManager.get("MessageCode", phone);
					} else {
						ccode = (String) CacheManager.get("VerifyCode", pset.getSessionId());
					}
					DataSet rds = this.verifyCode(ccode, vcode);
					if (rds.getState() == StateType.FAILT) {
						return rds;
					}

					// 对密保答案方式比较密保答案
					if ("ask".equals(method)) {
						String answer = jo.getString("ANSWER");
						String vanswer = (String) pset.getParameter("answer");
						if (StringUtils.isNotEmpty(answer)) {
							if (!answer.equals(vanswer)) {
								ds.setState(StateType.FAILT);
								ds.setMessage("密保答案输入错误！");
								return ds;
							}
						} else {
							ds.setState(StateType.FAILT);
							ds.setMessage("未读取到已设置的密保答案！");
							return ds;
						}
					}
				}
			}

			ds = UserDao.getInstance().resetPass(pset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 更新实名认证信息
	 * 
	 * @author HeJindong
	 */
	public DataSet updateRealName(ParameterSet pset) {
		String uid = String.valueOf(this.getUserInfo(pset).getUid());
		pset.put("ID", uid);
		return UserDao.getInstance().updateRealName(pset);
	}
	/**
	 * 实名认证
	 * 
	 * @author HeJindong
	 */
	public DataSet identification(ParameterSet pset) {
		String uid = String.valueOf(this.getUserInfo(pset).getUid());
		pset.put("ID", uid);
		return UserDao.getInstance().identification(pset);
	}

	/**
	 * 管理员重置用户密码，并发送短信及邮件
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet resetPassword(ParameterSet pset) {
		return UserDao.getInstance().resetPassword(pset);
	}

	public DataSet getHidePhone(ParameterSet pSet) {
		return UserDao.getInstance().getHidePhone(pSet);
	}

	public DataSet updatePhotoUri(ParameterSet pSet) {
		String uid = String.valueOf(this.getUserInfo(pSet).getUid());
		pSet.put("ID", uid);
		return UserDao.getInstance().updatePhotoUri(pSet);
	}
	
	public DataSet changeInuse(ParameterSet pset){
		return UserDao.getInstance().changeInuse(pset);
	}
	
	/**
	 * 验证用户登录信息，可以为用户名，身份证号码，手机号
	 */
	public DataSet verify(ParameterSet pset) {
		String account = (String)pset.getParameter("account");
		return UserDao.getInstance().verify(account);
	}
	
	/**
	 * 通过链接绑定新邮箱
	 */
	public DataSet bindEmail(ParameterSet pset) {
		String mailKey = (String)pset.getParameter("mailKey");
		return UserDao.getInstance().bindEmail(mailKey);
	}
	
	public DataSet identify(ParameterSet pset){
		return UserDao.getInstance().identify(pset);
	}
	/**
	 * 绑定邮箱或手机号
	 */
	public DataSet bindPhoneOrEmail(ParameterSet pSet){
		DataSet rds = new DataSet();
		String id = (String)pSet.getParameter("ID");
		if(!StringUtil.isNotEmpty(id)){
			id = String.valueOf(this.getUserInfo(pSet).getUid());
		}
		String type = (String)pSet.getParameter("TYPE");
		String link = (String)pSet.getParameter("link");
		String vcode = (String)pSet.getParameter("VCODE");
		if ("PHONE".equals(type)) {
			String messageCode = (String) CacheManager.get("MessageCode", link);
			rds = this.verifyCode(messageCode, vcode);
		} else if ("EMAIL".equals(type)) {
			String emailCode = (String) CacheManager.get("EmailCode", link);
			rds = this.verifyCode(emailCode, vcode);
		}
		if (rds.getState() == StateType.FAILT) {
			return rds;
		}else{
			return UserDao.getInstance().bindPhoneOrEmail(type,link,id);
		}
	}
	
	/**
	 * 登录验证短信发送
	 * 
	 */
	public DataSet sendMessage4Login(ParameterSet pset) {
		return UserDao.getInstance().sendMessage4Login(pset);
	}
	/**
	 * 获取用户信息
	 */
	public DataSet getListExt(ParameterSet pset) {
		String uid = String.valueOf(this.getUserInfo(pset).getUid());
		return UserDao.getInstance().getListExt(uid);
	}
	/**
	 * 重庆编辑
	 */
	public DataSet updateEmail(ParameterSet pset){
	
		return UserDao.getInstance().updateEmail(pset);
	}

	/**
	 * 通过用户Id 获取IS_EMS是否为EMS用户
	 * 
	 * @author yanhao
	 */
	public DataSet getISEMS(ParameterSet pset) {
		return UserDao.getInstance().getISEMS(pset);
	}
	
	/**
	 * 通过userId获取外网用户映射ID及用户类型，身份证等信息
	 * 
	 */
	public DataSet getMapAndUserInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String uid = String.valueOf(this.getUserInfo(pset).getUid());
			pset.put("uid", uid);
			ds = UserDao.getInstance().getMapAndUserInfo(pset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ds;
	}
}
