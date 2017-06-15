package app.icity.col;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import com.icore.StateType;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;

public class WidgetDao extends BaseJdbcDao {
	protected WidgetDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static WidgetDao getInstance() {
		return DaoFactory.getDao(WidgetDao.class.getName());
	}

	public DataSet getCatalogType(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String type = (String) pSet.getParameter("TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String cxId = (String) pSet.getParameter("AppId");
			String append = "";
			if (type.equals("gr") || type.equals("person")) {
				type = "个人";
			} else if (type.equals("fr") || type.equals("ent")) {
				type = "法人";
			} else if (type.equals("bmfw")) {
				type = "便民服务";
				append = " and (parent_id is null or parent_id='null')";
			} else {
				type = "";
			}
			if (!"".equals(cxId) && cxId != null && cxId.equals("ytsp")) {
				append = "order by SORT_ORDER asc";
			}
			String sql = "select * from power_catalog where region_code=? and class_type =? and code in(select distinct(parent_id) from power_catalog where parent_id is not null) order by name desc"
					+ append;
			ds = this.executeDataset(sql, new Object[] { region_code, type });
		} catch (Exception e) {
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	public DataSet getPersonalBusinessList(ParameterSet pSet) {
		DataSet ds;
		String type = (String) pSet.getParameter("TYPE");
		String region_code = (String) pSet.getParameter("region_code");
		String cxId = (String) pSet.getParameter("AppId");
		String append = "";
		if (type.equals("gr") || type.equals("person")) {
			type = "个人";
		} else if (type.equals("fr") || type.equals("ent")) {
			type = "法人";
		} else if (type.equals("bmfw")) {
			type = "便民服务";
			append = " and (parent_id is null or parent_id='null')";
		} else {
			type = "";
		}
		if (!"".equals(cxId) && cxId != null
				&& ("ytsp".equals(cxId) || "yt".equals(cxId))) {
			append = " order by SORT_ORDER asc";
		}
		String sql = "";
		if (StringUtils.isNotEmpty(region_code)) {
			sql = "SELECT * FROM power_catalog where is_use='1' and class_type = '"
					+ type
					+ "'"
					+ " and region_code='"
					+ region_code
					+ "'"
					+ append;
		} else {
			sql = "SELECT * FROM power_catalog where is_use='1' and class_type = '"
					+ type + "'" + append;
		}

		ds = this.executeDataset(sql, null);
		return ds;
	}

	public DataSet getAllThemeList(ParameterSet pSet) {
		DataSet ds;
		String ctype = "";
		String type = (String) pSet.getParameter("TYPE");
		if (type.equals("gr") || type.equals("person")) {
			type = "个人";
		} else if (type.equals("fr") || type.equals("ent")) {
			type = "法人";
		} else {
			type = "";
		}
		String sql = "";
		if (StringUtils.isNotEmpty(type)) {
			sql = "SELECT * FROM power_catalog  where is_use='1' and class_type = '" + type
					+ "' order by class_type desc";
		} else {
			type = "法人";
			ctype = "个人";
			sql = "SELECT * FROM power_catalog where is_use='1' and class_type = '" + type
					+ "' or class_type = '" + ctype
					+ "' order by class_type desc";
		}
		ds = this.executeDataset(sql, null);
		return ds;
	}

	public DataSet getSubcatalog(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String parent_id = (String) pSet.getParameter("parent_id");
			String sql = "SELECT * FROM power_catalog where parent_id = ?";
			ds = this.executeDataset(sql, new Object[] { parent_id });
		} catch (Exception e) {
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	public DataSet getAllList(ParameterSet pSet) {
		DataSet ds;
		String sql = "SELECT * FROM power_catalog where 1=1";
		ds = this.executeDataset(sql, null);
		return ds;
	}

	public DataSet setIcom(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String code = (String) pSet.getParameter("CODE");
		String val = (String) pSet.getParameter("VAL");
		String suffix = (String) pSet.getParameter("suffix");

		String sql = "update power_catalog set  icon_url = 'zwfwwtb_" + val
				+ "." + suffix + "'  where code = '" + code + "'";
		this.executeUpdate(sql);
		return ds;
	}

	public DataSet getAllFileName(ParameterSet pSet) {
		int count = (Integer) pSet.get("count");
		ArrayList<String[]> names = new ArrayList<String[]>();
		String propath = PathUtil.getWebPath();
		for (int i = 0; i < count; i++) {
			String path = propath + "public" + File.separator + "assets"
					+ File.separator + "icon" + File.separator + "icon-"+(i+1)
					+ File.separator;
			File file = new File(path);
			File[] files = file.listFiles();
			String[] m_names = file.list();
			names.add(m_names);
		}
		/*String path1 = propath + "public" + File.separator + "assets"
				+ File.separator + "icon" + File.separator + "icon-1"
				+ File.separator;
		File file1 = new File(path1);
		File[] files1 = file1.listFiles();
		String[] names1 = file1.list();

		String path2 = propath + "public" + File.separator + "assets"
				+ File.separator + "icon" + File.separator + "icon-2"
				+ File.separator;
		File file2 = new File(path2);
		File[] files2 = file2.listFiles();
		String[] names2 = file2.list();
*/
		DataSet ds = new DataSet();
		ds.setRawData(names);
		ds.setState(StateType.SUCCESS);
		return ds;
	}

	public DataSet setIcomKm(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String code = (String) pSet.getParameter("CODE");
		String val = (String) pSet.getParameter("VAL");

		String sql = "update power_catalog set  icon_url = '" + val
				+ "'  where code = '" + code + "'";
		this.executeUpdate(sql);
		return ds;
	}
	/**
	 * 分类获取主题 漳州
	 * @param pSet
	 * @return
	 */
	public DataSet getPersonalBusinessListZhangzhou(ParameterSet pSet) {
		DataSet ds;
		String type = (String) pSet.getParameter("TYPE");//个人  法人 
		String themType = (String) pSet.getParameter("themType");//主题类型
		String region_code = (String) pSet.getParameter("region_code");
		if (type.equals("gr") || type.equals("person")) {
			type = "个人";
		} else if (type.equals("fr") || type.equals("ent")) {
			type = "法人";
		} else {
			type = "";
		}
		String pSql = "select id from power_catalog where (parent_id ='0' or parent_id='null') and class_type ='"+type+"' and name='"+themType+"'";
		String sql = "";
		if (StringUtils.isNotEmpty(region_code)) {
			sql = "SELECT * FROM power_catalog where is_use='1' and class_type = '"
					+ type
					+ "'"
					+ " and region_code='"
					+ region_code
					+ "'"
					+" and parent_id = ("+pSql+") order by sort_order";
		}else{
			sql = "SELECT * FROM power_catalog where is_use='1' and class_type = '"
					+ type
					+ "'"
					+" and parent_id = ("+pSql+") order by sort_order";
		}
		ds = this.executeDataset(sql, null);
		return ds;
	}
}