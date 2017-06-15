package app.pmi;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.JsonUtil;
import com.icore.util.StringUtil;
import com.icore.util.Tools;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.DbHelper;

public class PortalDao extends BaseJdbcDao {
	private static String INSTANCE_NAME = PortalDao.class.getName();
	public static PortalDao getInstance(){
		return DaoFactory.getDao(INSTANCE_NAME);
	}
	protected PortalDao(){
		this.setDataSourceName("icityDataSource");
	} 
	public void savePage(String pageId,String content,Boolean isRelease) throws Exception{
		Connection conn = this.getConnection();
		try{
			conn.setAutoCommit(false);
			long lTotal = DbHelper.stat("SELECT COUNT(ID) FROM PORTAL_PAGE WHERE ID in(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE TYPE='portal' and VALUE=?)", new Object[]{pageId},conn);
			String sql = "";
			if(lTotal>0){
				//修改
				sql = "UPDATE PORTAL_PAGE SET TEST_CONTENT=? WHERE  ID in(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE TYPE='portal' and VALUE=?)";
				DbHelper.update(sql, new Object[]{content,pageId},conn);
			}else{
				sql = "INSERT INTO PORTAL_PAGE(ID, EST_CONTENT) VALUE(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE TYPE='portal' and VALUE=?),?,?,?)";
				DbHelper.update(sql, new Object[]{pageId,content},conn);
			}
			if(isRelease){
				sql = "UPDATE PORTAL_PAGE SET IS_RELEASE='1',CONTENT=TEST_CONTENT WHERE ID in(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE TYPE='portal' and VALUE=?)";
				DbHelper.update(sql, new Object[]{pageId},conn);
			}
			conn.commit();
		}
		catch(Exception ex){
			if(conn != null){
				conn.rollback();
			}
			throw ex;
		}
		finally{
			DbHelper.closeConnection(conn);
		}
	}
	public String getReleasePageContent(String pageId) throws Exception{
		String sql = "select CONTENT from PORTAL_PAGE WHERE IS_RELEASE='1' AND ID in(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE IS_USE='1' AND TYPE='portal' and VALUE=?)";
		List<?> list = this.queryRaw(sql, new Object[]{pageId});
		if(list != null && list.size()>0){
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)list.get(0);
			JSONObject obj = JsonUtil.toJSONObject(map);
			Object objContent = (Object)obj.get("CONTENT");
			if(objContent ==null){
				return "";
			}
			return objContent.toString();
		}else{
			throw new Exception("没有查询到编码为"+pageId+"的页面");
		}
	}
	public String getTestPageContent(String pageId) throws Exception{
		String sql = "select TEST_CONTENT from PORTAL_PAGE WHERE ID in(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE TYPE='portal' and VALUE=?)";
		List<?> list = this.queryRaw(sql, new Object[]{pageId});
		if(list != null && list.size()>0){
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)list.get(0);
			JSONObject obj = JsonUtil.toJSONObject(map);
			Object objContent = (Object)obj.get("TEST_CONTENT");
			if(objContent ==null){
				return "";
			}
			return objContent.toString();
		}else{
			throw new Exception("没有查询到编码为"+pageId+"的页面");
		}
	}
	public List<?> getWidgetList(Boolean bAll) throws Exception{
		String sql = "SELECT * FROM PORTAL_WIDGET";
		if(!bAll){
			sql +=" WHERE IS_USE='1'";
		}
		return this.queryRaw(sql, new Object[]{});
	}
	public List<?> getWebSiteMenu(Boolean bAll,String webSiteId,String webSiteType,String isRelease) throws Exception{
		List<String> paramList = new ArrayList<String>();
		String subSql = " SELECT a.* FROM PORTAL_WEBSITE_MENU a where a.TYPE='url' union all select b.* from PORTAL_WEBSITE_MENU b,portal_page p where b.TYPE='portal' and b.gid=p.id ";
		if(StringUtil.isNotEmpty(isRelease)){
			subSql += " and p.IS_RELEASE=?";
			paramList.add(isRelease);
		}
		String sql = "select * from ( " + subSql + " ) "
				+ " WHERE (WEBSITE_ID=? OR (WEBSITE_ID='-1'"
				+ " AND ID NOT IN(SELECT RAW_ID FROM PORTAL_WEBSITE_MENU WHERE WEBSITE_ID=? AND RAW_ID is not null AND length(RAW_ID)>0)))";
		if(!bAll){
			sql +=" AND IS_USE='1'";
		}
		sql +=" AND WEBSITE_TYPE=?";
		sql +=" ORDER BY SORT_ORDER ASC";
		paramList.add(webSiteId);
		paramList.add(webSiteId);
		paramList.add(webSiteType);
		List<?> list = this.queryRaw(sql, paramList.toArray(new String[paramList.size()]));
		for(int i=0;i<list.size();i++){
			Map<String,Object> map = (Map<String,Object>)list.get(i);
			String url = map.get("VALUE").toString();
			if("portal".equals(map.get("TYPE"))){
				url = HttpUtil.formatUrl(HttpUtil.contextPath()+"/portal/ui/"+url);
			}else{
				if(url.indexOf("http://")!=0){
					url = HttpUtil.formatUrl(HttpUtil.contextPath()+url);
				}
			}
			map.put("TARGET_URL", url);
		}
		return list;
	} 
	public boolean insertWebSiteMenu(ParameterSet pSet) throws Exception{
		Connection conn = null;
		try{
			conn = this.getConnection();
			conn.setAutoCommit(false);
			String type = (String)pSet.get("TYPE");
			String gid= (String)pSet.get("GID");
			if(gid ==null || gid.length()==0){
				gid = Tools.getUUID32();
			}
			if("portal".equalsIgnoreCase(type)){
				//查询是否存在portal
				long l = DbHelper.stat("SELECT COUNT(ID) FROM PORTAL_PAGE WHERE ID=?", new Object[]{gid}, conn);
				if(l>0){
					throw new Exception("已经存在了自定义页面不能再次添加，请重试！");
				}
			}
			String sql = "INSERT INTO PORTAL_WEBSITE_MENU "
					+ "(ID, NAME, SORT_ORDER, PARENT_ID, WEBSITE_ID, TYPE, VALUE,GID,IS_USE,IS_NAV,TARGET_TYPE,WEBSITE_TYPE) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
			int successNum = DbHelper.update(sql, new Object[]{
					(String)pSet.get("ID"),
					(String)pSet.get("NAME"),
					(String)pSet.get("SORT_ORDER"),
					(String)pSet.get("PARENT_ID"),
					(String)pSet.get("WEBSITE_ID"),
					type,
					(String)pSet.get("VALUE"),
					gid,
					(String)pSet.get("IS_USE"),
					(String)pSet.get("IS_NAV"),
					(String)pSet.get("TARGET_TYPE"),
					(String)pSet.get("WEBSITE_TYPE")
					},conn);
			if(successNum>0 && "portal".equalsIgnoreCase(type)){
				sql = "INSERT INTO PORTAL_PAGE(ID, NAME, CONTENT,IS_RELEASE, TEST_CONTENT,META) VALUES(?,?,?,?,?,?)";
				successNum = DbHelper.update(sql,new Object[]{
						gid,
						(String)pSet.get("TITLE"),
						"",
						"0",
						"",
						(String)pSet.get("META")
				},conn);
			}
			conn.commit();
			if(successNum>0){
				return true;
			}
		}
		catch(Exception ex){
			if(conn !=null){
				conn.rollback();
			}
			throw ex;
		}
		finally{
			if(conn != null){
				DbHelper.closeConnection(conn);
			}
		}
		return false;
	}
	public boolean updateWebSiteMenu(ParameterSet pSet) throws Exception{
		Connection conn = null;
		try{
			conn = this.getConnection();
			conn.setAutoCommit(false);
			String type = (String)pSet.get("TYPE");
			String gid= (String)pSet.get("GID");
			if("portal".equalsIgnoreCase(type)){
				//查询是否存在portal
				long l = DbHelper.stat("SELECT COUNT(ID) FROM PORTAL_PAGE WHERE ID=?", new Object[]{gid}, conn);
				if(l==0){
					String sql = "INSERT INTO PORTAL_PAGE(ID, NAME, CONTENT,IS_RELEASE, TEST_CONTENT,META) VALUE(?,?,?,?,?,?)";
					if(DbHelper.update(sql,new Object[]{
							gid,
							(String)pSet.get("TITLE"),
							"",
							"0",
							"",
							(String)pSet.get("META")
					},conn)==0){
						throw new Exception("添加自定义页面失败，可能服务器繁忙，请稍后再试！");
					}
				}else{
					String sql = "UPDATE PORTAL_PAGE SET NAME=?,META=? where ID = ?";
					if(DbHelper.update(sql,new Object[]{
							(String)pSet.get("TITLE"),
							(String)pSet.get("META"),
							gid
					},conn)==0){
						throw new Exception("修改自定义页面失败，可能服务器繁忙，请稍后再试！");
					}
				}
			}else{
				DbHelper.update("DELETE FROM PORTAL_PAGE WHERE ID=?", new Object[]{gid},conn);
			}
			String sql = "UPDATE PORTAL_WEBSITE_MENU SET"
					+ "NAME =?, SORT_ORDER =?, PARENT_ID =?, TYPE =?, VALUE =?,IS_USE=?,IS_NAV=?,TARGET_TYPE=?,WEBSITE_TYPE) "
					+ " WHERE ID =? AND WEBSITE_ID =?";
			int successNum = DbHelper.update(sql, new Object[]{
					(String)pSet.get("NAME"),
					(String)pSet.get("SORT_ORDER"),
					(String)pSet.get("PARENT_ID"),
					type,
					(String)pSet.get("VALUE"),
					(String)pSet.get("IS_USE"),
					(String)pSet.get("IS_NAV"),
					(String)pSet.get("TARGET_TYPE"),
					(String)pSet.get("WEBSITE_TYPE"),
					(String)pSet.get("ID"),
					(String)pSet.get("WEBSITE_ID")
					},conn);
			conn.commit();
			if(successNum>0){
				return true;
			}
		}
		catch(Exception ex){
			if(conn !=null){
				conn.rollback();
			}
			throw ex;
		}
		finally{
			if(conn != null){
				DbHelper.closeConnection(conn);
			}
		}
		return false;
	}
	public boolean deleteWebSiteMenu(ParameterSet pSet) throws Exception{
		Connection conn = null;
		try{
			conn = this.getConnection();
			conn.setAutoCommit(false);
			String id = (String)pSet.get("ID");
			String websiteId= (String)pSet.get("WEBSITE_ID");
			long lTotal = DbHelper.stat("SELECT COUNT(ID) FROM PORTAL_WEBSITE_MENU WHERE PARENT_ID = ? and WEBSITE_ID=?", new Object[]{id,websiteId},conn);
			if(lTotal>0){
				throw new Exception("存在子节点，不允许删除。");
			}
			DbHelper.update("DELETE FROM PORTAL_PAGE WHERE ID in(SELECT GID FROM PORTAL_WEBSITE_MENU WHERE ID=? and WEBSITE_ID=?)", new Object[]{id,websiteId},conn);
			DbHelper.update("DELETE FROM PORTAL_WEBSITE_MENU WHERE ID=? and WEBSITE_ID=?", new Object[]{id,websiteId},conn);
			conn.commit();
			return true;
		}
		catch(Exception ex){
			if(conn !=null){
				conn.rollback();
			}
			throw ex;
		}
		finally{
			if(conn != null){
				DbHelper.closeConnection(conn);
			}
		}
	}
}
