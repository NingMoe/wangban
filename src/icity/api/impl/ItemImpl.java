package api.impl;

import iop.http.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.guestbook.WriteDao;
import app.util.RestUtil;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.icore.util.URLEncode;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import com.icore.http.util.HttpUtil;

import core.util.CommonUtils;
import core.util.HttpClientUtil;
import core.util.CommonUtils_api;

@RestType(name = "api.item", descript = "状态更新相关接口")
public class ItemImpl extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ItemImpl.class);
	
	private static char[] codec_table = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
		'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
		'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', '+', '/' };
	
	public static ItemImpl getInstance(){
		return DaoFactory.getDao(ItemImpl.class.getName());
	}
	public DataSet setReturn(ParameterSet pSet) {
		System.out.println("【setReturn】:"+pSet.toString());
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		Timestamp d = null;		
		try {
			String state = (String) pSet.get("tag");
			String time = (String) pSet.get("pretreatmenttime");
			String opinion = (String) pSet.get("opinion");
			opinion = URLEncode.decodeURL(opinion);
			String receiveNum = (String) pSet.get("receiveNum");
			//威海市是否上门取件  16预审通过上门取件
			if("16".equals(state)&&"weihai".equals(SecurityConfig.getString("AppId"))){
				mailDataExchangeToEMS(receiveNum);
			}
			Object bzclqd = pSet.get("material");// 需要补齐的材料清单
			Object correctionTimes = pSet.get("correctionTimes");// 补齐时限
			Object correctionMaterial = pSet.get("correctionMaterial");// 窗口补齐后的材料列表			
			String yslztdm = "11";// 已提交，待预审
			String sql = "", bjjgdm = "", bjjgms = "", ysljgms = "";
			if (StringUtil.isNotEmpty(time)) {
				d = CommonUtils_api.getInstance().parseStringToTimestamp(time,
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			}
			if (state.equals("16") || state.equals("01") || state.equals("02")
					|| state.equals("14") || state.equals("21")
					|| state.equals("97") || state.equals("13")) {
				yslztdm = state;
				ysljgms = opinion;
				// 驳回-14或者补齐补正-21，可以再次进行提交
				if (state.equals("14") || state.equals("21")) {
					sql = "update business_index t set t.YSLSJ =?,t.YSLZTDM = ?,t.YSLJGMS=?,t.BZGZSJ=?,t.BZGZYY=?,t.BZCLQD=?,t.BZSX=?,t.state=?,t.pushstate=? where t.sblsh = ?";
					DbHelper.update(
							sql,
							new Object[] { d, yslztdm, ysljgms, d, ysljgms,
									bzclqd, correctionTimes, state,"1", receiveNum },
							conn);
				} else {
					if ("02".equals(state) || "13".equals(state)
							|| "97".equals(state)) {
						sql = "update business_index t set t.BJSJ =?, t.YSLSJ =?,t.YSLZTDM = ?,t.YSLJGMS=?,t.state=?,t.pushstate=? where t.sblsh = ?";
						DbHelper.update(sql, new Object[] { d, d, yslztdm,
								ysljgms, state,"1", receiveNum }, conn);
					} else {
						sql = "update business_index t set t.YSLSJ =?,t.YSLZTDM = ?,t.YSLJGMS=?,t.state=?,t.pushstate=? where t.sblsh = ?";
						DbHelper.update(sql, new Object[] { d, yslztdm,
								ysljgms, state,"1", receiveNum }, conn);
					}
				}

				conn.commit();
				ds.setState(StateType.SUCCESS);
				ds.setMessage("OK");
			} else if (state.equals("96") || state.equals("98")
					|| state.equals("99")) {
				bjjgdm = state;
				bjjgms = opinion;
				sql = "update business_index t set t.BJSJ =?,t.BJJGDM=?,t.BJJGMS = ?,t.state=?,t.license=?,t.pushstate=? where t.sblsh = ?";
				DbHelper.update(sql, new Object[] { d, bjjgdm, bjjgms, state,
						pSet.get("license"),// 证照材料列表
						"1",receiveNum }, conn);
				conn.commit();
				ds.setState(StateType.SUCCESS);
				ds.setMessage("OK");
			}
			// 补齐确认
			else if ("21-01".equals(state)) {
				sql = "update business_index t set BZSJ=?,state=?,limit_num=to_number(limit_num)+1,t.pushstate=? where sblsh=?";
				DbHelper.update(sql, new Object[] { d, "01","1", receiveNum }, conn);
				conn.commit();
			}
			if (correctionMaterial != null) {
				sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where sblsh = ?";
				DataSet r = DbHelper.query(sql, new Object[] { receiveNum },
						conn);
				JSONObject baseContent = r.getRecord(0).getJSONObject(
						"BASECONTENT");
				baseContent.put("metail",
						JSONArray.fromObject(correctionMaterial));
				sql = " update SUB_FOR_EX_APP_INFORMATION set basecontent = ? where sblsh = ?";
				DbHelper.update(sql, new Object[] { baseContent.toString(),
						receiveNum }, conn);
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
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 更新证照信息
	 * 
	 * @param pSet
	 *            state状态0提交1审核通过2审核不通过3挂起
	 * @return
	 */
	public DataSet setReturnLicense(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		Timestamp d = null;
		try {
			String state = (String) pSet.get("state");
			String reason = (String) pSet.get("reason");
			String licenseType = (String) pSet.get("licenseType");
			String licenseFileNumber = (String) pSet.get("licenseFileNumber");
			String id = (String) pSet.get("id");
			String org_name = (String) pSet.get("orgName");
			String sql = "update UC_LICENSE t set t.state =?,t.reason = ?,t.licenseType=?,t.licenseFileNumber=?,org_Name=? where t.id = ?";
			DbHelper.update(sql, new Object[] { state, reason, licenseType,
					licenseFileNumber, org_name, id }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 重庆企业设立，并联审批推送状态数据
	 * 
	 * @param pSet
	 * @return//获取表单内某元素的value 
	 *                         formiframe.document.getElementById("BianGengXiangMu"
	 *                         ).value
	 */
	public DataSet setEnterpriseState(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		Timestamp d = null;
		try {
			String firstcode = (String) pSet.get("firstcode");
			String state = (String) pSet.get("state");
			String dealtime = (String) pSet.get("dealtime");
			// _log.info("ENTERPRISE_BUSINESS_INDEX参数获取（状态）："+state);
			// _log.info("ENTERPRISE_BUSINESS_INDEX参数获取（时间）："+dealtime);
			// _log.info("ENTERPRISE_BUSINESS_INDEX参数获取（一级码）："+firstcode);
			d = CommonUtils_api.getInstance().parseStringToTimestamp(dealtime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String sql = null;
			if (state.equals("02")) {
				sql = "update ENTERPRISE_BUSINESS_INDEX set CURRENT_STATE=?,ACCEPT_TIME=? where FIRST_GRADE_CODE=?";
			} else if (state.equals("99")) {
				sql = "update ENTERPRISE_BUSINESS_INDEX set CURRENT_STATE=?,ACTUAL_FINISH_TIME=? where FIRST_GRADE_CODE=?";
			}
			DbHelper.update(sql, new Object[] { state, d, firstcode }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 重庆	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet setItemState(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		Timestamp d = null;
		try {
			String firstcode = (String) pSet.get("firstcode");
			String state = (String) pSet.get("state");
			String dealtime = (String) pSet.get("dealtime");
			String itemid = (String) pSet.get("itemid");
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（状态）："+state);
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（时间）："+dealtime);
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（一级码）："+firstcode);
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（itemid）："+itemid);
			d = CommonUtils_api.getInstance().parseStringToTimestamp(dealtime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String sql = null;
			if (state.equals("99")) {
				sql = "update ENTERPRISE_BUSINESS_COURSE set HANDLE_STATE=?,FINISH_TIME=? where FIRST_GRADE_CODE=? and ITEM_ID=?";
			} else if (state.equals("11")) {
				sql = "update ENTERPRISE_BUSINESS_COURSE set HANDLE_STATE=?,RECEIVE_TIME=? where FIRST_GRADE_CODE=? and ITEM_ID=?";
			}
			DbHelper.update(sql, new Object[] { state, d, firstcode, itemid },
					conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 重庆企业设立，设置一级码	 * 
	 * @param dcid企业标识码
	 * @param firstcode一级码
	 * @return 
	 *         {"total":0,"message":"错误信息","zip":0,"route":"","encrypt":0,"data":
	 *         null,"state":0}
	 */
	public DataSet setFirstCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String biz_id = "";
			String dcid = (String) pSet.get("dcid");
			String firstcode = (String) pSet.get("firstcode");
			// _log.info("setFirstCode参数获取（一级码）："+firstcode);
			// _log.info("setFirstCode参数获取（企业标识码 ）："+dcid);
			String sql = null;
			sql = "select t.id from ENTERPRISE_BUSINESS_INDEX t where t.receive_id=?";
			ds = DbHelper.query(sql, new Object[] { dcid }, conn);
			if (ds.getTotal() > 0) {
				JSONObject record = ds.getRecord(0);
				biz_id = record.getString("ID");
			}
			sql = "update ENTERPRISE_BUSINESS_INDEX set FIRST_GRADE_CODE=? where ID=?";
			DbHelper.update(sql, new Object[] { firstcode, biz_id }, conn);
			sql = "update ENTERPRISE_BUSINESS_COURSE set FIRST_GRADE_CODE=? where BIZ_ID=?";
			DbHelper.update(sql, new Object[] { firstcode, biz_id }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 审批事项查询密码更新	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updateItemspsxcxmm(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String spsxcxmm = (String) pSet.get("spsxcxmm");
			String sblsh = (String) pSet.get("sblsh");
			String sql = null;
			sql = "update business_index set spsxcxmm=? where sblsh=?";
			DbHelper.update(sql, new Object[] { spsxcxmm, sblsh }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 舟山 企业设立 更新业务状态	 * 
	 * @param pSet
	 *            bizid 业务标识 state 业务状态 dealtime 办理时间（时间格式：yyyy-MM-ddHH:mm:ss）
	 *            dealopinion 办理意见（描述）
	 * @return
	 */
	public DataSet setEnterpriseState_zs(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");

		Timestamp date = null;
		try {
			String bizid = (String) pSet.get("bizid");
			String state = (String) pSet.get("state");
			String dealtime = (String) pSet.get("dealtime");
			// _log.info("ENTERPRISE_BUSINESS_INDEX参数获取（状态）："+state);
			// _log.info("ENTERPRISE_BUSINESS_INDEX参数获取（时间）："+dealtime);
			date = CommonUtils_api.getInstance().parseStringToTimestamp(dealtime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String sql = null;
			//预审通过
			if ("02".equals(state)) {
				sql = "update ENTERPRISE_BUSINESS_INDEX set CURRENT_STATE=?,ACCEPT_TIME=? where RECEIVE_ID=?";
			} else if ("03".equals(state) || "99".equals(state)) {
				//不予受理或办结
				
				sql = "update ENTERPRISE_BUSINESS_INDEX set CURRENT_STATE=?,ACTUAL_FINISH_TIME=? where RECEIVE_ID=?";
			}
			DbHelper.update(sql, new Object[] { state, date, bizid }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 舟山 企业设立 更新事项状态	 * 
	 * @param pSet
	 *            bizid 业务标识 itemid 事项id state 状态 dealtime
	 *            办理时间（时间格式：yyyy-MM-ddHH:mm:ss） dealopinion 办理意见（描述）
	 * @return
	 */
	public DataSet setItemState_zs(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		Timestamp d = null;
		try {
			String receive_id = (String) pSet.get("bizid");
			String state = (String) pSet.get("state");
			String dealtime = (String) pSet.get("dealtime");
			String itemid = (String) pSet.get("itemid");
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（状态）："+state);
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（时间）："+dealtime);
			// _log.info("ENTERPRISE_BUSINESS_COURSE参数获取（itemid）："+itemid);
			String sql_index = "select * from ENTERPRISE_BUSINESS_INDEX where RECEIVE_ID = ?";
			DataSet __ds = new DataSet();
			__ds = DbHelper.query(sql_index, new Object[] { receive_id }, conn);
			String bizid = "";
			if (__ds.getTotal() > 0) {
				JSONObject record = __ds.getRecord(0);
				bizid = record.getString("ID");
			}

			d = CommonUtils_api.getInstance().parseStringToTimestamp(dealtime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String sql = null;
			if (state.equals("99")) {
				sql = "update ENTERPRISE_BUSINESS_COURSE set HANDLE_STATE=?,FINISH_TIME=? where BIZ_ID=? and ITEM_ID=?";
			} else if (state.equals("11")) {
				sql = "update ENTERPRISE_BUSINESS_COURSE set HANDLE_STATE=?,RECEIVE_TIME=? where BIZ_ID=? and ITEM_ID=?";
			}
			DbHelper.update(sql, new Object[] { state, d, bizid, itemid }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 接收踏勘的材料信息的接口	 * 
	 * @return
	 */
	public DataSet setMaterialInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String bizId = (String) pSet.get("bizId");
			JSONObject material = JSONObject.fromObject(pSet.get("material"));
			material = material.getJSONObject("info");
			JSONArray itemArray = material.getJSONArray("itemArray");
			int len = itemArray.size();
			String sql = null;
			if (len > 0) {
				String sql_index = "select t.id from ENTERPRISE_BUSINESS_INDEX t where t.receive_id=?";
				DataSet __ds = new DataSet();
				__ds = DbHelper.query(sql_index, new Object[] { bizId }, conn);
				if (__ds.getTotal() > 0) {
					JSONObject record = __ds.getRecord(0);
					bizId = record.getString("ID");
					for (int i = 0; i < len; i++) {
						String itemId = itemArray.getJSONObject(i).getString(
								"itemId");
						String itemName = itemArray.getJSONObject(i).getString(
								"itemName");
						JSONArray resourceArray = itemArray.getJSONObject(i)
								.getJSONArray("resourceArray");
						sql = "select 1 from ENTERPRISE_BUSINESS_COURSE where  biz_id = ? and item_id = ?";
						DataSet _ds = DbHelper.query(sql, new Object[] { bizId,
								itemId }, conn);
						if (_ds.getTotal() <= 0) {
							sql = "insert into ENTERPRISE_BUSINESS_COURSE "
									+ "(id,biz_id,Item_Id,Item_Name,Receive_Time,handle_state,Region_Code,is_lhtk,Material) "
									+ "values (?,?,?,?,sysdate,?,?,?,?) ";
							DbHelper.update(
									sql,
									new Object[] {
											Tools.getUUID32(),
											bizId,
											itemId,
											itemName,
											"10",
											SecurityConfig
													.getString("WebRegion"),
											"1", resourceArray.toString() },
									conn);
						}

					}
				}
			}
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 重庆,审批推送结果材料到网办 business_attach	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet insertBusinessAttach(ParameterSet pSet) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet ds = new DataSet();
		try {
			JSONArray ja = JSONArray.fromObject(pSet.get("metail"));
			int len = ja.size();
			for (int i = 0; i < len; i++) {
				String sqlucid = "select * from business_index where sblsh = ?";
				DataSet _ds = new DataSet();
				_ds = DbHelper.query(sqlucid, new Object[] { ja
						.getJSONObject(i).getString("receiveNum") }, conn);
				if (_ds.getTotal() > 0) {
					String sb = "select * from business_attach where docid=?";
					DataSet __ds = new DataSet();
					__ds = DbHelper.query(sb, new Object[] { ja
							.getJSONObject(i).getString("FILE_PATH") }, conn);
					if (__ds.getTotal() > 0) {
						String sblshAll = __ds.getRecord(0).getString("SBLSH");
						sblshAll += ja.getJSONObject(i).getString("receiveNum")
								+ ",";
						String sql = "update business_attach set sblsh=?,state='1',type='2' where docid = ?";
						int j = DbHelper.update(sql, new Object[] { sblshAll,
								ja.getJSONObject(i).getString("FILE_PATH") },
								conn);
						conn.commit();
						ds.setState(StateType.SUCCESS);
					} else {
						String sql = "insert into business_attach (id,type,sblsh,name,yname,docid,ucid,uploadtime,remark,state) values (?,?,?,?,?,?,?,?,?,?)";
						int j = DbHelper.update(
								sql,
								new Object[] {
										Tools.getUUID32(),
										"2",
										ja.getJSONObject(i).getString(
												"receiveNum"),
										ja.getJSONObject(i).getString("name"),
										ja.getJSONObject(i).getString("yname"),
										ja.getJSONObject(i).getString(
												"FILE_PATH"),
										_ds.getRecord(0).getString("UCID"),new Date(), "",
										"1" }, conn);
						conn.commit();
						ds.setState(StateType.SUCCESS);
					}
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

	/**
	 * 同步审批系统缴费信息	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updatePayStatusAndContent(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String sblsh = (String) pSet.get("receivenum");
			String paystatus = (String) pSet.get("paystatus");
			String paycontent = (String) pSet.get("paycontent");

			String sql = "update BUSINESS_INDEX set paystatus=?,paycontent=? where sblsh = ?";
			int i = DbHelper.update(sql, new Object[] { paystatus, paycontent,
					sblsh }, conn);
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("");
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 淄博：接口转换 获取事项列表	 * 
	 * @param pset
	 * @return
	 */
	public String getDisplayListByPage(ParameterSet pSet) {
		JSONObject obj = new JSONObject();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			Map<String, String> data=new HashMap<String, String>();
			data.put("page", pSet.getParameter("page")
					.toString());
			data.put("rows", pSet.getParameter("rows")
					.toString());
			data.put("whereValue",
					pSet.getParameter("whereValue").toString());
			data.put("paramValue",
					pSet.getParameter("paramValue").toString());
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	/**
	 * 淄博：接口转换 获取联审公示列表	 * 
	 * @param pset
	 * @return
	 */
	public String getDisplayLsListByPage(ParameterSet pSet) {
		JSONObject obj = new JSONObject();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getDisplayListByPage");
			Map<String, String> data=new HashMap<String, String>();
			data.put("page", pSet.getParameter("page")
					.toString());
			data.put("rows", pSet.getParameter("rows")
					.toString());
			data.put("whereValue",
					pSet.getParameter("whereValue").toString());
			data.put("paramValue",
					pSet.getParameter("paramValue").toString());
			data.put("getProject", "1");
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 * 淄博：接口转换 获取办件统计信息	 * 
	 * @param pset
	 * @return
	 */
	public String getAcceptQuantity(ParameterSet pSet) {
		String regionCode = pSet.getParameter("regionCode").toString();
		JSONObject json = new JSONObject();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url")
					+ "/getAcceptQuantity?regionCode=" + regionCode);
			HttpClientUtil client = new HttpClientUtil();
			json = JSONObject.fromObject(client.getResult(url, ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * 投资项目抄告短信发送(舟山)	 * 
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet cgSendMessage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			String phonestr = WriteDao.getInstance().getPhoneStr(pSet);
			String[] array = phonestr.split(",");
			String message = (String) pSet.getParameter("message");
			String channel = (String) pSet.getParameter("channel");
			String sql = "insert into pub_sms(ID, SMSCONTENT, SENDTIME,CHANNEL, STATUS,TELEPHONE, RID) values(?,?,?,?,?,?,?)";
			for (int i = 0; i < array.length; i++) {
				int j = DbHelper
						.update(sql,
								new Object[] {
										Tools.getUUID32(),
										message,
										CommonUtils
												.getInstance()
												.parseDateToTimeStamp(
														new Date(),
														CommonUtils.YYYY_MM_DD_HH_mm_SS),
										channel, "0", array[i],
										(String) pSet.getParameter("WebRegion") },
								conn);
			}
			ds.setState(StateType.SUCCESS);
			ds.setMessage("发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage("发送失败");
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;
	}
	
	/**
	 * 联审联办申办编码与联审联办内网同步（舟山）	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet updateApproveNumByBizId(ParameterSet pSet){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String receive_id = (String) pSet.get("bizId");
			String approve_num = (String) pSet.get("approveNum");
			String sql = "update enterprise_business_index set approve_num=? where receive_id=?";
			DbHelper.update(sql, new Object[] { approve_num, receive_id }, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		}catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			DBSource.closeConnection(conn);
		}
		return ds;		
	}
	/**
	 * 调用EMS上门取件接口，推送寄件信息
	 * @param pset
	 * @return
	 */
	public boolean mailDataExchangeToEMS(String receiveNum){
		DataSet ds = new DataSet();
		
		String sql = "select * from (select b.sblsh,b.order_number,b.istaken,s.basecontent from business_express b left join sub_for_ex_app_information s on b.sblsh=s.sblsh) t where t.sblsh = ? and t.istaken = ?";
		Connection conn = DbHelper.getConnection("icityDataSource");
		JSONObject ems = new JSONObject();
		DataSet bc = DbHelper.query(sql, new Object[]{receiveNum,"0"}, conn);
		
		if(bc.getTotal()>0){
			ems = bc.getJOData().getJSONObject("BASECONTENT").getJSONObject("ems");
		}else{
			return false;
		}
		String url = SecurityConfig.getString("ems_mailDataExchangeUrl");
		Map<String,String> map = new HashMap<String,String>();
		try {
			CommonUtils_api time = CommonUtils_api.getInstance();
			map.put("timestamp", time.parseDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
			map.put("sign", "");
			map.put("method", SecurityConfig.getString("ems_waybillMethod"));
			map.put("format", "json");
			map.put("app_key", SecurityConfig.getString("ems_appKey"));
			map.put("authorization", SecurityConfig.getString("ems_authorization"));
			map.put("size", "1");
			//取件地址：
			JSONObject collect = new JSONObject();
			collect.put("name", ems.getString("emstoName"));
			collect.put("mobile", ems.getString("emstoPhone"));
			String[] collectadd = new String[]{"","",""};
			collectadd = ems.getString("emstoAdd").split("-");
			collect.put("prov", collectadd[0]);
			collect.put("city", collectadd[1]);
			collect.put("county", collectadd[2]);
			collect.put("address", ems.getString("emstoAddress"));
			
			JSONObject receiver = new JSONObject();
			receiver.put("name", ems.getString("emsAcceptName"));
			receiver.put("mobile", ems.getString("emsAcceptPhone"));
			
			//String[] receiveradd = new String[]{"","",""};
			//测试收件地址//需改为事项提供
			//receiveradd = ems.getString("emstoAdd").split("-");
			receiver.put("prov", "山东省");
			receiver.put("city", "威海市");
			receiver.put("county", "");
			//end
			
			receiver.put("address", ems.getString("emsAcceptAddress"));
			
			JSONObject gotInfo = new JSONObject();
			gotInfo.put("collect",collect);
			gotInfo.put("sender",collect);
			gotInfo.put("receiver",receiver);
			
			gotInfo.put("txLogisticID",bc.getJOData().get("ORDER_NUMBER"));
			
			JSONArray gotInfoArray = new JSONArray();
			gotInfoArray.add(gotInfo);
			JSONObject gotInfo1 = new JSONObject();
			gotInfo1.put("gotInfo",gotInfoArray);
			
			//String gotInfo = "{\"gotInfo\":[{\"txLogisticID\":\"xh2016118880922ss\",\"mailNo\":\"32523525\",\"collect\":{\"name\":\"陈大\",\"mobile\":\"13800138000\",\"prov\":\"广东省\",\"city\":\"江门市\",\"county\":\"新会区\",\"address\":\"高新区泰康大厦\"},\"sender\":{\"name\":\"周杰伦\",\"postCode\":\"529000\",\"mobile\":\"13800138000\",\"prov\":\"广东省\",\"city\":\"江门市\",\"address\":\"高新区科技路9号\"},\"receiver\":{\"name\":\"张三丰\",\"postCode\":\"529000\",\"mobile\":\"17715369021\",\"prov\":\"广东省\",\"city\":\"江门市\",\"address\":\"税务局8座\"}}]}";
			//JSONObject obj1 = JSONObject.fromObject(gotInfo);
			map.put("gotInfo", gotInfo1.toString());
			map.put("version", "V0.1");
			
			String content = getSortParams(map) + SecurityConfig.getString("ems_appSecret");
			content = sign(content,"utf-8");
			map.put("sign",content);
			String oj = RestUtil.postData(url, map);
			
			JSONObject obj = JSONObject.fromObject(oj);
			if("T".equals(obj.getString("success"))){
				ds.setRawData(gotInfo);
				ds.setMessage("T");
				ds.setState(StateType.SUCCESS);
				sql = "update business_express set istaken = ? where order_number = ?";
				DbHelper.update(sql, new Object[]{"1",bc.getJOData().get("ORDER_NUMBER")}, conn);
			}else{
				ds.setState(StateType.FAILT);
				_log.error(obj.getString("errorMsg"));
				ds.setMessage(obj.getString("errorMsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("mailDataExchangeToEMS错误信息："+e.getMessage());
			ds.setState(StateType.FAILT);
		}
		return true;
	}
	/**
	 * ems生成签名方法（步骤一）
	 * @param params
	 * @return
	 */
	public static String getSortParams(Map<String, String> params) {
		// 删掉sign参数
		params.remove("sign");
		String contnt = "";
		Set<String> keySet = params.keySet();
		List<String> keyList = new ArrayList<String>();
		for (String key : keySet) {
			String value = params.get(key);
			// 将值为空的参数排除
			if (StringUtil.isNotEmpty(value)) {
				keyList.add(key);
			}
		}
		Collections.sort(keyList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int length = Math.min(o1.length(), o2.length());
				for (int i = 0; i < length; i++) {
					char c1 = o1.charAt(i);
					char c2 = o2.charAt(i);
					int r = c1 - c2;
					if (r != 0) {
						// char值小的排前边
						return r;
					}
				}
				// 2个字符串关系是str1.startsWith(str2)==true
				// 取str2排前边
				return o1.length() - o2.length();
			}
		});
		// 将参数和参数值按照排序顺序拼装成字符串
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			contnt += key + params.get(key);
		}
		return contnt;
	}

	/**
	 * ems生成签名方法（步骤二）
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String sign(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			charset = "UTF-8";
		}
		String sign = "";
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			sign = encode(md5.digest(content.getBytes(charset)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sign;
	}
	
	/**
	 * EMS取件（福州）
	 * 
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DataSet sendEmsData(ParameterSet pSet) {
		
		DataSet ds = new DataSet();
		String receiveNum = (String) pSet.get("receiveNum");
		//0：寄件（用户到行政中心）1：回寄件（行政中心到用户）
		String sendType = (String) pSet.get("sendType");
		//0：外网申报的件 1：内网申报的件
		String businessType = (String) pSet.get("businessType");
		
		if("0".equals(businessType)){
			
			String sql = "select * from (select b.sblsh,b.order_number,b.istaken,b.send_type,s.basecontent from business_express b left join sub_for_ex_app_information s on b.sblsh=s.sblsh) t where t.sblsh = ? and t.send_type = ? ";
			Connection conn = DbHelper.getConnection("icityDataSource");
			JSONObject ems = new JSONObject();
			DataSet bc = DbHelper.query(sql, new Object[]{receiveNum,sendType}, conn);
			
			if(bc.getTotal()>0){
				ems = bc.getJOData().getJSONObject("BASECONTENT").getJSONObject("ems");
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("未获取到EMS信息");
				
				return ds;
			}
			
			String url = SecurityConfig.getString("ems_mailDataExchangeUrl");
			
			Map<String,String> map = new HashMap<String,String>();
			try {
				CommonUtils_api time = CommonUtils_api.getInstance();
				map.put("timestamp", time.parseDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
				map.put("sign", "");
				map.put("method", SecurityConfig.getString("ems_waybillMethod"));
				map.put("format", "json");
				map.put("app_key", SecurityConfig.getString("ems_appKey"));
				map.put("authorization", SecurityConfig.getString("ems_authorization"));
				map.put("size", "1");

				JSONObject collect = new JSONObject();
				if("0".equals(sendType)){
					collect.put("name", ems.getString("emstoName"));
					collect.put("mobile", ems.getString("emstoPhone"));
					String[] collectadd = new String[]{"","",""};
					collectadd = ems.getString("emstoAdd").split("-");
					collect.put("prov", collectadd[0]);
					collect.put("city", collectadd[1]);
					collect.put("county", collectadd[2]);
					collect.put("address", ems.getString("emstoAddress"));
				}else if("1".equals(sendType)){
					collect.put("name", ems.getString("emsAcceptName"));
					collect.put("mobile", ems.getString("emsAcceptPhone"));
					String[] collectadd = new String[]{"","",""};
					collectadd = ems.getString("emsAdd").split("-");
					collect.put("prov", collectadd[0]);
					collect.put("city", collectadd[1]);
					collect.put("county", collectadd[2]);
					collect.put("address", ems.getString("emsAcceptAddress"));
				}
				
				JSONObject receiver = new JSONObject();
				if("0".equals(sendType)){
					receiver.put("name", ems.getString("emsAcceptName"));
					receiver.put("mobile", ems.getString("emsAcceptPhone"));
					receiver.put("prov", "福建省");
					receiver.put("city", "福州市");
					receiver.put("county", "");
					receiver.put("address", ems.getString("emsAcceptAddress"));
				}else if("1".equals(sendType)){
					receiver.put("name", ems.getString("emstoName"));
					receiver.put("mobile", ems.getString("emstoPhone"));
					receiver.put("prov", "福建省");
					receiver.put("city", "福州市");
					receiver.put("county", "");
					receiver.put("address", ems.getString("emstoAddress"));
				}
				
				JSONObject gotInfo = new JSONObject();
				gotInfo.put("collect",collect);
				gotInfo.put("sender",collect);
				gotInfo.put("receiver",receiver);
				gotInfo.put("txLogisticID",bc.getJOData().get("ORDER_NUMBER"));
				gotInfo.put("remark",SecurityConfig.getString("emsFrontMessage"));
				
				JSONArray gotInfoArray = new JSONArray();
				gotInfoArray.add(gotInfo);
				JSONObject gotInfo1 = new JSONObject();
				gotInfo1.put("gotInfo",gotInfoArray);
				
				map.put("gotInfo", gotInfo1.toString());
				map.put("version", "V0.1");
				
				String content = getSortParams(map) + SecurityConfig.getString("ems_appSecret");
				content = sign(content,"utf-8");
				map.put("sign",content);
				
				String oj = RestUtil.postData(url, map);
				JSONObject obj = JSONObject.fromObject(oj);
				
				if("T".equals(obj.getString("success"))){
					ds.setRawData(gotInfo);
					ds.setMessage("T");
					ds.setState(StateType.SUCCESS);
					sql = "update business_express set istaken = ? where order_number = ?";
					DbHelper.update(sql, new Object[]{"1",bc.getJOData().get("ORDER_NUMBER")}, conn);
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage(obj.getString("errorMsg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("mailDataExchangeToEMS错误信息："+e.getMessage());
			}
		}else if("1".equals(businessType)){
			
			//获取EMS信息
			String emsData = (String) pSet.get("emsData");
			JSONObject objEms = JSONObject.fromObject(emsData);
			
			//内网订单号由外网生成
			String orderNum = Tools.getUUID32();
			
			String url = SecurityConfig.getString("ems_mailDataExchangeUrl");
			Map<String,String> map = new HashMap<String,String>();
			try {
				CommonUtils_api time = CommonUtils_api.getInstance();
				map.put("timestamp", time.parseDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
				map.put("sign", "");
				map.put("method", SecurityConfig.getString("ems_waybillMethod"));
				map.put("format", "json");
				map.put("app_key", SecurityConfig.getString("ems_appKey"));
				map.put("authorization", SecurityConfig.getString("ems_authorization"));
				map.put("size", "1");

				JSONObject collect = new JSONObject();
				collect.put("name", objEms.getString("collectName"));
				collect.put("mobile", objEms.getString("collectPhone"));
				collect.put("prov", objEms.getString("collectProv"));
				collect.put("city", objEms.getString("collectCity"));
				collect.put("county", objEms.getString("collectCounty"));
				collect.put("address", objEms.getString("collectAddress"));
				
				JSONObject receiver = new JSONObject();
				receiver.put("name", objEms.getString("receiverName"));
				receiver.put("mobile", objEms.getString("receiverPhone"));
				receiver.put("prov", objEms.getString("receiverProv"));
				receiver.put("city", objEms.getString("receiverCity"));
				receiver.put("county", objEms.getString("receiverCounty"));
				receiver.put("address", objEms.getString("receiverAddress"));
				
				JSONObject gotInfo = new JSONObject();
				gotInfo.put("collect",collect);
				gotInfo.put("sender",collect);
				gotInfo.put("receiver",receiver);
				gotInfo.put("txLogisticID",orderNum);
				gotInfo.put("remark",SecurityConfig.getString("emsBackMessage"));
				
				JSONArray gotInfoArray = new JSONArray();
				gotInfoArray.add(gotInfo);
				JSONObject gotInfo1 = new JSONObject();
				gotInfo1.put("gotInfo",gotInfoArray);
				
				map.put("gotInfo", gotInfo1.toString());
				map.put("version", "V0.1");
				
				String content = getSortParams(map) + SecurityConfig.getString("ems_appSecret");
				content = sign(content,"utf-8");
				map.put("sign",content);
				
				String oj = RestUtil.postData(url, map);
				JSONObject obj = JSONObject.fromObject(oj);
				if("T".equals(obj.getString("success"))){
					
					//订单号等信息反馈到内网
					ds.setRawData(gotInfo);
					ds.setMessage("T");
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage(obj.getString("errorMsg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("mailDataExchangeToEMS错误信息："+e.getMessage());
			}
		}
		
		return ds;
	}
	
	public static String encode(byte[] a) {
		
		int totalBits = a.length * 8;
		int nn = totalBits % 6;
		int curPos = 0;// process bits
		StringBuffer toReturn = new StringBuffer();
		while (curPos < totalBits) {
			int bytePos = curPos / 8;
			switch (curPos % 8) {
			case 0:
				toReturn.append(codec_table[(a[bytePos] & 0xfc) >> 2]);
				break;
			case 2:

				toReturn.append(codec_table[(a[bytePos] & 0x3f)]);
				break;
			case 4:
				if (bytePos == a.length - 1) {
					toReturn.append(codec_table[((a[bytePos] & 0x0f) << 2) & 0x3f]);
				} else {
					int pos = (((a[bytePos] & 0x0f) << 2) | ((a[bytePos + 1] & 0xc0) >> 6)) & 0x3f;
					toReturn.append(codec_table[pos]);
				}
				break;
			case 6:
				if (bytePos == a.length - 1) {
					toReturn.append(codec_table[((a[bytePos] & 0x03) << 4) & 0x3f]);
				} else {
					int pos = (((a[bytePos] & 0x03) << 4) | ((a[bytePos + 1] & 0xf0) >> 4)) & 0x3f;
					toReturn.append(codec_table[pos]);
				}
				break;
			default:
				// never hanppen
				break;
			}
			curPos += 6;
		}
		if (nn == 2) {
			toReturn.append("==");
		} else if (nn == 4) {
			toReturn.append("=");
		}
		return toReturn.toString();
	}
	/**
	 * 審批調接口推送窗口收的辦件（威海）
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public DataSet synchronousData(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data = JSONObject.fromObject(pSet.get("data"));
		String o_sblsh=data.getString("receiveNum");
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String ex_dataId = data.containsKey("ex_dataId")?data.getString("ex_dataId"):"";
		String baseContent = data.toString();
		String sql="";
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String sql_select = "select 1 from business_Index  where sblsh = ?";
			DataSet ds_select = DbHelper.query(sql_select, new Object[]{o_sblsh}, conn);
			if(ds_select.getTotal()>0){
				ds.setState((byte)2);
				ds.setMessage("办件已存在");
			}else{
				sql = "insert into business_index (SBLSH,SXBM,SXID,"+
				"SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"+
				"LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"+
				"YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"+
				"SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"+
				"SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"+
				"TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"+
				"BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"+
				"STATE,URL,LIMIT_NUM,ASSORT) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				DbHelper.update(sql, new Object[]{
						o_sblsh ,//申办流水号类似的编码来确定事项 
						data.get("itemCode"),
						data.get("itemId"),
						data.get("itemName"),
						data.get("ucid"),
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
						data.get("objectType"),
						data.get("zjhm"),
						data.get("sqrxm"),
						data.get("lxrxm"),
						data.get("lxrphone"),
						"关于"+data.get("sqrxm")+data.get("itemName")+"的业务",
						o_sblsh,
						"",//BLFS办理方式
						data.get("regionName"),
						data.get("regionCode"),
						null,//YSLSJ业务受理时间
						"",//YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
						"",//YSLSBYY预受理失败原因
						null,//SLSJ预受理失败原因
						"",//YWLSH业务流水号
						data.get("orgName"),
						data.get("orgCode"),
						"",//SLZTDM受理状态代码(1受理，2不受理)
						"",//BSLYY不受理原因
						"",//SLHZH受理回执号
						data.get("passWord"),//CXMM查询密码
						null,//SPSJ审批时间
						"",//SPHJDM审批环节代码(1承办，2审核，3批准)
						"",//SPHJMC审批环节名称
						null,//BZGZSJ补正告知时间
						"",//BZGZYY补正告知原因
						"",//BZCLQD补正材料清单
						null,//BZSJ补正时间
						null,//TBCXKSRQ特别程序开始日期
						"",//TBCXQDLY特别程序启动理由或依据
						"",//SQNR申请内容
						null,//TBCXJSRQ特别程序结束日期
						"",//TBCXJG特别程序结果
						null,//BJSJ办结时间
						"",//BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
						"",//BJJGMS办结结果描述
						"",//ZFHTHYY作废或退回原因
						null,//LQSJ领取时间
						"",//REMARK备注信息
						"00",//STATE业务状态
						"",//URL跳转地址
						"0",//limit_num 补正补齐次数
						data.get("assort")
					}, conn);
				sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID,EX_DATAID) values (?,?,?,?,?,?)";
				DbHelper.update(sql, new Object[]{o_sblsh,objectType,baseContent,formId,dataId,ex_dataId}, conn);
				conn.commit();
				ds.setState((byte)1);
				ds.setMessage("同步成功");
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    	ds.setState((byte)0);
			ds.setMessage("同步失败:"+e.toString());
			try
			{
				conn.rollback();
			} catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
	    }finally{
	    	DBSource.closeConnection(conn);
	    }
	    return ds;
	}
}
