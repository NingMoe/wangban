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
	
	public DataSet getPersonInfo(ParameterSet pSet) {
		String sql = "select id, user_code, name, sex, to_char(birthday,'yyyy-MM-dd') as birthday, nation, origin_address, born_address, card_no, health, party, to_char(party_date,'yyyy-MM-dd') as party_date, "
				+ "family_address, fixed_phone, mobile_phone, email, to_char(jion_work_date,'yyyy-MM-dd') as jion_work_date, to_char(jion_dept_date,'yyyy-MM-dd') as jion_dept_date, family_major, specialty, technical_position, "
				+ "now_post, to_char(post_date,'yyyy-MM-dd') as post_date, full_time_school, full_time_major, to_char(full_time_entrance_date,'yyyy-MM-dd') as full_time_entrance_date, to_char(full_time_graduation_date,'yyyy-MM-dd') as full_time_graduation_date, full_time_education, "
				+ "full_time_degree, job_school, job_major, job_entrance_date, job_graduation_date, job_education, job_degree, personnel_identity, "
				+ "personnel_status, work_dept, office_location, post_type, photo, anniversary_book, org_code, rank, to_char(rank_date,'yyyy-MM-dd hh:mi:ss') as rank_date, to_char(create_time,'yyyy-MM-dd hh:mi:ss') as create_time, "
				+ "to_char(update_time,'yyyy-MM-dd hh:mi:ss') as update_time from hr_person order by org_code ";
		return this.executeDataset(sql);
	}
	
	public DataSet getPersonInfoById(ParameterSet pSet) {
		String sql = "select id, user_code, name, sex, to_char(birthday,'yyyy-MM-dd') as birthday, nation, origin_address, born_address, card_no, health, party, to_char(party_date,'yyyy-MM-dd') as party_date, "
				+ "family_address, fixed_phone, mobile_phone, email, to_char(jion_work_date,'yyyy-MM-dd') as jion_work_date, to_char(jion_dept_date,'yyyy-MM-dd') as jion_dept_date, family_major, specialty, technical_position, "
				+ "now_post, to_char(post_date,'yyyy-MM-dd') as post_date, full_time_school, full_time_major, to_char(full_time_entrance_date,'yyyy-MM-dd') as full_time_entrance_date, to_char(full_time_graduation_date,'yyyy-MM-dd') as full_time_graduation_date, full_time_education, "
				+ "full_time_degree, job_school, job_major, job_entrance_date, job_graduation_date, job_education, job_degree, personnel_identity, "
				+ "personnel_status, work_dept, office_location, post_type, photo, anniversary_book, org_code, rank, to_char(rank_date,'yyyy-MM-dd hh:mi:ss') as rank_date, to_char(create_time,'yyyy-MM-dd hh:mi:ss') as create_time, "
				+ "to_char(update_time,'yyyy-MM-dd hh:mi:ss') as update_time from hr_person where id='"+pSet.getParameter("ID")+"' order by org_code ";
		return this.executeDataset(sql);
	}
	
	public DataSet getPersonGzxsById(ParameterSet pSet) {
		int start = (int) pSet.getParameter("start");
		int limit = (int) pSet.getParameter("limit");
		String sql = "select id, l_id, status, to_char(title,'yyyy-mm-dd') as title, content, to_char(creat_time,'yyyy-mm-dd hh:mi:ss') as creat_time from hr_xs where l_id='"+pSet.getParameter("ID")+"' and status='1' order by title desc ";
		return this.executeDataset(sql, start, limit, null, this.getDataSourceName());
	}
	
	public DataSet getPersonGzrwById(ParameterSet pSet) {
		int start = (int) pSet.getParameter("start");
		int limit = (int) pSet.getParameter("limit");
		String sql = "select title, degree, task_status, id, l_id, status, content, create_time from hr_rw where status='1' and l_id='"+pSet.getParameter("ID")+"' order by create_time desc ";
		return this.executeDataset(sql, start, limit, null, this.getDataSourceName());
	}
	
	public DataSet getPersonGzyjYearById(ParameterSet pSet) {
		String sql = "select title from HR_YJ where l_id='"+pSet.getParameter("ID")+"' and status='1' group by title order by title desc";
		return this.executeDataset(sql);
	}
	
	
	
}