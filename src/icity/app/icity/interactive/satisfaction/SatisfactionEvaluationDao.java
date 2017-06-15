package app.icity.interactive.satisfaction;

import java.util.Date;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * <p>
 * 用户满意度评价数据库操作接口
 * </p>
 */
public class SatisfactionEvaluationDao extends BaseJdbcDao {

	private SatisfactionEvaluationDao() {
		// 使用网办数据库链接字串产生一个Dao
		this.setDataSourceName("icityDataSource");
	}

	public static SatisfactionEvaluationDao getInstance() {
		return DaoFactory.getDao(SatisfactionEvaluationDao.class.getName());
	}

	// 获取基本评价信息
	public DataSet getBasicEvaluationMessage(ParameterSet pSet) {
		DataSet ds;
		String sql = "select * from BUSINESS_INDEX where STATE in ('02','13','97','98','99') and SBLSH = ?";
		ds = this.executeDataset(sql, new Object[] { pSet.getParameter("SBLSH") });
		return ds;
	}

	// 插入评价
	public DataSet insertNewEvaluation1(ParameterSet pSet) {
		// 查询数据库中事项是否已被评价
		DataSet ds;
		String sql = "select 1 from STAR_LEVEL_EVALUATION where SERIAL_NUMBER = ?";
		ds = this.executeDataset(sql, new Object[] { pSet.getParameter("SBLSH") });
		if (ds.getTotal() > 0) {
			// 评价过了
			ds.setState(StateType.FAILT);
			ds.setMessage("当前事项已评！");
			return ds;
		}
		String sqlBusiness = "select * from business_index where sblsh = ?";
		ds = this.executeDataset(sqlBusiness, new Object[] { pSet.getParameter("SBLSH") });
		if (ds.getTotal() < 1) {
			// 评价过了
			ds.setState(StateType.FAILT);
			ds.setMessage("业务不存在！");
			return ds;
		}
		// 生成一个UUID
		String strId = Tools.getUUID32();
		// 获取评价等级
		Double strStarLevel = 0.0;
		if (("" + pSet.getParameter("STAR_LEVEL")).length() > 3) {
			strStarLevel = Double.valueOf(("" + pSet.getParameter("STAR_LEVEL")).substring(0, 3));
		} else {
			strStarLevel = Double.valueOf("" + pSet.getParameter("STAR_LEVEL"));
		}
		Integer strTimeStarLevel = 0;
		Integer strQuueryStarLevel = 0;
		Integer strMajorStarLevel = 0;
		Integer strConven = 0;
		if (pSet.getParameter("TIME_STAR_LEVEL") != null) {
			strTimeStarLevel = Integer.valueOf("" + pSet.getParameter("TIME_STAR_LEVEL"));
			strQuueryStarLevel = Integer.valueOf("" + pSet.getParameter("QUALITY_STAR_LEVEL"));
			strMajorStarLevel = Integer.valueOf("" + pSet.getParameter("MAJOR_STAR_LEVEL"));
		}
		if (pSet.getParameter("CONVENIENCE_STAR_LEVEL") != null) {
			strConven = Integer.valueOf("" + pSet.getParameter("CONVENIENCE_STAR_LEVEL"));
		}
		// 获取申办流水号
		String strSerialNumber = (String) pSet.getParameter("SBLSH");
		// 获取办事人名字
		String strBSRName = ds.getRecord(0).getString("SQRMC");
		// 评价内容
		String strEvaluateContent = (String) pSet.getParameter("EVALUATE_CONTENT");
		// 获取远端ip
		String remoteAddr = pSet.getRemoteAddr();
		// 获取创建者id
		String strCreatorId = (String) pSet.getParameter("CreatorId");
		//是否匿名评价
		String CRYPTONYM = (String) pSet.getParameter("CRYPTONYM");
		//评价类型
		String EVALUATE_TYPE = (String) pSet.getParameter("EVALUATE_TYPE");
		if(StringUtil.isNotEmpty(CRYPTONYM)){
			//（默认匿名）
			CRYPTONYM = "0";
		}
		sql = "insert into STAR_LEVEL_EVALUATION(ID,BSR_NAME,SERIAL_NUMBER,STAR_LEVEL,EVALUATE_CONTENT,"
				+ "CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_CODE,SERVICE_ID,SERVICE_NAME,SERVICE_ORG_ID,"
				+ "SERVICE_ORG_NAME,REGION_CODE,REGION_NAME,APPLY_NAME,EVALUATE_TYPE,TIME_STAR_LEVEL,QUALITY_STAR_LEVEL,MAJOR_STAR_LEVEL,CONVENIENCE_STAR_LEVEL,CRYPTONYM) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i;
			i = this.executeUpdate(sql,
					new Object[] { strId, strBSRName, strSerialNumber, strStarLevel, strEvaluateContent, remoteAddr,
							strCreatorId,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							ds.getRecord(0).getString("SXBM"), ds.getRecord(0).getString("SXID"),
							ds.getRecord(0).getString("SXMC"), ds.getRecord(0).getString("SJDWDM"),
							ds.getRecord(0).getString("SJDW"), ds.getRecord(0).getString("XZQHDM"),ds.getRecord(0).getString("XZQH"),
							ds.getRecord(0).getString("SBXMMC"),EVALUATE_TYPE,strTimeStarLevel, strQuueryStarLevel,
							strMajorStarLevel, strConven,CRYPTONYM });

			if (0 == i) {
				ds.setState(StateType.FAILT);
				ds.setMessage("添加内容失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 插入评价
	public DataSet insertNewEvaluation(ParameterSet pSet) {
		// 查询数据库中事项是否已被评价
		DataSet ds;
		String sql = "select 1 from STAR_LEVEL_EVALUATION where SERIAL_NUMBER = ?";
		ds = this.executeDataset(sql, new Object[] { pSet.getParameter("SBLSH") });
		if (ds.getTotal() > 0) {
			// 评价过了
			ds.setState(StateType.FAILT);
			ds.setMessage("当前事项已评！");
			return ds;
		}
		// 生成一个UUID
		String strId = Tools.getUUID32();
		// 获取评价等级
		Double strStarLevel = 0.0;
		if (("" + pSet.getParameter("STAR_LEVEL")).length() > 3) {
			strStarLevel = Double.valueOf(("" + pSet.getParameter("STAR_LEVEL")).substring(0, 3));
		} else {
			strStarLevel = Double.valueOf("" + pSet.getParameter("STAR_LEVEL"));
		}
		Integer strTimeStarLevel = 0;
		Integer strQuueryStarLevel = 0;
		Integer strMajorStarLevel = 0;
		if (pSet.getParameter("TIME_STAR_LEVEL") != null) {
			strTimeStarLevel = Integer.valueOf("" + pSet.getParameter("TIME_STAR_LEVEL"));
			strQuueryStarLevel = Integer.valueOf("" + pSet.getParameter("QUALITY_STAR_LEVEL"));
			strMajorStarLevel = Integer.valueOf("" + pSet.getParameter("MAJOR_STAR_LEVEL"));
		}
		// 获取申办流水号
		String strSerialNumber = (String) pSet.getParameter("SBLSH");
		// 获取办事人名字
		String strBSRName = (String) pSet.getParameter("SQRMC");
		//评价类型
		String EVALUATE_TYPE = (String) pSet.getParameter("EVALUATE_TYPE");
		// 评价内容
		String strEvaluateContent = (String) pSet.getParameter("EVALUATE_CONTENT");
		// 获取远端ip
		String remoteAddr = pSet.getRemoteAddr();
		// 获取创建者id
		String strCreatorId = (String) pSet.getParameter("CreatorId");
		sql = "insert into STAR_LEVEL_EVALUATION(ID,BSR_NAME,SERIAL_NUMBER,STAR_LEVEL,EVALUATE_CONTENT,"
				+ "CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_CODE,SERVICE_ID,SERVICE_NAME,SERVICE_ORG_ID,"
				+ "SERVICE_ORG_NAME,REGION_CODE,APPLY_NAME,TIME_STAR_LEVEL,QUALITY_STAR_LEVEL,MAJOR_STAR_LEVEL,EVALUATE_TYPE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i;
			i = this.executeUpdate(sql,
					new Object[] { strId, strBSRName, strSerialNumber, strStarLevel, strEvaluateContent, remoteAddr,
							strCreatorId,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							pSet.get("ITEM_CODE"), pSet.get("ITEM_ID"), pSet.get("ITEM_NAME"), pSet.get("DEPT_ID"),
							pSet.get("DEPT_NAME"), pSet.get("REGION_ID"), pSet.get("APPLY_SUBJECT"), strTimeStarLevel,
							strQuueryStarLevel, strMajorStarLevel,EVALUATE_TYPE });

			if (0 == i) {
				ds.setState(StateType.FAILT);
				ds.setMessage("添加内容失败！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}

	// 获取星级及意见
	public DataSet queryStarLevel(ParameterSet pSet) {
		DataSet ds;
		String sql = "select SERIAL_NUMBER,STAR_LEVEL,TIME_STAR_LEVEL,QUALITY_STAR_LEVEL,MAJOR_STAR_LEVEL,EVALUATE_CONTENT from STAR_LEVEL_EVALUATION where SERIAL_NUMBER = ?";
		ds = this.executeDataset(sql, new Object[] { pSet.getParameter("sblsh") });
		return ds;
	}

	public DataSet getSbsxlist(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sql = "select * from BUSINESS_INDEX where STATE in ('02','13','97','98','99') and UCID = ? and not exists  (select 1 from star_level_evaluation where serial_number = sblsh)";
		try {
			ds = this.executeDataset(sql, new Object[] { pSet.getParameter("CreatorId") });
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("");
		}
		return ds;
	}

	public DataSet getBjpjList(ParameterSet pSet) {

		StringBuffer str = new StringBuffer();
		str.append("select t.service_org_name,")
				.append("count(case when t.STAR_LEVEL='1' then id else null end) count1,")
				.append("count(case when t.STAR_LEVEL='2' then id else null end) count2,")
				.append("count(case when t.STAR_LEVEL='3' then id else null end) count3,")
				.append("count(case when t.STAR_LEVEL='4' or t.STAR_LEVEL='5' then id else null end) count4 ")
				.append("from star_level_evaluation t ");
		String sql = SqlCreator.getSimpleQuerySql(pSet, str.toString(), getDataSource());
		sql += " group by t.service_org_name ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet getBjpjListAll(ParameterSet pSet) {

		StringBuffer str = new StringBuffer();
		str.append("select t.service_org_name,")
				.append("count(case when t.STAR_LEVEL='1' then id else null end) count1,")
				.append("count(case when t.STAR_LEVEL='2' then id else null end) count2,")
				.append("count(case when t.STAR_LEVEL='3' then id else null end) count3,")
				.append("count(case when t.STAR_LEVEL='4' or t.STAR_LEVEL='5' then id else null end) count4 ")
				.append("from star_level_evaluation t ");
		String sql = SqlCreator.getSimpleQuerySql(pSet, str.toString(), getDataSource());
		sql += " group by t.service_org_name ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet QueryCount(ParameterSet pSet) {

		StringBuffer str = new StringBuffer();
		str.append("select ").append("count(case when t.STAR_LEVEL='1' then id else null end) c1,")
				.append("count(case when t.STAR_LEVEL='2' then id else null end) c2,")
				.append("count(case when t.STAR_LEVEL='3' then id else null end) c3,")
				.append("count(case when t.STAR_LEVEL='4' or t.STAR_LEVEL='5' then id else null end) c4 ")
				.append("from star_level_evaluation t ");
		String sql = SqlCreator.getSimpleQuerySql(pSet, str.toString(), getDataSource());
		return this.executeDataset(sql, null);
	}

	// 十堰 插入部门评价
	public DataSet insertDeptEvaluation(ParameterSet pSet) {
		DataSet ds = new DataSet();
		// 生成一个UUID
		String strId = Tools.getUUID32();
		// 获取评价等级
		Double strStarLevel = Double.valueOf("" + pSet.getParameter("STAR_LEVEL"));
		Integer strTimeStarLevel = 0;
		Integer strQuueryStarLevel = 0;
		Integer strMajorStarLevel = 0;
		if (pSet.getParameter("TIME_STAR_LEVEL") != null) {
			strTimeStarLevel = Integer.valueOf("" + pSet.getParameter("TIME_STAR_LEVEL"));
			strQuueryStarLevel = Integer.valueOf("" + pSet.getParameter("QUALITY_STAR_LEVEL"));
			strMajorStarLevel = Integer.valueOf("" + pSet.getParameter("MAJOR_STAR_LEVEL"));
		}
		// 获取申办流水号
		String strSerialNumber = (String) pSet.getParameter("SBLSH");
		// 获取远端ip
		String remoteAddr = pSet.getRemoteAddr();
		String strCreatorId = (String) pSet.getParameter("CreatorId");
		String strBSRName = (String) pSet.getParameter("BSR_NAME");
		String sql = "insert into STAR_LEVEL_EVALUATION(ID,BSR_NAME,SERIAL_NUMBER,STAR_LEVEL,EVALUATE_CONTENT,"
				+ "CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_CODE,SERVICE_ID,SERVICE_NAME,SERVICE_ORG_ID,"
				+ "SERVICE_ORG_NAME,REGION_CODE,APPLY_NAME,TIME_STAR_LEVEL,QUALITY_STAR_LEVEL,MAJOR_STAR_LEVEL,EVALUATE_TYPE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i;
			i = this.executeUpdate(sql,
					new Object[] { strId, strBSRName, strSerialNumber, strStarLevel, null, remoteAddr, strCreatorId,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							pSet.get("ITEM_CODE"), pSet.get("ITEM_ID"), pSet.get("ITEM_NAME"), pSet.get("DEPT_ID"),
							pSet.get("DEPT_NAME"), pSet.get("REGION_ID"), pSet.get("APPLY_SUBJECT"), strTimeStarLevel,
							strQuueryStarLevel, strMajorStarLevel, pSet.get("EVALUATE_TYPE") });

			if (0 == i) {
				ds.setState(StateType.FAILT);
				ds.setMessage("添加内容失败！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	
	// 插入评价
	public DataSet insertEvaluation(ParameterSet pSet) {
		// 查询数据库中事项是否已被评价
		DataSet ds;
		String sql = "select 1 from STAR_LEVEL_EVALUATION where SERIAL_NUMBER = ?";
		ds = this.executeDataset(sql, new Object[] { pSet.getParameter("SBLSH") });
		if (ds.getTotal() > 0) {
			// 评价过了
			ds.setState(StateType.FAILT);
			ds.setMessage("当前事项已评！");
			return ds;
		}
		// 生成一个UUID
		String strId = Tools.getUUID32();
		
		// 获取申办流水号
		String strSerialNumber = (String) pSet.getParameter("SBLSH");
		// 获取办事人名字
		String strBSRName = (String) pSet.getParameter("SQRMC");
		// 评价内容
		String strEvaluateContent = (String) pSet.getParameter("EVALUATE_CONTENT");
		// 获取远端ip
		String remoteAddr = pSet.getRemoteAddr();
		// 获取创建者id
		String strCreatorId = (String) pSet.getParameter("CreatorId");
		sql = "insert into STAR_LEVEL_EVALUATION(ID,BSR_NAME,SERIAL_NUMBER,STAR_LEVEL,EVALUATE_CONTENT,"
				+ "CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_CODE,SERVICE_ID,SERVICE_NAME,SERVICE_ORG_ID,"
				+ "SERVICE_ORG_NAME,REGION_CODE,APPLY_NAME,EVALUATE_TYPE,TIME_STAR_LEVEL,QUALITY_STAR_LEVEL,MAJOR_STAR_LEVEL) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i;
			i = this.executeUpdate(sql,
					new Object[] { strId, strBSRName, strSerialNumber, pSet.getParameter("STAR_LEVEL"), strEvaluateContent, remoteAddr,
							strCreatorId,
							CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
									CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),
							pSet.get("ITEM_CODE"), pSet.get("ITEM_ID"), pSet.get("ITEM_NAME"), pSet.get("DEPT_ID"),
							pSet.get("DEPT_NAME"), pSet.get("REGION_ID"), pSet.get("APPLY_SUBJECT"),pSet.get("EVALUATE_TYPE"),
							pSet.get("TIME_STAR_LEVEL"),pSet.get("QUALITY_STAR_LEVEL"),pSet.get("MAJOR_STAR_LEVEL")});

			if (0 == i) {
				ds.setState(StateType.FAILT);
				ds.setMessage("评价失败！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("评价失败！");
			e.printStackTrace();
		}
		return ds;
	}
	// 插入办事指南评价
	public DataSet insertGuideEvaluation(ParameterSet pSet) {
		// 查询数据库中事项是否已被评价
		DataSet ds;
		String sql = "select 1 from STAR_LEVEL_EVALUATION where SERVICE_ID = ? AND CREATOR_ID = ? and evaluate_type='3'";
		ds = this.executeDataset(sql, new Object[] { pSet.getParameter("ITEM_ID"),pSet.getParameter("CreatorId")});
		if (ds.getTotal() > 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("你已评价该办事指南！");
		}else{
			// 生成一个UUID
			String strId = Tools.getUUID32();
			// 评价内容
			String strEvaluateContent = (String) pSet.getParameter("EVALUATE_CONTENT");
			// 获取远端ip
			String remoteAddr = pSet.getRemoteAddr();
			// 获取创建者id
			String strCreatorId = (String) pSet.getParameter("CreatorId");
			String pjr_name=  (String) pSet.getParameter("CreatorName");
			sql = "insert into STAR_LEVEL_EVALUATION(ID,STAR_LEVEL,EVALUATE_CONTENT,"
					+ "CLIENT_IP,CREATOR_ID,CREATOR_DATE,SERVICE_ID,SERVICE_NAME,"
					+ "SERVICE_ORG_NAME,REGION_CODE,EVALUATE_TYPE,STATE,PJR_NAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				int i;
				i = this.executeUpdate(sql,
						new Object[] { strId, pSet.getParameter("STAR_LEVEL"), strEvaluateContent, remoteAddr,
						strCreatorId,
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),
								CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS), pSet.get("ITEM_ID"), pSet.get("ITEM_NAME"),
								pSet.get("DEPT_NAME"), pSet.get("REGION_ID"),pSet.get("EVALUATE_TYPE"),0,pjr_name});
				
				if (0 == i) {
					ds.setState(StateType.FAILT);
					ds.setMessage("添加评价内容失败！");
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				e.printStackTrace();
			}
		}
		
		return ds;
	}
	
	public DataSet getPjPercent(ParameterSet pSet) {

		StringBuffer str = new StringBuffer();
		str.append("select t.service_org_name,")
				.append("count(case when t.STAR_LEVEL='1' then id else null end) count1,")
				.append("count(case when t.STAR_LEVEL='2' then id else null end) count2,")
				.append("count(case when t.STAR_LEVEL='3' then id else null end) count3,")
				.append("count(case when t.STAR_LEVEL='4' then id else null end) count4,")
				.append("count(case when t.STAR_LEVEL='5' then id else null end) count5,")
				.append("sum(case when t.STAR_LEVEL='1' then STAR_LEVEL else 0 end) sum1,")
				.append("sum(case when t.STAR_LEVEL='2' then STAR_LEVEL else 0 end) sum2,")
				.append("sum(case when t.STAR_LEVEL='3' then STAR_LEVEL else 0 end) sum3,")
				.append("sum(case when t.STAR_LEVEL='4' then STAR_LEVEL else 0 end) sum4,")
				.append("sum(case when t.STAR_LEVEL='5' then STAR_LEVEL else 0 end) sum5 ")
				.append("from star_level_evaluation t where (t.EVALUATE_TYPE='1' or t.EVALUATE_TYPE='3') ");
		String sql = SqlCreator.getSimpleQuerySql(pSet, str.toString(), getDataSource());
		sql += " group by t.service_org_name ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}
	
	public DataSet getBjPjList(ParameterSet pSet) {

		StringBuffer str = new StringBuffer();
		str.append("select t.BSR_NAME,t.PJR_NAME,T.EVALUATE_CONTENT,t.STAR_LEVEL,t.CREATOR_DATE,t.STATE from star_level_evaluation t ");
		str.append("where t.SERVICE_ID='").append((String)pSet.getParameter(("service_id"))).append("' ");
		str.append(" and (t.EVALUATE_TYPE='1' or t.EVALUATE_TYPE='3') ");
		if(!"".equals(pSet.getParameter("star_level").toString())){
			str.append(" and t.star_level=").append(Integer.parseInt(pSet.getParameter("star_level").toString()));
		}
		
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(str.toString(), null);
		} else {
			return this.executeDataset(str.toString(), start, limit, null);
		}
	}
	
	public DataSet getBjAndZnPjPercent(ParameterSet pSet) {

		StringBuffer str = new StringBuffer();
		str.append("select t.service_org_name,")
				.append("count(case when t.STAR_LEVEL='1' and t.EVALUATE_TYPE='1' then id else null end) bjcount1,")
				.append("count(case when t.STAR_LEVEL='2' and t.EVALUATE_TYPE='1' then id else null end) bjcount2,")
				.append("count(case when t.STAR_LEVEL='3' and t.EVALUATE_TYPE='1' then id else null end) bjcount3,")
				.append("count(case when t.STAR_LEVEL='4' and t.EVALUATE_TYPE='1' then id else null end) bjcount4,")
				.append("count(case when t.STAR_LEVEL='5' and t.EVALUATE_TYPE='1' then id else null end) bjcount5,")
				.append("sum(case when t.STAR_LEVEL='1' and t.EVALUATE_TYPE='1' then STAR_LEVEL else 0 end) bjsum1,")
				.append("sum(case when t.STAR_LEVEL='2' and t.EVALUATE_TYPE='1' then STAR_LEVEL else 0 end) bjsum2,")
				.append("sum(case when t.STAR_LEVEL='3' and t.EVALUATE_TYPE='1' then STAR_LEVEL else 0 end) bjsum3,")
				.append("sum(case when t.STAR_LEVEL='4' and t.EVALUATE_TYPE='1' then STAR_LEVEL else 0 end) bjsum4,")
				.append("sum(case when t.STAR_LEVEL='5' and t.EVALUATE_TYPE='1' then STAR_LEVEL else 0 end) bjsum5,")
				.append("count(case when t.STAR_LEVEL='1' and t.EVALUATE_TYPE='3' then id else null end) zncount1,")
				.append("count(case when t.STAR_LEVEL='2' and t.EVALUATE_TYPE='3' then id else null end) zncount2,")
				.append("count(case when t.STAR_LEVEL='3' and t.EVALUATE_TYPE='3' then id else null end) zncount3,")
				.append("count(case when t.STAR_LEVEL='4' and t.EVALUATE_TYPE='3' then id else null end) zncount4,")
				.append("count(case when t.STAR_LEVEL='5' and t.EVALUATE_TYPE='3' then id else null end) zncount5,")
				.append("sum(case when t.STAR_LEVEL='1' and t.EVALUATE_TYPE='3' then STAR_LEVEL else 0 end) znsum1,")
				.append("sum(case when t.STAR_LEVEL='2' and t.EVALUATE_TYPE='3' then STAR_LEVEL else 0 end) znsum2,")
				.append("sum(case when t.STAR_LEVEL='3' and t.EVALUATE_TYPE='3' then STAR_LEVEL else 0 end) znsum3,")
				.append("sum(case when t.STAR_LEVEL='4' and t.EVALUATE_TYPE='3' then STAR_LEVEL else 0 end) znsum4,")
				.append("sum(case when t.STAR_LEVEL='5' and t.EVALUATE_TYPE='3' then STAR_LEVEL else 0 end) znsum5 ")
				.append("from star_level_evaluation t ");
		String sql = SqlCreator.getSimpleQuerySql(pSet, str.toString(), getDataSource());
		sql += " group by t.service_org_name ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}
}
