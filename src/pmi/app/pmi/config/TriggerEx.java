package app.pmi.config;

import net.sf.json.JSONObject;

import org.quartz.CronTrigger;

public class TriggerEx extends CronTrigger {
	
	public TriggerEx(String name,String group){
		super(name,group);
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	private static final long serialVersionUID = 1L;
	private String action;
	private String method;
	private JSONObject params;
	
	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}
}
