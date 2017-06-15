package hlj_qqheNew.app.icity.enterprises;

import app.icity.project.ProjectIndexDao;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.SecurityConfig;

/**
 * 企业设立  lhy
 * @author lenovo
 *
 */
public class EnterprisesCmd extends BaseQueryCommand {
	/**
	 * 工商核准
	 * @param pSet
	 * @return
	 */
	public DataSet namePreCheckLoad(ParameterSet pSet){ 
		return EnterprisesDao.getInstance().namePreCheckLoad(pSet);
	}
	/**
	 * 获取规则信息
	 * @return
	 */
	public DataSet getBusinessRulesInfo(ParameterSet pSet){ 
		String region = SecurityConfig.getString("WebRegion");
		return EnterprisesDao.getInstance().getBusinessRulesInfo(pSet, region);
	}
	/**
	 * 获取申报信息
	 * @param pSet
	 * @return
	 */
	public DataSet getApplicationInfo(ParameterSet pSet){ 
		return EnterprisesDao.getInstance().getApplicationInfo(pSet);
	}
	/**
	 * 获取工商材料
	 * @param pSet
	 * @return
	 */
	public DataSet getMaterialByCompanyType(ParameterSet pSet){
		return EnterprisesDao.getInstance().getMaterialByCompanyType(pSet);
	}
	/**
	 * 提交
	 * @param pSet 
	 * @return
	 */
	public DataSet submitApplicationInfo(ParameterSet pSet){
		DataSet ds= new DataSet();
		String uid = String.valueOf(this.getUserInfo((String)pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		DataSet _ds=EnterprisesDao.getInstance().submitApplicationInfo(pSet);
		if(_ds.getState()==1){
			ds.setMessage(_ds.getMessage());
		}else{
			ds.setMessage(null);
		}
		return ds;
	}
	/**
	 * 获取企业工商信息
	 * @author liuyq  approveNum:受理编号      password：企业密码
	 * @param pSet 
	 * @return
	 */
	public DataSet _namePreCheckLoad(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance()._namePreCheckLoad(pSet);
		return ds;
	}
	
	/**
	 * 获取联办说明-舟山
	 * @param pSet
	 * @return
	 */
	public DataSet zs_getSeecGuide(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().zs_getSeecGuide(pSet);
		return ds;
	}
	
	/**
	 * 获取联办事项材料及时限-舟山
	 * @param pSet
	 * @return
	 */
	public DataSet zs_getSeecItem(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().zs_getSeecItem(pSet);
		return ds;
	}
	
	/**
	 * 获取申报信息-舟山
	 * @param pSet
	 * @return
	 */
	public DataSet zs_getApplicationInfo(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().zs_getApplicationInfo(pSet);
		return ds;
	}
	
	/**
	 * 获取办事流程-舟山
	 * @param pSet
	 * @return
	 */
	public DataSet zs_getFlowImg(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().zs_getFlowImg(pSet);
		return ds;
	}
	
	public DataSet getResourceList(ParameterSet pSet){
		return EnterprisesDao.getInstance().getResourceList(pSet);
		
	}
	
	/**
	 * 上传材料页面【暂存】
	 * by kongweisong
	 */
	public DataSet saveDraftApplicationInfo(ParameterSet pSet)
	{
		DataSet ds= new DataSet();
		String uid=null;
		try{
              uid = String.valueOf(this.getUserInfo((String)pSet.get("param_icore_session_id")).getUid());}
		catch(Exception e)
		{
			ds.setMessage("暂存失败：未登录或获取用户信息失败");
			return ds;
		}
		pSet.setParameter("uid",uid);
		DataSet _ds =EnterprisesDao.getInstance().saveEnterprise_business_index(pSet);
		if(_ds.getState()==1){
			ds.setMessage("暂存成功");
			//_ds.getData()得到的是index表的ID.在页面上标识为：ID_index
			ds.setData(_ds.getData());
		}else{
			ds.setMessage("暂存失败");
		}
		return ds;
	}
	
	/**
	 * 表单页面【暂存】
	 * by kongweisong
	 */
	public DataSet first_saveDraftApplicationInfo(ParameterSet pSet)
	{	String uid = String.valueOf(this.getUserInfo((String)pSet.get("param_icore_session_id")).getUid());
	    pSet.setParameter("uid",uid);
		DataSet ds=new DataSet();
		DataSet _ds =EnterprisesDao.getInstance().fist_saveEnterprise_business_index(pSet);
		if(_ds.getState()==1){
			ds.setMessage(_ds.getMessage());
			//_ds.getData()得到的是index表的ID.在页面上标识为：ID_index
			ds.setData(_ds.getData());
		}
		else{
			ds.setMessage(null);
		}
		return ds;
	}
	
	//联合踏勘事项查询
	public DataSet queryLhtkById(ParameterSet pSet){
		return EnterprisesDao.getInstance().queryLhtkById(pSet);
	}
	
	//踏勘通知查询
	public DataSet getTaKanMessage(ParameterSet pSet){
		return EnterprisesDao.getInstance().getTaKanMessage(pSet);
	}
	
	//踏勘会签意见查询
	public DataSet getTaKanSign(ParameterSet pSet){
		return EnterprisesDao.getInstance().getTaKanSign(pSet);
	}
	
	//一次性告知
	public DataSet getTaKanItem2(ParameterSet pSet){
		return EnterprisesDao.getInstance().getTaKanItem2(pSet);
	}
	
	public DataSet getLhtkResourceList(ParameterSet pSet){
		return EnterprisesDao.getInstance().getLhtkResourceList(pSet);
		
	}
	
	
	/**
	 * 提交
	 * @param pSet 
	 * @return
	 */
	public DataSet submitLhtkInfo(ParameterSet pSet){
		DataSet ds= new DataSet();
		String uid = String.valueOf(this.getUserInfo((String)pSet.get("param_icore_session_id")).getUid());
		pSet.setParameter("uid", uid);
		DataSet _ds=EnterprisesDao.getInstance().submitLhtkInfo(pSet);
		if(_ds.getState()==1){
			ds.setMessage(_ds.getMessage());
		}else{
			ds.setMessage(null);
		}
		return ds;
	}
	
	/**
	 * 前台联合踏勘提交
	 * @param pSet 
	 * @return
	 */
	public DataSet submitQtLhtkInfo(ParameterSet pSet){
		DataSet ds= new DataSet();
		DataSet _ds=EnterprisesDao.getInstance().submitLhtkInfo(pSet);
		if(_ds.getState()==1){
			ds.setMessage(_ds.getMessage());
		}else{
			ds.setMessage(null);
		}
		return ds;
	}
	
	/**
	 * 联办进度查询
	 * @author liuyq
	 * @param pSet  acceptanceNum：受理面好   queryPsw：查询密码
	 * @return
	 */
	public DataSet queryProgress(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().queryProgress(pSet);
		return ds;
	}
	
	/**
	 * 联审联办办件公示
	 * @param pSet
	 * @return
	 */
	public DataSet getPublicity(ParameterSet pSet){
		UserInfo ui = this.getUserInfo(pSet);
		DataSet ds = EnterprisesDao.getInstance().getPublicity(pSet, ui);
		return ds;
	}
	/**
	 * 区划办件统计
	 * @param pSet
	 * @return
	 */
	public DataSet getRegionBusiness(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().getRegionBusiness(pSet);
		return ds;
	}
	
	/**
	 * 获取业务规则列表
	 * @param pSet
	 * @return
	 */
	public DataSet getFlow(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().getFlow(pSet);
		return ds;
	}
	
	/**
	 * 获取经营范围树
	 * @param pSet
	 * @return
	 */
	public DataSet getScopeTree(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().getScopeTree(pSet);
		return ds;
	}
	
	/**
	 * 根据业务规则获取申报材料-注册登记
	 * @param pSet
	 * @return
	 */
	public DataSet getApplicationInfo_qqhe(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().getApplicationInfo_qqhe(pSet);
		return ds;
	}
	
	/**
	 * 根据业务规则获取申报材料-注册登记
	 * @param pSet
	 * @return
	 */
	public DataSet getItemByScope(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().getItemByScope(pSet);
		return ds;
	}
	
	/**
	 * 提交
	 * @param pSet
	 * @return
	 */
	public DataSet submitApplicationInfo_qqhe(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().submitApplicationInfo_qqhe(pSet);
		return ds;
	}
	
	/**
	 * 进度查询
	 * @param pSet
	 * @return
	 */
	public DataSet queryBusinessEnterBybizId(ParameterSet pSet){
		DataSet ds = EnterprisesDao.getInstance().queryBusinessEnterBybizId(pSet);
		return ds;
	}
}
