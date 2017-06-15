package yt.app.icity.guestbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.guestbook.SmsDao;
import app.uc.UserDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import core.util.CommonUtils;

public class WriteDao extends BaseJdbcDao {
	public WriteDao() {
		this.setDataSourceName("icityDataSource");
	}

	protected static Log _log = LogFactory.getLog(WriteDao.class);

	public static WriteDao getInstance() {
		return DaoFactory.getDao(WriteDao.class.getName());
	}

	public DataSet insert(ParameterSet pSet) {
		DataSet ds = new DataSet();
		
		String url_gettimeout = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl")+"/web/c/gettimeout");
		String timeout="3";
		try {
			JSONObject obj;		
			obj =  JSONObject.fromObject(RestUtil.getData(url_gettimeout));	
			timeout = obj.getString("data");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Date now = new Date(); 
		String sdate=(new SimpleDateFormat("yyyy-MM-dd")).format(now);
		String url_getWorkDate = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl")+"/web/c/getWorkDate?startTime="+sdate+"&timeout="+timeout);
		try {
			JSONObject obj;		
			obj =  JSONObject.fromObject(RestUtil.getData(url_getWorkDate));	
			sdate = obj.getString("data");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		Date startTime = new Date();
		try {
			startTime = sdf.parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		String sql = "INSERT INTO GUESTBOOK "
				+ "(ID, WARN, CHECKS, DEPART_ID, DEPART_NAME, USERNAME, PHONE, EMAIL, ADDRESS, TITLE, TYPE, CONTENT, USER_IP, WRITE_DATE,BUSI_ID, GIVE_STATUS, GIVE_DEPT_ID, GIVE_DEPT_NAME,USER_ID,ISANONYMOUS,STATUS,REGION_ID,SXID,SXBM,SXMC,URL,DESIGN,PLEASE,ZJHM,POWERTYPE," +
						"COMMITMENTTIME,COMMITMENT) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		String id = Tools.getNewID("mutual");//Tools.getUUID32();//Tools.getNewID("mutual");
			int i = this.executeUpdate(
					sql,
					new Object[] {
							id, // id生成器生成
							"0",
							"0",
							(String) pSet.getParameter("DEPART_ID"),
						    (String) pSet.getParameter("DEPART_NAME"),
							(String) pSet.getParameter("USERNAME"),
							(String) pSet.getParameter("PHONE"),
							(String) pSet.getParameter("EMAIL"),
							(String) pSet.getParameter("ADDRESS"),
							(String) pSet.getParameter("TITLE"),
							(String) pSet.getParameter("TYPE"),
							(String) pSet.getParameter("CONTENT"),
							pSet.getRemoteAddr(),
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
						    (String) pSet.getParameter("BUSI_ID"),
							"0",
							(String) pSet.getParameter("GIVE_DEPT_ID"),
							(String) pSet.getParameter("GIVE_DEPT_NAME"),
							 pSet.getParameter("USER_ID"),
							(String) pSet.getParameter("ISANONYMOUS"),
							(String) pSet.getParameter("STATUS")!=null?(String) pSet.getParameter("STATUS"):"0",
							SecurityConfig.getString("WebRegion"),
							(String) pSet.getParameter("SXID"),
							(String) pSet.getParameter("SXBM"),
							(String) pSet.getParameter("SXMC"),
							(String) pSet.getParameter("URL"),
							(String) pSet.getParameter("DESIGN"),
							(String) pSet.getParameter("PLEASE"),
							(String) pSet.getParameter("ZJHM"),
							(String)pSet.getParameter("POWERTYPE"),
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(startTime,CommonUtils.YYYY_MM_DD),
									CommonUtils.YYYY_MM_DD),
							timeout
						});
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
				_log.error(ds.getMessage());
			} else {
				JSONObject jo = new JSONObject();
				jo.put("id", id);
				ds.setState(StateType.SUCCESS);
				ds.setData(Tools.stringToBytes(jo.toString()));
				//isSendSms=1，有咨询、投诉时需要短信告知部门联系人
				String isSendSms=SecurityConfig.getString("isSendSms");
				if ("1".equals(isSendSms)) {
					pSet.put("sms", "SmsContent4Consult");
					SmsDao.getInstance().sendMessage(pSet);
				}				
			}
		return ds;
	}
		public DataSet insert(ParameterSet pSet,UserInfo user) {
			DataSet ds = new DataSet();
			ParameterSet userPset = new ParameterSet();
			userPset.setParameter("id", user.getUid());
			DataSet userDs = UserDao.getInstance().getList(userPset);
			//JSONObject obj = new JSONObject();
			//String address = "";
			//String email = "";
			if(userDs.getState() == StateType.SUCCESS && userDs.getTotal()>0){
				//obj = userDs.getRecord(0);
				//address = obj.getString("ADDRESS");
				//email = obj.getString("EMAIL");
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("无用户数据");
				return ds;
			}
			pSet.setParameter("USERNAME", user.getUserName());
			pSet.setParameter("PHONE", user.getMobile());
			/*暂时屏蔽，如果登录的邮箱为空，则会清空原有邮箱
			pSet.setParameter("ADDRESS", address);
			pSet.setParameter("EMAIL", email);*/
			return this.insert(pSet);
		}
}
