package app.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.inspur.util.db.DbHelper;

public class IdGenereator {

	private static IdGenereator _instance = null;
	private Queue<Long> queue = new ConcurrentLinkedQueue<Long>();
	private AtomicInteger counter = new AtomicInteger(0);

	/// <summary>
	/// 静态方法,返回一个Singleton的实例
	/// </summary>
	public static IdGenereator instance() {
		synchronized (IdGenereator.class) {
			if (_instance == null) {
				_instance = new IdGenereator();
			}
		}
		return _instance;
	}

	private IdGenereator() {
		loadIds();
		new Worker();
	}

	public long getId() {
		Long id = queue.poll();
		if (id == null) {
			loadIds();
			id = queue.poll();
		}
		counter.set(counter.get() - 1);
		return id;
	}

	private void loadIds() {
		if (counter.get() > 20)
			return;
		while (counter.get() < 20) {
			Connection conn = DbHelper.getConnection("icityDataSource");
			PreparedStatement pstmt = null;
			try {
				ResultSet rs = null;
				Boolean re = false;
				long firstId = 0L;
				try {
					String csql = "select currentid from light_sequence_id t where id=1";

					pstmt = conn.prepareStatement(csql);
					rs = pstmt.executeQuery();

					if (rs.next()) {
						firstId = rs.getLong(1);
					}
					rs.close();

					pstmt = conn.prepareStatement("update light_sequence_id set currentid = ? where id=1");
					pstmt.setLong(1, firstId + 100 * 100);

					pstmt.execute();
				} catch (Exception ex) {
					firstId = 10000001;
				}
				for (long i = 0; i < 100; i++) {
					boolean flag = queue.offer(firstId + i * 100);
				}
				counter.set(counter.get() + 100);
			} catch (Exception e) {
			} finally {
				try {
					if(pstmt!=null){
						pstmt.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				DbHelper.closeConnection(conn);
			}
		}
	}

	class Worker implements Runnable {

		public Worker() {
			new Thread(this).start();
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(120000);
					loadIds();
				} catch (Throwable e) {
				}
			}
		}

	}
}
