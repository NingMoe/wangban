package api.impl;

import java.sql.Connection;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.IdGenereator;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.timevale.SecureUtils;

import core.util.CommonUtils;

public class taiheWechatImpl extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(taiheWechatImpl.class);
	private static api.tools.Tools mytools = new api.tools.Tools();
	public static taiheWechatImpl getInstance() {
		return DaoFactory.getDao(taiheWechatImpl.class.getName());
	}
	
	
	
	/**
	 * 个人用户注册接口 ly=2 微信
	 * @param pset
	 * @return
	 */
	public DataSet register_per(ParameterSet pSet) {
		System.out.println("test");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String username = (String) pSet.getParameter("username");
		String phone = "";
		String email = "";
		String pass_per = (String) pSet.getParameter("pass_per");
		String code_per = "";
		String name_per = "姓名";
		String code_type_per = "10";
		String address_per = "";
		String ly = "xf";
		Date now = CommonUtils.getInstance().parseStringToTimeStamp(
				Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
				CommonUtils.YYYY_MM_DD_HH_mm_SS);
		int type = 11;// 个人
		String is_inuse = "1";// 账号状态
		try {
			// 证件类型 10
			String sql_account = "select 1 from uc_user where account=?";// and
																			// ly='2'";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}

			
			// String sql1 = "select max(id) as id from uc_user ";
			// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
			long id = IdGenereator.getInstance("usercenter").getId();// Integer.parseInt((String)
																		// j.getJOData().get("ID"))
																		// + 1;
			String sql = "insert into uc_user (id,account,name,email,password,card_type,card_no,creation_time,address,type,phone,is_inuse,status,ly) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_per,
					email, pass_per, code_type_per, code_per, now, address_per,
					type, phone, is_inuse, "1", ly }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	//endregion
	
	
	/**
	 * 企业用户注册接口
	 * @param pset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet register_org(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		String username = (String) pSet.getParameter("username");
		String phone_org = "";
		String email = "";
		String pass_org = (String) pSet.getParameter("pass_org");
		String code_org = "";
		String name_org = "姓名";
		String code_type_org = "10";
		String address_org = "";
		String org_org = "";
		String org_all_org = "";
		String legal_person_org = "";
		String code_type_legal_person_org = "10";
		String code_legal_person_org = "";
		String ly = "xf";
		int type = 21;// 企业
		String is_inuse = "1";// 账号状态
		try {
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			// 证件类型 10
			String sql_account = "select 1 from uc_user where account=?";// and
																			// ly='2'";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}

			// String sql1 = "select max(id) as id from uc_user ";
			// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
			// int id = Integer.parseInt((String) j.getJOData().get("ID")) + 1;
			long id = IdGenereator.getInstance("usercenter").getId();
			String sql = "insert into uc_user (id,account,name,email,password,card_type,card_no,creation_time,address,type,org_no,"
					+ "org_name,org_boss_no,org_boss_type,org_boss_name,phone,is_inuse,status,ly) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_org,
					email, pass_org, code_type_org, code_org, now, address_org,
					type, org_org, org_all_org, code_legal_person_org,
					code_type_legal_person_org, legal_person_org, phone_org,
					is_inuse, "1", ly }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	
}
