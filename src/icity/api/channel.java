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

import api.impl.ChannelImpl;
import app.icity.sync.UploadUtil;
import core.util.CommonUtils_api;
/**
 * 栏目内容接口
 * @author lihongyun
 */
@RestType(name = "api.channel", descript = "栏目、内容相关接口")
public class channel extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(channel.class);

	/**
	 * 获取根节点栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getRootChannel(ParameterSet pset){
		return ChannelImpl.getInstance().getRootChannel(pset);
	}
	/**
	 * 根据栏目名称获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelByName(ParameterSet pset){
		return ChannelImpl.getInstance().getChildChannelByName(pset);
	}
	/**
	 * 根据栏目id获取子栏目列表
	 * @param pset
	 * @return
	 */
	public DataSet getChildChannelById(ParameterSet pset){
		return ChannelImpl.getInstance().getChildChannelById(pset);
	}
	/**
	 * 根据栏目id获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByCid(ParameterSet pset){
		return ChannelImpl.getInstance().getContentListByCid(pset);
	}
	/**
	 * 根据内容id获取内容详细
	 * @param pset
	 * @return
	 */
	public DataSet getContentById(ParameterSet pset){
		return ChannelImpl.getInstance().getContentById(pset);
	}
	/**
	 * 根据栏目名称获取内容列表
	 * @param pset
	 * @return
	 */
	public DataSet getContentListByName(ParameterSet pset){
		return ChannelImpl.getInstance().getContentListByName(pset);
	}
	
	public DataSet getContentListByPage(ParameterSet pset){
		return ChannelImpl.getInstance().getContentListByPage(pset);
	}
}
