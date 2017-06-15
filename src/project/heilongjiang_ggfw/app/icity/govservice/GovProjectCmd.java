package heilongjiang_ggfw.app.icity.govservice;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;

public class GovProjectCmd extends BaseQueryCommand{ 	
	/**
	 * 获取事项
	 * @param pSet
	 * @return
	 */	
	public DataSet getMattersList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getMattersList(pSet);
	}
}