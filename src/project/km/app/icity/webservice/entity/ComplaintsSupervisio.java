package km.app.icity.webservice.entity;

import java.util.Date;

/**
 * 
 * ClassName: ComplaintsSupervisio <br/>
 * date: 2015年10月14日 上午10:58:48 <br/>
 * @author lw
 */
public class ComplaintsSupervisio {
    private String fId;
    private String complaintId; //投诉编号
    private String wsdtCode; //网上大厅投诉信息ID
    private String xzspSupervisioId; //督办编号（新锐和达）
    private String supervisionContent; //督办内容
    private String supervisionDep; //督办部门
    private String supervisionPerson; //督办人
    private Date supervisionDate; //督办时间
    private String replyStatus; //回复状态 YES：已回复；NO:未回复
    private Date replyDate;//回复时间
    private String replyContent;//回复内容
    private String replyDep;//回复部门
    private String replyPerson;//回复人
    public String getfId() {
        return fId;
    }
    public void setfId(String fId) {
        this.fId = fId;
    }
    public String getComplaintId() {
        return complaintId;
    }
    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }
    public String getWsdtCode() {
        return wsdtCode;
    }
    public void setWsdtCode(String wsdtCode) {
        this.wsdtCode = wsdtCode;
    }
    public String getXzspSupervisioId() {
        return xzspSupervisioId;
    }
    public void setXzspSupervisioId(String xzspSupervisioId) {
        this.xzspSupervisioId = xzspSupervisioId;
    }
    public String getSupervisionContent() {
        return supervisionContent;
    }
    public void setSupervisionContent(String supervisionContent) {
        this.supervisionContent = supervisionContent;
    }
    public String getSupervisionDep() {
        return supervisionDep;
    }
    public void setSupervisionDep(String supervisionDep) {
        this.supervisionDep = supervisionDep;
    }
    public String getSupervisionPerson() {
        return supervisionPerson;
    }
    public void setSupervisionPerson(String supervisionPerson) {
        this.supervisionPerson = supervisionPerson;
    }
    public Date getSupervisionDate() {
        return supervisionDate;
    }
    public void setSupervisionDate(Date supervisionDate) {
        this.supervisionDate = supervisionDate;
    }
    public String getReplyStatus() {
        return replyStatus;
    }
    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }
    public Date getReplyDate() {
        return replyDate;
    }
    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }
    public String getReplyContent() {
        return replyContent;
    }
    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
    public String getReplyDep() {
        return replyDep;
    }
    public void setReplyDep(String replyDep) {
        this.replyDep = replyDep;
    }
    public String getReplyPerson() {
        return replyPerson;
    }
    public void setReplyPerson(String replyPerson) {
        this.replyPerson = replyPerson;
    }
}
