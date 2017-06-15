package hlj_qqheNew.app.icity.project;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.cpa.persistence.convertor.IntegerToLong;
import org.eclipse.jetty.util.security.Credential.MD5;

import app.icity.project.ProjectQueryDao;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

import exchange.entity.datacheckrequest.DataRow;

public class SendMessage extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(SendMessage.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String mssqlDataSource = "mssqlDataSource";
	private static String vcode = "1";
	public static SendMessage getInstance() {
		return DaoFactory.getDao(SendMessage.class.getName());
	}

	public SendMessage() {
		this.setDataSourceName(icityDataSource);
	}

	public static void createRandomVcode() {
		// 验证码
		vcode = "";
		for (int i = 0; i < 6; i++) {
			vcode = vcode + (int) (Math.random() * 9);
		}

	}

	// 发送短信验证码
	public DataSet message_send(ParameterSet pSet) {
	    Date date = new Date();
		DataSet ds = new DataSet();
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time1 = format1.format(date);
		SendMessage.createRandomVcode();
		String mescode = vcode;
		String phone = (String) pSet.getParameter("phone");
		//String sql = "insert into InBox(mbno,Msg,ArriveDate,ArriveDateTime,ArriveTime) values ('"+ phone+ "','您的短信验证码为:"+ mescode+ "','"+ time+ "','"+ time1+ "','" + time2 + "');";
		String sql = "insert into PUB_SMS (id,smscontent,sendtime,status,telephone) values ('"+ Tools.getUUID32()+"','您的短信验证码为:"+ mescode+ "',to_date('"+ time1+ "','yyyy-mm-dd hh24:mi:ss'),'0','"+ phone+ "')";
		int i = this.executeUpdate(sql, null, icityDataSource);
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setMessage(vcode);//短信验证码
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
	
	public DataSet sendGet(String id, String phonestr, String message, String sendtime) {
		DataSet ds = new DataSet();
		String[] d = sendtime.split(" ");
		String sql = "insert into InBox(mbno,Msg,ArriveDate,ArriveDateTime,ArriveTime) values ('"+ phonestr+ "','"+message+"','"+ d[0]+ "','"+ sendtime+ "','" + d[1] + "');";
		int i = executeUpdate(sql, null, mssqlDataSource);
		if (i == 0) {
			ds.setMessage("0");
			ds.setState(StateType.FAILT);
		} else {
			ds.setMessage("1");
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

}
