package api;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import api.impl.ApprovetransferImpl;
import api.impl.ChannelImpl;
import api.impl.IcityImpl;
import api.impl.WechatImpl;
import app.icity.sync.UploadUtil;
import core.util.CommonUtils_api;
/**
 * 为审批威海接口转接 主要是运管处业务。非转接审批接口
 * 暂不要向这个类增加方法
 * @author lihongyun
 */
@RestType(name = "api.approvetransfer", descript = "审批威海接口转接")
public class approvetransfer extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(approvetransfer.class);
	/**网约车
	 * 2.1	新增从业人员档案
	 * @param pset
	 * @return
	 */
	public DataSet addStaffArchive(ParameterSet pset){
		return ApprovetransferImpl.getInstance().addStaffArchive(pset);
	}	
	/**
	 * 查询办件进度(列表)//根据手机号
	 */
	public DataSet getBusinessList(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = WechatImpl.getInstance().getBusinessSearchQuery(pSet);	
		return ds;
	}
	/**
	 * 培训查询
	 * @param pset
	 * @return
	 */
	public DataSet queryStaffTrainingPlan(ParameterSet pset){
		return ApprovetransferImpl.getInstance().queryStaffTrainingPlan(pset);
	}
	/**
	 * 查询办件进度(列表)//根据身份证
	 */
	public DataSet getBusinessListByIdCard(ParameterSet pSet) {
		DataSet ds = new DataSet();		
		ds = WechatImpl.getInstance().getBusinessListByIdCard(pSet);	
		return ds;
	}
	/**
	 * 人员、资格查询
	 * @param pset
	 * @return
	 */
	public DataSet queryStaff(ParameterSet pset){
		return ApprovetransferImpl.getInstance().queryStaff(pset);
	}
	/**
	 * 新增资格
	 * @param pset
	 * @return
	 */
	public DataSet addStaffQualificationApply(ParameterSet pset){
		return ApprovetransferImpl.getInstance().addStaffQualificationApply(pset);
	}
	/**
	 * 新增户
	 * @param pset
	 * @return
	 */
	public DataSet newTaxiNetPlat(ParameterSet pset){
		return ApprovetransferImpl.getInstance().newTaxiNetPlat(pset);
	}
	/**
	 * 新增车
	 * @param pset
	 * @return
	 */
	public DataSet newTaxiNet(ParameterSet pset){
		return ApprovetransferImpl.getInstance().newTaxiNet(pset);
	}
	public String ygcgetpeixun(ParameterSet pset){
		return ApprovetransferImpl.getInstance().ygcgetpeixun(pset);
	}
}
