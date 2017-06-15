package km.app.icity.col;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class WidgetDao extends BaseJdbcDao {

	protected WidgetDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static WidgetDao getInstance() {
		return DaoFactory.getDao(WidgetDao.class.getName());
	}

	public DataSet getPersonalBusinessList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String type = (String) pSet.getParameter("TYPE");
		try {
			String sql = "select t.id,t.code,t.name,t.parent_id,t.is_required,t.icon_url,t.sort_order,t.class_type,t.status,level from power_catalog t where t.class_type =? start with t.id in (select id from power_catalog where (parent_id ='null' or parent_id is null) and class_type =? ) connect by prior t.id = t.parent_id order siblings by t.sort_order";
			ds = this.executeDataset(sql, new Object[] { type,type });
		} catch (Exception e) {
			e.printStackTrace();
			ds.setMessage("数据库查询失败");
		}
		return ds;
	}
	
	public DataSet getCatalogListByTheme(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String type = (String) pSet.getParameter("TYPE");
		String sort_order = (String) pSet.getParameter("SORT_ORDER");
		try {
			String sql = "select t.id,t.code,t.name,t.parent_id,t.is_required,t.icon_url,t.sort_order,t.class_type,t.status,level from power_catalog t where t.class_type =? start with t.id in (select id from power_catalog where parent_id ='null' and sort_order=? and class_type =? ) connect by prior t.id = t.parent_id order siblings by t.sort_order";
			ds = this.executeDataset(sql, new Object[] { type,sort_order,type });
		} catch (Exception e) {
			e.printStackTrace();
			ds.setMessage("数据库查询失败");
		}
		return ds;
	}
}
