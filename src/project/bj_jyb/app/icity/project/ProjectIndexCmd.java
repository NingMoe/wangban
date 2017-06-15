package bj_jyb.app.icity.project;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class ProjectIndexCmd extends BaseQueryCommand{	
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
	
	/**
	 * @Description: 根据申办流水号和申办人查询办件
	 * @return DataSet    返回类型
	 * @since 2013-8-22 
	 */
	public DataSet BusinessQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessIndexAllList(pSet);
	}	
	
	/**
	 * @Description: 根据申办流水号和申办人查询表单信息
	 * @return DataSet    返回类型
	 * @since 2015-8-10
	 * @author liuyq
	 */
	public DataSet formInfoQueryByNameAndId(ParameterSet pSet){
		return ProjectIndexDao.getInstance().formInfoQueryByNameAndId(pSet);
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
	//申办流水号
		public DataSet BusinessProgressQueryBySblsh(ParameterSet pSet){
			return ProjectIndexDao.getInstance().BusinessProgressQueryBySblsh(pSet);
		}
	
	//办件查询，申办流水号+密码
	public DataSet BusinessProgressQueryByPassword(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessProgressQueryByPassword(pSet);
	}
	//办件结果公示
	public DataSet BusinessNoticeQuery(ParameterSet pSet){
		return ProjectIndexDao.getInstance().BusinessNoticeQuery(pSet);
	}
	/**
	 * 根据申办流水号从审批获取办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfoFromApprovalBySblsh(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessInfoFromApprovalBySblsh(pSet);
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
	/**
	 * 根据申办流水号从审批获取办件信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessInfoFromApproval(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getBusinessInfoFromApproval(pSet);
	}
	
	/**
	 * 访问量统计部门
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsDept(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTrafficStatisticsDept(pSet);
	}
	/**
	 * 访问量统计分厅
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsHall(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTrafficStatisticsHall(pSet);
	}
	/**
	 * 访问量统计分厅--当前分厅总数
	 * @param pSet
	 * @return
	 */
	public DataSet getTrafficStatisticsHallNow(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getTrafficStatisticsHallNow(pSet);
	}
	/**
	 * 新余热门服务
	 * @param pSet
	 * @return
	 */
	public DataSet getPopularServices(ParameterSet pSet){
		return ProjectIndexDao.getInstance().getPopularServices(pSet);
	}
}