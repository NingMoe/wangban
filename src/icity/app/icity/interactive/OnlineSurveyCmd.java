package app.icity.interactive;

import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;

public class OnlineSurveyCmd extends BaseQueryCommand{
	/**
	 * 舟山市问卷调查列表
	 * @author liuyq
	 * @return
	 */
	public DataSet getSurveyList(ParameterSet pSet){
		DataSet result = OnlineSurveyDao.getInstance().getSurveyList(pSet); 
		return result;
	}
	/**
	 * 舟山市问卷调查   根据ID获取问卷信息
	 * @author liuyq
	 * @return
	 */
	public DataSet getSurveyByID(ParameterSet pSet){
		DataSet result = OnlineSurveyDao.getInstance().getSurveyByID(pSet); 
		return result;
	}
	
	/**
	 * 保存用户提交的问卷答案
	 * @author liuyq
	 * @param pSet  verifyCode:验证码    
	 * @return  state：  0：更新数据库失败     1：更新成功    2：验证码不正确     3：验证码过期    4:手机号重复
	 */
	public DataSet saveAnswer(ParameterSet pSet){
        //新余问卷调查没有验证码
		if("xy".equals(SecurityConfig.getString("AppId"))){
			DataSet ds = OnlineSurveyDao.getInstance().validatePhone(pSet, this.getUserInfo(pSet));
			if(ds.getTotal() == 0){
				DataSet result = OnlineSurveyDao.getInstance().saveAnswer(pSet, this.getUserInfo(pSet));
				return result;
			}else{
				ds.setState(new Byte("4"));
				return ds;
			}
		}
		
		String verifyCode = (String) CacheManager.get("VerifyCode", pSet.getSessionId());
		String verifyPara = "";
		if(pSet.containsKey("verifyCode")){
			verifyPara = pSet.get("verifyCode").toString();
			pSet.remove("verifyCode");
		}
		if(null == verifyCode){
			DataSet ds = new DataSet();
			ds.setState(new Byte("3"));
			return ds;
		}else if(verifyPara.equalsIgnoreCase(verifyCode)){
			DataSet ds = OnlineSurveyDao.getInstance().validatePhone(pSet, this.getUserInfo(pSet));
			if(ds.getTotal() == 0){
				DataSet result = OnlineSurveyDao.getInstance().saveAnswer(pSet, this.getUserInfo(pSet));
				return result;
			}else{
				ds.setState(new Byte("4"));
				return ds;
			}
		}else{
			DataSet ds = new DataSet();
			ds.setState(new Byte("2"));
			return ds;
		}
	}
	
	/**
	 * 获取问卷统计结果
	 * @author liuyq
	 * @param type 0:选择题     1：问答题
	 * @return state 0:还未有人答题    1：更新成功
	 */
	public DataSet getStatisticsByID(ParameterSet pSet){
		DataSet result = OnlineSurveyDao.getInstance().getStatisticsByID(pSet);
		return result;
	}
}
