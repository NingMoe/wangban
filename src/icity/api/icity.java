package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import api.impl.ChannelImpl;
import api.impl.IcityImpl;
import api.impl.ServiceImpl;
import api.impl.WechatImpl;
import app.icity.interactive.satisfaction.SatisfactionEvaluationDao;
import app.icity.search.SearchCmd;

import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
/**
 * 山东省各地市政务app接口
 * @author lihongyun
 */
@SuppressWarnings("deprecation")
@RestType(name = "api.icity", descript = "业务接口")
public class icity extends BaseQueryCommand {
	@SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(icity.class);
	private static final String TOKEN_SCOPE_GUEST = "guest";	
	
	/**
	 * 过滤
	 * @param pSet
	 * @return
	 */
	public DataSet authController(ParameterSet pSet) {
		return IcityImpl.getInstance().authController(pSet);		
	}

	/**
	 * 检测是否过期
	 * @param pSet
	 * @return
	 */
	public DataSet checkAccessToken(ParameterSet pSet) {
		return IcityImpl.getInstance().checkAccessToken(pSet);		
	}
	/**
	 * 威海根据手机号检测用户
	 * @param pSet
	 * @return
	 */
	public DataSet checkPhone(ParameterSet pSet) {
		return IcityImpl.getInstance().checkPhone(pSet);		
	}
	/**
	 * 添加或者更新设备信息，及添加或更新token
	 * deviceToken=ceshi&pushToken=&os=&model=&osVersion=&appVersion=
	 * @param pSet
	 * @return
	 */
	public DataSet makeAccessToken(ParameterSet pSet) {
		return IcityImpl.getInstance().makeAccessToken(pSet);
	}

