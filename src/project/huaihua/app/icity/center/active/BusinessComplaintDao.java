/**  
 * @Title: BusinessComplainDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 下午2:21:11 
 * @version V1.0  
 */
package huaihua.app.icity.center.active;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;

/**
 * @ClassName: BusinessComplainDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-5-31 下午2:21:11
 */

public class BusinessComplaintDao extends BaseJdbcDao {

	protected BusinessComplaintDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static BusinessComplaintDao getInstance() {
		return DaoFactory.getDao(BusinessComplaintDao.class.getName());
	}

	public DataSet getBusinessComplaintList(ParameterSet pSet) {
		String searchCenter = (String) pSet.remove("SEARCH_CENTER");
		String sql = "SELECT G.*,to_char(G.Write_Date,'yyyy-MM-dd HH24:mi:ss') Write_Date_Char," +
		"to_char(G.Deal_Date,'yyyy-MM-dd HH24:mi:ss') Deal_Date_Char," +
		"to_char(G.Commitmenttime,'yyyy-MM-dd HH24:mi:ss') Commitmenttime_Char," +
		"to_char(G.Accepttime,'yyyy-MM-dd HH24:mi:ss') Accepttime_Char," +
		"to_char(G.Accepttimeout,'yyyy-MM-dd HH24:mi:ss') Accepttimeout_Char " +
		" FROM GUESTBOOK G where 1=1 and G.open='1' ";	
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		if (StringUtil.isNotEmpty(searchCenter)) {
			sql += " AND (G.ID ='" + searchCenter + "' OR G.CONTENT like '%" + searchCenter + "%') ";
		}
		sql += " ORDER BY G.WRITE_DATE DESC";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}

	}

	public DataSet delete(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "delete from guestbook";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("id@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
				int i = this.executeUpdate(sql);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("请选择您需要删除的内容！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusinessComplaint(ParameterSet pSet) {
		String sql = "select g.id,g.title,g.content,g.open,g.write_date,g.depart_name,g.deal_result,g.deal_date,g.checks,g.status,b.sbxmmc,g.complain_type from guestbook g left join BUSINESS_INDEX b  on g.busi_id=b.sblsh where  g.type='3'";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	public DataSet update(ParameterSet pSet) {
		String sql = "update guestbook set open=?,title=?,content=? where id=?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql,
				new Object[] { (String) pSet.getParameter("OPEN"), (String) pSet.getParameter("TITLE"),
						(String) pSet.getParameter("CONTENT"), (String) pSet.getParameter("ID") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据更新失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

}
