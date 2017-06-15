/**   
 * @Title: JXDataExchangeDao.java 
 * @Package km.app.icity.guestbook 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author A18ccms A18ccms_gmail_com   
 * @date 2016-6-13 上午10:59:41 
 * @version V1.0   
 */
package km.app.icity.guestbook;

import java.sql.Connection;

import java.text.SimpleDateFormat;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.types.DateTime;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.sun.star.util.Date;

/**
 * @ClassName: JXDataExchangeDao
 * @Description: 描述
 * @author kongws
 * @date 2016-6-13 上午10:59:41
 */
public class JXDataExchangeDao extends BaseJdbcDao {
	private static Log log_ = LogFactory.getLog(RegcodeMappingDao.class);

	private JXDataExchangeDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static JXDataExchangeDao getInstance() {
		return (JXDataExchangeDao) DaoFactory.getDao(JXDataExchangeDao.class
				.getName());
	}
	public void handleData() {
		// 咨询投诉
		exchangeGuestbook();
		// 更新咨询投诉
		updateExchangeGuestbook();
		
		// 信息公开
		exchangeNotice();
		// 更新信息公开
		updateExchangeNotice();
		updateExchangeNotice2();

		// 标注已经删除的信息公开
		softDelNotice();
		
		// 星级评定
		exchangeStar();
	}

