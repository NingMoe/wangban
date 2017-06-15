package km.app.icity.col;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;

public class WidgetCmd extends BaseQueryCommand{ 
	private final static String CACHE_KEY_FLAG = "WidgetCmd";		

	/**
	 * 获取主题分类列表
	 * @param pSet
	 * @return
	 */
	public DataSet getcatalogList(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getcatalogList" + webRegion+(String)pSet.get("TYPE");
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds = WidgetDao.getInstance().getPersonalBusinessList(pSet);
					if (ds != null && ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		return ds;
	}
	
	/**
	 * 获取分类列表根据主题
	 * @param pSet
	 * @return
	 */
	public DataSet getCatalogListByTheme(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getCatalogListByTheme" + webRegion+(String)pSet.get("TYPE");
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds = WidgetDao.getInstance().getCatalogListByTheme(pSet);
					if (ds != null && ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		return ds;
	}
}