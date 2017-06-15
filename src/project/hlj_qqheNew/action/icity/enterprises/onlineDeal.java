package hlj_qqheNew.action.icity.enterprises;

import hlj_qqheNew.app.icity.project.QueryEcGapXMLJXDao;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import sun.security.action.PutAllAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.uc.UserDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import core.util.HttpClientUtil;

public class onlineDeal extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1325138082768356057L;
	public boolean handler(Map<String,Object> data){
		/*String itemId=this.getParameter("itemId");
		String urlForm = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getFormInfo?itemId="+itemId);
		HttpClientUtil client = new HttpClientUtil();
		JSONObject form=JSONObject.fromObject(client.getResult(urlForm,""));
		JSONObject fieldSetting=new JSONObject();
			JSONObject formInfo=form.getJSONObject("info");
			if(!formInfo.isNullObject()){
				fieldSetting=formInfo.getJSONObject("fieldSetting");
			}
			data.put("fieldSetting", fieldSetting);*/
		//	return true;
		return interfaceMode(data);
	}
	@SuppressWarnings("deprecation")
	public boolean interfaceMode(Map<String, Object> data) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		try{
			String itemId = this.getParameter("itemId");
			String id = this.getParameter("ID");
			DataSet resultsub = null;
			UserInfo ui=this.getUserInfo();
			String formid = "",dataId="",_dataId="",baseContent = "\"\"";
			String state = "00";
	
					
			String sql = "select * from ENTERPRISE_BUSINESS_INDEX where 1=1 ";
			if(StringUtils.isNotEmpty(id)){
				sql+="and id =?";
				resultsub = DbHelper.query(sql, new Object[]{itemId,ui.getUid()}, conn);
			}
			
			conn.commit();
		if (resultsub!=null&&resultsub.getTotal() > 0) {
			_dataId=resultsub.getRecord(0).getString("DATAID");
			if(!"".equals(_dataId)){
				dataId = "&dataId="+_dataId;
			}
			id=resultsub.getRecord(0).getString("ID");
			formid =resultsub.getRecord(0).getString("FORMID");
			
			baseContent = resultsub.getRecord(0).getString("APPLY_DATA");
			state=resultsub.getRecord(0).getString("STATE");
		}
			data.put("dataId", dataId); 
			data.put("_dataId", _dataId); 
			data.put("formid", formid);//表单id
			data.put("baseContent", baseContent);
			data.put("id", id);
			data.put("state", state);
			DataSet dSet = UserDao.getInstance().getUserById("" + this.getUserInfo().getUid());
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			DBSource.closeConnection(conn);
		}
		
		return true;
		
	}
}
