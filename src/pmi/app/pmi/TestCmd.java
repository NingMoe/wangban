package app.pmi;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.pmi.TestDao;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class TestCmd extends BaseQueryCommand {
	
	public DataSet delete(ParameterSet pSet){
		return TestDao.getInstance().delete(pSet);
	}
	public DataSet getList(ParameterSet pSet){
		return TestDao.getInstance().getList(pSet);
	}
	public void getTest(){
		
	}
	public DataSet getTestList(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONArray ja = new JSONArray();
		int num = Tools.formatInt(pSet.getParameter("num"));
		if(num ==0){
			num = 10;
		}
		for(int i=0;i<num;i++){
			JSONObject jo = new JSONObject();
			jo.put("ID", i);
			jo.put("USERNAME", "UNAME"+i);
			ja.add(jo);
		}
		ds.setTotal(num);
		ds.setData(Tools.stringToBytes(ja.toString()));
		return ds;
	}

}
