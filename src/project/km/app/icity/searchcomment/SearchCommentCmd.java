package km.app.icity.searchcomment;

import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import app.icity.govservice.GovProjectDao;

public class SearchCommentCmd {
	
	
//	public DataSet getChannelName(ParameterSet pSet){
//		return SearchCommentDao.getInstance().getChannelName(pSet);
//	}
	
	public DataSet getContentInfoOfEventPublic(ParameterSet pSet) {
		return SearchCommentDao.getInstance().getContentInfoOfEventPublic(pSet);
	}

}
