package syan.app.icity.project;

import java.sql.Connection;
import java.sql.SQLException;

import org.exolab.castor.jdo.QueryException;

import com.commnetsoft.proxy.util.MD5;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

public class PasswordDao extends BaseJdbcDao {
	

	public static PasswordDao getInstance() {
		return DaoFactory.getDao(PasswordDao.class.getName());
	}

	/**
	 * 通过登录名、真实姓名、身份证号获取用户列表
	 * @throws SQLException 
	 */
	public DataSet getUser(String Account, String Name, String Card_No) throws SQLException{
		DataSet ds = null;
		Connection conn = DbHelper.getConnection("icityDataSource");
		String sql = "select id from uc_user where account = ? and name = ? and card_no = ? ";
		ds = DbHelper.query(sql, new Object[] { Account, Name, Card_No }, conn);
		try {
			conn.commit();
		}  finally {
	    	DBSource.closeConnection(conn);
		}
		return ds;
	}
	/**
	 * 通过Id重置密码为123456
	 * @throws SQLException
	 */
	public int resetPwd(String Id) throws SQLException {
		Connection conn = DbHelper.getConnection("icityDataSource");
		String sql = "update uc_user set password = ? where id = ? ";
		int succCount = DbHelper.update(sql, new Object[] { MD5.MD5Encode("123"), Id  }, conn);
		try {
			conn.commit();
		} finally {
	    	DBSource.closeConnection(conn);
		}
		return succCount;
	}
}
