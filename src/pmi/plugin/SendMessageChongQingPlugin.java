package plugin;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;

public class SendMessageChongQingPlugin extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;
	@Override
	public boolean start() {
		_taskTimer = new Timer();
		TimerTask task = new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// 获取短信内容及手机号
				Plugin_Util plugin_Util = Plugin_Util.getInstance();
				DataSet ds = plugin_Util.getToSendList();
				if (ds != null&&ds.getTotal()>0) {
					// 错误日志信息
					String error_name = "";// 数据信息
					String content = "";// 日志信息
					String error_type = "重庆短信推送";
					//短信推送参数
					String id = "", address = "", message = "";
					String sendResult = "";
					for (int i = 0; i < ds.getTotal(); i++) {
						content = "";
						try {
							id = ds.getRecord(i).getString("ID");
							address = ds.getRecord(i).getString("TELEPHONE");
							message = ds.getRecord(i).getString("SMSCONTENT");
							error_name = message + "，发送到：" + address;
							sendResult = sendSms(message, address);
							if (sendResult.equals("success")) {
								plugin_Util.updateSms(id, "1");
							}else{
								content = sendResult;
								plugin_Util.updateSms(id, "2");
							}
								
						} catch (Exception e) {
							content="短信推送时异常："+e.toString();
							e.printStackTrace();
						}
						if (!content.equals("")) {
							int count = plugin_Util.insertErrorLog(error_type,
									error_name, id, content);
							if (count >= 3) {// count==3
								// 更新状态,推送错误超过3次，不在推送
								plugin_Util.updateSms(id, "3");
							}
						}
					}
				}
			}
		};
		long tt = 10000 * 1;// 每10秒进行操作
		_taskTimer.schedule(task, tt, tt);
		return true;
	}
	@Override
	public boolean stop() {
		_taskTimer.cancel();
		_taskTimer = null;
		return true;
	}
	public String sendSms(String message, String address) throws Exception {
		Call call = null;
		Service service = null;
		String strreturn = "111110", strReturn = "";
		try {
			// 电话号码参数,多个之间用','隔开
			String arg0 =address;
			// 短信内容
			String arg1 = message;
			//发送时间, 为null时立即发送
			String arg2 = "";
			//序列号，由业务系统产生(0-99999999，步长为1，循环产生)
			//String arg3 = "87654321";
			Random rand = new Random();
			String  arg3 = String.valueOf(rand.nextInt(99999999));
			//业务系统编码(两位数字编码)
			String arg4 = "06";
			//优先级，优先级，可为null，为null时默认为0：普通优先级
			String arg5 = "0";

			String mobileURL =SecurityConfig.getString("SMSNotification_url");// 短信访问webservice路径
			service = new Service();
			call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(mobileURL));
			call.setOperationName(new QName(
					"com.zenith.mobilemessage.sms.notification.service.SMSNotification",
					"sendSms"));

			call.addParameter("arg0", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("arg1", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("arg2", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("arg3", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("arg4", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("arg5", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			call.setUseSOAPAction(true);
			strreturn = (String) call.invoke(new Object[] { arg0,
					arg1, arg2, arg3, arg4, arg5 });
			if (strreturn.indexOf("0")<0) {
				if (strreturn.equals("-1")) {
					strReturn = "通过访问路径[" + mobileURL
							+ "]发送手机短信时出错，原因是：电话号码为空！";
				} else if (strreturn.equals("-2")) {
					strReturn = "通过访问路径[" + mobileURL
							+ "]发送手机短信时出错，原因是：短信内容为空！";
				} else if (strreturn.equals("-3")) {
					strReturn = "通过访问路径[" + mobileURL
							+ "]发送手机短信时出错，原因是：系统编码为空！";
				} else if (strreturn.equals("-4")) {
					strReturn = "通过访问路径[" + mobileURL
							+ "]发送手机短信时出错，原因是：无正确电话号码！";
				} else if (strreturn.equals("-5")) {
					strReturn = "通过访问路径[" + mobileURL
							+ "]发送手机短信时出错，原因是：其他错误！";
				}
			}else{
				strReturn = "success";
			}
		} catch (Exception ex) {
			strReturn += "发送手机短信出错，原因是：" + ex.toString();
			ex.printStackTrace();
		} finally {
			call = null;
			service = null;
		}
		return strReturn;
	}


}
