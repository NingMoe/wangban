/**  
 * @Title: ProjectBusinessStatDao.java 
 * @Package icity.project.dao 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-8-20 下午12:33:03 
 * @version V1.0  
 */
package app.icity.project;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import app.icity.ServiceCmd;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.SqlCreator;

import core.util.HttpClientUtil;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

/**
 * @ClassName: ProjectBusinessStatDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenzhiye
 * @date 2013-8-20 下午12:33:03
 */

public class ProjectBusinessStatDao extends BaseJdbcDao {
	private static Logger log = LoggerFactory.getLogger(ProjectBusinessStatDao.class);
	private static String icityDataSource = "icityDataSource";

	private ProjectBusinessStatDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ProjectBusinessStatDao getInstance() {
		return DaoFactory.getDao(ProjectBusinessStatDao.class.getName());
	}

	public DataSet getBusinessProcessById(ParameterSet pSet) {
		DataSet ds;
		String sql = "select b.sblsh,b.sxbm,b.sbsj,b.slsj,b.slztdm,b.bjsj,b.bjjgdm,b.lqsj,b.state,p.name as sxmc,p.dept_name,p.dept_name_short from business_index b inner join power_base_info p on b.sxbm=p.code"; // where
																																																						// b.state>'-1'
		String timerange = (String) pSet.remove("timerange");
		if ("on".equals(timerange)) {
			// 如果对事项进行排序，对申办时间进行过滤
			Calendar calendar = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			// 结束时间
			String end = df.format(calendar.getTime()) + " 23:59:00";
			pSet.setParameter("b.sbsj@<=@Date", end);
			// 开始时间
			calendar.add(Calendar.MONTH, -1);
			String start = df.format(calendar.getTime()) + " 00:00:00";
			pSet.setParameter("b.sbsj@>=@Date", start);
		}
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null, this.getDataSourceName());
		} else {
			ds = this.executeDataset(sql, start, limit, null, this.getDataSourceName());
		}
		return ds;
	}

	public DataSet getBusinessStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String webRegion = SecurityConfig.getString("WebRegion");
		try {
			// 只获取当前区划的统计信息
			// String sql = "select
			// stattime,ljsj,ljbj,sysj,sybj,bysj,bybj,zrsj,zrbj,jrsj,jrbj from
			// business_stat where stattime in (select max(stattime) from
			// business_stat where xzqhdm='"+webRegion+"')";
			// ds = this.executeDataset(sql, null, icityDataSource);
			String sql = "select stattime,content from project_busyinfo t where t.stattime = (select max(stattime) from project_busyinfo where xzqhdm = ? and type = ?) and t.xzqhdm = ? and t.type = ?";
			ds = this.executeDataset(sql, new Object[] { webRegion, "getBusinessStat", webRegion, "getBusinessStat" },
					icityDataSource);
			if (ds != null && ds.getTotal() == 0) {
				// 当前没有任何统计信息，就手动执行一次统计，再获取统计结果
				ProjectBusinessStatJob statJob = new ProjectBusinessStatJob();
				statJob.execute(pSet);
				ds = this.executeDataset(sql,
						new Object[] { webRegion, "getBusinessStat", webRegion, "getBusinessStat" }, icityDataSource);
			}
			if (ds != null && ds.getTotal() > 0) {
				JSONObject jo = ds.getRecord(0);
				JSONObject jContent = jo.getJSONObject("CONTENT");
				jo.remove("CONTENT");
				jo.putAll(jContent);
				ds.setRawData(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusinessSJStat(ParameterSet pSet) {
		DataSet ds;
		String sql = "select sblsh,sxbm,sbsj from business_index";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		ds = this.executeDataset(sql);
		return ds;
	}

	public DataSet getBusinessBJStat(ParameterSet pSet) {
		DataSet ds;
		String sql = "select sblsh,sxbm,bjsj from business_index where bjsj is not null";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		ds = this.executeDataset(sql);
		return ds;
	}

	public DataSet getBusyInfoHotStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String webRegion = SecurityConfig.getString("WebRegion");
		try {
			String sql = " select  stattime,content from project_busyinfo t where t.stattime = (select max(stattime) from project_busyinfo where xzqhdm = ? and type = ?) and t.xzqhdm = ? and t.type = ? ";
			ds = this.executeDataset(sql,
					new Object[] { webRegion, "getBusyInfoHotStat", webRegion, "getBusyInfoHotStat" }, icityDataSource);

			if (ds != null && ds.getTotal() == 0) {
				// 当前没有任何统计信息，就手动执行一次统计，再获取统计结果
				ProjectBusyInfoHotStatJob statJob = new ProjectBusyInfoHotStatJob();
				statJob.execute(pSet);
				ds = this.executeDataset(sql,
						new Object[] { webRegion, "getBusyInfoHotStat", webRegion, "getBusyInfoHotStat" },
						icityDataSource);
			}

			if (ds != null && ds.getTotal() > 0) {
				JSONObject jo = ds.getRecord(0);
				JSONArray jContent = jo.getJSONArray("CONTENT");
				JSONArray __jContent = new JSONArray();
				int limit = pSet.getPageLimit();
				int len = jContent.size();
				if (limit < len) {
					for (int i = 0; i < pSet.getPageLimit(); i++) {
						__jContent.add(jContent.get(i));
					}
				} else {
					__jContent = jContent;
				}
				jo.remove("CONTENT");
				ds.setTotal(__jContent.size());
				// ds.setData(jContent.toString().getBytes());
				ds.setRawData(__jContent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusinessListTab(ParameterSet pSet) {

		DataSet ds = new DataSet();
		String webRegion = SecurityConfig.getString("WebRegion");
		try {
			String sql = "select stattime,content from project_busyinfo t where t.stattime = (select max(stattime) from project_busyinfo where xzqhdm = ? and type = ?) and t.xzqhdm = ? and t.type = ?";
			ds = this.executeDataset(sql,
					new Object[] { webRegion, "getBusinessListTab", webRegion, "getBusinessListTab" }, icityDataSource);
			if (ds != null && ds.getTotal() == 0) {
				// 当前没有任何统计信息，就手动执行一次统计，再获取统计结果
				ProjectBusyInfoStateListJob statJob = new ProjectBusyInfoStateListJob();
				statJob.execute(pSet);
				ds = this.executeDataset(sql,
						new Object[] { webRegion, "getBusinessListTab", webRegion, "getBusinessListTab" },
						icityDataSource);
			}

			if (ds != null && ds.getTotal() > 0) {
				JSONObject jo = ds.getRecord(0);
				JSONArray jContent = jo.getJSONArray("CONTENT");
				jo.remove("CONTENT");
				ds.setTotal(jContent.size());
				// ds.setData(jContent.toString().getBytes());
				ds.setRawData(jContent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusyInfoNoticeStat(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String endPoint = SecurityConfig.getString("ZB_NOTICE") + "/Interface/GongGao.asmx?wsdl";
			String soap = "http://tempuri.org/";
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(endPoint));
			call.setOperationName(new QName(soap, "getResult"));

			// 该方法需要的参数
			call.addParameter(new QName(soap, "count"), // 设置要传递的参数
					org.apache.axis.encoding.XMLType.XSD_INT, javax.xml.rpc.ParameterMode.IN); // 方法的返回值类型
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
			call.setUseSOAPAction(true);
			call.setSOAPActionURI(soap + "getResult");
			int limit = pSet.getPageLimit();
			String i = limit + "";
			String rt = (String) call.invoke(new Object[] { i });
			JSONObject result = net.sf.json.JSONObject.fromObject(rt);
			JSONArray jContent = result.getJSONArray("gonggao");
			ds.setRawData(jContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getBusinessList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")
					+ "/getDisplayList?count=100&region=" + SecurityConfig.getString("WebRegion"));
			HttpClientUtil client = new HttpClientUtil();
			JSONObject json = JSONObject.fromObject(client.getResult(url, ""));
			JSONArray data = json.getJSONArray("data");
			if ("zs".equals(SecurityConfig.getString("AppId"))) {
				String url_ql = HttpUtil.formatUrl(SecurityConfig.getString("PowerOperation_url")
						+ "/getDisplayList?count=100&region=" + SecurityConfig.getString("WebRegion"));
				JSONObject json_ql = JSONObject.fromObject(client.getResult(url_ql, ""));

				int len = json_ql.size();
				for (int i = 0; i < len; i++) {
					data.add(json_ql.getJSONArray("data").getJSONObject(i));
				}
			}
			ds.setRawData(data);
		} catch (Exception e) {
			log.error("获取办件公示信息失败！");
			ds.setState(StateType.FAILT);
			ds.setMessage("获取办件公示信息失败！");
		}
		return ds;
	}
	
	public DataSet getBusinessUsed4Person(ParameterSet pSet) {
		DataSet ds;
		JSONArray array = new JSONArray();
		String ucid = (String)pSet.remove("uid");
		String sql = "select * from (select max(sxmc) sxmc,max(sjdw) sjdw,max(sxid) id,max(sxbm) code,count(*) count from business_index where ucid=? group by sxmc order by count desc) where rownum<=3 ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		ds = this.executeDataset(sql,new Object[] { ucid },	icityDataSource);
		
		if (ds != null) {
			for (int i = 0; i < ds.getTotal(); i++) {
				String url = SecurityConfig.getString("ItemSynchronizationUrl")+"/getItemInfoByItemCode?itemCode="+ds.getRecord(i).getString("CODE");
				
				url=HttpUtil.formatUrl(url);
				HttpClientUtil client = new HttpClientUtil();
				JSONObject item = JSONObject.fromObject(client.getResult(url,""));
				JSONArray itemInfo = item.getJSONArray("ItemInfo");
				JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
				String itemId = itemBasicInfo.getString("ID");
				
				JSONObject record = ds.getRecord(i);
				record.put("ID", itemId);
				array.add(record);
			}
			
			ds.setRawData(array);
		}
		
		return ds;
	}
	/**
	 * @Description: 西宁市办件统计
	 * @return DataSet 返回类型
	 * @since 2013-8-22
	 */
	public DataSet getBusinessStatXns(ParameterSet pSet) {
		DataSet m_ds = new DataSet();
		String webRegion = SecurityConfig.getString("WebRegion");
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");         
			Calendar calendar = Calendar.getInstance();			
			String kssj = "1900-01-01 00:00:00";
			String jssj = "1900-01-01 00:00:00";
			String options = (String)pSet.get("options");
			String where="?dateType=";
			if("day".equals(options)){
				where+="day";
				String s = df.format(calendar.getTime());
				kssj = s+" 00:00:00";
				jssj = s+" 23:59:59";
			}else if("month".equals(options)){
				where+="month";
				calendar.set(Calendar.DAY_OF_MONTH, 1);         
		        String month_first = df.format(calendar.getTime());	        
		        kssj = month_first+" 00:00:00";				
		        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		        String month_last = df.format(calendar.getTime());          
		        jssj = month_last+" 23:59:59";
			}else if("year".equals(options)){
				where+="year";
				int year = calendar.get(Calendar.YEAR);
		        calendar.clear();
		        calendar.set(Calendar.YEAR, year);
		        String year_first = df.format(calendar.getTime().getTime()); 	        
		        kssj = year_first+" 00:00:00";
		        calendar.roll(Calendar.DAY_OF_YEAR, -1);  
		        String year_last = df.format(calendar.getTime().getTime()); 
		        jssj = year_last+" 23:59:59";
			}
			String where_a=" and t.xzqhdm=? and t.sbsj between to_date(?,'yyyy-MM-dd HH24:mi:ss') and to_date(?,'yyyy-MM-dd HH24:mi:ss') ";
			String where_b=" and s.region_id=? and s.write_date between to_date(?,'yyyy-MM-dd HH24:mi:ss') and to_date(?,'yyyy-MM-dd HH24:mi:ss') ";
			String sql = "select case when a.sjdw is null then b.depart_name else a.sjdw end deptname,"+
			"case when a.sjdwdm is null then b.depart_id else a.sjdwdm end deptid,"+
			"case when a.wssb is null then 0 else a.wssb end wssb,"+
			"case when b.zxs is null then 0 else b.zxs end zxs,"+
			"case when b.tss is null then 0 else b.tss end tss"+
			" from (select t.sjdw,t.sjdwdm,"+
			"sum(case when t.state in ('01','02','11','13','14','16','21','96','97','98','99') then 1 else 0 end) wssb"+
			" from business_index t where t.sjdw is not null and t.sjdwdm is not null " +
			where_a+
			"group by t.sjdwdm,t.sjdw"+
			"  ) a"+
			"  full join "+
			"  ("+
			"  select s.depart_name,s.depart_id,sum(case when s.type='2' then 1 else 0 end) zxs, "+
			"  sum(case when s.type='3' then 1 else 0 end) tss "+
			" from guestbook s where s.depart_name is not null and s.depart_id is not null " +
			where_b+
			" group by s.depart_name,s.depart_id ) b on a.sjdwdm = b.depart_id and a.sjdw = b.depart_name";
			DataSet ds = this.executeDataset(sql,
					new Object[] {webRegion,kssj,jssj,webRegion,kssj,jssj}, icityDataSource);			
			ParameterSet m_pset = new ParameterSet();
		    m_pset.put("region_code", SecurityConfig.getString("WebRegion"));
		    DataSet ds_dept = ServiceCmd.getInstance().getDeptListT(m_pset);
		    JSONArray m_ja = ds_dept.getJOData().getJSONArray("organ");
		    StringBuilder deptList = new StringBuilder();
		    for(int i=0;i<m_ja.size();i++){
		      if("1".equals(m_ja.getJSONObject(i).getString("IS_HALL"))){
		        deptList.append(m_ja.getJSONObject(i).getString("CODE")+",");
		      }
		    }
		    String orgCode=deptList.toString();
		    orgCode = orgCode.substring(0, orgCode.length() - 1);
		    HttpClientUtil client = new HttpClientUtil();
		    String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url")+"/statisticsByOrgQuery")+where+"&orgCode="+orgCode;
		    Object ret= client.getResult(url,"");
		    System.out.print(ret);
		    JSONArray rows = JSONObject.fromObject(ret).getJSONArray("rows");  
		    int total = rows.size();
		    int m_total =  ds.getTotal();
		    JSONArray m_rows=new JSONArray();
		    for(int i=0;i<total;i++){
		      JSONObject  m_col = rows.getJSONObject(i).getJSONObject("cell");
		      m_col.put("TSS", "--");
		      m_col.put("ZXS", "--");
		      m_col.put("WSSB", "--");
		      for(int j=0;j<m_total;j++){
		        if(m_col.getString("orgCode").equals(ds.getJAData().getJSONObject(j).getString("DEPTID"))){
		          m_col.put("TSS", ds.getJAData().getJSONObject(j).getString("TSS"));
		          m_col.put("ZXS", ds.getJAData().getJSONObject(j).getString("ZXS"));
		          m_col.put("WSSB", ds.getJAData().getJSONObject(j).getString("WSSB"));
		        }
		      }
		      m_rows.add(m_col);
		    }
		    m_ds.setTotal(total);
		    m_ds.setRawData(m_rows);
		    m_ds.setState(StateType.SUCCESS);
		  } catch (Exception e) {
		    m_ds.setTotal(0);
		    m_ds.setRawData(new JSONArray());
		    m_ds.setState(StateType.FAILT);
		    m_ds.setMessage("统计失败！");
		    e.printStackTrace();
		  }
		  return m_ds;
	}
}
