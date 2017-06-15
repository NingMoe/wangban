package api;

import java.text.ParseException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import api.impl.taiheWechatImpl;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;

@RestType(name = "api.taihewechat", descript = "taihe相关接口")
public class taihewechat extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(item.class);
	

	

	/**
	 * 太和县网办对讯飞开放的特殊注册接口(用户名和密码)
	 * 个人用户注册接口
	 */
	public DataSet register_per(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		pSet.put("ly", "2");
		System.out.println("test1");
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = taiheWechatImpl.getInstance().register_per(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			System.out.println("test2");
			ds = taiheWechatImpl.getInstance().register_per(pSet);
		}
		return ds;
	}

	/**
	 * 企业用户注册接口
	 */
	public DataSet register_org(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String access = pSet.getRemoteAddr();
		pSet.put("ly", "2");
		if("on".equals(SecurityConfig.getString("accessSwith"))){
			if(SecurityConfig.getString("accessWhiteList").contains(access)){
				ds = taiheWechatImpl.getInstance().register_org(pSet);
			}else{
				ds.setRawData(new JSONArray());
				ds.setMessage(access+"非法访问！");
				ds.setState((byte)2);
			}
		}else{
			ds = taiheWechatImpl.getInstance().register_org(pSet);
		}
		return ds;
	}

	

}
