package app.pmi.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import com.icore.util.DaoFactory;
import com.icore.util.FrameConfig;
import com.icore.util.Tools.JSON_TYPE;
import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;

public class JobHandle implements ISecurity {
	private static Scheduler scheduler = null;

	// 单例
	public static JobHandle getInstance() {
		return DaoFactory.getDao(JobHandle.class.getName());
	}

	/**
	 * JOB初始化
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		if (FrameConfig.webPort() != Tools.webPort) {
			return;
		}
		try {
			Document registerJobDoc = getDoc(REGISTERJOB_FILE);
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			if (registerJobDoc != null) {
				Element registerJobRoot = registerJobDoc.getRootElement();
				List<Element> jobs = registerJobRoot.elements();
				if (jobs.size() > 0) {
					Document jobDoc = getDoc(JOB_FILE);
					for (int i = 0, n = jobs.size(); i < n; i++) {
						Element e = jobs.get(i);
						String id = e.attributeValue("id");
						JobDetail job = new JobDetail("jd_" + id, "jdGroup", CoreJob.class);
						String cron = e.attributeValue("cron");
						Element jroot = jobDoc.getRootElement();
						List<Element> jList = jroot.elements();
						Element jElement = null;
						for (Element ele : jList) {
							if (ele.attributeValue("id").equals(id)) {
								jElement = ele;
								break;
							}
						}
						String action = jElement == null ? "" : jElement.elementText("action");
						String method = jElement == null ? "" : jElement.elementText("method");
						String defaut_cron = jElement == null ? "" : jElement.elementText("defaut_cron");
						String name = jElement == null ? "" : jElement.elementText("name");
						String describe = jElement == null ? "" : jElement.elementText("describe");
						String strParams = jElement == null ? "" : jElement.elementText("params");

						cron = StringUtils.isEmpty(cron) == true ? defaut_cron : cron;
						TriggerEx trigger = new TriggerEx(name, "jdGroup");
						trigger.setDescription(describe);
						trigger.setAction(action);
						trigger.setMethod(method);
						trigger.setName(name);
						trigger.setCronExpression(cron);
						// 20秒后执行第一次
						// trigger.setStartTime(new Date(new
						// Date().getTime()+1000*20));
						trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
						if (StringUtils.isNotEmpty(strParams)
								&& Tools.getJSONType(strParams).equals(JSON_TYPE.JSON_TYPE_OBJECT)) {
							trigger.setParams(JSONObject.fromObject(strParams));
						}
						scheduler.scheduleJob(job, trigger);
					}
					scheduler.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("job配置文件异常!");
		}
	}

	/**
	 * 重启JOB
	 * 
	 * @return
	 */
	public DataSet restart() {
		DataSet ds = new DataSet();
		try {
			if (scheduler.isStarted()) {
				scheduler.shutdown(true);
			}
			init();
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("job重启失败!");
			e.printStackTrace();
		}
		return ds;
	}

	public Document getDoc(String path) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		File f = new File(path);
		try {
			if (!f.isFile()) {
				return null;
			}
			InputStreamReader in = new InputStreamReader(new FileInputStream(f), "UTF-8");
			doc = reader.read(in);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}
}
