package action.bsp;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.icore.util.CacheManager;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;

import core.util.RandomNumUtil;

public class verifyCode extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}
	
	@Override
	public boolean handler(Map<String, Object> data) {
		String action=this.getPostData().get("action");
		if("check".equals(action)){
			DataSet ds = new DataSet();
			String verify = (String)CacheManager.get("VerifyCode",this.getSessionId());	
			String VerifyCode = this.getPostData().get("VerifyCode");
			if (StringUtils.isEmpty(verify)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码超时，请换一张！");
				this.write(ds.toString());
				return false;
			}
			if (!verify.equalsIgnoreCase(VerifyCode)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("验证码输入错误，请重新输入！");
			}
			this.write(ds.toString());
			return false;
		}
		RandomNumUtil rnu = RandomNumUtil.getInstance();
		CacheManager.set("VerifyCode",this.getSessionId(),rnu.getString());
		this.write(rnu.getStream().toByteArray());
		this.setContentType("image/jpeg");
		return false;
	}
}
