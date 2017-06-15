package action.icity.hall;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import app.icity.project.RecordQueryThreadPool;

import com.icore.core.ThreadPoolManager;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;

/**
 * @ClassName: BusyQueryResults
 * @Description: 获取业务查询结果
 * @author XiongZhiwen
 * @date 2013-10-8 下午4:29:35
 */

public class info extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		Command cmd = new Command("app.icity.project.ProjectQueryCmd");// rest服务
		int queryNum = 0;		
		// 参数分别为申办流水号
		String sblsh = "";
		if ("post".equalsIgnoreCase(this.getMethod())) {
			// 从表单输入
			Map<String, String> map = this.getPostData();
			sblsh = map.get("SBLSH");
			cmd.setParameter("SBLSH@=", sblsh);
		} else {		
			sblsh = this.getParameter("SBLSH");
			cmd.setParameter("SBLSH@=", sblsh);

		}
		DataSet ds = cmd.execute("getProjectInfo");
		JSONObject jo;
		if (ds != null && ds.getTotal() > 0) {
			jo = ds.toJsonObject();
		} else {
			return false;
		}
		// 返回数据
		data.put("result", jo);
		return true;
	}
}
