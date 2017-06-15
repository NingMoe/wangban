package hubei.app.icity.project;

import hubei.app.icity.project.PowerDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;


public class PowerCmd extends BaseQueryCommand{
	
	public DataSet getPowerItem(ParameterSet pSet){
		return PowerDao.getInstance().getPowerItem(pSet);
	}
	
	public DataSet getPowerItemChild(ParameterSet pSet){
		return PowerDao.getInstance().getPowerItemChild(pSet);
	}
	
	public DataSet getDeptList(ParameterSet pSet){
		return PowerDao.getInstance().getDeptList(pSet);
	}
	
	
    /*	
     * 查询办事指南公共项
     */
	//只有一个子项时，根据父项code查询子项code
	public DataSet getCodeByFolder(ParameterSet pSet){
		String folderCode = (String) pSet.getParameter("folderCode");
		return PowerDao.getInstance().getCodeByFolder(folderCode);
	}
	//职权运行流程
	public DataSet getItemExt_zqyxlc(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_zqyxlc(pSet);
	}
	//责任事项、责任事项依据、职责边界、咨询方式、监督投诉方式、证照批复名称
	public DataSet getItemExt_form(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_form(pSet);
	}
	//流程图
	public DataSet getItemExt_map(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_map(pSet);
	}
	//职权依据
	public DataSet getItemExt_zqyj(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_zqyj(pSet);
	}
	
	/*
	 * XK
	 * 行政许可类
	 */
	//许可范围及条件
	public DataSet getItemExt_xkfwjtj(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_xkfwjtj(pSet);
	}
	//申请材料
	public DataSet getItemExt_sqcl(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_sqcl(pSet);
	}
    //法定期限、承诺期限、特殊程序期限
	public DataSet getItemExt_fdcntbcxqx(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_fdcntbcxqx(pSet);
	}
	//收费依据及标准
	public DataSet getItemExt_sfyjjbz(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_sfyjjbz(pSet);
	}
	
	
	/*
	 * CF
	 * 行政处罚类
	 */
	//细化量化自由裁量权标准
	public DataSet getItemExt_xhlhzyclqbz(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_xhlhzyclqbz(pSet);
	}
	
	
	/*
	 * QR
	 * 行政确认类
	 */
	//许可范围及条件
	public DataSet getItemExt_slfwjtj(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_xkfwjtj(pSet);
	}
	//需提供材料
	public DataSet getItemExt_xtgcl(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_sqcl(pSet);
	}
	
	/*
	 * ZS
	 * 行政征收类
	 */
	//许可范围及条件
	public DataSet getItemExt_zsdxjtj(ParameterSet pSet){
		return PowerDao.getInstance().getItemExt_xkfwjtj(pSet);
	}
	
	
}
