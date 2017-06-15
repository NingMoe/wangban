package app.db.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.icore.log.Logger;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;





import app.db.bean.ParameterBean;
import app.db.bean.ProxyBean;
import app.db.bean.ProxyInfo;
import app.db.util.DbProvider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/// <summary>
/// 数据库代理，实现透明的数据库的CURD操作
/// created by jac at 2011-03-06
/// </summary>
public class DbManager extends BaseJdbcDao{
	private static DbManager _instance = null;
	private static final Logger log = Logger.getLogger(DbManager.class);
    /// <summary>
    /// 静态方法,返回一个Singleton的实例
    /// </summary>
    public static DbManager instance()
    {
    	synchronized (DbManager.class)
    	
        {
            if (_instance == null)
            {
                _instance = new DbManager();
            }
        }
        return _instance;
    }
    
    /// <summary>
    /// 执行添加修改操作
    /// </summary>
    /// <param name="code">代理SQL编码</param>
    /// <param name="entity">需要绑定到SQL语句中的参数</param>
    /// <param name="err">用于返回错误信息</param>
    /// <returns>是否执行成功</returns>
    public DataSet execute(String code,JSONObject entity)
    {
    	code = code.toUpperCase();
    	DataSet sr = new DataSet();
    	
		Boolean re = false;
		try
		{
			ProxyBean pBean = ProxyInfo.getInstance().get(code);
			String dbSql ="";
			List<ParameterBean> dbParas= null;
			String dbOther = null;
			if (pBean != null) {
				dbSql = pBean.getSql();
				dbParas = pBean.getParameters();
				List<ParameterBean> dbConstant = pBean.getConstants();
				dbOther = pBean.getOther();
				
				if(dbConstant != null && dbConstant.size()>0)
				{
					String tpName = "";
					String tpDef = "";
					Boolean tpMust =false;
					for(int i=0;i<dbConstant.size();i++)
					{
						ParameterBean b = dbConstant.get(i);
						tpName = b.getName();
						tpDef = b.getDef();
						tpMust = b.getMust();
						dbSql = dbSql.replace("#"+tpName, DbProvider.formatPostString(entity, tpName,tpDef,tpMust));
					}
				}
			}
			else
			{
				throw new Exception("编号为【"+code+"】的没有对应的版本");
			}
			
			Object[] params = null;
			if(dbParas != null)
			{
				params = new Object[dbParas.size()];
				for(int i=0;i<dbParas.size();i++)
				{
					ParameterBean b = dbParas.get(i);
					String pName = b.getName();
					String pType = b.getType();
					String pDef = b.getDef();
					Boolean pMust = b.getMust();
					params[i] = formatParameter(entity, pName, pType, pDef, pMust);
				}
			}
			if(StringUtils.isNotEmpty(dbOther)){
				dbSql += " " + dbOther;
			}
			re = this.executeUpdate(dbSql,params,pBean.getDataSource()) >0 ? true :false;
			if(re){
				sr.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			sr.setState(StateType.FAILT);
			sr.setMessage(e.getMessage());
		}
        return sr;
    }
    
  /// <summary>
    /// 执行添加修改操作
    /// </summary>
    /// <param name="code">代理SQL编码</param>
    /// <param name="entity">需要绑定到SQL语句中的参数</param>
    /// <param name="err">用于返回错误信息</param>
    /// <returns>是否执行成功</returns>
    public DataSet batch(String code,JSONObject entity,JSONArray value)
    {
    	code = code.toUpperCase();
    	DataSet sr = new DataSet();
		Boolean re = false;
		Connection conn = null;
		ProxyBean pBean = null;
		try
		{
			pBean = ProxyInfo.getInstance().get(code);
			//this.setDataSourceName(pBean.getDataSource());
			String dbSql ="";
			List<ParameterBean> dbParas= null;
			String dbOther = "";
			if (pBean != null) {
				dbSql = pBean.getSql();
				dbParas = pBean.getParameters();
				List<ParameterBean> dbConstant = pBean.getConstants();
				dbOther = pBean.getOther();
				
				if(dbConstant != null && dbConstant.size()>0)
				{
					String tpName = "";
					String tpDef = "";
					Boolean tpMust =false;
					for(int i=0;i<dbConstant.size();i++)
					{
						ParameterBean b = dbConstant.get(i);
						tpName = b.getName();
						tpDef = b.getDef();
						tpMust = b.getMust();
						dbSql = dbSql.replace("#"+tpName, DbProvider.formatPostString(entity, tpName,tpDef,tpMust));
					}
				}
			}
			else
			{
				throw new Exception("编号为【"+code+"】的没有对应的版本");
			}
			if(StringUtils.isNotEmpty(dbOther)){
				dbSql += " "+dbOther;
			}
			
			conn = this.getConnection(pBean.getDataSource());
			conn.setAutoCommit(false);
			for(int ti =0;ti<value.size();ti++)
			{
				JSONObject jo = value.getJSONObject(ti);
				String pName = "";
				String pType = "";
				String pDef = "";
				Boolean pMust =false;
				if(dbParas == null){
					break;
				}
				Object[] params = new Object[dbParas.size()];
				for(int i=0;i<dbParas.size();i++)
				{
					ParameterBean b = dbParas.get(i);
					
					pName = b.getName();
					pType = b.getType();
					pDef = b.getDef();
					pMust = b.getMust();
					
					params[i] = formatParameter(jo, pName, pType, pDef, pMust);
				}
				DbHelper.update(dbSql, params, conn);
			}
			// 事务提交
			conn.commit();
			// 设置为自动提交,改为TRUE
			conn.setAutoCommit(true);
			re = true;
			if(re){
				sr.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {

			log.error(e.getMessage(),e);
			sr.setState(StateType.FAILT);
			sr.setMessage(e.getMessage());
			try {
				// 产生的任何SQL异常都需要进行回滚,并设置为系统默认的提交方式,即为TRUE
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException se1) {
				//se1.printStackTrace();
			}
		}finally{
			if(conn != null && pBean != null){
				DBSource.closeConnection(conn);
			}
		}
        return sr;
    }
    
    /// <summary>
    /// 查询并返回单个实体
    /// </summary>
    /// <param name="code">代理SQL编码</param>
    /// <param name="entity">需要绑定到SQL语句中的参数</param>
    /// <param name="err">用于返回错误信息</param>
    /// <returns>单个实体对象Map</returns>
	public DataSet getEntity(String code,JSONObject entity)
    {
		code = code.toUpperCase();
		DataSet ds = (DataSet)getCollection(code,entity,null,0,-1);
		JSONArray ja = ds.getJAData();
		if(ja != null && ja.size()>0){
			ds.setData(Tools.jsonToBytes(ja.getJSONObject(0)));
			ds.setTotal(1);
		}else{
			ds.setTotal(0);
		}
		return ds;
    }
	
	/// <summary>
    /// 查询并返回实体集合
    /// </summary>
    /// <param name="code">代理SQL编码</param>
    /// <param name="entity">需要绑定到SQL语句中的参数</param>
    /// <param name="err">用于返回错误信息</param>
    /// <returns>实体对象Map的集合</returns>
	public DataSet getCollection(String code,JSONObject entity,ParameterSet pSet,int start,int limit)
    {
		code = code.toUpperCase();
		//int curPage = start;
		//int pageSize = limit;
		
		
		//curPage = pageSize*curPage;
		DataSet sr = new DataSet();
		
		try
		{
			ProxyBean pBean = ProxyInfo.getInstance().get(code);
			
			String dbSql ="";
			List<ParameterBean> dbParas= null;
			String dbOther = "";
			if (pBean != null) {
				dbSql = pBean.getSql();
				dbParas = pBean.getParameters();
				List<ParameterBean> dbConstant = pBean.getConstants();
				dbOther = pBean.getOther();
				
				if(dbConstant != null && dbConstant.size()>0)
				{
					String tpName = "";
					String tpDef = "";
					Boolean tpMust =false;
					for(int i=0;i<dbConstant.size();i++)
					{
						ParameterBean b = dbConstant.get(i);
						tpName = b.getName();
						tpDef = b.getDef();
						tpMust = b.getMust();
						dbSql = dbSql.replace("#"+tpName, DbProvider.formatPostString(entity, tpName,tpDef,tpMust));
					}
				}
			}
			else
			{
				throw new Exception("编号为【"+code+"】的没有对应的版本");
			}
			
			Object[] params = null;
			if(dbParas != null)
			{
				params = new Object[dbParas.size()];
				for(int i=0;i<dbParas.size();i++)
				{
					ParameterBean b = dbParas.get(i);
					String pName = b.getName();
					String pType = b.getType();
					String pDef = b.getDef();
					Boolean pMust = b.getMust();
					params[i] = formatParameter(entity, pName, pType, pDef, pMust);
				}
			}
			
			dbSql = DbProvider.getPreparedSQL(dbSql, params);
			if(pSet != null){
				dbSql = SqlCreator.getSimpleQuerySql(pSet, dbSql,this.getDataSource(pBean.getDataSource()));
			}
			
			if(StringUtils.isNotEmpty(dbOther)){
				dbSql += " "+ dbOther;
			}
			
			if(limit==-1){
				sr = this.executeDataset(dbSql,null,pBean.getDataSource());
			}else{
				sr = this.executeDataset(dbSql,start,limit,null,pBean.getDataSource());
			}
			
		} catch (Exception e) {

			log.error(e.getMessage(),e);
			sr.setState(StateType.FAILT);
			sr.setMessage(e.getMessage());
		}finally{
		}
        return sr;
    }
	
	
	private Object formatParameter(JSONObject entity,String pName,String pType,String pDef,Boolean pMust) throws Exception{
		Object re = null;
		if(pType.equals("string"))
		{
			re = DbProvider.formatPostString(entity, pName,pDef,pMust);
		}
		else if(pType.equals("int"))
		{
			re = DbProvider.formatPostNumber(entity, pName,Integer.parseInt(pDef),pMust);
		}
		else if(pType.equals("long"))
		{
			re = DbProvider.formatPostLong(entity, pName,Long.parseLong(pDef),pMust);
		}
		else if(pType.equals("date"))
		{
			String d = DbProvider.formatPostString(entity, pName,pDef,pMust);
			java.util.Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(d);
			re = new java.sql.Date(dt.getTime());
		}
		else if(pType.equals("datetime"))
		{
			String d = DbProvider.formatPostString(entity, pName,pDef,pMust);
			try{
				java.util.Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d);
				re = new java.sql.Timestamp(dt.getTime());
			}
			catch(Exception ex11){
				java.util.Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(d);
				re = new java.sql.Timestamp(dt.getTime());
			}
		}
		else
		{
			throw new Exception("参数类型为【"+pType+"】未定义");
		}
		return re;
	}
}
