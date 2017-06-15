package plugin;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.icore.plugin.IPlugin;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PushConsultPlugin extends BaseQueryCommand implements IPlugin {

	private Timer _taskTimer;
	//private String WebServiceUrl = "http://zwfw.sd.gov.cn/sdzw/services/zxts?wsdl"; //WebServices地址
	private String WebServiceUrl =SecurityConfig.getString("WebServiceUrl");
		
	
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
				_taskTimer = new Timer();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
					//推送数据
					pushData();
					}
				};
				long tt = 1000 * 5;// 每5秒进行操作
				_taskTimer.schedule(task, tt, tt);
				return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		_taskTimer.cancel();
		_taskTimer = null;
		return true;
	}
	@SuppressWarnings("deprecation")
	private void pushData() {
		Connection conn = DbHelper.getConnection("icityDataSource");
		try {
			conn.setAutoCommit(false);
			String sql = "select u.account ACCOUNT,g.USERNAME USERNAME,g.SXBM SXBM,"
					+ "g.DEPART_ID DEPART_ID,to_char(g.WRITE_DATE,'yyyy-mm-dd hh24:mi:ss') WRITE_DATE,"
					+ "g.SXMC SXMC,to_char(g.DEAL_DATE,'yyyy-mm-dd hh24:mi:ss') DEAL_DATE,"
					+ "g.BUSI_ID BUSI_ID,g.OPEN OPEN,g.TITLE TITLE,g.CONTENT CONTENT,"
					+ "g.REGION_ID REGION_ID,g.ADDRESS ADDRESS,g.EMAIL EMAIL,g.PHONE PHONE,"
					+ "g.DEAL_RESULT DEAL_RESULT,g.DEPART_NAME GIVE_DEPT_NAME,g.ID ID,"
					+ "g.USER_ID USER_ID,g.TYPE TYPE,"
					+ "g.STATUS STATUS,u.TYPE USERTYPE,t.USER_ID_MAP uuid "
					+ "from GUESTBOOK g, uc_user_map t,uc_user u "
					+ "where (g.TYPE=? or g.TYPE=?) and g.EX_FLAG=?  "
					+ "and t.user_id=u.id and g.user_id = t.user_id";
			//type=2 咨询  ex_flag=0 为交换的数据
			DataSet ds = DbHelper.query(sql, new Object[] {"2","3","0"}, conn);
			//接受接口返回值
			JSONObject obj;
			// 命名空间 暂定
			String NAMESPACE = "http://zxts.webservice.sdzw.hanweb.com";
			//循环 往接口中推送
			for (int i = 0; i < ds.getTotal(); i++) {
				//申请WebService服务名
				Service serivce = new Service();
				Call call = (Call) serivce.createCall();
				call.setTargetEndpointAddress(new java.net.URL(WebServiceUrl));
				if(ds.getRecord(i).getString("TYPE").equals("3")){
					call.setOperationName(new QName(NAMESPACE, "tsreceive"));
				}else {
					call.setOperationName(new QName(NAMESPACE, "zxreceive"));
				}
				//必填参数 全是String类型 开始
				//咨询提交用户的登录名  --loginname
				String loginname = ds.getRecord(i).getString("ACCOUNT");
				//咨询事项的事项编码  --SXBM
				String itemid = ds.getRecord(i).getString("SXBM");
				//部门编码 --DEPART_ID
				String flowroleid=ds.getRecord(i).getString("DEPART_ID");
				//提交日期格式 格式问题
				String submitdate=ds.getRecord(i).getString("WRITE_DATE");
				//受理日期  
				String acceptdate=ds.getRecord(i).getString("DEAL_DATE");
				//咨询编号
				String questioncode=ds.getRecord(i).getString("ID");
				//历史注册用户的用户id
				String olduserid="";
				//统一用户的uuid
				String uuid=ds.getRecord(i).getString("UUID");
				//历史注册用户的登录名
				String oldloginname="";			
				//写信人姓名
				String name = ds.getRecord(i).getString("USERNAME");
				//是否公开 1 公开 0 不公开
				String isopen;
				if ("".equals(ds.getRecord(i).getString("OPEN"))) {
					 isopen="1";
				}else {
					isopen=ds.getRecord(i).getString("OPEN");
				}
				//咨询标题
				String title=ds.getRecord(i).getString("TITLE");
				//咨询内容
				String content=ds.getRecord(i).getString("CONTENT");
				//提交咨询的用户类型 usertype 1 法人 2 个人
				//用USER_ID去uc_user中查询出来
				//String UserInfoSql = "select TYPE from uc_user where id = ?";
				//DataSet user = DbHelper.query(UserInfoSql,new Object[] { ds.getRecord(i).getString("USER_ID") }, conn);
				//用户类型 11 个人 默认是法人 USERTYPE
				//个人是2，法人是1，按照规范来。
				String usertype ="1";
				if("11".equals(ds.getRecord(i).get("USERTYPE"))){
					 usertype ="2";
				}
				//推送数据类型 咨询为1 投诉为2
				String  platform="1";
				if(ds.getRecord(i).getString("TYPE").equals("3")){
				  platform="2";
				}
				//咨询的详细信息页面地址
				String detailurl=SecurityConfig.getString("ConsultDatailUrl")+ds.getRecord(i).getString("ID");
				if(ds.getRecord(i).getString("TYPE").equals("3")){
					//投诉的详细信息页面地址
					detailurl=SecurityConfig.getString("ComplaintDatailUrl")+ds.getRecord(i).getString("ID");
				}
				//咨询的编辑信息页面地址 暂无编辑地址 同详细信息页面地址
				String editurl=SecurityConfig.getString("ConsultDatailUrl")+ds.getRecord(i).getString("ID");
				if(ds.getRecord(i).getString("TYPE").equals("3")){
					//投诉的编辑信息页面地址
					editurl=SecurityConfig.getString("ComplaintDatailUrl")+ds.getRecord(i).getString("ID");
				}
				//咨询主键 格式:区划编码+咨询id REGION_ID
				String zxuniqueid=ds.getRecord(i).getString("REGION_ID")+ds.getRecord(i).getString("ID");
				//投诉主键 格式:区划编码+投诉id REGION_ID
				String tsuniqueid=ds.getRecord(i).getString("REGION_ID")+ds.getRecord(i).getString("ID");
				//区划编码
				String area = ds.getRecord(i).getString("REGION_ID");
				//验证标识（安全性）验证标识
				String sign = SecurityConfig.getString("SecuritySign");
				//必填要素结束
				
				//非必填要素开始
				//联系地址
				String lxdzbg=ds.getRecord(i).getString("ADDRESS");
				//电子邮箱
				String e_mail=ds.getRecord(i).getString("EMAIL");
				//手机号码
				String mphone=ds.getRecord(i).getString("PHONE");
				//联系电话
				String telephone=ds.getRecord(i).getString("PHONE");
				//答复时间
				String  capplydate=ds.getRecord(i).getString("DEAL_DATE");
				//答复内容
				String capplycontent = ds.getRecord(i).getString("DEAL_RESULT");
				//答复单位
				String answerdept = ds.getRecord(i).getString("GIVE_DEPT_NAME");
				//state 受理状态（1：待受理  2：受理中  3：受理完毕）
				String state  = ds.getRecord(i).getString("STATUS"); 
				//答复状态 0 未答复 1 答复  isanswer 暂无
				String isanswer ="0";
				//isreply 是否有追问（0：无追问  1：有追问未回复  2：有追问已回复）——非必填  暂无
				String isreply ="0";
				//reaskreplydate 追问答复时间——非必填 暂无
				String reaskreplydate="";
				//非必填要素结束
				Object[] param;
				//投诉的要素 
				if(ds.getRecord(i).getString("TYPE").equals("3")){
					//itemname 事项名称——必填
					String itemname = ds.getRecord(i).getString("SXMC");
					//根据busi_id去business_index查询出来办件id和办件查询码
					String ProInfoSql = "select sblsh,CXMM from business_index t where t.sblsh =?";
					DataSet ProInfo = DbHelper.query(ProInfoSql,new Object[] { ds.getRecord(i).getString("BUSI_ID") }, conn);
					//办件id和办件查询码
					String projid =(String) ProInfo.getJOData().get("SBLSH");
					String projpwd =(String) ProInfo.getJOData().get("CXMM");
					 param = new Object[] {loginname, itemid, flowroleid, submitdate,capplydate,questioncode,
							name,lxdzbg,olduserid,uuid,oldloginname,e_mail,mphone,capplycontent,answerdept,
							isanswer,isreply,reaskreplydate,isopen,title,content,
							//办件id和 查询码
							projid,projpwd,itemname,usertype,platform,detailurl,editurl,tsuniqueid,area,sign
							};
				}else {
					//咨询的要素
					 param = new Object[] {loginname, itemid, flowroleid, submitdate,acceptdate,questioncode,
							olduserid,uuid,oldloginname,name,
							lxdzbg,e_mail,mphone,telephone,capplydate,capplycontent,answerdept,isanswer,isreply,state,reaskreplydate,
							isopen,title,content,usertype,platform,detailurl,editurl,zxuniqueid,area,sign
							};
				}
				// 调用webservice接口
//				System.out.println("提交咨询投诉输出："+uuid+"|"+usertype+"|"+loginname+"|"+ds.getRecord(i).getString("ID")+"|"+ds.getRecord(i).get("USERTYPE")+"|"
//						+"11".equals(ds.getRecord(i).get("USERTYPE"))+"|"+ds.getRecord(i).get("USERTYPE").equals("11")+"|"+ds.getRecord(i).getString("TYPE").equals("3")+"|=TYPE:"
//						+"3".equals(ds.getRecord(i).getString("TYPE"))+"|");
				String value = (String) call.invoke(param);
				obj = JSONObject.fromObject(value);
				//接口返回值
				String returnStatus = obj.getString("status");
				//根据返回值判断
				if(returnStatus.equals("0")){
					//交换成功 把交换位变为 1 推送成功
					String UpdateSql = "update GUESTBOOK set EX_FLAG='1' where id=?";
					int j = DbHelper.update(UpdateSql, new Object[] { ds.getRecord(i).getString("ID")}, conn);
					if (j > 0) {
						conn.commit();
					}
					 else {
						conn.rollback();
					}
				}else{
					System.out.println("returnStatus:"+returnStatus+"错误信息："+obj.getString("msg")+"错误ID："+ds.getRecord(i).getString("ID"));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据交换失败");
		} finally {
			DBSource.closeConnection(conn);
		}
	}

}
