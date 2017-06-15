package km.app.icity.guestbook;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

public class RegcodeMappingDao extends BaseJdbcDao {
	private static Log log_ = LogFactory.getLog(RegcodeMappingDao.class);

	private RegcodeMappingDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static RegcodeMappingDao getInstance() {
		return (RegcodeMappingDao) DaoFactory.getDao(RegcodeMappingDao.class.getName());
	}

	public void handleData() {
		log_.error("------------区划映射开始----------");
		// scode=0表示还未进行映射。只更新未映射过的数据。
		String sql_GUESTBOOK = "update guestbook g set g.scode =( "
				+ "select distinct(r.scode) from regcodemapping r, guestbook g where region_id "
				+ "= r.kmcode and r.id = ?) " + "where g.region_id = (select distinct (r.kmcode) "
				+ "from regcodemapping r, guestbook g " + "where g.region_id = r.kmcode and r.id = ?) and g.scode=0";

		String sql_PUB_CONTENT = "update pub_content c set c.scode=( "
				+ "select distinct(r.scode) from regcodemapping r,pub_content c where rid "
				+ "= r.kmcode and r.id = ?) " + "where rid = (select distinct (r.kmcode) "
				+ "from regcodemapping r, pub_content p " + "where rid = r.kmcode and r.id = ?) and c.scode=0";

		// 这是事项系统的一张表，也需要做区划映射。（暂留）
		/*
		 * String sql_PROJECT_ITEM = "update project_item p set p.scode=( " +
		 * "select distinct(r.scode) from regcodemapping r,project_item p where region_code "
		 * + "= r.kmcode and r.id = ?) " +
		 * "where p.region_code = (select distinct (r.kmcode) " +
		 * "from regcodemapping r, project_item p " +
		 * "where p.region_code = r.kmcode and r.id = ?) ";
		 */
		Connection conn = null;
		int i_guestbook = 0;
		int i_pub_content = 0;
		for (int i = 0; i < 25; i++) {
			// 对guestbook表进行映射
			try {
				conn = DbHelper.getConnection(this.getDataSourceName());
				conn.setAutoCommit(false);
				int j = DbHelper.update(sql_GUESTBOOK, new Object[] { i, i }, conn);
				if (j > 0) {
					conn.commit();
					i_guestbook += j;
				}

			} catch (Exception e) {
				log_.error("guestbook表区划映射失败");
			} finally {
				DBSource.closeConnection(conn);
			}

			// 对PUB_CONTENT表进行映射
			try {
				conn = DbHelper.getConnection(this.getDataSourceName());
				conn.setAutoCommit(false);
				int j = DbHelper.update(sql_PUB_CONTENT, new Object[] { i, i }, conn);
				if (j > 0) {
					conn.commit();
					i_pub_content += j;
				}
			} catch (Exception e) {
				log_.error("PUB_CONTENT表区划映射失败");
			} finally {
				DBSource.closeConnection(conn);
			}

			// 对PROJECT_ITEM表进行映射
			// 这是事项系统的一张表，也需要做区划映射。（暂留）
			/*
			 * try { conn=DbHelper.getConnection("otherDataSource");
			 * conn.setAutoCommit(false); int j =
			 * DbHelper.update(sql_PROJECT_ITEM, new Object[] { i, i }, conn);
			 * if (j > 0) { conn.commit(); } log_.error("PROJECT_ITEM表区划映射成功");
			 * } catch (Exception e) { log_.error("PROJECT_ITEM表区划映射失败"); }
			 * finally { DBSource.closeConnection(conn); }
			 */

		}
		log_.error("GUESTBOOK表区划映射成功,此次映射共" + i_guestbook + "条");
		log_.error("PUB_CONTENT表区划映射成功,此次映射共" + i_pub_content + "条");
		log_.error("------------区划映射结束----------");
	}
}
