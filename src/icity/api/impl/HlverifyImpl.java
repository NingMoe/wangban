package api.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import app.pmi.validation.RequestVerify;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.IdGenereator;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

public class HlverifyImpl extends BaseQueryCommand {
	public static HlverifyImpl getInstance() {
		return DaoFactory.getDao(HlverifyImpl.class.getName());
	}

	/**
	 * phone 手机号（必填） idCard 身份证号码（必填） name 姓名（必填） passWord 登录密码（必填） hashSign
	 * 验签字符串 time 验签时间串
	 * 
	 * @param pSet
	 * @return 重庆华龙网注册 cqhl
	 */
	public JSONObject submitRegister(ParameterSet pSet) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		String phone = (String) pSet.get("phone");
		String idCard = (String) pSet.get("idCard");
		String name = (String) pSet.get("name");
		String passWord = (String) pSet.get("passWord");
		String hashSign = (String) pSet.get("hashSign");
		String time = (String) pSet.get("time");
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("idCard", idCard);
		params.put("name", name);
		params.put("passWord", passWord);
		params.put("hashSign", hashSign);
		params.put("time", time);
		boolean t = RequestVerify.getInstance().verifythesignature(params);
		System.out.println("verifythesignature:" + t);
		JSONObject result = new JSONObject();
		try {
			if (t) {
				Date now = CommonUtils.getInstance().parseStringToTimeStamp(
						Tools.formatDate(new Date(),
								CommonUtils.YYYY_MM_DD_HH_mm_SS),
						CommonUtils.YYYY_MM_DD_HH_mm_SS);
				int type = 11;// 个人
				String is_inuse = "1";// 账号状态
				// 证件类型 10
				String sql_account = "select id from uc_user where account=?";// and
																				// ly='2'";
				DataSet ds_account = DbHelper.query(sql_account,
						new Object[] { phone }, conn);
				if (ds_account.getTotal() > 0) {
					result.put("code", "300");
					result.put("error", "用户名已存在！");
					result.put("userID", ds_account.getJAData().getJSONObject(0).getString("ID"));
					return result;
				}

				String sql_phone = "select id from uc_user where phone=?";// and
																			// ly='2'";
				DataSet ds_phone = DbHelper.query(sql_phone,
						new Object[] { phone }, conn);
				if (ds_phone.getTotal() > 0) {
					result.put("code", "300");
					result.put("error", "手机号已注册！");
					result.put("userID",ds_phone.getJAData().getJSONObject(0).getString("ID"));
					return result;
				}

				String sql_card_no = "select id from uc_user where card_no=? and card_type=? and type='11'";// and
																											// ly='3'";
				DataSet ds_card_no = DbHelper.query(sql_card_no, new Object[] {
						idCard, "10" }, conn);
				if (ds_card_no.getTotal() > 0) {
					result.put("code", "300");
					result.put("error", "身份证号已注册！");
					result.put("userID", ds_card_no.getJAData().getJSONObject(0).getString("ID"));
					return result;
				}

				// String sql1 = "select max(id) as id from uc_user ";
				// DataSet j = DbHelper.query(sql1, new Object[] {}, conn);
				// int id = Integer.parseInt((String) j.getJOData().get("ID")) +
				// 1;
				long id = IdGenereator.getInstance("usercenter").getId();
				String sql = "insert into uc_user (id,account,name,email,password,card_type,card_no,creation_time,address,type,phone,is_inuse,status,ly) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				int i = DbHelper.update(sql, new Object[] { id, phone, name,
						"", passWord, "11", idCard, now, "", type, phone,
						is_inuse, "1", "3" }, conn);
				if (i == 0) {
					result.put("code", "300");
					result.put("error", "数据库操作失败！");
					result.put("userID", "");
				} else {
					result.put("code", "200");
					result.put("error", "");
					result.put("userID", id);
				}
				return result;
			} else {
				result.put("code", "300");
				result.put("error", "签名验证失败！");
				result.put("userID", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", "300");
			result.put("error", "签名验证失败！");
			result.put("userID", "");
			return result;
		}
	}
}