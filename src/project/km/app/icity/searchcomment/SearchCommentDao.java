package km.app.icity.searchcomment;

import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class SearchCommentDao extends BaseJdbcDao {

	private SearchCommentDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static SearchCommentDao _instance = null;

	public static SearchCommentDao getInstance() {
		return DaoFactory.getDao(SearchCommentDao.class.getName());
	}

	// public DataSet getChannelName(ParameterSet pSet){
	// String rid = SecurityConfig.getString("WebRegion");
	// String name = (String) pSet.getParameter("key");
	// DataSet ds = new DataSet();
	// try{
	// String sql = "select id, name from pub_content where rid='"+rid+"' and
	// name = '"+name+"'";
	// ds = this.executeDataset(sql);
	// } catch(Exception e )
	// {
	// e.printStackTrace();
	// }
	// return ds;
	//
	// }

	public DataSet getContentInfoOfEventPublic(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String name = (String) pSet.getParameter("key");
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String str = "and t.rid = '" + rid + "'" + " and c.rid = '" + rid + "'" + " and t.name like '%" + name
					+ "%'";
			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,c.name as cname "
					+ "from pub_content t, pub_channel c where t.cid = c.id and t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by ctime desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo：" + e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}
}
