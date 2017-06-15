package action.onlineapply;

import java.sql.Connection;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.DaoFactory;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.HttpClientUtil;

public class applyinfo extends BaseAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactory.getLog(applyinfo.class);
	@Override
	public boolean handler(Map<String, Object> data) throws Exception {
		BaseAction ba = (BaseAction)DaoFactory.getDao("action.icity.submitsp.baseinfo");
		if(ba != null){
			ba.setPageContext(this.getPageContext());
			if(!ba.init(this.getHttpAdapter()) || !ba.handler(data)){
				return false;
			}
		}	
		return true;
	}
}
