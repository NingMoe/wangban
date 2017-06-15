package app.icity.center.active;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.icore.util.Tools;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils_api;

/**
 * @ClassName: BusinessAskDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-5-31 上午10:23:07
 */

public class BusinessStrategyDao extends BaseJdbcDao {
	protected BusinessStrategyDao() {
		this.setDataSourceName("icityDataSource");
	}
	public static BusinessStrategyDao getInstance() {
		return DaoFactory.getDao(BusinessStrategyDao.class.getName());
	}
	/**
	 * 我的攻略
	 * @param pSet
	 * @return
	 */
	public DataSet getBusinessStrategy(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			//String sblsh = (String) pSet.getParameter("sblsh");
			//String sxbm = (String) pSet.getParameter("sxbm");
			String sxid = (String) pSet.getParameter("sxid");
			//String sxmc = (String) pSet.getParameter("sxmc");
			String CREATOR = (String) pSet.getParameter("CREATOR");
			String sql = "select * from pub_strategy where sxid = ? and ucid=?";			
			ds = this.executeDataset(sql,new Object[]{sxid,CREATOR});					
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
	/**
	 * 更新我的攻略
	 * @param pSet
	 * @return
	 */
	public DataSet updateBusinessStrategy(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sblsh = (String) pSet.getParameter("sblsh");
			String sxbm = (String) pSet.getParameter("sxbm");
			String sxid = (String) pSet.getParameter("sxid");
			String sxmc = (String) pSet.getParameter("sxmc");
			String content = (String) pSet.getParameter("content");
			String CREATOR = (String) pSet.getParameter("CREATOR");
			String CREATOR_NAME = (String) pSet.getParameter("CREATOR_NAME");
			Calendar cal   = Calendar.getInstance(); 
			Date date =cal.getTime();
			Timestamp d =null;
			d = CommonUtils_api.getInstance().parseDateToTimeStamp(date, CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String sql = "select * from pub_strategy where sxid = ? and ucid=?";			
			ds = this.executeDataset(sql,new Object[]{sxid,CREATOR});
			int i=0;
			if(ds.getTotal()>0){
				sql = "update pub_strategy set content=?,SUBMITDATE=? where sxid=? and ucid=?";
				i = this.executeUpdate(sql,new Object[]{content,d,sxid,CREATOR});
			}else{
				sql = "insert into pub_strategy (ID,SBLSH,SXID,SXBM,SXMC,CONTENT,STATUS,SUBMITDATE,AUDITDATE," +
					"AUDITCONTENT,UCID,NAME,REVIEWER,REVIEWERID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				String id = Tools.getUUID32();
				i = this.executeUpdate(sql,new Object[]{
						id,sblsh,sxid,sxbm,sxmc,content,"0",d,null,"",CREATOR,CREATOR_NAME,"",""
				});
			}
			if(i>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("更新成功！");
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("更新失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
}
