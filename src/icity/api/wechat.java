package api;

import java.text.ParseException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import api.impl.ChannelImpl;
import api.impl.IcityImpl;
import api.impl.ServiceImpl;
import api.impl.WechatImpl;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;

@RestType(name = "api.wechat", descript = "投诉咨询相关接口")
public class wechat extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(item.class);
	/**
	 * 微信登录舟山统一认证平台 gr
	 * @param pSet
	 * @return
	 */
	public DataSet wechatLoginGr(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().wechatLoginGr(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().wechatLoginGr(pSet);
		}
		return ds;	
	}

	/**
	 * 微信登录舟山统一认证平台 fr
	 * @param pSet
	 * @return
	 */
	public DataSet wechatLoginFr(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().wechatLoginFr(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().wechatLoginFr(pSet);
		}
		return ds;	
	}
	/**
	 * 山东省根据用户名,手机号，邮箱，密码登录
	 */
	public DataSet loginSd(ParameterSet pSet) {		
		return WechatImpl.getInstance().loginSd(pSet);
	}
	/**
	 * 获取微信咨询列表
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getGuestBookList(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getGuestBookList(pSet);
		}
		return ds;
	}

	/**
	 * 获取咨询投诉详情
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getGuestBookDetail(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getGuestBookDetail(pSet);
		}
		return ds;
	}

	/**
	 * 保存咨询、投诉
	 * @param pSet
	 * @return
	 */
	public DataSet saveGuestBook(ParameterSet pSet) throws ParseException{
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().saveGuestBook(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().saveGuestBook(pSet);
		}
		return ds;
	}

	/**
	 * 咨询、投诉 回复 status CHAR(1) Y '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理
	 * replayer 回复部门 dealResult 回复内容 id type VARCHAR2(10 CHAR) Y
	 * 信件类别，分为咨询-2、投诉-3、意见建议-10、纠错-11、求助-12、其他
	 */
	public DataSet updateGuestBook(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().updateGuestBook(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().updateGuestBook(pSet);
		}
		return ds;
	}

	/**
	 * 获取微信咨询列表
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookListByOpenid(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getGuestBookListByOpenid(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getGuestBookListByOpenid(pSet);
		}
		return ds;
	}

	/**
	 * 根据用户名密码查询此用户，如果有此用户，存一条数据到wechat_user表（外网用户与微信用户的绑定关系表）
	 */
	public DataSet GetUserDetailLogin(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				//微信端登录使用大汉统一身份认证
				if("1".equals(SecurityConfig.getString("UniteIdentifyFlag"))){
					ds = WechatImpl.getInstance().GetUserUniteIdentifyDetail(pSet);
				}else{
					ds = WechatImpl.getInstance().GetUserDetailLogin(pSet);
				}
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			//微信端登录使用大汉统一身份认证
			if("1".equals(SecurityConfig.getString("UniteIdentifyFlag"))){
				ds = WechatImpl.getInstance().GetUserUniteIdentifyDetail(pSet);
			}else{
				ds = WechatImpl.getInstance().GetUserDetailLogin(pSet);
			}
		}
		return ds;
	}

	/**
	 * 根据用户名密码查询此用户，如果有此用户，存一条数据到wechat_user表（外网用户与微信用户的绑定关系表）
	 */
	public DataSet GetUserDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().GetUserDetail(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().GetUserDetail(pSet);
		}
		return ds;
	}

	/**
	 * 根据openid查询wechat_user表，是否有此数据，判断是否绑定了
	 * @param pSet
	 * @return
	 */
	public DataSet ExistOpenid(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().ExistOpenid(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().ExistOpenid(pSet);
		}
		return ds;
	}

	/**
	 * 根据openid查询wechat_user表，查询UserId
	 * @param pSet
	 * @return
	 */
	public DataSet GetUserId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().GetUserId(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().GetUserId(pSet);
		}
		return ds;
	}

	/**
	 * 获取满意度评价查询结果
	 * @param pSet
	 * @return
	 */
	public DataSet getPjResult(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getPjResult(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getPjResult(pSet);
		}
		return ds;
	}

	/**
	 * 确认评价
	 * @param pSet
	 * @return
	 */
	public DataSet insert_pl(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().insert_pl(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().insert_pl(pSet);
		}
		return ds;
	}

	/**
	 * 获取微信咨询列表（手机审批客户端，分页）
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getGuestBookListByPage(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getGuestBookListByPage(pSet);
		}
		return ds;
	}

	/**
	 * 短信通知（舟山微信申报和大厅一体机申报调用）
	 * @param pSet
	 * @return
	 */
	public DataSet sendMessageForHallAndWeChat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().sendMessageForHallAndWeChat(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().sendMessageForHallAndWeChat(pSet);
		}
		return ds;
	}

	/**
	 * 爱城市网获取咨询投诉列表 type(2咨询，3投诉)，ly（来源2为爱城市，1为微信咨询2为微信投诉 3为手机app） STATUS CHAR(1) Y '0'
	 * 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理
	 */
	public DataSet iCityGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().iCityGuestBookListByPage(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().iCityGuestBookListByPage(pSet);
		}
		return ds;
	}

	/**
	 * 爱城市网提交咨询投诉 type(2咨询，3投诉)，ly（来源2为爱城市，0为微信咨询1为微信投诉） STATUS CHAR(1) Y '0'
	 * 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理 
	 */
	public DataSet iCityAddGuestBook(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().iCityAddGuestBook(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().iCityAddGuestBook(pSet);
		}
		return ds;
	}

	/**
	 * 舟山，给微信和大厅提供在线申报后短信提示接口
	 * 
	 * @param pSet
	 *            {organ_code:"",itemCode:"",TYPE:"20",userName:"",userName:""}[
	 *            外网申报type 20]
	 * @param ds
	 */
	public DataSet onlineapplySendMessage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().onlineapplySendMessage(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().onlineapplySendMessage(pSet);
		}
		return ds;
	}

	/**
	 * 爱城市网查询办件进度(列表)
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBusinessSearchQuery(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBusinessSearchQuery(pSet);
		}
		return ds;
	}

	/**
	 * 微信个人用户注册接口
	 */
	public DataSet register_per(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		pSet.put("ly", "2");
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().register_per(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().register_per(pSet);
		}
		return ds;
	}

	/**
	 * 微信企业用户注册接口
	 */
	public DataSet register_org(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		pSet.put("ly", "2");
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().register_org(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().register_org(pSet);
		}
		return ds;
	}

	/**
	 * 在线办理接口 
	 * @param pSet
	 * @return
	 */
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().submitSP(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().submitSP(pSet);
		}
		return ds;
	}
	/**
	 * 部门区划列表
	 * @param pset
	 * @return
	 */
	public JSONObject getDeptList(ParameterSet pSet) {
		JSONObject ds = new JSONObject();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ServiceImpl.getInstance().getDeptList(pSet);
			}else{
				ds.put("code","300");
				ds.put("error",access+"非法访问！");
			}
		}else{
			ds = ServiceImpl.getInstance().getDeptList(pSet);
		}
		return ds;
	}
	/**
	 * 根据单位获取已发布事项列表
	 * @param pSet
	 * @return
	 */
	public DataSet getItemList(ParameterSet pSet) {	
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getItemList(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getItemList(pSet);
		}
		return ds;
	}
	/**
	 * 根据单位分页已发布事项列表
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getItemListByPage(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getItemListByPage(pSet);
		}
		return ds;
	}
	/**
	 * 根据事项ID获取所有事项相关信息
	 * @param pSet
	 * @return
	 */
	public DataSet getAllItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ServiceImpl.getInstance().getAllItemInfoByItemID(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ServiceImpl.getInstance().getAllItemInfoByItemID(pSet);
		}
		return ds;
	}
	/**
	 * 获取主题
	 * @param pSet
	 * region_id
	 * class_type
	 * @return
	 */
	public DataSet getPowerCatalog(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = IcityImpl.getInstance().getPowerCatalog(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = IcityImpl.getInstance().getPowerCatalog(pSet);
		}
		return ds;		
	}
	/**
	 * 获取主题 昆明盘龙 分类主题获取
	 * @param pSet
	 * region_id
	 * class_type
	 * @return
	 */
	public DataSet getPowerCatalogKM(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = IcityImpl.getInstance().getPowerCatalogKM(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = IcityImpl.getInstance().getPowerCatalogKM(pSet);
		}
		return ds;		
	}
	/**
	 * 获取事项列表（分页）
	 * 
	 * @param pSet
	 *            add 属性 IS_COLLECTION 是否收藏
	 * @return 手机app和爱城市、微信调用
	 */
	public DataSet getMattersList(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = IcityImpl.getInstance().getMattersList(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = IcityImpl.getInstance().getMattersList(pSet);
		}
		return ds;
	}
	/**
	 * 根据材料code获取事项材料详情
	 * @param pSet
	 * @return
	 */
	public DataSet getMaterialDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getMaterialDetail(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getMaterialDetail(pSet);
		}
		return ds;
	}
	/**
	 * 根据事项ID分类获取事项相关信息
	 * @param pSet
	 * @return
	 */
	public DataSet getItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getItemInfoByItemID(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getItemInfoByItemID(pSet);
		}
		return ds;
	}
	/**
	 * 获取变更前事项
	 * @param pSet
	 * @return
	 */
	public DataSet getChangeItem(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getChangeItem(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getChangeItem(pSet);
		}
		return ds;
	}
	/**
	 * 获取外网、大厅收件、便民收件办理业务信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBusinessInfo(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBusinessInfo(pSet);
		}
		return ds;
	}
	/**
	 * 获取所有办理业务信息
	 * @param pSet
	 * @return
	 */
	public DataSet getAllBusinessInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getAllBusinessInfo(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getAllBusinessInfo(pSet);
		}
		return ds;		
	}
	/**
	 * 根据事项ID获取表单信息
	 * @param pSet
	 * @return
	 */
	public DataSet getFormInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getFormInfo(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getFormInfo(pSet);
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
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = IcityImpl.getInstance().makeAppointment(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = IcityImpl.getInstance().makeAppointment(pSet);
		}
		return ds;
	}
	/**
	 * 根据uid获取用户办件列表
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexList(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBusinessIndexList(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBusinessIndexList(pSet);
		}
		return ds;
	}
	/**
	 * 从附件表获取当前用户下的材料列表	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachListByPhone(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getAttachListByPhone(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getAttachListByPhone(pSet);
		}
		return ds;
	}
	/**
	 * 热点事项 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectBusyInfoHotStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getProjectBusyInfoHotStat(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getProjectBusyInfoHotStat(pSet);
		}
		return ds;
	}
	
	/**
	 * 获取用户信息 
	 * @param pSet
	 * @return
	 */
	public DataSet getUserInfoByUserId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getUserInfoByUserId(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getUserInfoByUserId(pSet);
		}
		return ds;
	}
	/**
	 * 获取根节点栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getRootChannel(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getRootChannel(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getRootChannel(pSet);
		}
		return ds;
	}
	/**
	 * 根据栏目名称获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelByName(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getChildChannelByName(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getChildChannelByName(pSet);
		}
		return ds;
	}
	/**
	 * 根据栏目id获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelById(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getChildChannelById(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getChildChannelById(pSet);
		}
		return ds;
	}
	/**
	 * 根据栏目id获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByCid(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getContentListByCid(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getContentListByCid(pSet);
		}
		return ds;
	}
	/**
	 * 根据内容id获取内容详细
	 * @param pset
	 * @return
	 */
	public DataSet getContentById(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getContentById(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getContentById(pSet);
		}
		return ds;
	}
	/**
	 * 根据栏目名称获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByName(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getContentListByName(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getContentListByName(pSet);
		}
		return ds;
	}
	
	public DataSet getContentListByPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = ChannelImpl.getInstance().getContentListByPage(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = ChannelImpl.getInstance().getContentListByPage(pSet);
		}
		return ds;
	}
	/**
	 * 查询当前 指导区划 指定部门下的 科室
	 * @param pset
	 * @return
	 */
	public DataSet getItemAgentList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getItemAgentList(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getItemAgentList(pSet);
		}
		return ds;
	}
	/**
	 * 漳州获取用户id 并落地用户信息
	 */
	public DataSet getUserId(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getUserId(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getUserId(pSet);
		}
		return ds;
	}
	
	
	/**
	 * 根据身份证号码获取微信办件记录
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexByICard(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBusinessIndexByICard(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBusinessIndexByICard(pSet);
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
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBqbzCodeBySblsh(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBqbzCodeBySblsh(pSet);
		}
		return ds;
	}
	
	/**
	 * 根据流水号获取办件记录
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexBySblsh(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBusinessIndexBySblsh(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBusinessIndexBySblsh(pSet);
		}
		return ds;
	}
	
	public Object getNBusinessIndex(ParameterSet pSet){
		Object ds = new Object();
		ds = WechatImpl.getInstance().getNBusinessIndex(pSet);
		return ds;
	}
	
	/**
	 * 根据大汉统一身份认证的uuid获取用户的办件列表
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessListByUuid(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = WechatImpl.getInstance().getBusinessListByUuid(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = WechatImpl.getInstance().getBusinessListByUuid(pSet);
		}
		return ds;
	}

}
