package syan.app.icity.govservice;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import syan.app.icity.common.ParameterSetExtension;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import com.icore.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.sun.star.auth.InvalidArgumentException;


public class GovProjectCmd extends app.icity.govservice.GovProjectCmd{
	

	private final static String CACHE_KEY_FLAG = "GovProjectCmd";
	//分页获取事项目录
	public DataSet getFolderInfoByPage(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getFolderInfoByPage_" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds =  syan.app.icity.govservice.GovProjectDao.getInstance().getFolderInfoByPage(pSet);
					if (ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		if(ds.getTotal()>Integer.parseInt(pSet.getParameter("rows").toString())){
			JSONArray __ja = new JSONArray();
			String page = "1", rows = "6";
			if(pSet.getParameter("page")!=null)
				page = pSet.getParameter("page").toString();
			if(pSet.getParameter("rows")!=null)
				rows = pSet.getParameter("rows").toString();
			int all = Integer.parseInt(page)*Integer.parseInt(rows);
			int index = (Integer.parseInt(page)-1)*Integer.parseInt(rows);
			if(all>ds.getTotal()){
				all = ds.getTotal();			
			}			
			for(int i=index;i<all;i++){
				__ja.add(ds.getRecord(i));
			}
			DataSet __ds = new DataSet();
			__ds.setTotal(ds.getTotal());
			__ds.setRawData(__ja);
			__ds.setState(StateType.SUCCESS);
			ds = __ds;
		}
		return ds;
	}
	//分页获取事项目录查询
	public DataSet onQuerySxcxSearch(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getFolderInfoByPage_" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds =  syan.app.icity.govservice.GovProjectDao.getInstance().getFolderInfoByPage(pSet);
					if (ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		DataSet searchds = new DataSet();
		JSONArray searchja = new JSONArray();		
		String SearchName="";
		if(pSet.getParameter("SearchName")!=null){
			SearchName = (String)pSet.getParameter("SearchName");
			for(int i=0;i<ds.getTotal();i++){
				JSONArray __searchja = new JSONArray();		
				JSONObject searchjo = new JSONObject();		

				int len = ds.getRecord(i).getJSONArray("datalist").size();
				for(int j=0;j<len;j++){
					if(ds.getRecord(i).getJSONArray("datalist").getJSONObject(j).getString("NAME").contains(SearchName)){
						__searchja.add(ds.getRecord(i).getJSONArray("datalist").getJSONObject(j));
					}
				}
				if(__searchja.size()>0){
					searchjo.put("columns", ds.getRecord(i).getJSONObject("columns"));
					searchjo.put("datalist",__searchja);
					searchja.add(searchjo);
				}
			}
		}
		searchds.setRawData(searchja);
		searchds.setTotal(searchja.size());
		if(searchds.getTotal()>Integer.parseInt(pSet.getParameter("rows").toString())){
			JSONArray __ja = new JSONArray();
			String page = "1", rows = "6";
			if(pSet.getParameter("page")!=null)
				page = pSet.getParameter("page").toString();
			if(pSet.getParameter("rows")!=null)
				rows = pSet.getParameter("rows").toString();
			
			int all = Integer.parseInt(page)*Integer.parseInt(rows);
			int index = (Integer.parseInt(page)-1)*Integer.parseInt(rows);
			if(all>searchds.getTotal()){
				all = searchds.getTotal();
			}
			for(int i=index;i<all;i++){
				__ja.add(searchds.getRecord(i));
			}
			DataSet __ds = new DataSet();
			__ds.setTotal(searchds.getTotal());
			__ds.setRawData(__ja);
			__ds.setState(StateType.SUCCESS);
			searchds = __ds;
		}
		return searchds;
	}
	
	//分页获取事项目录查询+部门
	public DataSet onQuerybsfwSearch(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getFolderInfoByPage_" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds =syan.app.icity.govservice.GovProjectDao.getInstance().getFolderInfoByPage(pSet);
					if (ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		int count=0;
		DataSet searchds = new DataSet();
		JSONArray searchja = new JSONArray();
		String SearchName="";
		String org_code = (String)pSet.getParameter("org_code");
		if(pSet.getParameter("SearchName")!=null){
			SearchName = (String)pSet.getParameter("SearchName");
			for(int i=0;i<ds.getTotal();i++){
				JSONArray __searchja = new JSONArray();
				JSONObject searchjo = new JSONObject();
				int len = ds.getRecord(i).getJSONArray("datalist").size();
				for(int j=0;j<len;j++){
					String name = ds.getRecord(i).getJSONArray("datalist").getJSONObject(j).getString("NAME");
					if((!"".equals(SearchName)&&name.contains(SearchName))||ds.getRecord(i).getJSONArray("datalist").getJSONObject(j).getString("ORG_CODE").equals(org_code)){
						__searchja.add(ds.getRecord(i).getJSONArray("datalist").getJSONObject(j));
						count++;
					}
				}
				if(__searchja.size()>0){
					searchjo.put("columns", ds.getRecord(i).getJSONObject("columns"));
					searchjo.put("datalist",__searchja);
					searchja.add(searchjo);
				}
			}
		}else{
			for(int i=0;i<ds.getTotal();i++){
				JSONArray __searchja = new JSONArray();
				JSONObject searchjo = new JSONObject();

				int len = ds.getRecord(i).getJSONArray("datalist").size();
				for(int j=0;j<len;j++){
					if(ds.getRecord(i).getJSONArray("datalist").getJSONObject(j).getString("ORG_CODE").equals(org_code)){
						__searchja.add(ds.getRecord(i).getJSONArray("datalist").getJSONObject(j));
						count++;
					}
				}
				if(__searchja.size()>0){
					searchjo.put("columns", ds.getRecord(i).getJSONObject("columns"));
					searchjo.put("datalist",__searchja);
					searchja.add(searchjo);
				}
			}
		}
		searchds.setRawData(searchja);
		searchds.setTotal(searchja.size());
		if(searchds.getTotal()>Integer.parseInt(pSet.getParameter("rows").toString())){
			JSONArray __ja = new JSONArray();
			String page = "1", rows = "6";
			if(pSet.getParameter("page")!=null)
				page = pSet.getParameter("page").toString();
			if(pSet.getParameter("rows")!=null)
				rows = pSet.getParameter("rows").toString();
			
			int all = Integer.parseInt(page)*Integer.parseInt(rows);
			int index = (Integer.parseInt(page)-1)*Integer.parseInt(rows);
			if(all>searchds.getTotal()){
				all = searchds.getTotal();
			}
			for(int i=index;i<all;i++){
				__ja.add(searchds.getRecord(i));
			}
			DataSet __ds = new DataSet();
			__ds.setRawData(__ja);
			__ds.setState(StateType.SUCCESS);
			searchds = __ds;
		}
		searchds.setTotal(count);
		return searchds;
	}

	public DataSet getFolderWithSubItemByPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String PageStr = String.valueOf(pSet.getParameter("page"));
			String RowsStr = String.valueOf(pSet.getParameter("rows"));
			if(PageStr.equals("null") || RowsStr.equals("null")){
				throw new InvalidArgumentException("参数为空");
			}
			
			String SearchName = String.valueOf(pSet.getParameter("SearchName")).equals("null") ? "" : String.valueOf(pSet.getParameter("SearchName"));
			JSONArray FolderWithSubItem  = getFolderWithSubItem(SearchName);
			JSONArray RtnFolderWithSubItem = new JSONArray();
			if(FolderWithSubItem.size() > 0){
				int Page = Integer.parseInt(PageStr);
				int Rows = Integer.parseInt(RowsStr);
				if(Page < 1 || Rows < 1 || (Page - 1) * Rows >= FolderWithSubItem.size()){
					throw new InvalidArgumentException("参数有误");
				}

				for(int i = 0; i < Rows && ((Page - 1) * Rows + i) < FolderWithSubItem.size(); i++){
					RtnFolderWithSubItem.add(FolderWithSubItem.get((Page - 1) * Rows + i));
				}
			}

			ds.setRawData(RtnFolderWithSubItem);
			ds.setTotal(FolderWithSubItem.size());
			ds.setState(StateType.SUCCESS);
		}catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}
	
	public abstract class CacheManage{
		public abstract Object GetObjVal() throws Exception;
		private String Key;
		public CacheManage(String Key){
			this.Key = Key;
		}

		public Object GetObjValByCache() throws Exception{
			return CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, this.Key);
		}
		
		public Object GetObjValByMethod() throws Exception{
			return this.GetObjVal();
		}
		
		public void BuildObjValCache() throws Exception{
			Object ObjCache = CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, this.Key);
			if (ObjCache == null) {
				synchronized (this.Key.intern()){
					ObjCache = CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, this.Key);
					if(ObjCache == null) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, this.Key, this.GetObjVal());
					}
				}
			}
		}

		public void BuildObjValMethod() throws Exception{
		}
		
		public void ClearObjValCache(){
			CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, this.Key, null);
		}
	}
	
	private class FolderCache extends CacheManage{
		private String SearchName;
		public FolderCache(String Key, String SearchName){
			super(Key);
			this.SearchName = SearchName;
		}
		public Object GetObjVal() throws Exception{
			ParameterSet pSetDao = new ParameterSet();
			pSetDao.setParameter("page", "1");
			pSetDao.setParameter("rows", "10000");
			pSetDao.setParameter("SearchName", this.SearchName);
			JSONArray FolderJsonArr = syan.app.icity.govservice.GovProjectDao.getInstance().getFolderInfoByPageWithNoLogic(pSetDao).getJSONArray("pageList");
			return FolderOrItemQuickSort(FolderJsonArr, "CODE", 0, FolderJsonArr.size() - 1);
		}
	}
	
	private class ItemCache extends CacheManage{
		public ItemCache(String Key) {
			super(Key);
		}

		public Object GetObjVal() throws Exception{
			ParameterSet pSetDao = new ParameterSet();
			pSetDao.setParameter("page", "1");
			pSetDao.setParameter("rows", "10000");
			JSONArray ItemJsonArr = syan.app.icity.govservice.GovProjectDao.getInstance().getItemListByPage(pSetDao).getJSONArray("pageList");
			return FolderOrItemQuickSort(ItemJsonArr, "FOLDER_CODE", 0, ItemJsonArr.size() - 1);
		}
	}
	
	private class FolderWithSubItemCache extends CacheManage{
		JSONArray FolderJsonArr = null, ItemJsonArr = null;
		public FolderWithSubItemCache(String Key) {
			super(Key);
		}
		
		public FolderWithSubItemCache(String Key, JSONArray FolderJsonArr, JSONArray ItemJsonArr){
			super(Key);
			this.FolderJsonArr = FolderJsonArr;
			this.ItemJsonArr = ItemJsonArr;
		}

		public FolderWithSubItemCache SetFolderJsonArr(JSONArray FolderJsonArr){
			this.FolderJsonArr = FolderJsonArr;
			return this;
		}
		
		public FolderWithSubItemCache SetItemJsonArr(JSONArray ItemJsonArr){
			this.ItemJsonArr = ItemJsonArr;
			return this;
		}

		public Object GetObjVal() throws Exception{
			if(this.FolderJsonArr == null || this.ItemJsonArr == null){
				throw new InvalidArgumentException("目录或事项数组为空");
			}
			return MergeFolderWithItem(this.FolderJsonArr, "CODE", this.ItemJsonArr, "FOLDER_CODE");
		}
	};
	
	private JSONArray getFolderOrItem(CacheManage CacheMana) throws Exception{
		Object FolderOrItem = CacheMana.GetObjValByCache();
		if(FolderOrItem == null){
			CacheMana.BuildObjValCache();
			FolderOrItem = CacheMana.GetObjValByCache();
		}
		return (JSONArray)FolderOrItem;
	}
	
	private JSONArray getFolderWithSubItem(String SearchName) throws Exception{
		Object FolderWithSubItem = null;//只缓存所有的目录和事项，有搜索条件的不缓存
		
		String WebRegion = SecurityConfig.getString("WebRegion");

		String FolderWithSubItemKey = "FolderWithSubItemCache_" + WebRegion;
		String FolderKey = "FolderCache_" + WebRegion;
		String ItemKey = "ItemCache_" + WebRegion;
		
		FolderWithSubItemCache FolderWithSubItemCacheObj = new FolderWithSubItemCache(FolderWithSubItemKey);
		FolderCache FolderCacheObj = new FolderCache(FolderKey, SearchName);
		ItemCache ItemCacheObj = new ItemCache(ItemKey);
		
		if(!StringUtils.isEmpty(SearchName)){
			JSONArray Folder = (JSONArray)FolderCacheObj.GetObjValByMethod();
			JSONArray Item = getFolderOrItem(ItemCacheObj);
			
			FolderWithSubItemCacheObj
			.SetFolderJsonArr(Folder)
			.SetItemJsonArr(Item);
			
			FolderWithSubItem = FolderWithSubItemCacheObj.GetObjValByMethod();
		}else{
			FolderWithSubItem = FolderWithSubItemCacheObj.GetObjValByCache();
			if(FolderWithSubItem == null){
				JSONArray Folder = getFolderOrItem(FolderCacheObj);
				JSONArray Item = getFolderOrItem(ItemCacheObj);
				
				FolderWithSubItemCacheObj
				.SetFolderJsonArr(Folder)
				.SetItemJsonArr(Item)
				.BuildObjValCache();
				
				FolderWithSubItem = FolderWithSubItemCacheObj.GetObjValByCache();
			}
		}
		return (JSONArray)FolderWithSubItem;
	}
	
	//快速排序
	private JSONArray FolderOrItemQuickSort(JSONArray JsonArr, String Key, int l, int r){
		if(l < r){
			int i = l, j = r;
			JSONObject FirstJsonObj = JsonArr.getJSONObject(i);
			String FirstStr = (String)FirstJsonObj.getJSONObject("columns").get(Key);
			
			String StrAtI = (String)JsonArr.getJSONObject(i).getJSONObject("columns").get(Key);
			String StrAtJ = (String)JsonArr.getJSONObject(j).getJSONObject("columns").get(Key);
			while(i < j){
				while(i < j && StrAtJ.compareTo(FirstStr) < 0){
					j--;
					StrAtJ = (String)JsonArr.getJSONObject(j).getJSONObject("columns").get(Key);
				}
				if(i < j){
					JsonArr.set(i, JsonArr.get(j));
					
					i++;
					StrAtI = (String)JsonArr.getJSONObject(i).getJSONObject("columns").get(Key);
				}
				while(i < j && StrAtI.compareTo(FirstStr) > 0){
					i++;
					StrAtI = (String)JsonArr.getJSONObject(i).getJSONObject("columns").get(Key);
				}
				if(i < j){
					JsonArr.set(j, JsonArr.get(i));
					
					j--;
					StrAtJ = (String)JsonArr.getJSONObject(j).getJSONObject("columns").get(Key);
				}
			}
			JsonArr.set(i, FirstJsonObj);

			FolderOrItemQuickSort(JsonArr, Key, l, i - 1);
			FolderOrItemQuickSort(JsonArr, Key, i + 1, r);
		}

		return JsonArr;
	}
	
	//合并目录及事项
	private JSONArray MergeFolderWithItem(JSONArray FolderJsonArr,String FolderKey, JSONArray ItemJsonArr,String ItemKey){
		JSONArray FolderWithSubItemJsonArr  = new JSONArray();
		
		int ItemIndex = 0;
		String CodeAtFolderAtI;
		String FolderCodeAtItemIndex;
		JSONObject TmpFolderObj;
		JSONArray TmpItemArr = new JSONArray();
		for(int i = 0; (i < FolderJsonArr.size() && ItemIndex < ItemJsonArr.size()); i++){
			CodeAtFolderAtI = (String)FolderJsonArr.getJSONObject(i).getJSONObject("columns").get(FolderKey);
			FolderCodeAtItemIndex = (String)ItemJsonArr.getJSONObject(ItemIndex).getJSONObject("columns").get(ItemKey);
			if(CodeAtFolderAtI.equals(FolderCodeAtItemIndex)){
				TmpFolderObj = FolderJsonArr.getJSONObject(i);
				TmpItemArr.clear();
				while(CodeAtFolderAtI.equals(FolderCodeAtItemIndex) && ItemIndex < ItemJsonArr.size()){
					TmpItemArr.add(ItemJsonArr.getJSONObject(ItemIndex).getJSONObject("columns"));

					ItemIndex = ItemIndex + 1;
					if(ItemIndex < ItemJsonArr.size()){
						FolderCodeAtItemIndex = (String)ItemJsonArr.getJSONObject(ItemIndex).getJSONObject("columns").get(ItemKey);
					}
				}
				TmpFolderObj.put("datalist", TmpItemArr);
				FolderWithSubItemJsonArr.add(TmpFolderObj);
			}else{
				while(CodeAtFolderAtI.compareTo(FolderCodeAtItemIndex) < 0){
					ItemIndex = ItemIndex + 1;
					if(ItemIndex < ItemJsonArr.size()){
						FolderCodeAtItemIndex = (String)ItemJsonArr.getJSONObject(ItemIndex).getJSONObject("columns").get(ItemKey);
					}
				}
				if(CodeAtFolderAtI.equals(FolderCodeAtItemIndex)){
					i = i - 1;
				}
			}
		}
		return FolderWithSubItemJsonArr;
	}

	
