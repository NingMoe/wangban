/**  
 * @Title: MapCmd.java 
 * @Package icity.home 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-6-8 下午12:50:26 
 * @version V1.0  
 */ 
package app.icity.home;

import net.sf.json.JSONObject;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

/** 
 * @ClassName: MapCmd 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-6-8 下午12:50:26  
 */

public class MapCmd extends BaseQueryCommand{
		public DataSet getChannelList(ParameterSet pSet){
			if(!(pSet.get("CHANNEL")==null||"".equals((String)pSet.get("CHANNEL")))){
				String[] c =  pSet.get("CHANNEL").toString().split(",");
				JSONObject jo = new JSONObject();
				ParameterSet paSet =null;
				DataSet ds = new DataSet();
				for(int i=0;i<c.length;i++){
					paSet = new ParameterSet();
					paSet.setParameter("PARENT", c[i]);
					DataSet ds_= MapDao.getInstance().getChannelList(paSet);
					if(ds_.getTotal()>0){
						jo.put(c[i], ds_.getJAData());
					}else{
						jo.put(c[i], new DataSet());
					}
				}
				ds.setData(Tools.jsonToBytes(jo));
				ds.setTotal(1);
				return ds;
			}else{
				return new DataSet();
			}
			
			
		}
}
