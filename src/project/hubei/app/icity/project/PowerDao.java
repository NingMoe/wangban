package hubei.app.icity.project;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class PowerDao extends BaseJdbcDao {
	private static Log _log = LogFactory.getLog(PowerDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String sxglDataSource = "sxglDataSource";
	protected static String formDataSource = "formDataSource";

	public static PowerDao getInstance() {
		return DaoFactory.getDao(PowerDao.class.getName());
	}

	public PowerDao() {
		this.setDataSourceName(sxglDataSource);
	}
	
	
	public DataSet getPowerItem(ParameterSet pSet) {
		DataSet dsFolder = new DataSet();
		DataSet dsFolderNum = new DataSet();
		int start = pSet.getPageStart()-1;
		int limit = pSet.getPageLimit();
		String type = pSet.getParameter("type").toString();
		String orgCode = pSet.getParameter("orgCode").toString();
		String wd = pSet.getParameter("wd").toString();
		start = start*limit;
		//查询目录
		try {
			String sql="select id,code,name,agent_name,type from project_folder f where f.status='5' ";
            if(type!=null && !"".equals(type)){
				sql+=" and type='"+type+"'";
			}
            if(orgCode!=null && !"".equals(orgCode)){
				sql+=" and org_code='"+orgCode+"'";
			}
            if(wd!=null && !"".equals(wd)){
            	//按名称模糊查询时，查询事项而不是目录
            	sql = "select id,code,name,agent_name,type from project_item f where f.status='5'  and name like '%"+wd+"%' ";
            }
            sql+=" order by agent_code ";
			dsFolder = this.executeDataset(sql, start, limit,null);
			//遍历dsFolder，利用code值查询目录下事项个数。
			JSONArray ja = dsFolder.getJAData();
			JSONArray jsa = new JSONArray();
			//循环取num:目录下事项个数并,放入jsa中
			String typeSql = "";
			String orgSql = "";
			if(type!=null && !"".equals(type)){
				typeSql+=" and type='"+type+"'";
			}
            if(orgCode!=null && !"".equals(orgCode)){
            	orgSql+=" and org_code='"+orgCode+"'";
			}
			for(int i=0;i<ja.size();i++){
				String code = ((JSONObject)ja.get(i)).get("CODE").toString();
				String sqlNum = "select count(1) as num from project_item where status='5' and folder_code='"+code+"' "+typeSql+orgSql;
				DataSet ds = this.executeDataset(sqlNum,null,sxglDataSource);
				String child_num = ((JSONObject)ds.getJAData().get(0)).get("NUM").toString();
				Map<String,Object> map = ja.getJSONObject(i);
				map.put("num", child_num);
				jsa.add(i,map);
			}
			dsFolderNum.setData(Tools.stringToBytes(jsa.toString()));
			dsFolderNum.setTotal(dsFolder.getTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dsFolderNum;
	}

	public DataSet getPowerItemChild(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = "select i.id,i.code,i.name,i.agent_name,f.type,f.name as folderName from project_item i,project_folder f where i.folder_code=f.code and i.status='5' and i.folder_code='"+code+"' order by i.code ";
		return this.executeDataset(sql);
	}
	
	//只有一个子项时，根据父项code查询子项code,多数情况下父项code==子项code
	public DataSet getCodeByFolder(String folderCode) {
		String sql = "select code from project_item t where folder_code='"+folderCode+"'";
		DataSet ds = this.executeDataset(sql,null,sxglDataSource);
		String code = ((JSONObject)ds.getJAData().get(0)).get("CODE").toString();
		return ds;
	}
	
	public DataSet getDeptList(ParameterSet pSet) {
		String sql = "select agent_name,agent_code from project_item t group by agent_name,agent_code order by agent_code";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_zqyxlc(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = "select name from base_process t where code in (select process_code from project_item_of_process where item_code='"+code+"' "
				+ "and version=(select max(version) from project_item_of_process where item_code='"+code+"')) order by sort_order";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_zqyj(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = "select t.code,t.name,t.content,l.law_name,la.law_type,la.law_type_code from base_law_term t,project_item_of_law l,base_law la where t.code=l.term_code and l.law_code=la.code and l.item_code='"+code+"'";
               sql += "and t.status='1' and l.version=(select max(version) from project_item_of_law where item_code='"+code+"') order by la.law_type_code "; //此处事项系统未做版本控制，为保持数据一致不过滤版本号
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_xkfwjtj(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = " select t.code,t.type,t.name,c.sort_order from project_condition t,project_item_of_condition c where c.condition_code=t.code and ";
        sql += "c.item_code='"+code+"' and c.version='1' and t.status='1'";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_sqcl(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = "select * from base_material t where code in (select material_code from project_item_of_material t where item_code='"+code+"' ";
               sql += "and version=(select max(version) from project_item_of_material where item_code='"+code+"') ) and status='1' order by name";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_fdcntbcxqx(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = "select id,law_time,agree_time,IS_PROCEDURE,PROCEDURE_NAME,PROCEDURE_TIME,IS_CHARGE,ASSORT,KIND_NAME,POWER_PROCESS,SCOPE from project_item t where code='"+code+"' ";
		       sql += " and version='1' ";
//               sql += "and version=(select max(version) from project_item_of_condition where item_code='"+code+"') ) and status='1' ";//此处事项系统未做版本控制，为保持数据一致不过滤版本号
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_sfyjjbz(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = " select code,name,basis,standard from project_charge_item t where code=(select charge_code from project_item_of_charge where item_code='"+code+"')";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_form(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String powtype = pSet.getParameter("powtype").toString();
		String sqlFormId = "select data_id from project_item_form_data t where item_code='"+code+"'";
		DataSet ds = this.executeDetail(sqlFormId, null, sxglDataSource);
		JSONArray ja = ds.getJAData();
		String MAIN_TBL_PK = ((JSONObject)ja.get(0)).get("DATA_ID").toString();
		String tableName = "quanzeqingdan";
		if(powtype.equals("CF")){
			tableName = "XingZhengChuFaQuanZeQingDan";
		}else if(powtype.equals("QZ")){
			tableName = "QiangZhiQuanZeQingDan";
		}else if(powtype.equals("QR")){
			tableName = "QueRenQuanZeQingDan";
		}else if(powtype.equals("JC")){
			tableName = "JianChaQuanZeQingDan";
		}else if(powtype.equals("ZS")){
			tableName = "ZhengShouQuanZeQingDan";
		}else if(powtype.equals("JL")){
			tableName = "JiangLiQuanZeQingDan";
		}else if(powtype.equals("JF")){
			tableName = "GeiFuQuanZeQingDan";
		}else if(powtype.equals("CJ")){
			tableName = "CaiJueQuanZeQingDan";
		}else if(powtype.equals("QT")){
			tableName = "QiTaQuanZeQingDan";
		}
		String sql = " select * from "+tableName+" where MAIN_TBL_PK='"+MAIN_TBL_PK+"' ";
		return this.executeDataset(sql,null,formDataSource);
	}
	
	public DataSet getItemExt_map(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = " select url from base_map t where code in (select map_code from project_item_of_map t where ITEM_CODE='"+code+"' and version='1')";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
	public DataSet getItemExt_xhlhzyclqbz(ParameterSet pSet) {
		String code = pSet.getParameter("code").toString();
		String sql = "select s.name,s.punish_info from base_sanction s where conduct_code =(select sanction_code from project_item_of_sanction where item_code='"+code+"' "
				+ "and version=(select max(version) from base_sanction where item_code='"+code+"'))";
		return this.executeDataset(sql,null,sxglDataSource);
	}
	
}