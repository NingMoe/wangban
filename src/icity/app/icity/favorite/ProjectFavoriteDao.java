/**  
 * @Title: ProjectFavoriteDao.java 
 * @Package icity.admin.favorite.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-4-8 下午4:45:35 
 * @version V1.0  
 */ 
package app.icity.favorite;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 
 * @ClassName: ProjectFavoriteDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-4-8 下午4:45:35  
 */

public class ProjectFavoriteDao extends BaseJdbcDao {
	
	private ProjectFavoriteDao(){
		this.setDataSourceName("icityDataSource");
	}
	
	
	public static ProjectFavoriteDao getInstance(){
		return DaoFactory.getDao(ProjectFavoriteDao.class.getName());
	}
	
	public DataSet getList(ParameterSet pSet){
		String sql="SELECT ID,SXBM,PNAME,UUID,FTIME,REMARK,STATUS FROM  "
				  +"(SELECT PF.ID,PF.SXBM,PI.NAME AS PNAME,PF.USID as UUID,PF.FTIME,PF.REMARK,PF.STATUS FROM "
				  +"POWER_BASE_INFO_FAVORITE PF,POWER_BASE_INFO PI WHERE PF.SXBM=PI.CODE AND PI.STATUS=1) P " 
				  +"WHERE ID IS NOT NULL ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if(start==-1||limit==-1){
			ds = this.executeDataset(sql,null);			
		}else{
			ds = this.executeDataset(sql, start, limit,null);
		}
		return ds;
	}
	
	public DataSet getItemById(ParameterSet pSet){
		String sql="SELECT CODE,NAME,DEPT_NAME FROM POWER_BASE_INFO";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if(start==-1||limit==-1){
			ds = this.executeDataset(sql,null);
		}else{
			ds = this.executeDataset(sql, start, limit,null);
		}
		return ds;
	}
	
	
	public DataSet insert(ParameterSet pSet){
		String sql = "INSERT INTO POWER_BASE_INFO_FAVORITE (ID, SXBM, USID, FTIME, REMARK, STATUS,SXMC,SXID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		DataSet ds = new DataSet(); 
		String userId = String.valueOf(pSet.getParameter("USER_ID"));
		int i = this.executeUpdate(sql, new Object[]{Tools.getUUID32(),
				(String)pSet.getParameter("SXBM"),
				userId,
			//	(String)pSet.getParameter("UUUID"),
				CommonUtils.getInstance().parseStringToTimeStamp(
						Tools.formatDate(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),
						CommonUtils.YYYY_MM_DD_HH_mm_SS),
				(String)pSet.getParameter("REMARK"),
				(String)pSet.getParameter("STATUS"),
				(String)pSet.getParameter("SXMC"),
				(String)pSet.getParameter("SXID")
		});
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}else{
			ds.setState(StateType.SUCCESS);
		}
		return ds;
		
	}
	
	
	public DataSet update(ParameterSet pSet){
		String sql = "UPDATE POWER_BASE_INFO_FAVORITE SET  SXBM = ?, USID = ?, FTIME = ?, REMARK = ?, STATUS = ? "
				   + "WHERE ID = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, 
				new Object[]{
					(String)pSet.getParameter("SXBM"),
					(String)pSet.getParameter("UUID"),
					CommonUtils.getInstance().parseStringToTimeStamp((String)pSet.getParameter("FTIME"),CommonUtils.YYYY_MM_DD),
					(String)pSet.getParameter("REMARK"),
					(String)pSet.getParameter("STATUS"),
					(String)pSet.getParameter("ID")
					});
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}else{
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
	
	public DataSet delete(ParameterSet pSet){
		String ids = (String)pSet.getParameter("ids");
		DataSet ds = new DataSet();
		if(StringUtils.isNotEmpty(ids)){
			String sql = "UPDATE POWER_BASE_INFO_FAVORITE SET STATUS=0";
			ParameterSet tSet = new ParameterSet();
			tSet.setParameter("ID@in", ids);
			sql = SqlCreator.getSimpleQuerySql(tSet, sql, getDataSource());
			int i= this.executeUpdate(sql);
			if(i==0){
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			}
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("参数ids的值为空！");
		}
		if(!"".equals(ids)){
			ds.setMessage("删除收藏成功！");
		}else{
			ds.setMessage("请选择您需要删除的内容");
		}
		return ds;
	}
	
	
	public DataSet getBusinessFavoriteList(ParameterSet pSet){
		String sql="SELECT * FROM POWER_BASE_INFO_FAVORITE PF WHERE PF.STATUS='1'  ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		sql += " ORDER BY PF.FTIME DESC ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if(start==-1||limit==-1){
			ds = this.executeDataset(sql);
		}else{
			ds = this.executeDataset(sql, start, limit ,null,this.getDataSourceName());			
		}	
		return ds;
	}
	/**
	 * 查询收藏记录
	 * @param pSet
	 * @return
	 */
	public DataSet searchFaveriot(ParameterSet pSet){
		DataSet ds = new DataSet();
		//java.sql.Timestamp date=CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),CommonUtils.YYYY_MM_DD_HH_MM_SS_SSS); 
		String sql_select = "SELECT ID FROM POWER_BASE_INFO_FAVORITE WHERE STATUS='1' AND SXBM=? AND SXID=? AND USID=?";
		DataSet ret = this.executeDataset(sql_select,new Object[]{(String)pSet.get("SXBM"),(String)pSet.get("SXID"),pSet.get("USID")});
		if(ret.getState()==1&&ret.getTotal()>0){
			ds.setState(StateType.SUCCESS);
		}else{
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}
		return ds;
	}
	/**
	 * 添加收藏记录
	 * @param pSet
	 * @return
	 */
	@SuppressWarnings("null")
	public DataSet addFaveriot(ParameterSet pSet){
		DataSet ds = new DataSet();
		java.sql.Timestamp date=CommonUtils.getInstance().parseDateToTimeStamp(new java.util.Date(),CommonUtils.YYYY_MM_DD_HH_MM_SS_SSS); 
		String sql_select = "SELECT ID FROM POWER_BASE_INFO_FAVORITE WHERE SXBM=? AND USID=? ";
		DataSet ret = this.executeDataset(sql_select,new Object[]{(String)pSet.get("SXBM"),pSet.get("USID")});
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		if(ret.getState()==1&&ret.getTotal()>0){
			String sql = "UPDATE POWER_BASE_INFO_FAVORITE SET SXID=?,SXMC=?, FTIME=?,STATUS=1 WHERE SXBM=? AND USID=? ";
			int i = this.executeUpdate(sql,
					new Object[]{
					
					(String)pSet.get("SXID"),
					(String)pSet.get("SXMC"),
					date,
					(String)pSet.get("SXBM"),
					pSet.get("USID")},this.getDataSourceName());		
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			}else{
				jo.put("collect","1");
				ja.add(jo);
				ds.setState(StateType.SUCCESS);
				ds.setRawData(ja);
			}
			return ds;
		}
		String sql = "INSERT INTO POWER_BASE_INFO_FAVORITE(ID,SXID, SXBM,SXMC, USID, FTIME, STATUS)VALUES(?,?,?,?,?,?,?)";
		int i = this.executeUpdate(sql,
				new Object[]{
				Tools.getUUID32(),
				(String)pSet.get("SXID"),
				(String)pSet.get("SXBM"),
				(String)pSet.get("SXMC"),
				(String)pSet.get("USID"),
				date,
				"1"},this.getDataSourceName());
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}else{
			jo.put("collect","0");//判断是否收藏过
			ja.add(jo);
			ds.setState(StateType.SUCCESS);
			ds.setRawData(ja);
		}
		return ds;
	}


	public DataSet updateRemark(ParameterSet pSet){
		DataSet ds = new DataSet();
		String remark=(String)pSet.get("REMARK");
		pSet.remove("REMARK");
		String sql = "UPDATE POWER_BASE_INFO_FAVORITE SET REMARK=?";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int i= this.executeUpdate(sql,new Object[]{remark});
		if(i==0){
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}
		ds.setMessage("删除收藏成功！");
		return ds;
	}
	
	
}
