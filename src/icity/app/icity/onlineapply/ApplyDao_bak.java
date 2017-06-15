package app.icity.onlineapply;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.StateType;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;

public class ApplyDao_bak extends BaseJdbcDao {

	private ApplyDao_bak() {
		this.setDataSourceName("icityDataSource");
	}

	public static ApplyDao_bak getInstance() {
		return DaoFactory.getDao(ApplyDao_bak.class.getName());
	}

	public void submitData(String o_sblsh, JSONObject receiveNum, JSONObject data) throws Exception {
		String sblsh = receiveNum.getString("receiveNum");
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String state = data.getString("state");
		String baseContent = data.toString();
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			if ("sp".equals(state) || "ql".equals(state)) {
				if (!"".equals(o_sblsh)) {
					sql = "update business_index set sblsh=?,state='11',SQRZJHM=?,SQRMC=?,LXRXM=?,LXRSJ=?,SBXMMC=?,sbsj=? where sblsh=? ";
					DbHelper.update(sql,
							new Object[] { sblsh, data.get("zjhm"),
									data.get("sqrxm"), data
											.get("lxrxm"),
							data.get("lxrphone"),
							"关于" + data.get("sqrxm") + data.get("itemName") + "的业务", CommonUtils_api.getInstance()
									.parseDateToTimeStamp(new Date(), CommonUtils_api.YYYY_MM_DD_HH_mm_SS), o_sblsh },
							conn);
					sql = " update SUB_FOR_EX_APP_INFORMATION set SBLSH=?, OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
					DbHelper.update(sql, new Object[] { sblsh, objectType, baseContent, formId, dataId, o_sblsh },
							conn);
				} else {
					sql = "insert into business_index (SBLSH,SXBM,SXID," + "SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"
							+ "LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM," + "YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"
							+ "SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ," + "SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"
							+ "TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG," + "BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"
							+ "STATE,URL,LIMIT_NUM,ASSORT) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					if ("ql".equals(data.getString("state"))) {
						DbHelper.update(sql,
								new Object[] { sblsh, // 申办流水号类似的编码来确定事项
										data.get("itemCode"), data.get("itemId"), data.get("itemName"),
										data.get("ucid"),
										CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), //
								data.get("objectType"), data.get("zjhm"), data.get("sqrxm"), data.get("lxrxm"),
								data.get("lxrphone"), "关于" + data.get("sqrxm") + data.get("itemName") + "的业务", sblsh,
								"", // BLFS办理方式
								data.get("regionName"), data.get("regionCode"), null, // YSLSJ业务受理时间
								"", // YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
								"", // YSLSBYY预受理失败原因
								null, // SLSJ预受理失败原因
								"", // YWLSH业务流水号
								data.get("orgName"), data.get("orgCode"), // 收件单位代码
								"", // SLZTDM受理状态代码(1受理，2不受理)
								"", // BSLYY不受理原因
								"", // SLHZH受理回执号
								"", // CXMM查询密码
								null, // SPSJ审批时间
								"", // SPHJDM审批环节代码(1承办，2审核，3批准)
								"", // SPHJMC审批环节名称
								null, // BZGZSJ补正告知时间
								"", // BZGZYY补正告知原因
								"", // BZCLQD补正材料清单
								null, // BZSJ补正时间
								null, // TBCXKSRQ特别程序开始日期
								"", // TBCXQDLY特别程序启动理由或依据
								"", // SQNR申请内容
								null, // TBCXJSRQ特别程序结束日期
								"", // TBCXJG特别程序结果
								null, // BJSJ办结时间
								"", // BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
								"", // BJJGMS办结结果描述
								"", // ZFHTHYY作废或退回原因
								null, // LQSJ领取时间
								"ql", // REMARK备注信息
								"11", // STATE业务状态
								"", // URL跳转地址
								"0", // limit_num 补正补齐次数
								data.get("assort") }, conn);
					} else {
						DbHelper.update(sql,
								new Object[] { sblsh, // 申办流水号类似的编码来确定事项
										data.get("itemCode"), data.get("itemId"), data.get("itemName"),
										data.get("ucid"),
										CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
												CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), //
								data.get("objectType"), data.get("zjhm"), data.get("sqrxm"), data.get("lxrxm"),
								data.get("lxrphone"), "关于" + data.get("sqrxm") + data.get("itemName") + "的业务", sblsh,
								"", // BLFS办理方式
								data.get("regionName"), data.get("regionCode"), null, // YSLSJ业务受理时间
								"", // YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
								"", // YSLSBYY预受理失败原因
								null, // SLSJ预受理失败原因
								"", // YWLSH业务流水号
								data.get("orgName"), data.get("orgCode"), // 收件单位代码
								"", // SLZTDM受理状态代码(1受理，2不受理)
								"", // BSLYY不受理原因
								"", // SLHZH受理回执号
								"", // CXMM查询密码
								null, // SPSJ审批时间
								"", // SPHJDM审批环节代码(1承办，2审核，3批准)
								"", // SPHJMC审批环节名称
								null, // BZGZSJ补正告知时间
								"", // BZGZYY补正告知原因
								"", // BZCLQD补正材料清单
								null, // BZSJ补正时间
								null, // TBCXKSRQ特别程序开始日期
								"", // TBCXQDLY特别程序启动理由或依据
								"", // SQNR申请内容
								null, // TBCXJSRQ特别程序结束日期
								"", // TBCXJG特别程序结果
								null, // BJSJ办结时间
								"", // BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
								"", // BJJGMS办结结果描述
								"", // ZFHTHYY作废或退回原因
								null, // LQSJ领取时间
								"sp", // REMARK备注信息
								"11", // STATE业务状态
								"", // URL跳转地址
								"0", // limit_num 补正补齐次数
								data.get("assort") }, conn);
					}

					sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values (?,?,?,?,?)";
					DbHelper.update(sql, new Object[] { sblsh, objectType, baseContent, formId, dataId }, conn);
				}

			} else if ("bqbz".equals(state) || "clbqbz".equals(state)) {
				// 可进行多次补齐补正,或者驳回后的提交
				sql = "select to_char(BZGZSJ,'yyyy-MM-dd HH24:mi:ss') BZGZSJ, BZGZYY from business_index where sblsh = ?";
				DataSet result = DbHelper.query(sql, new Object[] { sblsh }, conn);
				sql = "insert into BUSINESS_BZBQ_LOG (SBLSH,BZBQSJ,BZBQYY) values(?,to_date(?,'yyyy-MM-dd HH24:MI:ss'),?)";
				DbHelper.update(sql, new Object[] { sblsh, result.getRecord(0).getString("BZGZSJ"),
						result.getRecord(0).getString("BZGZYY") }, conn);
				sql = "update business_index set BZSJ=?,state=?,SQRZJHM=?,SQRMC=?,LXRXM=?,LXRSJ=?,SBXMMC=?,limit_num=to_number(limit_num)+1 where sblsh=?";
				DbHelper.update(sql,
						new Object[] {
								CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
										CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
								"bqbz".equals(state) ? "11" : "01", data.get("zjhm"), data.get("sqrxm"),
								data.get("lxrxm"), data.get("lxrphone"),
								"关于" + data.get("sqrxm") + data.get("itemName") + "的业务", sblsh },
						conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[] { objectType, baseContent, formId, dataId, sblsh }, conn);
			}
			conn.commit();
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
		String baseContent = data.toString();
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			if (!"".equals(o_sblsh)) {
				sql = "update business_index set sbsj=?,state='00' where sblsh=? ";
				DbHelper.update(sql, new Object[] { CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS), o_sblsh }, conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[] { objectType, baseContent, formId, dataId, o_sblsh }, conn);
			} else {
				o_sblsh = Tools.getUUID32();
				sql = "insert into business_index (SBLSH,SXBM,SXID," + "SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"
						+ "LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM," + "YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"
						+ "SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ," + "SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"
						+ "TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG," + "BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"
						+ "STATE,URL,LIMIT_NUM,ASSORT) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				DbHelper.update(sql,
						new Object[] { o_sblsh, // 申办流水号类似的编码来确定事项
								data.get("itemCode"), data.get("itemId"), data.get("itemName"), data.get("ucid"),
								CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
										CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), //
						data.get("objectType"), data.get("zjhm"), data.get("sqrxm"), data.get("lxrxm"),
						data.get("lxrphone"), "关于" + data.get("sqrxm") + data.get("itemName") + "的业务", o_sblsh, "", // BLFS办理方式
						data.get("regionName"), data.get("regionCode"), null, // YSLSJ业务受理时间
						"", // YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
						"", // YSLSBYY预受理失败原因
						null, // SLSJ预受理失败原因
						"", // YWLSH业务流水号
						data.get("orgName"), data.get("orgCode"), "", // SLZTDM受理状态代码(1受理，2不受理)
						"", // BSLYY不受理原因
						"", // SLHZH受理回执号
						"", // CXMM查询密码
						null, // SPSJ审批时间
						"", // SPHJDM审批环节代码(1承办，2审核，3批准)
						"", // SPHJMC审批环节名称
						null, // BZGZSJ补正告知时间
						"", // BZGZYY补正告知原因
						"", // BZCLQD补正材料清单
						null, // BZSJ补正时间
						null, // TBCXKSRQ特别程序开始日期
						"", // TBCXQDLY特别程序启动理由或依据
						"", // SQNR申请内容
						null, // TBCXJSRQ特别程序结束日期
						"", // TBCXJG特别程序结果
						null, // BJSJ办结时间
						"", // BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
						"", // BJJGMS办结结果描述
						"", // ZFHTHYY作废或退回原因
						null, // LQSJ领取时间
						"", // REMARK备注信息
						"00", // STATE业务状态
						"", // URL跳转地址
						"0", // limit_num 补正补齐次数
						data.get("assort") }, conn);
				sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values (?,?,?,?,?)";
				DbHelper.update(sql, new Object[] { o_sblsh, objectType, baseContent, formId, dataId }, conn);
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
	public DataSet updateBusinessAttach(JSONObject receiveNum, JSONObject data) {
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet ds = new DataSet();
		try {
			JSONArray ja = data.getJSONArray("metail");
			int len = ja.size();
			for (int i = 0; i < len; i++) {
				String sb = "select * from business_attach where docid=?";
				ds = this.executeDataset(sb, new Object[] { ja.getJSONObject(i).getString("FILE_PATH") });
				if (ds.getState() == 1 && ds.getTotal() > 0) {
					String sblshAll = ds.getRecord(0).getString("SBLSH");
					sblshAll += receiveNum.getString("receiveNum") + ",";
					String sql = "update business_attach set sblsh=?,state='1' where docid = ?";
					int j = DbHelper.update(sql, new Object[] { sblshAll, ja.getJSONObject(i).getString("FILE_PATH") },
							conn);
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
}
