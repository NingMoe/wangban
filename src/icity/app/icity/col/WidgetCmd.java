package app.icity.col;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;

public class WidgetCmd extends BaseQueryCommand{ 
	/**
	 * 获取个人主题分类
	 * @param pSet
	 * @return
	 */
	public DataSet getPersonalBusinessList(ParameterSet pSet){
		return WidgetDao.getInstance().getPersonalBusinessList(pSet);
	}
	public DataSet getAllThemeList(ParameterSet pSet){
		return WidgetDao.getInstance().getAllThemeList(pSet);
	}
	public DataSet getAllList(ParameterSet pSet){
		return WidgetDao.getInstance().getAllList(pSet);
	}	
	public DataSet setIcom(ParameterSet pSet){
		return WidgetDao.getInstance().setIcom(pSet);
	}
	public DataSet setIcomKm(ParameterSet pSet){
		return WidgetDao.getInstance().setIcomKm(pSet);
	}
	public DataSet getAllFileName(ParameterSet pSet)
    {
		return WidgetDao.getInstance().getAllFileName(pSet);
    }
	public DataSet getCatalogType(ParameterSet pSet){
		return WidgetDao.getInstance().getCatalogType(pSet);		
	}
	public DataSet getSubcatalog(ParameterSet pSet){
		return WidgetDao.getInstance().getSubcatalog(pSet);		
	}
	/**
	 * 分类获取主题 漳州
	 * @param pSet
	 * @return
	 */
	public DataSet getPersonalBusinessListZhangzhou(ParameterSet pSet){
		return WidgetDao.getInstance().getPersonalBusinessListZhangzhou(pSet);
	}
}