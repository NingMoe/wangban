package action.icity.guestbook;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.tools.ToolErrorReporter;

import app.icity.guestbook.WriteCmd;

import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GuestbookConsult extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
		WriteCmd cmd=new WriteCmd();
		ParameterSet pSet=new ParameterSet();
		String strPageNum = this.getParameter("page");//当前页数
		int pageNum = 1;
		if(StringUtils.isEmpty(strPageNum)){
			pageNum= 1;
		}else{
			pageNum = Integer.parseInt(strPageNum);
		}
		if(pageNum<1){
			pageNum=1;
		}
		String depart=this.getParameter("depart");
		if(StringUtils.isNotEmpty(depart)){
			pSet.setParameter("depart_id@=", depart);
		}
		String keyword=this.getParameter("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			pSet.setParameter("content@like",keyword);
		}		
		pSet.setParameter("start", (pageNum-1)*10);
		pSet.setParameter("limit", 10);
		DataSet ds = cmd.getHotConsult(pSet);
		data.put("ResultList", ds.toJsonObject());
		data.put("ResultList_Pager", makePager(ds,pageNum));
		return true;
	}	
}
