package action.zs;

import com.icore.http.util.HttpUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.Command;
import com.inspur.util.Constant;
import com.timevale.SecureUtils;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class loginfr extends BaseAction {
	private static final long serialVersionUID = 1L;
	public boolean handler(Map<String,Object> data){
			Map<String,String> postdata = this.getHttpAdapter().getPostData();	
			String sunData = postdata.get("sundata");
			String igoto = this.getParameter("goto");
			try {
				Map<String,Object> attrs = SecureUtils.VerifyData(sunData);
				String companyName = attrs.get("CompanyName").toString(); //企业名称
				//企业登录账户分企业法人、企业非法人
                Object companyRegNumber = attrs.get("CompanyRegNumber"); //工商注册号
                Object organizationNumber = attrs.get("OrganizationNumber"); //机构代码
                String account="";
                String ID = attrs.get("userId").toString(); //标识
                String STATUS = attrs.get("realLevel").toString();
                if(companyRegNumber!=null){
                	account=(String) companyRegNumber;
                }
                if(organizationNumber!=null){
                	account=(String) organizationNumber;
                }
                
                net.sf.json.JSONObject uInfo = new net.sf.json.JSONObject();
                uInfo.put("NAME", companyName);
    			uInfo.put("ACCOUNT", account);
    			uInfo.put("ORG_NAME", companyName);
    			uInfo.put("TYPE", "21");
    			uInfo.put("ICREGNUMBER", account);
    			uInfo.put("PHONE", "");
                uInfo.put("STATUS", STATUS);
                Command cmd = new Command("app.uc.GetUserMapDao");
        		cmd.setParameter("user_id_map", ID);
        		cmd.setParameter("uInfo", uInfo);
        		DataSet ds = cmd.execute("GetUid");
        		if (ds.getState() == StateType.SUCCESS) {
        			ID = ds.getJAData().getJSONObject(0).getString("USER_ID");
        		}
                
                cmd = new Command("app.uc.LoginCmd");
        		JSONObject jo = new JSONObject();
        		jo.put("ACCOUNT", account);
        		jo.put("PHONE", "");
        		jo.put("CARD_NO", account);
        		jo.put("ID", ID);
        		jo.put("ORG_NAME", companyName);
        		jo.put("TYPE", 21);
        		cmd.setParameter("jo", jo);
        		cmd.setParameter("cookie", getCookie(Constant.SESSIONID));		
        		ds = cmd.execute("setUcUserInfo");
        		if (ds.getState() == StateType.FAILT) {
        			
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
    		this.sendRedirect(HttpUtil.formatUrl(igoto)); 
		return false;
	}
}
