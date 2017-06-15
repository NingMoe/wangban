package api.impl;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

import core.util.HttpClientUtil;

public class ApprovetransferImpl extends BaseQueryCommand {
	private static Logger log = LoggerFactory.getLogger(ApprovetransferImpl.class);

	public static ApprovetransferImpl getInstance() {
		return DaoFactory.getDao(ApprovetransferImpl.class.getName());
	}
	/**
	 * 2.1	新增从业人员档案
	 * @param pset
	 * @return
	 */
	public DataSet addStaffArchive(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl("http://services.sdyzgl.com/test/dataQueryForStaff/addStaffArchive/1.0");
		JSONObject data=new JSONObject();
		data = JSONObject.fromObject(pSet.get("data"));
		try {
			String s = RestUtil.postJson(url, data);
			System.out.println(s);
			ds.setRawData(s);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 培训查询
	 * @param pset
	 * @return
	 */
	public DataSet queryStaffTrainingPlan(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl("http://services.sdyzgl.com/test/dataQuery/queryStaffTrainingPlan/1.0");
		JSONObject data=new JSONObject();
		data = JSONObject.fromObject(pSet.get("data"));
		try {
			String s = RestUtil.postJson(url, data);
			System.out.println(s);
			ds.setRawData(s);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 人员、资格查询
	 * @param pset
	 * @return
	 */
	public DataSet queryStaff(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl("http://services.sdyzgl.com/test/dataQuery/queryStaff/1.0");
		JSONObject data=new JSONObject();
		data = JSONObject.fromObject(pSet.get("data"));
		try {
			String s = RestUtil.postJson(url, data);
			System.out.println(s);
			ds.setRawData(s);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 新增资格
	 * @param pset
	 * @return
	 */
	public DataSet addStaffQualificationApply(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl("http://services.sdyzgl.com/test/dataQueryForStaff/addStaffQualificationApply/1.0");
		JSONObject data=new JSONObject();
		data = JSONObject.fromObject(pSet.get("data"));
		try {
			String s = RestUtil.postJson(url, data);
			System.out.println(s);
			ds.setRawData(s);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 新增户
	 * @param pset
	 * @return
	 */
	public DataSet newTaxiNetPlat(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl("http://services.sdyzgl.com/test/dataQueryForInfo/newTaxiNetPlat/1.0");
		JSONObject data=new JSONObject();
		data = JSONObject.fromObject(pSet.get("data"));
		try {
			String s = RestUtil.postJson(url, data);
			System.out.println(s);
			ds.setRawData(s);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 新增资格
	 * @param pset
	 * @return
	 */
	public DataSet newTaxiNet(ParameterSet pSet){		
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl("http://services.sdyzgl.com/test/dataQueryForInfo/newTaxiNet/1.0");
		JSONObject data=new JSONObject();
		data = JSONObject.fromObject(pSet.get("data"));
		try {
			String s = RestUtil.postJson(url, data);
			System.out.println(s);
			ds.setRawData(s);
		} catch (Exception e) {
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	public String ygcgetpeixun(ParameterSet pSet){	
		String resultString = null ;
		String ds1 = null ;
		String url = HttpUtil.formatUrl("http://59.224.26.72:8086/web/approval/ygcgetpeixun");
		String examDateBegin = (String) pSet.get("examDateBegin");
		String examDateEnd = (String) pSet.get("examDateEnd");
		String trainingOrgCode = (String) pSet.get("trainingOrgCode");
		
		try {
			HttpClient client = new HttpClient();
			PostMethod postMethod = new PostMethod(url);
			client.getParams().setContentCharset("UTF-8");
			 postMethod.setParameter("examDateBegin",examDateBegin);
			 postMethod.setParameter("examDateEnd",examDateEnd);
			 postMethod.setParameter("trainingOrgCode", trainingOrgCode);
			 client.executeMethod(postMethod);
			resultString = postMethod.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}
}
