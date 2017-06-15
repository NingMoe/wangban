package hlj_qqheNew.app.icity.project;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.project.ProjectQueryDao;

import com.alibaba.dubbo.common.json.JSONObject;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;

public class HotWordDao extends BaseJdbcDao{
	    protected static String icityDataSource = "icityDataSource";
	    protected static String ipfDataSource = "ipfDataSource";
	    protected static String icpDataSource = "icpDataSource";

	    public static HotWordDao getInstance() {
	    	return DaoFactory.getDao(HotWordDao.class.getName());
	    }
	    public HotWordDao() {
	        this.setDataSourceName(icityDataSource);
	    }
	    
	    
			//热词搜索 rs 2015年11月10日10:21:32
			public DataSet HotWord(ParameterSet pSet) {
		        DataSet ds=new DataSet();
				String hotword=(String) pSet.getParameter("hotword");
				String ip=(String) pSet.getParameter("ip");
				String id=UUID.randomUUID().toString().replaceAll("-", "");
				SensitivewordFilter swf = new SensitivewordFilter();
				Set<String> set = swf.getSensitiveWord(hotword, 1);
				String sensitive="0";
				if(set.size()==0){
					sensitive="0";
				}else{
					sensitive="1";
				}
				String sql="insert into hot_words (id,hotword,ip,create_time,is_sensitive) values('"+id+"','"+hotword+"','"+ip+"',sysdate,'"+sensitive+"' )";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		            ds.setState(StateType.FAILT);
		            ds.setMessage("数据库操作失败！");
		            return ds;
		        } else {
		            ds.setState(StateType.SUCCESS);
		            return ds;
		        }		
			}
			
			
			//热词搜索 rs 2015年11月10日11:07:17
			public DataSet getHotWord(ParameterSet pSet) {
		        DataSet ds;
				
				String sql="select * from (select count(hotword) count,hotword,is_sensitive from hot_words t group by hotword,is_sensitive  order by count(hotword) desc) where rownum<7 and is_sensitive!='1'";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		        	ds=this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.FAILT);
		            ds.setMessage("数据库操作失败！");
		            return ds;
		        } else {
		        	ds=this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.SUCCESS);
		            return ds;
		        }		
			}

}
