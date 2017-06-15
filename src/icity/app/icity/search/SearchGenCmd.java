package app.icity.search;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;

import app.icity.search.engine.SearchEngine;
import app.icity.search.engine.impl.SearchEngineImpl;
import app.util.CommonUtils_api;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

public class SearchGenCmd extends BaseQueryCommand {
	private static Log logger = LogFactory.getLog(SearchGenCmd.class);
	private static String icityDataSource = "icityDataSource";
	private static String index_path="solr";
	private static SearchEngine engine = new SearchEngineImpl();// 调用接口方法实现创建索引
	/**
	 * 事项索引
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet genProject(ParameterSet pSet) {
		String indexType = "project";
		DataSet ds=new DataSet();
		JSONArray jsons=new JSONArray();
		if("chq".equals(SecurityConfig.getString("AppId"))||"zs".equals(SecurityConfig.getString("AppId"))){
			for(int i=1;i<=15;i++){
				pSet.put("page", i);
				JSONArray json;
				json=genProject4cq(pSet);
			    for(int j=0;j<json.size();j++){
			    	jsons.add(json.get(j));
			    }
			}
			ds.setTotal(jsons.size());
			// 调用搜索引擎创建索引
			Term delTerm = new Term("TYPE", indexType);
			engine.clearIndex(index_path, delTerm);
			engine.createIndex(jsons);
			if (!engine.writeIndex(index_path, indexType)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("事项索引初始化失败！");
			}
			return ds;
		}
		JSONArray rows=new JSONArray();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemListByPage");
			Map<String, String> data=new HashMap<String, String>();
			StringBuilder whereValue=new StringBuilder();
			JSONArray paramValue=new JSONArray();
			if("xy".equals(SecurityConfig.getString("AppId"))){
				whereValue.append(" and REGION_CODE= ? ");
				paramValue.add(SecurityConfig.getString("WebRegion"));		
				data.put("whereValue", whereValue.toString());
				data.put("paramValue",paramValue.toString());
			}			
			data.put("page","1");
				data.put("rows","1000000");
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, data));
				JSONArray pageList=obj.getJSONArray("pageList");
				for(int i=0;i<pageList.size();i++){
					try {
						JSONObject column;
						column=(JSONObject) pageList.get(i);
						JSONObject o=column.getJSONObject("columns");
						o.put("GID", Tools.getUUID32());
						o.put("POWER_TYPE", o.get("TYPE"));//权力类型
						o.put("THEME", o.get("TITLE_NAME"));//主题
						o.put("TYPE", indexType);//索引类型
						o.put("WEBSITETYPE", "0");
						if(o.containsKey("LAST_TIME")){
							long time=o.getLong("LAST_TIME");
							Date date = new Date(time);
							CommonUtils_api _commonUtils_api=CommonUtils_api.getInstance();
							o.put("TIME", _commonUtils_api.parseDateToString(date, "yyyy-MM-dd"));
							o.put("YEAR", _commonUtils_api.parseDateToString(date, "yyyy"));
							o.put("MONTH", _commonUtils_api.parseDateToString(date, "yyyyMM"));
							o.put("DAY", _commonUtils_api.parseDateToString(date, "yyyyMMdd"));
							}
						rows.add(o);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			ds.setTotal(rows.size());
			// 调用搜索引擎创建索引
			Term delTerm = new Term("TYPE", indexType);
			engine.clearIndex(index_path, delTerm);
			engine.createIndex(rows);	
			if (!engine.writeIndex(index_path, indexType)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("事项索引初始化失败！");
			}
		}	 catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}
	
	/**
	 * 重庆搜索
	 */
	public JSONArray genProject4cq(ParameterSet pSet) {
		String indexType = "project";
		JSONArray rows=new JSONArray();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemListByPage");
			Map<String, String> data=new HashMap<String, String>();
			data.put("page",pSet.get("page").toString());
				data.put("rows","5000");
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, data));
				JSONArray pageList=obj.getJSONArray("pageList");
				for(int i=0;i<pageList.size();i++){
					try {
						JSONObject column;
						column=(JSONObject) pageList.get(i);
						JSONObject o=column.getJSONObject("columns");
						o.put("GID", Tools.getUUID32());
						o.put("POWER_TYPE", o.get("TYPE"));//权力类型
						o.put("THEME", o.get("TITLE_NAME"));//主题
						o.put("TYPE", indexType);//索引类型
						o.put("WEBSITETYPE", "0");
                         if(i==0){
                         }
						if(o.containsKey("LAST_TIME")){
							long time=o.getLong("LAST_TIME");
							Date date = new Date(time);
							CommonUtils_api _commonUtils_api=CommonUtils_api.getInstance();
							o.put("TIME", _commonUtils_api.parseDateToString(date, "yyyy-MM-dd"));
							o.put("YEAR", _commonUtils_api.parseDateToString(date, "yyyy"));
							o.put("MONTH", _commonUtils_api.parseDateToString(date, "yyyyMM"));
							o.put("DAY", _commonUtils_api.parseDateToString(date, "yyyyMMdd"));
							}
						rows.add(o);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}
	/**
	 * 发布内容索引
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet genContent(ParameterSet pSet) {
		DataSet reDs = new DataSet();
		String indexType = "content";
		Connection conn = null;
		try {
			DataSource dataSource = DBSource.getDataSource(icityDataSource);
			String sql="SELECT C.NAME CNAME,T.RID AS REGION_CODE,T.ID,T.NAME,T.URL,T.CTIME,T.ATTACH,C.WEBSITETYPE FROM PUB_CHANNEL C,PUB_CONTENT T where  C.ID=T.CID AND C.RID=T.RID "+ 
					" AND C.STATUS='1' AND T.STATUS='1' AND T.CHECKS='1' ";
			DataSet ds = null;
				conn = dataSource.getConnection();
				ds = DbHelper.query(sql, null, conn);
			JSONArray ja = ds.getJAData();
			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				try {
					jo.put("GID", Tools.getUUID32());
					jo.put("TYPE", indexType);
					long time=jo.getJSONObject("CTIME").getLong("time");
					Date date = new Date(time);
					CommonUtils_api _commonUtils_api=CommonUtils_api.getInstance();
					jo.put("TIME", _commonUtils_api.parseDateToString(date, "yyyy-MM-dd"));
					jo.put("YEAR", _commonUtils_api.parseDateToString(date, "yyyy"));
					jo.put("MONTH", _commonUtils_api.parseDateToString(date, "yyyyMM"));
					jo.put("DAY", _commonUtils_api.parseDateToString(date, "yyyyMMDD"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			reDs.setTotal(ja.size());

			// 调用搜索引擎创建索引
			Term delTerm = new Term("TYPE", indexType);
			engine.clearIndex(index_path, delTerm);
			engine.createIndex(ja);			
			if (!engine.writeIndex(index_path, indexType)) {
				reDs.setState(StateType.FAILT);
				reDs.setMessage("内容索引初始化失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			reDs.setState(StateType.FAILT);
			reDs.setMessage(e.getMessage());
		}
		 finally {
				closeConn(conn);
			}
		return reDs;
	}
	/**
	 * 获取要初始化的咨询信息列表 写入到索引
	 * @return
	 */
	public DataSet getConsultingList(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String indexType = "consult";
		Connection conn = DbHelper.getConnection("icityDataSource");	
		try{
			String sql = "select t.*,t.title NAME,t.region_id REGION_CODE,to_char(WRITE_DATE,'yyyy-MM-dd') TIME ,to_char(WRITE_DATE,'yyyy') YEAR,to_char(WRITE_DATE,'yyyyMM') MONTH,to_char(WRITE_DATE,'yyyyMMDD') DAY from guestbook t where INITLOG='1'";
			ds = DbHelper.query(sql, new Object[]{},conn);
			JSONArray ja = ds.getJAData();
			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				try {
					jo.put("GID", Tools.getUUID32());
					jo.put("TYPE", indexType);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ds.setTotal(ja.size());
			// 调用搜索引擎创建索引
			Term delTerm = new Term("TYPE", indexType);
			engine.clearIndex(index_path, delTerm);
			engine.createIndex(ja);			
			if (!engine.writeIndex(index_path, indexType)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("内容索引初始化失败！");
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}finally{
			if(conn != null)
				try {
					conn.setAutoCommit(true);
				}catch (SQLException e) {
					e.printStackTrace();
					ds.setState(StateType.FAILT);
				}
			DbHelper.closeConnection(conn);
		}	
		return ds;
	}
	/**
	 * 关闭数据库连接
	 * 
	 * @param conn
	 */
	private static void closeConn(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
}
