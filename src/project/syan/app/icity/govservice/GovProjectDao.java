package syan.app.icity.govservice;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

import app.icity.sync.UploadUtil;
import app.util.RestUtil;
import cn.org.bjca.client.exceptions.ParameterInvalidException;
import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;

import syan.app.icity.common.ParameterSetExtension;
import syan.app.icity.common.URLServers;

public class GovProjectDao extends BaseJdbcDao {
	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao _instance = null;

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
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
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("ItemSynchronizationUrl")
					+ "/getFolderListByPage");
			Map<String, String> map = new HashMap<String, String>();
			String page = "1", rows = "10000";
			if (pSet.getParameter("page") != null)
				page = pSet.getParameter("page").toString();
			// if(pSet.getParameter("rows")!=null)
			// rows = pSet.getParameter("rows").toString();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));
			/*
			 * String org_code = (String) pSet.getParameter("org_code");
			 * if(StringUtils.isNotEmpty(org_code)){
			 * whereValue.append(" and ORG_CODE=? "); paramValue.add(org_code);
			 * }
			 */
			String type = (String) pSet.getParameter("type");
			if (StringUtils.isNotEmpty(type)) {
				whereValue.append(" and TYPE = ? ");
				paramValue.add(type);
			}

			map.put("page", page);
			map.put("rows", rows);
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray data = new JSONArray();
			JSONObject mlist;
			int total = 0;
			for (int i = 0; i < pageList.size(); i++) {
				mlist = new JSONObject();
				mlist.put("columns",
						pageList.getJSONObject(i).getJSONObject("columns"));
				ParameterSet _pSet = new ParameterSet();
				_pSet.put("FOLDER_CODE", pageList.getJSONObject(i)
						.getJSONObject("columns").getString("CODE"));
				_pSet.put("SearchName",
						(String) pSet.getParameter("SearchName"));
				JSONArray ItemList = getMattersList2(_pSet);

				mlist.put("datalist", ItemList);
				if (ItemList.size() > 0) {
					data.add(mlist);
					total++;
				}
			}
			ds.setRawData(data);
			ds.setTotal(total);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询失败！");
		}
		return ds;
	}

	/**
	 * 分页获取目录信息
	 * 
	 * @param pSet
	 * @return
	 * @throws Exception
	 */
	public JSONObject getFolderInfoByPageWithNoLogic(ParameterSet pSet)
			throws Exception {
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl") + "/getFolderListByPage");
		Map<String, String> map = new HashMap<String, String>();
		String page = "1", rows = "10000";
		if (pSet.getParameter("page") != null)
			page = pSet.getParameter("page").toString();
		if (pSet.getParameter("rows") != null)
			rows = pSet.getParameter("rows").toString();

		StringBuilder whereValue = new StringBuilder();
		StringBuilder paramValue = new StringBuilder();
		paramValue.append("[");

		whereValue.append(" and REGION_CODE = ? ");
		paramValue.append("\"" + SecurityConfig.getString("WebRegion") + "\"");

		String type = (String) pSet.getParameter("type");
		if (StringUtils.isNotEmpty(type)) {
			whereValue.append(" and TYPE = ? ");
			paramValue.append(",\"" + type + "\"");
		}

		whereValue.append(" and NAME like ? ");
		paramValue.append(",\"%" + (String) pSet.getParameter("SearchName")
				+ "%\"");

		paramValue.append("]");

		map.put("page", page);
		map.put("rows", rows);
		map.put("whereValue", whereValue.toString());
		map.put("paramValue", paramValue.toString());
		JSONObject JsonObj = JSONObject.fromObject(RestUtil.postData(url, map));
		return JsonObj;
	}

	/**
	 * 获取事项列表
	 * 
	 * @param pSet
	 * @return
	 * @throws Exception 
	 */
	public JSONArray getItemList(ParameterSet pSet)
			throws Exception {
		if (String.valueOf(pSet.getParameter("org_code")).equals("null")) {
			throw new ParameterInvalidException("单位编码为空");
		}
		String OrgCode = String.valueOf(pSet.getParameter("org_code"));
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl")
				+ "/getItemList?orgCode="
				+ OrgCode);
		JSONArray JsonArr = JSONArray.fromObject(RestUtil.getData(url));

		return JsonArr;
	}

	/**
	 * 分页获取事项信息
	 * 
	 * @param pSet
	 * @return
	 * @throws Exception 
	 */
	public JSONObject getItemListByPage(ParameterSet pSet)
			throws Exception {
		String OrgCode = String.valueOf(pSet.getParameter("org_code")).equals(
				"null") ? "" : String.valueOf(pSet.getParameter("org_code"));
		String SearchName = String.valueOf(pSet.getParameter("SearchName"))
				.equals("null") ? "" : String.valueOf(pSet
				.getParameter("SearchName"));
		String TitleName = String.valueOf(pSet.getParameter("TitleName"))
				.equals("null") ? "" : String.valueOf(pSet
				.getParameter("TitleName"));
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl") + "/getItemListByPage");
		Map<String, String> map=new HashMap<String, String>();
		map.put("page",
				String.valueOf(pSet.getParameter("page")));
		map.put("rows",
				String.valueOf(pSet.getParameter("rows")));
		map.put("orgCode", OrgCode);
		StringBuilder whereValue = new StringBuilder();
		whereValue.append(" and NAME like ? ");
		whereValue.append(" and TITLE_NAME like ? ");
		map.put("whereValue", whereValue.toString());
		StringBuilder paramValue = new StringBuilder();
		paramValue.append("[");
		paramValue.append("\"%" + SearchName + "%\"");
		paramValue.append(",\"%" + TitleName + "%\"");
		paramValue.append("]");
		map.put("paramValue", paramValue.toString());
		JSONObject JsonObj = JSONObject.fromObject(RestUtil.postData(url,map));

		return JsonObj;
	}

	/**
	 * 获取事项
	 * 
	 * @param pSet
	 * @return
	 */
	public JSONArray getMattersList2(ParameterSet pSet) {
		JSONArray rows = new JSONArray();
		try {
			String url = HttpUtil
					.formatUrl(SecurityConfig
							.getString("ItemSynchronizationUrl")
							+ "/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String SearchName = (String) pSet.getParameter("SearchName");
			String FOLDER_CODE = (String) pSet.getParameter("FOLDER_CODE");
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();

			if (!"".equals(SearchName)) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}

			// 查询类型
			if (!"".equals(FOLDER_CODE) && FOLDER_CODE != null) {
				whereValue.append(" and FOLDER_CODE= ?");
				paramValue.add(FOLDER_CODE);
			}
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));

			map.put("page", "1");
			map.put("rows", "1000");
			// map.put("orgCode", "37011400000003");
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");

			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column;
				column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
		} catch (Exception e) {

		}
		return rows;
	}

	public DataSet getListNew(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select  to_char(content.ctime,'yyyy-mm-dd') as ctime,content.reason,content.source,content.dept_name,content.agent_code,content.agent_name,content.creator_id,content.rid_name,content.id,content.cid,content.rid,content.name,content.checks,content.url,content.creator,content.blank,content.summary,content.type,content.remark,content.status,content.corder,content.attach,channel.name as cname");
			sqlBuilder
					.append(" ,content.VALID_DATE_START,content.VALID_DATE_END,content.submit_status,attach.NAME as picname,attach.TYPE as pictype,attach.DOCID,attach.CONID ");
			if ((Boolean) pSet.get("ReadContent")) {
				sqlBuilder.append(" ,content.content ");
			}
			pSet.remove("ReadContent");
			sqlBuilder
					.append(" from pub_content content left join pub_channel channel on channel.id = content.cid and channel.rid = content.rid left join ATTACH attach on attach.CONID = content.id  and attach.TYPE ='1'  where content.status = '1' and content.submit_status = '1' ");
			String picModel = (String) pSet.get("PicModel");
			if (StringUtils.isNotEmpty(picModel)) {
				pSet.remove("PicModel");
				sqlBuilder
						.append(" and attach.docid !=' ' and attach.docid!='null' ");
			}
			String rid = (String) pSet.remove("content.rid@=");
			// 如果是工作时间栏目，只取本级的数据
			String workTime = (String) pSet.remove("workTime");
			String isDtyw = (String) pSet.get("isDtyw");
			pSet.remove("isDtyw");
			if (!"1".equals(workTime)) {
				if ("1".equals(isDtyw)) {
					sqlBuilder.append(" and (content.rid !='" + rid + "'");
					sqlBuilder.append(" or content.rid !='330901000000') ");
				} else {
					sqlBuilder.append(" and (content.rid ='" + rid + "'");
					sqlBuilder.append(" or content.rid ='330901000000') ");// 市级内容也在县区显示
				}
			} else {
				sqlBuilder.append(" and (content.rid !='" + rid + "') ");
			}
			String sql = SqlCreator.getSimpleQuerySql(pSet,
					sqlBuilder.toString(), this.getDataSource());

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
					String fileName = (String) ((JSONObject) upfile.get(i))
							.get("PICNAME");
					String fileType = (String) ((JSONObject) upfile.get(i))
							.get("PICTYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i))
							.get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public JSONObject getProjectTitle(ParameterSet pSet) throws Exception {
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl") + "/getProjectTitle");
		JSONObject rtnJsonObj = JSONObject.fromObject(RestUtil.getData(url));
		return rtnJsonObj;
	}

	
	public JSONObject getAppointmentDepts(ParameterSet pSet) throws Exception {

		String url =  HttpUtil.formatUrl(SecurityConfig
				.getString("synchronousDept4dt"));
		String param = "{\"Service\":\"Queue.GetBizDepts\",\"Reserve\":true}";

		String rtnStr = URLServers.getInstance().callURLPost(url, param);
		JSONObject rtnJsonObj = JSONObject.fromObject(rtnStr);
		
		if((Integer)rtnJsonObj.get("Succ") == 0){
			throw new Exception("返回值为空！");
		}
		return rtnJsonObj;
	}
	
	public JSONObject getAppointmentItems(ParameterSet pSet) throws Exception {
		
		String DeptName = (String) pSet.getParameter("org_name");
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("synchronousDept4dt"));
		String param = "{\"Service\":\"Reserve.ListBusiness\",\"DeptName\":\"" + DeptName + "\"}";
		System.out.println(param);
		String rtnStr = URLServers.getInstance().callURLPost(url, param);
		JSONObject rtnJsonObj = JSONObject.fromObject(rtnStr);
		
		if((Integer)rtnJsonObj.get("Succ") == 0){
			throw new Exception("返回值为空！");
		}
		return rtnJsonObj;
	}
	
	public JSONObject insertAppointmentRecord(ParameterSet pSet) throws Exception {

		ParameterSetExtension PSE = ParameterSetExtension.getInstance();

		String BizID = PSE.get(pSet, "BIZID");
		String SelectDate = PSE.get(pSet, "SELECTDATE");
		String SelectTime = PSE.get(pSet, "SELECTTIME");
		String IdCard = PSE.get(pSet, "IDCARD");
		String WeChat = PSE.get(pSet, "WECHAT");
		String Phone = PSE.get(pSet, "PHONE");
		String url = HttpUtil.formatUrl(SecurityConfig.getString("synchronousDept4dt"));
		String param = "{\"Service\":\"Reserve.AddRecord\""
				+ ",\"BizID\":\"" + BizID + "\""
				+ ",\"Date\":\"" + SelectDate + "\""
				+ ",\"Time\":\"" + SelectTime + "\""
				+ ",\"IDCard\":\"" + IdCard + "\""
				+ ",\"Phone\":\"" + Phone + "\""
				+ "}";
		String rtnStr = URLServers.getInstance().callURLPost(url, param);
		JSONObject rtnJsonObj = JSONObject.fromObject(rtnStr);
		
		return rtnJsonObj;
	}
	
}