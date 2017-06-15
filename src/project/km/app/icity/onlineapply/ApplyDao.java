package km.app.icity.onlineapply;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.StateType;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import app.util.RestUtil;
import core.util.CommonUtils;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class ApplyDao extends BaseJdbcDao {
	private static final String handlType = "01"; // 办件提交方式，01网上，02窗口，网办默认为网上提交
	private static final String appCode = com.icore.util.SecurityConfig
			.getString("uniteCodeAppCode"); // 统一编码服务配置的appcode
	private static final String salt = com.icore.util.SecurityConfig
			.getString("uniteCodeSalt"); // 统一编码服务配置的salt
	private static Logger log = LoggerFactory.getLogger(ApplyDao.class);

	private ApplyDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ApplyDao getInstance() {
		return (ApplyDao) DaoFactory.getDao(ApplyDao.class.getName());
	}

	public JSONObject submitData(JSONObject data) throws Exception {
		String o_sblsh = data.getString("receiveNum");// 暂存或者再次提交时，有值
		String sblsh = "", cxmm = "";
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String ex_dataId = data.containsKey("ex_dataId") ? data
				.getString("ex_dataId") : "";
		String state = data.getString("state");

		String appId = SecurityConfig.getString("AppId");
		String txLogisticID = Tools.getUUID32();
		String txLogisticID2 = Tools.getUUID32();
		// 是否邮寄 0否1是
		String istoEms = "0";
		// 是否回寄 0否1是
		String isEms = "0";
		JSONObject ems = new JSONObject();

		// 判断是否需要邮寄（威海、福州项目）
		if ("weihai".equals(appId) || "fz".equals(appId)) {
			ems = data.getJSONObject("ems");
			if (!ems.isNullObject()) {
				istoEms = ems.getString("istoEms");
				isEms = ems.getString("isEms");
			}
		}

		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		conn.setAutoCommit(false);
		try {
			if ("sp".equals(state) || "ql".equals(state)) {
				// 首次提交先生成申办流水号+查询密码
				JSONObject codeInfo = getCodeInfo(data);
				sblsh = codeInfo.getString("unitecode");
				cxmm = codeInfo.containsKey("password") ? codeInfo
						.getString("password") : "";// 查询密码
				data.put("receiveNum", sblsh);
				data.put("password", cxmm);
				if (!"".equals(o_sblsh)) {
					sql = "update business_index set sblsh=?,cxmm=?, state='11',SQRZJHM=?,SQRMC=?,LXRXM=?,LXRSJ=?,SBXMMC=?,sbsj=?,signstate=?,casecode=? where sblsh=? ";
					DbHelper.update(
							sql,
							new Object[] {
									sblsh,
									cxmm,
									data.get("zjhm"),
									data.get("sqrxm"),
									data.get("lxrxm"),
									data.get("lxrphone"),
									"关于" + data.get("sqrxm")
											+ data.get("itemName") + "的业务",
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_mm_SS),
									"1", data.get("Case_ID"), o_sblsh }, conn);
					sql = " update SUB_FOR_EX_APP_INFORMATION set sblsh=?, OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=?,EX_DATAID=? where sblsh = ?";
					DbHelper.update(sql,
							new Object[] { sblsh, objectType, data.toString(),
									formId, dataId, ex_dataId, o_sblsh }, conn);

					if ("1".equals(istoEms)) {
						sql = "update business_express set RECIEVER=?,RECIEVER_ADDRESS=?,RECIEVER_PHONE=?,SENDER=?,SENDER_PHONE=?,OPERATOR=?,OPERRATE_DATE=?,COM=?,ISTAKEN=?,ORDER_NUMBER=? where SBLSH=? ";

						DbHelper.update(
								sql,
								new Object[] {
										ems.getString("emsAcceptName"),
										ems.getString("emsAcceptAddress"),
										ems.getString("emsAcceptPhone"),
										ems.getString("emstoName"),
										ems.getString("emstoPhone"),
										data.get("ucid"),
										CommonUtils_api
												.getInstance()
												.parseDateToTimeStamp(
														new Date(),
														CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
										"EMS", "0", txLogisticID, o_sblsh },
								conn);

						// 福州（分寄件和回寄件，所以有两条数据，需要根据类型区分）
						if ("fz".equals(appId)) {
							updateEms4Fz(ems, isEms, sblsh, data, txLogisticID,
									o_sblsh);
						}
					}
				} else {
					sql = "insert into business_index (SBLSH,SXBM,SXID,"
							+ "SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"
							+ "LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"
							+ "YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"
							+ "SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"
							+ "SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"
							+ "TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"
							+ "BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"
							+ "STATE,URL,LIMIT_NUM,ASSORT,signstate,ly,casecode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					DbHelper.update(
							sql,
							new Object[] {
									sblsh,// 申办流水号类似的编码来确定事项
									data.get("itemCode"),
									data.get("itemId"),
									data.get("itemName"),
									data.get("ucid"),
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
									data.get("objectType"),
									data.get("zjhm"),
									data.get("sqrxm"),
									data.get("lxrxm"),
									data.get("lxrphone"),
									"关于" + data.get("sqrxm")
											+ data.get("itemName") + "的业务",
									sblsh,
									"",// BLFS办理方式
									data.get("regionName"),
									data.get("regionCode"), null,// YSLSJ业务受理时间
									"",// YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
									"",// YSLSBYY预受理失败原因
									null,// SLSJ预受理失败原因
									"",// YWLSH业务流水号
									data.get("orgName"), data.get("orgCode"),// 收件单位代码
									"",// SLZTDM受理状态代码(1受理，2不受理)
									"",// BSLYY不受理原因
									"",// SLHZH受理回执号
									cxmm,// CXMM查询密码
									null,// SPSJ审批时间
									"",// SPHJDM审批环节代码(1承办，2审核，3批准)
									"",// SPHJMC审批环节名称
									null,// BZGZSJ补正告知时间
									"",// BZGZYY补正告知原因
									"",// BZCLQD补正材料清单
									null,// BZSJ补正时间
									null,// TBCXKSRQ特别程序开始日期
									"",// TBCXQDLY特别程序启动理由或依据
									"",// SQNR申请内容
									null,// TBCXJSRQ特别程序结束日期
									"",// TBCXJG特别程序结果
									null,// BJSJ办结时间
									"",// BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
									"",// BJJGMS办结结果描述
									"",// ZFHTHYY作废或退回原因
									null,// LQSJ领取时间
									state,// REMARK备注信息sp、ql
									"11",// STATE业务状态
									"",// URL跳转地址
									"0",// limit_num 补正补齐次数
									data.get("assort"), "1",// 数据交换标志位
									data.get("ly"),// 来源，icity.wechat等
													// xnrtvzsxn（掌上西宁）
									data.get("Case_ID") }, conn);
					sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID,EX_DATAID) values (?,?,?,?,?,?)";
					DbHelper.update(sql,
							new Object[] { sblsh, objectType, data.toString(),
									formId, dataId, ex_dataId }, conn);
					if ("1".equals(istoEms)) {
						if ("weihai".equals(appId)) {
							sql = "insert into business_express (SBLSH,RECIEVER,RECIEVER_ADDRESS,RECIEVER_PHONE,SENDER,SENDER_PHONE,OPERATOR,OPERRATE_DATE,COM,ISTAKEN,ORDER_NUMBER) values(?,?,?,?,?,?,?,?,?,?,?)";
							DbHelper.update(
									sql,
									new Object[] {
											sblsh,
											ems.getString("emsAcceptName"),
											ems.getString("emsAcceptAddress"),
											ems.getString("emsAcceptPhone"),
											ems.getString("emstoName"),
											ems.getString("emstoPhone"),
											data.get("ucid"),
											CommonUtils_api
													.getInstance()
													.parseDateToTimeStamp(
															new Date(),
															CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
											"EMS", "0", txLogisticID }, conn);
						} else if ("fz".equals(appId)) {
							insertEms4Fz(ems, isEms, sblsh, data, txLogisticID,
									txLogisticID2);
						}
					}
				}
			} else if ("bqbz".equals(state) || "clbqbz".equals(state)) {
				// 查询查询密码
				sql = "select cxmm,limit_num from business_index where sblsh=?";
				DataSet pwdDs = DbHelper.query(sql, new Object[] { o_sblsh },
						conn);
				cxmm = pwdDs.getRecord(0).getString("CXMM");
				String limit_num = pwdDs.getRecord(0).getString("LIMIT_NUM");
				limit_num = (Integer.parseInt(limit_num) + 1) + "";
				data.put("password", cxmm);
				// 可进行多次补齐补正,或者驳回后的提交
				sql = "update business_index set BZSJ=?,state=?,SQRZJHM=?,SQRMC=?,LXRXM=?,LXRSJ=?,SBXMMC=?,LIMIT_NUM=?,signstate=?,pushstate=? where sblsh=?";
				DbHelper.update(
						sql,
						new Object[] {
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
								"bqbz".equals(state) ? "11" : "01",
								data.get("zjhm"),
								data.get("sqrxm"),
								data.get("lxrxm"),
								data.get("lxrphone"),
								"关于" + data.get("sqrxm") + data.get("itemName")
										+ "的业务", limit_num, "1", "3", o_sblsh },
						conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=?,EX_DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[] { objectType,
						data.toString(), formId, dataId, ex_dataId, o_sblsh },
						conn);

				if ("1".equals(istoEms)) {
					sql = "update business_express set RECIEVER=?,RECIEVER_ADDRESS=?,RECIEVER_PHONE=?,SENDER=?,SENDER_PHONE=?,OPERATOR=?,OPERRATE_DATE=?,COM=?,ISTAKEN=?,ORDER_NUMBER=? where SBLSH=?";
					DbHelper.update(
							sql,
							new Object[] {
									ems.getString("emsAcceptName"),
									ems.getString("emsAcceptAddress"),
									ems.getString("emsAcceptPhone"),
									ems.getString("emstoName"),
									ems.getString("emstoPhone"),
									data.get("ucid"),
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
									"EMS", "0", txLogisticID, o_sblsh }, conn);

					// 福州（分寄件和回寄件，所以有两条数据，需要根据类型区分）
					if ("fz".equals(appId)) {
						updateEms4Fz(ems, isEms, sblsh, data, txLogisticID,
								o_sblsh);
					}
				}
			}
			conn.commit();
			return data;
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			DBSource.closeConnection(conn);
		}
	}

	public String saveData(JSONObject data) throws Exception {
		String o_sblsh = data.getString("receiveNum");
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String ex_dataId = data.containsKey("ex_dataId") ? data
				.getString("ex_dataId") : "";
		String baseContent = data.toString();
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			if (!"".equals(o_sblsh)) {
				sql = "update business_index set sbsj=?,state='00' where sblsh=? ";
				DbHelper.update(
						sql,
						new Object[] {
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_mm_SS),
								o_sblsh }, conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=?,EX_DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[] { objectType, baseContent,
						formId, dataId, ex_dataId, o_sblsh }, conn);
			} else {
				o_sblsh = Tools.getUUID32();
				sql = "insert into business_index (SBLSH,SXBM,SXID,"
						+ "SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"
						+ "LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"
						+ "YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"
						+ "SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"
						+ "SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"
						+ "TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"
						+ "BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"
						+ "STATE,URL,LIMIT_NUM,ASSORT,LY,CASECODE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				DbHelper.update(
						sql,
						new Object[] {
								o_sblsh,// 申办流水号类似的编码来确定事项
								data.get("itemCode"),
								data.get("itemId"),
								data.get("itemName"),
								data.get("ucid"),
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
								data.get("objectType"),
								data.get("zjhm"),
								data.get("sqrxm"),
								data.get("lxrxm"),
								data.get("lxrphone"),
								"关于" + data.get("sqrxm") + data.get("itemName")
										+ "的业务",
								o_sblsh,
								"",// BLFS办理方式
								data.get("regionName"),
								data.get("regionCode"),
								null,// YSLSJ业务受理时间
								"",// YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
								"",// YSLSBYY预受理失败原因
								null,// SLSJ预受理失败原因
								"",// YWLSH业务流水号
								data.get("orgName"), data.get("orgCode"),
								"",// SLZTDM受理状态代码(1受理，2不受理)
								"",// BSLYY不受理原因
								"",// SLHZH受理回执号
								"",// CXMM查询密码
								null,// SPSJ审批时间
								"",// SPHJDM审批环节代码(1承办，2审核，3批准)
								"",// SPHJMC审批环节名称
								null,// BZGZSJ补正告知时间
								"",// BZGZYY补正告知原因
								"",// BZCLQD补正材料清单
								null,// BZSJ补正时间
								null,// TBCXKSRQ特别程序开始日期
								"",// TBCXQDLY特别程序启动理由或依据
								"",// SQNR申请内容
								null,// TBCXJSRQ特别程序结束日期
								"",// TBCXJG特别程序结果
								null,// BJSJ办结时间
								"",// BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
								"",// BJJGMS办结结果描述
								"",// ZFHTHYY作废或退回原因
								null,// LQSJ领取时间
								"",// REMARK备注信息
								"00",// STATE业务状态
								"",// URL跳转地址
								"0",// limit_num 补正补齐次数
								data.get("assort"), data.get("ly"),
								data.get("Case_ID") }, conn);
				sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID,EX_DATAID) values (?,?,?,?,?,?)";
				DbHelper.update(sql, new Object[] { o_sblsh, objectType,
						baseContent, formId, dataId, ex_dataId }, conn);
			}
			conn.commit();
			return o_sblsh;
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			DBSource.closeConnection(conn);
		}
	}

	/**
	 * 业务与附件关联business_attach
	 * 
	 * @param receiveNum
	 * @param data
	 * @return
	 */
	public DataSet updateBusinessAttach(JSONObject data) {
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet ds = new DataSet();
		try {
			JSONArray ja = data.getJSONArray("metail");
			int len = ja.size();
			for (int i = 0; i < len; i++) {
				String sb = "select * from business_attach where docid=?";
				ds = this.executeDataset(sb, new Object[] { ja.getJSONObject(i)
						.getString("FILE_PATH") });
				if (ds.getState() == 1 && ds.getTotal() > 0) {
					String sblshAll = ds.getRecord(0).getString("SBLSH");
					sblshAll += data.getString("receiveNum") + ",";
					String sql = "update business_attach set sblsh=?,state='1' where docid = ?";
					int j = DbHelper.update(sql, new Object[] { sblshAll,
							ja.getJSONObject(i).getString("FILE_PATH") }, conn);
					conn.commit();
					ds.setState(StateType.SUCCESS);
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("");
				}
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	public JSONObject getCodeInfo(JSONObject data) {
		JSONObject codeInfo;
		String uniteCodeSwitch = SecurityConfig.getString("uniteCodeSwitch",
				"off");
		String itemId = data.getString("itemId");
		if ("off".equals(uniteCodeSwitch)) {
			HttpClientUtil client = new HttpClientUtil();
			String url = SecurityConfig.getString("approval_url");
			if ("zs".equals(SecurityConfig.getString("AppId"))) {
				String enter_situation = data.getString("enter_situation");
				if ("0".equals(enter_situation)) {
					url = SecurityConfig.getString("PowerOperation_url");
				}
			}
			if ("xy".equals(SecurityConfig.getString("AppId"))
					|| "weihai".equals(SecurityConfig.getString("AppId"))
					|| "ytsp".equals(SecurityConfig.getString("AppId"))
					|| "yt".equals(SecurityConfig.getString("AppId"))
					|| "fz".equals(SecurityConfig.getString("AppId"))
					|| "chq".equals(SecurityConfig.getString("AppId"))
					|| "zhangzhou".equals(SecurityConfig.getString("AppId"))) {
				url += "createReceiveNum?orgCode=" + data.getString("orgCode")
						+ "&itemCode=" + data.getString("itemCode")
						+ "&regCode=" + data.getString("regionCode")
						+ "&itemId=" + data.getString("itemId") + "&assort=1";
			} else {
				url += "/createReceiveNum?itemId=" + itemId + "&applyFrom="
						+ handlType;
			}
			url = HttpUtil.formatUrl(url);
			codeInfo = JSONObject.fromObject(client.getResult(url, ""));
			log.error("测试url"+url);
			codeInfo.put("unitecode", codeInfo.getString("receiveNum"));
		} else {
			String token = this.getToken(itemId);
			String urlCode = HttpUtil.formatUrl(SecurityConfig
					.getString("CodeServicesUrl")
					+ "/getUniteCode?appCode="
					+ appCode
					+ "&itemId="
					+ data.getString("itemId")
					+ "&regionCode="
					+ data.getString("regionCode")
					+ "&deptCode="
					+ data.getString("orgCode")
					+ "&handlType="
					+ handlType
					+ "&powerType="
					+ data.getString("powerType")
					+ "&token=" + token);
			HttpClientUtil clientCode = new HttpClientUtil();
			log.error("测试"+urlCode);
			codeInfo = JSONObject.fromObject(clientCode.getResult(urlCode, ""));
		}
		log.error("测试"+codeInfo);
		return codeInfo;
	}

	/**
	 * 生成统一编码服务的token
	 * 
	 * @param itemId
	 * @return
	 */
	public String getToken(String itemId) {
		String token = DigestUtils.md5Hex(appCode + itemId + salt);
		if (StringUtils.isNotEmpty(token)) {
			return token;
		} else {
			return "";
		}
	}

	// 向 pub_sms 存放待发送的短信内容
	public void sendMessageToUser(String smsContent, String channel,
			String phone) {
		String sql = "insert into pub_sms(id,smscontent,sendtime,channel,status,telephone) values(?,?,?,?,?,?)";
		this.executeUpdate(
				sql,
				new Object[] {
						Tools.getUUID32(),
						smsContent,
						CommonUtils.getInstance().parseStringToTimeStamp(
								Tools.formatDate(new Date(),
										CommonUtils.YYYY_MM_DD_HH_mm_SS),
								CommonUtils.YYYY_MM_DD_HH_mm_SS), channel, "0",
						phone });
	}

	// 向 pub_sms 存放待发送的短信内容-昆明盘龙
	public void sendMessageKmsPl(String smsContent_user,
			String smsContent_dept, String ucid, String channel, String phone,
			String orgCode, String noticeStyle) {
		try {
			String rid = SecurityConfig.getString("WebRegion");
			if (noticeStyle.equals("sms")) {
				// 插入用户通知短信
				String sql = "insert into pub_sms(id,rid,smscontent,sendtime,channel,status,telephone,noticeType) values(?,?,?,?,?,?,?,?)";
				this.executeUpdate(
						sql,
						new Object[] {
								Tools.getUUID32(),
								rid,
								smsContent_user,
								CommonUtils
										.getInstance()
										.parseStringToTimeStamp(
												Tools.formatDate(
														new Date(),
														CommonUtils.YYYY_MM_DD_HH_mm_SS),
												CommonUtils.YYYY_MM_DD_HH_mm_SS),
								channel, "0", phone, noticeStyle });

				// 插入 部门工作人员短信内容
				String phonestr = getPhoneStr(orgCode, "sms");
				String[] phones = phonestr.split(",");
				for (int i = 0; i < phones.length && !phones[i].equals(""); i++) {
					String sql2 = "INSERT INTO PUB_SMS(id,smscontent,status,telephone,rid,channel,sendtime,noticeType) values(?,?,?,?,?,?,?,?)";
					String id = Tools.getUUID32();
					this.executeUpdate(
							sql2,
							new Object[] {
									id,
									smsContent_dept,
									"0",
									phones[i],
									rid,
									channel,
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
									noticeStyle });
				}
			} else if (noticeStyle.equals("email")) {// 邮件
				String sql = "select email from uc_user where id=?";
				DataSet ds = this.executeDataset(sql, new Object[] { ucid });
				String email = "";
				if (ds != null && ds.getTotal() > 0) {
					email = ds.getRecord(0).getString("EMAIL");
				}
				if (!email.equals("")) {
					// 插入用户通知信息
					String sql2 = "insert into pub_sms(id,rid,smscontent,sendtime,channel,status,email,noticeType) values(?,?,?,?,?,?,?,?)";
					this.executeUpdate(
							sql2,
							new Object[] {
									Tools.getUUID32(),
									rid,
									smsContent_user,
									CommonUtils
											.getInstance()
											.parseStringToTimeStamp(
													Tools.formatDate(
															new Date(),
															CommonUtils.YYYY_MM_DD_HH_mm_SS),
													CommonUtils.YYYY_MM_DD_HH_mm_SS),
									channel, "0", email, noticeStyle });
				}
				// 插入部门管理员（工作人员）通知信息
				// 插入 部门工作人员短信内容
				String emailstr = getPhoneStr(orgCode, "email");
				String[] emails = emailstr.split(",");
				for (int i = 0; i < emails.length && !emails[i].equals(""); i++) {
					String sql2 = "INSERT INTO PUB_SMS(id,smscontent,status,telephone,rid,channel,sendtime,noticeType) values(?,?,?,?,?,?,?,?)";
					String id = Tools.getUUID32();
					this.executeUpdate(
							sql2,
							new Object[] {
									id,
									smsContent_dept,
									"0",
									emails[i],
									rid,
									channel,
									CommonUtils_api
											.getInstance()
											.parseDateToTimeStamp(
													new Date(),
													CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
									noticeStyle });
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取手机-邮件列表（盘龙）
	 * 
	 * @param pSet
	 * @return
	 */
	public String getPhoneStr(String dept_code, String noticeType) {
		String type_code = "20";// 在线申报
		StringBuffer str = new StringBuffer();
		DataSet ds = new DataSet();
		try {
			String region_code = SecurityConfig.getString("WebRegion");
			String sql = "select t.phone,t.email from pub_msg_config t where t.type_code = '"
					+ type_code
					+ "'"
					+ " and t.organ_code = '"
					+ dept_code
					+ "'" + " and t.region_code = '" + region_code + "'";
			ds = this.executeDataset(sql);
			if (ds != null) {
				if (noticeType.equals("sms")) {
					for (int i = 0; i < ds.getTotal(); i++) {
						if (i == ds.getTotal() - 1) {
							str.append(ds.getRecord(i).getString("PHONE"));
						} else {
							str.append(ds.getRecord(i).getString("PHONE") + ",");
						}
					}
				} else {
					for (int i = 0; i < ds.getTotal(); i++) {
						if (i == ds.getTotal() - 1) {
							str.append(ds.getRecord(i).getString("EMAIL"));
						} else {
							str.append(ds.getRecord(i).getString("EMAIL") + ",");
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		return str.toString();
	}

	// 保存EMS（福州）
	public void insertEms4Fz(JSONObject ems, String isEms, String sblsh,
			JSONObject data, String txLogisticID, String txLogisticID2)
			throws Exception {
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		conn.setAutoCommit(false);
		try {
			String sql = "insert into business_express (SBLSH,RECIEVER,RECIEVER_ADDRESS,RECIEVER_PHONE,SENDER,SENDER_PHONE,OPERATOR,OPERRATE_DATE,COM,ISTAKEN,ORDER_NUMBER,SEND_TYPE) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			DbHelper.update(
					sql,
					new Object[] {
							sblsh,
							ems.getString("emsAcceptName"),
							ems.getString("emsAcceptAddress"),
							ems.getString("emsAcceptPhone"),
							ems.getString("emstoName"),
							ems.getString("emstoPhone"),
							data.get("ucid"),
							CommonUtils_api.getInstance().parseDateToTimeStamp(
									new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							"EMS", "0", txLogisticID, "0" }, conn);

			// 回寄件
			if ("1".equals(isEms)) {
				sql = "insert into business_express (SBLSH,RECIEVER,RECIEVER_ADDRESS,RECIEVER_PHONE,SENDER,SENDER_PHONE,OPERATOR,OPERRATE_DATE,COM,ISTAKEN,ORDER_NUMBER,SEND_TYPE) values(?,?,?,?,?,?,?,?,?,?,?,?)";
				DbHelper.update(
						sql,
						new Object[] {
								sblsh,
								ems.getString("emsName"),
								ems.getString("emsAddress"),
								ems.getString("emsPhone"),
								ems.getString("emsAcceptName"),
								ems.getString("emsAcceptPhone"),
								data.get("ucid"),
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
								"EMS", "0", txLogisticID2, "1" }, conn);
			}

			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			DBSource.closeConnection(conn);
		}
	}

	// 更新EMS（福州）
	public void updateEms4Fz(JSONObject ems, String isEms, String sblsh,
			JSONObject data, String txLogisticID, String o_sblsh)
			throws Exception {
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		conn.setAutoCommit(false);
		try {
			String sql = "update business_express set RECIEVER=?,RECIEVER_ADDRESS=?,RECIEVER_PHONE=?,SENDER=?,SENDER_PHONE=?,OPERATOR=?,OPERRATE_DATE=?,COM=?,ISTAKEN=?,ORDER_NUMBER=? where SBLSH=? and send_type=0 ";
			DbHelper.update(
					sql,
					new Object[] {
							ems.getString("emsAcceptName"),
							ems.getString("emsAcceptAddress"),
							ems.getString("emsAcceptPhone"),
							ems.getString("emstoName"),
							ems.getString("emstoPhone"),
							data.get("ucid"),
							CommonUtils_api.getInstance().parseDateToTimeStamp(
									new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
							"EMS", "0", txLogisticID, o_sblsh }, conn);

			// 回寄件
			if ("1".equals(isEms)) {
				sql = "update business_express set RECIEVER=?,RECIEVER_ADDRESS=?,RECIEVER_PHONE=?,SENDER=?,SENDER_PHONE=?,OPERATOR=?,OPERRATE_DATE=?,COM=?,ISTAKEN=?,ORDER_NUMBER=? where SBLSH=? and send_type=1 ";
				DbHelper.update(
						sql,
						new Object[] {
								ems.getString("emsName"),
								ems.getString("emsAddress"),
								ems.getString("emsPhone"),
								ems.getString("emsAcceptName"),
								ems.getString("emsAcceptPhone"),
								data.get("ucid"),
								CommonUtils_api
										.getInstance()
										.parseDateToTimeStamp(
												new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
								"EMS", "0", txLogisticID, o_sblsh }, conn);
			}

			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			DBSource.closeConnection(conn);
		}
	}

	public DataSet submitUpData(JSONObject data) {
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet ds = new DataSet();
		try {
			String sblsh = data.getString("receiveNum");
			String sql = "update business_index set bjbzw=? where sblsh=? ";
			DbHelper.update(sql, new Object[] { "1", sblsh }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	public JSONObject getSbxx(String receiveNum) {
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		JSONObject data = new JSONObject();
		DataSet ds = new DataSet();
		try {
			String sql = "select b.sblsh receiveNum,b.cxmm receivePwd,b.sqrmc sqrxm,b.lxrxm lxrxm,"
					+ "u.email email,u.card_type identityType,u.card_no idcardNo,b.lxrsj linkPhone, "
					+ "u.name userName,m.user_id_map uuid,b.sxid itemId,b.sxbm itemCode,b.sxmc itemName,"
					+ "b.sjdwdm orgCode,b.sjdw orgName,b.xzqhdm regionCode, b.xzqh regionName,"
					+ "to_char(b.sbsj,'yyyy-mm-dd hh:MM:ss') receiveTime ,b.state action, b.sbxmmc projectname "
					+ "from business_index b, uc_user_map m,uc_user u "
					+ "left join uc_user_ext e on  u.id=e.id "
					+ "where  b.sblsh=? and b.ucid=u.id and b.ucid = m.user_id";
			ds = DbHelper.query(sql, new Object[] { receiveNum }, conn);
			data.put("receiveNum", ds.getRecord(0).getString("RECEIVENUM")); // 流水号
			data.put("receivePwd", ds.getRecord(0).getString("RECEIVEPWD")); // 查询密码
			data.put("sqrxm", ds.getRecord(0).getString("SQRXM")); // 申请人姓名
			data.put("lxrxm", ds.getRecord(0).getString("LXRXM")); // 联系人姓名
			data.put("email", ds.getRecord(0).getString("EMAIL"));// email
			data.put("identityType", ds.getRecord(0).getString("IDENTITYTYPE")); // 证件类型
			data.put("idcardNo", ds.getRecord(0).getString("IDCARDNO"));// 证件号码
			data.put("linkPhone", ds.getRecord(0).getString("LINKPHONE"));// 联系电话
			data.put("userName", ds.getRecord(0).getString("USERNAME"));// 用户名
			data.put("uuid", ds.getRecord(0).getString("UUID"));// uuid
			data.put("itemId", ds.getRecord(0).getString("ITEMID"));// 具体办件id
			data.put("itemCode", ds.getRecord(0).getString("ITEMCODE"));// 事项code
			data.put("itemName", ds.getRecord(0).getString("ITEMNAME"));// 事项名称
			data.put("orgCode", ds.getRecord(0).getString("ORGCODE"));// 部门code
			data.put("orgName", ds.getRecord(0).getString("ORGNAME"));// 部门名称
			data.put("regionCode", ds.getRecord(0).getString("REGIONCODE"));// 区划代码
			data.put("regionName", ds.getRecord(0).getString("REGIONNAME"));// 区划名称
			// data.put("innerCode", (String) obj.get("INNERCODE"));//内码
			data.put("receiveTime", ds.getRecord(0).getString("RECEIVETIME"));// 接受时间
			data.put("projectname", ds.getRecord(0).getString("PROJECTNAME"));// 申报项目名称
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		} finally {
			DBSource.closeConnection(conn);
		}
		return data;
	}

	// 历史办件数据推送至省系统
	public DataSet sendData() {
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet dataSet = new DataSet();
		try {
			conn.setAutoCommit(false);
			String sql = "select b.sblsh receiveNum,b.cxmm receivePwd,b.sqrmc sqrxm,b.lxrxm lxrxm,"
					+ "u.email email,u.card_type identityType,u.card_no idcardNo,b.lxrsj linkPhone, "
					+ "u.name userName,m.user_id_map uuid,b.sxid itemId,b.sxbm itemCode,b.sxmc itemName,"
					+ "b.sjdwdm orgCode,b.sjdw orgName,b.xzqhdm regionCode, b.xzqh regionName,"
					+ "to_char(b.sbsj,'yyyy-mm-dd hh:MM:ss') receiveTime ,b.state action, b.sbxmmc projectname "
					+ "from business_index b, uc_user_map m,uc_user u "
					+ "left join uc_user_ext e on  u.id=e.id "
					+ "where  b.bjbzw=? and b.ucid=u.id and b.ucid = m.user_id";
			DataSet ds = DbHelper.query(sql, new Object[] { "0" }, conn);
			JSONArray dsAll = ds.getJAData();
			JSONObject obj;
			// 存放数据的js串，接口中的data参数
			JSONObject data = new JSONObject();
			String action;
			for (int i = 0; i < ds.getTotal(); i++) {
				try {
					// 逐条进行填充 两个参数 一个 action 一个data 都是string
					obj = JSONObject.fromObject(dsAll.get(i));
					// 取出来action
					action = (String) obj.get("ACTION"); // 状态字典编码值
					if ("99".equals(obj.get("ACTION"))
							|| "98".equals(obj.get("ACTION"))) {
						action = "10";
					}

					// 对应的data String
					data.put("receiveNum", (String) obj.get("RECEIVENUM")); // 流水号
					data.put("receivePwd", (String) obj.get("RECEIVEPWD")); // 查询密码
					data.put("sqrxm", (String) obj.get("SQRXM")); // 申请人姓名
					data.put("lxrxm", (String) obj.get("LXRXM")); // 联系人姓名
					data.put("email", (String) obj.get("EMAIL"));// email
					data.put("identityType", (String) obj.get("IDENTITYTYPE")); // 证件类型
					data.put("idcardNo", (String) obj.get("IDCARDNO"));// 证件号码
					data.put("linkPhone", (String) obj.get("LINKPHONE"));// 联系电话
					data.put("userName", (String) obj.get("USERNAME"));// 用户名
					data.put("uuid", (String) obj.get("UUID"));// uuid
					data.put("itemId", (String) obj.get("ITEMID"));// 具体办件id
					data.put("itemCode", (String) obj.get("ITEMCODE"));// 事项code
					data.put("itemName", (String) obj.get("ITEMNAME"));// 事项名称
					data.put("orgCode", (String) obj.get("ORGCODE"));// 部门code
					data.put("orgName", (String) obj.get("ORGNAME"));// 部门名称
					data.put("regionCode", (String) obj.get("REGIONCODE"));// 区划代码
					data.put("regionName", (String) obj.get("REGIONNAME"));// 区划名称
					// data.put("innerCode", (String) obj.get("INNERCODE"));//内码
					data.put("receiveTime", (String) obj.get("RECEIVETIME"));// 接受时间
					data.put("projectname", (String) obj.get("PROJECTNAME"));// 申报项目名称
					try {
						String url = HttpUtil.formatUrl(SecurityConfig
								.getString("SubmitUrl"));
						Map<String, String> map = new HashMap<String, String>();
						map.put("data", data.toString());
						map.put("action", action);
						Object ret = RestUtil.postData(url, map);
						JSONObject json = JSONObject.fromObject(ret);
						if (json.getString("state").equals("1")) {
							dataSet.setState(StateType.SUCCESS);
							dataSet.setMessage("提交成功！");
							// 修改标志位
							int h = 0;
							sql = "update business_index set bjbzw='1' where sblsh=?";
							h = DbHelper.update(sql,
									new Object[] { obj.get("RECEIVENUM") },
									conn);
							if (h > 0) {
								conn.commit();
							} else {
								conn.rollback();
							}
						} else {
							dataSet.setRawData(json);
							dataSet.setState(StateType.FAILT);
							dataSet.setMessage("提交失败submitSL！");
						}
					} catch (Exception e) {
						e.printStackTrace();
						dataSet.setState(StateType.FAILT);
						dataSet.setMessage("提交失败submitSL！");
						return dataSet;
					}
				} catch (Exception e) {
					e.printStackTrace();
					conn.rollback();
				}
			} // 循环结束
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}
		return dataSet;
	}
	/**
	 * 获取情形代码
	 * @param pset
	 * @return
	 */
	public DataSet getCaseCode(String sblsh) {
		String sql = "select t.casecode from business_index t where t.sblsh = '"+sblsh+"'";
		return this.executeQuery(sql);
	}
}
