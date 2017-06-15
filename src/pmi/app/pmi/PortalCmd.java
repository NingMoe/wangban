package app.pmi;
import java.util.List;

import com.google.code.fqueue.util.Config;
import com.icore.StateType;
import com.icore.http.util.HttpUtil;
import com.icore.log.Logger;
import com.icore.util.SecurityConfig;
import com.icore.util.SecurityConfig.ConfigEnum;
import com.icore.util.StringUtil;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class PortalCmd extends BaseQueryCommand {
	private static final Logger log = Logger.getLogger(PortalCmd.class);
	public DataSet getTestPageContent(ParameterSet pSet){
		String pageId = (String)pSet.get("ID");
		DataSet ds = new DataSet();
		try{
			String content = PortalDao.getInstance().getTestPageContent(pageId);
			ds.setRawData(content);
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet getReleasePageContent(ParameterSet pSet){
		String pageId = (String)pSet.get("ID");
		DataSet ds = new DataSet();
		try{
			String content = PortalDao.getInstance().getReleasePageContent(pageId);
			ds.setRawData(content);
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet savePage(ParameterSet pSet){
		String pageId = (String)pSet.get("ID");
		String content = (String)pSet.get("CONTENT");
		Boolean isRelease = ((String)pSet.get("IS_RELEASE")).equals("1") ? true : false;
		DataSet ds = new DataSet();
		try{
			PortalDao.getInstance().savePage(pageId,content,isRelease);
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet getWidgetList(ParameterSet pSet){
		String allFlag = (String)pSet.get("ALL_FLAG");
		Boolean bAll = false;
		if(StringUtil.isNotEmpty(allFlag) && allFlag.equals("1")){
			bAll = true;
		}
		
		DataSet ds = new DataSet();
		try{
			ds.setRawData(PortalDao.getInstance().getWidgetList(bAll));
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet getWebSiteMenu(ParameterSet pSet){
		String allFlag = (String)pSet.get("ALL_FLAG");
		Boolean bAll = false;
		if(StringUtil.isNotEmpty(allFlag) && allFlag.equals("1")){
			bAll = true;
		}
		String webSiteId = (String)pSet.get("WEBSITE_ID");
		if(!StringUtil.isNotEmpty(webSiteId)){
			webSiteId  = HttpUtil.contextPath().replace("/", "");
		}
		String isRelease = (String)pSet.get("IS_RELEASE");
		
		DataSet ds = new DataSet();
		String webSiteType = (String)pSet.get("WEBSITE_TYPE");
		if(!StringUtil.isNotEmpty(webSiteType)){
			webSiteType = SecurityConfig.getString(ConfigEnum.PAGE_MODE);
		}
		try{
			ds.setRawData(PortalDao.getInstance().getWebSiteMenu(bAll,webSiteId,webSiteType,isRelease));
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet insertWebSiteMenu(ParameterSet pSet){
		String webSiteId = (String)pSet.get("WEBSITE_ID");
		if(!StringUtil.isNotEmpty(webSiteId)){
			pSet.put("WEBSITE_ID",  HttpUtil.contextPath().replace("/", ""));
		}
		String webSiteType = (String)pSet.get("WEBSITE_TYPE");
		if(!StringUtil.isNotEmpty(webSiteType)){
			pSet.put("WEBSITE_TYPE", SecurityConfig.getString(ConfigEnum.PAGE_MODE));
		}
		DataSet ds = new DataSet();
		try{
			boolean successFlag = PortalDao.getInstance().insertWebSiteMenu(pSet);
			if(!successFlag){
				ds.setState(StateType.FAILT);
				ds.setMessage("添加菜单失败。");
			}
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet updateWebSiteMenu(ParameterSet pSet){
		String webSiteId = (String)pSet.get("WEBSITE_ID");
		if(!StringUtil.isNotEmpty(webSiteId)){
			pSet.put("WEBSITE_ID",  HttpUtil.contextPath().replace("/", ""));
		}
		String webSiteType = (String)pSet.get("WEBSITE_TYPE");
		if(!StringUtil.isNotEmpty(webSiteType)){
			pSet.put("WEBSITE_TYPE", SecurityConfig.getString(ConfigEnum.PAGE_MODE));
		}
		DataSet ds = new DataSet();
		try{
			boolean successFlag = PortalDao.getInstance().updateWebSiteMenu(pSet);
			if(!successFlag){
				ds.setState(StateType.FAILT);
				ds.setMessage("修改菜单失败。");
			}
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
	public DataSet deleteWebSiteMenu(ParameterSet pSet){
		String webSiteId = (String)pSet.get("WEBSITE_ID");
		if(!StringUtil.isNotEmpty(webSiteId)){
			pSet.put("WEBSITE_ID",  HttpUtil.contextPath().replace("/", ""));
		}
		DataSet ds = new DataSet();
		try{
			boolean successFlag = PortalDao.getInstance().deleteWebSiteMenu(pSet);
			if(!successFlag){
				ds.setState(StateType.FAILT);
				ds.setMessage("删除菜单失败。");
			}
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);
			ds.setState(StateType.FAILT);
			ds.setMessage(ex.getMessage());
		}
		return ds;
	}
}
