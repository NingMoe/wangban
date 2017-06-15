package app.pmi.config.cmd;

import java.util.List;

public class WebSite {
	private String id;
	private String pid;
	private String name;
	private String domain;
	private String value;
	private String title;
	private String is_show;
	
	private String PAGE_MODE;
	private String webRegion;
	private String webRank;
	
	private List<WebSite> childs;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIs_show() {
		return is_show;
	}
	public void setIs_show(String is_show) {
		this.is_show = is_show;
	}
	public List<WebSite> getChilds() {
		return childs;
	}
	public void setChilds(List<WebSite> childs) {
		this.childs = childs;
	}
	public WebSite() {
		super();
	}
	public WebSite(String id,String pid,String name,  String domain, String value,
			String title,String is_show) {
		super();
		this.id = id;
		this.pid =pid;
		this.name = name;
		this.domain = domain;
		this.value = value;
		this.title = title;
		this.is_show = is_show;
	}
	public String getPAGE_MODE() {
		return PAGE_MODE;
	}
	public void setPAGE_MODE(String pAGE_MODE) {
		PAGE_MODE = pAGE_MODE;
	}
	public String getWebRegion() {
		return webRegion;
	}
	public void setWebRegion(String webRegion) {
		this.webRegion = webRegion;
	}
	public String getWebRank() {
		return webRank;
	}
	public void setWebRank(String webRank) {
		this.webRank = webRank;
	}
	
	
}
