package app.icity.project;


import net.sf.json.JSONObject;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

//import hlj_dq.app.icity.project.ProjectIndexDao;

public class WdwpCmd extends BaseQueryCommand{	
	public DataSet getNetDiskList(ParameterSet pSet){
		return WdwpDao.getInstance().getNetDiskList(pSet);
	} 
	public DataSet deleteNetDiskDoc(ParameterSet pSet){
		return WdwpDao.getInstance().deleteNetDiskDoc(pSet);
	}
	
	/**
	 * 查询关联事项
	 * @param pSet
	 * @return
	 */
	public DataSet getRelatedItemAttach(ParameterSet pSet){
		return WdwpDao.getInstance().getRelatedItemAttach(pSet);
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
	/**
	 * 根据上传文件获取与其相似文件
	 * @return
	 */
	public DataSet getSimilarFile(ParameterSet pSet){
		return WdwpDao.getInstance().getSimilarFile(pSet);
	}
	/**
	 * 如果使用的资料库的文件首先判断本地资料库是否存在此文件，
	 * 有则返回此文件，无则写入此文件同时存入本地网盘
	 * @return
	 */
	public DataSet getBdBusinessAttach(ParameterSet pSet){
		return WdwpDao.getInstance().getBdBusinessAttach(pSet);
	}
	/**
	 * 对上传文件重命名
	 * @return
	 */
	public DataSet getFileRename(ParameterSet pSet){
		return WdwpDao.getInstance().getFileRename(pSet);
	}
	/**
	 * 提交历史资料至省资料库
	 * @return
	 */
	public DataSet sendData(ParameterSet pSet){
		return WdwpDao.getInstance().sendData();
	}
}
