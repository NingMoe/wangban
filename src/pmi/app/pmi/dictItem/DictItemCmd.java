package app.pmi.dictItem;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;

public class DictItemCmd extends BaseQueryCommand {
	public DataSet getList(ParameterSet pset){
		return DictItemDao.getInstance().getList(pset);
	}
	
	/**
	 * 查询字典项
	 * @return
	 */
	public DataSet getDictItemList(ParameterSet pset){
		String key = (String)pset.getParameter("ITEM.DIC_CODE");
		DataSet ds = (DataSet)CacheManager.get("icity_permission",key);
		if (ds == null) {
			ds = DictItemDao.getInstance().getList(pset);
			CacheManager.set("icity_permission", key,ds);
		}
		return ds;
	}
	
	public DataSet insert(ParameterSet pset){
		return DictItemDao.getInstance().insert(pset);
	} 
	public DataSet update(ParameterSet pset){
		return DictItemDao.getInstance().update(pset);
	} 
	
	public DataSet delete(ParameterSet pset){
		return DictItemDao.getInstance().delete(pset);
	}
}
