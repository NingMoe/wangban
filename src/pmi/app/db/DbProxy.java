package app.db;

import java.net.URLDecoder;
import java.util.Date;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.Tools;

import app.db.impl.DbManager;
import app.db.util.DbProvider;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DbProxy extends BaseQueryCommand{
	
	public DataSet execute(ParameterSet pSet) {
		DataSet sr = new DataSet();
		//StringBuffer err =new StringBuffer();
		String code = (String)pSet.getParameter("code");
		if(code == null || code.length()==0)
		{
			sr.setState(StateType.FAILT);
			sr.setMessage("参数【code】没有设置");
			return sr;
		}
		JSONObject map = (JSONObject)pSet.getParameter("pdata");
		map = insertBaseData(this.getUserInfo(pSet),map);
		return  DbManager.instance().execute(code,map);
	}
	private JSONObject insertBaseData(UserInfo u,JSONObject map){
		if(map == null || map.size()==0){
			map = new JSONObject();
		}
		if(u != null){
			map.put("__uid__", u.getUserId());
			map.put("__mid__", "");//u.getDomainId());
			map.put("__did__", u.getDeptId());
		}
		map.put("__date__", Tools.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		return map;
	}
	
	public DataSet batch(ParameterSet pSet) {
		DataSet sr = new DataSet();
		
		//StringBuffer err =new StringBuffer();
		String code = (String)pSet.getParameter("code");
		if(code == null || code.length()==0)
		{
			sr.setState(StateType.FAILT);
			sr.setMessage("参数【code】没有设置");
			return sr;
		}
		JSONObject map = (JSONObject)pSet.getParameter("pdata");
		map = insertBaseData(this.getUserInfo(pSet),map);
		JSONArray pvalue = (JSONArray)pSet.getParameter("pvalue");
		return DbManager.instance().batch(code,map,pvalue);
	}
	
	public DataSet entity(ParameterSet pSet) {
		DataSet sr = new DataSet();
		//StringBuffer err =new StringBuffer();
		String code = (String)pSet.getParameter("code");
		if(code == null || code.length()==0)
		{
			sr.setState(StateType.FAILT);
			sr.setMessage("参数【code】没有设置");
			return sr;
		}
		JSONObject map = (JSONObject)pSet.getParameter("pdata");
		map = insertBaseData(this.getUserInfo(pSet),map);
		return DbManager.instance().getEntity(code,map);
	}
	
	
	public DataSet collection(ParameterSet pSet) {
		DataSet sr = new DataSet();
		//StringBuffer err =new StringBuffer();
		String code = (String)pSet.getParameter("code");
		if(code == null || code.length()==0)
		{
			sr.setState(StateType.FAILT);
			sr.setMessage("参数【code】没有设置");
			return sr;
		}
		JSONObject map = (JSONObject)pSet.getParameter("pdata");
		map = insertBaseData(this.getUserInfo(pSet),map);
		int start = 0;
		try {
			start = pSet.getPageStart();
			//start = start+1;
			if(start<0){
				start = 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		int limit = -1;
		try {
			limit = pSet.getPageLimit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray ja = null;
		try{
			ja = (JSONArray)pSet.getParameter("pwhere");
		}
		catch(Exception ex){ex.printStackTrace();}
		
		ParameterSet pWhere = null;
		if(ja != null && ja.size()>0){
			pWhere = new ParameterSet();
			for(int i=0;i<ja.size();i++){
				JSONObject tjo = ja.getJSONObject(i);
				pWhere.setParameter(tjo.getString("label"), tjo.get("value"));
			}
		}
		return  DbManager.instance().getCollection(code,map,pWhere,start,limit);
	}
	public static void main(String[] args){
		/*DataSet ds = DbManager.instance().executeDatasetEx("SELECT * FROM pmi_db_sql",true);
		for(Iterator<Record> it = ds.getRecordSet().iterator(); it.hasNext();) {
			Record rd = (Record)it.next();
			JSONObject jo = new JSONObject();
			String str = (String)rd.get("SQL_CONTENT");
			jo.put("sql_content", StringUtils.isNotEmpty(str) ?  str:"");
			str = (String)rd.get("SQL_PARAMETERS");
			
			jo.put("sql_parameters", StringUtils.isNotEmpty(str) ? str : "[]");
			str = (String)rd.get("SQL_CONSTANT");
			jo.put("sql_constant", StringUtils.isNotEmpty(str) ? str: "[]");
			str = (String)rd.get("SQL_OTHER");
			jo.put("sql_other",StringUtils.isNotEmpty(str) ?  str:"");
			str = (String)rd.get("SQL_TITLE");
			jo.put("sql_title", StringUtils.isNotEmpty(str) ?  str:"");
			str = (String)rd.get("CODE");
			jo.put("code", StringUtils.isNotEmpty(str) ?  str:"");
			if(StringUtils.isNotBlank(str)){
				Tools.writeFile(Tools.jsonToBytes(jo), "v:\\temp",jo.getString("code")+".json");
			}
		}
		System.out.print("生成文件成功！");
		*/
	}
}
