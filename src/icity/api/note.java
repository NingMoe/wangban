package api;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

@RestType(name = "api.note", descript = "便签相关接口")
public class note {
	public DataSet addNote(ParameterSet pset){
		String Content = (String)pset.getParameter("content");
		String UserId = (String)pset.getParameter("userid");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			if(StringUtil.isEmpty(UserId)){
				throw new Exception("用户ID为空");
			}
			String Id = UUID.randomUUID().toString();
			String TimeStr = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date().getTime());
			String sql = "INSERT INTO NOTE_DATA (ID,CONTENT,USERID,MODIFYTIME) "
						+ "VALUES ('" + Id + "','" + Content + "','" + UserId + "',"
						+ "TO_DATE('" + TimeStr + "', 'yyyy-mm-dd hh24:mi:ss')) ";
			DbHelper.update(sql, null, conn);
			conn.commit();
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
	
	public DataSet deleteNote(ParameterSet pset){
		String Id = (String)pset.getParameter("id");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			if(StringUtil.isEmpty(Id)){
				throw new Exception("便签ID为空");
			}
			String sql = "DELETE FROM NOTE_DATA "
						+ "WHERE ID = '" + Id + "' ";
			DbHelper.update(sql, null, conn);
			conn.commit();
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
	
	public DataSet updateNote(ParameterSet pset){
		String Content = (String)pset.getParameter("content");
		String Id = (String)pset.getParameter("id");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			if(StringUtil.isEmpty(Id)){
				throw new Exception("便签ID为空");
			}
			String TimeStr = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date().getTime());
			String sql = "UPDATE NOTE_DATA "
						+ "SET CONTENT = '" + Content + "', "
						+ "MODIFYTIME = TO_DATE('"+ TimeStr +"', 'yyyy-mm-dd hh24:mi:ss') "
						+ "WHERE ID = '" + Id + "' ";
			DbHelper.update(sql, null, conn);
			conn.commit();
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

	public DataSet getNoteList(ParameterSet pset){
		String Start = StringUtil.isEmpty((String)pset.getParameter("start")) ? "0" : (String)pset.getParameter("start");
		String Limit = StringUtil.isEmpty((String)pset.getParameter("limit")) ? "1000" : (String)pset.getParameter("limit");
		String UserId = (String)pset.getParameter("userid");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			if (StringUtil.isEmpty(UserId)){
				throw new Exception("用户ID为空");
			}
			String sql = "SELECT Id, CONTENT, TO_CHAR(MODIFYTIME,'yyyy-mm-dd hh24:mi:ss') MODIFYTIME "
						+ "FROM NOTE_DATA "
						+ "WHERE USERID = '" + UserId + "' "
						+ "ORDER BY MODIFYTIME DESC";
			ds = DbHelper.query(sql, Integer.valueOf(Start), Integer.valueOf(Limit), null, conn,"icityDataSource");
			conn.commit();
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

	public DataSet getNoteDetail(ParameterSet pset){
		String Id = (String)pset.getParameter("id");
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			if (StringUtil.isEmpty(Id)){
				throw new Exception("便签ID为空");
			}
			String sql = "SELECT Id, CONTENT, TO_CHAR(MODIFYTIME,'yyyy-mm-dd hh24:mi:ss') MODIFYTIME "
						+ "FROM NOTE_DATA "
						+ "WHERE Id = '" + Id + "' ";
			ds = DbHelper.query(sql, null, conn);
			conn.commit();
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
