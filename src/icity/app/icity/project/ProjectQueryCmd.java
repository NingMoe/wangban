package app.icity.project;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

public class ProjectQueryCmd extends BaseQueryCommand {
	public DataSet getProjectInfo(ParameterSet Pset) {
		
//		String YZM = (String) Pset.remove("YZM");
//		String verifyCode = (String) CacheManager.get("VerifyCode", Pset.getSessionId());
//		if(verifyCode!=null){
//			if (StringUtils.isEmpty(verifyCode)) {
//				ds.setState(StateType.FAILT);
//				ds.setMessage("验证码已超时失效");
//				return ds;
//			} else {
//				if (!verifyCode.equalsIgnoreCase(YZM)) {
//					ds.setState(StateType.FAILT);
//					ds.setMessage("验证码输入错误");
//					return ds;
//				} 
//			}
//		}
		/*if(null != user){
			return ProjectQueryDao.getInstance().getProjectInfo(Pset,user);
		}else{*/
			return ProjectQueryDao.getInstance().getProjectInfo(Pset);
		//}
	}
	
	public DataSet getProjectInfo_pingyi(ParameterSet Pset) {
		return ProjectQueryDao.getInstance().getProjectInfo_pingyi(Pset);
	}
	
	public DataSet insert(ParameterSet pSet){ 
		return ProjectQueryDao.getInstance().insert(pSet);
	}
	
	public DataSet getPingYiList(ParameterSet pSet){
		return ProjectQueryDao.getInstance().getPingYiList(pSet);
	}
	public DataSet getPYList(ParameterSet Pset){
		UserInfo user = this.getUserInfo(Pset);
			return ProjectQueryDao.getInstance().getPYList(Pset,user);
	}
	public DataSet getPY(ParameterSet Pset){
		return ProjectQueryDao.getInstance().getPY(Pset);
	}
	public DataSet getMyPY(ParameterSet Pset){
		Pset.setParameter("BI.UCID", this.getUserInfo(Pset).getUid());
		return ProjectQueryDao.getInstance().getPY(Pset);
	}
	public DataSet update(ParameterSet pSet){
		return ProjectQueryDao.getInstance().update(pSet);
	}
}
