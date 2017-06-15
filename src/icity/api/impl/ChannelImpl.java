package api.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import app.icity.sync.UploadUtil;
import core.util.CommonUtils_api;

@RestType(name = "api.channel", descript = "栏目、内容相关接口")
public class ChannelImpl extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ChannelImpl.class);
	public static ChannelImpl getInstance(){
		return DaoFactory.getDao(ChannelImpl.class.getName());
	}
	/**
	 * 获取根节点栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getRootChannel(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String sql = "select id,name,parent,rid from pub_channel where parent='root' ";
			ds = DbHelper.query(sql, null, conn);
			conn.commit();
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		}catch(Exception e){
	    	e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	/**
	 * 根据栏目名称获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelByName(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String name = (String)pset.getParameter("name");
			String sqlChannel = "select id,name,parent,rid from pub_channel where name=? ";
			DataSet ds1 = DbHelper.query(sqlChannel, new Object[]{name}, conn);
			if(ds1.getTotal()==1){
				String sql = "select id,name,parent,rid from pub_channel where parent=(select id from pub_channel where name=? )";
				ds = DbHelper.query(sql, new Object[]{name}, conn);
				ds.setState(StateType.SUCCESS);
				ds.setMessage("OK");
			}else if(ds1.getTotal()==0){
				ds.setState(StateType.FAILT);
				ds.setMessage("栏目【"+name+"】不存在");
			}else if(ds1.getTotal()>1){
				ds.setState(StateType.FAILT);
				ds.setMessage("栏目【"+name+"】有多个，请通过栏目id进行查询");
			}
		}catch(Exception e){
	    	e.printStackTrace();			
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	/**
	 * 根据栏目id获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelById(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String id = (String)pset.getParameter("id");			
			String sql = "select id,name,parent,rid from pub_channel where parent=?";
			ds = DbHelper.query(sql, new Object[]{id}, conn);
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");			
		}catch(Exception e){
	    	e.printStackTrace();			
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	/**
	 * 根据栏目id获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByCid(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String cid = (String)pset.getParameter("cid");
			String type = (String)pset.getParameter("type");//0文字1图片2视频3链接
			String sql = "select id,cid,rid,name,to_char(ctime,'yyyy-mm-dd hh24:mi:ss') ctime from pub_content where submit_status='1' and  cid=? ";
			if(StringUtil.isNotEmpty(type)){
				sql+=" and type=? order by ctime desc,id desc";
				ds = DbHelper.query(sql, new Object[]{cid,type}, conn);
			}else{
				ds = DbHelper.query(sql, new Object[]{cid}, conn);
			}			
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		}catch(Exception e){
	    	e.printStackTrace();
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	/**
	 * 根据内容id获取内容详细
	 * @param pset
	 * @return
	 */
	public DataSet getContentById(ParameterSet pset){
		DataSet ds = new DataSet();
		DataSet ds1;
		DataSet ds2;
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String id = (String)pset.getParameter("id");
			String sql = "select id,cid,rid,name,content,type,url,summary,source,dept_name,creator,ctime from pub_content where id=? ";
			String sqlAtt = "select id,conid,name,yname,type,docid from ATTACH where conid=? ";
			ds1 = DbHelper.query(sql, new Object[]{id}, conn);
			ds2 = DbHelper.query(sqlAtt, new Object[]{id}, conn);
			if (ds2.getTotal() > 0) {
				// 这里加入下载代码
				JSONArray upfile = ds2.getJAData();
				for (int i = 0; i < upfile.size(); i++) {
					//下载文件到项目路径本地
					String fileName = (String) ((JSONObject) upfile.get(i)).get("NAME");
					String fileType = (String) ((JSONObject) upfile.get(i)).get("TYPE");
					String doc_id = (String) ((JSONObject) upfile.get(i)).get("DOCID");
					UploadUtil.downloadFile(fileName, doc_id, fileType);
				}
			}
			JSONObject ja1 = ds1.getJOData();
			JSONArray ja2 = ds2.getJAData();
			JSONObject jo = new JSONObject();
			jo.put("content", ja1);
			jo.put("attach", ja2);
			conn.commit();
			ds.setTotal(ds1.getTotal());
			ds.setData(Tools.jsonToBytes(jo));
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		}catch(Exception e){
	    	e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }
		return ds;
	}
	/**
	 * 根据栏目名称获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByName(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String name = (String)pset.getParameter("name");
			String type = (String)pset.getParameter("type");//0文字1图片2视频3链接
			String sql = "select id,cid,rid,name,to_char(ctime,'yyyy-mm-dd hh24:mi:ss') ctime from pub_content where submit_status='1' and  cid=(select id from pub_channel where name=?)  ";
			if(StringUtil.isNotEmpty(type)){
				sql+=" and type=? order by ctime desc,id desc";
				ds = DbHelper.query(sql, new Object[]{name,type}, conn);
			}else{
				ds = DbHelper.query(sql, new Object[]{name}, conn);
			}
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		}catch(Exception e){
	    	e.printStackTrace();			
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	/**
	 * 获取内容列表
	 * @param pset{page,limit,cname,cid,region_code,startTime,endTime,deptId,name}
	 * @return
	 */
	public DataSet getContentListByPage(ParameterSet pset){
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{		
			StringBuilder channel = new StringBuilder();	
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();		
			
			String cname = (String)pset.get("cname");	//cname	是	String	栏目名 与 栏目id对应 	
			String cid = (String)pset.get("cid");	//cid	是	String	
			if(!StringUtil.isEmpty(cname)){
				channel.append(" and p.name=? ");
				paramValue.add(cname);
			}else{
				if(!StringUtil.isEmpty(cid)){
					channel.append(" and p.id=? ");
					paramValue.add(cid);
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage("栏目名称和栏目ID不可同时为空！");
					return ds;
				}
			}			
			String region_code = (String)pset.get("region_code");	//region_code	否	String	区划			
			if(!StringUtil.isEmpty(region_code)){
				channel.append(" and p.rid=? ");
				paramValue.add(region_code);
				whereValue.append(" and t.rid=? ");
				paramValue.add(region_code);
			}else{
				channel.append(" and p.rid=? ");
				paramValue.add(SecurityConfig.getString("WebRegion"));
				whereValue.append(" and t.rid=? ");
				paramValue.add(SecurityConfig.getString("WebRegion"));
			}			
			String type = (String)pset.get("type");	//type	否	String	//type=1(图片)
			if(!StringUtil.isEmpty(region_code)){
				whereValue.append(" and t.type=? ");
				paramValue.add(type);
			}
			String key = (String)pset.get("key");	//key	否	String	根据内容名称模糊查询
			if(!StringUtil.isEmpty(region_code)){
				whereValue.append(" and t.name like ? ");
				paramValue.add("%"+key+"%");
			}
			String sql_content = "select t.id,t.cid,t.rid,t.name,to_char(t.ctime,'yyyy-mm-dd hh24:mi:ss') ctime from pub_content t where t.submit_status='1' and t.cid=(select p.id from pub_channel p where 1=1 "+channel+")";
			sql_content+=whereValue.toString();
			String startTime = (String)pset.get("startTime");
			String entTime = (String)pset.get("endTime");
			if(!StringUtil.isEmpty(startTime)){
				sql_content+=" and to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss') <= t.ctime";
			}
			if(!StringUtil.isEmpty(entTime)){
				sql_content+=" and t.ctime<=to_date('"+entTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			String orderby = (String)pset.get("orderby");	//orderby	否	String	排序  //desc或者asc（根据时间排序）			
			if(!StringUtil.isEmpty(orderby)){
				sql_content += " order by t.ctime desc,t.id "+orderby;
			}else{
				sql_content += " order by t.ctime desc,t.id desc ";
			}
			int page = Integer.parseInt(pset.getParameter("page").toString());
			int limit = Integer.parseInt(pset.getParameter("limit").toString());
			int start = (page - 1) * limit;
			ds = DbHelper.query(sql_content, start, limit, paramValue.toArray(), conn,"icityDataSource");
			ds.setState(StateType.SUCCESS);
			ds.setMessage("OK");
		}catch(Exception e){
	    	e.printStackTrace();			
			ds.setState(StateType.FAILT);
		    ds.setMessage(e.toString());
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}

}
