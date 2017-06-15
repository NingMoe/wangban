/**  
 * @Title: ProjectBusinessStatDao.java 
 * @Package icity.project.dao 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-8-20 下午12:33:03 
 * @version V1.0  
 */
package cq.app.icity.project;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import core.util.HttpClientUtil;

/**
 * @ClassName: ProjectBusinessStatDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-8-20 下午12:33:03
 */

public class ProjectBusinessStatDao extends BaseJdbcDao {

	private ProjectBusinessStatDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ProjectBusinessStatDao getInstance() {
		return DaoFactory.getDao(ProjectBusinessStatDao.class.getName());
	}

	// 重庆办件公告：只取审批办件
	public DataSet getCqBusinessList(ParameterSet pSet) {
		DataSet ds = new DataSet();

		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getDisplayList?count=5000&region="
				+ SecurityConfig.getString("WebRegion"));
		HttpClientUtil client = new HttpClientUtil();
		// String
		// ss="{\"error\":\"\",\"state\":\"200\",\"data\":[{\"acceptTime\":\"2015-07-28\",\"applySubject\":\"1头天你是什么人的问题\",\"limitTime\":\"2015-09-09\",\"orgName\":\"区农林局\",\"recerveNumber\":\"33090923424352435\",\"state\":\"办结\"},{\"acceptTime\":\"2015-07-28\",\"applySubject\":\"2头天你是什么人的问题\",\"limitTime\":\"2015-09-09\",\"orgName\":\"区农林局\",\"recerveNumber\":\"33090923424352435\",\"state\":\"办结\"},{\"acceptTime\":\"2015-07-28\",\"applySubject\":\"3头天你是什么人的问题\",\"limitTime\":\"2015-09-09\",\"orgName\":\"区农林局\",\"recerveNumber\":\"33090923424352435\",\"state\":\"办结\"},{\"acceptTime\":\"2015-07-28\",\"applySubject\":\"头天你是什么人的问题\",\"limitTime\":\"2015-09-09\",\"orgName\":\"区农林局\",\"recerveNumber\":\"33090923424352435\",\"state\":\"办结\"}]}";
		JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
		// JSONObject json=JSONObject.fromObject(ss);
		JSONArray data = json.getJSONArray("data");
		ds.setRawData(data);

		return ds;
	}
}