	/**
	 * 获取咨询投诉详情
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookDetail(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			ds = IcityImpl.getInstance().getGuestBookDetail(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 获取咨询投诉列表 type(2咨询，3投诉)，ly（来源2为爱城市，0为微信咨询1为微信投诉,3手机app） STATUS CHAR(1) Y
	 * '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理 where = "ly = ? and status=?";
	 * params = ['3','1'];
	 */
	public DataSet getGuestBookListByPage(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().getGuestBookListByPage(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 网提交咨询投诉 type(2咨询，3投诉)，ly（来源2为爱城市，0为微信咨询1为微信投诉,3为手机app） STATUS CHAR(1) Y
	 * '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理 where =
	 * "dept_name,dept_id,content"; params = ['部门','ID','内容'];
	 */
	public DataSet addGuestBook(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().addGuestBook(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 爱城市网查询办件进度(列表)
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			pSet.put("phone", ds_auth.getJOData().getJSONObject("userinfo").getString("PHONE"));
			ds = IcityImpl.getInstance().getBusinessSearchQuery(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 获取办件办理业务信息
	 */
	public DataSet getBusinessInfo(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = WechatImpl.getInstance().getBusinessInfo(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 根据用户名,手机号，邮箱，密码登录
	 */
	public DataSet login(ParameterSet pSet) {		
		return IcityImpl.getInstance().login(pSet);
	}

	/**
	 * 退出
	 */
	public DataSet logout(ParameterSet pSet) {		
		return IcityImpl.getInstance().logout(pSet);
	}

	/**
	 * 微信个人用户注册接口
	 * username=username&phone=1871111111&pass_per=123&code_per=123&name_per
	 * =姓名&code_type_per=10&address_per=地址
	 */
	public DataSet registerOfPer(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = ServiceImpl.getInstance().registerOfPer(pSet);		
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 个人用户信息编辑 *
	 */
	public DataSet updateUserInfoOfPer(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("username", ds_auth.getJOData().getJSONObject("userinfo").getString("ACCOUNT"));
			pSet.put("ly", "1");
			ds = ServiceImpl.getInstance().updateUserInfoOfPer(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 企业用户注册接口 username// 用户名 phone// 手机号 pass_org// 密码 code_org// 证件号码
	 * name_org// 姓名 code_type_org// 证件类型 address_org// 地址 org_org// 机构代码
	 * org_all_org// 机构名称 legal_person_org// 机构法人名称 code_type_legal_person_org//
	 * 机构法人证件类型 code_legal_person_org// 机构法人编码
	 */
	public DataSet registerOfOrg(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = ServiceImpl.getInstance().registerOfOrg(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 企业用户信息编辑 *
	 */
	public DataSet updateUserInfoOfOrg(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("username", ds_auth.getJOData().getJSONObject("userinfo").getString("ACCOUNT"));
			pSet.put("ly", "1");
			ds = ServiceImpl.getInstance().updateUserInfoOfOrg(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 通过手机号初始化密码 *
	 * password=&phone=
	 */
	public DataSet modifyPasswordByPhone(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = ServiceImpl.getInstance().modifyPasswordByPhone(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 根据用户名密码修改密码
	 * @param pSet
	 * @return
	 */
	public DataSet modifyPassword(ParameterSet pSet){
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("username", ds_auth.getJOData().getJSONObject("userinfo").getString("ACCOUNT"));
			pSet.put("ly", "1");
			ds = ServiceImpl.getInstance().modifyPassword(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 获取用户信息
	 */
	public DataSet getUserInfoByToken(ParameterSet pSet){
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			pSet.put("ly", "1");
			ds = ServiceImpl.getInstance().getUserInfoByToken(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 在线办理接口
	 * @param pSet
	 * @return
	 */
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			pSet.put("userName", ds_auth.getJOData().getJSONObject("userinfo").getString("NAME"));
			ds = IcityImpl.getInstance().submitSP(pSet);			
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 部门区划列表
	 * @param pset
	 * @return
	 */
	public JSONObject getDeptList(ParameterSet pSet) {
		JSONObject obj = new JSONObject();
		DataSet ds = authController(pSet);
		if (ds.getState() == StateType.SUCCESS) {
			obj = IcityImpl.getInstance().getDeptList(pSet);
		} else {
			obj.put("code", "300");
			obj.put("message", ds.getMessage());
		}
		return obj;		
	}

	/**
	 * 三级站点
	 * @param pset
	 * @return
	 */
	public JSONObject getWebSite(ParameterSet pSet) {
		JSONObject obj = new JSONObject();
		DataSet ds = authController(pSet);
		if (ds.getState() == StateType.SUCCESS) {
			obj = IcityImpl.getInstance().getWebSite(pSet);
		} else {
			obj.put("code", "300");
			obj.put("message", ds.getMessage());
		}
		return obj;
	}
	/**
	 * 根据栏目id获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByCid(ParameterSet pSet){
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = ChannelImpl.getInstance().getContentListByCid(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;		
	}
	/**
	 * 根据栏目名称获取内容列表
	 * @param pSet
	 * key 查询
	 * @return
	 */
	public DataSet getContentListByChannelName(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().getContentListByChannelName(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 根据内容id获取内容详情
	 * @param pSet
	 * @return
	 */
	public DataSet getContentDetailById(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().getContentDetailById(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 获取收藏列表
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessFavoriteList(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().getBusinessFavoriteList(pSet);			
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 添加收藏记录
	 * @param pSet
	 * @return
	 */
	public DataSet addFaveriot(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().addFavorite(pSet);	
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 删除收藏
	 * @param pSet
	 * @return
	 */
	public DataSet updateStatus(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			ds = IcityImpl.getInstance().updateStatus(pSet);			
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 从附件表获取当前用户下的材料列表
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachList(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().getAttachList(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 根据事项id获取事项的基本信息
	 * @param pSet
	 * add 属性  IS_COLLECTION 是否收藏 1已收藏0未收藏
	 * @return
	 */
	public DataSet getPermission(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				pSet.put("ucid", "");
			}else{
				pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			}			
			ds = IcityImpl.getInstance().getPermission(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	
	/**
	 * 根据事项code获取事项的基本信息
	 * @param pSet   itemCode
	 * add 属性  IS_COLLECTION 是否收藏 1已收藏0未收藏
	 * @return
	 */
	public DataSet getPermissionAll(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				pSet.put("ucid", "");
			}else{
				pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			}			
			ds = IcityImpl.getInstance().getPermissionAll(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 获取事项列表（分页）
	 * @param pSet
	 * add 属性  IS_COLLECTION 是否收藏
	 * @return
	 */
	public DataSet getMattersList(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				pSet.put("ucid", "");
			}else{
				pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			}			
			ds = IcityImpl.getInstance().getMattersList(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 生成验证码
	 * @param pSet
	 * @return
	 */
	public DataSet generateVerifyCode(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().generateVerifyCode(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 验证验证码
	 * @param pSet
	 * @return
	 */
	public DataSet verifyCode(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().verifyCode(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}

	/**
	 * 获取当前用户评论列表
	 * @param pSet
	 * @return
	 */
	public DataSet getStarLevelEvaluation(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().getStarLevelEvaluation(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 满意度评价统计
	 * @param pSet
	 * @return
	 */
	public DataSet getEvaluationStat(ParameterSet pSet) {		
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {			
			ds = IcityImpl.getInstance().getEvaluationStat(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 获取事项办事指南评论列表
	 * @param pSet
	 * @return
	 */
	public DataSet getStarLevelEvaluationByItem(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {			
			ds = IcityImpl.getInstance().getStarLevelEvaluationByItem(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 插入办事指南评价 
	 * @param pSet
	 *            EVALUATE_TYPE 评价类型 1办件评价 2部门评价3办事指南评价 ITEM_ID 事项id
	 *            EVALUATE_CONTENT 评价内容 STAR_LEVEL 星级 1-5 ITEM_NAME 事项名称
	 *            DEPT_NAME 部门名称 REGION_ID 行政区划代码
	 * @return
	 */
	public DataSet insertGuideEvaluation(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().insertGuideEvaluation(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	//region 辦件評價
	/**
	 * SBLSH,SQRMC,EVALUATE_CONTENT(评价内容)，CreatorId（评价人id），STAR_LEVEL（星级），
	 * ITEM_CODE（事项code），ITEM_ID，ITEM_NAME，DEPT_ID事项主管部门组织机构代码，DEPT_NAME，REGION_ID，
	 * APPLY_SUBJECT申报主题，EVALUATE_TYPE-1，TIME_STAR_LEVEL，QUALITY_STAR_LEVEL，MAJOR_STAR_LEVEL
	 * insertEvaluation?SBLSH=1111111111&SQRMC=34567&EVALUATE_CONTENT=1111111111&CreatorId=1111111111&STAR_LEVEL=1&ITEM_CODE=1111111111&ITEM_ID=1111111111&ITEM_NAME=1111111111&DEPT_ID=1111111111&DEPT_NAME=1111111111&REGION_ID=1111111111&APPLY_SUBJECT=1111111111&EVALUATE_TYPE=1
	 */
	public DataSet insertEvaluation(ParameterSet pSet){
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("CreatorId", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			pSet.put("EVALUATE_TYPE", "1");//办件评价
			ds = SatisfactionEvaluationDao.getInstance().insertEvaluation(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	//endregion
	/**
	 * 获取事项主题列表
	 * @param pSet
	 * @return
	 */
	public DataSet getPowerCatalog(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().getPowerCatalog(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 获取事项主题列表 分为个人和法人
	 * @param pSet
	 * @return
	 */
	public DataSet getPowerCatalogByClassType(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().getPowerCatalogByClassType(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 智能问答
	 * @param pSet
	 * limit page key
	 * @return
	 */
	public DataSet jrobotSearch(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				pSet.put("ucid", "");
			}else{
				pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			}	
			ds = IcityImpl.getInstance().jrobotSearch(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 大厅预约
	 * @param pSet{"map":"{'Service':'','':''}"}
	 **预约部门列表
		 * JSONObject map = new JSONObject();
		 * map.put("Service", "Queue.GetBizDepts");
		 * map.put("Reserve","true");	
		 *
	 **预约部门业务列表
		 * JSONObject map = new JSONObject();
		 *	map.put("Service", "Reserve.ListBusiness");
		 *	map.put("DeptName","部门名称");
		 *
	 **获取业务已预约数
		 * JSONObject map = new JSONObject();
		 *	map.put("Service", "Reserve.GetRecordCount");
		 *	map.put("YYDate","预约日期");		
		 *	map.put("BizID","业务ID");	
		 *
	 **填写完信息后进行预约
		 * JSONObject map = new JSONObject();
		 *	map.put("Service", "Reserve.AddRecord");
		 *	map.put("BizID","业务ID");
		 *	map.put("Date","日期");
		 *	map.put("Time","时间");
		 *	map.put("IDCard","身份证号码");
		 *	map.put("Phone","手机号");
		 *
		* @return
	 */
	public DataSet makeAppointment(ParameterSet pSet){		
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			ds = IcityImpl.getInstance().makeAppointment(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	
	/**
	 * 附件上传 
	 * @param pSet
	 * [{"fileName":"文件名","fileType":"扩展名","fileContent":"文件流","fullName":"全名"}]
	 * @return
	 */
	public DataSet uploadify(ParameterSet pSet){
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid", ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().uploadify(pSet);
		}else{
			ds.setRawData(new JSONArray());
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	
	/**
	 * 获取网点列表
	 * @param pSet
	 * REGION;--region
	 * WORK_INTERVAL--work_interval
	 * NAME--name
	 * CATAGORY--catagory
	 * ID--id
	 * @return
	 */
	public DataSet getPubHallListByPage(ParameterSet pSet) {		
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {			
			ds = IcityImpl.getInstance().getPubHallListByPage(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 获取收藏网点列表
	 * @param pSet
	 * @return
	 */
	public DataSet getPubHallFavoriteByPage(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid",ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().getPubHallFavoriteByPage(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 收藏网点
	 * @param pSet
	 * name 网点名册
	 * remark 备注
	 * hallid 网点ID
	 * @return
	 */
	public DataSet addPubHallFavorite(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid",ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().addPubHallFavorite(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 取消收藏网点
	 * @param pSet	 
	 * hallid 网点ID
	 * @return
	 */
	public DataSet delPubHallFavorite(ParameterSet pSet) {
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {
			if (TOKEN_SCOPE_GUEST.equals(ds_auth.getJOData().getString("scope"))) {
				ds.setState((byte)2);
				ds.setMessage("未登录！");
				return ds;
			}
			pSet.put("ucid",ds_auth.getJOData().getJSONObject("userinfo").getString("ID"));
			ds = IcityImpl.getInstance().delPubHallFavorite(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 搜索引擎接口；
	 * 
	 * */
	public DataSet getSearchIndex(ParameterSet pset){
		DataSet ds;
		ds = SearchCmd.getInstance().getIndex(pset);
		return ds;
	}
	
	/**
	 * 获取所有中介分类
	 * @param pSet	 * 
	 * @return
	 */
	public DataSet getMedclassify(ParameterSet pSet) {		
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {			
			ds = IcityImpl.getInstance().getMedclassify(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 获取中介列表
	 * @param pSet	 * 
	 * @return
	 */
	public DataSet getIntermedairyList(ParameterSet pSet) {		
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {			
			ds = IcityImpl.getInstance().getIntermedairyList(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}
	/**
	 * 中介详情接口
	 * @param pSet	 * 
	 * @return
	 */
	public DataSet getIntermedairyInfo(ParameterSet pSet) {		
		DataSet ds_auth = authController(pSet);
		DataSet ds = new DataSet();
		if (ds_auth.getState() == StateType.SUCCESS) {			
			ds = IcityImpl.getInstance().getIntermedairyInfo(pSet);
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage(ds_auth.getMessage());
		}
		return ds;
	}	
	/**
	 * 短信发送接口（西宁市）
	 * 西宁市手机端是把微信版本打包成APP的，里面都是html，获取不到手机端的版本号，手机型号和设备标识所以不传AccessToken；
	 */
	public DataSet sendMessage(ParameterSet pset){
		DataSet ds = new DataSet();
		String phone = (String)pset.getParameter("phone");//发送短信的手机号码
		String content = (String)pset.getParameter("content");//短信内容
		String channel = (String)pset.getParameter("channel");//短信类型
		if(StringUtil.isEmpty(phone)||StringUtil.isEmpty(content)||StringUtil.isEmpty(channel)){
			ds.setState(StateType.FAILT);
			ds.setMessage("必传字段不能为空！");
			return ds;
		}
		boolean flag = IcityImpl.getInstance().sendMessage(phone,content,channel);
		if(flag){
			ds.setState(StateType.SUCCESS);
			ds.setMessage("发送成功");
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("发送失败");
		}
		return ds;
	}
	
	public DataSet test(ParameterSet pSet) throws IOException{
		StringBuffer result = new StringBuffer();
		BufferedReader bufferedReader = null;
		try {
			String url = "http://10.47.0.149:8080/icity/c/api.wechat/register_org?username=yida&&phone=18765865242&&email=1554300947@163.com&&pass_org=cd8ce7d543ea7e2f9005ccb51a03a9bc&&code_org=622421199112181930&&name_org=" +
			java.net.URLEncoder.encode("肖鹏","utf-8")+"&&code_type_org=10&&address_org=" +
			java.net.URLEncoder.encode("济南市","utf-8")+"&&org_org=yd123&&org_all_org=" +
			java.net.URLEncoder.encode("益达 集团","utf-8")+"&&legal_person_org=" +
			java.net.URLEncoder.encode("肖鹏","utf-8")+"&&code_type_legal_person_org=10&&code_legal_person_org=622421199112181930&&ly=2";
			// 创建URL
			URL httpUrl = new URL(url);
			// 创建连接
			URLConnection connection = httpUrl.openConnection();			
			connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("connection", "keep-alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
			connection.connect();
			// 接受连接返回参数		
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bufferedReader!=null){
				bufferedReader.close();
			}
		}
		DataSet ds = new DataSet();
		ds.setRawData(result.toString());
		return ds;
	}
}
