package lianyungang.app.icity.project;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class ArchivesDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(ArchivesDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String sxglDataSource = "sxglDataSource";
	protected static String formDataSource = "formDataSource";

	public static ArchivesDao getInstance() {
		return DaoFactory.getDao(ArchivesDao.class.getName());
	}

	public ArchivesDao() {
		this.setDataSourceName(icityDataSource);
	}
	
	
	
    /*
     * 前言简介type=1
     * 单位档案type=2
     * 班子组成type=3
     * 机构组成type=4
     */
	public DataSet getIndexIntroduction(ParameterSet pSet) {
		String type = pSet.getParameter("type").toString();
		String sql = "select id, content, creater_id, creater_name, update_time from hr_introduction where type='"+type+"' ";
		return this.executeDataset(sql);
	}
	
	
	
}