package app.icity.govservice;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;


public class GovProjectCmd extends BaseQueryCommand{ 
	
	/**
	 * 获取事项
	 * @param pSet
	 * @return
	 */
	
	public DataSet getMattersList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getMattersList2(pSet);
	}
	
	public DataSet getPartList(ParameterSet pSet){
			//接口调用模式
			return GovProjectDao.getInstance().getMattersList3(pSet);
	}
	//重庆市获取大项和普通项调用方式
	public DataSet getmaxItemAndGeneralList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getmaxItemAndGeneralList(pSet);
	}
	//重庆市根据大项id获取改大项下的小项
	public DataSet getminItemList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getminItemList(pSet);
	}
	public DataSet showOnlineNum(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().showOnlineNum(pSet);
	}
	/**
	 * 山东版本，通知公告同时显示事项发布的与网办发布的
	 * @param pSet
	 * @return
	 */
	public DataSet getNoticeList(ParameterSet pSet){		
		return GovProjectDao.getInstance().getNoticeList(pSet);
	}
	public DataSet getInitList(ParameterSet pSet){
			//接口调用模式
			return GovProjectDao.getInstance().getPowerList(pSet);
	}
	
	public DataSet getAllItemInfoByItemID(ParameterSet pSet){
		//接口调用模式 根据事项ID分类获取事项相关信息
		return GovProjectDao.getInstance().getAllItemInfoByItemID(pSet);
	}
	public DataSet onQueryProgress(ParameterSet pSet){
		return GovProjectDao.getInstance().onQueryProgress(pSet);
	}
	//舟山政务服务网，通知公告等小模块
	public DataSet getContentInfo(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentInfo(pSet);
	}
	public DataSet getQuestionInfo(ParameterSet pSet){
		return GovProjectDao.getInstance().getQuestionInfo(pSet);
	}
	//舟山市审招委、内部办公平台
	public DataSet getList(ParameterSet pSet){
		return GovProjectDao.getInstance().getList(pSet);
	}
	
	//外网、联审联办及投资项目通用模块
	public DataSet getListNew(ParameterSet pSet){
		return GovProjectDao.getInstance().getListNew(pSet);
	}
	public DataSet getzbCatalog(ParameterSet pSet) {
		return GovProjectDao.getInstance().getzbCatalog(pSet);
	}
	public DataSet search(ParameterSet pSet){
		return GovProjectDao.getInstance().search(pSet);
	}
	public DataSet getChannel(ParameterSet pSet){
		return GovProjectDao.getInstance().getChannel(pSet);
	}
	public DataSet getContentDetail(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentDetail(pSet);
	}
	public DataSet getContentDetailByName(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentDetailByName(pSet);
	}
	
	public DataSet getChannelName(ParameterSet pSet){
		return GovProjectDao.getInstance().getChannelName(pSet);
	}
	
	//分页获取事项目录
	public DataSet getFolderInfoByPage(ParameterSet pSet){
		return GovProjectDao.getInstance().getFolderInfoByPage(pSet);
	}
	public DataSet BusinessSearchQuery(ParameterSet pSet) {
		return GovProjectDao.getInstance().BusinessSearchQuery(pSet);
	}
	//办件公告分页获取数据（权力系统）
	public DataSet BusinessSearchQueryPower(ParameterSet pSet) {
		return GovProjectDao.getInstance().BusinessSearchQueryPower(pSet);
	}
	
	//聊城项目开始  按照事项查询具体信息
	public DataSet MatterInfo(ParameterSet pSet) {
		return GovProjectDao.getInstance().MatterInfo(pSet);
	}
	//按照部门查询
	public DataSet SelectByDept(ParameterSet pSet) {
		return GovProjectDao.getInstance().SelectByDept(pSet);
	}
	
	
	//聊城项目结束
	
	
	public DataSet getPoliciList(ParameterSet pSet) {
		return GovProjectDao.getInstance().getPoliciList(pSet);
	}
	
	public DataSet getContentInfoOfEventPublic(ParameterSet pSet) {
		return GovProjectDao.getInstance().getContentInfoOfEventPublic(pSet);
	}
	/**
	 * 获取结果材料
	 * @param pSet
	 * @return
	 */
	public DataSet getResultMaterial(ParameterSet pSet) {
		return GovProjectDao.getInstance().getResultMaterial(pSet);
	}
	/**
	 * 新余三单
	 * @param pSet
	 * @return
	 */
	public DataSet getContentList(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentList(pSet);
	}
	/**
	 * 新余三单 数量
	 * @param pSet
	 * @return
	 */
	public DataSet getContentListCount(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentListCount(pSet);
	}
	//子栏目
	public DataSet getChannelList(ParameterSet pSet){
		return GovProjectDao.getInstance().getChannelList(pSet);
	}
	/**
	 * 根据内容ID获取评论内容
	 * @param pSet
	 * @return
	 */
	public DataSet getCommentById(ParameterSet pSet){
		return GovProjectDao.getInstance().getCommentById(pSet);
	}
	/**
	 * 保存评论
	 * @param pSet
	 * @return
	 */
	public DataSet saveComment(ParameterSet pSet){
		
		UserInfo user = this.getUserInfo(pSet);
		
		if(null!=user){
			pSet.put("CREATOR", user.getUid()+"");
			pSet.put("CREATOR_NAME", user.getUserName());
		}else{
			pSet.put("CREATOR", "");
			pSet.put("CREATOR_NAME", "");
		}
		
		return GovProjectDao.getInstance().saveComment(pSet);
	}
	
	/**
	 * 获取信息公示列表
	 * @param pSet
	 * @return
	 */
	public DataSet getPublicityList(ParameterSet pSet) {
		return GovProjectDao.getInstance().getPublicityList(pSet);
	}
	
	/**
	 * 获取信息公示明细
	 * @param pSet
	 * @return
	 */
	public DataSet getPublicityDetail(ParameterSet pSet) {
		return GovProjectDao.getInstance().getPublicityDetail(pSet);
	}
	
	/**
	 * 获取上下游链条
	 * @param pSet
	 * @return
	 */
	public DataSet getItemsByItemId(ParameterSet pSet) {
		return GovProjectDao.getInstance().getItemsByItemId(pSet);
	}
	/**
	 * 查询办件进度(列表)   手机验证码验证
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		String vcode = (String) pSet.getParameter("verifyCode");
		if(StringUtils.isNotEmpty(vcode)){
			String verifyCode = (String) CacheManager.get("MessageCode", pSet.getParameter("phone"));
			DataSet ds = new DataSet();
			if (StringUtils.isEmpty(verifyCode)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码已超时失效");
				return ds;
			} else if (!vcode.equalsIgnoreCase(verifyCode)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误");
				return ds;
			}
		}
		return GovProjectDao.getInstance().getBusinessSearchQuery(pSet);
	}
	/**
	 * 查询咨询投诉(列表)   手机验证码验证
	 * @param pSet
	 * @return
	 */
	public DataSet guestBookListByPage(ParameterSet pSet) {
		String vcode = (String) pSet.getParameter("verifyCode");
		if(StringUtils.isNotEmpty(vcode)){
			String verifyCode = (String) CacheManager.get("VerifyCode", pSet.getSessionId());
			DataSet ds = new DataSet();
			if (StringUtils.isEmpty(verifyCode)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码已超时失效");
				return ds;
			} else if (!vcode.equalsIgnoreCase(verifyCode)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误");
				return ds;
			}
		}
		return GovProjectDao.getInstance().guestBookListByPage(pSet);
	}
	/**
	 * 通过registernum查询业务具体办理进度
	 * @param pSet
	 * @return
	 */
	public DataSet BusinessProgressQueryByRegisternum(ParameterSet pSet) {
		return GovProjectDao.getInstance().BusinessProgressQueryByRegisternum(pSet);
	}
	/**
	 * 查询发布内容的列表
	 * @param pSet
	 * @return
	 */
	public DataSet getPubContentList(ParameterSet pSet) {
		return GovProjectDao.getInstance().getPubContentList(pSet);
	}
	
	/**
	 * 廉政格言
	 * @param pSet
	 * @return
	 */
	public DataSet getList4Lzgy(ParameterSet pSet){
		return GovProjectDao.getInstance().getList4Lzgy(pSet);
	}
	
	/**
	 * 三级联办地址接口
	 * @param pSet
	 * @return
	 */
	public DataSet getLianBanUrl(ParameterSet pSet){
		DataSet ds = new DataSet(); 
		String WebServiceUrl = SecurityConfig.getString("LianBanUrl");
		String NAMESPACE = "http://auth.webservice.jis.hanweb.com/";
		String METHOD = "getsburl";
		JSONObject retVal;
		Service serivce = new Service();
		String value = null;
		try {
			Call call = (Call) serivce.createCall();
			call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
			call.setOperationName(new QName(NAMESPACE, METHOD));
			String itemname = (String) pSet.getParameter("itemname");
			String itemcode = (String) pSet.getParameter("itemcode");
			Object[] param = new Object[] {itemcode, itemname};
			value = (String) call.invoke(param);
			System.out.println("========"+WebServiceUrl+"||"+itemname+"|"+itemcode);
			System.out.println("========"+value);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		ds.setRawData(value);
		
		return ds;
	}
	/**
	 * 根据申办流水号获取通知书
	 * @param pSet
	 * @return
	 */
	public DataSet getPrintHistory(ParameterSet pSet){
		return GovProjectDao.getInstance().getPrintHistory(pSet);
	}
	
	/**
	 * 根据材料空表或样表查询（福州  暂时不要分页。。）
	 * @param pSet
	 * @return
	 */
	public DataSet getMaterialList(ParameterSet pSet){
		return GovProjectDao.getInstance().getMaterialList(pSet);
	}
	/**
	 * 漳州 获取事项列表  方法
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			JSONObject JsonObj = JSONObject.fromObject(GovProjectDao.getInstance().getItemListByPage(pSet));
			JSONArray DeptItemList =  JsonObj.getJSONArray("pageList");
			ds.setRawData(DeptItemList);
			ds.setTotal(Integer.valueOf(JsonObj.getString("totlaRow")));
			ds.setState(StateType.SUCCESS);
		}catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}
	/**
	 * 根据单位获取有电子材料的事项
	 * @param pSet
	 * @return
	 */
	public DataSet getHaveSampleItem(ParameterSet pSet){
		return GovProjectDao.getInstance().getHaveSampleItem(pSet);
	}
}