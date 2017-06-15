package app.icity.guestbook;

import java.util.Date;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import core.util.CommonUtils;

public class SmsDao extends BaseJdbcDao {
	public SmsDao() {
		this.setDataSourceName("icityDataSource");
	}
	public static Log _log = LogFactory.getLog(SmsDao.class);
	public static SmsDao getInstance() {
		return (SmsDao)DaoFactory.getDao(SmsDao.class.getName());
	}
	public void sendMessage(ParameterSet pSet) {
		DataSet ds;
		String sql="";
		String phone="";
		String smsContent = "",message=null;
		String type=(String) pSet.getParameter("TYPE");
		try {
			sql=" select t.id,t.name,t.lxr,t.lxdh from sys_dept t where  t.id=?";
			ds=this.executeDataset(sql, new Object[]{(String) pSet.getParameter("DEPART_ID")});
			if (ds != null && ds.getTotal() > 0) {
				 phone = ds.getRecord(0).getString("LXDH");
			}
			if("".equals(phone)||phone.length()!=11){
				return;
			}
			if (pSet.containsKey("sms")) {
				// 如果配置了sms，则从security.properties加载对应短信模板，否则使用默认短信内容
				String sms = (String) pSet.getParameter("sms");
				message = SecurityConfig.getString(sms);
			} else {
				if("2".equals(type)){//咨询
					message="您好，您所在部门有一条咨询类问题需要处理";
				}
				if("3".equals(type)){//咨询
					message="您好，您所在部门有一条投诉类问题需要处理";
				}
			}
			smsContent = (message == null ? "" : message);
			 sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone,rid) values(?,?,?,?,?,?,?)";
			 this.executeUpdate(sql, new Object[] { Tools.getUUID32(), smsContent, CommonUtils.getInstance().parseStringToTimeStamp(Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS), "mutual", "0", phone,SecurityConfig.getString("WebRegion") });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendMessage2Guest(ParameterSet pSet) {
		DataSet ds;
		String sql="";
		String user="", phone="",title="",deal_result=null;
		StringBuilder smsContent = null;
		try {
			sql=" select  t.username,t.phone,t.title,t.deal_result  from  guestbook t where id=?";
			ds=this.executeDataset(sql,new Object[]{(String)pSet.getParameter("ID")},"icityDataSource");
			if (ds != null && ds.getTotal() > 0) {
				user=ds.getRecord(0).getString("USERNAME");
				phone = ds.getRecord(0).getString("PHONE");
				title=ds.getRecord(0).getString("TITLE");
				deal_result=ds.getRecord(0).getString("DEAL_RESULT");
			}
			if("".equals(phone)||phone.length()!=11){
				return;
			}
			if ("".equals(deal_result)) {
				smsContent=new StringBuilder("用户：");
				smsContent.append(user);
				smsContent.append("，您好。关于《");
				smsContent.append(title);
				smsContent.append("》的咨询/投诉，相关工作人员已处理。没有相关回复内容。");
			}
			else {
				smsContent=new StringBuilder("用户：");
				smsContent.append(user);
				smsContent.append("，您好。关于《");
				smsContent.append(title);
				smsContent.append("》的咨询/投诉，相关工作人员已处理。相关回复内容如下：");
				smsContent.append(deal_result);
			}
			 sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone,rid) values(?,?,?,?,?,?,?)";
			 this.executeUpdate(sql, new Object[] { Tools.getUUID32(), smsContent.toString(), CommonUtils.getInstance().parseStringToTimeStamp(Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS), "mutual", "0", phone,SecurityConfig.getString("WebRegion") });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public DataSet getVerifyCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rand = this.generateVerifyCode();
			String phone = (String) pSet.getParameter("PHONE");
			CacheManager.set("DynamicCode4Mutual", phone, rand);
			String smsContent = "咨询/投诉，验证码"+rand;
			String sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone,rid) values(?,?,?,?,?,?,?)";
			this.executeUpdate(
					sql,
					new Object[] { Tools.getUUID32(), smsContent,
							CommonUtils.getInstance().parseStringToTimeStamp(Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS), CommonUtils.YYYY_MM_DD_HH_mm_SS), "mutual", "0", phone,SecurityConfig.getString("WebRegion") });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 生成四位随机数验证码
	 */
	public String generateVerifyCode() {
		char[] CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };// 用来生成手机短信校验码
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(CHARS[Tools.random(CHARS.length - 1)]);// 从字符数组中随机产生一个字符
			sRand.append(rand);
		}
		return sRand.toString();
	}

}
