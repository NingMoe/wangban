package km.app.icity.govservice;

import java.util.HashMap;
import java.util.Map;

import app.icity.sync.UploadUtil;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GovProjectDao extends BaseJdbcDao {
	private static Logger log = LoggerFactory.getLogger(GovProjectDao.class);
	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
	}

	/**
	 * 获取事项
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getMattersList2(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("TYPE");
			String REGION_CODE = (String) pSet.getParameter("region_code");
			String TITLE = (String) pSet.getParameter("TITLE");

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			/*whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("SP");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("ZJJG");*/
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and ( service_object like ? ");
				paramValue.add("%2%");
				whereValue.append(" or service_object like ? ");
				paramValue.add("%3%");
				whereValue.append(" or service_object like ? ");
				paramValue.add("%4%");
				whereValue.append(" or service_object like ? ");
				paramValue.add("%5%");
				whereValue.append(" or service_object like ? ");
				paramValue.add("%6%");
				whereValue.append(" or service_object like ?) ");
				paramValue.add("%7%");
			}
			if (("dept").equals(cat)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);

				// 部门和主题都有的时候 cat=dept&id=编码&title=主题名称&pagemodel=person
				// 判断title是否存在 存在就查不存在就不查了
				if (!"".equals(TITLE) && TITLE != null) {
					whereValue.append(" and title_name like ? ");
					paramValue.add("%" + TITLE + "%");
				}
			} else if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			if (!"".equals(itemType) && itemType != null) {
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}else{
				whereValue.append(" and type in(?,?,?) ");
				paramValue.add("XK");
				paramValue.add("FW");
				paramValue.add("BM");
			}

			whereValue.append(" and REGION_CODE= ? ");
			if (!"".equals(REGION_CODE) && REGION_CODE != null) {
				paramValue.add(REGION_CODE);
			} else {
				paramValue.add(SecurityConfig.getString("WebRegion"));
			}

			map.put("page", pSet.getParameter("page").toString());
			map.put("rows", pSet.getParameter("limit").toString());
			// map.put("orgCode", "37011400000003");
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());

			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				JSONObject columns;
				column = (JSONObject) pageList.get(i);
				columns = column.getJSONObject("columns");
				String code = columns.getString("CODE");
				String sql = " select STAR_LEVEL from STAR_LEVEL_STAT t where t.SERVICE_CODE = ?";
				ds = this.executeDataset(sql, new Object[] { code });
				if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
					columns.put("STAR_LEVEL", ds.getRecord(0).getString("STAR_LEVEL"));
				} else {
					columns.put("STAR_LEVEL", "5");
				}

				rows.add(columns);
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 获取事项，添加便民的和非行政许可的也显示
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getMattersList3(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String itemType = (String) pSet.getParameter("TYPE");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("ZJJG");
			/*
			 * whereValue.append(" and TYPE!=? "); paramValue.add("SP");
			 */
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			}
			if (("dept").equals(cat)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);
			} else if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			if (!"".equals(itemType) && itemType != null) {
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));

			map.put("page", pSet.getParameter("page").toString());
			map.put("rows", pSet.getParameter("limit").toString());
			// map.put("orgCode", "37011400000003");
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 根据事项ID分类获取事项相关信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getAllItemInfoByItemID(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getAllItemInfoByItemID");
			String itemid = (String) pSet.getParameter("itemid");
			Map<String, String> map=new HashMap<String, String>();
			map.put("itemId", itemid);
			// GetMethod postMethod = new
			// GetMethod(url+"?itemId=DEB7A6A6DF01427094E232F5552E1CAB");
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url,map));
			String rows = obj.getString("material");
			ds.setRawData(rows);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public String getCatalog(String classtype) {
		String sql = "select * from power_catalog t where id = '" + classtype + "'";
		DataSet ds;
		ds = this.executeDataset(sql);
		// Object s = ds.getJOData().getString("NAME");
		return ds.getJOData().getString("NAME");
	}

	/**
	 * 事项列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPowerList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String deptid = (String) pSet.getParameter("deptid");
			String item_type = (String) pSet.getParameter("ITEM_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String folder_code = (String) pSet.getParameter("folder_code");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			// 判断是否为投资事项的字段
			String is_investment = (String) pSet.getParameter("is_investment");

			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (StringUtils.isNotEmpty(SearchName)) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}

			if (StringUtils.isNotEmpty(region_code)) {
				whereValue.append(" and region_code=? ");
				paramValue.add(region_code);
			}
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			if (StringUtils.isNotEmpty(item_type)) {
				whereValue.append(" and type=? ");
				paramValue.add(item_type);
			}else{
				whereValue.append(" and type in(?,?,?) ");
				paramValue.add("XK");
				paramValue.add("FW");
				paramValue.add("BM");
			}
			if (StringUtils.isNotEmpty(folder_code)) {
				whereValue.append(" and folder_code=? ");
				paramValue.add(folder_code);
			}
			if (StringUtils.isNotEmpty(is_investment)) {
				whereValue.append(" and is_investment=? ");
				paramValue.add(is_investment);
			}
			map.put("page", pSet.getParameter("page").toString());
			map.put("rows", pSet.getParameter("limit").toString());
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				JSONObject columns;
				column = (JSONObject) pageList.get(i);
				columns = column.getJSONObject("columns");
				String code = columns.getString("CODE");
				String sql = " select STAR_LEVEL from STAR_LEVEL_STAT t where t.SERVICE_CODE = ?";
				ds = this.executeDataset(sql, new Object[] { code });
				if (ds.getState() == StateType.SUCCESS && ds.getTotal() > 0) {
					columns.put("STAR_LEVEL", ds.getRecord(0).getString("STAR_LEVEL"));
				} else {
					columns.put("STAR_LEVEL", "5");
				}
				if(columns.containsKey("REMARK")){
					String remark = columns.getString("REMARK");
					if(remark==null||remark.equals("null")){
						columns.remove("REMARK");
						columns.put("REMARK","");
					}
				}
				rows.add(columns);
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("异常："+e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	public DataSet onQueryProgress(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String deptid = (String) pSet.getParameter("deptid");
			String NUM = (String) pSet.getParameter("NUM");

			String sql = "select rownum,t.* from business_index t where t.sjdwdm = '" + deptid + "' and rownum<" + NUM;
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			// _log.info("onQueryProgress："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	// 舟山通知公告等小模块
	public DataSet getContentInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");
			String picModel = (String) pSet.get("picModel");
			String str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			if ("1".equals(open)) {
				str = "and (t.rid = '" + rid + "'"
						+ " or t.rid like '330901000000%') and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid + "' or s.rid like '330901000000%') and s.name = '" + name + "')";
			}
			String sql = "select to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by ctime desc";
			if (StringUtils.isNotEmpty(picModel)) {
				pSet.remove("PicModel");
				// sqlBuilder.append(" and attach.docid !=' ' and
				// attach.docid!='null' ");
				sql = "select to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' and attach.docid !=' ' and attach.docid!='null' where t.submit_status='1' and t.status='1' and attach.docid is not null and t.checks='1' "
						+ str + " order by ctime desc";

			}

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

	public DataSet getContentDetail(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		JSONArray upfileAttach = new JSONArray();
		String id = (String) pSet.getParameter("id");
		try {
			String sql = "select t.*,to_char( t.ctime ,'YYYY-MM-DD') CT from pub_content t where id = '" + id + "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			// _log.info("getContentDetail："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		try {
			String sql = "select * from attach t where (type='2' or type ='3') and  conid = '" + id + "'";
			dsAttach = this.executeQuery(sql);
			int totla = dsAttach.getTotal();
			if (totla > 0) {
				// 这里加入下载代码
				JSONArray upfile = dsAttach.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i)).get("NAME");
					String fileType = (String) ((JSONObject) upfile.get(i)).get("TYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i)).get("DOCID");
					if (fileType.equals("2")) {
						upfileAttach.add(upfile.get(i));
					}
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}

			JSONObject rawData = ds.getJOData();
			rawData.element("upfiles", upfileAttach);
			ds.setRawData(rawData);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getChannel(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String pname = (String) pSet.getParameter("pname");
			String rid = (String) pSet.getParameter("rid");
			String sql = "select * from pub_channel where parent=(select id from pub_channel where NAME = '" + pname
					+ "' and rid = '" + rid + "' and type='0') order by corder";
			ds = this.executeDataset(sql);

		} catch (Exception e) {
			// _log.info("getChannel："+e.toString());
			ds.setState(StateType.FAILT);
			ds.setMessage("初始化失败...");
		}
		return ds;
	}

	public DataSet getList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append(
					"select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.agent_code,content.agent_name,content.creator_id,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder.append(
					" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.TYPE as pictype,attach.DOCID,attach.CONID ");
			sqlBuilder.append(
					" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1' and content.submit_status = '1' ");
			String picModel = (String) pSet.get("PicModel");
			if (StringUtils.isNotEmpty(picModel)) {
				pSet.remove("PicModel");
				sqlBuilder.append(" and attach.docid !=' ' and attach.docid!='null' ");
			}
			String rid = (String) pSet.remove("content.rid@=");
			String rida = (String) pSet.remove("rida");
			if (null != rid || null != rida) {
				sqlBuilder.append(" and (content.rid ='" + rid + "'");
				sqlBuilder.append(" or content.rid ='330901000000'");// 市级内容也在县区显示
				sqlBuilder.append(") ");
			}
			String sql = SqlCreator.getSimpleQuerySql(pSet, sqlBuilder.toString(), this.getDataSource());
			//按最近时间顺序排列
			sql+=" order by ctime DESC";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
			if (StringUtils.isNotEmpty(picModel)) {
				// 这里加入下载代码
				JSONArray upfile = ds.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i)).get("PICNAME");
					String fileType = (String) ((JSONObject) upfile.get(i)).get("PICTYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i)).get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	// 部门id查询通知公告
	public DataSet getNticeList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append(
					"select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_id,content.dept_name,content.agent_code,content.agent_name,content.creator_id,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder.append(
					" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.TYPE as pictype,attach.DOCID,attach.CONID ");
			sqlBuilder.append(
					" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1' and content.submit_status = '1' ");
			String picModel = (String) pSet.get("PicModel");
			if (StringUtils.isNotEmpty(picModel)) {
				pSet.remove("PicModel");
				sqlBuilder.append(" and attach.docid !=' ' and attach.docid!='null' ");
			}

			// 添加查询条件
			String startTime = (String) pSet.remove("startTime");
			String endTime = (String) pSet.remove("endTime");
			String keyWord = (String) pSet.remove("keyWord");
			String dept = (String) pSet.remove("dept");
			String searchCon = "";
			if (StringUtils.isNotBlank(startTime)) {
				searchCon += " and content.ctime >= to_date('" + startTime + "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(endTime)) {
				searchCon += " and content.ctime <= to_date('" + endTime + "','yyyy-mm-dd') ";
			}
			if (StringUtils.isNotBlank(keyWord)) {
				searchCon += " and content.name like '%" + keyWord + "%' ";
			}
			if (StringUtils.isNotBlank(dept)) {
				searchCon += " and content.dept_id = '" + dept + "' ";
			}

			sqlBuilder.append(searchCon);

			String rid = (String) pSet.remove("content.rid@=");
			String rida = (String) pSet.remove("rida");
			if (null != rid || null != rida) {
				sqlBuilder.append(" and (content.rid ='" + rid + "'");
				sqlBuilder.append(") ");
			}

			String sql = SqlCreator.getSimpleQuerySql(pSet, sqlBuilder.toString(), this.getDataSource());

			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}
			if (StringUtils.isNotEmpty(picModel)) {
				// 这里加入下载代码
				JSONArray upfile = ds.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					// ---下载文件到项目路径本地 start
					String fileName = (String) ((JSONObject) upfile.get(i)).get("PICNAME");
					String fileType = (String) ((JSONObject) upfile.get(i)).get("PICTYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i)).get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet BusinessSearchQuery(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getDisplayListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String page = pSet.getPageStart() + "";
			String rows = pSet.getPageLimit() + "";

			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			String keyword = (String) pSet.remove("keyword");
			if (StringUtils.isNotEmpty(keyword)) {
				whereValue.append(
						" and region_code=? and ( ORG_NAME like ? or APPLY_SUBJECT like ? or APPLICANT like ?)");
				paramValue.add(SecurityConfig.getString("WebRegion"));
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
				paramValue.add("%" + keyword + "%");
			} else {
				whereValue.append(" and region_code=?");
				paramValue.add(SecurityConfig.getString("WebRegion"));
			}
			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());

			Object ret = RestUtil.postData(url, map);
			JSONObject json = JSONObject.fromObject(ret);
			JSONArray data = json.getJSONArray("pageList");
			ds.setTotal(json.getInt("totalPage"));
			ds.setRawData(data);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}

		return ds;
	}

	public DataSet search(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append(
					"select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.rid_name,content.id,content.cid,content.rid,content.name,content.content,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder.append(
					" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.DOCID,attach.CONID ");
			sqlBuilder.append(
					" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1'");

			String name = (String) pSet.remove("name");
			String rid = (String) pSet.remove("rid");
			sqlBuilder.append(" and content.name like '" + "%" + name + "%" + "'");
			sqlBuilder.append(" and channel.rid = '" + rid + "'");
			sqlBuilder.append(
					" and (channel.name = '动态要闻' or channel.name = '改革创新' or channel.name = '专题专栏' or channel.name = '通知' or channel.name = '公告' or channel.name = '公示' or channel.name = '简报' or channel.name = '通报' or channel.name = '调查研究' or channel.name = '数据分析' or channel.name = '工作交流' or channel.name = '外地经验' or channel.name = '业务处室动态信息' or channel.name = '窗口动态')");

			String sql = SqlCreator.getSimpleQuerySql(pSet, sqlBuilder.toString(), this.getDataSource());

			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getChannelName(ParameterSet pSet) {
		String rid = SecurityConfig.getString("WebRegion");
		String name = (String) pSet.getParameter("cname");
		DataSet ds = new DataSet();
		try {
			String sql = "select id, name from PUB_CHANNEL where rid='" + rid + "' and name = '" + name + "'";
			ds = this.executeDataset(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;

	}

	/**
	 * 分页获取目录信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getFolderInfoByPage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getFolderListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String page = "1", rows = "6";
			if (pSet.getParameter("page") != null)
				page = pSet.getParameter("page").toString();
			if (pSet.getParameter("rows") != null)
				rows = pSet.getParameter("rows").toString();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));
			String org_code = (String) pSet.getParameter("org_code");
			if (StringUtils.isNotEmpty(org_code)) {
				whereValue.append(" and ORG_CODE=? ");
				paramValue.add(org_code);
			}
			String type = (String) pSet.getParameter("type");
			if (StringUtils.isNotEmpty(type)) {
				whereValue.append(" and TYPE=? ");
				paramValue.add(type);
			}

			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			String pageList = obj.getString("pageList");
			ds.setRawData(pageList);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}

	/**
	 * 获取中介机构事项
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getConvenienceItemList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			/*
			 * whereValue.append(" and TYPE!=? "); paramValue.add("CF");
			 * whereValue.append(" and TYPE!=? "); paramValue.add("SP");
			 * whereValue.append(" and TYPE!=? "); paramValue.add("ZJJG");
			 */
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			}
			if (("dept").equals(cat)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);
			} else if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (!"".equals(SearchName) && SearchName != null) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}
			// 查询类型
			whereValue.append(" and TYPE= ?");
			paramValue.add("ZJJG");

			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));

			map.put("page", pSet.getParameter("page").toString());
			map.put("rows", pSet.getParameter("limit").toString());
			// map.put("orgCode", "37011400000003");
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 政策法规
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getPoliciList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");
			String str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			if ("1".equals(open)) {
				str = "and (t.rid = '" + rid + "'" + ") and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid + "') and s.name = '" + name + "')";
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
				searchCon += " and t.cTime <= to_date('" + endTime + "','yyyy-mm-dd')+1 ";
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
	/**
	 * 获取街道社区简介
	 * @param pSet
	 * @return
	 */
	public DataSet getTownSummary(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		try {
			String name = (String) pSet.get("name");
			String contentName = (String) pSet.get("contentName");
			String str = "and cid in (select id from pub_channel s  where s.name = '" + name + "') and t.name ='"+contentName+"'";
			String sql = "select to_char(t.ctime,'yyyy-mm-dd') as ctime,t.reason,t.source,t.dept_name,t.rid_name,t.id,t.cid,t.rid,t.name,t.content,t.checks,t.url,t.creator,t.blank,t.summary,t.type,t.remark,t.status,t.corder,t.attach,attach.DOCID from pub_content t left join ATTACH attach on t.id = attach.CONID and attach.type='1' where t.submit_status='1' and t.status='1' and t.checks='1' "
					+ str + " order by ctime desc";
				ds = this.executeDataset(sql);
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
	/**
	 * 获取社区 街道主题
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getTheme(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemTitleByRegionAndType");
			Map<String, String> map=new HashMap<String, String>();
			String region = (String) pSet.getParameter("region");
			String type = (String) pSet.getParameter("type");
			log.error("主题测试："+url);
			JSONObject obj =  JSONObject.fromObject(RestUtil.getData(url+"?regionCode="+region+"&itemType="+type));
			log.error("返回值："+obj);
			ds.setRawData(obj.get("data"));
		} catch (Exception e) {
			log.error(e.getMessage());
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		return ds;
	}

	/**
	 * 获取结果材料
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getResultMaterial(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String itmeId = (String) pSet.get("itemId");
			String sblsh = (String) pSet.get("sblsh");
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/getMaterial?itemId=" + itmeId
					+ "&receiveNumber=" + sblsh);
			HttpClientUtil client = new HttpClientUtil();
			Object ret = client.getResult(url, "");
			JSONArray obj;
			JSONArray __obj = new JSONArray();
			obj = JSONObject.fromObject(ret).getJSONArray("data");
			int len = obj.size();
			int _len = 0;
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					if ("Finish".equals(obj.getJSONObject(i).getString("BUSINESS_TYPE"))) {
						__obj.add(obj.get(i));
						_len++;
					}
				}
				ds.setRawData(__obj);
				ds.setState(StateType.SUCCESS);
				ds.setTotal(_len);
			} else {
				ds.setState(StateType.SUCCESS);
				ds.setTotal(0);
			}
		} catch (Exception e) {
			ds.setTotal(0);
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}

	/**
	 * 行政授权
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getAuthority(ParameterSet pSet) {
		DataSet ds = new DataSet();
		DataSet dsAttach;
		// JSONArray upfileAttach=new JSONArray();
		try {
			String rid = SecurityConfig.getString("WebRegion");
			String name = (String) pSet.get("name");
			String open = (String) pSet.get("open");
			String str = "and t.rid = '" + rid + "'" + " and cid in (select id from pub_channel s  where s.rid = '"
					+ rid + "' and s.name = '" + name + "')";
			if ("1".equals(open)) {
				str = "and (t.rid = '" + rid + "'" + ") and cid in (select id from pub_channel s  where (s.rid = '"
						+ rid + "') and s.name = '" + name + "')";
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