	/**
	 * @Title: softDelNotice
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void softDelNotice() {
		// 查出汇总库有，网办库没有的
		String set_T_NOTICE = "update HZK.t_Notice n set n.is_public='2' where not exists (select 1 from pub_content where id=n.id)";
		Connection icityConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			 DbHelper.update(set_T_NOTICE, new Object[] {}, icityConn);
		} catch (Exception e) {
			log_.error("set_T_NOTICE表is_public=2失败");
		} finally {
			DBSource.closeConnection(icityConn);
		}

	}

	/**
	 * 更新咨询投诉表
	 * 
	 * @Title: updateExchangeGuestbook
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void updateExchangeGuestbook() {
		// 查出已经同步但是之前没有回复的,现在已经回复了的
		String select_GUESTBOOK = "SELECT T.ID,T.DEPART_ID,T.SXID,T.USERNAME,T.EMAIL,T.PHONE,T.ADDRESS,T.TITLE,T.CONTENT,T.TYPE,to_char(T.WRITE_DATE,'yyyy-mm-dd hh24:mi:ss') as WRITE_DATE,T.DEAL_RESULT,to_char(T.DEAL_DATE,'yyyy-mm-dd hh24:mi:ss') as DEAL_DATE FROM GUESTBOOK T WHERE EXCHANGE_FLAG='2' and status='1'";

		String update_T_CONSULT_COMPLAIN = "update T_CONSULT_COMPLAIN t set t.deal_result=? , t.deal_date=to_date(?,'yyyy-mm-dd hh24:mi:ss') , t.exchenge_flag=? , t.status=? where t.id=?";

		// 把已经回复的标志位设置为1
		String update_GUESTBOOK1 = "update guestbook g set g.exchange_flag=? where g.id=? and g.status=? and g.exchange_flag=?";

		Connection icityConn = null;
		Connection hzkConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			hzkConn = DbHelper.getConnection("qzDataSource");
			DataSet ds = DbHelper.query(select_GUESTBOOK, new Object[] {},
					icityConn);
			for (int i = 0; i < ds.getTotal(); i++) {
				JSONObject guestBookdata = ds.getRecord(i);
				Object[] object = {
						// 处理结果
						guestBookdata.getString("DEAL_RESULT"),
						// 处理时间
						guestBookdata.getString("DEAL_DATE"), "0", "1",
						guestBookdata.getString("ID"), };
				int j = DbHelper.update(update_T_CONSULT_COMPLAIN, object,
						hzkConn);
				if (j > 0) {
					int a = DbHelper.update(update_GUESTBOOK1, new Object[] {
							"1", guestBookdata.getString("ID"), "1", "2" },
							icityConn);
				}
			}
		} catch (Exception e) {
			log_.error("咨询投诉表更新失败");
			e.printStackTrace();
		} finally {
			DBSource.closeConnection(icityConn);
			DBSource.closeConnection(hzkConn);
		}
	}

	/**
	 * 
	 * @Title: exchangeGuestbook
	 * @Description: TODO咨询投诉交换方法
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void exchangeGuestbook() {
		// 咨询投诉交换表
		String select_GUESTBOOK = "SELECT T.ID,T.DEPART_ID,T.SXID,T.USERNAME,T.EMAIL,T.PHONE,T.ADDRESS,T.TITLE,T.CONTENT,T.TYPE,to_char(T.WRITE_DATE,'yyyy-mm-dd hh24:mi:ss') as WRITE_DATE,T.DEAL_RESULT,to_char(T.DEAL_DATE,'yyyy-mm-dd hh24:mi:ss') as DEAL_DATE,T.STATUS FROM GUESTBOOK T WHERE EXCHANGE_FLAG='0'";
		String insert_T_CONSULT_COMPLAIN = "insert into T_CONSULT_COMPLAIN values(?,?,?,?,?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?)";

		// 把已经回复的标志位设置为1
		String update_GUESTBOOK1 = "update guestbook g set g.exchange_flag='1' where g.id=? and g.status='1'";

		// 把未回复的标志位设置为2
		String update_GUESTBOOK2 = "update guestbook g set g.exchange_flag='2' where g.id=? and g.status='0'";
		Connection icityConn = null;
		Connection hzkConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			hzkConn = DbHelper.getConnection("qzDataSource");
			DataSet ds = DbHelper.query(select_GUESTBOOK, new Object[] {},
					icityConn);
			for (int i = 0; i < ds.getTotal(); i++) {
				JSONObject guestBookdata = ds.getRecord(i);
				Object[] object = { guestBookdata.getString("ID"),
						guestBookdata.getString("DEPART_ID"),
						guestBookdata.getString("SXID"),
						guestBookdata.getString("USERNAME"),
						guestBookdata.getString("EMAIL"),
						guestBookdata.getString("PHONE"),
						guestBookdata.getString("ADDRESS"),
						guestBookdata.getString("TITLE"),
						guestBookdata.getString("CONTENT"),
						guestBookdata.getString("TYPE"),
						guestBookdata.getString("WRITE_DATE"),
						guestBookdata.getString("DEAL_RESULT"),
						guestBookdata.getString("DEAL_DATE"), "0",
						guestBookdata.getString("STATUS") };
				int j = DbHelper.update(insert_T_CONSULT_COMPLAIN, object,
						hzkConn);
				if (j > 0) {
					DbHelper.update(update_GUESTBOOK1,
							new Object[] { guestBookdata.getString("ID") },
							icityConn);
					DbHelper.update(update_GUESTBOOK2,
							new Object[] { guestBookdata.getString("ID") },
							icityConn);
				}
			}
		} catch (Exception e) {
			log_.error("咨询投诉表交换失败");
			e.printStackTrace();
		} finally {
			DBSource.closeConnection(icityConn);
			DBSource.closeConnection(hzkConn);
		}
	}

	// 信息公开（测试通过）
	private void exchangeNotice() {
		String select_PUB_CONTENT = "select to_char(ctime,'yyyy-mm-dd hh24:mi:ss')as str_ctime,t.* from PUB_CONTENT t WHERE EXCHANGE_FLAG='0' and submit_status='1'";
		String insert_T_NOTICE = "insert into T_NOTICE values(?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,'1')";
		String update_PUB_CONTENT = "update PUB_CONTENT c set c.exchange_flag='1' where c.id=?";
		Connection icityConn = null;
		Connection hzkConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			hzkConn = DbHelper.getConnection("qzDataSource");
			DataSet ds = DbHelper.query(select_PUB_CONTENT, new Object[] {},
					icityConn);
			for (int i = 0; i < ds.getTotal(); i++) {
				JSONObject contentdata = ds.getRecord(i);
				Object[] object = { contentdata.getString("ID"),
						contentdata.getString("RID"),
						contentdata.getString("DEPT_ID"),
						contentdata.getString("CID"),
						contentdata.getString("NAME"),
						contentdata.getString("CONTENT"),
						contentdata.getString("STR_CTIME"),
						contentdata.getString("STR_CTIME"), "0" };
				int j = DbHelper.update(insert_T_NOTICE, object, hzkConn);
				if (j > 0) {
					int a = DbHelper.update(update_PUB_CONTENT,
							new Object[] { contentdata.getString("ID") },
							icityConn);
				}
			}
		} catch (Exception e) {
			log_.error("信息公开：信息公开交换失败");
			e.printStackTrace();
		} finally {
			DBSource.closeConnection(icityConn);
			DBSource.closeConnection(hzkConn);
		}
	}

	// 更新信息公开
	// 发布-->不发布（测试通过）
	private void updateExchangeNotice() {
		String select_PUB_CONTENT = "select to_char(ctime,'yyyy-mm-dd hh24:mi:ss')as str_ctime,t.* from PUB_CONTENT t WHERE EXCHANGE_FLAG='1' and submit_status<>'1'";
		String upate_T_NOTICE = "update T_NOTICE set is_public='0', EXCHENGE_FLAG='0' where id=?";
		String update_PUB_CONTENT = "update PUB_CONTENT c set c.exchange_flag='2' where c.id=?";
		Connection icityConn = null;
		Connection hzkConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			hzkConn = DbHelper.getConnection("qzDataSource");
			DataSet ds = DbHelper.query(select_PUB_CONTENT, new Object[] {},
					icityConn);
			for (int i = 0; i < ds.getTotal(); i++) {
				JSONObject Notice = ds.getRecord(i);
				Object[] object = { Notice.getString("ID") };
				int j = DbHelper.update(

				upate_T_NOTICE, object, hzkConn);
				if (j > 0) {
					int a = DbHelper.update(update_PUB_CONTENT,
							new Object[] { Notice.getString("ID") }, icityConn);
				}
			}
		} catch (Exception e) {
			log_.error("信息公开表：发布-->不发布更新失败");
			e.printStackTrace();
		} finally {
			DBSource.closeConnection(icityConn);
			DBSource.closeConnection(hzkConn);
		}
	}

	// 更新信息公开（测试通过）
	// 不发布-->发布
	private void updateExchangeNotice2() {
		String select_PUB_CONTENT = "select to_char(ctime,'yyyy-mm-dd hh24:mi:ss')as str_ctime,t.* from PUB_CONTENT t WHERE EXCHANGE_FLAG='2' and submit_status='1'";
		String upate_T_NOTICE = "update T_NOTICE set is_public='1', exchenge_flag='0' where id=?";
		String update_PUB_CONTENT = "update PUB_CONTENT c set c.exchange_flag='1' where c.id=?";

		Connection icityConn = null;
		Connection hzkConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			hzkConn = DbHelper.getConnection("qzDataSource");
			DataSet ds = DbHelper.query(select_PUB_CONTENT, new Object[] {},
					icityConn);
			for (int i = 0; i < ds.getTotal(); i++) {
				JSONObject contentdata = ds.getRecord(i);
				Object[] object = { contentdata.getString("ID") };
				int j = DbHelper.update(upate_T_NOTICE, object, hzkConn);
				if (j > 0) {
					int a = DbHelper.update(update_PUB_CONTENT,
							new Object[] { contentdata.getString("ID") },
							icityConn);
				}
			}
		} catch (Exception e) {
			log_.error("信息公开表：不发布-->发布更新失败");
			e.printStackTrace();
		} finally {
			DBSource.closeConnection(icityConn);
			DBSource.closeConnection(hzkConn);
		}
	}

	// 星级评定(测试通过)
	private void exchangeStar() {
		String select_STAR_LEVEL_EVALUATION = "select to_char(t.creator_date,'yyyy-mm-dd hh24:mi:ss') as crdate,t.*,(t.star_level+t.quality_star_level+t.time_star_level+t.major_star_level)/4 as str from STAR_LEVEL_EVALUATION t where t.exchange_flag=0";

		String insert_T_WORK_EVALUATE = "insert into T_WORK_EVALUATE values(?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?)";

		String update_STAR_LEVEL_EVALUATION = "update STAR_LEVEL_EVALUATION c set c.exchange_flag='1' where c.id=?";
		Connection icityConn = null;
		Connection hzkConn = null;
		try {
			icityConn = DbHelper.getConnection(this.getDataSourceName());
			hzkConn = DbHelper.getConnection("qzDataSource");
			DataSet ds = DbHelper.query(select_STAR_LEVEL_EVALUATION,
					new Object[] {}, icityConn);
			for (int i = 0; i < ds.getTotal(); i++) {
				JSONObject stardata = ds.getRecord(i);
				Object[] object = { stardata.getString("ID"),
						stardata.getString("SERVICE_ORG_ID"),
						stardata.getString("SERIAL_NUMBER"),
						stardata.getString("STR"),
						stardata.getString("CRDATE"),
						stardata.getString("EVALUATE_CONTENT"), "0" };
				int j = DbHelper
						.update(insert_T_WORK_EVALUATE, object, hzkConn);
				if (j > 0) {
					int a = DbHelper.update(update_STAR_LEVEL_EVALUATION,
							new Object[] { stardata.getString("ID") },
							icityConn);
				}
			}
		} catch (Exception e) {
			log_.error("星级评定交换到汇总库失败");
			e.printStackTrace();
		} finally {
			DBSource.closeConnection(icityConn);
			DBSource.closeConnection(hzkConn);
		}
	}
}
