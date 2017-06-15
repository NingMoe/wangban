package app.pmi.srr;

import java.util.Map;


/**
 * 应用程序
 *
 */
public class SrrAppBean {
	
	private String id;
	private String name;
	private String serverId;
	private String type;//应用类型（IIS、tomcat）
	private String isInUse;//可用状态（可用、不可用）
	private String status;//应用的状态（正常、异常）
	private int testTimes;//检测次数（心跳）
	private String serverName;//服务器名称
	private int serviceCount;//服务个数
	private int onService;//在线服务个数***一个list里所有数据此项相同，目前只有第一项有数据
	private int offService;//脱机服务个数***一个list里所有数据此项相同
	private int abnormalService;//异常服务个数***一个list里所有数据此项相同
	
	private Map<String,Object> monitor;//监控信息
	
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
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsInUse() {
		return isInUse;
	}
	public void setIsInUse(String isInUse) {
		this.isInUse = isInUse;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTestTimes() {
		return testTimes;
	}
	public void setTestTimes(int testTimes) {
		this.testTimes = testTimes;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getServiceCount() {
		return serviceCount;
	}
	public void setServiceCount(int serviceCount) {
		this.serviceCount = serviceCount;
	}
	public int getOnService() {
		return onService;
	}
	public void setOnService(int onService) {
		this.onService = onService;
	}
	public int getOffService() {
		return offService;
	}
	public void setOffService(int offService) {
		this.offService = offService;
	}
	public int getAbnormalService() {
		return abnormalService;
	}
	public void setAbnormalService(int abnormalService) {
		this.abnormalService = abnormalService;
	}
	public Map<String,Object> getMonitor() {
		return monitor;
	}
	public void setMonitor(Map<String,Object> monitor) {
		this.monitor = monitor;
	}
}
