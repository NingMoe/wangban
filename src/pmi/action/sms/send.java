package action.sms;


import com.icore.util.Tools;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.CacheManager;
import com.inspur.util.Command;
import com.inspur.util.SecurityConfig;
import org.apache.commons.lang.StringUtils;

import java.util.Map;


public class send extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	@Override
	public boolean handler(Map<String, Object> map) {
		String accept_ip = SecurityConfig.getString("AcceptIP");
		if(StringUtils.isNotEmpty(accept_ip)){
			String[] strs = accept_ip.split(",");
			for(String str:strs ){
				if(this.getRemoteAddr().equalsIgnoreCase(str)){
					//	防止其他服务程序恶意调用短信接口
						String phone = this.getParameter("phone");
						if(!StringUtils.isNotEmpty(phone)){
							phone = this.getParameter("telphone");
						}
						if(!StringUtils.isNotEmpty(phone)){
							DataSet ds = new DataSet();
							ds.setState(StateType.FAILT);
							ds.setMessage("请输入电话号码");
							this.write(ds.toJson());
							return false;
						}
						String channel = this.getParameter("channel");
						String message = this.getParameter("message");
						if(!StringUtils.isNotEmpty(message)){
							message = "您好，欢迎使用深圳市网上办事大厅，本次验证码为：";
							String rand  = String.valueOf((int)(Tools.random(9000)+1000));
							message += rand;
							//CacheManager.EhCacheType存放到服务器本地，Constant.SESSIONID唯一标识
							CacheManager.set(CacheManager.EhCacheType,"icity_sms", channel+"_"+phone, rand);
						}
						
						Command cmd = new Command("app.pmi.sms.SmsInfoCmd");
						cmd.setParameter("SMSCONTENT", message);
						cmd.setParameter("CHANNEL", channel);
						cmd.setParameter("STATUS", "0");
						cmd.setParameter("TELEPHONE", phone);
						DataSet ret = cmd.execute("insert");
						
						DataSet ds = new DataSet();
						if(ret.getState()==1){
							ds.setState(StateType.SUCCESS);
							this.write(ds.toJson());
							return false;
						}else{
							ds.setState(StateType.FAILT);
							ds.setMessage(ds.getMessage());
							this.write(ds.toJson());
						}
						return false;
					}
			}
		}
		return false;
	}

}
