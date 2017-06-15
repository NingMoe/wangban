package api;

import net.sf.json.JSONObject;

import api.impl.HlverifyImpl;
import api.impl.IcityImpl;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;

@SuppressWarnings("deprecation")
@RestType(name = "api.hlverify", descript = "重庆华龙网验证登陆业务接口")
public class hlverify extends BaseQueryCommand {
	/**
	 * zhuce
	 * @param pSet
	 * @return
	 */
	public JSONObject submitRegister(ParameterSet pSet){
		return HlverifyImpl.getInstance().submitRegister(pSet);
	}
}