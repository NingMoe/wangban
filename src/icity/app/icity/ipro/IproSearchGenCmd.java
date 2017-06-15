package app.icity.ipro;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;

import app.icity.search.engine.SearchEngine;
import app.icity.search.engine.impl.SearchEngineImpl;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class IproSearchGenCmd extends BaseQueryCommand {
	private static Log logger = LogFactory.getLog(IproSearchGenCmd.class);
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
		int page=1;
		String indexType = "project";
		DataSet ds=new DataSet();
		JSONArray rows=new JSONArray();
		try {
			try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemListByPage");
			Map<String, String> data=new HashMap<String, String>();
			data.put("page",page+"");
			data.put("rows","200");
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, data));
				JSONArray pageList=obj.getJSONArray("pageList");	
				for(int i=0;i<pageList.size();i++){
					JSONObject column=new JSONObject();
					column=(JSONObject) pageList.get(i);
					JSONObject o=column.getJSONObject("columns");
					o.put("GID", Tools.getUUID32());
					o.put("TYPE", indexType);
					rows.add(o);
				}
				//取总行数
				Integer totalRow=Integer.parseInt(obj.getString("totlaRow"));
				while((totalRow-200*page)>0){
					page=page+1;
					data.put("page",page+"");
					data.put("rows","200");
					RestUtil.postData(url, data);
					for(int i=0;i<pageList.size();i++){
						JSONObject column;
						column=(JSONObject) pageList.get(i);
						JSONObject o=column.getJSONObject("columns");
						o.put("GID", Tools.getUUID32());
						o.put("TYPE", indexType);
						rows.add(o);
					}
				}
				}
			 catch(Exception e){
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("查询失败！");
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
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		System.out.println(ds.toString());
		return ds;
	}
	
	/**
	 * 目录索引
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet genCatalog(ParameterSet pSet) {
		
		String indexType = "catalog";
		DataSet reDs = new DataSet();
		JSONArray rows=new JSONArray();
		try {
			try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ipro_url")+"/getInvestDictList");
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.getData(url));
				
				
				JSONArray xmhyflml=obj.getJSONArray("XMHYFLML");
				for(int i=0;i<xmhyflml.size();i++){
					JSONObject jo;
					jo=(JSONObject) xmhyflml.get(i);
					jo.put("GID", Tools.getUUID32());
					jo.put("TYPE", indexType);
					rows.add(jo);
				}
			}
			 catch(Exception e){
					e.printStackTrace();
					reDs.setState(StateType.FAILT);
					reDs.setMessage("查询失败！");
				}
			reDs.setTotal(rows.size());
				// 调用搜索引擎创建索引
				Term delTerm = new Term("TYPE", indexType);
				engine.clearIndex(index_path, delTerm);
				engine.createIndex(rows);
				if (!engine.writeIndex(index_path, indexType)) {
					reDs.setState(StateType.FAILT);
					reDs.setMessage("事项索引初始化失败！");
				}
			} catch (Exception e) {
				e.printStackTrace();
				reDs.setState(StateType.FAILT);
				reDs.setMessage(e.getMessage());
			}
		System.out.println(reDs.toString());
			return reDs;
		}
}
