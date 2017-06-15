package action.icity.unionb2c.com.pay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


//import CCBSign.RSASig;
import com.inspur.base.BaseAction;
import com.inspur.util.SecurityConfig;


public class BackRcvResponseByCCB extends BaseAction {
	/**
	 * 
	 */
	StringBuffer logStr =null;
	private static final long serialVersionUID = 1L;
	private static Log log_ = LogFactory.getLog(BackRcvResponseByCCB.class);
	@Override
	public boolean handler(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		BackRcvUtil br = new BackRcvUtil();
		try {
			logStr = new StringBuffer();
			logStr.append("建行缴费返回网办");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			br.printLog("时间：" + format.format(new Date()));
			receiveData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log_.error(e);
			br.printLog(e.toString());
		}
		return false;

	}
	public void receiveData() {
		BackRcvUtil br = new BackRcvUtil();
		String ORDERID = this.getParameter("ORDERID");
		String success = this.getParameter("SUCCESS");
		String payCode = this.getParameter("REMARK1");
		String PAYMENT = this.getParameter("PAYMENT");
		
		 
		//数字签名
		//String SIGN = this.getParameter("SIGN");
		/* String str3 = "POSID="+this.getParameter("POSID")
				+ "&BRANCHID="+this.getParameter("BRANCHID")
				+ "&ORDERID="+this.getParameter("ORDERID") + "&PAYMENT="+this.getParameter("PAYMENT")
				+ "&CURCODE="+this.getParameter("CURCODE") +"&REMARK1="+this.getParameter("REMARK1")
				+ "&REMARK2="+this.getParameter("REMARK2");
			if(this.getParameter("ACC_TYPE")!=null&&!this.getParameter("ACC_TYPE").equals(""))
				str3+= "&ACC_TYPE="+this.getParameter("ACC_TYPE");
			str3+="&SUCCESS="+ this.getParameter("SUCCESS");
			if(this.getParameter("TYPE")!=null&&!this.getParameter("TYPE").equals(""))
				str3+="&TYPE="+ this.getParameter("TYPE");
			if(this.getParameter("REFERER")!=null&&!this.getParameter("REFERER").equals(""))	
				str3+="&REFERER="+ this.getParameter("REFERER");
			if(this.getParameter("CLIENTIP")!=null&&!this.getParameter("CLIENTIP").equals(""))	
				str3+="&CLIENTIP="+ this.getParameter("CLIENTIP");
			if(this.getParameter("ACCDATE")!=null&&!this.getParameter("ACCDATE").equals(""))
				str3+="&ACCDATE="+ this.getParameter("ACCDATE");
			if(this.getParameter("USRMSG")!=null&&!this.getParameter("USRMSG").equals(""))
				str3+="&USRMSG="+ this.getParameter("USRMSG");
			if(this.getParameter("INSTALLNUM")!=null&&!this.getParameter("INSTALLNUM").equals(""))
				str3+="&INSTALLNUM="+ this.getParameter("INSTALLNUM");
			if(this.getParameter("ERRMSG")!=null&&!this.getParameter("ERRMSG").equals(""))
				str3+="&ERRMSG="+ this.getParameter("ERRMSG");
			if(this.getParameter("USRINFO")!=null&&!this.getParameter("USRINFO").equals(""))
				str3+="&USRINFO="+ this.getParameter("USRINFO");

		String pubKey = SecurityConfig.getString("PUBKEY");
		RSASig rsa = new RSASig();
		rsa.setPublicKey(pubKey);
		boolean b3 = rsa.verifySigature(SIGN,str3);
		br.printLog("Is OK?----" + b3);*/
		// 缴费成功
		if (success.equals("Y")) {
			logStr.append("建行缴费成功");
			br.printLog("建行缴费返回成功");
			// 反馈博思
			PAYMENT = Float.parseFloat(PAYMENT)*100+"";
			PAYMENT = PAYMENT.substring(0, PAYMENT.indexOf("."));
			br.printLog("反馈博思：payCode="+payCode+" PAYMENT="+PAYMENT);
			String str = br.bosiConfirmPay(payCode, PAYMENT);
			logStr.append("#反馈博思"+str);
			br.printLog("更新外网开始");
			br.printLog("订单号："+ORDERID);
			// 反馈外网
			br.updateBillState(ORDERID);
		}else{
			logStr.append("建行缴费失败");
		}
		//记录日志
		br.updateBillLog(ORDERID, logStr.toString());
	}
}
