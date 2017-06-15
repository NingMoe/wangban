package app.uc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.IdGenereator;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class GetUserMapDao extends BaseJdbcDao {
	private GetUserMapDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GetUserMapDao getInstance() {
		return (GetUserMapDao) DaoFactory.getDao(GetUserMapDao.class.getName());
	}

	public DataSet getUserMapInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String sql = "select * from UC_USER_MAP t where user_id_map = ?";
			ds = this
					.executeDataset(sql,
							new Object[] { pset.get("user_id_map") },
							"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}

	// 将id变为uid
	public DataSet GetUid(ParameterSet pset) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet ds;
		String user_id_map = (String) pset.get("user_id_map");
		JSONObject uInfo = (JSONObject) pset.get("uInfo");
		ds = getUserMapInfo(pset);
		int len = ds.getTotal();
		try {
			if (len == 0) {
				long user_id = IdGenereator.getInstance("usercenter").getId();
				conn.setAutoCommit(false);
				String sql = "insert into UC_USER_MAP (user_id_map,user_id) values (?,?)";
				DbHelper.update(sql, new Object[] { user_id_map, user_id },
						conn);
				if (uInfo != null) {
					sql = "INSERT INTO UC_USER(ID,ACCOUNT,NAME,TYPE,CARD_NO,ORG_NAME,ORG_NO,CREDIT_CODE,"
							+ "CARD_TYPE,ICREGNUMBER,PHONE,EMAIL,ORG_BOSS_NAME,"
							+ "ADDRESS,STATUS,ORG_BOSS_NO) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					int i = DbHelper.update(
							sql,
							new Object[] {
									user_id,
									uInfo.getString("ACCOUNT"),
									uInfo.getString("NAME"),
									uInfo.get("TYPE"), // 1表示普通注册
									uInfo.get("CARD_NO"),
									uInfo.get("ORG_NAME"),
									uInfo.get("ORG_NO"),
									uInfo.get("CREDIT_CODE"),
									"10",
									uInfo.getString("ICREGNUMBER"),
									uInfo.has("PHONE") ? uInfo
											.getString("PHONE") : "",
									uInfo.containsKey("EMAIL") ? uInfo
											.get("EMAIL") : "",
									uInfo.containsKey("ORG_BOSS_NAME") ? uInfo
											.get("ORG_BOSS_NAME") : "",
									uInfo.containsKey("ADDRESS") ? uInfo
											.get("ADDRESS") : "",
									uInfo.containsKey("STATUS") ? uInfo
											.get("STATUS") : "" ,
									uInfo.get("CARD_NO") //法人账号的法人证件号码
											}, conn);

				}
				conn.commit();
				ds = getUserMapInfo(pset);
			} else {
				if (uInfo != null) {
					String sql1 = "UPDATE UC_USER SET NAME=?,CARD_NO=?,ORG_NAME=?,ORG_NO=?,CREDIT_CODE=?,"
							+ "ICREGNUMBER=?,PHONE=?,EMAIL=?,ORG_BOSS_NAME=?,ADDRESS=?,"
							+ "STATUS=?,ORG_BOSS_NO=? "
							+ "WHERE ID = ?";
					int i = DbHelper.update(
							sql1,
							new Object[] {
									uInfo.getString("NAME"),
									uInfo.get("CARD_NO"),
									uInfo.get("ORG_NAME"),
									uInfo.get("ORG_NO"),
									uInfo.get("CREDIT_CODE"),
									uInfo.getString("ICREGNUMBER"),
									uInfo.has("PHONE") ? uInfo
											.getString("PHONE") : "",
									uInfo.containsKey("EMAIL") ? uInfo
											.get("EMAIL") : "",
									uInfo.containsKey("ORG_BOSS_NAME") ? uInfo
											.get("ORG_BOSS_NAME") : "",
									uInfo.containsKey("ADDRESS") ? uInfo
											.get("ADDRESS") : "",
									uInfo.containsKey("STATUS") ? uInfo
											.get("STATUS") : "",
									uInfo.get("CARD_NO") , //法人账号的法人证件号码
									ds.getRecord(0).get("USER_ID")
							}, conn);
				}

				conn.commit();
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException sqlE) {
				// TODO Auto-generated catch block
				sqlE.printStackTrace();
			}
			DbHelper.closeConnection(conn);
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}

	// 将uid变为Mapid
	public DataSet GetMapid(String uid) {
		DataSet ds = new DataSet();
		try {
			String sql = "select * from UC_USER_MAP t where user_id = ?";
			ds = this.executeDataset(sql, new Object[] { uid },
					"icityDataSource");
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	// 将Mapid变为uid
		public DataSet GetUid(String uuid) {
			DataSet ds = new DataSet();
			try {
				String sql = "select * from UC_USER_MAP t where user_id_map = ?";
				ds = this.executeDataset(sql, new Object[] { uuid },
						"icityDataSource");
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				e.printStackTrace();
			}
			return ds;
		}

	// 绑定用户，把新id改成老id
	public DataSet userBind(String uid, String oldUid) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			JSONObject uiNew = UserDao.getInstance().getListExt(uid)
					.getRecord(0);
			JSONObject uiOld = UserDao.getInstance().getListExt(oldUid)
					.getRecord(0);
			if (!uiNew.getString("TYPE").equals(uiOld.getString("TYPE"))) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户类型不匹配!");
				return ds;
			}
			// 判断当前账号是否已绑定
			DataSet uDs = GetMapid(oldUid);
			if (uDs.getState() == StateType.SUCCESS && uDs.getTotal() < 1) {
				conn.setAutoCommit(false);
				// 办件
				String business_sql = "update business_index set ucid= ? where ucid= ?";
				DbHelper.update(business_sql, new Object[] { oldUid, uid },
						conn);
				// 咨询投诉
				String GUESTBOOK_sql = "update GUESTBOOK set user_id= ? where user_id= ?";
				DbHelper.update(GUESTBOOK_sql, new Object[] { oldUid, uid },
						conn);
				// 收藏
				String FAVORITE_sql = "update POWER_BASE_INFO_FAVORITE set usid= ? where usid= ?";
				DbHelper.update(FAVORITE_sql, new Object[] { oldUid, uid },
						conn);
				// 资料库
				String attach_sql = "update business_power_attach set ucid= ? where ucid= ?";
				DbHelper.update(attach_sql, new Object[] { oldUid, uid }, conn);
				String battach_sql = "update BUSINESS_ATTACH set ucid= ? where ucid= ?";
				DbHelper.update(battach_sql, new Object[] { oldUid, uid }, conn);

				// 修改用户映射
				String sql = "update UC_USER_MAP set user_id = ? where user_id = ?";
				DbHelper.update(sql, new Object[] { oldUid, uid }, conn);

				// 修改用户账号，将旧账号修改成新账号
				sql = "update UC_USER set account = ? where id = ?";
				DbHelper.update(sql, new Object[] { uiNew.getString("ACCOUNT"),
						oldUid }, conn);

				// 删除新账号
				sql = "delete UC_USER  where id = ?";
				DbHelper.update(sql, new Object[] { uid }, conn);

				// 删除新账号扩展信息
				sql = "delete UC_USER_EXT  where id = ?";
				DbHelper.update(sql, new Object[] { uid }, conn);

				conn.commit();

			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户已绑定！");
			}

		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("用户绑定错误！");
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	//博罗用户信息保存
	public DataSet GetUid4Bl(ParameterSet pset) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet ds;
		String user_id_map = (String) pset.get("user_id_map");
		JSONObject uInfo = (JSONObject) pset.get("uInfo");
		ds = getUserMapInfo(pset);
		int len = ds.getTotal();
		try {
			if (len == 0) {
				long user_id = IdGenereator.getInstance("usercenter").getId();
				conn.setAutoCommit(false);
				String sql = "insert into UC_USER_MAP (user_id_map,user_id) values (?,?)";
				DbHelper.update(sql, new Object[] { user_id_map, user_id },
						conn);
				if (uInfo != null) {
					sql = "INSERT INTO UC_USER(ID,ACCOUNT,NAME,TYPE,CARD_NO,ORG_NAME,ORG_NO,CARD_TYPE,PHONE,EMAIL,ORG_BOSS_NAME,ADDRESS,STATUS,UVERSION,IS_REAL,ORIGIN) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					int i = DbHelper.update(
							sql,
							new Object[] {
									user_id,
									uInfo.getString("ACCOUNT"),
									uInfo.getString("NAME"),
									uInfo.get("TYPE"), // 1表示普通注册
									uInfo.get("CARD_NO"),
									uInfo.get("ORG_NAME"),
									uInfo.get("ORG_NO"),
									"10",
									uInfo.has("PHONE") ? uInfo
											.getString("PHONE") : "",
									uInfo.containsKey("EMAIL") ? uInfo
											.get("EMAIL") : "",
									uInfo.containsKey("ORG_BOSS_NAME") ? uInfo
											.get("ORG_BOSS_NAME") : "",
									uInfo.containsKey("ADDRESS") ? uInfo
											.get("ADDRESS") : "",
									uInfo.containsKey("STATUS") ? uInfo
											.get("STATUS") : "",uInfo.get("UVERSION"),
											uInfo.get("IS_REAL"),
											uInfo.get("ORIGIN") },
											 conn);

				}
				conn.commit();
				ds = getUserMapInfo(pset);
			} else {
				if (uInfo != null) {
					String sql1 = "UPDATE UC_USER SET NAME=?,CARD_NO=?,ORG_NAME=?,ORG_NO=?,PHONE=?,EMAIL=?,ORG_BOSS_NAME=?,ADDRESS=?,STATUS=?,UVERSION=?,IS_REAL=? WHERE ID = ?";
					int i = DbHelper.update(
							sql1,
							new Object[] {
									uInfo.getString("NAME"),
									uInfo.get("CARD_NO"),
									uInfo.get("ORG_NAME"),
									uInfo.get("ORG_NO"),
									uInfo.has("PHONE") ? uInfo
											.getString("PHONE") : "",
									uInfo.containsKey("EMAIL") ? uInfo
											.get("EMAIL") : "",
									uInfo.containsKey("ORG_BOSS_NAME") ? uInfo
											.get("ORG_BOSS_NAME") : "",
									uInfo.containsKey("ADDRESS") ? uInfo
											.get("ADDRESS") : "",
									uInfo.containsKey("STATUS") ? uInfo
											.get("STATUS") : "",uInfo.get("UVERSION"),
											uInfo.get("IS_REAL"),
									ds.getRecord(0).get("USER_ID") }, conn);
				}

				conn.commit();
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException sqlE) {
				// TODO Auto-generated catch block
				sqlE.printStackTrace();
			}
			DbHelper.closeConnection(conn);
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 漳州，登陆时落地省网办用户信息
	 * @param jo
	 * @return
	 */
	public DataSet insertIntoUcUser(JSONObject jo){
		DataSet ds = new DataSet();
		if("11".equals(jo.getString("type"))){
			ds = register_person(jo);
		}else if("21".equals(jo.getString("type"))){
			ds = register_ent(jo);
		}
		return ds;
	}
	/**
	 * 个人用户注册接口
	 */
	public DataSet register_person(JSONObject jo) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String username = jo.getString("login_name");// 用户名
			String phone = jo.getString("mobile_phone");// 手机号
			String email = jo.getString("email");// 邮箱
			String code_per = jo.getString("certificate_number");// 证件号码
			String name_per = jo.getString("name");// 姓名
			String code_type_per = jo.getString("certificate_type");// 证件类型
			String address_per =jo.getString("address");// 地址
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),CommonUtils.YYYY_MM_DD_HH_mm_SS);
			int type = 11;// 个人
			String is_inuse = "1";// 账号状态
			String sql_phone = "select 1 from uc_user where phone=? and type=11";
			DataSet k = DbHelper.query(sql_phone, new Object[] { phone }, conn);
			if (k.getTotal() > 0) {
				ds.setRawData(new JSONArray());
				ds.setState(StateType.FAILT);
				ds.setMessage("该手机已注册！");
				return ds;
			}
			String sql_account = "select 1 from uc_user where account=?";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}
			String sql_card_no = "select 1 from uc_user where card_no=? and type=11";																
			DataSet ds_card_no = DbHelper.query(sql_card_no,
					new Object[] { code_per }, conn);
			if (ds_card_no.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("身份证号已注册！");
				return ds;
			}
			long id = IdGenereator.getInstance("usercenter").getId();
			String sql = "insert into uc_user (id,account,name,email,card_type,card_no,creation_time,address,type,phone,is_inuse,status,ly) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_per,
					email, code_type_per, code_per, now, address_per,
					type, phone, is_inuse, "1", "4" }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("注册失败！");
			} else {
				ds.setRawData(id);
				ds.setState(StateType.SUCCESS);
				ds.setMessage("注册成功！");
				conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	/**
	 * 企业用户注册接口
	 */
	public DataSet register_ent(JSONObject jo) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String username = jo.getString("login_name");// 用户名
			String phone_org = jo.getString("org_law_mobile");// 手机号
			String email = jo.getString("jbr_email");// 邮箱
			String code_org = jo.getString("jbr_certificateNumber");// 证件号码
			String name_org = jo.getString("name");// 姓名
			String code_type_org = "10";// 证件类型
			String address_org = jo.getString("org_address");// 地址
			if("null".equals(address_org)){
				address_org="";
			}
			String org_org = jo.getString("org_code");// 机构代码
			String org_all_org = jo.getString("org_name");// 机构名称
			String legal_person_org = jo.getString("law_name");// 机构法人名称
			String code_type_legal_person_org = "10";// 机构法人证件类型
			String code_legal_person_org = jo.getString("org_law_idcard");// 机构法人编码
			int type = 21;// 企业
			String is_inuse = "1";// 账号状态
			Date now = CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS);
			String sql_phone = "select 1 from uc_user where phone=? and type=21";
			DataSet k = DbHelper.query(sql_phone, new Object[] { phone_org },
					conn);
			if (k.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("手机号已注册！");
				ds.setRawData(new JSONArray());
				return ds;
			}
			String sql_account = "select 1 from uc_user where account=?";
			DataSet ds_account = DbHelper.query(sql_account,
					new Object[] { username }, conn);
			if (ds_account.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("用户名已存在！");
				return ds;
			}
			String sql_card_no = "select 1 from uc_user where card_no=? type=21";
			DataSet ds_card_no = DbHelper.query(sql_card_no,
					new Object[] { code_org }, conn);
			if (ds_card_no.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("身份证号已注册！");
				return ds;
			}
			long id = IdGenereator.getInstance("usercenter").getId();
			String sql = "insert into uc_user (id,account,name,email,card_type,card_no,creation_time,address,type,org_no,"
					+ "org_name,org_boss_no,org_boss_type,org_boss_name,phone,is_inuse,status,ly) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = DbHelper.update(sql, new Object[] { id, username, name_org,
					email, code_type_org, code_org, now, address_org,
					type, org_org, org_all_org, code_legal_person_org,
					code_type_legal_person_org, legal_person_org, phone_org,
					is_inuse, "1", "4" }, conn);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("注册失败！");
			} else {
				conn.commit();
				ds.setRawData(id);
				ds.setMessage("注册成功！");
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			ds.setRawData(new JSONArray());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
}