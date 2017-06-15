package api;

import api.impl.CformImpl;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;

/**
 * 表单相关接口
 * 非表单接口不要添加到这个类
 * @author lihongyun
 */
@RestType(name = "api.cform", descript = "表单接口")
public class cform  extends BaseQueryCommand  {
	
	/**
	 * 转存表单数据
	 * @param pSet
	 * @return
	 */
	public DataSet interflowFormData(ParameterSet pSet) {
		return CformImpl.getInstance().interflowFormData(pSet);
	}
	/**
	 * 表单
	 * @param pSet
	 * @return
	 */
	public DataSet cformOper(ParameterSet pSet){
		return CformImpl.getInstance().cformOper(pSet);
	}
}
