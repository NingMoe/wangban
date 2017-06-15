package api;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jdo.conf.Database;

import api.impl.ChannelImpl;
import api.impl.IcityImpl;
import api.impl.InlcityImpl;
import api.impl.ServiceImpl;
import api.impl.WechatImpl;
import app.icity.ServiceCmd;
import app.icity.guestbook.WriteDao;
import app.icity.onlineapply.ApplyDao;
import app.icity.sync.UploadUtil;

import com.commnetsoft.proxy.SsoClient;
import com.commnetsoft.proxy.model.CallResult;
import com.commnetsoft.proxy.model.UserInfo;
import com.commnetsoft.proxy.model.ValidationResult;
import com.icore.http.util.HttpUtil;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.timevale.SecureUtils;

import core.util.CommonUtils;
import core.util.HttpClientUtil;
/**
 * 基础接口，对外统一提供的接口，如无特殊要求都用这个接口
 * 普通用户登录注册等都放在这里
 * @author lihongyun
 */
@RestType(name = "api.service", descript = "投诉咨询相关接口")
public class service extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(service.class);
	private static api.tools.Tools mytools = new api.tools.Tools();
	
	/**
	 * 获取UUID
	 * @return
	 */
	public String getUUID32(){
		return Tools.getUUID32();
	}
	
	//region 个人用户注册接口registerOfPer
	/**
	 * 个人用户注册接口
	 * @param pSet
	 * @return
	 */
	public DataSet registerOfPer(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("pass_per"));// 密码
			pSet.put("pass_per", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().registerOfPer(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion

	//region 个人用户信息编辑updateUserInfoOfPer
	/**
	 * 个人用户信息编辑 *
	 * @param pSet
	 * @return
	 */
	public DataSet updateUserInfoOfPer(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ServiceImpl.getInstance().updateUserInfoOfPer(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region企业用户注册接口registerOfOrg
	/**
	 * 企业用户注册接口
	 * @param pSet
	 * @return
	 */
	public DataSet registerOfOrg(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("pass_org"));// 密码
			pSet.put("pass_org", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().registerOfOrg(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region企业用户信息编辑updateUserInfoOfOrg
	/**
	 * 企业用户信息编辑 *
	 * @param pSet
	 * @return
	 */
	public DataSet updateUserInfoOfOrg(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ServiceImpl.getInstance().updateUserInfoOfOrg(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region通过手机号初始化密码modifyPasswordByPhone
	/**
	 * 通过手机号初始化密码 *
	 * password=&phone=
	 */
	public DataSet modifyPasswordByPhone(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("password"));// 密码
			pSet.put("password", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().modifyPasswordByPhone(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	/**
	 * 根据用户名密码修改密码
	 * @param pSet
	 * @return
	 */
	public DataSet modifyPassword(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("oldpwd"));// 密码
			pSet.put("oldpwd", mytools.decrypt(m_pSet));
			m_pSet.put("data", (String) pSet.getParameter("newpwd"));// 密码
			pSet.put("newpwd", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().modifyPassword(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;		
	}
	//endregion
	
	//region根据用户名密码登录login
	/**
	 * 根据用户名密码登录
	 */
	public DataSet login(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("password"));// 密码
			pSet.put("password", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().login(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
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
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = WechatImpl.getInstance().getUserInfoByUserId(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region获取个人用户信息 getPerUserInfoByCardId	
	/**
	 * 获取个人用户信息 
	 * @param pSet
	 * @return
	 */
	public DataSet getPerUserInfoByCardId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = WechatImpl.getInstance().getPerUserInfoByCardId(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region获取企业用户信息  getOrgUserInfoByCardId	
	/**
	 * 获取企业用户信息 
	 * @param pSet
	 * @return
	 */
	public DataSet getOrgUserInfoByCardId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = WechatImpl.getInstance().getOrgUserInfoByCardId(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region 获取事项列表（分页）getMattersList
	/**
	 * 获取事项列表（分页）
	 */
	public DataSet getMattersList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = InlcityImpl.getInstance().getMattersList(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region 根据事项itemCode或者itemId获取事项的基本信息getPermission
	/**
	 * 根据事项itemCode或者itemId获取事项的基本信息
	 */
	public DataSet getPermission(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = InlcityImpl.getInstance().getPermissionAll(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region 添加事项收藏 
	/**
	 * 添加收藏记录
	 * @param pSet
	 * @return
	 */
	public DataSet addFavorite(ParameterSet pSet) {	
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			if(!pSet.containsKey("ucid")||"".equals((String)pSet.get("ucid"))){
				ds = mytools.errorInfo(3,"用户未登陆，或缺少用户信息");	
			}else{
				ds = IcityImpl.getInstance().addFavorite(pSet);
			}			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion

	//region 获取事项收藏列表getBusinessFavoriteList
	/**
	 * 获取事项收藏列表
	 * @param pSet
	 * @return
	 */
	public DataSet getFavoriteList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			if(!pSet.containsKey("ucid")||"".equals((String)pSet.get("ucid"))){
				ds = mytools.errorInfo(3,"用户未登陆，或缺少用户信息");	
			}else{
				ds = IcityImpl.getInstance().getBusinessFavoriteList(pSet);
			}			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion

	//region 删除事项收藏记录delFavorite
	/**
	 * 删除事项收藏记录
	 * @param pSet
	 * @return
	 */
	public DataSet delFavorite(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){			
			ds = IcityImpl.getInstance().updateStatus(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
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
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = IcityImpl.getInstance().getAttachList(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
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
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = WechatImpl.getInstance().getBusinessSearchQuery(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}	
	//endregion
	
	//region 根据流水号查询办件信息getAllBusinessInfo
	public DataSet getAllBusinessInfo(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			Command command = new Command("app.icity.project.ProjectIndexCmd");		  	
			command.setParameter("SBLSH",(String)pSet.get("receiveNum"));
			ds = command.execute("getBusinessInfoFromApprovalBySblsh");
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;			
	}
	//endregion
	
	//region办件列表getDisplayListByPage
	public DataSet getDisplayListByPage(ParameterSet pSet) {
		System.out.print("api.iserver/getDisplayListByPage");
		System.out.print("pSet:"+pSet);
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ServiceImpl.getInstance().getDisplayListByPage(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region根据流水号密码查询办件详情getDisplayDetail
	public DataSet getBusinessProgressQueryByPassword(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ServiceImpl.getInstance().getBusinessProgressQueryByPassword(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region在线办理接口 submitSP
	/**
	 * 在线办理接口 
	 * @param pSet
	 * @return
	 */
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = WechatImpl.getInstance().submitSP(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region附件上传 uploadify
	/**
	 * 附件上传 
	 * @param pSet
	 * attachment = [{"fileName":"文件名","fileType":"扩展名","fileContent":"文件流","fullName":"全名"}]
	 * ucid=
	 * @return
	 */
	public DataSet uploadify(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = IcityImpl.getInstance().uploadify(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region生成验证码generateVerifyCode
	/**
	 * 生成验证码
	 * @param pSet
	 * @return
	 */
	public DataSet generateVerifyCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			String access_token = (String)pSet.get("param_icore_session_id");
			pSet.put("access_token", access_token);
			ds = ServiceImpl.getInstance().generateVerifyCode(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region验证验证码verifyCode
	/**
	 * 验证验证码
	 * @param pSet
	 * @return
	 */
	public DataSet verifyCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			String access_token = (String)pSet.get("param_icore_session_id");
			pSet.put("access_token", access_token);
			ds = ServiceImpl.getInstance().verifyCode(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");	
		}
		return ds;
	}
	//endregion
	
	//region部门区划列表getDeptList
	/**
	 * 部门区划列表
	 * @param pSet
	 * @return
	 */
	public DataSet getDeptList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds.setRawData(ServiceImpl.getInstance().getDeptList(pSet));
			ds.setState(StateType.SUCCESS);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
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
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ServiceImpl.getInstance().getPowerCatalog(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region 获取根节点栏目列表getRootChannel
	/**
	 * 获取根节点栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getRootChannel(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getRootChannel(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region根据栏目名称获取子栏目列表getChildChannelByName
	/**
	 * 根据栏目名称获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelByName(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getChildChannelByName(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region根据栏目id获取子栏目列表getChildChannelById
	/**
	 * 根据栏目id获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelById(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getChildChannelById(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region根据栏目id获取内容列表getContentListByCid
	/**
	 * 根据栏目id获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByCid(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getContentListByCid(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region根据内容id获取内容详细getContentById
	/**
	 * 根据内容id获取内容详细
	 * @param pset
	 * @return
	 */
	public DataSet getContentById(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getContentById(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region根据栏目名称获取内容列表getContentListByName
	/**
	 * 根据栏目名称获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByName(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getContentListByName(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region获取内容列表getContentListByPage
	/**
	 * 获取内容列表
	 * @param pset{page,limit,cname,cid,region_code,startTime,endTime,deptId,name}
	 * @return
	 */
	public DataSet getContentListByPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ChannelImpl.getInstance().getContentListByPage(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
	//endregion
	
	//region 大厅预约makeAppointment
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
		return IcityImpl.getInstance().makeAppointment(pSet);
	}
	//endregion
	

	//region 获取咨询投诉列表iCityGuestBookListByPage
	/**
	 * 爱城市网获取咨询投诉列表 type(2咨询，3投诉)
	 */
	public DataSet iCityGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getRawData().toString())){
			ds = InlcityImpl.getInstance().iCityGuestBookListByPage(pSet);
		}
		return ds;
	}
	//endregion 

	//region 提交咨询投诉iCityAddGuestBook
	/**
	 * 提交咨询投诉 type(2咨询，3投诉)
	 */
	public DataSet iCityAddGuestBook(ParameterSet pSet) {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getRawData().toString())){
			ds = InlcityImpl.getInstance().iCityAddGuestBook(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 获取咨询投诉详情getGuestBookDetail
	/**
	 * 获取咨询投诉详情getGuestBookDetail
	 */
	public DataSet getGuestBookDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = WechatImpl.getInstance().getGuestBookDetail(pSet);
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");			
		}
		return ds;
	}
	//endregion
	
	/**
	 * 山东省个性化 根据大汉统一身份认证的uuid获取用户的办件列表
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
	
	
	public DataSet BusinessSearchQuery(ParameterSet pSet){
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		if(mytools.checkInfo(access)){
			ds = ServiceImpl.getInstance().BusinessSearchQuery(pSet);			
		}else{
			ds = mytools.errorInfo(2,access+"非法访问！");
		}
		return ds;
	}
}
