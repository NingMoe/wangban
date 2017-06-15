package zbs.app.icity.project;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import zbs.app.icity.project.ProjectIndexDao;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

public class ProjectIndexDao extends BaseJdbcDao {
	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao) DaoFactory.getDao(ProjectIndexDao.class.getName());
	}

	private String ipfDataSource;
	private String icityDataSource;

	public DataSet getInfo_market(ParameterSet pSet) {
		DataSet ds;
		String channel_bszn = (String) pSet.remove("channel_bszn");// 办事指南
		String channel_bslc = (String) pSet.remove("channel_bslc");// 办事流程
		String channel_sfbz = (String) pSet.remove("channel_sfbz");// 收费标准

		String channel_sqcl = (String) pSet.remove("channel_sqcl");// 申请材料
		//String channel_spsx = (String) pSet.remove("channel_spsx");// 审批事项
		String channel_bgxz = (String) pSet.remove("channel_bgxz");// 表格下载
		// String ssb_in = "('" + channel_sqcl + "','" + channel_spsx + "','" +
		// channel_bgxz + "')";
		String ssb_in = "('" + channel_sqcl + "','" + channel_bgxz + "')";

		String sort = (String) pSet.remove("sort"); // 事项排序条件
		//String webRank = SecurityConfig.getString("WebRank"); // 默认区划级别
		String webRegion = SecurityConfig.getString("WebRegion"); // 默认区划代码
		StringBuffer sqlSb2 = new StringBuffer(), sqlSb3 = new StringBuffer(), sqlSb4 = new StringBuffer(),
				sqlSb5 = new StringBuffer();

		ParameterSet ps = new ParameterSet();
		ps.setParameter("status", "1");
		// ps.setParameter("submit_status", "1");
		pSet.setParameter("checks", "1");
		ps.setParameter("rid", webRegion);
		String sql_bszn = "select  t.reason,t.source,t.dept_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.ctime,"
				+ "t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,t.VALID_DATE_START,t.VALID_DATE_END,"
				+ "t.submit_status from PUB_CONTENT t";

		String sql_bslc = "select  t.reason,t.attach,t.source,t.dept_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.ctime,"
				+ "t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,t.VALID_DATE_START,t.VALID_DATE_END,"
				+ "t.submit_status from PUB_CONTENT t";

		String sql_sfbz = "select  t.reason,t.source,t.dept_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.ctime,"
				+ "t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,t.VALID_DATE_START,t.VALID_DATE_END,"
				+ "t.submit_status from PUB_CONTENT t";

		sqlSb2.append(SqlCreator.getSimpleQuerySql(ps, sql_bszn, this.getDataSource()));// 查询相关政策，取列表数据

		sqlSb3.append(SqlCreator.getSimpleQuerySql(ps, sql_bslc, this.getDataSource()));

		sqlSb4.append(SqlCreator.getSimpleQuerySql(ps, sql_sfbz, this.getDataSource()));

		sqlSb5.append("select  t.reason,t.source,t.dept_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.ctime,"
				+ "t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,t.VALID_DATE_START,t.VALID_DATE_END,"
				+ "t.submit_status from PUB_CONTENT t where t.rid='" + webRegion + "' and status = '1' and t.cid in "
				+ ssb_in);

		if (channel_bszn != null && channel_bszn.length() > 0) {
			sqlSb2.append("and t.cid = '").append(channel_bszn).append("'");
		}

		if (channel_bslc != null && channel_bslc.length() > 0) {
			sqlSb3.append("and t.cid = '").append(channel_bslc).append("'");
		}

		if (channel_sfbz != null && channel_sfbz.length() > 0) {
			sqlSb4.append("and t.cid = '").append(channel_sfbz).append("'");
		}

		if (StringUtils.isNotEmpty(sort)) {
			sqlSb2.append(" order by ").append(sort);
			sqlSb3.append(" order by ").append(sort);
			sqlSb4.append(" order by ").append(sort);
			sqlSb5.append(" order by ").append(sort);
		}
		JSONArray pArray = new JSONArray();

		ds = this.executeDataset(sqlSb2.toString(), null, this.ipfDataSource);
		if (ds != null) {
			pArray.add(ds.getRecord(0));
		}
		DataSet ds1 = this.executeDataset(sqlSb3.toString(), null, this.ipfDataSource);

		DataSet ds2 = this.executeDataset(sqlSb4.toString(), null, this.ipfDataSource);

		DataSet ds3 = this.executeDataset(sqlSb5.toString(), null, this.ipfDataSource);

		String topcid = (String) pSet.get("topcid");
		String sqlSb6 = "select  'spsxscztslsp' CID,t.code,t.name,t.dept_name CONTENT,(select orderby from power_base_info_topic s  where topicid = '"
				+ topcid
				+ "' and s.code = t.code) orderby from power_base_info t where t.code in (select code from power_base_info_topic where topicid='"
				+ topcid + "' ) order by orderby desc";
		DataSet ds4 = this.executeDataset(sqlSb6, null, this.icityDataSource);

		if (ds1 != null) {
			pArray.add(ds1.getRecord(0));
		}
		if (ds2 != null) {
			pArray.add(ds2.getRecord(0));
		}
		if (ds3 != null) {
			pArray.addAll(ds3.getJAData());
		}
		if (ds4 != null) {
			pArray.addAll(ds4.getJAData());
		}
		if (ds != null) {
			ds.setData(Tools.stringToBytes(pArray.toString()));
		}

		return ds;
	}
}
