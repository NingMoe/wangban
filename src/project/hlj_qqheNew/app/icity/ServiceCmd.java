package hlj_qqheNew.app.icity;

import io.netty.util.CharsetUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

import core.util.HttpClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;


public class ServiceCmd extends BaseQueryCommand {
    public DataSet getDeptList(ParameterSet pset) {
        DataSet ds = new DataSet();
        String url = HttpUtil.formatUrl(SecurityConfig.getString("synchronousDept") + "/web/organ");
        //String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl")+"/web/c/getDeptList");		
        HttpClient client = new HttpClient();
        client.getParams().setContentCharset("UTF-8");
        PostMethod postMethod = new PostMethod(url);
        postMethod.setParameter("region_code", (String) pset.getParameter("region_code"));
        try {
            client.executeMethod(postMethod);
            JSONObject obj = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            ds.setData(obj.toString().getBytes(CharsetUtil.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
    public DataSet getItemList(ParameterSet pset) {
        DataSet ds = new DataSet();
        String org_code = String.valueOf(pset.getParameter("org_code"));
        HttpClientUtil client = new HttpClientUtil();
        String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemList?orgCode="+org_code);//deptid;
        //String url = HttpUtil.formatUrl(SecurityConfig.getString("webSiteUrl")+"/web/c/getDeptList");		
      
        String strItemList = "";
		JSONArray ja;
		JSONObject json=new JSONObject();
        try {
        	strItemList= client.getResult(url,"");
			ja = JSONArray.fromObject(strItemList);
			json.put("ja", ja);
			ds.setData(Tools.jsonToBytes(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
    public DataSet getPermission(ParameterSet pSet) {
        DataSet ds = new DataSet();
        String itemId = (String) pSet.getParameter("id");
        String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getAllItemInfoByItemID?itemId=" + itemId);
        HttpClientUtil client = new HttpClientUtil();
        JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
        JSONArray itemInfo = item.getJSONArray("ItemInfo");//基本信息
        JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
        item.put("itemBasicInfo", itemBasicInfo);
        String downurl = HttpUtil.formatUrl(SecurityConfig.getString("NetDiskDownloadAddress"));
        item.put("DOWNURL", downurl);
        ds.setData(item.toString().getBytes(CharsetUtil.UTF_8));
        return ds;

    }

    public DataSet getDuty(ParameterSet pSet) {
        DataSet ds = new DataSet();
        try {
            String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseOrgDutyList");
            HttpClient client = new HttpClient();
            client.getParams().setContentCharset("UTF-8");
            PostMethod postMethod = new PostMethod(url);
            StringBuilder whereValue = new StringBuilder();
            JSONArray paramValue = new JSONArray();
            String deptid = (String) pSet.getParameter("deptid");
            String duty_id = (String) pSet.getParameter("duty_id");
            if (StringUtils.isNotEmpty(deptid)) {
                whereValue.append(" and org_code=? ");
                paramValue.add(deptid);
            }
            if (StringUtils.isNotEmpty(duty_id)) {
                whereValue.append(" and id=? ");
                paramValue.add(duty_id);
            }
            postMethod.setParameter("page", pSet.getParameter("page").toString());
            postMethod.setParameter("rows", pSet.getParameter("limit").toString());
            postMethod.setParameter("whereValue", whereValue.toString());
            postMethod.setParameter("paramValue", paramValue.toString());
            client.executeMethod(postMethod);
            JSONObject obj;
            obj = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            JSONArray pageList = obj.getJSONArray("pageList");
            JSONArray rows = new JSONArray();
            for (int i = 0; i < pageList.size(); i++) {
                JSONObject column;
                column = (JSONObject) pageList.get(i);
                rows.add(column.get("columns"));
            }
            ds.setRawData(rows);
            ds.setTotal(obj.getInt("totlaRow"));
        } catch (Exception e) {
            e.printStackTrace();
            ds.setState(StateType.FAILT);
            ds.setMessage("查询失败！");
        }
        return ds;
    }

    public DataSet getBoundary(ParameterSet pSet) {
        DataSet ds = new DataSet();
        try {
            String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseBoundaryItemList");
            HttpClient client = new HttpClient();
            client.getParams().setContentCharset("UTF-8");
            PostMethod postMethod = new PostMethod(url);
            StringBuilder whereValue = new StringBuilder();
            JSONArray paramValue = new JSONArray();
            String deptid = (String) pSet.getParameter("deptid");
            String duty_id = (String) pSet.getParameter("duty_id");
            if (StringUtils.isNotEmpty(deptid)) {
                whereValue.append(" and org_code=? ");
                paramValue.add(deptid);
            }
            if (StringUtils.isNotEmpty(duty_id)) {
                whereValue.append(" and id=? ");
                paramValue.add(duty_id);
            }
            postMethod.setParameter("page", pSet.getParameter("page").toString());
            postMethod.setParameter("rows", pSet.getParameter("limit").toString());
            postMethod.setParameter("whereValue", whereValue.toString());
            postMethod.setParameter("paramValue", paramValue.toString());
            client.executeMethod(postMethod);
            JSONObject obj;
            obj = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            JSONArray pageList = obj.getJSONArray("pageList");
            JSONArray rows = new JSONArray();
            for (int i = 0; i < pageList.size(); i++) {
                JSONObject column;
                column = (JSONObject) pageList.get(i);
                rows.add(column.get("columns"));
            }
            ds.setRawData(rows);
            ds.setTotal(obj.getInt("totlaRow"));
        } catch (Exception e) {
            e.printStackTrace();
            ds.setState(StateType.FAILT);
            ds.setMessage("查询失败！");
        }
        return ds;
    }

    //获取办理意见
    public DataSet getEnterPrise(ParameterSet pset) {
        DataSet ds = new DataSet();
        String url = HttpUtil.formatUrl(SecurityConfig.getString("EAParallelUrl") + "getHandleOpinion");
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        postMethod.setParameter("firstCode", (String) pset.getParameter("fircode"));
        postMethod.setParameter("itemId", (String) pset.getParameter("iid"));

        try {
            client.executeMethod(postMethod);
            JSONObject obj;
            obj = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            ds.setData(obj.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }


    public DataSet getSupervise(ParameterSet pSet) {
        DataSet ds = new DataSet();
        try {
            String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getBaseSuperviseSystemList");
            HttpClient client = new HttpClient();
            client.getParams().setContentCharset("UTF-8");
            PostMethod postMethod = new PostMethod(url);
            StringBuilder whereValue = new StringBuilder();
            JSONArray paramValue = new JSONArray();
            String deptid = (String) pSet.getParameter("deptid");
            String duty_id = (String) pSet.getParameter("duty_id");
            if (StringUtils.isNotEmpty(deptid)) {
                whereValue.append(" and org_code=? ");
                paramValue.add(deptid);
            }
            if (StringUtils.isNotEmpty(duty_id)) {
                whereValue.append(" and id=? ");
                paramValue.add(duty_id);
            }
            postMethod.setParameter("page", pSet.getParameter("page").toString());
            postMethod.setParameter("rows", pSet.getParameter("limit").toString());
            postMethod.setParameter("whereValue", whereValue.toString());
            postMethod.setParameter("paramValue", paramValue.toString());
            client.executeMethod(postMethod);
            JSONObject obj;
            obj = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            JSONArray pageList = obj.getJSONArray("pageList");
            JSONArray rows = new JSONArray();
            for (int i = 0; i < pageList.size(); i++) {
                JSONObject column;
                column = (JSONObject) pageList.get(i);
                rows.add(column.get("columns"));
            }
            ds.setRawData(rows);
            ds.setTotal(obj.getInt("totlaRow"));
        } catch (Exception e) {
            e.printStackTrace();
            ds.setState(StateType.FAILT);
            ds.setMessage("查询失败！");
        }
        return ds;
    }
    
    
    
  //获取工程建设企业投资阶段
  	public DataSet getEnginState(ParameterSet pset){
  		DataSet ds = new DataSet();
  		String url = HttpUtil.formatUrl(SecurityConfig.getString("ECParallelUrl")+"getStage");
  		HttpClient client = new HttpClient();
  		PostMethod postMethod = new PostMethod(url);
  		postMethod.setParameter("investType",(String) pset.getParameter("investType"));
  		try {
  			client.executeMethod(postMethod);
  			JSONObject obj;		
  			obj =  JSONObject.fromObject(postMethod.getResponseBodyAsString());
  			ds.setRawData(obj);
  		} catch (Exception e) {
  			e.printStackTrace();
  		} 
  		return ds;
  	}


}