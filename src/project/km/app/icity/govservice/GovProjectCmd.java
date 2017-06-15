package km.app.icity.govservice;
import km.app.icity.govservice.GovProjectDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectCmd extends BaseQueryCommand{ 
	/**
	 * 获取事项
	 * @param pSet
	 * @return
	 */
	public DataSet getMattersList(ParameterSet pSet){
		//接口调用模式
		return GovProjectDao.getInstance().getMattersList2(pSet);
	}
	
	public DataSet getPartList(ParameterSet pSet){
			//接口调用模式
			return GovProjectDao.getInstance().getMattersList3(pSet);
	}
	
	public DataSet getInitList(ParameterSet pSet){
			//接口调用模式
			return GovProjectDao.getInstance().getPowerList(pSet);
	}
	
	public DataSet getAllItemInfoByItemID(ParameterSet pSet){
		//接口调用模式 根据事项ID分类获取事项相关信息
		return GovProjectDao.getInstance().getAllItemInfoByItemID(pSet);
}
	public DataSet onQueryProgress(ParameterSet pSet){
		return GovProjectDao.getInstance().onQueryProgress(pSet);
	}
	public DataSet getContentInfo(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentInfo(pSet);
	}
	
	
	//舟山市审招委
	public DataSet getList(ParameterSet pSet){
		return GovProjectDao.getInstance().getList(pSet);
	}
	
	//部门id查询通知公告列表
	public DataSet getNticeList(ParameterSet pSet){
		return GovProjectDao.getInstance().getNticeList(pSet);
	}
	
	public DataSet search(ParameterSet pSet){
		return GovProjectDao.getInstance().search(pSet);
	}
	public DataSet getChannel(ParameterSet pSet){
		return GovProjectDao.getInstance().getChannel(pSet);
	}
	public DataSet getContentDetail(ParameterSet pSet){
		return GovProjectDao.getInstance().getContentDetail(pSet);
	}
	
	public DataSet getChannelName(ParameterSet pSet){
		return GovProjectDao.getInstance().getChannelName(pSet);
	}
	
	//分页获取事项目录
	public DataSet getFolderInfoByPage(ParameterSet pSet){
		return GovProjectDao.getInstance().getFolderInfoByPage(pSet);
	}
	//获取中介事项
	public DataSet getConvenienceItemList(ParameterSet pSet){
		return GovProjectDao.getInstance().getConvenienceItemList(pSet);
	}
	public DataSet getPoliciList(ParameterSet pSet) {
		return GovProjectDao.getInstance().getPoliciList(pSet);
	}
	/**
	 * 查询街道社区简介
	 * @param pSet
	 * @return
	 */
	public DataSet getTownSummary(ParameterSet pSet) {
		return GovProjectDao.getInstance().getTownSummary(pSet);
	}
	/**
	 * 获取 街道 社区 主题
	 * @param pSet
	 * @return
	 */
	public DataSet getTheme(ParameterSet pSet) {
		return GovProjectDao.getInstance().getTheme(pSet);
	}
	/**
	 * 获取结果材料
	 * @param pSet
	 * @return
	 */
	public DataSet getResultMaterial(ParameterSet pSet) {
		return GovProjectDao.getInstance().getResultMaterial(pSet);
	}
	//获取行政授权
	public DataSet getAuthority(ParameterSet pSet){
		return GovProjectDao.getInstance().getAuthority(pSet);
	}
}