package km.app.icity.onlineapply;

import java.sql.Connection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;
import core.util.HttpClientUtil;

public class ApplyCloudDao extends BaseJdbcDao{
	private static final String handlType = "01"; //办件提交方式，01网上，02窗口，网办默认为网上提交
    private static final String appCode = com.icore.util.SecurityConfig.getString("uniteCodeAppCode"); //统一编码服务配置的appcode
    private static final String salt = com.icore.util.SecurityConfig.getString("uniteCodeSalt"); //统一编码服务配置的salt
	private static Log log = LogFactory.getLog(ApplyCloudDao.class);
	private ApplyCloudDao() {
		this.setDataSourceName("icityDataSource");
	}
	
	public static ApplyCloudDao getInstance() {
		return (ApplyCloudDao)DaoFactory.getDao(ApplyCloudDao.class.getName());
	}
	private JSONObject changeData(JSONObject data){
		JSONObject m_data = data;
		m_data.put("FormId", data.getString("formId"));
		m_data.put("DataId", data.getString("dataId"));
					  
	    m_data.put("ObjectType", data.getString("objectType"));
	    m_data.put("powerType", data.getString("powerType"));
	    m_data.put("ItemId", data.getString("itemId"));
	    m_data.put("ItemCode", data.getString("itemCode"));
	    m_data.put("ItemName", data.getString("itemName"));
	    m_data.put("OrgCode", data.getString("orgCode"));
	    m_data.put("OrgName", data.getString("orgName"));
	    m_data.put("RegionCode", data.getString("regionCode"));
	    m_data.put("RegionName", data.getString("regionName"));
	    m_data.put("ReceiveNum", data.getString("receiveNum"));
	    m_data.put("InnerCode", data.getString("InnerCode").contains("null")?"":data.getString("InnerCode"));//编办规定事项编码（用于数据交换）（必填）
	    m_data.put("IsPrejudication","11");
	    m_data.put("State", data.getString("state"));
	    return m_data;
	}
	public JSONObject submitData(JSONObject data) throws Exception {	
		data = changeData(data);
		String o_sblsh = data.getString("receiveNum");//暂存或者再次提交时，有值
		String state=data.getString("state");		
		String sblsh="",cxmm="";
		String objectType = data.getString("objectType");
		String formId = data.getString("formId");
		String dataId = data.getString("dataId");
		String ex_dataId = data.containsKey("ex_dataId")?data.getString("ex_dataId"):"";		
		
		String sql="";
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		conn.setAutoCommit(false);
		try{
			if("sp".equals(state)||"ql".equals(state)){
				//首次提交先生成申办流水号+查询密码
				JSONObject codeInfo = getCodeInfo(data);
				String status = "00";
				if("200".equals(codeInfo.getString("state"))){
					status = "11";
					sblsh = codeInfo.getString("receiveNum");
					cxmm = codeInfo.getString("password");//查询密码
					data.put("retstate", "1");							
				}else{//提交失败 设为暂存
					sblsh = Tools.getUUID32();
					//cxmm = codeInfo.getString("password");//查询密码
					data.put("retstate", "0");
				}		
				data.put("receiveNum", sblsh);
				data.put("ReceiveNum", sblsh);
				data.put("password", cxmm);
				data.put("PassWord", cxmm);			
				if(!"".equals(o_sblsh)){
					sql="update business_index set sblsh=?,cxmm=?, state='11',SQRZJHM=?,SQRMC=?,LXRXM=?,LXRSJ=?,SBXMMC=?,sbsj=?,signstate=?,CASECODE=? where sblsh=? ";
					DbHelper.update(sql, new Object[]{sblsh,cxmm, data.get("zjhm"),data.get("sqrxm"),data.get("lxrxm"),data.get("lxrphone"),"关于"+data.get("sqrxm")+data.get("itemName")+"的业务",CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_mm_SS),"1",data.get("Case_ID"),o_sblsh},conn);
					sql = " update SUB_FOR_EX_APP_INFORMATION set sblsh=?, OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=?,EX_DATAID=? where sblsh = ?";
					DbHelper.update(sql, new Object[]{sblsh,objectType,data.toString(),formId,dataId,ex_dataId,o_sblsh}, conn);
				}else{
					sql = "insert into business_index (SBLSH,SXBM,SXID,"+
					"SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"+
					"LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"+
					"YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"+
					"SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"+
					"SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"+
					"TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"+
					"BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"+
					"STATE,URL,LIMIT_NUM,ASSORT,signstate,ly,casecode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						DbHelper.update(sql, new Object[]{
								sblsh,//申办流水号类似的编码来确定事项 
								data.get("itemCode"),
								data.get("itemId"),
								data.get("itemName"),
								data.get("ucid"),
								CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),//
								data.get("objectType"),
								data.get("zjhm"),
								data.get("sqrxm"),
								data.get("lxrxm"),
								data.get("lxrphone"),
								"关于"+data.get("sqrxm")+data.get("itemName")+"的业务",
								sblsh,
								"",//BLFS办理方式
								data.get("regionName"),
								data.get("regionCode"),
								null,//YSLSJ业务受理时间
								"",//YSLZTDM预受理状态代码(1审核通过.2不受理.3补交补正材料)
								"",//YSLSBYY预受理失败原因
								null,//SLSJ预受理失败原因
								"",//YWLSH业务流水号
								data.get("orgName"),
								data.get("orgCode"),//收件单位代码
								"",//SLZTDM受理状态代码(1受理，2不受理)
								"",//BSLYY不受理原因
								"",//SLHZH受理回执号
								cxmm,//CXMM查询密码
								null,//SPSJ审批时间
								"",//SPHJDM审批环节代码(1承办，2审核，3批准)
								"",//SPHJMC审批环节名称
								null,//BZGZSJ补正告知时间
								"",//BZGZYY补正告知原因
								"",//BZCLQD补正材料清单
								null,//BZSJ补正时间
								null,//TBCXKSRQ特别程序开始日期
								"",//TBCXQDLY特别程序启动理由或依据
								"",//SQNR申请内容
								null,//TBCXJSRQ特别程序结束日期
								"",//TBCXJG特别程序结果
								null,//BJSJ办结时间
								"",//BJJGDM办结结果代码(0出证办结,1退回办结.2作废办结.3删除办结.4转报办结.5补正超时驳回.6办结)
								"",//BJJGMS办结结果描述
								"",//ZFHTHYY作废或退回原因
								null,//LQSJ领取时间
								state,//REMARK备注信息sp、ql
								status,//STATE业务状态
								"",//URL跳转地址
								"0",//limit_num 补正补齐次数
								data.get("assort"),
								"1",//数据交换标志位
								data.get("ly"),//来源，icity.wechat等
								data.get("Case_ID")
							}, conn);
					
