package app.icity.project;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;

public class ProjectQueryDao extends BaseJdbcDao {

	private ProjectQueryDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ProjectQueryDao getInstance() {
		return DaoFactory.getDao(ProjectQueryDao.class.getName());
	}

	/**
	 * 按\r\n或\n拆分换行
	 * 
	 * @author XiongZhiwen
	 */
	protected static String replaceLine(String field) {
		StringBuilder ret = new StringBuilder();
		if (StringUtils.isNotEmpty(field)) {
			String[] t = field.split("(\r\n|\n)");
			for (int i = 0; i < t.length; i++) {
				t[i] = t[i].replaceAll("(^\\s+|\\s+$)", "");
				if (t[i].length() > 0) {
					ret.append("<p>" + t[i] + "</p>");
				}
			}
		}
		return ret.toString();
	}
	
	public DataSet getProjectInfo(ParameterSet pset) {
		String sql = "SELECT SBLSH,SXBM,SXMC,SQRZJHM,UCID,to_char(SBSJ,'yyyy-mm-dd') SBSJ, SQRLX, SQRMC, LXRXM,LXRSJ, SBXMMC, SBHZH, XZQHDM,to_char(YSLSJ,'yyyy-mm-dd') YSLSJ, YSLZTDM, YSLSBYY, to_char(SLSJ,'yyyy-mm-dd') SLSJ, YWLSH, SLBMZZJDDM, "
				+ " BMZZJGDM, SLZTDM, BSLYY, SLHZH, CXMM,to_char(SPSJ,'yyyy-mm-dd') SPSJ, SPHJDM, SPHJMC,to_char(BZGZSJ,'yyyy-mm-dd') BZGZSJ, BZGZYY, BZCLQD,to_char(BZSJ,'yyyy-mm-dd') BZSJ,to_char(TBCXKSRQ,'yyyy-mm-dd') TBCXKSRQ, TBCXQDLY, SQNR,to_char(TBCXJSRQ,'yyyy-mm-dd') TBCXJSRQ,"
				+"TBCXJG,to_char(BJSJ,'yyyy-mm-dd') BJSJ, BJJGDM, BJJGMS, ZFHTHYY,to_char(LQSJ,'yyyy-mm-dd') LQSJ, REMARK ,STATE FROM BUSINESS_INDEX B ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}

	}

	public DataSet getProjectInfo_pingyi(ParameterSet pset) {
		String sql = "SELECT SQRZJHM,SBLSH, SXBM, SXMC, UCID, to_char(SBSJ,'yyyy-mm-dd') SBSJ, SQRLX, SQRMC, LXRXM,LXRSJ, SBXMMC, SBHZH, XZQHDM,to_char(YSLSJ,'yyyy-mm-dd') YSLSJ, YSLZTDM, YSLSBYY, to_char(SLSJ,'yyyy-mm-dd') SLSJ, YWLSH, SLBMZZJDDM, "
			+ " BMZZJGDM, SLZTDM, BSLYY, SLHZH, CXMM,to_char(SPSJ,'yyyy-mm-dd') SPSJ, SPHJDM, SPHJMC,to_char(BZGZSJ,'yyyy-mm-dd') BZGZSJ, BZGZYY, BZCLQD,to_char(BZSJ,'yyyy-mm-dd') BZSJ,to_char(TBCXKSRQ,'yyyy-mm-dd') TBCXKSRQ, TBCXQDLY, SQNR, TBCXJSRQ, TBCXJG,"
			+ "to_char(BJSJ,'yyyy-mm-dd') BJSJ, BJJGDM, BJJGMS, ZFHTHYY,to_char(LQSJ,'yyyy-mm-dd') LQSJ, REMARK ,STATE,DEPT_NAME,NAME,DEPTNO FROM BUSINESS_INDEX B,POWER_BASE_INFO WHERE SXBM=CODE ";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		return this.executeDataset(sql, null);
	}

