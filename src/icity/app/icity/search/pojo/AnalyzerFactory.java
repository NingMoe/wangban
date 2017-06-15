package app.icity.search.pojo;

import org.apache.commons.lang.StringUtils;

import app.icity.search.pojo.impl.IKSearchAnalyzer;

import com.inspur.util.DaoFactory;
import com.inspur.util.SecurityConfig;

public class AnalyzerFactory {
	private static SearchAnalyzer _instance = null;

	public static SearchAnalyzer getInstance() {
		if (_instance == null) {
			synchronized (AnalyzerFactory.class) {
				String analyzerClass = SecurityConfig.getString("SearchAnalyzer");
				if (StringUtils.isNotEmpty(analyzerClass)) {
					_instance = (SearchAnalyzer) DaoFactory.getDao(analyzerClass);
				} else {
					_instance = new IKSearchAnalyzer();
				}
			}
		}
		return _instance;
	}
}
