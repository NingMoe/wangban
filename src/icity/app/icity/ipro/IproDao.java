package app.icity.ipro;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;

public class IproDao extends BaseJdbcDao {

	private IproDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static IproDao getInstance() {
		return DaoFactory.getDao(IproDao.class.getName());
	}

	/*
	 * 检查项目年报的项目代码
	 */
	public DataSet checkXmnbCode(ParameterSet pSet) {
		System.out.println("000");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			String uuid = (String) pSet.getParameter("uuid");
			String project_code = (String) pSet.getParameter("project_code");
			String sqlEx = "select count(*) TOTAL from tzsp.dc_project where " + "project_code=? and project_code "
					+ "in (select d.project_code from ipro_index i inner join "
					+ "tzsp.dc_project d on d.seq_id=i.seqid " + "where uuid=?)";
			try {
				DataSet result = DbHelper.query(sqlEx, new Object[] { project_code, uuid }, conn);
				ds = result;
				System.err.println("checkXmnbCode==" + ds);
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("查询错误");
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		}
		return ds;
	}

	/*
	 * 获得进程
	 */
	public DataSet getJD(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String seqId = (String) pSet.getParameter("seqId");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getProjectProgress?seqId=" + seqId);
		try {
			Object itemInfoJC = RestUtil.getData(url);
			ds.setState(StateType.SUCCESS);
			ds.setRawData(itemInfoJC);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getPermitItemInfo！");
			return ds;
		}
		return ds;
	}

