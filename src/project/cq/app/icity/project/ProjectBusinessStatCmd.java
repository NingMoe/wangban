/**  
 * @Title: ProjectBusinessStatCmd.java 
 * @Package icity.project 
 * @Description: 事项统计及办件查询 
 * @date 2013-8-20 上午11:59:28 
 * @version V1.0  
 */
package cq.app.icity.project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.sf.json.JSONObject;
import cq.app.icity.project.ProjectBusinessStatDao;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

/**
 * @ClassName: ProjectBusinessStat
 * @Description: 事项统计及办件查询
 * @date 2013-8-20 上午11:59:28
 */

public class ProjectBusinessStatCmd extends app.icity.project.ProjectBusinessStatCmd {

	
	/**
	 * @Description: 重庆办件公告列表
	 * @return DataSet 返回类型
	 * @since 2015-9-2
	 */
	public DataSet getCqBusinessList(ParameterSet pSet) {
		return ProjectBusinessStatDao.getInstance().getCqBusinessList(pSet);
	}
}
