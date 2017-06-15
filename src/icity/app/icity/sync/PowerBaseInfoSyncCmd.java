package app.icity.sync;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import java.util.ArrayList;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import core.util.HttpClientUtil;

/** 
 * @ClassName: PowerBaseInfoSyncCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-9-23 上午10:54:21  
 */

public class PowerBaseInfoSyncCmd extends BaseQueryCommand {
	public DataSet bSave(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String formId = (String)pSet.get("formId");
			String data = pSet.get("data").toString();
			HttpClientUtil client = new HttpClientUtil();
			String url = HttpUtil.formatUrl(SecurityConfig.getString("Form_url")+"/cform/saveData?formId="+formId);//;
			Object strItemList= client.getResult(url,data);
			JSONObject item = JSONObject.fromObject(strItemList);
			ds.setRawData(item);
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败！");
			return ds;
		}
		return ds;
	}
	public DataSet bLoad(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String url = (String)pSet.get("url");
			HttpClientUtil client = new HttpClientUtil();
			Object html= client.getResult(url,"");
			ds.setRawData(html);				
		
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage("提交失败！");
			return ds;
		}		//System.out.print(strItemList);
		return ds;
	}
	
	
	public DataSet DelInsSyncData(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().DelInsSyncData(pSet);
	}
	
	public DataSet DeleteSyncData(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().DeleteSyncData(pSet);
	}
	
	public DataSet DeleteData(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().DeleteData(pSet);
	}
	
	public DataSet PublishPreviewDataRest(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().PublishPreviewDataRest(pSet);
	}
	
	public DataSet deleteGabige(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().deleteGabige(pSet);
	}
	/*
	 *网盘 
	 */
	public DataSet apply_wp(ParameterSet pSet){
		String url = "D:\\workspace\\v3常用命令.txt";//(String)pSet.get("URL");
		DataSet ds = new DataSet();
		String Networklocationaddress = "http://220.162.162.25:7000/WebDiskServerDemo/upload";//PowerBaseInfoSyncDao.getInstance().getSecurityItem("NetDiskAddress");
		Map<String,String> params = new HashMap<String, String>();
		params.put("uid", "3336");
		params.put("type", "doc");
		params.put("folder_name", "//");
		String scc = UploadUtil.startUploadService(params, url, Networklocationaddress);
		
		List<JSONObject> list = new ArrayList<JSONObject>();
	    JSONObject value = new JSONObject();
	    value.accumulate("scc", scc);
	    list.add(value);
	    ds.setData(list.toString().getBytes());		
		//System.out.println(scc);

		return ds;
	}
	public DataSet select_SUB_FOR_EX_APP_INFORMATION(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().select_SUB_FOR_EX_APP_INFORMATION(pSet);
	}

	/*
	 * 提交审批（根据流水号取SUB_FOR_EX_APP_INFORMATION里面的数据提交审批）
	 */
	public DataSet submitapproval(ParameterSet pSet){
		DataSet ds = new DataSet();
		String state = (String)pSet.get("state");	
		String sblsh = (String)pSet.get("SBLSH");
		JSONObject precontent = JSONObject.fromObject(pSet.get("precontent"));
		String _state = "";
		if(state.equals("7")){//不通过
			_state = "noaccept";//return ds;
		}else if(state.equals("6")){//办结
			_state = "finish";
		}else{//通过
			_state = "accept";
		}
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		JSONObject cs = new JSONObject();
		cs.put("receiveNum", sblsh);
		cs.put("submitDate", dateString);//new Date());
		cs.put("userCode", precontent.get("USER_CODE"));
		cs.put("userName", precontent.get("USER_NAME"));
		cs.put("opinion",  precontent.get("OPINION"));
		cs.put("state",_state);//预审不通过noaccept  预审通过 accept 办结 finish	
		String data = cs.toString();
		HttpClient client = new HttpClient();
		//String url = "http://10.65.11.53:8080/web/approval/accpeted";
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/accpeted");

		PostMethod postMethod = new PostMethod(url);
		client.getParams().setContentCharset("UTF-8");		
		postMethod.setParameter("postdata", data);
		try{
			//提交事项应返回一个唯一id 用作更新审批状态标识
			client.executeMethod(postMethod);
			JSONObject ItemList =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
			if(ItemList.get("code").equals("200")){
				PowerBaseInfoSyncDao.getInstance().setPreList(pSet);
				ds.setState(StateType.SUCCESS);
				ds.setMessage("成功！");
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage("失败！");
		}
		return ds;
	}
	/*
	 * 提交审批(即将改为预审，请求流水号并将数据存储，等待提交审批)
	 */
	public DataSet submitSP(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));

				HttpClient client = new HttpClient();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/accpet");
		PostMethod postMethod = new PostMethod(url);
		client.getParams().setContentCharset("UTF-8");		
		postMethod.setParameter("postdata", data.toString());
		try{
				client.executeMethod(postMethod);
				JSONObject receiveNum =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
				if(receiveNum.get("state").equals("200")){
					PowerBaseInfoSyncDao.getInstance().submitData(receiveNum,data);
					if(data.getString("Transferred").equals("0")){//是否办结0是1否
						ParameterSet pst = new ParameterSet();
						Date currentTime = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String dateString = formatter.format(currentTime);
						JSONObject joPrecontent = new JSONObject();
						joPrecontent.put("ACTIVE", "0");
						joPrecontent.put("BSNUM", data.getString("itemId"));
						joPrecontent.put("CURRENT_NODE_ID", "1");
						joPrecontent.put("CURRENT_NODE_NAME", "预审通过");
						joPrecontent.put("NODE_TYPE", "1");
						joPrecontent.put("OPINION", "同意");
						joPrecontent.put("RECEIVE_TIME", dateString);//new Date());
						joPrecontent.put("SEND_TIME", dateString);//new Date());
						joPrecontent.put("STATUS", "1");
						joPrecontent.put("USER_CODE", "41D2DB1E914C4D38911AFF5D1865F2BC");
						joPrecontent.put("USER_NAME", "高新区工商局审核");					
						pst.put("state", "1");
						pst.put("SBLSH", receiveNum.getString("receiveNum"));
						pst.put("precontent", joPrecontent);
						submitapproval(pst);
						pst.clear();
						joPrecontent.put("ACTIVE", "1");
						joPrecontent.put("CURRENT_NODE_NAME", "办结");
						pst.put("state", "6");
						pst.put("SBLSH", receiveNum.getString("receiveNum"));
						pst.put("precontent", joPrecontent);
						submitapproval(pst);
					}
					ds.setState(StateType.SUCCESS);
					ds.setMessage("提交成功！申办流水号"+receiveNum.getString("receiveNum"));
				}else{
					ds.setState(StateType.FAILT);
					ds.setMessage("调用审批接口失败！");
				}
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("网办处理数据失败submitData！");
			return ds;
		}
		return ds;
		
	}
	public DataSet submitSP2(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
				HttpClient client = new HttpClient();///inspur/zhsl/webApply_geren  /Service/webApply
		String url = HttpUtil.formatUrl(SecurityConfig.getString("cloud_accept")+"/Service/webApply");
		PostMethod postMethod = new PostMethod(url);
		client.getParams().setContentCharset("UTF-8");		
		postMethod.setParameter("postdata", data.toString());
		try{
				client.executeMethod(postMethod);
				JSONObject receiveNum =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
				if(receiveNum.get("state").equals("200")){
					PowerBaseInfoSyncDao.getInstance().submitData(receiveNum,data);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("提交成功！申办流水号"+receiveNum.getString("receiveNum"));
				}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("网办处理数据失败submitData！");
			return ds;
		}
		return ds;
		
	}
	public DataSet saveDraft(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
		try{
					String o_sblsh=PowerBaseInfoSyncDao.getInstance().saveData(data);
					ds.setRawData(o_sblsh);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("保存成功！");
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("保存失败saveDraft！");
			return ds;
		}
		return ds;
		
	}
	public DataSet updatebaseinfo(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().updatebaseinfo(pSet);
	}
	public DataSet selectbusiness_log(ParameterSet pSet){
		return PowerBaseInfoSyncDao.getInstance().selectbusiness_log(pSet);
	}
}
