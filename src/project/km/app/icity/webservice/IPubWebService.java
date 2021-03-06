package km.app.icity.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import km.app.icity.webservice.entity.CommitTranResponse;
import km.app.icity.webservice.entity.Complaint;
import km.app.icity.webservice.entity.ComplaintsSupervisio;
import km.app.icity.webservice.entity.Transaction;

/**
 * This class was generated by Apache CXF 3.1.0 2015-10-13T13:22:33.867+08:00
 * Generated source version: 3.1.0
 * 
 */
/**
 * 
 * ClassName: IPubWebService <br/>
 * date: 2015年10月14日 上午10:58:35 <br/>
 * @author lw
 */
@WebService(targetNamespace = "http://webservice.synda.com/", name = "IPubWebService")

@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IPubWebService {
	// 提交办件信息
	public CommitTranResponse commitTransaction(@WebParam(name = "tranBean") Transaction tranBean);

	public java.lang.String commitTransactionInfo(
			@WebParam(partName = "postData", name = "postData") java.lang.String postData);

	// 提交投诉信息
	public String commitComplaint(@WebParam(name = "complaintBean") Complaint complaintBean);

	// 提交投诉回复信息
	public String replyComplaint(@WebParam(name = "complaintBean") Complaint complaintBean);

	// 提交督办回复信息
	public String replyComplaintSupervisio(
			@WebParam(name = "complaintsSupervisio") ComplaintsSupervisio complaintsSupervisio);

}