	/*
	 * 字典
	 */
	public DataSet getInvestDictList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getInvestDictList");
		try {
			JSONObject receiveNum = JSONObject.fromObject(RestUtil.getData(url));
			receiveNum.remove("XMHYFLML");
			ds.setRawData(receiveNum);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getInvestDictList！");
			return ds;
		}
		return ds;
	}

	/*
	 * 提交
	 */
	public DataSet saveInvestInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try{
		//System.out.println("====test1");
		
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		//System.out.println("====test1"+baseInfo);
		// baseInfo.put("investment", "aaa ss
		// dssaerqweasdfawe12来了万元￥￥'34.32~!@#$%^&*()+=|{}'$");
		// 过滤金额
		String investment = baseInfo.getString("investment");
		// boolean strResult=investment.matches("^[0-9]+(\\.[0-9]+)?$");
		// boolean strResult2=investment.matches("[0-9]");
		boolean strResult = investment.matches("[0-9]+.?[0-9]*");
		if (!(strResult)) {
			System.out.println(investment + "：不是数字");
			String value1 = investment.replaceAll("[\u4E00-\u9FA5]", "");
			String value2 = value1.replaceAll("[a-zA-Z]", "");
			String value3 = value2.replaceAll("[ \t]", "");
			String value = value3.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]", "");
			baseInfo.put("investment", value);
			System.out.println("过滤后：" + value);
		}
	/*	// 过滤tab字符
		String baseInfos = baseInfo.toString();
		if (baseInfos.contains("\\t") || baseInfos.contains(" ")) {
			String projectName = baseInfo.getString("projectName");
			String contractor = baseInfo.getString("contractor");
			String lerepCertno = baseInfo.getString("lerepCertno");
			String projectContent = baseInfo.getString("projectContent");
			String linkMan = baseInfo.getString("linkMan");
			String linkPhone = baseInfo.getString("linkPhone");
			String contactEmail = baseInfo.getString("contactEmail");
			String deAreaName = baseInfo.getString("deAreaName");
			projectName = projectName.replaceAll("[ \t]", "");
			contractor = contractor.replaceAll("[ \t]", "");
			lerepCertno = lerepCertno.replaceAll("[ \t]", "");
			projectContent = projectContent.replaceAll("[ \t]", "");
			linkMan = linkMan.replaceAll("[ \t]", "");
			linkPhone = linkPhone.replaceAll("[ \t]", "");
			contactEmail = contactEmail.replaceAll("[ \t]", "");
			deAreaName = deAreaName.replaceAll("[ \t]", "");
			System.out.println("申办的项目：'" + baseInfo.getString("projectName") + "'中有tab字符");
			baseInfo.put("projectName", projectName);
			baseInfo.put("contractor", contractor);
			baseInfo.put("lerepCertno", lerepCertno);
			baseInfo.put("projectContent", projectContent);
			baseInfo.put("linkMan", linkMan);
			baseInfo.put("linkPhone", linkPhone);
			baseInfo.put("contactEmail", contactEmail);
			baseInfo.put("deAreaName", deAreaName);
		}*/
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/saveInvestInfo");
		Map<String, String> data=new HashMap<String, String>();
		try {
			//add by cys,for:添加接送Array		
			JSONArray ja = baseInfo.getJSONArray("lerepInfo");	
			JSONArray janew=new JSONArray();
			JSONObject jsonObject = new JSONObject();
		
			for(int i=0;i<ja.size();i++){  
				jsonObject=ja.getJSONObject(i);
				String id=jsonObject.getString("id");
				if(!id.contains("del")){//代表未删除的数据
					jsonObject.remove("id");
					janew.add(jsonObject);
				}
		      }
			baseInfo.put("lerepInfo", janew);	
			if(baseInfo.containsKey("contrInfo")){
			JSONArray jacontrInfo = baseInfo.getJSONArray("contrInfo");	
			JSONArray janewcontrInfo=new JSONArray();
			jsonObject = null;
			
			for(int i=0;i<jacontrInfo.size();i++){  
				jsonObject=jacontrInfo.getJSONObject(i);
				String id=jsonObject.getString("id");
				if(!id.contains("del")){//代表未删除的数据
					jsonObject.remove("id");
					janewcontrInfo.add(jsonObject);
				}
		      }
			baseInfo.put("contrInfo", janewcontrInfo);	
			}
			//end by cys
			
			data.put("baseInfo", baseInfo.toString());
			// data.put("baseInfo",java.net.URLEncoder.encode(baseInfo.toString(),"UTF-8"));
			// //编码一层
			System.err.println("提交baseInfo表单：" + baseInfo);
			String oldseqid = (String) pSet.getParameter("oldseqid");
			if (!(oldseqid == null || oldseqid.equals(""))) {
				// DataSet ds1 = new DataSet();
				// String seqid="%"+oldseqid+"%";
				// String sql = "select SBLSH from ipro_index where SEQID like
				// ?";
				// Connection conn =
				// DbHelper.getConnection(this.getDataSourceName());
				// DataSet result = DbHelper.query(sql, new Object[] { seqid },
				// conn);
				// String
				// oldinvestid=ds1.getJAData().getJSONObject(0).get("SBLSH").toString();
				data.put("oldInvestId", (String) pSet.getParameter("oldInvestId"));
				data.put("oldSeqId", (String) pSet.getParameter("oldseqid"));
			}
			data.put("region_code", (String) pSet.getParameter("region_code"));
			data.put("region_name", (String) pSet.getParameter("region_name"));
			JSONObject obj = new JSONObject();
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			System.err.println("返回json串：" + obj);
			pSet.put("retValue", obj);
			if (!StringUtils.isEmpty(oldseqid)) {
				updateData(pSet);
				ds.setState(StateType.SUCCESS);
			} else if (submitData(pSet)) {
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ds;
	}

	
	// 打印itemCode
	/*
	 * public DataSet printItemCode(ParameterSet pSet) { JSONObject retValue =
	 * (JSONObject) pSet.getParameter("retValue"); String
	 * itemCode=retValue.getString("permitItemCode");
	 * System.out.println(itemCode); return null; }
	 */
	// 插入数据库
	public Boolean submitData(ParameterSet pSet) throws Exception {
		JSONObject retValue = (JSONObject) pSet.getParameter("retValue");
		String SXBM = (String) pSet.getParameter("SXBM");
		String SXMC = (String) pSet.getParameter("SXMC");
		String region_code = (String) pSet.getParameter("region_code");
		String region_name = (String) pSet.getParameter("region_name");
		JSONObject userInfo = (JSONObject) pSet.getParameter("userInfo");
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			sql = "insert into ipro_index(sblsh, sxbm, sxmc, uuid, ucname, region_code, region_name, SBSJ, content, projectname, seqid) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?)";
			DbHelper.update(sql,
					new Object[] { retValue.get("investId"), SXBM, SXMC, userInfo.get("uuid"), userInfo.get("name"),
							region_code, region_name,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							baseInfo.toString(), baseInfo.getString("projectName"), retValue.getString("seqId") },
					conn);

		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			return false;
		}
        System.err.println("新建成功");
		return true;
	}

	public Boolean updateData(ParameterSet pSet) throws Exception {
		JSONObject retValue = (JSONObject) pSet.getParameter("retValue");
		String seqid = (String) pSet.getParameter("oldseqid");
		String newInvestId = retValue.getString("investId");
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			sql = "update ipro_index set SBLSH=?,SBSJ=?,content=?,projectname=?,seqid=? " + "where seqid=?";
			DbHelper.update(sql,
					new Object[] { newInvestId,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							baseInfo.toString(), baseInfo.getString("projectName"), retValue.getString("seqId"),
							seqid },
					conn);

		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			return false;
		}
       System.err.println("修改成功");
		return true;
	}

	public DataSet checkCode(ParameterSet pSet) {
		DataSet ds;
		String project_code = (String) pSet.getParameter("code");
		String sql = "select * from tzsp.approve_permit_item_info where code=?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { project_code }, conn);
		ds = result;
		return ds;
	}

	public DataSet checkCode2(ParameterSet pSet) {
		DataSet ds;
		String project_code = (String) pSet.getParameter("code");
		String sql = "select PERMIT_ITEM_NAME from tzsp.approve_permit_item_info where code=?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { project_code }, conn);
		ds = result;
		return ds;
	}

	public DataSet searchsblsh(ParameterSet pSet) {
		DataSet ds;
		String seqid = (String) pSet.getParameter("code");
		String sql = "select sblsh from IPRO_INDEX t where seqid = ?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { seqid }, conn);
		ds = result;
		return ds;
	}
	public String updateorinput(ParameterSet pSet) {
		String seqid = (String) pSet.getParameter("code");
		String sql = "select seqid from IPRO_INDEX t where seqid = ?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { seqid }, conn);
		if (result.getTotal()>0) {
			return "1";
		}else {
			return "2";
		}
		
	}

	public DataSet searchProject(ParameterSet pSet) {
		DataSet ds;
		String project_code = (String) pSet.getParameter("code");
		String project_name = (String) pSet.getParameter("name");
		String sql;
		DataSet result;
		if (project_code == null || project_code.equals("")) {
			project_name = "%" + project_name + "%";
			sql = "select SEQ_ID,project_code,project_name,project_type,construct_per,contractor,investment,apply_date,link_man,status "
					+ "from tzsp.DC_PROJECT,tzsp.approve_invest_index "
					+ "where tzsp.DC_PROJECT.seq_id=tzsp.approve_invest_index.projectid and project_name like ?";
			Connection conn = DbHelper.getConnection(this.getDataSourceName());
			result = DbHelper.query(sql, new Object[] { project_name }, conn);
		} else {
			project_code = "%" + project_code + "%";
			project_name = "%" + project_name + "%";
			sql = "select SEQ_ID,project_code,project_name,project_type,construct_per,contractor,investment,apply_date,link_man,status "
					+ "from tzsp.DC_PROJECT,tzsp.approve_invest_index "
					+ "where tzsp.DC_PROJECT.seq_id=tzsp.approve_invest_index.projectid and project_code like ? and project_name like ?";
			Connection conn = DbHelper.getConnection(this.getDataSourceName());
			result = DbHelper.query(sql, new Object[] { project_code, project_name }, conn);
		}

		ds = result;
		return ds;
	}

	public DataSet searchProjects(ParameterSet pSet) {
		DataSet ds;
		String business_object_code = (String) pSet.getParameter("code");
		business_object_code = "%" + business_object_code + "%";
		String sql = "select * from tzsp.approve_business_index where business_object_code like ?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { business_object_code }, conn);
		ds = result;
		return ds;
	}

	public DataSet searchP(ParameterSet pSet) {
		DataSet ds;
		String receive_number = (String) pSet.getParameter("code");
		receive_number = "%" + receive_number + "%";
		String sql = "select * from tzsp.approve_business_index where receive_number like ?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { receive_number }, conn);
		ds = result;
		return ds;
	}

	public DataSet getProjectModify(ParameterSet pSet) {
		DataSet ds;
		String seq_id = (String) pSet.getParameter("code");
		String sql = "select * from tzsp.DC_PROJECT where seq_id = ?";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		DataSet result = DbHelper.query(sql, new Object[] { seq_id }, conn);
		try {
			// 内网项目信息可能修改字段
			String PROJECT_NAME = (String) result.getRecord(0).get("PROJECT_NAME");// 项目名称
			String START_YEAR = (String) result.getRecord(0).get("START_YEAR");// 拟开工时间
			String END_YEAR = (String) result.getRecord(0).get("END_YEAR");// 拟建成时间
			String INVESTMENT = (String) result.getRecord(0).get("INVESTMENT");// 总投资（万元）
			String PLACE_NAME = (String) result.getRecord(0).get("PLACE_NAME");// 建设地点
			String PLACE_AREA_DETAIL = (String) result.getRecord(0).get("PLACE_AREA_DETAIL");// 建设地点详情
			// System.out.println(PLACE_AREA_DETAIL);
			String DIVISION_CODE = (String) result.getRecord(0).get("DIVISION_CODE");// 区划编号
			String PROJECT_CONTENT = (String) result.getRecord(0).get("PROJECT_CONTENT");// 建设规模及内容
			String IS_DE_AREA = (String) result.getRecord(0).get("IS_DE_AREA");// 是否开发项目
			String DE_AREA_NAME = (String) result.getRecord(0).get("DE_AREA_NAME");// 是否开发项目
			pSet.put("SEQID", seq_id);
			DataSet ds3 = getProjectLerepInfoBySeqid(pSet);
			
			// 内网企业信息可能修改字段
			String CONTRACTOR = (String) ds3.getRecord(0).get("ENTERPRISE_NAME"); // 项目（法人）单位
			String CONTRACTNAME = (String) ds3.getRecord(0).get("CONTACT_NAME"); // 项目法人姓名
			String LEREP_CERTTYPE = (String) ds3.getRecord(0).get("LEREP_CERTTYPE");// 项目（法人）单位证照类型
			String LEREP_CERTNO = (String) ds3.getRecord(0).get("LEREP_CERTNO");// 项目（法人）单位证照号码
			String ENTERPRISE_NATURE = (String) ds3.getRecord(0).get("ENTERPRISE_NATURE");// 项目单位性质
			String CORRESPONDENCE_ADDRESS = (String) ds3.getRecord(0).get("CORRESPONDENCE_ADDRESS");// 单位注册地址
			String CONTACT_TEL = (String) ds3.getRecord(0).get("CONTACT_TEL"); // 联系电话
			
			//JSONObject jodata = result.getJOData();
			JSONArray jadata = result.getJAData();
			JSONObject jodata = (JSONObject) jadata.get(0);
			jodata.put("contractor", CONTRACTOR);
			jodata.put("contractname", CONTRACTNAME);
			jodata.put("lerepcerttype", LEREP_CERTTYPE);
			jodata.put("lerepcertno", LEREP_CERTNO);
			jodata.put("enterpriseNature", ENTERPRISE_NATURE);
			jodata.put("correspondenceAddress", CORRESPONDENCE_ADDRESS);
			jodata.put("contactTel", CONTACT_TEL);
			
			//获取外商出资信息
			DataSet ds4 = getContributionBySeqid(pSet);
			if (ds4.getJAData().size()>0) {
				String BUSINESS_TYPE = (String) ds4.getRecord(0).get("BUSINESS_TYPE"); // 出资类型
				if ("0".equals(BUSINESS_TYPE)) {
					BUSINESS_TYPE = "项目单位中、外方出资情况";
				}else {
					BUSINESS_TYPE = "项目资本金出资情况";
				}
				String INVESTMENT_NAME = (String) ds4.getRecord(0).get("INVESTMENT_NAME"); // 投资者名称
				String REG_COUNTRY = (String) ds4.getRecord(0).get("REG_COUNTRY"); //注册国别地区
				String CONTRIBUTION_MODE = (String) ds4.getRecord(0).get("CONTRIBUTION_MODE"); // 出资方式
				String CONTRIBUTION_LIMIT = (String) ds4.getRecord(0).get("CONTRIBUTION_LIMIT"); // 出资额度
				String CONTRIBUTION_RATIO = (String) ds4.getRecord(0).get("CONTRIBUTION_RATIO"); // 出资比例
				
				jodata.put("businessType", BUSINESS_TYPE);
				jodata.put("investmentName", INVESTMENT_NAME);
				jodata.put("regCountry", REG_COUNTRY);
				jodata.put("contributionMode", CONTRIBUTION_MODE);
				jodata.put("contributionLimit", CONTRIBUTION_LIMIT);
				jodata.put("contributionRatio", CONTRIBUTION_RATIO);
			}else {
				jodata.put("businessType", "");
				jodata.put("investmentName", "");
				jodata.put("regCountry", " ");
				jodata.put("contributionMode", "");
				jodata.put("contributionLimit", "");
				jodata.put("contributionRatio", "");
			}
			jadata.remove(0);
			jadata.add(0, jodata);
			result.setRawData(jadata);
			// 外网
			
			DataSet ds2 = getIpro(pSet);
			JSONObject CONTENT = (JSONObject) ds2.getRecord(0).get("CONTENT");// 项目名称
			// System.out.println("===CONTENT:"+CONTENT);
			// 外网项目信息可能修改字段
			String projectName = (String) CONTENT.get("projectName");// 项目名称
			String startYear = (String) CONTENT.get("startYear");// 拟开工时间
			String endYear = (String) CONTENT.get("endYear");// 拟建成时间
			String investment = (String) CONTENT.get("investment");// 总投资（万元）
			String placeName = (String) CONTENT.get("placeName");// 建设地点
			String placeAreaDetail = (String) CONTENT.get("placeAreaDetail");// 建设地点
			String divisionCode = (String) CONTENT.get("divisionCode");// 建设地点详情
			String projectContent = (String) CONTENT.get("projectContent");// 建设规模及内容
			String isDeArea = (String) CONTENT.get("isDeArea");// 是否开发区项目
			String deAreaName = (String) CONTENT.get("deAreaName");// 开发区名称
			// 外网企业信息可能修改字段
			//String contractor = (String) CONTENT.get("contractor"); // 项目（法人）单位
			//String lerepCertType = (String) CONTENT.get("lerepCerttype");// 项目（法人）单位证照类型
			//String lerepCertNo = (String) CONTENT.get("lerepCertno");// 项目（法人）单位证照号码
			 String contractor = null;
			 String lerepCertType= null;
			 String lerepCertNo= null;
			 JSONArray datat=(JSONArray) CONTENT.get("lerepInfo");
			 for (int j = 0; j < datat.size(); j++) {
				 JSONObject objt = JSONObject.fromObject(datat.get(j));
						 contractor=(String) objt.get("enterpriseName");
						 lerepCertType=(String) objt.get("lerepCerttype");
						 lerepCertNo=(String) objt.get("lerepCertno");
				}
			JSONObject update = new JSONObject();
			JSONObject obj;
			obj = CONTENT;
			obj.put("contractor", CONTRACTOR);
			// 判断项目名称是否有变化
			if (!(projectName.equals(PROJECT_NAME))) {
				System.out.println("“项目名称”字段有变化：" + PROJECT_NAME);
				obj.put("projectName", PROJECT_NAME);
				update.put("projectName", PROJECT_NAME);
			}
			// 判断拟开工时间是否有变化
			if (!(startYear.equals(START_YEAR))) {
				System.out.println("“拟开工时间”字段有变化：" + START_YEAR);
				obj.put("startYear", START_YEAR);
				update.put("startYear", START_YEAR);
			}
			// 判断拟建成时间是否有变化
			if (!(endYear.equals(END_YEAR))) {
				System.out.println("“拟建成时间”字段有变化：" + END_YEAR);
				obj.put("endYear", END_YEAR);
				update.put("endYear", END_YEAR);
			}
			// 判断总投资（万元）是否有变化
			if (!(investment.equals(INVESTMENT))) {
				System.out.println("“总投资（万元）”字段有变化：" + INVESTMENT);
				obj.put("investment", INVESTMENT);
				update.put("investment", INVESTMENT);
			}
			// 判断建设地点是否有变化
			if (!(placeName.equals(PLACE_NAME))) {
				System.out.println("“建设地点”字段有变化：" + PLACE_NAME);
				obj.put("placeName", PLACE_NAME);
				update.put("placeName", PLACE_NAME);
			}
			// 判断建设地点详情是否有变化
			if (!(placeAreaDetail.equals(PLACE_AREA_DETAIL))) {
				System.out.println("“建设地点详情”字段有变化：" + PLACE_AREA_DETAIL);
				obj.put("placeAreaDetail", PLACE_AREA_DETAIL);
				update.put("placeAreaDetail", PLACE_AREA_DETAIL);
			}
			// 判断区划编号是否有变化
			if (!(divisionCode.equals(DIVISION_CODE))) {
				System.out.println("“区划编号”字段有变化：" + DIVISION_CODE);
				obj.put("divisionCode", DIVISION_CODE);
				update.put("divisionCode", DIVISION_CODE);
			}
			// 判断建设规模及内容是否有变化
			if (!(projectContent.equals(PROJECT_CONTENT))) {
				System.out.println("“建设规模及内容”字段有变化!");
				obj.put("projectContent", PROJECT_CONTENT);
				update.put("projectContent", PROJECT_CONTENT);
			}
			// 判断项目（法人）单位是否有变化
			if (!(contractor.equals(CONTRACTOR))) {
				System.out.println("“项目（法人）单位”字段有变化：" + CONTRACTOR);
				obj.put("contractor", CONTRACTOR);
				update.put("contractor", CONTRACTOR);
			}
			// 判断项目（法人）单位证照类型是否有变化
			if (!(lerepCertType.equals(LEREP_CERTTYPE))) {
				System.out.println("“项目（法人）单位证照类型”字段有变化：" + LEREP_CERTTYPE);
				obj.put("lerepCertType", LEREP_CERTTYPE);
				update.put("lerepCertType", LEREP_CERTTYPE);
			}
			// 判断项目（法人）单位证照号码是否有变化
			if (!(lerepCertNo.equals(LEREP_CERTNO))) {
				System.out.println("“项目（法人）单位证照号码”有字段变化：" + LEREP_CERTNO);
				obj.put("lerepCertNo", LEREP_CERTNO);
				update.put("lerepCertNo", LEREP_CERTNO);
			}
			if (!(isDeArea.equals(IS_DE_AREA))) {
				System.out.println("“是否开发区项目”字段有变化：" + IS_DE_AREA);
				obj.put("isDeArea", IS_DE_AREA);
				update.put("isDeArea", IS_DE_AREA);
				if (isDeArea.equals("0")) {
					System.out.println("“开发区名称”字段有变化：" + DE_AREA_NAME);
					obj.put("deAreaName", DE_AREA_NAME);
					update.put("deAreaName", DE_AREA_NAME);
				} else if (isDeArea.equals("1")) {
					System.out.println("“开发区名称”字段有变化!");
					obj.remove("deAreaName");
				}
			}
			if ((isDeArea.equals(IS_DE_AREA)) && isDeArea.equals("1")) {
				if (!(deAreaName.equals(DE_AREA_NAME))) {
					System.out.println("“开发区名称”字段有变化：" + DE_AREA_NAME);
					obj.put("deAreaName", DE_AREA_NAME);
					update.put("deAreaName", DE_AREA_NAME);
				}
			}
			pSet.put("baseInfo", obj);
			// 判断字段值是否有变化，有则调用updateIpro方法
			if (!(projectName.equals(PROJECT_NAME)) || !(startYear.equals(START_YEAR)) || !(endYear.equals(END_YEAR))
					|| !(investment.equals(INVESTMENT)) || !(placeName.equals(PLACE_NAME))
					|| !(placeAreaDetail.equals(PLACE_AREA_DETAIL)) || !(divisionCode.equals(DIVISION_CODE))
					|| !(projectContent.equals(PROJECT_CONTENT)) || !(contractor.equals(CONTRACTOR))
					|| !(lerepCertType.equals(LEREP_CERTTYPE)) || !(lerepCertNo.equals(LEREP_CERTNO))
					|| !(isDeArea.equals(IS_DE_AREA))
					|| (isDeArea.equals(IS_DE_AREA)) && isDeArea.equals("1") && !(deAreaName.equals(DE_AREA_NAME))) {
				// 修改外网数据库
				System.out.println("需要修改外网字段~~~~~~update：" + update);
				updateIpro(pSet);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ds = result;
		return ds;
	}

	public Boolean dele(ParameterSet pSet) throws Exception {
		String SEQ_ID = (String) pSet.getParameter("seqid");
		SEQ_ID = "%" + SEQ_ID + "%";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			// 项目主表
			String sql1 = "delete from tzsp.DC_PROJECT where SEQ_ID like ?";
			DbHelper.update(sql1, new Object[] { SEQ_ID }, conn);
			// 项目状态表
			String sql2 = "delete from tzsp.approve_invest_index where projectid like ?";
			DbHelper.update(sql2, new Object[] { SEQ_ID }, conn);
			// 查bsnum删小的
			String sql = "select bsnum from tzsp.approve_business_index where BUSINESS_OBJECT_CODE like ?";
			DataSet ds = DbHelper.query(sql, new Object[] { SEQ_ID }, conn);
			int n = ds.getTotal();
			JSONArray BSNUMS = ds.getJAData();
			for (int i = 0; i < n; i++) {
				String BSNUM = BSNUMS.getJSONObject(i).get("BSNUM").toString();
				// 打印历史表
				String sql3 = "delete from  tzsp.approve_print_history where BSNUM like ?";
				int result3 = DbHelper.update(sql3, new Object[] { BSNUM }, conn);
				// 办理事项审核记录表
				String sql5 = "delete from  tzsp.approve_business_course where BSNUM like ?";
				int result5 = DbHelper.update(sql5, new Object[] { BSNUM }, conn);
				// 作废事项表
				String sql6 = "delete from  tzsp.approve_voided where BSNUM like ?";
				int result6 = DbHelper.update(sql6, new Object[] { BSNUM }, conn);
			}
			// 办理事项记录主表
			String sql4 = "delete from tzsp.approve_business_index where BUSINESS_OBJECT_CODE like ?";
			DbHelper.update(sql4, new Object[] { SEQ_ID }, conn);
		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			return false;
		}

		return true;
	}

	public Boolean deles(ParameterSet pSet) throws Exception {
		String BSNUM = (String) pSet.getParameter("bsnum");
		BSNUM = "%" + BSNUM + "%";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			// 办理事项记录主表
			String sql = "delete from tzsp.approve_business_index where BSNUM like ?";
			DbHelper.update(sql, new Object[] { BSNUM }, conn);
			// 打印历史表
			String sql3 = "delete from  tzsp.approve_print_history where BSNUM like ?";
			DbHelper.update(sql3, new Object[] { BSNUM }, conn);
			// 办理事项审核记录表
			String sql5 = "delete from  tzsp.approve_business_course where BSNUM like ?";
			DbHelper.update(sql5, new Object[] { BSNUM }, conn);
			// 作废事项表
			String sql6 = "delete from  tzsp.approve_voided where BSNUM like ?";
			DbHelper.update(sql6, new Object[] { BSNUM }, conn);
		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			return false;
		}

		return true;
	}
    /**
     * 根据事项code获取事项详情
     * @param pSet
     * @return
     */
	public DataSet getItemInfoByCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String code = (String) pSet.getParameter("code");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getPermitItemInfo?code=" + code);
		try {
			Object itemInfo = RestUtil.getData(url);
			ds.setState(StateType.SUCCESS);
			ds.setRawData(itemInfo);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getPermitItemInfo！");
			return ds;
		}
		return ds;
	}
	/**
	 * 根据行业和区划获得目录信息
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectDirectory(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url") + "/getProjectDirectory");
		StringBuffer paramInfo = new StringBuffer();
		ArrayList<String> paramList = new ArrayList<String>();
		String region_code = (String) pSet.getParameter("region_code");
		if (!StringUtils.isEmpty(region_code)) {
			paramList.add("divisionCode=" + region_code);
		}
		String industryCode = (String) pSet.getParameter("industryCode");
		if (!StringUtils.isEmpty(industryCode)) {
			paramList.add("industryCode=" + industryCode);
		}
		String projectType = (String) pSet.getParameter("projectType");
		if (!StringUtils.isEmpty(projectType)) {
			paramList.add("projectType=" + projectType);
		}
		if (paramList.size() > 0) {
			for (int i = 0; i < paramList.size(); i++) {
				if (i == 0) {
					paramInfo.append(paramList.get(i));
				} else {
					paramInfo.append("&" + paramList.get(i));
				}
			}
			url += "?" + paramInfo.toString();
		}
		try {
			Object obj = RestUtil.getData(url);
			ds.setRawData(obj);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("处理数据失败getProjectDirectory！");
			return ds;
		}
		return ds;
	}	
	public DataSet getUserJDCX(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());

		try {
			JSONObject userInfo = (JSONObject) pSet.getParameter("userInfo");
			int page = (Integer) pSet.getParameter("page");
			int rows = (Integer) pSet.getParameter("limit");

			String sqlEx = "SELECT count(1) cnt FROM IPRO_INDEX WHERE uuid = ? ";
			int cnt = 0;
			try {
				DataSet result = DbHelper.query(sqlEx, new Object[] { userInfo.getString("uuid") }, conn);
				cnt = result.getRecord(0).getInt("CNT");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			conn = DbHelper.getConnection(this.getDataSourceName());
			sqlEx = "select * from (select rownum rn,t.* from (SELECT  SBLSH, SXBM, " +

			"SXMC, UUID, to_char(SBSJ, 'yyyy-mm-dd') SBSJ, CONTENT, PROJECTNAME,"
					+ "PROJECT_CODE,SEQID,STATUS,PROJECTID,IS_USE,PRE_REMARK FROM IPRO_INDEX i LEFT JOIN tzsp.DC_PROJECT  d ON "
					+ "d.SEQ_ID=i.SEQID LEFT JOIN tzsp.APPROVE_INVEST_INDEX  a   on a.PROJECTID=i.SEQID where uuid = ?  order by sbsj desc) t) WHERE rn>? and rn<=?";
			try {
				DataSet result = DbHelper.query(sqlEx,
						new Object[] { userInfo.getString("uuid"), (page - 1) * rows, page * rows }, conn);
				ds = result;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			ds.setTotal(cnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getIpro(ParameterSet pSet) throws Exception {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("SEQID");
		String sqlEx = "SELECT * FROM IPRO_INDEX WHERE seqid = ?";
		try {
			// 外网
			DataSet result = DbHelper.query(sqlEx, new Object[] { seqid }, conn);
			ds.setRawData(result.getRecord(0));
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		} finally {
			conn.close();
		}
		return ds;
	}

	public DataSet getContent(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			String sblsh = (String) pSet.getParameter("id");
			String sqlEx = "SELECT * FROM IPRO_INDEX WHERE sblsh = ?";
			try {
				// 外网
				DataSet result = DbHelper.query(sqlEx, new Object[] { sblsh }, conn);
				ds.setRawData(result.getRecord(0));
				ds.setState(StateType.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("查询错误");
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		}
		return ds;
	}
	public DataSet getCompanyContent(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			String sblsh = (String) pSet.getParameter("id");
			String sqlEx = "SELECT * FROM IPRO_INDEX WHERE sblsh = ?";
			try {
				// 外网
				DataSet result = DbHelper.query(sqlEx, new Object[] { sblsh }, conn);
				JSONObject content = result.getRecord(0).getJSONObject("CONTENT");
				JSONArray lerepInfo = content.getJSONArray("lerepInfo");
				JSONObject objt = JSONObject.fromObject(lerepInfo.get(0));
				ds.setRawData(objt);
				ds.setState(StateType.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("查询错误");
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		}
		return ds;
	}

	public DataSet getNData(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			String sblsh = (String) pSet.getParameter("id");
			String sqlEx = "SELECT * FROM IPRO_INDEX WHERE sblsh = ?";
			try {
				// 外网
				DataSet result = DbHelper.query(sqlEx, new Object[] { sblsh }, conn);
				ds.setRawData(result.getRecord(0));
				String seqid = (String) result.getRecord(0).get("SEQID");
				// System.out.println("===result:"+result.getRecord(0));
				JSONObject CONTENT = (JSONObject) result.getRecord(0).get("CONTENT");// 项目名称
				String ucname = result.getRecord(0).getString("UCNAME");
				// System.out.println("===CONTENT:"+CONTENT);
				// 外网项目信息可能修改字段
				String projectName = (String) CONTENT.get("projectName");// 项目名称
				String linkMan = (String) CONTENT.get("linkMan");// 项目负责人
				String linkPhone = (String) CONTENT.get("linkPhone");// 项目负责人联系方式
				String startYear = (String) CONTENT.get("startYear");// 拟开工时间
				String endYear = (String) CONTENT.get("endYear");// 拟建成时间
				String investment = (String) CONTENT.get("investment");// 总投资（万元）
				String placeName = (String) CONTENT.get("placeName");// 建设地点
				String placeAreaDetail = (String) CONTENT.get("placeAreaDetail");// 建设地点
				String divisionCode = (String) CONTENT.get("divisionCode");// 建设地点详情
				String projectContent = (String) CONTENT.get("projectContent");// 建设规模及内容
				
				JSONArray lerepInfo = CONTENT.getJSONArray("lerepInfo");
				JSONObject objt = JSONObject.fromObject(lerepInfo.get(0));
				
				// 外网企业信息可能修改字段
				String contractor = (String) objt.get("enterpriseName"); // 项目（法人）单位
				String contactName = (String) objt.get("contactName"); // 项目法人姓名
				String lerepCertType = (String) objt.get("lerepCerttype");// 项目（法人）单位证照类型
				String lerepCertNo = (String) objt.get("lerepCertno");// 项目（法人）单位证照号码
				String isDeArea = (String) CONTENT.get("isDeArea");// 是否开发区项目
				String deAreaName = (String) CONTENT.get("deAreaName");// 开发区名称

				// 内网
				pSet.put("SEQID", seqid);
				DataSet ds2 = getProjectInfoBySeqid(pSet);
				DataSet ds3 = getProjectLerepInfoBySeqid(pSet);
				// 内网项目信息可能修改字段
				String PROJECT_NAME = (String) ds2.getRecord(0).get("PROJECT_NAME");// 项目名称
				String LINK_MAN = (String) ds2.getRecord(0).get("LINK_MAN");// 项目负责人
				String LINK_PHONE = (String) ds2.getRecord(0).get("LINK_PHONE");// 项目负责人联系方式
				String START_YEAR = (String) ds2.getRecord(0).get("START_YEAR");// 拟开工时间
				String END_YEAR = (String) ds2.getRecord(0).get("END_YEAR");// 拟建成时间
				String INVESTMENT = (String) ds2.getRecord(0).get("INVESTMENT");// 总投资（万元）
				String PLACE_NAME = (String) ds2.getRecord(0).get("PLACE_NAME");// 建设地点
				String PLACE_AREA_DETAIL = (String) ds2.getRecord(0).get("PLACE_AREA_DETAIL");// 建设地点详情
				// System.out.println(PLACE_AREA_DETAIL);
				String DIVISION_CODE = (String) ds2.getRecord(0).get("DIVISION_CODE");// 区划编号
				String PROJECT_CONTENT = (String) ds2.getRecord(0).get("PROJECT_CONTENT");// 建设规模及内容
				String IS_DE_AREA = (String) ds2.getRecord(0).get("IS_DE_AREA");// 是否开发项目
				String DE_AREA_NAME = (String) ds2.getRecord(0).get("DE_AREA_NAME");// 是否开发项目
				// 内网企业信息可能修改字段
				String CONTRACTOR = (String) ds3.getRecord(0).get("ENTERPRISE_NAME"); // 项目（法人）单位
				String CONTRACTNAME = (String) ds3.getRecord(0).get("CONTACT_NAME"); // 项目法人姓名
				String LEREP_CERTTYPE = (String) ds3.getRecord(0).get("LEREP_CERTTYPE");// 项目（法人）单位证照类型
				String LEREP_CERTNO = (String) ds3.getRecord(0).get("LEREP_CERTNO");// 项目（法人）单位证照号码
																					// System.out.println("====PROJECT_NAME:"+PROJECT_NAME);

				JSONObject update = new JSONObject();
				JSONObject obj;
				obj = CONTENT;
				Boolean isChange;
				// 判断项目名称是否有变化
				if (!(projectName.equals(PROJECT_NAME))) {
					System.out.println("“项目名称”字段有变化：" + PROJECT_NAME);
					obj.put("projectName", PROJECT_NAME);
					update.put("projectName", PROJECT_NAME);
				}
				// 判断项目负责人是否有变化
				if (linkMan==null) {
					System.out.println("“项目负责人”字段有变化1：" + LINK_MAN);
					obj.put("linkMan", LINK_MAN);
					update.put("linkMan", LINK_MAN);
					isChange = true;
				}else {
					if (!(linkMan.equals(LINK_MAN))) {
						System.out.println("“项目负责人”字段有变化：" + LINK_MAN);
						obj.put("linkMan", LINK_MAN);
						update.put("linkMan", LINK_MAN);
						isChange = true;
					}else {
						isChange = false;
					}
				}
				// 判断项目负责人联系方式是否有变化
				if (linkPhone==null) {
					System.out.println("“项目负责人联系方式”字段有变化1：" + LINK_PHONE);
					obj.put("linkPhone", LINK_PHONE);
					update.put("linkPhone", LINK_PHONE);
					isChange = true;
				}else {
					if (!(linkPhone.equals(LINK_PHONE))) {
						System.out.println("“项目负责人联系方式”字段有变化：" + LINK_PHONE);
						obj.put("linkPhone", LINK_PHONE);
						update.put("linkPhone", LINK_PHONE);
						isChange = true;
					}else {
						isChange = false;
					}
				}
				// 判断拟开工时间是否有变化
				if (!(startYear.equals(START_YEAR))) {
					System.out.println("“拟开工时间”字段有变化：" + START_YEAR);
					obj.put("startYear", START_YEAR);
					update.put("startYear", START_YEAR);
				}
				// 判断拟建成时间是否有变化
				if (!(endYear.equals(END_YEAR))) {
					System.out.println("“拟建成时间”字段有变化：" + END_YEAR);
					obj.put("endYear", END_YEAR);
					update.put("endYear", END_YEAR);
				}
				// 判断总投资（万元）是否有变化
				if (!(investment.equals(INVESTMENT))) {
					System.out.println("“总投资（万元）”字段有变化：" + INVESTMENT);
					obj.put("investment", INVESTMENT);
					update.put("investment", INVESTMENT);
				}
				// 判断建设地点是否有变化
				if (!(placeName.equals(PLACE_NAME))) {
					System.out.println("“建设地点”字段有变化：" + PLACE_NAME);
					obj.put("placeName", PLACE_NAME);
					update.put("placeName", PLACE_NAME);
				}
				// 判断建设地点详情是否有变化
				if (!(placeAreaDetail.equals(PLACE_AREA_DETAIL))) {
					System.out.println("“建设地点详情”字段有变化：" + PLACE_AREA_DETAIL);
					obj.put("placeAreaDetail", PLACE_AREA_DETAIL);
					update.put("placeAreaDetail", PLACE_AREA_DETAIL);
				}
				// 判断区划编号是否有变化
				if (!(divisionCode.equals(DIVISION_CODE))) {
					System.out.println("“区划编号”字段有变化：" + DIVISION_CODE);
					obj.put("divisionCode", DIVISION_CODE);
					update.put("divisionCode", DIVISION_CODE);
				}
				// 判断建设规模及内容是否有变化
				if (!(projectContent.equals(PROJECT_CONTENT))) {
					System.out.println("“建设规模及内容”字段有变化!");
					obj.put("projectContent", PROJECT_CONTENT);
					update.put("projectContent", PROJECT_CONTENT);
				}
				// 判断项目（法人）单位是否有变化
				if (!(contractor.equals(CONTRACTOR))) {
					System.out.println("“项目（法人）单位”字段有变化：" + CONTRACTOR);
					objt.put("enterpriseName", CONTRACTOR);
					update.put("enterpriseName", CONTRACTOR);
				}
				// 判断项目法人姓名是否有变化
				if (!(contactName.equals(CONTRACTNAME))) {
					System.out.println("“项目法人姓名”字段有变化：" + CONTRACTNAME);
					objt.put("contactName", CONTRACTNAME);
					update.put("contactName", CONTRACTNAME);
				}
				// 判断项目（法人）单位证照类型是否有变化
				if (!(lerepCertType.equals(LEREP_CERTTYPE))) {
					System.out.println("“项目（法人）单位证照类型”字段有变化：" + LEREP_CERTTYPE);
					objt.put("lerepCertType", LEREP_CERTTYPE);
					update.put("lerepCertType", LEREP_CERTTYPE);
				}
				// 判断项目（法人）单位证照号码是否有变化
				if (!(lerepCertNo.equals(LEREP_CERTNO))) {
					System.out.println("“项目（法人）单位证照号码”字段有变化：" + LEREP_CERTNO);
					objt.put("lerepCertno", LEREP_CERTNO);
					update.put("lerepCertno", LEREP_CERTNO);
				}
				if (!(isDeArea.equals(IS_DE_AREA))) {
					System.out.println("“是否开发区项目”字段有变化：" + IS_DE_AREA);
					obj.put("isDeArea", IS_DE_AREA);
					update.put("isDeArea", IS_DE_AREA);
					if (isDeArea.equals("0")) {
						System.out.println("“开发区名称”字段有变化：" + DE_AREA_NAME);
						obj.put("deAreaName", DE_AREA_NAME);
						update.put("deAreaName", DE_AREA_NAME);
					} else if (isDeArea.equals("1")) {
						System.out.println("“开发区名称”字段有变化!");
						obj.remove("deAreaName");
					}
				}
				if ((isDeArea.equals(IS_DE_AREA))) {
					if (isDeArea.equals("1")) {
						if (!(deAreaName.equals(DE_AREA_NAME))) {
							System.out.println("开发区名称字段有变化：" + DE_AREA_NAME);
							obj.put("deAreaName", DE_AREA_NAME);
							update.put("deAreaName", DE_AREA_NAME);
						}
					}
				}
				lerepInfo.remove(0);
				lerepInfo.add(0, objt);
				obj.put("lerepInfo", lerepInfo);
				obj.put("contractor", ucname);
				pSet.put("baseInfo", obj);
				// 判断字段值是否有变化，有则调用updateIpro方法
				if (!(projectName.equals(PROJECT_NAME)) || !(startYear.equals(START_YEAR))
						|| !(endYear.equals(END_YEAR)) || !(investment.equals(INVESTMENT))
						|| !(placeName.equals(PLACE_NAME)) || !(placeAreaDetail.equals(PLACE_AREA_DETAIL))
						|| !(divisionCode.equals(DIVISION_CODE)) || !(projectContent.equals(PROJECT_CONTENT))
						|| !(contractor.equals(CONTRACTOR)) ||!(contactName.equals(CONTRACTNAME))
						||isChange|| !(lerepCertType.equals(LEREP_CERTTYPE))
						|| !(lerepCertNo.equals(LEREP_CERTNO)) || !(isDeArea.equals(IS_DE_AREA))
						|| (isDeArea.equals(IS_DE_AREA)) && isDeArea.equals("1")
								&& !(deAreaName.equals(DE_AREA_NAME))) {
					// 修改外网数据库
					System.out.println("需要修改外网字段~~~~~~update：" + update);
					updateIpro(pSet);
				}
				ds.setState(StateType.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("查询错误");
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		}
		return ds;
	}

	public DataSet getProjectInfoBySeqid(ParameterSet pSet) throws Exception {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("SEQID");
		// System.out.println("=========SEQ_ID:"+seqid);
		try {

			String sqlEx = "SELECT * FROM tzsp.DC_PROJECT WHERE seq_id = ?";
			DataSet result = DbHelper.query(sqlEx, new Object[] { seqid }, conn);
			ds.setRawData(result.getRecord(0));
			System.out.println("内网信息~~~~~dc_project：" + result);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		} finally {
			conn.close();
		}
		return ds;
	}
	public DataSet getProjectLerepInfoBySeqid(ParameterSet pSet) throws Exception {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("SEQID");
		// System.out.println("=========SEQ_ID:"+seqid);
		try {

			String sqlEx = "SELECT * FROM tzsp.APPROVE_PROJECT_LEREP_INFO WHERE PROJECT_ID = ?";
			DataSet result = DbHelper.query(sqlEx, new Object[] { seqid }, conn);
			
			ds.setRawData(result.getRecord(0));
			System.out.println("内网用户信息~~~~~dc_project：" + result);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		} finally {
			conn.close();
		}
		return ds;
	}
	//根据seqid获取外商出资信息
	public DataSet getContributionBySeqid(ParameterSet pSet) throws Exception {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("SEQID");
		// System.out.println("=========SEQ_ID:"+seqid);
		try {

			String sqlEx = "SELECT * FROM tzsp.APPROVE_CONTRIBUTION_INFO WHERE PROJECT_ID = ?";
			DataSet result = DbHelper.query(sqlEx, new Object[] { seqid }, conn);
			
			ds.setRawData(result.getRecord(0));
			System.out.println("外商出资信息~~~~~dc_project：" + result);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		} finally {
			conn.close();
		}
		return ds;
	}
	
	

	public Boolean updateIpro(ParameterSet pSet) throws Exception {
		String seqid = (String) pSet.getParameter("SEQID");
		// System.out.println("updateIpro~~~~~seqid:"+seqid);
		JSONObject baseInfo = ((JSONObject) pSet.get("baseInfo"));
		System.out.println("需修改外网基本信息~~~~~~baseInfo:" + baseInfo);
		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {

			sql = "update ipro_index set ucname=?, region_code=?, region_name=?, content=?, projectname=?"
					+ " where seqid=?";
			DbHelper.update(sql,
					new Object[] { baseInfo.getString("contractor"), baseInfo.getString("divisionCode") + "000000",
							baseInfo.getString("placeAreaDetail"), baseInfo.toString(),
							baseInfo.getString("projectName"), seqid },
					conn);
		} catch (Exception e) {
			e.printStackTrace();
			conn.close();
			return false;
		}
		return true;
	}

	public DataSet getXmnb(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());

		try {

			String uuid = (String) pSet.getParameter("uuid");
			String sqlEx = "select t3.check_state, t2.uuid,t2.projectname,t3.project_code,t3.report_type,t3.report_date, t3.remark from tzsp.dc_project t1 inner join ipro_index t2 on t2.seqid=t1.seq_id inner join tzsp.APPROVE_PROJECT_BUILD_INFO t3 "
					+ "on t1.project_code=t3.project_code where uuid=?";

			try {
				DataSet result = DbHelper.query(sqlEx, new Object[] { uuid }, conn);
				ds = result;
			} catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("查询错误");
			} finally {
				conn.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("查询错误");
		}
		return ds;
	}

	public DataSet saveXMNB(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String projectCode = (String) pSet.getParameter("projectCode");
		String applyDate = (String) pSet.getParameter("applyDate");
		String reportType = (String) pSet.getParameter("reportType");
		String constructYear = (String) pSet.getParameter("constructYear");
		String remark = (String) pSet.getParameter("remark");

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			Date date = formatter.parse(applyDate);
			sql = "insert into TZSP.APPROVE_PROJECT_BUILD_INFO(ID, PROJECT_CODE, CONSTRUCT_YEAR, REPORT_TYPE, REPORT_DATE, CREATE_TIME, OPINION, REMARK, CHECK_DATE, REVIEWER, CHECK_STATE, DB_FLAG) "
					+ "values(sys_guid(),?,?,?,?,?,?,?,?,?,?,?)";
			DbHelper.update(sql,
					new Object[] { projectCode, constructYear, reportType,
							CommonUtils_api.getInstance().parseDateToTimeStamp(date,
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
					CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
							CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), null, remark, null, null, 0, 1 },
					conn);

		} catch (Exception e) {
			e.printStackTrace();

			return ds;
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return ds;
	}

	public DataSet queryMatters(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String sql = "select * from tzsp.dc_project d order by d.PROJECT_TYPE";
		ds = DbHelper.query(sql, null, conn);
		return ds;
	}

	public DataSet queryChildMatter(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String objectCode = (String) pSet.getParameter("object_code");
		String sql = "select * from tzsp.approve_business_index a where a.BUSINESS_OBJECT_CODE=?";
		ds = DbHelper.query(sql, new Object[] { objectCode }, conn);
		return ds;
	}

	public DataSet queryByMatterName(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String projectName = (String) pSet.getParameter("project_name");
		projectName = "%" + projectName + "%";
		String sql = "select * from tzsp.dc_project d where d.PROJECT_NAME like ?";
		ds = DbHelper.query(sql, new Object[] { projectName }, conn);
		return ds;
	}

	public DataSet queryByMatterCode(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String projectCode = (String) pSet.getParameter("project_code");
		projectCode = "%" + projectCode + "%";
		String sql = "select * from tzsp.dc_project d where d.PROJECT_CODE like ?";
		ds = DbHelper.query(sql, new Object[] { projectCode }, conn);
		return ds;
	}

	public DataSet delMatter(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("seqid");
		String sql = "select * from tzsp.DC_PROJECT dp left join tzsp.approve_invest_index aii on dp.SEQ_ID=aii.PROJECTID left join  tzsp.approve_business_index abi on dp.SEQ_ID=abi.BUSINESS_OBJECT_CODE left join tzsp.approve_business_course abc on abi.BSNUM=abc.BSNUM left join tzsp.approve_print_history aph on abi.BSNUM=aph.BSNUM left join tzsp.approve_voided av on abi.BSNUM=av.BSNUM where SEQ_ID = ?";
		ds = DbHelper.query(sql, new Object[] { seqid }, conn);
		return ds;
	}

	public DataSet delChildMatter(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String bsnum = (String) pSet.getParameter("bsnum");
		String sql = "select * from tzsp.approve_business_index abi left join tzsp.approve_business_course abc on abi.BSNUM=abc.BSNUM left join tzsp.approve_print_history aph on abi.BSNUM=aph.BSNUM left join tzsp.approve_voided av on abi.BSNUM=av.BSNUM where abi.BSNUM = ?";
		ds = DbHelper.query(sql, new Object[] { bsnum }, conn);
		return ds;
	}

	public DataSet detailedChildMatter(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String bsnum = (String) pSet.getParameter("bsnum");
		String sql = "select * from tzsp.approve_business_index abi left join tzsp.approve_business_course abc on abi.BSNUM=abc.BSNUM left join tzsp.approve_print_history aph on abi.BSNUM=aph.BSNUM left join tzsp.approve_voided av on abi.BSNUM=av.BSNUM where abi.BSNUM = ?";
		ds = DbHelper.query(sql, new Object[] { bsnum }, conn);
		return ds;
	}

	public DataSet detailMatter(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String seqid = (String) pSet.getParameter("seqid");
		String sql = "select * from tzsp.DC_PROJECT dp left join tzsp.approve_invest_index aii on dp.SEQ_ID=aii.PROJECTID left join  tzsp.approve_business_index abi on dp.SEQ_ID=abi.BUSINESS_OBJECT_CODE left join tzsp.approve_business_course abc on abi.BSNUM=abc.BSNUM left join tzsp.approve_print_history aph on abi.BSNUM=aph.BSNUM left join tzsp.approve_voided av on abi.BSNUM=av.BSNUM where SEQ_ID = ?";
		ds = DbHelper.query(sql, new Object[] { seqid }, conn);
		return ds;
	}

	public DataSet queryByReceiveNumber(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String receiveNumber = (String) pSet.getParameter("receive_number");
		String sql = "select * from tzsp.approve_business_index  a  where  a.RECEIVE_NUMBER=?";
		ds = DbHelper.query(sql, new Object[] { receiveNumber }, conn);
		return ds;
	}

	public DataSet queryByItemName(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String itemName = (String) pSet.getParameter("item_name");
		itemName = "%" + itemName + "%";
		String sql = "select * from tzsp.approve_business_index a where a.ITEM_NAME like ?";
		ds = DbHelper.query(sql, new Object[] { itemName }, conn);
		return ds;
	}

	public DataSet queryAllChildMatter(ParameterSet pSet) {
		DataSet ds;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String sql = "select * from tzsp.approve_business_index";
		ds = DbHelper.query(sql, null, conn);
		return ds;
	}

	public DataSet getNewsMattersCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String code = (String) pSet.getParameter("code");
		code = code + "%";
		try {
			String sqlEx = "select count(1) cnt  from  tzsp.dc_project d  where d.DIVISION_CODE  like ?";
			int cnt = 0;
			try {
				DataSet result = DbHelper.query(sqlEx, new Object[] { code }, conn);
				cnt = result.getRecord(0).getInt("CNT");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			conn = DbHelper.getConnection(this.getDataSourceName());
			sqlEx = "select count(2) as num2  from  tzsp.approve_business_index abi  where  abi.REGION_CODE  like ?";
			try {
				DataSet result = DbHelper.query(sqlEx, new Object[] { code }, conn);
				ds = result;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			ds.setTotal(cnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getMattersCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String code = (String) pSet.getParameter("code");
		code = code + "%";
		String sql = "select count(2) as num2  from  tzsp.approve_business_index abi  where  abi.region_code  like ?";
		try {
			DataSet result = DbHelper.query(sql, new Object[] { code }, conn);
			ds = result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getMCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		String code = (String) pSet.getParameter("code");
		code = "%" + code + "%";
		String sql = "select count(1) as num1  from  tzsp.dc_project d  where d.DIVISION_CODE  like ?";
		try {
			ds = DbHelper.query(sql, new Object[] { code }, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	

}
