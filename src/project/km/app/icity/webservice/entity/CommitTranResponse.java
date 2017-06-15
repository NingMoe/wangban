package km.app.icity.webservice.entity;

/**
 * 
 * ClassName: CommitTranResponse <br/>
 * date: 2015年10月14日 上午11:00:23 <br/>
 * @author lw
 */
public class CommitTranResponse {
	private String status;//状态标记
	private String message; //消息
	private String tranCode; //办件编号
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	
}
