package hlj_qqheNew.app.icity.govservice;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

public class GovProjectDao extends BaseJdbcDao{ 
	   private GovProjectDao(){
		   this.setDataSourceName("icityDataSource");
	   }		
	
		public static GovProjectDao getInstance(){
			return DaoFactory.getDao(GovProjectDao.class.getName());
		}
		
		public DataSet getPowerList(ParameterSet pSet){
			DataSet ds = new  DataSet();
			try{
				String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemListByPage");
				HttpClient client = new HttpClient();
				client.getParams().setContentCharset("UTF-8");
				PostMethod postMethod = new PostMethod(url);
				StringBuilder whereValue=new StringBuilder();
				JSONArray paramValue=new JSONArray();
				String cat = (String) pSet.getParameter("CAT");
				String classtype = (String) pSet.getParameter("ID");
				String item_type = (String)pSet.getParameter("ITEM_TYPE");
				String region_code=(String)pSet.getParameter("region_code");
				String is_online=(String)pSet.getParameter("IS_ONLINE");
				String pagemodel = (String) pSet.getParameter("PAGEMODEL");
				if("person".equals(pagemodel)){
					whereValue.append(" and service_object like ? ");
					paramValue.add("%0%");
				}else if("ent".equals(pagemodel)){
					whereValue.append(" and service_object like ? ");
					//大庆的企业服务对象编码是4
					if(region_code.substring(0, 4).equals("2306")){
						paramValue.add("%4%");
					}else{
						paramValue.add("%1%");
					}
					
				}else if("personAent".equals(pagemodel)){
					whereValue.append(" and (service_object like ? or service_object like ?)");
					paramValue.add("%0%");
					paramValue.add("%1%");
				}
				if(StringUtils.isNotEmpty(region_code)){
					whereValue.append(" and region_code=? ");
					paramValue.add(region_code);
				}
				
				if("dept".equals(cat)){
					if(StringUtils.isNotEmpty(classtype)){
						whereValue.append(" and org_code=? ");
						paramValue.add(classtype);			
					}
				}else if("theme".equals(cat)){
					whereValue.append(" and title_name like ? ");
					paramValue.add("%"+classtype+"%");
				}
				
//				if(StringUtils.isNotEmpty(deptid)){
//					whereValue.append(" and org_code=? ");
//					paramValue.add(deptid);
//				}
				if(StringUtils.isNotEmpty(item_type)){
					whereValue.append(" and type=? ");
					paramValue.add(item_type);
				}
				if(StringUtils.isNotEmpty(is_online)){
					whereValue.append(" and IS_ONLINE=? ");
					paramValue.add(is_online);
				}
				postMethod.setParameter("page",pSet.getParameter("page").toString());
				postMethod.setParameter("rows",pSet.getParameter("limit").toString());
				postMethod.setParameter("whereValue", whereValue.toString());
				postMethod .setParameter("paramValue",paramValue.toString());
					client.executeMethod(postMethod);
					JSONObject obj;		
					obj =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
					JSONArray pageList=obj.getJSONArray("pageList");
					JSONArray rows=new JSONArray();
					for(int i=0;i<pageList.size();i++){
						JSONObject column;
						column=(JSONObject) pageList.get(i);
						rows.add(column.get("columns"));
					}
					ds.setRawData(rows);
					ds.setTotal(obj.getInt("totlaRow"));
			}catch(Exception e){
				ds.setState(StateType.FAILT);
				ds.setMessage("查询失败！");
			}			
			return ds;
		}
		
		public DataSet getMattersList2(ParameterSet pSet){
			DataSet ds=new DataSet();
			try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemListByPage");
			HttpClient client = new HttpClient();
			client.getParams().setContentCharset("UTF-8");
			PostMethod postMethod = new PostMethod(url);
			String cat = (String) pSet.getParameter("CAT");
			String classtype = (String) pSet.getParameter("ID");
			String pagemodel = (String) pSet.getParameter("PAGEMODEL");
			StringBuilder whereValue=new StringBuilder();
			JSONArray paramValue=new JSONArray();
			if("person".equals(pagemodel)){
				whereValue.append(" and service_object like ? ");
				paramValue.add("%0%");
			}else if("ent".equals(pagemodel)){
				whereValue.append(" and service_object like ? ");
				paramValue.add("%1%");
			}		
			if("dept".equals(cat)){
				whereValue.append(" and org_code=? ");
				paramValue.add(classtype);			
			}else if("theme".equals(cat)){
				whereValue.append(" and title_name like ? ");
				paramValue.add("%"+classtype+"%");
			}
//			if("1".equals(SUIT_ONLINE)){
//				whereValue.append(" and is_online=? ");
//				paramValue.add("1");
//			}
//			if(!"".equals(SearchName)){
//				whereValue.append(" and name like ? ");
//				paramValue.add("%"+SearchName+"%");
//			}
			postMethod.setParameter("page",pSet.getParameter("page").toString());
			postMethod.setParameter("rows",pSet.getParameter("limit").toString());
			//postMethod.setParameter("orgCode", "37011400000003");
			postMethod.setParameter("whereValue", whereValue.toString());
			postMethod .setParameter("paramValue",paramValue.toString());
				client.executeMethod(postMethod);
				JSONObject obj;		
				obj =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
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
}