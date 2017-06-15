package bl.app.icity.govservice;

import java.util.HashMap;
import java.util.Map;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GovProjectDao extends BaseJdbcDao{ 
	   private GovProjectDao(){
		   this.setDataSourceName("icityDataSource");
	   }		
		public static GovProjectDao _instance=null;

		public static GovProjectDao getInstance(){
			return DaoFactory.getDao(GovProjectDao.class.getName());
		}
		/**
		 * 获取FolderListByPage
		 * @param pSet
		 * @return
		 */
		public DataSet getFolderListByPage(ParameterSet pSet){
			DataSet ds=new DataSet();
			try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getFolderListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String SUIT_ONLINE = (String)pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String)pSet.getParameter("SearchName");
			String itemType = (String)pSet.getParameter("TYPE");			
			
			
			StringBuilder whereValue=new StringBuilder();
			JSONArray paramValue=new JSONArray();
			whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("SP");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("ZJJG");
			/*if(pagemodel.equals("person")){
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			}else if(pagemodel.equals("ent")){
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			}		*/
			if(cat.equals("dept")){
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);			
			}
			if("1".equals(SUIT_ONLINE)){
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if(!"".equals(SearchName)){
				whereValue.append(" and name like ? ");
				paramValue.add("%"+SearchName+"%");
			}
			//查询类型
			if(!"".equals(itemType)&&itemType!=null){
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));
			
			map.put("page",pSet.getParameter("page").toString());
			map.put("rows",pSet.getParameter("limit").toString());
			//map.put("orgCode", "37011400000003");
			map.put("whereValue", whereValue.toString());
			map.put("paramValue",paramValue.toString());
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, map));
				JSONArray pageList=obj.getJSONArray("pageList");
				JSONArray rows=new JSONArray();
				for(int i=0;i<pageList.size();i++){
					JSONObject column;
					column=(JSONObject) pageList.get(i);
					rows.add(column.get("columns"));
				}
				ds.setRawData(rows);
				ds.setTotal(obj.getInt("totlaRow"));
				}
			 catch(Exception e){
				ds.setState(StateType.FAILT);
				ds.setMessage("查询失败！");
			}			
			return ds;
		}
		
		
		
		
		/**
		 * 获取事项
		 * @param pSet
		 * @return
		 */
		public DataSet getMattersList2(ParameterSet pSet){
			DataSet ds=new DataSet();
			try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemListByPage");
			Map<String, String> map=new HashMap<String, String>();
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			String SUIT_ONLINE = (String)pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String)pSet.getParameter("SearchName");
			String itemType = (String)pSet.getParameter("TYPE");
			
			String FOLDER_CODE = (String)pSet.getParameter("FOLDER_CODE");
			
			StringBuilder whereValue=new StringBuilder();
			JSONArray paramValue=new JSONArray();
			whereValue.append(" and TYPE!=? ");
			paramValue.add("CF");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("SP");
			whereValue.append(" and TYPE!=? ");
			paramValue.add("ZJJG");
			if(pagemodel.equals("person")){
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			}else if(pagemodel.equals("ent")){
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			}		
			if(cat.equals("dept")){
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);			
			}else if(cat.equals("theme")){
				whereValue.append(" and title_name like ? ");
				paramValue.add("%"+classtype+"%");
			}
			if("1".equals(SUIT_ONLINE)){
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if(!"".equals(SearchName)){
				whereValue.append(" and name like ? ");
				paramValue.add("%"+SearchName+"%");
			}
			//查询类型
			if(!"".equals(itemType)&&itemType!=null){
				whereValue.append(" and TYPE= ?");
				paramValue.add(itemType);
			}
			//查询类型
			if(!"".equals(FOLDER_CODE)&&FOLDER_CODE!=null){
				whereValue.append(" and FOLDER_CODE= ?");
				paramValue.add(FOLDER_CODE);
			}
			whereValue.append(" and REGION_CODE= ? ");
			paramValue.add(SecurityConfig.getString("WebRegion"));
			
			map.put("page",pSet.getParameter("page").toString());
			map.put("rows",pSet.getParameter("limit").toString());
			//map.put("orgCode", "37011400000003");
			map.put("whereValue", whereValue.toString());
			map.put("paramValue",paramValue.toString());
				JSONObject obj;		
				obj =  JSONObject.fromObject(RestUtil.postData(url, map));
				JSONArray pageList=obj.getJSONArray("pageList");
				JSONArray rows=new JSONArray();
				for(int i=0;i<pageList.size();i++){
					JSONObject column;
					column=(JSONObject) pageList.get(i);
					rows.add(column.get("columns"));
				}
				ds.setRawData(rows);
				ds.setTotal(obj.getInt("totlaRow"));
				}
			 catch(Exception e){
				ds.setState(StateType.FAILT);
				ds.setMessage("查询失败！");
			}			
			return ds;
		}
		
		/**
		 * 根据事项ID分类获取事项相关信息
		 * @param pSet
		 * @return
		 */
		public DataSet getAllItemInfoByItemID(ParameterSet pSet){
			DataSet ds=new DataSet();
			try {
	 			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getAllItemInfoByItemID");
	 			Map<String, String> map=new HashMap<String, String>();
				String itemid = (String) pSet.getParameter("itemid");
				map.put("itemId", itemid);
				//GetMethod postMethod = new GetMethod(url+"?itemId=DEB7A6A6DF01427094E232F5552E1CAB");
				JSONObject obj;
				obj =  JSONObject.fromObject(RestUtil.getData(url,map));
				String rows = obj.getString("material");
				ds.setRawData(rows);
			}
			catch(Exception e){
				ds.setState(StateType.FAILT);
				ds.setMessage("查询失败！");
			}			
			return ds;
		}		
}


