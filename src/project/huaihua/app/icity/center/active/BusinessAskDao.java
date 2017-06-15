/**  
 * @Title: BusinessAskDao.java 
 * @Package icity.center.active 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-5-31 上午10:23:07 
 * @version V1.0  
 */ 
package huaihua.app.icity.center.active;

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

public class BusinessAskDao extends BaseJdbcDao {
	
	protected BusinessAskDao(){
		this.setDataSourceName("icityDataSource");
	}
	
	public static BusinessAskDao getInstance() {
		return DaoFactory.getDao(BusinessAskDao.class.getName());
	}	
	
	public DataSet getBusinessAskList(ParameterSet pSet){	
		String searchTitle = (String)pSet.remove("SEARCH_TITLE");
		String status = (String) pSet.getParameter("STATUS");
		String sql ="SELECT G.*,to_char(G.Write_Date,'yyyy-MM-dd HH24:mi:ss') Write_Date_Char," +
				"to_char(G.Deal_Date,'yyyy-MM-dd HH24:mi:ss') Deal_Date_Char," +
				"to_char(G.Commitmenttime,'yyyy-MM-dd HH24:mi:ss') Commitmenttime_Char," +
				"to_char(G.Accepttime,'yyyy-MM-dd HH24:mi:ss') Accepttime_Char," +
				"to_char(G.Accepttimeout,'yyyy-MM-dd HH24:mi:ss') Accepttimeout_Char " +
				" FROM GUESTBOOK G where 1=1 and G.open='1' ";		
		if(StringUtil.isNotEmpty(searchTitle)){
			sql+=" AND (G.SXMC like '%"+searchTitle+"%' OR G.CONTENT like '%"+searchTitle+"%' OR G.TITLE like '%"+searchTitle+"%') ";
		}
		if(StringUtil.isNotEmpty(status)){
			sql+=" AND STATUS ="+status;
		}
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		sql += " ORDER BY G.WRITE_DATE DESC";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if(start==-1||limit==-1){
			return this.executeDataset(sql,null);
		}else{
			return this.executeDataset(sql, start, limit,null);
		}
	}
	
	//聊城 按照时间和标题进行选择
	public DataSet getBusinessAskList2(ParameterSet pSet){	
		//String searchTitle = (String)pSet.remove("SEARCH_TITLE");
		String CONTENT = (String) pSet.getParameter("CONTENT");
		String sql ="SELECT * FROM GUESTBOOK G where 1=1 ";		
		//if(StringUtil.isNotEmpty(CONTENT)){
			sql+="AND (G.CONTENT like '%"+CONTENT+"%' OR G.TITLE like '%"+CONTENT+"%')";
		//}
		sql+="AND G.TYPE = " +(String) pSet.getParameter("TYPE");	
				//sql+=" AND STATUS ="+status;
		sql += " ORDER BY G.WRITE_DATE DESC";
		int start = (Integer)pSet.getParameter("start");
		int limit = (Integer)pSet.getParameter("limit");
		if(start==-1||limit==-1){
			return this.executeDataset(sql,null);
		}else{
			DataSet ds =this.executeDataset(sql, start, limit,null);
			return ds;
		}
	}
	
	public DataSet delete(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String ids = (String)pSet.getParameter("ids");		
			if(StringUtils.isNotEmpty(ids)){
				String sql = "delete from guestbook";
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
	
	public DataSet getBusinessAsk(ParameterSet pSet){
		String sql="select g.username,g.replayer,g.sxmc,g.phone,g.email,g.address,g.write_date,g.open,g.checks,g.warn,g.title,g.content,g.status,g.deal_result,g.deal_date,(case when g.reply_ip is null then 0 else 1 end) reply from guestbook g where g.type=2";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if(start==-1||limit==-1){
			return this.executeDataset(sql);
		}else{
			return this.executeDataset(sql, start, limit,null);
		}
	}	
	
	public DataSet getBusinessJycx(ParameterSet pSet){
		String sql="select g.sxmc,g.phone,g.email,g.address,g.write_date,g.open,g.checks,g.warn,g.title,g.content,g.status,g.deal_result,g.deal_date,(case when g.reply_ip is null then 0 else 1 end) reply from guestbook g where g.type=10";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if(start==-1||limit==-1){
			return this.executeDataset(sql,null);
		}else{
			return this.executeDataset(sql, start, limit,null);
		}
	}
	
	public DataSet getBusinessTzzx(ParameterSet pSet){
		String type = (String) pSet.getParameter("type");
		String projectCode = (String) pSet.getParameter("projectCode");
		String sql="select g.write_date,g.busi_id,g.title,g.content from guestbook g where g.type=? and g.projectcode=?";
		return this.executeDataset(sql,new Object[]{type,projectCode});
	}
	
	public DataSet update (ParameterSet pSet){
		String sql="update guestbook set open=?,checks=?,title=?,content=? where id=?";
		DataSet ds = new DataSet();
		try{
			int i = this.executeUpdate(sql, 
					new Object[]{		
						(String)pSet.getParameter("OPEN"),
						(String)pSet.getParameter("CHECKS"),
						(String)pSet.getParameter("TITLE"),
						java.net.URLDecoder.decode((String)pSet.getParameter("CONTENT"),"UTF-8"),
						(String)pSet.getParameter("ID")
					});
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据更新失败！");
			}else{
				ds.setState(StateType.SUCCESS);
			}
		}catch(Exception e){
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
}
