package hlj_qqheNew.app.icity.project;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.project.ProjectQueryDao;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;


public class ZwgkDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(ZwgkDao.class);
	protected static String icityDataSource = "icityDataSource";
	private static ZwgkDao _instance = null;

	public static ZwgkDao getInstance() {
		return (ZwgkDao)DaoFactory.getDao(ZwgkDao.class.getName());
	}

	public ZwgkDao() {
		this.setDataSourceName(icityDataSource);
	}

//政务公开信息指南列表查询

public DataSet select (ParameterSet pSet)
{
 String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务信息公开指南'  ";
	int start = pSet.getPageStart();
	int limit = pSet.getPageLimit();
	if (start == -1 || limit == -1) {
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql,null);
	} else {
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql, start, limit, null);
	}
}
 //信息公开目录
public DataSet selectgkml (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务中心动态'  ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}
}
//年度报告
public DataSet selectndbg (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务信息公开年度报告'   ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}
}
//领导信箱
public DataSet selectdzxx (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.name,PUB_CONTENT.SUMMARY,PUB_CONTENT.content  from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务信息公开意见箱'   ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}
}
public DataSet selectbmbs(ParameterSet pSet)
{  
	 String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='部门办事信息公开'   ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}
}

public DataSet selectbzhjs(ParameterSet pSet)
{  
	 String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='服务标准化建设'   ";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}
}

public DataSet selectview(ParameterSet pSet)
{  
	
		DataSet ds = new DataSet();
        String id=(String) pSet.getParameter("id");
        String sql="select Name,content,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START,SOURCE from PUB_CONTENT where ID='"+id+"' " ;
        int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
            ds.setState(StateType.FAILT);
            ds.setMessage("数据库操作失败！");
            return ds;
        } else {
        	DataSet result = this.executeDataset(sql, null, icityDataSource);
            ds.setState(StateType.SUCCESS);
            return result;
        }	
}

public DataSet selectztxw(ParameterSet pSet)
{  
	String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='专题新闻'   ";
	int start = pSet.getPageStart();
	int limit = pSet.getPageLimit();
	if (start == -1 || limit == -1) {
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql, null);
	} else {
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql, start, limit, null);
	}
}

public DataSet selecttzgg(ParameterSet pSet)
{  
	String sql= "select pub_content.ID,pub_content.name,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='通知公告'   ";
	int start = pSet.getPageStart();
	int limit = pSet.getPageLimit();
	if (start == -1 || limit == -1) {
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql, null);
	} else {
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql, start, limit, null);
	}
}

public DataSet getDown (ParameterSet pSet)
{
	 String id=(String) pSet.getParameter("id");
	 String sql= "select * from attach t where conid='"+id+"' and type='2'  ";
	 return this.executeDataset(sql,null,icityDataSource);
}
public DataSet wzsm(ParameterSet pSet)
{  
	
		DataSet ds = new DataSet();
        String name=(String) pSet.getParameter("name");
        String sql="select Name,content,to_char(valid_date_start,'yyyy-MM-dd') as VALID_DATE_START,SOURCE from PUB_CONTENT where name='"+name+"' " ;
        int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
            ds.setState(StateType.FAILT);
            ds.setMessage("数据库操作失败！");
            return ds;
        } else {
        	DataSet result = this.executeDataset(sql, null, icityDataSource);
            ds.setState(StateType.SUCCESS);
            return result;
        }	
}


public DataSet getTzgg (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.CONTENT,substr(pub_content.name,0,17) as name,to_char(valid_date_start,'MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='通知公告' and rownum<6 and pub_content.rid='"+pSet.remove("pub_content.rid")+"' order by pub_content.ctime desc ";
	 sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
	 return this.executeDataset(sql,null);
}	


public DataSet getTzgg_xx(ParameterSet pSet) {
    DataSet ds=new DataSet();
	String id=(String) pSet.getParameter("id");
	String sql= "select pub_content.ID,pub_content.CONTENT,substr(pub_content.name,0,44) as name,to_char(valid_date_start,'MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='通知公告' and rownum<6 and pub_content.rid='"+pSet.remove("pub_content.rid")+"' and pub_content.id='"+id+"' order by pub_content.ctime desc ";
    int i = this.executeUpdate(sql, null, icityDataSource);
    if (i == 0) {
        ds.setState(StateType.FAILT);
        ds.setMessage("数据库操作失败！");
        return ds;
    } else {
    	DataSet result = this.executeDataset(sql, null, icityDataSource);
        ds.setState(StateType.SUCCESS);
        return result;
    }		
}

public DataSet getZxjj (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.CONTENT,substr(pub_content.name,0,44) as name,to_char(valid_date_start,'MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='中心简介' and rownum<6 and pub_content.rid='"+pSet.remove("pub_content.rid")+"' order by pub_content.ctime desc ";
	 sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
	 return this.executeDataset(sql,null);
}

public DataSet getWzjs (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.CONTENT,substr(pub_content.name,0,44) as name,to_char(valid_date_start,'MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='网站介绍' and rownum<6 and pub_content.rid='"+pSet.remove("pub_content.rid")+"' order by pub_content.ctime desc ";
	 sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
	 return this.executeDataset(sql,null);
}

public DataSet getWzsm (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.CONTENT,substr(pub_content.name,0,44) as name,to_char(valid_date_start,'MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='网站声明' and rownum<6 and pub_content.rid='"+pSet.remove("pub_content.rid")+"' order by pub_content.ctime desc ";
	 sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
	 return this.executeDataset(sql,null);
}

public DataSet getLxwm (ParameterSet pSet)
{
	 String sql= "select pub_content.ID,pub_content.CONTENT,substr(pub_content.name,0,44) as name,to_char(valid_date_start,'MM-dd') as VALID_DATE_START from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='联系我们' and rownum<6 and pub_content.rid='"+pSet.remove("pub_content.rid")+"' order by pub_content.ctime desc ";
	 sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
	 return this.executeDataset(sql,null);
}


}
