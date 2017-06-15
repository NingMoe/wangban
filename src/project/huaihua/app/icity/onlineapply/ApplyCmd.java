package huaihua.app.icity.onlineapply;


import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.guestbook.WriteDao;
import huaihua.app.icity.onlineapply.ApplyCloudDao;
import app.icity.onlineapply.ApplyDao;

import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import core.util.HttpClientUtil;

public class ApplyCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ApplyCmd.class);
	
	private WriteDao writeDao;
	
	public DataSet submitSP(ParameterSet pSet) {
		DataSet ds = new DataSet();
		JSONObject data = ((JSONObject) pSet.get("data"));
		try {
			if("1".equals(SecurityConfig.getString("is_cloud_accept"))){
				data = ApplyCloudDao.getInstance().submitData(data);
			}else{
				data = ApplyDao.getInstance().submitData(data);
			}
				// 方便第三方公司存储，读取配置文件security.properties的NetDiskDownloadAddress路径
				data.put("filepath", SecurityConfig.getString("NetDiskDownloadAddress"));
				//获取业务表单数据
				String formId = data.getString("formId");
				String formDataId=data.getString("dataId");
				String url1=SecurityConfig.getString("Form_url"); 
				url1+="/cform/getFormStandardData?formId="+formId+"&formDataId="+formDataId;
				HttpClientUtil client1 = new HttpClientUtil();
				String html= client1.getResult(url1,"");
				//将业务表单信息添加到data
				data.put("html", html);
				//提交综合受理系统失败，转为暂存
				/*if(data.containsKey("retstate")){
					if("0".equals(data.getString("retstate"))){
						ds.setState((byte)4);
					}
				}*/
				ds.setRawData(data);
				
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("网办处理数据失败submitData！");
		}
		return ds;
	}
	
	public DataSet saveDraft(ParameterSet pSet){
		DataSet ds = new DataSet();
		JSONObject data =((JSONObject)pSet.get("data"));
		try{
					String o_sblsh=ApplyDao.getInstance().saveData(data);
					ds.setRawData(o_sblsh);
					ds.setState(StateType.SUCCESS);
					ds.setMessage("保存成功！");
						
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("保存失败！");
			return ds;
		}
		return ds;
		
	}
}
