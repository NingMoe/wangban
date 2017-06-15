package shenyang_hepingqu.app.icity;

import io.netty.util.CharsetUtil;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class ServiceCmd extends BaseQueryCommand {
	private final static String CACHE_KEY_FLAG = "ServiceCmd";
	/**
	 * 设置语言
	 * @param pset
	 * @return
	 */
	public DataSet setLanguage(ParameterSet pset) {
		DataSet ds = new DataSet();
		String flag_language = (String)pset.get("language");
		String webRegion = SecurityConfig.getString("WebRegion");		
		String key = "setLanguage" + webRegion;
		String language = (String)CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
		if (!"".equals(flag_language)&&!flag_language.equals(language)) {
			synchronized (key.intern()) {
				language = (String)CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (!flag_language.equals(language)) {
					CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, flag_language);				
				}
			}
		}else{
			if(!"".equals(language)&&StringUtil.isNotEmpty(language)){
				flag_language = language;
			}else{
				flag_language = "CH";
			}			
		}	
		ds.setRawData(flag_language);
		return ds;
	}
}