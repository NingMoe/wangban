package hlj_qqheNew.app.icity.project;


import hlj_qqheNew.app.icity.project.WdwpDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;


public class WdwpCmd extends BaseQueryCommand{	
	public DataSet getNetDiskList(ParameterSet pSet){
		return WdwpDao.getInstance().getNetDiskList(pSet);
	} 
	public DataSet deleteNetDiskDoc(ParameterSet pSet){
		return WdwpDao.getInstance().deleteNetDiskDoc(pSet);
	} 
	/**
	 * 从附件表获取当前用户下的材料列表
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachList(ParameterSet pSet){
		return WdwpDao.getInstance().getAttachList(pSet);
	}
	
	/**
	 * 上传到网盘的材料写入数据库
	 * @param pSet
	 * @return
	 */
	public DataSet setBusinessAttach(ParameterSet pSet){
		return WdwpDao.getInstance().setBusinessAttach(pSet);
	}
	/**
	 * 上传到网盘的材料写入数据库（满意度调查问卷）
	 * @param pSet
	 * @return
	 */
	public DataSet setEvaluationAttach(ParameterSet pSet){
		return WdwpDao.getInstance().setEvaluationAttach(pSet);
	}


}
