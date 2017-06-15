package app.pmi.hits;

import java.sql.Timestamp;

import oracle.sql.DATE;


/**
 * PUB_HITS统计访问
 * @author wq
 *
 */
public class HitsBean {
	private String id;
	private String appid;
	private String webname;
	private String webregion;
	private String webrank;
	private String catalog;
	private String ip;
	private Timestamp visittime;
	private String visitedtitle;
	private String visitedurl;
	private String userid;
	private String os;
	private String user_agent;
	private String screen;
	private String referer;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWebname() {
		return webname;
	}
	public void setWebname(String webname) {
		this.webname = webname;
	}
	public String getWebregion() {
		return webregion;
	}
	public void setWebregion(String webregion) {
		this.webregion = webregion;
	}
	public String getwebrank() {
		return webrank;
	}
	public void setWebrank(String webrank) {
		this.webrank = webrank;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public Timestamp getVisittime() {
		return visittime;
	}
	public void setVisittime(Timestamp visittime) {
		this.visittime = visittime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getUrl() {
		return visitedurl;
	}
	public void setUrl(String visitedurl) {
		this.visitedurl = visitedurl;
	}
	public String getVisitedtitle() {
		return visitedtitle;
	}
	public void setVisitedtitle(String visitedtitle) {
		this.visitedtitle = visitedtitle;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getUser_agent() {
		return user_agent;
	}
	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}
	public String getScreen() {
		return screen;
	}
	public void setScreen(String screen) {
		this.screen = screen;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
}
