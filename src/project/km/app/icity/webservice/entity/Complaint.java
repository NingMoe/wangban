package km.app.icity.webservice.entity;

/**
 * 
 * @ClassName: Complaint 
 * @Date: 2015年10月14日 上午11:01:53 
 * @author lw
 */
public class Complaint {
    private String address;//投诉人地址
    private String orgCode;//无部门时对应的区划代码（通用审批中心代码）
    private String content;//投诉内容
    private String depId;
    private String depCode;//投诉部门对应的组织机构代码
    private String email;//投诉人邮箱
    private String person;//投诉人姓名
    private String phone;//投诉人手机
    private String theme;//投诉主题
    private String wsdtCode;//投诉ID
    private String replyOpinion;//部门回复信息

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDepCode() {
        return depCode;
    }
    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getPerson() {
        return person;
    }
    public void setPerson(String person) {
        this.person = person;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }
    public String getWsdtCode() {
        return wsdtCode;
    }
    public void setWsdtCode(String wsdtCode) {
        this.wsdtCode = wsdtCode;
    }

    public String getReplyOpinion() {
        return replyOpinion;
    }
    public void setReplyOpinion(String replyOpinion) {
        this.replyOpinion = replyOpinion;
    }
	public String getDepId() {
		return depId;
	}
	public void setDepId(String depId) {
		this.depId = depId;
	}

}
