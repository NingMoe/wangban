package bl.app.icity.govservice;

import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd extends app.icity.govservice.GovProjectCmd{ 
	/**
	 * 获取事项
	 * @param pSet
	 * @return
	 */
	public DataSet getMattersList(ParameterSet pSet){
			//接口调用模式
			return GovProjectDao.getInstance().getMattersList2(pSet);
	}	
	public DataSet getAllItemInfoByItemID(ParameterSet pSet){
		//接口调用模式 根据事项ID分类获取事项相关信息
		return GovProjectDao.getInstance().getAllItemInfoByItemID(pSet);
	}
	
	public DataSet getFolderListByPage(ParameterSet pSet){
		//接口调用模式  获取事项folderlist
		return GovProjectDao.getInstance().getFolderListByPage(pSet);
	}
}