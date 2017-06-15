/**   
* @Title: KunMing.java 
* @Package api 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2016-8-24 下午5:44:58 
* @version V1.0   
*/
package api;

import java.sql.Connection;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.icore.http.util.HttpUtil;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.rest.RestType;
import com.inspur.util.SecurityConfig;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.HttpClientUtil;

/** 
 * @ClassName: KunMing 
 * @Description: 描述 
 * @author kongws
 * @date 2016-8-24 下午5:44:58 
 *  
 */
@RestType(name = "api.KunMing", descript = "昆明个性化接口")
public class KunMing{
	/**
	 * 自建审批系统进行办件撤回接口（如：住建局,生成新办件）
	* @Title: sendCheHui 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param pSet
	* @param @return    设定文件 
	* @return DataSet    返回类型 
	* @throws
	 */
	public DataSet sendCheHui(ParameterSet pSet) {
		JSONObject json=new JSONObject();
		DataSet ds = new DataSet();
		//抓取要重办的件申办流水号,以及原因
		String sblsh = (String) pSet.get("sblsh");
		String reason = (String) pSet.get("reason");
		
		if(sblsh==null||"".equals(sblsh)){
			if(reason==null ||"".equals(reason)){
				json.put("state", "300");
				json.put("error", "参数错误!缺少参数sblsh和reason");
			}else{
				json.put("state", "300");
				json.put("error", "参数错误!缺少参数sblsh");
			}
			
		}else if(reason==null ||"".equals(reason)){
			json.put("state", "300");
			json.put("error", "参数错误!缺少参数reason");
		}else{  //如果传参正确,进行处理.

			Connection conn = DbHelper.getConnection("icityDataSource");
			try{
				String sqlChannel = "select * from business_index where sblsh=? ";
				DataSet ds1 = DbHelper.query(sqlChannel, new Object[]{sblsh}, conn);
				if(ds1.getTotal()>0){ //如果存在这个件
					//获取新的申办流水号
					JSONArray iteminfo = ds1.getJAData();
					String SXID = (String) ((JSONObject) iteminfo.get(0)).get("SXID");	
					String SBLSH = (String) ((JSONObject) iteminfo.get(0)).get("SBLSH");	
					
					HttpClientUtil client = new HttpClientUtil();
					String url =SecurityConfig.getString("approval_url");
					url+="/createReceiveNum?itemId="+SXID+"&applyFrom=01";
					url=HttpUtil.formatUrl(url);
					JSONObject codeInfo = JSONObject.fromObject(client.getResult(url,""));
					String new_sblsh=codeInfo.getString("receiveNum");
					
					//生成新办件到主表中
					String insert="insert into business_index(SBLSH,SXBM,SXID,"+
									"SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"+
									"LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"+
									"YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"+
									"SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"+
									"SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"+
									"TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"+
									"BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"+
									"STATE,URL,LIMIT_NUM,ASSORT) " +

									"select ?,SXBM,SXID,"+
									"SXMC,UCID,SBSJ,SQRLX,SQRZJHM,SQRMC,"+
									"LXRXM,LXRSJ,SBXMMC,SBHZH,SBFS,XZQH,XZQHDM,"+
									"YSLSJ,YSLZTDM,YSLJGMS,SLSJ,YWLSH,SJDW,"+
									"SJDWDM,SLZTDM,BSLYY,SLHZH,CXMM,SPSJ,"+
									"SPHJDM, SPHJMC,BZGZSJ,BZGZYY,BZCLQD,BZSJ,"+
									"TBCXKSRQ,TBCXQDLY,SQNR,TBCXJSRQ,TBCXJG,"+
									"BJSJ,BJJGDM,BJJGMS,ZFHTHYY,LQSJ,REMARK,"+
									"'00',URL,LIMIT_NUM,ASSORT from business_index where sblsh=? ";

					int i=DbHelper.update(insert, new Object[]{new_sblsh,SBLSH}, conn);
					  //生成新信息到SUB_FOR_EX_APP_INFORMATION表中
						String insert2 = " insert into SUB_FOR_EX_APP_INFORMATION (SBLSH,OBJECTTYPE,FORMID,DATAID) SELECT ?,OBJECTTYPE,FORMID,DATAID FROM SUB_FOR_EX_APP_INFORMATION WHERE SBLSH=?";
						int j=DbHelper.update(insert2, new Object[]{new_sblsh,SBLSH}, conn);
						
						//获取SUB_FOR_EX_APP_INFORMATION 表中basecontent的值,修改里面的receiveNumber.
						String selectBaseContent="select basecontent from SUB_FOR_EX_APP_INFORMATION WHERE SBLSH=?";
						DataSet content = DbHelper.query(selectBaseContent, new Object[]{sblsh}, conn);
						JSONArray basecontentArray= content.getJAData();
						String str=basecontentArray.getJSONObject(0).getString("BASECONTENT").replace(SBLSH, new_sblsh);
						
						//把新的basecontent值更新到SUB_FOR_EX_APP_INFORMATION表中.
						String updateBasecontent="update SUB_FOR_EX_APP_INFORMATION set BASECONTENT=? where SBLSH=?";
						DbHelper.update(updateBasecontent, new Object[]{str,new_sblsh}, conn);
						if(i>0 && j>0){
							//更新主表状态为已办结
							String update1="update business_index set state=? where sblsh=?";
							DbHelper.update(update1, new Object[]{"97",SBLSH}, conn);
						}
						json.put("state", "200");
						json.put("error", "退件成功！");
				}else{
					json.put("state", "300");
					json.put("error", "退件失败，为找到此件！");
				}

			}catch(Exception e){
				json.put("state", "300");
				json.put("error", "退件失败，请与管理员联系！");
		    }finally{
		    	DBSource.closeConnection(conn);
		    }	
		}
		ds.setRawData(json.toString());
		return ds;
	}
}