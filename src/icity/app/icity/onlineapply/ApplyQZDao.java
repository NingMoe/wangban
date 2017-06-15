package app.icity.onlineapply;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import org.apache.commons.lang.StringUtils;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import plugin.Plugin_Util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApplyQZDao extends BaseJdbcDao {

	private static Log log_ = LogFactory.getLog(ApplyQZDao.class);

	private ApplyQZDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ApplyQZDao getInstance() {
		return (ApplyQZDao) DaoFactory.getDao(ApplyQZDao.class.getName());
	}

	public void handleData() {
		String sql = "", sblsh = "";
		String power_source = "C";// 默认是使用统建的审批系统
		JSONObject data;
		String itemId = "";
		String version = "";
		String enter_situation = "1";
		Connection conn = null;
		// 错误日志信息
		String error_name = "";
		String error_sign = "";
		String content = "";
		String error_type = "在线申报数据";
		try {
			conn = DbHelper.getConnection(this.getDataSourceName());
			conn.setAutoCommit(false);
			// 一次处理50条数据
			sql = "select i.sblsh,i.SBXMMC,s.basecontent from business_index i inner join sub_for_ex_app_information s on i.sblsh=s.sblsh "
					+ " where i.signstate='1'"
					+ " AND not exists(select e.error_sign from PUB_DATAEXCHANGE_ERROR_LOG e where e.error_sign = i.sblsh AND e.send_count>='3')"
					+ " ORDER BY i.sbsj asc";
			DataSet ds = this.executeDataset(sql, 0, 50, new Object[] {},
					this.getDataSourceName());
			for (int i = 0; i < ds.getTotal(); i++) {
				try {
					sblsh = ds.getRecord(i).getString("SBLSH");
					error_name = ds.getRecord(i).getString("SBXMMC");
					error_sign = sblsh;
					// error_name =
					data = ds.getRecord(i).getJSONObject("BASECONTENT");
					String url = SecurityConfig
							.getString("ItemSynchronizationUrl");
					itemId = data.getString("itemId");
					if (StringUtils.isNotEmpty(itemId)) {
						url += "/getAllItemInfoByItemID?itemId=" + itemId;
					}
					url = HttpUtil.formatUrl(url);
					HttpClientUtil client = new HttpClientUtil();
					JSONObject item = JSONObject.fromObject(client.getResult(
							url, ""));
					JSONArray itemInfo = item.getJSONArray("ItemInfo");// 基本信息
					JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
					version = itemBasicInfo.getString("VERSION");
					if (itemBasicInfo.containsKey("ENTER_SITUATION")) {
						enter_situation = itemBasicInfo
								.getString("ENTER_SITUATION");
					}
					if (data.containsKey("power_source")) {
						power_source = data.getString("power_source");
					}
					// 更新数据同步状态
					sql = "update business_index set signstate='2' where sblsh=?";
					int j = DbHelper.update(sql, new Object[] { sblsh }, conn);
					if (j > 0) {
						// 使用统建的审批系统
						if (!"B".equals(power_source)) {
							System.out.println("开始推送数据到云受理----------");
							JSONObject ret = insertCom(data, enter_situation);
							System.out.println("结束推送数据到云受理----------ret:"+ret.toString());
							if ("300".equals(ret.getString("state"))) {
								conn.rollback();
								log_.error("300");
								content = "推送时，业务：" + sblsh
										+ "数据推给审批或权力系统时出错，返回300。错误信息："
										+ ret.getString("error");
								int count = Plugin_Util.getInstance()
										.insertErrorLog(error_type, error_name,
												error_sign, content);
								if (count >= 3) {
									updateBusiness(sblsh, "3");
								}
								continue;
							}
							if ("kms".equals(SecurityConfig.getString("AppId"))) {
								// 昆明数据全部写入统一库
								insertUnify(data, version, power_source);
							}
						}
						// 使用自建的审批系统的把数据写入统一库
						else {
							/* 把同样的数据写入统一库 */
							insertUnify(data, version, power_source);
						}
						conn.commit();
					} else {
						log_.error("业务：" + sblsh + "更新状态失败！");
						conn.rollback();
						content = "更新状态时，业务：[" + sblsh
								+ "]，更新状态失败！更新business_index表signstate为2时出错";
					}
				} catch (Exception e) {
					log_.error("业务：" + sblsh + "同步失败！");
					conn.rollback();
					content = "推送前，业务：[" + sblsh + "]，推送数据前获取数据错误。message:"
								+ e.toString();
				}
				if (!content.equals("")&&!sblsh.equals("")) {
					int re = Plugin_Util.getInstance().insertErrorLog(
							error_type, error_name, error_sign, content);
					if (re >= 3) {
						updateBusiness(sblsh, "3");
					}
				}
			}
		} catch (Exception e) {
			log_.error(e);
		} finally {
			DBSource.closeConnection(conn);
		}
	}

	public void updateBusiness(String id, String signstate) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (!id.equals("")) {
				// 更新状态,推送错误超过3次，不在推送
				String sqltem = "update business_index set signstate=? where sblsh=?";
				DbHelper.update(sqltem, new Object[] { signstate, id }, conn);
				conn.commit();
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		} finally {
			if (conn != null)
				DbHelper.closeConnection(conn);
		}
	}

	// 使用统建的系统，直接把数据推给审批、或权力系统
	public JSONObject insertCom(JSONObject data, String enter_situation) {
		try {
			String url = "";
			// 是否云受理=1 件推送到云受理，否则推送到审批
			if("1".equals(SecurityConfig.getString("is_cloud_accept"))){
				url = HttpUtil.formatUrl(SecurityConfig
						.getString("cloud_accept")+"/Service/webApply");
			}else{
				url = HttpUtil.formatUrl(SecurityConfig
						.getString("approval_url") + "/accpet");
			}
			if ("zs".equals(SecurityConfig.getString("AppId"))) {
				if ("0".equals(enter_situation)) {
					url = HttpUtil.formatUrl(SecurityConfig
							.getString("PowerOperation_url") + "/accpet");
				}
			}
			System.out.println("推送数据到云受理----------url"+url);
			System.out.println("推送数据到云受理----------data"+data.toString());
			Map<String, String> map = new HashMap<String, String>();
			map.put("postdata", data.toString());
			JSONObject ret = JSONObject.fromObject(RestUtil.postData(url, map));
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject m_ret = new JSONObject();
			m_ret.put("state", "300");
			m_ret.put("error", e);
			return m_ret;
		}

	}

	/* 把同样的数据写入统一库 */
	public void insertUnify(JSONObject data, String version, String power_source)
			throws Exception {
		Connection connQZ = DbHelper.getConnection("qzDataSource");
		try {
			connQZ.setAutoCommit(false);
			String sql = "", sblsh = "", cxmm = "", formId = "", dataId = "";
			JSONArray metails;
			sblsh = data.getString("receiveNum");
			cxmm = data.getString("password");
			formId = data.getString("formId");
			dataId = data.getString("dataId");
			metails = data.getJSONArray("metail");
			sql = "insert into PRE_APASINFO (projid, projpwd, itemno, itemversion, itemname, projectname, "
					+ "projectstate, infotype, applyname, apply_cardtype, apply_cardtypenumber, contactman, "
					+ "contactman_cardtype, contactman_cardnumber, telphone, postcode, address, legalman, deptid, "
					+ "deptname, receive_useid, receive_name, applyfrom, receivetime, approve_type, region_id, "
					+ "datastate, create_time, dataver, signstate,power_source,ACCEPTLIST,EXT4) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] APASInfo = new Object[] {
					sblsh, // 申办流水号类似的编码来确定事项
					cxmm, // 查询密码，六位。暂存时为空。
					data.get("itemCode"), // 审批事项的唯一标示
					version, // 审批事项版本号
					data.get("itemName"), // 申报事项名称
					"关于" + data.get("sqrxm") + "的" + data.get("itemName"), // 申请审批的项目的具体名称。如：关于XXX的交通建设工程施工许可，暂用itemname替代
					"02", // 办件状态，需要参考字典项，暂设置暂存为02
					"2", // 办件类型 不可空，1-即办件，2-承诺件，3-联办件，4-上报件。
					data.get("sqrxm"), // 填写申报者的名称，如为个人，则填写姓名；如为法人，则填写单位名称，不可空
					"0", // 申报者提供的有效证件名称，包括身份证、组织机构代码证等，详见 数据字典中编号证件类型，可为空
					data.get("zjhm"), // 申报者提供的有效证件的识别号。如身份证号码：340102198805059786，可为空
					data.get("lxrxm"), // 联系人/代理人姓名 如果无代理人，联系人就是申报者，不可空
					"0", // 联系人/代理人证件类型
							// 提供的有效证件名称，包括身份证、组织机构代码证等详见数据字典证件类型，可为空
					data.get("zjhm"), // 联系人/代理人证件号码
										// 提供的有效证件的识别号。如身份证号码：340111199303222102，可为空
					data.get("lxrphone"), // 手机号
					"1", // 邮政编码
					"1", // 通讯地址
					data.has("frdb") ? data.get("frdb").toString() : "", // 法人代表
					data.get("orgCode"), // 收件部门编码 审批事项所对应的负责部门组织机构代码，不可空
					data.get("orgName"), // 收件部门名称 审批事项所对应的负责部门名称，不可空
					data.get("uuid"), // 创建用户标识 创建用户唯一标识，可为空
					data.get("userName"), // 创建用户名称 创建用户名称，可为空
					"网上", // 申报来源 标识办件的申报源头：网上、窗口、其它。不可空
					CommonUtils_api.getInstance()
							.parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), // 申报时间
																				// 时间格式：yyyy-mm-ddhh24:mi:ss。不可空
					"1", // 审批类型 详见 数据字典中编号事项类型编码。不可空
					SecurityConfig.getString("WebRegion"), // 行政区划 可为空，默认值：
					"1", // 数据状态 标识办件是否为有效件，默认是有效。0=作废1=有效，不可空
					(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"))
							.format(new Date()), // 数据产生时间
													// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
					1, // 数据版本号 默认值=1，如果有信息变更，则版本号递增
					"0", // 标志位 数据交换状态标志位
					power_source, "1", // 暂时设置为1
					data.getString("info") // 申报基本信息
			};
			DbHelper.update(sql, APASInfo, connQZ);// 更新统一库中对应的数据
			// 处理表单数据
			HttpClientUtil client = new HttpClientUtil();
			// 调用表单服务
			String url = SecurityConfig.getString("Form_url")
					+ "/cform/getFormStandardData?formId=" + formId
					+ "&formDataId=" + dataId;
			String html = client.getResult(url, "");
			JSONObject formdata = JSONObject.fromObject(html);
			sql = " insert into PRE_COMM_FORM (unid,projid,form_name,form_unid,form_sort,use_unid,use_type,item_values,"
					+ "remark,create_time,SignState,deptid,power_source,dataver) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			Object[] SaveForm = new Object[] {
					Tools.getUUID32(), // 唯一标识
										// 由业务系统自动产生，不可空
					sblsh, // 申办号 为办件的唯一标识，由业务系统按规则自动生成，不可空
					data.get("formName"), // 业务表单名称 业务表单的具体中文名称，不可空
					data.get("formId"), // 业务表单唯一标识 如果业务系统有该字段，则填写，可为空
					"1", // 业务表单顺序号 业务表单展示的顺序号，如果只有一个表单则指=1，不可空
					data.get("formId") + "-" + sblsh, // 使用对象关联号
														// 如材料的表单、办件申报号等，不可空
					"", // 使用对象类型 详见数据字典中证件类型，可为空
					formdata.toString(), // 业务表单信息项的值 详见 附录中业务表单JSON定义，可空
					"", // 备注 补充说明
					(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"))
							.format(new Date()), // 数据产生时间
													// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
					"0", // 标志位 数据交换状态标志位
					data.get("orgCode"), data.get("power_source"), 1 };
			DbHelper.update(sql, SaveForm, connQZ);// 更新统一库中对应的数据
			// 处理附件
			for (int j = 0; j < metails.size(); j++) {
				JSONObject item = metails.getJSONObject(j);

				sql = "insert into pre_file (unid,projid,attrname,attrid,sortid,taketype,istake,amount,taketime,"
						+ "filename,EntityPath,memo,CREATE_TIME,SignState,deptid,power_source,DATAVER) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				Object[] SaveFile = new Object[] { Tools.getUUID32(), // 唯一标识
																		// 由业务系统自动产生，不可空
						sblsh, // 申办号 为办件的唯一标识，由业务系统按规则自动生成，不可空
						item.getString("DOCUMENT_NAME"), // 材料名称
															// 审批事项所对应的提交材料，不可空
						item.getString("DOCUMENT_ID"), // 材料标识 材料唯一标识，不可空
						j, // 材料序号 根据材料顺序依次编号
						"附件上传", // 收取方式 纸质收取、附件上传、电子证照库，不可空
						"0", // 是否收取 标识材料收取的情况，1=是，0=否，不可空
						"0", // 收取数量 记录所收取材料的数量
						(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"))
								.format(new Date()), // 收取时间
														// 时间格式：yyyy-mm-dd
														// hh24:mi:ss，不可空
						item.getString("FILE_NAME"), // 附件文件名称
														// 如果有上传附件必填，上传附件的文件全称包含后缀名，如身份证.jpg
						item.getString("FILE_PATH"), // 附件路径
														// 附件实体文件路径，附件默认以文件形式交换
						"", // 备注 作为材料收取情况的补充说明
						(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"))
								.format(new Date()), // 数据产生时间
														// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
						"0", // 标志位 数据交换状态标志位
						data.get("orgCode"), data.get("power_source"), 1 };
				DbHelper.update(sql, SaveFile, connQZ);
			}
			connQZ.commit();
		} catch (Exception e) {
			connQZ.rollback();
			throw e;
		} finally {
			DBSource.closeConnection(connQZ);
		}

	}
}
