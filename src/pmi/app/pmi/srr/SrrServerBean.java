package app.pmi.srr;

import java.util.List;


/**
 * 服务器表
 *
 */
public class SrrServerBean {
	private String id;
	private String name;
	private String ip;
	private String status;//服务器状态0-正常；1-异常
	private String isInUse;
	private List<SrrAppBean> appList;
	private int sort;//排序号
	
	private int normalApp;//正常应用个数
	private int abnormalApp;//异常应用个数
	
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIsInUse() {
		return isInUse;
	}
	public void setIsInUse(String isInUse) {
		this.isInUse = isInUse;
	}
	public List<SrrAppBean> getAppList() {
		return appList;
	}
	public void setAppList(List<SrrAppBean> appList) {
		this.appList = appList;
	}
	public int getAbnormalApp() {
		return abnormalApp;
	}
	public void setAbnormalApp(int abnormalApp) {
		this.abnormalApp = abnormalApp;
	}
	public int getNormalApp() {
		return normalApp;
	}
	public void setNormalApp(int normalApp) {
		this.normalApp = normalApp;
	}
}
