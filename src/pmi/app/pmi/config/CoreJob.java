package app.pmi.config;

import java.util.Iterator;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;





import com.icore.log.Logger;
import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.util.Command;

public class CoreJob implements StatefulJob{
	
	private static Logger log = Logger.getLogger(CoreJob.class);
	
	@Override
	public void execute(JobExecutionContext jContext) throws JobExecutionException {
		TriggerEx trigger = (TriggerEx)jContext.getTrigger();
		log.info("调用定时任务："+trigger.getName());
		Command command = new Command(trigger.getAction());
		JSONObject joParams = trigger.getParams();
		if(joParams != null){
			Iterator<Entry<String,Object>> itParams = joParams.entrySet().iterator();
			while(itParams.hasNext()){
				Entry<String,Object> p = itParams.next();
				command.setParameter(p.getKey(), p.getValue());
			}
		}
		DataSet ds = (DataSet)command.execute(trigger.getMethod());
		if (ds !=null && ds.getState() == StateType.FAILT) {
			log.info("定时任务："+trigger.getName()+"调用失败!");
		}
	}
}
