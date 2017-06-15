package app.pmi.hits;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class HitsCmd extends BaseQueryCommand {
	
	public DataSet batchAddHits(ParameterSet pSet) {
		return HitsDao.getInstance().batchAddHits(pSet);
	}
	
	public DataSet getList(ParameterSet pSet){
		return HitsDao.getInstance().getList(pSet);
	}
	
	public DataSet getStastictList(ParameterSet pSet){
		return HitsDao.getInstance().getStastictList(pSet);
	}
	
	public DataSet getHitsListByOneDay(ParameterSet pSet){
		return HitsDao.getInstance().getHitsListByOneDay(pSet);
	}
	
	public DataSet getHitsListByTimeAll(ParameterSet pSet){
		return HitsDao.getInstance().getHitsListByTimeAll(pSet);
	}
	
	public DataSet getHitsListByTime(ParameterSet pSet){
		return HitsDao.getInstance().getHitsListByTime(pSet);
	}
	
	public DataSet getSummaryByVisit(ParameterSet pSet){
		return HitsDao.getInstance().getSummaryByVisit(pSet);
	}

	public DataSet getStastictListByVisit(ParameterSet pSet){
		return HitsDao.getInstance().getStastictListByVisit(pSet);
	}
	
	public DataSet getStastictListByArea(ParameterSet pSet){
		return HitsDao.getInstance().getStastictListByArea(pSet);
	}
	public DataSet getStastictListBySource(ParameterSet pSet){
		return HitsDao.getInstance().getStastictListBySource(pSet);
	}
}
