package km.app.icity.webservice.entity;

import java.util.Date;
import java.util.List;
/**
 * 
 * ClassName: Transaction <br/>
 * date: 2015年10月14日 上午10:57:38 <br/>
 * @author lw
 */

public class Transaction {
	private String tranCode;//办件编号
	private String tranSource; //办件来源
	private String tranContent; //办件内容
	private int tranNumber;//办件数
	private Date tranAppTime; //办件申请时间
	private String proceedCode; //业务编码
	private int  proceedVersion;//业务版本
	private String applicantType;//申请人类型
	private String applicantName;//申请人名称
	private String appCardType;//申请人证件类型
	private String appCardNumber;//申请人证件号码
	private String applicantAddress;//申请人联系地址
	private String applicantEmail;//申请人邮箱
	private String applicantPhone;//申请人电话
	private String representative;//法定代表人 REPRESENTATIVE
	private String repCardType;//法定代表人证件类型
	private String repCardNumber;//法定代表人证件号码	
	private String agentName;//联系人姓名AGENT
	private String agentCardType;//联系人证件类型
	private String agentCardNumber;//联系人证件号码
	private String agentPhone;//联系人电话
	private String agentFax;//联系人传真
	private String agentEmail;//联系人邮箱
	private String agentAddress;//联系人地址
	private List<TransactionMaterial> tranMaterialList;  //申请材料bean
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	public String getTranSource() {
		return tranSource;
	}
	public void setTranSource(String tranSource) {
		this.tranSource = tranSource;
	}
	public String getTranContent() {
		return tranContent;
	}
	public void setTranContent(String tranContent) {
		this.tranContent = tranContent;
	}
	public int getTranNumber() {
		return tranNumber;
	}
	public void setTranNumber(int tranNumber) {
		this.tranNumber = tranNumber;
	}
	public Date getTranAppTime() {
		return tranAppTime;
	}
	public void setTranAppTime(Date tranAppTime) {
		this.tranAppTime = tranAppTime;
	}
	public String getProceedCode() {
		return proceedCode;
	}
	public void setProceedCode(String proceedCode) {
		this.proceedCode = proceedCode;
	}
	public int getProceedVersion() {
		return proceedVersion;
	}
	public void setProceedVersion(int proceedVersion) {
		this.proceedVersion = proceedVersion;
	}
	public String getApplicantType() {
		return applicantType;
	}
	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getAppCardType() {
		return appCardType;
	}
	public void setAppCardType(String appCardType) {
		this.appCardType = appCardType;
	}
	public String getAppCardNumber() {
		return appCardNumber;
	}
	public void setAppCardNumber(String appCardNumber) {
		this.appCardNumber = appCardNumber;
	}
	public String getApplicantAddress() {
		return applicantAddress;
	}
	public void setApplicantAddress(String applicantAddress) {
		this.applicantAddress = applicantAddress;
	}
	public String getApplicantEmail() {
		return applicantEmail;
	}
	public void setApplicantEmail(String applicantEmail) {
		this.applicantEmail = applicantEmail;
	}
	public String getApplicantPhone() {
		return applicantPhone;
	}
	public void setApplicantPhone(String applicantPhone) {
		this.applicantPhone = applicantPhone;
	}
	public String getRepresentative() {
		return representative;
	}
	public void setRepresentative(String representative) {
		this.representative = representative;
	}
	public String getRepCardType() {
		return repCardType;
	}
	public void setRepCardType(String repCardType) {
		this.repCardType = repCardType;
	}
	public String getRepCardNumber() {
		return repCardNumber;
	}
	public void setRepCardNumber(String repCardNumber) {
		this.repCardNumber = repCardNumber;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentCardType() {
		return agentCardType;
	}
	public void setAgentCardType(String agentCardType) {
		this.agentCardType = agentCardType;
	}
	public String getAgentCardNumber() {
		return agentCardNumber;
	}
	public void setAgentCardNumber(String agentCardNumber) {
		this.agentCardNumber = agentCardNumber;
	}
	public String getAgentPhone() {
		return agentPhone;
	}
	public void setAgentPhone(String agentPhone) {
		this.agentPhone = agentPhone;
	}
	public String getAgentFax() {
		return agentFax;
	}
	public void setAgentFax(String agentFax) {
		this.agentFax = agentFax;
	}
	public String getAgentEmail() {
		return agentEmail;
	}
	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}
	public String getAgentAddress() {
		return agentAddress;
	}
	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}
	public List<TransactionMaterial> getTranMaterialList() {
		return tranMaterialList;
	}
	public void setTranMaterialList(List<TransactionMaterial> tranMaterialList) {
		this.tranMaterialList = tranMaterialList;
	}
	
}