	public DataSet insert(ParameterSet pSet) {
		DataSet ds;
		String sblsh = (String) pSet.getParameter("serial_number");
		String sql = "select * from STAR_LEVEL_EVALUATION t where t.serial_number = '"
				+ sblsh + "'";
		ds = this.executeDataset(sql, null);
		if (ds.getTotal() > 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("每笔业务只能评议一次！");
			return ds;
		}

		sql = "insert into STAR_LEVEL_EVALUATION(ID, SERVICE_CODE, SERVICE_ORG_ID, BSR_NAME, BSR_IDENTITY_NUMBER, SERIAL_NUMBER, STAR_LEVEL, EVALUATE_CONTENT, NOTES, CLIENT_IP, CLIENT_TYPE, CREATOR_ID, CREATOR_DATE) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int i = this.executeUpdate(sql, new Object[] {
				Tools.getUUID32(),
				(String) pSet.getParameter("service_code"),
				(String) pSet.getParameter("service_org_id"),
				(String) pSet.getParameter("bsr_name"),
				(String) pSet.getParameter("bsr_identity_number"),
				(String) pSet.getParameter("serial_number"),
				Integer.parseInt(pSet.getParameter("star_level").toString()),
				(String) pSet.getParameter("evaluate_content"),
				"",
				pSet.getRemoteAddr(),
				(String) pSet.getParameter("client_type"),
				pSet.getParameter("creator_id"),
				CommonUtils.getInstance().parseDateToTimeStamp(new Date(),
						CommonUtils.YYYY_MM_DD_HH_mm_SS) });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}

	public DataSet getPingYiList(ParameterSet pset) {
		String sql = "SELECT ID,SBLSH,SXMC,STAR_LEVEL,EVALUATE_CONTENT,to_char(CREATOR_DATE,'yyyy-mm-dd') CREATOR_DATE FROM STAR_LEVEL_EVALUATION T LEFT JOIN BUSINESS_INDEX TT ON T.SERIAL_NUMBER = TT.SBLSH";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		/*String evaluate_content = obj.getString("EVALUATE_CONTENT");
		if (StringUtils.isNotEmpty(evaluate_content)) {
			evaluate_content = replaceLine(evaluate_content);
			obj.put("evaluate_content", evaluate_content);
		}*/
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		if (start == -1 || limit == -1) {
			return this.executeDataset(sql, null);
		} else {
			return this.executeDataset(sql, start, limit, null);
		}
	}

	
	public DataSet getPYList(ParameterSet pSet,UserInfo user){
		pSet.setParameter("BI.UCID", user.getUid());
		return this.getPY(pSet);
	}
	
	public DataSet getPY(ParameterSet pSet){
		String sql="SELECT SL.ID, SL.SERIAL_NUMBER, SL.EVALUATE_CONTENT, SL.CLIENT_IP, SL.STAR_LEVEL, SL.NOTES, " +
				   "SL.CREATOR_DATE, SL.BSR_NAME, SL.SERVICE_CODE,  PB.NAME  AS PNAME " +
				   "FROM STAR_LEVEL_EVALUATION SL,POWER_BASE_INFO PB,BUSINESS_INDEX BI WHERE SL.SERIAL_NUMBER=BI.SBLSH AND SL.SERVICE_CODE=PB.CODE AND BI.SXBM=PB.CODE ";
		String schCenter = (String)pSet.remove("SEARCH_PNAME");
		Object[] params = null;
		if(StringUtils.isNotBlank(schCenter)){
			schCenter = "%" + schCenter + "%";
			sql += " and (PB.NAME like ?)";
			params = new Object[]{schCenter};
		}
		sql = SqlCreator.getSimpleQuerySql(pSet, sql,this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = null;
		if(start==-1||limit==-1){
			ds = this.executeDataset(sql,params);
		}else{
			ds = this.executeDataset(sql, start, limit,params);
		}
		return  ds;
	}
	
	public DataSet delete(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ids = (String) pSet.getParameter("ids");
			if (StringUtils.isNotEmpty(ids)) {
				String sql = "DELETE FROM STAR_LEVEL_EVALUATION";
				ParameterSet tSet = new ParameterSet();
				tSet.setParameter("id@in", ids);
				sql = SqlCreator.getSimpleQuerySql(tSet, sql, this
						.getDataSource());
				int i = this.executeUpdate(sql);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("数据删除失败！");
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("参数ids的值为空！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet detail(ParameterSet pset) {
		String sql = "SELECT ID,SBLSH,SXMC,STAR_LEVEL,EVALUATE_CONTENT,to_char(CREATOR_DATE,'yyyy-mm-dd') CREATOR_DATE FROM STAR_LEVEL_EVALUATION T ,BUSINESS_INDEX TT WHERE T.SERIAL_NUMBER = TT.SBLSH";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		return this.executeDataset(sql, null);
	} 
	
	public DataSet update(ParameterSet pSet){
		String sql = "UPDATE STAR_LEVEL_EVALUATION SET  EVALUATE_CONTENT=?,CREATOR_DATE=? WHERE ID = ?";
		DataSet ds = new DataSet();
		int i = this.executeUpdate(sql, 
				new Object[]{		
					(String)pSet.getParameter("EVALUATE_CONTENT"),
					CommonUtils.getInstance().parseDateToTimeStamp(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS),
					(String)pSet.getParameter("ID")
					});
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}else{
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
}
