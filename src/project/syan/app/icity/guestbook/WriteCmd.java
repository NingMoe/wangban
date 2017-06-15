package syan.app.icity.guestbook;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;


public class WriteCmd extends BaseQueryCommand {
	
	private static Logger log = LoggerFactory.getLogger(BaseQueryCommand.class);
	
	//聊城开始
	public DataSet getList2(ParameterSet pSet){
		return WriteDao.getInstance().getList2(pSet);
	}
	public DataSet getSurpervise(ParameterSet pSet){
		return WriteDao.getInstance().getSurpervise(pSet);
	}
	//根据部门查询我的咨询
	public DataSet getListByDept(ParameterSet pSet){
		return WriteDao.getInstance().getListByDept(pSet);
	}
	//根据id查询具体信息
	public DataSet getMatterInfo(ParameterSet pSet){
		return WriteDao.getInstance().getMatterInfo(pSet);
	}
	
	public DataSet insertInfo(ParameterSet pSet){
		return WriteDao.getInstance().insertInfo(pSet);
	}
	
	public DataSet insertInfo2(ParameterSet pSet){
		return WriteDao.getInstance().insertInfo2(pSet);
	}
	//聊城投诉
	public DataSet insertInfo3(ParameterSet pSet){
		UserInfo u = this.getUserInfo(pSet);
		pSet.put("user_id", u.getUid()+"");
		return WriteDao.getInstance().insertInfo3(pSet);
	}
	//聊城结束
	
	public DataSet getList(ParameterSet pSet){
		return WriteDao.getInstance().getList(pSet);
	}
	
	public DataSet insert(ParameterSet pSet){
		DataSet ds = new DataSet();
		String verify = (String)CacheManager.get("VerifyCode",pSet.getSessionId());	
		String VerifyCode = (String)pSet.get("VerifyCode");
		if(VerifyCode != null && !"".equals(VerifyCode)){
			if(!VerifyCode.equalsIgnoreCase(verify)){
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误,请重新输入！");
				return ds;
			}
		}
		
		String pageModel = SecurityConfig.getString("PAGE_MODE");
		if("zs_city".equals(pageModel)){
			sendMessage(pSet);
		}
		
		UserInfo user = this.getUserInfo(pSet);
		if(null!=user){
			return WriteDao.getInstance().insert(pSet,user);
		}else{
			return WriteDao.getInstance().insert(pSet);
		}
	}
	
	public DataSet getCount(ParameterSet pSet){
		return WriteDao.getInstance().getCount(pSet);
	}
	
	public DataSet setSatisfy(ParameterSet pSet){
		return WriteDao.getInstance().setSatisfy(pSet);
	}
	
	public DataSet setUseful(ParameterSet pSet){
		return WriteDao.getInstance().setUseful(pSet);
	}
	
	/**
	 * @author xiongzhw@inspur.com
	 * @param pSet 参数
	 * @return 热门历史咨询信息
	 */
	public DataSet getHotConsult(ParameterSet pSet){
		return WriteDao.getInstance().getHotConsult(pSet);
	}
	/*
	 * 非上班时间网上预约 pan
	 * 
	 */
	public DataSet wsyyinsert(ParameterSet pSet){
		UserInfo user = this.getUserInfo(pSet);
		
		String pageModel = SecurityConfig.getString("PAGE_MODE");
		if("zs_city".equals(pageModel)){
			sendMessageToUser(pSet);
		}
		
		return WriteDao.getInstance().wsyyinsert(pSet,user);
	}
	
	//短信发送
	public void sendMessage(ParameterSet pSet){
		
		String type = (String)pSet.getParameter("TYPE");
		String message = "";
		
		String phonestr = WriteDao.getInstance().getPhoneStr(pSet);
		
		//意见建议
		if("10".equals(type) || "11".equals(type)){
			message = SecurityConfig.getString("message_yjjy");
			message = message.replace("{name}", (String)pSet.getParameter("USERNAME"));
		}
		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url")+"sendMessage");		
		Map<String, String> data=new HashMap<String, String>();
		data.put("phoneList",phonestr);
		data.put("mesConent",message);
		
		try {
			RestUtil.postData(url, data);
		} catch (Exception e) {
			log.error("短信发送失败！");
		} 
	}
	
	//获取 咨询、投诉列表
	public DataSet getGuestBookList(ParameterSet pSet) {
		return WriteDao.getInstance().getGuestBookList(pSet);
	}
	
	public DataSet getGuestBookList_huifu(ParameterSet pSet) {
		return WriteDao.getInstance().getGuestBookList_huifu(pSet);
	}
	
	//非上班时间预约短信发送
	public void sendMessageToUser(ParameterSet pSet){
		
		String message = "";
		
		//非上班时间预约 type 21
		pSet.setParameter("TYPE", "21");
		pSet.setParameter("organ_code", (String)pSet.getParameter("CODE"));
		
		String phonestr = WriteDao.getInstance().getPhoneStr(pSet);
		
		message = SecurityConfig.getString("message_fsbsj");
		message = message.replace("{name}", (String)pSet.getParameter("YYRNAME"))
					.replace("{date}", (String)pSet.getParameter("YYDAY"))
					.replace("{time}", (String)pSet.getParameter("YYTIME"))
					.replace("{content}", (String)pSet.getParameter("YYCONTENT"));
		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("message_url")+"sendMessage");		
		Map<String, String> data=new HashMap<String, String>();
		data.put("phoneList",phonestr);
		data.put("mesConent",message);
		
		try {
			RestUtil.postData(url, data);
		} catch (Exception e) {
			log.error("短信发送失败！");
		} 
	}
	/*
	 * 淄博插入投诉评价
	 */
	public DataSet insertStarLevel(ParameterSet pSet) {
		return WriteDao.getInstance().insertStarLevel(pSet);
	}
}

