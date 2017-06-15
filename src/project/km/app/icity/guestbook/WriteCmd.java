package km.app.icity.guestbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;

/**
 * 投诉
 * 
 * @ClassName: WriteCmd
 * @Date: 2015年10月15日 上午10:54:25
 * @author lw
 */
public class WriteCmd extends app.icity.guestbook.WriteCmd {
	
	private static Log log = LogFactory.getLog(WriteCmd.class);

	public DataSet getList(ParameterSet pSet) {
		return WriteDao.getInstance().getList(pSet);
	}

	public DataSet insert(ParameterSet pSet) {
		UserInfo user = this.getUserInfo(pSet);
		if (null != user) {
			return WriteDao.getInstance().insert(pSet, user);
		} else {
			return WriteDao.getInstance().insert(pSet);
		}
	}

	public DataSet getCount(ParameterSet pSet) {
		return WriteDao.getInstance().getCount(pSet);
	}

	public DataSet setSatisfy(ParameterSet pSet) {
		return WriteDao.getInstance().setSatisfy(pSet);
	}

	public DataSet setUseful(ParameterSet pSet) {
		return WriteDao.getInstance().setUseful(pSet);
	}

	/**
	 * @author xiongzhw@inspur.com
	 * @param pSet
	 *            参数
	 * @return 热门历史咨询信息
	 */
	public DataSet getHotConsult(ParameterSet pSet) {
		return WriteDao.getInstance().getHotConsult(pSet);
	}

	/*
	 * 非上班时间网上预约 pan
	 * 
	 */
	public DataSet wsyyinsert(ParameterSet pSet) {
		UserInfo user = this.getUserInfo(pSet);

		return WriteDao.getInstance().wsyyinsert(pSet, user);

	}
}
