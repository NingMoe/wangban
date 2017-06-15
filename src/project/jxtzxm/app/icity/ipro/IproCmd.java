package jxtzxm.app.icity.ipro;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jxtzxm.app.icity.ipro.IproDao;
import app.util.RestUtil;
import core.util.HttpClientUtil;
import io.netty.util.CharsetUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.CacheManager;
import com.inspur.util.db.DbHelper;

public class IproCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(IproCmd.class);
	/**
	 * 江西投资项目提交
	 */
	public DataSet saveInvestInfoJX(ParameterSet pSet) {
		return IproDao.getInstance().saveInvestInfoJX(pSet);
	}
	/**
	 * 江西投资项目 窗口待办
	 */
	public DataSet getWindowProject(ParameterSet pSet) {
		return IproDao.getInstance().getWindowProject(pSet);
	}
	/**
	 * 作废
	 * @param pSet
	 * @return
	 */
	public DataSet voidInvestInfo(ParameterSet pSet){
		return IproDao.getInstance().voidInvestInfo(pSet);
	}
	/**
	 * 根据项目代码获得项目登记信息
	 */
	public DataSet getInvestInfoByCode(ParameterSet pSet) {
		return IproDao.getInstance().getInvestInfoByCode(pSet);
	}
	/**
	 * 根据虚拟事项ID 获得虚拟事项下的真实事项
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByVirtualCode(ParameterSet pSet) {
		return IproDao.getInstance().getItemListByVirtualCode(pSet);
	}	
	/**
	 * 获取审批核准备案类的目录信息
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectDirectoryList(ParameterSet pSet) {
		return IproDao.getInstance().getProjectDirectoryList(pSet);
	}
	/**
	 * 根据目录编码获取事项列表江西
	 * @param pSet
	 * @return
	 */
	public DataSet getItemListByPermitCodeJX(ParameterSet pSet) {
		return IproDao.getInstance().getItemListByPermitCodeJX(pSet);
	}
	/**
	 * 获取FormId
	 * @param pSet
	 * @return
	 */
	public DataSet getFormInfo(ParameterSet pSet){
		return IproDao.getInstance().getFormInfo(pSet);
	}
	/**
	 * 单体事项在线办理
	 * @param pSet
	 * @return
	 */
	public DataSet accept(ParameterSet pSet){
		return IproDao.getInstance().accept(pSet);
	}	
	/**
	 * 单体事项在线办理，存库
	 * @param pSet
	 * @return
	 */
	public DataSet saveIproBusiness(ParameterSet pSet){
		return IproDao.getInstance().saveIproBusiness(pSet);
	}
	private final static String CACHE_KEY_FLAG = "IproCmd";
	/**
	 * 办件结果公示
	 * @param pSet
	 * @return
	 */
	public DataSet getDisplayList(ParameterSet pSet){
		String webRegion = SecurityConfig.getString("WebRegion");		
		String key = "IproCmd_getDisplayList" + webRegion;
		DataSet ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
		if (ds == null) {
			synchronized (key.intern()) {
				ds = (DataSet) CacheManager.get(CacheManager.EhCacheType, CACHE_KEY_FLAG, key);
				if (ds == null) {
					ds = IproDao.getInstance().getDisplayList(pSet);
					if (ds.getTotal() > 0) {
						CacheManager.set(CacheManager.EhCacheType, CACHE_KEY_FLAG, key, ds);
					}
				}
			}
		}
		return ds;
	}
	/**
	 * 公开公示分页
	 * @param pSet
	 * @return
	 */
	public DataSet getDisplayListByPage(ParameterSet pSet) {
		return IproDao.getInstance().getDisplayListByPage(pSet);
	}
	/**
	 * 全省申报项目动态
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectCount(ParameterSet pSet){
		return IproDao.getInstance().getProjectCount(pSet);
	}
	/**
	 * 全省申报项目行业分布
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectTopFive(ParameterSet pSet){
		return IproDao.getInstance().getProjectTopFive(pSet);
	}
	/**
	 * 我的项目列表
	 * @param pSet
	 * @return
	 */
	public DataSet getIproindexList(ParameterSet pSet){
		return IproDao.getInstance().getIproindexList(pSet);
	}
	/**
	 * 我的项目详情seqid
	 * @param pSet
	 * @return
	 */
	public DataSet getIproInfoBySeqid(ParameterSet pSet){
		return IproDao.getInstance().getIproInfoBySeqid(pSet);
	}
	/**
	 * 我的项目详情SBLSH
	 * @param pSet
	 * @return
	 */
	public DataSet getIproIndexByProjectCode(ParameterSet pSet) {
		return IproDao.getInstance().getIproIndexByProjectCode(pSet);
	}
	/**
	 * 单体事项办件详情
	 * @param pSet
	 * @return
	 */
	public DataSet getIproDetailByID(ParameterSet pSet){
		return IproDao.getInstance().getIproDetailByID(pSet);
	}
	/**
	 * 单体事项办件列表
	 * @param pSet
	 * @return
	 */
	public DataSet getIproBusinessList(ParameterSet pSet){
		return IproDao.getInstance().getIproBusinessList(pSet);
	}
	/**
	 * 获取项目报告
	 * @param pSet
	 * @return
	 */
	public DataSet getIproReport(ParameterSet pSet){
		return IproDao.getInstance().getIproReport(pSet);
	}
	/**
	 * 项目报告上传
	 * @param pSet
	 * @return
	 */
	public DataSet buildReport(ParameterSet pSet){
		return IproDao.getInstance().buildReport(pSet);
	}
	/**
	 * 项目进度查询
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectSchedule(ParameterSet pSet){
		return IproDao.getInstance().getProjectSchedule(pSet);
	}
}