public DataSet getItemList(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			JSONArray DeptItemList = syan.app.icity.govservice.GovProjectDao.getInstance().getItemList(pSet);
			JSONArray JsonArrCol = new JSONArray();
			JSONObject JsonObjCol = new JSONObject();
			for(int i = 0; i < DeptItemList.size(); i++){
				JSONObject tmpJsonObj = (JSONObject) DeptItemList.get(i);
				String SearchName = String.valueOf(pSet.getParameter("SearchName"));
				if(!tmpJsonObj.get("NAME").toString().contains(SearchName)){
					continue;
				}
				for(Iterator<?> itr = tmpJsonObj.keys(); itr.hasNext();){
					String keyNext = (String)itr.next();
					Object ObjNext = tmpJsonObj.get(keyNext);
					String ObjTypeNext = ObjNext.getClass().getName();
					boolean ObjTypeNextIsJSONNull = "net.sf.json.JSONNull".equals(ObjTypeNext);
					boolean ObjTypeNextIsJSONObject = "net.sf.json.JSONObject".equals(ObjTypeNext);
					if((!ObjTypeNextIsJSONNull && !ObjTypeNextIsJSONObject)){
						JsonObjCol.put(keyNext, ObjNext);
					}
				}
				JsonArrCol.add(JsonObjCol);
			}
			ds.setRawData(JsonArrCol);
			ds.setTotal(JsonArrCol.size());
			ds.setState(StateType.SUCCESS);
		}catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}
	
	public DataSet getItemListByPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			JSONObject JsonObj = JSONObject.fromObject(syan.app.icity.govservice.GovProjectDao.getInstance().getItemListByPage(pSet));
			JSONArray DeptItemList =  JsonObj.getJSONArray("pageList");
			ds.setRawData(DeptItemList);
			ds.setTotal(Integer.valueOf(JsonObj.getString("totlaRow")));
			ds.setState(StateType.SUCCESS);
		}catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}

	public DataSet getListNew(ParameterSet pSet){
		return syan.app.icity.govservice.GovProjectDao.getInstance().getListNew(pSet);
	}

	//获取事项主题信息
	public DataSet getProjectTitle(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			JSONObject rtnJsonObj = syan.app.icity.govservice.GovProjectDao.getInstance().getProjectTitle(pSet);
			JSONArray JsonArrCol = new JSONArray();
			JSONObject JsonObjCol = new JSONObject();
			for(int i = 0; i < rtnJsonObj.getJSONArray("data").size(); i++){
				JSONObject tmpJsonObj = rtnJsonObj.getJSONArray("data").getJSONObject(i);
				for(Iterator<?> itr = tmpJsonObj.keys(); itr.hasNext();){
					String keyNext = (String)itr.next();
					Object ObjNext = tmpJsonObj.get(keyNext);
					String ObjTypeNext = ObjNext.getClass().getName();
					if(!("net.sf.json.JSONNull".equals(ObjTypeNext) || "net.sf.json.JSONObject".equals(ObjTypeNext))){
						JsonObjCol.put(keyNext, ObjNext);
					}
				}
				JsonArrCol.add(JsonObjCol);
			}
			ds.setRawData(JsonArrCol);
			ds.setState(StateType.SUCCESS);
		}
		catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}

	//获取预约部门
	public DataSet getAppointmentDepts(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			JSONObject rtnJsonObj = syan.app.icity.govservice.GovProjectDao.getInstance().getAppointmentDepts(pSet);
			
			ds.setRawData(rtnJsonObj.getJSONArray("depts"));
			ds.setTotal(rtnJsonObj.getJSONArray("depts").size());
			ds.setState(StateType.SUCCESS);
		}
		catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}

	//获取预约事项
	public DataSet getAppointmentItems(ParameterSet pSet){
		String SearchName = String.valueOf(pSet.getParameter("SearchName"));
		SearchName = SearchName.equals("null") ? "" : SearchName;
		
		DataSet ds = new DataSet();
		try{
			JSONObject rtnJsonObj = syan.app.icity.govservice.GovProjectDao.getInstance().getAppointmentItems(pSet);
			
			JSONArray JsonArrCol = new JSONArray();
			for(int i = 0; i < rtnJsonObj.getJSONArray("Biz").size(); i++){
				JSONObject tmpJsonObj = rtnJsonObj.getJSONArray("Biz").getJSONObject(i);
				if("" != SearchName && tmpJsonObj.get("BizName").toString().indexOf(SearchName) != -1){
					JsonArrCol.add(tmpJsonObj);
				}
			}
			
			ds.setRawData(JsonArrCol);
			ds.setTotal(JsonArrCol.size());
			ds.setState(StateType.SUCCESS);
		}
		catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}

	//添加预约
	public DataSet insertAppointmentRecord(ParameterSet pSet){
		
		DataSet ds = new DataSet();
		try{
			JSONObject rtnJsonObj = syan.app.icity.govservice.GovProjectDao.getInstance().insertAppointmentRecord(pSet);
			
			ds.setData(Tools.stringToBytes(rtnJsonObj.toString()));
			ds.setState(StateType.SUCCESS);
		}
		catch(Exception ex){
			ds.setState(StateType.FAILT);
			ds.setMessage("调用失败！");
			ex.printStackTrace();
		}
		return ds;
	}

}