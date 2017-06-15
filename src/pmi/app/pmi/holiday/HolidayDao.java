package app.pmi.holiday;

import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONObject;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.db.SqlCreator;

public class HolidayDao extends BaseJdbcDao {
	private HolidayDao() {
		this.setDataSourceName("icpDataSource");
	}

	public static HolidayDao getInstance() {
		return DaoFactory.getDao(HolidayDao.class.getName());
	}

	public DataSet getDateSet(ParameterSet pSet) {
		String sql = "select * from sys_holidays t ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		DataSet ds = this.executeDataset(sql);
		return ds;
	}

	public DataSet modifyDay(ParameterSet pSet) {
		String sql = "update sys_holidays t set t.is_holiday = ?";
		DataSet ds = new DataSet();
		Integer is_holiday = (Integer) pSet.getParameter("t.is_holiday");
		pSet.remove("t.is_holiday");
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int i = this.executeUpdate(sql, new Object[] { is_holiday });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	/**
	 * 获取偏移时间
	 * 
	 * @param fromDate
	 *            参照时间
	 * @param day
	 *            偏移数
	 * @param timeType
	 *            偏移类型：1-工作日；2-自然日；3-小时；4-分钟；5-月；
	 * @return
	 */
	public Date getLimitDate(Date fromDate, int day, int timeType) {
		Date shiftDate = fromDate;
		switch (timeType) {
		case 2:
			shiftDate = new Date(fromDate.getTime() + ((long) day * 24 * 60 * 60 * 1000));
			break;
		case 3:
			shiftDate = new Date(fromDate.getTime() + ((long) day * 60 * 60 * 1000));
			break;
		case 4:
			shiftDate = new Date((long) (fromDate.getTime() + ((long) day * 60 * 1000)));
			break;
		case 5:
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(fromDate.getTime());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.MONTH, day);
			shiftDate = c.getTime();
			break;
		case 1:
			if (day == 0)
				break;
			String sql = "";
			if (day > 0) {
				sql = "select max(datetime) datetime from (select * from sys_holidays s where datetime > trunc(?,'dd') and s.is_holiday = 0 order by datetime) WHERE ROWNUM <= ?";
			} else {
				sql = "select min(datetime) datetime from (select * from sys_holidays s where datetime < trunc(?,'dd') and s.is_holiday = 0 order by datetime desc) WHERE ROWNUM <= ?";
			}
			DataSet ds = this.executeDataset(sql, new Object[] { fromDate, day });
			shiftDate = (java.util.Date) JSONObject.toBean(((JSONObject) ds.getRecord(0).get("DATETIME")),
					java.util.Date.class);
			break;
		default:
			break;
		}
		return shiftDate;
	}

	/**
	 * 获取两个日期之间的工作日
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param timeType
	 *            偏移类型：1-工作日；2-自然日；3-小时；4-分钟；5-月；
	 * @return
	 */
	public DataSet getHolidays(Date fromDate, Date toDate, int timeType) {
		String sql = "select count(1) day from sys_holidays where is_holiday = 0 and datetime > ? and datetime <= ?";
		DataSet ds = this.executeDataset(sql, new Object[] { fromDate, toDate });
		return ds;
	}
}
