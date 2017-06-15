package km.app.icity.govservice;
/**
@Author： 赵宏明
@Version：
@Since:
@Create at: 2015年10月6日 上午10:47:37 
@Description:
**/

import org.apache.commons.lang.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.icity.sync.UploadUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovServiceDao extends BaseJdbcDao {

	private GovServiceDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovServiceDao getInstance() {
		return DaoFactory.getDao(GovServiceDao.class.getName());
	}

	/**
	 * 投资动态
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getInvestmentList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");
			String str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			if ("1".equals(open)) {
				str = "and (t.rid = '" + rid + "'"
						+ " or t.rid like '330901000000%') and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid + "' or s.rid like '330901000000%') and s.name = '" + name + "')";
			}

			// add by liuyq 添加查询条件
			String startTime = (String) pSet.get("startTime");
			String endTime = (String) pSet.get("endTime");
			String keyWord = (String) pSet.get("keyWord");
			String dept = (String) pSet.get("dept");
			String searchCon = "";
			if (StringUtils.isNotBlank(startTime)) {
				searchCon += " and t.cTime >= to_date('" + startTime + "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(endTime)) {
				searchCon += " and t.cTime <= to_date('" + endTime + "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(keyWord)) {
				searchCon += " and t.name like '%" + keyWord + "%' ";
			}
			if (StringUtils.isNotBlank(dept)) {
				searchCon += " and t.dept_id = '" + dept + "' ";
			}

			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + searchCon + " order by ctime desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfo："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}

		try {
			if (ds.getTotal() > 0) {
				String id = ds.getJOData().getString("ID");
				String sql = "select * from attach t where (type='2' or type ='3') and  conid = '" + id + "'";
				dsAttach = this.executeDataset(sql);
				int totla = dsAttach.getTotal();
				if (totla > 0) {
					// 这里加入下载代码
					JSONArray upfile = dsAttach.getJAData();
					for (int i = 0; i < upfile.size(); i++) {
						// ---下载文件到项目路径本地 start
						String fileName = (String) ((JSONObject) upfile.get(i)).get("NAME");
						String fileType = (String) ((JSONObject) upfile.get(i)).get("TYPE");
						String doc_id = (String) ((JSONObject) upfile.get(i)).get("DOCID");

						UploadUtil.downloadFile(fileName, doc_id, fileType);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

}
