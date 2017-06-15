/**  
 * @Title: PowerBaseInfoQZDao.java 
 * @Package app.icity.sync 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-9-23 上午10:57:14 
 * @version V1.0  
 */
package app.icity.sync;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import core.util.CommonUtils_api;
import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: PowerBaseInfoQZDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-9-23 上午10:57:14
 */

public class PowerBaseInfoQZDao extends BaseJdbcDao {

	private PowerBaseInfoQZDao() {
		this.setDataSourceName("qzDataSource");
	}

	public static PowerBaseInfoQZDao getInstance() {
		return DaoFactory.getDao(PowerBaseInfoQZDao.class.getName());
	}

	public String saveData(JSONObject data) throws Exception {
		// String o_sblsh=data.getString("receiveNum");
		String o_sblsh = "";
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		JSONArray metails = data.getJSONArray("metail");
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			if (!"".equals(o_sblsh)) {
				sql = "update PRE_APASINFO set receivetime=? where projid=? ";
				DbHelper.update(sql, new Object[] { CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
						CommonUtils_api.YYYY_MM_DD_HH_mm_SS), o_sblsh }, conn);
				// sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE =
				// ?,basecontent = ?,FORMID=?,DATAID=? where sblsh = ?";
				// DbHelper.update(sql, new
				// Object[]{objectType,baseContent,formId,dataId,o_sblsh},
				// conn);
			} else {
				// o_sblsh=Tools.getUUID32();
				String token = DigestUtils.md5Hex("004502403" + data.getString("innerCode") + "lzhlmclyhblsqt");

				String urlCode = HttpUtil.formatUrl(SecurityConfig.getString("CodeServicesUrl")
						+ "/getUniteCode?appCode=004502403&itemId=" + data.getString("innerCode") + "&regionCode="
						+ data.getString("regionCode") + "&powerType=01&token=" + token);
				HttpClientUtil clientCode = new HttpClientUtil();
				JSONObject codeInfo = JSONObject.fromObject(clientCode.getResult(urlCode, ""));

				o_sblsh = codeInfo.getString("unitecode");
				String slmm = codeInfo.getString("password");

				sql = "insert into PRE_APASINFO (projid, projpwd, itemno, itemversion, itemname, projectname, "
						+ "projectstate, infotype, applyname, apply_cardtype, apply_cardtypenumber, contactman, "
						+ "contactman_cardtype, contactman_cardnumber, telphone, postcode, address, legalman, deptid, "
						+ "deptname, receive_useid, receive_name, applyfrom, receivetime, approve_type, region_id, "
						+ "datastate, create_time, dataver, signstate) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				DbHelper.update(sql,
						new Object[] { o_sblsh, // 申办流水号类似的编码来确定事项
								slmm, // 查询密码，六位。暂存时为空。
								data.get("itemCode"), // 审批事项的唯一标示
								"", // 审批事项版本号
								data.get("itemName"), // 申报事项名称
								data.get("itemName"), // 申请审批的项目的具体名称。如：关于XXX的交通建设工程施工许可，暂用itemname替代
								"0", // 办件状态，需要参考字典项，暂设置暂存为0
								"1", // 办件类型 不可空，1-即办件，2-承诺件，3-联办件，4-上报件。
								data.get("sqrxm"), // 填写申报者的名称，如为个人，则填写姓名；如为法人，则填写单位名称，不可空
								"0", // 申报者提供的有效证件名称，包括身份证、组织机构代码证等，详见
										// 数据字典中编号证件类型，可为空
								data.get("zjhm"), // 申报者提供的有效证件的识别号。如身份证号码：340102198805059786，可为空
								data.get("lxrxm"), // 联系人/代理人姓名
													// 如果无代理人，联系人就是申报者，不可空
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
								data.get("orgName"), // 收件部门名称
														// 审批事项所对应的负责部门名称，不可空
								"", // 创建用户标识 创建用户唯一标识，可为空
								"", // 创建用户名称 创建用户名称，可为空
								"网上", // 申报来源 标识办件的申报源头：网上、窗口、其它。不可空
								CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
										CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), // 申报时间
																					// 时间格式：yyyy-mm-ddhh24:mi:ss。不可空
						"1", // 审批类型 详见 数据字典中编号事项类型编码。不可空
						"370000", // 行政区划 可为空，默认值：370000
						"1", // 数据状态 标识办件是否为有效件，默认是有效。0=作废1=有效，不可空
						(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).format(new Date()), // 数据产生时间
																							// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
						1, // 数据版本号 默认值=1，如果有信息变更，则版本号递增
						"0" // 标志位 数据交换状态标志位

				}, conn);

				String urlForm = HttpUtil.formatUrl(
						SecurityConfig.getString("approval_url") + "/getFormInfo?itemId=" + data.getString("itemId"));
				HttpClientUtil client = new HttpClientUtil();
				JSONObject forminfo = JSONObject.fromObject(client.getResult(urlForm, "")).getJSONObject("info");

				String url = SecurityConfig.getString("Form_url") + "/cform/getFormStandardData?formId=" + formId
						+ "&formDataId=" + dataId;
				client = new HttpClientUtil();
				String html = client.getResult(url, "");
				JSONObject formdata = JSONObject.fromObject(html);

				sql = " insert into PRE_COMM_FORM (unid,projid,form_name,form_unid,form_sort,use_unid,use_type,item_values,"
						+ "remark,create_time,SignState) " + "values (?,?,?,?,?,?,?,?,?,?,?)";
				DbHelper.update(sql,
						new Object[] { Tools.getUUID32(), // 唯一标识 由业务系统自动产生，不可空
								o_sblsh, // 申办号 为办件的唯一标识，由业务系统按规则自动生成，不可空
								forminfo.get("formName"), // 业务表单名称
															// 业务表单的具体中文名称，不可空
								forminfo.get("formId"), // 业务表单唯一标识
														// 如果业务系统有该字段，则填写，可为空
								"1", // 业务表单顺序号 业务表单展示的顺序号，如果只有一个表单则指=1，不可空
								forminfo.get("formId") + "-" + o_sblsh, // 使用对象关联号
																		// 如材料的表单、办件申报号等，不可空
								"", // 使用对象类型 详见数据字典中证件类型，可为空
								formdata.toString(), // 业务表单信息项的值 详见
														// 附录中业务表单JSON定义，可空
								"", // 备注 补充说明
								(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).format(new Date()), // 数据产生时间
																									// 由各业务系统产生，时间格式：yyyy-mm-ddhh24:mi:ss，不可空
								"0" // 标志位 数据交换状态标志位

				}, conn);

				// 保存上传的附件信息
				for (int i = 0; i < metails.size(); i++) {
					JSONObject item = metails.getJSONObject(i);

					sql = "insert into pre_file (unid,projid,attrname,attrid,sortid,taketype,istake,amount,taketime,"
							+ "filename,EntityPath,memo,CREATE_TIME,SignState) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					DbHelper.update(sql,
							new Object[] { Tools.getUUID32(), // 唯一标识
																// 由业务系统自动产生，不可空
									o_sblsh, // 申办号 为办件的唯一标识，由业务系统按规则自动生成，不可空
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
									"0" // 标志位 数据交换状态标志位

					}, conn);

				}
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
}
