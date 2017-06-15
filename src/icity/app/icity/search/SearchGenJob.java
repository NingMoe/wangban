package app.icity.search;

import com.icore.core.ThreadPoolBean;
import com.icore.core.ThreadPoolManager;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class SearchGenJob extends BaseQueryCommand {

	static class PostDataHandler extends ThreadPoolBean {
		private String searchType;

		public PostDataHandler(String searchType) {
			this.setId(searchType);
			this.searchType = searchType;
		}

		@Override
		public String descript() {
			return null;
		}

		@Override
		public void errorHandler() {

		}

		@Override
		public boolean handler() {
			SearchGenCmd cmd = new SearchGenCmd();
			ParameterSet pSet = new ParameterSet();
			if ("project".equals(searchType)) {
				cmd.genProject(pSet);
			} else {
				cmd.genContent(pSet);
			}
			return false;
		}
	}

	public DataSet execute(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			String key = "SearchPost";
			String[] searchType = { "project","content" };
			for (int i = 0; i < searchType.length; i++) {
				PostDataHandler pdh = new PostDataHandler(searchType[i]);
				boolean flag = ThreadPoolManager.getInstance(key).addTask(pdh);
				if (!flag) {
					ds.setState(StateType.FAILT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
}