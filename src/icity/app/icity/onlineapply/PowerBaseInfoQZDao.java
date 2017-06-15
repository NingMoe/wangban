package app.icity.onlineapply;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PowerBaseInfoQZDao extends BaseJdbcDao {
	private static final String handlType = "01"; // 办件提交方式，01网上，02窗口，网办默认为网上提交
	private static final String appCode = com.icore.util.SecurityConfig.getString("uniteCodeAppCode"); // 统一编码服务配置的appcode
	private static final String salt = com.icore.util.SecurityConfig.getString("uniteCodeSalt"); // 统一编码服务配置的salt

	private PowerBaseInfoQZDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static PowerBaseInfoQZDao getInstance() {
		return DaoFactory.getDao(PowerBaseInfoQZDao.class.getName());
	}

	public JSONObject submitData(JSONObject data) throws Exception {
		String sblsh = data.getString("receiveNum");
		String cxmm = "";
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String state = data.getString("state");
		String power_source = data.getString("power_source");
		String baseContent = data.toString();
		JSONArray metails = data.getJSONArray("metail");
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		Connection connQZ = DbHelper.getConnection("qzDataSource");
		conn.setAutoCommit(false);
		connQZ.setAutoCommit(false);
		try {
			// 首次提交
			if ("sp".equals(state)) {
				if (!"".equals(sblsh)) {
					sql = "update business_index set state='11',sbsj=? where sblsh=? ";
					DbHelper.update(sql, new Object[] { CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
							CommonUtils_api.YYYY_MM_DD_HH_mm_SS), sblsh }, conn);
					sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
					DbHelper.update(sql, new Object[] { objectType, baseContent, formId, dataId, sblsh }, conn);
					// 查询查询密码
					sql = "select projpwd from business_index where sblsh=?";
					DataSet pwdDs = DbHelper.query(sql, new Object[] { sblsh }, conn);
					cxmm = pwdDs.getRecord(0).getString("CXMM");
				} else {
					String token = this.getToken(data.getString("itemId"));
					String urlCode = HttpUtil.formatUrl(SecurityConfig.getString("CodeServicesUrl")
							+ "/getUniteCode?appCode=" + appCode + "&itemId=" + data.getString("itemId")
							+ "&regionCode=" + data.getString("regionCode") + "&deptCode=" + data.getString("orgCode")
							+ "&handlType=" + handlType + "&token=" + token);
					HttpClientUtil clientCode = new HttpClientUtil();
					JSONObject codeInfo = JSONObject.fromObject(clientCode.getResult(urlCode, ""));
					sblsh = codeInfo.getString("unitecode");
					cxmm = codeInfo.getString("password");// 查询密码

					sql = "insert into business_index (SBLSH,SXBM,SXID," + "SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"
							+ "LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM," + "YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"
							+ "SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ," + "SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"
							+ "TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG," + "BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"
							+ "STATE,URL,LIMIT_NUM,ASSORT) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					DbHelper.update(sql,
							new Object[] { sblsh, // 申办流水号类似的编码来确定事项
									data.get("itemCode"), data.get("itemId"), data.get("itemName"), data.get("ucid"),
									CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
											CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), //
							data.get("objectType"), data.get("zjhm"), data.get("sqrxm"), data.get("lxrxm"),
							data.get("lxrphone"), "关于" + data.get("sqrxm") + data.get("itemName") + "的业务", sblsh, "", // BLFS办理方式
							data.get("regionName"), data.get("regionCode"), null, // YSLSJ业务受理时间
							"", // YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
							"", // YSLSBYY预受理失败原因
							null, // SLSJ预受理失败原因
							"", // YWLSH业务流水号
							data.get("orgName"), data.get("orgCode"), // 收件单位代码
							"", // SLZTDM受理状态代码(1受理，2不受理)
							"", // BSLYY不受理原因
							"", // SLHZH受理回执号
							cxmm, // CXMM查询密码
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
							"11", // STATE业务状态
							"", // URL跳转地址
							"0", // limit_num 补正补齐次数
							data.get("assort") }, conn);

					sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID) values (?,?,?,?,?)";
					DbHelper.update(sql, new Object[] { sblsh, objectType, baseContent, formId, dataId }, conn);

				}
				data.put("receiveNum", sblsh);
				data.put("password", cxmm);
				// 使用统建的审批系统，直接把数据推给审批
				if (!"B".equals(power_source)) {
					String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/accpet");
					Map<String, String> map=new HashMap<String, String>();
					map.put("postdata", data.toString());
					RestUtil.postData(url, map);
				}
				// 使用自建的审批系统的把数据写入统一库
				else {
					/* 把同样的数据写入统一库 */
					sql = "insert into PRE_APASINFO (projid, projpwd, itemno, itemversion, itemname, projectname, "
							+ "projectstate, infotype, applyname, apply_cardtype, apply_cardtypenumber, contactman, "
							+ "contactman_cardtype, contactman_cardnumber, telphone, postcode, address, legalman, deptid, "
							+ "deptname, receive_useid, receive_name, applyfrom, receivetime, approve_type, region_id, "
							+ "datastate, create_time, dataver, signstate,power_source,ACCEPTLIST,EXT4) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					Object[] APASInfo = new Object[] { sblsh, // 申办流水号类似的编码来确定事项
							cxmm, // 查询密码，六位。暂存时为空。
							data.get("itemCode"), // 审批事项的唯一标示
							"", // 审批事项版本号
							data.get("itemName"), // 申报事项名称
							"关于" + data.get("sqrxm") + "的" + data.get("itemName"), // 申请审批的项目的具体名称。如：关于XXX的交通建设工程施工许可，暂用itemname替代
							"02", // 办件状态，需要参考字典项，暂设置暂存为02
							"2", // 办件类型 不可空，1-即办件，2-承诺件，3-联办件，4-上报件。
							data.get("sqrxm"), // 填写申报者的名称，如为个人，则填写姓名；如为法人，则填写单位名称，不可空
							"0", // 申报者提供的有效证件名称，包括身份证、组织机构代码证等，详见
									// 数据字典中编号证件类型，可为空
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
							data.get("orgCode"), // 收件部门编码
													// 审批事项所对应的负责部门组织机构代码，不可空
							data.get("orgName"), // 收件部门名称 审批事项所对应的负责部门名称，不可空
							data.get("uuid"), // 创建用户标识 创建用户唯一标识，可为空
							data.get("userName"), // 创建用户名称 创建用户名称，可为空
							"网上", // 申报来源 标识办件的申报源头：网上、窗口、其它。不可空
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), // 申报时间
																				// 时间格式：yyyy-mm-ddhh24:mi:ss。不可空
							"1", // 审批类型 详见 数据字典中编号事项类型编码。不可空
							SecurityConfig.getString("WebRegion"), // 行政区划
																	// 可为空，默认值：
							"1", // 数据状态 标识办件是否为有效件，默认是有效。0=作废1=有效，不可空
							(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).format(new Date()), // 数据产生时间
																								// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
							1, // 数据版本号 默认值=1，如果有信息变更，则版本号递增
							"0", // 标志位 数据交换状态标志位
							power_source, "1", // 暂时设置为1
							data.getString("info")// 申报基本信息
					};
					DbHelper.update(sql, APASInfo, connQZ);// 更新统一库中对应的数据
					// 处理表单数据
					HttpClientUtil client = new HttpClientUtil();
					// 调用表单服务
					String url = SecurityConfig.getString("Form_url") + "/cform/getFormStandardData?formId=" + formId
							+ "&formDataId=" + dataId;
					String html = client.getResult(url, "");
					JSONObject formdata = JSONObject.fromObject(html);
					sql = " insert into PRE_COMM_FORM (unid,projid,form_name,form_unid,form_sort,use_unid,use_type,item_values,"
							+ "remark,create_time,SignState,deptid,power_source,dataver) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					Object[] SaveForm = new Object[] { Tools.getUUID32(), // 唯一标识
																			// 由业务系统自动产生，不可空
							sblsh, // 申办号 为办件的唯一标识，由业务系统按规则自动生成，不可空
							data.get("formName"), // 业务表单名称 业务表单的具体中文名称，不可空
							data.get("formId"), // 业务表单唯一标识 如果业务系统有该字段，则填写，可为空
							"1", // 业务表单顺序号 业务表单展示的顺序号，如果只有一个表单则指=1，不可空
							data.get("formId") + "-" + sblsh, // 使用对象关联号
																// 如材料的表单、办件申报号等，不可空
							"", // 使用对象类型 详见数据字典中证件类型，可为空
							formdata.toString(), // 业务表单信息项的值 详见
													// 附录中业务表单JSON定义，可空
							"", // 备注 补充说明
							(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).format(new Date()), // 数据产生时间
																								// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
							"0", // 标志位 数据交换状态标志位
							data.get("orgCode"), data.get("power_source"), 1 };
					DbHelper.update(sql, SaveForm, connQZ);// 更新统一库中对应的数据
					// 处理附件
					for (int i = 0; i < metails.size(); i++) {
						JSONObject item = metails.getJSONObject(i);

						sql = "insert into pre_file (unid,projid,attrname,attrid,sortid,taketype,istake,amount,taketime,"
								+ "filename,EntityPath,memo,CREATE_TIME,SignState,deptid,power_source,DATAVER) "
								+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						Object[] SaveFile = new Object[] { Tools.getUUID32(), // 唯一标识
																				// 由业务系统自动产生，不可空
								sblsh, // 申办号 为办件的唯一标识，由业务系统按规则自动生成，不可空
								item.getString("DOCUMENT_NAME"), // 材料名称
																	// 审批事项所对应的提交材料，不可空
								item.getString("DOCUMENT_ID"), // 材料标识
																// 材料唯一标识，不可空
								i, // 材料序号 根据材料顺序依次编号
								"附件上传", // 收取方式 纸质收取、附件上传、电子证照库，不可空
								"0", // 是否收取 标识材料收取的情况，1=是，0=否，不可空
								"0", // 收取数量 记录所收取材料的数量
								(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).format(new Date()), // 收取时间
																									// 时间格式：yyyy-mm-dd
																									// hh24:mi:ss，不可空
								item.getString("FILE_NAME"), // 附件文件名称
																// 如果有上传附件必填，上传附件的文件全称包含后缀名，如身份证.jpg
								item.getString("FILE_PATH"), // 附件路径
																// 附件实体文件路径，附件默认以文件形式交换
								"", // 备注 作为材料收取情况的补充说明
								(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).format(new Date()), // 数据产生时间
																									// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
								"0", // 标志位 数据交换状态标志位
								data.get("orgCode"), data.get("power_source"), 1 };
						DbHelper.update(sql, SaveFile, connQZ);
					}
				}

			}
			// 使用自建的审批系统的，暂未处理补齐补正
			else if ("bqbz".equals(state) || "clbqbz".equals(state)) {
				// 可进行多次补齐补正,或者驳回后的提交
				sql = "update business_index set BZSJ=?,state=?,limit_num=to_number(limit_num)+1 where sblsh=?";
				DbHelper.update(sql,
						new Object[] {
								CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
										CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
								"bqbz".equals(state) ? "11" : "01", sblsh },
						conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[] { objectType, baseContent, formId, dataId, sblsh }, conn);
				// 使用统建的审批系统，直接把数据推给审批
				if ("C".equals(power_source)) {
					String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/accpet");
					Map<String, String> map=new HashMap<String, String>();
					map.put("postdata", data.toString());
					RestUtil.postData(url, map);
				}
			}
			conn.commit();
			connQZ.commit();
			return data;
		} catch (Exception e) {
			conn.rollback();
			connQZ.rollback();
			throw e;
		} finally {
			DBSource.closeConnection(conn);
			DBSource.closeConnection(connQZ);
		}
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

}
