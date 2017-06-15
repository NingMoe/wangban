package api.impl;

import java.net.URLDecoder;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import core.util.HttpClientUtil;

public class CformImpl extends BaseQueryCommand {
	private static Logger log = LoggerFactory.getLogger(CformImpl.class);

	public static CformImpl getInstance() {
		return DaoFactory.getDao(CformImpl.class.getName());
	}

	/**
	 * 转存表单数据
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet interflowFormData(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONObject obj = new JSONObject();
			String formId = (String) pSet.getParameter("formId");
			String formData = URLDecoder.decode(pSet.get("formData").toString(), "UTF-8");
			String url=SecurityConfig.getString("Form_url")+"/cform/saveData?formId="+formId;
			HttpClientUtil client = new HttpClientUtil();
			JSONObject re = JSONObject.fromObject(client.getResult(url,
					formData));
			if ("200".equals(re.getString("state"))) {
				obj.put("dataId", re.getString("dataId"));
				obj.put("formId", formId);
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else if ("300".equals(re.getString("state"))) {
				ds.setMessage(re.getString("error"));
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 表单
	 * http://localhost:8080/icity/c/api.cform/cformOper?action=getFormUI&formId=ShouQianYanShiBiaoDan
	 * http://localhost:8080/icity/c/api.cform/cformOper?action=getFormData&formId=ShouQianYanShiBiaoDan&formDataId=20151116145252164900
	 * http://localhost:8080/icity/c/api.cform/cformOper?action=getDataBind&formId=ShouQianYanShiBiaoDan
	 * http://localhost:8080/icity/c/api.cform/cformOper?action=saveData&formId=ShouQianYanShiBiaoDan&formData={}	 * 
	 * {"ShouLiDanWei":"434343","ShouLiShiJian":""}
	 */
	public DataSet cformOper(ParameterSet pSet) {
		DataSet ds = new DataSet();
		ds.setState(StateType.SUCCESS);
		String url = SecurityConfig.getString("Form_url");
		String action = (String) pSet.get("action");
		String formId = (String) pSet.get("formId");
		try {
			HttpClientUtil client = new HttpClientUtil();
			String html="";
			if ("getFormUI".equals(action)) {
				url += "/cform/getFormUI?formId=" + formId;
				html = client.getResult(url, "");
			}
			if ("getFormData".equals(action)) {
				String formDataId = (String) pSet.get("formDataId");
				url += "/cform/getFormStandardData?formId=" + formId
						+ "&formDataId=" + formDataId;
				html = client.getResult(url, "");
				/*
				 * this.setHeader("Content-Type",
				 * "application/json;charset=UTF-8"); this.setHeader("Pragma",
				 * "No-cache"); this.setHeader("Cache-Control", "no-cache");
				 * this.write(html); return false;
				 */
			}
			if ("getDataBind".equals(action)) {
				url += "/cform/getDataBind?formId=" + formId;
				html = client.getResult(url, "");
				/*
				 * this.setHeader("Content-Type",
				 * "application/json;charset=UTF-8"); this.setHeader("Pragma",
				 * "No-cache"); this.setHeader("Cache-Control", "no-cache");
				 * this.write(html); return false;
				 */
			}
			if ("saveData".equals(action)) {
				String formData = new String(pSet.get("formData").toString()
						.getBytes("UTF-8"));
				url += "/cform/saveData?formId=" + formId;
				html = client.getResult(url, formData);
				/*
				 * this.setHeader("Content-Type",
				 * "application/json;charset=UTF-8"); this.setHeader("Pragma",
				 * "No-cache"); this.setHeader("Cache-Control", "no-cache");
				 * this.write(html); return false;
				 */
			}
			ds.setRawData(html);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
		}
		return ds;
	}
}
