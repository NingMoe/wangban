package app.icity.search;

import com.icore.core.ThreadPoolBean;
import com.icore.core.ThreadPoolManager;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Command;

public class SearchConsultingListJob extends BaseQueryCommand {
	public DataSet execute(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			Command cmd = new Command("app.icity.search.SearchGenCmd");
			ds = cmd.execute("getConsultingList");
			if (ds.getState() == StateType.SUCCESS) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
}