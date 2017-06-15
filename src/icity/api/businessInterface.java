package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jute.Record;
import org.exolab.castor.mapping.xml.Sql;

import api.impl.BusinessInterfaceImpl;
import app.icity.enterprises.EnterprisesDao;
import app.icity.guestbook.WriteCmd;
import app.icity.guestbook.WriteDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

/**
 * 投资项目在线审批 非上班时间预约 评价 咨询投诉相关  除了业务系统调接口更新状态
 * 暂不要再向这个文件里增加方法
 * @author lihongyun
 */
@RestType(name = "api.businessInterface", descript = "业务相关接口")
public class businessInterface extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(businessInterface.class);
	/**
	 * 插入评价
	 * @param pSet
	 * @return
	 */
	public DataSet insertNewEvaluation(ParameterSet pSet){
		return BusinessInterfaceImpl.getInstance().insertNewEvaluation(pSet);
	}
	/**
	 * 更新投资项目在线审批：调该接口更新项目编码 
	 * @param pSet
	 * @return
	 */
	public DataSet updateInvestmentProject(ParameterSet pSet){
		return BusinessInterfaceImpl.getInstance().updateInvestmentProject(pSet);
	}
	/**
	 * 更新投资项目在线审批 报告上传 状态
	 * @param pSet
	 * @return
	 */
	public DataSet updateBuildReport(ParameterSet pSet){
		return BusinessInterfaceImpl.getInstance().updateBuildReport(pSet);
	}
	/**
	 * 投资项目在线审批--单体事项状态更新接口
	 * @param pSet
	 * @return
	 */
	public DataSet setReturn(ParameterSet pSet) {
		return BusinessInterfaceImpl.getInstance().setReturn(pSet);
	}
	/**
	 * 非上班时间网上预约
	 * 
	 */
	public DataSet wsyyinsert(ParameterSet pSet){
		return BusinessInterfaceImpl.getInstance().wsyyinsert(pSet);
	}
	/**
	 * 删除咨询投诉
	 * transactid  咨询投诉id
	 * platform 1咨询2投诉
	 */
	public JSONObject delGuestbookById(ParameterSet pSet){
		return BusinessInterfaceImpl.getInstance().delGuestbookById(pSet);
	}
}