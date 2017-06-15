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
import api.impl.ServiceImpl;
import api.impl.WechatImpl;
import api.impl.XngdappImpl;
import app.icity.ServiceCmd;
import app.icity.guestbook.WriteDao;
import app.icity.onlineapply.ApplyDao;

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

@RestType(name = "api.xngdapp", descript = "西宁广电app接口")
public class xngdapp extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(xngdapp.class);	
	/**
	 * 西宁广电app调用网办接口获取事项信息，办件信息，咨询投诉信息，并能够提交咨询投诉，以及网上办理
	 * 再次过程中，当需要用户登录的时候，西宁广电app将自己的用户信息传递过来，
	 * 暂时不处理----如果有需要 再在网办接口对用户信息进行验证
	 * 如果在库中已经存在，则继续，若不存在则将用户信息记录到用户表中并
	 */
	//region分页已发布事项列表getItemListByPage
	/**
	 * 分页已发布事项列表
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = WechatImpl.getInstance().getItemListByPage(pSet);
		return ds;
	}
	//endregion
	
	//region获取事项列表（分页）getMattersList
	/**
	 * 获取事项列表（分页）	 * 
	 * @param pSet
	 *            add 属性 IS_COLLECTION 是否收藏
	 * @return 
	 */
	public DataSet getMattersList(ParameterSet pSet){
		DataSet ds = new DataSet();		
		ds = IcityImpl.getInstance().getMattersList(pSet);
		return ds;
	}
	//endregion
	
	//region根据事项ID获取所有事项相关信息getAllItemInfoByItemID
	/**
	 * 根据事项ID获取所有事项相关信息
	 * @param pSet
	 * @return
	 */
	public DataSet getAllItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = ServiceImpl.getInstance().getAllItemInfoByItemID(pSet);
		return ds;
	}
	//endregion
	
	//region网上申报、在线办理接口 submitSP
	/**
	 * 在线办理接口 
	 * @param pSet
	 * @return
	 */
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject userinfo = JSONObject.fromObject(pSet.get("userinfo"));
		ds = WechatImpl.getInstance().submitSP(pSet);
		return ds;
	}
	//endregion
	
	
	//region获取咨询投诉列表获取咨询投诉列表
	/**
	 * 获取咨询投诉列表
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookListByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = XngdappImpl.getInstance().getGuestBookListByPage(pSet);
		return ds;
	}
	//endregion
	
	//region获取咨询投诉详情getGuestBookDetail
	/**
	 * 获取咨询投诉详情
	 * @param pSet
	 * @return
	 */
	public DataSet getGuestBookDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = WechatImpl.getInstance().getGuestBookDetail(pSet);
		return ds;
	}
	//endregion
	
	//region网提交咨询投诉addGuestBook
	/**
	 * 网提交咨询投诉 type(2咨询，3投诉)，ly（ 0咨询(微信)、1投诉(微信)、 2（来源于爱城市网）、3（来源于手机app）、4（西宁广电app）） STATUS CHAR(1) Y
	 * '0' 状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理 where =
	 * "dept_name,dept_id,content"; params = ['部门','ID','内容'];
	 */
	public DataSet addGuestBook(ParameterSet pSet) {		
		DataSet ds = new DataSet();	
		JSONObject userinfo = JSONObject.fromObject(pSet.get("userinfo"));
		pSet.put("LY", "4");
		pSet.put("ucid", userinfo.getString("id"));
		pSet.put("phone", userinfo.getString("phone"));
		ds = IcityImpl.getInstance().addGuestBook(pSet);	
		return ds;
	}
	//endregion
	
	
	//region查询服务（进度查询），getBusinessSearchQuery
	/**
	 * 查询办件进度(列表)
	 */
	public DataSet getBusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = WechatImpl.getInstance().getBusinessSearchQuery(pSet);
		return ds;
	}
	//endregion	
	
	//region 根据流水号密码查询办件信息getAllBusinessInfoByPassword
	public DataSet getAllBusinessInfoByPassword(ParameterSet pSet){
		DataSet ds = new DataSet();
		Command command = new Command("app.icity.project.ProjectIndexCmd");		  	
		command.setParameter("sblsh",(String)pSet.get("receiveNum"));
		command.setParameter("spsxcxmm",(String)pSet.get("password"));
		ds = command.execute("BusinessProgressQueryByPassword");		
		return ds;			
	}
	//endregion
	
	//region获取主题getPowerCatalog
	/**
	 * 获取主题
	 * @param pSet
	 * region_id
	 * class_type
	 * @return
	 */
	public DataSet getPowerCatalog(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = IcityImpl.getInstance().getPowerCatalog(pSet);		
		return ds;		
	}
	//endregion
	
	//region部门区划列表getDeptList
	/**
	 * 部门区划列表
	 * @param pset
	 * @return
	 */
	public JSONObject getDeptList(ParameterSet pSet) {
		JSONObject ds = new JSONObject();	
		ds = ServiceImpl.getInstance().getDeptList(pSet);
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
		//org.eclipse.jetty.util.log.Log.info(" 附件上传 参数："+pSet);
		return IcityImpl.getInstance().uploadify(pSet);
	}
	//endregion
}
