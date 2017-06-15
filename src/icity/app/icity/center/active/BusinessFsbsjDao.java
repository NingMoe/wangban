/**  
 * @Title: BusinessAskDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:23:07 
 * @version V1.0  
 */ 
package app.icity.center.active;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

/** 
 * @ClassName: BusinessAskDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:23:07  
 */

public class BusinessFsbsjDao extends BaseJdbcDao {
	
	protected BusinessFsbsjDao(){
		this.setDataSourceName("icityDataSource");
	}
	
	public static BusinessFsbsjDao getInstance() {
		return DaoFactory.getDao(BusinessFsbsjDao.class.getName());
	}	
	
	public DataSet getBusinessFsbsjList(ParameterSet pSet){	
		String searchTitle = (String)pSet.remove("SEARCH_TITLE");
		String sql ="SELECT * FROM FSBSJYY F";		
		if(StringUtil.isNotEmpty(searchTitle)){
			sql+=" AND (G.SXMC like '%"+searchTitle+"%' OR G.CONTENT like '%"+searchTitle+"%') ";
		}
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		sql += " ORDER BY F.ID DESC";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if(start==-1||limit==-1){
			return this.executeDataset(sql,null);
		}else{
			return this.executeDataset(sql, start, limit,null);
		}
	}	
	
	public DataSet delete(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String ids = (String)pSet.getParameter("ids");		
			if(StringUtils.isNotEmpty(ids)){
				String sql = "delete from FSBSJYY";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("id@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql, this.getDataSource());
				int i= this.executeUpdate(sql);
				if(i==0){
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			}else{
				ds.setState(StateType.FAILT);				
				ds.setMessage("请选择您需要删除的内容！");
//				ds.setMessage("参数ids的值为空！");
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		return ds;
	}	
	
	public DataSet getBusinessFsbsj(ParameterSet pSet){
		String sql="select * from fsbsjyy f ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if(start==-1||limit==-1){
			return this.executeDataset(sql,null);
		}else{
			return this.executeDataset(sql, start, limit,null);
		}
	}	
	
	public DataSet update (ParameterSet pSet){
		String sql="update guestbook set open=?,checks=?,title=?,content=? where id=?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, 
				new Object[]{		
					(String)pSet.getParameter("OPEN"),
					(String)pSet.getParameter("CHECKS"),
					(String)pSet.getParameter("TITLE"),
					(String)pSet.getParameter("CONTENT"),
					(String)pSet.getParameter("ID")
				});
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据更新失败！");
		}else{
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
}
