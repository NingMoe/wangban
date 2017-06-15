package app.icity.search.engine;

import org.apache.commons.lang.StringUtils;

import app.icity.search.engine.impl.SearchEngineImpl;

import com.inspur.util.DaoFactory;
import com.inspur.util.SecurityConfig;

public class SearchEngineFactory {
	private static SearchEngine _instance = null;

	public static SearchEngine getInstance() {
		if (_instance == null) {
			synchronized (SearchEngineFactory.class) {
				String searchClass = SecurityConfig.getString("SearchClass");
				if (StringUtils.isNotEmpty(searchClass)) {
					_instance=(SearchEngine)DaoFactory.getDao(searchClass);
				} else {
					_instance = new SearchEngineImpl();
				}
			}
		}
		return _instance;
	}
}
