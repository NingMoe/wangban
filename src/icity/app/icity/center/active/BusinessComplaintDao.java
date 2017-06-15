/**  
 * @Title: BusinessComplainDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 下午2:21:11 
 * @version V1.0  
 */
package app.icity.center.active;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.icore.util.conf.DataSourceConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

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
		DataSet ds = new DataSet();
		try {
			String searchCenter = (String) pSet.remove("SEARCH_CENTER");
			String sql = "SELECT G.* FROM GUESTBOOK G where 1=1";
			String date_s = (String) pSet.remove("g.write_date@>@Date");
			String date_e = (String) pSet.remove("g.write_date@<@Date");
			List<Object> param=new ArrayList<Object>();
			Timestamp d =null;
			if(StringUtil.isNotEmpty(date_s)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(date_s,
						CommonUtils_api.YYYY_MM_DD);
				param.add(d);
				sql +=" AND g.WRITE_DATE > ?";
			}
			if(StringUtil.isNotEmpty(date_e)){
				d = CommonUtils_api.getInstance().parseStringToTimestamp(date_e,
						CommonUtils_api.YYYY_MM_DD);
				param.add(d);
				sql +=" AND g.WRITE_DATE < ?";
			}
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			if (StringUtil.isNotEmpty(searchCenter)) {
				sql += " AND (G.ID ='" + searchCenter
						+ "' OR G.CONTENT like '%" + searchCenter + "%') ";
			}
			sql += " ORDER BY G.WRITE_DATE DESC";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql, param.toArray());
			} else {
				ds = this.executeDataset(sql, start, limit, param.toArray());
			}
			return ds;
		} catch (Exception e) {
			e.printStackTrace();
			return ds;
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
				sql = SqlCreator.getSimpleQuerySql(tSet, sql,
						this.getDataSource());
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
		String sql = "select g.replayer,g.id,g.title,g.content,g.open,g.write_date,g.depart_name,g.deal_result,g.deal_date,g.checks,g.status,b.sbxmmc,g.complain_type from guestbook g left join BUSINESS_INDEX b  on g.busi_id=b.sblsh where  g.type='3'";
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
		int i = this.executeUpdate(
				sql,
				new Object[] { (String) pSet.getParameter("OPEN"),
						(String) pSet.getParameter("TITLE"),
						(String) pSet.getParameter("CONTENT"),
						(String) pSet.getParameter("ID") });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据更新失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

}
