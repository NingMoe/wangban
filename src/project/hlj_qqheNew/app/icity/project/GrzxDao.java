package hlj_qqheNew.app.icity.project;

import io.netty.util.CharsetUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.project.ProjectQueryDao;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.hsf.service.common.utils.StringUtils;
import com.inspur.util.CacheManager;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.HttpClientUtil;


public class GrzxDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(GrzxDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String tyspDataSource = "tyspDataSource";

	public static GrzxDao getInstance() {
		return DaoFactory.getDao(GrzxDao.class.getName());
	}

	public GrzxDao() {
		this.setDataSourceName(icityDataSource);
	}
	
	public DataSet getUserById(ParameterSet pset){
		return this.getList(pset);
	}
	
	public DataSet getList(ParameterSet pset) {
		String sql = "select u.id,u.account,u.name,u.email,u.phone,u.type,u.card_type,u.card_no,to_char(u.creation_time,'yyyy-mm-dd hh24:mi:ss') creation_time,u.address from UC_USER u ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}
	
	public DataSet queryZxTsCount(ParameterSet pset) {
		String sql = "select sum(case when type='2' then 1 else 0 end) zx,sum(case when type='3' then 1 else 0 end) ts from guestbook where 1=1 ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		return this.executeDataset(sql, null);
	}
	
	public DataSet getBusinessCount(ParameterSet pset) {
		String sql = "select sum(case when state='00' then 1 else 0 end) zcj," +
				"            sum(case when state in ('01','11','23','24') then 1 else 0 end) zbj," +
				"            sum(case when state in ('02','97','98','99') then 1 else 0 end) bjj," +
				"            sum(case when state in ('14','21','26','96') then 1 else 0 end) thj from BUSINESS_INDEX t ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		return this.executeDataset(sql, null);
	}
	
	public DataSet getBusinessIndexList(ParameterSet pset) {
		String searchTitle = (String)pset.remove("SEARCH_TITLE");
		String sql = "SELECT LIMIT_NUM,SBLSH,SXID,SXBM,SXMC,SJDW,SQRMC,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,SBFS,UCID,SBXMMC,SLSJ,YSLZTDM,YSLJGMS,SLZTDM,SPHJDM,BJJGDM,REMARK,STATE,URL,"
				+ " YSLSJ,BJSJ,l.id as starLevelId FROM BUSINESS_INDEX t "
				+ " left outer join star_level_evaluation l on t.sblsh=l.SERIAL_NUMBER and t.sxbm=l.SERVICE_CODE where 1=1 ";
		if(StringUtil.isNotEmpty(searchTitle)){
			sql+=" AND (SBLSH like '%"+searchTitle+"%' OR SBXMMC like '%"+searchTitle+"%' OR SXMC like '%"+searchTitle+"%' ) ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null, this.getDataSourceName());
		} else {
			return this.executeDataset(sql, start, limit, null, this.getDataSourceName());
		}
	}
	
	public DataSet getBusinessAccept(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String sblsh = (String)pSet.get("sblsh");//
		HttpClientUtil client = new HttpClientUtil();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("cloud_accept")+"/Service/getBusinessInfo?receiveNumber="+sblsh);//;
		try{
			Object strItemList= client.getResult(url,"");
			JSONArray info;
			info = JSONObject.fromObject(strItemList).getJSONArray("info");		
			//ds.setData((Tools.jsonToBytes((JSONObject)((JSONObject)ja.get(0)).get("BASECONTENT"))));
			ds.setData(Tools.jsonToBytes((JSONObject)info.toJSONObject(info)));
	    }catch(Exception e){
	    	e.printStackTrace();
	    }	
		return ds;
	}
	
	

}
