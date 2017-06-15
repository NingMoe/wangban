package hlj_qqheNew.app.icity.project;

import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jute.Record;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.HttpClientUtil;


public class ProjectIndexDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String tyspDataSource = "tyspDataSource";
	private static ProjectIndexDao _instance = null;

	public static ProjectIndexDao getInstance() {
		return DaoFactory.getDao(ProjectIndexDao.class.getName());
	}

	public ProjectIndexDao() {
		this.setDataSourceName(icityDataSource);
	}
	
	
	
	public DataSet getMenuContent(ParameterSet pset) {
		String sql = "select * from (select pub_content.ID,pub_content.name,pub_content.content,to_char(pub_content.ctime,'yyyy-MM-dd') as VALID_DATE_START "
				+ "from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务新闻' and pub_content.status='1' "
				+ "order by pub_content.ctime desc ) where rownum<7";
		return this.executeDataset(sql, null, icityDataSource);
	}
	
	public DataSet getMenuContentMore(ParameterSet pset) {
		String sql = "select pub_content.ID,pub_content.name,pub_content.content,to_char(pub_content.ctime,'yyyy-MM-dd') as VALID_DATE_START "
				+ "from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务新闻' and pub_content.status='1'  "
				+ "order by pub_content.ctime desc ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null, this.getDataSourceName());
		} else {
			return this.executeDataset(sql, start, limit, null, this.getDataSourceName());
		}
	}
	
	public DataSet getMenuContentById(ParameterSet pset) {
		String sql = "select pub_content.ID,pub_content.name,pub_content.content,to_char(pub_content.ctime,'yyyy-MM-dd') as VALID_DATE_START "
				+ "from PUB_CONTENT,pub_channel  where pub_channel.status='1' and  pub_content.CID=pub_channel.ID(+) and pub_channel.name='政务新闻' and pub_content.status='1' "
				+ " and pub_content.ID='"+pset.getParameter("ID")+"' order by pub_content.ctime desc";
		return this.executeDataset(sql, null, icityDataSource);
	}
	
	public DataSet getRedian(ParameterSet pSet) {
		//String sql = "select * from (select SXMC,SXID,count(sxmc) con from business_index where XZQHDM='"+pSet.remove("XZQHDM")+"' group by sxmc,SXID order by count(sxmc) desc) where rownum<18 ";
		String sql = "select * from (select SXMC,SXID,count(sxmc) con from business_index  group by sxmc,SXID order by count(sxmc) desc) where rownum<18 ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
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
	
	public DataSet getBusinessIndexAllList(ParameterSet pset) {
		String sql = "SELECT SBLSH, SXBM, SXMC, UCID,SBSJ, SQRLX, SQRMC, LXRXM, LXRSJ, SBXMMC, SBHZH, XZQHDM, YSLSJ, YSLZTDM, YSLJGMS, SLSJ, YWLSH, SJDW, "
				+ " SJDWDM, SLZTDM, BSLYY, SLHZH, CXMM, SPSJ, SPHJDM, SPHJMC, BZGZSJ, BZGZYY, BZCLQD, BZSJ, TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
				+ " BJSJ, BJJGDM, BJJGMS, ZFHTHYY, LQSJ, REMARK ,STATE,B.CONTENT FROM BUSINESS_INDEX B WHERE 1=1 ";
		DataSet ds = new DataSet();
		try {
			sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
			int start = pset.getPageStart();
			int limit = pset.getPageLimit();
			if (start == -1 || limit == -1) {
				ds = this.executeDataset(sql);
			} else {
				ds = this.executeDataset(sql, start, limit, null);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return ds;
	}
	
	//抓取齐齐哈尔政府网
	public DataSet getMess(){
		DataSet ds = new DataSet();
		Document doc;
		try {
			doc = Jsoup.connect("http://www.qqhr.gov.cn/newApp/qwys/zwyw1.jsp").get();
			String  s= doc.toString();
			int start=s.indexOf("<td valign=\"top\">");
		    int end=s.indexOf("</table>", start);
		    String message=s.substring(start,end); 
		    message=message.replace("News_showNews.action?messagekey=", "http://www.qqhr.gov.cn/News_showNews.action?messagekey=");
		    String regex = "<a (.*)</a>";
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(message);
	        JSONArray ja=new JSONArray();
	        int a=0;
	        while (matcher.find()) {
	        	ja.add(a, matcher.group(0));
	        	a=a+1;
	        }
	        ds.setRawData(ja);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ds;  
	}
	
	
	public DataSet getBusinessEnterList(ParameterSet pset) {
		String searchTitle = (String)pset.remove("SEARCH_TITLE");
		String sql="SELECT a.receive_id,a.ID,a.APPLY_SUBJECT,a.FIRST_GRADE_CODE,to_char(a.APPLY_TIME,'yyyy-MM-dd hh24:mm:ss') APPLY_TIME,a.CURRENT_STATE,a.FORM_ID,a.ENTERPRISE_TYPE,a.DATA_ID,a.APPLY_DATA,(select sum(case when b.is_lhtk='1' then 1 else 0 end) is_lhtk from enterprise_business_course b where b.biz_id = a.id) IS_LHTK from ENTERPRISE_BUSINESS_INDEX a where 1=1 ";
		if(StringUtil.isNotEmpty(searchTitle)){
			sql+=" AND (SBLSH like '%"+searchTitle+"%' OR SBXMMC like '%"+searchTitle+"%' OR SXMC like '%"+searchTitle+"%' ) ";
		}
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		DataSet ds;
		if (start == -1 || limit == -1) {
			ds =  this.executeDataset(sql, new Object[]{}, this.getDataSourceName());
		}else {
			ds = this.executeDataset(sql, start, limit);//.executeDataset(sql, start, limit, new Object[]{}, this.getDataSourceName());
		}
		return ds;
	}
	
	/**
	 * 
	 * @Description: 获取表单详细信息
	 * @param @param pset    TYPE 0:业务列表      1：企业设立        2：工程建设 
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2015-8-10
	 * @throws
	 */
	public DataSet formInfoQueryByNameAndId(ParameterSet pset) {
		
		DataSet ds = new DataSet();
		String type = null;
		if(pset.containsKey("TYPE")){
			type = String.valueOf( pset.getParameter("TYPE") );
			pset.remove("TYPE");
		}
		if("0".equals(type)){    //业务列表
			String sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb=jb.getJSONObject("BASECONTENT");
						String dataId = jb.getString("dataId");
						String formId = jb.getString("formId");
						String itemId = jb.getString("itemId");
						jb = new JSONObject();
						jb.put("dataId", dataId);
						jb.put("formId", formId);
						jb.put("itemId", itemId);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("1".equals(type)){     //企业设立
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("ID", SBLSH);
			}
			String sql = "select ID as SBLSH, FORM_ID as formId, DATA_ID as dataId from enterprise_business_index WHERE 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb.put("dataId", String.valueOf( jb.get("DATAID")));
						jb.put("formId", String.valueOf( jb.get("FORMID")));
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("2".equals(type)){
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("BIZ_ID", SBLSH);
			}
			String sql = "select BIZ_ID as SBLSH, FORM_ID as formId, DATA_ID as dataId from parallel_biz_base where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb.put("dataId", String.valueOf( jb.get("DATAID")));
						jb.put("formId", String.valueOf( jb.get("FORMID")));
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}
		
		return ds;
	}

	/**
	 * 
	 * @Description: 获取材料信息列表
	 * @param @param pset  TYPE 0:业务列表      1：企业设立        2：工程建设 
	 * @param @return
	 * @return DataSet 返回类型
	 * @author liuyq
	 * @since 2015-8-13
	 * @throws
	 */
	public DataSet materialListQueryByNameAndId(ParameterSet pset) {
		String type = null;
		DataSet ds = new DataSet();
		if(pset.containsKey("TYPE")){
			type = String.valueOf( pset.getParameter("TYPE") );
			pset.remove("TYPE");
		}
		if("0".equals(type)){      //业务列表
			String sql = "select basecontent from SUB_FOR_EX_APP_INFORMATION where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						jb = jb.getJSONObject("BASECONTENT");
						String metail = String.valueOf( jb.get("metail") );
						String windowsub = String.valueOf(jb.get("windowsub"));
						jb = new JSONObject();
						jb.put("metail", metail);
						jb.put("windowsub", windowsub);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("1".equals(type)){          //企业
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("ID", SBLSH);
			}
			String sql = "select APPLY_DATA from enterprise_business_index  where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){				
                     JSONArray ja = ds.getJAData();                    
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						String re = String.valueOf( jb.get("APPLY_DATA") );
						JSONArray materialArray = JSONArray.fromObject(re);
						JSONArray materialArrayResu = new JSONArray();
						for(int i=0; i<materialArray.size(); i++){
							jb = materialArray.getJSONObject(i);
							jb.put("DOCUMENT_ID", jb.get("resourceCode"));
							jb.put("DOCUMENT_NAME", jb.get("fileName"));
							jb.put("FILE_NAME", jb.get("name"));
							jb.put("FILE_PATH", jb.get("filePath"));
							jb.put("OPERATOR_NAME", jb.get("— —"));
							jb.put("SUBJECT", jb.get("subject"));
							materialArrayResu.add(jb);
						}
						jb = new JSONObject();
						jb.put("metail", materialArrayResu);
						ds.setRawData(jb);
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}else if("2".endsWith(type)){        //工程设立 
			if(pset.containsKey("SBLSH")){
				String SBLSH = String.valueOf( pset.getParameter("SBLSH") );
				pset.remove("SBLSH");
				pset.put("BIZ_ID", SBLSH);
			}
			String sql = "select APPLY_DATA from parallel_biz_base where 1=1 ";
			try {
				sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
				ds = this.executeDataset(sql);
				if(ds != null && ds.getData() != null){
					JSONArray ja = ds.getJAData();
					if(ja.size() > 0){
						JSONObject jb = ja.getJSONObject(0);
						String re = String.valueOf( jb.get("APPLY_DATA") );
						re = getResourceStr(re);
						JSONArray materialArray = JSONArray.fromObject(re);
						JSONArray materialArrayResu = new JSONArray();
						for(int i=0; i<materialArray.size(); i++){
							jb = materialArray.getJSONObject(i);
							jb.put("DOCUMENT_ID", jb.get("resourceCode"));
							jb.put("DOCUMENT_NAME", jb.get("fileName"));
							jb.put("FILE_NAME", jb.get("--"));
							jb.put("FILE_PATH", jb.get("filePath"));
							jb.put("OPERATOR_NAME", jb.get("--"));
							jb.put("SUBJECT", jb.get("subject"));
							materialArrayResu.add(jb);
						}
						jb = new JSONObject();
						jb.put("metail", materialArrayResu);
						ds.setData(jb.toString().getBytes(CharsetUtil.UTF_8));
					}
				}
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				_log.error(e.getMessage());
			}
		}
		Calendar c = Calendar.getInstance();
		c.getTimeInMillis();
		return ds;
	}
	
	/**
	 * 截取resource字符串
	 * @param applydata
	 * @return
	 */
	public String getResourceStr(String applydata){
		String result = "";
		if(applydata != null){
			int i = applydata.indexOf("[");
			int j = applydata.indexOf("]");
			result = applydata.substring(i, j+1);
		}
		return result;
	}
	
	//更新business_index，通过审批流水号类似的编码来更新
	public DataSet updatebaseinfo(ParameterSet pSet){
		DataSet ds = new DataSet();
		String sblsh = (String)pSet.get("sblsh");//
		HttpClientUtil client = new HttpClientUtil();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getAllBusinessInfo?receiveNumber="+sblsh);//;
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try{
			Object strItemList= client.getResult(url,"");
			JSONArray info = JSONObject.fromObject(strItemList).getJSONArray("COURSELIST");		
			//int len = info.size();
			String getPreSql = " select * from business_index where sblsh='"+sblsh+"'";
			this.executeDataset(getPreSql);
			//DataSet PreDs = 
			/*String preContent = PreDs.getRecord(0).getString("PRECONTENT");
			if(!preContent.equals("")){
				if(len==0){
					//info.add(JSONObject.fromObject(preContent));
				}else{
					//info.add(0, JSONObject.fromObject(preContent));
				}
			}	*/		
			String sql = "update business_index t set t.content = ? where t.sblsh = ?";
			DbHelper.update(sql, new Object[]{
					info.toString(),					
					sblsh
					}, conn);
		    conn.commit();
	    }catch(Exception e){
	    	e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}	
	    }finally{
	    	DBSource.closeConnection(conn);
	    }	
		return ds;
	}
	
	//办件公示大厅滚动展示
	public DataSet showPage(ParameterSet pSet){
		DataSet ds = new DataSet();
		String count = pSet.getParameter("count").toString();
		String region = pSet.getParameter("region").toString();
		HttpClientUtil client = new HttpClientUtil();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/getDisplayList?count="+count+"&region="+region);
		try{
			Object strItemList= client.getResult(url,"");
			ds.setRawData(strItemList);	
			
	    }catch(Exception e){
	    	e.printStackTrace();
			
	    }	
		return ds;
	}
	
}
