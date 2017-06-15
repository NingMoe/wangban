package plugin;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.icore.util.sms.MailSenderInfo;
import com.icore.util.sms.SimpleMailSender;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.mascloud.sdkclient.Client;
/**
 * 昆明市盘龙区 短信-邮件 通知信息  发送插件
 * @author yanhao
 *
 */
public class SendMessagePlugin_pl extends BaseQueryCommand implements IPlugin {
	private Timer _taskTimer;
	final Client client = Client.getInstance();
	@Override
	public boolean start() {
		_taskTimer = new Timer();
		// 正式环境IP，登录验证URL，用户名，密码，集团客户名称
		client.login(SecurityConfig.getString("MasWsLoginUrl_pl"), SecurityConfig.getString("SDKAccount_pl"),
				SecurityConfig.getString("SDKPassword_pl"), SecurityConfig.getString("Customer_pl"));
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
					String error_type = "";
					//短信推送参数
					String id = "", address = "",email="", message = "";
					int sendResult = 0;
					for (int i = 0; i < ds.getTotal(); i++) {
						content = "";
						String type ="";//sms 短信 email 邮件
						try {
							type = ds.getRecord(i).getString("NOTICETYPE");
							id = ds.getRecord(i).getString("ID");
							address = ds.getRecord(i).getString("TELEPHONE");
							email = ds.getRecord(i).getString("EMAIL");
							message = ds.getRecord(i).getString("SMSCONTENT");
							error_name = message + "，发送到：" + address;
							//短信
							if(type.equals("sms1")){
								error_type = "盘龙短信推送";
								sendResult = sendSms(message, address);
								if (sendResult == 1) {
									plugin_Util.updateSms(id, "1");
								}else{
									plugin_Util.updateSms(id, "2");
									switch (sendResult) {
										case 101:
											content="短信内容为空";
											break;
										case 102:
											content="号码数组为空";
											break;
										case 103:
											content="号码数组为空数组";
											break;
										case 104:
											content="批次短信的号码中存在非法号码";
											break;
										case 105:
											content="未进行身份认证或认证失败，用户请确认输入的用户名，密码和企业名是否正确";
											break;
										case 106:
											content="网关签名为空， 用户需要填写网关签名编号";
											break;
										case 108:
											content="JMS异常， 需要联系移动集团维护人员";
											break;
										case 109:
											content="批次短信号码中存在重复号码";
											break;
										case 110:
											content="发送的号码在一分钟之内全部内容（包括，号码，模板ID,模板内容都相同）都重复发送";
											break;
										case 111:
											content="扩展码错误,扩展码只能是15位以内数字或空字符串";
											break;
										case 112:
											content="签名错误或普通短信不允许使用模板短信的签名编码";
											break;
										default:
											content="其他错误";
											break;
									}
								}
								//邮件通知
							}else if(type.equals("email")){//邮件
								error_type = "盘龙邮件推送";
								String restr = sendEmail(message,email);
								if("success".equals(restr)){
									plugin_Util.updateSms(id, "1");
								}else{
									plugin_Util.updateSms(id, "2");
									content=restr;
								}
							}
						} catch (Exception e) {
							content="推送时异常："+e.toString();
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
	//短信发送
	private int sendSms(String message, String address) {
		int sendResult = 0;
		try {
		 sendResult = client.sendDSMS (new String[] {address}, message,
		  "", 1,SecurityConfig.getString("Sign_pl"),
		  UUID.randomUUID().toString(),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendResult;
	}
	// 发送邮件
	private String sendEmail(String message,String receiveAddress) {
			DataSet ds = new DataSet();
			String reStr = "";
			try {
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setMailServerHost(SecurityConfig
						.getString("mail_server_host"));
				mailInfo.setMailServerPort(SecurityConfig
						.getString("mail_server_port"));
				mailInfo.setValidate(true);
				mailInfo.setUserName(SecurityConfig.getString("mail_username"));// 用户名
				mailInfo.setPassword(SecurityConfig.getString("mail_password"));// 用户密码
				mailInfo.setFromAddress(SecurityConfig
						.getString("mail_from_address"));
				String subject = SecurityConfig.getString("mail_online_subject");
				mailInfo.setToAddress(receiveAddress);
				mailInfo.setSubject(subject);
				mailInfo.setContent(message);
				boolean success = SimpleMailSender.sendHtmlMail(mailInfo);
				if (success) {
					reStr="success";
					ds.setState(StateType.SUCCESS);
				} else {
					reStr="邮件发送失败";
					ds.setState(StateType.FAILT);
					ds.setMessage("邮件发送失败");
				}
			} catch (Exception e) {
				reStr=e.getMessage();
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				e.printStackTrace();
			}
			return reStr;
		}

}
