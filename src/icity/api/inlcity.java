package api;

import java.io.IOException;
import java.text.ParseException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import api.impl.ChannelImpl;
import api.impl.IcityImpl;
import api.impl.InlcityImpl;
import api.impl.ServiceImpl;
import api.impl.WechatImpl;
import app.icity.interactive.satisfaction.SatisfactionEvaluationDao;
import app.icity.search.SearchCmd;

import com.icore.util.Tools;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.Command;
import com.inspur.util.SecurityConfig;
/**
 * 爱城市网标准接口
 * 非爱城市网接口不要加到这里
 * @author lihongyun
 */
@SuppressWarnings("deprecation")
@RestType(name = "api.inlcity", descript = "爱城市网业务接口")
public class inlcity extends BaseQueryCommand {
	@SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(inlcity.class);
	private static api.tools.Tools mytools = new api.tools.Tools();
	
	/**
	 * 获取UUID
	 * @return
	 */
	public String getUUID32(){
		return Tools.getUUID32();
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
	
	//region 获取事项列表（分页）getMattersList
	/**
	 * 爱城市网获取事项列表（分页）
	 * @throws ParseException 
	 */
	public DataSet getMattersList(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = InlcityImpl.getInstance().getMattersList(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 根据事项itemCode或者itemId获取事项的基本信息getPermission
	/**
	 * 根据事项itemCode或者itemId获取事项的基本信息
	 * @throws ParseException 
	 */
	public DataSet getPermission(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = InlcityImpl.getInstance().getPermissionAll(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 添加事项收藏 
	/**
	 * 添加收藏记录
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet addFavorite(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			if(!pSet.containsKey("ucid")||"".equals((String)pSet.get("ucid"))){
				ds = mytools.errorInfo(0,"用户未登陆，或缺少用户信息");	
			}else{
				ds = IcityImpl.getInstance().addFavorite(pSet);
			}
		}
		return ds;
	}
	//endregion

	//region 获取事项收藏列表getBusinessFavoriteList
	/**
	 * 获取事项收藏列表
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getFavoriteList(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			if(!pSet.containsKey("ucid")||"".equals((String)pSet.get("ucid"))){
				ds = mytools.errorInfo(0,"用户未登陆，或缺少用户信息");	
			}else{
				ds = IcityImpl.getInstance().getBusinessFavoriteList(pSet);
			}
		}
		return ds;
	}
	//endregion

	//region 删除事项收藏记录delFavorite
	/**
	 * 删除事项收藏记录
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet delFavorite(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().updateStatus(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 在线办理submitSP
	/**
	 * 爱城市网在线办理
	 * @throws ParseException 
	 */
	public DataSet submitSP(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = WechatImpl.getInstance().submitSP(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 附件上传uploadify
	/**
	 * 爱城市网附件上传
	 * @throws ParseException 
	 */
	public DataSet uploadify(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().uploadify(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 查询办件进度(列表)getBusinessSearchQuery
	/**
	 * 爱城市网查询办件进度(列表)
	 * @throws ParseException 
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = WechatImpl.getInstance().getBusinessSearchQuery(pSet);
		}
		return ds;
	}	
	//endregion
	
	//region 根据流水号查询办件信息getAllBusinessInfo
	public DataSet getAllBusinessInfo(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			Command command = new Command("app.icity.project.ProjectIndexCmd");		  	
			command.setParameter("SBLSH",(String)pSet.get("receiveNum"));
			ds = command.execute("getBusinessInfoFromApprovalBySblsh");
		}
		return ds;		
	}
	//endregion
	
	//region 获取办件状态getBusinessState
	/**
	 * 爱城市网获取办件状态
	 * @throws ParseException 
	 */
	public DataSet getBusinessState(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().getBusinessState(pSet);
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
	 * @throws ParseException 
	 */
	public DataSet makeAppointment(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().makeAppointment(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 获取咨询投诉列表iCityGuestBookListByPage
	/**
	 * 爱城市网获取咨询投诉列表 type(2咨询，3投诉)
	 * @throws ParseException 
	 */
	public DataSet iCityGuestBookListByPage(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = InlcityImpl.getInstance().iCityGuestBookListByPage(pSet);
		}
		return ds;
	}
	//endregion 

	//region 提交咨询投诉iCityAddGuestBook
	/**
	 * 爱城市网提交咨询投诉 type(2咨询，3投诉)
	 * @throws ParseException 
	 */
	public DataSet iCityAddGuestBook(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = InlcityImpl.getInstance().iCityAddGuestBook(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 获取咨询投诉详情getGuestBookDetail
	/**
	 * 获取咨询投诉详情getGuestBookDetail
	 * @throws ParseException 
	 */
	public DataSet getGuestBookDetail(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = WechatImpl.getInstance().getGuestBookDetail(pSet);
		}
		return ds;
	}
	//endregion
		
	//region 部门区划列表getDeptList
	/**
	 * 部门区划列表
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public JSONObject getDeptList(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		JSONObject m_ds = new JSONObject();
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			m_ds = ServiceImpl.getInstance().getDeptList(pSet);
		}else{
			m_ds.put("code","300");
			m_ds.put("error",ds.getMessage());
			m_ds.put("errorcode",ds.getJOData().getString("code"));
		}
		return m_ds;
	}
	//endregion
	
	//region 主题
	/**
	 * 某区划下的主题
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getPowerCatalog(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ServiceImpl.getInstance().getPowerCatalog(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 个人用户注册接口registerOfPer
	/**
	 * 个人用户注册接口
	 * @param pSet
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	public DataSet registerOfPer(ParameterSet pSet) throws IOException, Exception {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("pass_per"));// 密码
			pSet.put("pass_per", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().registerOfPer(pSet);
		}
		return ds;
	}
	//endregion
	
	//region个人用户信息编辑updateUserInfoOfPer
	/**
	 * 个人用户信息编辑 *
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet updateUserInfoOfPer(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){			
			ds = ServiceImpl.getInstance().updateUserInfoOfPer(pSet);
		}
		return ds;
	}
	//endregion
	
	//region企业用户注册接口registerOfOrg
	/**
	 * 企业用户注册接口
	 * @param pSet
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	public DataSet registerOfOrg(ParameterSet pSet) throws IOException, Exception {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("pass_org"));// 密码
			pSet.put("pass_org", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().registerOfOrg(pSet);
		}
		return ds;
	}
	//endregion

	//region企业用户信息编辑updateUserInfoOfOrg
	/**
	 * 企业用户信息编辑 *
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet updateUserInfoOfOrg(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ServiceImpl.getInstance().updateUserInfoOfOrg(pSet);
		}
		return ds;
	}
	//endregion
	
	//region通过手机号初始化密码modifyPasswordByPhone
	/**
	 * 通过手机号初始化密码 *
	 * password=&phone=
	 * @throws ParseException 
	 */
	public DataSet modifyPasswordByPhone(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("password"));// 密码
			pSet.put("password", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().modifyPasswordByPhone(pSet);
		}
		return ds;
	}
	//endregion
	
	//region根据用户名密码修改密码modifyPassword
	/**
	 * 根据用户名密码修改密码
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet modifyPassword(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("oldpwd"));// 密码
			pSet.put("oldpwd", mytools.decrypt(m_pSet));
			m_pSet.put("data", (String) pSet.getParameter("newpwd"));// 密码
			pSet.put("newpwd", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().modifyPassword(pSet);
		}
		return ds;
	}
	//endregion
	
	//region根据用户名密码登录login
	/**
	 * 根据用户名/手机号 密码登录
	 * @throws ParseException 
	 */
	public DataSet login(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){			
			//解密密码
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("data", (String) pSet.getParameter("password"));// 密码
			pSet.put("password", mytools.decrypt(m_pSet));
			ds = ServiceImpl.getInstance().login(pSet);
		}
		return ds;
	}
	//endregion
	
	//region获取用户信息 getUserInfoByUserId
	/**
	 * 获取用户信息 
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getUserInfoByUserId(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = WechatImpl.getInstance().getUserInfoByUserId(pSet);
		}
		return ds;
	}
	//endregion

	//region生成验证码generateVerifyCode
	/**
	 * 生成验证码
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet generateVerifyCode(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ServiceImpl.getInstance().generateVerifyCode(pSet);
		}
		return ds;
	}
	//endregion
	
	//region验证验证码verifyCode
	/**
	 * 验证验证码
	 * @param pSet
	 * @return
	 * @throws ParseException 
	 */
	public DataSet verifyCode(ParameterSet pSet) throws ParseException {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ServiceImpl.getInstance().verifyCode(pSet);
		}
		return ds;
	}
	//endregion	

	//region 获取根节点栏目列表getRootChannel
	/**
	 * 获取根节点栏目列表
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getRootChannel(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getRootChannel(pSet);	
		}
		return ds;
	}
	//endregion
	
	//region根据栏目名称获取子栏目列表getChildChannelByName
	/**
	 * 根据栏目名称获取子栏目列表
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getChildChannelByName(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getChildChannelByName(pSet);	
		}
		return ds;
	}
	//endregion
	
	//region根据栏目id获取子栏目列表getChildChannelById
	/**
	 * 根据栏目id获取子栏目列表
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getChildChannelById(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getChildChannelById(pSet);	
		}
		return ds;
	}
	//endregion
	
	//region根据栏目id获取内容列表getContentListByCid
	/**
	 * 根据栏目id获取内容列表
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getContentListByCid(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getContentListByCid(pSet);	
		}
		return ds;
	}
	//endregion
	
	//region根据内容id获取内容详细getContentById
	/**
	 * 根据内容id获取内容详细
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getContentById(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getContentById(pSet);		
		}
		return ds;
	}
	//endregion
	
	//region根据栏目名称获取内容列表getContentListByName
	/**
	 * 根据栏目名称获取内容列表
	 * @param pset
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getContentListByName(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getContentListByName(pSet);		
		}
		return ds;
	}
	//endregion
	
	//region获取内容列表getContentListByPage
	/**
	 * 获取内容列表
	 * @param pset{page,limit,cname,cid,region_code,startTime,endTime,deptId,name}
	 * @return
	 * @throws ParseException 
	 */
	public DataSet getContentListByPage(ParameterSet pSet) throws ParseException{
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ChannelImpl.getInstance().getContentListByPage(pSet);			
		}
		return ds;
	}
	//endregion
	
	//region app版本更新 appVersion
	public DataSet appVersion(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = InlcityImpl.getInstance().appVersion(pSet);			
		}
		return ds;
	}
	//endregion
	
	//region 获取站点getWebSite
	public DataSet getWebSite(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = ServiceImpl.getInstance().getWebSite(pSet);			
		}
		return ds;
	}
	//endregion
	
	//region 获取网点列表getPubHallListByPage
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
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().getPubHallListByPage(pSet);			
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
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().getPubHallFavoriteByPage(pSet);	
		}
		return ds;
	}
	//endregion
	
	//region 收藏网点addPubHallFavorite
	/**
	 * 收藏网点
	 * @param pSet
	 * name 网点名册
	 * remark 备注
	 * hallid 网点ID
	 * @return
	 */
	public DataSet addPubHallFavorite(ParameterSet pSet) {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().addPubHallFavorite(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 取消收藏网点delPubHallFavorite
	/**
	 * 取消收藏网点
	 * @param pSet	 
	 * hallid 网点ID
	 * @return
	 */
	public DataSet delPubHallFavorite(ParameterSet pSet) {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = IcityImpl.getInstance().delPubHallFavorite(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 智能问答jrobotSearch
	/**
	 * 智能问答
	 * @param pSet
	 * limit page key
	 * @return
	 */
	public DataSet jrobotSearch(ParameterSet pSet) {
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			if(!pSet.containsKey("ucid")){
				pSet.put("ucid", "");
			}
			ds = IcityImpl.getInstance().jrobotSearch(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 搜索引擎接口getSearchIndex
	/**
	 * 搜索引擎接口；
	 * 
	 * */
	public DataSet getSearchIndex(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = SearchCmd.getInstance().getIndex(pSet);		
		}
		return ds;
	}
	//endregion

	//region 辦件評價
	/**
	 * SBLSH,SQRMC,EVALUATE_CONTENT(评价内容)，CreatorId（评价人id），STAR_LEVEL（星级），
	 * ITEM_CODE（事项code），ITEM_ID，ITEM_NAME，DEPT_ID事项主管部门组织机构代码，DEPT_NAME，REGION_ID，
	 * APPLY_SUBJECT申报主题，EVALUATE_TYPE-1，TIME_STAR_LEVEL，QUALITY_STAR_LEVEL，MAJOR_STAR_LEVEL
	 * insertEvaluation?SBLSH=1111111111&SQRMC=34567&EVALUATE_CONTENT=1111111111&CreatorId=1111111111&STAR_LEVEL=1&ITEM_CODE=1111111111&ITEM_ID=1111111111&ITEM_NAME=1111111111&DEPT_ID=1111111111&DEPT_NAME=1111111111&REGION_ID=1111111111&APPLY_SUBJECT=1111111111&EVALUATE_TYPE=1
	 */
	public DataSet insertEvaluation(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			pSet.put("EVALUATE_TYPE", "1");//办件评价
			ds = SatisfactionEvaluationDao.getInstance().insertEvaluation(pSet);
		}
		return ds;
	}
	//endregion
	
	//region 获取某个办件的评价信息
	/**
	 * 获取某个办件的评价信息
	 * param：sblsh
	 * */
	public DataSet queryStarLevel(ParameterSet pSet){
		DataSet ds = mytools.validate(pSet);
		if(ds.getState()==1&&"100".equals(ds.getJOData().getString("code"))){
			ds = SatisfactionEvaluationDao.getInstance().queryStarLevel(pSet);
		}
		return ds;
	}
	//endregion
}
