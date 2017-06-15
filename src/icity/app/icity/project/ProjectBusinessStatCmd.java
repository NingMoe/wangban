/**  
 * @Title: ProjectBusinessStatCmd.java 
 * @Package icity.project 
 * @Description: 事项统计及办件查询 
 * @date 2013-8-20 上午11:59:28 
 * @version V1.0  
 */
package app.icity.project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.sf.json.JSONObject;
import app.icity.project.ProjectBusinessStatDao;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

/**
 * @ClassName: ProjectBusinessStat
 * @Description: 事项统计及办件查询
 * @date 2013-8-20 上午11:59:28
 */

public class ProjectBusinessStatCmd extends BaseQueryCommand {

	private final static String CACHE_KEY_FLAG = "ProjectBusinessStatCmd";
	public static final int FD_OFTHISMONTH = 1; // 本月第一天
	public static final int LD_OFTHISMONTH = 2; // 本月最后一天
	public static final int FD_OFLASTMONTH = 3; // 上月第一天
	public static final int LD_OFLASTMONTH = 4; // 上月最后一天
	public static final int TODAY = 5; // 今天
	public static final int YESTERDAY = 6; // 昨天
	public static final int FD_OFTHISYEAR = 7; // 本年度第一天
	public static final int LD_OFTHISYEAR = 8; // 本年度最后一天

	/**
	 * @Description: 办件信息表格取前50条记录进行滚动显示
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	public DataSet getBusinessListTab(ParameterSet pSet) {
		String webRegion = SecurityConfig.getString("WebRegion");
		String key = "getBusinessListTab_" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);

		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds = ProjectBusinessStatDao.getInstance().getBusinessListTab(pSet);
					if (ds != null && ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		return ds;
	}

	/**
	 * @Description: 根据受理编号模糊查询业务概要信息
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	public DataSet getBusinessListById(ParameterSet pSet) {
		return ProjectBusinessStatDao.getInstance().getBusinessProcessById(pSet);
	}

	/**
	 * @Description: 根据申办流水号和申办人查询办件
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	/*
	 * public DataSet BusinessQueryByNameAndId(){ ParameterSet pSet =
	 * getParameterSet(); return
	 * ProjectBusinessStatDao.getInstance().BusinessQueryByNameAndId(pSet); }
	 */
	/**
	 * @Description: 根据申办流水号查询办件的过程
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	public DataSet getBusinessProcessById(ParameterSet pSet) {
		return ProjectBusinessStatDao.getInstance().getBusinessProcessById(pSet);
	}

	/*
	 * public DataSet getBusinessSPSJById(){ ParameterSet pSet =
	 * getParameterSet(); return
	 * ProjectBusinessStatDao.getInstance().getBusinessSPSJById(pSet); }
	 */

	/**
	 * @Description: 办件统计列表
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	public DataSet getBusinessStat(ParameterSet pSet) {
		try {
			String webRegion = SecurityConfig.getString("WebRegion");
			String key = "icity_businessStat_" + webRegion;
			DataSet object = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
			if (object == null) {
				synchronized (key.intern()) {
					object = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
					if (object == null) {
						object = ProjectBusinessStatDao.getInstance().getBusinessStat(pSet);
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, object);
					}
				}
			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataSet reError = new DataSet();
		reError.setState(StateType.FAILT);
		reError.setMessage("服务器正忙，请稍后...");
		return reError;
	}

	public static String getOneDay(Calendar calendar, int status) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (status == FD_OFTHISMONTH) {
			// 获取本月第一天
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		} else if (status == LD_OFTHISMONTH) {
			// 获取本月最后一天
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.roll(Calendar.DAY_OF_MONTH, -1);
		} else if (status == FD_OFLASTMONTH) {
			// 获取上个月第一天
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, -1);
		} else if (status == LD_OFLASTMONTH) {
			// 获取上个月最后一天
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		} else if (status == TODAY) {
			// 获取今天的日期
		} else if (status == YESTERDAY) {
			// 获取昨天的日期
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		} else if (status == FD_OFTHISYEAR) {
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		} else if (status == LD_OFTHISYEAR) {
			//12月都有31号
			calendar.set(Calendar.MONTH, 11);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
		}
		return df.format(calendar.getTime());

	}

	public DataSet getBusyInfoHotStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String webRegion = SecurityConfig.getString("WebRegion");
			String key = "icity_business_hotstat_" + webRegion;
			DataSet object = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
			if (object == null) {
				synchronized (webRegion.intern()) {
					object = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
					if (object == null) {
						object = ProjectBusinessStatDao.getInstance().getBusyInfoHotStat(pSet);
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, object);
					}
				}
			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		ds.setState(StateType.FAILT);
		ds.setMessage("服务器忙，请稍后^_^");
		return ds;
	}
	
	public DataSet getBusyInfoNoticeStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String webRegion = SecurityConfig.getString("WebRegion");
			String key = "icity_business_noticestat_" + webRegion;
			System.out.println(key);
			DataSet object = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
			object = ProjectBusinessStatDao.getInstance().getBusyInfoNoticeStat(pSet);

		/*	if (object == null) {
				synchronized (webRegion.intern()) {
					object = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
					if (object == null) {
						object = ProjectBusinessStatDao.getInstance().getBusyInfoNoticeStat(pSet);
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, object);
					}
				}
			}*/
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		ds.setState(StateType.FAILT);
		ds.setMessage("服务器忙，请稍后^_^");
		return ds;
	}
	/**
	 * @Description: 办件公告列表
	 * @return DataSet 返回类型
	 * @since 2015-9-2
	 */
	public DataSet getBusinessList(ParameterSet pSet) {
		return ProjectBusinessStatDao.getInstance().getBusinessList(pSet);
	}
	
	/**
	 * @Description: 获取个人最常用事项
	 * @return DataSet 返回类型
	 * @since 2016-7-20
	 */
	public DataSet getBusinessUsed4Person(ParameterSet pSet) {
		UserInfo user = this.getUserInfo(pSet);
		pSet.put("uid", user.getUid()+"");
		return ProjectBusinessStatDao.getInstance().getBusinessUsed4Person(pSet);
	}
	
	/**
	 * @Description: 西宁市办件统计
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	public DataSet getBusinessStatXns(ParameterSet pSet) {
		return ProjectBusinessStatDao.getInstance().getBusinessStatXns(pSet);
	}
}
