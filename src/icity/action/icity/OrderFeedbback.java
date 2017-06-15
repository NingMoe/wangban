package action.icity;

import java.util.Map;

import com.inspur.base.BaseAction;

import app.icity.govservice.OrderCmd;

public class OrderFeedbback extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean handler(Map<String, Object> data) throws Exception {
		// TODO Auto-generated method stub
		String status = this.getParameter("STATUS");
		/*String resmsg = this.getParameter("RESMSG");
		String resmsgsign = this.getParameter("RESMSGSIGN");*/
		OrderCmd orderCmd=new OrderCmd();
		
		
		String resmsg=(String) orderCmd.getOrderXMLDoc().getRecord(0).get("reqmsg");
		String resmsgsign=(String) orderCmd.getOrderXMLDoc().getRecord(0).get("signstr");
		//Map<String,String> postData =this.getPostData();
		//data.put("status", status);
		data.put("resmsg", resmsg);
		data.put("resmsgsign", resmsgsign);
		
		//orderCmd.getOrderFeedback(data);
				
		this.write("接收订单成功");
		return false;
	}
	
	
	
	
	
	
}
