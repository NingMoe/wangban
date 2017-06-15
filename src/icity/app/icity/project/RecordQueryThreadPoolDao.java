/**  
 * @Title: RecordQueryThreadPoolDao.java 
 * @Package app.icity.project 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-10-29 下午6:58:02 
 * @version V1.0  
 */
package app.icity.project;

import java.util.Calendar;
import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

/**
 * @ClassName: RecordQueryThreadPoolDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-10-29 下午6:58:02
 */

public class RecordQueryThreadPoolDao extends BaseJdbcDao {

	private RecordQueryThreadPoolDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static RecordQueryThreadPoolDao getInstance() {
		return DaoFactory.getDao(RecordQueryThreadPoolDao.class.getName());
	}

	public DataSet getList(ParameterSet pSet) {
		String sql = "SELECT CXBH,XZQH,CXLYDM,YWSBLSH,BCXZZJGDM,SQR,CXSJ,YWBLJG,BLJG,BZ,BYZD FROM EX_GDBS_WSCX ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null);
		} else {
			ds = this.executeDataset(sql, start, limit, null);
		}
		return ds;
	}

	public DataSet insert(ParameterSet pSet) {
		String sql = "INSERT INTO EX_GDBS_WSCX (CXBH,XZQH,CXLYDM,YWSBLSH,BCXZZJGDM,SQR,CXSJ,YWBLJG,BLJG,BZ,BYZD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		DataSet ds = new DataSet();
		Calendar calendar = Calendar.getInstance();
		String str = String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf((calendar.get(Calendar.MONTH)) + 1)
				+ String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		int i = this.executeUpdate(sql,
				new Object[] { SecurityConfig.getString("WebRegion") + str + "0000" + Tools.getUUID32(),
						// 主键：行政区划+YYYYMMDD+36位唯一流水号
						SecurityConfig.getString("WebRegion"), // 行政区划
						"000000000", // 主厅或分厅查询时填写“000000000”，窗口查询时填写组织机构代码
						(String) pSet.getParameter("YWSBLSH"), // 业务申办流水号
						(String) pSet.getParameter("BCXZZJGDM"), // 被查询部门
						(String) pSet.getParameter("SQR"), // 申请人
						new java.sql.Timestamp(System.currentTimeMillis()), // 查询时间
						(String) pSet.getParameter("YWBLJG"), // 有无反馈办理结果：0有，1无
						(String) pSet.getParameter("BLJG"), // 反馈办理结果信息
						(String) pSet.getParameter("BZ"), // 备注信息
						(String) pSet.getParameter("BYZD") // 备用字段，用户扩展使用
		});
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;

	}

	public DataSet update(ParameterSet pSet) {
		String sql = "UPDATE EX_GDBS_WSCX SET  ,XZQH = ? ,CXLYDM = ? ,YWSBLSH = ? ,BCXZZJGDM = ? ,SQR = ?,CXSJ = ?,YWBLJG = ?,"
				+ "BLJG=?,BZ=?,BYZD=?  WHERE CXBH = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { SecurityConfig.getString("WebRegion"), // 行政区划
						"000000000", // 主厅或分厅查询时填写“000000000”，窗口查询时填写组织机构代码
						(String) pSet.getParameter("YWSBLSH"), // 业务申办流水号
						(String) pSet.getParameter("BCXZZJGDM"), // 被查询部门
						(String) pSet.getParameter("SQR"), // 申请人
						new java.sql.Timestamp(System.currentTimeMillis()), // 查询时间
						(String) pSet.getParameter("YWBLJG"), // 有无反馈办理结果：0有，1无
						(String) pSet.getParameter("BLJG"), // 反馈办理结果信息
						(String) pSet.getParameter("BZ"), // 备注信息
						(String) pSet.getParameter("BYZD"), // 备用字段，用户扩展使用
						(String) pSet.getParameter("CXBH") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	public DataSet delete(ParameterSet pSet) {
		String ids = (String) pSet.getParameter("ids");
		DataSet ds = new DataSet();
		if (StringUtils.isNotEmpty(ids)) {
			String sql = "UPDATE EX_GDBS_WSCX SET STATUS=0";
			ParameterSet tSet = new ParameterSet();
			tSet.setParameter("ID@in", ids);
			sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
			int i = this.executeUpdate(sql);
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			}
		} else {
			ds.setState(StateType.FAILT);
			ds.setMessage("参数ids的值为空！");
		}
		return ds;
	}

}
