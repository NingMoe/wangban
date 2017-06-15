package action.bsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.pmi.hits.HitsBean;

import com.icore.http.adapter.HttpAdapter;
import com.icore.util.fifo.FIFO;
import com.icore.util.fifo.FIFOImpl;
import com.inspur.base.BaseAction;
import com.inspur.util.Command;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

import core.util.CommonUtils;

public class hits extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Timer _taskTimer;
	private final static FIFO beanQueue = new FIFOImpl<HitsBean>();
	
	static{
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() { 
				if(!beanQueue.isEmpty()){
					List<HitsBean> hitsBeans = new ArrayList<HitsBean>();
					HitsBean hitsBean;
					while((hitsBean=(HitsBean) beanQueue.poll())!=null){  
						hitsBeans.add(hitsBean);
					}
					Command cmd = new Command("app.pmi.hits.HitsCmd");
					cmd.setParameter("hitsBeans", hitsBeans);
					cmd.execute("batchAddHits");
				}
			}
		};
		long tt = 1000*5;//每5秒进行数据库操作
		_taskTimer.scheduleAtFixedRate(task, tt, tt);
		
	}
	
	/**
	 * 将数据存储到queue里，在任务管理中取出数据进行存库，五秒执行一次存库
	 */
	@Override
	public boolean handler(Map<String, Object> arg0) throws Exception {
		try {
			HttpAdapter ha = this.getHttpAdapter();
			HitsBean hitsBean = new HitsBean();
			hitsBean.setCatalog(ha.getParameter("catalog"));
			hitsBean.setAppid(SecurityConfig.getString("AppId"));
			hitsBean.setWebname(SecurityConfig.getString("AppHall"));
			hitsBean.setWebregion(SecurityConfig.getString("WebRegion"));
			hitsBean.setWebrank(SecurityConfig.getString("WebRank"));
			hitsBean.setVisittime(CommonUtils.getInstance().parseStringToTimeStamp(
					Tools.formatDate(new Date(),CommonUtils.YYYY_MM_DD_HH_mm_SS),
					CommonUtils.YYYY_MM_DD_HH_mm_SS));
			hitsBean.setVisitedtitle(ha.getParameter("title"));
			hitsBean.setUser_agent(ha.getHeader("User-Agent"));
			hitsBean.setIp( ha.getRemoteAddr());
			hitsBean.setUserid(this.getUserInfo()==null?"":this.getUserInfo().getUid()+"");
			//hitsBean.setScreen(ha.getParameter("w")+"×"+ha.getParameter("h"));
			hitsBean.setReferer(ha.getHeader("referer"));
			beanQueue.offer(hitsBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setContentType("application/javascript");
		return false;
	}
}
