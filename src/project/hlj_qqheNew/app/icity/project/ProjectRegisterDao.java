package hlj_qqheNew.app.icity.project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.cpa.persistence.convertor.IntegerToLong;
import org.eclipse.jetty.util.security.Credential.MD5;

import app.icity.project.ProjectQueryDao;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.IdGenereator;
import com.inspur.util.SecurityConfig;

import exchange.entity.datacheckrequest.DataRow;

public class ProjectRegisterDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(ProjectRegisterDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String ipfDataSource = "ipfDataSource";
	protected static String icpDataSource = "icpDataSource";
	protected static String mssqlDataSource = "mssqlDataSource";
	private static ProjectRegisterDao _instance = null;
	private static String vcode = "1";
	private HashMap<String, Object> Msgmap = new HashMap<String, Object>();
	public static ProjectRegisterDao getInstance() {
		return DaoFactory.getDao(ProjectRegisterDao.class.getName());
	}

	public ProjectRegisterDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static void createRandomVcode() {
		// 验证码
		vcode = "";
		for (int i = 0; i < 6; i++) {
			vcode = vcode + (int) (Math.random() * 9);
		}

	}

	public DataSet register_per(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String username = (String) pSet.getParameter("username");
		String phone = (String) pSet.getParameter("phone");
		String pass_per = (String) pSet.getParameter("pass_per");
		String code_per = (String) pSet.getParameter("code_per");
		String name_per = (String) pSet.getParameter("name_per");
		String code_type_per = (String) pSet.getParameter("code_type_per");
		String address_per = (String) pSet.getParameter("address_per");
		int type = 11;// 个人
		String is_inuse = "1";// 账号状态
		// 证件类型 10
		//String sql1 = "select max(id) as id from uc_user ";
		//DataSet j = this.executeDataset(sql1, null, icityDataSource);
		//int id = Integer.parseInt((String) j.getJOData().get("ID")) + 1;
		long id;
		try {
			id = IdGenereator.getInstance("usercenter").getId();
		
		String sql = "insert into uc_user (id,account,name,password,card_type,card_no,creation_time,address,type,phone，is_inuse) values ('"
				+ id
				+ "','"
				+ username
				+ "','"
				+ name_per
				+ "','"
				+ pass_per
				+ "','"
				+ code_type_per
				+ "','"
				+ code_per
				+ "',"
				+ "sysdate"
				+ ",'"
				+ address_per
				+ "','"
				+ type
				+ "','"
				+ phone
				+ "','"
				+ is_inuse + "')";
			int i = this.executeUpdate(sql, null, icityDataSource);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;

	}

	public DataSet register_org(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String username = (String) pSet.getParameter("username");
		String phone_org = (String) pSet.getParameter("phone");
		String pass_org = (String) pSet.getParameter("pass_org");
		String code_org = (String) pSet.getParameter("code_org");
		String name_org = (String) pSet.getParameter("name_org");
		String code_type_org = (String) pSet.getParameter("code_type_org");
		String address_org = (String) pSet.getParameter("address_org");
		String org_org = (String) pSet.getParameter("org_org");
		String org_all_org = (String) pSet.getParameter("org_all_org");
		String legal_person_org = (String) pSet
				.getParameter("legal_person_org");
		String code_type_legal_person_org = (String) pSet
				.getParameter("code_type_legal_person_org");
		String code_legal_person_org = (String) pSet
				.getParameter("code_legal_person_org");
		int type = 21;// 企业
		String is_inuse = "1";// 账号状态
		// 证件类型 10
		//String sql1 = "select max(id) as id from uc_user ";
		//DataSet j = this.executeDataset(sql1, null, icityDataSource);
		//int id = Integer.parseInt((String) j.getJOData().get("ID")) + 1;
		long id;
		try {
			id = IdGenereator.getInstance("usercenter").getId();
		String sql = "insert into uc_user (id,account,name,password,card_type,card_no,creation_time,address,type,org_no,org_name,org_boss_no,org_boss_type,org_boss_name,phone,is_inuse) values "
				+ " ('"
				+ id
				+ "','"
				+ username
				+ "','"
				+ name_org
				+ "','"
				+ pass_org
				+ "','"
				+ code_type_org
				+ "','"
				+ code_org
				+ "',"
				+ "sysdate"
				+ ",'"
				+ address_org
				+ "','"
				+ type
				+ "','"
				+ org_org
				+ "','"
				+ org_all_org
				+ "','"
				+ code_legal_person_org
				+ "','"
				+ code_type_legal_person_org
				+ "','"
				+ legal_person_org
				+ "','"
				+ phone_org + "','" + is_inuse + "')";
			int i = this.executeUpdate(sql, null, icityDataSource);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;

	}

	public DataSet check_username(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String username = (String) pSet.getParameter("username");
		String sql = "select id from uc_user where account='" + username + "' ";
		int i = this.executeUpdate(sql, null, icityDataSource);
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	// 验证身份证卡号唯一性
	public DataSet check_cardno(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String code_per = (String) pSet.getParameter("code_per");
		String sql = "select id from uc_user where card_no='" + code_per + "' ";
		int i = this.executeUpdate(sql, null, icityDataSource);
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	// 发送短信验证码
	public DataSet message_send(ParameterSet pSet) {
	    Date date = new Date();
		DataSet ds = new DataSet();
		String isOn = SecurityConfig.getString("messageAudit");
		if(isOn.equals("on")){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format2 = new SimpleDateFormat("HH:mm:ss");
		String time = format.format(date);
		String time1 = format1.format(date);
		String time2 = format2.format(date);
		ProjectRegisterDao.createRandomVcode();
		String mescode = vcode;
		String phone = (String) pSet.getParameter("phone");
		Msgmap.remove(phone);
		Msgmap.put(phone, mescode);
		String sql = "insert into InBox(mbno,Msg,ArriveDate,ArriveDateTime,ArriveTime) values ('"+ phone+ "','您的短信验证码为:"+ mescode+ "','"+ time+ "','"+ time1+ "','" + time2 + "');";
		int i = this.executeUpdate(sql, null, mssqlDataSource);
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");

		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("短信功能未打开!");
			return ds;
		}
	}

	// 比对验证码
	public DataSet isTrue(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String isOn = SecurityConfig.getString("Msgcode");
		if (isOn.equals("on")) {
			ds.setState(StateType.SUCCESS);
			return ds;
		}
		String message = (String) pSet.getParameter("messagecode");
		String phone = (String) pSet.getParameter("phone");
		vcode = (String)Msgmap.get(phone);
		if (message.equals(vcode)) {
			ds.setState(StateType.SUCCESS);
			Msgmap.remove(phone);
		} else {
			ds.setMessage("验证码对比失败！");
			ds.setState(StateType.FAILT);

		}
		return ds;
	}

	public DataSet password_cz(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String account = (String) pSet.getParameter("account");
		String card_no = (String) pSet.getParameter("card");
		String password = (String) pSet.getParameter("password");
		String sql = "update uc_user set password = '" + password
				+ "' where account = '" + account + "' and card_no = "
				+ card_no + "";
		int i = this.executeUpdate(sql, null, icityDataSource);

		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
	

}
