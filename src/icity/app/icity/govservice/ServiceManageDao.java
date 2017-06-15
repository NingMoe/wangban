package app.icity.govservice;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.icity.project.ProjectBusinessStatDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.conf.DataSourceConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class ServiceManageDao extends BaseJdbcDao {
	private static Logger log = LoggerFactory.getLogger(ProjectBusinessStatDao.class);
	private static String icityDataSource = "icityDataSource";
	private ServiceManageDao() {
		this.setDataSourceName("icityDataSource");
	}
	public static ServiceManageDao getInstance() {
		return DaoFactory.getDao(ServiceManageDao.class.getName());
	}
	/**
	 * 相关服务推荐
	 * @param pSet
	 * @return
	 */
	public DataSet getBusyInfoRelatedServices(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String ucid = (String)pSet.remove("uid");
			String key = (String)pSet.remove("key");
			String sql = "select * from business_index t where t.ucid = ? and t.sbsj>sysdate-?";
			
			String DEFAULT_DB_TYPE = DataSourceConfig.getString("jdbc.dataType","orcl");
			if("mysql".equals(DEFAULT_DB_TYPE)){
				sql = "select * from business_index t where t.ucid = ? and t.sbsj>DATE_SUB(CURDATE(), INTERVAL ? DAY)";
			}
			
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			ds = this.executeDataset(sql, start, limit,new Object[] {ucid,key}, icityDataSource);	
			int size = ds.getJAData().size();
			JSONArray m_j = new JSONArray();
			for(int i=0;i<size;i++){
				if(m_j.size()<limit){
					ParameterSet m_pSet = new ParameterSet();
					m_pSet.put("itemId", ds.getJAData().getJSONObject(i).getString("SXID"));
					DataSet m_items = getItemsByItemId(m_pSet);
					if(m_items.getState()==StateType.SUCCESS&&m_items.getTotal()>0){
						JSONArray m_j_before = m_items.getJAData().getJSONArray(0);
						for(int j=0;j<m_j_before.size();j++){
							if(m_j.size()<limit){
								m_j.add(m_j_before.getJSONObject(j));
							}
						}
						JSONArray m_j_after = m_items.getJAData().getJSONArray(1);
						for(int j=0;j<m_j_after.size();j++){
							if(m_j.size()<limit){
								m_j.add(m_j_after.getJSONObject(j));
							}
						}
					}
				}
			}
			ds.setRawData(m_j);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 事项上下游链条
	 * @param pSet
	 * @return
	 */
	public DataSet getItemsByItemId(ParameterSet pSet){
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemsByItemId");		
		String itemId = (String) pSet.getParameter("itemId");
		url+="?itemId="+itemId;		
		try {
			JSONObject obj;
			obj =  JSONObject.fromObject(RestUtil.getData(url));
			/*String o = "{\"state\": 1,\"data\": [[{\"GROUP_NAME\": \"打算\",\"ITEM_ID\": \"18f3bf4d1b89447ca6f7f64efa8ca089\",\"LIMIT_TIME\": \"20\",\"CREATE_TIME\": \"2016-06-02 03:48:19\",\"GROUP_ID\": \"6DAC3A8928BA414683AC3E1B7EB05FE4\",\"ITEM_NAME\": \"6.省管基金会分支机构、代表机构注销登记\"}]" +
					",[{\"GROUP_NAME\": \"打算\",\"ITEM_ID\": \"18f3bf4d1b89447ca6f7f64efa8ca089\",\"LIMIT_TIME\": \"20\",\"CREATE_TIME\": \"2016-06-02 03:48:19\",\"GROUP_ID\": \"6DAC3A8928BA414683AC3E1B7EB05FE4\",\"ITEM_NAME\": \"6.省管基金会分支机构、代表机构注销登记\"}]]}";
			JSONObject obj = JSONObject.fromObject(o);*/
			if("1".equals(obj.getString("state"))){
				ds.setRawData(obj.getJSONArray("data"));
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		} 
		return ds;
	}
	/**
	 * 近期办件
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessRecentDo(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String ucid = (String)pSet.remove("uid");
			String key = (String)pSet.remove("key");
			String sql = "select * from business_index t where t.ucid = ? and t.sbsj>sysdate-?";
			
			String DEFAULT_DB_TYPE = DataSourceConfig.getString("jdbc.dataType","orcl");
			if("mysql".equals(DEFAULT_DB_TYPE)){
				sql = "select * from business_index t where t.ucid = ? and t.sbsj>DATE_SUB(CURDATE(), INTERVAL ? DAY)";
			}
			
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			ds = this.executeDataset(sql, start, limit,new Object[] {ucid,key}, icityDataSource);			
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 近期咨询投诉信息
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessRecentGuestbook(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String ucid = (String)pSet.remove("uid");
			String key = (String)pSet.remove("key");
			String sql = "select * from guestbook t where t.user_id = ? and t.write_date>sysdate-?";
			
			String DEFAULT_DB_TYPE = DataSourceConfig.getString("jdbc.dataType","orcl");
			if("mysql".equals(DEFAULT_DB_TYPE)){
				sql = "select * from guestbook t where t.user_id = ? and t.write_date>DATE_SUB(CURDATE(), INTERVAL ? DAY)";
			}
			
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit,new Object[] {ucid,key}, icityDataSource);		
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 我的消息（办件消息，咨询投诉消息）
	 * @param pSet
	 * @return
	 */
	public DataSet getNewsPubmynews(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String sql = "select * from pub_mynews t ";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			sql += " order by status asc,writetime desc";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit,new Object[] {}, icityDataSource);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 我的消息（更新状态为已读）
	 * @param pSet
	 * @return
	 */
	public DataSet updateNewsPubmynews(ParameterSet pSet){
		DataSet ds = new DataSet();
		try {
			String ucid = (String)pSet.remove("ucid");
			String isupdate = (String)pSet.remove("isall");
			String id = (String)pSet.remove("ID");
			String sql = "select t.id from pub_mynews t where t.ucid = ? and t.status = '0'";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			ds = this.executeDataset(sql,new Object[] {ucid}, icityDataSource);
			if(StringUtils.isNotEmpty(isupdate)&&ds.getTotal()>0){
				for(int i =0;i<ds.getTotal();i++){
					String sql_update = "update pub_mynews set status='1' where id = ?";
					this.executeUpdate(sql_update,new Object[] {ds.getRecord(i).getString("ID")}, icityDataSource);
				}
			}
			if(StringUtils.isNotEmpty(id)){
				String sql_update = "update pub_mynews set status='1' where id = ?";
				this.executeUpdate(sql_update,new Object[] {id}, icityDataSource);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}
}