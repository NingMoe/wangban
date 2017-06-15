package fz.app.icity.project;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.uc.UserDao;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

public class ProjectIndexDao extends BaseJdbcDao {
	protected static Log _log = LogFactory.getLog(ProjectIndexDao.class);
	protected static String icityDataSource = "icityDataSource";

	public ProjectIndexDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static ProjectIndexDao getInstance() {
		return (ProjectIndexDao)DaoFactory.getDao(ProjectIndexDao.class.getName());
	}
	/**
	 * //个人
		if("11".equals(type)) {
			userInfo.accumulate("TYPE", uInfo.getString("TYPE"));//类型
			userInfo.accumulate("NAME", uInfo.getString("NAME"));//姓名
			userInfo.accumulate("CARD_TYPE", uInfo.getString("CARD_TYPE"));//证件类型
			userInfo.accumulate("CARD_NO", uInfo.getString("CARD_NO"));//证件编号
			userInfo.accumulate("PHONE", uInfo.getString("PHONE"));//联系电话
			userInfo.accumulate("ADDRESS", uInfo.getString("ADDRESS"));//联系地址
			userInfo.accumulate("EMAIL", uInfo.getString("EMAIL"));//邮箱
		}else if("21".equals(type)) {//单位
			userInfo.accumulate("TYPE", uInfo.getString("TYPE"));//类型
			userInfo.accumulate("ORG_NAME", uInfo.getString("ORG_NAME"));//机构名称
			userInfo.accumulate("ORG_NO", uInfo.getString("ORG_NO"));//机构代码
			userInfo.accumulate("ORG_BOSS_TYPE", uInfo.getString("ORG_BOSS_TYPE"));//法人证件类型
			userInfo.accumulate("ORG_BOSS_NO", uInfo.getString("ORG_BOSS_NO"));//法人证件号码
			userInfo.accumulate("ORG_BOSS_NAME", uInfo.getString("ORG_BOSS_NAME"));//法人姓名
			
			userInfo.accumulate("NAME", uInfo.getString("NAME"));//姓名
			userInfo.accumulate("CARD_TYPE", uInfo.getString("CARD_TYPE"));//证件类型
			userInfo.accumulate("CARD_NO", uInfo.getString("CARD_NO"));//证件编号
			userInfo.accumulate("ADDRESS", uInfo.getString("ADDRESS"));//联系地址
		}
	 * @param uid
	 * @return
	 */
	public JSONObject getUserInfo(String uid){
		JSONObject userInfo = new JSONObject();
		DataSet dSet = UserDao.getInstance().getUserById(uid);
		if(dSet.getTotal() > 0) {
			userInfo = dSet.getRecord(0);	
		}
		return userInfo;
	}
	/**
	 * 企业个人中心手动添加证照信息到 表UC_LICENSE
	 * @param pSet（）
	 * ID	VARCHAR2(100)	Y			ID
		CARD_ID	VARCHAR2(100)	Y			用户证件号码 例 ：个人为身份证 企业为组织机构代码
		DOCID	VARCHAR2(20)	Y			网盘返回的文档地址
		TYPE	VARCHAR2(2)	Y	1		默认为1，和正常上传的资料一样，从电子证照系统获取的信息提交到审批时type为3
		LICENSENAME	VARCHAR2(100)	Y			证照名称
		LICENSECODE	VARCHAR2(100)	Y			证照编码
		REMARK	VARCHAR2(100)	Y			其他
		FILENAME
	 * @return
	 */
	public DataSet saveLicense(ParameterSet pSet){
		JSONObject userInfo = getUserInfo(pSet.get("uid").toString());
		DataSet ds = new  DataSet();
		String getsql = "select 1 from UC_LICENSE t where t.licensecode=? and t.card_id=?";
		String insertsql = "insert into UC_LICENSE (ID,CARD_ID,DOCID,TYPE,LICENSENAME,LICENSECODE,FILENAME,REMARK,CTIME,state,LICENSEFILENUMBER,LICENSETYPE,REASON) values (?,?,?,?,?,?,?,?,sysdate,'0','','','')";
		String updatesql = "update UC_LICENSE set DOCID=?,LICENSENAME=?,FILENAME=?,CTIME=sysdate where LICENSECODE=?";
		String delsql = "delete from UC_LICENSE  where ID=?";

		try{
			DataSet _ds = this.executeDataset(getsql, new Object[]{pSet.get("licensecode"),
					"11".endsWith(userInfo.getString("TYPE"))?userInfo.getString("CARD_NO"):userInfo.getString("ORG_NO")
					}, this.getDataSourceName());
			if(_ds.getTotal()>0){
				int i=this.executeUpdate(updatesql, new Object[]{
						pSet.get("docid"),
						pSet.get("licensename"),
						pSet.get("filename"),
						pSet.get("licensecode")
				}, this.getDataSourceName());
				if (i == 0){
					ds.setState(StateType.FAILT);
					ds.setMessage("数据库操作失败！");
				} else {
					ds.setState(StateType.SUCCESS);
				}
			}else{			
				String id = Tools.getUUID32();
				int i = this.executeUpdate(insertsql, new Object[] {
						id,
						"11".endsWith(userInfo.getString("TYPE"))?userInfo.getString("CARD_NO"):userInfo.getString("ORG_NO"),
						pSet.get("docid"),
						"1",
						pSet.get("licensename"),
						pSet.get("licensecode"),
						pSet.get("filename"),
						""					
				}, this.getDataSourceName());
				if (i == 0){
					ds.setState(StateType.FAILT);
					ds.setMessage("数据库操作失败！");
				} else {
					ds.setState(StateType.SUCCESS);
					//postdata:{"type":"1新增,2更新,3删除"，id = uuid holder=身份证/组织机构代码     licensename= 证照名称    licencecode=证照编码  filepath=网盘docid}
					JSONObject data = new JSONObject();
					data.put("type", "1");
					data.put("holder","11".endsWith(userInfo.getString("TYPE"))?userInfo.getString("CARD_NO"):userInfo.getString("ORG_NO"));
					data.put("id", id);
					data.put("licencename", pSet.get("licensename"));
					data.put("licencecode", pSet.get("licensecode"));
					data.put("filepath", pSet.get("docid"));
					pSet.put("data", data);
					try{
					   additLicense(pSet);
					}catch(Exception ex){
						ex.printStackTrace();
						this.executeUpdate(delsql, new Object[] {id}, this.getDataSourceName());
						ds.setState(StateType.FAILT);
						ds.setMessage("提交审批失败！");
					}					
				}		
			}				
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("添加失败！");
		}	
		return ds;
	}
	/**
	 * get UC_LICENSE
	 * @param pSet（）
	 * @return
	 */
	public DataSet getLicense(ParameterSet pSet){
		JSONObject userInfo = getUserInfo(pSet.get("uid").toString());
		DataSet ds = new  DataSet();
		String getsql = "select * from UC_LICENSE t where t.card_id=?";
		String orderby = (String)pSet.get("order");
		if(StringUtils.isNotEmpty(orderby)){
			getsql+=" order by t.CTIME "+orderby;
		}
		try{
			ds = this.executeDataset(getsql, new Object[]{
					"11".endsWith(userInfo.getString("TYPE"))?userInfo.getString("CARD_NO"):userInfo.getString("ORG_NO")
					}, this.getDataSourceName());		
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}	
		return ds;
	}
	/**
	 * 编辑和删除
	 * @param pSet
	 * type=edit   del
	 * @return
	 */
	public DataSet editLicense(ParameterSet pSet){
		DataSet ds = new  DataSet();
		String licensecode = (String)pSet.get("licensecode");
		String type = (String)pSet.get("type");
		String sql = "select * from UC_LICENSE t where t.licensecode=?";		
		try{
			if("del".equals(type)){
				sql = "delete from uc_license t where t.licensecode=?";
				int i = this.executeUpdate(sql, new Object[]{
						licensecode
				}, this.getDataSourceName());
				if(i>0){
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
				}
			}else{
				ds = this.executeDataset(sql, new Object[]{
						licensecode
						}, this.getDataSourceName());	
			}				
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		}	
		return ds;
	}
	/**
	 * 调审批接口审核证照 福州自己的接口
	 * @param pset
	 * postdata:{"type":"1新增,2更新,3删除"，id = uuid holder=身份证/组织机构代码     licencename= 证照名称    licencecode=证照编码  filepath=网盘docid}
	 * @return
	 */
	public void additLicense(ParameterSet pset){
		String url = HttpUtil.formatUrl(SecurityConfig.getString("approval_url") + "/licencevalidate");
		Map<String, String> map=new HashMap<String, String>();
		Object data = pset.get("data");
		map.put("postdata", data.toString());
		try {
			RestUtil.postData(url, map);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
