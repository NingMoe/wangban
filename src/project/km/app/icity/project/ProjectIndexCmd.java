package km.app.icity.project;


import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import app.icity.project.*;

public class ProjectIndexCmd extends app.icity.project.ProjectIndexCmd{	
	//业务中心->业务信息
	public DataSet getBusinessIndexList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexList(pSet);
	}
	
	//获取企业设立列表
	public DataSet getBusinessEnterList(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessEnterList(pSet);
	}
	
	public DataSet BusinessENTERByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessENTERByNameAndId(pSet);
	}
	//单体事项、企业设立、工程建设联合查询
	public DataSet UnionSearch(ParameterSet pSet){
		return ProjectIndexDao.getInstance().UnionSearch(pSet);
	}
	
	/***
	 * 获取各种状态的总条数
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessIndexCount(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexCount(pSet);
	}
	
	/**
	 * 更新备注
	 */	
	public DataSet updateRemark(ParameterSet pset){
		return ProjectIndexDao.getInstance().updateRemark(pset);
	}
	
	/**
	 * 获取总数
	 * @return
	 */
	public DataSet getTotal(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTotal(pSet);
	}
	
	/**
	 *  按事项类型统计
	 * @return
	 */
	public DataSet getTypeTotal(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTypeTotal(pSet);
	}
	
	/**
	 *按业务状态统计
	 * @return
	 */
	public DataSet getStateTotal(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getStateTotal(pSet);
	}
	
	
	public DataSet BusinessQueryByProjid(ParameterSet pSet){
	 return ProjectIndexDao.getInstance().getBusinessIndexAllList(pSet);
	}
	
	/**
	 * @Description: 根据申办流水号查询流转过程信息
	 * @return DataSet    返回类型
	 * @since 2015-12-24
	 */
	public DataSet GetBusinessProgressInfo(ParameterSet pSet){
		return ProjectIndexDao.getInstance().GetBusinessProgressInfo(pSet);
	}	
	
	/**
	 * @Description: 根据申办流水号和查询密码查询表单信息
	 * @return DataSet    返回类型
	 * @since 2015-8-10
	 * @author liuyq formInfoQueryByNameAndPhone
	 */
	public DataSet formInfoQueryByNameAndPassword(ParameterSet pSet){
		return ProjectIndexDao.getInstance().formInfoQueryByNameAndPassword(pSet);
	}
	
	//根据申办流水号和电话号码从汇总库查询办件信息
	public DataSet formInfoQueryByNameAndPhoneQZK(ParameterSet pSet){
		return ProjectIndexDao.getInstance().formInfoQueryByNameAndPhone(pSet);
	}
	
	public DataSet formInfoQueryBySBLSH(ParameterSet pSet){
		return ProjectIndexDao.getInstance().formInfoQueryBySBLSH(pSet);
	}
	
	/**
	 * @Description: 根据申办流水号和联系人手机查询表单信息
	 * @return DataSet    返回类型
	 * @since 2015-8-10
	 * @author liuyq
	 */
	public DataSet formInfoQueryByNameAndPhone(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexByNameAndPhone(pSet);
	}
	
	/**
	 * @Description: 根据申办流水号和申办人查询流程信息
	 * @return DataSet    返回类型
	 * @since 2015-8-13
	 * @author liuyq
	 */
	public DataSet materialListQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().materialListQueryByNameAndId(pSet);
	}
	
	//办件查询，申办流水号+申请人名称、单位、申办主题
	public DataSet BusinessProgressQuery(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessProgressQuery(pSet);
	}
	//办件查询，申办流水号+密码
	public DataSet BusinessProgressQueryByPassword(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessProgressQueryByPassword(pSet);
	}
	//办件结果公示
	public DataSet BusinessNoticeQuery(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessNoticeQuery(pSet);
	}

	
	public DataSet deleteBusiness(ParameterSet pSet) {
		return ProjectIndexDao.getInstance().deleteBusiness(pSet);
	}
	
	public DataSet getBzbqLog(ParameterSet pSet) {
		return ProjectIndexDao.getInstance().getBzbqLog(pSet);
	}
	
	//撤回
	public DataSet updateBusinessIndex(ParameterSet pSet){
		return ProjectIndexDao.getInstance().updateBusinessIndex(pSet);
	}
	
	//ylxfw数据,办件统计
		public DataSet getStateTotalLyxf(ParameterSet pSet){
			return ProjectIndexDao.getInstance().getStateTotalLyxf(pSet);
		}
		
	//ylxfw数据,办件动态统计
		public DataSet getHotLyxf(ParameterSet pSet){
			return ProjectIndexDao.getInstance().getHotLyxf(pSet);
		}	
		
}