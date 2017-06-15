package zb.app.icity.govservice;

import app.icity.sync.UploadUtil;
import core.util.HttpClientUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GovProjectDao extends BaseJdbcDao {
	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
	}

	public DataSet getContentInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "')";
			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
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
		DataSet dsAttach;
		try {
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 获取重点事项公布的内容
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentInfoOfEventName(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String cid = (String) pSet.getParameter("cid");
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String sql = "select s.name as mname from pub_channel s  where s.rid = '" + rid + "' and s.id= '" + cid
					+ "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	public DataSet getContentInfoOfEventPublic(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		String cid = (String) pSet.getParameter("cid");
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and id='" + cid + "' )";
			String sql = "select l.name as mname,to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join  pub_channel l on t.cid=l.id left join ATTACH attach on t.id = attach.CONID  and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
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

		try {
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * GQ
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentInfoOfGQPublic(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		String cid = (String) pSet.getParameter("cid");
		try {
			String str = " and cid in (select id from pub_channel s  where s. id= '" + cid + "' )";
			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
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

		try {
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusinessStatDao2(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String count = (String) pSet.getParameter("count");
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl("/Interface/GongGao.asmx?count=" + count);
			Object ret = client.getResult(url, "");
			ret = JSONObject.fromObject(ret);
			System.out.println("统计结果22:" + ret);
			ds.setRawData(ret);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public DataSet getBusinessStatDao(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String regionCode = "";
		try {
			String rid = SecurityConfig.getString("WebRegion");
			if ((!"".equals(rid) || rid != null) && !"370000000000".equals(rid)) {
				regionCode = rid;
			}
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(
					SecurityConfig.getString("approval_url") + "/getAcceptQuantity?regionCode=" + regionCode);
			Object ret = client.getResult(url, "");
			ret = JSONObject.fromObject(ret);
			System.out.println("统计结果:" + ret);
			ds.setRawData(ret);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}
}
