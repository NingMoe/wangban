package xy.app.icity.govservice;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class GovProjectDao extends BaseJdbcDao {
	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
	}

	/**
	 * 江西新余办件公示 50条数据
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet BusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url_jxxy")
					+ "/getBusinessListInfo?regionCode=" + SecurityConfig.getString("WebRegion"));
			HttpClientUtil client = new HttpClientUtil();
			Object ret = client.getResult(url, "");
			ret = java.net.URLDecoder.decode(ret.toString(), "UTF-8");
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("info");
			// ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}

		return ds;
	}

	/**
	 * 新余三单
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String power_type = (String) pSet.get("power_type");
			String deptid = (String) pSet.get("deptid");
			String str = "";
			if (StringUtils.isNotEmpty(name)) {
				str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '" + rid
						+ "' and s.name = '" + name + "')";
			} else {
				str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '" + rid
						+ "')";
			}
			if (StringUtils.isNotEmpty(power_type)) {
				str += " and t.power_type='" + power_type + "'";
			}
			if (StringUtils.isNotEmpty(deptid)) {
				str += " and t.dept_id='" + deptid + "'";
			}
			String sql = "select to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,attach.DOCID,attach.NAME PICNAME,t.POWER_TYPE from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by power_type_id,t.POWER_TYPE asc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentList："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	/**
	 * 新余三单 数量
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContentListCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String power_type = (String) pSet.get("power_type");
			String deptid = (String) pSet.get("deptid");
			String str = "";
			if (StringUtils.isNotEmpty(name)) {
				str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '" + rid
						+ "' and s.name = '" + name + "')";
			} else {
				str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '" + rid
						+ "')";
			}
			if (StringUtils.isNotEmpty(power_type)) {
				str += " and t.power_type='" + power_type + "'";
			}
			if (StringUtils.isNotEmpty(deptid)) {
				str += " and t.dept_id='" + deptid + "'";
			}
			String sql = "select sumxzxk+sumxzcf+sumxzqz+sumxzzs+sumxzcj+sumxzqr+sumxzjl+sumqtxzql+sumxzjf+sumbmfw count,"
					+ "sumxzxk,sumxzcf,sumxzqz,sumxzzs,sumxzcj,sumxzqr,sumxzjl,sumqtxzql,sumxzjf,sumbmfw  from "
					+ "(select sum(case when t.power_type ='行政许可' then 1 else 0 end) sumxzxk,"
					+ "sum(case when t.power_type ='行政处罚' then 1 else 0 end) sumxzcf,"
					+ "sum(case when t.power_type ='行政强制' then 1 else 0 end) sumxzqz,"
					+ "sum(case when t.power_type ='行政征收' then 1 else 0 end) sumxzzs,"
					+ "sum(case when t.power_type ='行政裁决' then 1 else 0 end) sumxzcj,"
					+ "sum(case when t.power_type ='行政确认' then 1 else 0 end) sumxzqr,"
					+ "sum(case when t.power_type ='行政奖励' then 1 else 0 end) sumxzjl,"
					+ "sum(case when t.power_type ='其他行政权力' then 1 else 0 end) sumqtxzql,"
					+ "sum(case when t.power_type ='行政给付' then 1 else 0 end) sumxzjf,"
					+ "sum(case when t.power_type ='便民服务' then 1 else 0 end) sumbmfw from "
					+ "pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' "
					+ "where t.submit_status='1' and t.status='1' and t.checks='1'" + str + " order by ctime desc)";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentList："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	// 新余底部
	public DataSet getContentInfoXy(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String name = (String) pSet.get("name");
			String sql = "select to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ct,t.* from pub_content t where name=?";
			ds = this.executeDataset(sql, new Object[] { name });
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentInfoXy："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	// 获取三单的部门
	public DataSet getContentDept(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String rid = SecurityConfig.getString("WebRegion");
		String name = (String) pSet.get("name");
		try {
			String sql = "select t.dept_name,t.dept_id from PUB_CONTENT t  where 1=1 and t.cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "' ) group by t.dept_name,t.dept_id ";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
			// _log.info("getContentDept："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}
}