					sql = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,basecontent,FORMID,DATAID,EX_DATAID) values (?,?,?,?,?,?)";
					DbHelper.update(sql, new Object[]{sblsh,objectType,data.toString(),formId,dataId,ex_dataId}, conn);
				}
			}else if("bqbz".equals(state)||"clbqbz".equals(state)){
				//查询查询密码
				sql = "select cxmm,limit_num from business_index where sblsh=?";
				DataSet pwdDs = DbHelper.query(sql, new Object[]{o_sblsh}, conn);
				cxmm=pwdDs.getRecord(0).getString("CXMM");
				String limit_num = pwdDs.getRecord(0).getString("LIMIT_NUM");
				limit_num = (Integer.parseInt(limit_num)+1)+"";
				data.put("password", cxmm);
				data.put("PassWord", cxmm);		
				//可进行多次补齐补正,或者驳回后的提交
				 sql= "update business_index set BZSJ=?,state=?,SQRZJHM=?,SQRMC=?,LXRXM=?,LXRSJ=?,SBXMMC=?,LIMIT_NUM=?,signstate=?,pushstate=?,casecode=? where sblsh=?";
				DbHelper.update(sql, new Object[]{
						CommonUtils_api.getInstance().parseDateToTimeStamp(new Date(),CommonUtils_api.YYYY_MM_DD_HH_MM_SS_SSS),"bqbz".equals(state)?"11":"01",data.get("zjhm"),data.get("sqrxm"),data.get("lxrxm"),data.get("lxrphone"),"关于"+data.get("sqrxm")+data.get("itemName")+"的业务",limit_num,"1","3",data.get("Case_ID"),o_sblsh }, conn);
				sql = " update SUB_FOR_EX_APP_INFORMATION set OBJECTTYPE = ?,basecontent = ?,FORMID=?,DATAID=?,EX_DATAID=? where sblsh = ?";
				DbHelper.update(sql, new Object[]{objectType,data.toString(),formId,dataId,ex_dataId,o_sblsh}, conn);
			}
		    conn.commit();
		    return data;
	    }catch(Exception e){
	    	log.error(e.getMessage());
	    	conn.rollback();
	    	throw e;
	    }finally{
	    	DBSource.closeConnection(conn);
	    }		
	}
	public JSONObject getCodeInfo(JSONObject data) {
		JSONObject codeInfo;
		String itemId=data.getString("ItemId");
		HttpClientUtil client = new HttpClientUtil();	
		String url = HttpUtil.formatUrl(SecurityConfig.getString("cloud_accept")+"/Service/createReceiveNum?itemId="+itemId+"&applyFrom="+handlType);
		String returnstr = client.getResult(url,"");
		codeInfo = JSONObject.fromObject(returnstr);
		System.out.println(codeInfo);
		codeInfo.put("unitecode", codeInfo.getString("receiveNum"));
		return codeInfo;
	}
}
