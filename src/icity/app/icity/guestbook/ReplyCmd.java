package app.icity.guestbook;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class ReplyCmd extends BaseQueryCommand{ 
	public DataSet getList(ParameterSet pSet){
		return ReplyDao.getInstance().getList(pSet);
	}
	
	public DataSet insert(ParameterSet pSet){
		return ReplyDao.getInstance().insert(pSet);
	}
	
	public DataSet update(ParameterSet pSet){
		return ReplyDao.getInstance().update(pSet);
	}
		
	public DataSet delete(ParameterSet pSet){
		return ReplyDao.getInstance().delete(pSet);
	}	
	
	//部门回复
	public DataSet reply(ParameterSet pSet){
		return ReplyDao.getInstance().reply(pSet);
	}
	
	//审核
	public DataSet check(ParameterSet pSet){
		return ReplyDao.getInstance().check(pSet);
	}
	
	//分发
	public DataSet giveaway(ParameterSet pSet){
		return ReplyDao.getInstance().giveaway(pSet);
	}
	
	//分发人直接回复
	public DataSet give_reply(ParameterSet pSet){
		return ReplyDao.getInstance().give_reply(pSet);
	}
}
