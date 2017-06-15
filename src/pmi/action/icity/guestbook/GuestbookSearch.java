package action.icity.guestbook;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.Command;

public class GuestbookSearch extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String,Object> data){
		/*
		if("get".equals(this.getMethod())){
			this.sendRedirect(this.getContextPath()+"{{PageContext.ContextPath}}icity/guestbook/GuestbookSearch");
			return false;
		}
		this.getPostData();
		*/
		Map<String, String> PostData=  this.getPostData();
		String id =PostData.get("SeekCode");
		String type = PostData.get("Type");
		String depart =PostData.get("Departments");
		String keyword = PostData.get("KeyWord");
		String time_s = PostData.get("ctime_s");
		String time_e = PostData.get("ctime_e");
		
		Command cmd = new Command("app.icity.guestbook.WriteCmd");
		cmd.setParameter("OPEN", "1");
		if(StringUtils.isNotEmpty(id)){
			cmd.setParameter("ID", id);
		}
		if(StringUtils.isNotEmpty(type)&&!type.equals("所有问题类型")){
			cmd.setParameter("TYPE", type);
		}
		if(StringUtils.isNotEmpty(depart)&&!depart.equals("所有部门")){
			cmd.setParameter("DEPART_ID", depart);
		}		
		if(StringUtils.isNotEmpty(keyword )){
			cmd.setParameter("TITLE@like",keyword);
		}
		if(StringUtils.isNotEmpty(time_s)){
	    	   cmd.setParameter("WRITE_DATE@>=@Date",time_s+" 00:00:00");
	    }
	    if(StringUtils.isNotEmpty(time_e)){
	    	   cmd.setParameter("WRITE_DATE<=@Date",time_e+" 23:59:59");
	    }
		
		String strPageNum = this.getParameter("page");//当前页数
		int pageNum = 1;
		if(!StringUtils.isNotEmpty(strPageNum)){
			pageNum= 1;
		}else{
			pageNum = Integer.parseInt(strPageNum);
		}
		if(pageNum<1){
			pageNum=1;
		}
		cmd.setParameter("start", (pageNum-1)*10);
		cmd.setParameter("limit", 10);
		DataSet ds = cmd.execute("getList");
		data.put("ResultList", ds.toJsonObject());
		data.put("ResultList_Pager", makePager(ds,pageNum));
		return true;
	}
	
}
