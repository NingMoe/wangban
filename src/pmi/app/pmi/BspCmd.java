package app.pmi;

import org.apache.commons.lang.StringUtils;

import app.pmi.BspDao;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;


public class BspCmd extends BaseQueryCommand {
	public DataSet getTest(){
		return new DataSet();
	}
	
	
	 public DataSet proxy(ParameterSet pset){
			DataSet ds = new DataSet();
			String url = (String)pset.get("url");
			if(StringUtils.isNotEmpty(url)){
				try {
					String c = com.inspur.util.UrlClient.newInstance().readUrl(url,"UTF-8");
					if(c == null){
						c = "";
					}
					try{
						net.sf.json.JSONObject jo = net.sf.json.JSONObject.fromObject(c);
						c = jo.toString();
					}
					catch(Exception ee){
						ee.printStackTrace();
					}
					ds.setData(Tools.stringToBytes(c));
				} catch (Exception e) {
					ds.setMessage(e.getMessage());
					ds.setState(StateType.FAILT);
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("请传递url参数");
			}
			return ds;
		}
	 
	 public DataSet getNewId(ParameterSet pset) throws Exception{
		 String idString = (String)pset.get("idString");
		 DataSet ds = new DataSet();
		 if(null == idString || "".equals(idString)){
			 throw new Exception("getNewId方法参数为空");
		 }
		 String id = Tools.getNewID(idString);
		 ds.setData(Tools.stringToBytes(id));
		 return ds;
	 }
	 
	public DataSet keyValueArrays(ParameterSet pset) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		DataSet ds =  BspDao.getInstance().getKeyValueArrays(pset);
		return ds;
	}
}